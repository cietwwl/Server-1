<html>
<body>
<h2>Hello World!</h2>

<table>
	<tr>
		<td>id</td>
		<td><input id = "id" value="100"/></td>
	</tr>
	<tr>
		<td>data</td>
		<td><input id = "data" value="100"/></td>
	</tr>
	<tr>
		<td>client</td>
		<td><input id = "client" value="100"/></td>
	</tr>
	<tr>
		<td>encrypt</td>
		<td><input id = "encrypt" value="100"/></td>
	</tr>
	<tr>
		<td><input type="button" id="GetBtn" value="GetBtn"/></td>
		<td><input type="button" id="PostBtn" value="PostBtn"/></td>
	</tr>
</table>

<script type="text/javascript">
	$(document).ready(function(){
		$("#GetBtn").click(function(){
			getBtnAction();
		});
		
		$("#PostBtn").click(function(){
			postBtnAction();
		});
	});
	var url = "9game";
	function getBtnAction() {
		var getUrl = "/zonelist?id=" + $('#id').val()+"&data="+$('#data').val()+"&client="+$('#client').val()
				+"&encrypt="+$('#encrypt').val();
		var requestUrl = url + getUrl;
		alert(requestUrl);
		$.get(requestUrl, function(data) {
			alert(data)
		});
	}
	
	function postBtnAction() {
		var requestUrl = url +"/roleInfo";
		var dateStr = '{"id":'+$('#id').val() +',"client":'+$('#client').val()
			+',"data":'+$('#data').val()+',"encrypt":'+$('#encrypt').val()+'}';
			alert(data);
			$.ajax({
				type:'post',
				contentType:'application/json',
				url:requestUrl,
				dataType:'json',
				data:dateStr,
				success: function(data) {
					alert(data);
				},
				error:function(){
					alert("error.....");
				}
			
			});

	}

</script>
</body>
</html>
