<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>HOME</title>
</head>
<body>
	System Properties !
	<hr />
	<%
		//java.util.Properties prop = System.getProperties();
	    java.util.Map<String, Object> prop = new java.util.TreeMap(System.getProperties());
		for (String key: prop.keySet())	{
			out.println("<br/><nobr>" + key + "=" + prop.get(key) + "</nobr>");
		}
	%>
</body>
</html>