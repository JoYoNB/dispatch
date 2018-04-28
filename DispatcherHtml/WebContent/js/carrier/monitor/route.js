var Route={
	mapWidget:null,
	_aIcon: new BMap.Icon("/images/A.png", new BMap.Size(28,32), {anchor:new BMap.Size(14,32)}),
	_1Icon: new BMap.Icon("/images/1.png", new BMap.Size(28,32), {anchor:new BMap.Size(14,32)}),
	_2Icon: new BMap.Icon("/images/2.png", new BMap.Size(28,32), {anchor:new BMap.Size(14,32)}),
	_3Icon: new BMap.Icon("/images/3.png", new BMap.Size(28,32), {anchor:new BMap.Size(14,32)}),
	_4Icon: new BMap.Icon("/images/4.png", new BMap.Size(28,32), {anchor:new BMap.Size(14,32)}),
	_5Icon: new BMap.Icon("/images/5.png", new BMap.Size(28,32), {anchor:new BMap.Size(14,32)}),
	_6Icon: new BMap.Icon("/images/6.png", new BMap.Size(28,32), {anchor:new BMap.Size(14,32)}),
	_7Icon: new BMap.Icon("/images/7.png", new BMap.Size(28,32), {anchor:new BMap.Size(14,32)}),
	_8Icon: new BMap.Icon("/images/8.png", new BMap.Size(28,32), {anchor:new BMap.Size(14,32)}),
	_9Icon: new BMap.Icon("/images/9.png", new BMap.Size(28,32), {anchor:new BMap.Size(14,32)}),
	_10Icon: new BMap.Icon("/images/10.png", new BMap.Size(28,32), {anchor:new BMap.Size(14,32)}),
	_bIcon: new BMap.Icon("/images/B.png", new BMap.Size(28,32), {anchor:new BMap.Size(14,32)}),
	_drivingRoute: null,
	init:function(){
		var self=this;
		self._render();
		self._bind();
		var orderNo = CommonUtils.getParam("orderNo");
		self._getOrderInfo(orderNo);
	},
	
	_render:function(){
		var self=this;
		//设置底部菜单连接
		Footer.nav("配送监控","carrier/monitor/monitor.html");
		
		self.deptWidget=$("#deptWidget").DeptWidget({
			showAllItem:true //会多出一行 "全部" 的选择
		});
		
		self.mapWidget=$("#routemap").MapWidget({initPoint:{
			lon:113.953316,
			lat:22.558688
		}});
	},
	_bind:function(){
		var self=this;
		
	},
	_getOrderInfo: function(orderNo){
		var self = this;
		var data = {orderNo:orderNo};
		CommonUtils.async({
			url:"/DispatcherWeb/carrier/order/getOrder.json",
			data:data,
			success:function(result){
				if(result.code != 0)
					return;
				var order = result.data.order;
				var sites = result.data.sites;
				var orderStatus = order.orderStatus;
				if(orderStatus == 80 || orderStatus == 90){ // 配送中、已完成 画轨迹
					var trackPoints = [];
					var hasMore = true;
					data.pageNum = 1;
					while(hasMore){
						CommonUtils.async({
							url:"/DispatcherWeb/carrier/monitor/getOrderTransportRoute.json",
							async:false,
							data:data,
							success:function(result){
								if(result.code != 0)
									return;
								if(result.list && list.length > 0){
									for(var i=0; i<list.length; i++){
										var gps = list[i];
										var lonlat = {lon: gps.lon, lat:gps.lat};
										trackPoints.push(lonlat);
									}
									self.mapWidget._api.addLine(trackPoints);
									trackPoints = [];
								}
								hasMore = result.hasMore;
								data.pageNum = data.pageNum + 1;
							},
							error:function(result){
							}
						});
					}
				}else{  // 画路径规划
					self._sortOrderSites(sites);
				}
				self._addSitesMarker(sites);
				self._setOrderAndSites(order, sites);
				
			},
			error:function(result){
			}
		});
	},
	_sortOrderSites: function(sites){
		var self = this;
		self._resultSites = [];
		var startSite = null;
		var endSite = null;
		var waySites = [];
		for(var i=0; i<sites.length; i++){
			var site = sites[i];
			if(site.siteType == 1){
				startSite = site;
			} else	if(site.siteType == 3){
				endSite = site;
			} else{
				waySites.push(site);
			}
		}
		self._drivingRoute = new BMap.DrivingRoute(self.mapWidget.map, {renderOptions:{map: self.mapWidget.map, autoViewport: true}});
		var startPoint = self._getBmapPointBySite(startSite);
		var endPoint = self._getBmapPointBySite(endSite);
		var waypoints = [];
		for(var i=0; i< waySites.length; i++){
			waypoints.push(self._getBmapPointBySite(waySites[i]));
		}
		self._drivingRoute.setPolicy(BMAP_DRIVING_POLICY_LEAST_TIME);
		self._drivingRoute.setMarkersSetCallback(function(pois){
			self._onMarkerSet(pois);
		});
		self._drivingRoute.setPolylinesSetCallback(function(routes){
			self._onPolyLinesSet(routes);
		});
		self._drivingRoute.setSearchCompleteCallback(function(results){
			self._onSearchComplete(results);
		});
		self._drivingRoute.search(startPoint, endPoint, {waypoints:waypoints});
		return sites;
	},
	_onMarkerSet: function(pois){
		var self = this;
		for(var i=0; i<pois.length; i++){
			self.mapWidget.map.removeOverlay(pois[i].marker);
			self.mapWidget.map.removeOverlay(pois[i].Nm);
		}
	},
	_onPolyLinesSet:function(routes){ 
		var self = this;
		/*if(self._orderStatus != 80 || self._orderStatus !=90){
			for(var i=0; i<routes.length; i++){
				var polyline = routes[i].getPolyline();
				self.mapWidget.map.removeOverlay(polyline);
			}
		}*/
	},
	_onSearchComplete:function(results){
	},
	_addSitesMarker:function(sites){
		var self = this;
		if(!sites)
			return;
		for(var i=0; i<sites.length; i++){
			var site = sites[i];
			var siteType = site.siteType;
			var icon = null;
			if(siteType == 1){
				icon = self._aIcon;
			} else if(siteType == 3){
				icon = self._bIcon;
			} else{
				icon = self['_'+i+'Icon'];
			}
			var title = site.siteName;
			var lon = site.coordinate.split(",")[0];
			var lat = site.coordinate.split(",")[1];
			var point = {lng:lon, lat:lat, id:site.siteId};
			self.mapWidget.addMarker({point:point, icon:icon, enableDragging:false, title:title});
		}
	},
	_setOrderAndSites:function(order, sites){
		// 设置order信息
		$("#orderNo").text(order.orderNo);
		$("#distance").text(order.distance);
		// 显示site信息
		var html = "";
		var display = "";
		var iconClss = "";
		var idxNo = "";
		for(var i=0; i<sites.length; i++){ 
			var unload = ""; 
			var site = sites[i];
			var siteType = site.siteType;
			if(siteType == 1){ // 起点
				iconClss = "first";
				display = "none";
				idxNo = "A";
			} else if( siteType == 3){ // 终点
				iconClss = "end";
				display = "block";
				idxNo = "B";
			} else{
				iconClss = "";
				display = "block";
				idxNo = i;
			}
			if(site.unloadPackageNum){
				unload += site.unloadPackageNum + "件|";
			}
			if(site.unloadVolume){
				unload += site.unloadVolume + "方|";
			}
			if(site.unloadWeight){
				unload += site.unloadWeight + "吨";
			}
			html += 
				'<li class="transport-item">                                 '+
				'	<span class="tap '+ iconClss+'">'+ idxNo+'</span>        '+
				'	<div class="name">'+ site.siteName +'</div>              '+
				'	<div class="text2">'+ site.address +'</div>              '+
				'	<div class="text2">                                      '+
				'		收货人：'+ site.receiverName +' | '+ site.receiverPhone+
				'	</div>                                                   '+
				'	<div class="text2" style="display:'+display+'">          '+
				'		卸货量：'+ unload +'                                  '+
				'	</div>                                                   '+
				'</li>                                                       ';
		}
		$(".city-transport-details").append(html);
	},
	_getBmapPointBySite : function(site){
		if(!site.coordinate){
			return null;
		}
		var lon = site.coordinate.split(",")[0];
		var lat = site.coordinate.split(",")[1];
		return new BMap.Point(lon, lat);
	}
}
$(function(){
	Route.init();
});