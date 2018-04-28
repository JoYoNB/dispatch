var Editdriver={
	id:null,
	
	init:function(){
		var self=this;
		self.id=CommonUtils.getParam("id");
		self._render();
		self._getDriver();
		self._bind();
	},
	_render:function(){
		var self=this;
		var token=CommonUtils.Cookie.get(Constant.PROJECT_NAME+"_token");
		//设置底部菜单连接
		Footer.nav("司机管理","carrier/driverManagement/driverList.html");
		self.deptWidget=$("#deptWidget").DeptWidget({});
		self.vehicleWidget=$("#vehicleWidget").ComboBoxWidget({
			url:"/DispatcherWeb/vehicle/getCommonVehicles.json?token="+token,
			textField:"plateNo",
			valueField:"vehicleId",
			showText:"请选择车辆",
		    width: 250
		});
		self.timeWidget=$("#timeWidget").TimeWidget({
			format:"yyyy-mm-dd",
			width:"250px"
		});
	},
	_bind:function(){
		var self=this;
		$("#saveBtn").unbind("click").click(function(){
			self._saveDriver();
		});
		
	},
	_getDriver:function(){
		var self=this;
		CommonUtils.async({
			url:"/DispatcherWeb/driver/getDriverList.json",
			data:{driverId:self.id},
			success:function(result){
				if(result.code==0){
					var driver=result.data.list[0];
					$("#driverName").val(driver.driverName);
					$("#phoneNo").val(driver.phoneNo);
					
					var deptName=driver.deptName;
					self.deptWidget.setText(deptName);
					
					var vehicleId=driver.vehicleId;
					self.vehicleWidget.setValue(vehicleId);
					
					var entryTime=CommonUtils.Date.timestampToDate(driver.entryTime);
					
					self.timeWidget.setValue(entryTime);
					
				}
			}
		});
		
	},
	_saveDriver:function(){
		var self=this;
		
		var driver={driverId:self.id};
		
		var driverName=$("#driverName").val();
		if(driverName&&driverName!=""){
			driver.driverName=driverName;
		}
		var deptId=self.deptWidget.getValue();
		if(deptId&&deptId!=""){
			driver.deptId=deptId;
		}
		var vehicleId=self.vehicleWidget.getValue();
		if(vehicleId&&vehicleId!=''){
			driver.vehicleId=vehicleId;
		}
		var phoneNo=$("#phoneNo").val();
		if(phoneNo&&phoneNo!=""){
			if(!CommonUtils.Validate.phone(phoneNo)){
				MessageUtil.alert("手机号格式不正确");
				return;
			}
			driver.phoneNo=phoneNo;
		}
		var entryTime=self.timeWidget.getValue();
		if(entryTime&&entryTime!=""){
			if(!CommonUtils.Validate.dateValidate(entryTime)){
				MessageUtil.alert("时间格式不正确");
				return;
			}
			driver.entryTime=entryTime+" 00:00:00";
		}
		
		CommonUtils.async({
			url:"/DispatcherWeb/driver/updateDriver.json",
			data:driver,
			success:function(result){
				if(result.code==0){
					MessageUtil.info("成功",function(){
						//返回列表
						location.href="/"+Constant.PROJECT_NAME+"/carrier/driverManagement/driverList.html";
					});
				}else if(result.code==130006){
					MessageUtil.alert("车辆已被其他司机关联,请重新选择关联车辆");
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
	Editdriver.init();
});