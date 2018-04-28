var Assignment={
	tableWidget:null,// 表格控件
	deptWidget:null,// 部门选择控件
	winWidget:null, // 车辆弹出列
	_orderNo:null,
	vehicleTypeWidget:null,
	carryTypeWidget:null,
	vehicleWidget:null,
	init:function(){
		var self=this;
		
		self._render();
		self._bind();
	},
	_render:function(){
		var self=this;
	},
	_bind:function(){
		var self=this;
		
	},
	initVehiclePage:function(){
		var self = this;
		self._bindAssignmentBtn();
		self._bindCancelBtn();
		
		self.vehicleTypeWidget = $("#vehicleTypeWidget").ComboBoxWidget({
			url : "/DispatcherWeb/common/getVehicleTypeList.json",
			textField : "name",
			valueField : "id",
			showText : "请选择车型",
			width : 150
		});
		self.vehicleTypeWidget = $("#carryTypeWidget").ComboBoxWidget({
			url : "/DispatcherWeb/common/getCarryTypeList.json",
			textField : "name",
			valueField : "id",
			showText : "请选择载货属性",
			width : 150
		});
		var orderNo = self._orderNo;
		self.vehicleWidget = $("#vehicleWidget").VehicleWidget({
			url:"/DispatcherWeb/carrier/assignment/listSuitableVehicles.json",
			param:{orderNo:orderNo}
		});
		setTimeout(function(){
			$("#assignForm").find("#orderNo").val(orderNo);
		}, 500)
		$(".pop-search-btn").unbind("click").click(function(){
			self._listVehicles();
		});
	},
	_bindAssignmentBtn : function(){
		var self = this;
		$(".assginBtn").unbind("click").click(function(){
			self._assignOrder();
		});
	},
	_bindCancelBtn: function(){
		var self = this;
		$(".cancelBtn").unbind("click").click(function(){
			self.winWidget.close();
		});
		$(".pop-close-icon").unbind("click").click(function(){
			self.winWidget.close();
		});
	},
	listOrders:function(){
		var self = this;
		CommonUtils.async({
			url:"/DispatcherWeb/carrier/index/listOrders.json",
			data:{},
			success:function(result){
				if(result.code != 0)
					return;
				var orders = result.data;
				for(var i=0; i<orders.length; i++){
					var order = orders[i];
					var orderNo = order.orderNo;
					var goodsName = order.goodsTypeName;
					if(order.goodsTypeName){
						goodsName += order.packageNum + '件';
					}
					var prePickupTime = CommonUtils.Date.date2String(new Date(order.prePickupTime), 'MM-dd HH:mm');
					$(this).find(".prePickupTime").text(prePickupTime);
					var preFinishTime = CommonUtils.Date.date2String(new Date(order.preFinishTime), 'MM-dd HH:mm');
					$(this).find(".preFinishTime").text(preFinishTime);
					var html = 
						'<li class="recommend-order-item" id="order_'+ orderNo +'">                                       '+
						'	<span class="order-icon"><i class="iconfont iconfont-order"></i></span>                       '+
						'	<div class="order-details">                                                                   '+
						'		<div class="order-desc fl">                                                               '+
						'			<p>                                                                                   '+
						'				<span class="siteName">'+order.startSiteName + ' - ' + order.endSiteName+'</span> '+
						'			</p>                                                                                  '+
						'			<p>                                                                                   '+
						'				全程约<span class="distance">'+ order.distance +'km</span>                        '+
						'				<em class="order-money fee"> ￥'+ order.fee +'</em>                               '+
						'			</p>                                                                                  '+
						'		</div>                                                                                    '+
						'		<input  class="order-item-btn fr acceptBtn" type="button" value="接单">                   '+
						'		<p>                                                                                       '+
						'			货物: <span class="goodsName">'+ order.goodsTypeName +'</span>                        '+
						'			车型:<span class="vehicleTypeName">'+order.vehicleTypeName+'</span></p>               '+
						'		<p>                                                                                       '+
						'			提货:<span class="prePickupTime">'+prePickupTime+'</span>                             '+
						'			送达:<span class="preFinishTime">'+preFinishTime+'</span>                             '+
						'		</p>                                                                                      '+
						'	</div>                                                                                        '+
						'</li>                                                                                            ';
					for(var j=0; j<10; j++){
						$(".order-list").append(html);
					}
					//$(".order-list").append(html);
					$("#order_"+ orderNo).find(".acceptBtn").unbind("click").click(function(){
						self.acceptOrder(orderNo);
					});
				}
				$('.transport-list-box').myScroll({
					speed : 4, // 数值越大，速度越慢
					rowHeight : 139, // li的高度
					switchBtn:"autoScroll" //滚动启动开关
				});
			},
			error:function(result){
			}
		});
	},
	acceptOrder:function(orderNo){
		var self = this;
		MessageUtil.confirm('确认接单？', function(){
			CommonUtils.async({
				url:"/DispatcherWeb/carrier/assignment/accept.json",
				data:{orderNo: orderNo},
				success:function(result){
					if (result.code == 0) {
						$("#order_" + orderNo).slideUp("fast", function(){
							$(this).remove();
						});
						MessageUtil.info("接单成功");
						self.showVehiclesDlg(orderNo);
					}else if (result.code != 1) {
						MessageUtil.alert(result.msg);
					}else{
						MessageUtil.alert("接单失败，系统错误");
					}
				},
				error:function(result){
					MessageUtil.alert("接单失败，系统错误");
				}
			});
		});
		self.showVehiclesDlg(orderNo);
	},
	showVehiclesDlg:function(orderNo){
		var self = this;
		self._orderNo = orderNo;
		self.winWidget = WinWidget.create({
			title:'分配司机', 
			width:'500px', 
			height:'320px', 
			url:'/dsp/carrier/assignment/suitableVehicles.html'
		});
	},
	_listVehicles:function(){ // 获取车辆列表
		var self = this;
		/*var content =  $(".vehicleSearchBox").find("#content").val();
		$("#assignForm").find("#orderNo").val(orderNo);
		CommonUtils.async({
			url:"/DispatcherWeb/carrier/assignment/listSuitableVehicles.json",
			data:{carryTypeId: carryTypeId, vehicleType:vehicleType, content:content, orderNo:orderNo},
			success:function(result){
				$(".pop-main-content").empty();
				var vehicles = result.data;
				for(var i=0; i<vehicles.length; i++){
					var vehicle = vehicles[i];
					console.log(vehicle);
					var vehicleId = vehicle.vehicleId;
					var driverId = vehicle.driverId;
					
					var onlineStatus = '离线';
					if(vehicle.onlineStatus == 1){
						onlineStatus = '在线';
					}
					var loadStatus = self.getLoadStatusNameByLoadRate(vehicle.loadRate);
					var html = 
						'<div class="pop-main-select-item" id="vehicle_'+ vehicleId +'">                          '+
						'	<span class="item-icon">                                                              '+
						'		<i class="iconfont icon-cheliangziliao"></i>                                      '+
						'	</span>                                                                               '+
						'	<div class="item-details">                                                            '+
						'		<p>                                                                               '+
						'			<span class="plateNo">'+ vehicle.plateNo +'</span> |                          '+
						'			<span class="driver">'+ vehicle.driverName +'('+ vehicle.phoneNo+')' +'</span>'+
						'			<span class="onlineStatus">'+ onlineStatus +'</span>                          '+
						'		</p>                                                                              '+
						'		<p>                                                                               '+
						'			<span> 当前位置:</span><span class="position">'+ vehicle.position +'</span>   '+
						'		</p>                                                                              '+
						'		<p>                                                                               '+
						'			<span class="vehicleType">'+ vehicle.vehicleTypeName +'</span>                '+
						'			货载状态: <span class="loadStatus">'+ loadStatus +'</span>            		  '+
						'		</p>                                                                              '+
						'	</div>                                                                                '+
						'</div>																					  ';
					$(".pop-main-content").append(html);
					$("#vehicle_"+ vehicleId).attr("vehicleId", vehicleId);
					$("#vehicle_"+ vehicleId).attr("driverId", driverId);
					$("#vehicle_"+ vehicleId).unbind("click").click(function(){
						if(!$(this).hasClass("active")){
							$(".pop-main-select-item").removeClass('active');
							$(this).addClass('active');
							$("#assignForm").find("#vehicleId").val($(this).attr("vehicleId"));
							$("#assignForm").find("#driverId").val($(this).attr("driverId"));
						}
					});
				}
			},
			error:function(result){
			}
		});*/
		var carryTypeId = null;
		if(self.carryTypeWidget){
			carryTypeId = self.carryTypeWidget.getValue();
		}
		var vehicleType =  null;
		if(self.vehicleTypeWidget){
			vehicleType = self.vehicleTypeWidget.getValue();
		}
		var orderNo = $("#assignForm").find("#orderNo").val();
		var content =  $(".vehicleSearchBox").find("#content").val();
		var param = {carryTypeId: carryTypeId, vehicleType:vehicleType, content:content, orderNo:orderNo};
		self.vehicleWidget.query(param);
	},
	_assignOrder: function(){
		var self = this;
		var orderNo = $("#assignForm").find("#orderNo").val();
		var vehicleId = $("#assignForm").find("#vehicleId").val();
		var driverId = $("#assignForm").find("#driverId").val();
		console.log({orderNo:orderNo, vehicleId:vehicleId, driverId:driverId});
		if(vehicleId == '' || driverId == ''){
			MessageUtil.alert("请选中车辆");
			return;
		}
		CommonUtils.async({
			url:"/DispatcherWeb/carrier/assignment/assign.json",
			data:{orderNo:orderNo, vehicleId:vehicleId, driverId:driverId},
			success:function(result){
				if(result.code == 0){
					MessageUtil.info("分配车辆成功");
				}else if(result.code != 1){
					MessageUtil.alert(result.msg);
				}else{
					MessageUtil.alert("系统错误，分配车辆失败");
				}
				
				self.winWidget.close();
			},
			error:function(result){
				MessageUtil.alert("系统错误，分配车辆失败");
			}
		});
	},
	getLoadStatusNameByLoadRate: function(loadRate){
		loadRate = parseInt(loadRate);
		var statusName = "";
		if(loadRate == 0){
			statusName = "空载";
		}
		else if(loadRate > 0 && loadRate < 90){
			statusName = "半载";
		}
		else if(loadRate > 90){
			statusName = "满载";
		}
		return statusName;
	}
}
