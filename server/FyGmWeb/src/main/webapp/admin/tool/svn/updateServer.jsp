<%@include file="/admin/common/header.jsp" %>
</head>
	<%
		String svnId = request.getAttribute("projectId").toString();
	%>
<body>
	<center>
		<form action='<%=response.encodeURL(request.getContextPath() + "/admin/_updateres.a")%>' enctype="multipart/form-data" id="form" method="post">
			<div style="height: 30px; float: center; margin-right: 8px;">
				<p>请选择对应的服务器更新</p>
				<label>服务器组</label> 
				<select id="server" name="serverName"
					style="width: 100px">
					<s:iterator value="serverlist" status="st">
						<option value=<s:property value="serverName" /> selected="selected"><s:property value="serverName" /></option>
					</s:iterator>
				</select> 
				<input type="submit" value="更新" />
			</div>
		</form>
	</center>
</body>
</html>