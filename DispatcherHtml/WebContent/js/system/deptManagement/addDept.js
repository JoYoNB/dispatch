var AddDept={
	deptWidget:null,
	
	init:function(){
		var self=this;
		
		self._render();
		self._bind();
	},
	_render:function(){
		var self=this;
		//设置底部菜单连接
		Footer.nav("部门管理","system/deptManagement/deptList.html");
		self.deptWidget=$("#deptWidget").DeptWidget({});
	},
	_bind:function(){
		var self=this;
		
		$("#saveBtn").unbind("click").click(function(){
			self._saveDept();
		});
	},
	_saveDept:function(){
		var self=this;
		
		var name=$("#name").val();
		if(!name||name==""){
			MessageUtil.alert("部门名称不能为空");
			return;
		}
		var contacter=$("#contacter").val();
		if(!contacter||contacter==""){
			MessageUtil.alert("部门联系人不能为空");
			return;
		}
		var phone=$("#phone").val();
		if(!phone||phone==""){
			MessageUtil.alert("手机号不能为空");
			return;
		}else if(!CommonUtils.Validate.phone(phone)){
			MessageUtil.alert("手机号格式不正确");
			return;
		}
		var email=$("#email").val();
		if(!email||email==""){
			MessageUtil.alert("邮箱不能为空");
			return;
		}else if(!CommonUtils.Validate.email(email)){
			MessageUtil.alert("邮箱格式不正确");
			return;
		}
		
		var dept={
			name:name,
			contacter:contacter,
			phone:phone,
			email:email
			//isCarrier:true
		}
		var parentId=self.deptWidget.getValue();
		if(parentId&&parentId!=""){
			dept.parentId=parentId;
		}
		
		//JSON.stringify(data)
		CommonUtils.async({
			url:"/DispatcherWeb/user/addDept.json",
			//contentType:"json",
			data:dept,
			success:function(result){
				if(result.code==0){
					MessageUtil.info("成功",function(){
						location.href="/"+Constant.PROJECT_NAME+"/system/deptManagement/deptList.html";
					});
				}else if(result.code==10051){
					//没有改部门权限
					MessageUtil.alert("没有该父级部门权限");
				}else if(result.code==110000){
					//没有权限新建承运商/货主部门
					MessageUtil.alert("没有权限新建承运商/货主部门");
				}else if(result.code==110001){
					MessageUtil.alert("同级部门存在同名部门");
				}else if(result.code==110002){
					MessageUtil.alert("部门联系人手机号已经存在");
				}else if(result.code==110003){
					MessageUtil.alert("部门联系人邮箱已经存在");
				}else if(result.code==110004){
					MessageUtil.alert("添加团车平台部门失败");
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
	AddDept.init();
});