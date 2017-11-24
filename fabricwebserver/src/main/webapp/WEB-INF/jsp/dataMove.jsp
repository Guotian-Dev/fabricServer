<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>转账</title>
    <link rel="shortcut icon" href="../css/fav.png">
    <link rel="stylesheet" href="../css/style1.css">
	<script type="text/javascript" src="../js/jquery-1.11.1.min.js"></script>
</head>
<script type="text/javascript">
	$(function() {
		$("#zz").click(function(e) {
			e.preventDefault();
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
		        	alert("转账失败，原因：收款方/付款方在块中不存在！")
		        }
				
			 });
		})
	})
</script>
<body>
<div class="pay-tittle">
    <h1>价值流转展示</h1>
    <a href="/fabricwebserver/chaincode/brow" class="btn-back">返回首页</a>
</div>
<form action="" class="smart-green">
    <h1>请填写转账信息
        <span>Please fill all the texts in the fields.</span>
    </h1>
    <label>
        <span>付款方 :</span>
        <input id=fkf type="text" name="fromAddr" placeholder="Your Address" />
    </label>
    <label>
        <span>收款方 :</span>
        <input id="skf" type="text" name="toAddr" placeholder="Destination Address" />
    </label>
    <label>
        <span>转账金额 :</span>
        <input id="je" type="number" name="value" placeholder="0.00" />
    </label>
    <!--<label>-->
        <!--<span>Subject :</span><select name="selection">-->
        <!--<option value="Job Inquiry">Job Inquiry</option>-->
        <!--<option value="General Question">General Question</option>-->
    <!--</select>-->
    <!--</label>-->
    <label>
        <span>&nbsp;</span>
        <input id='zz' type="button" class="button" value="转账" />
    </label>
</form>
</body>
</html>