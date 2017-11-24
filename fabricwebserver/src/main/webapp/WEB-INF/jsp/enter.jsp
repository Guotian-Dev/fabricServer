<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>录入</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<!-- bootstrap 基于Jquery 注意要导入jquery包 -->
<script type="text/javascript" src="../js/jquery-1.11.1.min.js"></script>

<script type="text/javascript" src="../BootStrap/StuPersonInfo/jquery.min.js"></script>
<link href="../BootStrap/StuPersonInfo/bootstrap.min.css"
	rel="stylesheet" />
<script src="../BootStrap/StuPersonInfo/bootstrap.min.js"></script>

<link href="../BootStrap/bootstrap-3.3.5-dist/css/bootstrap.css"
	rel="stylesheet" />
<link href="../BootStrap/datagrid/css/bootstrap-table.min.css"
	rel="stylesheet" />
<link href="../BootStrap/datagrid/css/bootstrap.min.css"
	rel="stylesheet" />
<script type="text/javascript" src="../BootStrap/datagrid/js/jquery.min.js"></script>
<script type="text/javascript" src="../BootStrap/datagrid/js/jquery.base64.js"></script>
<script type="text/javascript" src="../BootStrap/datagrid/js/bootstrap-table.js"></script>
<script type="text/javascript" src="../BootStrap/datagrid/js/bootstrap-table-export.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
		$("#btnDel").click(function() {
			$(":checked").parent().parent().fadeOut("show"); //隐藏所有被选中的input元素  
			//parent() 获得当前匹配元素集合中每个元素的父元素,  

		})
		$("tr").mousemove(function() {
			$(this).css("background", "#F0F0F0"); //鼠标经过背景颜色变为灰色  

		})
		$("tr").mouseout(function() {
			$(this).css("background", "#fff"); //离开后背景颜色回复白色  
		})

		//全选  
		$("#checkAll").click(function() {

			if ($("#checkAll").attr("checked") == false) {
				$("input[name='checkbox']").each(function() {
					$(this).attr("checked", true);
				});
			} else {
				$("input[name='checkbox']").each(function() {
					$(this).attr("checked", false);
				});
			}

		});
	});
	
	function append() {
		var strAppend = '<tr style="background: rgb(255, 255, 255) none repeat scroll 0% 0%;"><td ><input type="checkbox" value="" editable="false" name="checkbox"></td><td></td><td><tr>';
		$("#AddFamily tbody").append(strAppend).editableTableWidget();
	}
	
	$(function() {
		$("#btnSave").click(function() {
			alert(1);
			var allTableData = $("#AddFamily").bootstrapTable('getData');
			console.log(allTableData);
		})
	})
</script>

<style>
	table {
		border-collapse: collapse;
		border: 1px solid #FFFFFF;
	}
	
	table td {
		text-align: center;
		height: 30px;
		font-size: 12px;
		line-height: 30px;
		border: 1px solid #efecec;
	}
</style>

</head>

<body>
	<div class="heading">
	<button id="build" type="button" class="btn  btn-success" 
		data-toggle="modal" data-target="" onclick="append()">
		<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>添加
	</button>
	
	<button id="btnSave" type="button" class="btn   btn-success">
		<span></span>保存
	</button>

