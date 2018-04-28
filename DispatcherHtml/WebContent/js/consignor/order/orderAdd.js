var OrderAdd={
	goodsTypeWidget:null, //货物类型下拉控件
	vehicleTypeWidget:null, //车辆类型下拉控件
	startSiteWidget:null, //起点站点下拉控件
	endSiteWidget:null, //终点下拉控件
	middleSiteWidget:null, //配送点下拉控件群
	pickupTimeWidget:null, //提货时间控件
	finishTimeWidget:null, //送达时间控件
	siteIds:null, //配送点id组
	distance:0,
	
	
	init:function(){
		var self=this;
		self._render();
		self._bind();
	},
	_render:function(){
		var self = this;
		
		//设置底部菜单连接
		Footer.nav("站点管理","consignor/order/orderList.html");
		
		
		self.goodsTypeWidget=$("#goodsTypeWidget").ComboBoxWidget({
			url:"/DispatcherWeb/common/getGoodsTypeList.json",
			textField:"name",
			valueField:"id",
			showText:"请选择货物类型",
		    width: 250,
		    onSelected:function(value,text){
				self.getDistance(function(result){
					if(result.distance){
						self.getPriceBack(result.distance,function(price){
							$('#spanAdvicePrice').text(price.toFixed(2));
						});
					}
				});
			},
		});
		self.vehicleTypeWidget=$("#vehicleTypeWidget").ComboBoxWidget({
			url:"/DispatcherWeb/common/getVehicleTypeList.json",
			textField:"name",
			valueField:"id",
			showText:"请选择车型",
		    width: 250,
		    onSelected:function(value,text){
				self.getDistance(function(result){
					if(result.distance){
						self.getPriceBack(result.distance,function(price){
							$('#spanAdvicePrice').text(price.toFixed(2));
						});
					}
				});
			}
		});
		self.startSiteWidget=$("#startSiteWidget").ComboBoxWidget({
			url:"/DispatcherWeb/consignor/site/getSiteForSelect.json",
			textField:"name",
			valueField:"id",
			showText:"请选择起点",
		    width: 250,
		    onSelected:function(value,text,widget){
		    	//不能选相同站点
				if(self.endSiteWidget.getValue()&&self.endSiteWidget.getValue()==value){
					MessageUtil.alert("配送点不能重复选择");
					widget.setText("请选择起点");
					return;
				}
				if(self.middleSiteWidget&&self.middleSiteWidget.length>0){
					for(var i in self.middleSiteWidget){
						if(self.middleSiteWidget[i].getValue()&&self.middleSiteWidget[i].getValue()==value){
							MessageUtil.alert("配送点不能重复选择");
							widget.setText("请选择起点");
							return;
						}
					}
				}
		    	
				$('#startLinkName').val(value.split('@#$%')[1]);
				$('#startLinkPhone').val(value.split('@#$%')[2]);
				self.getDistance(function(result){
					if(result.distance){
						self.getPriceBack(result.distance,function(price){
							$('#spanAdvicePrice').text(price.toFixed(2));
						});
					}
				});
				//从下拉选项中移除已经被选中的
				/*self.endSiteWidget.el.children("option[value='"+value+"']").remove();
				if(self.middleSiteWidget&&self.middleSiteWidget.length>0){
					for(var i in self.middleSiteWidget){
						self.middleSiteWidget[i].el.children("option[value='"+value+"']").remove();
					}
				}*/
				
			}
		});
		
		var middleSiteWidget0=$("#middleSiteWidget0").ComboBoxWidget({
			url:"/DispatcherWeb/consignor/site/getSiteForSelect.json",
			textField:"name",
			valueField:"id",
			showText:"请选择配送点",
			width:250,
			onSelected:function(value,text,widget){
				//不能选相同站点
				if(self.startSiteWidget.getValue()&&self.startSiteWidget.getValue()==value){
					MessageUtil.alert("配送点不能重复选择");
					widget.setText("请选择配送点");
					return;
				}
				if(self.endSiteWidget.getValue()&&self.endSiteWidget.getValue()==value){
					MessageUtil.alert("配送点不能重复选择");
					widget.setText("请选择配送点");
					return;
				}
				var middle = self.middleSiteWidget;
				if(middle&&middle.length>0){
					for(var i in middle){
						if(middle[i].id!=widget.id&&middle[i].getValue()&&middle[i].getValue()==value){
							MessageUtil.alert("配送点不能重复选择");
							widget.setText("请选择配送点");
							return;
						}
					}
				}
				
				$('#middleLinkName0').val(value.split('@#$%')[1]);
				$('#middleLinkPhone0').val(value.split('@#$%')[2]);
				self.getDistance(function(result){
					if(result.distance){
						self.getPriceBack(result.distance,function(price){
							$('#spanAdvicePrice').text(price.toFixed(2));
						});
					}
				});
				
			},
		});
		self.middleSiteWidget = new Array();
		self.siteIds = new Array();
		self.middleSiteWidget.push(middleSiteWidget0);
		self.siteIds.push(0);
		self.endSiteWidget=$("#endSiteWidget").ComboBoxWidget({
			url:"/DispatcherWeb/consignor/site/getSiteForSelect.json",
			textField:"name",
			valueField:"id",
			showText:"请选择终点",
			width:250,
			onSelected : function(value, text,widget) {
				//不能选相同站点
				if(self.startSiteWidget.getValue()&&self.startSiteWidget.getValue()==value){
					MessageUtil.alert("配送点不能重复选择");
					widget.setText("请选择终点");
					return;
				}
				if(self.middleSiteWidget&&self.middleSiteWidget.length>0){
					for(var i in self.middleSiteWidget){
						if(self.middleSiteWidget[i].getValue()&&self.middleSiteWidget[i].getValue()==value){
							MessageUtil.alert("配送点不能重复选择");
							widget.setText("请选择终点");
							break;
						}
					}
				}
				
				$('#endLinkName').val(value.split('@#$%')[1]);
				$('#endLinkPhone').val(value.split('@#$%')[2]);
				// 计算建议价格
				self.getDistance(function(result) {
					console.log("distance",result.distance);
					if (result.distance) {
						self.getPriceBack(result.distance, function(price) {
							$('#spanAdvicePrice').text(price.toFixed(2));
						});
					}
				});
			},
		});
		self.pickupTimeWidget=$('#pickupTimeWidget').TimeWidget({
			width:250,
			option:{
				startDate:new Date()
		}});
		self.finishTimeWidget=$('#finishTimeWidget').TimeWidget({
			width:250,
			option:{
				startDate:new Date()
		}});
		
		
	},
	_bind:function(){
		var self=this;
		$('#saveOrder').unbind('click').click(function(){
			self._saveOrder();
		});
		$('#unitSelect').change(function(){
			var _value = $(this).val();
			var text = $.trim($(this).find(':selected').text());
			$('#endUnit').val(text);
			for(var i in self.siteIds){
				$('#middleUnit'+self.siteIds[i]).val(text);
			}
		});
		
		$(document).on('click', '.icon-add', function(){
			var _this = $(this);
			//站点创建弹窗
			var win=WinWidget.create({
				title:"新增站点",
				width:"1120px",
				height:"500px",
				//content:"Hello World",
				url:"/dsp/consignor/order/orderSiteAdd.html"
			});
			win.onClose=function(data){
				var value=data.id+"@#$%"+data.linkName+"@#$%"+data.linkPhone+
				"@#$%"+data.coordinate+"@#$%"+data.districtId+"@#$%"+data.address;
				_this.parent().parent().find('select').append(
						"<option value='"+value+"'>"+data.name+"</option>")
						.find("option[value='"+value+"']").attr("selected",true);
				_this.parent().siblings('input[name=linkName]').val(data.linkName);
				_this.parent().siblings('input[name=linkPhone]').val(data.linkPhone);
			};
			window.orderSiteAdd=win;
		});
		
		$(document).on('click', '.iconfont-remove', function(){
			var middleUnitId=$(this).parent().prev().prev().attr('id');
			var idNum = middleUnitId.substr(middleUnitId.length-1,1);
			self.siteIds=$.grep(self.siteIds,function(n,i){
				return n!=idNum;
			});
			for(var w in self.middleSiteWidget){
				var id=self.middleSiteWidget[w].el.attr('id');
				console.log(id);
				if(id.substr(id.length-1,1)==idNum){
					self.middleSiteWidget=$.grep(self.middleSiteWidget,function(n,i){
						return n!=self.middleSiteWidget[w];
					});
					break;
				}
			}
			$(this).parent().parent().remove();
			//console.log(self.siteIds);
			//console.log(self.middleSiteWidget);
		});
		
		
		$('#addMiddleSite').click(function(){
			var i=0;
			if(self.siteIds.length>0){
				i = self.siteIds[self.siteIds.length-1]+1;
			}
			var _html='<li class="city-form-item clearfix">'+
				'<div class="city-form-tip">配送点名称：</div>'+
				'<span class="city-form-tap" style="width: auto;">'+
				'<div id="middleSiteWidget'+i+'"></div></span>'+	
				' <span class="city-form-tap w80">收货人：</span>'+
				' <input id="middleLinkName'+i+'" class="city-ipt-text w250" placeholder="收货人姓名">'+
				' <span class="city-form-tap w80">电话：</span>'+
				' <input id="middleLinkPhone'+i+'" class="city-ipt-text w250" placeholder="收货人电话">'+
				' <span class="city-form-tap w80"><i class="must-icon">*</i>卸货量：</span>'+
				' <input id="outturn" type="text" class="city-ipt-text w120" placeholder="请输入卸货量">'+
				' <input id="middleUnit'+i+'" class="city-ipt-text" style="width:35px" readonly="true" value="件">'+
				' <a class="address-add"><i class="icon icon-add"></i></a>'+
				' <a class="address-btn"><i class="iconfont iconfont-remove"></i></a></li>';
			$('#li_endSite').before(_html);
			self.siteIds.push(i);
			var middleSiteWidgeti=$("#middleSiteWidget"+i).ComboBoxWidget({
				url:"/DispatcherWeb/consignor/site/getSiteForSelect.json",
				textField:"name",
				valueField:"id",
				showText:"请选择配送点",
				width:250,
				onSelected:function(value,text, widget){
					//不能选相同站点
					if(self.startSiteWidget.getValue()&&self.startSiteWidget.getValue()==value){
						MessageUtil.alert("配送点不能重复选择");
						widget.setText("请选择配送点");
						return;
					}
					if(self.endSiteWidget.getValue()&&self.endSiteWidget.getValue()==value){
						MessageUtil.alert("配送点不能重复选择");
						widget.setText("请选择配送点");
						return;
					}
					var middle = self.middleSiteWidget;
					if(middle&&middle.length>0){
						for(var i in middle){
							if(middle[i].id!=widget.id&&middle[i].getValue()&&middle[i].getValue()==value){
								MessageUtil.alert("配送点不能重复选择");
								widget.setText("请选择配送点");
								return;
							}
						}
					}
					
					
					$('#middleLinkName'+i).val(value.split('@#$%')[1]);
					$('#middleLinkPhone'+i).val(value.split('@#$%')[2]);
					//计算建议价格
						self.getDistance(function(result){
							if(result.distance){
								self.getPriceBack(result.distance,function(price){
									$('#spanAdvicePrice').text(price.toFixed(2));
								});
							}
						});
				},
			});
			self.middleSiteWidget.push(middleSiteWidgeti);
			//console.log(self.siteIds);
		});
	},
		
	_saveOrder:function(){
		var self=this;
		var param = {};
		
		//货物类型
		if(self.goodsTypeWidget.getValue()){
			param.cargoType = parseInt(self.goodsTypeWidget.getValue());
		}else{
			MessageUtil.alert("请选择货物类型");
			return;
		}
		
		//车辆类型
		if(self.vehicleTypeWidget.getValue()){
			param.vehicleType = parseInt(self.vehicleTypeWidget.getValue());
		}else{
			MessageUtil.alert("请选择车辆类型");
			return;
		}
		//运输总量
		if(!!$('#totalTraffic').val()&&$('#totalTraffic').val()!='0'){
			switch($('#unitSelect').val()){//TODO
			case 'piece': param.amount = parseInt($('#totalTraffic').val());break;
			case 'side': param.volume = parseFloat($('#totalTraffic').val());break;
			case 'ton':param.weight = parseFloat($('#totalTraffic').val());break;
			default: MessageUtil.alert("运输总量单位出错");return;
			}
		}else{
			MessageUtil.alert("请输入运输总量");
			return;
		}
		var sites = new Array();
		//起点信息
		var startPoint = {};
		var startSite = self.startSiteWidget.getValue();
		if(startSite){
			var arr = startSite.split('@#$%');
			param.startSiteId = parseInt(arr[0]);
			param.senderName = arr[1];
			param.senderPhone = arr[2];
			param.districtId = arr[4];
			var coordinate = arr[3];
			startPoint.lng = coordinate.split(",")[0];
			startPoint.lat = coordinate.split(",")[1];
			
			var startSiteBean = {
					siteId : parseInt(arr[0]),
					linkMan : arr[1],
					linkPhone : arr[2],
					coordinate : arr[3],
					address : arr[5],
					siteType : 1,
					idxNo : 0,
					name : self.startSiteWidget.getText()
				};
			sites.push(startSiteBean);
		}else{
			MessageUtil.alert("请选择起点");
			return;
		}
		
		
		//要求提货时间
		var pickupTime = self.pickupTimeWidget.getValue();
		//预计到达时间
		var arriveTime = self.finishTimeWidget.getValue();
		if(!pickupTime){
			MessageUtil.alert("请填写提货时间");
			return;
		}
		if(!arriveTime){
			MessageUtil.alert("请填写送达时间");
			return;
		}
		if(pickupTime>=arriveTime){
			MessageUtil.alert("送达时间必须大于提货时间");
			return;
		}
		param.pickupTime = pickupTime;
		param.arriveTime = arriveTime;
		param.orderStatus = 10; //订单状态，未发布状态
		//备注
		if($('#orderBz').val()){
			param.remark = $('#orderBz').val();
		}
		
		//中途配送点信息
		var wayPoints = null;
		var totalUpload=0;
		var j=1;
		if(self.middleSiteWidget&&self.middleSiteWidget.length>0){//有配送点
			wayPoints = new Array();
			for ( var i in self.middleSiteWidget) {
				if (self.middleSiteWidget[i].getValue()) {
					var widget = self.middleSiteWidget[i];
					var arr = widget.getValue().split("@#$%");
					$('#unitSelect').val();
					var site = {
						siteId : parseInt(arr[0]),
						linkMan : arr[1],
						linkPhone : arr[2],
						coordinate : arr[3],
						address : arr[5],
						siteType : 2,
						idxNo : j,
						name : widget.getText()
					}
					var outturn = widget.el.parent().parent().children('#outturn').val();
					//console.log(widget.el);
					//console.log(outturn);
					if(!outturn||outturn==0){
						MessageUtil.alert("请填写卸货量");
						return;
					}
					totalUpload += parseInt(outturn);
					switch($('#unitSelect').val()){
					case 'piece': site.uploadNum = parseInt(outturn);break;
					case 'side': site.uploadVolume = parseFloat(outturn);break;
					case 'ton':site.uploadWeight = parseFloat(outturn);break;
					default: MessageUtil.alert("卸货量单位出错");return;
					}
					j++;
					sites.push(site);
					var wayPoint = new BMap.Point(arr[3].split(',')[0],arr[3].split(',')[1]);
					wayPoints.push(wayPoint);
				}
			}
		}
		//终点信息
		var endPoint = {};
		var endSiteValue = self.endSiteWidget.getValue();
		var endSiteBean=null;
		if(endSiteValue){
			var arr = endSiteValue.split('@#$%');
			param.endSiteId = parseInt(arr[0]);
			param.senderName = arr[1];
			param.senderPhone = arr[2];
			param.districtId = arr[4];
			var coordinate = arr[3];
			endPoint.lng = coordinate.split(",")[0];
			endPoint.lat = coordinate.split(",")[1];
			
			
			var endSiteBean = {
					siteId : parseInt(arr[0]),
					linkMan : arr[1],
					linkPhone : arr[2],
					coordinate : arr[3],
					address : arr[5],
					siteType : 3,
					idxNo : j,
					name : self.endSiteWidget.getText()
				};
			var outturn = $('#endOutturn').val();
			if(!outturn||outturn==0){
				MessageUtil.alert("请填终点写卸货量");
				return;
			}
			totalUpload += parseInt(outturn);
			switch($('#unitSelect').val()){
			case 'piece': endSiteBean.uploadNum = parseInt(outturn);break;
			case 'side': endSiteBean.uploadVolume = parseFloat(outturn);break;
			case 'ton':endSiteBean.uploadWeight = parseFloat(outturn);break;
			default: MessageUtil.alert("卸货量单位出错");return;
			}
			sites.push(endSiteBean);
		}else{
			MessageUtil.alert("请选择终点");
			return;
		}
		if(totalUpload!=(param.amount||param.volume||param.weight)){
			MessageUtil.alert("总运输量和总卸货量不符,请重新输入分配卸货量");
			return;
		}
		
		param.sites = sites;

		
		//订单总价
		var feeType = $("input[name=cost]:checked").val();
		var fee = 0;
		if(feeType==1){
			fee = $('#inputPrice').val();
			if(!fee){
				MessageUtil.alert("请输入自定义订单总价");
				return;
			}
		}else{
			fee = $("#spanAdvicePrice").text();
			if(!fee){
				self.getAdivsePrice({
					vehicleTypeId:param.vehicleType,
					cityId:param.districtId.substr(0,param.districtId.length-2)+'00',
					distance:self.distance
				},function(price){
					fee = price;
				});
			}
			if(!fee){
				MessageUtil.alert("建议价格计算失败，请选择自定义价格");
				return;
			}
		}
		param.feeType=feeType;
		param.fee = parseFloat(fee);
		param.distance = self.distance;
		console.log("param",param);
		CommonUtils.async({
			url:"/DispatcherWeb/consignor/order/create.json",
			data:param,
			contentType:'json',
			success:function(result){
				if(result.code==0){
					//成功创建订单信息
					MessageUtil.info("创建成功");
				}else{
					MessageUtil.alert("创建失败");
				}
			},
			error:function(result){
				MessageUtil.alert("创建失败");
			}
		});
	},
	//获取建议价格
	getAdivsePrice:function(param,callback){
		CommonUtils.async({
			url:"/DispatcherWeb/consignor/order/calculatePrice.json",
			data:param,
			success:function(result){
				if(result.code==0&&result.data){
					typeof callback==='function'&&callback(result.data);
				}else{
					console.info("获取建议价格失败！");
				}
			},
			error:function(result){
				console.info("获取建议价格失败！");
			}
		});
	},
	//-	获取百度地图规划路线
	getDrivingDistance : function(data, callback) {
		var self = this;
		var startpoint =new BMap.Point(data.startPoint.lng,data.startPoint.lat);
		var endpoint =new BMap.Point(data.endPoint.lng,data.endPoint.lat);
		var transit = new BMap.DrivingRoute(startpoint, {
			onSearchComplete : function(results) {
				if (transit.getStatus() != BMAP_STATUS_SUCCESS){
					return ;
				}
				var plan = results.getPlan(0); //默认第一个规划优先
				//var duration= plan.getDuration(false);   //获取时间 单位秒
				var distance= plan.getDistance(false);   //获取距离  单位米
				var result = {
						//duration:duration,
						distance:distance/1000  //换算成公里
				}
				if(!!distance){
					self.distance = distance/1000;
				}
				typeof callback=== 'function'&& callback(result);
			}
		});
		//option:{startCity:String,endCity:string,waypoints:array } waypoints途径点集合最多10个
		if(data.option){
			transit.search(startpoint,endpoint,data.option);
		}else{
			transit.search(startpoint,endpoint);
		}
		
	},
	getDistance:function(callback){
		console.log("1111111111111111");
		var self = this;
		//起点信息
		var startPoint = {};
		var endPoint = {};
		var startSite = self.startSiteWidget.getValue();
		var endSite = self.endSiteWidget.getValue();
		if(startSite&&endSite){
			var coordinate = startSite.split('@#$%')[3];
			startPoint.lng = coordinate.split(",")[0];
			startPoint.lat = coordinate.split(",")[1];
			var coordinate1 = endSite.split('@#$%')[3];
			endPoint.lng = coordinate1.split(",")[0];
			endPoint.lat = coordinate1.split(",")[1];
			var _data = {
					startPoint:startPoint,
					endPoint:endPoint
			};
			var wayPoints = null;
			if(self.middleSiteWidget&&self.middleSiteWidget.length>0){//有配送点
				wayPoints = new Array();
				for ( var i in self.middleSiteWidget) {
					if (self.middleSiteWidget[i].getValue()) {
						var widget = self.middleSiteWidget[i];
						var arr = widget.getValue().split("@#$%");							
						var wayPoint = new BMap.Point(arr[3].split(',')[0],arr[3].split(',')[1]);
						wayPoints.push(wayPoint);
					}
				}
			}
			
			if(wayPoints&&wayPoints.length>0){
				_data.option = {
						waypoints:wayPoints
				}
			}
			
			self.getDrivingDistance(_data,callback);
		}
	},
	getPriceBack:function(distance,callback){
		var self = this;
		var vehicleTypeId = self.vehicleTypeWidget.getValue();
		var cityId=null;
		if(self.startSiteWidget.getValue()){
			var districtId = self.startSiteWidget.getValue().split('@#$%')[4];
			cityId = districtId.substr(0,districtId.length-2)+"00";
		}
		var param={
			vehicleTypeId:vehicleTypeId,
			cityId:cityId,
			distance:distance
		};
		console.log("priceParam",param);
		if(vehicleTypeId&&cityId){
			self.getAdivsePrice(param,callback);
		}
	}

}
$(function(){
	OrderAdd.init();
});