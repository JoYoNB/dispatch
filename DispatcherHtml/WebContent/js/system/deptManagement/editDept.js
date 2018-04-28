var EditDept={
	id:null,
	
	init:function(){
		var self=this;
		self.id=CommonUtils.getParam("id");
		self._render();
		self._bind();
		self._getDept();
	},
	_render:function(){
		var self=this;
		//设置底部菜单连接
		Footer.nav("部门管理","system/deptManagement/deptList.html");
		
	},
	_bind:function(){
		var self=this;
		$("#saveBtn").unbind("click").click(function(){
			self._saveDept();
		});
		
	},
	_getDept:function(){
		var self=this;
		CommonUtils.async({
			url:"/DispatcherWeb/user/getDept.json",
			data:{id:self.id},
			success:function(result){
				if(result.code==0){
					var dept=result.data||{};
					$("#name").val(dept.name);
					$("#contacter").val(dept.contacter);
					$("#phone").val(dept.phone);
					$("#email").val(dept.email);
					$("#parentName").val(dept.parentName);
				}
			}
		});
	},
	_saveDept:function(){
		var self=this;
		
		var dept={id:self.id};
		
		var name=$("#name").val();
		if(name&&name!=""){
			dept.name=name;
		}
		var contacter=$("#contacter").val();
		if(contacter&&contacter!=""){
			dept.contacter=contacter;
		}
		var phone=$("#phone").val();
		if(phone&&phone!=""){
			if(!CommonUtils.Validate.phone(phone)){
				MessageUtil.alert("手机号格式不正确");
				return;
			}
			dept.phone=phone;
		}
		var email=$("#email").val();
		if(email&&email!=""){
			if(!CommonUtils.Validate.email(email)){
				MessageUtil.alert("邮箱格式不正确");
				return;
			}
			dept.email=email;
		}
		
		CommonUtils.async({
			url:"/DispatcherWeb/user/updateDept.json",
			data:dept,
			success:function(result){
				if(result.code==0){
					MessageUtil.info("成功",function(){
						//返回列表
						location.href="/"+Constant.PROJECT_NAME+"/system/deptManagement/deptList.html";
					});
				}else if(result.code==10051){
					//没有改部门权限
					MessageUtil.alert("没有该父级部门权限");
				}else if(result.code==110000){
					//没有权限新建承运商/货主部门
					MessageUtil.alert("没有权限修改承运商/货主部门");
				}else if(result.code==110001){
					MessageUtil.alert("同级部门存在同名部门");
				}else if(result.code==110002){
					MessageUtil.alert("部门联系人手机号已经存在");
				}else if(result.code==110003){
					MessageUtil.alert("部门联系人邮箱已经存在");
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
	EditDept.init();
});