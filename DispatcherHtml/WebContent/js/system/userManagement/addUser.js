var AddUser={
	deptWidget:null,
	roleWidget:null,
	gmtZoneWidget:null,
	
	init:function(){
		var self=this;
		
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
		//默认选中东八区
		self.gmtZoneWidget.setValue("utc+0800000");
	},
	_bind:function(){
		var self=this;
		$("#saveBtn").unbind("click").click(function(){
			self._saveUser();
		});
	},
	_saveUser:function(){
		var self=this;
		
		var account=$("#account").val();
		if(!account||account==""){
			MessageUtil.alert("用户账号不能为空");
			return;
		}
		var name=$("#name").val();
		if(!name||name==""){
			MessageUtil.alert("用户名不能为空");
			return;
		}
		var deptId=self.deptWidget.getValue();
		if(!deptId||deptId==""){
			MessageUtil.alert("请先选择部门");
			return;
		}
		var roleId=self.roleWidget.getValue();
		if(!roleId||roleId==""){
			MessageUtil.alert("请先选择角色");
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
		var gmtZone=self.gmtZoneWidget.getValue();
		if(!gmtZone||gmtZone==""){
			MessageUtil.alert("请先选择时区");
			return;
		}
		
		var user={
			account:account,
			name:name,
			deptId:deptId,
			roleId:roleId,
			phone:phone,
			email:email,
			gmtZone:gmtZone
		}
		
		CommonUtils.async({
			url:"/DispatcherWeb/user/addUser.json",
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
	AddUser.init();
});