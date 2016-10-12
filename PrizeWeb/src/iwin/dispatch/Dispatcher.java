package iwin.dispatch;

import iwin.admin.Monitoring;
import iwin.admin.NotifyMailLogger;
import iwin.exception.HexaException;
import iwin.front.net.TcpSocketServer;
import iwin.log.DiagnoseLogFactory;
import iwin.txn.p2.ITxnAction;
import iwin.util.Global;
import iwin.util.NetUtil;
import iwin.util.ResponseUtil;
import iwin.util.SpendTimer;
import iwin.util.UuidUtil;
import iwin.xml.XMLParser2;
import iwin.xml.XmlManipulation;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

/**
 * taidi (20120702 已上正式)
 * @author Refactor by leotu@nec.com.tw
 * @update 20120907 taidi
 */
public class Dispatcher {
	protected static final Logger log = LoggerFactory.getLogger(Dispatcher.class);

	protected static final Logger fromMobile = DiagnoseLogFactory.getFromMobileLogger();
	protected static final Logger toMobile = DiagnoseLogFactory.getToMobileLogger();

	protected XMLParser2 xmlParser = new XMLParser2();

	protected static String refpath = Global.REFERENCE_PATH;
	protected static String serviceXmlContent; // XXX: cache
	protected static String serviceXmlErrorMsg = null;
	/**
	 * @param XMLString
	 * @return
	 */
	static {
		String serviceXml = refpath + "/XMLVol/service.xml";
		String serviceXml2 = refpath + "/XMLVol/service2.xml";

		try {
			// Document serviceDoc = XmlManipulation.getDocument(new File(serviceXml));// 所有交易的設定檔
			// //log.debug("::serviceDoc=[" + XmlManipulation.nodeToXmlString(serviceDoc) + "]");
			//
			// Document serviceDoc2 = XmlManipulation.getDocument(new File(serviceXml2));// 所有交易的設定檔
			// log.debug("::serviceDoc2, new File(serviceXml2)=" + new File(serviceXml2) + "=[" + XmlManipulation.nodeToXmlString(serviceDoc2) + "]");
			// //
			// Element elmt = (Element) serviceDoc.importNode(serviceDoc2.getDocumentElement().cloneNode(true), true);
			// // log.debug("::elmt=[" + XmlManipulation.nodeToXmlString(elmt) + "]");
			// //
			// Element documentElement = serviceDoc.getDocumentElement();
			// NodeList nl = elmt.getElementsByTagName("ServiceEntry");
			// log.debug("nl.getLength()=" + nl.getLength());
			// int size = nl.getLength();
			// for (int i = 0; i < size; i++) {
			// Node node = nl.item(i);
			// log.debug("::node=[" + XmlManipulation.nodeToXmlString(node) + "]");
			// documentElement.appendChild(node);
			// }
			// Document serviceDoc3 = serviceDoc;
			// // log.debug("::serviceDoc3=[" + XmlManipulation.nodeToXmlString(serviceDoc3) + "]");
			// serviceXmlContent = XmlManipulation.nodeToXmlString(serviceDoc3);
			//
			InputStream input = new FileInputStream(new File(serviceXml));
			String xml = IOUtils.toString(input, "UTF8");
			IOUtils.closeQuietly(input);

			InputStream input2 = new FileInputStream(new File(serviceXml2));
			String xml2 = IOUtils.toString(input2, "UTF8");
			IOUtils.closeQuietly(input2);
			//
			int idx = xml.lastIndexOf("</XMLData>");
			String preXml = xml.substring(0, idx);

			int idx2 = xml2.indexOf("<XMLData>");
			String postXml = xml2.substring(idx2 + "<XMLData>".length());
			//
			serviceXmlContent = preXml + postXml;
			// log.debug("::serviceXmlContent=[" + serviceXmlContent + "]");
		} catch (Exception e) {
			serviceXmlErrorMsg = e.toString();
			log.error("", e);
		}
	}

