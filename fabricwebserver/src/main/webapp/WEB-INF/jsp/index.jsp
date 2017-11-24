<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="../js/jquery-1.11.1.min.js"></script>
<title>Insert title here</title>
<script type="text/javascript">
	$(function() {
		
		$("#btn").click(function() {
			alert(1);
/* 			$.ajax({  
		        type : 'POST',  
		        contentType : 'application/json',  
		        url : 'http://localhost:80/fabricwebserver/emp/getObj?id=100',  
		        processData : false,  
		        dataType : 'json',  
		        success : function(data) {  
		            alert("id: " + data.id + "\nname: " + data.name + "\nemail: "  
		                    + data.email);  
		        },  
		        error : function() {  
		            alert('Err...');  
		        }  
			}) */
			$.ajax({  
		        type : 'POST',  
		        contentType : 'application/json',  
		        url : 'http://localhost:80/fabricwebserver/emp/getList',  
		        processData : false,  
		        dataType : 'json',  
		        success : function(data) {  
		           console.log(data);
		        },  
		        error : function() {  
		            alert('Err...');  
		        }  
			})
		});
		
	});

</script>
</head>
<body>
	<h1>显示所有员工</h1>
	<%-- ${emps } --%>
	<button id="btn">获取Employee对象</button>
</body>
</html>