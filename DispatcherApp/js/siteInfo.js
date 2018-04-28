(function($, window, undefined){
	var siteId=plus.webview.currentWebview().siteId;
	var myMap = null;
	
	$.plusReady(function() {
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
		console.info(JSON.stringify(result));
		if(result.code==0){
			var data=result.data;
			$("#J_siteName")[0].value=data.siteName;
			$("#J_curPosition")[0].value=data.address;
			$("#J_name")[0].value=data.linkName;
			$("#J_phone")[0].value=data.linkPhone;
			var coordinate=data.coordinate;
			coordinate=coordinate.split(",");
			console.info(coordinate);
			var lng=parseFloat(coordinate[0]);
			var lat=parseFloat(coordinate[1]);
			myMap.map.setCenter(myMap.getPoint({lng:lng,lat:lat}));
			myMap.drawMarker({lng:lng,lat:lat});
		}else{
			return;
		}
	}
	
	//-	地图初始化回调方法
	function mapInit(){
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
	}

	//-	绘制当前位置标注回调方法
	function drawCurMarkerCallback(marker){
	}

	$(document).on('tap', '#J_goback', function(){
		 $.back();
	});
})(mui, window);