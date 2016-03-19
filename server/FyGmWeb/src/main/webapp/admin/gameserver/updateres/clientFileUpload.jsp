<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags" %> 
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
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
<link href='<%=response.encodeURL(request.getContextPath()+"/css/style.css")%>' rel="stylesheet" type="text/css" />
<script type="text/javascript" src='<%=response.encodeURL(request.getContextPath() + "/js/jquery-1.7.2.min.js")%>'></script>
</head>
<body>
<center>
	<h1>上传游戏客户端资源更新文件(请压缩成zip格式的压缩文件)</h1>
	<form method="post" action="<%=response.encodeURL(request.getContextPath() + "/admin/clientupload.a")%>" enctype="multipart/form-data" accept-charset="utf-8">
		<input type="file" name=upload />
		<input type="submit"  value="上传"/><br/>
	</form>
	</center>
</body>
</html>