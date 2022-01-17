package com.ctrip.framework.drc.core.server.conflict;

import org.junit.Assert;
import org.junit.Test;

import static com.ctrip.framework.drc.core.server.conflict.RegionManager.Region.Domestic;
import static com.ctrip.framework.drc.core.server.conflict.RegionManager.Region.Oversea;

/**
 * @Author limingdong
 * @create 2022/1/17
 */
public class DefaultRegionManagerTest {

    private RegionManager defaultRegionManager = new DefaultRegionManager();

    @Test
    public void resolveRegion() {
        Assert.assertEquals(Domestic, defaultRegionManager.resolveRegion("shaxy"));
        Assert.assertEquals(Oversea, defaultRegionManager.resolveRegion("awsfra"));
    }
}