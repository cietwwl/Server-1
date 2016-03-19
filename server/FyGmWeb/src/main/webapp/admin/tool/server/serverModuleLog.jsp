<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%@page import="com.dx.gods.common.log.data.LoggerData"%>
<%@include file="/admin/common/header.jsp" %>
</head>
<body>
	<div align="center">
	<p>服务器管理操作日志</p>
	<table border="1" width="100%">
		<tr>
			<th align="center" width="20%">操作人</th>
			<th align="center" width="40%">操作时间</th>
			<th  align="center" width="50%">操作描述</th>
		</tr>
		<s:iterator value="logList" status="st">
		<tr>
			<td align="center"><s:property value="userName" /></td>
			<td align="center"><s:property value="operatorTime" /></td>
			<td align="center"><s:property value="operatorDesc" /></td>
		</tr>
		</s:iterator>
	</table>
	</div>
</body>
</html>