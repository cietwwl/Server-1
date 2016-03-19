<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags" %> 
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<%  
String path = request.getContextPath();  
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";  
%>
<head>
<title>飞雨后台管理登陆</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Cache-Control" content="no-cache" />
<STYLE type="text/css">
body {
	line-height: 30px;
	background-color: #EEF2FB;
}
</STYLE>
<link href='<%=response.encodeURL(request.getContextPath()+"/css/bootstrap/bootstrap.min.css")%>' rel="stylesheet">
<link href='<%=response.encodeURL(request.getContextPath()+"/css/bootstrap/bootstrap-responsive.min.css")%>' rel="stylesheet">
<link href='<%=response.encodeURL(request.getContextPath()+"/css/bootstrap/datetimepicker.css")%>' rel="stylesheet">
<link href='<%=response.encodeURL(request.getContextPath()+"/css/table_tools/jquery.dataTables.css")%>' rel="stylesheet">
<link href='<%=response.encodeURL(request.getContextPath()+"/css/main.css")%>' rel="stylesheet">
<link href='<%=response.encodeURL(request.getContextPath()+"/css/histogram.css")%>' rel="stylesheet">