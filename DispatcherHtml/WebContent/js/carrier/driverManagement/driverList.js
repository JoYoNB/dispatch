var DriverList={
	tableWidget:null,//表格控件
	deptWidget:null,//部门选择控件
	timeWidgetStart:null,//筛选条件开始时间
	timeWidgetEnd:null,//筛选条件结束时间
	
	init:function(){
		var self=this;
		self._render();
		self._bind();
	},
	_render:function(){
		var self=this;
		//设置底部菜单连接
		Footer.nav("司机管理","carrier/driverManagement/driverList.html");
		
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
		
		self.tableWidget=$("#tableWidget").TableWidget({
			url:"/DispatcherWeb/driver/getDriverList.json",
			para:{},
			extParam:"userDeptLevel",//额外的参数，除了total，list以外的参数
			cells:[{
				field:"driverName",
				text:"司机名称"
			},{
				field:"plateNo",
				text:"车牌号"
			},{
				field:"deptName",
				text:"部门"
			},{
				field:"phoneNo",
				text:"手机号"
			},{
				field:"orderCount",
				text:"累计订单"
			},{
				field:"addOrderCount",
				text:"新增订单"
			},{
				field:"contacter",
				text:"货物完整率"
			},{
				field:"contacter",
				text:"准点率"
			},{
				field:"contacter",
				text:"驾驶评分(5分)"
			},{
				field:"entryTime",
				text:"入职时间"
			},{
				field:"op",
				text:"操作",
				render:function(value,row){
					var _url="/"+Constant.PROJECT_NAME+"/carrier/driverManagement/editDriver.html?id="+row.driverId;
					var _html='<a href="'+_url+'">编辑</a><a href="javascript:DriverList.deleteDriver('+row.driverId+');">删除</a>';
					return _html;
				}
			}]
			//pageSize:10//不填写默认是20
		});
		
	},
	_bind:function(){
		self=this;
		//删除按钮
		$("#batchDelete").unbind("click").click(function(){
			//MessageUtil.alert("你好");
			MessageUtil.confirm("是否要删除?",function(){
				//删除动作
				self._deleteDriverList();
				
			});
		});
		
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
			var driverName=$("#driverName").val();
			var plateNo=$("#plateNo").val();
			var phone=$("#phone").val();
			var equipmentPn=$("#equipmentPn").val();
			var timeFlag=$("#timeFlag").val();
			var startTime="";
			var endTime="";
			var timeScope=CommonUtils.Date.getTimeObject(timeFlag);
			if(timeScope&&timeScope!=""){
				startTime=timeScope.startTime;
				endTime=timeScope.endTime
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
			
			var param={
				driverName:driverName,
				plateNo:plateNo,
				phoneNo:phone,
				equipmentPn:equipmentPn,
				startTime:startTime,
				endTime:endTime
			}
			var deptId=self.deptWidget.getValue();
			var startTime=self.timeWidgetStart.getValue();
			var endTime=self.timeWidgetEnd.getValue();
			if(deptId&&deptId!=""){
				param.deptId=deptId;
			}
			//重新给条件查询
			self.tableWidget.query(param);
		});
	},
	_deleteDriverList:function(){
		var self=this;
		var ids=self.tableWidget.getSelectedRowIds();
		if(!ids||ids.length==0){
			MessageUtil.alert("请先选择行");
			return;
		}
		
		CommonUtils.async({
			url:"/DispatcherWeb/driver/deleteDriverList.json",
			data:{id:ids},
			success:function(result){
				if(result.code==0){
					MessageUtil.info("成功",function(){
						//删除后重新加载
						self.tableWidget.reload();
					});
				}else if(result.code=130007){
					MessageUtil.alert("该司机有关联车辆,删除需先解绑");
				}else{
					MessageUtil.alert("失败");
				}
			},
			error:function(result){
				MessageUtil.alert("失败");
			}
		});
	},
	deleteDriver:function(id){
		var self=this;
		
		MessageUtil.confirm("是否要删除?",function(){
			//删除动作
			CommonUtils.async({
				url:"/DispatcherWeb/driver/deleteDriver.json",
				data:{driverId:id},
				success:function(result){
					if(result.code==0){
						MessageUtil.info("成功",function(){
							//删除后重新加载
							self.tableWidget.reload();
						});
					}else if(result.code=130007){
						MessageUtil.alert("该司机有关联车辆,删除需先解绑");
					}else{
						MessageUtil.alert("失败");
					}
				},
				error:function(result){
					MessageUtil.alert("失败");
				}
			});
			
		});
		
	}
}
$(function(){
	DriverList.init();
});