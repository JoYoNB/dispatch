var CustomerList={
	tableWidget:null,//表格控件
	
	init:function(){
		var self=this;
		self._render();
		self._bind();
	},
	_render:function(){
		var self=this;
		
		//设置底部菜单连接
		Footer.nav("客户管理","settlement/customerManagement/customerList.html");
		
		//权限判断
		if(CommonUtils.hasAuth("addCustomer")){
			$("#addBtn").removeClass("hide");
		}
		if(CommonUtils.hasAuth("deleteCustomer")){
			$("#batchDelete").removeClass("hide");
		}
		
		self.tableWidget=$("#tableWidget").TableWidget({
			url:"/DispatcherWeb/customer/list.json",
			para:{},
			cells:[{
				field:"name",
				text:"客户名称"
			},{
				field:"role",
				text:"角色",
				render:function(value,row){
					if(value==2){
						return "货主";
					}else if(value==3){
						return "承运商";
					}else if(value==4){
						return "货主&承运商"
					}
					return areaName;
				}
			},{
				field:"contacter",
				text:"联系人"
			},{
				field:"phone",
				text:"联系电话"
			},{
				field:"createTime",
				text:"加入时间"
			},{
				field:"operation",
				text:"操作",
				buttons:[{
					name:"编辑",
					authCode:"updateCustomer",
					click:function(id){
						CustomerList._gotoEditPage(id);
					}
				},{
					name:"删除",
					authCode:"deleteCustomer",
					click:function(id){
						CustomerList._deleteOne(id);
					}
				}]
			}]
			//pageSize:10//不填写默认是20
		});
		
	},
	_bind:function(){
		self=this;
		//删除按钮
		$("#batchDelete").unbind("click").click(function(){
			//MessageUtil.alert("你好");
			MessageUtil.confirm("是否要删除?",function(){
				//删除动作
				self._deleteBatch();
			});
		});
		
		$("#queryBtn").unbind("click").click(function(){
			var name=$("#name").val();
			var role= $("#role").val();
			if(role<2||role>4){
				role=null;
			}
			console.info(name);
			console.info(role);
			var param={
				name:name,
				role:role
			}
			//重新给条件查询
			self.tableWidget.query(param);
		});
	},
	_deleteBatch:function(){
		var self=this;
		var ids=self.tableWidget.getSelectedRowIds();
		if(!ids||ids.length==0){
			MessageUtil.alert("请先选择行");
			return;
		}
		
		CommonUtils.async({
			url:"/DispatcherWeb/customer/delete.json",
			data:{id:ids},
			success:function(result){
				if(result.code==0){
					MessageUtil.info("成功",function(){
						//删除后重新加载
						self.tableWidget.reload();
					});
				}else if(result.code==10051){
					MessageUtil.alert("没有该部门操作权限");
				}else if(result.code==110005){
					MessageUtil.alert("客户还存在子部门");
				}else if(result.code==110006){
					MessageUtil.alert("客户还有关联角色");
				}else if(result.code==110007){
					MessageUtil.alert("客户还有关联用户");
				}else if(result.code==110008){
					MessageUtil.alert("客户还有关联车辆");
				}else if(result.code==110009){
					MessageUtil.alert("客户还有关联司机");
				}else{
					MessageUtil.alert("失败");
				}
			},
			error:function(result){
				MessageUtil.alert("失败");
			}
		});
	},
	_deleteOne:function(id){
		var self=this;
		
		MessageUtil.confirm("是否要删除?",function(){
			//删除动作
			CommonUtils.async({
				url:"/DispatcherWeb/customer/delete.json",
				data:{id:[id]},
				success:function(result){
					if(result.code==0){
						MessageUtil.info("成功",function(){
							//删除后重新加载
							self.tableWidget.reload();
						});
					}else if(result.code==10051){
						MessageUtil.alert("没有该部门操作权限");
					}else if(result.code==110005){
						MessageUtil.alert("客户还存在子部门");
					}else if(result.code==110006){
						MessageUtil.alert("客户还有关联角色");
					}else if(result.code==110007){
						MessageUtil.alert("客户还有关联用户");
					}else if(result.code==110008){
						MessageUtil.alert("客户还有关联车辆");
					}else if(result.code==110009){
						MessageUtil.alert("客户还有关联司机");
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
	_gotoEditPage:function(id){
		var _url="/"+Constant.PROJECT_NAME+"/settlement/customerManagement/customerUpdate.html?id="+id;
		location.href=_url;
	}
}
$(function(){
	CustomerList.init();
});