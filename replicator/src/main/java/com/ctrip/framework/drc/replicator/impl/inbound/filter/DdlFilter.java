package com.ctrip.framework.drc.replicator.impl.inbound.filter;

import com.ctrip.framework.drc.core.driver.binlog.LogEvent;
import com.ctrip.framework.drc.core.driver.binlog.constant.LogEventType;
import com.ctrip.framework.drc.core.driver.binlog.impl.DrcDdlLogEvent;
import com.ctrip.framework.drc.core.driver.binlog.impl.DrcSchemaSnapshotLogEvent;
import com.ctrip.framework.drc.core.driver.binlog.impl.QueryLogEvent;
import com.ctrip.framework.drc.core.driver.binlog.manager.SchemaManager;
import com.ctrip.framework.drc.core.driver.binlog.manager.TableInfo;
import com.ctrip.framework.drc.core.driver.util.CharsetConversion;
import com.ctrip.framework.drc.replicator.impl.inbound.schema.ghost.DDLPredication;
import com.ctrip.framework.drc.replicator.impl.inbound.schema.parse.DdlParser;
import com.ctrip.framework.drc.replicator.impl.inbound.schema.parse.DdlResult;
import com.ctrip.framework.drc.core.driver.binlog.constant.QueryType;
import com.ctrip.framework.drc.replicator.impl.monitor.MonitorManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.ctrip.framework.drc.core.driver.binlog.constant.LogEventType.*;

/**
 * @Author limingdong
 * @create 2020/2/24
 */
public class DdlFilter extends AbstractLogEventFilter {

    protected final Logger DDL_LOGGER = LoggerFactory.getLogger("com.ctrip.framework.drc.replicator.impl.inbound.filter.DdlFilter");

    public static final String XA_START = "XA START";

    public static final String XA_END = "XA END";

    public static final String XA_COMMIT = "XA COMMIT";

    public static final String XA_ROLLBACK = "XA ROLLBACK";

    public static final String BEGIN = "BEGIN";

    public static final String COMMIT = "COMMIT";

    public static final String DEFAULT_CHARACTER_SET_SERVER = "utf8mb4";

    private SchemaManager schemaManager;

    private MonitorManager monitorManager;

    public DdlFilter(SchemaManager schemaManager, MonitorManager monitorManager) {
        this.schemaManager = schemaManager;
        this.monitorManager = monitorManager;
    }

    @Override
    public boolean doFilter(LogEventWithGroupFlag value) {
        LogEvent logEvent = value.getLogEvent();
        final LogEventType logEventType = logEvent.getLogEventType();
        if (query_log_event == logEventType) {
            QueryLogEvent queryLogEvent = (QueryLogEvent) logEvent;
            parseQueryEvent(queryLogEvent);
        } else if (drc_schema_snapshot_log_event == logEventType) {
            DrcSchemaSnapshotLogEvent snapshotLogEvent = (DrcSchemaSnapshotLogEvent) logEvent;
            schemaManager.recovery(snapshotLogEvent);
            value.setInExcludeGroup(true);  // just init schema
        } else if (drc_ddl_log_event == logEventType) {
            DrcDdlLogEvent ddlLogEvent = (DrcDdlLogEvent) logEvent;
            doParseQueryEvent(ddlLogEvent.getDdl(), ddlLogEvent.getSchema(), DEFAULT_CHARACTER_SET_SERVER);
        }

        return doNext(value, value.isInExcludeGroup());

    }

