<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@ include file="common.jsp"%>
<script src="../js/pako.js"></script>
<script type="text/javascript">
	
	$(function() {
		
		$("#BTC").click(function() {
			var webSocketUrl = "wss://api.huobi.pro/ws";
		    var sendMessage = {'sub': 'market.btcusdt.kline.1day','id': 'id1 ' + new Date()};
		    var bz = 'BTC';
		    
		    webSocket (webSocketUrl, sendMessage, bz);
		})
		
		$("#LTC").click(function() {
			var webSocketUrl = "wss://api.huobi.pro/ws";
		    var sendMessage = {'sub': 'market.ltcusdt.kline.1day','id': 'depth ' + new Date()};
		    var bz = 'LTC';
		    
		    webSocket (webSocketUrl, sendMessage, bz);
		})
		
		$("#ETH").click(function() {
			var webSocketUrl = "wss://api.huobi.pro/ws";
		    var sendMessage = {'sub': 'market.ethusdt.kline.1day','id': 'depth ' + new Date()};
		    var bz = 'ETH';
		    
		    webSocket (webSocketUrl, sendMessage, bz);
		})
		
		
		$("#BTC_bitfinex").click(function() {
			var webSocketUrl = "wss://api.bitfinex.com/ws";
		    var sendMessage = {
			    	   "event":"subscribe",
			    	   "channel":"ticker",
			    	   "pair":"BTCUSD"
			    	};
		    var webSocket = new WebSocket(webSocketUrl);
		    webSocket.binaryType = "arraybuffer";

		    webSocket.onopen = function(event){
		        console.log(("webSocket connect at time: "+new Date()));
		        webSocket.send(JSON.stringify(sendMessage));
		    }
		    webSocket.onmessage = function(event){
		        var raw_data = event.data;
		        var obj =JSON.parse(raw_data);
		        console.log(raw_data);
		        console.log(obj);
		        console.log(obj[1]);
		        if(obj[1] != 'hb') {
		        	$("#priceBTC_bitfinex").html(obj[1]);
		        }
		    }
		    
		    webSocket.onclose = function(){
		        console.log("webSocket connect is closed");
		        console.log(arguments);
		    }
		    webSocket.onerror = function(){
		        console.log("error");
		        console.log(arguments);
		    }
		});
		
		$("#BTC_OK").click(function() {
			var webSocketUrl = "wss://real.okcoin.com:10440/websocket";
			var sendMessage = {'event':'addChannel','channel':'ok_sub_spot_btc_usd_ticker'};
		    var webSocket = new WebSocket(webSocketUrl);
		    webSocket.binaryType = "arraybuffer";

		    webSocket.onopen = function(event){
		        console.log(("webSocket connect at time: "+new Date()));
		        webSocket.send(JSON.stringify(sendMessage));
		    }
		    webSocket.onmessage = function(event){
		    	//响应的数据解析并展示
		        var raw_data = event.data;
		        console.log(raw_data);
		        var r1 = raw_data.replace("[", "");
		        var r2 = r1.replace("]", "");
		        var obj = JSON.parse(r2);
		        console.log(obj);
		        console.log(obj.data.buy);
		        $("#priceBTC_OK").html(obj.data.buy);
		        $("#volBTC_OK").html(obj.data.vol);
		       
		    }
		    
		    webSocket.onclose = function(){
		        console.log("webSocket connect is closed");
		        console.log(arguments);
		    }
		    webSocket.onerror = function(){
		        console.log("error");
		        console.log(arguments);
		    }
		});
		
		$("#BTC_HitBtc").click(function() {
			var webSocketUrl = "wss://api.hitbtc.com/api/2/ws";
			var sendMessage = {
					  "method": "subscribeTicker",
					  "params": {
					    "symbol": "ETHBTC"
					  },
					  "id": 123
					};
		    var webSocket = new WebSocket(webSocketUrl);
		    webSocket.binaryType = "arraybuffer";
			//连接并发送请求
		    webSocket.onopen = function(event){
		        console.log(("webSocket connect at time: "+new Date()));
		        webSocket.send(JSON.stringify(sendMessage));
		    }
		    //响应参数的解析并展示
		    webSocket.onmessage = function(event){
		        var raw_data = event.data;
		        console.log(raw_data);
		        var obj = JSON.parse(raw_data);
		        console.log(obj.id);
		        if(obj.id == undefined) {
			       if(obj.params == undefined) {
			        	$("#price_BTC_HitBtc").html(obj.data.ask);
			       } else {
			        	$("#price_BTC_HitBtc").html(obj.params.ask);
			       }
		        }
		    }
		    
		    webSocket.onclose = function(){
		        console.log("webSocket connect is closed");
		        console.log(arguments);
		    }
		    webSocket.onerror = function(){
		        console.log("error");
		        console.log(arguments);
		    }
		});
		
		   /* setInterval("$.ajax({"   
					 + "url:'https://api.bitfinex.com/v2/tickers?symbols=tOMGUSD',"      
				     +" type : 'GET',"    
				     +" async:false,"
				     +" dataType: 'json', "
				     +" success: function (data){ "
				     +"    $('#BugOMG').html((data[0][1]) * 6.6); "
				     +" $('#amount1').html(data[0][2]); "
				     +"    $('#SaleOMG').html(data[0][3]*6.66); "
				     +"    $('#amount2').html(data[0][4]); "
				     +"    $('#MaxOMG').html(data[0][9]*6.66); "
				     +"    $('#MinOMG').html(data[0][7]*6.66); "
				      +" }    "
					 +" });",5000);//1000为1秒钟
		}) */
			  
		//获取数据
		function webSocket (webSocketUrl, sendMessage, bz) {
			
			var webSocket = new WebSocket(webSocketUrl);
		    webSocket.binaryType = "arraybuffer";
		    webSocket.onopen = function(event){
		        console.log(("webSocket connect at time: "+new Date()));
		        webSocket.send(JSON.stringify(sendMessage));
		    };
		    webSocket.onmessage = function(event){
		        var raw_data = event.data;
		        console.log(event);
		        console.log(raw_data);
		        window.raw_data = raw_data;
		        var ua = new Uint8Array(raw_data);
		        var json = pako.inflate(ua,{to:"string"});
		        var data = JSON.parse(json);
		        if(data["ping"]){
		            webSocket.send(JSON.stringify({"pong":data["ping"]}));
		        }
		        else{
		            if(data.ch){
		                var tick = data.tick;
		                $("#nowPrice" + bz).html(data.tick.close);
		                $("#nowAmount" + bz).html(data.tick.amount+tick.count);
		                $("#zhangfu" + bz).html((Math.floor((tick.close-tick.open)/(tick.open*100*100)/100)+"%"));
		            }
		        }
		    };
		    webSocket.onclose = function(){
		        console.log("webSocket connect is closed");
		        console.log(arguments);
		    };
		    webSocket.onerror = function(){
		        console.log("error");
		        console.log(arguments);
		    };
		};
	})
	
