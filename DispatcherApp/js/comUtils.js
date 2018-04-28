//var httpServer = "http://192.168.0.148:8891/";
var httpServer = "http://192.168.0.249:8080/"
var comDataIdent = "_disp_";
var immersed = 0;
(function(w) {
	document.addEventListener('plusready', function() {
//		console.log("Immersed-UserAgent: " + navigator.userAgent);
	}, false);
	var ms = (/Html5Plus\/.+\s\(.*(Immersed\/(\d+\.?\d*).*)\)/gi).exec(navigator.userAgent);
	if(ms && ms.length >= 3) {
		immersed = parseFloat(ms[2]);
	}
	w.immersed = immersed;
	if(!immersed) {
		return;
	}
	var headerClass = document.querySelector('.mui-bar.mui-bar-nav');
	headerClass && (headerClass.style.paddingTop = immersed + 'px', headerClass.style.height = immersed + 56 + 'px');

	var contentClass = document.querySelector('.mui-bar-nav ~ .mui-content');
	contentClass && (contentClass.style.top = immersed + 56 + 'px');

	var tabBarClass = document.querySelector('.mui-bar.mui-bar-tab');
	tabBarClass && (tabBarClass.style.top = immersed + 56 + 'px');
})(window);

//等待图标样式
var waitingStyle = {
	modal: false,
	back: "none",
	style: "black",
	background: "rgba(0,0,0,0)"
};


var utils = {
	/* 发送请求
	 * @options.url : 请求的地址
	 * @options.data: 数据
	 * @options.success: 请求成功执行的函数
	 * @options.error: 请求失败执行的函数
	 */
	ajaxFn:function(options) {
		var _data =  {
			token:localStorage.getItem(comDataIdent + "token")
		};
		if(! options.data){
			options.data = {};
		};
		$.extend(_data,options.data);
		console.log("请求的数据:"+JSON.stringify(_data));
		console.log("请求的数据地址:"+options.url);
		$.ajax({
			url:options.url,
			data:_data,
			dataType:'json',//服务器返回json格式数据
			type:options.type || 'post',//HTTP请求类型
			async:options.async || true,
			timeout:10000,//超时时间设置为10秒；
			success:function(result){
				console.log("返回的数据:"+JSON.stringify(result));
				if(result){
					if(result.code == "1000"){
						plus.nativeUI.toast("系统异常");
					}
					if(result.code == "1001"){
						plus.nativeUI.toast("网络异常");
					}
					if(result.code == "1002"){
						plus.nativeUI.toast("角色错误");
					}
					if(result.code == "1003"){
						plus.nativeUI.toast("没有权限qq");
					}
					if(result.code == "1004"){
						plus.nativeUI.toast("token失效,请重新登录");
						localStorage.removeItem(comDataIdent + "token");
						utils.gorestart();
					}
					if(result.code == "1000"){
						plus.nativeUI.toast("系统异常");
					}else{
						if(options.success){
							options.success(result);
						}
					}
				}
			},
			error:function(error){
				//异常处理；
				if(options.error){
					options.error(error);
				}
			}
		});
	},
	/* 打开新的页面
	 * 
	 * @options.url : 要打开的页面
	 * @options.id : 页面的id
	 * @datas: 要传的参数
	 */
	openView:function(options){
		var datas = options.datas || {};
		mui.openWindow({
			url: options.url,
			id: options.id,
			styles: {
				top: '0', //新页面顶部位置
				bottom: '0' //新页面底部位置
			},
			extras:datas,
			show: {
				aniShow: 'slide-in-right',
				duration: 150
			},
			waiting: {
				autoShow: false
			},
			createNew:false,
			styles: {
				hardwareAccelerated: true
			}
		});
	},
	gorestart:function(){
		if(mui.os.ios) {
			utils.openView({"url":"userLogin.html","id":"userLogin.html"});
			var wvs = plus.webview.all();
			for(var i = 0; i < wvs.length; i++) {
				if(wvs[i].id=="userLogin.html"){
					continue;
				}
				wvs[i].close("none");
			}
		} else {
			plus.runtime.restart();
		}
	},
	
}