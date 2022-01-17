package com.ctrip.framework.drc.applier.resource.context.conflict;

import com.ctrip.framework.drc.core.driver.binlog.impl.TableMapLogEvent;
import com.ctrip.framework.drc.core.driver.schema.data.Columns;
import com.ctrip.framework.drc.core.server.conflict.RegionManager;
import com.ctrip.framework.drc.core.server.conflict.ReplicationStrategy;
import com.ctrip.xpipe.api.codec.Codec;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Author limingdong
 * @create 2022/1/17
 */
public abstract class ReplicationStrategyTest {

    protected static final String IDC = "shaxy";

    protected static final RegionManager.Region PRIORITY_REGION = RegionManager.Region.Domestic;

    protected static final String PRIORITY_IDC = "shaxy";

    protected ReplicationStrategy replicationStrategy;

    static Columns columns() {
        String json = "{\"columns\":[" +
                "{\"type\":3,\"meta\":0,\"nullable\":true,\"name\":\"id\",\"charset\":null,\"collation\":null,\"unsigned\":false,\"binary\":false,\"pk\":true,\"uk\":false,\"onUpdate\":false}," +
                "{\"type\":15,\"meta\":60,\"nullable\":true,\"name\":\"user\",\"charset\":\"utf8\",\"collation\":\"utf8_general_ci\",\"unsigned\":false,\"binary\":false,\"pk\":false,\"uk\":false,\"onUpdate\":false}," +
                "{\"type\":15,\"meta\":60,\"nullable\":true,\"name\":\"gender\",\"charset\":\"utf8\",\"collation\":\"utf8_general_ci\",\"unsigned\":false,\"binary\":false,\"pk\":false,\"uk\":false,\"onUpdate\":false}," +
                "{\"type\":18,\"meta\":3,\"nullable\":false,\"name\":\"lt\",\"charset\":null,\"collation\":null,\"unsigned\":false,\"binary\":false,\"pk\":false,\"uk\":false,\"onUpdate\":true}" +
                "]}";
        return Columns.from(Codec.DEFAULT.decode(json.getBytes(), ColumnContainer.class).columns,
                Lists.<List<String>>newArrayList(
                        Lists.<String>newArrayList("id")
                ));
    }

    static class ColumnContainer {
        public List<TableMapLogEvent.Column> getColumns() {
            return columns;
        }

        public void setColumns(List<TableMapLogEvent.Column> columns) {
            this.columns = columns;
        }

        public List<TableMapLogEvent.Column> columns;
    }
}
