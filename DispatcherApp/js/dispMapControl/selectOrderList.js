var old_back = mui.back;
mui.back = function(){
	thisView.hide("slide-out-right",200);
}
var pageNo = 1;
httpServer = './';
mui.plusReady(function() {
	thisView = plus.webview.currentWebview();
	
	
	var orderData = {
		url:httpServer + 'js/data/orderListData.json',
		data:{pageNo:pageNo},
		type:"get",
		success:renderOrderList,
		error:function(error){console.log(error)}
	};

	utils.ajaxFn(orderData);
	
	//	//跳转详情页面
	// $(".order-list").on("tap",".mui-table-view-cell",function(){
	// 	var oId = $(this).attr("data-id");
	// 	utils.openNewWindow({
	// 		url:"",
	// 		id:"",
	// 		data:{orderId:oId}
	// 	});
	// });
	
	//分配成功
	// $(".order-list").on("tap",".radio_list",function(){
	$(".mui-content").on("tap",".submit-btn",function(){
		var checked = null;
		var temp = {};
		var oId;
		var detailsData;
		$(".mui-table-view-cell input[type=radio]").each(function(i,index){
			if($(this).is(":checked")){
				oId = $(this).parents(".mui-table-view-cell").attr("data-id");
				detailsData = $(this).parents(".mui-table-view-cell").find(".selected-order-data").val();
			}
		});
			
		console.log(JSON.parse(detailsData));
		if(detailsData){
			detailsData = JSON.parse(detailsData);
			

			temp.orderId = detailsData.orderId;
			temp.startAddress = detailsData.startAddress;
			temp.endAddress = detailsData.endAddress;
			temp.distance = detailsData.distance;
			temp.goodsTypeName = detailsData.goodsTypeName;
			temp.goodsNum = detailsData.goodsNum;
			temp.vehicleTypeName = detailsData.vehicleTypeName;
			temp.cost = detailsData.cost;
			temp.loadData = detailsData.loadData;
		}
		console.log("temp :" + JSON.stringify(temp));
		var view = plus.webview.getWebviewById('selectOrder.html');
        mui.fire(view, 'selected', {
            detailsData:temp
        });
	});

});
//$(".mui-content").on("tap",".submit-btn",function(){
//		var checked = null;
//		var temp = {};
//		var oId;
//		var detailsData;
//		$(".mui-table-view-cell input[type=radio]").each(function(i,index){
//			if($(this).is(":checked")){
//				oId = $(this).parents(".mui-table-view-cell").attr("data-id");
//				detailsData = $(this).parents(".mui-table-view-cell").find(".selected-order-data").val();
//			}
//		});
//			
//		console.log(JSON.parse(detailsData));
//		if(detailsData){
//			detailsData = JSON.parse(detailsData);
//			
//
//			temp.orderId = detailsData.orderId;
//			temp.startAddress = detailsData.startAddress;
//			temp.endAddress = detailsData.endAddress;
//			temp.distance = detailsData.distance;
//			temp.goodsTypeName = detailsData.goodsTypeName;
//			temp.goodsNum = detailsData.goodsNum;
//			temp.vehicleTypeName = detailsData.vehicleTypeName;
//			temp.cost = detailsData.cost;
//			temp.loadData = detailsData.loadData;
//		}
//		console.log("temp :" + JSON.stringify(temp));
//		var view = plus.webview.getWebviewById('selectOrder.html');
//      mui.fire(view, 'selected', {
//          detailsData:temp
//      });
//});
var orderData = {
		url:httpServer + 'js/data/orderListData.json',
		data:{pageNo:pageNo},
		type:"get",
		success:renderOrderList,
		error:function(error){console.log(error)}
	};

	utils.ajaxFn(orderData);

