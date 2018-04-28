var SiteAdd={
	deptWidget:null, //部门控件
	mapWidget:null, //地图控件
	//marker:null, //标注点对象
	localSearch:null, //检索函数
	//geocoder:null, //地址解析对象
	position:{},//地址信息对象
	
	
	init:function(){
		var self=this;
		self._render();
		self._bind();
	},
	_render:function(){
		var self = this;
		
		//设置底部菜单连接
		Footer.nav("站点管理","consignor/site/siteList.html");
		
		self.deptWidget=$("#deptWidget").DeptWidget({});
		self.mapWidget=$("#allmap").MapWidget({initPoint:{
			lon:113.953316,
			lat:22.558688
		}});
		//self.geocoder = new BMap.Geocoder(); // 创建地址解析对象
		self.localSearch = new BMap.LocalSearch(self.mapWidget.map, {
			onSearchComplete: function(res){
				if(res && res.zr){
					//typeof callback === 'function' && callback(res.zr);
					//console.log(res.zr);
					var comment = '';
					for(var i in res.zr){
						comment += "<li class='li' value-lng='"+res.zr[i].point.lng+"'" +
								"value-lat='"+res.zr[i].point.lat+"'>" +
								res.zr[i].title+"</li>";
					}
					if(comment)
					$('#search_result').empty();
					$('#search_result').append(comment).parent().css('display','block');
					$('#search_result li').click(function(){
						var _this = $(this);
						$('#search_result').empty().parent().css('display','none');
						var lng=_this.attr('value-lng');
						var lat=_this.attr('value-lat');
						$('#address').val(_this.html());
						self.mapWidget.map.clearOverlays();
						//self.marker=new BMap.Marker(new BMap.Point(lng, lat)); // 创建标注点
						self._makerBind({
							lng:lng,
							lat:lat
						});
						self.mapWidget.locationPoint({
							lng:lng,
							lat:lat
						},function(result){
							self.position={
									coordinate:result.point.lng+','+result.point.lat,
									province:result.addressComponents.province,
									city:result.addressComponents.city,
									district:result.addressComponents.district
							};
						});
						/*self.geocoder.getLocation(new BMap.Point(lng,lat), function(result){
							//console.log(result);
							self.position={
									coordinate:result.point.lng+','+result.point.lat,
									province:result.addressComponents.province,
									city:result.addressComponents.city,
									district:result.addressComponents.district
							};
						})*/
					});
				}
			}
		});
	},
	_bind:function(){
		var self=this;
		self._makerBind();
		$('#address').bind('input', function(){
			var val = this.value;
			self.localSearch.search(val);
		});
		$("#saveSite").unbind('click').click(function(){
			self._saveSite();
		});
		//联系人信息自动联想
		self._getLink(function(data){
			//console.log(data);
			if(data&&data.name)
			$.selectSuggest('linkMan',data.name,function(){
				var id = this.id;
				console.log(id);
				$('#linkId').val(this.id);
				for(var i in data.phone){
					if(data.phone[i].id==id){
						$('#linkPhone').val(data.phone[i].text);
					}
				}
			});
			if(data&&data.phone)
			$.selectSuggest('linkPhone',data.phone,function(){
				var id = this.id;
				//console.log(id);
				$('#linkId').val(this.id);
				for(var i in data.name){
					if(data.name[i].id==id){
						$('#linkMan').val(data.name[i].text);
					}
				}
			});
		});	
	},
	
	_makerBind:function(point){
		var self = this;
		var _point = {
				lng:113.953316,
				lat:22.558688
		};
		/*var render = function(){
			var icon={
					url:'/images/map_icon05.png',
					width:35,
					height:35
			};
			return icon;
		};*/
		point=point||_point;
		//console.log(point);
		self.mapWidget.addMarker({
			point:point
		},function(marker){
			marker.addEventListener('dragend',function(event){//添加拖拽完成时监听事件
				//console.log(event);
				self.mapWidget.locationPoint(event.point,function(result){
					$('#address').val(result.address);
					self.position={
							coordinate:result.point.lng+','+result.point.lat,
							province:result.addressComponents.province,
							city:result.addressComponents.city,
							district:result.addressComponents.district
					};
				});
				/*self.geocoder.getLocation(new BMap.Point(event.point.lng, event.point.lat), function(result){
					//console.log(result);
					$('#address').val(result.address);
					self.position={
							coordinate:result.point.lng+','+result.point.lat,
							province:result.addressComponents.province,
							city:result.addressComponents.city,
							district:result.addressComponents.district
					};
				})*/
			});
		});
		/*var _p=new BMap.Point(point.lng, point.lat);
		self.marker=new BMap.Marker(_p); // 创建标注点
		self.mapWidget.map.panTo(_p); //跳转到标注点
		self.mapWidget.map.addOverlay(self.marker);
		setTimeout(function(){
			self.marker.enableDragging(); //可拖拽
			self.marker.addEventListener('dragend',function(event){//添加拖拽完成时监听事件
				//console.log(event);
				self.geocoder.getLocation(new BMap.Point(event.point.lng, event.point.lat), function(result){
					//console.log(result);
					$('#address').val(result.address);
					self.position={
							coordinate:result.point.lng+','+result.point.lat,
							province:result.addressComponents.province,
							city:result.addressComponents.city,
							district:result.addressComponents.district
					};
				})
				
			});
		}, 0);*/
	},
	
	_saveSite:function(){
		var self=this;
		var name = $("#siteName").val();
		if(!name){
			MessageUtil.alert("名称不能为空");
			return;
		}
		var deptId=self.deptWidget.getValue();
		var linkMan = $("#linkMan").val();
		if(!linkMan){
			MessageUtil.alert("联系人姓名不能为空");
			return;
		}
		var linkPhone = $("#linkPhone").val();
		if(!linkPhone){
			MessageUtil.alert("联系人电话不能为空");
			return;
		}
		var linkId = $('#linkId').val();
		//var coordinate = self.position.coordinate;
		var address=$('#address').val();
		//address='广东省深圳市南山区松坪山新东路1号清华信息港';
		if(!address){
			MessageUtil.alert("地址不能为空");
			return;
		}
		//var province='广东省';
		//var city='深圳市';
		//var district='南山区';
		
		var param={
				name:name,
				deptId:deptId,
				linkMan:linkMan,
				linkPhone:linkPhone,
				linkId:linkId,
				//coordinate:coordinate,
				address:address,
				//province:province,
				//city:city,
				//district:district
		}
		
		param = $.extend({},param,self.position);
		//console.log("position",self.position);
		//console.log("param",param);
		CommonUtils.async({
			url:"/DispatcherWeb/consignor/site/create.json",
			data:param,
			success:function(result){
				if(result.code==0){
					//成功创建站点信息
					MessageUtil.info("创建成功");
				}else{
					MessageUtil.alert("创建失败");
				}
			},
			error:function(result){
				MessageUtil.info("创建失败！");
			}
		});
	},
	_getLink:function(callback){
		var param = {};
		CommonUtils.async({
			url:"/DispatcherWeb/consignor/site/getLink.json",
			data:param,
			//contentType:'json',
			success:function(result){
				if(result.code==0){
					//成功获取联系人信息
					var link=result.data;
					//console.info(link);
					if(link){
						var nameData=new Array(link.length);
						var phoneData=new Array(link.length);
						for(var i=0;i<link.length;i++){
							
							nameData[i]={
								id:link[i].linkId,
								text:link[i].name
							};
							phoneData[i]={
								id:link[i].linkId,
								text:link[i].phone
							}
						}
						//console.log(nameData);
						//console.log(phoneData);
						if(nameData&&phoneData){
							var data={
									name:nameData,
									phone:phoneData
							};
							//console.info(data);
							typeof callback === 'function' && callback(data);
						}
					}
					
				}
			},
			error:function(result){
				console.log(result);
				console.log("获取常用联系人失败!");
			}
		});	
	},
	//-	位置检索、周边检索和范围检索
	localSearch: function(string, callback){
		var localSearch = new BMap.LocalSearch(this.map, {
			onSearchComplete: function(res){
				if(res && res.zr){
					typeof callback === 'function' && callback(res.zr);
				}
			}
		});
		localSearch.search(string);
	}

}
$(function(){
	SiteAdd.init();
});