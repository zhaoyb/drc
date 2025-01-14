package com.ctrip.framework.drc.replicator.impl.inbound;

import com.ctrip.framework.drc.core.driver.binlog.LogEventHandler;
import com.ctrip.framework.drc.core.driver.binlog.impl.ITransactionEvent;
import com.ctrip.framework.drc.core.driver.command.netty.endpoint.DefaultEndPoint;
import com.ctrip.framework.drc.core.driver.config.MySQLSlaveConfig;
import com.ctrip.framework.drc.core.server.common.Filter;
import com.ctrip.framework.drc.core.server.config.SystemConfig;
import com.ctrip.framework.drc.replicator.impl.inbound.driver.ReplicatorPooledConnector;
import com.ctrip.framework.drc.replicator.impl.inbound.event.ReplicatorLogEventHandler;
import com.ctrip.framework.drc.replicator.impl.inbound.filter.DefaultFilterChainFactory;
import com.ctrip.framework.drc.replicator.impl.inbound.filter.FilterChainContext;
import com.ctrip.framework.drc.replicator.impl.inbound.transaction.EventTransactionCache;
import com.ctrip.framework.drc.replicator.impl.inbound.transaction.TransactionCache;
import com.ctrip.framework.drc.replicator.impl.monitor.DefaultMonitorManager;
import com.ctrip.framework.drc.replicator.store.EventStore;
import com.ctrip.framework.drc.replicator.store.FilePersistenceEventStore;
import com.ctrip.xpipe.api.endpoint.Endpoint;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.net.InetSocketAddress;

/**
 * Created by mingdongli
 * 2019/9/21 上午11:09.
 */
public class ReplicatorSlaveServerTest extends AbstractServerTest {

    private ReplicatorSlaveServer mySQLServer;

    private Endpoint endpoint;

    private MySQLSlaveConfig mySQLSlaveConfig;

    private EventStore eventStore;

    private TransactionCache transactionCache;

    private DefaultMonitorManager delayMonitor = new DefaultMonitorManager();

    @Mock
    private Filter<ITransactionEvent> filterChain;

    @Before
    public void setUp() throws Exception {
        System.setProperty(SystemConfig.REPLICATOR_FILE_LIMIT, String.valueOf(1024 * 2));
        System.setProperty(SystemConfig.REPLICATOR_WHITE_LIST, String.valueOf(true));  //循环检测通过show variables like ""动态更新，集成测试使用该方式
        endpoint = new DefaultEndPoint(AbstractServerTest.IP, 8386, AbstractServerTest.USER, AbstractServerTest.PASSWORD);
        mySQLSlaveConfig = new MySQLSlaveConfig();
        mySQLSlaveConfig.setEndpoint(endpoint);
        mySQLSlaveConfig.setRegistryKey(AbstractServerTest.DESTINATION, MHA_NAME);
        mySQLServer = new ReplicatorSlaveServer(mySQLSlaveConfig, new ReplicatorPooledConnector(mySQLSlaveConfig.getEndpoint()), null);  //需要连接的master信息
        eventStore = new FilePersistenceEventStore(null, DESTINATION);
        transactionCache = new EventTransactionCache(eventStore, filterChain);
        LogEventHandler eventHandler = new ReplicatorLogEventHandler(transactionCache, delayMonitor, DefaultFilterChainFactory.createFilterChain(new FilterChainContext()));
        mySQLServer.setLogEventHandler(eventHandler);
    }

    @Test
    public void testSlaveId() {
        InetSocketAddress socketAddress = new InetSocketAddress("10.60.44.132", 55944);
        byte[] addr = socketAddress.getAddress().getAddress();
        String destination = "bbztriptrackShardBaseDB_dalcluster";
        int salt = (destination != null) ? destination.hashCode() : 0;
        Assert.assertEquals( ((0x7f & salt) << 24) + ((0xff & (int) addr[1]) << 16) // NL
                + ((0xff & (int) addr[2]) << 8) // NL
                + (0xff & (int) addr[3]) , 1765551236);
    }

    @Test
    public void testStart() throws Exception {
        eventStore.initialize();
        mySQLServer.initialize();
        mySQLServer.start();
        Thread.currentThread().join();
    }

}
