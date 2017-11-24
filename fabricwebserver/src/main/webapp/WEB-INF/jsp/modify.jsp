<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel ="stylesheet" href="../bootstrap/css/bootstrap.css">
<link rel ="stylesheet" href="../bootstrap/css/bootstrap-responsive.css">
<script type="text/javascript" src="../js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="../bootstrap/js/bootstrap.js"></script>
</head>
<body>
	<div class="page-header">
		<div align="center">修改</div>
	</div>
	<div align="center">
		<form id="addForm" action="/fabricwebserver/lot/add" method="post" >
				<input type="text" class="form-control" name="id" value="${lot.id }" style="display:none"/>
				<div class="form-group">
				    红号：<input type="text" class="form-control" name="redNumber" value="${lot.redNumber }"/>
				</div>
				<div class="form-group">
				    蓝号：<input type="text" class="form-control" name="blueNumber" value="${lot.blueNumber }"/>
				</div>
				<button type="submit" class="btn btn-default">确认</button>
		</form>
	</div>

</body>
</html>