package com.ctrip.framework.drc.console.monitor.healthcheck.task;

import com.ctrip.xpipe.api.endpoint.Endpoint;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jdbc.pool.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author shenhaibo
 * @version 1.0
 * date: 2019-12-25
 */
public class UuidQueryTask extends AbstractQueryTask<String> {

    private static final String SERVER_UUID_COMMAND = "show global variables like \"server_uuid\";";

    public UuidQueryTask(Endpoint master) {
        super(master);
    }

    @Override
    protected String doQuery() {
        return getUuid(master);
    }

    @SuppressWarnings("findbugs:RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
    protected String getUuid(Endpoint endpoint) {
        DataSource dataSource = dataSourceManager.getDataSource(endpoint);
        try (Connection connection = dataSource.getConnection()){
            try (Statement statement = connection.createStatement()) {
                try (ResultSet uuidResultSet = statement.executeQuery(SERVER_UUID_COMMAND);) {
                    if (uuidResultSet.next()) {
                        return uuidResultSet.getString("Value");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("[[db={}:{}]]query uuid error", endpoint.getHost(), endpoint.getPort(), e);
        }
        return StringUtils.EMPTY;
    }
}
