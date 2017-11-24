<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>查询页面</title>
<link rel ="stylesheet" href="../bootstrap/css/bootstrap.css">
<link rel ="stylesheet" href="../bootstrap/css/bootstrap-responsive.css">
<script type="text/javascript" src="../js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="../bootstrap/js/bootstrap.js"></script>

<script type="text/javascript">

	$(function() {
		
		$("#querydo").click(function() {
			$.ajax({    
				url:'/fabricwebserver/chaincode/queryByKey',
				type:'POST',
				async:false,
				dataType:"json",
				data:{
					key:$("#queryKey").val()
				},
				success: function (data){ 
					$("#value").val(data);
				},
				error: function (data) {
					alert("块中没有对应的key");
				}
			});
		})
		
		$("#add").click(function() {
			window.location.href = "http://localhost/fabricwebserver/chaincode/add";
		})
	});



</script>
</head>
<body>
	<div class="page-header" align="center">
		<h3>查询页面</h3>
	</div>
	
	<div class="container">
		key:<input id="queryKey" type="text" class="form-control" name="key"/><br>
		value:<input id="value" type="text" class="form-control" name="value" /><br>
		<button id="querydo" class="btn btn-default">查询</button>
		<button id="add" class="btn btn-default">新增</button>
	</div>
	
</body>
</html>