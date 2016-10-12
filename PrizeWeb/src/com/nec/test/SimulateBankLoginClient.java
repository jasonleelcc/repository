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
import org.w3c.dom.Node;

/**
 * 網頁類交易
 * 
 * @author leotu@nec.com.tw
 */
public class SimulateBankLoginClient extends TestBase {
	final protected static Logger log = LoggerFactory.getLogger(SimulateBankLoginClient.class);

	protected ClientAgent ca = null;
	// 第一組測試(Bank-EAI)
	// protected static String user = "N120075925A";
	// protected static String pwd = "C23746";

	// 第二組測試(Bank-EAI)
	// protected static String user = "Y120084815A";
	// protected static String pwd = "A13579";
	//
	// protected static String nickName = "ub1234";
	//
	// protected static String dynaPwd;
	//
	protected static String kbIdx;
	protected static String kbKeycode;
	protected static String kbKeyLetter;
	//
	protected static String logonIdNo;
	protected static String logonSessionId; // = "XCDZ33JZFNSZ2KFXPVP4TII";
	protected static String logonCname;

	//
	@Before
	public void before() throws Exception {
		log.debug("...");
		try {
			// XXX: UB Testing Env.
			// InetAddress address = InetAddress.getByName("172.16.2.82"); // necadmin/`1qaz2wsx;
			// InetAddress address = InetAddress.getByName("192.168.2.100"); // necadmin/`1qaz2wsx;
			InetAddress address = InetAddress.getLocalHost();
			ca = new ClientAgent(address, 30002);

			// XXX: UB Online Env.
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
			} else {
				log.warn("ca == null");
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
	 * 一、取得動態鍵盤(GETKBRAND)
	 * ServiceType="GetKBRandomRq"
	 * </pre>
	 */
	@Test
	public void testGetKBRandomRq() {
		log.debug("----------------------------------------------------------------------------------");
		String xmlFile = new File(referencePath, "/XMLVol/test/GetKBRandomRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		try {
			XMLParser2 xmlParser = new XMLParser2();
			Document mobileRequestDoc = xmlParser.fileToDoc(xmlFile);// 手機傳來EAI的電文
			String data = xmlParser.docToString(mobileRequestDoc);
			log.info("data=[" + data + "]");
			ca.send(data);
			String result = ca.retrieve();
			log.info("result=[" + formatXML(result) + "]");
			result = formatXML(result);
			//
			Document doc = XmlManipulation.getDocument(result);
			XmlManipulation.trimTextNode(doc);
			//
			kbIdx = XmlManipulation.getNodeTextValue(doc, "/DataXML/Text/SEND-KB-IDX");
			log.info("kbIdx=[" + kbIdx + "]");
			kbKeycode = XmlManipulation.getNodeTextValue(doc, "/DataXML/Text/SEND-KB-KEYCODE");
			log.info("kbKeycode=[" + kbKeycode + "]");
			kbKeyLetter = XmlManipulation.getNodeTextValue(doc, "/DataXML/Text/SEND-KB-KEYLETTER");
			log.info("kbKeyLetter=[" + kbKeyLetter + "]");
		} catch (Exception e) {
			log.error("xmlFile=[" + xmlFile + "]", e);
		}
		log.debug("END.");
	}

	// =====================================================
	/**
	 * TODO: Must enable "testGetKBRandomRq()"
	 * 
	 * <pre>
	 * 二、行動銀行登入(SSOVFYUSR)
	 * ServiceType="MBLoginRq"
	 * </pre>
	 */
	@Test
	public void testMBLoginRq() {
		log.debug("----------------------------------------------------------------------------------");
		String xmlFile = new File(referencePath, "/XMLVol/test/MBLoginRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		try {
			XMLParser2 xmlParser = new XMLParser2();
			Document mobileRequestDoc = xmlParser.fileToDoc(xmlFile);// 手機傳來EAI的電文

			//
			String pwd = XmlManipulation.getNodeTextValue(mobileRequestDoc, "/DataXML/Text/REVE-PWD");
			log.info("pwd=[" + pwd + "]");
			//
			String dynaPwd = getDynaPwd(kbIdx, kbKeycode, kbKeyLetter, pwd);
			log.info("dynaPwd=[" + dynaPwd + "] of pwd=[" + pwd + "]");
			//
			String data = xmlParser.docToString(mobileRequestDoc);
			log.info("Before: dynaPwd=[" + dynaPwd + "], pwd=[" + pwd + "], kbIdx=[" + kbIdx + "]");
			//
			Node pwdNode = XmlManipulation.getNode(mobileRequestDoc, "/DataXML/Text/REVE-PWD");
			XmlManipulation.setNodeTextValue(pwdNode, dynaPwd);
			//
			Node kbIdxNode = XmlManipulation.getNode(mobileRequestDoc, "/DataXML/Text/REVE-KB-IDX");
			XmlManipulation.setNodeTextValue(kbIdxNode, kbIdx);

			// ==================
			data = xmlParser.docToString(mobileRequestDoc);
			log.info("*** LOGIN Before: data=[" + data + "]");
			ca.send(data);
			String result = ca.retrieve();
			log.info("*** LOGIN After: result=[" + formatXML(result) + "]");
			result = formatXML(result);

			// ==================
			Document sessionDoc = xmlParser.stringToDoc(result);
			logonIdNo = XmlManipulation.getNodeTextValue(sessionDoc, "/DataXML/Text/SEND-ID-NO");
			log.info("logonIdNo=[" + logonIdNo + "]");
			logonSessionId = XmlManipulation.getNodeTextValue(sessionDoc, "/DataXML/Text/SEND-SESSION-ID");
			log.info("logonSessionId=[" + logonSessionId + "]");
			logonCname = XmlManipulation.getNodeTextValue(sessionDoc, "/DataXML/Text/SEND-CNAME");
			log.info("logonCname=[" + logonCname + "]");
			//
			// logonIdNo=[N120075925A]
			// logonSessionId=[AGIZLEJA4F00XTPJEX13UXY]
			// logonCname=[長官好]
		} catch (Exception e) {
			log.error("xmlFile=[" + xmlFile + "]", e);
		}
		log.debug("END.");
	}

	// =====================================================
	/**
	 * TODO: Must enable "testGetKBRandomRq()"
	 * 
	 * <pre>
	 * 三十、網銀首次啟用
	 * ServiceType="MBIMEIRq"
	 * </pre>
	 */
	//@Test
	public void testMBIMEIRq() {
		// String xmlFile = new File(referencePath, "/XMLVol/test/MBIMEIRq.xml").toString();
		// log.debug("BEGIN...[" + xmlFile + "]");
		// getXMLParser2(xmlFile);
		// log.debug("END.");
		log.debug("----------------------------------------------------------------------------------");
		String xmlFile = new File(referencePath, "/XMLVol/test/MBIMEIRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		try {
			XMLParser2 xmlParser = new XMLParser2();
			Document mobileRequestDoc = xmlParser.fileToDoc(xmlFile);// 手機傳來EAI的電文

			//
			String pwd = XmlManipulation.getNodeTextValue(mobileRequestDoc, "/DataXML/Text/REVE-PWD");
			log.info("pwd=[" + pwd + "]");
			//
			String dynaPwd = getDynaPwd(kbIdx, kbKeycode, kbKeyLetter, pwd);
			log.info("dynaPwd=[" + dynaPwd + "] of pwd=[" + pwd + "]");
			//
			String data = xmlParser.docToString(mobileRequestDoc);
			log.info("Before: dynaPwd=[" + dynaPwd + "], pwd=[" + pwd + "], kbIdx=[" + kbIdx + "]");
			//
			Node pwdNode = XmlManipulation.getNode(mobileRequestDoc, "/DataXML/Text/REVE-PWD");
			XmlManipulation.setNodeTextValue(pwdNode, dynaPwd);
			//
			Node kbIdxNode = XmlManipulation.getNode(mobileRequestDoc, "/DataXML/Text/REVE-KB-IDX");
			XmlManipulation.setNodeTextValue(kbIdxNode, kbIdx);

			// ==================
			data = xmlParser.docToString(mobileRequestDoc);
			log.info("*** LOGIN Before: data=[" + data + "]");
			ca.send(data);
			String result = ca.retrieve();
			log.info("*** LOGIN After: result=[" + formatXML(result) + "]");

			// ==================
			Document sessionDoc = xmlParser.stringToDoc(result);
			logonIdNo = XmlManipulation.getNodeTextValue(sessionDoc, "/DataXML/Text/SEND-ID-NO");
			log.info("logonIdNo=[" + logonIdNo + "]");
			logonSessionId = XmlManipulation.getNodeTextValue(sessionDoc, "/DataXML/Text/SEND-SESSION-ID");
			log.info("logonSessionId=[" + logonSessionId + "]");
			logonCname = XmlManipulation.getNodeTextValue(sessionDoc, "/DataXML/Text/SEND-CNAME");
			log.info("logonCname=[" + logonCname + "]");
			//
			// logonIdNo=[N120075925A]
			// logonSessionId=[AGIZLEJA4F00XTPJEX13UXY]
			// logonCname=[長官好]
		} catch (Exception e) {
			log.error("xmlFile=[" + xmlFile + "]", e);
		}
		log.debug("END.");
	}

	// =====================================================
	/**
	 * OK
	 * 
	 * <pre>
	 * 四、使用者登出 (SSOCLRSES)
	 * ServiceType="MBLogoutRq"
	 * </pre>
	 */
	// @Test
	public void testMBLogoutRq() {
		log.debug("----------------------------------------------------------------------------------");
		String xmlFile = new File(referencePath, "/XMLVol/test/MBLogoutRq.xml").toString();
		log.debug("BEGIN...[" + xmlFile + "]");
		try {
			XMLParser2 xmlParser = new XMLParser2();
			Document mobileRequestDoc = xmlParser.fileToDoc(xmlFile);// 手機傳來EAI的電文
			//
			// Node sessionIdNode = XmlManipulation.getNode(mobileRequestDoc, "/DataXML/Text/REVE-SESSION-ID");
			// log.info("logonSessionId=[" + logonSessionId + "]");
			// XmlManipulation.setNodeTextValue(sessionIdNode, logonSessionId);
			//
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
	// /**
	// * <pre>
	// * 歸戶帳號查詢(SSOGETACT)
	// * ServiceType="GetAccountRq"
	// * </pre>
	// */
	// // @Test
	// public void testGetAccountRq() {
	// log.debug("----------------------------------------------------------------------------------");
	// String xmlFile = new File(referencePath, "/XMLVol/test/GetAccountRq.xml").toString();
	// log.debug("BEGIN...[" + xmlFile + "]");
	// try {
	// XMLParser xmlParser2 = new XMLParser2();
	// Document mobileRequestDoc = xmlParser.fileToDoc(xmlFile);// 手機傳來EAI的電文
	// //
	// Node sessionIdNode = XmlManipulation.getNode(mobileRequestDoc, "/DataXML/Text/REVE-SESSION-ID");
	// log.info("sessionIdNode=[" + sessionIdNode + "]");
	// log.info("logonSessionId=[" + logonSessionId + "]");
	// sessionIdNode.setTextContent(logonSessionId);
	// //
	// String data = xmlParser.docToString(mobileRequestDoc);
	// log.info("data=[" + data + "]");
	// ca.send(data);
	// String result = ca.retrieve();
	// log.info("result=[" + formatXML(result) + "]");
	// } catch (Exception e) {
	// log.error("xmlFile=[" + xmlFile + "]", e);
	// }
	// log.debug("END.");
	// }

	// =====================================================
	protected String getDynaPwd(String kbIdx, String kbKeycode, String kbKeyLetter, String pwd) {
		if (kbKeycode == null || kbKeycode == null) {
			throw new RuntimeException("(kbKeycode == null || kbKeycode == null), kbKeycode=[" + kbKeycode + "], kbKeyLetter=[" + kbKeyLetter + "]");
		}
		// kbIdx=[141C]
		// kbKeycode=[17CD17791EC91EED1E2D19951D191A911EA51F7D18F91D2519A11A25179D1AF11D311DF117491C29182D1E151CF51C591BF91C951AA918E11C35197D1C0517D91ED51C6518691F05]
		// kbKeyLetter=[ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789]
		// kbKeycode = kbKeycode.trim();
		if (kbKeycode.length() != kbKeyLetter.length() * 4) {
			throw new RuntimeException("(kbKeycode.length() != kbKeyLetter.length() * 4), kbKeycode=[" + kbKeycode + "], kbKeyLetter=[" + kbKeyLetter
					+ "]");
		}
		pwd = pwd.toUpperCase();
		StringBuffer dynaPwd = new StringBuffer(pwd.length() * 4);
		for (int i = 0; i < pwd.length(); i++) {
			char ch = pwd.charAt(i);
			int idx = kbKeyLetter.indexOf(ch);
			if (idx == -1) {
				throw new RuntimeException("(idx == -1), ch=[" + ch + "], kbKeyLetter=[" + kbKeyLetter + "]");
			}
			int start = idx * 4;
			int end = (idx + 1) * 4;
			String code = kbKeycode.substring(start, end);
			dynaPwd.append(code);
		}
		// log.debug("dynaPwd=[" + dynaPwd + "] of pwd=[" + pwd + "]");
		return dynaPwd.toString();
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
			log.warn("xml[" + i + "]=[0x" + Integer.toHexString((int) xml.charAt(i)) + "]");
		}
		xml = xml.trim(); // 聲達後面多加byte: 0x02 0x03
		return XmlManipulation.nodeToXmlString(XmlManipulation.xmlStringToNode(xml), false, true);
	}
}
