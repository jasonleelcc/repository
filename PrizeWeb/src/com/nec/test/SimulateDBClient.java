package com.nec.test;

import iwin.util.ClientAgent;
import iwin.util.HttpClientAgent;
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
 * 應用程式類交易
 * 
 * @author leotu@nec.com.tw
 */
public class SimulateDBClient extends TestBase {
	final protected static Logger log = LoggerFactory.getLogger(SimulateDBClient.class);

	//
	protected ClientAgent ca = null;
	protected HttpClientAgent httpca = null;
	protected boolean httpMode = false;

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
			// InetAddress address=InetAddress.getByName("172.16.2.82"); // necadmin/`1qaz2wsx;
			// ca = new ClientAgent(address, 8080);

			if (httpMode) {
				InetAddress address = InetAddress.getLocalHost();
				httpca = new HttpClientAgent("http://" + address.getHostAddress() + ":8080/IwinWeb/api/xml");
			} else {
				InetAddress address = InetAddress.getLocalHost();
				ca = new ClientAgent(address, 30002);
			}
			// XXX: UB Online Env.
			// InetAddress address=InetAddress.getByName("mbank.ubot.com.tw");
			// InetAddress address = InetAddress.getByName("210.65.249.143");
			// ca = new ClientAgent(address, 8080);
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
			if (httpca != null) {
				httpca.disconnect();
			}
		} catch (Exception e) {
			log.error("", e);
			throw e;
		}
	}

	// =====================================================
	/**
	 * <pre>
	 * 一、點擊統計
	 * ServiceType="AddCountRq"
	 * @author weiwei 2011/08/03
	 * </pre>
	 */
	// @Test
	public void testAddCountRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/AddCountRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 五、首頁初始化查詢
	 * ServiceType="InitHomeRq"
	 * </pre>
	 */
	@Test
	public void testInitHomeRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/InitHomeRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		try {
			XMLParser2 xmlParser = new XMLParser2();
			Document mobileRequestDoc = xmlParser.fileToDoc(xmlFile);// 手機傳來EAI的電文
			String data = xmlParser.docToString(mobileRequestDoc);
			log.info("data=[" + data + "]");
			String result;
			if (httpMode) {
				httpca.send(data);
				result = httpca.retrieve();
			} else {
				ca.send(data);
				result = ca.retrieve();
			}
			log.info("result=[" + formatXML(result) + "]");
			//
			// int cdataLen = ("<![CDATA[" + "]]>").length();
			// Node node = XmlManipulation.xmlStringToNode(result);
			// NodeList nl = XmlManipulation.getNodeList(node, "/DataXML/Text/RECORD/Image");
			// for (int i = 0; i < nl.getLength(); i++) {
			// Node n = nl.item(i);
			// String image = XmlManipulation.getNodeTextValue(n);
			// log.info("image[" + i + "].length=" + image.length() + ", cdataLen=" + cdataLen + ", (" + (image.length() + cdataLen) + ")");
			// }
		} catch (Exception e) {
			log.error("xmlFile=[" + xmlFile + "]", e);
		}
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 六、好康快報-卡友優惠查詢
	 * ServiceType="CardMemberRq"
	 * </pre>
	 */
	// @Test
	public void testCardMemberRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/CardMemberRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
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
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 七、活動內容
	 * ServiceType="ActivityDetailRq"
	 * </pre>
	 */
	// @Test
	public void tesActivityDetailRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/ActivityDetailRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
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
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 八、好康快報-新卡快訊查詢
	 * ServiceType="NewCardRq"
	 * </pre>
	 */
	// @Test
	public void testNewCardRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/NewCardRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
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
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 九、好康快報-銀行優惠查詢
	 * ServiceType="BankMemberRq"
	 * </pre>
	 */
	// @Test
	public void testBankMemberRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/BankMemberRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
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
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 十、好康快報-樂活優惠查詢
	 * ServiceType="HappyMemberRq"
	 * </pre>
	 */
	// @Test
	public void testHappyMemberRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/HappyMemberRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
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
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 十一、好康快報-會員優惠查詢
	 * ServiceType="SuperMemberRq"
	 * </pre>
	 */
	// @Test
	public void testSuperMemberRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/SuperMemberRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
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
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 十二、理財好康查詢
	 * ServiceType="ManagerLinkRq"
	 * </pre>
	 */
	// @Test
	public void testManagerLinkRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/ManagerLinkRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
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
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 十三、地點一般資訊 
	 * ServiceType="PositionGeneralRq"
	 * @author weiwei 2011/07/13
	 * </pre>
	 */
	// @Test
	public void testPositionGeneralRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/PositionGeneralRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 十四、地點明細資訊
	 * ServiceType="PositionDetailRq"
	 * @author weiwei 2011/07/13
	 * </pre>
	 */
	// @Test
	public void tesPositionDetailRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/PositionDetailRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 十九、信用卡大項查詢
	 * ServiceType="CreditcardInqRq"
	 * @author weiwei 2011/07/18
	 * </pre>
	 */
	// @Test
	public void testCreditcardInqRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/CreditcardInqRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 二十、附近優惠商店查詢
	 * ServiceType="NeighborStoreInqRq"
	 * @author weiwei 2011/07/18
	 * </pre>
	 */
	// @Test
	public void testNeighborStoreInqRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/NeighborStoreInqRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 二十一、最新優惠查詢
	 * ServiceType="NewestInqRq"
	 * @author weiwei 2011/07/21
	 * </pre>
	 */
	// @Test
	public void testNewestInqRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/NewestInqRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 二十二、屆期優惠查詢
	 * ServiceType="OverDueInqRq"
	 * @author weiwei 2011/07/21
	 * </pre>
	 */
	// @Test
	public void testOverDueInqRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/OverDueInqRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 二十三、區域查詢
	 * ServiceType="AreaInqRq"
	 * @author weiwei 2011/07/21
	 * </pre>
	 */
	// @Test
	public void testAreaInqRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/AreaInqRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 二十四、分類附近優惠商店查詢
	 * ServiceType="SubtypeNeighborStoreInqRq"
	 * @author weiwei 2011/07/21
	 * </pre>
	 */
	// @Test
	public void testSubtypeNeighborStoreInqRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/SubtypeNeighborStoreInqRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 二十五、分類區域查詢
	 * ServiceType="SubtypeAreaInqRq"
	 * @author weiwei 2011/07/21
	 * </pre>
	 */
	// @Test
	public void testSubtypeAreaInqRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/SubtypeAreaInqRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 二十六、分類最新優惠查詢 
	 * ServiceType="SubtypeNewestInqRq"
	 * @author weiwei 2011/07/21
	 * </pre>
	 */
	// @Test
	public void testSubtypeNewestInqRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/SubtypeNewestInqRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 二十七、分類屆期優惠查詢
	 * ServiceType="SubtypeOverDueInqRq"
	 * @author weiwei 2011/07/22
	 * </pre>
	 */
	// @Test
	public void testSubtypeOverDueInqRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/SubtypeOverDueInqRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 二十八、行動銀行會員登錄
	 * ServiceType="MBankRegRq"
	 * @author weiwei 2011/07/27
	 * </pre>
	 */
	// @Test
	public void testMBankRegRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/MBankRegRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 二十九、Help 頁面連結
	 * ServiceType="HelpInqRq"
	 * @author weiwei 2011/07/27
	 * </pre>
	 */
	// @Test
	public void testHelpInqRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/HelpInqRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 三十、客服電話查詢
	 * ServiceType="ServicePhoneInqRq"
	 * @author weiwei 2011/08/08
	 * </pre>
	 */
	// @Test
	public void testServicePhoneInqRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/ServicePhoneInqRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 三十一、分行(ATM)位置查詢
	 * ServiceType="BranchInqRq"
	 * @author weiwei 2011/08/09
	 * </pre>
	 */
	// @Test
	public void testBranchInqRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/BranchInqRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 三十二、主類別查詢次類別
	 * ServiceType="MainInqSubRq"
	 * @author weiwei 2011/08/08
	 * </pre>
	 */
	// @Test
	public void testMainInqSubRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/MainInqSubRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 三十三、Q&A查詢
	 * ServiceType="QAInqRq"
	 * </pre>
	 */
	// @Test
	public void testQAInqRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/QAInqRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 三十四、系統公告查詢
	 * ServiceType="BulletinInqRq"
	 * </pre>
	 */
	// @Test
	public void testBulletinInqRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/BulletinInqRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 三十五、優惠券下載圖片
	 * ServiceType="CouponRq"
	 * </pre>
	 */
	// @Test
	public void testCouponRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/CouponRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 三十六、優惠券數量異動
	 * ServiceType="CouponAddRq"
	 * </pre>
	 */
	// @Test
	public void testCouponAddRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/CouponAddRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 三十七、優惠活動卡片搜尋
	 * ServiceType="ActivityCardRq"
	 * </pre>
	 */
	// @Test
	public void testActivityCardRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/ActivityCardRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 三十八、分類其他分店查詢
	 * ServiceType="TypeOtherSytoreRq"
	 * </pre>
	 */
	// @Test
	public void testTypeOtherSytoreRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/TypeOtherSytoreRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 三十九、好康快報最近地點 
	 * ServiceType="ActivityStoreRq"
	 * </pre>
	 */
	// @Test
	public void testActivityStoreRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/ActivityStoreRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 四十、好康快報地點列表
	 * ServiceType="ActivityStoreListRq"
	 * </pre>
	 */
	// @Test
	public void testActivityStoreListRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/ActivityStoreListRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 四十一、信用卡細項查詢
	 * ServiceType="CardDetailnqRq"
	 * </pre>
	 */
	// @Test
	public void testCardDetailnqRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/CardDetailnqRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 四十二、全國性/網路優惠查詢
	 * ServiceType="GInternetInqRq"
	 * </pre>
	 */
	// @Test
	public void testGInternetInqRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_db/GInternetInqRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================

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
			log.info("*result=[" + result + "]");
			log.info("result=[" + formatXML(result) + "]");
		} catch (Exception e) {
			log.error("xmlFile=[" + xmlFile + "]", e);
		}
	}

	// =====================================================
	protected String formatXML(String xml) {
		int idx = xml.lastIndexOf('>');
		for (int i = idx + 1; idx != -1 && i < xml.length(); i++) {
			log.warn("xml[" + i + "]=[0x" + Integer.toHexString((int) xml.charAt(i)) + "]");
		}
		xml = xml.trim(); // 聲達後面多加byte: 0x02 0x03
		return XmlManipulation.nodeToXmlString(XmlManipulation.xmlStringToNode(xml), false, true);
	}
}
