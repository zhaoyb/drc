package com.ctrip.framework.drc.core.server.conflict;

import com.ctrip.framework.drc.core.driver.schema.data.Bitmap;
import com.ctrip.framework.drc.core.driver.schema.data.Columns;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @Author limingdong
 * @create 2022/1/14
 */
public interface ReplicationStrategy {

    Bitmap unionWhereCondition(Columns columns);

    default Bitmap onUpdateWhereCondition(Columns columns) {
        return columns.getLastBitmapOnUpdate();
    }

    enum Category {

        Time {
            @Override
            public List<String> nameLists() {
                return Lists.newArrayList(name().toLowerCase());
            }
        },

        Region {
            @Override
            public List<String> nameLists() {
                return Lists.newArrayList(RegionManager.Region.Domestic.name().toLowerCase(), RegionManager.Region.Oversea.name().toLowerCase());
            }
        },

        Idc {
            @Override
            public List<String> nameLists() {
                return Lists.newArrayList();
            }
        };

        public abstract List<String> nameLists();

        public static boolean isTimeBased(String replicationStrategyConfig) {
            return StringUtils.isBlank(replicationStrategyConfig)
                    || Time.nameLists().contains(replicationStrategyConfig);
        }

        public static boolean isRegionBased(String replicationStrategyConfig) {
            return StringUtils.isBlank(replicationStrategyConfig)
                    || Region.nameLists().contains(replicationStrategyConfig);
        }

        // add all idcs to support idc strategy outside
        public static List<String> allNameLists() {
            List<String> all = Lists.newArrayList();
            for (Category category : values()) {
                all.addAll(category.nameLists());
            }
            return all;
        }
    }
}
