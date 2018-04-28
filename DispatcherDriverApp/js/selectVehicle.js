var old_back=mui.back;
mui.back=function(){
	ws.hide("slide-out-right",200);
}

//初始化区域滚动
mui('.mui-scroll-wrapper').scroll({indicators: false});

mui.plusReady(function() {
	ws=plus.webview.currentWebview();
	
	console.log(new Date().getTime() + "|车辆选择:plusReady");
	var vehicleData = {
		url:httpServer + '/DriverAppWeb/common/getCommonVehicles.json',
		data:{},
		type:"get",
		success:renderVehicleList,
		error:function(error){console.log(error)}
	};
	utils.loadData(vehicleData);
	
	$(".mui-content").on("tap",".mui-table-view-cell",function(){
		
		var id = '',name = '';
		id = $(this).attr("data-id");
		name = $(this).find(".item-title").text();
		var view = plus.webview.getWebviewById('driverEdit.html');
        mui.fire(view, 'doit', {
             id: id,
            name:name
        });
        mui.back();
	})
});


//渲染车辆列表
function renderVehicleList(result){
	if(! result){
		return;
	}
	var data = result.data.list;
	var _html = '';
	for(var i = 0;i < data.length;i++){
		_html +=' <li class="mui-table-view-cell" data-id="'+data[i].vehicleId+'">\
			    	<label class="radio_list">\
			    		<span class="vehicle-radio">\
			    			<input name="radio" type="radio">\
							<span class="_radio"></span>\
			    		</span>\
						<div class="vehicle-item-box">\
							<span class="item-title">'+(data[i].plateNo)+'</span>\
							'+(data[i].selectDriverId?"(已使用)":"(未使用)")+'\
				        	<p><span class="item-desc">车辆类型: </span><span>'+(data[i].vehicleTypeName ? data[i].vehicleTypeName : "--")+'</span></p>\
				        	<p><span class="item-desc">设备PN: </span><span>'+(data[i].equipmentPn ? data[i].equipmentPn : "--")+'</span></p>\
						</div>\
					</label>\
			    </li>';
	}
	$(".mui-table-view").append(_html);
}