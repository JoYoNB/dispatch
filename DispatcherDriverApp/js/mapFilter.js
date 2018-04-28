//main.html引用  纯webview模式的侧滑菜单
var subWebview = null,
	template = null,
	index = null;
var webview_style = {
	popGesture: "close" //窗口侧滑返回功能
};

//初始化区域滚动
mui('.mui-scroll-wrapper').scroll();

mui.plusReady(function() {
//	initPageLanguage();
	//获得主页面webview引用；
	index = plus.webview.currentWebview().opener();
	//
});
 
/**
 * @description 打开新窗口
 * @param {string} target 需要打开的页面的地址
 * */
var openNew = function(target) {
	mui.openWindow({
		url: target,
		id: target,
		styles: {
			top: '0', //新页面顶部位置
			bottom: '0' //新页面底部位置
		},
		show: {
			aniShow: 'slide-in-right',
			duration: 150
		},
		waiting: {
			autoShow: false
		}
	});
};

//点击重置
mui('.mui-bar-nav').on('tap', '.status-reset', function() {
	console.log(new Date().getTime() + "|mainMenu:点击了重置按钮");
	mui(".filter-content .checkbox_list input[type=checkbox]").each(function(){
		this.removeAttribute("checked");
		console.log(this);
		this.setAttribute("checked",false)
		console.log()
	});
});

$('.mui-bar-nav').on('tap', '.status-reset', function() {
	$(".filter-content .checkbox_list input[type=checkbox]").prop("checked",false);
});	

/**
 * 关闭侧滑菜单
 */
function close() {
	mui.fire(mui.currentWebview.opener(), "menu:close");
}
//在android4.4.2中的swipe事件，需要preventDefault一下，否则触发不正常
window.addEventListener('dragstart', function(e) {
	mui.gestures.touch.lockDirection = true; //锁定方向
	mui.gestures.touch.startDirection = e.detail.direction;
});
window.addEventListener('dragleft', function(e) {
	if (!mui.isScrolling) {
		e.detail.gesture.preventDefault();
	}
});
//监听左滑事件，若菜单已展开，左滑要关闭菜单；
window.addEventListener("swipeleft", function() {
	console.log(new Date().getTime() + "|mainMenu:左滑!");
	close();
});
//添加页面刷新事件，子页面可以刷新主页面
window.addEventListener("pageflowrefresh",function(e){
	location.reload();
})
