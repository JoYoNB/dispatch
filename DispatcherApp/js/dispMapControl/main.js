//初始化区域滚动
mui('.mui-scroll-wrapper').scroll();
var menu = null,
	main = null,
	showMenu = false,
	contentWebview = null,
	isInTransition = false;

mui.plusReady(function() {
	main = plus.webview.currentWebview();
	
	if(mui.os.android){
		if (plus.webview.getWebviewById('userLogin.html')) {
			plus.webview.getWebviewById('userLogin.html').close();
		}
	}
	if (plus.webview.getLaunchWebview()) {
		plus.webview.getLaunchWebview().close();
	}
	
	main.addEventListener('maskClick', closeMenu); //点遮罩时关闭menu
//	 动态改变webview的侧滑返回功能：
//	 关闭侧滑返回功能
	main.setStyle({
		'popGesture': 'none'
	});
	
	//初始化地图
	mainMap.init();
});

mui.preload({
	"id": 'mapFilter.html',
	"url": 'mapFilter.html'
});

//点击问号，显示图示
$(".mui-content").on("tap",".map-icon-tips",function(){
	$(".tips-box").toggleClass("hide");
});

//添加全局自定义监听
window.addEventListener("menu:close", closeMenu);
window.addEventListener("menu:open", openMenu);
//点击左上角侧滑图标，打开侧滑菜单；
document.querySelector('.main-filter').addEventListener('tap', function(e) {
	if (showMenu) {
		console.log("close");
		closeMenu();
	} else {
		console.log("open");
		openMenu();
	}
});

// 打开显示侧滑菜单
function openMenu() {
	var _now = new Date().getTime();
	menu = mui.preload({
		id: 'mapFilter.html?_now='+_now,
		url: 'mapFilter.html?_now='+_now,
		styles: {
			right: 0,
			width: '80%',
			zindex: -1
		},
		show: {
			aniShow: 'none'
		}
	});

	if (isInTransition) {
		return;
	}
	if (!showMenu) { //侧滑菜单处于隐藏状态，则立即显示出来；
		isInTransition = true;
		menu.setStyle({
			mask: 'rgba(0,0,0,0)'
		});
		//menu设置透明遮罩防止点击
		menu.show('none', 0, function() { //主窗体开始侧滑并显示遮罩
			main.setStyle({
				mask: 'rgba(0,0,0,0.3)',
				right: '80%',
				transition: {
					duration: 150
				}
			});
			mui.later(function() {
				isInTransition = false;
				menu.setStyle({ //移除menu的mask
					mask: "none",
					zindex:9
				});
			}, 160);
			showMenu = true;
		});
	}
	
};

//关闭侧滑菜单
function closeMenu() {
	var main = plus.webview.currentWebview();
	if (isInTransition) {
		return;
	}
	if (showMenu) {
		//关闭遮罩；
		//主窗体开始侧滑；
		isInTransition = true;
		main.setStyle({
			mask: 'none',
			right: '0',
			transition: {
				duration: 150
			}
		});
		showMenu = false;
		//等动画结束后，隐藏菜单webview，节省资源；
		mui.later(function() {
			isInTransition = false;
			menu.hide();
		}, 300);
	}

};

//重写mui.menu方法，Android版本menu按键按下可自动打开、关闭侧滑菜单；
mui.menu = function() {
	if (showMenu) { closeMenu(); }
	else { openMenu(); }
};

//首页返回键处理
var first = null;
mui.back = function() {
	if (showMenu) {
		closeMenu();
	} else {
		//首次按键，提示‘再按一次退出应用’
		if (!first) {
			first = new Date().getTime();
			setTimeout(function() {
				first = null;
			}, 1000);
		} else {
			if (new Date().getTime() - first < 1000) {
				plus.runtime.quit();
			}
		}
	}
};

