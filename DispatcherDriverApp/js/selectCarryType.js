
//初始化区域滚动
mui('.mui-scroll-wrapper').scroll({indicators: false});

mui.plusReady(function() {
	
	console.log(new Date().getTime() + "|载货类型选择:plusReady");
	var carryTypeData = {
		url:httpServer + '/DriverAppWeb/common/getCarryTypeList.json',
		data:{},
		type:"get",
		success:renderCarryTypeList,
		error:function(error){console.log(error)}
	};
	utils.loadData(carryTypeData);
	
	
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
		var view = plus.webview.getWebviewById('vehicleEdit.html');
        mui.fire(view, 'doit', {
            inputText: inputText,
            inputId:inputId
        });
        mui.back();
	})
});


//渲染载货类型列表
function renderCarryTypeList(result){
	if(! result){
		return;
	}
	var data = result.data.list;
	var _html = '';
	for(var i = 0;i < data.length;i++){
		_html +='<li class="attr-item" data-id="'+data[i].id+'">'+data[i].name+'</li>';
	}
	$(".mui-table-view").append(_html);
}