(function($, window, undefined){

	$('.mui-scroll-wrapper').scroll();

	//当前订单页码
	var nowPageNum=1;
	//历史订单页码
	var hisPageNum=1;
	var pageSize=1;

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
					var parent = item.querySelector('ul');
					setTimeout(function(){
						$(parent)[0].innerHTML = '';
						ajaxOrderList({type: type,"pageNum":1}, 'afterBegin');
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
		var _data=type==-1?({"pageNum":hisPageNum,"pageSize":pageSize}):({"pageNum":nowPageNum,"pageSize":pageSize});
		var data = $.extend(_data, opts);
		if(type&&type==0){
			//查询历史订单
			$.extend(data,{"orderStatus":90});
		}
		
		var orderData = {
			url:httpServer + '/DriverAppWeb/order/getOrderList.json',
			data:data,
			dataType:"json",
			type:"get",
			success:function(res){
				orderListSuccess(res, parent,type)
			},
			error:function(error){console.log(error)}
		};
		utils.loadData(orderData);
	}	
	
	function orderListSuccess(res, parent,type, where){
		
		if(!res || !res.data){
			return;
		}
		where = where || 'beforeEnd';
		var data = res.data.list;
		if(type&&type==0){
			//历史订单页码加一
			if(data.length==0){
				mui.toast("没有更多数据了");
			}else{
				nowPageNum++;
			}
		}else{
			//当前订单页码加一
			if(data.length==0){
				mui.toast("没有更多数据了");
			}else{
				hisPageNum++;
			}
		}
		
		var html = '';
		for(var i = 0; i < data.length; i++){
			//- 已发布: state0  待分配: state1  已分配: state2  配送中: state3 已完成: state4
			var r = data[i];
			var goods = r.packageNum?(r.packageNum+'件'):
							(r.volume?(r.volume+'方'):
							(r.weight?(r.weight+'吨'):''));
			
			var h = (r.goodsTypeName ? '<span class="tip">'+r.goodsTypeName+'</span>' : '') + 
					(goods ? '<span class="tip">'+goods+'</span>' : '') + 
					(r.distance ? '<span class="tip">'+r.distance+'km</span>' : '') + 
					(r.fee ? '<span class="tip">'+r.fee+'元</span>' : '') + 
					(r.height ? '<span class="tip">'+r.height+'M高栏</span>' : '') + 
					(r.carDoor ? '<span class="tip">'+r.carDoor+'</span>' : '');
			var stateName = r.orderStatus==50 ? '待确认' : 
							r.orderStatus==70 ? '待提货' : 
							r.orderStatus==80 ? '配送中' : 
							r.orderStatus==90 ? '已完成' : '已发布';
			html += ''
			+'	<li class="mui-table-view-cell ct-table-view-cell"  data-id = "'+r.orderNo+'">'
			+'      <div class="mui-slider-right mui-disabled">'
			+			(r.orderStatus==50 ? '<a class="mui-btn mui-btn-green ct-media-order">确认</a>' : '')
			+'			<a class="mui-btn mui-btn-danger ct-media-del">删除</a>'
			+'		</div>'
			+'		<div class="mui-slider-handle  mui-navigate-right">'
			+'			<a href="javascript:;" class="ct-media-box">'
			+'				<div class="ct-media-body">'
			+'					<p class="ct-media-address"><span class="ct-address">'+r.startSiteName+'</span><span class="ct-space">—</span><span class="ct-address">'+r.endSiteName+'</span></p>'
			+'					<p class="ct-ellipsis">'
			+						h
			+'					</p>'
			+'					<p class="ct-ellipsis">'+r.createTime+'</p>'
			+'				</div>'
			+'				<div class="ct-media-state">'+stateName+'</div>'
			+'			</a>'
			+'		</div>'
			+'	</li>';
		}
		$(parent)[0].insertAdjacentHTML(where, html);
	}

	//-	确认订单
	$(document).on('tap', '.ct-media-order', function(){
		var ctPopup = new CtPopup({
			body: '<div class="ct-talk-order">确认后，请尽快到订单收货点提货！</div>',
			title: '确认订单？',
			btnsArr: ['取消', '确定'],
			callback: function(res){
				if(res && res.index==1){
					//取订单编号
					var sss=this.getProperty('state');
					alert(sss);
					
					CommonUtils.async({
						url: httpServer +'/DriverAppWeb/order/confirmOrder.json',
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

	//-	订单详情
	$(document).on('tap', '.ct-table-view-cell', function(){
		var orderNo = this.getAttribute('data-id');
		CommonUtils.headWebview({
			url: 'orderDetail.html',
			title: '订单详情',
			extras:{"orderNo":orderNo}
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