<!-- 统计等级分布信息 -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/admin/common/header.jsp" %>
<%
	String svnId = (String)request.getParameter("projectId");
%>
</head>
<body>
<div id="header" class="well well-large">
	<div stype="height: 65px;float:left;margin-right:8px;">
			<label>server List</label>
			<select id="serverid" name="serverid" style="width: 100px">
			<s:iterator value="serverList" status="st">
			<option value=<s:property value="id" /> selected="selected"><s:property
					value="serverName" /></option>
			</s:iterator>
			</select>
	</div>
	<div>
		<input type="hidden" id="pId2" name="pId2" value="<%=svnId %>" />
		<input type="submit" id="query" name="query" class="btn btn-primary" style="vertical-align:top;" data-loading-text="Loading..." value="查询" onclick="query()"/>
	</div>
</div>

<table width="100%" cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered table-hover" id="table">
	<tbody id="_table">
	</tbody>		
</table>

<table id="example" class="display" width="100%"></table>

<div id="showArea"> 
	<div id="tableArea" style="margin-bottom: 30px;display: none;" class="main_round_40">
		<a href="#" class="close" style="position: relative;left: 25px;top: -30px;z-index: 999;">&times;</a>
		<div id="showTable">
		</div>
	</div>
	<!-- 
	<div id="chartArea" style="display:none;" class="main_round_30">
		<a href="#" class="close" style="position:relative;left:-3px;top:-3px;z-index:999;">&times;</a>
		<div id="showChart"></div>
	</div>
	-->
</div>
<%@include file="/admin/common/footer.jsp" %>
<script type="text/javascript" src="<%=response.encodeURL(request.getContextPath()+"/dwr/interface/GameServerStatisticsAction.js")%>"></script>
<script type="text/javascript">
function query(){
	var pid=document.getElementById("pId2").value;
	var serverId = document.getElementById("serverid").value;
	GameServerStatisticsAction.queryGameServerLevelSpread(serverId, pid, callBack);
}

var _row;
var _cell;

function callBack(data){
	$("table tr").html("");
	var dataTable = data.table;
	$("table tr").html("");
	_row = document.createElement("tr");
	document.getElementById("_table").appendChild(_row);
	_cell = document.createElement("th");
	_cell.innerText="等级(等级分布)";
	_row.appendChild(_cell);
	_cell = document.createElement("th");
	_cell.innerText="人数";
	_row.appendChild(_cell);
	var dataTable = data.table;
	for(var i=0;i<dataTable.length;i++){
		var sublist = dataTable[i];
		_row = document.createElement("tr");
		document.getElementById("_table").appendChild(_row);
		for(var j=0;j<sublist.length;j++){
			_cell = document.createElement("td");
			_cell.innerText=sublist[j];
			_row.appendChild(_cell);
		}
	}
}
</script>
</body>
</html>