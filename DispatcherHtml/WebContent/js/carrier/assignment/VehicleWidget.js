/*
 * var t=$("id1").TableWidget({
 * 		url:"/xxx/ddd/cc.json",
 * 		param:{a:1,b:2},
 * 		//data:[],//与url互斥
 * 		cells:[{
 * 			field:"",
 * 			text:"",
 * 			render:function(value,row){
 * 				return "";
 * 			}
 * 		}],
 * 		showPagination:false
 * 		pageSize:10
 * });
 * 
 * */

(function(){
	var _table={
		id:null,//控件本身Id
		el:null,//控件容器dom
		config:{},
		_defaultPageSize:20,
		param:null,//缓存每次的查询参数
		pageCount:0,
		
		
		init:function(c){
			var self=this;
			var _c=c||{};
			self.config=_c;
			
			self._render();
			self._bind();
		},
		_render:function(){
			var self=this;
			var c=self.config;
			
			var cells=c.cells||[];
			
			// <!-- 车辆明细列表 -->
			var _html = '<div class="pop-main-content clearfix" id="tbody_'+self.id +'"></div>';
			
			var _footer='<div class="city-table-pages clearfix">'
				+'<div class="city-pages-left fl">'
				+'共 <span id="totalCount_'+self.id+'">0</span>条</span>'
				+'</div>'
				+'<div class="city-pages-right fr">'
				+'	<ul id="pageNums_'+self.id+'" class="clearfix">'
				//具体页码动态生成
				+'		<li id="prev_'+self.id+'" class="prev fl hide"><a href="javascript:;" title="上一页"><i class="iconfont iconfont-prev"></i></a></li>'
				//+'		<li class="page fl active"><a href="javascript:;">1</a></li>'
				+'		<li id="next_'+self.id+'" class="next fl hide"><a href="javascript:;" title="下一页"><i class="iconfont iconfont-next"></i></a></li>'
				+'	</ul>'
				+'</div>'
				+'</div>';
			var _form = 
				'<form id="assignForm" style="display:none">                     '+
				'	<input id="orderNo" name="orderNo" type="hidden"></input>    '+
				'	<input id="vehicleId" name="vehicleId" type="hidden"></input>'+
				'	<input id="driverId" name="driverId" type="hidden"></input> '+
				'</form>                                                         ';
			_html += _footer;
			_html += _form;
			$(self.el).html(_html);
			
			setTimeout(function(){
				self._afterRender();
			},200);
		},
		_bind:function(){
			var self=this;
			//绑定每个页码的点击事件
			$(self.el).on("click", ".page", function(){ 
				//此处的$(this)指$( "#testDiv")，而非$(document) 
				var _pageNum=$(this).text();
				self.gotoPageNum(_pageNum*1);
			});
			
			//上一页
			$("#prev_"+self.id).unbind("click").click(function(){
				//获取当前页
				var _currentPageNum=$("#pageNums_"+self.id).find(".active").text();
				//alert(_currentPageNum);
				_currentPageNum=_currentPageNum*1;
				var _prevPageNum=_currentPageNum-1;
				if(_prevPageNum<1){
					_prevPageNum=1;
				}
				self.gotoPageNum(_prevPageNum);
			});
			//下一页
			$("#next_"+self.id).unbind("click").click(function(){
				//获取当前页
				var _currentPageNum=$("#pageNums_"+self.id).find(".active").text();
				//alert(_currentPageNum);
				_currentPageNum=_currentPageNum*1;
				var _nextPageNum=_currentPageNum+1;
				if(_nextPageNum>self.pageCount){
					_nextPageNum=self.pageCount;
				}
				self.gotoPageNum(_nextPageNum);
			});
			
		},
		_getPageSize:function(){
			var self=this;
			var c=self.config;
			var _pageSize=c.pageSize;
			if(!_pageSize){
				_pageSize=self._defaultPageSize;
			}else if(_pageSize!=10&&_pageSize!=20&&_pageSize!=50&&_pageSize!=100&&_pageSize!=200){
				_pageSize=self._defaultPageSize;
			}
			return _pageSize;
		},
		_afterRender:function(){
			var self=this;
			var c=self.config;
			var _param=c.param||{};
			if(c.data){
				//优先加载data
				self.loadData(c.data);
			}else if(c.url){
				self.loadUrl(_param);
			}
		},
		/**
		 * 只负责查询
		 */
		loadUrl:function(pa){
			var self=this;
			var c=self.config;
			var _param=pa||{};
			
			//如果false则不穿page参数
			if(self.showPagination!=false){
				//没有页码，则默认是第一页
				if(!_param.pageNum){
					_param.pageNum=1;
				}
				//如果没有页大小，则默认是20条
				if(!_param.pageSize){
					_param.pageSize=self._getPageSize();
				}
			}
			var _type=c.type||"POST";
			CommonUtils.async({
				url:c.url,
				type:_type,
				data:_param,
				success:function(result){
					if(result.code==0){
						var ret=result.data||{};
						var total=ret.total||0;
						var list=ret.list||[];
						var extParam=null;
						if(c.extParam){
							extParam=ret[c.extParam];
						}
						
						self.loadData(list,extParam);
						if(self.showPagination==false){
							//如果是false，则不设置分页器
						}else{
							//删除页码
							$(".page").remove();
							self.intPagination(_param.pageNum,total);
						}
					}
				}
			});
			
		},
		loadData:function(list,extParam){
			var self=this;
			var c=self.config;
			var cells=c.cells||[];
			
			var hasCheckbox='';
			if(c.showSelect==false){
				hasCheckbox='hide';
			}
			
			var hasNum='hide';
			if(c.showNum==true){
				hasNum='';
			}
			
			var _html='';
			for(var i=0,len=list.length;i<len;i++){
				var vehicle = list[i];
				var vehicleId = vehicle.vehicleId;
				var driverId = vehicle.driverId;
				
				var onlineStatus = '离线';
				if(vehicle.onlineStatus == 1){
					onlineStatus = '在线';
				}
				var loadRate = parseInt(vehicle.loadRate);
				var loadStatus = "";
				if(loadRate == 0){
					loadStatus = "空载";
				}
				else if(loadRate > 0 && loadRate < 90){
					loadStatus = "半载";
				}
				else if(loadRate > 90){
					loadStatus = "满载";
				}
				_html += 
					'<div class="pop-main-select-item" id="vehicle_'+ vehicleId +'" vehicleId="'+ vehicleId +'" driverId="'+ driverId +'">'+
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
			}
			$("#tbody_"+self.id).html(_html);
			$(".pop-main-select-item").unbind("click").click(function(){
				if(!$(this).hasClass("active")){
					$(".pop-main-select-item").removeClass('active');
					$(this).addClass('active');
					console.log($(this).attr("vehicleId"));
					console.log($(this).attr("driverId"));
					$("#assignForm").find("#vehicleId").val($(this).attr("vehicleId"));
					$("#assignForm").find("#driverId").val($(this).attr("driverId"));
				}
			});
			
		},
		intPagination:function(currentPageNum,total){
			var self=this;
			//设置总条数
			$("#totalCount_"+self.id).html(total);
			var _pageSize=self._getPageSize();
			//根据总行数和页大小初始化分页器
			var pageCount=Math.ceil((total/_pageSize));
			self.pageCount=pageCount;
			currentPageNum=currentPageNum*1;
			
			if(currentPageNum<=5){
				//前5条
				//没有上一页
				$("#prev_"+self.id).addClass("hide");
				var hasMorePage=false;
				var showCount=pageCount;//最多显示10个页码
				if(showCount>10){
					showCount=10;
					hasMorePage=true;
				}
				
				var _html='';
				for(var i=1;i<=showCount;i++){
					var _active='';
					if(i==currentPageNum){
						_active='active';
					}
					_html+='<li class="page fl '+_active+'"><a href="javascript:void(0);">'+i+'</a></li>';
				}
				$("#prev_"+self.id).after(_html);
				//如果还有更多的页，则显示下一页按钮
				if(hasMorePage){
					$("#next_"+self.id).removeClass("hide");
				}
			}else if(currentPageNum>=(pageCount-5)){
				//倒数第6个时
				//没有下一页按钮
				$("#next_"+self.id).addClass("hide");
				var e=pageCount;
				var s=pageCount-9;
				var hasPre=false;
				if(s<1){
					s=1;
				}
				if(s==1){
					hasPre=false;
				}else{
					hasPre=true;
				}
				var _html='';
				for(var i=s;i<=e;i++){
					var _active='';
					if(i==currentPageNum){
						_active='active';
					}
					_html+='<li class="page fl '+_active+'"><a href="javascript:void(0);">'+i+'</a></li>';
				}
				$("#prev_"+self.id).after(_html);
				if(hasPre){
					$("#prev_"+self.id).removeClass("hide");
				}else{
					$("#prev_"+self.id).addClass("hide");
				}
			}else{
				//中间的页码
				var s=currentPageNum-4;
				var e=currentPageNum+5;
				if(s<1){
					s=1;
				}
				if(e>pageCount){
					e=pageCount;
				}
				if(s==1){
					$("#prev_"+self.id).addClass("hide");
				}else{
					$("#prev_"+self.id).removeClass("hide");
				}
				if(e==pageCount){
					$("#next_"+self.id).addClass("hide");
				}else{
					$("#next_"+self.id).removeClass("hide");
				}
				var _html='';
				for(var i=s;i<=e;i++){
					var _active='';
					if(i==currentPageNum){
						_active='active';
					}
					_html+='<li class="page fl '+_active+'"><a href="javascript:void(0);">'+i+'</a></li>';
				}
				$("#prev_"+self.id).after(_html);
			}
			
		},
		/**
		 * 用于翻页
		 * @param pageNum
		 * @returns
		 */
		gotoPageNum:function(pageNum){
			var self=this;
			//用上次的查询条件
			var c=self.config;
			var _param=self.param||{};
			_param.pageNum=pageNum;
			self.loadUrl(_param);
		},
		/**
		 * 用于刷新
		 */
		reload:function(){
			var self=this;
			var c=self.config;
			//清空上次查询的参数
			self.param={};
			var _param={};
			_param.pageNum=1;
			
			self.loadUrl(_param);
		},
		/**
		 * 用于选择条件查询
		 */
		query:function(param){
			var self=this;
			//替换上次查询参数
			var c=self.config;
			var _param=param||{};
			self.param=_param;
			_param.pageNum=1;
			self.loadUrl(_param);
		},
		
		
	}
	
	//封装成jquery的控件
	$.fn.VehicleWidget=function(options){
		//生成uuid
		var uuid=(new Date()).getTime();
		var r=(Math.random()+"").replace(".","");
		uuid+=r;
		
		var _o=$.extend({},_table);//后面的覆盖前面的
		_o.id="t"+uuid;
		_o.el=this;
		
		var _c=$.extend({},options,$.fn.TableWidget.defaults);//后面的覆盖前面的
		_o.init(_c);
		return _o;
    }
})();