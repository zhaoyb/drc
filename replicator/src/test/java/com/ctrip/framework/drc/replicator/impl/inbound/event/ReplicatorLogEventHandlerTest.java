package com.ctrip.framework.drc.replicator.impl.inbound.event;

import com.ctrip.framework.drc.core.driver.binlog.impl.*;
import com.ctrip.framework.drc.core.driver.binlog.manager.SchemaManager;
import com.ctrip.framework.drc.core.driver.config.MySQLSlaveConfig;
import com.ctrip.framework.drc.core.monitor.kpi.InboundMonitorReport;
import com.ctrip.framework.drc.core.server.common.Filter;
import com.ctrip.framework.drc.core.server.config.MonitorConfig;
import com.ctrip.framework.drc.core.server.config.SystemConfig;
import com.ctrip.framework.drc.core.server.config.replicator.MySQLMasterConfig;
import com.ctrip.framework.drc.core.server.config.replicator.ReplicatorConfig;
import com.ctrip.framework.drc.replicator.container.config.TableFilterConfiguration;
import com.ctrip.framework.drc.replicator.impl.inbound.filter.DefaultFilterChainFactory;
import com.ctrip.framework.drc.replicator.impl.inbound.filter.FilterChainContext;
import com.ctrip.framework.drc.replicator.impl.inbound.filter.LogEventWithGroupFlag;
import com.ctrip.framework.drc.replicator.impl.inbound.filter.transaction.DefaultTransactionFilterChainFactory;
import com.ctrip.framework.drc.replicator.impl.inbound.transaction.EventTransactionCache;
import com.ctrip.framework.drc.replicator.impl.inbound.transaction.TransactionCache;
import com.ctrip.framework.drc.replicator.impl.monitor.DefaultMonitorManager;
import com.ctrip.framework.drc.replicator.store.AbstractTransactionTest;
import com.ctrip.framework.drc.replicator.store.FilePersistenceEventStore;
import com.ctrip.framework.drc.replicator.store.manager.file.FileManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.netty.buffer.ByteBuf;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.ctrip.framework.drc.core.driver.binlog.constant.LogEventType.table_map_log_event;
import static com.ctrip.framework.drc.core.driver.config.GlobalConfig.BU;
import static com.ctrip.framework.drc.core.server.config.SystemConfig.EMPTY_DRC_UUID_EVENT_SIZE;
import static com.ctrip.framework.drc.core.server.config.SystemConfig.EMPTY_PREVIOUS_GTID_EVENT_SIZE;
import static com.ctrip.framework.drc.replicator.store.manager.file.DefaultFileManager.FORMAT_LOG_EVENT_SIZE;
import static com.ctrip.framework.drc.replicator.store.manager.file.DefaultFileManager.LOG_EVENT_START;

/**
 * Created by @author zhuYongMing on 2019/9/18.
 */
public class ReplicatorLogEventHandlerTest extends AbstractTransactionTest {

    private ReplicatorLogEventHandler logEventHandler;

    private Filter<LogEventWithGroupFlag> flagFilter;

    @Mock
    private SchemaManager schemaManager;

    @Mock
    private DefaultMonitorManager delayMonitor;

    @Mock
    private InboundMonitorReport inboundMonitorReport;

    private Filter<ITransactionEvent> filterChain = DefaultTransactionFilterChainFactory.createFilterChain(delayMonitor);

    private FilePersistenceEventStore filePersistenceEventStore;

    private TransactionCache transactionCache;

    private FileManager fileManager;

    private String clusterName = "unitTest";

    private static final int TABLE_MAP_EVENT_SIZE = 19 + 55 + 1;  //1 for identifier

    private ReplicatorConfig replicatorConfig = new ReplicatorConfig();

    private  Set<UUID> uuidSet = Sets.newHashSet();

    private  Set<String> tableNames = Sets.newHashSet();

    private static final String UUID_1 = "56027356-0d03-11ea-a2f0-c6a9fbf1c3fe";

    private FilterChainContext filterChainContext;

    private TableFilterConfiguration tableFilterConfiguration = new TableFilterConfiguration();

