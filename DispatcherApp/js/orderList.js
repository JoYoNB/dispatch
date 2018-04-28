(function($, window, undefined){

	$('.mui-scroll-wrapper').scroll();

	$('.J_scrollWrap').each(function(i, item){
		var pullRefresh = $(item).pullRefresh({
			up: {
				callback: function(){
					var type = $('.J_orderControl.mui-active')[0].getAttribute('data-type');
					setTimeout(function(){
						ajaxOrderList({type: type});
						pullRefresh.endPullupToRefresh();
					}, 1000);
				}
			},
			down: {
				callback: function(){
					var type = $('.J_orderControl.mui-active')[0].getAttribute('data-type');
					setTimeout(function(){
						ajaxOrderList({type: type}, 'afterBegin');
						pullRefresh.endPulldownToRefresh();
					}, 1000);
				}
			}
		})
	});

	ajaxOrderList();
	function ajaxOrderList(opts, where){
		var type = opts && opts.type ? opts.type : '-1';
		var wrap= type=='-1' ? 'J_allOrdersWrap' : 'J_ordersWrap'+type;
		var parent = $('#'+wrap)[0].querySelector('ul');

		var data = $.extend({}, opts);
		CommonUtils.async({
			url: 'json/orderList.json',
			data: {},
			success: function(res){
				orderListSuccess(res, parent, where);
			}
		});
	}
	
	var num = 0;
	function orderListSuccess(res, parent, where){
		if(!res || !res.data){
			return;
		}
		where = where || 'beforeEnd';
		var data = res.data;
		var html = '';
		for(var i = 0; i < data.length; i++){
			//- 已发布: state0  待分配: state1  已分配: state2  配送中: state3 已完成: state4
			var r = data[i];
			var h = (r.goodsType ? '<span class="tip">'+r.goodsType+'</span>' : '') + 
					(r.number ? '<span class="tip">'+r.number+'件</span>' : '') + 
					(r.distance ? '<span class="tip">'+r.distance+'km</span>' : '') + 
					(r.height ? '<span class="tip">'+r.height+'M高栏</span>' : '') + 
					(r.carDoor ? '<span class="tip">'+r.carDoor+'</span>' : '');
			var stateName = r.state==1 ? '待分配' : 
							r.state==2 ? '已分配' : 
							r.state==3 ? '配送中' : 
							r.state==4 ? '已完成' : '已发布';
			html += ''
			+'	<li class="mui-table-view-cell ct-table-view-cell state'+r.state+'">'
			+'      <div class="mui-slider-right mui-disabled">'
			+			(r.state==1 ? '<a class="mui-btn mui-btn-green ct-media-order">接单</a>' : '')
			+'			<a class="mui-btn mui-btn-danger ct-media-del">删除</a>'
			+'		</div>'
			+'		<div class="mui-slider-handle  mui-navigate-right">'
			+'			<a href="javascript:;" class="ct-media-box">'
			+'				<div class="ct-media-body">'
			+'					<p class="ct-media-address"><span class="ct-address">'+(++num)+r.start+'</span><span class="ct-space">—</span><span class="ct-address">'+r.end+'</span></p>'
			+'					<p class="ct-ellipsis">'
			+						h
			+'					</p>'
			+'					<p class="ct-ellipsis">'+r.time+'</p>'
			+'				</div>'
			+'				<div class="ct-media-state">'+stateName+'</div>'
			+'			</a>'
			+'		</div>'
			+'	</li>';
		}
		$(parent)[0].insertAdjacentHTML(where, html);
	}

	//-	接单
	$(document).on('tap', '.ct-media-order', function(){
		var ctPopup = new CtPopup({
			body: '<div class="ct-talk-order">接单后，请在10分钟内完成分配工作，超时不分配，接单将作废。</div>',
			title: '确认接单？',
			btnsArr: ['取消', '接单'],
			callback: function(res){
				if(res && res.index==1){
					CommonUtils.async({
						url: 'json/test.json',
						data: {},
						success: function(){
							$.toast('接单成功');
						},
						error: function(){

						}
					});
				}
			}
		});
	});

	//-	删除订单
	$(document).on('tap', '.ct-media-del', function(){
		var li = this.closest('li');
		$.confirm('是否删除订单', '提示', ['删除', '取消'], function(res){
			if(res && res.index==0){
				CommonUtils.async({
					url: 'json/test.json',
					data: {},
					success: function(){
						li.parentNode.removeChild(li);
						$.toast('删除成功');
					},
					error: function(){

					}
				});
			}
		})
	});

	//-	创建订单
	$(document).on('tap', '#J_createOrder', function(){
		CommonUtils.headWebview({
			url: 'createOrder.html',
			title: '创建订单'
		});
	});

	//-	订单详情
	$(document).on('tap', '.ct-table-view-cell', function(){
		CommonUtils.headWebview({
			url: 'orderDetail.html',
			title: '订单详情'
		});
	});

	//-	栏目切换
	$(document).on('tap', '.J_orderControl', function(){
		var type = this.getAttribute('data-type');
		var wrap= type=='-1' ? '#J_allOrdersWrap' : '#J_ordersWrap'+type;
		var parent = $(wrap)[0].querySelector('ul');

		if( $(parent)[0].children.length==0 ){
			ajaxOrderList({type: type});
		}
	});

	function CtPopup(opts){
		this.options = $.extend({}, {
			btnsArr: ['取消', '确定'],
			title: '提示',
			body: '内容',
			callback: null
		}, opts)
		this.init();
		return this;
	}

	CtPopup.prototype = {
		init: function(){
			var opts = this.options;
			var id = this.wrapEle = 'ct-popup-'+(new Date().getTime());
			var btns = '';
			for(var i =0; i < opts.btnsArr.length; i++){
				btns += '<a href="javascript:;" data-index="'+i+'" class="ct-popup-btn">'+opts.btnsArr[i]+'</a>';
			}
			var html = ''
			+'	<div class="ct-popup-wrapper" id="'+id+'">'
			+'		<div class="ct-popup-shadow"></div>'
			+'		<div class="ct-popup">'
			+'			<div class="ct-popup-title">'+opts.title+'</div>'
			+'			<div class="ct-popup-body">'
			+				opts.body
			+'			</div>'
			+'			<div class="ct-popup-footer">'
			+				btns
			+'			</div>'
			+'		</div>'
			+'	</div>';
			document.body.insertAdjacentHTML('beforeEnd', html);
			this.handEvent();
		},
		handEvent: function(){
			var opts = this.options;
			var self = this;
			var btns = document.querySelectorAll('.ct-popup-footer a');
			for(var i = 0; i < btns.length; i++){
				var btn = btns[i];
				btn.addEventListener('tap', function(){
					var index = this.getAttribute('data-index');
					typeof opts.callback==='function' && opts.callback({index: index});
					self.remove();
				})
			}
			document.querySelector('#'+this.wrapEle+' .ct-popup-shadow').addEventListener('tap', function(){
				self.remove();
			});
		},
		remove: function(){
			document.querySelector('#'+this.wrapEle).remove();
		}
	}
	
})(mui, window);