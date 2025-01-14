package com.ctrip.framework.drc.replicator.impl.inbound.filter;

import com.ctrip.framework.drc.core.server.common.Filter;

/**
 * Created by mingdongli
 * 2019/10/9 上午10:30.
 *
 * preFilter
 * TransactionMonitorFilter -> EventTypeFilter -> UuidFilter -> DdlFilter -> BlackTableNameFilter
 *
 * postFilter
 * TransactionMonitorFilter(read) -> DelayMonitorFilter(read) -> PersistPostFilter(write) -> EventReleaseFilter(release)
 */
public class DefaultFilterChainFactory {

    public static Filter<LogEventWithGroupFlag> createFilterChain(FilterChainContext context) {

        EventReleaseFilter eventReleaseFilter = new EventReleaseFilter();

        PersistPostFilter persistPostFilter = new PersistPostFilter(context.getTransactionCache());
        eventReleaseFilter.setSuccessor(persistPostFilter);

        DelayMonitorFilter delayMonitorFilter = new DelayMonitorFilter(context.getMonitorManager());
        persistPostFilter.setSuccessor(delayMonitorFilter);

        TransactionMonitorFilter transactionMonitorFilter = new TransactionMonitorFilter(context.getInboundMonitorReport());
        delayMonitorFilter.setSuccessor(transactionMonitorFilter);

        EventTypeFilter eventTypeFilter = new EventTypeFilter();
        transactionMonitorFilter.setSuccessor(eventTypeFilter);

        UuidFilter uuidFilter = new UuidFilter();
        uuidFilter.setWhiteList(context.getWhiteUUID());
        eventTypeFilter.setSuccessor(uuidFilter);

        DdlFilter ddlFilter = new DdlFilter(context.getSchemaManager(), context.getMonitorManager());
        uuidFilter.setSuccessor(ddlFilter);

        BlackTableNameFilter tableNameFilter = new BlackTableNameFilter(context.getInboundMonitorReport(), context.getTableNames());
        context.registerBlackTableNameFilter(tableNameFilter);
        ddlFilter.setSuccessor(tableNameFilter);

        return eventReleaseFilter;
    }

}
