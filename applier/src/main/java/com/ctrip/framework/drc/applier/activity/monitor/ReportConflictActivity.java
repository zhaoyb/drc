package com.ctrip.framework.drc.applier.activity.monitor;

import com.ctrip.framework.drc.applier.activity.monitor.entity.ConflictTransactionLog;
import com.ctrip.framework.drc.fetcher.activity.monitor.ReportActivity;
import com.ctrip.framework.drc.fetcher.system.InstanceConfig;
import com.ctrip.framework.drc.core.http.ApiResult;
import com.ctrip.framework.drc.fetcher.system.TaskQueueActivity;
import com.ctrip.xpipe.spring.RestTemplateFactory;
import org.springframework.web.client.RestOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jixinwang on 2020/10/14
 */
public class ReportConflictActivity extends ReportActivity<ConflictTransactionLog, Boolean> {

    @InstanceConfig(path = "cluster")
    public String cluster = "unset";

    @InstanceConfig(path = "replicator.mhaName")
    public String srcMhaName = "unset";

    @InstanceConfig(path = "target.mhaName")
    public String destMhaName = "unset";

    @InstanceConfig(path = "conflict.log.upload.url")
    public String conflictLogUploadUrl = "unset";

    @InstanceConfig(path = "conflict.log.upload.switch")
    public String conflictLogUploadSwitch = "unset";

    @Override
    public void doReport(List<ConflictTransactionLog> taskList) {
        restTemplate.postForObject(conflictLogUploadUrl, taskList, ApiResult.class);
    }

    @Override
    public boolean report(ConflictTransactionLog conflictTransactionLog) {
        if ("on".equals(conflictLogUploadSwitch)) {
            conflictTransactionLog.setSrcMhaName(srcMhaName);
            conflictTransactionLog.setDestMhaName(destMhaName);
            conflictTransactionLog.setClusterName(cluster);
            conflictTransactionLog.setConflictHandleTime(System.currentTimeMillis());
            return trySubmit(conflictTransactionLog);
        }
        return true;
    }

    public void setRestTemplate(RestOperations restTemplate) {
        this.restTemplate = restTemplate;
    }
}
