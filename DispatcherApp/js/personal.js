mui.init({
	swipeBack: true //启用右滑关闭功能
});
//初始化区域滚动
mui('.mui-scroll-wrapper').scroll();
var roleCode="";
var authList="";
mui.plusReady(function() {
	console.log(new Date().getTime() + "|personal.js:plusReady");
	roleCode = localStorage.getItem(comDataIdent + "userRoleCode") ? localStorage.getItem(comDataIdent + "userRoleCode") : "";
	var auths = localStorage.getItem("authList")||"";
	console.info(auths);
	authList=auths.split(",");
	//初始化区域滚动
	// initPageLanguage();
	// 加载用户信息
	var userData = {
		url:(httpServer + '/DispatcherAppWeb/common/myInfo.json'),
		data:{},
		dataType:"json",
		type:"post",
		success:renderUserInfo,
		error:function(error){console.log(error)}
	};
	
	var userOrderData = {
		url:'',
		data:{},
		type:"post",
		success:renderUserOrderInfo,
		error:function(error){console.log(error)}
	};
	// 加载用户订单信息
	//承运商或者复合角色用户
	console.error(roleCode);
	if(roleCode.indexOf("consignor")>=0||roleCode.indexOf("dual")>=0){
		$("#carrier-num-show").addClass("hide");
		$("#consignor-num-show").removeClass("hide");
		userOrderData.url = httpServer + '/DispatcherAppWeb/common/getConsignorOrderTotal.json';
	}
	//货主
	if(roleCode.indexOf("carrier")>=0){
		$("#consignor-num-show").addClass("hide");
		$("#carrier-num-show").removeClass("hide");
		
		userOrderData.url = httpServer + '/DispatcherAppWeb/common/getCarrierOrderTotal.json';
		
	}
	
	//车辆管理权限
	if($.inArray("vehicleManagement",authList)>=0){
		$("#vehicleManage").parent().removeClass("hide");
	}else{
		$("#vehicleManage").parent().addClass("hide");
	}
	console.error($.inArray("siteManagement",authList));
	//车辆站点
	if($.inArray("siteManagement",authList)>=0){
		$("#siteManage").parent().removeClass("hide");
	}else{
		$("#siteManage").parent().addClass("hide");
	}
	//车辆管理权限
	if($.inArray("driverManagement",authList)>=0){
		$("#driverManage").parent().removeClass("hide");
	}else{
		$("#driverManage").parent().addClass("hide");
	}
	
	utils.ajaxFn(userData);
	utils.ajaxFn(userOrderData);
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
	console.log(JSON.stringify(result));
	if(! result){
		return;
	}
	var data = result.data;
	$("#company").html(data.companyName ? data.companyName : "--");
	$("#name").html(data.name ? data.name : "--");
	$("#deptName").html(data.deptName ? data.deptName : "--");
	$("#personal-logo").css("backgroundImage","url("+data.logo+")");
	
	if(data.authStatus){
		$(".auth-icon").css("display","block");
	}else{
		$(".auth-icon").css("display","none");
	}
}

//渲染用户订单信息
function renderUserOrderInfo(result){
	console.error("订单信息");
	console.log(JSON.stringify(result));
	if(! result){
		return;
	}
	var data = result.data;
	$(".orderNum .num").html(data.totalOrder ? data.totalOrder : "0");
	var totalNum=data.totalNum ? data.totalNum : "-";
	var totalVolume=data.totalVolume ? data.totalVolume : "-";
	var totalWeight=data.totalWeight ? data.totalWeight : "-";
	$("#goodsAmount").html(totalNum+"件|"+totalVolume+"方|"+totalWeight+"吨");
	$("#fPoint").html(data.fPoint ? data.fPoint : "100%");
	$("#full").html(data.full ? data.full : "100%");

}