    private boolean doParseQueryEvent(String queryString, String schemaName, String charset) {
        List<DdlResult> results = DdlParser.parse(queryString, schemaName);
        if (results.isEmpty()) {
            return false;
        }

        QueryType type = results.get(0).getType();
        if (StringUtils.isBlank(schemaName) && StringUtils.isNotBlank(results.get(0).getSchemaName())) {
            schemaName = results.get(0).getSchemaName().toLowerCase();
        }

        String tableCharset = results.get(0).getTableCharset();
        if (QueryType.CREATE == type && tableCharset == null && !DEFAULT_CHARACTER_SET_SERVER.equalsIgnoreCase(charset)) {  //not set and serverCollation != DEFAULT_CHARACTER_SET_SERVER
            String previousQueryString = queryString;
            queryString = DdlParser.appendTableCharset(queryString, charset);
            logger.info("[Create] table sql transfer from {} to {}", previousQueryString, queryString);
        }

        boolean isDml = (type == QueryType.INSERT || type == QueryType.UPDATE || type == QueryType.DELETE || type == QueryType.TRUNCATE);

        if (!isDml) {

            String tableName = results.get(0).getTableName();
            boolean res = schemaManager.apply(schemaName, queryString);
            schemaManager.persistDdl(schemaName, tableName, queryString);
            DDL_LOGGER.info("[Apply] DDL {} with result {}", queryString, res);

            if (StringUtils.isBlank(schemaName) || StringUtils.isBlank(tableName)) {
                DDL_LOGGER.info("[Skip] ddl for one of blank {}.{} with query {}", schemaName, tableName, queryString);
                return false;
            }

            //deal with ghost
            if (DDLPredication.mayGhostOps(tableName)) {
                if (type == QueryType.RENAME && results.size() == 2) {
                    String tableNameOne = results.get(0).getTableName();
                    String originTableNameTwo = results.get(1).getOriTableName();
                    if (DDLPredication.mayGhostRename(tableNameOne, originTableNameTwo)) {
                        schemaName = results.get(1).getSchemaName();
                        if (StringUtils.isNotBlank(schemaName)) {
                            schemaName = schemaName.toLowerCase();
                        }
                        tableName = results.get(1).getTableName();
                        doPersistColumnInfo(schemaName, tableName, queryString);
                        DDL_LOGGER.info("[Rename] detected for {}.{} to go to persist drc table map and ddl event", schemaName, tableName);
                        return true;
                    }

                }
                return false;
            } else {
                doPersistColumnInfo(schemaName, tableName, queryString);
            }

            return true;
        } else {
            if(type == QueryType.TRUNCATE) {
                String tableName = results.get(0).getTableName();
                DDL_LOGGER.info("[Truncate] detected for {}.{}", schemaName, tableName);
                monitorManager.onDdlEvent(schemaName, tableName, queryString, type);
            }
        }

        return false;
    }

    public boolean parseQueryEvent(QueryLogEvent event) {
        String queryString = event.getQuery();
        if (StringUtils.startsWithIgnoreCase(queryString, BEGIN)      ||
            StringUtils.startsWithIgnoreCase(queryString, COMMIT)     ||
            StringUtils.startsWithIgnoreCase(queryString, XA_COMMIT)  ||
            StringUtils.startsWithIgnoreCase(queryString, XA_ROLLBACK)||
            StringUtils.endsWithIgnoreCase(queryString, XA_START)     ||
            StringUtils.endsWithIgnoreCase(queryString, XA_END)) {
            return false;
        } else {
            QueryLogEvent.QueryStatus queryStatus = event.getQueryStatus();
            String charset = DEFAULT_CHARACTER_SET_SERVER;
            if (queryStatus != null) {
                int serverCollation = queryStatus.getServerCollation();
                charset = getServerCollation(serverCollation);
            }
            return doParseQueryEvent(queryString, event.getSchemaName(), charset);
        }
    }

    private String getServerCollation(int serverCollation){
        String charset = CharsetConversion.getCharset(serverCollation);
        if (charset == null) {
            return DEFAULT_CHARACTER_SET_SERVER;
        }
        return charset;
    }

    private boolean doPersistColumnInfo(String schemaName, String tableName, String queryString) {
        TableInfo tableInfo = schemaManager.find(schemaName, tableName);
        if (tableInfo != null) {
            schemaManager.persistColumnInfo(tableInfo, false);
            return true;
        } else {
            DDL_LOGGER.info("[Find] column info {}.{} with query {} return NULL", schemaName, tableName, queryString);
            return false;
        }
    }

}
