package com.ctrip.framework.drc.applier.resource.context.conflict;

import com.ctrip.framework.drc.core.driver.schema.data.Bitmap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @Author limingdong
 * @create 2022/1/17
 */
public class TimeBasedReplicationStrategyTest extends ReplicationStrategyTest {

    @Before
    public void setUp() {
        replicationStrategy = new TimeBasedReplicationStrategy();
    }

    @Test
    public void unionWhereCondition() {
        Bitmap where = replicationStrategy.unionWhereCondition(columns());
        Bitmap excepted = Bitmap.from(true, false, false, true);
        Assert.assertEquals(excepted, where);
    }

    @Test
    public void onUpdateWhereCondition() {
        Bitmap where = replicationStrategy.onUpdateWhereCondition(columns());
        Bitmap excepted = Bitmap.from(false, false, false, true);
        Assert.assertEquals(excepted, where);
    }
}