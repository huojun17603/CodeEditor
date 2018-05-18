<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../common/taglib.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    <title>测试页</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<jsp:include page="../common/shareJs.jsp" />

	<script type="text/javascript" src="<%=basePath%>view/admin/iTextIndex.js"></script>
	<script type="text/javascript">
		var addIText = "admin/iText/add";
		var editIText = "admin/iText/edit";
		var editstatusIText = "admin/iText/editstatus";
		var deleteIText = "admin/iText/delete";
		var ITextlist = "admin/iText/list";
	</script>

  </head>
  <body style="width:100%;height:100%;">

    <table id="datagrid"></table>
    <div id="tool" style="padding:0px;height:auto;overflow: hidden;">
   		<div style="padding: 8px 35px 8px 14px;text-shadow: 0 1px 0 rgba(255,255,255,0.5);background-color: #fcf8e3;border: 1px solid #fbeed5;-webkit-border-radius: 4px;-moz-border-radius: 4px;border-radius: 4px;color: #666;">
			<div style="line-height:30px">备注</div>
			<a href="javascript:void(0)" class="easyui-linkbutton" style="width:120px"  onclick="openAddWin()">新增</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" style="width:120px"  onclick="openEditWin()">修改</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" style="width:120px"  onclick="editStatus()">修改状态</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" style="width:120px"  onclick="deletePojo()">删除</a>
			<div class="sgtz_atn">
				<span style="font-weight: bold;">搜索筛选：</span>
				<input id="sk_searchkey" class="easyui-searchbox" data-options="prompt:'搜索关键字（TODO）',searcher:doSearch" style="width:300px"></input>
			</div> 
		</div>
	</div>

    <div id="apply_window" class="easyui-window" title="信息表单" 
		data-options="modal:true,collapsible:false,minimizable:false,maximizable:false,resizable:false,closable:false,closed:true" 
		style="width:800px;height:600px;padding:10px;">
       	<form id="apply_form">
       		<input id="id_apply_input" name="id" type="hidden">
       		<ul class="fm_s" style="overflow: inherit;">
								<li class="fm_2l">
					<label>ID：</label>
					<input name="id" class="easyui-validatebox textbox vipt" data-options="required:true">
				</li>
				<li class="fm_2l">
					<label>编码：</label>
					<input name="code" class="easyui-validatebox textbox vipt" data-options="required:true">
				</li>
				<li class="fm_2l">
					<label>名称：</label>
					<input name="name" class="easyui-validatebox textbox vipt" data-options="required:true">
				</li>
				<li class="fm_2l">
					<label>年龄：</label>
					<input name="age" class="easyui-validatebox textbox vipt" data-options="required:true">
				</li>
				<li class="fm_2l">
					<label>时间：</label>
					<input name="time" class="easyui-validatebox textbox vipt" data-options="required:true">
				</li>
				<li class="fm_2l">
					<label>余额：</label>
					<input name="balance" class="easyui-validatebox textbox vipt" data-options="required:true">
				</li>
				<li class="fm_2l">
					<label>现金：</label>
					<input name="money" class="easyui-validatebox textbox vipt" data-options="required:true">
				</li>
				<li class="fm_2l">
					<label>时间戳：</label>
					<input name="stamp" class="easyui-validatebox textbox vipt" data-options="required:true">
				</li>
				<li class="fm_2l">
					<label>性别：</label>
					<input name="six" class="easyui-validatebox textbox vipt" data-options="required:true">
				</li>
				<li class="fm_2l">
					<label>生日：</label>
					<input name="birthbay" class="easyui-validatebox textbox vipt" data-options="required:true">
				</li>

				<li class="fm_1l" style="text-align: center;padding-top: 20px">
					<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-ok"  style="width:100px"  onclick="apply()">确认</a>
					<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel"  style="width:100px"  onclick="closeWin()">取消</a>
				</li>
			</ul>
       	</form>
    </div>
    

  </body>
</html>