    @Before
    public void setUp() throws Exception {
        super.initMocks();
        uuidSet.add(UUID.fromString(UUID_1));
        initReplicatorConfig();
        filePersistenceEventStore = new FilePersistenceEventStore(schemaManager, clusterName);
        filePersistenceEventStore.initialize();
        filePersistenceEventStore.start();

        transactionCache = new EventTransactionCache(filePersistenceEventStore, filterChain);
        transactionCache.initialize();
        transactionCache.start();

        fileManager = filePersistenceEventStore.getFileManager();
        File logDir = fileManager.getDataDir();
        deleteFiles(logDir);

        filterChainContext = new FilterChainContext(uuidSet, tableNames, schemaManager, inboundMonitorReport, transactionCache, delayMonitor, clusterName, tableFilterConfiguration);
        flagFilter = DefaultFilterChainFactory.createFilterChain(filterChainContext);

        logEventHandler = new ReplicatorLogEventHandler(transactionCache, delayMonitor, flagFilter);
    }

    @After
    public void tearDown() throws Exception {
        File logDir = fileManager.getDataDir();
        deleteFiles(logDir);
    }

    private void initReplicatorConfig() {
        MonitorConfig monitorConfig = new MonitorConfig(100023928, BU, "SHAJQ");
        replicatorConfig.setMonitorConfig(monitorConfig);
        MySQLMasterConfig mySQLMasterConfig = new MySQLMasterConfig();
        mySQLMasterConfig.setIp("127.0.0.1");
        mySQLMasterConfig.setPort(1234);
        replicatorConfig.setMySQLMasterConfig(mySQLMasterConfig);
        MySQLSlaveConfig mySQLSlaveConfig = new MySQLSlaveConfig();
        mySQLSlaveConfig.setRegistryKey(clusterName, SystemConfig.MHA_NAME_TEST);
        replicatorConfig.setMySQLSlaveConfig(mySQLSlaveConfig);
    }

    @Test
    public void testXidWithFilterFalse() {
        File logDir = fileManager.getDataDir();
        deleteFiles(logDir);
        doWriteTransaction(false);

        File file = fileManager.getCurrentLogFile();
        long length = file.length();
        Assert.assertEquals(length, LOG_EVENT_START + EMPTY_PREVIOUS_GTID_EVENT_SIZE + EMPTY_SCHEMA_EVENT_SIZE + EMPTY_DRC_UUID_EVENT_SIZE + DrcIndexLogEvent.FIX_SIZE + FORMAT_LOG_EVENT_SIZE + 3 * ((GTID_ZISE + 4) + XID_ZISE));
        logDir = fileManager.getDataDir();
        deleteFiles(logDir);
    }

    @Test
    public void testXidWithFilterTrue() {
        File logDir = fileManager.getDataDir();
        deleteFiles(logDir);

        doWriteTransaction(true);

        File file = fileManager.getCurrentLogFile();
        long length = file.length();
        Assert.assertEquals(length, LOG_EVENT_START + EMPTY_PREVIOUS_GTID_EVENT_SIZE + EMPTY_SCHEMA_EVENT_SIZE + EMPTY_DRC_UUID_EVENT_SIZE + DrcIndexLogEvent.FIX_SIZE + FORMAT_LOG_EVENT_SIZE + 3 * (GTID_ZISE + 4));
        logDir = fileManager.getDataDir();
        deleteFiles(logDir);
    }

    @Test
    public void testXidWithFilterBoth() {
        doTestBoth(false, true);
    }

    @Test
    public void testXidWithFilterTrueAndFalse() {
        doTestBoth(true, false);
    }

    private void doTestBoth(boolean first, boolean second) {
        File logDir = fileManager.getDataDir();
        deleteFiles(logDir);

        doWriteTransaction(first); //3 pair

        doWriteTransaction(second); //3 gtid

        File file = fileManager.getCurrentLogFile();
        long length = file.length();
        Assert.assertEquals(length, LOG_EVENT_START + EMPTY_PREVIOUS_GTID_EVENT_SIZE + EMPTY_SCHEMA_EVENT_SIZE + EMPTY_DRC_UUID_EVENT_SIZE + DrcIndexLogEvent.FIX_SIZE + FORMAT_LOG_EVENT_SIZE + 3 * ((GTID_ZISE + 4) + XID_ZISE) + 3 * (GTID_ZISE + 4));

        logDir = fileManager.getDataDir();
        deleteFiles(logDir);
    }

    @Test
    public void testFilteredDb() throws Exception {
        File logDir = fileManager.getDataDir();
        deleteFiles(logDir);

        writeFilteredTransaction();

        File file = fileManager.getCurrentLogFile();
        long length = file.length();
        Assert.assertEquals(length, LOG_EVENT_START + EMPTY_PREVIOUS_GTID_EVENT_SIZE + FORMAT_LOG_EVENT_SIZE + EMPTY_SCHEMA_EVENT_SIZE + EMPTY_DRC_UUID_EVENT_SIZE + DrcIndexLogEvent.FIX_SIZE + TABLE_MAP_EVENT_SIZE + 2 * ((GTID_ZISE + 4) + XID_ZISE));

        logDir = fileManager.getDataDir();
        deleteFiles(logDir);
    }

