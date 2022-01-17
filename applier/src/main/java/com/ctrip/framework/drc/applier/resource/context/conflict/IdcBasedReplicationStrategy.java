package com.ctrip.framework.drc.applier.resource.context.conflict;

/**
 * @Author limingdong
 * @create 2022/1/14
 */
public class IdcBasedReplicationStrategy extends LocationBasedReplicationStrategy {

    private String priorityIdc;

    public IdcBasedReplicationStrategy(String srcIdc, String priorityIdc) {
        super(srcIdc);
        this.priorityIdc = priorityIdc;
    }

    @Override
    boolean directWrite(String srcIdc) {
        return srcIdc.equalsIgnoreCase(priorityIdc);
    }

}
