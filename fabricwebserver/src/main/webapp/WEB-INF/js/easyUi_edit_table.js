/**
 * 数据录入js
 */

var editIndex = undefined;
var editTable_id = undefined;
var editTableComboboxFields = undefined;
var boolean = false;
var count = 0;
//结束编辑
function endEditing(){
	if(editIndex==undefined){return true}
	if(boolean) {
		//获取key的编辑器
		var ed = $('#'+editTable_id).datagrid('getEditor', {index:editIndex,field:'key'});
		
		//获取value的编辑器
		var ed1 = $('#'+editTable_id).datagrid('getEditor', {index:editIndex,field:'value'});
		var key = $(ed.target).textbox('getText');
		
		if(key == undefined || key == '') {
			$.messager.alert("操作提示", "请输入key值");
			return false;
		}
		
		$.ajax({    
			url:'/fabricwebserver/chaincode/queryByKey',
			type:'POST',
			async:false,
			dataType:"json",
			data:{
				key:key
			},
			success: function (data){ 
				if(data != null) {
					$(ed1.target).textbox('setText', data);
					$('#'+editTable_id).datagrid('endEdit', editIndex);
					editIndex = undefined;
					boolean = false;
					count = 0;
					return true;
				}
				if(count > 2) {
					$.messager.alert("操作提示", "请不要重复查询无效地址，单击刷新！");
				}
				$.messager.alert("操作提示", "块中没有对应的key,请输入有效的key值");
//				$('#'+editTable_id).datagrid('rejectChanges');
//				editIndex = undefined;
//				boolean = false;
				return true;
			},
			error: function (data) {
//				alert("块中没有对应的key");
			}
		});
	}
	if (!boolean && $('#'+editTable_id).datagrid('validateRow', editIndex)){
		
		//获取key的编辑器
		var ed = $('#'+editTable_id).datagrid('getEditor', {index:editIndex,field:'key'});
		
		//获取value的编辑器
		var ed1 = $('#'+editTable_id).datagrid('getEditor', {index:editIndex,field:'value'});
		
		var key = $(ed.target).textbox('getText');
		var value = $(ed1.target).textbox('getText');
		
		if(value == undefined) {
			$.messager.alert('警告','value值不能为空！')
			return false;
		}
		
		if(key == undefined) {
			$.messager.alert('警告','key值不能为空！');
			return false;
		}
		var rows = $('#'+editTable_id).datagrid('getRows');
		for(i=0; i<rows.length; i++) {
			console.log(rows[i].key);
			if(key == rows[i].key) {
				$.messager.alert('警告','key值不能重复！');
				$(ed.target).textbox('setText', '');
				return false;
			}
		}
		
		$('#'+editTable_id).datagrid('endEdit', editIndex);
		editIndex = undefined;
		return true;
	} 
}
//点击行
function onClickRow(table_id,index,comboboxFields){
	if (editIndex != index || editTable_id != table_id){
		if (endEditing()){
			$('#'+table_id).datagrid('beginEdit', index);
			$('#'+table_id).datagrid('unselectRow', index);
			editIndex = index;
			editTableComboboxFields = comboboxFields;
			editTable_id=table_id;
		}
	}
}
//追加
function append(table_id,defaultValues,comboboxFields,colspanfield){
	if(colspanfield!=undefined&&$('#'+table_id).datagrid('getRows').length>0){
		var lits = $('#'+table_id).datagrid('getRows');
		if(lits[0][colspanfield].indexOf("没有相关记录！")>0){
			$('#'+table_id).datagrid('deleteRow', 0);
		}
	}
	if (endEditing()){
		$('#'+table_id).datagrid('appendRow',{});
		//$('#'+table_id).datagrid('appendRow',defaultValues);
		editIndex = $('#'+table_id).datagrid('getRows').length-1;
		$('#'+table_id).datagrid('beginEdit', editIndex);
		editTableComboboxFields = comboboxFields;
		editTable_id=table_id;
	}
}

//获取数据
function accept(table_id){
	if (endEditing()){
		var row = $('#' + table_id).datagrid('getRows');
		saveTableData(table_id, row);
	}
}

