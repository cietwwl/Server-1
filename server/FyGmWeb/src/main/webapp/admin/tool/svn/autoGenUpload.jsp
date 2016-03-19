<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/admin/common/header.jsp" %>
</head>
	<%
		String svnId = (String)request.getParameter("projectId");
	%>
<body>
	<div align="center">
	<tr>
		<form action='<%=response.encodeURL(request.getContextPath()+"/admin/processAutoGenUpload.a") %>' id=form method="post">
			<input style="width:500px" type="text" name="commitMessage" />  注释<br/>
			<input type="submit" name="onekeygenerate" value="一键提交"/><br/>
			<input type="hidden" name="pId" value="<%=svnId %>" />
		</form>
	</tr>
	
	</div>
	<table class="comtable" align="center" border="1" cellpadding="0" style="text-align: center;">
		<tr>
			<th width="500px">文件名</th>
			<th width="500px">操作</th>
		</tr>
		<s:iterator value="excellist" status="st">
			<tr>
				<td><s:property value="name"/>
				</td>
				<td><s:property value="statusDes"/>
				</td>
			</tr>
		</s:iterator>
	</table>
</body>
</html>