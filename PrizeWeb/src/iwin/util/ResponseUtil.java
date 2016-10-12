package iwin.util;

import iwin.admin.DynamicMapMBean;
import iwin.admin.Jmx;
import iwin.admin.NotifyMailLogger;
import iwin.lib.ErrorAccessBean;
import iwin.xml.XmlHtmlUtil;
import iwin.xml.XmlManipulation;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.Enumeration;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;


/**
 * taidi (20120702 已上正式)
 * @update 20120907 taidi
 */
public class ResponseUtil {
	protected static final Logger log = LoggerFactory.getLogger(ResponseUtil.class);

	protected static ErrorAccessBean errorAccess;
	private static DynamicMapMBean mbean = new DynamicMapMBean();
	private static String jmxObjectName;
	private static boolean started = false;

	static {
		init();
	}

	public static void init() {
		log.debug("...");
		if (started) {
			log.info("started=" + started);
			return;
		}
		errorAccess = ErrorAccessBean.getInstance();
		jmxObjectName = "Hexa:type=ErrorMessage,NationCode=" + errorAccess.getNationCode() + ",file="
				+ StringUtils.join(errorAccess.getFileNames(), "+");
		log.debug("BEGIN...jmxObjectName=" + jmxObjectName);
		for (Enumeration<?> e = errorAccess.getKeys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String value = errorAccess.getMessage(key);
			if (key.isEmpty()) {
				log.debug("skip: key=[" + key + "], value=[" + value + "]");
			} else {
				mbean.getModel().put(key, value);
			}
		}
		// log.debug("registerMBean..., jmxObjectName=[" + jmxObjectName + "]");
		Jmx.registerMBean(errorAccess, mbean, jmxObjectName);
		started = true;
		log.debug("END.");
	}

	public static void destroy() {
		log.debug("...");
		if (!started) {
			log.warn("started=" + started);
			return;
		}
		Jmx.unregisterMBean(jmxObjectName);
		started = false;
	}

	// ===
	public static ErrorAccessBean getErrorAccess() {
		return errorAccess;
	}

	public static String normalizeXmlData(String value) { // normalizeXmlData
		if (value == null) {
			return "";
		}
		if (value.isEmpty()) {
			return value;
		}
		return XmlHtmlUtil.specialChar(value);
	}

	public static String eliminateNull(String value) {
		return value == null ? "" : value;
	}

	// public static Object eliminateNull(Object value) {
	// return value == null ? "" : value;
	// }

	/**
	 * XML format
	 * 
	 * <pre>
	 * <TxnName>txnName</TxnName>
	 * <StatusCode>0000</StatusCode>
	 * <Text>textContent</Text>
	 * </pre>
	 */
	static public String buildXml(String txnName, String textContent) {
		StringBuffer sb = new StringBuffer(1024);
		// sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		sb.append("<DataXML>");
		sb.append("<Header>");
		sb.append("<RsTime/>");
		sb.append("<ProcTime/>");
		sb.append("<ProcIP/>");
		sb.append("<FrnMsgID/>");
		sb.append("<TxnName>").append(txnName).append("</TxnName>");
		sb.append("<StatusCode>").append("0000").append("</StatusCode>");
		sb.append("<Encoding>").append("UTF-8").append("</Encoding>");
		sb.append("<Language>").append("zh_TW").append("</Language>");
		sb.append("</Header>");
		sb.append("<Text>");
		sb.append(textContent == null ? "" : textContent);
		sb.append("</Text>");
		sb.append("</DataXML>");
		return sb.toString();
	}

	/**
	 * XML format
	 * 
	 * <pre>
	 * <TxnName>txnName</TxnName>
	 * <StatusCode>0000</StatusCode>
	 * <Text>textContent</Text>
	 * </pre>
	 */
	static public String buildXml(String txnName, StringBuffer textContent) {
		StringBuffer sb = new StringBuffer(1024);
		// sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		sb.append("<DataXML>");
		sb.append("<Header>");
		sb.append("<RsTime/>");
		sb.append("<ProcTime/>");
		sb.append("<ProcIP/>");
		sb.append("<FrnMsgID/>");
		sb.append("<TxnName>").append(txnName).append("</TxnName>");
		sb.append("<StatusCode>").append("0000").append("</StatusCode>");
		sb.append("<Encoding>").append("UTF-8").append("</Encoding>");
		sb.append("<Language>").append("zh_TW").append("</Language>");
		sb.append("</Header>");
		sb.append("<Text>");
		sb.append(textContent == null ? "" : textContent);
		sb.append("</Text>");
		sb.append("</DataXML>");
		return sb.toString();
	}

	// ========================================
	public enum ConnectServer {
		EAI, ECARD, NONE;
	}

	static public String getExceptionErrMsg(Throwable e, String moreInfo, boolean isXmlData) {
		return getExceptionErrMsg(e, moreInfo, null, isXmlData);
	}

