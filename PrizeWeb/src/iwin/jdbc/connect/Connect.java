package iwin.jdbc.connect;

import java.sql.Connection;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * taidi (20120702 已上正式)
 * Insert the type's description here.
 * Creation date: (2002/8/19 �U�� 03:32:01)
 * @author: Administrator
 * @update 20120907 taidi
 */
public class Connect {
	/**
	 * Connect constructor comment.
	 */
	public Connect() {
		super();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2002/8/19 �U�� 03:50:35)
	 * @param con java.sql.Connection
	 */
	public static void close(Connection con) {
		try {
			con.close();
		} catch (Exception e) {
			System.out.println("close error " + e);
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2002/8/19 �U�� 03:38:55)
	 * @return java.sql.Connection
	 * @param userid java.lang.String
	 * @param password java.lang.String
	 */
	public static java.sql.Connection getConnection(DataSource ds,String userId,String password) {
		Connection con = null;
		int _iCount = 0;
		while (true) {
			try {
				con = ds.getConnection();
				break;
			} catch (Exception e) {
				System.out.println("getConnection error " + e);
				try {
					_iCount++;
					if (_iCount < 5) {
						Thread.sleep(150);
					} else {
						System.out.println("getConnection error " + e);
						break;
					}
				} catch (Exception ex) {
					System.out.println("sleep error " + ex);
				}
			}
		}
		return con;
	}
	
	
	
	/**
	 * Insert the method's description here.
	 * Creation date: (2002/8/19 �U�� 03:33:26)
	 * @return javax.sql.DataSource
	 */
	public static javax.sql.DataSource getDataSource(String DataSourceName) {
		DataSource ds = null;
		try {
			javax.naming.InitialContext initctx = new javax.naming.InitialContext();
			javax.naming.Context envctx = (Context) initctx.lookup("java:comp/env");
 			ds = (DataSource) envctx.lookup(DataSourceName);
		} catch (NamingException e) {
			System.err.println(
				"DataSource Error:Naming-Exception: " + e.getMessage());
			return null;
		}
		return ds;
	}
}