//向下滑动隐藏详情
document.getElementById("mapOrderDetails").addEventListener("swipedown",function(){
	console.log("你向下滑动了");
	$(".map-order-details").animate({"bottom":"-200px"},function(){
		$(".map-order-details").hide();
		$(".mui-main-menu").css("box-shadow","0 -2px 7px 0px #c1c1c1");
	});
});

//监听过滤事件
window.addEventListener('filter', function(e){
	var filerList = e.detail.filterIcon;
	mainMap.filterMarker(filerList);
});

//监听分配事件
window.addEventListener('selected', function(e){
	var orderData = e.detail.orderData;
	// var iconName = orderData.loadData ?  (orderData.loadData == 100 ? "full" : "half") : "empty";
	// var _html = ' <div class="vehicle-detials">\
	// 				 <p><span class="localtion">'+ (orderData.startAddress ? orderData.startAddress : "--") +' —— '+ (orderData.endAddress ? orderData.endAddress : "--") +'('+(orderData.distance ? orderData.distance : "--")+'KM)</span></p>\
	// 				 <p class="item-box"><span class="vehicle-details-item">'+(orderData.goodsTypeName ? orderData.goodsTypeName : "--")+'</span><span class="vehicle-details-item">'+(orderData.goodsNum ? orderData.goodsNum : "--")+'</span><span class="vehicle-details-item">'+(orderData.vehicleTypeName ? orderData.vehicleTypeName : "--")+'</span><span class="vehicle-details-item">'+(orderData.cost ? orderData.cost : "--")+'元</span></p>\
	// 			 </div>'; 

	// $(emptyObj).parent(".right").hide();
	// $(emptyObj).parents(".vehicle-empty-box").find(".status").removeClass("empty").addClass(iconName);
	// $(emptyObj).parents(".vehicle-empty-box").append(_html);
	//更改新当前车辆的状态，查询接口 or 把数据全部带过来 or 刷新页面？
});

var emptyObj = null;
//跳转分配页面
$(".main-content").on("tap",".distribute",function(){
	var vehicleId = $(this).parent().siblings(".vehicle-empty-detials").find(".name").attr("data-vehicleId");
	emptyObj = this;
	utils.openView({
		url:"selectOrder.html",
		id:"selectOrder.html",
		datas:{
			vehicleId:vehicleId
		}
	});
});

