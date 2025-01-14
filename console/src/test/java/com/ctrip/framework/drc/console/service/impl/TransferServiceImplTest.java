package com.ctrip.framework.drc.console.service.impl;

import com.ctrip.framework.drc.console.AbstractTest;
import com.ctrip.framework.drc.console.AllTests;
import com.ctrip.framework.drc.console.config.DefaultConsoleConfig;
import com.ctrip.framework.drc.console.enums.BooleanEnum;
import com.ctrip.framework.drc.console.mock.helpers.DcComparator;
import com.ctrip.framework.drc.console.monitor.delay.config.DataCenterService;
import com.ctrip.framework.drc.core.entity.Dc;
import com.ctrip.framework.drc.core.entity.Drc;
import com.ctrip.framework.drc.core.transform.DefaultSaxParser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Map;

import static com.ctrip.framework.drc.console.AllTests.*;

/**
 * @author shenhaibo
 * @version 1.0
 * date: 2020-08-21
 */

public class TransferServiceImplTest extends AbstractTest {

    @InjectMocks
    private TransferServiceImpl transferService;

    @Mock
    private DefaultConsoleConfig consoleConfig;

    @Mock
    private DataCenterService dataCenterService;

    @Mock
    private MetaInfoServiceImpl metaInfoService = new MetaInfoServiceImpl();

    @InjectMocks
    private MetaGenerator metaGenerator = new MetaGenerator();

    private static final String IDC = "shaxx";

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.openMocks(this);
        Mockito.doReturn(IDC).when(dataCenterService).getDc();
        Mockito.doReturn(new HashSet<>()).when(consoleConfig).getPublicCloudDc();
        AllTests.truncateAllMetaDb();

        /**
         * These mocks are typically for loading after all table is first generated or truncated
         */
        String drc1 = "fat-fx-drc1";
        String drc2 = "fat-fx-drc2";
        String drc3 = "fat-fx-drc3";
        String w1 = "drcTestW1";
        String w2 = "drcTestW2";
        Mockito.when(metaInfoService.getMhaGroupId(drc1, drc2)).thenReturn(null);
        Mockito.when(metaInfoService.getMhaGroupId(drc1, drc3)).thenReturn(null);
        Mockito.when(metaInfoService.getMhaGroupId(drc2, drc1)).thenReturn(1L);
        Mockito.when(metaInfoService.getMhaGroupId(drc3, drc1)).thenReturn(2L);
        Mockito.when(metaInfoService.getMhaGroupId(w1, w2)).thenReturn(null);
        Mockito.when(metaInfoService.getMhaGroupId(w2, w1)).thenReturn(3L);
        Mockito.when(metaInfoService.getMhaGroupId(drc3, drc1, BooleanEnum.TRUE)).thenReturn(2L);
        Mockito.when(metaInfoService.getReplicatorGroupId("fat-fx-drc1")).thenReturn(1L);
        Mockito.when(metaInfoService.getReplicatorGroupId("fat-fx-drc2")).thenReturn(2L);
        Mockito.when(metaInfoService.getReplicatorGroupId("fat-fx-drc3")).thenReturn(3L);
        Mockito.when(metaInfoService.getReplicatorGroupId("drcTestW1")).thenReturn(4L);
        Mockito.when(metaInfoService.getReplicatorGroupId("drcTestW2")).thenReturn(5L);


    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * test loadMetaData(String meta), loadOneMetaData(String oneMetaData), getAllMetaData()
     */
    @Test
    public void testLoadAndGetMetaData() throws Exception {
        transferService.loadMetaData(DRC_XML2_1);
        transferService.loadOneMetaData(DRC_XML2_2);
        String actualAllMetaData = metaGenerator.getDrc().toString();
        System.out.println("actualAllMetaData: " + actualAllMetaData);
        Drc expectedDrc = DefaultSaxParser.parse(DRC_XML2);
        Drc actualDrc = DefaultSaxParser.parse(actualAllMetaData);

        Map<String, Dc> expectedDcs = expectedDrc.getDcs();
        Map<String, Dc> actualDcs = actualDrc.getDcs();
        Assert.assertEquals(2, expectedDcs.keySet().size());
        Assert.assertEquals(2, actualDcs.keySet().size());

        for(String key : expectedDcs.keySet()) {
            Dc expectedDc = expectedDcs.get(key);
            Dc actualDc = actualDcs.get(key);
            DcComparator dcMetaComparator = new DcComparator(expectedDc, actualDc);
            dcMetaComparator.compare();
            Assert.assertEquals(0, dcMetaComparator.totalChangedCount());
        }
    }

    /**
     * test removeConfig(String mhaName)
     */
    @Test
    public void testRemoveConfig() throws Exception {

        transferService.loadMetaData(DRC_XML2_1);
        transferService.loadOneMetaData(DRC_XML2_2);

        // do remove
        transferService.removeConfig("drcTestW2", "drcTestW1");

        // assert
        String actualMetaData = metaGenerator.getDrc().toString();
        Drc expectedDrc = DefaultSaxParser.parse(DRC_XML2_1);
        Drc actualDrc = DefaultSaxParser.parse(actualMetaData);

        Map<String, Dc> expectedDcs = expectedDrc.getDcs();
        Map<String, Dc> actualDcs = actualDrc.getDcs();
        Assert.assertEquals(2, expectedDcs.keySet().size());
        Assert.assertEquals(2, actualDcs.keySet().size());

        for(String key : expectedDcs.keySet()) {
            Dc expectedDc = expectedDcs.get(key);
            Dc actualDc = actualDcs.get(key);
            DcComparator dcMetaComparator = new DcComparator(expectedDc, actualDc);
            dcMetaComparator.compare();
            Assert.assertEquals(0, dcMetaComparator.totalChangedCount());
        }
    }

    @Test
    public void testRecoverDeletedDrc() throws Exception {

        transferService.loadMetaData(DRC_XML2_1);
        transferService.loadOneMetaData(DRC_XML2_2);
        transferService.removeConfig("fat-fx-drc3", "fat-fx-drc1");
        System.out.println("pause and watch mysql status");
        
        //some status like monitor_switch cannot recover
        transferService.recoverDeletedDrc("fat-fx-drc3", "fat-fx-drc1");
        
        System.out.println("pause and watch mysql status");
        
    // to make data consistent for test after
        AllTests.truncateAllMetaDb();
        transferService.loadMetaData(DRC_XML2_1);
        transferService.loadOneMetaData(DRC_XML2_2);
        transferService.removeConfig("drcTestW2", "drcTestW1");
    }


}