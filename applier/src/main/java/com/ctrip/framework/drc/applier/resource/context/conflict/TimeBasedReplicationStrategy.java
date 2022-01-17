package com.ctrip.framework.drc.applier.resource.context.conflict;

import com.ctrip.framework.drc.core.driver.schema.data.Bitmap;
import com.ctrip.framework.drc.core.driver.schema.data.Columns;
import com.ctrip.framework.drc.core.server.conflict.ReplicationStrategy;

/**
 * add onUpdate field in where condition
 * @Author limingdong
 * @create 2022/1/14
 */
public class TimeBasedReplicationStrategy implements ReplicationStrategy {

    @Override
    public Bitmap unionWhereCondition(Columns columns) {
        return Bitmap.union(columns.getBitmapsOfIdentifier().get(0), columns.getLastBitmapOnUpdate());
    }
}
