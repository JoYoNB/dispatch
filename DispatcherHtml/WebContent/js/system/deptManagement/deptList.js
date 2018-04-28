var DeptList={
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
		Footer.nav("部门管理","system/deptManagement/deptList.html");
		
		self.deptWidget=$("#deptWidget").DeptWidget({
			showAllItem:true //会多出一行 "全部" 的选择
		});
		
		self.tableWidget=$("#tableWidget").TableWidget({
			url:"/DispatcherWeb/user/getDeptList.json",
			para:{},
			extParam:"userDeptLevel",//额外的参数，除了total，list以外的参数
			cells:[{
				field:"name",
				text:"部门名称",
				style:"text-align: left;",
				render:function(value,row){
					var _html=value||"";
					var userDeptLevel=row.userDeptLevel*1;//用户本身部门级别
					var level=row.level*1;//此部门级别
					//console.info(level);
					level=level-userDeptLevel+1;
					
					var levelIcon='';
					if(level>1){
						var _l=level-1;
						levelIcon='<i class="icon dept_level_icon icon_dept0'+_l+'"></i>'
						_html=levelIcon+value;
					}
					return _html;
				}
			},{
				field:"parentName",
				text:"上级部门",
				render:function(value,row){
					var _html=value||"";
					//如果此机构和他本身机构级别一致，则不显示上级机构
					if(row.userDeptLevel==row.level){
						_html="";
					}
					return _html;
				}
			},{
				field:"phone",
				text:"电话"
			},{
				field:"contacter",
				text:"联系人"
			},{
				field:"operation",
				text:"操作",
				buttons:[{
					name:"修改",
					click:function(id){
						DeptList._gotoEditPage(id);
					}
				},{
					name:"删除",
					click:function(id){
						DeptList.deleteDept(id);
					}
				}]
			}],
			filterKey:"deptList"
			//pageSize:10//不填写默认是20
		});
		
	},
	_gotoEditPage:function(id){
		var _url="/"+Constant.PROJECT_NAME+"/system/deptManagement/editDept.html?id="+id;
		location.href=_url;
	},
	_bind:function(){
		self=this;
		//删除按钮
		$("#batchDelete").unbind("click").click(function(){
			//MessageUtil.alert("你好");
			MessageUtil.confirm("是否要删除?",function(){
				//删除动作
				self._deleteDeptList();
				
			});
		});
		
		$("#queryBtn").unbind("click").click(function(){
			var deptName=$("#deptName").val();
			var contacter=$("#contacter").val();
			var phone=$("#phone").val();
			var param={
				name:deptName,
				contacter:contacter,
				phone:phone
			}
			var deptId=self.deptWidget.getValue();
			if(deptId&&deptId!=""){
				param.deptId=deptId;
			}
			//重新给条件查询
			self.tableWidget.query(param);
		});
	},
	_deleteDeptList:function(){
		var self=this;
		var ids=self.tableWidget.getSelectedRowIds();
		if(!ids||ids.length==0){
			MessageUtil.alert("请先选择行");
			return;
		}
		
		CommonUtils.async({
			url:"/DispatcherWeb/user/deleteDeptList.json",
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
					MessageUtil.alert("部门还存在子部门");
				}else if(result.code==110006){
					MessageUtil.alert("部门还关联着角色");
				}else if(result.code==110007){
					MessageUtil.alert("部门还关联着用户");
				}else if(result.code==110008){
					MessageUtil.alert("部门还关联着车辆");
				}else if(result.code==110009){
					MessageUtil.alert("部门还关联着司机");
				}else{
					MessageUtil.alert("失败");
				}
			},
			error:function(result){
				MessageUtil.alert("失败");
			}
		});
	},
	deleteDept:function(id){
		var self=this;
		
		MessageUtil.confirm("是否要删除?",function(){
			//删除动作
			CommonUtils.async({
				url:"/DispatcherWeb/user/deleteDept.json",
				data:{id:id},
				success:function(result){
					if(result.code==0){
						MessageUtil.info("成功",function(){
							//删除后重新加载
							self.tableWidget.reload();
						});
					}else if(result.code==10051){
						MessageUtil.alert("没有该部门操作权限");
					}else if(result.code==110005){
						MessageUtil.alert("部门还存在子部门");
					}else if(result.code==110006){
						MessageUtil.alert("部门还关联着角色");
					}else if(result.code==110007){
						MessageUtil.alert("部门还关联着用户");
					}else if(result.code==110008){
						MessageUtil.alert("部门还关联着车辆");
					}else if(result.code==110009){
						MessageUtil.alert("部门还关联着司机");
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
	DeptList.init();
});