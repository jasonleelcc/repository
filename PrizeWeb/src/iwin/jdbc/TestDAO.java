package iwin.jdbc;

import iwin.front.net.TcpSocketServer;
import iwin.jdbc.connect.Connect;
import iwin.jdbc.model.PrizeList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;



public class TestDAO extends BaseDao1 {

	PreparedStatement ps = null;
	ResultSet rs = null;
	public static HashMap<Integer, Integer> prize = new HashMap<Integer, Integer>();
	public static HashMap<Integer, Integer> rprize = new HashMap<Integer, Integer>();
	public static HashMap<Integer, String> prizeName = new HashMap<Integer, String>();
	public static int hitCount = 0;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestDAO td = new TestDAO();
		try {
			td.test();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void test() throws SQLException{
//		PrizeDao pd = new PrizeDao();
//		System.out.println(pd.getIdCount("F223877730"));
		int hitNo = 0;
		
		hitNo = Draw();
		if (hitNo != 0) {hitCount ++;System.out.println("!!!!!!!="+hitCount);} 
	}
	
	public static synchronized int Draw(){
		PrizeDao pd = new PrizeDao();
		List<PrizeList> al = pd.QueryPrize();
//		System.out.println(al.size());
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
		Random ran = new Random();
		int hit = ran.nextInt(total)+1;
		int hitNo = prizeHit(hit);
		String hitName = prizeName.get(hitNo);
//		System.out.println(" You Got : "+hitNo + ":" +hitName);
//		pd.insertPrizeHit("A123456789", "qazwsxedc");
		return hitNo;
		
	}	
    public static int initData(int[] initNo, int[] initNum, String[] initName){
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
		prize.put(0, total0*100+1);
//		System.out.println(prize);
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
//		System.out.println(rprize);
        return total;
    }
    public static int prizeHit(int hit){
		Set<Integer> key1 = rprize.keySet();
//		System.out.println(rprize);
		Iterator<Integer> it1 = key1.iterator();
		int max = 999999;
//		hit=5001;
		while(it1.hasNext()){
			//System.out.println(it.next());
			int kk = it1.next();
			if (hit <= kk && max > kk){
				max = kk;
			}
		}
//		System.out.println(max);
//		System.out.println(rprize.get(max)+ prizeName.get(rprize.get(max)));
		return rprize.get(max);
    	
    }


}
