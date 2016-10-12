package iwin.txn.p2;

import static iwin.xml.XmlManipulation.getDocument;
import static iwin.xml.XmlManipulation.getNode;
import static iwin.xml.XmlManipulation.getNodeTextValue;
//import iwin.biz.model.CompositeActivityStoreFin;
//import iwin.biz.model.LonLat;
//import iwin.jdbc.ActivityStoreFinDao;
import iwin.jdbc.PrizeDao;
//import iwin.jdbc.VentureFinDao;
import iwin.jdbc.model.PrizeHit;
//import iwin.jdbc.model.VentureFin;
import iwin.txn.p2.ITxnAction;
import iwin.util.EnocderUtil;
import iwin.util.ResponseUtil;
import iwin.util.EnocderUtil.Pixel;
import iwin.xml.XmlManipulation;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * 四、中獎資料查詢 ServiceType="PrizeHitInqRq"
 * 
 * @author jasonleelcc 2015/10/21
 * 
 * @update 
 */
public class PrizeHitInqAction implements ITxnAction {
	protected static final Logger log = LoggerFactory.getLogger(PrizeHitInqAction.class);

	@Override
	public String action(String xmlStr) {
		// log.debug("xmlStr=[" + xmlStr + "]");
		String retStr = "";
		try {
			Document mobileAddServiceDoc = getDocument(xmlStr); // 手機傳來EAI的電文+service內容的電文
			Node textData = getNode(mobileAddServiceDoc, "/DataXML/Text");
			String idNo = getNodeTextValue(textData, "IdNo"); // 身分證字號
			if (idNo == null) idNo = "";
			else  idNo = "%" +idNo + "%";
			log.debug("idNo=[" + idNo + "]");
			String startInq = getNodeTextValue(textData, "StartInq"); // 查詢起日
			if (startInq == null) startInq = "";
			log.debug("startInq=[" + startInq + "]");
			String stopInq = getNodeTextValue(textData, "StopInq"); // 查詢止日
			if (stopInq == null) stopInq = "99999999";
			log.debug("stopInq=[" + stopInq + "]");
			String txnName = getNodeTextValue(textData, "TxnName"); // 交易名稱
			if (txnName == null) txnName = "";
			log.debug("txnName=[" + txnName + "]");
			String prizeNo = getNodeTextValue(textData, "PrizeNo"); // 獎品編號
			if (prizeNo == null) prizeNo = "";
			log.debug("prizeNo=[" + prizeNo + "]");
			
			//
			String errorCodePrefix = "PrizeHitInqRq";

			PrizeDao dao = new PrizeDao();
			List<PrizeHit> data = dao.QueryPrizeHit(idNo, startInq, stopInq, txnName, prizeNo);
			log.debug("list.size=[" + data.size() + "]");
			
			retStr = getXml(data);
		} catch (Exception e) {
			log.error("", e);
			retStr = ResponseUtil.getExceptionErrMsg(e, xmlStr, true);
		}
		return retStr;
	}

	private String getXml(List<PrizeHit> data) throws Exception {
		StringBuffer sb = new StringBuffer(1024 * 32);
		for (PrizeHit vo : data) {
			sb.append("<RECORD>");
			sb.append("<IdNo>").append(ResponseUtil.normalizeXmlData(vo.getId())).append("</IdNo>"); // 身分證字號
			sb.append("<HitDate>").append(ResponseUtil.normalizeXmlData(vo.getHitDate())).append("</HitDate>"); // 中獎日期
			sb.append("<TxnName>").append(ResponseUtil.normalizeXmlData(vo.getTxnName())).append("</TxnName>"); // 交易名稱
			sb.append("<PrizeName>").append(ResponseUtil.normalizeXmlData(vo.getPrizeName())).append("</PrizeName>"); // 獎品名稱
			sb.append("<UBno>").append(ResponseUtil.normalizeXmlData(vo.getUbno())).append("</UBno>"); // 兌獎編號
			sb.append("</RECORD>");
		}
		//
		return ResponseUtil.buildXml("PrizeHitInqRs", sb);
	}

}

