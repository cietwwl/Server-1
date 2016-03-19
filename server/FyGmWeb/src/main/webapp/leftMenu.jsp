<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.Date"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags" %> 
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<base target="main" />
<title>管理首页</title>
<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
<link href='<%=response.encodeURL(request.getContextPath() + "/css/style.css")%>' rel="stylesheet" type="text/css" />
<style type="text/css">
html,body {font:14px Arial, Helvetica, sans-serif;color: #fff;margin-top:-10px;background-color: #677cd9;margin: 0px;width: 200;}
a {margin-left: 10px;}
a:link {color: #fff;text-decoration: none;}
a:visited {color: #fff;text-decoration: none;}
a:hover {text-decoration: underline;color: #ff0000;}
</style>
</head>
<body>
<div class="comul">
<ul>
<li><font style="margin-left: 10px;" color="red">欢迎  <sec:authentication property="name"></sec:authentication></font></li>
<li><a href='<%=response.encodeURL(request.getContextPath()+"/admin/user/cpwd.jsp")%>'>修改密码</a><a href='<%=response.encodeURL(request.getContextPath()+"/j_spring_security_logout")%>' target="_parent">退出</a></li>
<sec:authorize ifAnyGranted="0"><!-- sec:authorize 管理员才能访问里面的节点 -->
<li><a href="<%=response.encodeURL(request.getContextPath()+"/admin/getLogList.a")%>" >后台操作记录</a><br/></li>
<li><a href="<%=response.encodeURL(request.getContextPath()+"/admin/a_list.a")%>" >权限分配</a><br/></li>
<li><a href="<%=response.encodeURL(request.getContextPath()+"/admin/_list.a")%>" >用户管理</a><br/></li>
</sec:authorize>
<!--<li><a href="<%=response.encodeURL(request.getContextPath()+"/admin/gameserver/updateres/fileupload.jsp")%>" >上传文件</a><br/></li>-->
<!-- <li><a href="<%=response.encodeURL(request.getContextPath()+"/admin/update_list.a")%>" >更新文件</a><br/></li> 
<li><a href="<%=response.encodeURL(request.getContextPath()+"/admin/rollback_list.a")%>" >回滚服务器</a><br/></li>
<li><a href="<%=response.encodeURL(request.getContextPath()+"/admin/gameserver/updateres/rollbackClient.jsp")%>" >回滚客户端</a><br/></li>-->
<!--<li><a href="<%=response.encodeURL(request.getContextPath()+"/admin/svnlist.a")%>" >上传SVN文件</a><br/></li>-->
<!--<li><a href="<%=response.encodeURL(request.getContextPath()+"/admin/tool/syncRes.jsp")%>" >内外网资源同步</a><br/></li>-->
<!--<li><a href="<%=response.encodeURL(request.getContextPath()+"/admin/commitExcel.a")%>" >上传Excel</a><br/></li> -->
<li><a href="<%=response.encodeURL(request.getContextPath()+"/admin/gameserver/updateres/clientFileUpload.jsp")%>" >上传客户端文件</a><br/></li>
<li><a href="<%=response.encodeURL(request.getContextPath()+"/admin/tool/svn/svnindex.jsp")%>" >配置表管理</a><br/></li>
<li><a href="<%=response.encodeURL(request.getContextPath()+"/admin/tool/server/serverIndex.jsp")%>" >服务器管理</a><br/></li>
<li><a href="<%=response.encodeURL(request.getContextPath()+"/admin/gameserver/statistics/statisticsIndex.jsp")%>" >数据统计</a><br/></li>
</ul>
</div>
</body>
</html>