var AddDriver={
	deptWidget:null,
	vehicleWidget:null,
	
	init:function(){
		var self=this;
		
		self._render();
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
	_saveDriver:function(){
		var self=this;
		
		var driverName=$("#driverName").val();
		if(!driverName||driverName==""){
			MessageUtil.alert("司机姓名不能为空");
			return;
		}
		var deptId=self.deptWidget.getValue();
		if(!deptId||deptId==""){
			MessageUtil.alert("部门不能为空");
			return;
		}
		var vehicleId=self.vehicleWidget.getValue();
		if(!vehicleId||vehicleId==""){
			MessageUtil.alert("请先选择车辆");
			return;
		}
		var phoneNo=$("#phoneNo").val();
		if(!phoneNo||phoneNo==""){
			MessageUtil.alert("手机号不能为空");
			return;
		}else if(!CommonUtils.Validate.phone(phoneNo)){
			MessageUtil.alert("手机号格式不正确");
			return;
		}
		
		var entryTime=self.timeWidget.getValue();
		if(!entryTime||entryTime==""){
			MessageUtil.alert("入职时间不能为空");
			return;
		}else if(!CommonUtils.Validate.dateValidate(entryTime)){
			MessageUtil.alert("时间格式不正确");
			return;
		}
		
		var driver={
			driverName:driverName,
			deptId:deptId,
			vehicleId:vehicleId,
			phoneNo:phoneNo,
			entryTime:entryTime+" 00:00:00"
		}
		
		//JSON.stringify(data)
		/*alert(JSON.stringify(driver));*/
		CommonUtils.async({
			url:"/DispatcherWeb/driver/addDriver.json",
			//contentType:"json",
			data:driver,
			success:function(result){
				if(result.code==0){
					MessageUtil.info("成功",function(){
						location.href="/"+Constant.PROJECT_NAME+"/carrier/driverManagement/driverList.html";
					});
				}else if(result.code==130006){
					//没有改部门权限
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
	AddDriver.init();
});