package iwin.jdbc;

import iwin.jdbc.model.PrizeHit;
import iwin.jdbc.model.PrizeList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrizeDao extends BaseDao1 {
	PreparedStatement ps = null;
	ResultSet rs = null;
	public void insertPrizeHit(String id, String uid){
		String sql = " insert into PrizeHit (id, uid, txn_name, hitdate, hittime, prize_name, modify_date)  " +
	                  " values (?,?,?,?,?,?,?)";
		try {
			String sdf =  new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ps.setString(2, uid);
			ps.setString(3, "");
			ps.setString(4, sdf.substring(0,8));
			ps.setString(5, sdf.substring(8));
			ps.setInt(6, 999);
			ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				ps.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void updatePrizeHit(String uid, String txn_name, int prize_name, String ubno) throws Exception {
		String sql = "update PrizeHit set prize_name = ?, txn_name = ?, hitdate = ?, hittime = ?, modify_date = ?, ubno = ? where uid = ?";
		try {
			String sdf =  new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			ps = conn.prepareStatement(sql);
			ps.setInt(1, prize_name);
			ps.setString(2, txn_name);
			ps.setString(3, sdf.substring(0,8));
			ps.setString(4, sdf.substring(8));
			ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
			ps.setString(6, ubno);
			ps.setString(7, uid);
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("Database Error");
		}finally{
			try {
				ps.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public List<PrizeList> QueryPrize(){
			String sql = " select * from PrizeList where ? between start and stop";
			try {
				String sdf =  new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
				ps = conn.prepareStatement(sql);
				ps.setString(1, sdf.substring(0, 8));
				rs = ps.executeQuery();
				List<PrizeList> ret = new ArrayList<PrizeList>();
				while(rs.next()){
					PrizeList pl = new PrizeList();
					pl.setPrizeId(rs.getInt(1));
					pl.setPrizeName(rs.getString(2));
					pl.setStart(rs.getString(3));
					pl.setStop(rs.getString(4));
					pl.setAmount(rs.getInt(5));
					pl.setModifyDate(rs.getTimestamp(6));
					ret.add(pl);
				}
				return ret;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}finally{
				try {
					ps.close();
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
	}

	public List<PrizeHit> QueryPrizeHit(String id, String startInq, String stopInq, String txnName, String prizeNo){
		String sql = "select a.id,a.hitdate, a.hittime, a.txn_name, b.prize_name, a.ubno from PrizeList b, PrizeHit a where a.prize_name = b.prize_id ";
		String whereCon = "";
		try {
			if (id.trim().length() != 0)
				whereCon = whereCon + "and a.id like '"+id+"' ";
			if (startInq.trim().length() != 0)
				whereCon = whereCon + "and a.hitdate between '"+startInq+"' and '"+stopInq+"' ";
			if (txnName.trim().length() != 0)
				whereCon = whereCon + "and a.txn_name = '"+txnName+"' ";
			if (prizeNo.trim().length() != 0)
				whereCon = whereCon + "and a.prize_name = "+Integer.parseInt(prizeNo);
			sql = sql + whereCon;
			System.out.println(sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			List<PrizeHit> ret = new ArrayList<PrizeHit>();
			while(rs.next()){
				PrizeHit pl = new PrizeHit();
				pl.setId(rs.getString(1));
				pl.setHitDate(rs.getString(2)+rs.getString(3));
				pl.setTxnName(rs.getString(4));
				pl.setPrizeName(rs.getString(5));
				pl.setUbno(rs.getString(6));
				ret.add(pl);
			}
			return ret;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}finally{
			try {
				ps.close();
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	}

	public int QueryPrizeHit(String uid){
		String sql = "select prize_name from PrizeHit where uid = ?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, uid);
			rs = ps.executeQuery();
			int ret=0;
			while(rs.next()){
				ret = rs.getInt(1);
			}
			return ret;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}finally{
			try {
				ps.close();
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	}

	public int getHitCount(int prizeNo){
		String sql = " select count(*) as count from PrizeHit where ? = left(hitdate, 6) and prize_name = ?";
		try {
			String sdf =  new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			ps = conn.prepareStatement(sql);
			ps.setString(1, sdf.substring(0, 6));
			ps.setInt(2, prizeNo);
			rs = ps.executeQuery();
			int ret=0;
			while(rs.next()){
				ret = rs.getInt("count");
			}
			return ret;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}finally{
			try {
				ps.close();
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public boolean getIdCount(String id){
		String sql = " select count(*) as count from PrizeList where ? between start and stop and prize_id = '0'";
		try {
			String sdf =  new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			ps = conn.prepareStatement(sql);
			ps.setString(1, sdf.substring(0, 8));
			rs = ps.executeQuery();
			int ret=0;
			while(rs.next()){
				ret = rs.getInt("count");
			}
			if (ret == 0)
				return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}finally{
			try {
				ps.close();
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		sql = " select count(*) as count from PrizeHit where ? = hitdate and id = ? and prize_name <> '999'";
		try {
			String sdf =  new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			ps = conn.prepareStatement(sql);
			ps.setString(1, sdf.substring(0, 8));
			ps.setString(2, id);
			rs = ps.executeQuery();
			int ret=0;
			while(rs.next()){
				ret = rs.getInt("count");
			}
			if (ret < 5)
				return true;
			else 
				return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}finally{
			try {
				ps.close();
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public String QueryPrizeUBno(int prize_id){
		String sql = "select top 1 prize_ubno from PrizeUBno where prize_id = ? and used <> 'Y'";
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, prize_id);
			rs = ps.executeQuery();
			String ret = "";
			while(rs.next()){
				ret = rs.getString(1);
			}
			return ret;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}finally{
			try {
				ps.close();
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	}

	public void updatePrizeUBno(String ubno) throws Exception {
		String sql = "update PrizeUBno set used = 'Y' where prize_ubno = ?";
		try {
			String sdf =  new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			ps = conn.prepareStatement(sql);
			ps.setString(1, ubno);
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("Database Error");
		}finally{
			try {
				ps.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