mui.init({
	swipeBack: false,
	pullRefresh: {
		container: '#pullrefresh',
		down: {
			callback: pulldownRefresh
		},
		up: {
			contentrefresh: '正在加载...',
			callback: pullupRefresh
		}
	}
});
//渲染订单列表
function renderOrderList(result,down,up){
	if(! result){ return; }

	var data = result.data;
	var _html = '';
	for(var i = 0;i < data.length;i++){
		var tempData = JSON.stringify(data[i]);
		_html += '<li class="mui-table-view-cell mui-navigate-right"  data-id = "'+data[i].orderId+'">\
					<label class="radio_list">\
			    		<span class="vehicle-radio">\
			    			<input name="radio" type="radio" data-id="'+data[i].orderId+'">\
							<span class="_radio"></span>\
			    		</span>\
					</label>\
					<div class="vehicle-item-box">\
						<span class="item-title">'+ (data[i].startAddress ? data[i].startAddress : "--") +' -' + (data[i].endAddress ? data[i].endAddress : "--") + '</span>\
			        	<p><span>'+ (data[i].goodsTypeName ? data[i].goodsTypeName : "--") +'</span><span>'+ (data[i].goodsNum ? data[i].goodsNum : "--" ) +'</span><span>'+ (data[i].distance ? data[i].distance : "--") +'</span><span>'+ (data[i].vehicleTypeName ? data[i].vehicleTypeName : "--") +'</span><span>'+ (data[i].vehicleTypeName ? data[i].vehicleTypeName : "--") +'</span></p>\
			        	<p><span>'+ (data[i].time ? data[i].time : "--") +'</span></p>\
					</div>\
					<input type="text" class="hide selected-order-data" value='+ tempData+ '>\
			    </li>';
	}
	if(down){
		$(".mui-table-view").prepend(_html);
	}else if(up){
		$(".mui-table-view").append(_html);
	}else{
		$(".mui-table-view").html(_html);
	}
}

/**
   * 下拉刷新具体业务实现
   */
function pulldownRefresh() {
	console.log("下拉刷新");
	var downRefresh = {
		url:httpServer + 'js/data/orderListData.json',
		data:{},
		type:"get",
		success:function(result){
			mui('#pullrefresh').pullRefresh().endPulldownToRefresh(); 
			renderOrderList(result,true,false);
		},
		error:function(error){console.log(error)}
	}
	utils.ajaxFn(downRefresh);
	// 测试代码
	setTimeout(function() {
		var table = document.body.querySelector('.mui-table-view');
		var cells = document.body.querySelectorAll('.mui-table-view-cell');
		for (var i = cells.length, len = i + 3; i < len; i++) {
			var li = document.createElement('li');
			li.className = 'mui-table-view-cell';
			li.innerHTML = '<a class="mui-navigate-right">Item ' + (i + 1) + '</a>';
			//下拉刷新，新纪录插到最前面；
			$(".mui-table-view").prepend(li);
		}
		mui('#pullrefresh').pullRefresh().endPulldownToRefresh(); 
	}, 1000);
};
var count = 0;
/**
   * 上拉加载具体业务实现
   */
function pullupRefresh() {
	var upRefres = {
		url:httpServer + 'js/data/orderListData.json',
		data:{},
		type:"get",
		success:function(result){
			//result.more : 后台返回的还有没有更多数据的标识
			mui('#pullrefresh').pullRefresh().endPullupToRefresh(result.more); //参数为true代表没有更多数据了。
			renderOrderList(result,false,true);
		},
		error:function(error){console.log(error)}
	};
	utils.ajaxFn(upRefres);
	//测试代码
	setTimeout(function() {
		mui('#pullrefresh').pullRefresh().endPullupToRefresh((++count > 2)); //参数为true代表没有更多数据了。
		var table = document.body.querySelector('.mui-table-view');
		var cells = document.body.querySelectorAll('.mui-table-view-cell');
		for (var i = cells.length, len = i + 20; i < len; i++) {
			var li = document.createElement('li');
			li.className = 'mui-table-view-cell';
			li.innerHTML = '<a class="mui-navigate-right">Item ' + (i + 1) + '</a>';
			table.appendChild(li);
		}
	}, 1000);
}
