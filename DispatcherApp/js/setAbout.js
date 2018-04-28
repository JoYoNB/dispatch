mui.init({
	swipeBack: true //启用右滑关闭功能
});
//初始化区域滚动
mui('.mui-scroll-wrapper').scroll();
mui.plusReady(function() {
	console.log(new Date().getTime() + "|setAbout.js:plusReady");
	//初始化区域滚动
	// initPageLanguage();
	// 加载关于信息
	var aboutUsData = {
		url:(httpServer + '/DispatcherAppWeb/common/aboutUs.json'),
		data:{},
		dataType:"json",
		type:"post",
		success:renderAboutData,
		error:function(error){console.log(error)}
	};
	utils.ajaxFn(aboutUsData);
	
	//退出登录
	$(".mui-content").on("tap",".logout",function(){
		console.log("用户点击了退出登录.");
		var logout = {
			url:httpServer + '/DispatcherAppWeb/common/logout.json',
			data:{
				userId: localStorage.getItem(comDataIdent + "userId"),	
				account:localStorage.getItem(comDataIdent + "userAccount")
			},
			success:function(result){
				console.log(result);
				console.log("退出登录:"+JSON.stringify(result));
				if(result){
					if(result.code == 0){
						localStorage.removeItem(comDataIdent +　"userAccount");
						localStorage.removeItem(comDataIdent + "userId");
						localStorage.removeItem(comDataIdent + "userRole");
						localStorage.removeItem(comDataIdent + "token");
						utils.gorestart();
					}
					plus.nativeUI.toast("已退出登录");
				}
			},
			error:function(error){
				console.log(error);
			}
		};
		utils.ajaxFn(logout);
	});
});

//主菜单切换
mui(".personal-menu").on("tap",".mui-navigate-right",function(){
	var name = this.getAttribute("data-name");
	if(name){
		var _url = name;
		var _id = name;

			mui.openWindow({
				url: _url,
				id: _id,
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
				},
				createNew:false
			});

	}
});

//
function renderAboutData(result){
	console.log(JSON.stringify(result));
	if(! result){
		return;
	}
	var _data = result.data;
	var _copyright=_data.copyright ? _data.copyright : "0000-0000";
	var _companName=_data.companyName ? _data.companyName : "********";
	var _icp=_data.icp ? _data.icp : "********";
	$("#copy_right_1").html("&copy;"+_copyright);
	$("#copy_right_2").html(_companName);
	$("#copy_right_3").html(_icp);
}