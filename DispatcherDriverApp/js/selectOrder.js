
var pageNo = 1;
//初始化区域滚动
mui('.mui-scroll-wrapper').scroll({indicators: false});

mui.plusReady(function() {
	console.log(new Date().getTime() + "|订单分配:plusReady");
	// initPageLanguage();
	// 加载订单列表
	var vehicleData = {
		url:httpServer + 'js/data/vehicleListData.json',
		data:{pageNo:pageNo},
		type:"get",
		success:renderOrderList,
		error:function(error){console.log(error)}
	};
	utils.loadData(vehicleData);

	//跳转详情页面
//	mui(".order-list").on("tap",".mui-table-view-cell",function(){
//		var oId = $(this).attr("data-id");
//		openDetailPage("订单详情页面","页面id",oId);
//	});


});

//渲染订单列表
function renderOrderList(result,down,up){
	if(! result){
		return;
	}
	var data = result.data;
	var _html = '';
	for(var i = 0;i < data.length;i++){
		_html += '<li class="mui-table-view-cell"  data-id = "'+data[i].id+'">\
					
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
/**
 * 下拉刷新具体业务实现
 */
function pulldownRefresh() {
	console.log("下拉刷新");
	var downRefresh = {
		url:httpServer + 'js/data/vehicleListData.json',
		data:{},
		type:"get",
		success:function(result){
			console.log("success");
			mui('#pullrefresh').pullRefresh().endPulldownToRefresh(); 
			renderOrderList(result,true,false);
		},
		error:function(error){console.log(error)}
	}
	utils.loadData(downRefresh);
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
		mui('#pullrefresh').pullRefresh().endPulldownToRefresh(); //refresh completed
	}, 1000);
}
var count = 0;
/**
 * 上拉加载具体业务实现
 */
function pullupRefresh() {
	console.log("上拉加载");
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
	}
	utils.loadData(upRefres);
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
/**
 * 打开详情页面
 * orderId: 订单的id
 **/
function openDetailPage(url,id,orderId) {
	mui.openWindow({
		url: url,
		id: id,
		show: {
			aniShow: 'slide-in-right',
			duration: 150
		},
		waiting: {
			autoShow: false
		},
		extras: {
			orderId: orderId,
		},
		styles: {
			hardwareAccelerated: true
		}
	});
};
