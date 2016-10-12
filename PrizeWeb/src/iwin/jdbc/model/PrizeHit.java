package iwin.jdbc.model;

import java.sql.Date;
import java.sql.Timestamp;

public class PrizeHit {
    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getHitDate() {
		return hitDate;
	}
	public void setHitDate(String hitDate) {
		this.hitDate = hitDate;
	}
	public String getTxnName() {
		return txnName;
	}
	public void setTxnName(String txnName) {
		this.txnName = txnName;
	}
	public String getPrizeName() {
		return prizeName;
	}
	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}
	private String id;
    private String hitDate;
    private String txnName;
    private String prizeName;
    private String ubno;
	public String getUbno() {
		return ubno;
	}
	public void setUbno(String ubno) {
		this.ubno = ubno;
	}

}
