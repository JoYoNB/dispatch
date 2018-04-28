var old_back = mui.back;
mui.back = function(){
	ws.hide("slide-out-right",200);
}

//初始化区域滚动
mui('.mui-scroll-wrapper').scroll({indicators: false});

mui.plusReady(function() {
	ws=plus.webview.currentWebview();
	
	$(".mui-content").on("tap",".attr-item",function(){
		console.log("click")
		$(this).toggleClass("active");
	});
	
	$(".mui-content").on("tap",".submit-btn",function(){
		var list = [];
		var name = [];
		$(".attr-item.active").each(function(){
			list.push($(this).attr("data-id"));
			name.push($(this).text());
		});
		
		var inputText = name.length ? name.join("/") : '';
		var inputId = list.length ? list.join(",") : '';
		
		var view = plus.webview.getWebviewById('vehicleAdd.html');
        mui.fire(view, 'doit', {
            inputText: inputText,
            inputId:inputId
        });
        mui.back();
	})
});


