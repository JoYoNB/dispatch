
//初始化区域滚动
mui('.mui-scroll-wrapper').scroll({indicators: false});

mui.plusReady(function() {
	$(".mui-content").on("tap",".attr-item",function(){
		console.log("click");
		$(this).toggleClass("active");
	});
	
	$(".mui-content").on("tap",".mui-table-view-cell",function(){
		var id = '',name = '';
		id = $(this).attr("data-id");
		name = $(this).find(".item-title").text();
		
		
		var view = plus.webview.getWebviewById('driverAdd.html');
        mui.fire(view, 'doit', {
             id: id,
            name:name
        });
        mui.back();
	})
});


