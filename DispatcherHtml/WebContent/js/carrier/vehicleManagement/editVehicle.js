var EditVehicle={
	id:null,
	vehicleWidget:null,
	carryTypeWidget:null,
	
	init:function(){
		var self=this;
		self.id=CommonUtils.getParam("id");
		self._render();
		self._getVehicle();
		self._bind();
	},
	_render:function(){
		var self=this;
		var token=CommonUtils.Cookie.get(Constant.PROJECT_NAME+"_token");
		//设置底部菜单连接
		Footer.nav("司机管理","carrier/driverManagement/driverList.html");
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
			self._saveDriver();
		});
		
	},
	_getVehicle:function(){
		var self=this;
		CommonUtils.async({
			url:"/DispatcherWeb/vehicle/getVehicleList.json",
			data:{vehicleId:self.id},
			success:function(result){
				if(result.code==0){
					var vehicle=result.data.list[0];
					$("#plateNo").val(vehicle.plateNo);
					$("#equipmentPn").val(vehicle.equipmentPn);
					var vehicleTypeId=vehicle.vehicleTypeId;
					self.vehicleTypeWidget.setValue(vehicleTypeId);
					var carryTypes=vehicle.carryTypes;
					var carryTypeIds=[];
					carryTypes.forEach(function(value,index){
						carryTypeIds.push(value.carryTypeId);
					});
					self.carryTypeWidget.setSelectedValues(carryTypeIds)
					$("#vehicleWeightMax").val(vehicle.vehicleWeightMax);
					$("#vehicleLength").val(vehicle.vehicleLength);
					$("#size").val(vehicle.vehicleInsideLength+"/"+vehicle.vehicleInsideWidth+"/"+vehicle.vehicleInsideHeight);
					$("#carryWeigthMax").val(vehicle.carryWeigthMax);
					$("#weigthUseFactor").val(vehicle.weigthUseFactor);
					$("#swerveRadiusMin").val(vehicle.swerveRadiusMin);
					$("#powerRate").val(vehicle.powerRate);
				}
			}
		});
		
	},
	_saveDriver:function(){
		var self=this;
		
		var vehicle={vehicleId:self.id};
		
		var plateNo=$("#plateNo").val();
		if(plateNo&&plateNo!=""){
			vehicle.plateNo=plateNo;
		}
		var equipmentPn=$("#equipmentPn").val();
		if(equipmentPn&&equipmentPn!=""){
			vehicle.equipmentPn=equipmentPn;
		}
		var vehicleTypeId=self.vehicleTypeWidget.getValue();
		if(vehicleTypeId&&vehicleTypeId!=''){
			vehicle.vehicleTypeId=vehicleTypeId;
		}
		
		var carryTypeIds=self.carryTypeWidget.getSelectedValues();
		if(carryTypeIds&&carryTypeIds!=''){
			vehicle.carryTypeIds=carryTypeIds.join(",");
		}
		var vehicleWeightMax=$("#vehicleWeightMax").val();
		if(vehicleWeightMax&&vehicleWeightMax!=""){
			if(!CommonUtils.Validate.number(vehicleWeightMax)){
				MessageUtil.alert("最大允许总质量格式不对，需为数字");
				return;
			}
			vehicle.vehicleWeightMax=vehicleWeightMax;
		}
		var vehicleLength=$("#vehicleLength").val();
		if(vehicleLength&&vehicleLength!=""){
			if(!CommonUtils.Validate.number(vehicleWeightMax)){
				MessageUtil.alert("整车总长格式不对，需为数字");
				return;
			}
			vehicle.vehicleLength=vehicleLength;
		}
		var size=$("#size").val();
		if(size&&size!=""){
			if(!CommonUtils.Validate.size(size)){
				MessageUtil.alert("货箱内部尺寸格式不正确,正确格式为:长/宽/高");
				return;
			}
			var sizeArray= new Array();
			sizeArray=size.split("/");
			var vehicleInsideLength=sizeArray[0];
			var vehicleInsideWidth=sizeArray[1];
			var vehicleInsideHeight=sizeArray[2];
			vehicle.vehicleInsideLength=vehicleInsideLength;
			vehicle.vehicleInsideWidth=vehicleInsideWidth;
			vehicle.vehicleInsideHeight=vehicleInsideHeight;
		}
		
		var carryWeigthMax=$("#carryWeigthMax").val();
		if(carryWeigthMax&&carryWeigthMax!=""){
			if(!CommonUtils.Validate.number(carryWeigthMax)){
				MessageUtil.alert("载质量格式不对，需为数字");
				return;
			}
			vehicle.carryWeigthMax=carryWeigthMax;
		}
		var weigthUseFactor=$("#weigthUseFactor").val();
		if(weigthUseFactor&&weigthUseFactor!=""){
			vehicle.weigthUseFactor=weigthUseFactor;
		}
		var swerveRadiusMin=$("#swerveRadiusMin").val();
		if(swerveRadiusMin&&swerveRadiusMin!=""){
			vehicle.swerveRadiusMin=swerveRadiusMin;
		}
		var powerRate=$("#powerRate").val();
		if(powerRate&&powerRate!=""){
			vehicle.powerRate=powerRate;
		}
		
		CommonUtils.async({
			url:"/DispatcherWeb/vehicle/updateVehicle.json",
			data:vehicle,
			success:function(result){
				if(result.code==0){
					MessageUtil.info("成功",function(){
						//返回列表
						location.href="/"+Constant.PROJECT_NAME+"/carrier/vehicleManagement/vehicleList.html";
					});
				}else if(result.code==130001){
					MessageUtil.alert("车牌号重复");
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
	EditVehicle.init();
});