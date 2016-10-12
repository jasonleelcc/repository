<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="iwin.jdbc.BaseDao1,java.sql.*,javax.sql.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Test Connection Pool</title>
</head>
<body>
	<%
		Connection conn = null;
		Statement stmt = null;
		try {
			DataSource ds = BaseDao1.getDataSource();
			if (ds != null) {
				out.println("datasource is OK!<br/>");

				conn = ds.getConnection();
				if (conn != null) {
					out.println("connection is Ok!<br/>");

					stmt = conn.createStatement();
					String sql = "select 'hello' as hello";
					ResultSet rs = null;
					rs = stmt.executeQuery(sql);
					if (rs.next()) {
						out.println(rs.getString(1));
					}
					out.println("query is Ok!<br/>");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			out.print(e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
					out.print(e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
					out.print(e);
				}
			}
		}
	%>
</body>
</html>