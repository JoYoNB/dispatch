var CarrierIndex={
	tableWidget:null,//表格控件
	init:function(){
		var self=this;
		self._render();
		self._bind();
		self._getTotalAmount();
		self._listFinishedOrderDist();
		Assignment.listOrders();
	},
	_render:function(){
		
		var self=this;
		var timeFlag = 0;
		$(".city-btn").each(function(){
			if($(this).hasClass("current")){
				timeFlag = $(this).attr("value");
			}
		});
		var timeObj = CommonUtils.Date.getTimeObject(timeFlag);
		self.tableWidget=$("#consignorRankingChart").TableWidget({
			url:"/DispatcherWeb/carrier/index/listConsignorRanking.json",
			param:timeObj,
			showNum:true,
			showSelect:false,
			showPagination:false,
			showFilter:false,
			cells:[{
				field:"consignorName", 
				text:"货主",
			},{
				field:"amount",
				text:"数量"
			}]
		});
		$("#consignorRankingChart").hide();
	},
	_bind:function(){
		var self = this;
		$('.city-btn').unbind("click").click(function(){
			if(!$(this).hasClass("current")){
				$('.city-btn').removeClass('current');
				$(this).addClass("current");
				var chartFlag = $(".city-tap").find(".active").attr("value");
				self._executeFn(chartFlag);
			}
		});
		$('.tap-item').unbind("click").click(function(){
			if(!$(this).hasClass("active")){
				$('.tap-item').removeClass('active');
				$(this).addClass("active");
				var chartFlag = $(this).attr("value");
				self._executeFn(chartFlag);
			}
		});
	},
	_executeFn: function(chartFlag){
		var self = this;
		if(chartFlag == 0){
			self._listFinishedOrderDist();
		} else if(chartFlag == 1){
			self._listDeptOrderDist();
		} else if(chartFlag == 2){
			self._listConsignorRanking();
		}
	},
	_getTotalAmount:function(){ //完成订单总数
		CommonUtils.async({
			url:"/DispatcherWeb/carrier/index/getTotalAmount.json",
			data:{},
			success:function(result){
				if(result.code != 0)
					return;
				$("#totalAmount").text(result.data.amount);
			},
			error:function(result){
			}
		});
	},
	_listFinishedOrderDist:function(){ // 获取完成订单分布
		var self = this;
		var timeFlag = self._getTimeFlag();
		var timeObj = CommonUtils.Date.getTimeObject(timeFlag);
		CommonUtils.async({
			url:"/DispatcherWeb/carrier/index/listFinishedOrderDist.json",
			data:timeObj,
			success:function(result){
				if(result.code != 0)
					return;
				var res = result.data;
				var option = {};
				var data = {};
				for(var i=0; i<res.length; i++){
					var d = res[i];
					data[d.day] = d.amount;
				}
				var yData = [{name:'订单统计趋势图', type:'column', data: data}];
				option.xData = CommonUtils.Date.getDaysArr(timeObj.startTime, timeObj.endTime, 'MM-dd');
				option.yData = yData;
				option.yInterval = 1;
				option.width = 1000
				$("#finishedOrderChart").ColumnChartWidget(option);
				$("#finishedOrderChart").show();
				$("#deptOrderChart").hide();
				$("#consignorRankingChart").hide();
			},
			error:function(result){
			}
		});
	},
	_listDeptOrderDist:function(){  // 部门订单完成分布
		var self = this;
		var timeFlag = self._getTimeFlag();
		var timeObj = CommonUtils.Date.getTimeObject(timeFlag);
		CommonUtils.async({
			url:"/DispatcherWeb/carrier/index/listDeptOrderDist.json",
			data:timeObj,
			success:function(result){
				if(result.code != 0)
					return;
				var res = result.data.deptOrders;
				var option = {};
				var data = {};
				for(var i=0; i<res.length; i++){
					var d = res[i];
					data[d.deptName] = d.amount;
				}
				var xData = result.data.subDepts;
				var yData = [{name:'部门订单分布统计', type:'column', data: data}];
				option.xData = xData;
				option.yData = yData;
				option.yInterval = 1;
				option.width = 1000;
				$("#deptOrderChart").ColumnChartWidget(option);
			
				$("#finishedOrderChart").hide();
				$("#deptOrderChart").show();
				$("#consignorRankingChart").hide();
			},
			error:function(result){
			}
		});
	},
	_listConsignorRanking:function(){  // 客户订单排行
		var self = this;
		/*var timeFlag = 0;
		$(".city-btn").each(function(){
			if($(this).hasClass("current")){
				timeFlag = $(this).attr("value");
			}
		});
		var timeObj = CommonUtils.Date.getTimeObject(timeFlag);
		CommonUtils.async({
			url:"/DispatcherWeb/carrier/index/listConsignorRanking.json",
			data:timeObj,
			success:function(result){
				var res = result.data;
				var option = {};
				var data = {};
				var xData = [];
				for(var i=0; i<res.length; i++){
					var d = res[i];
					data[d.consignorName] = d.amount;
					xData[xData.length] = d.consignorName;
				}
				
				if($.isEmptyObject(data)){
					$("#consignorRankingChart").empty();
				}else{
					var yData = [{name:'客户订单排行', type:'bar', data: data}];
					option.xData = xData;
					option.yData = yData;
					option.yInterval = 1;
					$("#consignorRankingChart").ColumnChartWidget(option);
				}
				
				
			},
			error:function(result){
			}
		});*/
		var timeFlag = self._getTimeFlag();
		var timeObj = CommonUtils.Date.getTimeObject(timeFlag);
		self.tableWidget.query(timeObj);
		$("#finishedOrderChart").hide();
		$("#deptOrderChart").hide();
		$("#consignorRankingChart").show();
	},
	_getTimeFlag : function(){ // 获取查询时间标识 
		// 0本周、1上周、2本月、3上月、4今天、5昨天
		var timeFlag = 0;
		$(".city-btn").each(function(){
			if($(this).hasClass("current")){
				timeFlag = $(this).attr("value");
			}
		});
		return timeFlag;
	}
}
$(function(){
	CarrierIndex.init();
});