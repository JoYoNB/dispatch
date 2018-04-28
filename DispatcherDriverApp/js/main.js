//mui.init({
//	swipeBack: false,
//	gestureConfig: {
//		doubletap: true
//	}
//});
//初始化区域滚动
mui('.mui-scroll-wrapper').scroll();

var menu = null,
	main = null,
	showMenu = false,
	contentWebview = null,
	isInTransition = false;

mui.plusReady(function() {
	main = plus.webview.currentWebview();
	main.addEventListener('maskClick', closeMenu); //点遮罩时关闭menu
//	 动态改变webview的侧滑返回功能：
//	 关闭侧滑返回功能
	main.setStyle({
		'popGesture': 'none'
	});
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
	if (plus.webview.getWebviewById('personal')) {
		plus.webview.getWebviewById('personal').close();
	}

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
	if (showMenu) {
		closeMenu();
	} else {
		openMenu();
	}
};

//首页返回键处理，处理逻辑：1秒内，连续两次按返回键，则退出应用
var first = null;
mui.back = function() {
	if (showMenu) {
//		console.log(mklog() + "|main:按键:back-关闭菜单");
		closeMenu();
	} else {
		//首次按键，提示‘再按一次退出应用’
		if (!first) {
			first = new Date().getTime();
//			mui.toast(language.exitMsg);
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

var isPause=false;
(function(){
	// 监听应用活动状态信息
	document.addEventListener("pause", function(){
		console.log("应用从前台切换到后台");
		isPause = true;
	})
	
	// 监听应用活动状态信息
	document.addEventListener("resume", function() {
		console.log("应用从后台切换到前台");
		isPause = false;
	})
})();


//地图相关
var iMap = new ZMap();
	iMap.init("map");


var mapData = {
	url:'./js/data/mapData.json',
//	url:httpServer+'js/data/mapData.json',
	data:{},
	dataType:"json",
	type:"get",
	success:setMapMark,
	error:function(error){console.log(error)}
}
utils.loadData(mapData);
//画标记点 point,iconName,id,clickHandler
function setMapMark(result){
	if(result && result.data){
		var list = result.data;
		
		iMap.clearOverlays();
		
		for(var i = 0;i < list.length;i++){
			var temp = list[i];
			var point = {lon:temp.lon,lat:temp.lat};
			var iconName = temp.type ? temp.type + "_icon" : "goods_site_icon";
			var id = temp.vehicleId ? temp.vehicleId  : temp.siteId;
			var type = temp.vehicleId ? "车辆" : "提货点";
			iMap.setMaker(point,iconName,id,type,clickHandler);
		}
	}
}


function clickHandler(e,id,type){
	console.log(e);
	console.log("id 是："+id);
//	alert("类型是"+type+",id 是："+id);
	$(".mui-main-menu").css({"box-shadow":"none","border-top":"1px solid #e2e2e2"});
	$(".map-order-details").show();
	$(".map-order-details").animate({"bottom":"65px"});
	
}

document.getElementById("mapOrderDetails").addEventListener("swipedown",function(){
	console.log("你向下滑动了");
	$(".map-order-details").show();
	$(".map-order-details").animate({"bottom":"-200px"},function(){
		$(".map-order-details").hide();
		$(".mui-main-menu").css("box-shadow","0 -2px 7px 0px #c1c1c1");
	});
});


//删除可以使用  getIcon()	Icon	返回标注所用的图标对象？ 