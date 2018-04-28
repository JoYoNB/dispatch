mui.init({
	swipeBack: false //禁用右滑关闭功能
});
//初始化区域滚动
mui('.mui-scroll-wrapper').scroll();

mui.plusReady(function() {
	var AutoLogin = ls.geting("AutoLogin");
	var sessionKey = localStorage.getItem("sessionKey");
	var showGuide = plus.storage.getItem("lauchFlag");
	var self = plus.webview.currentWebview();
	var accountBox = document.getElementById('account');
	var passwordBox = document.getElementById('password');
	var toMain = function() { //定义main.html转场函数
		openNew('index.html', '10002') //TODO userid应从localstorage中读取
	};
	// 动态改变webview的侧滑返回功能：
	// 关闭侧滑返回功能
	self.setStyle({
		'popGesture': 'none'
	});
	if (sessionKey) { //自动登录时
		console.log(new Date().getTime() + "|login:自动登录:" + sessionKey);
		toMain();
	} else {
		var account = localStorage.getItem("account"); 
		var password = localStorage.getItem("password");      
		if (account != null && password != null) {           
			document.getElementById('checkbox').checked = true;           
			document.getElementById('password').value = password;
			document.getElementById('account').value = account;       
		}
		console.log(new Date().getTime() + "|login:是否自动登录:" + sessionKey);
		if (showGuide) {
			self.show();
			console.log(new Date().getTime() + "|login:显示登录界面");
		}
		plus.navigator.closeSplashscreen();
		console.log(new Date().getTime() + "|login:关闭了启动画面");
	}

	//切换记住密码状态
	mui('#autoLogin').on('change', 'input', function() {
		var isAlogin = this.checked ? true : false;
		AutoLogin.stat = isAlogin;
		ls.seting("AutoLogin", AutoLogin);
		console.log(new Date().getTime() + "|login:点击自动登录:" + ls.geting("AutoLogin").stat);
	});

	//监听账号输入框的输入操作
	var _a = localStorage.getItem("account");
	var _p = localStorage.getItem("password"); 
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
			plus.nativeUI.toast("用户名或密码不能为空.");
			return;
		} else {
			var _password=Trim(passwordBox.value);
		     getpassword=SHA256(_password);
			var loginInfo = {
				phoneNo: accountBox.value,
				password: getpassword,
				//sign:localStorage.getItem("sign"),
				// language:localStorage.getItem("language"),
				//clientId: clientInfo.clientid,
				//appId: clientInfo.appid
			};
			console.log("getpassword:"+getpassword);
			console.log("loginInfo: "+JSON.stringify(loginInfo));
			mui.ajax(httpServer + '/DriverAppWeb/common/login.json', {
				data: loginInfo,
				dataType: 'json', //服务器返回json格式数据
				type: 'post', //HTTP请求类型
				timeout: 30000, //超时时间设置为10秒；
				notNeedRestart:true,//不需要重启app
				success: function(data) {
					//服务器返回响应，根据响应结果，分析是否登录成功；
					console.log("ok:"+JSON.stringify(data))
					if (data != '') {
//						if(data.errCode == ERROR_CODE.SESSION_IS_ERROR || data.errCode ==ERROR_CODE.SESSION_IS_NULL){
//							restartApp();
//						} 
						if (data.code == '1005') {
							plus.nativeUI.closeWaiting();
							plus.nativeUI.toast("用户名密码错误");
							return;
						}else if (data.code == '0') {
							//如果用户选择记住了账号
							if (document.getElementById("checkbox").checked == true) {
								//记住密码
								localStorage.setItem("account", document.getElementById("account").value);
								localStorage.setItem("password", document.getElementById("password").value);
							} else {
								//立即过期
								localStorage.removeItem("account");
								localStorage.removeItem("password");
							}
							//把登录返回成功的sessionKey放入本地中
							if (data.data) {
								//设置token
								localStorage.setItem("token", data.data.token);
								//设置登录的账号
                                localStorage.setItem("userAccount", data.data.account); 
                                localStorage.setItem("userId", data.data.id);
                                //记录用户权限
                                localStorage.setItem("userRole", data.data.role);
                                localStorage.setItem("userRoleCode", data.data.roleCode);
							}
							console.log(localStorage.getItem("userRole"));
							console.log(new Date().getTime() + "|login:登录参数：" + JSON.stringify(loginInfo));
							plus.nativeUI.closeWaiting();
							toMain();
						}
					}
				},
				error: function(xhr, type, errorThrown) {
					plus.nativeUI.closeWaiting();
					//异常处理；
					console.log("errorinfo:" + type);
					//测试代码，直接跳转到首页
					//toMain();
					//测试代码
				}
			});
		}
	});
}); //mui.plusReady结束

mui.ready(function() {
	
}); //mui.ready结束

//新开窗口
var openNew = function(target, uid) {
	mui.openWindow({
		url: target,
		id: target,
		show: {
			aniShow: 'slide-in-right',
			duration: 150
		},
		waiting: {
			autoShow: false
		},
		extras: {
			userid: '10002' //TODO 应从localstorage中读取
		}
	});
};

//重新封装localstorage读写
var ls = {
	geting: function(geting) {
		var getText = localStorage.getItem("$" + geting) || "{}";
		return JSON.parse(getText);
	},
	seting: function(key, value) {
		value = value || {};
		localStorage.setItem("$" + key, JSON.stringify(value));
	},
	clear: function() {
		ls.seting("token", '{"token":undefined,"expires":undefined}')
			//TODO ls.clear 清除localstorage
	}
};
/**设置语言*/
function initPageLanguage(){
	var language = localStorage.getItem("language");//获取当前语言
	var commonLanguage = commonJS.getLanguage().common;
	
	commonJS.initLanguage("userLogin");//初始化语言
	document.getElementById("ico_txt").src="language/"+language+"/images/login_txt.png";
	document.getElementById("LanguageSel").value=commonLanguage[language];
	// 选择语言
	var languageOption = {};
	languageOption.buttons = commonLanguage.dtButtons;
	var LanguagePicker = new mui.PopPicker(languageOption);
	LanguagePicker.setData([
		{
			value: 'zh_cn',
			text: commonLanguage.zh_cn
		}, 
		// {
		// 	value: 'en',
		// 	text: commonLanguage.en
		// }
	]);
	LanguagePicker.pickers[0].setSelectedValue(language);
	var LanguageSel = document.getElementById('LanguageSel');
	LanguageSel.addEventListener('tap', function(event) {
		LanguagePicker.show(function(items) {
			LanguageSel.value = items[0].text;
			localStorage.setItem("language",items[0].value);
			location.reload();//重新加载页面
			
			//返回 false 可以阻止选择框的关闭
			//return false;
		});
	}, false);
}

//首页返回键处理，处理逻辑：1秒内，连续两次按返回键，则退出应用
var first = null;
mui.back = function() {
	//首次按键，提示‘再按一次退出应用’
	if (!first) {
		first = new Date().getTime();
		var language = commonJS.getLanguage().main;
		//mui.toast('再按一次退出应用');
		mui.toast(language.exitMsg);
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


var originalHeight=document.documentElement.clientHeight || document.body.clientHeight;
window.onresize=function(){
    //软键盘弹起与隐藏  都会引起窗口的高度发生变化
    var  resizeHeight=document.documentElement.clientHeight || document.body.clientHeight;
    if(resizeHeight*1<originalHeight*1){ //resizeHeight<originalHeight证明窗口被挤压了
            plus.webview.currentWebview().setStyle({
                height:originalHeight
            });
     }
}




