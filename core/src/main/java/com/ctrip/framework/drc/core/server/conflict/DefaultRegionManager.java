package com.ctrip.framework.drc.core.server.conflict;

import com.ctrip.xpipe.codec.JsonCodec;
import com.ctrip.xpipe.config.AbstractConfigBean;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @Author limingdong
 * @create 2022/1/14
 */
public class DefaultRegionManager extends AbstractConfigBean implements RegionManager {

    private static final String DOMESTIC_IDCS = "idc.domestic";

    private static final String OVERSEAS_IDCS = "idc.overseas";

    private Set<String> domesticDcInfos = Sets.newHashSet();

    private Set<String> overseasDcInfos = Sets.newHashSet();

    @Override
    public Region resolveRegion(String idc) {
        if(domesticDcInfos.isEmpty()) {
            domesticDcInfos = getDcInofs(DOMESTIC_IDCS);
        }
        if (domesticDcInfos.contains(idc)) {
            return Region.Domestic;
        }

        if(overseasDcInfos.isEmpty()) {
            overseasDcInfos = getDcInofs(OVERSEAS_IDCS);
        }
        if (overseasDcInfos.contains(idc)) {
            return Region.Oversea;
        }

        return Region.Domestic;
    }

    @Override
    public void onChange(String key, String oldValue, String newValue) {
        super.onChange(key, oldValue, newValue);
        domesticDcInfos = getDcInofs(DOMESTIC_IDCS);
        overseasDcInfos = getDcInofs(OVERSEAS_IDCS);
    }

    private Set<String> getDcInofs(String key) {

        String dcInfoStr = getProperty(key, "[]");
        Set<String> dcInfos = JsonCodec.INSTANCE.decode(dcInfoStr, Set.class);

        Set<String> result = Sets.newHashSet();
        for(String entry : dcInfos){
            result.add(entry.toLowerCase());
        }

        logger.info("[getDcInofs]{}", result);
        return result;
    }
}
