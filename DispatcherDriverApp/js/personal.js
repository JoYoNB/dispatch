mui.init({
	swipeBack: false //启用右滑关闭功能
});
//初始化区域滚动
mui('.mui-scroll-wrapper').scroll();
mui.plusReady(function() {
	 console.log(new Date().getTime() + "|personal.js:plusReady");
	//初始化区域滚动
	// initPageLanguage();
	// 加载用户信息
	var userData = {
		url:httpServer + '/DriverAppWeb/information/getDriverInfo.json',
		data:{},
		dataType:"json",
		type:"get",
		success:renderUserInfo,
		error:function(error){console.log(error)}
	};
	// 加载用户订单信息
	var userOrderData = {
		url:httpServer + '/DriverAppWeb/information/getDriverStatis.json',
		data:{},
		type:"get",
		success:renderUserOrderInfo,
		error:function(error){console.log(error)}
	};
	utils.loadData(userData);
	utils.loadData(userOrderData);
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



//渲染用户信息
function renderUserInfo(result){
	console.log(result);
	if(! result){
		return;
	}
	var data = result.data;
	utils.getEl(".personal-details .company").innerText = data.deptName ? data.deptName : "--";
	utils.getEl(".personal-details .name").innerText = data.driverName ? data.driverName : "--";
	utils.getEl(".personal-details .deptName").innerText = data.deptName ? data.deptName : "--";
	utils.getEl(".personal-details .plateNo").innerText = data.plateNo ? data.plateNo : "--";
	//utils.getEl(".personal-details .personal-logo").style.backgroundImage = 'url('+data.url+')'
	
	console.log(data.logo);
	if(data.authStatus){
		utils.getEl(".auth-icon").style.display = "block";
	}else{
		utils.getEl(".auth-icon").style.display = "none";
	}
}

//渲染用户订单信息
function renderUserOrderInfo(result){
	if(! result){
		return;
	}
	var data = result.data;

	//调数据时改class 名字
	utils.getEl(".personal-num-show .orderNum .num").innerText = data.orderCount ? data.orderCount : 0;
	utils.getEl(".personal-num-show .fPoint .num").innerText = data.fPoint ? data.fPoint :0;
	utils.getEl(".personal-num-show .full .num").innerText = data.full ? data.full : 0;
}
