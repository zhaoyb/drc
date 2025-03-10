package com.ctrip.framework.drc.monitor.module.replicate;

import com.ctrip.framework.drc.applier.server.LocalApplierServer;
import com.ctrip.framework.drc.core.driver.command.netty.endpoint.DefaultEndPoint;
import com.ctrip.framework.drc.core.server.config.replicator.ReplicatorConfig;
import com.ctrip.framework.drc.monitor.module.DrcModule;
import com.ctrip.framework.drc.monitor.module.config.AbstractConfigTest;
import com.ctrip.framework.drc.replicator.ReplicatorServer;
import com.ctrip.framework.drc.replicator.container.config.TableFilterConfiguration;
import com.ctrip.framework.drc.replicator.impl.DefaultReplicatorServer;
import com.ctrip.framework.drc.replicator.impl.inbound.schema.SchemaManagerFactory;
import com.ctrip.xpipe.api.lifecycle.Destroyable;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by mingdongli
 * 2019/10/15 上午12:09.
 */
public class ReplicatorApplierPairModule extends AbstractConfigTest implements DrcModule {

    private ReplicatorServer replicatorServer;

    private LocalApplierServer localApplierServer;

    private int srcMySQLPort;

    private int destMySQLPort;

    private int repPort;

    private String destination;

    private Set<String> includedDb = Sets.newHashSet();

    public ReplicatorApplierPairModule() {
        this(SOURCE_MASTER_PORT, DESTINATION_MASTER_PORT, REPLICATOR_MASTER_PORT, REGISTRY_KEY);
    }

    public ReplicatorApplierPairModule(int srcMySQLPort, int destMySQLPort, int repPort, String destination) {
        this.srcMySQLPort = srcMySQLPort;
        this.destMySQLPort = destMySQLPort;
        this.repPort = repPort;
        this.destination = destination;
        logger.info("srcMySQLPort [{}], destMySQLPort [{}], repPort [{}], destination [{}]", srcMySQLPort, destMySQLPort, repPort, destination);
    }

    @Override
    protected void doInitialize() throws Exception {
        ReplicatorConfig replicatorConfig = getReplicatorConfig();
        replicatorServer = new DefaultReplicatorServer(replicatorConfig, SchemaManagerFactory.getOrCreateMySQLSchemaManager(replicatorConfig), new TableFilterConfiguration());
        localApplierServer = new LocalApplierServer(destMySQLPort, repPort, destination, includedDb);
        replicatorServer.initialize();
        localApplierServer.initialize();
    }

    @Override
    protected void doStart() throws Exception {
        replicatorServer.start();
        localApplierServer.start();
    }

    @Override
    protected void doStop() throws Exception{
        localApplierServer.stop();
        replicatorServer.stop();
    }

    @Override
    protected void doDispose() throws Exception {
        localApplierServer.dispose();
        replicatorServer.dispose();
    }

    private ReplicatorConfig getReplicatorConfig() {
        ReplicatorConfig replicatorConfig = new ReplicatorConfig();
        DefaultEndPoint endpoint = new DefaultEndPoint(AbstractConfigTest.SOURCE_MASTER_IP, srcMySQLPort, AbstractConfigTest.USER, AbstractConfigTest.PASSWORD);
        replicatorConfig.setEndpoint(endpoint);
        replicatorConfig.setRegistryKey(destination, MHA_NAME);

        replicatorConfig.setApplierPort(repPort);

        return replicatorConfig;
    }

    @Override
    public void destroy() throws Exception {
        if (replicatorServer instanceof Destroyable) {  //删除文件
            ((Destroyable) replicatorServer).destroy();
        }
    }

    public Set<String> getIncludedDb() {
        return includedDb;
    }

    public void setIncludedDb(Set<String> includedDb) {
        this.includedDb = includedDb;
    }
}
