<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/admin/common/header.jsp" %>
</head>
<body>
	<form method="POST" action="<%=response.encodeURL(request.getContextPath() + "/admin/processCompileAndUpload.a")%>" enctype="multipart/form-data">
		<label>svn server</label>
		<select id="server" name="svnName" style="width: 100px">
			<s:iterator value="list" status="st">
				<option value=<s:property value="versionId" /> selected="selected"><s:property value="versionName" /></option>
			</s:iterator>
		</select>
		<input type="submit" class="btn btn-primary" style="vertical-align:top;" data-loading-text="Loading..." value="上传"/><br/>
	</form>
</body>
</html>