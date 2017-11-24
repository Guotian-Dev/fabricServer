<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>区块浏览</title>
    <link rel="shortcut icon" href="../images/fav.png">
    <link rel="stylesheet" href="../css/bootstrap.min.css">
    <link rel="stylesheet" href="../css/style.css">
    <script type="text/javascript" src="../js/jquery-1.11.1.min.js"></script>
</head>
<script type="text/javascript">
	$(function() {
		$.ajax({    
			url:'/fabricwebserver/chaincode/blockInfo',      
		       type : 'POST',    
		       async:false,
		       dataType: "json",
		       success: function (data){    
		       	console.log(data);
		       }
		 });
	})

</script>
<body>
<div id="header">
    <div class="header-cover"></div>
    <div class="header-tittle">联盟链区块浏览器</div>
    <div class="container-fluid">
        <div class="row">
            <div class="col-xs-3 col-xs-offset-3">
                <a href="/fabricwebserver/chaincode/blockBrow">
                    <div class="link">
                        区块浏览
                    </div>
                </a>
            </div>
            <div class="col-xs-3">
                <a href="/fabricwebserver/chaincode/dataQuery">
                    <div class="link">
                        数据查询
                    </div>
                </a>
            </div>
        </div>
    </div>
</div>
<div class="content container">
    <ul class="blocks-wrap" id="blocks">
    	<c:forEach items="${blockMap }" var="bl">
	        <li>
	            <div class="block-number-wrap">Block <span class="block-number">${bl.blockHeight }</span></div>
	            <span>块Hash <span class="block-detail-1">${bl.blockHash }</span></span>
<!-- 	            <span>AAAAA <span class="block-detail-2">123456</span></span>
	            <span>AAAAA <span class="block-detail-3">123456</span></span>
	            <span>AAAAA <span class="block-detail-4">123456</span></span>
	            <span>AAAAA <span class="block-detail-5">123456</span></span> -->
	        </li>
	    </c:forEach>
    </ul>
</div>
<div class="footer">
    <div class="center-block">
        <p>联盟链区块浏览器2017</p>
    </div>
</div>
</body>
</html>