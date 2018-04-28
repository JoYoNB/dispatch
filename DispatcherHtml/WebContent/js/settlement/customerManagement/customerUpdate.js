var CustomerUpdate={
	gmtZoneWidget:null,
	fileWidget:null,//文件上传控件
	logoWidget:null,//上传logo
	goodsTypeWidget:null,
	id:null,
	userId:null,
	areaIndex:0,
	areaItems:{},
	init:function(){
		var self=this;
		self.id=CommonUtils.getParam("id");
		self._render();
		self._bind();
		self._loadData();
	},
	_render:function(){
		var self=this;
		
		//设置底部菜单连接
		Footer.nav("客户管理","settlement/customerManagement/customerList.html");
		
		//初始化时区控件
		self.gmtZoneWidget=$("#gmtZoneWidget").ComboBoxWidget({
			url:"/js/ui/timeZone_zh.json",
			type:"GET",
			textField:"text",
			valueField:"data",
			showText:"请选择时区",
			width:676
		});
		self.gmtZoneWidget.setValue("utc+0800000");//默认选中东八区
		
		//初始化logo上传控件
		self.logoWidget=$("#logoWidget").FileWidget({
			isBatch:false,
			description:"为保证显示效果，请上传尺寸100*100的图片，格式为png、jpg"
		});
		
		//初始化文件上传控件
		self.fileWidget=$("#fileWidget").FileWidget({
			isBatch:false,
			description:"请上传大小不超过3M的图片，格式为png、jpg"
		});
		
		//初始化货物类型控件
		self.goodsTypeWidget=$("#goodsTypeWidget").CheckBoxWidget({
			url:"/DispatcherWeb/common/getGoodsTypeList.json"
		});
		
		//初始化区域选择控件
		var provinceWidget=$("#provinceWidget_0").ComboBoxWidget({
			url:"/DispatcherWeb/common/getAreaByParentId.json",
			textField:"name",
			valueField:"id",
			showText:"请选择省份",
		    width: 200,
			onSelected:function(value,text){
				cityWidget.loadUrl("/DispatcherWeb/common/getAreaByParentId.json?parent="+value);
			}
		});
		var cityWidget=$("#cityWidget_0").ComboBoxWidget({
			textField:"name",
			valueField:"id",
			showText:"请选择城市",
			width:200,
			onSelected:function(value,text){
				districtWidget.loadUrl("/DispatcherWeb/common/getAreaByParentId.json?parent="+value);
			}
		});
		var districtWidget=$("#districtWidget_0").ComboBoxWidget({
			textField:"name",
			valueField:"id",
			showText:"请选择区县",
			width:200
		});
		var areaItem={
				provinceWidget:provinceWidget,
				cityWidget:cityWidget,
				districtWidget:districtWidget
				};
		self.areaItems["areaItme_0"]=areaItem;
	},
	_bind:function(){
		self=this;
		
		//单选角色按钮
		$('input[name="role"]').click(function(){
			if($(this).val()==2||$(this).val()==4){
				$("#settleTypeRdo").removeClass("hide");
			}else{
				$("#settleTypeRdo").addClass("hide");
			}
		});
		
		//追加一行区域选择
		$(".iconfont-add").live("click",function(){
			$(this).removeClass("iconfont-add");
			$(this).addClass("iconfont-remove");
			self.areaIndex++;
			var _html='<div id="areaItme_'+self.areaIndex+'" style="margin-bottom:5px;">'
				+'<div id="provinceWidget_'+self.areaIndex+'" style="display:inline-block;margin-right: 20px;float:left;"></div>'
				+'<div id="cityWidget_'+self.areaIndex+'" style="display:inline-block;margin-right: 20px;float:left;"></div>'
				+'<div id="districtWidget_'+self.areaIndex+'" style="display:inline-block;margin-right: 20px;float:left;"></div>'
				+'<div class="address-btn"><i class="iconfont iconfont-add"></div>'
				+'</div>';
			$("#area").append(_html);
			var provinceWidget=$("#provinceWidget_"+self.areaIndex).ComboBoxWidget({
				url:"/DispatcherWeb/common/getAreaByParentId.json",
				textField:"name",
				valueField:"id",
				showText:"请选择省份",
			    width: 200,
				onSelected:function(value,text){
					cityWidget.loadUrl("/DispatcherWeb/common/getAreaByParentId.json?parent="+value);
				}
			});
			var cityWidget=$("#cityWidget_"+self.areaIndex).ComboBoxWidget({
				textField:"name",
				valueField:"id",
				showText:"请选择城市",
				width:200,
				onSelected:function(value,text){
					districtWidget.loadUrl("/DispatcherWeb/common/getAreaByParentId.json?parent="+value);
				}
			});
			var districtWidget=$("#districtWidget_"+self.areaIndex).ComboBoxWidget({
				textField:"name",
				valueField:"id",
				showText:"请选择区县",
				width:200
			});
			var areaItem={
					provinceWidget:provinceWidget,
					cityWidget:cityWidget,
					districtWidget:districtWidget
					};
			self.areaItems["areaItme_"+self.areaIndex]=areaItem;
		});
	
		//删除一行区域选择
		$(".iconfont-remove").live("click",function(){
			delete self.areaItems[$(this).parent().parent().attr("id")];
			$(this).parent().parent().remove();
			self.areaIndex--;
		});
		
		//保存按钮
		$("#saveBtn").click(function(){
			var areaIds=[];
			for(var k in self.areaItems){
				var _provinceId=self.areaItems[k].provinceWidget.getValue();
				var _cityId=self.areaItems[k].cityWidget.getValue();
				var _districtId=self.areaItems[k].districtWidget.getValue();
				if(_provinceId&&_provinceId.length>0){
					areaIds.push({provinceId:_provinceId,cityId:_cityId,districtId:_districtId});
				}
			}
			if(areaIds.length<1){
				MessageUtil.alert("请选择地区范围");
				return;
			}
			
			var name=$("#name").val();
			if(!name||name==""){
				MessageUtil.alert("客户名称不能为空");
				return;
			}else if(name.length>100){
				MessageUtil.alert("客户名称最长不能超过100字符");
				return;
			}
			
			var account=$("#account").val();
			if(!account||account==""){
				MessageUtil.alert("管理账号不能为空");
				return;
			}else if(account.length>100){
				MessageUtil.alert("账号最长不能超过50字符");
				return;
			}
			
			var contacter=$("#contacter").val();
			if(!contacter||contacter==""){
				MessageUtil.alert("联系人不能为空");
				return;
			}else if(contacter.length>100){
				MessageUtil.alert("联系人名称最长不能超过100字符");
				return;
			}
			
			var phone=$("#phone").val();
			if(!phone||phone==""){
				MessageUtil.alert("手机号不能为空");
				return;
			}else if(phone.length>20){
				MessageUtil.alert("手机号最长不能超过20字符");
				return;
			}else if(!CommonUtils.Validate.phone(phone)){
				MessageUtil.alert("手机号格式不正确");
				return;
			}
			
			var email=$("#email").val();
			if(!email&&email!=""&&email.length>100){
				MessageUtil.alert("邮箱最长不能超过100个字符");
				return;
			}else if(!email&&email!=""&&!CommonUtils.Validate.email(email)){
				MessageUtil.alert("邮箱格式不正确");
				return;
			}
			
			var remark=$("#remark").val();
			if(remark!=null&&remark!=""&&remark.length>300){
				MessageUtil.alert("简介长度为0-300字符");
				return;
			}
			
			var registeredCapital=$("#registeredCapital").val();
			if(!registeredCapital&&registeredCapital!=""){
				if(!CommonUtils.Validate.amount(registeredCapital)||!CommonUtils.Validate.decimalsLimit(registeredCapital,2)||!CommonUtils.Validate.maxValLmit(registeredCapital,9999999)){
					MessageUtil.alert("注册资金范围为0-999999999的数值，最多保留两位小数");
					return;
				}
			}
			var scc=$("#scc").val();
			
			var goodsTypeIds=self.goodsTypeWidget.getSelectedValues();
			if(!goodsTypeIds||goodsTypeIds==""){
				MessageUtil.alert("请选择货物类型");
				return;
			}
			
			var role=$('input[name="role"]:checked').val();
			if(!role||role==""){
				MessageUtil.alert("请选择客户角色");
				return;
			}
			
			var settleType=0;
			if(role==2||role==4){
				var settleType=$('input[name="settleType"]:checked').val();
				if(!settleType||settleType==""){
					MessageUtil.alert("请选择结算类型");
					return;
				}
			}
			
			var gmtZone=self.gmtZoneWidget.getValue();
			if(!gmtZone||gmtZone==""){
				MessageUtil.alert("请先选择时区");
				return;
			}
			
			var mileageMin=$("#mileageMin").val();
			if(!mileageMin||mileageMin==""){
				MessageUtil.alert("最小里程不能为空");
				return;
			}else if(!CommonUtils.Validate.number(mileageMin)||!CommonUtils.Validate.maxValLmit(mileageMin,9999999)){
				MessageUtil.alert("最小里程为0-999999999的整数");
				return;
			}
			var mileageMax=$("#mileageMax").val();
			if(!mileageMax||mileageMax==""){
				MessageUtil.alert("最大里程不能为空");
				return;
			}else if(!CommonUtils.Validate.number(mileageMax)||!CommonUtils.Validate.maxValLmit(mileageMax,9999999)){
				MessageUtil.alert("最大里程为0-999999999的整数");
				return;
			}
			if(parseInt(mileageMin)>=parseInt(mileageMax)){
				MessageUtil.alert("最大里程不能小于或等于最小里程");
				return;
			}
			var files=[];
			
			var _filesName=[];
			var _fileList=self.fileWidget.getValue();
			if(_fileList.length>0){
				for(var i=0,len=_fileList.length;i<len;i++){
					var _fileElId=_fileList[i];
					var _fname="file"+i;
					_filesName.push(_fname);
					var _file={id:_fileElId,name:_fname};
					files.push(_file);
				}
			}
			var _logoName=[];
			var _logoList=self.logoWidget.getValue();
			if(_logoList.length>0){
				for(var i=0,len=_logoList.length;i<len;i++){
					var _fileElId=_logoList[i];
					var _fname="logo"+i;
					_logoName.push(_fname);
					var _file={id:_fileElId,name:_fname};
					files.push(_file);
				}
			}
			
			var param={
					files:_filesName.join(","),
					logos:_logoName.join(","),
					id:self.id,
					userId:self.userId,
					name:name,
					account:account,
					role:role,
					settleType:settleType,
					goodsTypeIds:goodsTypeIds.join(","),
					contacter:contacter,
					phone:phone,
					email:email,
					remark:remark,
					registeredCapital:registeredCapital,
					scc:scc,
					areaIds:JSON.stringify(areaIds),
					mileageMin:mileageMin,
					mileageMax:mileageMax,
					gmtZone:gmtZone
				}
			CommonUtils.uploadFile({
				url:"/DispatcherWeb/customer/update.json",
				data:param,
				files:files,
				success:function(result){
					if(result.code==0){
						MessageUtil.info("成功",function(){
							location.href="/"+Constant.PROJECT_NAME+"/settlement/customerManagement/customerList.html";
						});
					}else if(result.code==110013){
						MessageUtil.alert("已经存在相同账号");
					}else if(result.code==110014){
						MessageUtil.alert("已经存在用户邮箱");
					}else if(result.code==110015){
						MessageUtil.alert("已经存在用户手机号");
					}else if(result.code==110001){
						MessageUtil.alert("同级部门存在同名部门");
					}else if(result.code==110002){
						MessageUtil.alert("部门联系人手机号已经存在");
					}else if(result.code==110003){
						MessageUtil.alert("部门联系人邮箱已经存在");
					}else if(result.code==110004){
						MessageUtil.alert("添加团车平台部门失败");
					}else{
						MessageUtil.alert("失败");
					}
				},
				error:function(result){
					MessageUtil.alert("失败");
				}
			});
		});
	},
	_loadData:function(){
		var self=this;
		
		CommonUtils.async({
			url:"/DispatcherWeb/customer/info.json",
			data:{id:self.id},
			success:function(result){
				if(result.code==0){
					var customerInfo=result.data;
					self.userId=customerInfo.userId;
					$("#name").val(customerInfo.name);
					$("#account").val(customerInfo.account);
					$("#contacter").val(customerInfo.contacter);
					$("#phone").val(customerInfo.phone);
					$("#email").val(customerInfo.email);
					$("#remark").val(customerInfo.remark);
					$("#registeredCapital").val(customerInfo.registeredCapital);
					$("#scc").val(customerInfo.scc);
					$("#mileageMin").val(customerInfo.mileageMin);
					$("#mileageMax").val(customerInfo.mileageMax);
					self.gmtZoneWidget.setValue(customerInfo.gmtZone);
					var goodsTypes=customerInfo.goodsTypes;
					var goodsTypeIds=[];
					for (var i = 0; i < goodsTypes.length; i++) {
						goodsTypeIds.push(goodsTypes[i].id);
					};
					self.goodsTypeWidget.setSelectedValues(goodsTypeIds);
					$('input[name="role"]').each(function(){
						var temp=$(this).val();
						if(temp==customerInfo.role){
							$(this).attr('checked', true);
						}
					});
					$('input[name="settleType"]').each(function(){
						var temp=$(this).val();
						if(temp==customerInfo.settleType){
							$(this).attr('checked', true);
						}
					});
					
					var areas=customerInfo.areas;
					for (var i = 0; i < areas.length; i++) {
						(function(_i){
							var area=areas[_i];
							var _html='<div id="areaItme_'+self.areaIndex+'" style="margin-bottom:5px;">'
							+'<div id="provinceWidget_'+self.areaIndex+'" style="display:inline-block;margin-right: 20px;float:left;"></div>'
							+'<div id="cityWidget_'+self.areaIndex+'" style="display:inline-block;margin-right: 20px;float:left;"></div>'
							+'<div id="districtWidget_'+self.areaIndex+'" style="display:inline-block;margin-right: 20px;float:left;"></div>'
							+'<div class="address-btn"><i class="iconfont iconfont-add"></div>'
							+'</div>';
							if(i>0){
								$(".iconfont-add").addClass("iconfont-remove");
								$(".iconfont-add").removeClass("iconfont-add");
								$("#area").append(_html);
							}
							var provinceWidget=$("#provinceWidget_"+self.areaIndex).ComboBoxWidget({
								url:"/DispatcherWeb/common/getAreaByParentId.json",
								textField:"name",
								valueField:"id",
								showText:"请选择省份",
							    width: 200,
								onSelected:function(value,text){
									cityWidget.loadUrl("/DispatcherWeb/common/getAreaByParentId.json?parent="+value);
								}
							});
							var cityWidget=$("#cityWidget_"+self.areaIndex).ComboBoxWidget({
								textField:"name",
								valueField:"id",
								showText:"请选择城市",
								width:200,
								onSelected:function(value,text){
									districtWidget.loadUrl("/DispatcherWeb/common/getAreaByParentId.json?parent="+value);
								}
							});
							var districtWidget=$("#districtWidget_"+self.areaIndex).ComboBoxWidget({
								textField:"name",
								valueField:"id",
								showText:"请选择区县",
								width:200
							});
							
							provinceWidget.setValue(area.provinceId);
							cityWidget.loadUrl("/DispatcherWeb/common/getAreaByParentId.json?parent="+area.provinceId,area.cityId);
							setTimeout(function(){
								districtWidget.loadUrl("/DispatcherWeb/common/getAreaByParentId.json?parent="+area.cityId,area.districtId);
							},300);
							var areaItem={
									provinceWidget:provinceWidget,
									cityWidget:cityWidget,
									districtWidget:districtWidget
									};
							self.areaItems["areaItme_"+self.areaIndex]=areaItem;
							self.areaIndex++;
							
						})(i);
						
					};
					self.fileWidget.setValue([customerInfo.businessLicence]);
					self.logoWidget.setValue([customerInfo.logo]);
				}
			}
		});
	}
}
$(function(){
	CustomerUpdate.init();
});