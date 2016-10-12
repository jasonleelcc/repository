package iwin.util;

import static iwin.xml.XmlManipulation.getNodeTextValue;
import iwin.dispatch.Dispatcher;
import iwin.log.DiagnoseLogFactory;
import iwin.xml.XmlManipulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.w3c.dom.Document;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class TxnBankLogout {
	protected static final Logger log = LoggerFactory.getLogger(TxnBankLogout.class);
	protected static final Logger fromlogin = DiagnoseLogFactory.getFromLoginLogger();

	public static void logout(Document mobileAddServiceDoc, String sendSessionId, String actioName) {
		String clientTime = getNodeTextValue(mobileAddServiceDoc, "/DataXML/Header/ClientTime");
		String clinetID = getNodeTextValue(mobileAddServiceDoc, "/DataXML/Header/ClinetID");
		String frnIP = getNodeTextValue(mobileAddServiceDoc, "/DataXML/Header/FrnIP");
		String frnMsgID = getNodeTextValue(mobileAddServiceDoc, "/DataXML/Header/FrnMsgID");
		String encoding = getNodeTextValue(mobileAddServiceDoc, "/DataXML/Header/Encoding");
		String language = getNodeTextValue(mobileAddServiceDoc, "/DataXML/Header/Language");
		//
		String tipMsg = "(System-Force-Logout:" + actioName + ")";
		String input = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>";
		input += "<DataXML>";
		input += "<Header>";
		input += "<ClientTime>" + clientTime + "</ClientTime>";
		input += "<ClinetID>" + clinetID + "</ClinetID>";
		input += "<FrnIP>" + frnIP + "</FrnIP>";
		input += "<FrnMsgID>" + frnMsgID + tipMsg + "</FrnMsgID>";
		input += "<TxnName>MBLogoutRq</TxnName>";
		input += "<Encoding>" + encoding + "</Encoding>";
		input += "<Language>" + language + "</Language>";
		input += "</Header>";
		input += "<Text>";
		input += "<REVE-SESSION-ID>" + sendSessionId + "</REVE-SESSION-ID>";
		input += "</Text>";
		input += "<ServiceData>";
		input += "</ServiceData>";
		input += "</DataXML>";
		log.debug(tipMsg + ":input=[" + input + "] of sendSessionId=[" + sendSessionId + "]");
		fromlogin.trace(tipMsg + "(sendSessionId:" + sendSessionId + "): " + input); // TOOD
		try {
			// XXX: forbid: MDC.remove(Global.MDC_TX_UUID_KEY) & MDC.put(Global.MDC_TX_UUID_KEY, UuidUtil.generateUuid());
			MDC.put(Global.KEEP_PREVIOUS_MDC_TX_UUID_KEY, "true");
			Dispatcher dispatcher = new Dispatcher();
			String returnStr = dispatcher.dispatch(input);
			Document returnDoc = XmlManipulation.getDocument(returnStr);
			String statusCode = getNodeTextValue(returnDoc, "/DataXML/Header/StatusCode");
			log.debug(tipMsg + "(sendSessionId:" + sendSessionId + "):statusCode=[" + statusCode + "]");
			log.debug(tipMsg + "(sendSessionId:" + sendSessionId + "):output=[" + returnStr + "]");
		} catch (Exception e) {
			log.warn(tipMsg + "(sendSessionId:" + sendSessionId + ")", e);
		} finally {
			MDC.remove(Global.KEEP_PREVIOUS_MDC_TX_UUID_KEY);
		}
	}

}
