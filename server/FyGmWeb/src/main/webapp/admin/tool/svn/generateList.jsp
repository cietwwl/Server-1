<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.dx.gods.service.tools.SVNWorkCopy"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags" %> 
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<%  
String path = request.getContextPath();  
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";  
%>
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
</head>
<body>
	<%
		String svnId = request.getAttribute("projectId").toString();
	%>
	 
	<script type="text/javascript">
		<!--
		function chkclient(){
			var test = document.getElementsByName("belongclient");
			for(var i=1;i<=test.length; i+=1){
				if(test[i-1].checked){
					alert(document.getElementsByName("belongclient").value);
					//document.getElementsByName("belongclient").value = true;
				}
			}
			
		}
		function checkserver(){
			var test = document.getElementsByName("belongserver");
			for(var i=1;i<=test.length; i+=1){
				if(test[i-1].checked){
					//alert(document.getElementsByName("belongserver").value);
					document.getElementsByName("belongserver").value = "true";
					alert(document.getElementsByName("belongserver").value);
				}else{
					document.getElementsByName("belongserver").value = "false";
					alert(document.getElementsByName("belongserver").value);
				}
			}
		}
		
		function generate(){
			var testc = document.getElementsByName("belongclient");
			for(var i=1;i<=testc.length; i+=1){
				if(testc[i-1].checked){
					document.getElementsByName("belongclient").value = "true";
				}else{
					document.getElementsByName("belongclient").value = "false";
				}
			}
			var tests = document.getElementsByName("belongserver");
			for(var i=1;i<=tests.length; i+=1){
				if(tests[i-1].checked){
					document.getElementsByName("belongserver").value = "true";
				}else{
					document.getElementsByName("belongserver").value = "false";
				}
			}
		}
		-->
	</script>
	<center>
	<tr>
		<form action='<%=response.encodeURL(request.getContextPath()+"/admin/generatejson.a") %>' id=form method="post">
			<input type="submit" name="onekeygenerate" value="一键生成"/>
			<input type="hidden" name="pId1" value="<%=svnId %>" />
		</form>
	</tr>
	
	</center>
	<table class="comtable" border="1" align="center" cellpadding="0" style="text-align:center;">
		<tr>
			<th width="500px">文件名</th>
			
			 <!-- 
			<th width="200px">客户端</th>
			<th width="200px">服务器</th>
			  -->
			<th width="500px">操作</th>
		</tr>
		<s:iterator value="genList" id="temp" status="st">
			<tr>
				<form action='<%=response.encodeURL(request.getContextPath()+"/admin/generatesinglejson.a") %>' id=form method="post">
					<td><s:property value="name"/></td>
					<!-- 
					<td>
						<input type="checkbox" name="belongclient" id="belongclientid" value="${temp.belongClient}" <s:if test="#temp.belongClient==true">checked="checked"</s:if> />客户端&nbsp;
					</td>
					<td>
						<input type="checkbox" name="belongserver" id="belongserverid" value="${temp.belongServer}" <s:if test="#temp.belongServer==true">checked="checked"</s:if> />服务器&nbsp;
					</td>
					 -->
					<td>
						<input type="submit" name="generate"  value="生成" onclick="generate()"/>
						<input type="hidden" name="pId2" value="<%=svnId %>" />
						<input type="hidden" name="path" id="pathid" value='<s:property value="path"/>' />
					</td>
				</form>
			</tr>
		</s:iterator>
	</table>
	
</body>
</html>