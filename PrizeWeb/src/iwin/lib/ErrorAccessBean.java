package iwin.lib;

import iwin.exception.HexaException;
import iwin.util.NetUtil;
import iwin.xml.XMLParser2;

//import java.net.InetAddress;
//import java.net.UnknownHostException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * taidi (20120702 已上正式)
 * @author Refactor by leotu@nec.com.tw
 * @update 20120907 taidi
 */
public class ErrorAccessBean {
	protected static final Logger log = LoggerFactory.getLogger(ErrorAccessBean.class);
	
	static protected String[] fileNames = { "ErrorCode", "ErrorCode-EAI", "ErrorCode-ECARD", "ErrorCode-DB", "ErrorCode-P2" ,"payTax"}; // XXX: add more file if needed !

	static final private String iniString = "<DataXML>" + "<Header>" + "<RsTime/>" + "<ProcTime/>" + "<ProcIP/>" + "<FrnMsgID/>" + "<TxnName/>"
			+ "<StatusCode/>" + "<Encoding>UTF-8</Encoding>" + "<Language>zh_TW</Language>" + "</Header>" + "<Text>" + "<ErrorDesc/>"
			+ "<ErrorExtend/>" + "</Text>" + "</DataXML>";
	
	protected PropertyAccessBean propertyAccess;
	//protected XMLParser parser = new XMLParser();
	protected XMLParser2 parser = new XMLParser2();
	protected String nationCode = "zh_TW";
	protected static ErrorAccessBean instance = new ErrorAccessBean();
	
	private ErrorAccessBean() {
		this.propertyAccess = new PropertyAccessBean();		
		this.propertyAccess.initResourceBundle(nationCode, fileNames);
	}
	
	public static ErrorAccessBean getInstance() {
		return instance;
	}

	public String getMessage(String keyCode) {
		return propertyAccess.getMessage(keyCode);
	}
	
	public String getMessage(String keyCode, String... keyCodeArgs) {
		return propertyAccess.getMessage(keyCode, (Object[])keyCodeArgs);	
	}	
	
	public String getMessageWithPrefix(String keyCodePrefix, String keyCode, String... keyCodeArgs) {		
		String msgCode = (keyCodePrefix == null || keyCodePrefix.isEmpty()) ? keyCode : keyCodePrefix + "." + keyCode;
		return propertyAccess.getMessage(msgCode, (Object[])keyCodeArgs);
	}
	
	public Enumeration<String> getKeys() {
		return propertyAccess.getKeys();
	}
	
//	public String parseError(String clientXMLStr, iwin.exception.HexaException e) {
//		Document RetDoc = null;
//		try {
//			Document clientXMLDoc = XMLBean.stringToDoc(clientXMLStr);
//			RetDoc = this.parseError(clientXMLDoc, e);
//
//		} catch (HexaException e1) {
//			System.out.println(e1.getError());
//		}
//		return XMLBean.docToString(RetDoc);
//	}
//
//	public Document parseError(Document clientXMLDoc, iwin.exception.HexaException e) {
//		Document RetDoc = null;
//		try {
//			String nationCode = XMLBean.getTagData(clientXMLDoc, "Language");
//			String errorString = new PropertyAccessBean().getMessage(nationCode, "C680", fileNames);
//
//			RetDoc = this.getReturnError(clientXMLDoc, "C680", errorString);
//		} catch (HexaException e1) {
//			System.out.println(e1.getError());
//		}
//		return RetDoc;
//	}

//	public String parseError(String clientXMLStr, String hostID, iwin.exception.HexaException e) {
//		Document RetDoc = null;
//		try {
//			Document clientXMLDoc = XMLBean.stringToDoc(clientXMLStr);
//			RetDoc = this.parseError(clientXMLDoc, hostID, e);
//
//		} catch (HexaException e1) {
//			System.out.println(e1.getError());
//		}
//		return XMLBean.docToString(RetDoc);
//	}
//
//	public Document parseError(Document clientXMLDoc, String hostID, iwin.exception.HexaException e) {
//		Document RetDoc = null;
//		try {
//			String nationCode = XMLBean.getTagData(clientXMLDoc, "Language");
//			String errorString = new PropertyAccessBean().getMessage(nationCode, "C680", fileNames);
//
//			RetDoc = this.getReturnError(clientXMLDoc, "C680", errorString, hostID);
//		} catch (HexaException e1) {
//			System.out.println(e1.getError());
//		}
//		return RetDoc;
//	}

//	/**
//	 * Insert the method's description here. Creation date: (2001/2/21 �U�� 04:02:33)
//	 * 
//	 * @return org.w3c.dom.Document
//	 * @param errorCode
//	 *            java.lang.String
//	 */
//	public Document hexaError(String errorDesc) {
//		return this.hexaError("8888", errorDesc);
//	}
//
//	/**
//	 * Insert the method's description here. Creation date: (2001/5/26 �W�� 11:31:27)
//	 */
//	public Document hexaError(String className, String errorDesc) {
//		Document RetDoc = null;
//		try {
//			RetDoc = XMLBean.stringToDoc(iniString);
//		} catch (HexaException e) {
//			System.out.println(e.getError());
//		}
//		XMLBean.setTagData(RetDoc, "SvcType", "MsgErrorRs");
//		XMLBean.setTagData(RetDoc, "StatusCode", className);
//		XMLBean.setTagData(RetDoc, "StatusDesc", errorDesc);
//		ClockAccessBean clock = ClockAccessBean.getInstance();
//		XMLBean.setTagData(RetDoc, "PrcDtTm", clock.getDtTm());
//		// new tag
//		//try {
//	        String localIp = NetUtil.getLocalIp();
//			XMLBean.setTagData(RetDoc, "PrcIP", localIp);
//		//} catch (UnknownHostException e1) {
//		//}
//
//		return RetDoc;
//	}

