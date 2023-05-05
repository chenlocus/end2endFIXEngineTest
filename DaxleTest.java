import org.testng.Assert;
import org.testng.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.ConfigError;
import quickfix.LogFactory;
import quickfix.Message;
import utility.Order;
import utility.TestContext;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static utility.Order.createOrder;

public class DaxleTest {
    private LogFactory logFactory;
    private static TestContext testContext;
    private static final Logger LOG = LoggerFactory.getLogger(DaxleTest.class);

    @BeforeClass
    public static void setUp() throws ConfigError, FileNotFoundException {
        // Create a new SessionSettings object and load the configuration file
        final String CONNECTIVITY_CFG = "conf/bidfx_session.cfg";
        //final String CONNECTIVITY_CFG = "conf/session.cfg";
        testContext = new TestContext(CONNECTIVITY_CFG);
        testContext.start();
    }

    @Test
    public void testCreateTWAPDurationOrderOK() throws Exception {
        Map<String, Object> nos = new HashMap<>();
        nos.put("StrategyType", "TWAP_DURATION");
        nos.put("OrderQty", 1200000);
        Order order = createOrder(testContext,nos);

        Map<String, Object> expectedNew = new HashMap<>();
        expectedNew.put("OrdStatus", 0);
        Message msgReceivedNew = order.waitUntil(expectedNew,120);
        assertNotNull(msgReceivedNew);
        Assert.assertEquals("1200000", msgReceivedNew.getString(38));
        Assert.assertEquals("1", msgReceivedNew.getString(54));
        Assert.assertEquals("GBP", msgReceivedNew.getString(15));
        Assert.assertEquals("GBP/USD", msgReceivedNew.getString(55));
        Assert.assertEquals("1200000", msgReceivedNew.getString(151));
        Assert.assertEquals("FXSPOT", msgReceivedNew.getString(167));

//        Map<String, Object> expectedFilled = new HashMap<>();
//        expectedFilled.put("OrdStatus", 2);
//        Message msgReceivedFilled = order.waitUntil(expectedFilled,360);
//        assertNotNull(msgReceivedFilled);
//        Assert.assertEquals("F", msgReceivedFilled.getString(150));
//        Assert.assertEquals("0", msgReceivedFilled.getString(151));
    }

    @Test
    public void testCreateTWAPEODOrderOK() throws Exception {
        Map<String, Object> nos = new HashMap<>();
        nos.put("StrategyType", "TWAP_EndOfDay");
        nos.put("OrderQty", 1300000);
        Order order = createOrder(testContext,nos);

        Map<String, Object> expectedNew = new HashMap<>();
        expectedNew.put("OrdStatus", 0);
        Message msgReceivedNew = order.waitUntil(expectedNew,120);
        assertNotNull(msgReceivedNew);
        Assert.assertEquals("1300000", msgReceivedNew.getString(38));

        Map<String, Object> expectedFilled = new HashMap<>();
        expectedFilled.put("OrdStatus", 2);
        Message msgReceivedFilled = order.waitUntil(expectedFilled,360);
        assertNotNull(msgReceivedFilled);
        Assert.assertEquals("F", msgReceivedFilled.getString(150));
        Assert.assertEquals("0", msgReceivedFilled.getString(151));
    }

    @Test
    public void testCreateDynamicNeutralNowOrderOK() throws Exception {
        Map<String, Object> nos = new HashMap<>();
        nos.put("StrategyType", "DYNAMIC_Neutral_NOW");
        nos.put("OrderQty", 1400000);
        Order order = createOrder(testContext,nos);

        Map<String, Object> expectedNew = new HashMap<>();
        expectedNew.put("OrdStatus", 0);
        Message msgReceivedNew = order.waitUntil(expectedNew,120);
        assertNotNull(msgReceivedNew);
        Assert.assertEquals("1400000", msgReceivedNew.getString(38));

        Map<String, Object> expectedFilled = new HashMap<>();
        expectedFilled.put("OrdStatus", 2);
        Message msgReceivedFilled = order.waitUntil(expectedFilled,360);
        assertNotNull(msgReceivedFilled);
        Assert.assertEquals("F", msgReceivedFilled.getString(150));
        Assert.assertEquals("0", msgReceivedFilled.getString(151));
    }

    @Test
    public void testCreateDynamicAggressiveNowOrderOK() throws Exception {
        Map<String, Object> nos = new HashMap<>();
        nos.put("StrategyType", "DYNAMIC_Aggressive_NOW");
        nos.put("OrderQty", 1500000);
        Order order = createOrder(testContext,nos);

        Map<String, Object> expectedNew = new HashMap<>();
        expectedNew.put("OrdStatus", 0);
        Message msgReceivedNew = order.waitUntil(expectedNew,120);
        assertNotNull(msgReceivedNew);
        Assert.assertEquals("1500000", msgReceivedNew.getString(38));

        Map<String, Object> expectedFilled = new HashMap<>();
        expectedFilled.put("OrdStatus", 2);
        Message msgReceivedFilled = order.waitUntil(expectedFilled,360);
        assertNotNull(msgReceivedFilled);
        Assert.assertEquals("F", msgReceivedFilled.getString(150));
        Assert.assertEquals("0", msgReceivedFilled.getString(151));
    }