//保存数据
function saveTableData(table_id,datas){
	if(datas.length > 0){
		var jsonDatas = dataToJson(table_id,datas);
		var jsonStr = JSON.stringify(jsonDatas);
		$.ajax({    
			url:'/fabricwebserver/chaincode/addDataToChain',
			type:'POST',
			async:false,
			dataType:"json",
			data:{
				datas:jsonStr
			},
			success: function (data){ 
				$.messager.alert("操作提示", data.success);
				//重置列表
				$('#'+table_id).datagrid('rejectChanges');
				editIndex = undefined;
			},
			error: function (data) {
				if(data.status == 500) {
					$.messager.alert("操作提示", "录入失败！");
				};
				//重置列表
				$('#'+table_id).datagrid('rejectChanges');
				editIndex = undefined;
			}
		});
	} else {
		$.messager.alert("操作提示", "请先添加数据！");
	}
}

//封装列表数据为Json数组
function dataToJson(table_id, datas) {
	var arrayDatas = new Array();
	var columns = $("#"+table_id).datagrid("getColumnFields");
	
	for(i=0;i<datas.length;i++){
		var jsonData = {};
		for(j=0;j<columns.length;j++){
			jsonData[columns[j]] = datas[i][columns[j]];
		}
		arrayDatas[i]=jsonData;
	}
	return arrayDatas;
}

////勾选复选框
function onCheck(table_id,index,comboboxFields){
	if(endEditing()){

	}else{
		$('#'+table_id).datagrid('uncheckAll');
	}
}


function query(table_id){
	//alert(1);
//	window.location.href = "http://localhost/fabricwebserver/chaincode/query";
//	if($('#'+table_id).datagrid('getRows').length>0){
//		var lits = $('#'+table_id).datagrid('getRows');
//		if(lits[0][colspanfield].indexOf("没有相关记录！")>0){
//			$('#'+table_id).datagrid('deleteRow', 0);
//		}
//	}
//	if(boolean = true) {
//		return false;
//	}
	count = count++;
	boolean = true;
	if (endEditing()){
		$('#'+table_id).datagrid('appendRow',{});
		//$('#'+table_id).datagrid('appendRow',defaultValues);
		editIndex = $('#'+table_id).datagrid('getRows').length-1;
		$('#'+table_id).datagrid('beginEdit', editIndex);
//		editTableComboboxFields = comboboxFields;
		editTable_id=table_id;
	}
}

function remove(table_id){
	//alert(1);
//	window.location.href = "http://localhost/fabricwebserver/chaincode/delete";
	var row = $('#'+table_id).datagrid('getSelected');
	if(row == undefined || row.length < 0) {
		$.messager.alert("操作提示", "请先选中要删除的数据");
		return false;
	}
	
	var key = row.key;
	
//	console.log(row.key);
	$.ajax({    
		url:'/fabricwebserver/chaincode/deleteByKey',
		type:'POST',
		async:false,
		dataType:"json",
		data:{
			key:key
		},
		success: function (data){ 
			if(data == true) {
				$.messager.alert("操作提示", "删除成功!")
			}
			//重置列表
			$('#'+table_id).datagrid('rejectChanges');
			editIndex = undefined;
		},
		error: function (data) {
			$.messager.alert("操作提示", "删除失败，块中没有对应的key");
		}
	});
}


function dataMove() {
	window.location.href = "/fabricwebserver/chaincode/dataMove";
}
function dataQuery() {
	window.location.href = "/fabricwebserver/chaincode/dataQuery";
}









///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

////删除
//function removeit(table_id){
//	var list = $('#'+table_id).datagrid('getSelections');
//	if(list){
//		var ids="";
//		for(i=0; i<list.length; i++){
//			var id = list[i]["ID"];
//			if(id!=undefined&&id!=""){
//				ids+=(id+",");
//			}
//		}
//		if(ids.length>0){
//			ids = ids.substring(0,ids.length-1);
//			$.messager.confirm("提示", "您确定要删除吗?", function (r){
//				if(r){
//					$.messager.progress({ title: "删除", msg: "正在提交..." });
//					$.ajax({    
//						url:'/CYTSMICE_PRM_WEBSITE/tableDbData/removeOneTableData?'
//							+'sid='+$('#sid').val()
//							+'&tablename='+table_id
//							+'&ids='+ids,
//							type:'POST',
//							async:false,  
//							success: function (data){
//								$.messager.progress('close');
//								//页面删除
//								for(i=0; i<list.length; i++){
//									var selectIndex = $('#'+table_id).datagrid('getRowIndex',list[i]); 
//									$('#'+table_id).datagrid('cancelEdit', selectIndex)
//									.datagrid('deleteRow', selectIndex);
//								}
//								if(editTable_id==table_id){
//									editIndex = undefined;
//								}
//							},
//							error: function (text) {
//								$.messager.progress('close');
//								$.messager.alert('错误', text.statusCode);
//							}
//					});	
//				}
//			});
//		}else{
//			//页面删除
//			for(i=0; i<list.length; i++){
//				var selectIndex = $('#'+table_id).datagrid('getRowIndex',list[i]); 
//				$('#'+table_id).datagrid('cancelEdit', selectIndex)
//				.datagrid('deleteRow', selectIndex);
//			}
//			if(editTable_id==table_id){
//				editIndex = undefined;
//			}
//		}
//	}
//
//}

