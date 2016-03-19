<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="java.net.URL"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib prefix="s" uri="/struts-tags" %> 
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="Cache-Control" content="no-cache" />
		<title>查用户各分区信息</title>
		<link href='<%=response.encodeURL(request.getContextPath()+"/admin/css/main.css")%>' rel="stylesheet" type="text/css" />
	</head>
	<body>
<%-- 	
// 			String ip = request.getRemoteAddr();
// 			if(ip.contains("192.168")||ip.contains("127.0.0.1")){
<%-- 		%><span style="float:right; margin-right:30px;"><a href='<%=response.encodeUrl(request.getContextPath() + "/admin/getUrl.a?listAll=1")%>'>Click Me!</a> (<%=ip %>)<br/></span><%} %><br/> --%>
		<center>
			<form action='<%=response.encodeUrl(request.getContextPath() + "/admin/getUserDetail.a")%>' id="form" method="post">			
				<table align="center" width="80%" style="line-height: 35px;">
					<tr><th height="80"><h2>查用户各分区信息</h2></th></tr>		
					<tr>
						<td align="center"  style="padding-right: 20%">
							用户UserId或者数字ID: <br/>
							<input type="text" name="userId" value='<s:property value="userId"/>' style="width: 60%" />
						</td>
					</tr>
					<tr>
						<td align="center" style="padding-right: 20%;">
							&nbsp;
							<input type="submit" value=" &nbsp;查 &nbsp; &nbsp;询 &nbsp;" />
							&nbsp;&nbsp;
						</td>
					</tr>
				</table>
			</form>
		</center>
		<s:if test="userIdList!=null && userIdList.size()>0">
		<table id="userRes" cellpadding="0" cellspacing="0" align="left">
		
			<tr>
				<th width="39">
					序号   
				</th>
				<th width="39">
					区号                
				</th>
				<th width="65">
					区名
				</th>
					<th width="300">
					用户UID
				</th>
					<th width="85">
					用户昵称
				</th>
				<th width="44">
					等级                
				</th>
				<th width="44">
					职业              
				</th>
				<th width="44">
					性别               
				</th>
				<th width="125">
					账&nbsp;&nbsp;号              
				</th>
				<th width="69">
					VIP等级             
				</th>
				</tr>
				
			<s:iterator value="userIdList" status="st" id="v" >
				<style>td {
	text-align: center;
}
</style>

				<tr>
					<td>
						<s:property value="#st.index+1"/>
					</td>
					<td>
						<s:property value="zoneId"/>
					</td>
					<td>
						<s:property value="zoneName" escape="false"/>
					</td>
						<td>
						<s:property value="userId"/>
					</td>
						<td>
						<s:property value="userNick" escape="false"/>
				        </td>
				    <td>
						<s:property value="level"/>
					</td>
					<td>
						<s:property value="career"/>
					</td>
					<td>
						<s:property value="sex"/>
					</td>
						<td>
						<s:property value="account"/>
					</td>
					<td>
						<s:property value="vipLevel"/>
					</td>
				</tr>
			</s:iterator>
			</table>
		</s:if>
		<s:elseif test="userIdList!=null && userIdList.size()==0">
		 <center><b>结果:</b> </center><br />
		查无结果，此ID有误！！！
		</s:elseif>
		</body>
</html>