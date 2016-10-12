package iwin.jdbc;

import iwin.jdbc.connect.Connect;
import iwin.util.DataSourceWrapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * taidi (20120702 已上正式)
 * 基本DAO
 * 
 * @author leotu@nec.com.tw
 * @update 20120907 taidi
 */
public class BaseDao1 {
	protected static final Logger log = LoggerFactory.getLogger(BaseDao1.class);

	/**
	 * http://support.microsoft.com/kb/135861/zh-tw?spid=2852&sid=global
	 */
	protected static boolean isSqlServer = true;

	protected static DataSource ds;
	protected static boolean dataSourceWrapper = true; // TODO
	static {
		String dataSourceName = "jdbc/sqlserver1";
		try {
			ds = Connect.getDataSource(dataSourceName);
			if (ds == null) {
				// XXX: java.lang.NoClassDefFoundError: Could not initialize class iwin.jdbc.BaseDao
				throw new Exception("(ds == null) of " + dataSourceName);
			}
		} catch (Exception e) {
			log.error("", e);
			throw new ExceptionInInitializerError(e);
		}
		log.info("isSqlServer=" + isSqlServer + ", dataSourceWrapper=" + dataSourceWrapper);
		if (dataSourceWrapper) {
			ds = new DataSourceWrapper(ds);
		}
	}
	protected static Connection conn = Connect.getConnection(ds, "", "");

	/**
	 * 請用此Method,而不要直接使用"new QueryRunner(ds)"
	 * 
	 * @return
	 */
	protected QueryRunner createQueryRunner() {
		return new QueryRunnerExt(getDataSource(), true); // TODO: true
	}

	public static DataSource getDataSource() {
		return ds;
	}

	// ===============
	class QueryRunnerExt extends QueryRunner {

		public QueryRunnerExt() {
			super();
		}

		public QueryRunnerExt(boolean pmdKnownBroken) {
			super(pmdKnownBroken);
		}

		public QueryRunnerExt(DataSource ds, boolean pmdKnownBroken) {
			super(ds, pmdKnownBroken);
		}

		public QueryRunnerExt(DataSource ds) {
			super(ds);
		}

		@Override
		public void fillStatement(PreparedStatement stmt, Object... params) throws SQLException {
			if (params != null && params.length > 0) {
				boolean change = false;
				for (Object param : params) {
					if (param != null && (param instanceof java.util.Date) && !(param instanceof java.sql.Timestamp)
							&& !(param instanceof java.sql.Date) && !(param instanceof java.sql.Time)) {
						change = true;
						break;
					}
				}
				if (change) {
					Object params2[] = new Object[params.length];
					for (int i = 0; i < params.length; i++) {
						if (params[i] != null && (params[i] instanceof java.util.Date) && !(params[i] instanceof java.sql.Timestamp)
								&& !(params[i] instanceof java.sql.Date) && !(params[i] instanceof java.sql.Time)) {
							params2[i] = new java.sql.Timestamp(((java.util.Date) params[i]).getTime()); // TODO
						} else {
							params2[i] = params[i];
						}
					}
					params = params2;
				}
			}
			super.fillStatement(stmt, params);
		}
	}

	// ===============
	protected Date minDate(Date date) {
		try {
			return DateUtils.parseDate(DateFormatUtils.format(date, "yyyy-MM-dd"), new String[] { "yyyy-MM-dd" });
		} catch (Throwable e) {
			log.error("", e);
			throw new RuntimeException("date=[" + date + "]", e);
		}
	}

	protected Date minDateSqlServer(Date date) {
		try {
			return DateUtils.parseDate(DateFormatUtils.format(date, "yyyy-MM-dd") + " 00:00:00.001", new String[] { "yyyy-MM-dd HH:mm:ss.SSS" });
		} catch (Throwable e) {
			log.error("", e);
			throw new RuntimeException("date=[" + date + "]", e);
		}
	}

	// ===============
	protected Date maxDate(Date date) {
		try {
			return DateUtils.parseDate(DateFormatUtils.format(date, "yyyy-MM-dd") + " 23:59:59.999", new String[] { "yyyy-MM-dd HH:mm:ss.SSS" });
		} catch (Throwable e) {
			log.error("", e);
			throw new RuntimeException("date=[" + date + "]", e);
		}
	}

	protected Date maxDateSqlServer(Date date) {
		try {
			return DateUtils.parseDate(DateFormatUtils.format(date, "yyyy-MM-dd") + " 23:59:59.998", new String[] { "yyyy-MM-dd HH:mm:ss.SSS" });
		} catch (Throwable e) {
			log.error("", e);
			throw new RuntimeException("date=[" + date + "]", e);
		}
	}

	// ===============
	protected Date onlyDate(Date date) {
		return minDate(date);
	}

	// ===============
	// protected Date minOfStartDate(Date startDate) {
	// return minDate(startDate);
	// }
	//
	// protected Date maxOfEndDate(Date endDate) {
	// return maxDate(endDate);
	// }

	/**
	 * Using Sample: <code><pre>
	 * Date currentDate = new Date()
	 * String sql = "... start_date <= ? and ? <= end_date ...";
	 * return qry.query(sql, rsh, maxDate(currentDate), minDate(currentDate));
	 * or
	 * return qry.query(sql, rsh, maxOfStartDate(currentDate), minOfEndDate(currentDate));
	 * <pre></code>
	 */
	protected Date maxOfStartDate(Date startDate) {
		return isSqlServer ? maxDateSqlServer(startDate) : maxDate(startDate);
	}

	/**
	 * Using Sample: <code><pre>
	 * Date currentDate = new Date()
	 * String sql = "... start_date <= ? and ? <= end_date ...";
	 * return qry.query(sql, rsh, maxDate(currentDate), minDate(currentDate));
	 * or
	 * return qry.query(sql, rsh, maxOfStartDate(currentDate), minOfEndDate(currentDate));
	 * <pre></code>
	 */
	protected Date minOfEndDate(Date endDate) {
		// return isSqlServer ? minDateSqlServer(endDate) : minDate(endDate);
		return minDate(endDate);
	}

	// ===============
	protected Date toDate(String dateStr, String formatStr) {
		return new SimpleDateFormat(formatStr).parse(dateStr, new ParsePosition(0));
	}

	protected String toString(Date date, String formatStr) {
		return new SimpleDateFormat(formatStr).format(date);
	}
}
