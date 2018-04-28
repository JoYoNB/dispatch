
var subpage_style = {
	top: immersed + 56 + 'px',
	bottom: '0'
};
	
mui.plusReady(function() {
	var self = plus.webview.currentWebview();
	var wv_list =plus.webview.create("driverList.html","driverList.html",subpage_style,{type:self.type});
	//通过webview.create创建子窗口
	self.append(wv_list);
	//载入子窗口
});

mui(".mui-bar-nav").on("tap",".right-act",function(){
	utils.openView({
		url:"driverAdd.html",
		id:"driverAdd.html"
	});
});