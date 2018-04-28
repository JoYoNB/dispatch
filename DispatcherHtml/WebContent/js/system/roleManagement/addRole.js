var AddRole={
	treeWidget:null,
	deptWidget:null,
	
	init:function(){
		var self=this;
		
		self._render();
		self._bind();
	},
	_render:function(){
		var self=this;
		//设置底部菜单连接
		Footer.nav("角色管理","system/roleManagement/roleList.html");
		
		//权限树控件
		self.treeWidget=$("#treeWidget").TreeWidget({
			url:"/DispatcherWeb/user/getUserRoleAuthTree.json"
		});
		//var ids=[1,2,3,4];
		//self.treeWidget.setValue(ids);
		
		self.deptWidget=$("#deptWidget").DeptWidget({});
	},
	_bind:function(){
		var self=this;
		$("#saveBtn").unbind("click").click(function(){
			self._saveRole();
		});
	},
	_saveRole:function(){
		var self=this;
		//console.info(self.treeWidget.getValue());
		
		var name=$("#name").val();
		if(!name||name==""){
			MessageUtil.alert("角色名称不能为空");
			return;
		}
		var role={
			name:name
		};
		var remark=$("#remark").val();
		if(remark&&remark!=""){
			role.remark=remark;
		}
		var deptId=self.deptWidget.getValue();
		if(deptId&&deptId!=""){
			role.deptId=deptId;
		}
		
		var authList=self.treeWidget.getValue();
		if(!authList||authList.length<1){
			MessageUtil.alert("请先选择角色权限");
			return;
		}
		var auths=[];
		for(var i=0,len=authList.length;i<len;i++){
			var auth={
				code:authList[i]
			};
			auths.push(auth);
		}
		
		role.authList=auths;
		
		//console.info(role);
		CommonUtils.async({
			url:"/DispatcherWeb/user/addRole.json",
			contentType:"json",
			data:role,
			success:function(result){
				if(result.code==0){
					MessageUtil.info("成功",function(){
						location.href="/"+Constant.PROJECT_NAME+"/system/roleManagement/roleList.html"
					});
				}else if(result.code==10051){
					MessageUtil.alert("没有该部门权限");
				}else if(result.code==110010){
					MessageUtil.alert("角色权限不能为空");
				}else if(result.code==110011){
					MessageUtil.alert("角色权限错误");
				}else if(result.code==110012){
					MessageUtil.alert("同一部门下存在同名角色");
				}else{
					MessageUtil.alert("失败");
				}
			},
			error:function(result){
				MessageUtil.alert("失败");
			}
		});
	}
}
$(function(){
	AddRole.init();
});