package com.ctrip.framework.drc.replicator.container;

import com.ctrip.framework.drc.core.driver.binlog.manager.task.NamedCallable;
import com.ctrip.framework.drc.core.driver.binlog.manager.task.RetryTask;
import com.ctrip.framework.drc.core.driver.command.packet.ResultCode;
import com.ctrip.framework.drc.core.exception.DrcServerException;
import com.ctrip.framework.drc.core.http.ApiResult;
import com.ctrip.framework.drc.core.monitor.reporter.DefaultEventMonitorHolder;
import com.ctrip.framework.drc.core.server.DrcServer;
import com.ctrip.framework.drc.core.server.common.AbstractResourceManager;
import com.ctrip.framework.drc.core.server.config.replicator.ReplicatorConfig;
import com.ctrip.framework.drc.core.server.container.ComponentRegistryHolder;
import com.ctrip.framework.drc.core.server.container.ServerContainer;
import com.ctrip.framework.drc.core.server.utils.ThreadUtils;
import com.ctrip.framework.drc.replicator.ReplicatorServer;
import com.ctrip.framework.drc.replicator.container.zookeeper.UuidConfig;
import com.ctrip.framework.drc.replicator.impl.inbound.schema.MySQLSchemaManager;
import com.ctrip.framework.drc.replicator.impl.inbound.schema.SchemaManagerFactory;
import com.ctrip.framework.drc.replicator.store.manager.file.DefaultFileManager;
import com.ctrip.xpipe.api.cluster.LeaderElector;
import com.ctrip.xpipe.api.codec.Codec;
import com.ctrip.xpipe.api.endpoint.Endpoint;
import com.ctrip.xpipe.exception.ErrorMessage;
import com.ctrip.xpipe.lifecycle.LifecycleHelper;
import com.ctrip.xpipe.utils.OsUtils;
import com.ctrip.xpipe.zk.ZkClient;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static com.ctrip.framework.drc.core.driver.command.packet.ResultCode.PORT_ALREADY_EXIST;
import static com.ctrip.framework.drc.core.driver.command.packet.ResultCode.SERVER_ALREADY_EXIST;
import static com.ctrip.framework.drc.core.driver.config.GlobalConfig.REPLICATOR_UUIDS_PATH;

/**
 * @Author limingdong
 * @create 2020/1/3
 */
public abstract class AbstractServerContainer extends AbstractResourceManager implements ServerContainer<ReplicatorConfig, ApiResult>, ApplicationRunner {

    private Map<String, Integer> runningPorts = Maps.newConcurrentMap();

    private Map<String, DrcServer> drcServers = Maps.newConcurrentMap();

    @Autowired
    protected ZkClient zkClient;

    private Set<String> processingReplicators = Sets.newConcurrentHashSet();

    private ExecutorService addExecutorService = ThreadUtils.newFixedThreadPool(OsUtils.getCpuCount(), "ReplicatorServerContainer");

    @Override
    public ApiResult addServer(ReplicatorConfig config) {
        String registryKey = config.getRegistryKey();
        int port = config.getApplierPort();
        if (!drcServers.containsKey(registryKey)) {
            synchronized (this) {
                if (!drcServers.containsKey(registryKey)) {
                    Set<Integer> ports = Sets.newHashSet(runningPorts.values());
                    if (ports.contains(port)) {
                        logger.error("[Used] port {} for cluster {}", port, registryKey);
                        return ApiResult.getInstance(Boolean.FALSE, PORT_ALREADY_EXIST.getCode(), String.valueOf(getMaxNotInUsedPort()));
                    }
                    if (!processingReplicators.contains(registryKey)) {
                        processingReplicators.add(registryKey);
                    } else {
                        logger.error("[Add] replicator {} fail due to already in processingReplicators", registryKey);
                        return ApiResult.getInstance(Boolean.FALSE, SERVER_ALREADY_EXIST.getCode(), SERVER_ALREADY_EXIST.getMessage());
                    }
                    addExecutorService.submit(() -> {
                        try {
                            new RetryTask<>(new RegisterTask(config), 2).call();
                        } finally {
                            processingReplicators.remove(registryKey);
                        }
                    });
                    return ApiResult.getInstance(Boolean.TRUE, ResultCode.HANDLE_SUCCESS.getCode(), ResultCode.HANDLE_SUCCESS.getMessage());

                }
            }
        }

        logger.error("[Add] replicator {} fail due to server already exist in port {}", registryKey, port);
        return ApiResult.getInstance(Boolean.FALSE, SERVER_ALREADY_EXIST.getCode(), SERVER_ALREADY_EXIST.getMessage());
    }

