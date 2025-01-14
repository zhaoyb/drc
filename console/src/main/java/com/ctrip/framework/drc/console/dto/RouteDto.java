package com.ctrip.framework.drc.console.dto;

import java.util.List;

public class RouteDto {

    private Long id;

    private String routeOrgName;

    private String srcDcName;

    private String dstDcName;

    private List<String> srcProxyUris;

    private List<String> relayProxyUris;

    private List<String> dstProxyUris;

    private String tag;

    public RouteDto() {
    }

    public RouteDto(Long id, String routeOrgName, String srcDcName, String dstDcName, List<String> srcProxyUris, List<String> relayProxyUris, List<String> dstProxyUris, String tag) {
        this.id = id;
        this.routeOrgName = routeOrgName;
        this.srcDcName = srcDcName;
        this.dstDcName = dstDcName;
        this.srcProxyUris = srcProxyUris;
        this.relayProxyUris = relayProxyUris;
        this.dstProxyUris = dstProxyUris;
        this.tag = tag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRouteOrgName() {
        return routeOrgName;
    }

    public void setRouteOrgName(String routeOrgName) {
        this.routeOrgName = routeOrgName;
    }

    public String getSrcDcName() {
        return srcDcName;
    }

    public void setSrcDcName(String srcDcName) {
        this.srcDcName = srcDcName;
    }

    public String getDstDcName() {
        return dstDcName;
    }

    public void setDstDcName(String dstDcName) {
        this.dstDcName = dstDcName;
    }

    public List<String> getSrcProxyUris() {
        return srcProxyUris;
    }

    public void setSrcProxyUris(List<String> srcProxyUris) {
        this.srcProxyUris = srcProxyUris;
    }

    public List<String> getRelayProxyUris() {
        return relayProxyUris;
    }

    public void setRelayProxyUris(List<String> relayProxyUris) {
        this.relayProxyUris = relayProxyUris;
    }

    public List<String> getDstProxyUris() {
        return dstProxyUris;
    }

    public void setDstProxyUris(List<String> dstProxyUris) {
        this.dstProxyUris = dstProxyUris;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "RouteDto{" +
                "id=" + id +
                ", routeOrgName='" + routeOrgName + '\'' +
                ", srcDcName='" + srcDcName + '\'' +
                ", dstDcName='" + dstDcName + '\'' +
                ", srcProxyUris=" + srcProxyUris +
                ", relayProxyUris=" + relayProxyUris +
                ", dstProxyUris=" + dstProxyUris +
                ", tag='" + tag + '\'' +
                '}';
    }
}
