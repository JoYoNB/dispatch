var ChargeRuleUpdate={
	id:null,
	provinceWidget:null,
	cityWidget:null,
	vehicleTypeWidget:null,
	init:function(){
		var self=this;
		self._render();
		self.id=window.chargeRuleUpdate.param;
		self._bind();
		self._loadData();
	},
	_render:function(){
		var self=this;
		self.provinceWidget=$("#provinceWidget").ComboBoxWidget({
			url:"/DispatcherWeb/common/getAreaByParentId.json",
			textField:"name",
			valueField:"id",
			showText:"请选择省份",
		    width: 200,
			onSelected:function(value,text){
				self.cityWidget.loadUrl("/DispatcherWeb/common/getAreaByParentId.json?parent="+value);
			}
		});
		self.cityWidget=$("#cityWidget").ComboBoxWidget({
			textField:"name",
			valueField:"id",
			showText:"请选择城市",
			width:200
		});
		self.vehicleTypeWidget=$("#vehicleTypeWidget").ComboBoxWidget({
			url:"/DispatcherWeb/common/getVehicleTypeList.json",
			textField:"name",
			valueField:"id",
			showText:"请选择车型",
		    width: 423
		});
	},
	_bind:function(){
		self=this;
		//返回按钮
		$("#uniq_cancelId").unbind("click").click(function(){
			//获取当前窗口的实体
			window.chargeRuleUpdate.close();
		});
		//保存按钮
		$("#saveBtn").unbind("click").click(function(){
			self._save();
		});
	},
	_loadData:function(){
		var self=this;
		
		CommonUtils.async({
			url:"/DispatcherWeb/chargeRule/info.json",
			data:{id:self.id},
			success:function(result){
				if(result.code==0){
					var chargeRule=result.data;
					$("#startingPrice").val(chargeRule.startingPrice);
					$("#startingMileage").val(chargeRule.startingMileage);
					$("#price").val(chargeRule.price);
					setTimeout(function(){
						self.vehicleTypeWidget.setValue(chargeRule.vehicleTypeId);
						self.provinceWidget.setValue(chargeRule.provinceId);
						self.cityWidget.loadUrl("/DispatcherWeb/common/getAreaByParentId.json?parent="+chargeRule.provinceId,chargeRule.cityId);
					},500);
				}
			}
		});
	},
	_save:function(){
		var self=this;
		var cityId=self.cityWidget.getValue();
		if(!cityId||cityId==""){
			MessageUtil.alert("请先选择地区");
			return;
		}
		
		var vehicleTypeId=self.vehicleTypeWidget.getValue();
		if(!vehicleTypeId||vehicleTypeId==""){
			MessageUtil.alert("请先选择车型");
			return;
		}
		
		var startingMileage=$("#startingMileage").val();
		if(!startingMileage||startingMileage==""){
			MessageUtil.alert("起步里程不能为空");
			return;
		}else if(!CommonUtils.Validate.decimals(startingMileage,9999999,2)){
			MessageUtil.alert("里程范围应为1-9999999之间的数值，精确两位小数");
			return;
		}
		
		var startingPrice=$("#startingPrice").val();
		if(!startingPrice||startingPrice==""){
			MessageUtil.alert("起步价格不能为空");
			return;
		}else if(!CommonUtils.Validate.decimals(startingPrice,9999999,2)){
			MessageUtil.alert("起步价格应为1-9999999之间的数值，精确两位小数");
			return;
		}
		
		var price=$("#price").val();
		if(!price||price==""){
			MessageUtil.alert("价格不能为空");
			return;
		}else if(!CommonUtils.Validate.decimals(price,9999999,2)){
			MessageUtil.alert("价格应为1-9999999之间的数值，精确两位小数");
			return;
		}
		var chargeRule={
			id:self.id,
			cityId:cityId,
			vehicleTypeId:vehicleTypeId,
			startingMileage:startingMileage,
			startingPrice:startingPrice,
			price:price
		}
		
		CommonUtils.async({
			url:"/DispatcherWeb/chargeRule/update.json",
			data:chargeRule,
			success:function(result){
				if(result.code==0){
					MessageUtil.info("成功",function(){
						location.href="/"+Constant.PROJECT_NAME+"/settlement/chargeRuleManagement/chargeRUleList.html";
					});
				}else if(result.code==140000){
					MessageUtil.alert("同城市同车型的规则已存在");
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
	ChargeRuleUpdate.init();
});