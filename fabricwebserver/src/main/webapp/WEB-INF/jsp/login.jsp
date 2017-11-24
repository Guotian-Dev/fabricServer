<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="common.jsp"%>
    <link rel="shortcut icon" href="../css/fav.png">
    <link rel="stylesheet" href="../css/style1.css">
<script src="../js/md5.js" type="text/javascript"></script>
<script src="../js/sha256.js" type="text/javascript"></script>
<script src="../js/base64.js" type="text/javascript"></script>

<!-- <script src="../js/login.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" href="../css/login.css"> -->
<style type="text/css">
	
	#login{
		width: 80px;
	    display: block;
	    margin: 15px auto;
	}
	
	#loginForm label{
		display: inline-block;
		width : 60px;
	}
	#loginForm input{
		width: 250px;
	}
	#verifyCodeImage {
		margin-top: 2px;
	    display: inline-block;
	    height: 30px;
	    float: right;
	    margin-right: 78px;
	}
</style>
<script type="text/javascript">
	
  	var errMsg = "${errMsg}";
	if(errMsg != null && errMsg != "") {
		alert(errMsg);
	}
	//表单的非空检查
	function checkForm() {
		var userName = $("#userName").val();
		if(userName == "") {
			alert("请输入用户名");
			return false;
		}
		var password = $("#password").val();
		if(password == "") {
			alert("请输入密码");
			return false;
		}
		var verifyCode = $("#verifyCode").val();
		if(verifyCode == "") {
			alert("请输入验证码");
			return false;
		}
		return true;
	}
	
	$(function() {
		
		$.ajax({    
			url:'/fabricwebserver/user/getRandom',      
		       type : 'POST',    
		       async:false,
		       dataType: "json",
		       success: function (data){
		    	   console.log(data);
		    	   $("#randomData").val(data);
		       }    
		 });
		
		
		$("#login").click(function() {
			
			if(checkForm()) {
				var randomData = $("#randomData").val();
				
				//对密码进行加密
				var enPassword = hex_sha256($("#password").val()) + hex_sha256(randomData);
				$("#password").val(enPassword);
				
				var verifyCode = $("#verifyCode").val();
				//对用户名进行base64
				var userName = new Base64().encode($("#userName").val());
				$("#userName").val(userName);
				
				$("#loginForm").submit();
			}
		})
	})
	
	
	//验证码逻辑
	function reloadVerifyCode() {
		var imgSrc = $("#verifyCodeImage");
		var src = imgSrc.attr("src");
		imgSrc.attr("src", chgUrl(src));
	}
	function chgUrl(url) {
		var timestamp = (new Date()).valueOf();
		newurl = url + "?timestamp=" + timestamp;
		return newurl;
	}
</script>
<title>登录页面</title>
</head>
<body>
	
	<div class="pay-tittle">
	    <h1>用户登录</h1>
	    
	</div>
	
	<form action="/fabricwebserver/user/login" class="smart-green" method="post" id="loginForm">
		<label for="userName">用户名：</label>
		<input id="randomData" type="hidden" name="randomData" />
       	<input id="userName" type="text" name="userName" placeholder="请输入用户名" size="35" />
    	<br />
		<label for="password">密码：</label>
       	<input id="password" type="password" name="password" placeholder="请输入密码" />
    	<br />
		<label for="verifyCode">验证码：</label>
        <input id="verifyCode" type="text" name="verifyCode" placeholder="请输入验证码" />
        <img id="verifyCodeImage" class="verify-code" 
						onclick="reloadVerifyCode()"
						src="/fabricwebserver/user/getVerifyCode" />
		<br />
		<!-- <em  onclick="reloadVerifyCode()" title="换一张">
		换一张
		</em> -->
			<button id="login" type="button" value="登录">登录</button>
	</form>
	
</body>
</html>