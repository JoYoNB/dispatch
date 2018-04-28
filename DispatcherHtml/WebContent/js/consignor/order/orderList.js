var OrderList={
	tableWidget:null,
	timeComboBox:null,
	orderComboBox:null,
	cargoComboBox:null,
	statusComboBox:null,
	startTimeWidget:null, //开始时间
	endTimeWidget:null, //结束时间
	orderStatus:null,
	
	init:function(){
		var self=this;
		self._render();
		self._bind();
	},
	_render:function(){
		var self=this;
		
		//设置底部菜单连接
		Footer.nav("订单管理","consignor/order/orderList.html");
		
		/*self.deptWidget=$("#deptWidget").DeptWidget({
		});*/
		self.tableWidget=$("#tableWidget").TableWidget({
			url:"/DispatcherWeb/consignor/order/orderList.json",
			pageSize:20,
			cells:[{
				field:"id",
				text:"订单编号"
			},{
				field:"startName",
				text:"起点名称"
			},{
				field:"endName",
				text:"终点名称"
			},{
				field:"goodsType",
				text:"货物类型"
			},{
				field:"freightVolume",
				text:"运输量"
			},{
				field:"createTime",
				text:"创建时间"
			},{
				field:"statusName",
				text:"订单状态"
			},{
				field:"operation",
				text:"操作",
				render:function(value,row){
					//查看
					var _html = "<li><a href='javascript:OrderList.orderDetail(\""+row.id+"\");'>详情</a></li>";
					//编辑、发布
					if(row.orderStatus==10||row.orderStatus==30||row.orderStatus==60){ 
						//未发布可以编辑
						//已失效、已取消只能编辑要求提货时间
						_html += "<li><a href='javascript:OrderList.updateOrder(\""+row.id+"\");'>编辑</a></li>";
						_html += "<li><a href='javascript:OrderList.releaseOrder(\""+row.id+"\");'>发布</a></li>";
					}
					//取消 已发布、已分配、已接单可以取消
					if(row.orderStatus==20||row.orderStatus==40||row.orderStatus==50){ 
						_html += "<li><a href='javascript:OrderList.cancleOrder(\""+row.id+"\");'>取消</a></li>";
					}
					//删除 待发布、已结束可以删除
					if(row.orderStatus==10||row.orderStatus==90){ 
						_html += "<li><a href='javascript:OrderList.deleteOrder(\""+row.id+"\");'>删除</a></li>";
					}
					return _html;
				}
			}]
		});
		
		self.timeComboBox=$("#timeComboBox").ComboBoxWidget({
			textField:"name",
			valueField:"value",
			showText:"选择时间",
		    width: 228,
			onSelected : function(value, text) {
				if(value){
					$('#startTimeWidget').addClass('hide');
					$('#endTimeWidget').addClass('hide');
				}else{
					$('#startTimeWidget').removeClass('hide');
					$('#endTimeWidget').removeClass('hide');
				}
			},
			data: [ {
				name : "今天",
				value : "4"
			}, {
				name : "昨天",
				value : "5"
			}, {
				name : "本周",
				value : "0"
			},{
				name : "上周",
				value : "1"
			}, {
				name : "本月",
				value : "2"
			}, {
				name : "上月",
				value : "3"
			}]
		});
		self.orderComboBox=$("#orderComboBox").ComboBoxWidget({
			textField:"name",
			valueField:"value",
			//showText:"订单号",
		    width: 228,
			onSelected : function(value, text) {
				$('#orderCondition').attr('placeholder','输入'+text);
			},
			data: [ {
				name : "订单号",
				value : "1"
			}, {
				name : "车牌号",
				value : "2"
			}, {
				name : "驾驶员姓名",
				value : "3"
			}]
		});
		self.cargoComboBox=$("#cargoComboBox").ComboBoxWidget({
			url:"/DispatcherWeb/common/getGoodsTypeList.json",
			textField:"name",
			valueField:"id",
			showText:"选择货物类型",
		    width: 228,
		    onSelected:function(value,text){
			},
		});
		
		self.statusComboBox=$("#statusComboBox").ComboBoxWidget({
			textField:"name",
			valueField:"value",
			showText:"选择订单状态",
		    width: 228,
			onSelected : function(value, text) {
				
			}, //待发布/已发布/已分配/待提货/配送中/已结束 
			data: [ {
				name : "待发布",
				value : "10"
			}, {
				name : "已发布",
				value : "20"
			}, {
				name : "已分配",
				value : "50"
			},{
				name : "待提货",
				value : "70"
			}, {
				name : "配送中",
				value : "80"
			}, {
				name : "已结束",
				value : "90"
			}]
		});
		
		self.startTimeWidget=$('#startTimeWidget').TimeWidget({
			width:228,
			showText:"开始时间",
			event:{
				name:'changeDate',
				callback:function(ev){
					console.log(11111);
					self.timeComboBox.el.find('select').attr('disabled','disabled');
				}
			}});
		self.endTimeWidget=$('#endTimeWidget').TimeWidget({
			width:228,
			showText:"结束时间",
			event:{
				name:'changeDate',
				callback:function(ev){
					self.timeComboBox.el.find('select').attr('disabled','disabled');
				}
			}});
	},
	_bind:function(){
		var self=this;
		//导出
		$("#exportOrder").unbind('click').click(function(){
			self.exportOrder();	
		});
		//批量发布
		$("#batchRelease").unbind('click').click(function(){
			self.batchRelease();	
		});
		//批量删除
		$("#batchDelete").unbind('click').click(function(){
			//删除动作
			self._batchDelete();	
		});
		//查询
		$("#queryList").unbind('click').click(function(){
			self._queryList();
		});
		$('#statusGroup').find('a').click(function(){
			var _this = $(this);
			var status = parseInt(_this.attr('data-status'));
			_this.parent().siblings('.active').removeClass('active');
			_this.parent().addClass('active');
			self._queryList(status);
		});
		$('#reset').unbind('click').click(function(){
			self.timeComboBox.setText('选择时间');
			self.startTimeWidget.reset();
			self.endTimeWidget.reset();
			self.orderComboBox.setText('请选择');
			$('#orderCondition').attr('placeholder','条件输入').val(null);
			self.cargoComboBox.setText('选择货物类型');
			self.statusComboBox.setText('选择订单状态');
			$("#startName").val(null);
			$("#middleOrEnd").val(null);
			self.timeComboBox.el.find('select').removeAttr("disabled");
			$('#startTimeWidget').removeClass('hide');
			$('#endTimeWidget').removeClass('hide');

		});
	},
	_batchDelete:function(){
		var self=this;
		var ids=self.tableWidget.getSelectedRowIds();
		if(!ids||ids.length==0){
			MessageUtil.alert("请先选择行");
			return;
		}
		MessageUtil.confirm("是否要删除?",function(){
		CommonUtils.async({
			url:"/DispatcherWeb/consignor/order/deleteList.json",
			data:{orderNo:ids},
			success:function(result){
				if(result.code==0){
					MessageUtil.info("成功",function(){
						//删除后重新加载
						self.tableWidget.reload();
					});
				}else if(result.code==120006){
					MessageUtil.alert("订单当前状态不能删除");
				}else{
					MessageUtil.alert('失败');
				}
			},
			error:function(result){
				MessageUtil.alert("失败");
			}
		});
		});
	},
	deleteOrder:function(id){
		var self = this;
		MessageUtil.confirm("是否要删除?",function(){
			//删除动作
			CommonUtils.async({
				url:"/DispatcherWeb/consignor/order/delete.json",
				data:{orderNo:id},
				success:function(result){
					if(result.code==0){
						MessageUtil.info("成功",function(){
							//删除后重新加载
							self.tableWidget.reload();
						});
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
	_queryList:function(status){
		var self = this;
		var param={};
		if(status){
			if(status!=1){
				param.status=status;
				self.orderStatus = status;
			}else{
				self.orderStatus = null;
			}
			self.tableWidget.query(param);
			return;
		}
		//时间
		var timeOpt=self.timeComboBox.getValue();
		if(timeOpt){
			var timeObj=CommonUtils.Date.getTimeObject(timeOpt);
			param = $.extend({},param,timeObj);
		}else{
			if(self.startTimeWidget.getValue()){
				param.startTime = self.startTimeWidget.getValue();
			}
			if(self.endTimeWidget.getValue()){
				param.endTime = self.endTimeWidget.getValue();
			}
			if(param.startTime&&param.endTime&&param.startTime>=param.endTime){
				MessageUtil.alert('开始时间必须小于结束时间');
				return;
			}
		}
		//起点名称
		var startName=$("#startName").val();
		if(startName){
			param.startSiteName = startName;
		}
		//配送点或终点
		var middleOrEnd=$("#middleOrEnd").val();
		if(middleOrEnd){
			param.middleOrEnd = middleOrEnd;
		}
		var orderOpt = self.orderComboBox.getValue();
		if(orderOpt){
			var conVal = $('#orderCondition').val();
			if(conVal){
				switch(orderOpt){
					case '1':param.orderNo = conVal;break; //订单编号
					case '2':param.plateNo = conVal;break; //车牌号
					case '3':param.driver = conVal;break; //驾驶员姓名
					default:;
				}	
			}
		}
		//货物类型
		var cargoType = self.cargoComboBox.getValue();
		if(cargoType){
			param.cargoType = parseInt(cargoType);
		}
		//订单状态
		var orderStatus = self.statusComboBox.getValue();
		if(orderStatus){
			param.status = parseInt(orderStatus);
			var topStatus = $('#statusGroup');
			topStatus.find('.active').removeClass('active');
			topStatus.find('a[data-status='+orderStatus+']').parent().addClass('active');
		}else if(self.orderStatus){
			param.status = self.orderStatus;
		}
		console.log(param);
		self.tableWidget.query(param);
		
	},
	updateOrder:function(id){
		window.location = "/"+Constant.PROJECT_NAME+"/consignor/order/orderModify.html?id="+id;
	},
	orderDetail : function(id){
		window.location = "/"+Constant.PROJECT_NAME+"/consignor/order/orderDetail.html?id="+id;
	},
	releaseOrder: function(id){
		var win=WinWidget.create({
			title:"发布订单",
			width:"560px",
			height:"430px",
			url:"/dsp/consignor/order/releaseOrder.html"
		});
		win.param=id;
		window.chargeRuleUpdate=win;
	},
	cancleOrder : function(id){
		
	},
	batchRelease : function(){
		
	},
	exportOrder : function(){
		var self = this;
		var param = {};
		//时间
		var timeOpt=self.timeComboBox.getValue();
		if(timeOpt){
			var timeObj=CommonUtils.Date.getTimeObject(timeOpt);
			param = $.extend({},param,timeObj);
		}else{
			if(self.startTimeWidget.getValue()){
				param.startTime = self.startTimeWidget.getValue();
			}
			if(self.endTimeWidget.getValue()){
				param.endTime = self.endTimeWidget.getValue();
			}
			if(param.startTime&&param.endTime&&param.startTime>=param.endTime){
				MessageUtil.alert('开始时间必须小于结束时间');
				return;
			}
		}
		//起点名称
		var startName=$("#startName").val();
		if(startName){
			param.startSiteName = startName;
		}
		//配送点或终点
		var middleOrEnd=$("#middleOrEnd").val();
		if(middleOrEnd){
			param.middleOrEnd = middleOrEnd;
		}
		var orderOpt = self.orderComboBox.getValue();
		if(orderOpt){
			var conVal = $('#orderCondition').val();
			if(conVal){
				switch(orderOpt){
					case '1':param.orderNo = conVal;break; //订单编号
					case '2':param.plateNo = conVal;break; //车牌号
					case '3':param.driver = conVal;break; //驾驶员姓名
					default:;
				}	
			}
		}
		//货物类型
		var cargoType = self.cargoComboBox.getValue();
		if(cargoType){
			param.cargoType = parseInt(cargoType);
		}
		//订单状态
		var orderStatus = self.statusComboBox.getValue();
		if(orderStatus){
			param.status = parseInt(orderStatus);
			//var topStatus = $('#statusGroup');
			//topStatus.find('.active').removeClass('active');
			//topStatus.find('a[data-status='+orderStatus+']').parent().addClass('active');
		}else if(self.orderStatus){
			param.status = self.orderStatus;
		}
		console.log(param);
		//导出订单
		CommonUtils.async({
			url:"/DispatcherWeb/consignor/order/export.json",
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
	}
	
}
$(function(){
	OrderList.init();
});