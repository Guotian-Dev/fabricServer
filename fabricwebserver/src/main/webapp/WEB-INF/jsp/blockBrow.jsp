<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>区块浏览</title>
    <link rel="shortcut icon" href="../images/fav.png">
    <link rel="stylesheet" href="../css/bootstrap.min.css">
    <link rel="stylesheet" href="../css/jsoneditor.css">
    <link rel="stylesheet" href="../css/style.css">
    <script src="../js/jquery-3.2.1.min.js"></script>
    <script src="../js/jquery.jsoneditor.min.js"></script>
</head>
<body>
<div id="header">
    <div class="header-cover"></div>
    <a href="/fabricwebserver/chaincode/brow" class="btn-back">Home</a>
    <div class="header-tittle"><a href="/fabricwebserver/chaincode/brow">联盟链区块浏览器</a></div>
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

<!-- {"peerName":"peer0.org1.example.com","channelHeight":"2","channelName":"foo","currentBlockHash":"f7234881f51ecf70ff4015b000ac7c8a5cd735e18164f42bd08aa4e44d7e6422"}
 -->
<div class="content container">
    <div class="block-info">
        <div class="info-tittle">节点信息</div>
        <ul class="info-detail">
            <li>节点名称:
                <span>${block.peerName }</span>
            </li>
            <li>区块高度:
                <span>${block.channelHeight }</span>
            </li>
            <li>通道名称:
                <span>${block.channelName }</span>
            </li>
        </ul>
        <div class="info-hash">当前块hash: <span>
            ${block.currentBlockHash }</span></div>
    </div>
    <div class="block-tittle">
        <hr>
        区块详情
    </div>
    <div id="blocks" class="json-editor">
    </div>
</div>
<script>
    $(document).ready(function () {
        var url = '/fabricwebserver/chaincode/blockInfo';
        $.getJSON(url,function (data) {
            console.log(data);
            var myjson = data.blockListInfo.reverse();
            var opt = {
                change: function(data) { /* called on every change */ },
                propertyclick: function(path) { /* called when a property is clicked with the JS path to that property */ }
            };
            /* opt.propertyElement = '<textarea>'; */ // element of the property field, <input> is default
            /* opt.valueElement = '<textarea>'; */  // element of the value field, <input> is default
            $('#blocks').jsonEditor(myjson, opt);
        })
    });
</script>
<div class="footer">
    <div class="center-block">
        <p>联盟链区块浏览器2017</p>
    </div>
</div>
</body>
</html>