	public String getNationCode() {
		return nationCode;
	}

	public String[] getFileNames() {
		return fileNames;
	}

	public Document parseError(String errorCode, String ...errorCodeArgs) {
		return parseErrorWithPrefix(errorCode, null, errorCodeArgs);
	}

	/**
	 * 
	 * @return org.w3c.dom.Document
	 * @param errorCode
	 *            java.lang.String
	 */
	public Document parseErrorWithDesc(String errorCode, String errorDesc) {		
		Document RetDoc = null;
		try {
			RetDoc = parser.stringToDoc(iniString);

		} catch (HexaException e) {
			log.error("", e.getError());
		}
		String errorString = (errorDesc != null && !errorDesc.isEmpty()) ? errorDesc : propertyAccess.getMessage(errorCode);		
		
		parser.setTagData(RetDoc, "TxnName", "MsgErrorRs");
		parser.setTagData(RetDoc, "StatusCode", errorCode);
		parser.setTagData(RetDoc, "ErrorDesc", errorString);
		//try {
			String localIp = NetUtil.getLocalIp();
			parser.setTagData(RetDoc, "ProcIP", localIp);
		//} catch (UnknownHostException e1) {
		//}

		return RetDoc;
	}
	
	/**
	 * 
	 * @return org.w3c.dom.Document
	 * @param errorCode
	 *            java.lang.String
	 */
	public Document parseErrorWithPrefix(String errorCode, String errorCodePrefix, String ...errorCodeArgs) {		
		Document RetDoc = null;
		try {
			RetDoc = parser.stringToDoc(iniString);

		} catch (HexaException e) {
			log.error("", e.getError());
		}
		String msgCode = (errorCodePrefix == null || errorCodePrefix.isEmpty()) ? errorCode : errorCodePrefix + "." + errorCode;
		String errorString = propertyAccess.getMessage(msgCode, (Object[])errorCodeArgs);		

		parser.setTagData(RetDoc, "TxnName", "MsgErrorRs");
		parser.setTagData(RetDoc, "StatusCode", errorCode);
		parser.setTagData(RetDoc, "ErrorDesc", errorString);
		//try {
			String localIp = NetUtil.getLocalIp();
			parser.setTagData(RetDoc, "ProcIP", localIp);
		//} catch (UnknownHostException e1) {
		//}

		return RetDoc;
	}

//	/**
//	 * Insert the method's description here. Creation date: (2001/2/21 �U�� 04:02:33)
//	 * 
//	 * @return org.w3c.dom.Document
//	 * @param errorCode
//	 *            java.lang.String
//	 */
//	public String parseErrorWithClient(String clientXMLDoc, String errorCode) {
//		//XMLParser parser = new XMLParser();
//		XMLParser2 parser = new XMLParser2();
//		Document RetDoc = null;
//		try {
//			Document clientDoc = parser.stringToDoc(clientXMLDoc);
//			RetDoc = parseErrorWithClient(clientDoc, errorCode);
//
//		} catch (HexaException e) {
//			log.error("", e.getError());
//		}
//		return parser.docToString(RetDoc);
//	}

//	/**
//	 * 
//	 * @return org.w3c.dom.Document
//	 * @param errorCode
//	 *            java.lang.String
//	 */
//	static public Document parseErrorWithClient(Document clientXMLDoc, String errorCode) {
//		//XMLParser parser = new XMLParser();
//		XMLParser2 parser = new XMLParser2();
//		//Debugger debugger = new Debugger(); // FIXME: 若要使用Debug請改用非同步方式處理(如jms), 移掉此處程式碼. Leo Tu.
//		//debugger.print(parser.docToString(clientXMLDoc));
//
//		Document RetDoc = null;
//		String nationCode = "", errorString = "";
//		try {
//			nationCode = parser.getTagData(clientXMLDoc, "Language");
//			errorString = PropertyAccessBean.getMessage(nationCode, errorCode, fileNames);
//
//			RetDoc = getReturnError(clientXMLDoc, errorCode, errorString);
//
//		} catch (HexaException e1) {
//			log.error(e1.getError());
//		}
//
//		// if (errorCode.equals("C026") || errorCode.equals("C050")) {
//		// RetDoc = this.setErr_C026_C050(RetDoc, nationCode, errorCode);
//		// }
//		//debugger = null;
//		return RetDoc;
//	}

//	public String parseError(String clientXMLStr, String hostID, String errorCode) {
//		Document RetDoc = null;
//		try {
//			Document clientXMLDoc = XMLBean.stringToDoc(clientXMLStr);
//			RetDoc = this.parseError(clientXMLDoc, hostID, errorCode);
//
//		} catch (HexaException e1) {
//			System.out.println(e1.getError());
//		}
//		return XMLBean.docToString(RetDoc);
//	}

//	public org.w3c.dom.Document parseError(Document clientXMLDoc, String hostID, String errorCode) {
//		Debugger debugger = new Debugger();
//		debugger.print(XMLBean.docToString(clientXMLDoc));
//
//		Document RetDoc = null;
//		String nationCode = "", errorString = "";
//		try {
//			nationCode = XMLBean.getTagData(clientXMLDoc, "Language");
//			errorString = new PropertyAccessBean().getMessage(nationCode, errorCode, fileNames);
//
//			RetDoc = this.getReturnError(clientXMLDoc, errorCode, errorString, hostID);
//
//		} catch (HexaException e1) {
//			System.out.println(e1.getError());
//		}
//
//		// if (errorCode.equals("C026") || errorCode.equals("C050")) {
//		// RetDoc = this.setErr_C026_C050(RetDoc, nationCode, errorCode);
//		// }
//		debugger = null;
//		return RetDoc;
//	}

//	/**
//	 * Insert the method's description here. Creation date: (2003/3/11 �U�� 05:54:00)
//	 * 
//	 * @return org.w3c.dom.Document
//	 * @param xmlDoc
//	 *            org.w3c.dom.Document
//	 * @param e
//	 *            tw.com.taione.exception.HexaSQLException
//	 */
//	public Document parseError(Document clientXMLDoc, iwin.exception.HexaSQLException e) {
//		Document RetDoc = null;
//		try {
//			String nationCode = XMLBean.getTagData(clientXMLDoc, "Language");
//			String errorString = new PropertyAccessBean().getMessage(nationCode, e.getErrorCode(), fileNames);
//			errorString = errorString + "\n" + e.getErrorDesc();
//
//			RetDoc = this.getReturnError(clientXMLDoc, "C677", errorString);
//
//		} catch (HexaException e1) {
//			System.out.println(e1.getError());
//		}
//		return RetDoc;
//	}

//	public String parseError(String clientXMLStr, iwin.exception.HexaSQLException e) {
//		Document RetDoc = null;
//		try {
//			Document clientXMLDoc = XMLBean.stringToDoc(clientXMLStr);
//			RetDoc = this.parseError(clientXMLDoc, e);
//
//		} catch (HexaException e1) {
//			System.out.println(e1.getError());
//		}
//		return XMLBean.docToString(RetDoc);
//	}

//	public Document parseError(Document clientXMLDoc, String hostID, iwin.exception.HexaSQLException e) {
//		Document RetDoc = null;
//		try {
//			String nationCode = XMLBean.getTagData(clientXMLDoc, "Language");
//			String errorString = new PropertyAccessBean().getMessage(nationCode, e.getErrorCode(), fileNames);
//			errorString = errorString + "\n" + e.getErrorDesc();
//
//			RetDoc = this.getReturnError(clientXMLDoc, "C677", errorString, hostID);
//
//		} catch (HexaException e1) {
//			System.out.println(e1.getError());
//		}
//		return RetDoc;
//	}
//
//	public String parseError(String clientXMLStr, String hostID, iwin.exception.HexaSQLException e) {
//		Document RetDoc = null;
//		try {
//			Document clientXMLDoc = XMLBean.stringToDoc(clientXMLStr);
//			RetDoc = this.parseError(clientXMLDoc, hostID, e);
//
//		} catch (HexaException e1) {
//			System.out.println(e1.getError());
//		}
//		return XMLBean.docToString(RetDoc);
//	}

//	static private Document getReturnError(Document clientXMLDoc, String statusCode, String errorString) {
//		//XMLParser parser = new XMLParser();
//		XMLParser2 parser = new XMLParser2();
//		
//		Document RetDoc = null;
//		String nationCode = "", frnName = "", frnMsgid = "", encoding = "", prcIP = "", hostID = "", svcCode = "";
//		try {
//			RetDoc = parser.stringToDoc(iniString);
//			nationCode = parser.getTagData(clientXMLDoc, "Language");
//			//frnName = parser.getTagData(clientXMLDoc, "FrnName");
//			frnMsgid = parser.getTagData(clientXMLDoc, "FrnMsgID");
//			encoding = parser.getTagData(clientXMLDoc, "Encoding");
//
//			// new tag
//			//if (clientXMLDoc.getElementsByTagName("HostID").getLength() > 0)
//			//	hostID = parser.getTagData(clientXMLDoc, "HostID").trim();
//			//if (clientXMLDoc.getElementsByTagName("SvcCode").getLength() > 0)
//			//	svcCode = parser.getTagData(clientXMLDoc, "SvcCode").trim();
//
//		} catch (HexaException e1) {
//			log.error(e1.getError());
//		}
//
//		parser.setTagData(RetDoc, "SvcType", "MsgErrorRs");
//		parser.setTagData(RetDoc, "StatusCode", statusCode);
//		parser.setTagData(RetDoc, "StatusDesc", errorString);
//		//parser.setTagData(RetDoc, "FrnName", frnName);
//		parser.setTagData(RetDoc, "FrnMsgID", frnMsgid);
//		parser.setTagData(RetDoc, "Encoding", encoding);
//		parser.setTagData(RetDoc, "Language", nationCode);
//
//		//ClockAccessBean clock = ClockAccessBean.getInstance();
//		//parser.setTagData(RetDoc, "PrcDtTm", clock.getDtTm());
//
//		// new tag
//		//try {
//			prcIP = NetUtil.getLocalIp();
//		//} catch (UnknownHostException e2) {
//		//}
//		parser.setTagData(RetDoc, "PrcIP", prcIP);
//		//parser.setTagData(RetDoc, "HostID", hostID);
//		//parser.setTagData(RetDoc, "SvcCode", svcCode);
//
//		return RetDoc;
//	}

//	private Document getReturnError(Document clientXMLDoc, String statusCode, String errorString, String hostID) {
//		Document RetDoc = this.getReturnError(clientXMLDoc, statusCode, errorString);
//		XMLBean.setTagData(RetDoc, "HostID", hostID);
//		return RetDoc;
//	}

}
