<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.Date"%>
<%@page import="com.dx.gods.controller.admin.sec.Role"%>
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
<br/><br/>
<table class="comtable" align="center" cellpadding="0" cellspacing="0" style="text-align: center;">
<tr>
<th width="50px">编号</th>
<th width="200px">用户</th>
<th width="100px">是否生效</th>
<th width="300px">角色</th>
<th width="200px">分区</th>
<th width="200px">渠道</th>
<th width="200px">操作</th>
</tr>
<s:iterator value="users" status="st">
<tr>
<td><s:property value="#st.index+1"/></td>
<td><s:property value="username"/></td>
<td><s:property value="enabled"/></td>
<td><s:property value="rolesDesc"/></td>
<td><s:property value="zoneId"/></td>
<td><s:property value="channel"/></td>
<td>
<s:if test="adminName!=username">
<a href='<%=response.encodeURL(request.getContextPath()+"/admin/_tedit.a?adminUser.username=")%><s:property value="username"/>'>修改</a>&nbsp;&nbsp;
<a href='<%=response.encodeURL(request.getContextPath()+"/admin/_delete.a?adminUser.username=")%><s:property value="username"/>'>删除</a>
</s:if></td>
</tr>
</s:iterator>
</table>
<br/><br/>
<form action='<%=response.encodeURL(request.getContextPath() + "/admin/_add.a")%>' enctype="multipart/form-data" id="form" method="post">
<table class="comtable" align="center" cellpadding="0" cellspacing="0" style="text-align: center;">
<tr>
<th width="200px">用户</th>
<th width="200px">初始密码<br/>(默认name+"@gods.dx")</th>
<th width="150px">是否生效</th>
<th width="200px">角色</th>
<th width="200px">分区</th>
<th width="200px">渠道</th>
<th width="100px">操作</th>
</tr>
<tr>
<td><input type="text" name="adminUser.username" /></td>
<td><input type="text" name="adminUser.password" /></td>
<td>
<input type="radio" name="adminUser.enable" value="1" checked>true &nbsp;
<input type="radio" name="adminUser.enable" value="0">false
</td>
<td align="left">
<s:iterator value="@com.dx.gods.controller.admin.sec.Role@values()" id="item" status="st"> 
    <input type="radio" name="roles" value='<s:property value="#st.index"/>'><s:property value="#item.role" /><br/>
</s:iterator>
</td>
<td align="left">
<s:iterator value="zoneList" id="channel" status="st"> 
    <input type="checkbox" name="zoneId" value='<s:property value="zoneId"/>'><s:property value="zoneName" />(<s:property value="zoneId" />)<br/>
</s:iterator>
</td>
<td align="left">
<s:iterator value="channelList" id="channel" status="st"> 
    <input type="checkbox" name="channelId" value='<s:property value="id"/>'><s:property value="name" />(<s:property value="id" />)<br/>
</s:iterator>
</td>
<td>
<input type="submit" value="添加" />
</td>
</tr>
</table>
</form>
</body>
</html>