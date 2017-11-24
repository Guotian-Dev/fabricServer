<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel ="stylesheet" href="../bootstrap/css/bootstrap.css">
<link rel ="stylesheet" href="../bootstrap/css/bootstrap-responsive.css">
<script type="text/javascript" src="../js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="../bootstrap/js/bootstrap.js"></script>

<script type="text/javascript">
	$(function() {
		$("#addBtn").click(function(){
			$("#addForm").attr("style","display:;");
		})
		
		$("#jxBtn").click(function() {
   	  		//jQuery.post(url,data,success(data, textStatus, jqXHR),dataType)
			window.location.href="/fabricwebserver/lot/jxAdd";
    	});
	})
	
</script>
</head>
<body>
	<div class="page-header" align="center">
		<h3>彩票列表</h3>
	</div>
	<div class="panel panel-default">
	  <!-- Table -->
	  <table class="table" class="pagination">
	  	<tr>
			<th>id</th>
			<th>红号</th>
			<th>蓝号</th>
			<th>操作</th>
		</tr>
		<c:forEach items="${lotList.list }" var="lot">
		<tr>
			<td>
				${lot.id }
			</td>
			<td>
				${lot.redNumber }
			</td>
			<td>
				${lot.blueNumber }
			</td>
			<td>
				<a href="/fabricwebserver/lot/modify?id=${lot.id }">修改</a>
				<a href="/fabricwebserver/lot/delete?id=${lot.id }">删除</a>
			</td>
		</tr>
		</c:forEach>
	  </table>
	</div>
	
	<div align="center">
		<button id="addBtn" class="btn btn-default">新增</button>
		<button id="jxBtn" class="btn btn-default">机选</button>
		<form id="addForm" action="/fabricwebserver/lot/add" method="post" style="display: none">
			<div class="form-group">
			    红号：<input type="text" class="form-control" name="redNumber"/>
			</div>
			<div class="form-group">
			    蓝号：<input type="text" class="form-control" name="blueNumber"/>
			</div>
			<button type="submit" class="btn btn-default">确认</button>
		</form>
		
	</div>
	
</body>
</html>