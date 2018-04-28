(function($, window, undefined){
	var siteId=plus.webview.currentWebview().siteId;
	var myMap = null;
	var __p__ = {curRes: {}, curMarPosit: {}};

	$.plusReady(function() {
		var curWeb = plus.webview.currentWebview();
		console.info("plusReady|siteUpdate.js");
		var mapOpts = {
			mapInit: mapInit
		}
		myMap = new ZMap('myMap', mapOpts);
		ajaxSiteDetail();
	});
	function ajaxSiteDetail(){
		CommonUtils.async({
			url: '/DispatcherAppWeb/consignor/site/getSiteInfo.json',
			data: {siteId:siteId},
			success: orderDetailSuccess,
			error: orderDetailSuccess
		});
	}
	function orderDetailSuccess(result){
		if(result.code==0){
			var data=result.data;
			$("#J_siteName")[0].value=data.siteName;
			$("#J_curPosition")[0].value=data.address;
			$("#J_name")[0].value=data.linkName;
			$("#J_phone")[0].value=data.linkPhone;
			var coordinate=data.coordinate;
			coordinate=coordinate.split(",");
			var lng=parseFloat(coordinate[0]);
			var lat=parseFloat(coordinate[1]);
			myMap.map.setCenter(myMap.getPoint({lng:lng,lat:lat}));
			myMap.drawMarker({lng:lng,lat:lat},function(marker){
				if(__p__.curRes.point){
					marker.setPosition(__p__.curRes.point);
					myMap.map.panTo(__p__.curRes.point);
				}
				var p = myMap.map.getCenter();
				marker.setPosition(p);
				myMap.getGeocoder(p, function(res){
					var positionTitle="未知区域";
					if(res.surroundingPois[0]){
						positionTitle=res.surroundingPois[0].title;
					}else{
						positionTitle=res.addressComponents.street||res.address;
					}
					$('#J_curPositionTitle')[0].innerText = positionTitle;
					$('#J_curPosition')[0].value = res.address || '';
					__p__.curMarPosit = res;
				})
			});
		}else{
			return;
		}
	}
	
	__p__.curRes.point && (mapOpts.init.center = __p__.curRes.point);
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
		myMap.bind('touchend', function(){
			if(!isMove){
				var action = $('#J_positionWrap')[0].className.indexOf('hide')>-1 ? 'remove' : 'add';
				$('#J_positionWrap')[0].classList[action]('hide');
			}
			isMove = false;
		});


		var allName = CommonUtils.serializeFn('#J_positionWrap');
		
		myMap.bind('dragend', function(){
			isMove = true;	
			myMap.clearOverlaysFn();
			myMap.drawMarker(myMap.map.getCenter(),function(marker){
				if(__p__.curRes.point){
					marker.setPosition(__p__.curRes.point);
					myMap.map.panTo(__p__.curRes.point);
				}
				setTimeout(function(){
					myMap.bind('tilesloaded', function(){
						var p = myMap.map.getCenter();
						marker.setPosition(p);
						myMap.getGeocoder(p, function(res){
							var positionTitle="未知区域";
							if(res.surroundingPois[0]){
								positionTitle=res.surroundingPois[0].title;
							}else{
								positionTitle=res.addressComponents.street||res.address;
							}
							$('#J_curPositionTitle')[0].innerText = positionTitle;
							$('#J_curPosition')[0].value = res.address || '';
							__p__.curMarPosit = res;
						})
					});
				}, 1000);
			});
		});
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
					$('#J_curPositionTitle')[0].innerText = res.addressComponents.street || res.address;
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

	$(document).on('tap', '#J_curPositionBox', function(){
		$.openWindow('test.html', {
			extras: {curMarPosit: __p__.curMarPosit,parentPage:"siteUpdate.html"}
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
		var province=__p__.curMarPosit.province||__p__.curMarPosit.addressComponents.province;
		var city=__p__.curMarPosit.city||__p__.curMarPosit.addressComponents.city;
		var district=__p__.curMarPosit.district||__p__.curMarPosit.addressComponents.district;
		var coordinate=lng+","+lat;
		var param={
			siteId:siteId,
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
			url: '/DispatcherAppWeb/consignor/site/update.json',
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