    private void writeFilteredTransaction() throws Exception {
        GtidLogEvent gtidLogEvent = getGtidLogEvent();
        gtidLogEvent.setServerUUID(UUID.fromString(UUID_1));
        logEventHandler.onLogEvent(gtidLogEvent, null, null);
        TableMapLogEvent tableMapLogEvent = getFilteredTableMapLogEvent();
        logEventHandler.onLogEvent(tableMapLogEvent, null, null);
        XidLogEvent xidLogEvent = getXidLogEvent();
        logEventHandler.onLogEvent(xidLogEvent, null, null);


        gtidLogEvent = getGtidLogEvent();
        gtidLogEvent.setServerUUID(UUID.fromString(UUID_1));
        logEventHandler.onLogEvent(gtidLogEvent, null, null);
        tableMapLogEvent = getNonFilteredTableMapLogEvent();
        logEventHandler.onLogEvent(tableMapLogEvent, null, null);
        xidLogEvent = getXidLogEvent();
        logEventHandler.onLogEvent(xidLogEvent, null, null);
    }

    private void doWriteTransaction(boolean filtered) {
        GtidLogEvent gtidLogEvent = getGtidLogEvent();
        if (!filtered) {
            gtidLogEvent.setServerUUID(UUID.fromString(UUID_1));
        }
        XidLogEvent xidLogEvent = getXidLogEvent();
        logEventHandler.onLogEvent(gtidLogEvent, null, null);
        String currentGtid = logEventHandler.getCurrentGtid();
        Assert.assertTrue(!filtered ? currentGtid.length() > 0 : currentGtid.length() == 0);
        logEventHandler.onLogEvent(xidLogEvent, null, null);
        currentGtid = logEventHandler.getCurrentGtid();
        Assert.assertTrue(currentGtid.length() == 0);

        gtidLogEvent = getGtidLogEvent();
        if (!filtered) {
            gtidLogEvent.setServerUUID(UUID.fromString(UUID_1));
        }
        logEventHandler.onLogEvent(gtidLogEvent, null, null);

        gtidLogEvent = getGtidLogEvent();
        if (!filtered) {
            gtidLogEvent.setServerUUID(UUID.fromString(UUID_1));
        }
        logEventHandler.onLogEvent(gtidLogEvent, null, null);
        xidLogEvent = getXidLogEvent();
        logEventHandler.onLogEvent(xidLogEvent, null, null);

        try {
            fileManager.flush();
        } catch (IOException e) {
        }

    }

    private GtidLogEvent getGtidLogEvent() {
        final ByteBuf byteBuf = super.getGtidEvent();
        GtidLogEvent gtidLogEvent = new GtidLogEvent().read(byteBuf);
        byteBuf.release();
        return gtidLogEvent;
    }

    private XidLogEvent getXidLogEvent() {
        final ByteBuf byteBuf = super.getXidEvent();
        XidLogEvent xidLogEvent = new XidLogEvent().read(byteBuf);
        byteBuf.release();
        return xidLogEvent;
    }

    private TableMapLogEvent getFilteredTableMapLogEvent() throws IOException {
        List<TableMapLogEvent.Column> columns = mockColumns();
        TableMapLogEvent constructorTableMapLogEvent = new TableMapLogEvent(
                1L, 813, 123, "configdb", "unitest", columns, null, table_map_log_event, 0
        );
        return constructorTableMapLogEvent;
    }

    private TableMapLogEvent getNonFilteredTableMapLogEvent() throws IOException {
        List<TableMapLogEvent.Column> columns = mockColumns();
        TableMapLogEvent constructorTableMapLogEvent = new TableMapLogEvent(
                1L, 813, 123, "configdbs", "unitest", columns, null, table_map_log_event, 0
        );
        return constructorTableMapLogEvent;
    }

    public static List<TableMapLogEvent.Column> mockColumns() {
        List<TableMapLogEvent.Column> columns = Lists.newArrayList();
        TableMapLogEvent.Column column1 = new TableMapLogEvent.Column("id", true, "int", null, "10", null, null, null, null, "int(11)", null, null, "default");
        Assert.assertFalse(column1.isOnUpdate());
        Assert.assertFalse(column1.isPk());
        Assert.assertFalse(column1.isUk());
        Assert.assertEquals("default", column1.getColumnDefault());

        columns.add(column1);

        return columns;
    }
}
