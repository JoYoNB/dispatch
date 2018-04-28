var CusomerStats={
	tableWidget:null,//表格控件
	deptWidget:null,//部门选择控件
	timeWidgetStart:null,//筛选条件开始时间
	timeWidgetEnd:null,//筛选条件结束时间
	
	init:function(){
		var self=this;
		self._render();
		self._bind();
		
		self._setTotalCustomerNum();
		
		self._search();
	},
	_render:function(){
		var self=this;
		//设置底部菜单连接
		Footer.nav("客户统计","carrier/stats/customerStats.html");
		
		self.deptWidget=$("#deptWidget").DeptWidget({
			showAllItem:true //会多出一行 "全部" 的选择
		});
		
		self.timeWidgetStart=$("#timeWidgetStart").TimeWidget({
			format:"yyyy-mm-dd",
			width:"228px",
			showText:"开始时间"
		});
		
		self.timeWidgetEnd=$("#timeWidgetEnd").TimeWidget({
			format:"yyyy-mm-dd",
			width:"228px",
			showText:"结束时间"
		});
		
		self.tableWidget= $("#tableWidget").TableWidget({
			url:"/DispatcherWeb/carrier/stats/listCustomerOrderCount.json",
			cells:[{
				field:"consignorName",
				text:"客户名称"
			},{
				field:"amount",
				text:"新增订单"
			},{
				field:"total",
				text:"累计订单"
			}],
			showSelect:false
		});
		
	},
	
	_bind:function(){
		var self=this;
		$("#timeFlag").change(function(){
			//清除选中
		 	self.timeWidgetStart.setValue("");
		 	self.timeWidgetEnd.setValue("");
		});
		$("#timeWidgetStart").change(function(){
			//清除选中
		 	$("#timeFlag").val("-1");
		});
		$("#timeWidgetEnd").change(function(){
		 	//清除选中
		 	$("#timeFlag").val("-1");
		});
		
		$("#queryBtn").unbind("click").click(function(){
			self._search();
		});
		
		$("#exportBtn").unbind("click").click(function(){
			self._export();
		});
	},
	_listCustomerOrderCount:function(params){
		console.log(params);
		CommonUtils.async({
			url:"/DispatcherWeb/carrier/stats/listConsignorRanking.json",
			data:params,
			success:function(result){
				var res = result.data;
				var option = {};
				var amounts = {};
				var totals = {};
				var xData = [];
				for(var i=0; i<res.length; i++){
					var d = res[i];
					amounts[d.consignorName] = d.amount;
					totals[d.consignorName] =  d.total;
					xData.push(d.consignorName);
				}
				
				var yData = [{name:'累计订单', type:'bar', data: totals},
				             {name:'新增订单', type:'bar', data: amounts}];
				option.xData = xData;
				option.yData = yData;
				option.yInterval = 1;
				$("#customerCountChart").ColumnChartWidget(option);
				
				
			},
			error:function(result){
			}
		});
	},
	_search : function(){
		var self = this;
		var startTime="";
		var endTime="";
		var timeFlag = $("#timeFlag").val();
		if(timeFlag > -1){
			var timeScope=CommonUtils.Date.getTimeObject(timeFlag);
			if(timeScope&&timeScope!=""){
				startTime=timeScope.startTime;
				endTime=timeScope.endTime
			}
		}
		var _startTime=self.timeWidgetStart.getValue();
		var _endTime=self.timeWidgetEnd.getValue();
		if(_startTime&&_startTime!=""){
			if(!_endTime||_endTime==""){
				MessageUtil.alert("请选择结束时间！");
				return ;
			}
			if(CommonUtils.Date.string2Date(_startTime)>CommonUtils.Date.string2Date(_endTime)){
				MessageUtil.alert("开始时间不应当超过结束时间！");
				return ;
			}
			startTime=_startTime;
			endTime=_endTime;
		}
		
		var queryDeptId = self.deptWidget.getValue();
		var params={
			startTime:startTime,
			endTime:endTime,
			queryDeptId:queryDeptId
		}
		
		self.tableWidget.query(params);
		params.maxNum = 10;
		self._listCustomerOrderCount(params);
	},
	_export:function(){
		var self = this;
		var startTime="";
		var endTime="";
		var timeFlag = $("#timeFlag").val();
		if(timeFlag > -1){
			var timeScope=CommonUtils.Date.getTimeObject(timeFlag);
			if(timeScope&&timeScope!=""){
				startTime=timeScope.startTime;
				endTime=timeScope.endTime
			}
		}
		var _startTime=self.timeWidgetStart.getValue();
		var _endTime=self.timeWidgetEnd.getValue();
		if(_startTime&&_startTime!=""){
			if(!_endTime||_endTime==""){
				MessageUtil.alert("请选择结束时间！");
				return ;
			}
			if(CommonUtils.Date.string2Date(_startTime)>CommonUtils.Date.string2Date(_endTime)){
				MessageUtil.alert("开始时间不应当超过结束时间！");
				return ;
			}
			startTime=_startTime;
			endTime=_endTime;
		}
		var queryDeptId = self.deptWidget.getValue();
		var params={
			startTime:startTime,
			endTime:endTime,
			queryDeptId:queryDeptId
		}
		//导出订单
		CommonUtils.async({
			url:"/DispatcherWeb/carrier/stats/exportOrderStatus.json",
			data:params,
			success:function(result){
				if(result.code==0){
					console.log(result);
					MessageUtil.info("成功",function(){
						if(result.data.url){
							window.open(result.data.url);
						}
					});
				}else{
					MessageUtil.alert("导出订单失败");
				}
			},
			error:function(result){
				MessageUtil.alert("导出订单失败");
			}
		});
	},
	_setTotalCustomerNum:function(){
		CommonUtils.async({
			url:"/DispatcherWeb/carrier/stats/getCustomerCount.json",
			data:{},
			success:function(result){
				if(result.code != 0)
					return;
				$("#totalNum").text(result.data.totalNum);
				$("#incNum").text(result.data.incNum);
			},
			error:function(result){
			}
		});
	}
}
$(function(){
	CusomerStats.init();
});