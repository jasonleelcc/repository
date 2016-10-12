package com.nec.test;

import iwin.util.ClientAgent;
import iwin.xml.XMLParser2;
import iwin.xml.XmlManipulation;

import java.io.File;
import java.net.InetAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * 網頁類交易
 * 
 * @author leotu@nec.com.tw
 */
public class SimulateBankClient extends TestBase {
	final protected static Logger log = LoggerFactory.getLogger(SimulateBankClient.class);

	//
	protected ClientAgent ca = null;

	// @BeforeClass
	// public static void setUpBeforeClass() throws Exception {
	// log.debug("...");
	// }
	//
	// @AfterClass
	// public static void tearDownAfterClass() throws Exception {
	// log.debug("...");
	// }

	//
	@Before
	public void before() throws Exception {
		log.debug("...");
		try {
			// XXX: UB Testing Env.
			//InetAddress address=InetAddress.getByName("172.16.2.82"); // necadmin/`1qaz2wsx;
			//InetAddress address=InetAddress.getByName("192.168.2.100");
			InetAddress address = InetAddress.getLocalHost();
			ca = new ClientAgent(address, 30002);
			
			// XXX: UB Online Env.
			// InetAddress address=InetAddress.getByName("mbank.ubot.com.tw");
			//InetAddress address = InetAddress.getByName("210.65.249.143");
			//ca = new ClientAgent(address, 8080);
			ca.connect();
		} catch (Exception e) {
			log.error("", e);
			throw e;
		}
	}

	@After
	public void after() throws Exception {
		log.debug("...");
		try {
			if (ca != null) {
				ca.disconnect();
			}
		} catch (Exception e) {
			log.error("", e);
			throw e;
		}
	}

	// =====================================================

