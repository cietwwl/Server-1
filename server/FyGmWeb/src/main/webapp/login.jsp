<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.Date"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags" %> 
<html>
<head>
<title>飞雨后台管理登陆</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<base target="_top" />
<STYLE type="text/css">
body {
	line-height: 30px;
	background-color: #EEF2FB;
}
</STYLE>
</head>
<body onload='document.f.j_username.focus();'>
<form name='f' action='j_spring_security_check' method='POST' target="_top">
<table border="0" align="center" cellpadding="0" cellspacing="0" style="text-align: center;">
<tr><th height="80px;"><font color="red">非法入侵者将被依法追究法律责任！</font><br/></th></tr>
<tr><td>
账号:
<input type='text' name='j_username' value=''><br/></td></tr>
<tr><td>
密码:
<input type='password' name='j_password'/><br/>
</td></tr>
<tr><td>
&nbsp;<input name="submit" type="submit" value="登陆"/> 
&nbsp;<input name="reset" type="reset"/>
</td></tr>
<tr><td>
<span style="font-size:12px;color:#aaa;">版权所有 &copy;2011-2012 飞雨</span>
</td></tr>
</table>
</form>
</body>
</html>