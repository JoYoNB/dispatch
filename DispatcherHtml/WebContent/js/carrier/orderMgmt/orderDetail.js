var OrderDetail={
	init:function(){
		var self=this;
		self._render();
		self._bind();
	},
	_render:function(){
		var self=this;
		//设置底部菜单连接
		Footer.nav("订单列表","carrier/orderMgmt/orderList.html");
		var orderNo = CommonUtils.getParam("orderNo");
		console.log(orderNo);
		CommonUtils.async({
			url:"/DispatcherWeb/carrier/order/getOrder.json",
			data:{orderNo:orderNo},
			success:function(result){
				if(result.code != 0)
					return;
				var order  = result.data.order;
				console.log(order);
				$("#orderNo").text(order.orderNo);
				$("#goodsTypeName").text(order.goodsTypeName);
				$("#vehicleTypeName").text(order.vehicleTypeName);
				$("#startSiteName").text(order.startSiteName + "("+ order.startAddr +")");
				$("#senderName").text(order.senderName + "(" + order.senderPhone+")");
				$("#pickupTime").text(order.pickupTime);
				$("#finishTime").text(order.finishTime);
				$("#remark").text(order.remark);
				$("#carrierName").text(order.carrierName);
				
				if(!order.plateNo || !order.driverName){
					$("#vehicleDesc").hide();
					$("#driverDesc").hide();
				}else{
					$("#plateNo").text(order.plateNo);
					$("#driverName").text(order.driverName);
				}
			
				$("#publishTime").text(order.publishTime);
				
				if(!order.position){
					$("#positionDesc").hide();
				}else{
					$("#position").text(order.position);
				}
				
				var sites = result.data.sites;
				if(sites){
					var html = "";
					for(var i=1; i<sites.length; i++){
						var site = sites[i];
						if(site.siteType == 1){
							break;
						} else{
							var unload = "";
							if(site.unloadPackageNum){
								unload += site.unloadPackageNum + "件|";
							}
							if(site.unloadVolume){
								unload += site.unloadVolume + "方|";
							}
							if(site.unloadWeight){
								unload += site.unloadWeight + "吨";
							}
							var siteName = site.siteName +'（'+site.address +'）';
							var receiver = site.receiverName +'（'+site.receiverPhone+'）';
							if(site.siteType == 2){ // 不是终点
								html += 
									'<p class="list">                                                        '+
									'	<span class="tip">配送点：</span>                                    '+
									'	<span>' +receiver + '</span>										 '+
									'</p>                                                                    '+
									'<p class="list">                                                        '+
									'	<span class="tip">收货人：</span>                                    '+
									'	<span>' + siteName + '</span>										 '+
									'</p>                                                                    '+
									'<p class="list">                                                        '+
									'	<span class="tip">卸货量：</span>                                    '+
									'	<span>'+ unload +'</span>                                            '+
									'</p>                                                                    ';
							} else if(site.siteType == 3){
								// 修改终点
								$("#unload").text(unload);
								$("#endSiteName").text(siteName);
								$("#receiverName").text(receiver);
							}
						}
					}
					$("#line").after(html);
				}
				var circlys = result.data.circlys;
				if(circlys && circlys.length > 0){
					var html = "";
					for(var i=0; i<circlys.length; i++){
						var circly = circlys[i];
						var statusName = OrderStatusUtil.getStatusName(circly.orderStatus);
						html +=
							'<li class="transport-item">                                      '+
							'	<span class="tap">'+ (i+1) +'</span>                   '+
							'	<div class="time">'+ circly.operateTime +'</div>              '+
							'	<div class="text">【'+statusName+'】'+ circly.content +'</div>'+
							'</li>                                                            ';
					}
					$("#transportDetail").append(html);
				}
			},
			error:function(result){
				
			}
		});
		
	},
	_bind:function(){
		var self=this;
		$("#orderBtn").unbind("click").click(function(){
			if(!$(this).parent().hasClass("active")){
				$(".tap-item").removeClass("active");
				$(this).parent().addClass("active");
				$("#orderDetail").show();
				$("#transportDetail").hide();
			}
		});
		$("#transportBtn").unbind("click").click(function(){
			if(!$(this).parent().hasClass("active")){
				$(".tap-item").removeClass("active");
				$(this).parent().addClass("active");
				$("#orderDetail").hide();
				$("#transportDetail").show();
			}
		});
	}
}
$(function(){
	OrderDetail.init();
});