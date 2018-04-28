var ChargeRuleList={
	tableWidget:null,//表格控件
	init:function(){
		var self=this;
		self._render();
		self._bind();
	},
	_render:function(){
		var self=this;
		
		//设置底部菜单连接
		Footer.nav("计费规则","settlement/chargeRuleManagement/chargeRuleList.html");
		
		//权限判断
		if(CommonUtils.hasAuth("addChargeRule")){
			$("#addBtn").removeClass("hide");
		}
		if(CommonUtils.hasAuth("deleteChargeRule")){
			$("#batchDelete").removeClass("hide");
		}
		
		self.tableWidget=$("#tableWidget").TableWidget({
			url:"/DispatcherWeb/chargeRule/list.json",
			para:{},
			cells:[{
				field:"operation",
				text:"操作",
				buttons:[{
					name:"编辑",
					authCode:"updateChargeRule",
					click:function(id){
						ChargeRuleList.update(id);
					}
				},{
					name:"删除",
					authCode:"deleteChargeRule",
					click:function(id){
						ChargeRuleList.deleteOne(id);
					}
				}]
			},{
				field:"provinceName", //cityName
				text:"地区",
				render:function(value,row){
					var provinceName=value;
					var cityName=row.cityName;
					var areaName=provinceName+cityName;
					return areaName;
				}
			},{
				field:"vehicleTypeName",
				text:"车型"
			},{
				field:"createTime",
				text:"创建时间"
			}]
			//pageSize:10//不填写默认是20
		});
		
	},
	_bind:function(){
		self=this;
		//新建按钮
		$("#btnAdd").unbind("click").click(function(){
			var win=WinWidget.create({
				title:"新增计费规则",
				width:"560px",
				height:"430px",
				//content:"Hello World"
				url:"/dsp/settlement/chargeRuleManagement/chargeRuleAdd.html"
			});
			window.chargeRuleAdd=win;
		});
		
		//删除按钮
		$("#batchDelete").unbind("click").click(function(){
			//MessageUtil.alert("你好");
			MessageUtil.confirm("是否要删除?",function(){
				//删除动作
				self._deleteBatch();
				
			});
		});
		
		$("#queryBtn").unbind("click").click(function(){
			var areaName=$("#areaName").val();
			var vehicleTypeName=$("#vehicleTypeName").val();
			var param={
				areaName:areaName,
				vehicleTypeName:vehicleTypeName,
			}
			//重新给条件查询
			self.tableWidget.query(param);
		});
	},
	_deleteBatch:function(){
		var self=this;
		var ids=self.tableWidget.getSelectedRowIds();
		if(!ids||ids.length==0){
			MessageUtil.alert("请先选择行");
			return;
		}
		
		CommonUtils.async({
			url:"/DispatcherWeb/chargeRule/delete.json",
			data:{id:ids},
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
	},
	update:function(id){
		var win=WinWidget.create({
			title:"修改计费规则",
			width:"560px",
			height:"430px",
			url:"/dsp/settlement/chargeRuleManagement/chargeRuleUpdate.html"
		});
		win.param=id;
		window.chargeRuleUpdate=win;
	},
	deleteOne:function(id){
		var self=this;
		
		MessageUtil.confirm("是否要删除?",function(){
			//删除动作
			CommonUtils.async({
				url:"/DispatcherWeb/chargeRule/delete.json",
				data:{id:[id]},
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
}
$(function(){
	ChargeRuleList.init();
});