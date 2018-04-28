var limit = 10;
var downId=null;
var upId=null;
mui('.mui-scroll-wrapper').scroll();
mui.plusReady(function() {
	console.log(new Date().getTime() + "|站点管理:plusReady");
	// initPageLanguage();
	// 加载站点列表
	var siteList = {
		url:httpServer + '/DispatcherAppWeb/consignor/site/queryList.json',
		data:{limit:limit},
		type:"post",
		success:renderSiteList,
		error:function(error){console.log(error)}
	};
	utils.ajaxFn(siteList);
	//详情
	mui(".site-list").on("tap",".mui-slider-handle",function(){
		var vId = $(this).parents(".mui-table-view-cell").attr("data-id");
		openDetailPage("siteInfo.html","siteInfo.html",vId);
	});
	//编辑
	mui(".site-list").on("tap",".edit-btn",function(){
		var vId = $(this).parents(".mui-table-view-cell").attr("data-id");
		openDetailPage("siteUpdate.html","siteUpdate.html",vId);
	});
	//删除
	mui(".site-list").on("tap",".delete-btn",function(){
		var sId = $(this).parents(".mui-table-view-cell").attr("data-id");
		var elem = this;
		var btnArray = ['确认', '取消'];
		var li = elem.parentNode.parentNode;
		mui.confirm('确认删除该条记录？', '站点管理', btnArray, function(e) {
			if (e.index == 0) {
				console.log("站点id 是:"+sId);
				//发送站点id 到后端
				utils.ajaxFn({
					url:httpServer + '/DispatcherAppWeb/consignor/site/delete.json',
					data:{siteId:sId},
					type:"post",
					success:function(result){
						console.error(JSON.stringify(result));
						var code=result.code;
						console.error(JSON.stringify(result.code));
						if(code==0){
							console.log("删除成功");
							li.parentNode.removeChild(li);
						}else if(code==1003){
							mui.toast("没有权限"); 
						}else if(code==120002){
							mui.toast("站点已经被使用，不能删除"); 
						}
					},
					error:function(error){
						console.error(JSON.stringify(error));
						mui.toast("删除失败");
					}
				});
			} else {
				setTimeout(function() {
//					$.swipeoutClose(li);
				}, 0);
			}
		});
	});
});

//初始化scroll控件,开启上线滚动
mui('.mui-scroll-wrapper').scroll({
});
//渲染站点列表
function renderSiteList(result,down,up){
	if(! result){
		return;
	}
	if(!result.data){
		return;
	}
	if(!result.data.list){
		return;
	}
	if(!result.data.list.length){
		return;
	}
	var list = result.data.list;
	console.info(JSON.stringify(result));
	var _html = '';
	for(var i = 0;i < list.length;i++){
		_html += '<li class="mui-table-view-cell"  data-id = "'+list[i].id+'">'
					+'<div class="mui-slider-right mui-disabled">'
						+'<a class="mui-btn mui-btn-red delete-btn">删除</a>'
						+'<a class="mui-btn mui-btn-yellow edit-btn">编辑</a>'
					+'</div>'
			        +'<div class="mui-navigate-right mui-slider-handle">'
			        +'<span class="item-title">'+(list[i].siteName ? list[i].siteName : "--" )+'</span>'
			        +'<p><span class="item-desc">位置: </span><span>'+(list[i].address ? list[i].address : "--" )+'</span></p>'
			        +'<p><span class="item-desc">联系人: </span><span>'+(list[i].linkName ? list[i].linkName : "--" ) +'|'+ (list[i].linkPhone ? list[i].linkPhone : "--" ) +'</span></p>'
			        +'</div>'
			   	 +'</li>';
	}
	if(down){
		downId=list[0].id;
		$(".mui-table-view").prepend(_html);
	}else if(up){
		upId=list[list.length-1].id;
		$(".mui-table-view").append(_html);
	}else{
		downId=list[0].id;
		upId=list[list.length-1].id;
		console.info("downId="+downId);
		console.info("upId="+upId);
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
		url:httpServer + '/DispatcherAppWeb/consignor/site/queryList.json',
		data:{limit:limit,downId:downId},
		type:"post",
		success:function(result){
			console.log("success");
			mui('#pullrefresh').pullRefresh().endPulldownToRefresh(); 
			renderSiteList(result,true,false);
		},
		error:function(error){console.log(error)}
	}
	utils.ajaxFn(downRefresh);
}
var count = 0;
/**
 * 上拉加载具体业务实现
 */
function pullupRefresh() {
	console.log("上拉加载");
	var upRefres = {
		url:httpServer + '/DispatcherAppWeb/consignor/site/queryList.json',
		data:{limit:limit,upId:upId},
		type:"post",
		success:function(result){
			//result.more : 后台返回的还有没有更多数据的标识
			mui('#pullrefresh').pullRefresh().endPullupToRefresh(result.more); //参数为true代表没有更多数据了。
			renderSiteList(result,false,true);
		},
		error:function(error){console.log(error)}
	}
	utils.ajaxFn(upRefres);
}
/**
 * 打开详情页面
 * siteId: 车辆的id
 **/
function openDetailPage(url,id,siteId) {
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
			siteId: siteId,
		},
		styles: {
			hardwareAccelerated: true
		}
	});
};