	/**
	 * OK
	 * 
	 * <pre>
	 * 二、各幣別匯率查詢(IBKMWUM9523)
	 * ServiceType="CurExchangeRateRq"
	 * </pre>
	 */
	//@Test
	public void testCurExchangeRateRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test/CurExchangeRateRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	/**
	 * <pre>
	 * 三、新台幣利率查詢(IBKUCPM9524)
	 * ServiceType="TWDIntRateRq"
	 * </pre>
	 */
	//@Test
	public void testTWDIntRateRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test/TWDIntRateRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}
	
	/**
	 * <pre>
	 * 四、外幣利率查詢(IBKUCPTA870)
	 * ServiceType="ForIntRateRq"
	 * </pre>
	 */
	@Test
	public void testForIntRateRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test/ForIntRateRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	/**
	 * OK
	 * 
	 * <pre>
	 * 五、歸戶帳號查詢(SSOGETACT)
	 * ServiceType="GetAccountRq"
	 * </pre>
	 */
	// @Test
	public void testGetAccountRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test/GetAccountRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	/**
	 * OK
	 * 
	 * <pre>
	 * 六、台幣存款帳戶交易明細查詢(IBKUCPM1030)
	 * ServiceType="NtdDepActTxnRq"
	 * </pre>
	 */
	// @Test
	public void testNtdDepActTxnRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test/NtdDepActTxnRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	/**
	 * OK
	 * 
	 * <pre>
	 * 七、(網銀)台幣存款帳戶交易明細查詢(IBKTXNLOGS)
	 * ServiceType="NBNtdDepActTxnRq"
	 * </pre>
	 */
	// @Test
	public void testNBNtdDepActTxnRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test/NBNtdDepActTxnRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	/**
	 * OK
	 * 
	 * <pre>
	 * 八、台幣帳戶餘額查詢(IBKVIN)
	 * ServiceType="NtdDepBalRq"
	 * </pre>
	 */
	// @Test
	public void testNtdDepBalRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test/NtdDepBalRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	/**
	 * OK
	 * 
	 * <pre>
	 * 九、客戶中文姓名與定存帳號查詢(IBKUCPM1032)
	 * ServiceType="FixDepActRq"
	 * </pre>
	 */
	// @Test
	public void testFixDepActRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test/FixDepActRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	/**
	 * <pre>
	 * 十、台幣存單明細查詢(IBKUCPM2038)
	 * ServiceType="NtdFixDepActRq"
	 * </pre>
	 */
	//@Test
	public void testNtdFixDepActRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test/NtdFixDepActRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	/**
	 * <pre>
	 * 十一、台幣支票明細查詢(IBKUCPM3069)
	 * ServiceType="NtdChkActTxnRq"
	 * </pre>
	 */
	// @Test
	public void testNtdChkActTxnRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test/NtdChkActTxnRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	/**
	 * <pre>
	 * 十二、外幣餘額查詢及所有帳號(IBKUCPTA071)
	 * ServiceType="FrnAllActRq"
	 * </pre>
	 */
	// @Test
	public void testFrnAllActRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test/FrnAllActRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	/**
	 * <pre>
	 * 十三、外幣存單餘額查詢(IBKUCPTB031)
	 * ServiceType="FrnFixActBalRq"
	 * </pre>
	 */
	//@Test
	public void testFrnFixActBalRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test/FrnFixActBalRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	/**
	 * <pre>
	 * 十四、外幣存摺交易明細查詢(IBKUCPTA081)
	 * ServiceType="FrnFixActBalRq"
	 * </pre>
	 */
	//@Test
	public void testFrnDepActTxnRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test/FrnDepActTxnRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	/**
	 * OK
	 * 
	 * <pre>
	 * 十五、外幣存單明細查詢(IBKUCPTB032)
	 * ServiceType="FrnFixActTxnRq"
	 * </pre>
	 */
	// @Test
	public void testFrnFixActTxnRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test/FrnFixActTxnRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	/**
	 * <pre>
	 * 十六、外幣查詢網路銀行歷史交易明細(IBKTXNLOGS1)
	 * ServiceType="DBFrnDepActTxnRq"
	 * </pre>
	 */
	// @Test
	public void testDBFrnDepActTxnRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test/DBFrnDepActTxnRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	/**
	 * <pre>
	 * 十七、信用卡明細查詢(IBK3060)
	 * ServiceType="CreditTxnRq"
	 * </pre>
	 */
	//@Test
	public void testCreditTxnRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test/CreditTxnRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	/**
	 * <pre>
	 * 十八、信用卡未出帳消費明細查詢(IBK9011)
	 * ServiceType="CreditNotDueTxnRq"
	 * </pre>
	 */
	//@Test
	public void testCreditNotDueTxnRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test/CreditNotDueTxnRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	/**
	 * <pre>
	 * 十九、查詢信用卡可用額度(IBK1020)
	 * ServiceType="CreditAvlAmtRq"
	 * </pre>
	 */
	//@Test
	public void testCreditAvlAmtRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test/CreditAvlAmtRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	/**
	 * <pre>
	 * 二十、查詢信用卡紅利點數及帳單日(IBK7021)
	 * ServiceType="CreditBonusRq"
	 * </pre>
	 */
	// @Test
	public void testCreditBonusRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test/CreditBonusRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	/**
	 * XML的轉換
	 * 
	 * @param xmlFile
	 */
	public void getXMLParser2(String xmlFile) {
		log.info("xmlFile=[" + xmlFile + "]");
		try {
			XMLParser2 xmlParser = new XMLParser2();
			Document mobileRequestDoc = xmlParser.fileToDoc(xmlFile);
			String data = xmlParser.docToString(mobileRequestDoc);
			log.info("data=[" + data + "]");
			ca.send(data);
			String result = ca.retrieve();
			log.info("result=[" + formatXML(result) + "]");
		} catch (Exception e) {
			log.error("xmlFile=[" + xmlFile + "]", e);
		}
	}

	// =====================================================
	protected String formatXML(String xml) {
		int idx = xml.lastIndexOf('>');
		for (int i = idx + 1; idx != -1 && i < xml.length(); i++) {
			log.warn("xml[" + i + "]=[0x" + Integer.toHexString((int)xml.charAt(i)) + "]");
		}
		xml = xml.trim(); // 聲達後面多加byte: 0x02 0x03
		return XmlManipulation.nodeToXmlString(XmlManipulation.xmlStringToNode(xml), false, true);
	}
}
