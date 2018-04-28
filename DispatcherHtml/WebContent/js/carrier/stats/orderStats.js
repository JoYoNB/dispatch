var OrderStats={
	tableWidget:null,//表格控件
	deptWidget:null,//部门选择控件
	timeWidgetStart:null,//筛选条件开始时间
	timeWidgetEnd:null,//筛选条件结束时间
	
	init:function(){
		var self=this;
		self._render();
		self._bind();
		self._setTotalOrderNum();
		
		var timeFlag = $("#timeFlag").val();
		var params = CommonUtils.Date.getTimeObject(timeFlag);
		self._listDeptDayOrderDist(params);
		self._search();
	},
	_render:function(){
		var self=this;
		//设置底部菜单连接
		Footer.nav("订单统计","carrier/stats/orderStats.html");
		
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
		
		/*var timeFlag = $("#timeFlag").val();
		var params = CommonUtils.Date.getTimeObject(timeFlag);
		
		self.tableWigdetRender(params);*/
	},
	tableWigdetRender:function(params){
		var self = this;
		var params = params || {};
		CommonUtils.async({
			url:"/DispatcherWeb/carrier/stats/listSubDepts.json",
			data:params,
			success:function(result){
				if(result.code != 0)
					return;
				var data = result.data;
				var cells = [];
				cells.push({field:"day", text:"日期"});
				for(var i=0; i<data.length; i++){
					cells.push({field:data[i].deptId, text:data[i].deptName});
				}
				cells.push({field:"sum", text:"总计"});
				console.log(cells);
				self.tableWidget= $("#tableWidget").TableWidget({
					url:"/DispatcherWeb/carrier/stats/listDeptDayOrderDist2.json",
					para:params,
					cells:cells,
					showPagination:false,
					showSelect:false
				});
			},
			error:function(result){
			}
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
	_listDeptDayOrderDist:function(params){
		var self = this;
		var params = params || {};
		CommonUtils.async({
			url:"/DispatcherWeb/carrier/stats/listDeptDayOrderDist.json",
			data:params,
			success:function(result){
				if(result.code != 0)
					return;
				var res = result.data.totalDist;
				var option = {};
				var data = {};
				for(var i=0; i<res.length; i++){
					data[res[i].day] = res[i].amount;
				}
				var xData = CommonUtils.Date.getDaysArr(params.startTime, params.endTime, 'MM-dd');
				var yData = [{name:'总量', type:'column', data: data}];
				var res = result.data.deptDist;
				
				var depts = result.data.subDepts;
				var deptDist = result.data.deptDist;
				for(var i=0; i<depts.length; i++){
					data = {};
					var deptName = depts[i].deptName;
					for(var j=0; j<deptDist.length; j++){
						if(deptDist[j].deptName == deptName){
							data[deptDist[j].day] = deptDist[j].amount;
						}
					}
					 yData.push({name:deptName, type:'spline', data: data});
				}
				option.xData = xData;
				option.yData = yData;
				option.yInterval = 1;
				option.width = 1800;
				$("#deptDayOrderChart").ColumnChartWidget(option);
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
		
		self.tableWigdetRender(params);
		setTimeout(function(){
			var daysArr = CommonUtils.Date.getDaysArr(params.startTime, params.endTime, 'MM-dd').join(",");
			params.daysArr = daysArr;
			self.tableWidget.query(params);
		},1000);
		self._listDeptDayOrderDist(params);
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
			//url:"/DispatcherWeb/consignor/order/export.json",
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
	_setTotalOrderNum:function(){
		CommonUtils.async({
			url:"/DispatcherWeb/carrier/stats/getFinishedOrderCount.json",
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
	OrderStats.init();
});