<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/admin/common/header.jsp" %>
</head>
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
                'uploader'       : '<%=response.encodeURL(request.getContextPath() + "/admin/handlerCommitClass.a")%>',
                'fileObjName'    : 'filelist',
                //'formData'       :  {'selectSvnName':"1"},
                'auto'           : false,    
                'fileTypeExts' 	 : '*.zip', //控制可上传文件的扩展名，启用本项时需同时声明fileDesc  
                'buttonText'     : '选择文件',
                onUploadStart    : function(file){
                	//var value = document.getElementById("pId2").value;
                	
                	//$("#uploadify").uploadify('settings','formData', {'selectSvnName' : value});
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
    	<h3>请选择上传更新的文件</h3>
        <input type="file" name="uploadify" id="uploadify" />
        <p>  
        <a href="javascript:$('#uploadify').uploadify('upload','*')">开始上传</a>&nbsp; 
        </p>  
        </div>
</body>
</html>