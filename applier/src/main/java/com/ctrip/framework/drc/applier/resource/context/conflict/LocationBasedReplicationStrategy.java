package com.ctrip.framework.drc.applier.resource.context.conflict;

import com.ctrip.framework.drc.core.driver.schema.data.Bitmap;
import com.ctrip.framework.drc.core.driver.schema.data.Columns;
import com.ctrip.framework.drc.core.server.conflict.ReplicationStrategy;

/**
 * @Author limingdong
 * @create 2022/1/14
 */
public abstract class LocationBasedReplicationStrategy implements ReplicationStrategy {

    private String srcIdc;

    public LocationBasedReplicationStrategy(String srcIdc) {
        this.srcIdc = srcIdc.toLowerCase();
    }

    @Override
    public Bitmap unionWhereCondition(Columns columns) {
        if (directWrite(srcIdc)) {
            return columns.getBitmapsOfIdentifier().get(0);  // without onUpdate, let priorityIdc work
        }
        return Bitmap.union(columns.getBitmapsOfIdentifier().get(0), columns.getLastBitmapOnUpdate());
    }

    abstract boolean directWrite(String srcIdc);
}
