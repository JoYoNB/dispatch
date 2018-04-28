(function($, window, undefined){

	var myMap = null;
	var __p__ = {};

	$.plusReady(function() {
		var curWeb = plus.webview.currentWebview();
		var curMarPosit = curWeb.curMarPosit || {};
		var addressComponents = curMarPosit.addressComponents||{};
		$('#J_inputPosit')[0].value = curMarPosit.title || addressComponents.street || curMarPosit.address || '';
		setTimeout(function(){
			$('#J_inputPosit')[0].focus();
		}, 100);

		$('.ct-map-search')[0].style.paddingTop = 20+'px';
		$('.ct-map-searchList')[0].style.top = 71+20+'px';
	});

	myMap = new ZMap('myMap', {
		isGetLocation: true, 
		isDrawCurMarker: true, 
		mapInit: function(){
			myMap.getGeocoder(myMap.options.center, function(res){
				$('#J_curPosition')[0].innerText = res.address;
				__p__.cur = res;
			})
		}
	});


	$(document).on('input', '#J_inputPosit', function(){
		var val = this.value;
		localSearchFn(val);
	});

	var _localSearch_ = null;
	function localSearchFn(val){
		if(!_localSearch_){
			_localSearch_ = myMap.localSearch(val, function(res){
				if(res.length==0){
					$('#J_addressList')[0].classList.add('hide');
					$('#J_curPositionBox')[0].classList.remove('hide');
					return;
				}
				var html = '';
				for(var i = 0; i < res.length; i++){
					var r = res[i];
					html += ''
					+'	<li data-index="'+i+'">'
					+'		<div class="name">'+r.title+'</div>'
					+'		<div class="detail">'+r.address+'</div>'
					+'	</li>';
				}
				$('#J_addressList')[0].innerHTML = html;
				$('#J_addressList')[0].classList.remove('hide');
				$('#J_curPositionBox')[0].classList.add('hide');
				__p__.list = res;
			});
		}else{
			_localSearch_.search(val);
		}
	}

	$(document).on('submit', '#J_inputPositForm', function(e){
		var val = $('#J_inputPosit')[0].value;
		localSearchFn(val);
		e.preventDefault();
	});

	$(document).on('tap', '#J_empty', function(){
		$('#J_inputPosit')[0].value = '';
	});

	$(document).on('tap', '#J_back', function(){
		$.back();
	});

	$(document).on('tap', '#J_addressList li, #J_curPositionBox', function(){
		var index = this.getAttribute('data-index') || 0;
		var res = this.id==='J_curPositionBox' ? __p__.cur : __p__.list[index];
		res.district=__p__.cur.addressComponents.district;
		var prevWeb = plus.webview.getWebviewById(plus.webview.currentWebview().parentPage);
		console.info(plus.webview.currentWebview().parentPage);
		$.fire(prevWeb,'refreshPosition', res);
		$.back();
	});

})(mui, window);