	static public String getExceptionErrMsg(Throwable e, String moreInfo, String refData, boolean isXmlData) {
		return getExceptionErrMsg(e, moreInfo, refData, isXmlData, ConnectServer.NONE);
	}

	/**
	 * ConnectException, NoRouteToHostException, SocketTimeoutException ==> errorCode = "Z002" or errorCode = "Z003" message
	 * 
	 */
	static public String getExceptionErrMsg(Throwable e, String moreInfo, String refData, boolean isXmlData, ConnectServer server) {
		String errorCode = "Z001"; // "Z004"; // "C680";
		Throwable t = e.getCause();
		String errorExtend = null;
		boolean causedBySQLException = false;// java.sql.SQLException: ...
		//
		while (t != null) {
			if (t instanceof SQLException) {
				causedBySQLException = true;
				//log.debug("SQLException=[" + t + "]");
			}
			if ((t instanceof ConnectException && (t.toString().indexOf("Connection refused:") != -1 || t.toString().indexOf("Connection timed out:") != -1))
					|| (t instanceof NoRouteToHostException && t.toString().indexOf("No route to host:") != -1)
					|| (t instanceof SocketTimeoutException && t.toString().indexOf("connect timed out") != -1)
					|| (t instanceof SocketException && t.toString().indexOf("Connection reset") != -1)) {
				if (server == ConnectServer.EAI) {
					errorCode = "Z002";
				} else if (server == ConnectServer.ECARD) {
					errorCode = "Z003";
				} else {
					if (causedBySQLException) {
						errorCode = "Z010";
					} else {
						errorCode = "Z008";
					}
				}
				if (moreInfo == null || moreInfo.isEmpty()) {
					NotifyMailLogger.send("HEXA Server Exception Notify", t.toString(), refData, isXmlData); // TODO
				} else {
					NotifyMailLogger.send("HEXA Server Exception Notify", t.toString() + ", " + moreInfo, refData, isXmlData); // TODO
				}
				errorExtend = "[" + t.getClass().getName() + "] " + t.getMessage();
				break;
			} else if (t instanceof SocketTimeoutException) {
				if (server == ConnectServer.EAI) {
					errorCode = "Z006";
				} else if (server == ConnectServer.ECARD) {
					errorCode = "Z007";
				} else {
					errorCode = "Z008";
				}
				if (moreInfo == null || moreInfo.isEmpty()) {
					NotifyMailLogger.send("HEXA Server Exception Notify", t.toString(), refData, isXmlData); // TODO
				} else {
					NotifyMailLogger.send("HEXA Server Exception Notify", t.toString() + ", " + moreInfo, refData, isXmlData); // TODO
				}
				errorExtend = "[" + t.getClass().getName() + "] " + t.getMessage();
				break;
			} else {
				errorExtend = t.toString();
			}
			t = t.getCause();
		}
		//
		if (errorExtend == null) {
			errorExtend = e.toString();
		}
		//
		//log.debug("causedBySQLException=[" + causedBySQLException + "]");
		Document doc = errorAccess.parseError(errorCode);
		if (errorExtend != null) {
			log.debug("errorExtend=[" + errorExtend + "]");
			XmlManipulation.setNodeTextValue(XmlManipulation.getNode(doc, "/DataXML/Text/ErrorExtend"), errorExtend);
		}
		String retStr = XmlManipulation.nodeToXmlString(doc);
		return retStr;
	}

	// ========================================
	/**
	 * Text
	 */
	static public String getMessage(String messageCode) {
		return errorAccess.getMessage(messageCode);
	}

	/**
	 * Text
	 */
	static public String getMessage(String messageCode, String... messagArgs) {
		return errorAccess.getMessage(messageCode, messagArgs);
	}

	// ========================================
	/**
	 * XML format
	 * 
	 * <pre>
	 * <TxnName>MsgErrorRs</TxnName>
	 * <StatusCode>errorCode</StatusCode>
	 * <ErrorDesc>I18n Message by errorCode</ErrorDesc>
	 * </pre>
	 */
	static public String getErrorCodeI18nMsg(String errorCode) {
		return getErrorCodeI18nMsgWithPrefix(errorCode, null, (String[]) null);
	}

	/**
	 * XML format
	 * 
	 * <pre>
	 * <TxnName>MsgErrorRs</TxnName>
	 * <StatusCode>errorCode</StatusCode>
	 * <ErrorDesc>I18n Message by errorCode with errorCodeArgs</ErrorDesc>
	 * </pre>
	 */
	static public String getErrorCodeI18nMsg(String errorCode, String... errorCodeArgs) {
		return getErrorCodeI18nMsgWithPrefix(errorCode, null, errorCodeArgs);
	}

