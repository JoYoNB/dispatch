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
	//获得主页面webview引用；
	index = plus.webview.currentWebview().opener();

});
//初始化过滤选项
initFilter();

//点击确定
$('.mui-bar-nav').on('tap', '.status-sure', function() {
	var list = [];
	$(".filter-content .checkbox_list input[type=checkbox]").each(function(){
		if($(this).is(":checked")){
			list.push($(this).attr("data-type"));
		}
	});
	localStorage.setItem(comDataIdent + "filterIconName",list);
	var mainView = plus.webview.getWebviewById('main.html');
    mui.fire(mainView, 'filter', {
        filterIcon: list
    });
    close();
});

//点击重置
$('.mui-bar-nav').on('tap', '.status-reset', function() {
	$(".filter-content .checkbox_list input[type=checkbox]").prop("checked",false);
	localStorage.removeItem(comDataIdent + "filterIconName");
	var mainView = plus.webview.getWebviewById('main.html');
    mui.fire(mainView, 'filter', {
        filterIcon: []
    });
});	

//初始化过滤选项
function initFilter(){
	var filterList = localStorage.getItem(comDataIdent + "filterIconName");
	if(filterList){
		filterList = filterList.split(",");

		for(var z = 0; z < filterList.length;z++){
			if($("."+filterList[z])){
				$("."+filterList[z]).attr("checked",true);
			}
		}
	}
}
// 关闭侧滑菜单
function close() {
	mui.fire(mui.currentWebview.opener(), "menu:close");
}



