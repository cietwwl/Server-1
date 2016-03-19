<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="java.net.URL"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="Cache-Control" content="no-cache" />
		<title>查询</title>
	</head>
	<body style="background-color: #000;color: #09db2b; font-size: 12px; padding: 0px;margin: 0;">
	<s:if test="fileName!=null"><div style="background-color: #09db2b;font-size: 14px; color:#fff; margin:0px; font-weight: bold;">文件: <%=System.getProperty("user.dir")+ "/logs/"%><s:property value="fileName"/> <span style="float:right;"><a href="<%=response.encodeURL(request.getContextPath()+"/admin/getLogList.a")%>" >返回</a>&nbsp;&nbsp;</span></div><br/></s:if>
		<s:iterator value="list" id="value" status="st">
			<s:property value="#value" /><br />
		</s:iterator>
	</body>
</html>