<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.Date"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>飞雨后台管理</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="Cache-Control" content="no-cache" />
		<STYLE type="text/css">
body {
	line-height: 30px;
	background-color: #EEF2FB;
}
</STYLE>
		<link
			href='<%=response.encodeURL(request.getContextPath() + "/css/style.css")%>'
			rel="stylesheet" type="text/css" />
		<script type="text/javascript"
			src='<%=response.encodeUrl(request.getContextPath() + "/js/jquery-1.7.2.min.js")%>'></script>
	</head>
	<body>
		<br />
		<br />
		<form name='f' id="form1" action='<%=response.encodeUrl(request.getContextPath() + "/admin/cpwd.a")%>' method='POST'>
			<table border="0" align="center" cellpadding="0" cellspacing="0"
				style="text-align: center;">
				<tr>
					<td>
						原密码:
						<input type='password' id="password1" name='roles' />
					</td>
				</tr>
				<tr>
					<td>
						新密码:
						<input type='password' id="password1" name='roles' />
					</td>
				</tr>
				<tr>
					<td>
						确认新:
						<input type='password' id="password2" name='roles' />
						<br />
					</td>
				</tr>
				<tr>
					<td>
						&nbsp;
						<input name="submit" type="submit" value="修改" />
						&nbsp;
						<input name="reset" type="reset" />
					</td>
				</tr>
				<tr>
					<td>
					<br/>
					<span style="font-size:12px;color:#aaa;">密码长度8~20为宜,加密处理可放心设置</span>
					</td>
				</tr>
			</table>
		</form>
	</body>
</html>