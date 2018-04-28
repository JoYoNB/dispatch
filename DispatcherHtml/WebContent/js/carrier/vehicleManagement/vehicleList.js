var VehicleList={
	tableWidget:null,//表格控件
	vehicleTypeWidget:null,//车辆类型选择控件
	
	init:function(){
		var self=this;
		self._render();
		self._bind();
	},
	_render:function(){
		var self=this;
		//设置底部菜单连接
		Footer.nav("车辆管理","carrier/vehicleManagement/vehicleList.html");
		
		self.vehicleTypeWidget=$("#vehicleTypeWidget").ComboBoxWidget({
			url:"/DispatcherWeb/common/getVehicleTypeList.json",
			textField:"name",
			valueField:"id",
			showText:"请选择车型",
		    width: 228,
		    height:32
		});
		self.tableWidget=$("#tableWidget").TableWidget({
			url:"/DispatcherWeb/vehicle/getVehicleList.json",
			para:{},
			extParam:"userDeptLevel",//额外的参数，除了total，list以外的参数
			cells:[{
				field:"plateNo",
				text:"车牌号"
			},{
				field:"equipmentPn",
				text:"设备PN"
			},{
				field:"vehicleTypeName",
				text:"车辆类型"
			},{
				field:"carryType",
				text:"载货类型",
				render:function(value,row){
					var carryTypesArr=row.carryTypes;
					var carryTypes='';
					carryTypesArr.forEach(function(value,index){
						carryTypes=carryTypes+value.carryTypeName;
						if(index!=carryTypesArr.length-1){
							carryTypes+="/";
						}
					})
					return carryTypes;
				}
			},{
				field:"vehicleWeightMax",
				text:"最大允许总质量(kg)"
			},{
				field:"vehicleLength",
				text:"整车总长(mm)"
			},{
				field:"size",
				text:"货厢内部尺寸(长/宽/高)",
				render:function(value,row){
					var length=row.vehicleInsideLength;
					var width=row.vehicleInsideWidth;
					var height=row.vehicleInsideHeight;
					return length+"/"+width+"/"+height;
				}
			},{
				field:"carryWeigthMax",
				text:"载质量(kg)"
			},{
				field:"createTime",
				text:"创建时间"
			},{
				field:"op",
				text:"操作",
				render:function(value,row){
					var _url="/"+Constant.PROJECT_NAME+"/carrier/vehicleManagement/editVehicle.html?id="+row.vehicleId;
					var _html='<a href="'+_url+'">编辑</a><a href="javascript:VehicleList.deleteVehicle('+row.vehicleId+');">删除</a>';
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
				self._deleteDeptList();
				
			});
		});
		
		$("#queryBtn").unbind("click").click(function(){
			var plateNo=$("#plateNo").val();
			var equipmentPn=$("#equipmentPn").val();
			var param={
				plateNo:plateNo,
				equipmentPn:equipmentPn
			}
			var vehicleTypeId=self.vehicleTypeWidget.getValue();
			if(vehicleTypeId&&vehicleTypeId!=""){
				param.vehicleTypeId=vehicleTypeId;
			}
			//重新给条件查询
			self.tableWidget.query(param);
		});
	},
	_deleteVehicleList:function(){
		var self=this;
		var ids=self.tableWidget.getSelectedRowIds();
		if(!ids||ids.length==0){
			MessageUtil.alert("请先选择行");
			return;
		}
		
		CommonUtils.async({
			url:"/DispatcherWeb/driver/deleteVehicle.json",
			data:{driverIds:ids},
			success:function(result){
				if(result.code==0){
					MessageUtil.info("成功",function(){
						//删除后重新加载
						self.tableWidget.reload();
					});
				}else if(result.code=130004){
						MessageUtil.alert("该车辆有关联司机,删除需先解绑");
				}else{
					MessageUtil.alert("失败");
				}
			},
			error:function(result){
				MessageUtil.alert("失败");
			}
		});
	},
	deleteVehicle:function(id){
		var self=this;
		
		MessageUtil.confirm("是否要删除?",function(){
			//删除动作
			CommonUtils.async({
				url:"/DispatcherWeb/vehicle/deleteVehicle.json",
				data:{vehicleId:id},
				success:function(result){
					if(result.code==0){
						MessageUtil.info("成功",function(){
							//删除后重新加载
							self.tableWidget.reload();
						});
					}else if(result.code=130004){
						MessageUtil.alert("该车辆有关联司机,删除需先解绑");
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
	VehicleList.init();
});