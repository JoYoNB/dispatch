var TransportDataList={
	tableWidget:null,//表格控件
	startTimeWidget:null,
	endTimeWidget:null,
	init:function(){
		var self=this;
		self._render();
		self._bind();
	},
	_render:function(){
		var self=this;
		
		self.tableWidget=$("#tableWidget").TableWidget({
			url:"/DispatcherWeb/transportData/getTransportDataList.json",
			showSelect:false,
			para:{},
			cells:[{
				field:"name",
				text:"客户名称"
			},{
				field:"vehicleTotle",
				text:"车辆数"
			},{
				field:"orderTotle",
				text:"累计订单"
			},{
				field:"orderGrowth",
				text:"新增订单"
			},{
				field:"createTime",
				text:"加入时间"
			}]
			//pageSize:10//不填写默认是20
		});
		//时间控件
		self.startTimeWidget=$("#startTimeWidget").TimeWidget({
			showText:"开始日期",
			width:228,
			format:"yyyy-mm-dd",
		    	onSelected:function(value){
	    			$("#timeSelect").val("-1");
		    	}
		});
		self.endTimeWidget=$("#endTimeWidget").TimeWidget({
			showText:"结束日期",
			format:"yyyy-mm-dd",
			width:228,
		    	onSelected:function(value){
		    		$("#timeSelect").val("-1");
		    	}
		});
		
	},
	_bind:function(){
		self=this;
		
		//导出按钮
		$("#exp").unbind("click").click(function(){
			//导出动作
			self._exp();
		});
		
		//时间快捷选择
		$("#timeSelect").change(function(){
			var selectedValue = $(this).children('option:selected').val();
			if(selectedValue==-1){//		-1请选择
				self.startTimeWidget.setValue(null);
				self.endTimeWidget.setValue(null);
			}else if(selectedValue==0){//		0今天
				var today=CommonUtils.Date.formatDate(new Date());
				self.startTimeWidget.setValue(today);
				self.endTimeWidget.setValue(today);
			}else if(selectedValue==1){//		1昨天
				var yesterday=CommonUtils.Date.formatDate(CommonUtils.Date.add(new Date(),"day",-1));
				self.startTimeWidget.setValue(yesterday);
				self.endTimeWidget.setValue(yesterday);
			}else if(selectedValue==2){//		2本周
				self.startTimeWidget.setValue(CommonUtils.Date.formatDate(CommonUtils.Date.string2Date(CommonUtils.Date.getWeekStartDate())));
				self.endTimeWidget.setValue(CommonUtils.Date.formatDate(CommonUtils.Date.string2Date(CommonUtils.Date.getWeekEndDate())));
			}else if(selectedValue==3){//		3上周
				self.startTimeWidget.setValue(CommonUtils.Date.formatDate(CommonUtils.Date.string2Date(CommonUtils.Date.getLastWeekStartDate())));
				self.endTimeWidget.setValue(CommonUtils.Date.formatDate(CommonUtils.Date.string2Date(CommonUtils.Date.getLastWeekEndDate())));
			}else if(selectedValue==4){//		4本月
				self.startTimeWidget.setValue(CommonUtils.Date.formatDate(CommonUtils.Date.string2Date(CommonUtils.Date.getMonthStartDate())));
				self.endTimeWidget.setValue(CommonUtils.Date.formatDate(CommonUtils.Date.string2Date(CommonUtils.Date.getMonthEndDate())));
			}else if(selectedValue==5){//		5上月
				self.startTimeWidget.setValue(CommonUtils.Date.formatDate(CommonUtils.Date.string2Date(CommonUtils.Date.getLastMonthStartDate())));
				self.endTimeWidget.setValue(CommonUtils.Date.formatDate(CommonUtils.Date.string2Date(CommonUtils.Date.getLastMonthEndDate())));
			}
		});
		
		//查询按钮
		$("#queryBtn").unbind("click").click(function(){
			self=this;
			var startTime=self.startTimeWidget.getValue();
			if(startTime&&startTime!=""){
				startTime+=" 00:00:00";
			}
			var endTime=self.endTimeWidget.getValue();
			if(endTime&&endTime!=""){
				endTime+=" 23:59:59";
			}
			var name=$("#name").val();
			var param={
				name:name,
				startTime:startTime,
				endTime:endTime
			}
			//重新给条件查询
			self.tableWidget.query(param);
		});
	},
	_exp:function(){
		self=this;
		var startTime=self.startTimeWidget.getValue();
		if(startTime&&startTime!=""){
			startTime+=" 00:00:00";
		}
		var endTime=self.endTimeWidget.getValue();
		if(endTime&&endTime!=""){
			endTime+=" 23:59:59";
		}
		var name=$("#name").val();
		var param={
			name:name,
			startTime:startTime,
			endTime:endTime
		}
		CommonUtils.async({
			url:"/DispatcherWeb/transportData/exp.json",
			data:param,
			success:function(result){
				if(result.code==0){
					console.log(result);
					MessageUtil.info("成功",function(){
						if(result.data.url){
							window.open(result.data.url);
						}
					});
				}else{
					MessageUtil.alert("失败");
				}
			},
			error:function(result){
				MessageUtil.alert("失败");
			}
		});
	}
}
$(function(){
	TransportDataList.init();
});