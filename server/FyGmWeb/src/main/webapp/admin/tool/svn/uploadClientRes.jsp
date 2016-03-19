<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/admin/common/header.jsp" %>
</head>
	<%
		String svnId = (String)request.getParameter("projectId");
	%>
</head>
<body>
	<h1 align="center">上传客户端资源更新文件</h1>
	<div align="center">
	<form method="POST" action="<%=response.encodeURL(request.getContextPath() + "/admin/svnuploadjson.a")%>" enctype="multipart/form-data">
		<input type="submit"  value="上传"/><br/>
		<input type="hidden" name="pId" value="<%=svnId %>" />
	</form>
	</div>
</body>
</html>