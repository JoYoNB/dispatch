//var httpServer = "http://localhost:8020/cp_app/";
var httpServer = 'http://192.168.0.148:8093';



// 沉浸状态栏
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
	
	var detectionHeaderClass = document.querySelector('.mui-bar.mui-bar-nav.detection_bar_nav');
	detectionHeaderClass && (detectionHeaderClass.style.paddingTop = immersed + 'px', detectionHeaderClass.style.height = immersed + 296 + 'px');

	var detectionContentClass = document.querySelector('.detection_bar_nav ~ .detection_content');
	detectionContentClass && (detectionContentClass.style.top = immersed + 296 + 'px');
	
	var bcidClass = document.querySelector('#bcid');
	bcidClass && (bcidClass.style.top = immersed + 56 + 'px');

	var menuUserClass = document.querySelector('.menu_user');
	menuUserClass && (menuUserClass.style.paddingTop = immersed + 40 + 'px');

	var loginTopClass = document.querySelector('.login_top');
	loginTopClass && (loginTopClass.style.paddingTop = immersed + 'px', loginTopClass.style.height = immersed + 192 + 'px');

	var adCloseClass = document.querySelector('.pop_ad .close_ad');
	adCloseClass && (adCloseClass.style.top = immersed + 10 + 'px');

	var adClass = document.querySelector('.pop_ad .ad');
	adClass && (adClass.style.margin = '26% auto');
})(window);
//等待图标样式
var waitingStyle = {
	modal: false,
	back: "none",
	style: "black",
	background: "rgba(0,0,0,0)"
};
//
var mklog = function() {
	return new Date().getTime()
};

//返回所有webview
var mkwv = function() {
	var wvs = plus.webview.all(); //循环显示当前webv
	var t1 = "|debug:当前共有" + wvs.length + "个webview\n";
	var t2 = "";
	for(var i = 0; i < wvs.length; i++) {
		t2 += "|webview" + i + "|" + wvs[i].id + "..@--" + wvs[i].getURL().substr(82) + '\n';
	}
	return t1 + t2;
}