//保存
//function accept(table_id,comboboxFields){
//	if (endEditing()){
////		saveSublist(table_id,$('#'+table_id).datagrid('acceptChanges'));
//		//saveSublist(table_id,$('#'+table_id).datagrid('getChanges'),comboboxFields);
////		saveSublist(table_id, $('#'+table_id).datagrid('getData'));
//		
//		var row = $('#' + table_id).datagrid('getRows');
////		console.log(row);
////		console.log(row[0])
//		saveSublist(table_id, row);
////		dataToJson(table_id, row);
//	}
//}

//回滚
//function reject(table_id){
//	$('#'+table_id).datagrid('rejectChanges');
//	editIndex = undefined;
//}
////查询改变
//function getChanges(table_id){
//	var rows = $('#'+table_id).datagrid('getChanges');
//	alert(rows.length+' rows are changed!');
//}
////勾选复选框
//function onCheck(table_id,index,comboboxFields){
//	if(endEditing()){
//
//	}else{
//		$('#'+table_id).datagrid('uncheckAll');
//	}
//}

//保存回调函数
//function saveSublist(table_id,datas,comboboxFields){
//	if(datas){
//		var jsonDatas = getJsonObjectToTablelist(table_id,datas);
//		console.log(jsonDatas);
//		$.ajax({    
//			url:'/CYTSMICE_PRM_WEBSITE/lsgyszzData/sskhxx?'
//				+'sid='+$('#sid').val()
//				+'&bindid='+$('#bindid').val()
//				+'&datas='+JSON.stringify(jsonDatas)
//				+'&table_id='+table_id,
//				type:'POST',
//				async:false,  
//				success: function (data){    
//					alert(data)
//				},
//				error: function (data) {
//					alert(data)
//				}
//		});
//	}
//}

//删除回调函数
//function removeSublist(table_id,datas){
//}
//获取需存储json对象
//function getJsonObjectToTablelist(table_id,datas){
//	var arrayDatas = new Array();
//	var columns = $("#"+table_id).datagrid("getColumnFields");
//	for(i=0;i<datas.length;i++){
//		var jsonData = {};
//		for(j=0;j<columns.length;j++){
//			jsonData[columns[j]] = datas[i][columns[j]];
//		}
//		arrayDatas[i]=jsonData;
//	}
//	var jsonDatas = {};
//	jsonDatas[table_id]=arrayDatas;
//	return jsonDatas;
//}
//页面子表初始化时，将列表代码转化为中文显示
//function dmToZW(table_id,comboboxFields){
//	var tablelist = $('#'+table_id).datagrid('getRows');
//	for(i=0;i<tablelist.length;i++){
//		$('#'+table_id).datagrid('beginEdit', i);
//		for(j in comboboxFields){
//			var dm = tablelist[i][j];
//			var index;
//			for(k=0;k<comboboxFields[j][1].length;k++){
//				if(comboboxFields[j][1][k]['dm']==dm){
//					index = k;
//					break;
//				}
//			}
//			var mc = comboboxFields[j][1][index]['zwmc'];
//			tablelist[i][comboboxFields[j][0]]=mc;
//		}
//		$('#'+table_id).datagrid('endEdit', i);
//	}
//}


//function DeleteBind() {
//	var selected = $('#showBind').datagrid('getSelected');
//	if (selected) {
//		$.messager.confirm("提示", "您确定要删除此绑定吗?", function (r) {
//			if (r) {
//				$.messager.progress({ title: "删除", msg: "正在提交..." });
//				$.ajax({
//					type: "POST",
//					url: "/Customer/CanelBindCompany",
//					data: { bindId: selected.Id },
//					dataType: "json",
//					success: function (obj) {
//						$.messager.progress('close');
//
//						var selected = $('#showBind').datagrid('getSelected');
//						var index = $('#showBind').datagrid('getRowIndex', selected);
//						$('#showBind').datagrid('deleteRow', index);
//
//					},
//					error: function (text) {
//						$.messager.progress('close');
//						$.messager.alert('错误', text.statusCode);
//					}
//				});
//			}
//		});
//	}
//}