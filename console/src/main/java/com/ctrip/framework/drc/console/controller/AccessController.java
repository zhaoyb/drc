package com.ctrip.framework.drc.console.controller;


import com.ctrip.framework.drc.console.dto.BuildMhaDto;
import com.ctrip.framework.drc.console.dto.MhaInstanceGroupDto;
import com.ctrip.framework.drc.console.service.impl.AccessServiceImpl;
import com.ctrip.framework.drc.console.service.impl.DrcMaintenanceServiceImpl;
import com.ctrip.framework.drc.core.http.ApiResult;
import com.ctrip.framework.drc.core.monitor.reporter.DefaultEventMonitorHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * @author maojiawei
 * @version 1.0
 * date: 2020-07-28
 */
@RestController
@RequestMapping("/api/drc/v1/access/")
public class AccessController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AccessServiceImpl accessServiceImp;

    @Autowired
    private DrcMaintenanceServiceImpl drcMaintenanceService;

    @PostMapping("precheck")
    public ApiResult preCheck(@RequestBody String requestBody) {
        logger.info("[API] precheck : {}", requestBody);
        Map<String, Object> res = null;
        try {
            res = accessServiceImp.applyPreCheck(requestBody);
            return ApiResult.getSuccessInstance(res);
        } catch (Exception e) {
            DefaultEventMonitorHolder.getInstance().logError(e);
            return ApiResult.getFailInstance(res);
        }
    }

    /**
     * Deprecated
     */
    @PostMapping("mha")
    public ApiResult buildMhaCluster(@RequestBody String requestBody) {
        Map<String, Object> res = null;
        try {
            res = accessServiceImp.buildMhaCluster(requestBody);
            return ApiResult.getSuccessInstance(res);
        } catch (Exception e) {
            DefaultEventMonitorHolder.getInstance().logError(e);
            return ApiResult.getFailInstance(res);
        }
    }

    /**
     * build a new mha cluster
     */
    @PostMapping("mhaV2")
    public ApiResult buildMhaClusterV2(@RequestBody String requestBody) {
        Map<String, Object> res = null;
        try {
            res = accessServiceImp.buildMhaClusterV2(requestBody);
            return ApiResult.getSuccessInstance(res);
        } catch (Exception e) {
            DefaultEventMonitorHolder.getInstance().logError(e);
            return ApiResult.getFailInstance(res);
        }
    }

    @PostMapping("mha/standalone")
    public ApiResult standaloneBuildMhaGroup(@RequestBody BuildMhaDto dto) {
        logger.info("standalone build mha group: {}", dto);
        return accessServiceImp.initMhaGroup(dto);
    }

    @PostMapping("machine/standalone")
    public ApiResult standAloneBuildMachine(@RequestBody MhaInstanceGroupDto dto) {
        logger.info("standalone build mha instance: {}", dto);
        try {
            Boolean res = drcMaintenanceService.updateMhaInstances(dto, false);
            return ApiResult.getSuccessInstance(String.format("standalone build mha instance %s result: %s", dto, res));
        } catch (Throwable t) {
            return ApiResult.getFailInstance(String.format("Fail standalone build mha instance %s for %s", dto, t));
        }
    }


    @DeleteMapping("mhas/{mha}")
    public ApiResult stopCheckNewMhaBuilt(@PathVariable String mha) {
        return accessServiceImp.stopCheckNewMhaBuilt(mha);
    }

    /**
     * disconnect the new slave cluster from old one
     * or check the copy status of the new cluster
     */
    @PostMapping("copystatus")
    public ApiResult checkOrDisconnectCopy(@RequestBody String requestBody) {
        Map<String, Object> res = null;
        try {
            res = accessServiceImp.getCopyResult(requestBody);
            return ApiResult.getSuccessInstance(res);
        } catch (Exception e) {
            DefaultEventMonitorHolder.getInstance().logError(e);
            return ApiResult.getFailInstance(res);
        }
    }

    /**
     * deploy the dns of the old and new drc cluster
     */
    @PostMapping("dns")
    public ApiResult deployDns(@RequestBody String requestBody) {
        Map<String, Object> res = null;
        try {
            res = accessServiceImp.deployDns(requestBody);
            return ApiResult.getSuccessInstance(res);
        } catch (Exception e) {
            DefaultEventMonitorHolder.getInstance().logError(e);
            return ApiResult.getFailInstance(res);
        }
    }

    /**
     * initialize dal cluster for respective environment
     */
    @PostMapping("clusters/env/{env}/goal/{goal}")
    public ApiResult registerDalCluster(@RequestBody String requestBody, @PathVariable String env, @PathVariable String goal) {
        Map<String, Object> res = null;
        try {
            res = accessServiceImp.registerDalCluster(requestBody, env, goal);
            return ApiResult.getSuccessInstance(res);
        } catch (Exception e) {
            DefaultEventMonitorHolder.getInstance().logError(e);
            return ApiResult.getFailInstance(res);
        }
    }

    /**
     * release dal clusters
     */
    @PostMapping("clusters/clustername/{dalClusterName}/env/{env}/releases")
    public ApiResult releaseDalCluster(@PathVariable String dalClusterName, @PathVariable String env) {
        Map<String, Object> res = null;
        try {
            res = accessServiceImp.releaseDalCluster(dalClusterName, env);
            return ApiResult.getSuccessInstance(res);
        } catch (Exception e) {
            DefaultEventMonitorHolder.getInstance().logError(e);
            return ApiResult.getFailInstance(res);
        }
    }
}
