package com.ctrip.framework.drc.manager.healthcheck.service.task;

import com.ctrip.framework.drc.core.driver.command.netty.endpoint.DefaultEndPoint;
import com.ctrip.framework.drc.core.entity.Db;
import com.ctrip.framework.drc.core.entity.DbCluster;
import com.ctrip.framework.drc.core.entity.Dbs;
import com.ctrip.framework.drc.core.server.utils.ThreadUtils;
import com.ctrip.xpipe.api.endpoint.Endpoint;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by mingdongli
 * 2019/11/21 下午10:50.
 */
public class DbClusterHeartbeatTask extends AbstractMasterQueryTask<DbCluster> {

    private ListeningExecutorService zoneInfoExecutorService = MoreExecutors.listeningDecorator(ThreadUtils.newCachedThreadPool("DbClusterHeartbeatTask-Zone"));

    private DbCluster dbCluster;

    public DbClusterHeartbeatTask(Endpoint master, DbCluster dbCluster) {
        super(master);
        this.dbCluster = dbCluster;
    }

    @Override
    protected DbCluster doQuery() {
        Dbs dbs = dbCluster.getDbs();
        List<Db> dbList = dbs.getDbs();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        List<ListenableFuture<Boolean>> queries = Lists.newArrayList();

        for (Db db : dbList) {
            Endpoint endpoint = new DefaultEndPoint(db.getIp(), db.getPort(), dbs.getMonitorUser(), dbs.getMonitorPassword());
            ListenableFuture<Boolean> masterFuture = zoneInfoExecutorService.submit(new MasterHeartbeatTask(endpoint));
            queries.add(masterFuture);
        }

        ListenableFuture<List<Boolean>> successfulQueries = Futures.successfulAsList(queries);
        Futures.addCallback(successfulQueries, new FutureCallback<List<Boolean>>() {
            @Override
            public void onSuccess(List<Boolean> result) {
                for (int i = 0; i < result.size(); ++i) {
                    dbList.get(i).setMaster(result.get(i));
                }
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(Throwable t) {
                countDownLatch.countDown();
            }
        });

        try {
            boolean queryResult = countDownLatch.await(1100, TimeUnit.MILLISECONDS);
            if (!queryResult) {
                logger.error("[Timeout] for countDownLatch querying {}", dbCluster.getName());
            }
        } catch (InterruptedException e) {
            logger.error("doQuery error in countDownLatch wait", e);
        }

        return dbCluster;
    }
}
