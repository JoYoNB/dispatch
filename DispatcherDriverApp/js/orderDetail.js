(function($, window, undefined){

	var myMap = null;

	$('.mui-scroll-wrapper').scroll();

	// 获得参数
	var self = plus.webview.currentWebview();
    var orderNo = self.orderNo;

	ajaxOrderDetail();
	function ajaxOrderDetail(){
		CommonUtils.async({
			url: httpServer +'/DriverAppWeb/order/getOrderInfo.json',
		/*	url:'json/orderDetail.json',*/
			data: {"orderNo":orderNo},
			success: orderDetailSuccess,
			error: orderDetailSuccess
		});
	}

	function orderDetailSuccess(res){
		res = res || {};
		var r = res.data || {};
		var points = [];
		var orderStatus = r.orderStatus==50 ? '待确认' : 
						  r.orderStatus==70 ? '待提货' : 
						  r.orderStatus==80 ? '配送中' : 
						  r.orderStatus==90 ? '已完成' : '';
		var html = ''
		+'	<div class="ct-detail-box">'
		+'		<div class="title">订单详情</div>'
		+'		<ul class="list">'
		+'			<li>订单编号：'+(r.orderNo || "--")+'</li>'
		+'			<li>货物类型：'+(r.goodsTypeName || "--")+'</li>'
		+'			<li>运输量：'+(r.packageNum || "--")+'</li>'
		+'			<li>订单状态：'+(orderStatus || "--")+'</li>'
		+'			<li>客户：'+(r.senderName || "--")+'</li>'
		+'			<li>联系电话：'+(r.senderPhone || "--")+'</li>'
		+'			<li>提货时间：'+(r.prePickupTime || "--")+'</li>'
		+'			<li>预计送达时间：'+(r.finishTime || "--")+'</li>'
		+'		</ul>'
		+'	</div>'
		
		
		if(r.orderSites && r.orderSites.length>0){
			html += ''
			+'	<div class="ct-detail-box">'
			+'		<div class="title">配送路线</div>'
			+'		<ul class="orderstate">';
			
			var orderStatus=r.orderStatus==50?'待确认':'已接单';
			for(var i = 0 ; i < r.orderSites.length; i++){
				var _r_ = r.orderSites[i];
				var contacts = _r_.idxNo==0 ? '发货人' : '收货人';
				var _goods = _r_.idxNo==0 ? '提货量' : '卸货量';
				
				var stateName = _r_.idxNo==0 ? (_r_.loadStatus==1?'已提货':'') : 
								(_r_.loadStatus==1 ? '已签收' : '');
								
				var idxNo=_r_.idxNo+1;
							
				var goods = _r_.uploadPackageNum?(_r_.uploadPackageNum+'件'):
							(_r_.unloadVolume?(_r_.unloadVolume+'方'):
							(_r_.unloadWeight?(_r_.unloadWeight+'吨'):''));
				if(i==0){
					if(r.orderStatus&&r.orderStatus==50){
						html += ''
						+'	<li>'
						+'		<i class="icon icon-order-'+_r_.idxNo+'">'+(_r_.idxNo!=0 ? (i) : "")+'</i>'
						+		(orderStatus ? '<div class="ordertit">'+orderStatus+'<span class="ordertime">'+(r.comfirmTime?r.comfirmTime:"")+'</span></div>' : '')
						+		(orderStatus ? '<div class="ordertip">'+(r.deptName || "--")+'['+(r.vehicleNo || "--")+' | '+(r.driverName || "--")+' | '+(r.driverPhone || "--")+']</div><div class="ordertip">待确认，请尽快确认</div>' : '')
						+'	</li>';	
					}else{
						html += ''
						+'	<li>'
						+'		<i class="icon icon-order-'+_r_.idxNo+'">'+(_r_.idxNo!=0 ? (i) : "")+'</i>'
						+		(orderStatus ? '<div class="ordertit">'+orderStatus+'<span class="ordertime">'+(r.comfirmTime?r.comfirmTime:"")+'</span></div>' : '')
						+		(orderStatus ? '<div class="ordertip">'+(r.deptName || "--")+'['+(r.vehicleNo || "--")+' | '+(r.driverName || "--")+' | '+(r.driverPhone || "--")+']</div><div class="ordertip">已确认，已出发前往提货</div>' : '')
						+'	</li>';	
					}
					
				}
				
				html += ''
				+'	<li>'
				+'		<i class="icon icon-order-'+(_r_.loadStatus==1?1:3)+'">'+(idxNo ? (idxNo) : "")+'</i>'
				+		'<div class="ordertit">'+stateName+'<span class="ordertime">'+(_r_.time?_r_.time:"")+'</span></div>' 
				+		'<div class="ordertip">'+(_r_.siteName || "--")+'（'+(_r_.address || "--")+'）</div><div class="ordertip">'+contacts+'：'+(_r_.linkMan || "--")+'（'+(_r_.linkPhone || "--")+'）</div>' 
				+	    '<div class="ordertip">'+_goods+'：'+goods+'</div>'
				+	     (_r_.idxNo==0 ? (_r_.loadStatus==0?'<div class="ordertip"><button class="mui-btn mui-btn-outlined submit-btn" id="pick" data-id="'+_r_.siteId+'">确认提货</button></div>':'') : 
								(_r_.loadStatus==0 ? '<div class="ordertip"><button class="mui-btn mui-btn-outlined submit-btn" id="sign" data-id="'+_r_.siteId+'">确认签收</button></div>' : ''))
				+'	</li>';

				_r_.point && points.push(_r_.point);
			}
			html += ''
			+'		</ul>'
			+'	</div>';
		}

		r.point && points.push(r.point);

		html = html.replace(/\s+/g, ' ');
		$('#J_detailBox')[0].insertAdjacentHTML('beforeEnd', html);

		myMap = new ZMap('myMap', {});
	}
	
	//-	确认提货
	$(document).on('tap', '#sign', function(){
	    var siteId = this.getAttribute('data-id');
		var btnArray=['取消','确认'];
		var title='';
		mui.confirm("确认签收？",title,btnArray,function(e){
			if(e.index==1){
				/*var postData = {
					url:httpServer + '/DriverAppWeb/order/pickup.json',
					data:{"orderNo":orderNo,"siteId":siteId},
					success: function(result){
						console.log(JSON.stringify(result));
						if(result.code==0){
							$.toast("成功");
							location.href="orderDetail.html";
						}else if(result.code==150000){
							$.toast("订单状态已改变，非待提货状态订单无法提货");
						}else{
							$.toast("失败");
						}
					},
					error: function(result){
						$.toast("失败");
					}
				};
				utils.loadData(postData);*/
			}else{
				//取消
				return ;
			}
		});
	});
	
	//-	确认签收
	$(document).on('tap', '#pick', function(){
	    var siteId = this.getAttribute('data-id');
		var btnArray=['取消','确认'];
		var title='';
		mui.confirm("确认提货？",title,btnArray,function(e){
			if(e.index==1){
				var postData = {
					url:httpServer + '/DriverAppWeb/order/pickup.json',
					data:{"orderNo":orderNo,"siteId":siteId},
					success: function(result){
						console.log(JSON.stringify(result));
						if(result.code==0){
							$.toast("成功");
							location.href="orderDetail.html";
						}else if(result.code==150000){
							$.toast("订单状态已改变，非待提货状态订单无法提货");
						}else{
							$.toast("失败");
						}
					},
					error: function(result){
						$.toast("失败");
					}
				};
				utils.loadData(postData);
			}else{
				//取消
				return ;
			}
		});
	});
	
	
})(mui, window);


	
