<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/admin/common/header.jsp" %>
</head>
	<%
		String svnId = (String)request.getParameter("projectId");
	%>
</head>
<body>
	<div align="center">
	<tr>
		<form action='<%=response.encodeURL(request.getContextPath()+"/admin/generatecsv.a") %>' id=form method="post">
			<input type="submit" name="onekeygenerate" value="一键生成"/>
			<input type="hidden" name="pId1" value="<%=svnId %>" />
		</form>
	</tr>
	</div>
	<table class="comtable" border="1" align="center" cellpadding="0" style="text-align:center;">
		<tr>
			<th width="500px">文件名</th>
			<!-- 
			<th width="200px">客户端</th>
			<th width="200px">服务器</th>
			 -->
			<th width="500px">操作</th>
		</tr>
		<s:iterator value="genList" id="temp" status="st">
			<tr>
				<form action='<%=response.encodeURL(request.getContextPath()+"/admin/generatesinglecsv.a") %>' id=form method="post">
					<td><s:property value="name"/></td>
					<td>
						<input type="submit" name="generate"  value="生成"/>
						<input type="hidden" name="pId2" value="<%=svnId %>" />
						<input type="hidden" name="path" id="pathid" value='<s:property value="path"/>' />
					</td>
				</form>
			</tr>
		</s:iterator>
	</table>
	
</body>
</html>