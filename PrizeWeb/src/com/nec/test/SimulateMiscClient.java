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
 * 應用程式類交易
 * 
 * @author leotu@nec.com.tw
 */
public class SimulateMiscClient extends TestBase {
	final protected static Logger log = LoggerFactory.getLogger(SimulateMiscClient.class);

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
			//System.setProperty("http.proxyHost", "172.28.66.108");
			//System.setProperty("http.proxyPort", "8080");
			
			// XXX: UB Testing Env.
			// InetAddress address=InetAddress.getByName("172.16.2.82") // necadmin/`1qaz2wsx;			
			InetAddress address = InetAddress.getLocalHost();
			ca = new ClientAgent(address, 30002);
			
			// XXX: UB Online Env.
			//InetAddress address=InetAddress.getByName("mbank.ubot.com.tw");
			//InetAddress address=InetAddress.getByName("210.65.249.143"); 
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
	 * <pre>
	 * 十五、高鐵時刻查詢
	 * ServiceType="TWHSInqRq"
	 * </pre>
	 */
	//@Test
	public void testTWHSInqRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_misc/TWHSInqRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 十六、高鐵到站明細查詢
	 *  ServiceType="TWHSDetailInqRq"
	 * </pre>
	 */
	//@Test
	public void testTWHSDetailInqRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_misc/TWHSDetailInqRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 十七、台鐵時刻查詢
	 * ServiceType="TaiTrainInqRq"
	 * </pre>
	 */
	@Test
	public void testTRAInqRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_misc/TaiTrainInqRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	// =====================================================
	/**
	 * <pre>
	 * 十八、台鐵到站明細查詢
	 *  ServiceType="TWHSDetailInqRq"
	 * </pre>
	 */
	//@Test
	public void testTaiTrainDetailInqRq() {
		String xmlFile = new File(referencePath, "/XMLVol/test_misc/TaiTrainDetailInqRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		getXMLParser2(xmlFile);
		log.debug("END.");
	}

	/**
	 * XML的轉換
	 * @param xmlFile
	 */
	protected void getXMLParser2(String xmlFile){
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
