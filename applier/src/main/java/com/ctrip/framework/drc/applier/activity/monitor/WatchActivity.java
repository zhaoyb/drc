package com.ctrip.framework.drc.applier.activity.monitor;

import com.ctrip.framework.drc.applier.container.ApplierServerContainer;
import com.ctrip.framework.drc.applier.server.ApplierServer;
import com.ctrip.framework.drc.core.monitor.reporter.DefaultEventMonitorHolder;
import com.ctrip.framework.drc.fetcher.system.AbstractLoopActivity;
import com.ctrip.framework.drc.fetcher.system.InstanceConfig;
import com.ctrip.framework.drc.fetcher.system.TaskActivity;
import com.ctrip.framework.drc.fetcher.system.TaskSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @Author Slight
 * Aug 19, 2020
 */
public class WatchActivity extends AbstractLoopActivity implements TaskSource<Boolean> {

    private final Logger loggerP = LoggerFactory.getLogger("PROGRESS");

    @InstanceConfig(path = "servers")
    public ConcurrentHashMap<String, ? extends ApplierServer> servers;

    @InstanceConfig(path = "container")
    public ApplierServerContainer container;

    @Override
    public void loop() {
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
        Iterator<String> keys = servers.keys().asIterator();
        while(keys.hasNext()) {
            String key = keys.next();
            try {
                ApplierServer server = servers.get(key);
                if (server != null)
                    patrol(key, server);
            } catch (Throwable t) {
                logger.info("UNLIKELY - when patrol {}", servers.get(key).getName());
            }
        }
    }

    public static class LastLWM {
        public final long lwm;
        public final long progress;
        public final long lastTimeMillis;

        public LastLWM(long lwm, long progress, long lastTimeMillis) {
            this.lwm = lwm;
            this.progress = progress;
            this.lastTimeMillis = lastTimeMillis;
        }
    }

    private HashMap<String, LastLWM> lastLWMHashMap = new HashMap<>();

    public void patrol(String key, ApplierServer server) {
        try {
            long currentLWM = server.getLWM();
            long currentProgress = server.getProgress();
            long bearingTimeMillis = 60 * 1000; //1 minutes
            if (currentLWM == 0)
                bearingTimeMillis = 10 * 60 * 1000; //10 minutes
            long currentTimeMillis = System.currentTimeMillis();
            LastLWM lastLWM = lastLWMHashMap.computeIfAbsent(key, k -> new LastLWM(currentLWM, currentProgress, currentTimeMillis));
            if (lastLWM.lwm == currentLWM && lastLWM.progress == currentProgress) {
                if (currentTimeMillis - lastLWM.lastTimeMillis > bearingTimeMillis) {
                    logger.info("lwm does not raise since {}ms, going to remove server", lastLWM.lastTimeMillis);
                    DefaultEventMonitorHolder.getInstance().logBatchEvent("alert", "lwm does not raise for a long time.", 1, 0);
                    container.removeServer(key, true);
                    container.registerServer(key);
                    lastLWMHashMap.remove(key);
                }
            } else {
                lastLWMHashMap.put(key, new LastLWM(currentLWM, currentProgress, currentTimeMillis));
                loggerP.info("go ahead ({}): lwm {} progress {}", key, currentLWM, currentProgress);
            }
        } catch (Throwable t) {
        }
    }

    @Override
    public <U> TaskActivity<Boolean, U> link(TaskActivity<Boolean, U> latter) {
        throw new AssertionError("WatchActivity.link() should not be invoked.");
    }

}
