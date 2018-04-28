mui.init({
	swipeBack: false //禁用右滑关闭功能
});
mui.back = function() {};
mui.plusReady(function() {
	var self = plus.webview.currentWebview();
	self.setStyle({
		'popGesture': 'none'
	});
	plus.navigator.closeSplashscreen();

	document.getElementById("close").addEventListener('tap', function(event) {
		console.log(new Date().getTime() + "|guide:tap了关闭按钮")
		localStorage.setItem(comDataIdent + "lauchFlag", "true");     
		
		var wv_login =plus.webview.getWebviewById("userLogin.html");
		if (wv_login) {
			wv_login.show();
		}else{
			utils.openView({
				url:"userLogin.html",
				id:"userLogin.html"
			});
		}
		plus.webview.currentWebview().close();
	}, false);
});
//图片切换时，触发动画
document.querySelector('.mui-slider').addEventListener('slide', function(event) {
	//注意slideNumber是从0开始的；
	var index = event.detail.slideNumber + 1;
	if (index == 2 || index == 3) {
		var item = document.getElementById("tips-" + index);
		if (item.classList.contains("mui-hidden")) {
			item.classList.remove("mui-hidden");
			item.classList.add("guide-show");
		}
	}
});
