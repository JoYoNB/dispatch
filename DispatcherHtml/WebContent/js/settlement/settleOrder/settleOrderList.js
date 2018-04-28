var SettleOrderList={
	payStatus:null,
	tableWidget:null,//表格控件
	goodsTypeWidget:null,//货物类型控件
	startTimeWidget:null,
	endTimeWidget:null,
	init:function(){
		var self=this;
		self._render();
		self._bind();
	},
	_render:function(){
		var self=this;
		
		//设置底部菜单连接
		Footer.nav("订单结算","settlement/settleOrder/settleOrderList.html");
		//权限判断
		if(CommonUtils.hasAuth("settle")){
			$("#batchSettle").removeClass("hide");
		}
		if(CommonUtils.hasAuth("receipt")){
			$("#batchReceipt").removeClass("hide");
		}
		self.tableWidget=$("#tableWidget").TableWidget({
			url:"/DispatcherWeb/settleOrder/list.json",
			para:{},
			cells:[{
				field:"operation",
				text:"操作",
				buttons:[{
					name:"详情",
					click:function(orderNo){
						SettleOrderList._gotoEditPage(orderNo);
					}
				},{
					name:"收款",
					authCode:"receipt",
					click:function(orderNo){
						var _orders=[orderNo];
						SettleOrderList._receipt(_orders);
					}
				},{
					name:"结算",
					authCode:"settle",
					click:function(orderNo){
						var _orders=[orderNo];
						SettleOrderList._settle(_orders);
					}
				}]
			},{
				field:"orderNo",
				text:"订单号"
			},{
				field:"consignorDeptName",
				text:"付款方"
			},{
				field:"carrierDeptName",
				text:"收款方"
			},{
				field:"startSiteName",
				text:"起点名称"
			},{
				field:"endSiteName",
				text:"终点名称"
			},{
				field:"goodsTypeName",
				text:"货物类型"
			},{
				field:"packageNum", //cityName
				text:"运输量",
				render:function(value,row){
					var packageNum=value==null||value==''?'-':value;
					var weight=row.weight==null||value==''?'-':row.weight;
					var volume=row.volume==null||value==''?'-':row.volume;;
					var size=packageNum+'件|'+weight+'吨|'+volume+'方';
					return size;
				}
			},{
				field:"createTime",
				text:"创建时间"
			},{
				field:"fee",
				text:"费用(元)"
			},{
				field:"orderStatus",
				text:"运输状态",
				render:function(value,row){
//					订单状态（10待发布、20已发布、30已失效、40已接单、50已分配、60已取消、70待提货、80配送中、90已结束）
					var _orderStatus='';
					if(value==10){
						_orderStatus='待发布'
					}else if(value==20){
						_orderStatus='已发布'
					}else if(value==30){
						_orderStatus='已失效'
					}else if(value==40){
						_orderStatus='已接单'
					}else if(value==50){
						_orderStatus='已分配'
					}else if(value==60){
						_orderStatus='已取消'
					}else if(value==70){
						_orderStatus='待提货'
					}else if(value==80){
						_orderStatus='配送中'
					}else if(value==90){
						_orderStatus='已结束'
					}
					return _orderStatus;
				}
			},{
				field:"payStatus",
				text:"支付状态",
				render:function(value,row){
//					支付状态（10未付款、20已付款、30已结算、40待退款、50已退款）
					var _payStatus='';
					if(value==10){
						_payStatus='未付款'
					}else if(value==20){
						_payStatus='已付款'
					}else if(value==30){
						_payStatus='已结算'
					}else if(value==40){
						_payStatus='待退款'
					}else if(value==50){
						_payStatus='已退款'
					}
					return _payStatus;
				}
			}]
			//pageSize:10//不填写默认是20
		});
		
		//加载货物类型下拉数据
		self.goodsTypeWidget=$("#goodsTypeWidget").ComboBoxWidget({
			url:"/DispatcherWeb/common/getGoodsTypeList.json",
			textField:"name",
			valueField:"id",
			showText:"请选择货物类型",
		    width: 228
		});
		//时间控件
		self.startTimeWidget=$("#startTimeWidget").TimeWidget({
			showText:"开始日期",
			width:228,
			format:"yyyy-mm-dd",
		    	onSelected:function(value){
	    			$("#timeSelect").val("-1");
		    	}
		});
		self.endTimeWidget=$("#endTimeWidget").TimeWidget({
			showText:"结束日期",
			format:"yyyy-mm-dd",
			width:228,
		    	onSelected:function(value){
		    		$("#timeSelect").val("-1");
		    	}
		});
	},
	_bind:function(){
		self=this;

		//时间快捷选择
		$("#timeSelect").change(function(){
			var selectedValue = $(this).children('option:selected').val();
			if(selectedValue==-1){//		-1请选择
				self.startTimeWidget.setValue(null);
				self.endTimeWidget.setValue(null);
			}else if(selectedValue==0){//		0今天
				var today=CommonUtils.Date.formatDate(new Date());
				self.startTimeWidget.setValue(today);
				self.endTimeWidget.setValue(today);
			}else if(selectedValue==1){//		1昨天
				var yesterday=CommonUtils.Date.formatDate(CommonUtils.Date.add(new Date(),"day",-1));
				self.startTimeWidget.setValue(yesterday);
				self.endTimeWidget.setValue(yesterday);
			}else if(selectedValue==2){//		2本周
				self.startTimeWidget.setValue(CommonUtils.Date.formatDate(CommonUtils.Date.string2Date(CommonUtils.Date.getWeekStartDate())));
				self.endTimeWidget.setValue(CommonUtils.Date.formatDate(CommonUtils.Date.string2Date(CommonUtils.Date.getWeekEndDate())));
			}else if(selectedValue==3){//		3上周
				self.startTimeWidget.setValue(CommonUtils.Date.formatDate(CommonUtils.Date.string2Date(CommonUtils.Date.getLastWeekStartDate())));
				self.endTimeWidget.setValue(CommonUtils.Date.formatDate(CommonUtils.Date.string2Date(CommonUtils.Date.getLastWeekEndDate())));
			}else if(selectedValue==4){//		4本月
				self.startTimeWidget.setValue(CommonUtils.Date.formatDate(CommonUtils.Date.string2Date(CommonUtils.Date.getMonthStartDate())));
				self.endTimeWidget.setValue(CommonUtils.Date.formatDate(CommonUtils.Date.string2Date(CommonUtils.Date.getMonthEndDate())));
			}else if(selectedValue==5){//		5上月
				self.startTimeWidget.setValue(CommonUtils.Date.formatDate(CommonUtils.Date.string2Date(CommonUtils.Date.getLastMonthStartDate())));
				self.endTimeWidget.setValue(CommonUtils.Date.formatDate(CommonUtils.Date.string2Date(CommonUtils.Date.getLastMonthEndDate())));
			}
		});
		
		//批量收款按钮
		$("#batchReceipt").unbind("click").click(function(){
			var orderNos=self.tableWidget.getSelectedRowIds();
			if(!orderNos||orderNos.length==0){
				MessageUtil.alert("请先选择要收款的订单");
				return;
			}
			self._receipt(orderNos);
		});
		
		//批量结算按钮
		$("#batchSettle").unbind("click").click(function(){
			var orderNos=self.tableWidget.getSelectedRowIds();
			if(!orderNos||orderNos.length==0){
				MessageUtil.alert("请先选择要结算的订单");
				return;
			}
			self._settle(orderNos);
		});
		
		//搜索按钮
		$("#queryBtn").unbind("click").click(function(){
			self._query();
		});
		//搜索按钮
		$("#exp").unbind("click").click(function(){
			self._exp();
		});
		
		//状态筛选
		$(".city-tap li").unbind("click").click(function(){
			$(".city-tap li").removeClass("active ");
			$(this).addClass("active");
			self.payStatus=$(this).val();
			if(self.payStatus==""||self.payStatus==0){
				self.payStatus=null;
			}
			self._query();
		});
	},
	_query:function(){
		self=this;
		var goodsTypeId=self.goodsTypeWidget.getValue();
		var startTime=self.startTimeWidget.getValue();
		if(startTime&&startTime!=""){
			startTime+=" 00:00:00";
		}
		var endTime=self.endTimeWidget.getValue();
		if(endTime&&endTime!=""){
			endTime+=" 23:59:59";
		}
		var orderNo=$("#orderNo").val();
		var param={
				goodsTypeId:goodsTypeId,
				startTime:startTime,
				endTime:endTime,
				orderNo:orderNo,
				payStatus:self.payStatus
		}
		//重新给条件查询
		self.tableWidget.query(param);
	},
	_exp:function(){
		self=this;
		var goodsTypeId=self.goodsTypeWidget.getValue();
		var startTime=self.startTimeWidget.getValue();
		if(startTime&&startTime!=""){
			startTime+=" 00:00:00";
		}
		var endTime=self.endTimeWidget.getValue();
		if(endTime&&endTime!=""){
			endTime+=" 23:59:59";
		}
		var orderNo=$("#orderNo").val();
		var param={
				goodsTypeId:goodsTypeId,
				startTime:startTime,
				endTime:endTime,
				orderNo:orderNo,
				payStatus:self.payStatus
		}
		CommonUtils.async({
			url:"/DispatcherWeb/settleOrder/exp.json",
			data:param,
			success:function(result){
				if(result.code==0){
					console.log(result);
					MessageUtil.info("成功",function(){
						if(result.data.url){
							window.open(result.data.url);
						}
					});
				}else{
					MessageUtil.alert("失败");
				}
			},
			error:function(result){
				MessageUtil.alert("失败");
			}
		});
	},
	_receipt:function(orderNos){
		var self=this;
		MessageUtil.confirm("是否要对选中的订单收款?",function(){
			CommonUtils.async({
				url:"/DispatcherWeb/settleOrder/receipt.json",
				data:{orderNos:orderNos},
				success:function(result){
					if(result.code==0){
						MessageUtil.info("成功",function(){
							//删除后重新加载
							self.tableWidget.reload();
						});
					}else if(result.code==140001){
						MessageUtil.alert("订单当前支付状态不能结算");
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
	_settle:function(orderNos){
		var self=this;
		MessageUtil.confirm("是否要结算选中的订单?",function(){
			CommonUtils.async({
				url:"/DispatcherWeb/settleOrder/settle.json",
				data:{orderNos:orderNos},
				success:function(result){
					if(result.code==0){
						MessageUtil.info("成功",function(){
							//删除后重新加载
							self.tableWidget.reload();
						});
					}else if(result.code==140001){
						MessageUtil.alert("订单当前支付状态不能结算");
					}else if(result.code==140002){
						MessageUtil.alert("订单当前运输状态不能结算");
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
	_gotoEditPage:function(orderNo){
		var _url="/"+Constant.PROJECT_NAME+"/settlement/settleOrder/orderInfo.html?orderNo="+orderNo;
		location.href=_url;
	}
}
$(function(){
	SettleOrderList.init();
});