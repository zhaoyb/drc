package com.ctrip.framework.drc.applier.resource.context.conflict;

import com.ctrip.framework.drc.core.server.conflict.DefaultRegionManager;
import com.ctrip.framework.drc.core.server.conflict.RegionManager;

/**
 * srcIdc should not be same with currentIdc
 * @Author limingdong
 * @create 2022/1/14
 */
public class RegionBasedReplicationStrategy extends LocationBasedReplicationStrategy {

    private RegionManager regionManager = new DefaultRegionManager();

    private RegionManager.Region priorityRegion;

    public RegionBasedReplicationStrategy(String srcIdc, RegionManager.Region priorityRegion) {
        super(srcIdc);
        this.priorityRegion = priorityRegion;
    }

    @Override
    boolean directWrite(String srcIdc) {
        return regionManager.resolveRegion(srcIdc) == priorityRegion;
    }
}
