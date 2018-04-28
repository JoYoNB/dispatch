var SettleIndex={
	tableWidget:null,//表格控件
	dualYWidget:null,
	startTime:null,
	endTime:null,
	xData:[],
	yData:[],
	init:function(){
		var self=this;
		self.startTime=CommonUtils.Date.getWeekStartDate();
		self.endTime=CommonUtils.Date.getWeekEndDate();
		self.xData=CommonUtils.Date.getDaysArr(self.startTime,self.endTime);
		self._render();
		self._bind();
		self._loadData();
	},
	_render:function(){
		var self=this;
		self.tableWidget=$("#tableWidget").TableWidget({
			url:"/DispatcherWeb/settlementIndex/orderRank.json",
			param:{startTime:self.startTime,endTime:self.endTime},
			showNum:true,
			showSelect:false,
			showPagination:false,
			showFilter:false,
			cells:[{
				field:"name", 
				text:"承运商",
			},{
				field:"amount",
				text:"金额"
			},{
				field:"counts",
				text:"数量"
			}]
		});
	},
	_bind:function(){
		var self=this;
		//本周
		$("#thisWeek").unbind("click").click(function(){
			$("#thisWeek").addClass("current");
			$("#lastWeek").removeClass("current");
			$("#thisMonth").removeClass("current");
			$("#lastMonth").removeClass("current");
			self.startTime=CommonUtils.Date.getWeekStartDate();
			self.endTime=CommonUtils.Date.getWeekEndDate();
			self.xData=CommonUtils.Date.getDaysArr(self.startTime,self.endTime);
			self._loadData();
			self.tableWidget.query({startTime:self.startTime,endTime:self.endTime});
		});
		
		//上奏
		$("#lastWeek").unbind("click").click(function(){
			$("#thisWeek").removeClass("current");
			$("#lastWeek").addClass("current");
			$("#thisMonth").removeClass("current");
			$("#lastMonth").removeClass("current");
			self.startTime=CommonUtils.Date.getLastWeekStartDate();
			self.endTime=CommonUtils.Date.getLastWeekEndDate();
			self.xData=CommonUtils.Date.getDaysArr(self.startTime,self.endTime);
			self._loadData();
			self.tableWidget.query({startTime:self.startTime,endTime:self.endTime});
		});
		
		//本月
		$("#thisMonth").unbind("click").click(function(){
			$("#thisWeek").removeClass("current");
			$("#lastWeek").removeClass("current");
			$("#thisMonth").addClass("current");
			$("#lastMonth").removeClass("current");
			self.startTime=CommonUtils.Date.getMonthStartDate();
			self.endTime=CommonUtils.Date.getMonthEndDate();
			self.xData=CommonUtils.Date.getDaysArr(self.startTime,self.endTime);
			self._loadData();
			self.tableWidget.query({startTime:self.startTime,endTime:self.endTime});
		});
		
		//上月
		$("#lastMonth").unbind("click").click(function(){
			$("#thisWeek").removeClass("current");
			$("#lastWeek").removeClass("current");
			$("#thisMonth").removeClass("current");
			$("#lastMonth").addClass("current");
			self.startTime=CommonUtils.Date.getLastMonthStartDate();
			self.endTime=CommonUtils.Date.getLastMonthEndDate();
			self.xData=CommonUtils.Date.getDaysArr(self.startTime,self.endTime);
			self._loadData();
			self.tableWidget.query({startTime:self.startTime,endTime:self.endTime});
		});
	},
	_loadData:function(){
		var self=this;
		self.yData=[];
		var param={
				startTime:self.startTime,
				endTime:self.endTime
				};
		CommonUtils.async({
			url:"/DispatcherWeb/settlementIndex/orderBar.json",
			data:param,
			success:function(result){
				if(result.code==0){
					var ret=result.data||{};
					var list=ret.list||[];
					var yData_culomn={};
					var yData_culomn_data={};
					var yData_spline={};
					var yData_spline_data={};
					for (var i = 0; i < list.length; i++) {
						var temp=list[i];
						yData_culomn_data[temp.settleTime]=temp.counts;
						yData_spline_data[temp.settleTime]=temp.amount;
					}
					yData_culomn.name="数量";
					yData_culomn.type="column";
					yData_culomn.data=yData_culomn_data;
					yData_spline.name="金额";
					yData_spline.type="spline";
					yData_spline.data=yData_spline_data;
					self.yData.push(yData_culomn);
					self.yData.push(yData_spline);
				}
			}
		});
		CommonUtils.async({
			url:"/DispatcherWeb/settlementIndex/orderSum.json",
			data:param,
			success:function(result){
				if(result.code==0){
					var data=result.data;
					$("#cumulativeCount").html(data.cumulativeCount);
					$("#newCount").html(data.newCount);
					$("#cumulativeAmount").html(data.cumulativeAmount+"元");
					$("#newAmount").html(data.newAmount+"元");
				}
			}
		});
		setTimeout(function(){
			self.dualYWidget=$("#dualYWidget").DualYAxisChartWidget({
				/*多个Y轴的配置*/
				width:1000,
				yAxis:[{
					unit:"个",
					name:"数量",
//					tickInterval:10
				},{
					unit:"元",
					name:"金额",
//					tickInterval:100
				}],
				xData:self.xData,
				yData:self.yData
			});
		},500);
	}
}
$(function(){
	SettleIndex.init();
});