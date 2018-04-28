mui.init({
	swipeBack: true //启用右滑关闭功能
});
//初始化区域滚动
mui('.mui-scroll-wrapper').scroll();

mui.plusReady(function() {
	// 获得参数
	var self = plus.webview.currentWebview();
    var vId = self.vehicleId;//获得参数
    console.log(vId);
    getVehicleInfo(vId);
    
});

//根据id 查询车辆信息
function getVehicleInfo(vehicleId){
	utils.loadData({
		url:httpServer+'js/data/vehicleDetail.json',
		type:'get',
		data:{vehicleId:vehicleId},
		success:function(result){
			if(result){
				//获取数据成功，渲染数据
				renderVehicleInfo(result);
			}	
		},
		error:function(error){
			console.log(error);
		}
	});
}

function renderVehicleInfo(result){
	var data = result.data;
	
	if(data.img){
		$(".v-img").attr("src",data.img);
	}
//	$(".plateNo").val(data.plateNo ? data.plateNo : '');
//	$(".pn").val(data.pn ? data.pn : '');
//	$(".maxWeight").val(data.maxWeight ? data.maxWeight : '');
//	$(".totalLength").val(data.totalLength ? data.totalLength : '');
//	$(".length").val(data.length ? data.length : '');
//	$(".width").val(data.width ? data.width : '');
//	$(".height").val(data.height ? data.height : '');
//	$(".payload").val(data.payload ? data.payload : '');
//	$(".payloadRatio").val(data.payloadRatio ? data.payloadRatio : '');
//	$(".minR").val(data.minR ? data.minR : '');
//	$(".powerDensity").val(data.powerDensity ? data.powerDensity : '');
}