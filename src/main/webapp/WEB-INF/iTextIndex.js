$(function(){
	$("#datagrid").datagrid({
		url:basePath + ITextlist,
		border:false,
		striped:true,
	    fit:true,
	    pageSize:50,
	    pageList:[50,100],
	    idField:'id',
	    loadMsg:'加载中……',
	    rownumbers:true,//序号
	    pagination:true,//显示底部分页工具栏
	    singleSelect:true,//单选
	    columns:[[
		      {field:'id',title:'ID',align:"center",width:200},
		      {field:'code',title:'编码',align:"center",width:200},
		      {field:'name',title:'名称',align:"center",width:200},
		      {field:'age',title:'年龄',align:"center",width:200},
		      {field:'time',title:'时间',align:"center",width:200},
		      {field:'balance',title:'余额',align:"center",width:200},
		      {field:'money',title:'现金',align:"center",width:200},
		      {field:'stamp',title:'时间戳',align:"center",width:200},
		      {field:'six',title:'性别',align:"center",width:200},
		      {field:'birthbay',title:'生日',align:"center",width:200}
		]],
		toolbar:'#tool',
		onLoadSuccess:function(){
			$("#datagrid").datagrid('scrollTo',0);
		}
	});
});

var applyUrl = "";

function openAddWin(){
	$('#apply_form').form("clear");
	applyUrl = addIText;
	$('#apply_window').window('open');
	$('#apply_window').window('center');	
}

function openEditWin(){
	var row = $("#datagrid").datagrid("getSelected");
	if(isEmpty(row)){
		$.messager.alert("提示框","请选择一条记录！");
		return;
	}
	$('#apply_form').form("clear");
	$("#apply_form").form("load",{
		id:row.id,
		code:row.code,
		name:row.name,
		age:row.age,
		time:row.time,
		balance:row.balance,
		money:row.money,
		stamp:row.stamp,
		six:row.six,
		birthbay:row.birthbay
	});
	applyUrl = editIText;
	$('#apply_window').window('open');
	$('#apply_window').window('center');	
}

function closeWin(){
	$("#apply_window").window('close');
}

function apply(){
	$.ajax({
        url: basePath+applyUrl,
        data: $('#apply_form').serialize(),
        success: function(data){
            if(data.status==0){
            	$('#datagrid').datagrid('reload');
            	$('#apply_window').window('close');
            	$.messager.alert("提示框","完成操作请求！");
            }else if(data.status==1){
            	$.messager.alert("错误",data.msg,'warning');
            }else if(data.status==3){
            	$.messager.alert("警告","无权访问",'warning');
            }
        }
    });
}

function editStatus(){
	var row = $('#datagrid').datagrid('getSelected');
	if(isEmpty(row)){
		$.messager.alert("提示框","请选择一条数据记录！");
		return;
	}
	var prompt = row.status==1?"注销":"启用";
	var status = row.status==1?0:1;
	$.messager.confirm('禁用提示', '你确定要'+prompt+'吗？', function(r){
		if(r){
			$.ajax({
				url:basePath+editstatusIText,
				data:{"id":row.id,"status":status},
				success: function (data) {
	            	if(data.status==0){
	            		$('#datagrid').datagrid('reload');
						$.messager.alert("提示框","完成操作请求！");
		            }else if(data.status==1){
		            	$.messager.alert("错误",data.msg,'warning');
		            }else if(data.status==3){
		            	$.messager.alert("警告","无权访问",'warning');
		            }
	            }
			});
		}
	});
}

function deletePojo(){
	var row = $('#datagrid').datagrid('getSelected');
	if(isEmpty(row)){
		$.messager.alert("提示框","请选择一条数据记录！");
		return;
	}
	$.messager.confirm('禁用提示', '你确定要删除吗？', function(r){
		if(r){
			$.ajax({
				url:basePath+deleteIText,
				data:{"id":row.id},
				success: function (data) {
	            	if(data.status==0){
	            		$('#datagrid').datagrid('reload');
						$.messager.alert("提示框","完成操作请求！");
		            }else if(data.status==1){
		            	$.messager.alert("错误",data.msg,'warning');
		            }else if(data.status==3){
		            	$.messager.alert("警告","无权访问",'warning');
		            }
	            }
			});
		}
	});
}

function doSearch(){
	var searchkey = $("#sk_searchkey").val();
	$("#datagrid").datagrid("reload",{searchkey:searchkey});
}
