package com.ctrip.framework.drc.applier.resource.context.conflict;

import com.ctrip.framework.drc.core.driver.schema.data.Bitmap;
import com.ctrip.framework.drc.core.driver.schema.data.Columns;
import com.ctrip.framework.drc.core.server.conflict.ReplicationStrategy;
import com.ctrip.framework.drc.core.server.conflict.RegionManager;
import com.ctrip.framework.drc.fetcher.system.AbstractResource;
import com.ctrip.framework.drc.fetcher.system.InstanceConfig;

/**
 * @Author limingdong
 * @create 2022/1/14
 */
public class AdaptiveReplicationStrategyResource extends AbstractResource implements ReplicationStrategy {

    private ReplicationStrategy delegate;

    @InstanceConfig(path = "replicator.idc")
    public String srcIdc;

    @InstanceConfig(path = "replicationStrategy")
    public String replicationStrategyConfig;

    @Override
    protected void doInitialize() {
        logger.info("[Init] replicationStrategy, {}:{}", srcIdc, replicationStrategyConfig);
        if (ReplicationStrategy.Category.isTimeBased(replicationStrategyConfig)) {
            delegate = new TimeBasedReplicationStrategy();
        } else if (ReplicationStrategy.Category.isRegionBased(replicationStrategyConfig)) {
            delegate = new RegionBasedReplicationStrategy(srcIdc, RegionManager.Region.valueOf(replicationStrategyConfig));
        } else {
            delegate = new IdcBasedReplicationStrategy(srcIdc, replicationStrategyConfig);
        }
    }

    @Override
    public Bitmap unionWhereCondition(Columns columns) {
        return delegate.unionWhereCondition(columns);
    }

}
