(function($, window, undefined){

	var myMap = null;
	var __p__ = {curRes: {}, curMarPosit: {}};

	$.plusReady(function() {
		var curWeb = plus.webview.currentWebview();
		console.info("plusReady|siteAdd.js");
	});

	var mapOpts = {
		mapInit: mapInit,
		isGetLocation: true,
		isDrawCurMarker: true,
		drawCurMarkerCallback: drawCurMarkerCallback
	}
	
	__p__.curRes.point && (mapOpts.init.center = __p__.curRes.point);
	myMap = new ZMap('myMap', mapOpts);

	//-	地图初始化回调方法
	function mapInit(){
		//-	解析
		if(!__p__.curRes.point){
			myMap.getGeocoder(myMap.options.center, function(res){
				$('#J_curPositionTitle')[0].innerText = res.addressComponents.street || res.address;
				$('#J_curPosition')[0].value = res.address || '';
				__p__.curMarPosit = res || {};
			});
		}

		var isMove = false;
		myMap.bind('touchmove', function(){
			isMove = true;	
		});
		myMap.bind('touchend', function(){
			if(!isMove){
				var action = $('#J_positionWrap')[0].className.indexOf('hide')>-1 ? 'remove' : 'add';
				$('#J_positionWrap')[0].classList[action]('hide');
			}
			isMove = false;
		});
		var allName = CommonUtils.serializeFn('#J_positionWrap');
	}
	//-	绘制当前位置标注回调方法
	function drawCurMarkerCallback(marker){
		if(__p__.curRes.point){
			marker.setPosition(__p__.curRes.point);
			myMap.map.panTo(__p__.curRes.point);
		}
		setTimeout(function(){
			myMap.bind('tilesloaded', function(){
				var p = myMap.map.getCenter();
				marker.setPosition(p);
				myMap.getGeocoder(p, function(res){
					var positionTitle="未知地区";
					if(res.surroundingPois[0]){
						positionTitle=res.surroundingPois[0].title||res.addressComponents.street||res.address;
					}else{
						positionTitle=res.addressComponents.street||res.address;
					}
					$('#J_curPositionTitle')[0].innerText = positionTitle;
					$('#J_curPosition')[0].value = res.address || '';
					__p__.curMarPosit = res;
				})
			});
		}, 1000);
	}
	//-	自定义事件：更新位置信息
	window.addEventListener('refreshPosition', function(res) {
		console.error("refreshPosition");
		var r = res.detail || {};
		__p__.curMarPosit = r;
		$('#J_curPositionTitle')[0].innerText = r.title || r.addressComponents.street || r.address || '';
		$('#J_curPosition')[0].value = r.address || '';
		if(myMap&&r.point){
			myMap.map.setCenter( myMap.getPoint(r.point));
		}
	});
	//-	打开搜索地址页面
	$(document).on('tap', '#J_curPositionBox', function(){
		$.openWindow('positionInfo-search.html', {
			extras: {curMarPosit: __p__.curMarPosit,parentPage:"siteAdd.html"}
		});
	});
	
	$(document).on('tap', '#J_mapCenter', function(){
		if(myMap){
			myMap.map.setCenter( myMap.options.center );
		}
	});

	$(document).on('tap', '#J_save', function(){
		var siteName=$('#J_siteName')[0].value;
		var linkMan=$('#J_name')[0].value;
		var linkMan=$('#J_name')[0].value;
		var linkPhone=$('#J_phone')[0].value;
		var lng=__p__.curMarPosit.point.lng;
		var lat=__p__.curMarPosit.point.lat;
		var address=$('#J_curPosition')[0].value;
		var province=__p__.curMarPosit.addressComponents.province||__p__.curMarPosit.province;
		var city=__p__.curMarPosit.addressComponents.city||__p__.curMarPosit.city;
		var district=__p__.curMarPosit.addressComponents.district||__p__.curMarPosit.district;
		var coordinate=lng+","+lat;
		var param={
			name:siteName,
			coordinate:coordinate,
			address:address,
			linkMan:linkMan,
			linkPhone:linkPhone,
			province:province,
			city:city,
			district:district
		};
		CommonUtils.async({
			url: '/DispatcherAppWeb/consignor/site/create.json',
			data: param,
			success: _success
		})
	});

	function _success(res){
		if(res.code==0){
			mui.toast("成功"); 
		}else if(res.code==120001){
			mui.toast("省市区数据异常"); 
		}else if(res.code==120001){
			mui.toast("省市区数据异常"); 
		}else{
			mui.toast("未知错误"); 
		}
		mui.back();
	}

})(mui, window);