	/**
	 * XML format
	 * 
	 * <pre>
	 * <TxnName>MsgErrorRs</TxnName>
	 * <StatusCode>errorCode</StatusCode>
	 * <ErrorDesc>errorDesc</ErrorDesc>
	 * </pre>
	 */
	static public String getErrorCodeDescMsg(String errorCode, String errorDesc) {
		Document doc = errorAccess.parseErrorWithDesc(errorCode, errorDesc);
		String retStr = XmlManipulation.nodeToXmlString(doc);
		return retStr;
	}

	/**
	 * XML format
	 * 
	 * <pre>
	 * <TxnName>MsgErrorRs</TxnName>
	 * <StatusCode>errorCode</StatusCode>
	 * <ErrorDesc>I18n Message by errorCodePrefix.errorCode with errorCodeArgs</ErrorDesc>
	 * </pre>
	 */
	static public String getErrorCodeI18nMsgWithPrefix(String errorCode, String errorCodePrefix, String... errorCodeArgs) {
		Document doc = errorAccess.parseErrorWithPrefix(errorCode, errorCodePrefix, errorCodeArgs);
		String retStr = XmlManipulation.nodeToXmlString(doc);
		return retStr;
	}

	/**
	 * XML format
	 * 
	 * <pre>
	 * <TxnName>MsgErrorRs</TxnName>
	 * <StatusCode>errorCode</StatusCode>
	 * <ErrorDesc>I18n Message by errorCodePrefix.errorCode with errorCodeArgs</ErrorDesc>
	 * </pre>
	 */
	static public String getErrorCodeI18nMsgWithPrefixWithErrorExtend(String errorCode, String errorExtend, String errorCodePrefix,
			String... errorCodeArgs) {
		Document doc = errorAccess.parseErrorWithPrefix(errorCode, errorCodePrefix, errorCodeArgs);
		XmlManipulation.setNodeTextValue(XmlManipulation.getNode(doc, "/DataXML/Text/ErrorExtend"), errorExtend);
		String retStr = XmlManipulation.nodeToXmlString(doc);
		return retStr;
	}

	/**
	 * XML format
	 * 
	 * <pre>
	 * <TxnName>MsgErrorRs</TxnName>
	 * <StatusCode>errorCode</StatusCode>
	 * <ErrorDesc>I18n Message by errorCode with errorCodeArgs</ErrorDesc>
	 * </pre>
	 */
	static public String getErrorCodeMsg(String rsTime, String procTime, String frnMsgID, String errorCode, String... errorCodeArgs) {
		Document doc = errorAccess.parseError(errorCode, errorCodeArgs);
		XmlManipulation.setNodeTextValue(XmlManipulation.getNode(doc, "/DataXML/Header/RsTime"), rsTime);
		XmlManipulation.setNodeTextValue(XmlManipulation.getNode(doc, "/DataXML/Header/ProcTime"), procTime);
		XmlManipulation.setNodeTextValue(XmlManipulation.getNode(doc, "/DataXML/Header/FrnMsgID"), frnMsgID);
		String retStr = XmlManipulation.nodeToXmlString(doc);
		return retStr;
	}

	static public String getErrorWithErrorExtendCodeMsg(String rsTime, String procTime, String frnMsgID, String errorCode, String errorExtend,
			String... errorCodeArgs) {
		Document doc = errorAccess.parseError(errorCode, errorCodeArgs);
		XmlManipulation.setNodeTextValue(XmlManipulation.getNode(doc, "/DataXML/Header/RsTime"), rsTime);
		XmlManipulation.setNodeTextValue(XmlManipulation.getNode(doc, "/DataXML/Header/ProcTime"), procTime);
		XmlManipulation.setNodeTextValue(XmlManipulation.getNode(doc, "/DataXML/Header/FrnMsgID"), frnMsgID);
		XmlManipulation.setNodeTextValue(XmlManipulation.getNode(doc, "/DataXML/Text/ErrorExtend"), errorExtend);
		String retStr = XmlManipulation.nodeToXmlString(doc);
		return retStr;
	}

	/**
	 * XML format
	 * 
	 * <pre>
	 * <TxnName>MsgErrorRs</TxnName>
	 * <StatusCode>errorCode</StatusCode>
	 * <ErrorDesc>errorDesc</ErrorDesc>
	 * </pre>
	 */
	static public String getErrorCodeDescMsg(String rsTime, String procTime, String frnMsgID, String errorCode, String errorDesc) {
		Document doc = errorAccess.parseErrorWithDesc(errorCode, errorDesc);
		XmlManipulation.setNodeTextValue(XmlManipulation.getNode(doc, "/DataXML/Header/RsTime"), rsTime);
		XmlManipulation.setNodeTextValue(XmlManipulation.getNode(doc, "/DataXML/Header/ProcTime"), procTime);
		XmlManipulation.setNodeTextValue(XmlManipulation.getNode(doc, "/DataXML/Header/FrnMsgID"), frnMsgID);
		String retStr = XmlManipulation.nodeToXmlString(doc);
		return retStr;
	}
}
