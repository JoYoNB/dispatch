(function($, window, undefined){

	var allWeb = [];
	var activeTap = 0;

	$.plusReady(function(){

		initPages(0);
		$(document).on('tap', '#J_tabBar a', function(){
			var index = this.getAttribute('data-index');
			initPages(index);
		})

	});

	function initPages(index){
		index = index || 0;
		if(allWeb[activeTap]){
			allWeb[activeTap].hide();
		}
		activeTap = index;
		if(!allWeb[index]){
			var pages = ['orderList.html', 'personal.html'];
			allWeb[index] = $.openWindow(pages[index], {
				styles: {
					bottom: '60',
					top: '0'
				},
				show: {autoShow: false},
				waiting: {autoShow: false}
			});
			setTimeout(function(){
				allWeb[index].show();
			}, 0);
		}else{
			allWeb[index].show();
		}
	}

})(mui, window);