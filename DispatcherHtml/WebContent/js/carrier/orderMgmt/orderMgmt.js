var OrderMgmt={
	tableWidget:null,//表格控件
	deptWidget:null,//部门选择控件
	timeWidgetStart:null,//筛选条件开始时间
	timeWidgetEnd:null,//筛选条件结束时间
	goodsTypeWidget:null,//筛选条件货物类型
	
	init:function(){
		var self=this;
		self._render();
		self._bind();
	},
	_render:function(){
		var self=this;
		//设置底部菜单连接
		Footer.nav("订单列表","carrier/orderMgmt/orderList.html");
		
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
		
		//加载货物类型下拉数据
		self.goodsTypeWidget=$("#goodsTypeWidget").ComboBoxWidget({
			url:"/DispatcherWeb/common/getGoodsTypeList.json",
			textField:"name",
			valueField:"id",
			showText:"请选择货物类型",
		    width: 228
		});
		
		self.tableWidget=$("#tableWidget").TableWidget({
			url:"/DispatcherWeb/carrier/order/getOrderList.json",
			cells:[{
				field:"operation",
				text:"操作",
				/*buttons:[{
					name:"详情",
					click:function(id){
						OrderMgmt.getOrderDetail(id)
					}
				},{
					name:"接单",
					click:function(id){
						Assignment.acceptOrder(id);
					}
				},{
					name:"分配",
					click:function(id){
						Assignment.showVehiclesDlg(id);
					}
				},{
					name:"线路",
					click:function(id){
						OrderMgmt.getOrderRoute(id);
					}
				}]*/
				render:function(value,row){
					console.log(row);
					var _html='<li><a href="javascript:OrderMgmt.getOrderDetail(\''+ row.orderNo +'\');" target= "_blank">详情</a></li>';
					var _url= '';
					if(row.orderStatus == 20){
						_html += '<li><a href="javascript:Assignment.acceptOrder(\''+ row.orderNo +'\');">接单</a></li>';
					}
					if(row.orderStatus == 40){
						_html += '<li><a href="javascript:Assignment.showVehiclesDlg(\''+ row.orderNo +'\');">分配</a></li>';
					}
					_html += '<li><a href="javascript:OrderMgmt.getOrderRoute(\''+ row.orderNo +'\');">线路</a></li>';
					return _html;
				}
			},{
				field:"orderNo",
				text:"订单编号"
			},{
				field:"consignorName",
				text:"客户"
			},{
				field:"startSiteName",
				text:"起点名称",
				render:function(value, row){
					var _html = row.startSiteName  + "("+ row.startAddr +")";
					return _html;
				}
			},{
				field:"endSiteName",
				text:"终点名称",
				render:function(value, row){
					var _html = row.endSiteName  + "("+ row.endAddr +")";
					return _html;
				}
			},{
				field:"goodsTypeName",
				text:"货物类型"
			},{
				field:"packageNum",
				text:"运输量",
				render:function(value, row){
					var _html = '';
					if(row.packageNum){
						_html += row.packageNum + '件|'
					}
					if(row.volume){
						_html +=  + row.volume + '方|'
					}
					if(row.weight){
						_html +=  row.weight + '吨'
					}
					return _html;
				}
			},{
				field:"publishTime",
				text:"发布时间",
				render:function(value,row){
					//时间戳转换为日期格式
					/*var _entryTime=CommonUtils.Date.timestampToTime(value);
					return _entryTime;*/
					return value;
				}
			},{
				field:"orderStatus",
				text:"状态",
				render:function(value,row){
					return OrderStatusUtil.getStatusName(value);
				}
			}]
			//pageSize:10//不填写默认是20
		});
		
	},
	_bind:function(){
		var self=this;
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
			self._search();
		});
		
		$(".tap-item").unbind("click").click(function(){
			if(!$(this).hasClass("active")){
				$(".tap-item").removeClass("active");
				$(this).addClass("active");
				var orderStatus = $(this).find("a").attr("data");
				$("#orderStatus").val(orderStatus);
				self._search();
			}
		});
		$("#acceptBtn").unbind("click").click(function(){
			self._accept();
		});
		$("#assignBtn").unbind("click").click(function(){
			self._assign();
		});
	},
	_search : function(){
		var self = this;
		var orderNo=$("#orderNo").val();
		var goodsType = self.goodsTypeWidget.getValue();
		var startTime="";
		var endTime="";
		var timeFlag = $("#timeFlag").val();
		if(timeFlag > -1){
			var timeScope=CommonUtils.Date.getTimeObject(timeFlag);
			if(timeScope&&timeScope!=""){
				startTime=timeScope.startTime;
				endTime=timeScope.endTime
			}
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
		var orderStatus = $("#orderStatus").val();
		var startSiteName = $("#startSiteName").val();
		var endSiteName = $("#endSiteName").val();
		var plateNo = $("#plateNo").val();
		var driverName = $("#driverName").val();
		var param={
			orderNo:orderNo,
			orderStatus: orderStatus,
			goodsType:goodsType,
			startTime:startTime,
			endTime:endTime,
			startSiteName:startSiteName,
			endSiteName:endSiteName,
			plateNo:plateNo,
			driverName:driverName
		}
		console.log(param);
		//重新给条件查询
		self.tableWidget.query(param);
	},
	_accept: function(){
		var ids=self.tableWidget.getSelectedRowIds();
		if(!ids||ids.length==0){
			MessageUtil.alert("请先选择行");
			return;
		}
		if(ids.length>1){
			MessageUtil.alert("接单只能选择一行");
			return;
		}
		var orderNo = ids[0].split('-')[0];
		var orderStatus = ids[0].split('-')[1];
		if(orderStatus !=  20){
			MessageUtil.alert("该订单状态不能执行接单操作");
			return;
		}
		Assignment.acceptOrder(orderNo)
	},
	_assign: function(){
		var ids=self.tableWidget.getSelectedRowIds();
		console.log(ids);
		if(!ids||ids.length==0){
			MessageUtil.alert("请先选择行");
			return;
		}
		if(ids.length>1){
			MessageUtil.alert("分配只能选择一行");
			return;
		}
		var orderNo = ids[0].split('-')[0];
		var orderStatus = ids[0].split('-')[1];
		if(orderStatus !=  40){
			MessageUtil.alert("该订单状态不能执行分配操作");
			return;
		}
		Assignment.showVehiclesDlg(orderNo);
	},
	getOrderDetail:function(orderNo){
		window.open("/dsp/carrier/orderMgmt/orderDetail.html?orderNo=" + orderNo);
	},
	getOrderRoute:function(orderNo){
		window.open("/dsp/carrier/monitor/route.html?orderNo=" + orderNo);
	}
}
$(function(){
	OrderMgmt.init();
});