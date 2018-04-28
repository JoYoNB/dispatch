var mainPage={
	orderChartWidget:null, //订单统计柱状图
	cargoChartWidget_piece:null, //货物统计柱状图
	cargoChartWidget_side:null,
	cargoChartWidget_ton:null,
	orderRankWidget:null, //订单排行表
	cargoRankWidget:null, //送达货物排行
	format:'MM-dd',
	
	init:function(){
		var self=this;
		self._render();
		self._bind();
	},
	_render:function(){
		var self = this;
		self.getTotalStatistics();
		self.getOrderStatistics(0);
		self.getCargoStatistics(0);
		self.orderRank(0);
	},
	_bind:function(){
		var self = this;
		$('#orderStatistics').unbind('click').click(function(){
			$(this).parent().addClass('active');
			$('#cargoStatistics').parent().removeClass('active');
			$('#unitSelect').parent().addClass('hide');
			$('#cargoGroup').addClass('hide');
			$('#orderGroup').removeClass('hide');
		});
		$('#cargoStatistics').unbind('click').click(function(){
			$(this).parent().addClass('active');
			$('#orderStatistics').parent().removeClass('active');
			$('#unitSelect').parent().removeClass('hide');
			$('#orderGroup').addClass('hide');
			$('#cargoGroup').removeClass('hide');
		});
		$('.city-btn').unbind('click').click(function(){
			var _this = $(this);
			$('.city-btn').removeClass('current');
			_this.addClass('current');
			self.getOrderStatistics(_this.val());
			self.getCargoStatistics(_this.val());
			$('#orderRankWidget').empty();
			$('#cargoRankWidget').empty();
			self.orderRank(_this.val());
			self.cargoRank(_this.val());
		});
		$('#unitSelect').on('change',function(){
			var _this = $(this);
			var unit = _this.val();
			$('.cargo-chart').addClass('hide');
			if(unit==0){
				$('#cargoChartWidget_piece').removeClass('hide');
			}else if(unit==1){
				$('#cargoChartWidget_side').removeClass('hide');
			}else if(unit==2){
				$('#cargoChartWidget_ton').removeClass('hide');
			}
			//console.log($('.current').val());
			self.cargoRank($('.current').val());
		});
	},
		
	getTotalStatistics:function(){
		var self=this;
		var param = {};
		CommonUtils.async({
			url:"/DispatcherWeb/consignor/mainpage/totalStatistics.json",
			data:param,
			success:function(result){
				if(result.code==0){
					if(result.data){
						if(result.data.totalOrder){
							$('#totalOrder').empty().text(result.data.totalOrder);
						}
						if(result.data.totalWeight){
							$('#totalWeight').empty().html(result.data.totalWeight+'<b class="unit">吨</b>');
						}
						if(result.data.totalVolume){
							$('#totalVolume').empty().html(result.data.totalVolume+'<b class="unit">方</b>');
						}
						if(result.data.totalNum){
							$('#totalNum').empty().html(result.data.totalNum+'<b class="unit">件</b>');
						}
					}
				}else{
					//MessageUtil.alert("获取订单详情失败");
				}
			},
			error:function(result){
				//MessageUtil.alert("获取订单详情失败");
			}
		});
	},
	getOrderStatistics:function(timeOpt){
		var self=this;
		var param = CommonUtils.Date.getTimeObject(timeOpt);
		//console.log(param);
		CommonUtils.async({
			url:"/DispatcherWeb/consignor/mainpage/orderStatistics.json",
			data:param,
			success:function(result){
				if(result.code==0){
					//if(result.data){
						$('#orderChartWidget').empty();
						var xData = CommonUtils.Date.getDaysArr(param.startTime,param.endTime,self.format);
						//console.log(xData);
						var yData = [];
						var yObject ={
							name:'订单数量',
							data:result.data
						}
						yData.push(yObject);
						//console.log(yData);
						self.orderChartWidget = $('#orderChartWidget').ColumnChartWidget({
							width:1200,
							yAxisTitle:'订单数量',
							yUnit:'个',
							xData:xData,
							yData:yData
						});
					//}
				}else{
					//MessageUtil.alert("获取订单详情失败");
				}
			},
			error:function(result){
				//MessageUtil.alert("获取订单详情失败");
			}
		});
	},
	getCargoStatistics:function(timeOpt){
		var self=this;
		var param = CommonUtils.Date.getTimeObject(timeOpt);
		//console.log(param);
		CommonUtils.async({
			url:"/DispatcherWeb/consignor/mainpage/cargoStatistics.json",
			data:param,
			success:function(result){
				if(result.code==0){
					//if(result.data){
						$('#cargoChartWidget_piece').empty();
						$('#cargoChartWidget_side').empty();
						$('#cargoChartWidget_ton').empty();
						var xData = CommonUtils.Date.getDaysArr(param.startTime,param.endTime,self.format);
						var yData_piece = [];
						var _dataP = null;
						if(result.data){
							_dataP = result.data.piece;
						}
						var yObject_piece ={
							name:'货物数量（件）',
							data:_dataP
						}
						yData_piece.push(yObject_piece);
						console.log(yData_piece);
						self.cargoChartWidget_piece = $('#cargoChartWidget_piece').ColumnChartWidget({
							width:1200,
							yAxisTitle:'货物数量',
							yUnit:'件',
							xData:xData,
							yData:yData_piece
						});
						var yData_side = [];
						var _dataS = null;
						if(result.data){
							_dataS = result.data.side;
						}
						var yObject_side ={
							name:'货物体积（方）',
							data:_dataS
						}
						yData_side.push(yObject_side);
						self.cargoChartWidget_side = $('#cargoChartWidget_side').ColumnChartWidget({
							width:1200,
							yAxisTitle:'货物体积',
							yUnit:'方',
							xData:xData,
							yData:yData_side
						});
						var yData_ton = [];
						var _dataT = null;
						if(result.data){
							_dataT = result.data.ton;
						}
						var yObject_ton ={
							name:'货物重量（吨）',
							data:_dataT
						}
						yData_ton.push(yObject_ton);
						self.cargoChartWidget_ton = $('#cargoChartWidget_ton').ColumnChartWidget({
							width:1200,
							yAxisTitle:'货物重量',
							yUnit:'吨',
							xData:xData,
							yData:yData_ton
						});
						
					//}
				}else{
					//MessageUtil.alert("获取订单详情失败");
				}
			},
			error:function(result){
				//MessageUtil.alert("获取订单详情失败");
			}
		});
	},
	orderRank:function(timeOpt){
		self.orderRankWidget=$('#orderRankWidget').TableWidget({
			url:"/DispatcherWeb/consignor/mainpage/orderRank.json",
			param:CommonUtils.Date.getTimeObject(timeOpt),
			showNum:true, //显示序号
			showSelect:false,//不显示选择框
			showPagination:false, //不显示分页
			showFilter:false, //不显示列过滤
			cells:[{
				field:"name",
				text:"发货点名称"
			},{
				field:"total",
				text:"数量"
			}]
		});
		
	},
	cargoRank:function(timeOpt){
		//console.log('1111111111111111111111111111111');
		if(!self.cargoRankWidget){
			$('#cargoRankWidget').empty();
		}
		var _param=CommonUtils.Date.getTimeObject(timeOpt);
		var unit=$('#unitSelect').val();
		//console.log(unit);
		var _text = '件';
		switch(unit){
		case '0':_param.type='num';break;
		case '1':_param.type='volume';_text = '方';break;
		case '2':_param.type='weight';_text = '吨';break;
		default:break;
		}
		console.log(_param);
		//console.log(_text);
		self.cargoRankWidget=$('#cargoRankWidget').TableWidget({
			url:"/DispatcherWeb/consignor/mainpage/deliveryCargoRank.json",
			param:_param,
			showNum:true, //显示序号
			showSelect:false,//不显示选择框
			showPagination:false, //不显示分页
			showFilter:false, //不显示列过滤
			cells:[{
				field:"name",
				text:"配送点名称"
			},{
				field:'total',
				text:_text
			}]
		});
	}

}
$(function(){
	mainPage.init();
});