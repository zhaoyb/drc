package com.ctrip.framework.drc.applier.resource.context.conflict;

import com.ctrip.framework.drc.core.driver.schema.data.Bitmap;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author limingdong
 * @create 2022/1/17
 */
public class AdaptiveReplicationStrategyResourceTest extends ReplicationStrategyTest {

    @Test
    public void timeBased() throws Exception {
        replicationStrategy = new AdaptiveReplicationStrategyResource();
        ((AdaptiveReplicationStrategyResource)replicationStrategy).srcIdc = "shaoy";
        ((AdaptiveReplicationStrategyResource)replicationStrategy).replicationStrategyConfig = "time";
        ((AdaptiveReplicationStrategyResource) replicationStrategy).initialize();

        Bitmap where = replicationStrategy.unionWhereCondition(columns());
        Bitmap excepted = Bitmap.from(true, false, false, true);
        Assert.assertEquals(excepted, where);

        where = replicationStrategy.onUpdateWhereCondition(columns());
        excepted = Bitmap.from(false, false, false, true);
        Assert.assertEquals(excepted, where);
    }

    @Test
    public void regionBased() throws Exception {
        replicationStrategy = new AdaptiveReplicationStrategyResource();
        ((AdaptiveReplicationStrategyResource)replicationStrategy).srcIdc = "shaoy";
        ((AdaptiveReplicationStrategyResource)replicationStrategy).replicationStrategyConfig = "Oversea";
        ((AdaptiveReplicationStrategyResource) replicationStrategy).initialize();

        Bitmap where = replicationStrategy.unionWhereCondition(columns());  // with on update
        Bitmap excepted = Bitmap.from(true, false, false, true);
        Assert.assertEquals(excepted, where);

        where = replicationStrategy.onUpdateWhereCondition(columns());
        excepted = Bitmap.from(false, false, false, true);
        Assert.assertEquals(excepted, where);
    }

    @Test
    public void idcBased() throws Exception {
        replicationStrategy = new AdaptiveReplicationStrategyResource();
        ((AdaptiveReplicationStrategyResource)replicationStrategy).srcIdc = "shaoy";
        ((AdaptiveReplicationStrategyResource)replicationStrategy).replicationStrategyConfig = "shaoy";
        ((AdaptiveReplicationStrategyResource) replicationStrategy).initialize();

        Bitmap where = replicationStrategy.unionWhereCondition(columns());  // with on update
        Bitmap excepted = Bitmap.from(true);
        Assert.assertEquals(excepted, where);

        where = replicationStrategy.onUpdateWhereCondition(columns());
        excepted = Bitmap.from(false, false, false, true);
        Assert.assertEquals(excepted, where);
    }
}