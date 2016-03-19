<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.dx.gods.controller.admin.common.OpenIdSwapUtil"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="Cache-Control" content="no-cache" />
	<link href='<%=response.encodeURL(request.getContextPath() + "/css/style.css")%>' rel="stylesheet" type="text/css" />
	<title>action</title>
</head>
<body>
<table class="comtable" align="center" cellpadding="0" cellspacing="0" style="text-align: center;">
<tr>
<th width="200px">动作</th>
<th width="200px">描述</th>
<th width="400px">权限分配</th>
<th width="100px">操作</th>
</tr>
<s:iterator value="list" status="st">
<tr>
<form action='<%=response.encodeURL(request.getContextPath() + "/admin/a_update.a")%>' enctype="multipart/form-data" id="form" method="post">
<td align="left"><s:property value="name" /><input type="hidden" name="actionAuths.name" value='<s:property value="name" />' /></td>
<td><input type="text" name="actionAuths.desc" value='<s:property value="desc"/>' /></td>
<td align="left">
<input type="checkbox" name="roles" value="0" <s:if test='auths.contains("0")'>checked</s:if>><s:property value="@com.dx.gods.controller.admin.sec.Role@values()[0].getRole()"/>&nbsp;
<input type="checkbox" name="roles" value="1" <s:if test='auths.contains("1")'>checked</s:if>><s:property value="@com.dx.gods.controller.admin.sec.Role@values()[1].getRole()"/>&nbsp;
<input type="checkbox" name="roles" value="2" <s:if test='auths.contains("2")'>checked</s:if>><s:property value="@com.dx.gods.controller.admin.sec.Role@values()[2].getRole()"/>&nbsp;
<input type="checkbox" name="roles" value="3" <s:if test='auths.contains("3")'>checked</s:if>><s:property value="@com.dx.gods.controller.admin.sec.Role@values()[3].getRole()"/>&nbsp;
<input type="checkbox" name="roles" value="4" <s:if test='auths.contains("4")'>checked</s:if>><s:property value="@com.dx.gods.controller.admin.sec.Role@values()[4].getRole()"/>&nbsp;
<input type="checkbox" name="roles" value="5" <s:if test='auths.contains("5")'>checked</s:if>><s:property value="@com.dx.gods.controller.admin.sec.Role@values()[5].getRole()"/>&nbsp;
</td>
<td>
<input type="submit" value="修改" />
</td>
</form>
</tr>
</s:iterator>
</table>
<br/><br/>
</body>
</html>