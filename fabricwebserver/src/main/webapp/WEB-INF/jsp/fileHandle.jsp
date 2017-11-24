<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>文件管理</title>
    <link rel="shortcut icon" href="../css/fav.png">
    <link rel="stylesheet" href="../css/style1.css">
<%@ include file="common.jsp"%>

<%
	session.setMaxInactiveInterval(900);
%>

<style type="text/css">
	.logOut {
    position: absolute;
    left: 60px;
    bottom: 20px;
    color: #e3e3e3;
    font-size: 14px;
	}

</style>

<script type="text/javascript">

	$(function() {
		
		var startTime = undefined;
		var endTime = undefined;
		var peerName = undefined;
		
		initFileHandleTable(startTime, endTime);
		$('#peer').combobox({
		    url:'/fabricwebserver/file/peerJson',
		    valueField:'peerName',
		    textField:'peerName'
		});
		
		$("#query").click(function() {
			var startTime = $("#startTime").datebox('getValue');
			var endTime = $("#endTime").datebox('getValue');
			
			if(startTime != "" && endTime != "") {
				if(startTime > endTime) {
					alert("开始时间必须小于结束时间");
					$('#startTime').combo('setText','');
					$('#endTime').combo('setText','');
					var peerName = $('#peer').combobox('getValue');
					
					//initFileHandleTable(null, null, peerName);
				} else {
					var peerName = $('#peer').combobox('getValue');
					
					initFileHandleTable(startTime, endTime, peerName);
				}
			} else {
				
				if(startTime != "") {
					
					var peerName = $('#peer').combobox('getValue');
					//如果选择了开始时间，没有结束时间
					initFileHandleTable(startTime, endTime, peerName);
				}
				
			}
		})
		
	})
	
	
	function initFileHandleTable(startTime, endTime, peerName) {
		$('#fileHandle').datagrid({
			url:'/fabricwebserver/file/fileList',
			method: 'post',
			title:'文件管理',
			queryParams:{
				startTime:startTime,
				endTime:endTime,
				peerName:peerName
			},
			fitColumns: true,
			singleSelect: true, 
			autoRowHeight:true,
			//开启分页功能
			pageSize : 10,
			pagination : true,
			rownumbers : true,
			remoteSort : false,
			columns:[[
				// '&tx_ids=' +row.tx_ids+ 不能使用 太大了
				{field:'file_name',title:'文件名称',width:50, formatter: function(value,row,index){
					
					var options = $('#fileHandle').datagrid('getPager').data("pagination").options;  
					var page = options.pageNumber;
					var rows = options.pageSize;
					
			        return '<a style="color:blue" href="/fabricwebserver/file/download?id='+row.file_id+'&fileName='+row.file_name+ '&fileSize='+row.file_size+'&block_size='+row.block_size+'&block_total='+row.block_number+ '&upload_time='+row.upload_time+ '&file_uuid='+row.file_uuid+ '&page='+page+ '&rows='+rows+ '">'+row.file_name+'</a>';
			        }
				},
			    {field:'file_size',title:'大小（Byte）',width:50, formatter: function(value,row,index){
			        return row.file_size + ' <span style="color:Silver">Byte</span>'}
			    },
			    {field:'upload_time',title:'上传时间',width:50, sortable:true, order:'desc',
			    	sorter:function(a, b){
			    		
						AnyrSfm = a.split(' ');
						Anyr = AnyrSfm[0];
						Asfm = AnyrSfm[1];
						
						An = Anyr.split('-')[0];
						Ay = Anyr.split('-')[1];
						Ar = Anyr.split('-')[2];
						
						As = Asfm.split(':')[0];
						Af = Asfm.split(':')[1];
						Am = Asfm.split(':')[2];
						
						
						BnyrSfm = b.split(' ');
						Bnyr = BnyrSfm[0];
						Bsfm = BnyrSfm[1];
						
						Bn = Bnyr.split('-')[0];
						By = Bnyr.split('-')[1];
						Br = Bnyr.split('-')[2];
						
						Bs = Bsfm.split(':')[0];
						Bf = Bsfm.split(':')[1];
						Bm = Bsfm.split(':')[2];
						
						//年月日
 						if(An == Bn) {
							if(Ay == By) {
								if(Ar == Br) {
									if(As == Bs) {
										if(Af == Bf) {
											if(Am == Bm) {
												return (Am > Bm?1: -1)
											}
										} else {
											return (Af > Bf?1: -1)
										}
										
									} else {
										return (As > Bs ? 1:-1);
									}
									
								} else {
									return (Ar > Br ? 1:-1);
								}
								 
							} else {
								return (Ay > By ? 1:-1);
							} 
							
						} else {
							return  (An > Bn ? 1:-1);
						} 
						
					} 
			    },
				{field:'file_id',title:'HASH值',width:80},
				{field:'block_number', title:'块数（块）', hidden:false, formatter: function(value,row,index){
			        return row.block_number + ' <span style="color:Silver">块</span>'
			        }
				},
				{field:'block_size', title:'块大小（Byte）', hidden:false, formatter: function(value,row,index){
			        return row.file_size + ' <span style="color:Silver">Byte</span>'
			        }
			   	},
				
				{field:'file_uuid', title:'uuid', hidden:false }
			]]
		})
	}
	
</script>
</head>
<body>
	<div class="pay-tittle">
	    <h1>文件管理</h1>
	    <a href="/fabricwebserver/chaincode/brow" class="btn-back">返回首页</a>
	    <a href="/fabricwebserver/user/logOut" class="logOut">退出登录</a>
	   
	</div>
	
	<div style="margin: 0 auto;padding: 20px 5%">
		
		<table align="center">
			<tr>
				<td>开始时间:</td>
				<td>
					<input id="startTime" class="easyui-datebox" data-options="sharedCalendar:'#cc'">
				</td>
				
				<!-- <td>-</td> -->
				<td>结束时间:</td>
				<td>
					<input id="endTime" class="easyui-datebox" data-options="sharedCalendar:'#cc'">
				</td>
				
				<td>
					<input id="peer" name="peerName" value="请选择节点">
				</td>
				
				<td>
					<a id="query" class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width:80px">查询</a>
				</td>
			</tr>
		</table>
		
		<div id="cc" class="easyui-calendar"></div><br>
		
		<table id="fileHandle" ></table>
	</div>
</body>
</html>