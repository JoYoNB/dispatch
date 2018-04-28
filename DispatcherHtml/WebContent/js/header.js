var Header={
	init:function(){
		var self=this;
		
		self._render();
		self._bind();
	},
	_render:function(){
		var self=this;
		//自动填写用户名
		var userName=CommonUtils.Cookie.get(Constant.PROJECT_NAME+"_user_name");
		if(userName&&userName!=""){
			userName=unescape(userName);
		}
		$("#headerUserName").html(userName);
		//展示菜单
		var roleCode=CommonUtils.Cookie.get(Constant.PROJECT_NAME+"_user_role");
		var auths=CommonUtils.Cookie.get(Constant.PROJECT_NAME+"_user_authList");
		var authList=auths.split("%2C");
		//console.info(authList);
		self._initMenu(roleCode,authList);
		self._initIndexNav(roleCode);
	},
	_bind:function(){
		var self=this;
		$("#logoutBtn").click(function(){
			MessageUtil.confirm("是否要退出?",function(){
				CommonUtils.async({
					url:"/DispatcherWeb/common/logout.json",
					data:{},
					success:function(result){
						if(result.code==0){
							//清除登录的cookie
							CommonUtils.Cookie.del(Constant.PROJECT_NAME+"_token");
							CommonUtils.Cookie.del(Constant.PROJECT_NAME+"_user_name");
							CommonUtils.Cookie.del(Constant.PROJECT_NAME+"_user_id");
							CommonUtils.Cookie.del(Constant.PROJECT_NAME+"_user_role");
							CommonUtils.Cookie.del(Constant.PROJECT_NAME+"_user_authList");
							
							setTimeout(function(){
								location.href="/"+Constant.PROJECT_NAME+"/login.html";
							},500);
						}
					}
				});
			});
		});
		//修改密码
		$("#updatePasswordBtn").unbind("click").click(function(){
			var win=WinWidget.create({
				title:"修改密码",
				//width:"500px",
				height:"300px",
				//content:"Hello World"
				url:"/dsp/updatePasswordWin.html"
			});
			//updatePasswordWin 必须是全页面唯一，要不然在弹出的窗口页面，自定义关闭按钮时，会有问题
			window.updatePasswordWin=win;
			
			
		});
	},
	_initMenu:function(roleCode,list){
		var self=this;
		for(var i=0,len=list.length;i<len;i++){
			//货主/承运商用户不能看到结算后台的业务菜单
			$("#menu_"+list[i]).removeClass("hide");
			if("admin"==roleCode||roleCode.indexOf("common")>-1){
				//admin和普通用户是看不到货主/承运商的业务菜单的
				//显示结算后台首页菜单
				$("#menu_siteDataManagement").addClass("hide");
				$("#index_consignor").addClass("hide");
				$("#menu_siteManagement").addClass("hide");
				$("#menu_vehicleManagement").addClass("hide");
				$("#menu_driverManagement").addClass("hide");
			}else if(roleCode.indexOf("consignor")>-1){
				//货主
				$("#menu_customerManagement").addClass("hide");
				$("#menu_transportDataManagement").addClass("hide");
				$("#menu_chargeRuleManagement").addClass("hide");
				
			}else if(roleCode.indexOf("carrier")>-1){
				//承运商
				$("#menu_customerManagement").addClass("hide");
				$("#menu_transportDataManagement").addClass("hide");
				$("#menu_chargeRuleManagement").addClass("hide");
				
				$("#menu_siteManagement").addClass("hide");
			}else if(roleCode.indexOf("dual")>-1){
				//货主/承运商
				$("#menu_customerManagement").addClass("hide");
				$("#menu_transportDataManagement").addClass("hide");
				$("#menu_chargeRuleManagement").addClass("hide");
			}
		}
		
		
		
	},
	_initIndexNav:function(roleCode){
		var self=this;
		if("admin"==roleCode||roleCode.indexOf("common")>-1){
			//后台用户
			$("#index_settlement").children(":first").attr("href","/"+Constant.PROJECT_NAME+"/settlement/index.html");
		}else if(roleCode.indexOf("consignor")>-1){
			//货主
			$("#index_settlement").children(":first").attr("href","/"+Constant.PROJECT_NAME+"/consignor/index.html");
		}else if(roleCode.indexOf("carrier")>-1){
			//承运商
			$("#index_settlement").children(":first").attr("href","/"+Constant.PROJECT_NAME+"/carrier/index.html");
		}else if(roleCode.indexOf("dual")>-1){
			//货主/承运商
			$("#index_settlement").children(":first").attr("href","/"+Constant.PROJECT_NAME+"/carrier/index.html");
		}
	}
}
$(function(){
	Header.init();
});