</script>
</head>

<body>
	<input type="button" id="BTC" value="BTC"/>
	<input type="button" id="LTC" value="LTC"/>
	<input type="button" id="ETH" value="ETH"/>
	<input type="button" id="BTC_bitfinex" value="BTC_bitfinex"/>
	<input type="button" id="BTC_OK" value="BTCOK"/>
	<input type="button" id="BTC_HitBtc" value="BTC_HitBtc"/>
	<br>
	
	<table>
	<tr>
	<td>
	
	BTC	
	<div>
	    当前价格:<span id="nowPriceBTC">0</span>
	    <br>
	    量:<span id="nowAmountBTC">0</span>
	    <br>
	    涨幅:<span id="zhangfuBTC">0</span>
	</div>
	
	</td>
	
	<td>
		BTC_OK	
	<div>
	    当前价格:<span id="priceBTC_OK">0</span>
	    <br>
	    量:<span id="volBTC_OK">0</span>
	    <br>
	    涨幅:<span id="priceBTCOK">0</span>
	</div>
	</td>
	
	<td>
	LTC
	<div>
	    当前价格:<span id="nowPriceLTC">0</span>
	    <br>
	    量:<span id="nowAmountLTC">0</span>
	    <br>
	    涨幅:<span id="zhangfuLTC">0</span>
	</div>
	</td>
	
	<td>
	ETH
	<div>
	    当前价格:<span id="nowPriceETH">0</span>
	    <br>
	    量:<span id="nowAmountETH">0</span>
	    <br>
	    涨幅:<span id="zhangfuETH">0</span>
	</div>
	</td>
	
	<td>
	BTC_bitfinex
	<div>
	  当前价格:<span id="priceBTC_bitfinex">0</span>
	    <br>
	    量:<span id="amount_bitfinex">0</span>
	    <br>
	 涨幅:<span id="zhangfuBitfinex">0</span>
	</div>
	</td>
	<td>
	BTC_HitBtc
	<div>
	  当前价格:<span id="price_BTC_HitBtc">0</span>
	    <br>
	    量:<span id="amount_BTC_HitBtc">0</span>
	    <br>
	 涨幅:<span id="zhangfuBitfinex">0</span>
	</div>
	</td>
	
	</tr>
	</table>
</body>
</html>