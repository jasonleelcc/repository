/*
 * Copyright 1999,2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package iwin.util;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import javax.sql.DataSource;

import net.sf.log4jdbc.ConnectionSpy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * taidi (20120702 已上正式)
 * @author leotu@nec.com.tw
 * @update 20120907 taidi
 */
public class DataSourceWrapper implements DataSource {
	protected static final Logger log = LoggerFactory.getLogger(DataSourceWrapper.class);

	protected DataSource delegate;

	/**
	 * Default constructor
	 */
	public DataSourceWrapper(DataSource delegate) {
		this.delegate = delegate;
	}

	public PrintWriter getLogWriter() throws SQLException {
		return delegate.getLogWriter();
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return delegate.unwrap(iface);
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		delegate.setLogWriter(out);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return delegate.isWrapperFor(iface);
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		delegate.setLoginTimeout(seconds);
	}

	public Connection getConnection() throws SQLException {	
		Connection conn = delegate.getConnection();
		//log.debug("conn=" + conn);
		if (!(conn instanceof ConnectionSpy)) {
			conn = new ConnectionSpy(conn);
		}
		return conn;
	}

	public Connection getConnection(String username, String password) throws SQLException {
		Connection conn = delegate.getConnection(username, password);
		//log.debug("conn=" + conn);
		if (!(conn instanceof ConnectionSpy)) {
			conn = new ConnectionSpy(conn);
		}
		return conn;
	}

	public int getLoginTimeout() throws SQLException {
		return delegate.getLoginTimeout();
	}

	// JDK 1.7
	//@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

}
