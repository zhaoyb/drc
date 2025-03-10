package com.ctrip.framework.drc.console.config;

import com.ctrip.xpipe.config.AbstractConfigBean;
import org.springframework.stereotype.Component;

/**
 * @ClassName DomainConifg
 * @Author haodongPan
 * @Date 2021/12/9 20:39
 * @Version: $
 */
@Component
public class DomainConfig extends AbstractConfigBean {
    
    private static final String DAL_SERVICE_PREFIX = "dal.service.prefix";
    private static final String DEFAULT_DAL_SERVICE_PREFIX = "http://localhost:8080/database/";
    
    private static final String DAL_REGISTER_PREFIX = "dal.register.prefix";
    private static final String DEFAULT_DAL_REGISTER_PREFIX = "http://localhost:8080/api/dal/v2/";
    
    private static final String BEACON_PREFIX = "beacon.prefix";
    private static final String DEFAULT_BEACON_PREFIX = "http://localhost:8080/api/v1/";

    private static final String DAL_CLUSTER_URL = "dal.cluster.url";
    private static final String DEFAULT_DAL_CLUSTER_URL = "http://localhost:8080/dalcluster/";
    
    private static final String MYSQL_PRECHECK_URL ="mysql.precheck.url";
    private static final String DEFAULT_MYSQL_PRECHECK_URL ="http://localhost:8080/mysqltool/precheck/";

    private static final String BUILD_NEW_CLUSTER_URL ="build.newcluster.url";
    private static final String DEFAULT_BUILD_NEW_CLUSTER_URL ="http://localhost:8080/mysqltool/buildnewCluster/";

    private static final String SLAVE_CASCADE_URL ="slave.cascade.url";
    private static final String DEFAULT_SLAVE_CASCADE_URL ="http://localhost:8080/mysqltool/slavecascade/";

    private static final String DNS_DEPLOY_URL ="dns.deploy.url";
    private static final String DEFAULT_DNS_DEPLOY_URL ="http://localhost:8080/mysqltool/dnsdeploy/";
    
    private static final String GET_ALL_CLUSTER_URL = "get.allcluster.url";
    private static final String DEFAULT_GET_ALL_CLUSTER_URL = "http://localhost:8080/ops/getallcluster";

    private static final String MYSQL_DB_CLUSTER_URL = "mysql.dbcluster.url";
    private static final String DEFAULT_MYSQL_DB_CLUSTER_URL = "http://localhost:8080/ops/mysqldbcluster";

    private static final String OPS_ACCESS_TOKEN = "ops.access.token";
    private static final String DEFAULT_OPS_ACCESS_TOKEN = "";
    
    public String getDalServicePrefix() {
        return getProperty(DAL_SERVICE_PREFIX,DEFAULT_DAL_SERVICE_PREFIX);
    }
    
    public String getDalRegisterPrefix() {
        return getProperty(DAL_REGISTER_PREFIX,DEFAULT_DAL_REGISTER_PREFIX);
    }
    
    public String getBeaconPrefix() {
        return getProperty(BEACON_PREFIX,DEFAULT_BEACON_PREFIX);
    }

    public String getDalClusterUrl() {
        return getProperty(DAL_CLUSTER_URL,DEFAULT_DAL_CLUSTER_URL);
    }

    public String getMysqlPrecheckUrl() {
        return getProperty(MYSQL_PRECHECK_URL,DEFAULT_MYSQL_PRECHECK_URL);
    }

    public String getBuildNewClusterUrl() {
        return getProperty(BUILD_NEW_CLUSTER_URL,DEFAULT_BUILD_NEW_CLUSTER_URL);
    }
    
    public String getSlaveCascadeUrl() {
        return getProperty(SLAVE_CASCADE_URL,DEFAULT_SLAVE_CASCADE_URL);
    }
    
    public String getDnsDeployUrl() {
        return getProperty(DNS_DEPLOY_URL,DEFAULT_DNS_DEPLOY_URL);
    }
    
    public String getGetAllClusterUrl() {
        return getProperty(GET_ALL_CLUSTER_URL,DEFAULT_GET_ALL_CLUSTER_URL);
    }

    public String getMysqlDbClusterUrl() {
        return getProperty(MYSQL_DB_CLUSTER_URL,DEFAULT_MYSQL_DB_CLUSTER_URL);
    }
    
    public String getOpsAccessToken() {
        return getProperty(OPS_ACCESS_TOKEN,DEFAULT_OPS_ACCESS_TOKEN);
    }
}
