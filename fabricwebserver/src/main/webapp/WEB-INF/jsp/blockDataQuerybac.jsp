<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>联盟链区块浏览器</title>
<link rel ="stylesheet" href="../bootstrap/css/bootstrap.css">
<link rel ="stylesheet" href="../bootstrap/css/bootstrap-responsive.css">
<script type="text/javascript" src="../js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="../bootstrap/js/bootstrap.js"></script>
<script type="text/javascript">
	$(function() {
		$("#query").click(function() {
			/* alert($("#jd option:selected").text()) */
			var peerName = $("#jd option:selected").text();
			var key = $("#key").val();
			
			$.ajax({    
				url:'/fabricwebserver/chaincode/queryByKey',
				type:'POST',
				async:false,
				dataType:"json",
				data:{
					key:key,
					peerName:peerName
				},
				success: function (data){ 
					if(data != null) {
						$("#value").val(data);
					}
				},
				error: function (data) {
					alert("块中没有输入的key");
				}
			});
			
		})
		
		$("#add").click(function() {
			window.location.href = "/fabricwebserver/chaincode/add";
		})
	})
</script>
</head>
<body>
	<div class="page-header" align="center">
		<h3>联盟链区块浏览器</h3>
	</div>
	<div>区块数据查询</div>
	
	<div>
		选择区块节点
		<select id="jd">
		  <option value ="peer0.org1.example.com">peer0.org1.example.com</option>
		  <option value ="peer1.org1.example.com">peer1.org1.example.com</option>
		  <option value="peer2.org1.example.com">peer2.org1.example.com</option>
		  <option value="peer3.org1.example.com">peer3.org1.example.com</option>
		</select>
	</div>
	<div>
		key值:<input id="key" type="text" name="key">
		value值:<input id="value" type="text" name="value">
	</div>
	
	<div>
		<input id="query" type="button" value="查询">
		<input id="add" type="button" value="添加">
	</div>
</body>
</html>