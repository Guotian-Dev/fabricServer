<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>区块查询</title>
    <link rel="shortcut icon" href="../images/fav.png">
    <link rel="stylesheet" href="../css/bootstrap.min.css">
    <link rel="stylesheet" href="../css/style.css">
    <script type="text/javascript" src="../js/jquery-1.11.1.min.js"></script>
</head>

<script type="text/javascript">
	$(function() {
		$("#search-submit").click(function() {
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
					console.log(data);
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
    <div class="search">
        <form action="#">
            <label for="search-input">请输入：</label>
            <input id="key" type="text" id="search-input" placeholder="">
            <!-- <input id="value" type="text" id="search-input" placeholder=""> -->
            <button id="search-submit" type="submit">查询</button>
        </form>
        
	        选择区块节点
			<select id="jd">
			  <option value ="peer0.org1.example.com">peer0.org1.example.com</option>
			  <option value ="peer1.org1.example.com">peer1.org1.example.com</option>
			  <option value="peer2.org1.example.com">peer2.org1.example.com</option>
			  <option value="peer3.org1.example.com">peer3.org1.example.com</option>
			</select>
    </div>
    <ul class="blocks-wrap" id="blocks">
        <li>
            <div class="block-number-wrap">Block <span class="block-number">123456</span></div>
            <span>AAAAA <span class="block-detail-1">123456</span></span>
            <span>AAAAA <span class="block-detail-2">123456</span></span>
            <span>AAAAA <span class="block-detail-3">123456</span></span>
            <span>AAAAA <span class="block-detail-4">123456</span></span>
            <span>AAAAA <span class="block-detail-5">123456</span></span>
        </li>
        <li>
            <div class="block-number-wrap">Block <span class="block-number">123456</span></div>
            <span>AAAAA <span class="block-detail-1">123456</span></span>
            <span>AAAAA <span class="block-detail-2">123456</span></span>
            <span>AAAAA <span class="block-detail-3">123456</span></span>
            <span>AAAAA <span class="block-detail-4">123456</span></span>
            <span>AAAAA <span class="block-detail-5">123456</span></span>
        </li>
        <li>
            <div class="block-number-wrap">Block <span class="block-number">123456</span></div>
            <span>AAAAA <span class="block-detail-1">123456</span></span>
            <span>AAAAA <span class="block-detail-2">123456</span></span>
            <span>AAAAA <span class="block-detail-3">123456</span></span>
            <span>AAAAA <span class="block-detail-4">123456</span></span>
            <span>AAAAA <span class="block-detail-5">123456</span></span>
        </li>
    </ul>
</div>
<div class="footer">
    <div class="center-block">
        <p>联盟链区块浏览器2017</p>
    </div>
</div>
</body>
</html>