    @Test
    public void testCreatePEGAggressiveNowOrderOK() throws Exception {
        Map<String, Object> nos = new HashMap<>();
        nos.put("StrategyType", "PEG_Agressive_NOW");
        nos.put("OrderQty", 1600000);
        Order order = createOrder(testContext,nos);

        Map<String, Object> expectedNew = new HashMap<>();
        expectedNew.put("OrdStatus", 0);
        Message msgReceivedNew = order.waitUntil(expectedNew,120);
        assertNotNull(msgReceivedNew);
        Assert.assertEquals("1600000", msgReceivedNew.getString(38));

        Map<String, Object> expectedFilled = new HashMap<>();
        expectedFilled.put("OrdStatus", 2);
        Message msgReceivedFilled = order.waitUntil(expectedFilled,360);
        assertNotNull(msgReceivedFilled);
        Assert.assertEquals("F", msgReceivedFilled.getString(150));
        Assert.assertEquals("0", msgReceivedFilled.getString(151));
    }

    @Test
    public void testCreatePEGNeutralNowOrderOK() throws Exception {
        Map<String, Object> nos = new HashMap<>();
        nos.put("StrategyType", "PEG_Neutral_NOW");
        nos.put("OrderQty", 1700000);
        Order order = createOrder(testContext,nos);

        Map<String, Object> expectedNew = new HashMap<>();
        expectedNew.put("OrdStatus", 0);
        Message msgReceivedNew = order.waitUntil(expectedNew,120);
        assertNotNull(msgReceivedNew);
        Assert.assertEquals("1700000", msgReceivedNew.getString(38));

        Map<String, Object> expectedFilled = new HashMap<>();
        expectedFilled.put("OrdStatus", 2);
        Message msgReceivedFilled = order.waitUntil(expectedFilled,360);
        assertNotNull(msgReceivedFilled);
        Assert.assertEquals("F", msgReceivedFilled.getString(150));
        Assert.assertEquals("0", msgReceivedFilled.getString(151));
    }

    @Test
    public void testCreatePEGPassiveNowOrderOK() throws Exception {
        Map<String, Object> nos = new HashMap<>();
        nos.put("StrategyType", "PEG_Passive_NOW");
        nos.put("OrderQty", 1800000);
        Order order = createOrder(testContext,nos);

        Map<String, Object> expectedNew = new HashMap<>();
        expectedNew.put("OrdStatus", 0);
        Message msgReceivedNew = order.waitUntil(expectedNew,120);
        assertNotNull(msgReceivedNew);
        Assert.assertEquals("1800000", msgReceivedNew.getString(38));

        Map<String, Object> expectedFilled = new HashMap<>();
        expectedFilled.put("OrdStatus", 2);
        Message msgReceivedFilled = order.waitUntil(expectedFilled,360);
        assertNotNull(msgReceivedFilled);
        Assert.assertEquals("F", msgReceivedFilled.getString(150));
        Assert.assertEquals("0", msgReceivedFilled.getString(151));
    }

    @Test
    public void testCreatePASSIVENowOrderOK() throws Exception {
        Map<String, Object> nos = new HashMap<>();
        nos.put("StrategyType", "PASSIVE_NOW");
        nos.put("OrderQty", 1900000);
        Order order = createOrder(testContext,nos);

        Map<String, Object> expectedNew = new HashMap<>();
        expectedNew.put("OrdStatus", 0);
        Message msgReceivedNew = order.waitUntil(expectedNew,120);
        assertNotNull(msgReceivedNew);
        Assert.assertEquals("1900000", msgReceivedNew.getString(38));

//        Map<String, Object> expectedFilled = new HashMap<>();
//        expectedFilled.put("OrdStatus", 2);
//        Message msgReceivedFilled = order.waitUntil(expectedFilled,360);
//        assertNotNull(msgReceivedFilled);
//        Assert.assertEquals("F", msgReceivedFilled.getString(150));
//        Assert.assertEquals("0", msgReceivedFilled.getString(151));
    }

    @Test
    public void testCreateLimitParticipateNowOrderOK() throws Exception {
        Map<String, Object> nos = new HashMap<>();
        nos.put("StrategyType", "LIMIT_PARTICIPATE_NOW");
        nos.put("OrderQty", 1900000);
        Order order = createOrder(testContext, nos);

        Map<String, Object> expectedNew = new HashMap<>();
        expectedNew.put("OrdStatus", 0);
        Message msgReceivedNew = order.waitUntil(expectedNew, 120);
        assertNotNull(msgReceivedNew);
        Assert.assertEquals("1900000", msgReceivedNew.getString(38));
    }

    @Test
    public void testCreateBidfxTWAPOrderOK() throws Exception {
        Map<String, Object> nos = new HashMap<>();
        nos.put("StrategyType", "BFX_TWAP");
        nos.put("OrderQty", 1200000);
        Order order = createOrder(testContext,nos);

        Map<String, Object> expectedNew = new HashMap<>();
        expectedNew.put("OrdStatus", 0);
        Message msgReceivedNew = order.waitUntil(expectedNew,120);
        assertNotNull(msgReceivedNew);
        Assert.assertEquals("1200000", msgReceivedNew.getString(38));
        Assert.assertEquals("1", msgReceivedNew.getString(54));
        Assert.assertEquals("GBP", msgReceivedNew.getString(15));
        Assert.assertEquals("GBP/USD", msgReceivedNew.getString(55));
        Assert.assertEquals("1200000", msgReceivedNew.getString(151));
        Assert.assertEquals("FXSPOT", msgReceivedNew.getString(167));

//        Map<String, Object> expectedFilled = new HashMap<>();
//        expectedFilled.put("OrdStatus", 2);
//        Message msgReceivedFilled = order.waitUntil(expectedFilled,360);
//        assertNotNull(msgReceivedFilled);
//        Assert.assertEquals("F", msgReceivedFilled.getString(150));
//        Assert.assertEquals("0", msgReceivedFilled.getString(151));
    }

    @AfterClass
    public static void tearDown() {
        //testContext.stop();
    }
}


