var UserCenter={
	gmtZoneWidget:null,
	
	init:function(){
		var self=this;
		self._render();
		self._bind();
		self._getUser();
	},
	_render:function(){
		var self=this;
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
		
		$("#saveBtn").unbind("click").click(function(){
			self._saveUser();
		});
	},
	_getUser:function(){
		var self=this;
		
		CommonUtils.async({
			url:"/DispatcherWeb/common/getUser.json",
			data:{},
			success:function(result){
				if(result.code==0){
					var user=result.data;
					$("#account").val(user.account);
					$("#name").val(user.name);
					$("#deptName").val(user.deptName);
					$("#roleName").val(user.roleName);
					$("#phone").val(user.phone);
					$("#email").val(user.email);
					self.gmtZoneWidget.setValue(user.gmtZone);
				}
			}
		});
	},
	_saveUser:function(){
		var self=this;
		var user={};
		var name=$("#name").val();
		if(name&&name!=""){
			user.name=name;
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
			user.email=email;
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
			url:"/DispatcherWeb/common/updateUser.json",
			data:user,
			success:function(result){
				if(result.code==0){
					MessageUtil.info("成功");
					var retUser=result.data||{};
					if(retUser.name&&retUser.name!=""){
						//更新cookie
						CommonUtils.Cookie.add(Constant.PROJECT_NAME+"_user_name",retUser.name);
						//改变页面
						$("#headerUserName").html(retUser.name);
					}
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
					}else{
						MessageUtil.alert("失败");
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
	UserCenter.init();
});