(function(){
	mainMap =  {
		iMap:null,
	    updateDataSymbol : false,
		mapVehicleData:{
	//		url:httpServer+"DispatcherAppWeb/dispatcherMonitor/listVehiclePositions.json",
			url:"./js/data/mapData.json",
			data:{},
			dataType:"json",
			async:false,
		//	type:"get",
			type:"post",
			success:function(result){
				mainMap.hadnleLoadData(result);
			},
			error:function(error){console.log(error)}
		},
		mapSiteData : {
	//		url:httpServer+'DispatcherAppWeb/dispatcherMonitor/listOrderPositions.json ',
			url:'./js/data/mapSiteData.json',
			data:{},
			dataType:"json",
			 type:"post",
		//	type:"get",
			success:function(result){
				mainMap.hadnleLoadData(result);
			},
			error:function(error){console.log(error)}
		},
		init :function (){
			iMap = new ZMap();
			iMap.init("map");
			utils.ajaxFn(this.mapVehicleData);
			utils.ajaxFn(this.mapSiteData);
		},
		hadnleLoadData :function (result){
			//onlineStatus ：司机的登录状态： 在线 、 离线
			//vehicleStatus : 车辆的状态 ：在线 、 休眠 、离线
			//loadStatus：货载的状态： 空载、半载 、满载 
			var mList = [];
			var onlineStatusText = ["离线","在线"];
			// var vehicleStatusText = ["在线","休眠","离线"];
			var vehicleStatusText = '';
			// var loadStatusText = ["空载","半载","满载"];
			var loadStatusText = '';
			
			if(result.code != 0) return;
			if(result && result.data && result.data.list){

				var list = result.data.list;
				for(var i = 0;i < list.length; i++){
					var temp = list[i];
					var tempObj = {};
					
					if(! temp.vehicleId){ //站点数据
						var latLon = temp.startCoordinate ? temp.startCoordinate : "";
						if(latLon){
							tempObj.lon = latLon.split(",")[0];
							tempObj.lat = latLon.split(",")[1];
						}
						tempObj.type = "goods_site";
						tempObj.iconName = "goods_site";
						tempObj.detail = temp;
						tempObj.id = temp.id;
					}else{ //车辆数据
						if(!temp.onlineStatus){
							tempObj.iconName = "offline";
						}else{
							tempObj.iconName =  temp.loadRate ? ( temp.loadRate == 100 ? "full" : "half" ): "empty";
						}
	
						switch(temp.loadRate){
							case 0:    loadStatusText = "空载"; break;
							case 100:  loadStatusText = "满载"; break;
							default:
								       loadStatusText = "半载";
						}
	
						tempObj.type =  tempObj.iconName;
						tempObj.loadStatusText = loadStatusText;
						tempObj.lon = temp.bd_lon;
						tempObj.lat = temp.bd_lat;
						tempObj.vehicleId = temp.vehicleId;
						tempObj.detail = temp;
						tempObj.goodsNum = temp.packageNum ? temp.packageNum + "件" : (temp.weight ? temp.weight + "吨" : (temp.volume ? temp.volume + "立方" : "0"));
	
						//暂定车辆状态为vehicleStatus ： 0：离线 ， 1：休眠，2：在线
						tempObj.vehicleStatusText = temp.vehicleStatus ? (temp.vehicleStatus == "1" ? "休眠" : "在线") : "离线";
						tempObj.id = temp.vehicleId;
					}
					mList.push(tempObj);
				}
			}	
			if(!this.updateDataSymbol){
				this.updateDataSymbol = true;
				this.setMapMarker(mList);
				this.refreshData30s();
			}
			else{
				this.updateMapMarker(mList);
			}
			console.log(mList);
		},
	
		setMapMarker :function(list){
			var self = this;
			if(list){
				for(var i = 0;i < list.length;i++){
					var temp = list[i];
					var point = {lon:temp.lon,lat:temp.lat};
					var iconName = temp.iconName ? temp.iconName + "_icon" : "";
					iMap.setMarker(point,iconName,temp,self.clickHandler);
				}
			}
			this.initShowMarker();
		},
	
		clickHandler :function(e,detailsData){
			$(".map-order-details").show();
			$(".map-od-details").hide();
			$(".map-order-details").animate({"bottom":"0px"});
			mainMap.renderMarkerDetails(detailsData);
		},
	
		renderMarkerDetails :function (detailsData){
			if(!detailsData ||  !detailsData.detail){
				return;
			}
			var data = detailsData.detail
			var _html = '';
			if(detailsData.type == "goods_site"){ 
				_html = '<h3><span class="name">'+(data.startSiteName ? data.startSiteName : "--")+'</span></h3>\
						<p><span class="site-driver-icon"></span><span class="driver">'+ (data.senderName ? data.senderName :"--") +' | '+ (data.senderPhone ? data.senderPhone :"--") +'</span></p>\
						<p><span class="site-localtion-icon"></span><span class="localtion">'+ (data.startAddress ? data.startAddress :"--") +' </span></p>';
				
				$(".site-box .site-detials").html(_html);
				$(".site-box").show(); 
			}else if( detailsData.type == "empty"){ 
				_html = '<h3><span class="name" data-vehicleId='+ ( detailsData.vehicleId ? detailsData.vehicleId : "" ) +'>'+ (data.plateNo ? data.plateNo : "--") +'</span><span class="status empty">空载('+detailsData.vehicleStatusText+')</span></h3>\
						<p><span class="driver">'+(data.driverName ? data.driverName : "--")+'('+(data.driverPhone ? data.driverPhone : "--")+') | '+ (data.deptName ? data.deptName : "--") +'</span></p>';
				
				$(".vehicle-empty-box .vehicle-empty-detials").html(_html);
				$(".vehicle-empty-box").show();
			}else { 
				_html += '<div class="driver-detials">\
							 <h3><span class="name">'+(data.plateNo ? data.plateNo : "--")+'</span><span class="status '+detailsData.iconName+'">'+(detailsData.loadStatusText ? detailsData.loadStatusText : "--")+'('+ (detailsData.vehicleStatusText ? detailsData.vehicleStatusText :"--") +')</span></h3>\
							 <p><span class="driver">'+(data.driverName ? data.driverName : "--")+'('+(data.driverPhone ? data.driverPhone : "--")+') | '+ (data.deptName ? data.deptName : "--") +'</span></p>\
						  </div>\
						  <div class="vehicle-detials">\
							 <p><span class="localtion">'+ (data.startAddress ? data.startAddress : "--") +' —— '+ (data.endAddress ? data.endAddress : "--") +'('+(data.distance ? data.distance : "--")+'KM)</span></p>\
							 <p class="item-box"><span class="vehicle-details-item">'+(data.goodsTypeName ? data.goodsTypeName : "--")+'</span><span class="vehicle-details-item">'+(data.goodsNum ? data.goodsNum : "--")+'</span><span class="vehicle-details-item">'+(data.vehicleTypeName ? data.vehicleTypeName : "--")+'</span><span class="vehicle-details-item">'+(data.cost ? data.cost : "--")+'元</span></p>\
						 </div>'; 
				$(".vehicle-box").html(_html);
				$(".vehicle-box").show();
			}
		},
	
		filterMarker : function(iconNameList){
			var list = iMap.getAllMarker();
			for(var i = 0;i < list.length;i++){
	
				list[i].show();
				var iconObj = list[i].getIcon();
				if(iconNameList && iconNameList.length != 0){
					for(var j = 0;j < iconNameList.length;j++){
						if(iconObj.imageUrl == "images/"+iconNameList[j]+"_icon.png"){
							list[i].hide();
						}
					}
				}
			}
		},
	
		initShowMarker : function(){
			var filterList = localStorage.getItem("filterIconName");
			if(filterList){
				filterList = filterList.split(",");
				this.filterMarker(filterList);
			}
		},
	
		getMarkerId : function (){
			var allMarker = iMap.getAllMarker();
			var idList = [];
			for(var z=0; z < allMarker.length; z++){
				var temp = allMarker[z].getLabel().content;
				idList.push(temp.id);
			}
		},
	
		isMarkerExist : function(allMark,options){
			var symbol = 0;
			for(var z=0; z < allMark.length; z++){
				var markerId = allMark[z].getLabel().content.id;
				if(options.id == markerId){
					iMap.updateMarker(allMark[z],options.point,options.temp,clickHandler);
					allMark.splice(z,1);  
					symbol = true;
				}
			}
			return symbol;
		},
	
		updateMapMarker:function (list){
			var allMark = iMap.getAllMarker(); 
			var addList = [];
			if(list){
				for(var i = 0;i < list.length;i++){
					var temp = list[i];
					var point = {lon:temp.lon,lat:temp.lat};
					var id = temp.id;
					var symbol = false;
					for(var z=0; z < allMark.length; z++){
						var markerId = allMark[z].getLabel().content.id;
						if(id == markerId){
							iMap.updateMarker(allMark[z],point,temp,this.clickHandler);
							allMark.splice(z,1);  
							symbol = true;
						}
					}
					if(!symbol){
						addList.push(list[i]);
					}
				}
	
				var delList = allMark; 
				if(delList.length){
					iMap.removeMarker(delList);
				}
				if(addList.length){
					this.setMapMarker(addList); 
				}else{
					this.initShowMarker();  
				}
			}
		},
	
		refreshData30s:function(){
			console.log("调用刷新方法刷新");
			var self = this;
			var timer  = setInterval(function(){ 
				console.log("刷新............");

				utils.ajaxFn(self.mapVehicleData);
				utils.ajaxFn(self.mapSiteData);
			},30000);		
		}
	}
})();

