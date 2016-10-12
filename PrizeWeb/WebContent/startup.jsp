<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>StartUp</title>
</head>
<body>
	<p />
	<div align="center">
		<b>Start Up Time: <font color="blue"><%=application.getAttribute(iwin.servlet.StartupContextListener.START_TIME_KEY)%></font>
		</b>&nbsp;<font color="#666666">(Build: <%=application.getAttribute(iwin.servlet.StartupContextListener.BUILD_VERSION_KEY)%>)</font>
	</div>
	<p />
	<div align="center">
		<b><font color="green">Open socket channels size:</font></b>&nbsp;<font color="red">${server.socketChannelSize}</font>
	</div>
</body>
</html>