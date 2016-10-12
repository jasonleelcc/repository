<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Menu</title>
</head>
<%
	String tdStyle = "valign='middle' bgcolor='#53a4d4' align='center'";
	String style = "style=\"color: white; font-size: 12px; text-decoration: none; line-height: 22px; font-weight: bold;\"";
%>
<body>
	<table width="100%" cellspacing="0" cellpadding="0" bordercolor="#cecece" border="1" style="border-collapse: collapse;">
		<tr>
			<td <%=tdStyle%>>
				<div>
					<a href="./txnlog" target="rightContent" <%=style%>>查看Log檔案</a>
				</div>
			</td>
		</tr>
		<tr>
			<td <%=tdStyle%>>
				<div>
					<a href="./test_conn.jsp" target="rightContent" <%=style%>>測試資料庫連線</a>
				</div>
			</td>
		</tr>
		<tr>
			<td <%=tdStyle%>>
				<div>
					<a href="./monitoring?part=counterSummaryPerClass&counter=services" target="rightContent" <%=style%>>中介電文統計資訊</a>
				</div>
			</td>
		</tr>
		<tr>
			<td <%=tdStyle%>>
				<div>
					<a href="http://<%=request.getLocalAddr()%>:<%=iwin.servlet.StartupContextListener.jmxAdapterPort%>/Filter?fstr=Hexa%3A*" target="rightContent"
						<%=style%>>查看系統參數</a>
				</div>
			</td>
		</tr>
		<tr>
			<td <%=tdStyle%>>
				<div>
					<a href="./monitoring" target="rightContent" <%=style%>>監視系統狀態</a>
				</div>
			</td>
		</tr>
		<tr>
			<td <%=tdStyle%>>
				<div>
					<a href="./sys.jsp" target="rightContent" <%=style%>>JVM系統資訊</a>
				</div>
			</td>
		</tr>
		<tr>
			<td <%=tdStyle%>>
				<div>
					<a href="./test/test-action.jsp?output=xml" target="_blank" <%=style%>>##網頁直接測試電文</a>
				</div>
			</td>
		</tr>
		<tr>
			<td <%=tdStyle%>>
				<div>
					<a href=".//test/testio?output=xml&testrq=db-rq" target="_blank" <%=style%>>*測試資料庫電文</a>
				</div>
			</td>
		</tr>
		<tr>
			<td <%=tdStyle%>>
				<div>
					<a href=".//test/testio?output=xml&testrq=mbank-rq" target="_blank" <%=style%>>*測試MBANK電文</a>
				</div>
			</td>
		</tr>
		<tr>
			<td <%=tdStyle%>>
				<div>
					<a href=".//test/testio?output=xml&testrq=ecard-rq" target="_blank" <%=style%>>*測試ECARD電文</a>
				</div>
			</td>
		</tr>
		<tr>
			<td <%=tdStyle%>>
				<div>
					<a href="./test/testio?proxy=hsr" target="rightContent" <%=style%>>*測試高鐵抓XML</a>
				</div>
			</td>
		</tr>
		<tr>
			<td <%=tdStyle%>>
				<div>
					<a href="./test/testio?proxy=tra" target="rightContent" <%=style%>>*測試台鐵抓XML</a>
				</div>
			</td>
		</tr>
		<tr>
			<td <%=tdStyle%>>
				<div>
					<a href="./test/testio?proxy=tra-price" target="rightContent" <%=style%>>*測試台鐵網站抓票價</a>
				</div>
			</td>
		</tr>
		<tr>
			<td <%=tdStyle%>>
				<div>
					<a href="./test/testio?listxml=hsr-xml" target="rightContent" <%=style%>>$$目前高鐵抓回的XML</a>
				</div>
			</td>
		</tr>
		<tr>
			<td <%=tdStyle%>>
				<div>
					<a href="./test/testio?listxml=tra-xml" target="rightContent" <%=style%>>$$目前台鐵抓回的XML</a>
				</div>
			</td>
		</tr>
		<tr>
			<td <%=tdStyle%>>
				<div>
					<a href="./test/testio?listxml=timer-xml" target="rightContent" <%=style%>>$$目前BANK/ECARD定時抓回</a>
				</div>
			</td>
		</tr>
		<tr>
			<td <%=tdStyle%>>
				<div>
					<a href="./test/testio?pythagorean=true" target="rightContent" <%=style%>>測兩點座標距離(畢氏定理)</a>
				</div>
			</td>
		</tr>
	</table>
</body>
</html>