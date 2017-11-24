<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>数据查询</title>
    <link rel="shortcut icon" href="../images/fav.png">
    <link rel="stylesheet" href="../css/bootstrap.min.css">
    <link rel="stylesheet" href="../css/style.css">
    <script type="text/javascript" src="../js/jquery-1.11.1.min.js"></script>
</head>
<script type="text/javascript">
	$(function() {
		
		$.ajax({    
			url:'/fabricwebserver/chaincode/peerInfo',
			type:'POST',
			async:false,
			dataType:"json",
			success: function (data){ 
				console.log(data);
				$("#jdmc").text("peer0.org1.example.com");
				$("#tdmc").text(data.channelName);
				$("#dqhash").text(data.currentBlockHash);
				$("#qkgd").text(data.channelHeight);
			},
			error: function (data) {
			}
		});
		
		
		$("#search-submit").click(function(e) {
			/* alert($("#jd option:selected").text()) */
			var peerName = $("#jd option:selected").text();
			var key = $("#key").val();
			/* e.preventDefault(); */
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
					//console.log(data);
					//alert(data.payload);
					if(data != null) {
						$(".block-detail-1").text(data.payload);
						$(".block-detail-2").text(data.txId);
						$("#key").val("");
						
					}
					if(data.payload == undefined) {
						alert("块中没有输入的key");
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
    <a href="/fabricwebserver/chaincode/brow" class="btn-back">Home</a>    
    <div class="header-tittle"><a href="index.html">联盟链区块浏览器</a></div>
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
    <div class="block-info">
        <div class="info-tittle">节点信息</div>
        <ul class="info-detail">
            <li>节点名称:
                <span id='jdmc'></span>
            </li>
            <li>区块高度:
                <span id='qkgd'></span>
            </li>
            <li>通道名称:
                <span id='tdmc'></span>
            </li>
        </ul>
        <div class="info-hash">当前块hash: <span id='dqhash'>
            </span></div>
    </div>
    <div class="block-tittle">
        <hr>
        区块详情
    </div>
    <div id="blocks" class="json-editor">
    </div>
	
    <div class="search">
        <!-- <form action="#"> -->
            <label for="search-input">请输入账户主键：</label>
            <input id="key" type="text" id="search-input" placeholder="">
            <!-- <input id="value" type="text" id="search-input" placeholder=""> -->
            <button id="search-submit" type="submit">查询</button>
            <hr>
            <label for="jd">选择区块节点</label>
            <div class="styled-select">
                <select name="peer" id="jd">
                    <option value ="peer0.org1.example.com">peer0.org1.example.com</option>
                    <option value ="peer1.org1.example.com">peer1.org1.example.com</option>
                    <option value="peer0.org2.example.com">peer0.org2.example.com</option>
                    <option value="peer1.org2.example.com">peer1.org2.example.com</option>
                </select>
            </div>
        <!-- </form> -->
    </div>
    <ul class="blocks-wrap" id="blocks">
        <li>
            <div class="block-number-wrap">Block <span class="block-number"></span></div>
            <span>Value <span class="block-detail-1"></span></span>
            <span>TxID <span class="block-detail-2"></span></span>
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