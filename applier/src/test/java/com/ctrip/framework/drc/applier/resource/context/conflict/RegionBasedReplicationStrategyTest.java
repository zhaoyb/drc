package com.ctrip.framework.drc.applier.resource.context.conflict;

import com.ctrip.framework.drc.core.driver.schema.data.Bitmap;
import com.ctrip.framework.drc.core.server.conflict.RegionManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author limingdong
 * @create 2022/1/17
 */
public class RegionBasedReplicationStrategyTest extends ReplicationStrategyTest {

    @Test
    public void unionWhereCondition_match() {
        replicationStrategy = new RegionBasedReplicationStrategy(IDC, RegionManager.Region.Domestic);
        Bitmap where = replicationStrategy.unionWhereCondition(columns());
        Bitmap excepted = Bitmap.from(true);  // just identifier, write direct
        Assert.assertEquals(excepted, where);

        where = replicationStrategy.onUpdateWhereCondition(columns());
        excepted = Bitmap.from(false, false, false, true);
        Assert.assertEquals(excepted, where);
    }

    @Test
    public void unionWhereCondition_not_match() {
        replicationStrategy = new RegionBasedReplicationStrategy(IDC, RegionManager.Region.Oversea);
        Bitmap where = replicationStrategy.unionWhereCondition(columns());
        Bitmap excepted = Bitmap.from(true, false, false, true);  // with on update
        Assert.assertEquals(excepted, where);

        where = replicationStrategy.onUpdateWhereCondition(columns());
        excepted = Bitmap.from(false, false, false, true);
        Assert.assertEquals(excepted, where);
    }
}