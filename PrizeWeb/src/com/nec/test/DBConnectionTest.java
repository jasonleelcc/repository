package com.nec.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

/**
 * http://msdn.microsoft.com/library/ms378672.aspx
 * 
 * @author leotu@nec.com.tw
 */
public class DBConnectionTest {
	protected static Logger logger = Logger.getLogger(DBConnectionTest.class.getName());

	public static void main(String[] args) {
		String connectionUrl = "jdbc:jtds:sqlserver://172.28.66.144:1433/mobile_ub";
		//String connectionUrl = "jdbc:sqlserver://172.28.66.144:1433;databaseName=mobile_ub";
		// Declare the JDBC objects.
		Connection conn = null;
		// Statement stmt = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			logger.info("BEGIN...");
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
			// Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			logger.info("after forName...");
			// Properties pro = new Properties();
			conn = DriverManager.getConnection(connectionUrl, "mobile_ub_user", "1qaz2wsx3edc");
			logger.info("connection is Ok!<br/>");

			// stmt = conn.createStatement();
			// String sql = "select 'hello' as hello";
			String sql = "SELECT start_date FROM bulletin a " + " WHERE a.status = 'checked' " + //
					"   AND a.creater_dept_id like 'B%' " + //
					"   AND a.creater_dept_id <> 'B600'" + //
					"   AND a.modify_date = (SELECT MAX(b.modify_date) FROM bulletin b" + //
					"                         WHERE b.status = 'checked'" + //
					"					       AND b.creater_dept_id like 'B%' " + //
					"                           AND b.creater_dept_id <> 'B600')" + //
					// "   AND  a.start_date <= convert(datetime, '2011-09-15 23:59:59.999', 121)";
					"   AND  a.start_date <= ?";
			stmt = conn.prepareStatement(sql);
			Date date = toDate("2011-09-15 23:59:59.999", "yyyy-MM-dd HH:mm:ss.SSS");
			logger.info("date=" + toString(date, "yyyy-MM-dd HH:mm:ss.SSS"));
			logger.info("(2)date=" + toString(new Timestamp(date.getTime()), "yyyy-MM-dd HH:mm:ss.SSS"));
			stmt.setTimestamp(1, new Timestamp(date.getTime()));
			//stmt.setDate(1, new java.sql.Date(date.getTime()));
			// rs = stmt.executeQuery(sql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				logger.info("---> RS:" + rs.getString(1));
			}
			logger.info("query is Ok!<br/>");
			logger.info("END...");
			//

		} catch (Throwable e) {
			logger.log(Level.WARNING, "", e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (Exception e) {
				}
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
				}
		}
	}

	static protected Date toDate(String dateStr, String formatStr) {
		try {
			return DateUtils.parseDate(dateStr, new String[] { formatStr });
		} catch (Throwable e) {
			throw new RuntimeException("dateStr=[" + dateStr + "], formatStr=[" + formatStr + "]", e);
		}
		// return new SimpleDateFormat(formatStr).parse(dateStr, new ParsePosition(0));
	}

	static protected String toString(Date date, String formatStr) {
		return DateFormatUtils.format(date, formatStr);
		// return new SimpleDateFormat(formatStr).format(date);
	}
}