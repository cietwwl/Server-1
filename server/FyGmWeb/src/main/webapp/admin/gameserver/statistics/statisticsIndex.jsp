<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.Date"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags" %> 
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>飞雨科技后台管理</title>
</head>
<frameset rows="35,*" id="frame" framespacing="0" frameborder="no">
	<frame src="<%=response.encodeURL(request.getContextPath()+"/admin/serverstatisticssvnlist.a")%>" name="leftFrame" marginwidth="0" noresize="noresize" frameborder="0" scrolling="auto"/>
	<frame src="" name="submain" marginwidth="0" marginheight="10" frameborder="0" scrolling="auto" target="_self" />
</frameset>
<noframes>
  <body></body>
</noframes>
</html>