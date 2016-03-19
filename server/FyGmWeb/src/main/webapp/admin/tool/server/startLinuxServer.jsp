<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/admin/common/header.jsp" %>
</head>
<body>
	<div align="center">
		<form
			action='<%=response.encodeURL(request.getContextPath() + "/admin/startLinuxServer.a")%>' enctype="multipart/form-data" id="form" method="post">
			<div style="height: 30px; float: center; margin-right: 8px;">
				<p>请选择对应的服务器启动</p>
				<label>服务器组</label> 
				<select id="server" name="serverid"
					style="width: 100px">
					<s:iterator value="serverlist" status="st">
						<option value=<s:property value="id" />
							selected="selected"><s:property value="serverName" /></option>
					</s:iterator>
				</select> 
				<input type="submit" class="btn btn-primary" style="vertical-align:top;" data-loading-text="Loading..." value="启动" />
			</div>
		</form>
	</div>
</body>
</html>