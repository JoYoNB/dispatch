var AddDriver={
	deptWidget:null,
	vehicleWidget:null,
	carryTypeWidget:null,
	
	init:function(){
		var self=this;
		
		self._render();
		self._bind();
	},
	_render:function(){
		var self=this;
		//设置底部菜单连接
		Footer.nav("车辆管理","carrier/driverManagement/driverList.html");
		self.vehicleTypeWidget=$("#vehicleTypeWidget").ComboBoxWidget({
			url:"/DispatcherWeb/common/getVehicleTypeList.json",
			textField:"name",
			valueField:"id",
			showText:"请选择车型",
		    width: 250
		});
		
		//初始化载货类型控件
		self.carryTypeWidget=$("#carryTypeWidget").CheckBoxWidget({
			url:"/DispatcherWeb/common/getCarryTypeList.json"
		});
		
	},
	_bind:function(){
		var self=this;
		
		$("#saveBtn").unbind("click").click(function(){
			self._saveVehicle();
		});
	},
	_saveVehicle:function(){
		var self=this;
		
		var plateNo=$("#plateNo").val();
		if(!plateNo||plateNo==""){
			MessageUtil.alert("车牌号不能为空");
			return;
		}
		var equipmentPn=$("#equipmentPn").val();
		if(!equipmentPn||equipmentPn==""){
			MessageUtil.alert("设备PN不能为空");
			return;
		}
		var vehicleTypeId=self.vehicleTypeWidget.getValue();
		if(!vehicleTypeId||vehicleTypeId==""){
			MessageUtil.alert("车辆类型不能为空");
			return;
		}
		var carryTypeIds=self.carryTypeWidget.getSelectedValues();
		if(!carryTypeIds||carryTypeIds==""){
			MessageUtil.alert("载货属性不能为空");
			return;
		}
		
		var vehicleWeightMax=$("#vehicleWeightMax").val();
		if(!vehicleWeightMax||vehicleWeightMax==""){
			MessageUtil.alert("最大允许总质量不能为空");
			return;
		}else if(!CommonUtils.Validate.number(vehicleWeightMax)){
			MessageUtil.alert("最大允许总质量格式不对，需为数字");
			return;
		}
		
		var vehicleLength=$("#vehicleLength").val();
		if(!vehicleLength||vehicleLength==""){
			MessageUtil.alert("整车总长不能为空");
			return;
		}else if(!CommonUtils.Validate.number(vehicleLength)){
			MessageUtil.alert("整车总长格式不对，需为数字");
			return;
		}
		
		var size=$("#size").val();
		if(!size||size==""){
			MessageUtil.alert("货箱内部尺寸不能为空");
			return;
		}else if(!CommonUtils.Validate.size(size)){
			MessageUtil.alert("货箱内部尺寸格式不正确,正确格式为:长/宽/高");
			return;
		}else{
			var sizeArray= new Array();
			sizeArray=size.split("/");
			var vehicleInsideLength=sizeArray[0];
			var vehicleInsideWidth=sizeArray[1];
			var vehicleInsideHeight=sizeArray[2];
		}
		var carryWeigthMax=$("#carryWeigthMax").val();
		if(!carryWeigthMax||carryWeigthMax==""){
			MessageUtil.alert("载质量不能为空");
			return;
		}else if(!CommonUtils.Validate.number(carryWeigthMax)){
			MessageUtil.alert("载质量格式不对，需为数字");
			return;
		}
		
		var weigthUseFactor=$("#weigthUseFactor").val();
		var swerveRadiusMin=$("#swerveRadiusMin").val();
		var powerRate=$("#powerRate").val();
		
		var vehicle={
			plateNo:plateNo,
			equipmentPn:equipmentPn,
			vehicleTypeId:vehicleTypeId,
			carryTypeIds:carryTypeIds.join(","),
			vehicleWeightMax:vehicleWeightMax,
			vehicleLength:vehicleLength,
			vehicleInsideLength:vehicleInsideLength,
			vehicleInsideWidth:vehicleInsideWidth,
			vehicleInsideHeight:vehicleInsideHeight,
			carryWeigthMax:carryWeigthMax,
			weigthUseFactor:weigthUseFactor,
			swerveRadiusMin:swerveRadiusMin,
			powerRate:powerRate
		}
		
		//JSON.stringify(data)
		/*alert(JSON.stringify(driver));*/
		CommonUtils.async({
			url:"/DispatcherWeb/vehicle/addVehicle.json",
			//contentType:"json",
			data:vehicle,
			success:function(result){
				if(result.code==0){
					MessageUtil.info("成功",function(){
						location.href="/"+Constant.PROJECT_NAME+"/carrier/vehicleManagement/vehicleList.html";
					});
				}else if(result.code==130006){
					MessageUtil.alert("车辆已被其他司机关联,请重新选择关联车辆");
				}else if(result.code==130002){
					MessageUtil.alert("其他平台新增车辆失败");
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