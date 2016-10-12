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
 * 網頁類交易(E-Card)
 * 
 * @author leotu@nec.com.tw
 */
public class SimulateEcardClient extends TestBase {
	final protected static Logger log = LoggerFactory.getLogger(SimulateEcardClient.class);

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
	// /**
	// * <pre>
	// * (ECARD) 信用卡可用餘額查詢: 7021(FUN-CODE=02)、1020
	// * ServiceType="AvailAmtRq"
	// * </pre>
	// */
	// //@Test
	// public void testAvailAmtRq_bypass() {
	// public void testMBRLOGIN() {
	// AxisProperties.setProperty("http.proxyHost", "172.16.2.82");
	// AxisProperties.setProperty("http.proxyPort", "3030");
	// static protected String mobileWsEndpoint = "http://172.28.233.13:8080/MobileService.asmx";
	// log.debug("----------------------------------------------------------------------------------");
	// String xmlFile = new File(referencePath, "/XMLVol/test_ecard/7021-02.xml").toString();
	// log.debug("BEGIN...[" + xmlFile + "]");
	// try {
	// String strTXNID = "7021";
	// InputStream input = new FileInputStream(xmlFile);
	// String strText = IOUtils.toString(input, "UTF8");
	// input.close();
	// //
	// log.debug("strText=[" + strText + "]");
	// MobileServiceSoapProxy mobileWebService = new MobileServiceSoapProxy(mobileWsEndpoint);
	// String rtnXml = mobileWebService.runTXN(strTXNID, strText);
	// log.debug("rtnXml=[" + rtnXml + "]");
	// } catch (Exception e) {
	// log.error("xmlFile=[" + xmlFile + "]", e);
	// }
	// log.debug("END.");
	// }

	// =====================================================
	/**
	 * OK
	 * 
	 * <pre>
	 * 二十一、(ECARD)信用卡紅利積點查詢: MOQRYBP
	 * ServiceType="BonusPointRq"
	 * </pre>
	 */
	// @Test
	public void testBonusPointRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_ecard/BonusPointRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * OK
	 * 
	 * <pre>
	 * 二十三、(ECARD) 信用卡消費明細查詢: (當期消費明細): 7021(FUN-CODE=04)、3062(FUN-CODE=01) ==> <REVE-TYPE>1</REVE-TYPE>
	 * 二十三、(ECARD) 信用卡消費明細查詢: (近三個月消費明細): 7021(FUN-CODE=04)、3062(FUN-CODE=02) ==> <REVE-TYPE>2</REVE-TYPE>
	 * 二十三、(ECARD) 信用卡消費明細查詢: (未出帳單消費明細): 7021(FUN-CODE=04)、9011 ==> <REVE-TYPE>3</REVE-TYPE>
	 * ServiceType="ExpDetailRq"
	 * </pre>
	 */
	@Test
	public void testExpDetailRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_ecard/ExpDetailRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * OK
	 * 
	 * <pre>
	 * 二十四、(ECARD) 信用卡可用餘額查詢: 7021(FUN-CODE=04)、1020
	 * ServiceType="AvailAmtRq"
	 * </pre>
	 */
	//@Test
	public void testAvailAmtRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_ecard/AvailAmtRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * 
	 * 
	 * <pre>
	 * 二十五、(ECARD) 信用卡近三月繳款紀錄查詢: 7021(FUN-CODE=04)、9020、3062
	 * ServiceType="PayRecord3MRq"
	 * </pre>
	 */
	//@Test
	public void testPayRecord3MRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_ecard/PayRecord3MRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * OK
	 * 
	 * <pre>
	 * 二十六、(ECARD)線上註冊: MBRREG、7021(FUN-CODE=05)
	 * ServiceType="OnlineRegisterRq"
	 * </pre>
	 */
	// @Test
	public void testOnlineRegisterRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_ecard/OnlineRegisterRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * OK
	 * 
	 * <pre>
	 * 二十七、(ECARD)與我聯絡: DISCUSS
	 * ServiceType="ContactUsRq"
	 * </pre>
	 */
	// @Test
	public void testContactUsRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_ecard/ContactUsRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * OK
	 * 
	 * <pre>
	 * 二十八、(ECARD)啟動密碼: ENABLEPWD
	 * ServiceType="ActivatePasswordRq"
	 * </pre>
	 */
	// @Test
	public void testActivatePasswordRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_ecard/ActivatePasswordRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * 
	 * 
	 * <pre>
	 * 二十九、(ECARD)當期未繳款紀錄: 7021(FUN-CODE=04)、9020
	 * ServiceType="PayRecordNonRq"
	 * </pre>
	 */
	//@Test
	public void testPayRecordNonRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_ecard/PayRecordNonRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}
	
	/**
	 * XML的轉換
	 * 
	 * @param xmlFile
	 */
	protected void getXMLParser2(String xmlFile) {
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
		for (int i = idx + 1;  idx != -1 && i < xml.length(); i++) {
			log.warn("xml[" + i + "]=[0x" + Integer.toHexString((int)xml.charAt(i)) + "]");
		}
		xml = xml.trim(); // 聲達後面多加byte: 0x02 0x03
		return XmlManipulation.nodeToXmlString(XmlManipulation.xmlStringToNode(xml), false, true);
	}
}