<!-- 		<button id="btnEdit" type="button" class="btn   btn-warning">
			<span class="glyphicon glyphicon-edit" aria-hidden="true"></span>修改
		</button>

		<button id="btnDel" type="button" class="btn  btn-danger"
			data-toggle="modal" data-target="#DeleteForm" onclick="">
			<span class="glyphicon glyphicon-minus" aria-hidden="true"></span>删除
		</button> -->
	</div>

	<div class="widget-content padded clearfix">
		<table id="AddFamily" class="table table-bordered table-striped"
			width="1000px" border="0" cellspacing="0" cellpadding="0"
			style="margin: 0 auto">

			<thead>
				<th class="check-header hidden-xs"><input id="checkAll"
					name="checkAll" type="checkbox">
				<th>key</th>
				<th>value</th>
			</thead>
			<tbody id="mainbody">
			</tbody>
		</table>
	</div>

	<script>
		//绑定编辑、回车事件  
		$(function() {
		//   $('#build').click(build);//实现创建表格  
			$('#btnEdit').click(edit);

			$('#cells, #rows').keyup(function(e) {
				if (e.keyCode === 13) {
					//添加存入数据库的代码  
				}
			});
		});

		//将表格转成可编辑的表格  
		function edit(index) {
			//  $('#table').editableTableWidget();--效果是单击编辑按钮后，所有的都可以编辑  
			// $(":checked").editableTableWidget();           
			$(":checked").parent().parent().editableTableWidget();//整行的可以编辑  

		}
		//转成可编辑的表格  
		/*global $, window*/
		$.fn.editableTableWidget = function(options) {
			'use strict';
			return $(this)
					.each(
							function() {
								var buildDefaultOptions = function() {
									var opts = $
											.extend(
													{},
													$.fn.editableTableWidget.defaultOptions);
									opts.editor = opts.editor.clone();
									return opts;
								}, activeOptions = $.extend(
										buildDefaultOptions(), options), ARROW_LEFT = 37, ARROW_UP = 38, ARROW_RIGHT = 39, ARROW_DOWN = 40, ENTER = 13, ESC = 27, TAB = 9, element = $(this), editor = activeOptions.editor
										.css('position', 'absolute').hide()
										.appendTo(element.parent()), active, showEditor = function(
										select) {
									active = element.find('td:focus');
									if (active.length) {
										editor
												.val(active.text())
												.removeClass('error')
												.show()
												.offset(active.offset())
												.css(
														active
																.css(activeOptions.cloneProperties))
												.width(active.width()).height(
														active.height())
												.focus();
										if (select) {
											editor.select();
										}
									}
								}, setActiveText = function() {
									var text = editor.val(), evt = $
											.Event('change'), originalContent;
									if (active.text() === text
											|| editor.hasClass('error')) {
										return true;
									}
									originalContent = active.html();
									active.text(text).trigger(evt, text);
									if (evt.result === false) {
										active.html(originalContent);
									}
								}, movement = function(element, keycode) {
									if (keycode === ARROW_RIGHT) {
										return element.next('td');
									} else if (keycode === ARROW_LEFT) {
										return element.prev('td');
									} else if (keycode === ARROW_UP) {
										return element.parent().prev()
												.children().eq(element.index());
									} else if (keycode === ARROW_DOWN) {
										return element.parent().next()
												.children().eq(element.index());
									}
									return [];
								};
								editor
										.blur(function() {
											setActiveText();
											editor.hide();
										})
										.keydown(
												function(e) {
													if (e.which === ENTER) {
														setActiveText();
														editor.hide();
														active.focus();
														e.preventDefault();
														e.stopPropagation();
													} else if (e.which === ESC) {
														editor.val(active
																.text());
														e.preventDefault();
														e.stopPropagation();
														editor.hide();
														active.focus();
													} else if (e.which === TAB) {
														active.focus();
													} else if (this.selectionEnd
															- this.selectionStart === this.value.length) {
														var possibleMove = movement(
																active, e.which);
														if (possibleMove.length > 0) {
															possibleMove
																	.focus();
															e.preventDefault();
															e.stopPropagation();
														}
													}
												})
										.on('input paste', function() {
											var evt = $.Event('validate');
											active.trigger(evt, editor.val());
											if (evt.result === false) {
												editor.addClass('error');
											} else {
												editor.removeClass('error');
											}
										});
								element
										.on('click keypress dblclick',
												showEditor)
										.css('cursor', 'pointer')
										.keydown(
												function(e) {
													var prevent = true, possibleMove = movement(
															$(e.target),
															e.which);
													if (possibleMove.length > 0) {
														possibleMove.focus();
													} else if (e.which === ENTER) {
														showEditor(false);
													} else if (e.which === 17
															|| e.which === 91
															|| e.which === 93) {
														showEditor(true);
														prevent = false;
													} else {
														prevent = false;
													}
													if (prevent) {
														e.stopPropagation();
														e.preventDefault();
													}
												});

								element.find('td').prop('tabindex', 1);

								$(window)
										.on(
												'resize',
												function() {
													if (editor.is(':visible')) {
														editor
																.offset(
																		active
																				.offset())
																.width(
																		active
																				.width())
																.height(
																		active
																				.height());
													}
												});
							});

		};
		$.fn.editableTableWidget.defaultOptions = {
			cloneProperties : [ 'padding', 'padding-top', 'padding-bottom',
					'padding-left', 'padding-right', 'text-align', 'font',
					'font-size', 'font-family', 'font-weight', 'border',
					'border-top', 'border-bottom', 'border-left',
					'border-right' ],
			editor : $('<input>')
		};
	</script>

</body>
</html>
