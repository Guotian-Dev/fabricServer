<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>联盟链区块浏览器</title>
<link rel ="stylesheet" href="../bootstrap/css/bootstrap.css">
<link rel ="stylesheet" href="../bootstrap/css/bootstrap-responsive.css">
<script type="text/javascript" src="../js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="../bootstrap/js/bootstrap.js"></script>
</head>
<body>
	<div class="page-header" align="center">
		<h3>联盟链区块浏览器</h3>
	</div>
	<div>区块浏览</div>
	
<%-- 	${channel }
	${blockMap } --%>
	
	<div class="page-header" align="center">
		<h4>区块浏览</h4>
	</div>
	<c:forEach items="${channel }" var="ch">
	<div>节点名称:</div>
	<div>${ch.peerName }</div>
	<div>区块高度:</div>
	<div>${ch.channelHeight }</div>
	<div>通道名称:</div>
	<div>${ch.peerName }</div>
	<div>当前块hash:</div>
	<div>${ch.currentBlockHash }</div>
	</c:forEach>
	
	-------------------------------------------
	
	<div class="page-header" align="center">
		<h4>块信息</h4>
	</div>
	<c:forEach items="${blockMap }" var="bl">
	<div>块高度:</div>
	<div>${bl.blockHeight }</div>
	<div>块hash:</div>
	<div>${bl.blockHash }</div>
	</c:forEach>
	
<%-- 	<c:forEach items="${blockInfo }" var="block">
		<div>peerName:${block.peerName }</div>
		<div>高度:${block.channelHeight }</div>
		<div>priHash:${block. previousBlockHash}</div>
		<div>hash:${block.currentBlockHash }</div>
	</c:forEach> --%>
</body>
</html>