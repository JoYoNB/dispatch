(function($, window, undefined){

	var myMap = null;

	$('.mui-scroll-wrapper').scroll();

	ajaxOrderDetail();
	function ajaxOrderDetail(){
		CommonUtils.async({
			url: 'json/orderDetail.json',
			data: {},
			success: orderDetailSuccess,
			error: orderDetailSuccess
		});
	}

	function orderDetailSuccess(res){
		res = res || {};
		var r = res.data || {};
		var points = [];

		var load = r.load==1 ? '半载' : r.load==2 ? '满载' : '空载';
		var state = r.state==1 ? '在线' : r.state==2 ? '休眠' : '离线';
		var goods = !r.goods ? '' : 
					(r.goods.ton ? r.goods.ton+'吨' : '') + 
					(r.goods.piece ? r.goods.piece+'件' : '') + 
					(r.goods.cube ? r.goods.cube+'方' : '');
		var html = ''
		+'	<div class="ct-detail-box">'
		+'		<div class="title">'+(r.plateNo || "--")+'</div>'
		+'		<div class="name">'+(r.driver || "--")+(r.phone ? "（"+r.phone+"）" : "")+' |  '+(r.depart || '--')+'</div>'
		+'		<ul class="list border">'
		+'			<li>车辆状态：<span class="state">'+load+'（'+state+'）</span></li>'
		+'			<li>车型：'+r.height+'M高栏 '+r.carDoor+'</li>'
		+'			<li>当前速度：'+(r.speed || "--")+'km/h</li>'
		+'			<li>当前位置：'+(r.position || "--")+'</li>'
		+'		</ul>'
		+'	</div>'
		+'	<div class="ct-detail-box">'
		+'		<div class="title">订单详情</div>'
		+'		<ul class="list">'
		+'			<li>订单编号：'+(r.orderId || "--")+'</li>'
		+'			<li>货物类型：'+(r.goodsType || "--")+'</li>'
		+'			<li>运输量：'+(goods || "--")+'</li>'
		+'			<li>配送企业：'+(r.logistics || "--")+'</li>'
		+'			<li>提货时间：'+(r.takeGoodsTime || "--")+'</li>'
		+'		</ul>'
		+'	</div>'

		if(r.routes && r.routes.length>0){
			html += ''
			+'	<div class="ct-detail-box">'
			+'		<div class="title">配送路线</div>'
			+'		<ul class="orderstate">';

			for(var i = 0 ; i < r.routes.length; i++){
				var _r_ = r.routes[i];
				var contacts = _r_.state==1 ? '发货人' : '收货人';
				var stateName = _r_.state==1 ? '已发出' : 
								_r_.state==2 ? '已签收' : '已接单';
				var goods = !_r_.goods ? '' : 
							(_r_.goods.ton ? _r_.goods.ton+'吨' : '') + 
							(_r_.goods.piece ? _r_.goods.piece+'件' : '') + 
							(_r_.goods.cube ? _r_.goods.cube+'方' : '');

				html += ''
				+'	<li>'
				+'		<i class="icon icon-order-'+_r_.state+'">'+(_r_.state!=0 ? (i) : "")+'</i>'
				+		(_r_.state!=3 ? '<div class="ordertit">'+stateName+'<span class="ordertime">'+_r_.time+'</span></div>' : '')
				+		(_r_.state==0 ? '<div class="ordertip">'+(_r_.logistics || "--")+'['+(_r_.plateNo || "--")+' | '+(_r_.driver || "--")+' | '+(_r_.phone || "--")+']</div><div class="ordertip">已确认，已出发前往提货</div>' : '')
				+		(_r_.state!=0 ? '<div class="ordertip">'+(_r_.depot || "--")+'（'+(_r_.position || "--")+'）</div><div class="ordertip">'+contacts+'：'+(_r_.contacts || "--")+'（'+(_r_.phone || "--")+'）</div>' : '')
				+		(_r_.state==2 || _r_.state==3 ? '<div class="ordertip">卸货量：'+goods+'</div>' : '')
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
	
})(mui, window);