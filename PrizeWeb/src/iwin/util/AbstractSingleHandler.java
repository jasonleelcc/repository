package iwin.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

/**
 * 
 * @author leotu@nec.com.tw
 */
public abstract class AbstractSingleHandler<T> implements ResultSetHandler<T> {

	@Override
	public T handle(ResultSet rs) throws SQLException {
		if (rs.next()) {
			return this.handleRow(rs);
		} else {
			return null;
		}
	}

	protected abstract T handleRow(ResultSet rs) throws SQLException;
}
