var UserList={
	tableWidget:null,
	deptWidget:null,
	
	init:function(){
		var self=this;
		self._render();
		self._bind();
	},
	_render:function(){
		var self=this;
		//设置底部菜单连接
		Footer.nav("用户管理","system/userManagement/userList.html");
		//部门选择控件
		self.deptWidget=$("#deptWidget").DeptWidget({
			showAllItem:true //会多出一行 "全部" 的选择
		});
		
		self.tableWidget=$("#tableWidget").TableWidget({
			url:"/DispatcherWeb/user/getUserList.json",
			cells:[{
				field:"account",
				text:"账号"
			},{
				field:"name",
				text:"名称"
			},{
				field:"deptName",
				text:"所属部门"
			},{
				field:"phone",
				text:"手机号"
			},{
				field:"email",
				text:"邮箱"
			},{
				field:"roleName",
				text:"角色"
			},{
				field:"status",
				text:"状态",
				render:function(value,row){
					var _html='';
					if(value==1){
						_html='<span style="color: #4caf50">有效</span></div>'
					}else{
						_html='<span style="color: #f44336">失效</span></div>'
					}
					return _html;
				}
			},{
				field:"operation",
				text:"操作",
				buttons:[{
					name:"修改",
					click:function(id){
						UserList._gotoEditPage(id);
					}
				},{
					name:"删除",
					click:function(id){
						UserList.deleteUser(id);
					}
				},{
					name:"禁用",
					click:function(id){
						UserList.disabledUser(id);
					},
					render:function(row){
						var _html='';
						if(row.status==1){
							_html='禁用';
						}else{
							_html='启用';
						}
						return _html;
					}
				}]
			}],
			filterKey:"userList"
		});
		
	},
	_bind:function(){
		var self=this;
		$("#queryBtn").unbind("click").click(function(){
			var param={};
			var deptId=self.deptWidget.getValue();
			if(deptId&&deptId!=""){
				param.deptId=deptId;
			}
			var name=$("#name").val();
			if(name&&name!=""){
				param.name=name;
			}
			var phone=$("#phone").val();
			if(phone&&phone!=""){
				param.phone=phone;
			}
			
			self.tableWidget.query(param);
		});
		//删除
		$("#batchDelete").unbind("click").click(function(){
			var ids=self.tableWidget.getSelectedRowIds();
			if(ids.length==0){
				MessageUtil.alert("请先选择行");
				return;
			}
			self._batchDelete(ids);
		});
	},
	_gotoEditPage:function(id){
		if(id==1){
			MessageUtil.alert("不能编辑管理员账号");
			return;
		}
		var _url="/"+Constant.PROJECT_NAME+"/system/userManagement/editUser.html?id="+id;
		location.href=_url;
	},
	_batchDelete:function(ids){
		var self=this;
		for(var i=0,len=ids.length;i<len;i++){
			var id=ids[i];
			if(id==1){
				MessageUtil.alert("不能删除管理员账号");
				return;
			}
		}
		
		MessageUtil.confirm("是否要删除?",function(){
			CommonUtils.async({
				url:"/DispatcherWeb/user/deleteUserList.json",
				data:{id:ids},
				success:function(result){
					if(result.code==0){
						MessageUtil.info("成功",function(){
							self.tableWidget.reload();
						});
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
	deleteUser:function(id){
		var self=this;
		if(id==1){
			MessageUtil.alert("不能删除管理员账号");
			return;
		}
		MessageUtil.confirm("是否要删除?",function(){
			CommonUtils.async({
				url:"/DispatcherWeb/user/deleteUser.json",
				data:{id:id},
				success:function(result){
					if(result.code==0){
						MessageUtil.info("成功",function(){
							self.tableWidget.reload();
						});
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
	disabledUser:function(id){
		var self=this;
		if(id==1){
			MessageUtil.alert("不能禁用管理员账号");
			return;
		}
		MessageUtil.confirm("是否要执行?",function(){
			CommonUtils.async({
				url:"/DispatcherWeb/user/disabledUser.json",
				data:{id:id},
				success:function(result){
					if(result.code==0){
						MessageUtil.info("成功",function(){
							self.tableWidget.reload();
						});
					}else{
						MessageUtil.alert("失败");
					}
				},
				error:function(result){
					MessageUtil.alert("失败");
				}
			});
		});
	}
}
$(function(){
	UserList.init();
});