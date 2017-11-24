<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>数据管理</title>
    <link rel="shortcut icon" href="../css/fav.png">
    <link rel="stylesheet" href="../css/style1.css">
<title>数据录入</title>
<%@ include file="common.jsp"%>

<script type="text/javascript">
	
	/* var peerJson = '[{"peerName":"peer0.org1.example.com","peerIp":"grpc://10.0.6.226:7051"},{"peerName":"peer1.org1.example.com","peerIp":"grpc://10.0.6.226:7056"}]'; */
	
	var peerJson;
	//console.log(peerJson)
	
	$(function() {
		$.ajax({    
			url:'/fabricwebserver/chaincode/peerJson',      
		       type : 'POST',    
		       async:false,
		       dataType: "json",
		       success: function (data){    
		       	peerJson = data; 
		       	console.log(peerJson);
		       }    
		 });
	 	initKeyValueTable();
		
	})
	function initKeyValueTable() {
		$('#keyValueTable').datagrid({
			/*url: '',
			method: 'post',
			title:'项目合作评分项明细',
			queryParams:{
				gysbh:gysbh
			},*/
			title:'数据录入',
			fitColumns: true,
			singleSelect: true, 
			autoRowHeight:true,
			/* //开启分页功能
			pageSize : 10,
			pagination : true,
			rownumbers : true, */
			columns:[[
				{field:'ck', width:50, checkbox:true},
				{field:'peerName', title:'节点名称', width:50, 
					editor:{
						type:'combobox',
						options:{
							valueField:'peerName',
							textField:'peerName',
							data:peerJson,
							required:true
						}
					}
				},
				{field:'key',title:'键',width:50,editor:{type:'textbox'}},
				{field:'value',title:'值',width:50,editor:{type:'textbox'}}
			]],
			onClickRow: function(rowIndex, rowData){onClickRow('keyValueTable',rowIndex,undefined)},
			onCheck: function(rowIndex, rowData){onCheck('keyValueTable',rowIndex,undefined)},
			toolbar: [{
				text:'添加',
				iconCls: 'icon-add',
				handler: function(){append('keyValueTable',undefined,undefined,undefined)}
			},'-',{
				text:'保存',
				iconCls: 'icon-save',
				handler: function(){accept('keyValueTable')}
			},'-',{
				text:'查询',
				iconCls: 'icon-search',
				handler: function(){query('keyValueTable')}
			},'-',{
				text:'删除',
				iconCls: 'icon-remove',
				handler: function(){remove('keyValueTable')}
			},/* '-',{
				text:'刷新',
				iconCls:'',
				handler: function(){dataMove()}
			}, */
			'-',{
				text:'数据流动(转账)',
				iconCls:'',
				handler: function(){dataMove()}
			},'-',{
				text:'区块数据查询',
				iconCls:'',
				handler: function(){dataQuery()}
			}
			/* ,'-',{
				text:'删除',
				iconCls: 'icon-remove',
				handler: function(){removeit('zyx_list')}
			},'-',{
				text:'保存',
				iconCls: 'icon-save',
				handler: function(){accept('zyx_list',undefined)}
			},'-',{
				text:'撤销',
				iconCls: 'icon-undo',
				handler: function(){reject('zyx_list')}
			} */]
		})
	}
/* 	easyUi_edit_table('keyValueTable',zhxx_columns,zhxx_comboboxFields,isEidt,
			'','','','','',
			publicDefaults,'',tabListHideBt,
			false,'','','','',''); */
	
</script>
</head>
<body>
	<div class="pay-tittle">
    <h1>数据管理展示</h1>
    <a href="/fabricwebserver/chaincode/brow" class="btn-back">返回首页</a>
</div>

	<div style="margin: 0 auto;padding: 20px 5%">
		<table id="keyValueTable" ></table>
	</div>
</body>
</html>