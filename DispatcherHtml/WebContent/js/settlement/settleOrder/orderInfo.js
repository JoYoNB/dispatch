var OrderInfo={
	orderNo:null,
	init:function(){
		var self=this;
		self.orderNo=CommonUtils.getParam("orderNo");
		self._render();
		self._bind();
		self._loadData();
	},
	_render:function(){
		var self=this;
		
	},
	_bind:function(){
		var self=this;
		
		$("#orderDetailsBtn").unbind("click").click(function(){
			$("#orderDetailsBtn").addClass("active");
			$("#transportDetailsBtn").removeClass("active");
			$("#orderDetailsDiv").removeClass("hide");
			$("#transportDetailsDiv").addClass("hide");
		});
		
		$("#transportDetailsBtn").unbind("click").click(function(){
			$("#transportDetailsBtn").addClass("active");
			$("#orderDetailsBtn").removeClass("active");
			$("#transportDetailsDiv").removeClass("hide");
			$("#orderDetailsDiv").addClass("hide");
		});
	},
	_loadData:function(){
		var self=this;
		CommonUtils.async({
			url:"/DispatcherWeb/settleOrder/getOrderDetails.json",
			data:{orderNo:self.orderNo},
			success:function(result){
				if(result.code==0){
					var orderInfo=result.data;
					var _orderNo=orderInfo.orderNo;
					$("#orderNo").html(_orderNo);
					var _goodsTypeName=orderInfo.goodsTypeName;
					$("#goodsTypeName").html(_goodsTypeName);
					var _vehicleTypeName=orderInfo.vehicleTypeName;
					$("#vehicleTypeName").html(_vehicleTypeName);
					var _pickupTime=orderInfo.pickupTime;
					$("#pickupTime").html(_pickupTime);
					var _finishTime=orderInfo.finishTime;
					$("#finishTime").html(_finishTime);
					var _remark=orderInfo.remark;
					$("#remark").html(_remark);
					var _carrierDeptName=orderInfo.carrierDeptName;
					$("#carrierDeptName").html(_carrierDeptName);
					var _vehicleNo=orderInfo.vehicleNo;
					$("#vehicleNo").html(_vehicleNo);
					var _driverName=orderInfo.driverName||"...";
					var _driverPhone=orderInfo.driverPhone||"...";
					_driverName+="("+_driverPhone+")";
					$("#driverName").html(_driverName);
					var _createTime=orderInfo.createTime;
					$("#createTime").html(_createTime);
					var _position=orderInfo.position;
					$("#position").html(_position);
					
					var _sites=orderInfo.sites;
					var _html='';
					for (var i = 0; i < _sites.length; i++) {
						var _site=_sites[i];
						var _siteName=_site.siteName||"...";
						var _address=_site.address||"...";
						_siteName+="("+_address+")";
						var _linkMan=_site.linkMan||"...";
						var _linkPhone=_site.linkPhone||"...";
						_linkMan+="("+_linkPhone+")";
						var _unloadPackageMum=_site.unloadPackageMum||"*";
						var _unloadWeight=_site.unloadWeight||"*";
						var _unloadVolume=_site.unloadVolume||"*";
						_unloadPackageMum+="件"+_unloadWeight+"吨"+_unloadVolume+"方"
						if(i==0){//第一个为发货点
							_html+='<p class="list"><span class="tip">起点名称：</span>'+_siteName+'</p>'
								+'<p class="list"><span class="tip">发货人：</span>'+_linkMan+'</p>'
								+'<br>';
						}else if(i>0&&i!=_sites.length-1){//不是第一个且不是最后一个，则为中间的配送点
							_html+='<p class="list"><span class="tip">配送点：</span>'+_siteName+'</p>'
								+'<p class="list"><span class="tip">收货人：</span>'+_linkMan+'</p>'
								+'<p class="list"><span class="tip">卸货量：</span>'+_unloadPackageMum+'</p>'
								+'<br>';
						}else{
							_html+='<p class="list"><span class="tip">终点名称：</span>'+_siteName+'</p>'
								+'<p class="list"><span class="tip">收货人：</span>'+_linkMan+'</p>'
								+'<p class="list"><span class="tip">卸货量：</span>'+_unloadPackageMum+'</p>'
								+'<br>';
						}
					}
					$("#sitesDiv").html(_html);
				}
			}
		});
		
		CommonUtils.async({
			url:"/DispatcherWeb/settleOrder/getOrderLifecycle.json",
			data:{orderNo:self.orderNo},
			success:function(result){
				if(result.code==0){
					var ret=result.data||{};
					var list=ret.list||[];
					var _html='';
					for (var i = 0; i < list.length; i++) {
						var _orderLifecycle=list[i];
						var _operateTime=_orderLifecycle.operateTime||"...";
						var _operationName=self._getOperationName(_orderLifecycle.operation)||"...";
						var _operatorName=_orderLifecycle.operatorName||"...";
						var _operatorPhone=_orderLifecycle.operatorPhone||"...";
						_html+='<li class="transport-item">'
							+'<span class="tap">'+(i+1)+'</span>'
							+'<div class="time">'+_operateTime+'</div>'
							+'<div class="text">【'+_operationName+'】'+_operatorName+'('+_operatorPhone+')</div>'
							+'</li>';
					}
					$("#transportDetailsDiv").html(_html);
				}
			}
		});
	},
	_getOperationName:function(operation){
		var operations={
				0:"失效",
				1:"创建",
				2:"编辑",
				3:"删除",
				4:"付款",
				5:"申请退款",
				6:"退款",
				7:"发布",
				8:"接单",
				9:"取消",
				10:"分配",
				11:"司机确认",
				12:"提货",
				13:"签收",
				14:"结算"
		};
		var operationName=operations[operation]||"...";
		return operationName;
	}
}
$(function(){
	OrderInfo.init();
});