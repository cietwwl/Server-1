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
	href='<%=response.encodeURL(request.getContextPath()
					+ "/css/style.css")%>'
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
	
	var link_update_value;
	var link_generate_value;
	var link_commit_value;
	if(link_update_value ==undefined){
		link_update_value = document.getElementById("link_update").href;
	}
	if(link_generate_value ==undefined){
		link_generate_value = document.getElementById("link_generate").href;
	}
	if(link_commit_value ==undefined){
		link_commit_value = document.getElementById("link_commit").href;
	}
	 

	function submit_upate() {
		var obj = document.getElementById("server");
		var index = obj.selectedIndex;
		var value = obj.options[index].value;
		var link_value = document.getElementById("link_update_value").value;
		if(link_update_value ==undefined){
			link_update_value = document.getElementById("link_update").href;
		}
		document.getElementById("link_update").href = link_value + value;

	}
	function submit_generate() {
		var obj = document.getElementById("server");
		var index = obj.selectedIndex;
		var value = obj.options[index].value;
		var link_value = document.getElementById("link_generate_value").value;
		document.getElementById("link_generate").href = link_value + value;
	}
	function submit_generatecsv() {
		var obj = document.getElementById("server");
		var index = obj.selectedIndex;
		var value = obj.options[index].value;
		var link_value = document.getElementById("link_generate_csv_value").value;
		document.getElementById("link_generate_csv").href = link_value + value;
	}
	function submit_commit() {
		var obj = document.getElementById("server");
		var index = obj.selectedIndex;
		var value = obj.options[index].value;
		var link_value = document.getElementById("link_commit_value").value;
		document.getElementById("link_commit").href = link_value + value;
	}
	
	function submit_upload(){
		var obj = document.getElementById("server");
		var index = obj.selectedIndex;
		var value = obj.options[index].value;
		var link_value = document.getElementById("link_upload_value").value;
		document.getElementById("link_upload").href = link_value + value;
	}
	function submit_uploadftp(){
		var obj = document.getElementById("server");
		var index = obj.selectedIndex;
		var value = obj.options[index].value;
		var link_value = document.getElementById("link_uploadftp_value").value;
		document.getElementById("link_uploadftp").href = link_value + value;
	}
	function submit_uploadclientftp(){
		var obj = document.getElementById("server");
		var index = obj.selectedIndex;
		var value = obj.options[index].value;
		var link_value = document.getElementById("link_uploadclientftp_value").value;
		document.getElementById("link_uploadclientftp").href = link_value + value;
	}
	function submit_autocommit(){
		var obj = document.getElementById("server");
		var index = obj.selectedIndex;
		var value = obj.options[index].value;
		var link_value = document.getElementById("link_autocommit_value").value;
		document.getElementById("link_autocommit").href = link_value + value;
	}
	function submit_incrementalres(){
		var obj = document.getElementById("server");
		var index = obj.selectedIndex;
		var value = obj.options[index].value;
		var link_value = document.getElementById("link_incrementalres_value").value;
		document.getElementById("link_incrementalres").href = link_value + value;
	}
	
	function submit_clearclientcache(){
		var obj = document.getElementById("server");
		var index = obj.selectedIndex;
		var value = obj.options[index].value;
		var link_value = document.getElementById("link_clearclientcache_value").value;
		document.getElementById("link_clearclientcache").href = link_value + value;
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

	<!-- 
	<a href='<%=response.encodeURL(request.getContextPath() + "/admin/tool/svn/uploadexcel.jsp?projectId=")%>' id="link_upload" onclick="submit_upload()">上传Excel </a>
		<input type="hidden" id="link_upload_value" value='<%=response.encodeURL(request.getContextPath() + "/admin/tool/svn/uploadexcel.jsp?projectId=")%>' />
	-->
	<a href='<%=response.encodeURL(request.getContextPath() + "/admin/svnupdate.a?projectId=")%>' id="link_update" onclick="submit_upate()">(更新 </a>
	<input type="hidden" id="link_update_value" value='<%=response.encodeURL(request.getContextPath() + "/admin/svnupdate.a?projectId=")%>' />
	<!-- 
	<a href='<%=response.encodeURL(request.getContextPath() + "/admin/showgeneratelist.a?projectId=")%>' id="link_generate" onclick="submit_generate()">生成 </a>
	<input type="hidden" id="link_generate_value" value='<%=response.encodeURL(request.getContextPath() + "/admin/showgeneratelist.a?projectId=")%>' />
	-->
	<a href='<%=response.encodeURL(request.getContextPath() + "/admin/showgeneratecsvlist.a?projectId=")%>' id="link_generate_csv" onclick="submit_generatecsv()">生成CSV </a>
	<input type="hidden" id="link_generate_csv_value" value='<%=response.encodeURL(request.getContextPath() + "/admin/showgeneratecsvlist.a?projectId=")%>' />
	 
	
	<a href='<%=response.encodeURL(request.getContextPath() + "/admin/tool/svn/commitgenfiletosvn.jsp?projectId=")%>' id="link_commit" onclick="submit_commit()">提交) </a>
	<input type="hidden" id="link_commit_value" value='<%=response.encodeURL(request.getContextPath() + "/admin/tool/svn/commitgenfiletosvn.jsp?projectId=")%>' />
	
	<a href='<%=response.encodeURL(request.getContextPath() + "/admin/autoupdate.a?projectId=")%>' id="link_autocommit" onclick="submit_autocommit()">一键提交</a>
	<input type="hidden" id="link_autocommit_value" value='<%=response.encodeURL(request.getContextPath() + "/admin/autoupdate.a?projectId=")%>' />
	
	<a href='<%=response.encodeURL(request.getContextPath() + "/admin/tool/svn/uploadjsontoftp.jsp?projectId=")%>' id="link_uploadftp" onclick="submit_uploadftp()">上传Server资源 </a>
	<input type="hidden" id="link_uploadftp_value" value='<%=response.encodeURL(request.getContextPath() + "/admin/tool/svn/uploadjsontoftp.jsp?projectId=")%>' />
	
	<!-- <a href="<%=response.encodeURL(request.getContextPath()+"/admin/update_list.a")%>" >更新文件</a> -->
	
	<td>................</td>
	
	<a href='<%=response.encodeURL(request.getContextPath() + "/admin/tool/svn/uploadclientjsontoftp.jsp?projectId=")%>' id="link_uploadclientftp" onclick="submit_uploadclientftp()">上传客户端资源 </a>
	<input type="hidden" id="link_uploadclientftp_value" value='<%=response.encodeURL(request.getContextPath() + "/admin/tool/svn/uploadclientjsontoftp.jsp?projectId=")%>' />
	
	<a href="<%=response.encodeURL(request.getContextPath()+"/admin/rollback_list.a")%>" >回滚服务器</a>
	<a href="<%=response.encodeURL(request.getContextPath()+"/admin/gameserver/updateres/rollbackClient.jsp")%>" >回滚客户端</a>
	
	<a href='<%=response.encodeURL(request.getContextPath() + "/admin/tool/svn/incrementalResNUpload.jsp?projectId=")%>' id="link_incrementalres" onclick="submit_incrementalres()">上传客户端差量资源包 </a>
	<input type="hidden" id="link_incrementalres_value" value='<%=response.encodeURL(request.getContextPath() + "/admin/tool/svn/incrementalResNUpload.jsp?projectId=")%>' />
	
	<a href='<%=response.encodeURL(request.getContextPath() + "/admin/tool/svn/clearClientCache.jsp?projectId=")%>' id="link_clearclientcache" onclick="submit_clearclientcache()">清除客户端缓存</a>
	<input type="hidden" id="link_clearclientcache_value" value='<%=response.encodeURL(request.getContextPath() + "/admin/tool/svn/clearClientCache.jsp?projectId=")%>' />
	<!-- <a href="<%=response.encodeURL(request.getContextPath()+"/admin/tool/syncRes.jsp")%>" >内外网资源同步</a> -->
</body>
</html>