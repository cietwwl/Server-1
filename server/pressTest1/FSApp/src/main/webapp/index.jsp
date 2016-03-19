<%@taglib prefix="s" uri="/struts-tags" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>  
<html>  
<head>  
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">  
    <title>debug</title>  
</head>  
<body>  
	<h2>Debug</h2>   
 
	<s:form action="debug.action" method="post" validate="false" >  
	
	<table style="background-color: yellow; color:blue;">
		<tbody>
			<tr>
				<td>发送内容：</td>
				<td></td>
			</tr>
			<tr>
				
				<td><s:textfield name="host" label="host"></s:textfield></td>
			</tr>
			
			<tr>
			
				<td>Post Content:<s:textarea name="content" style="width:600px;height:300px"></s:textarea></td>
			</tr>
			<tr>
				<td><s:submit value="发送请求"></s:submit>  </td>
			</tr>
			</tbody>
	
	</table>
	
	<table>
		<tbody>
			<tr>
				<td>返回结果：</td>
			</tr>
			<tr>
				<td>response:<s:textarea name="responseStr" readonly="true" style="width:600px;height:300px"></s:textarea></td>
			</tr>
			<tr>
				<td>Serialized Content:<s:textarea name="SerializedContentStr" readonly="true"  style="width:600px;height:300px"></s:textarea></td>
			</tr>
		
		</tbody>
	
	</table>
	
		
		 
	</s:form>  
	
</body>  
</html>  