    protected abstract ReplicatorServer getReplicatorServer(ReplicatorConfig config);

    private int getMaxNotInUsedPort() {
        int maxPort = -1;
        for (int p : runningPorts.values()) {
            if (p > maxPort) {
                maxPort = p;
            }
        }
        return maxPort + 1;
    }

    private void registerReplicators() {
        List<String> replicators = DefaultFileManager.getReplicators(null);
        for (String replicator : replicators) {
            if (replicator.startsWith(".")) {
                continue;
            }
            getLeaderElector(replicator);
            logger.info("[INACTIVE] {} to zookeeper", replicator);
        }
    }

    @Override
    public void removeServer(String registryKey, boolean closeLeaderElector) {
        try {
            DrcServer drcServer = drcServers.get(registryKey);
            if (drcServer != null) {
                logger.info("[Deregister] {} start", registryKey);
                deRegister(drcServer);
            }
            removeReplicatorCache(registryKey);  //no need to close zk
            if (closeLeaderElector) {
                LeaderElector zkLeaderElector = zkLeaderElectors.remove(registryKey);
                if (zkLeaderElector != null) {
                    zkLeaderElector.stop();
                    zkLeaderElector.dispose();
                }
                stopMySQLSchemaManager(registryKey);
                deleteFile(registryKey);
            }
        } catch (Throwable t) {
            throw new DrcServerException(
                    new ErrorMessage<>(ResultCode.UNKNOWN_ERROR,
                            String.format("Add server for cluster %s failed", registryKey)), t);
        }
    }

    private void stopMySQLSchemaManager(String registryKey) {
        MySQLSchemaManager mySQLSchemaManager = SchemaManagerFactory.remove(registryKey);
        if (mySQLSchemaManager != null) {
            try {
                LifecycleHelper.stopIfPossible(mySQLSchemaManager);
                LifecycleHelper.disposeIfPossible(mySQLSchemaManager);
            } catch (Exception e) {
                logger.error("[MySQLSchemaManager] stop for {} error", registryKey, e);
            }
        }
    }

