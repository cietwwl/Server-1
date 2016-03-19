<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/admin/common/header.jsp" %>
</head>
	<%
		String svnId = (String)request.getParameter("projectId");
	%>
<body>
	<h1 align="center">提交SVN</h1>
	<div align="center">
	<form method="POST" action="<%=response.encodeURL(request.getContextPath() + "/admin/commitjson.a")%>" enctype="multipart/form-data">
		<input style="width:500px" type="text" name="commitMessage" />  注释
		<input type="hidden" name="pId" value="<%=svnId %>" />
		<input type="submit"  value="上传" /><br/>
	</form>
	</div>
</body>
</html>