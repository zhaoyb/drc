package com.ctrip.framework.drc.replicator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Created by mingdongli
 * 2019/11/3 下午10:12.
 */
public abstract class AbstractZkTest extends MockTest {

    protected CuratorFramework curatorFramework;

    public void setUp() throws Exception {
        super.initMocks();
        if (curatorFramework == null) {
            curatorFramework = CuratorFrameworkFactory.newClient("127.0.0.1:12181", new ExponentialBackoffRetry(1000, 3));
            curatorFramework.start();
        }
    }
}
