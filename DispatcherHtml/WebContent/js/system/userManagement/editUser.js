var EditUser={
	id:null,
	deptWidget:null,
	roleWidget:null,
	gmtZoneWidget:null,
	
	init:function(){
		var self=this;
		self.id=CommonUtils.getParam("id");
		self._getUser();
		self._render();
		self._bind();
	},
	_render:function(){
		var self=this;
		//设置底部菜单连接
		Footer.nav("用户管理","system/userManagement/userList.html");
		self.roleWidget=$("#roleWidget").ComboBoxWidget({
			textField:"name",
			valueField:"id",
			showText:"请选择角色",
			width:228
		});
		
		self.deptWidget=$("#deptWidget").DeptWidget({
			afterSelected:function(value,text){
				//alert(value+"  "+text);
				//级联查询角色信息
				CommonUtils.async({
					url:"/DispatcherWeb/user/getRoleList.json",
					data:{deptId:value,pageNum:1,pageSize:1000},
					success:function(result){
						if(result.code==0){
							var ret=result.data||{};
							var list=ret.list||[];
							self.roleWidget.loadData(list);
						}
					}
				});
			}
		});
		
		self.gmtZoneWidget=$("#gmtZoneWidget").ComboBoxWidget({
			url:"/js/ui/timeZone_zh.json",
			type:"GET",
			textField:"text",
			valueField:"data",
			showText:"请选择时区",
			width:228
		});
	},
	_bind:function(){
		var self=this;
		var self=this;
		$("#saveBtn").unbind("click").click(function(){
			self._saveUser();
		});
	},
	_getUser:function(){
		var self=this;
		
		CommonUtils.async({
			url:"/DispatcherWeb/user/getUser.json",
			data:{id:self.id},
			success:function(result){
				if(result.code==0){
					var user=result.data;
					$("#account").val(user.account);
					$("#name").val(user.name);
					$("#phone").val(user.phone);
					$("#email").val(user.email);
					
					var deptName=user.deptName;
					self.deptWidget.setText(deptName);
					//加载部门角色
					CommonUtils.async({
						url:"/DispatcherWeb/user/getRoleList.json",
						data:{deptId:user.deptId,pageNum:1,pageSize:1000},
						success:function(result){
							if(result.code==0){
								var ret=result.data||{};
								var list=ret.list||[];
								self.roleWidget.loadData(list);
								
								var roleId=user.roleId;
								self.roleWidget.setValue(roleId);
							}
						}
					});
					
					
					var gmtZone=user.gmtZone;
					self.gmtZoneWidget.setValue(gmtZone);
				}
			}
		});
		
	},
	_saveUser:function(){
		var self=this;
		var user={id:self.id};
		var name=$("#name").val();
		if(name&&name!=""){
			user.name=name;
		}
		var deptId=self.deptWidget.getValue();
		if(deptId&&deptId!=""){
			user.deptId=deptId;
		}
		var roleId=self.roleWidget.getValue();
		if(roleId&&roleId!=""){
			user.roleId=roleId;
		}
		var phone=$("#phone").val();
		if(phone&&phone!=""){
			if(!CommonUtils.Validate.phone(phone)){
				MessageUtil.alert("手机号格式不正确");
				return;
			}
			user.phone=phone;
		}
		var email=$("#email").val();
		if(email&&email!=""){
			if(!CommonUtils.Validate.email(email)){
				MessageUtil.alert("邮箱格式不正确");
				return;
			}
			user.email=email;
		}
		var gmtZone=self.gmtZoneWidget.getValue();
		if(gmtZone&&gmtZone!=""){
			user.gmtZone=gmtZone;
		}
		
		CommonUtils.async({
			url:"/DispatcherWeb/user/updateUser.json",
			data:user,
			success:function(result){
				if(result.code==0){
					MessageUtil.info("成功",function(){
						location.href="/"+Constant.PROJECT_NAME+"/system/userManagement/userList.html";
					});
				}else if(result.code==10005){
					//超出最大长度
					var msg=result.msg;
					if("name"==msg){
						MessageUtil.alert("名字超过最大长度100限制");
					}else if("account"==msg){
						MessageUtil.alert("账号超过最大长度50限制");
					}else if("phone"==msg){
						MessageUtil.alert("手机超过最大长度20限制");
					}else if("email"==msg){
						MessageUtil.alert("邮箱超过最大长度100限制");
					}
				}else if(result.code==110013){
					MessageUtil.alert("已经存在相同账号");
				}else if(result.code==110014){
					MessageUtil.alert("已经存在用户邮箱");
				}else if(result.code==110015){
					MessageUtil.alert("已经存在用户手机号");
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
	EditUser.init();
});