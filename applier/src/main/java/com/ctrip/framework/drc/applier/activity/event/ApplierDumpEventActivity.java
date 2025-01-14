package com.ctrip.framework.drc.applier.activity.event;

import com.ctrip.framework.drc.applier.activity.replicator.converter.ApplierByteBufConverter;
import com.ctrip.framework.drc.applier.activity.replicator.driver.ApplierPooledConnector;
import com.ctrip.framework.drc.applier.event.ApplierDrcTableMapEvent;
import com.ctrip.framework.drc.applier.event.ApplierGtidEvent;
import com.ctrip.framework.drc.applier.event.ApplierXidEvent;
import com.ctrip.framework.drc.applier.resource.condition.Progress;
import com.ctrip.framework.drc.core.driver.schema.data.Columns;
import com.ctrip.framework.drc.fetcher.activity.event.DumpEventActivity;
import com.ctrip.framework.drc.fetcher.activity.replicator.FetcherSlaveServer;
import com.ctrip.framework.drc.fetcher.event.FetcherEvent;
import com.ctrip.framework.drc.fetcher.system.InstanceConfig;
import com.ctrip.framework.drc.fetcher.system.InstanceResource;

/**
 * @Author limingdong
 * @create 2021/3/4
 */
public class ApplierDumpEventActivity extends DumpEventActivity<FetcherEvent> {

    @InstanceResource
    public Progress progress;

    @InstanceConfig(path = "skipEvent")
    public String skipEvent = "false";

    @Override
    protected FetcherSlaveServer getFetcherSlaveServer() {
        return new FetcherSlaveServer(config, new ApplierPooledConnector(config.getEndpoint()), new ApplierByteBufConverter());
    }

    @Override
    protected void doHandleLogEvent(FetcherEvent event) {
        if (event instanceof ApplierGtidEvent) {
            ((ApplierGtidEvent) event).involve(context);
        }

        if (event instanceof ApplierXidEvent) {
            ((ApplierXidEvent) event).involve(context);
        }

        if (event instanceof ApplierDrcTableMapEvent) {
            ApplierDrcTableMapEvent drcEvent = (ApplierDrcTableMapEvent) event;
            loggerER.info("- DRC - {}: {}", drcEvent.getSchemaNameDotTableName(),
                    Columns.from(drcEvent.getColumns()).getNames());
        }
    }

    @Override
    protected void afterHandleLogEvent(FetcherEvent logEvent) {
        if (shouldSkip()) {
            logEvent.release();
            capacity.release();
            progress.tick();
        } else {
            super.afterHandleLogEvent(logEvent);
        }
    }

    protected boolean shouldSkip() {
        return "true".equalsIgnoreCase(skipEvent);
    }
}
