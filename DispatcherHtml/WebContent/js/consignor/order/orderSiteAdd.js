var OrderSiteAdd={
	mapWidget:null, //地图控件
	localSearch:null, //检索函数
	position:{},//地址信息对象
	
	
	init:function(){
		var self=this;
		self._render();
		self._bind();
	},
	_render:function(){
		var self = this;
		self.mapWidget=$("#allmap").MapWidget({initPoint:{
			lon:113.953316,
			lat:22.558688
		}});
		self.localSearch = new BMap.LocalSearch(self.mapWidget.map, {
			onSearchComplete: function(res){
				if(res && res.zr){
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
		$('#cancel').click(function(){
			window.ordersSiteAdd.close();
		});
	},
	
	_makerBind:function(point){
		var self = this;
		var _point = {
				lng:113.953316,
				lat:22.558688
		};
		point=point||_point;
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
			});
		});
	},
	
	_saveSite:function(){
		var self=this;
		var name = $("#siteName").val();
		if(!name){
			MessageUtil.alert("名称不能为空");
			return;
		}
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
		var address=$('#address').val();
		if(!address){
			MessageUtil.alert("地址不能为空");
			return;
		}
		var param={
				name:name,
				linkMan:linkMan,
				linkPhone:linkPhone,
				linkId:linkId,
				address:address
		}
		
		param = $.extend({},param,self.position);
		CommonUtils.async({
			url:"/DispatcherWeb/consignor/site/create.json",
			data:param,
			success:function(result){
				//console.log(result);
				if(result.code==0){
					//成功创建站点信息
					MessageUtil.info("创建成功");
					var _data={
							name:name,
							id:result.data.id,
							linkName:linkMan,
							linkPhone:linkPhone,
							coordinate:param.coordinate,
							districtId:result.data.districtId,
							address:address
					};
					//console.log(_data);
					window.orderSiteAdd.onClose(_data);
					window.orderSiteAdd.close();
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
		console.log(string);
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
	OrderSiteAdd.init();
});