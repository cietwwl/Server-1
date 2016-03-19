<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.Date"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<base target="submain" />
<title>管理首页</title>
<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
<link
	href='<%=response.encodeURL(request.getContextPath() + "/css/style.css")%>'
	rel="stylesheet" type="text/css" />
<style type="text/css">
html, body {
	font: 14px Arial, Helvetica, sans-serif;
	color: #fff;
	margin-top: -10px;
	background-color: #3c8cef;
	margin: 0px;
	width: 200;
}

a {
	margin-left: 10px;
}

a:link {
	color: #fff;
	text-decoration: none;
}

a:visited {
	color: #fff;
	text-decoration: none;
}

a:hover {
	text-decoration: underline;
	color: #ff0000;
}
</style>
<script type="text/javascript">
	
</script>
</head>
<body>
	<a href='<%=response.encodeURL(request.getContextPath() + "/admin/showClassList.a")%>' id="link_uploadclass">上传Class</a>
	<a href='<%=response.encodeURL(request.getContextPath() + "/admin/tool/server/syncClasses.jsp")%>' id="link_upload">同步Class</a>
	<a href='<%=response.encodeURL(request.getContextPath() + "/admin/server_list.a")%>' id="link_upload">停服(windows平台)</a>
	<a href='<%=response.encodeURL(request.getContextPath() + "/admin/linuxserver_sdlist.a")%>' id="link_upload">停服(linux平台)</a>
	<a href='<%=response.encodeURL(request.getContextPath() + "/admin/linuxserver_ulist.a")%>' id="link_upload">更新(linux平台)</a>
	<a href='<%=response.encodeURL(request.getContextPath() + "/admin/linuxserver_stlist.a")%>' id="link_upload">起服(linux平台)</a>
	<!-- <a href='<%=response.encodeURL(request.getContextPath() + "/admin/showLogList.a") %>' id="link_showlog">日志</a> -->
</body>
</html>