var SettleStats={
	tableWidget:null,//表格控件
	deptWidget:null,//部门选择控件
	timeWidgetStart:null,//筛选条件开始时间
	timeWidgetEnd:null,//筛选条件结束时间
	
	init:function(){
		var self=this;
		self._render();
		self._bind();
		self._search();
	},
	_render:function(){
		var self=this;
		//设置底部菜单连接
		Footer.nav("结算统计","carrier/stats/settleStats.html");
		
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
			url:"/DispatcherWeb/carrier/stats/settleStats.json",
			cells:[{
				field:"month",
				text:"月份"
			},{
				field:"order_num",
				text:"客户量"
			},{
				field:"cust_num",
				text:"订单量"
			},{
				field:"receivable",
				text:"应收结算费用"
			},{
				field:"receipt",
				text:"实收结算费用"
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
			url:"/DispatcherWeb/carrier/stats/exportSettleStatus.json",
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
	}
}
$(function(){
	SettleStats.init();
});