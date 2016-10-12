<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String outputType = request.getParameter("output");
if (outputType == null) {
	outputType = "xml";
}
%>
<html>
<head>
<title>Test Service Response</title>
</head>

<body>
	<h1>請放入 電文 XML資料 (&lt;DataXML&gt;...&lt;/DataXML&gt;)</h1>
	<form action="<%=request.getContextPath()%>/test/testio" method="post">
		<table>
	   	    <tr>
				<td>Output:&nbsp;<input type="radio" name="output" value="xml" <%=(outputType.equalsIgnoreCase("xml") ? "checked=checked" : "")%>>XML</input>
				&nbsp;&nbsp;<input type="radio" name="output" value="text" <%=(outputType.equalsIgnoreCase("text") ? "checked=checked" : "")%>>TEXT</input>
				&nbsp;&nbsp;<input type="radio" name="output" value="html" <%=(outputType.equalsIgnoreCase("html") ? "checked=checked" : "")%>>HTML</input></td>
			</tr>
			<tr>
				<td><textarea name="input" id="input" style="width: 800px; height: 380px" wrap="off"></textarea></td>
			</tr>
			<tr>
				<td colspan=2><input type="submit" value="Send" /></td>
			</tr>
		</table>
	</form>
</body>
</html>
