<!-- 统计留存玩家信息 -->
<%@page import="java.util.Random"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/admin/common/header.jsp" %>
<%
	String svnId = (String)request.getParameter("projectId");
%>
</head>
<%
	SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
	Date now = new Date();
	String today = sdf.format(now);
	Date lastDayTime = new Date(now.getTime() - (1000 * 60 * 60* 24));
	String yesterday = sdf.format(lastDayTime);
%>
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
		<div class="control-group pull-left">
			<lable class="control-lable">开始时间</lable>
			<div class="controls input-append date form_datetime" data_date="" data-date-format="yyyy-mm-dd">
				<input size="16" type="text" name="beginDate" id="beginDate" value="<%=today%>">
				<span class="add-on"><i class="icon-remove"></i></span>
				<span class="add-on"><i class="icon-th"></i></span> 
			</div>
		</div>
		<div class="control-group">
			<lable class="control-lable">结束时间</lable>
			<div class="controls input-append date form_datetime" data_date="" data-date-format="yyyy-mm-dd">
				<input size="16" type="text" name="endDate" id="endDate" value="<%=today%>">
				<span class="add-on"><i class="icon-remove"></i></span>
				<span class="add-on"><i class="icon-th"></i></span> 
			</div>
		</div>
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

<div id="showArea">
	<div id="tableArea" style="margin-botton:30px;display:none;" class="main_round_40">
		<a href="#" class="close" style="position:relative;left:25px;top:-30px;z-index:999;">&times;</a>
		<div id="showTable"></div>
	</div>
	<div id="chartArea" style="display:none;" class="main_round_30">
		<a href="#" class="close" style="position:relative;left:-3px;top:-3px;z-index:999;">&times;</a>
		<div id="showChart"></div>
	</div>
</div>
<%@include file="/admin/common/footer.jsp" %>
<script type="text/javascript" src="<%=response.encodeURL(request.getContextPath()+"/dwr/interface/GameServerStatisticsAction.js")%>"></script>
<script type="text/javascript">
$(".form_datetime").datetimepicker({
	startView:2,
	minView:2,
	autoclose:true
});
function query(){
	
	var timeReg=/^(\d{4})-(\d{2})-(\d{2})$/;
	
	var begin=document.getElementById("beginDate").value;
	var end=document.getElementById("endDate").value;
	var pid=document.getElementById("pId2").value;
	var serverId = document.getElementById("serverid").value;
	
	
	if(begin == "" || end=="" || !timeReg.test(begin) || !timeReg.test(end)){
		alert('请输入格式为yyyy-MM-dd格式的日期');
		return;
	}
	GameServerStatisticsAction.queryGameServerRetainedData(begin, end, serverId, pid, callBack);
}

var _row;
var _cell;

function callBack(data){
	if(data.result != "success"){
		alert(data.result);
		return;
	}
	<!--
	if(data == null){
		return;
	}
	var dataChart= data.chart;
	var dataTable = data.table;
	var renderTo = "showChart";
	var title='注册人数统计';
	var subTitle='';
	
	$('#showTable').html('<table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered table-hover" id="tableInfo"></table>');
	
	$('#tableInfo').dataTable({
		"sDom":'T<"clear">lfrtip',
		"oTableTools":{
			"sSwfPath": "../../js/dataTable/swf/copy_csv_xls_pdf.swf",
			"aButton": ["copy",{
				"sExtends": "xls",
				"sFileName": "*.xls"
			}]
		},
		"aaData": dataTable,
		"aoColumns": [
		   {"sTitle": "时间", "sClass": "center"},
		   {"sTitle": "人数", "sClass": "center"},
		 ]
	});
	-->
	
	var dataTable = data.table;
	$("table tr").html("");
	_row = document.createElement("tr");
	document.getElementById("_table").appendChild(_row);
	_cell = document.createElement("th");
	_cell.innerText="时间(留存)";
	_row.appendChild(_cell);
	_cell = document.createElement("th");
	_cell.innerText="创建人数";
	_row.appendChild(_cell);
	
	_cell = document.createElement("th");
	_cell.innerText="D1";
	_row.appendChild(_cell);
	_cell = document.createElement("th");
	_cell.innerText="D2";
	_row.appendChild(_cell);
	_cell = document.createElement("th");
	_cell.innerText="D3";
	_row.appendChild(_cell);
	_cell = document.createElement("th");
	_cell.innerText="D4";
	_row.appendChild(_cell);
	_cell = document.createElement("th");
	_cell.innerText="D5";
	_row.appendChild(_cell);
	_cell = document.createElement("th");
	_cell.innerText="D6";
	_row.appendChild(_cell);
	_cell = document.createElement("th");
	_cell.innerText="D7";
	_row.appendChild(_cell);
	_cell = document.createElement("th");
	_cell.innerText="D15";
	_row.appendChild(_cell);
	_cell = document.createElement("th");
	_cell.innerText="D30";
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