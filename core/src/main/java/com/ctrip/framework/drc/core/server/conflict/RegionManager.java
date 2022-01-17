package com.ctrip.framework.drc.core.server.conflict;

/**
 * @Author limingdong
 * @create 2022/1/14
 */
public interface RegionManager {

    enum Region {

        Domestic,

        Oversea
    }

    Region resolveRegion(String idc);

}
