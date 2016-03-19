<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/admin/common/header.jsp" %>
</head>
	<%
		String svnId = (String)request.getParameter("projectId");
	%>
<body>
	<h1 align="center">清除客户端缓存</h1>
	<div align="center">
	<form method="POST" action="<%=response.encodeURL(request.getContextPath() + "/admin/clearclientcache.a")%>" enctype="multipart/form-data">
		<input type="submit"  value="清除缓存"/><br/>
		<input type="hidden" name="pId" value="<%=svnId %>" />
	</form>
	</div>
</body>
</html>