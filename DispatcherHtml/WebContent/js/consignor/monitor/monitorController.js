var monitorController={
	mapWidget:null, //地图控件
	localSearch:null, //检索函数
	markerArr:null, //地图上显示的点集
	
	init:function(){
		var self=this;
		self._render();
		self._bind();
	},
	_render:function(){
		var self = this;
		
		//设置底部菜单连接
		Footer.nav("配送监控","consignor/monitor/monitorController.html");
		self.mapWidget=$("#myMap").MapWidget({initPoint:{
			lon:113.953316,
			lat:22.558688
		}});
		self.queryOrder();
	},
	_bind:function(){
		var self=this;	
		$("#query").unbind('click').click(function(){
			self.queryOrder();
		});
		$('#allOrder').unbind('click').click(function(){
			$('#noPickup').parent().removeClass('active');
			$('#distribution').parent().removeClass('active');
			$('#allOrder').parent().addClass('active');
			$('#orderList li').removeClass('hide');
		});
		$('#noPickup').unbind('click').click(function(){
			$('#allOrder').parent().removeClass('active');
			$('#distribution').parent().removeClass('active');
			$('#noPickup').parent().addClass('active');
			$('li.status70').removeClass('hide');
			$('li.status80').addClass('hide');
		});
		$('#distribution').unbind('click').click(function(){
			$('#noPickup').parent().removeClass('active');
			$('#allOrder').parent().removeClass('active');
			$('#distribution').parent().addClass('active');
			$('li.status70').addClass('hide');
			$('li.status80').removeClass('hide');
		});
	},
	queryOrder:function(){
		var self=this;
		var param={};
		var startName = $('#startName').val();
		var middleOrEnd = $('#middleOrEnd').val();
		var orderNo = $('#orderNo').val();
		if(startName){
			param.startSiteName = startName;
		}
		if(middleOrEnd){
			param.middleOrEnd = middleOrEnd;
		}
		if(orderNo){
			param.orderNo = orderNo;
		}
		console.log("param",param);
		CommonUtils.async({
			url:"/DispatcherWeb/consignor/dispatchingMonitor/orderList.json",
			data:param,
			success:function(result){
				if(result.code==0){
					console.log(result.data);
					//订单列表展示
					if(result.data){
						$('#orderList').empty();
						$('#allOrder').children('span').text('('+result.data.length+')');
						$('#noPickup').children('span').empty();
						$('#distribution').children('span').empty();
						var _html = '';
						var nopickNum = 0;
						var distribution = 0;
						for(var i in result.data){
							var _data = result.data[i];
							var statusClass = '';
							if(_data.orderStatus==70){//未提货
								statusClass = 'status70';
								nopickNum++;
							}else if(_data.orderStatus==80){ //配送中
								statusClass = 'status80';
								distribution++;
							}
							_html += '<li class="city-order-item '+statusClass+'">'
								+'<a href="javascript:;" class="order">'
								+'<i class="iconfont iconfont-order"></i>'
								+'<div class="tit">订单号：'+_data.orderNo+'</div>'
								+'<div class="text"><span>'+(_data.driverName?_data.driverName:"--")+'</span>'
								+'<span>'+(_data.driverPhone?_data.driverPhone:"--")
								+'</span><span>'+(_data.plateNo?_data.plateNo:"--")+'</span></div>'
								+'</a>'
								+'<div class="links">'
								+'<a href="javascript:monitorController.transportRoute(\''+_data.orderNo+'\');">运输路线</a>'
								+'<a href="javascript:monitorController.orderDetail(\''+_data.orderNo+'\');">订单详情</a>'
								+'</div>'
								+'</li>';
						}
						if(_html){
							$('#noPickup').children('span').text('('+nopickNum+')');
							$('#distribution').children('span').text('('+distribution+')');
							$('#orderList').html(_html);
						}
						
						//地图标注
						self.mapMarker(result.data);
					}
				}else{
					MessageUtil.alert("查询失败");
				}
			},
			error:function(result){
				MessageUtil.info("查询失败！");
			}
		});
	},
	//订单详情
	orderDetail : function(id){
		window.location = "/"+Constant.PROJECT_NAME+"/consignor/order/orderDetail.html?id="+id;
	},
	//运输路线
	transportRoute : function(id){
		var self = this;
		var param = {
			orderNo:id,
			ifLog:'no'
		};
		console.log("param",param);
		CommonUtils.async({
			url:"/DispatcherWeb/consignor/order/orderDetail.json",
			data:param,
			success:function(result){
				if(result.code == 0){
					if(result.data){
						console.log(result.data);
						self.transportDetail(result.data);
					}
				}else{
					MessageUtil.info("查询失败！");
				}
			},
			error:function(result){
				MessageUtil.info("查询失败！");
			}
		});	
	},
	mapMarker : function(data){
		var self = this;
		self.mapWidget.clear();
		var pointTest = [{
			bd_lon:113.934423,
			bd_lat:22.556659
		},{//113.943119,22.5624
			bd_lon:113.943119,
			bd_lat:22.5624
		}];
		for(var i in data){
			var _data = data[i];
			if(i<pointTest.length){
				_data.vehicleInfo = pointTest[i];
			}
			console.log(_data);
			var transportNum = '--';
			if(_data.weight){
				transportNum = _data.weight+'吨';
			}else if(_data.volume){
				transportNum = _data.volume+'方';
			}else if(_data.num){
				transportNum = _data.num+'件';
			}
			var startName = _data.startName?_data.startName:"--";
			var startAddress = _data.startAddress?_data.startAddress:"--";
			var endName = _data.endName?_data.endName:"--";
			var endAddress = _data.endAddress?_data.endAddress:"--";
			var goodsName = _data.goodsName?_data.goodsName:"--";
			var pickupTime = _data.pickupTime?_data.pickupTime:'--';
			var startMan = _data.startMan?_data.startMan:'--';
			var startPhone = _data.startPhone?_data.startPhone:'--';
			if(_data.startCoordinate){
				var coordinate = _data.startCoordinate.split(',');
				var lng = coordinate[0];
				var lat = coordinate[1];
				if(lng && lat){ //坐标正确才画点
					var _html = '<div class="city-map-infoWindow"><div class="info-lists" style="height: auto;width: auto;"><ul>'
						+'<li class="info-item" style="display: block;"></li>'
						+'<p>起点名称：'+startName+'</p>'
						+'<p>地址：'+startAddress+'</p>'
						+'<p>货物：'+goodsName+'|'+transportNum+'</p>'
						+'</ul></div></div>';
					console.log(_html);
					//订单提货点
					self.mapWidget.addMarker({
							icon:{
								url:'/images/map_icon05.png',
								width:34,
								height:34,
								anchor:{
									width:17,
									height:34
								}
							},
							panTo:false,
							enableDragging:false,
							point:{
								lng:lng,
								lat:lat
							},
							infowindow:{
								content:_html,
								opts:{
									offset:new BMap.Size(0,-17)
								}	
							}
					});
				}
			}
			
			if(_data.vehicleInfo&&_data.vehicleInfo.bd_lon&&_data.vehicleInfo.bd_lat){ //车辆最新位置信息
				//运输明细
				var logHtml = '';
				if(_data.logs&&_data.logs.length>0){
					for(var i in _data.logs){
						var log = _data.logs[i];
						var status = '--';
						var content = '--';
						//10待发布、20已发布、30已失效、40已接单、50已分配、60已取消、70待提货、80配送中、90已结束
						switch(log.orderStatus){
						case 10:status = '待发布';break;
						case 20:status = '已发布';break;
						case 30:status = '已失效';break;
						case 40:status = '已接单';break;
						case 50:status = '已分配';break;
						case 60:status = '已取消';break;
						case 70:status = '待提货';break;
						case 80:status = '配送中';break;
						case 90:status = '已结束';break;
						default:;
						}
						logHtml +='<p><span class="time">'+log.operateTime+'</span></p>'
						+'<p>【'+status+'】'+content+'</p>';
					}
				}
				
				var _status = '配送中';
				if(_data.orderStatus == 70){
					_status = '待提货';
				}
				var plateNo = _data.plateNo?_data.plateNo:'--';
				var driverName =_data.driverName?_data.driverName:'--';
				var driverPhone = _data.driverPhone?_data.driverPhone:'--';
				var vehicleType = _data.vehicleType?_data.vehicleType:'--';
				var speed = _data.vehicleInfo.speed?_data.vehicleInfo.speed:'--';
				var position = _data.vehicleInfo.position?_data.vehicleInfo.position:'--';
				//配送车辆
				var _content = '<div class="city-map-infoWindow">'
				+'<ul class="city-tap clearfix">'
				+'<li class="tap-item active"><a onclick="javascript:monitorController.infoSwitch(this,0);">订单详情</a></li>'
				+'<li class="tap-item"><a onclick="javascript:monitorController.infoSwitch(this,1);">车辆详情</a></li>'
				+'<li class="tap-item"><a onclick="javascript:monitorController.infoSwitch(this,2);">运输明细</a></li>'
				+'</ul>'
				+'<div class="info-lists"><ul>'
					+'<li class="info-item" id="orderDetail" style="display: block;">'
					+'<p>订单号：'+_data.orderNo+'（'+_status+'）</p>'
					+'<p>提货点：'+startName+'（'+startAddress+'）</p>'
					+'<p>终点：'+endName+'（'+endAddress+'）</p>'
					+'<p>货物：'+goodsName+'|'+transportNum+'</p>'
					+'<p>提货时间：'+pickupTime+'</p>'
					+'<p>发货人：'+startMan+'（'+startPhone+'）</p>'
					+'</li>'
					+'<li class="info-item" id="vehicleDetail">'
					+'<p>车辆：'+plateNo+' | '+vehicleType+'</p>'
					+'<p>驾驶员：'+driverName+'（'+driverPhone+'）在线</p>'
					+'<p>车辆状态：运行</p>'
					+'<p>当前速度：'+speed+'km/h</p>'
					+'<p>当前位置：'+position+'</p>'
					+'</li>'
					+'<li class="info-item" id="transportDetail">'
					+logHtml
					+'</li>'
				+'</ul></div>'
				+'<div class="info-btns">'
				+'<a href="javascript:monitorController.transportRoute(\''+_data.orderNo+'\');">运输路线</a>'
				+'<a href="javascript:monitorController.orderDetail(\''+_data.orderNo+'\');">订单详情</a>'
				+'</div>'
				+'</div>';
				var icon_url = '';
				if(_data.orderStatus==70){
					icon_url = '/images/map_icon03.png';
				}else{
					icon_url = '/images/map_icon04.png';
				}
				self.mapWidget.addMarker({
					icon:{
						url:icon_url,
						width:28,
						height:28
					},
					panTo:false,
					enableDragging:false,
					point:{
						lng:_data.vehicleInfo.bd_lon,
						lat:_data.vehicleInfo.bd_lat
					},
					infowindow:{
						content:_content,
						opts:{
							offset:new BMap.Size(0,-17)
						}	
					}
				});
			}
		}
	},
	infoSwitch: function(obj,type){
		$(obj).parent().addClass('active').siblings().removeClass('active');
		$('li.info-item').hide().eq(type).show();
	},
	transportDetail:function(data){
		var self = this;
		var orderNo = data.orderNo?data.orderNo:'--';
		var carrier = data.carrierName?data.carrierName:'--';
		var plateNo = data.plateNo?data.plateNo:'--';
		//驾驶员
		var driver = !data.driverName?'--':data.driverName;
		if(data.driverPhone){
			driver += '('+data.deriverPhone+')';
		}
		//车辆最新位置
		var position = '--';
		var speed = 0;
		if(data.vehicle){
			if(data.vehicle.position){
				position = data.vehicle.position;
			}
			if(data.vehicle.speed){
				speed = data.vehicle.speed;
			}
		}
		//货物类型
		var goodsType = data.goodsType?data.goodsType:'--';
		//运输量
		var transportNum = '--';
		if(data.weight){
			transportNum = data.weight+'吨';
		}else if(data.volume){
			transportNum = data.volume+'方';
		}else if(data.packageNum){
			transportNum = data.packageNum+'件';
		}
		var topHtml = '<p>订单编号：'+orderNo+'</p>'
			+'<p>配送企业：'+carrier+'</p>'
			+'<p>车牌号：'+plateNo+'</p>'
			+'<p>驾驶员：'+driver+'</p>'
			+'<p>当前位置：'+position+'</p>'
			+'<p>当前速度：'+speed+'km/h</p>'
			+'<p>货物类型：'+goodsType+'</p>'
			+'<p>运输量：'+transportNum+'</p>';
		$('.city-order-detailTop').empty().html(topHtml);
		
		if(data.sites){
			var middleSiteHtml = '';
			for(var i in data.sites){
				var site = data.sites[i];
				if(site.siteType==1){ //起点
					var startName = !site.siteName?'--':site.siteName;
					var startAddress = site.siteAddress?site.siteAddress:'--';
					if(site.siteAddress){
						startName += '('+site.siteAddress+')';
					}
					var consignor = !site.linkmanName?'--':site.linkmanName;
					if(site.linkPhone){
						consignor += '|'+site.linkPhone;
					}
					$('#startNameD').empty().text(startName);
					$('#startAddressD').empty().text(startAddress);
					$('#consignorD').empty().text('发货人：'+consignor);
				}else if(site.siteType==2){ //配送点
					var name = !site.siteName?'--':site.siteName;
					var address = site.siteAddress?site.siteAddress:'--';
					var link = !site.linkmanName?'--':site.linkmanName;
					if(site.linkPhone){
						link += '|'+site.linkPhone;
					}
					var upload = '--';
					if(site.packageNum){
						upload = site.packageNum+'件';
					}else if(site.packageWeight){
						upload = site.packageWeight+'吨';
					}else if(site.unloadVolume){
						upload = site.uploadVolume+'方';
					}
					var idxNo = data.idxNo?data.idxNo:i;
					middleSiteHtml +='<li class="transport-item">'
						+'<span class="tap">'+idxNO+'</span>'
						+'<div class="name">'+name+'</div>'
						+'<div class="text2">'+address+'</div>'
						+'<div class="text2">收货人：'+link+'</div>'
						+'<div class="text2">卸货量：'+upload+'</div>'
						+'</li>';
				}else if(site.siteType==3){ //终点
					var name = !site.siteName?'--':site.siteName;
					var endAddress = site.siteAddress?site.siteAddress:'--';
					var link = !site.linkmanName?'--':site.linkmanName;
					if(site.linkPhone){
						link += '|'+site.linkPhone;
					}
					var upload = '--';
					if(site.packageNum){
						upload = site.packageNum+'件';
					}else if(site.packageWeight){
						upload = site.packageWeight+'吨';
					}else if(site.unloadVolume){
						upload = site.uploadVolume+'方';
					}
					$('#endNameD').empty().text(name);
					$('#endAddressD').empty().text(endAddress);
					$('#consigneeD').empty().text(link);
					$('#uploadAmount').empty().text('卸货量：'+upload);
				}
			}
			if(middleSiteHtml){
				$('.end-site').before(middleSiteHtml);
			}
		}
		
		$('.city-order-detail').show();
		
		self.mapWidget.clear();
		//self.mapWidget.
	}
	
}
$(function(){
	monitorController.init();
});