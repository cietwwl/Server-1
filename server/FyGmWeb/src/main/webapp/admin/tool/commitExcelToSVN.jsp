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
<link href='<%=response.encodeURL(request.getContextPath()+"/css/style.css")%>' rel="stylesheet" type="text/css" />
<base href="<%=basePath%>">  
        <title>Upload</title>  
        <link href="<%=response.encodeURL(request.getContextPath()+"/css/uploadify.css")%>" rel="stylesheet" type="text/css" />  
        <script type="text/javascript" src='<%=response.encodeURL(request.getContextPath() + "/js/jquery-1.7.2.min.js")%>'></script>  
        <script type="text/javascript" src='<%=response.encodeURL(request.getContextPath() + "/js/swfobject.js")%>'></script>  
        <script type="text/javascript" src='<%=response.encodeURL(request.getContextPath() + "/js/uploadify/jquery.uploadify.min.js")%>'></script> 
		<script type="text/javascript">  
        $(document).ready(function() {  
            $("#uploadify").uploadify({  
            	'swf'            : '<%=path%>/uploadify.swf',
                'uploader'       : '<%=response.encodeURL(request.getContextPath() + "/admin/handlerCommitExcel.a")%>',
                'fileObjName'    : 'filelist',
                'formData'       :  {'selectSvnName':"1"},
                'auto'           : false,    
                'fileTypeExts' 	 : '*.xlsx;*.xls', //控制可上传文件的扩展名，启用本项时需同时声明fileDesc  
                'buttonText'     : '选择文件',
                onUploadStart    : function(file){
                	var index = document.getElementById("server").selectedIndex;
                	var value = document.getElementById("server").options[index].value;
                	$("#uploadify").uploadify('settings','formData', {'selectSvnName' : value});
                },
                onUploadSuccess   : function(file, data , response){
                	alert(data);
                	//window.location.href = data ;
                }
            });  
        });  
</script>
</head>
<body>
	<div align="center">
    	<h3>请选择上传的Excel</h3>
		<label>SVN服务器组</label>
		<select id="server" name="svnName" style="width:200px">
			<s:iterator value="svnList" status="st">
			<option value=<s:property value="svnName" /> selected="selected"><s:property value="svnName" /></option>
			</s:iterator>
		</select>
        <input type="file" name="uploadify" id="uploadify" />  
        <p>  
        <a href="javascript:$('#uploadify').uploadify('upload','*')">开始上传</a>&nbsp; 
        </p>  
        </div>
</body>
</html>