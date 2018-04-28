(function($, window, undefined){

	var __p__ = {};

	$.plusReady(function() {
		
	});

	$('.mui-scroll-wrapper').scroll();

	//-	设置位置信息
	$(document).on('tap', '.J_getPosition', function(){
		var type = this.getAttribute('data-type');
		var id = this.parentNode.id;
		var res = __p__[id] = __p__[id] || {type: type, id: id};

		// $.openWindow('positionInfo.html', {
		// 	extras: {curRes: res}
		// });

		CommonUtils.headWebview({
			url: 'positionInfo.html',
			title: '位置信息',
			extras: {curRes: res}
		});
	});

	//-	添加站点
	$(document).on('tap', '#J_addSite', function(e){
		var num = $('.siteName').length;
		if(num>=3){
			alert('最多新增3个配送点')
			return;
		}
		var parent = this.parentNode;
		var li = document.createElement('li');
		var html = ''
		+'	<a href="javascript:;" data-type="3" class="J_getPosition">'
		+'		<i class="icon icon-order-1">'+(num+1)+'</i>'
		+'		<div class="ct-input-control"><input type="text" readonly="true" placeholder="请输入配送点位置"></div>'
		+'		<div class="hidden addressBox">'
		+'			<div class="address"></div>'
		+'			<div class="tip"></div>'
		+'		</div>'
		+'	</a>'
		+'	<a href="javascript:;" class="tianjiaBtn J_removeSite"><i class="ifont ifont-jianqu"></i></a>';
		li.innerHTML = html;
		li.className = 'siteName';
		li.id = 'site_'+(new Date().getTime());
		parent.parentNode.insertBefore(li, parent);
	});

	//-	删除站点
	$(document).on('tap', '.J_removeSite', function(e){
		this.parentNode.remove();
		$('.siteName').each(function(i, item){
			this.querySelector('.icon-order-1').innerText = i+1;
		})
	});

	//-	货物类型
	$(document).on('tap', '#J_goodsType', function(){
		var self = this;
		var opts = {ele: self};
		CommonUtils.popPickerGoods(opts, function(res){
			self.value = res[0].text;
		});
	});

	//-	提货时间
	$(document).on('tap', '#J_pickUpTime', function(){
		var self = this;
		var opts = {ele: self};
		CommonUtils.popPickerSevenDays(opts, function(res){
			self.value = res[0].text+' '+res[1].text;
			self.times = res;
		});
	});

	//-	送货时间
	$(document).on('tap', '#J_deliverTime', function(){
		var self = this;
		var opts = {ele: self}
		var times = $('#J_pickUpTime')[0].times;
		if(!!times){
			var y = new Date().getFullYear();
			var sTime = times[0].day;
			sTime = sTime.replace(/-/g, '\/');
			opts.sTime = new Date(y+'/'+sTime)
		}
		CommonUtils.popPickerSevenDays(opts, function(res){
			self.value = res[0].text+' '+res[1].text;
			self.times = [res[0].value, res[1].value];
		});
	});

	//-	自定义事件：更新位置信息
	window.addEventListener('refreshPosition', function(result) {
		var res = result.detail;
		// var res = {
		// 	id: 'J_getStartPosition',
		// 	type: '1',
		// 	name: '张三',
		// 	phone: '18512345678',
		// 	huowu: {ton: 4, piece: 5, cube: 2},
		// 	point: {lng: 114.025974, lat: 22.546054},
		// 	address: '广东省深圳市福田区农轩路55号',
		// 	title: '农轩路'
		// }
		var _huo_ = []
		res.huowu.ton && _huo_.push(res.huowu.ton+'吨');
		res.huowu.cube && _huo_.push(res.huowu.cube+'方');
		res.huowu.piece && _huo_.push(res.huowu.piece+'件');
		var ele = document.getElementById(res.id);
		var huowu = res.type=='1' ? '货物总量: ' : '卸货量: ';
		huowu += _huo_.join('/')
		ele.querySelector('.tip').innerText = res.name+' | '+res.phone+' | '+huowu
		ele.querySelector('.address').innerText = res.address;
		ele.querySelector('.addressBox').classList.remove('hidden');
		ele.querySelector('.ct-input-control').classList.add('hidden');

		__p__[res.id] = res;
	});

})(mui, window);