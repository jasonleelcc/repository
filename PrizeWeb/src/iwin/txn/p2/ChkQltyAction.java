package iwin.txn.p2;

import static iwin.xml.XmlManipulation.getDocument;
import static iwin.xml.XmlManipulation.getNode;
import static iwin.xml.XmlManipulation.getNodeTextValue;
//import iwin.jdbc.LoginAdFinDao;
import iwin.jdbc.PrizeDao;
//import iwin.jdbc.model.LoginAdFin;
import iwin.txn.p2.ITxnAction;
import iwin.util.EnocderUtil;
import iwin.util.ResponseUtil;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * 二、檢查抽獎資格(每人每天最多(含)五次)
 * @author jasonleelcc 2015/09/25
 * @update jasonleelcc 2015/10/26
 *  */
public class ChkQltyAction implements ITxnAction {
	
	protected static final Logger log = LoggerFactory.getLogger(ChkQltyAction.class);
	private String id = ""; //身分證字號
	private String uid = ""; //抽獎編號
	
	@Override
	public String action(String xmlStr) {
		
		// log.debug("xmlStr=[" + xmlStr + "]");
		String retStr = "";
		try {
			Document mobileAddServiceDoc = getDocument(xmlStr);// 手機傳來EAI的電文+service內容的電文
			Node textData = getNode(mobileAddServiceDoc, "/DataXML/Text");
			//
			//String errorCodePrefix = "LoginADRq";
			id = getNodeTextValue(textData, "REVE-ID-NO");
			log.debug("id=[" + id + "]");
			uid = getNodeTextValue(textData, "REVE-UID");
			log.debug("uid=[" + uid + "]");
			//
			PrizeDao dao = new PrizeDao();
			boolean bn = dao.getIdCount(id);
			String bns = null;
			if (bn == true){
				bns="0";
				dao.insertPrizeHit(id, uid);
			}
			else 
				bns="1";
			StringBuffer sb = new StringBuffer(50);
			sb.append("<SEND-STATUS>").append(bns).append("</SEND-STATUS>");
			retStr = ResponseUtil.buildXml("ChkQltyRs", sb);
			
		} catch (Exception e) {
			log.error("", e);
			retStr = ResponseUtil.getExceptionErrMsg(e, xmlStr, true);
		}
		return retStr;
	}

	
}