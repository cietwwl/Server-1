<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.dx.gods.common.gameserver.GameServer"%>
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
		<form action='<%=response.encodeURL(request.getContextPath()+ "/admin/rollbackserverres.a")%>' enctype="multipart/form-data" id="form" method="post">
			<div style="height: 30px; float: center; margin-right: 8px;">
				<p>请选择对应的服务器回滚</p>
				<label>服务器组</label> <select id="server" name="serverid"
					style="width: 100px">
					<s:iterator value="serverlist" status="st">
						<option value=<s:property value="id" />
							selected="selected"><s:property value="serverName" /></option>
					</s:iterator>
				</select> <input type="submit" value="回滚" />
			</div>
		</form>
	</center>
</body>
</html>