var Monitor={
	tableWidget:null,//表格控件
	deptWidget:null,//部门选择控件
	mapWidget:null,
	_vehicleMarkers:{},
	_emptyIcon: new BMap.Icon("/images/map_icon03.png", new BMap.Size(28,28)),
	_halfIcon: new BMap.Icon("/images/map_icon04.png", new BMap.Size(28,28)),
	_fullIcon: new BMap.Icon("/images/map_icon02.png", new BMap.Size(28,28)),
	_offlineIcon: new BMap.Icon("/images/map_icon01.png", new BMap.Size(28,28)),
	_siteIcon:new BMap.Icon("/images/map_icon05.png", new BMap.Size(28,35)),
	init:function(){
		var self=this;
		self._render();
		self._bind();
		self._listVehiclePositions();
		self._getVehicleCount();
		self._listSitePositions();
		setInterval(function(){
			self._clearOverlays();
			self._listVehiclePositions();
			self._getVehicleCount();
			self._listSitePositions();
		},30000);
	},
	
	_render:function(){
		var self=this;
		//设置底部菜单连接
		Footer.nav("配送监控","carrier/monitor/monitor.html");
		self.deptWidget=$("#deptWidget").DeptWidget({
			showAllItem:true //会多出一行 "全部" 的选择
		});
		self.mapWidget=$("#allmap").MapWidget({initPoint:{
			lon:113.953316,
			lat:22.558688
		}});
	},
	_bind:function(){
		var self=this;
		$(".searchBtn").unbind("click").click(function(){
			self._clearOverlays();
			self._listVehiclePositions();
			self._getVehicleCount();
			self._listSitePositions();
		});
	},
	_clearOverlays:function(){
		var self = this;
		self.mapWidget.map.clearOverlays();
	},
	_listVehiclePositions:function(){
		var self = this;
		var queryDeptId = self.deptWidget.getValue();
		var fieldId = $("#searchForm").find("#fieldId").val();
		var content = $("#searchForm").find("#content").val();
		var data = {queryDeptId:queryDeptId, fieldId:fieldId, content:content};
		console.log(data);
		CommonUtils.async({
			url:"/DispatcherWeb/carrier/monitor/listVehiclePositions.json",
			data:data,
			success:function(result){
				if(result.code != 0)
					return;
				var data = result.data;
				if(!data){
					return;
				}
				$(".vehicleList").empty();
				for(var i = 0; 	i<data.length ; i++){
					(function(arg){
						var vehicle = data[arg];
						var vehicleId = vehicle.vehicleId;
						var html = 
							'<li class="city-order-item" id="vehicle_'+ vehicleId +'">'+
							'	<a href="javascript:;" class="order">                 '+
							'		<i class="iconfont icon-cheliangziliao"></i>      '+
							'		<div class="tit">                                 '+
							'			<span class="plateNo">'+vehicle.plateNo+'</span>'+
							'			<span class="orderNo"></span>                 '+
							'		</div>                                            '+
							'		<div class="text">                                '+
							'			<span class="driverName">'+ vehicle.driverName +'</span>'+
							'			<span class="phoneNo">'+vehicle.driverPhone +'</span>'+
							'		</div>                                            '+
							'	</a>                                                  '+
							'	<div class="links">                                   '+
							'		<a href="javascript:;" onclick="Monitor.getOrderRoute(\''+ vehicle.orderNo +'\')">运输路线</a>               '+
							'		<a href="javascript:;" onclick="Monitor.getOrderDetail(\''+ vehicle.orderNo +'\')">订单详情</a>               '+
							'	</div>                                                '+
							'</li>                                                    ';
						$(".vehicleList").append(html);
						var loadRate = vehicle.loadRate;
						var onlineStatus = vehicle.driverOnlineStatus;
						var status = self.getStatusClass(onlineStatus, loadRate);
						var jqVehicle = $("#vehicle_" + vehicleId);
						jqVehicle.addClass('status-' + status);
						jqVehicle.find(".plateNo").text(vehicle.plateNo);
						if(vehicle.orderNo){
							jqVehicle.find(".orderNo").text("(订单号："+ vehicle.orderNo +")");
						}else{
							jqVehicle.find(".orderNo").remove();
							jqVehicle.find(".links").remove();
						}
						
						if(vehicle.lon && vehicle.lat){
							var point = {lng:vehicle.lon, lat:vehicle.lat, id:vehicle.vehicleId};
							self._addVehicleMarker(point, vehicle);
						}
						jqVehicle.unbind("click").click(function(){
							if(!$(this).hasClass('active')){
								$(".city-order-item").removeClass("active");
								self._openVehicleInfoWindow(i, vehicleId);
							}
						});
					})(i);
				}
			},
			error:function(result){
			}
		});
	},
	_addVehicleMarker:function(point, vehicle){
		var self = this;
		var status = self.getStatusClass(vehicle.driverOnlineStatus, vehicle.loadRate);
		var vIcon = self._getVehicleIconByStatus(status);
		var title = vehicle.plateNo +' | '+ vehicle.vehicleTypeName;
		self.mapWidget.addMarker({point:point, icon:vIcon, enableDragging:false, title:title, panTo:false}, function(marker){
			self._vehicleMarkers[vehicle.vehicleId] = marker;
			var onlineStatus = '离线';
			if(vehicle.onlineStatus == 1){
				onlineStatus = '在线';
			}
			var sContent = 
				'<div class="city-map-infoWindow">                                                               '+    
				'	<ul class="city-tap clearfix">                                                               '+    
				'		<li class="tap-item active">                                                             '+
				'			<a href="javascript:;"  class="vehicleDetailBtn" onclick="Monitor._swithcInfoWindow(1)">车辆详情</a>'+
				'		</li>                                                                                    ';
			if(vehicle.orderNo){
				sContent += 
					'		<li class="tap-item">                                                                    '+
					'			<a href="javascript:;" class="orderDetailBtn" onclick="Monitor._swithcInfoWindow(2)">订单详情</a>'+
					'		</li>                                                                                    ';
			}
			var loadStatus = self.getLoadStatusNameByLoadRate(vehicle.loadRate);
			
			var state = ""
			if(vehicle.state == 0){
				state = "离线";
			}else if(vehicle.state == 1){
				state = "在线";
			}else if(vehicle.state == 2){
				state = "休眠";
			}
			
			sContent += 	
				'	</ul>                                                                                        '+    
				'	<div class="info-lists">                                                                     '+    
				'		<ul>                                                                                     '+    
				'			<li class="info-item vehicleInfo" style="display: block;">                           '+    
				'				<p>车辆：'+ vehicle.plateNo +' | '+ vehicle.vehicleTypeName + '</p>              '+                         
				'				<p>驾驶员：'+ vehicle.driverName +'('+ vehicle.driverPhone+')' + onlineStatus + '</p>'+                                               
				'				<p>车辆状态：'+ state +'</p>                                                      '+    
				'				<p>当前速度：'+ vehicle.speed +'km/h</p>                                         '+                     
				'				<p>当前位置：'+ vehicle.position +'</p>                                          '+      
				'				<div class="status">                                                             '+    
				'					<span class="status-desc">载货状态:</span>                                   '+    
				'					<div class="status-main">                                                    '+    
				'						<span class="status-text"> '+ loadStatus +'</span>               		 '+
				'					</div>                                                                       '+    
				'				</div>                                                                           '+    
				'			</li>                                                                                ';
			if(vehicle.orderNo){
				sContent += 
					'			<li class="info-item orderInfo" >                            						'+    
					'				<p>订单号：'+ vehicle.orderNo +'（配送中）</p>                                    '+               
					'				<p>提货点：'+ vehicle.startSiteName + '('+ vehicle.startAddress +')'+'</p>       '+                                
					'				<p>提货时间: '+ vehicle.pickupTime +'</p>                                        '+         
					'				<p>终点：'+ vehicle.endSiteName +'('+vehicle.endAddress+')'+'</p>               '+                          
					'				<p>预计送达时间：'+ vehicle.preFinishTime +'</p>                                 '+                
					'				<p>货物：'+ vehicle.goodsTypeName +' | '+ vehicle.packageNum +'件</p>            '+                                                 
					'				<p>发货人：'+vehicle.senderName+'（'+ vehicle.senderPhone+'）</p>                    '+                               
					'			</li>                                                                                ';   
			}
			sContent +=
				'		</ul>                                                                                    '+    
				'	</div>                                                                                       '+    
				'	<div class="info-btns clearfix">                                                             '+    
				'		<a href="javascript:;" class="fr" onclikc="Monitor.getOrderRoute("'+ vehicle.orderNo +'")">运输路线</a>'+    
				'		<a href="javascript:;" class="fr" onclick="Monitor.getOrderDetail("'+ vehicle.orderNo +'")">订单详情</a>'+    
				'	</div>                                                                                       '+    
				'</div>                                                                                          ';
			marker.disableDragging();
			marker.addEventListener("click", function(){
				var infoWindow = new BMap.InfoWindow(sContent);  // 创建信息窗口对象
				this.openInfoWindow(infoWindow);
			});
		});
	},
	_swithcInfoWindow :function(v){
		if(v == 1){
			$(".vehicleInfo").show();
			$(".orderInfo").hide();
			$(".vehicleDetailBtn").parents(".tap-item").addClass("active");
			$(".orderDetailBtn").parent(".tap-item").removeClass("active");
			
		} else if(v == 2){
			$(".vehicleInfo").hide();
			$(".orderInfo").show();
			$(".vehicleDetailBtn").parents(".tap-item").removeClass("active");
			$(".orderDetailBtn").parent(".tap-item").addClass("active");
		}
	},
	_openVehicleInfoWindow: function(idx, vehicleId){
		var self = this;
		var marker = self._vehicleMarkers[vehicleId];
		marker.V.click();
	},
	getStatusClass: function(onlineStatus, loadRate){
		onlineStatus = parseInt(onlineStatus);
		loadRate = parseInt(loadRate);
		var clss = "";
		if(onlineStatus == 0){
			clss = "offline"
		} else{
			if(loadRate == 0){
				clss = "empty";
			}
			else if(loadRate > 0 && loadRate < 90){
				clss = "half";
			}
			else if(loadRate > 90){
				clss = "full";
			}
		}
		
		return clss;
	},
	_getVehicleIconByStatus:function(status){
		var self = this;
		if(status == "empty"){
			return self._emptyIcon;
		}
		if(status == "half"){
			return self._halfIcon;
		}
		if(status == "full"){
			return self._fullIcon;
		}
		if(status == "offline"){
			return self._offlineIcon;
		}
		return self.emptyIcon;
	},
	_getVehicleCount:function(){
		var self = this;
		var queryDeptId = self.deptWidget.getValue();
		var fieldId = $("#searchForm").find("#fieldId").val();
		var content = $("#searchForm").find("#content").val();
		var data = {queryDeptId:queryDeptId, fieldId:fieldId, content:content};
		CommonUtils.async({
			url:"/DispatcherWeb/carrier/monitor/getVehicleCount.json",
			data:data,
			success:function(result){
				if(result.code != 0)
					return;
				var data = result.data;
				$("#total").text("("+data.total+")");
				$("#noLoad").text("("+data.noLoad+")");
				$("#halfLoad").text("("+data.halfLoad+")");
				$("#fullLoad").text("("+data.fullLoad+")");
				$("#offLine").text("("+data.offLine+")");
			},
			error:function(result){
			}
		});
	},
	getLoadStatusNameByLoadRate: function(loadRate){
		loadRate = parseInt(loadRate);
		var statusName = "";
		if(loadRate == 0){
			statusName = "空载";
		}
		else if(loadRate > 0 && loadRate < 90){
			statusName = "半载";
		}
		else if(loadRate > 90){
			statusName = "满载";
		}
		return statusName;
	},
	/*_listOrders:function(){
		CommonUtils.async({
			url:"/DispatcherWeb/carrier/monitor/listOrderPositions.json",
			data:data,
			success:function(result){
				var data = result.data;
				for(var i=0; i<data.length; i++){
					var order = data[i];
					self._addOrderMarker(order);
				}
			},
			error:function(result){
			}
		});
	},
	_addOrderMarker:function(order){
		
	},*/
	_listSitePositions:function(){
		var self = this;
		CommonUtils.async({
			url:"/DispatcherWeb/carrier/monitor/listSitePositions.json",
			data:{},
			success:function(result){
				if(result.code != 0)
					return;
				var data = result.data;
				if(data){
					for(var i=0; i<data.length; i++){
						var site = data[i];
						if(site.coordinate){
							self._addSiteMarker(site);
						}
					}
				}
			},
			error:function(result){
			}
		});
	},
	_addSiteMarker:function(site){
		var self = this;
		var lon = site.coordinate.split(",")[0];
		var lat = site.coordinate.split(",")[1];
		var point = {lng:lon, lat:lat};
		var vIcon = self._siteIcon;
		var title = site.siteName + "<br>" + site.address +"<br> "+ site.linkName + "("+ site.linkPhone +")";
		self.mapWidget.addMarker({point:point, icon:vIcon, enableDragging:false, panTo:false, title:title});
	},
	getOrderDetail:function(orderNo){
		window.open("/dsp/carrier/orderMgmt/orderDetail.html?orderNo=" + orderNo);
	},
	getOrderRoute:function(orderNo){
		window.open("/dsp/carrier/monitor/route.html?orderNo=" + orderNo);
	}
}
$(function(){
	Monitor.init();
});