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
	function query_registered(){
		var obj = document.getElementById("server");
		var index = obj.selectedIndex;
		var value = obj.options[index].value;
		var link_value = document.getElementById("link_registered_value").value;
		document.getElementById("link_registered").href = link_value + value;
	}
	
	function query_lvspread(){
		var obj = document.getElementById("server");
		var index = obj.selectedIndex;
		var value = obj.options[index].value;
		var link_value = document.getElementById("link_lvspread_value").value;
		document.getElementById("link_lvspread").href = link_value + value;
	}
	
	function query_active(){
		var obj = document.getElementById("server");
		var index = obj.selectedIndex;
		var value = obj.options[index].value;
		var link_value = document.getElementById("link_active_value").value;
		document.getElementById("link_active").href = link_value + value;
	}
	
	function query_retained(){
		var obj = document.getElementById("server");
		var index = obj.selectedIndex;
		var value = obj.options[index].value;
		var link_value = document.getElementById("link_retained_value").value;
		document.getElementById("link_retained").href = link_value + value;
	}
</script>
</head>
<body>
	<label>svn server</label>
	<select id="server" name="svnName" style="width: 100px">
		<s:iterator value="list" status="st">
			<option value=<s:property value="versionId" /> selected="selected"><s:property
					value="versionName" /></option>
		</s:iterator>
	</select>
	<a href='<%=response.encodeURL(request.getContextPath() + "/admin/registeredserverlist.a?projectId=")%>' id="link_registered" onclick="query_registered()">注册统计</a>
	<input type="hidden" id="link_registered_value" value='<%=response.encodeURL(request.getContextPath() + "/admin/registeredserverlist.a?projectId=")%>' />
	
	<a href='<%=response.encodeURL(request.getContextPath() + "/admin/levelspreadserverlist.a?projectId=")%>' id="link_lvspread" onclick="query_lvspread()">等级分布</a>
	<input type="hidden" id="link_lvspread_value" value='<%=response.encodeURL(request.getContextPath() + "/admin/levelspreadserverlist.a?projectId=")%>' />
	
	<a href='<%=response.encodeURL(request.getContextPath() + "/admin/activeuserserverlist.a?projectId=")%>' id="link_active" onclick="query_active()">活跃玩家统计</a>
	<input type="hidden" id="link_active_value" value='<%=response.encodeURL(request.getContextPath() + "/admin/activeuserserverlist.a?projectId=")%>' />
	
	<a href='<%=response.encodeURL(request.getContextPath() + "/admin/retaineduserserverlist.a?projectId=")%>' id="link_retained" onclick="query_retained()">留存统计</a>
	<input type="hidden" id="link_retained_value" value='<%=response.encodeURL(request.getContextPath() + "/admin/retaineduserserverlist.a?projectId=")%>' />
</body>
</html>