<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="java.net.URL"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib prefix="s" uri="/struts-tags" %> 
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="Cache-Control" content="no-cache" />
		<title>查询</title>
	</head>
	<body>
	<s:iterator value="list" id="value" status="st">
		<s:property value="#st.index+1"/>.<a href='<%=response.encodeURL(request.getContextPath()+"/admin/readLogFile.a?fileName=")%><s:property value="#value"/>' ><s:property value="#value"/></a><br/>
	</s:iterator>
	</body>
</html>