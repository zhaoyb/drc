package com.ctrip.framework.drc.manager.healthcheck.service.task;

import com.ctrip.framework.drc.core.driver.command.netty.endpoint.DefaultEndPoint;
import com.ctrip.framework.drc.core.entity.Db;
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
 * 2019/11/22 上午9:59.
 */
public class DbClusterUuidQueryTask extends AbstractQueryTask<List<String>> {

    public static final int WAIT_TIMEOUT_MS = 1000;

    private ListeningExecutorService uuidInfoExecutorService = MoreExecutors.listeningDecorator(ThreadUtils.newCachedThreadPool("UuidQueryTask-Zone"));

    private Dbs dbs;

    private volatile List<String> uuids = Lists.newArrayList();

    public DbClusterUuidQueryTask(Endpoint master, Dbs dbs) {
        super(master);
        this.dbs = dbs;
    }

    @Override
    protected List<String> doQuery() {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        List<ListenableFuture<String>> queries = Lists.newArrayList();
        List<Db> dbList = dbs.getDbs();
        for (Db db : dbList) {
            Endpoint endpoint = new DefaultEndPoint(db.getIp(), db.getPort(), dbs.getMonitorUser(), dbs.getMonitorPassword());
            ListenableFuture<String> masterFuture = uuidInfoExecutorService.submit(new UuidQueryTask(endpoint));
            queries.add(masterFuture);
        }

        ListenableFuture<List<String>> successfulQueries = Futures.successfulAsList(queries);
        Futures.addCallback(successfulQueries, new FutureCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                uuids.addAll(result);
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(Throwable t) {
                logger.error("doQuery error in countDownLatch wait", t);
                countDownLatch.countDown();
            }
        });

        try {
            boolean queryResult = countDownLatch.await(WAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if (!queryResult) {
                for (Db db : dbs.getDbs()) {
                    logger.error("[Timeout] for query uuid for {}:{}", db.getIp(), db.getPort());
                }
            }
        } catch (InterruptedException e) {
            logger.error("doQuery error in countDownLatch wait", e);
        }

        return uuids;
    }
}
