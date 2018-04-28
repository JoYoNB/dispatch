(function($, window, undefined){

	var myMap = null;
	var __p__ = {curRes: {}, curMarPosit: {}};

	$.plusReady(function() {
		var curWeb = plus.webview.currentWebview();
		var _curRes_ = __p__.curRes = curWeb.curRes || {};
		if(_curRes_.address){
			$('#J_curPositionTitle')[0].value = _curRes_.title;
			$('#J_curPosition')[0].value = _curRes_.address;
			$('#J_ton')[0].value = _curRes_.huowu.ton;
			$('#J_cube')[0].value = _curRes_.huowu.cube;
			$('#J_piece')[0].value = _curRes_.huowu.piece;
			$('#J_phone')[0].value = _curRes_.phone;
			$('#J_name')[0].value = _curRes_.name;
		}
		var c = 'icon icon-order-';
		c += _curRes_.type=='1' ? 'start' : _curRes_.type=='2' ? 'end' : 'distribut'
		$('#J_icon')[0].className = c;

		var mapOpts = {
			mapInit: mapInit,
			isGetLocation: true,
			init: {}
		}
		__p__.curRes.point && (mapOpts.init.center = __p__.curRes.point);
		myMap = new ZMap('myMap', mapOpts);

		//-	点击确定按钮，返回所有的地址信息
		$(document).on('tap', '#J_goback', getAllPisitionInfo);

		//-	获取通讯录联系人
		$(document).on('tap', '#J_phoneContact', getContacts);

		//-	自定义事件：更新位置信息
		window.addEventListener('refreshPosition', function(res) {
			var r = res.detail || {};
			__p__.curMarPosit = r;
			myMap.map.setCenter( myMap.getPoint(r.point) );
			$('#J_curPositionTitle')[0].value = r.title || r.addressComponents.street || r.address || '';
			$('#J_curPosition')[0].value = r.address || '';
		});

		//-	打开搜索地址页面
		$(document).on('tap', '#J_curPositionBox', function(){
			$.openWindow('positionInfo-search.html', {
				extras: {curMarPosit: __p__.curMarPosit,parentPage:"positionInfo.html"}
			});
		});

		//-	回到当前位置
		$(document).on('tap', '#J_mapCenter', function(){
			if(!!myMap){
				var p = myMap.options.center;
				myMap.map.setCenter( p );
				getGeocoder(p);
			}
		});
	});

	//-	地图初始化回调方法
	function mapInit(){
		//-	解析
		if(!__p__.curRes.point){
			getGeocoder(myMap.options.center);
		}

		var isMove = false;
		myMap.bind('touchmove', function(){
			isMove = true;	
		});
		myMap.bind('touchend', function(){
			if(!isMove){
				var action = $('#J_positionWrap')[0].className.indexOf('hide')>-1 ? 'remove' : 'add';
				$('#J_positionWrap')[0].classList[action]('hide');
			}
			isMove = false;

			var p = myMap.map.getCenter();
			getGeocoder(p);
		});
	}

	function getGeocoder(point){
		var p = __p__.curRes.point = myMap.getPoint(point);
		myMap.getGeocoder(p, function(res){
			$('#J_curPositionTitle')[0].value = res.addressComponents.street || res.address;
			$('#J_curPosition')[0].value = res.address || '';
			__p__.curMarPosit = res || {};
		})
	}

	//-	点击确定按钮，返回所有的地址信息
	function getAllPisitionInfo(){
		var allIpt = CommonUtils.serializeFn('#J_positionWrap');
		for(var i in allIpt){
			console.log('1111111111111 : '+(i=='ton' || i=='piece' || i=='cube') && !!allIpt[i]);
			if((i=='ton' || i=='piece' || i=='cube') && !!allIpt[i]){
				continue;
			}
			var msg = CommonUtils.proxyValid(allIpt[i], getErrorTip(i));
			if(msg){
				$.toast(msg);
				return;
			}
		}
		var p = $('#J_savePosition')[0].checked;
		if(!!p){
			CommonUtils.async({
				url: '../json/test.json',
				data: {
					address: __p__.curRes.address, 
					title: __p__.curRes.title
				},
				complete: function(){
					backPrevPage(allIpt);
				}
			})
		}else{
			backPrevPage(allIpt);
		}
	};

	//-	获取通讯录联系人
	function getContacts(){
		plus.contacts.getAddressBook(plus.contacts.ADDRESSBOOK_PHONE, function(addressbook){
			if(addressbook.type==0){
				addressbook.find(null, function(contacts){
					// console.log(JSON.stringify(contacts));
				})
			}
		}, function(err){
			alert('获取通讯录失败');
			console.log(JSON.stringify(err));
		});
	}

	function getErrorTip(type){
		var arr = [];
		switch(type){
			case 'title':
				arr = [{s: 'empty:J_curPositionTitle', msg: '起点名称不能为空'}];
				break;
			case 'address':
				arr = [{s: 'empty:J_curPosition', msg: '详细地址不能为空'}];
				break;
			case 'name':
				arr = [{s: 'empty:J_name', msg: '联系人不能为空'}];
				break;
			case 'phone':
				arr = [{s: 'empty:J_phone', msg: '手机不能为空'}];
				break;
		}
		return arr;
	}

	function backPrevPage(allIpt){
		var res = {
			id: __p__.curRes.id,
			name: allIpt.name,
			phone: allIpt.phone,
			huowu: {ton: allIpt.ton || 0, piece: allIpt.piece || 0, cube: allIpt.cube || 0},
			point: __p__.curRes.point,
			address: allIpt.address,
			title: allIpt.title,
			type: __p__.curRes.type
		}

		var prevWeb = plus.webview.getWebviewById('createOrder.html');
		$.fire(prevWeb,'refreshPosition', res);

		var parentWeb = plus.webview.getWebviewById('head_positionInfo.html');
		$.fire(parentWeb, 'goback');
	}

})(mui, window);