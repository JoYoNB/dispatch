var OrderDetail={
	
	init:function(){
		var self=this;
		self._render();
		self._bind();
	},
	_render:function(){
		var self = this;
		//设置底部菜单连接
		Footer.nav("站点管理","consignor/order/orderList.html");
		self.getOrderDetail();
	},
	_bind:function(){
		var self = this;
		$('#orderDetail').unbind('click').click(function() {
			var _this = $(this);
			_this.parent().addClass('active');
			$('#transportDetail').parent().removeClass('active');
			$('.city-transport-details').hide();
			$('.city-goods-details').show();
		});
		$('#transportDetail').unbind('click').click(function() {
			var _this = $(this);
			_this.parent().addClass('active');
			$('#orderDetail').parent().removeClass('active');
			$('.city-goods-details').hide();
			$('.city-transport-details').show();
		});
	},
		
	getOrderDetail:function(){
		var self=this;
		var param = {};
		var id = CommonUtils.getParam('id');
		if(!id){
			return;
		}
		param.orderNo = id;	
		console.log("param",param);
		CommonUtils.async({
			url:"/DispatcherWeb/consignor/order/orderDetail.json",
			data:param,
			success:function(result){
				if(result.code==0){
					if(result.data){
						self.initHtml(result.data);
					}
				}else{
					MessageUtil.alert("获取订单详情失败");
				}
			},
			error:function(result){
				MessageUtil.alert("获取订单详情失败");
			}
		});
	},
	initHtml:function(data){
		console.log(data);
		$('#orderNo').empty().text(!data.orderNo?'--':data.orderNo);
		$('#goodsType').empty().text(!data.goodsType?'--':data.goodsType);
		$('#vehicleType').empty().text(!data.vehicleType?'--':data.vehicleType);
		if(data.sites){
			var middleSiteHtml = '';
			for(var i in data.sites){
				var site = data.sites[i];
				if(site.siteType==1){ //起点
					var startName = !site.siteName?'--':site.siteName;
					if(site.siteAddress){
						startName += '('+site.siteAddress+')';
					}
					var consignor = !site.linkmanName?'--':site.linkmanName;
					if(site.linkPhone){
						consignor += '('+site.linkPhone+')';
					}
					$('#startName').empty().text(startName);
					$('#consignor').empty().text(consignor);
				}else if(site.siteType==2){ //配送点
					var name = !site.siteName?'--':site.siteName;
					if(site.siteAddress){
						name += '('+site.siteAddress+')';
					}
					var link = !site.linkmanName?'--':site.linkmanName;
					if(site.linkPhone){
						link += '('+site.linkPhone+')';
					}
					var upload = '--';
					if(site.packageNum){
						upload = site.packageNum+'件';
					}else if(site.packageWeight){
						upload = site.packageWeight+'吨';
					}else if(site.unloadVolume){
						upload = site.uploadVolume+'方';
					}
					middleSiteHtml +='<p class="list"><span class="tip">配送点：</span>'+name+'</p>'
						+'<p class="list"><span class="tip">收货人：</span>'+link+'</p>'
						+'<p class="list"><span class="tip">卸货量：</span>'+upload+'</p>'
						+'<br>';
				}else if(site.siteType==3){ //终点
					var name = !site.siteName?'--':site.siteName;
					if(site.siteAddress){
						startName += '('+site.siteAddress+')';
					}
					var link = !site.linkmanName?'--':site.linkmanName;
					if(site.linkPhone){
						link += '('+site.linkPhone+')';
					}
					var upload = '--';
					if(site.packageNum){
						upload = site.packageNum+'件';
					}else if(site.packageWeight){
						upload = site.packageWeight+'吨';
					}else if(site.unloadVolume){
						upload = site.uploadVolume+'方';
					}
					$('#endName').empty().text(name);
					$('#consignee').empty().text(link);
					$('#uploadAmount').empty().text(upload);
				}
			}
			if(middleSiteHtml){
				$('.end-site').before(middleSiteHtml);
			}
		}
		//提货时间
		$('#pickupTime').empty().text(!data.pickupTime?'--':data.pickupTime);
		//结束时间
		$('#finishTime').empty().text(!data.finishTime?'--':data.finishTime);
		//备注
		$('#remark').empty().text(!data.remark?'--':data.remark);
		//配送企业
		$('#carrierName').empty().text(!data.carrierName?'--':data.carrierName);
		//车牌号
		$('#plateNo').empty().text(!data.plateNo?'--':data.plateNo);
		//驾驶员
		var driver = !data.driverName?'--':data.driverName;
		if(data.driverPhone){
			driver += '('+data.deriverPhone+')';
		}
		$('#driver').empty().text(driver);
		//创建时间
		$('#createTime').empty().text(!data.createTime?'--':data.createTime);
		//车辆最新位置
		var position = '--';
		if(data.vehicle&&data.vehicle.position){
			position = data.vehicle.position;
		}
		$('#position').empty().text(position);
		
		//运输详情
		if(data.list&&data.list.length>0){
			for(var i in data.list){
				var log = data.list[i];
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
				var logHtml = '<li class="transport-item"> '
					+' <span class="tap">'+(i+1)+'</span> '
					+' <div class="time">'+log.operateTime+'</div> '
					+' <div class="text">【'+status+'】'+content+'</div></li>';
				$('.city-transport-details').append(logHtml);
			}
		}
	}

}
$(function(){
	OrderDetail.init();
});