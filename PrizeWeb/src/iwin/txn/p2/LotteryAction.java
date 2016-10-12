package iwin.txn.p2;

import static iwin.xml.XmlManipulation.getDocument;
import static iwin.xml.XmlManipulation.getNode;
import static iwin.xml.XmlManipulation.getNodeTextValue;
import iwin.front.net.TcpSocketServer;
//import iwin.jdbc.LoginAdFinDao;
import iwin.jdbc.PrizeDao;
//import iwin.jdbc.model.LoginAdFin;
import iwin.jdbc.model.PrizeList;
import iwin.txn.p2.ITxnAction;
import iwin.util.EnocderUtil;
import iwin.util.ResponseUtil;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * 二、抽獎
 * @author jasonleelcc 2015/09/25
 * @update 
 *  */
public class LotteryAction implements ITxnAction {
	
	protected static final Logger log = LoggerFactory.getLogger(LotteryAction.class);
	private String id = ""; //身分證字號
	private String txnName = ""; //交易名稱
	private String uid = ""; //抽獎編號
	public HashMap<Integer, Integer> prize = new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> rprize = new HashMap<Integer, Integer>();
	public HashMap<Integer, String> prizeName = new HashMap<Integer, String>();
	private static int prsb;
	
	@Override
	public String action(String xmlStr) {
		
		// log.debug("xmlStr=[" + xmlStr + "]");
		String retStr = "";
		String errorCodePrefix = "LotteryRq";
		try {
			Document mobileAddServiceDoc = getDocument(xmlStr);// 手機傳來EAI的電文+service內容的電文
			Node textData = getNode(mobileAddServiceDoc, "/DataXML/Text");
			//
			//String errorCodePrefix = "LoginADRq";
			prsb = Integer.parseInt(TcpSocketServer.getInstance().getNetconfig().getIniValue("prize.prosibility"));
			id = getNodeTextValue(textData, "REVE-ID-NO");
			txnName = getNodeTextValue(textData, "REVE-TXN-NAME");
			uid = getNodeTextValue(textData, "REVE-UID");
			log.debug("id=[" + id + "]");
			if (GenericValidator.isBlankOrNull(id)) {
				retStr = ResponseUtil.getErrorCodeI18nMsgWithPrefix("X0001", errorCodePrefix);
				return retStr;
			}

			//
			int hitNo = 0;
			PrizeDao dao = new PrizeDao();
			hitNo = dao.QueryPrizeHit(uid);
			if (hitNo == 999){
				hitNo = Draw();
			}
			else {
				hitNo = 999;
			}
			StringBuffer sb = new StringBuffer(150);
			sb.append("<SEND-ID-NO>").append(id).append("</SEND-ID-NO>");
			sb.append("<SEND-PRIZE-NAME>").append(hitNo).append("</SEND-PRIZE-NAME>");
			retStr = ResponseUtil.buildXml("LotteryRs", sb);
			
			
		} catch (Exception e) {
			log.error("", e);
			retStr = ResponseUtil.getExceptionErrMsg(e, xmlStr, true);
		}
		return retStr;
	}
	
	public synchronized int Draw() throws Exception{
		PrizeDao pd = new PrizeDao();
		List<PrizeList> al = pd.QueryPrize();
		int count=0;
		int[] initNo = new int[al.size()];
		int[] initNums = new int[al.size()];
		String[] initNames = new String[al.size()];
		for(int i=0; i<al.size(); i++){
			count++;
			initNo[i]=al.get(i).getPrizeId();
			initNames[i]=al.get(i).getPrizeName();
			initNums[i]=al.get(i).getAmount()-pd.getHitCount(initNo[i]);
		}
		int total = initData(initNo, initNums, initNames);
		log.debug(prize.toString());
		
		Random ran = new Random();
		int hit = ran.nextInt(total)+1;
		int hitNo = prizeHit(hit);
		String hitName = prizeName.get(hitNo);
		log.debug(id +" You Got : "+hitName);
		try {
			String ubno = pd.QueryPrizeUBno(hitNo);
			pd.updatePrizeHit(uid, txnName, hitNo, ubno);
			pd.updatePrizeUBno(ubno);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		return hitNo;
		
	}

	   public int initData(int[] initNo, int[] initNum, String[] initName){
			for(int i=0; i<initNo.length; i++){
				prize.put(initNo[i], initNum[i]);
				prizeName.put(initNo[i], initName[i]);
			}
			Set<Integer> key0 = prize.keySet();
			Iterator<Integer> it0 = key0.iterator();
			int total0 = 0;
			while(it0.hasNext()){
				//System.out.println(it.next());
				int kk = it0.next();
				if (kk > 0)
					total0 = total0 + prize.get(kk);
			}
			prize.put(0, total0*prsb+1);
			int total = 0;
			int total1 = 0;
			Set<Integer> key = prize.keySet();
			Iterator<Integer> it = key.iterator();
			rprize.clear();
			while(it.hasNext()){
				//System.out.println(it.next());
				int kk = it.next();
				total = total + prize.get(kk);
				if (total > total1){
					rprize.put(total, kk);
					total1 = total;
				}
			}
//			System.out.println(rprize);
	        return total;
	    }
	    public int prizeHit(int hit){
			Set<Integer> key1 = rprize.keySet();
			Iterator<Integer> it1 = key1.iterator();
			int max = 999999;
//			hit=5001;
			while(it1.hasNext()){
				//System.out.println(it.next());
				int kk = it1.next();
				if (hit <= kk && max > kk){
					max = kk;
				}
			}
//			System.out.println(max);
//			System.out.println(rprize.get(max)+ prizeName.get(rprize.get(max)));
			return rprize.get(max);
	    	
	    }

}