var RoleList={
	tableWidget:null,//表格控件
	deptWidget:null,//部门选择控件
	
	init:function(){
		var self=this;
		
		self._render();
		self._bind();
	},
	_render:function(){
		var self=this;
		//设置底部菜单连接
		Footer.nav("角色管理","system/roleManagement/roleList.html");
		//部门选择控件
		self.deptWidget=$("#deptWidget").DeptWidget({
			showAllItem:true //会多出一行 "全部" 的选择
		});
		
		self.tableWidget=$("#tableWidget").TableWidget({
			url:"/DispatcherWeb/user/getRoleList.json",
			cells:[{
				field:"name",
				text:"角色名称"
			},{
				field:"deptName",
				text:"所属部门"
			},{
				field:"remark",
				text:"备注"
			},{
				field:"operation",
				text:"操作",
				buttons:[{
					name:"修改",
					click:function(id){
						RoleList._gotoEditPage(id);
					}
				},{
					name:"删除",
					click:function(id){
						RoleList.deleteRole(id);
					}
				}]
			}],
			filterKey:"roleList"
		});
		
		
		
	},
	_gotoEditPage:function(id){
		var _url="/"+Constant.PROJECT_NAME+"/system/roleManagement/editRole.html?id="+id;
		location.href=_url;
	},
	_bind:function(){
		var self=this;
		
		$("#queryBtn").unbind("click").click(function(){
			var param={};
			var deptId=self.deptWidget.getValue();
			var roleName=$("#roleName").val();
			if(deptId&&deptId!=""){
				param.deptId=deptId;
			}
			if(roleName&&roleName!=""){
				param.name=roleName;
			}
			
			self.tableWidget.query(param);
		});
		//批量删除
		$("#batchDelete").unbind("click").click(function(){
			//MessageUtil.alert("你好");
			MessageUtil.confirm("是否要删除?",function(){
				//删除动作
				self._deleteRoleList();
			});
		});
	},
	deleteRole:function(id){
		var self=this;
		
		MessageUtil.confirm("是否要删除?",function(){
			//删除动作
			CommonUtils.async({
				url:"/DispatcherWeb/user/deleteRole.json",
				data:{id:id},
				success:function(result){
					if(result.code==0){
						MessageUtil.info("成功",function(){
							//删除后重新加载
							self.tableWidget.reload();
						});
					}else if(result.code==10052){
						MessageUtil.alert("没有该角色权限");
					}else{
						MessageUtil.alert("失败");
					}
				},
				error:function(result){
					MessageUtil.alert("失败");
				}
			});
			
		});
	},
	_deleteRoleList:function(){
		var self=this;
		var ids=self.tableWidget.getSelectedRowIds();
		if(!ids||ids.length==0){
			MessageUtil.alert("请先选择行");
			return;
		}
		
		CommonUtils.async({
			url:"/DispatcherWeb/user/deleteRoleList.json",
			data:{id:ids},
			success:function(result){
				if(result.code==0){
					MessageUtil.info("成功",function(){
						//删除后重新加载
						self.tableWidget.reload();
					});
				}else if(result.code==10052){
					MessageUtil.alert("没有该角色权限");
				}else{
					MessageUtil.alert("失败");
				}
			},
			error:function(result){
				MessageUtil.alert("失败");
			}
		});
	},
}
$(function(){
	RoleList.init();
});