
var pageNo = 1;
mui.plusReady(function() {
	var vehicleData = {
		url:'./js/data/vehicleListData.json',
		data:{pageNo:pageNo},
		type:"get",
		success:renderVehicleList,
		error:function(error){console.log(error)}
	};
	utils.ajaxFn(vehicleData);

	//跳转编辑/查看页面
	$(".vehicle-list").on("tap",".mui-slider-handle",function(){
		var vId = $(this).parents(".mui-table-view-cell").attr("data-id");
		var options = {
			url:"vehicleAdd.html",
			id:"vehicleAdd.html",
			datas:{vehicleId:vId}
		};
		utils.openView(options);
	});

	//删除
	$(".vehicle-list").on("tap",".delete-btn",function(){
		var vId = $(this).parents(".mui-table-view-cell").attr("data-id");
		var elem = this;
		var btnArray = ['确认', '取消'];
		var li = elem.parentNode.parentNode;
		mui.confirm('确认删除该条记录？', '车辆管理', btnArray, function(e) {
			if (e.index == 0) {
				console.log("车辆id 是:"+vId);
				utils.loadData({
					url:httpServer + '删除的接口',
					data:{vehicleId:vId},
					type:"get",
					success:function(){
						console.log("删除成功");
						li.parentNode.removeChild(li);
					},
					error:function(error){
						console.log(error);
						mui.toast("删除失败");
					}
				});
			}
		});
	});
});

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

//渲染车辆列表
function renderVehicleList(result,down,up){
	if(! result){ return; }

	var data = result.data;
	var _html = '';
	for(var i = 0;i < data.length;i++){
		_html += '<li class="mui-table-view-cell"  data-id = "'+data[i].id+'">\
					<div class="mui-slider-right mui-disabled">\
						<a class="mui-btn mui-btn-red delete-btn">删除</a>\
					</div>\
			        <div class="mui-navigate-right mui-slider-handle">\
			        	<span class="item-title">'+(data[i].plateNo ? data[i].plateNo : "--")+'</span>\
			        	<p><span class="item-desc">车辆类型: </span><span>'+(data[i].vehicleType ? data[i].vehicleType : "--")+'</span></p>\
			        	<p><span class="item-desc">设备PN: </span><span>'+(data[i].pn ? data[i].pn : "--")+'</span></p>\
			        </div>\
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


// 下拉刷新具体业务实现
function pulldownRefresh() {
	var downRefresh = {
		url:'./js/data/vehicleListData.json',
		data:{pageNum:1},
		type:"get",
		success:function(result){
			mui('#pullrefresh').pullRefresh().endPulldownToRefresh(); 
			renderVehicleList(result,true,false);
		},
		error:function(error){console.log(error)}
	}
	utils.ajaxFn(downRefresh);
}
// 上拉加载具体业务实现
function pullupRefresh() {
	pageNo++;
	var upRefres = {
		url:'./js/data/vehicleListData.json',
		data:{pageNum:pageNo},
		type:"get",
		success:function(result){
			//result.more : 后台返回的还有没有更多数据的标识
			mui('#pullrefresh').pullRefresh().endPullupToRefresh(result.more); //参数为true代表没有更多数据了。
			renderVehicleList(result,false,true);
		},
		error:function(error){console.log(error)}
	}
	utils.ajaxFn(upRefres);
}