	/**
	 * @param XMLString
	 * @return
	 */
	public String dispatch(String XMLString) {
		if (MDC.get(Global.KEEP_PREVIOUS_MDC_TX_UUID_KEY) == null) {
			MDC.put(Global.MDC_TX_UUID_KEY, UuidUtil.generateUuid()); // TODO
		}
		fromMobile.trace(XMLString); // TODO
		// log.debug("XMLString=[" + XMLString + "]");
		Document mobileAddServiceDoc;
		String retString = "";
		// 程式開始時間
		SpendTimer st = new SpendTimer();
		st.timerStart();
		String txnName = null;
		String frnMsgID = null;
		String description = null;
		try {
			if (XMLString == null || XMLString.trim().isEmpty()) { // TODO
				retString = toExceptionMsg(XMLString, st, frnMsgID, "Z011", "", "");
				log.warn("(XMLString == null || XMLString.trim().isEmpty()), input=[" + XMLString + "], output=[" + retString + "]");
				toMobile.trace(retString); // TODO
				// log.debug("retString=[" + retString + "]");
				return retString;
			}
			// long startTime = System.currentTimeMillis();
			mobileAddServiceDoc = xmlParser.stringToDoc(XMLString);// 手機傳來EAI的電文
			frnMsgID = xmlParser.getTagData(mobileAddServiceDoc, "FrnMsgID");// 手機傳給EAI電文中的原前置訊息編號
			txnName = xmlParser.getTagData(mobileAddServiceDoc, "TxnName");// 手機傳給EAI電文中的交易名稱
			if (GenericValidator.isBlankOrNull(txnName)) { // TODO
				retString = toExceptionMsg(XMLString, st, frnMsgID, "Z009", txnName, null);
				log.warn("(GenericValidator.isBlankOrNull(txnName)), txnName=[" + txnName + "], input=[" + XMLString + "], output=[" + retString
						+ "]");
				toMobile.trace(retString); // TODO
				// log.debug("retString=[" + retString + "]");
				return retString;
			}
			// 將前端手機電文加上service內定義的設定檔組成同一個電文
			if (mobileAddServiceDoc.getElementsByTagName("ServiceData").item(0) == null) {
				Element newChild = mobileAddServiceDoc.createElement("ServiceData");
				mobileAddServiceDoc.getDocumentElement().appendChild(newChild);
			}
			// Document serviceDoc = xmlParser.getDocfromFName(refpath + "/XMLVol/service.xml");// 所有交易的設定檔
			if (serviceXmlContent == null) {
				log.error("(serviceXmlContent == null)");
				throw new RuntimeException(serviceXmlErrorMsg);
			}
			Document serviceDoc = xmlParser.stringToDoc(serviceXmlContent);// 所有交易的設定檔
			// log.debug("::serviceDoc=[" + XmlManipulation.nodeToXmlString(serviceDoc) + "]");
			NodeList nl = serviceDoc.getElementsByTagName("ServiceEntry");
			boolean foundTxnName = false;
			for (int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				if (node.getAttributes().getNamedItem("ServiceType").getNodeValue().equals(txnName)) {
					foundTxnName = true;
					description = node.getAttributes().getNamedItem("Description").getNodeValue();
					NodeList nl2 = node.getChildNodes();
					for (int j = 0; j < nl2.getLength(); j++) {
						if (nl2.item(j).getNodeType() == Node.ELEMENT_NODE) {
							Element elmt = (Element) mobileAddServiceDoc.importNode(nl2.item(j).cloneNode(true), true);
							mobileAddServiceDoc.getElementsByTagName("ServiceData").item(0).appendChild(elmt);
						}
					}
					break;
				}
			}
			if (!foundTxnName) { // TODO
				retString = toExceptionMsg(XMLString, st, frnMsgID, "Z005", txnName, txnName, txnName);
				log.warn("(!foundTxnName), txnName=[" + txnName + "], input=[" + XMLString + "], output=[" + retString + "]");
				toMobile.trace(retString); // TODO
				// log.debug("retString=[" + retString + "]");
				return retString;
			}
			// log.debug("txnName=[" +txnName + "], description=[" + description + "]");
			String mobileElectronicStrAddServiceStr = xmlParser.docToString(mobileAddServiceDoc);
			// log.debug("mobileElectronicStrAddServiceStr=[" + mobileElectronicStrAddServiceStr + "]");
			String BOclass = new String("iwin.txn.");
			String BOName = xmlParser.getTagData(mobileAddServiceDoc, "UCOName");
			BOclass = BOclass + BOName;
			// Class<?>[] para = { java.lang.String.class };
			// Object[] paraObj = { mobileElectronicStrAddServiceStr };
			Class<?> className = Class.forName(BOclass);
			// Method action = className.getMethod("action", para);
			Object bizObj = className.newInstance(); // TODO: default constructor with argument
			// retString = (String) action.invoke(bizObj, paraObj);
			// TODO: add monitoring (Leo Tu)
			ITxnAction txnAction = (ITxnAction) bizObj;
			String key = description == null || description.isEmpty() ? ("TXN: " + txnName) : ("TXN: " + txnName + " @" + description);
			txnAction = Monitoring.add(txnAction, key); // TODO
			retString = txnAction.action(mobileElectronicStrAddServiceStr); // XXX
			//
			// 程式結束時間
			// log.debug("BEFORE: retString=[" + retString + "]");
			st.timerStop();
			String rsTime = st.toEndTimeFormatStr("yyyyMMddHHmmss");
			String procTime = st.toTimeFormatStr("ssS");
			// App Server發出訊息之系統日期時間
			retString = retString.replaceAll("<RsTime/>", "<RsTime>" + rsTime + "</RsTime>");
			// 處理訊息之使用時間
			retString = retString.replaceAll("<ProcTime/>", "<ProcTime>" + procTime + "</ProcTime>");
			// AP Server之HostIP
			String eaiAsApIp = NetUtil.getEaiAsApIp(TcpSocketServer.getInstance().getNetconfig());
			retString = retString.replaceAll("<ProcIP/>", "<ProcIP>" + eaiAsApIp + "</ProcIP>");
			// 原前置訊息編號
			retString = retString.replaceAll("<FrnMsgID/>", "<FrnMsgID>" + xmlParser.getTagData(mobileAddServiceDoc, "FrnMsgID") + "</FrnMsgID>");
			// retString=retString.replaceAll("<StatusCode/>", "<StatusCode>"+"0000"+"</StatusCode>");
			// if (!retString.startsWith("<?xml version=\"1.0\" encoding=\"utf-8\" ?>")) {
			retString = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" + retString;
			// }
			// log.debug("AFTER: retString=[" + retString + "]");
		} catch (HexaException e) {
			log.error("input=[" + XMLString + "], output=[" + retString + "]", e);
			if (e.getException() instanceof SAXParseException) {
				retString = toExceptionMsg(XMLString, st, frnMsgID, "Z010", txnName, e.toString());
			} else {
				retString = toExceptionMsg(XMLString, st, frnMsgID, "Z001", txnName, ((HexaException) e).getError());
			}
		} catch (Throwable e) {
			log.error("input=[" + XMLString + "], output=[" + retString + "]", e);
			retString = toExceptionMsg(XMLString, st, frnMsgID, "Z001", txnName, e.toString());
		} finally {
			toMobile.trace(retString); // TODO
			// log.debug("retString=[" + retString + "]");
			if (MDC.get(Global.KEEP_PREVIOUS_MDC_TX_UUID_KEY) == null) {
				MDC.remove(Global.MDC_TX_UUID_KEY); // TODO
			}
		}
		return retString;
	}

	private String toExceptionMsg(String XMLString, SpendTimer st, String frnMsgID, String errorCode, String txnName, String notifyMsg,
			String... errorCodeArgs) {
		st.timerStop();
		String rsTime = st.toEndTimeFormatStr("yyyyMMddHHmmss");
		String procTime = st.toTimeFormatStr("ssS");
		String retString = ResponseUtil.getErrorWithErrorExtendCodeMsg(rsTime, procTime, frnMsgID, errorCode, "交易名稱:[" + txnName + "], " + notifyMsg,
				errorCodeArgs);
		NotifyMailLogger.send("HEXA Server Exception Notify (Dispatcher)", notifyMsg, XMLString, false); // TODO
		return retString;
	}

	// private String getprctm(long startTm) {
	// long _prctime = System.currentTimeMillis() - startTm;
	// String prctimestr = String.valueOf(_prctime);
	// for (int i = prctimestr.length() + 1; i <= 5; i++) {
	// prctimestr = "0" + prctimestr;
	// }
	// return prctimestr;
	// }
}