    private void deleteFile(String registryKey) {
        File file = new File(DefaultFileManager.LOG_PATH + registryKey);
        if (file == null) {
            return;
        }
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            f.delete();
        }
        file.delete();
    }

    private void removeReplicatorCache(String registryKey) {
        DrcServer drcServer = drcServers.remove(registryKey);
        if (drcServer != null) {
            runningPorts.remove(registryKey);
        }
    }

    private void register(ReplicatorServer replicatorServer) throws Exception {
        ComponentRegistryHolder.getComponentRegistry().add(replicatorServer);
    }

    private void deRegister(DrcServer drcServer) throws Exception {
        ComponentRegistryHolder.getComponentRegistry().remove(drcServer);
    }

    private void cacheServer(String key, int port, ReplicatorServer replicatorServer) {
        drcServers.put(key, replicatorServer);
        runningPorts.put(key, port);
        logger.info("[Register] {} to drcServers and  runningPorts with port {}", key, port);
    }

    @Override
    public Endpoint getUpstreamMaster(String registryKey) {
        DrcServer drcServer = drcServers.get(registryKey);
        if (drcServer == null) {
            return null;
        }
        return drcServer.getUpstreamMaster();
    }

    @Override
    public synchronized ApiResult register(String registryKey, int port) {
        try {
            if(zkLeaderElectors.get(registryKey) != null) {
                logger.info("[zkLeaderElectors] {} error, already elect leader", registryKey);
                return ApiResult.getInstance(Boolean.FALSE, SERVER_ALREADY_EXIST.getCode(), SERVER_ALREADY_EXIST.getMessage());
            }
            if(drcServers.get(registryKey) != null){
                logger.info("[Register] {} error, already cached in drcServers", registryKey);
                return ApiResult.getInstance(Boolean.FALSE, SERVER_ALREADY_EXIST.getCode(), SERVER_ALREADY_EXIST.getMessage());
            }
            Set<Integer> ports = Sets.newHashSet(runningPorts.values());
            if (ports.contains(port)) {
                logger.warn("[Used] port {} for cluster {}", port, registryKey);
                return ApiResult.getInstance(Boolean.FALSE, PORT_ALREADY_EXIST.getCode(), String.valueOf(getMaxNotInUsedPort()));
            }
            getLeaderElector(registryKey);  //register when restart or called by register
            return ApiResult.getSuccessInstance(Boolean.TRUE);
        } catch (Exception e) {
            logger.error("[checkExists] for {} error", registryKey, e);
        }

        return ApiResult.getFailInstance(Boolean.FALSE);
    }

    private Set<String> getUuidFromZookeeper(CuratorFramework curatorFramework, String registryKey) {
        try {
            String registerPath = REPLICATOR_UUIDS_PATH + "/" + registryKey;
            if (curatorFramework.checkExists().forPath(registerPath) == null) {
                curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(registerPath);
                return Sets.newHashSet();
            }
            byte[] uuidArray = curatorFramework.getData().forPath(REPLICATOR_UUIDS_PATH + "/" + registryKey);
            UuidConfig uuidConfig = Codec.DEFAULT.decode(uuidArray, UuidConfig.class);
            return uuidConfig.getUuids();
        } catch (Exception e) {
        }
        return Sets.newHashSet();
    }

    protected void updateUuids(ReplicatorConfig config, boolean persistToZk) {
        UuidConfig uuidConfig = new UuidConfig();
        Set<String> uuids = getUuidFromZookeeper(zkClient.get(), config.getRegistryKey());
        Set<UUID> uuidSet = config.getWhiteUUID();
        for (UUID uuid : uuidSet) {
            uuids.add(uuid.toString());
        }
        uuidConfig.setUuids(uuids);
        if (persistToZk) {
            updateUuidToZookeeper(zkClient.get(), config.getRegistryKey(), uuidConfig);
        }

        Set<UUID> res = Sets.newHashSet();
        for (String uuid : uuids) {
            res.add(UUID.fromString(uuid));
        }

        config.setWhiteUUID(res);
    }

    private void updateUuidToZookeeper(CuratorFramework curatorFramework, String registryKey, UuidConfig uuidConfig) {
        try {
            String registerPath = REPLICATOR_UUIDS_PATH + "/" + registryKey;
            curatorFramework.inTransaction().check().forPath(registerPath).and().setData().forPath(registerPath, Codec.DEFAULT.encodeAsBytes(uuidConfig)).and().commit();
        } catch (Exception e) {
        }
    }

    @Override
    public void run(ApplicationArguments applicationArguments) {
        registerReplicators();
        logger.info("[Start] register replicator to zk");
    }

    class RegisterTask implements NamedCallable<Boolean> {

        private ReplicatorConfig config;

        private ReplicatorServer replicatorServer;

        public RegisterTask(ReplicatorConfig config) {
            this.config = config;
        }

        @Override
        public Boolean call() throws Exception {
            String registryKey = config.getRegistryKey();
            replicatorServer = getReplicatorServer(config);
            register(replicatorServer);
            cacheServer(registryKey, config.getApplierPort(), replicatorServer);
            logger.info("[Add] replicator {} successfully at port {}", registryKey, port);
            DefaultEventMonitorHolder.getInstance().logEvent("Register", registryKey);
            return true;
        }

        public void afterFail() {
            NamedCallable.super.afterFail();
            logger.error("[Add] server {} error", config.getRegistryKey());
        }

        public void afterException(Throwable t) {
            NamedCallable.super.afterException(t);
            releaseResource();
        }

        private void releaseResource() {
            try {
                stopMySQLSchemaManager(config.getRegistryKey());
                deRegister(replicatorServer);
            } catch (Throwable t) {
                logger.error("releaseResource error", t);
            }
        }
    }
}
