<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/admin/common/header.jsp" %>
</head>
<body>
<div align="center">
	<form action='<%=response.encodeURL(request.getContextPath() + "/admin/syncclass.a")%>' enctype="multipart/form-data" id="form" method="post">
		<p>点击更新进行内外网服务器资源同步</p>
		<input type="submit" class="btn btn-primary" style="vertical-align:top;" data-loading-text="Loading..." value="更新" />
</form>
</div>
</body>
</html>