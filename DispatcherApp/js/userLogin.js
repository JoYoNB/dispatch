
mui.init({
	swipeBack: false //禁用右滑关闭功能
});
//初始化区域滚动
mui('.mui-scroll-wrapper').scroll();

mui.plusReady(function() {
	var AutoLogin = localStorage.getItem(comDataIdent + "AutoLogin");
	var token = localStorage.getItem(comDataIdent + "token");
	var showGuide = localStorage.getItem(comDataIdent + "lauchFlag");
	var self = plus.webview.currentWebview();
	var accountBox = document.getElementById('account');
	var passwordBox = document.getElementById('password');
	var toMain = function() { //定义main.html转场函数
		utils.openView({
			url:"index.html",
			id:"index.html"
		});
	};
	// 关闭侧滑返回功能
	self.setStyle({
		'popGesture': 'none'
	});
	if (token) { //自动登录时
		console.log(new Date().getTime() + "|login:自动登录:" + token);
		toMain();
	} else {
		var account = localStorage.getItem(comDataIdent +"account"); 
		var password = localStorage.getItem(comDataIdent +"password");      
		if (account != null && password != null) {           
			document.getElementById('checkbox').checked = true;           
			document.getElementById('password').value = password;
			document.getElementById('account').value = account;       
		}
		
		if (showGuide) {
			self.show();
			console.log(new Date().getTime() + "|login:显示登录界面");
		}
		//关闭启动界面
		plus.navigator.closeSplashscreen();
		if(plus.navigator.hasSplashscreen()){
			plus.navigator.closeSplashscreen();
		}
	}
	//切换记住密码状态
	mui('#autoLogin').on('change', 'input', function() {
		var isAlogin = this.checked ? true : false;
		AutoLogin.stat = isAlogin;
		localStorage.getItem(comDataIdent + "AutoLogin", AutoLogin);
		console.log(new Date().getTime() + "|login:点击自动登录:" + localStorage.getItem(comDataIdent + "AutoLogin").stat);
	});

	//监听账号输入框的输入操作
	var _a = localStorage.getItem(comDataIdent + "account");
	var _p = localStorage.getItem(comDataIdent + "password"); 
	document.getElementById("account").addEventListener('input',function(){
		var _v=this.value;
		//如果手机号和缓存的不一样,则去掉密码框密码和记住密码勾选
		if(_a && _a!="" &&_v==_a){
			document.getElementById('checkbox').checked = true; 
			document.getElementById('password').value = _p;
		}else{
			document.getElementById('checkbox').checked = false; 
			document.getElementById('password').value = "";
		}
	});
	
	document.getElementById('login-btn').addEventListener('tap', function(event) { //登录按钮点击事件
		var clientInfo = plus.push.getClientInfo();
		console.log(new Date().getTime() + "|login:点击登录按钮");
		plus.nativeUI.showWaiting('', waitingStyle);
	    if (!accountBox.value.length || !passwordBox.value.length) {
			plus.nativeUI.closeWaiting();
			plus.nativeUI.toast("用户名密码不能为空.");
			return;
		} else {
			var _password=Trim(passwordBox.value);
		     getpassword=SHA256(_password);
			var loginInfo = {
				account: accountBox.value,
				password: getpassword,
				//sign:localStorage.getItem("sign"),
				// language:localStorage.getItem("language"),
				//clientId: clientInfo.clientid,
				//appId: clientInfo.appid
			};

			console.log("loginInfo: "+JSON.stringify(loginInfo));
			console.log(httpServer);
			utils.ajaxFn({
				url:httpServer+"/DispatcherAppWeb/common/login.json",
				data:loginInfo,
				timeout: 30000,
				success:function(result){
					var data = result;
					plus.nativeUI.closeWaiting();
					if (data != '') {
						if(data.code == "10025"){
							plus.nativeUI.toast("用户不存在");
						}
						if(data.code == '10050'){
							plus.nativeUI.toast("用户已被锁定,请稍候再试");
							return;
						} 
						if (data.code == '1005') {
							plus.nativeUI.toast("用户名密码错误");
							return;
						}else if (data.code == '0') {
							//如果用户选择记住了账号
							if (document.getElementById("checkbox").checked == true) {
								//记住密码
								localStorage.setItem(comDataIdent + "account", document.getElementById("account").value);
								localStorage.setItem(comDataIdent + "password", document.getElementById("password").value);
							} else {
								localStorage.removeItem(comDataIdent + "account");
								localStorage.removeItem(comDataIdent + "password");
							}
							if (data.data) {
								localStorage.setItem(comDataIdent + "token", data.data.token);
                                localStorage.setItem(comDataIdent + "userAccount", data.data.account); 
                                localStorage.setItem(comDataIdent + "userId", data.data.id);
                                localStorage.setItem(comDataIdent + "userRole", data.data.role);
                                localStorage.setItem(comDataIdent + "userRoleCode", data.data.roleCode);
                                
                                var authList=data.data.role.authList||[];
								var _auths=[];
								for(var i=0,len=authList.length;i<len;i++){
									var _a=authList[i];
									var _code=_a.code;
									_auths.push(_code);
								}
								 localStorage.setItem("authList", _auths);
							}
							
							console.log(JSON.stringify(data));
							console.log(new Date().getTime() + "|login:登录参数：" + JSON.stringify(loginInfo));
							plus.nativeUI.closeWaiting();
							toMain();
						}
					}
				},
				error:function(error){
					plus.nativeUI.closeWaiting();
					plus.nativeUI.toast("请求失败");
				}
			})
		}
	});
});

//首页返回键处理，处理逻辑：1秒内，连续两次按返回键，则退出应用
var first = null;
mui.back = function() {
	//首次按键，提示‘再按一次退出应用’
	if (!first) {
		first = new Date().getTime();
		mui.toast('再按一次退出应用');
		setTimeout(function() {
			first = null;
		}, 1000);
	} else {
		if (new Date().getTime() - first < 1000) {
			plus.runtime.quit();
		}
	}
};

function Trim(str){ 
   return str.replace(/(^\s*)|(\s*$)/g, ""); 
}

//
var _focusElem = null; 
document.body.addEventListener("focus", function(e) {
    _focusElem = e.target || e.srcElement;
}, true);

var originalHeight=document.documentElement.clientHeight || document.body.clientHeight;
window.onresize=function(){
	 var  resizeHeight=document.documentElement.clientHeight || document.body.clientHeight;
	 if(resizeHeight*1<originalHeight*1){ 
	         _focusElem.scrollIntoView(false);
	        document.getElementById("login-footer").style.position = "static";
	  }else{
	        document.getElementById("login-footer").style.position = "absolute";
	  }
}
