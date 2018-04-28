var pageNo = 1;
mui.plusReady(function() {
	var driverData = {
//		url:httpServer+'js/data/driverListData.json',
		url:'./js/data/driverListData.json',
		data:{},
		type:"get",
		success:renderDriverList,
		error:function(error){console.log(error)}
	};
	utils.ajaxFn(driverData);
	//跳转编辑/查看页面
	$(".driver-list").on("tap",".mui-slider-handle",function(){
		var dId = $(this).parents(".mui-table-view-cell").attr("data-id");
		var options = {
			url:"driverAdd.html",
			id:"driverAdd.html",
			datas:{driverId:dId}
		};
		utils.openView(options);
	});
	//删除
	$(".driver-list").on("tap",".delete-btn",function(){
		var vId = $(this).parents(".mui-table-view-cell").attr("data-id");
		var elem = this;
		var btnArray = ['确认', '取消'];
		var li = elem.parentNode.parentNode;
		mui.confirm('确认删除该条记录？', '司机管理', btnArray, function(e) {
			if (e.index == 0) {
				console.log("司机id 是:"+vId);
				//发送车辆id 到后端
				utils.ajaxFn({
					url:httpServer + '删除的接口',
					data:{vehicleId:vId},
					type:"get",
					success:function(result){
						if(result.code == 0){
							mui.toast("删除成功");
						}
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
function renderDriverList(result,down,up){
	if(! result ){ return; }
	if( result.code == 0) { return; }

	var data = result.data;
	var _html = '';
	for(var i = 0;i < data.length;i++){
		_html += '<li class="mui-table-view-cell"  data-id = "'+data[i].id+'">\
					<div class="mui-slider-right mui-disabled">\
						<a class="mui-btn mui-btn-red delete-btn">删除</a>\
					</div>\
			        <div class="mui-navigate-right mui-slider-handle">\
			        	<p><span class="item-title">'+(data[i].name ? data[i].id : "--") + ' | ' + (data[i].phone ? data[i].phone : "--") +'</span></p>\
			        	<p><span class="item-desc">位置: </span><span>'+ (data[i].localtion ? data[i].localtion : "--") +'</span></p>\
			        	<p><span class="item-desc">部门: </span><span>' + (data[i].deptName ? data[i].deptName : "--") + '</span></p>\
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
//下拉刷新
function pulldownRefresh() {
	console.log("下拉刷新");
	var downRefresh = {
//		url:httpServer + 'js/data/driverListData.json',
		url:'./js/data/driverListData.json',
		data:{pageNum:1},
		type:"get",
		success:function(result){
			mui('#pullrefresh').pullRefresh().endPulldownToRefresh(); 
			renderDriverList(result,true,false);
		},
		error:function(error){console.log(error)}
	}
	utils.ajaxFn(downRefresh);
}
 //上拉加载
function pullupRefresh() {
	pageNo++;
	var upRefres = {
		url:'./js/data/driverListData.json',
		data:{pageNum:pageNo},
		type:"get",
		success:function(result){
			//result.more : 后台返回的还有没有更多数据的标识
			mui('#pullrefresh').pullRefresh().endPullupToRefresh(result.more); //参数为true代表没有更多数据了。
			renderDriverList(result,false,true);
		},
		error:function(error){console.log(error)}
	}
	utils.ajaxFn(upRefres);
}
