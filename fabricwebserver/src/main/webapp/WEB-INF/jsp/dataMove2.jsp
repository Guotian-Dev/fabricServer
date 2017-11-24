<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>数据流动</title>
<link rel ="stylesheet" href="../bootstrap/css/bootstrap.css">
<link rel ="stylesheet" href="../bootstrap/css/bootstrap-responsive.css">
<script type="text/javascript" src="../js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="../bootstrap/js/bootstrap.js"></script>
<script type="text/javascript">
	$(function() {
		$("#zz").click(function() {
			var fkf = $("#fkf").val();
			var skf = $("#skf").val();
			var je = $("#je").val();
			console.log(fkf);
			console.log(skf);
			console.log(je);
			$.ajax({    
				url:'/fabricwebserver/chaincode/zz',      
		        type : 'POST',    
		        async:false,
		        dataType: "json",
		        data:{
					skf:skf,
					fkf:fkf,
					je:je
				},
		       	success: function (data){ 
		       		alert(JSON.stringify(data));
		       		console.log(data);
		        },
		        error: function(data) {
		        	alert("转账失败")
		        }
				
			 });
		})
	})
</script>
</head>
<body>
	<div class="page-header" align="center">
		<h3>数据流动</h3>
	</div>
	
	付款方<input id="fkf" type="text" name="fkf"/><br>
	收款方<input id="skf" type="text" name="skf"/><br>
	转账金额<input id="je" type="text" name="je"/><br>
	
	<input id="zz" type="button" value="转账"/>
</body>
</html>