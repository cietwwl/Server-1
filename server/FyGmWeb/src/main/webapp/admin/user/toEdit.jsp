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
<form action='<%=response.encodeURL(request.getContextPath() + "/admin/_edit.a")%>' enctype="multipart/form-data" id="form" method="post">
<table class="comtable" align="center" cellpadding="0" cellspacing="0" style="text-align: center;">
<tr>
<th width="200px">用户</th>
<th width="150px">是否生效</th>
<th width="200px">角色</th>
<th width="200px">分区</th>
<th width="200px">渠道</th>
<th width="100px">操作</th>
</tr>
<tr>
<td><input type="text" name="adminUser.username" value='<s:property value="adminUser.username"/>' /></td>
<td>
<input type="radio" name="adminUser.enable" value="1" <s:if test="adminUser.enable==1">checked="checked"</s:if>/>true &nbsp;
<input type="radio" name="adminUser.enable" value="0" <s:if test="adminUser.enable==0">checked="checked"</s:if>/>false
</td>
<td align="left">
<s:iterator value="@com.dx.gods.controller.admin.sec.Role@values()" id="item" status="st"> 
    <input type="radio" name="roles" value='<s:property value="#st.index"/>' <s:if test="adminUserRole==#item.ordinal()">checked="checked"</s:if>/><s:property value="#item.role" /><br/>
</s:iterator>
</td>
<td align="left">
<s:iterator value="zoneList" id="zone" status="st"> 
    <input type="checkbox" name="zoneId" value='<s:property value="zoneId"/>' <s:if test="zonesForAdminUser.contains(#zone.zoneId)">checked="checked"</s:if>/><s:property value="zoneName" />(<s:property value="zoneId" />)<br/>
</s:iterator>
</td>
<td align="left">
<s:iterator value="channelList" id="channel" status="st"> 
    <input type="checkbox" name="channelId" value='<s:property value="id"/>' <s:if test="channelsForAdminUser.contains(#channel.id)">checked="checked"</s:if>/><s:property value="name" />(<s:property value="id" />)<br/>
</s:iterator>
</td>
<td>
<input type="submit" value="修改" />
</td>
</tr>
</table>
</form>
</body>
</html>