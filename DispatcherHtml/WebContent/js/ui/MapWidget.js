/*
// * 	地图控件
 *	百度地图 
 * */
(function(){
	var _map={
		id:null,
		el:null,
		config:null,
		allPoints:{},//缓存地图上所有的点的索引
		allLines:{},//缓存地图上所有的线的索引
		
		map:null,//原生地图变量
		defaultInitPoint:{lon:116.404,lat:39.915},//初始化地图时，默认的中心点
		loadOK:false,
		geocoder:null, //一个地址解析对象
		
		init:function(c){
			var self=this;
			var _c=c||{};
			self.config=_c;
			//异步加载地图
			//self.loadJScript();
			self._initMap();
			
			self._render();
			self._bind();
		},
		/**
		 * 初始化地图
		 */
		_initMap:function(){
			var self=this;
			var c=self.config;
			
			self.map=new BMap.Map(self.el.attr('id'),{
				enableMapClick: false //禁止弹出百度自己的信息窗口
			});// 创建Map实例,最好固定是这个Id
			if(c.initPoint){
				//初始化中心点
				var point=new BMap.Point(c.initPoint.lon, c.initPoint.lat); // 创建点坐标
				self.map.centerAndZoom(point,15);
			}else{
				self.getLocation(function(location){
					var point=new BMap.Point(location.lon, location.lat); // 创建点坐标
					self.map.centerAndZoom(point,15);
				});
			}
			/*var point=new BMap.Point(_initPoint.lon, _initPoint.lat); // 创建点坐标
			self.map.centerAndZoom(point,15);*/
			self.map.enableScrollWheelZoom();
			
			self.map.addEventListener("load", function() {
				self.loadOK=true;
			});
		},
		_render:function(){
			var self=this;
			var c=self.config;
			var cid=$(self.el).attr("id");
			
		},
		_bind:function(){
			var self=this;
			
		}
		
	}
	
	//对外提供的api
	var _api={
		/**
		 * 获取地图上所有的点，为了给播放控件使用
		 */
		getAllPoints:function(){
			var self=this
			return self.allPoints||{};
		},
		/**
		 * 清除地图上所有覆盖物
		 */
		clear:function(){
			var self=this;
			self.map.clearOverlays();
			//清除索引
			self.allPoints={};
			self.allLines={};
		},
		/**
		 * 删除某个点
		 * point {lon:113.362,lat:29.365}
		 */
		deletePoint:function(point){
			var self=this;
			//清除索引
			//delete self.allPoints[point.id]
		},
		/**
		 * 删除某条线
		 * line [{lon:113.69874,lat:29.6587},{lon:113.69874,lat:29.6587},{lon:113.69874,lat:29.6587},.....]
		 */
		deleteLine:function(line){
			var self=this;
			
		},
		/**
		 * point {id,lon,lat} 经纬度
		 * render function 渲染点的具体方式，默认是地图本身的画点方式
		 */
		addPoint:function(point,render){
			var self=this;
			
			var icon=null;
			if(render){
				icon=render();
			}
			var marker=null;
			if(icon){
				var icon=new BMap.Icon(icon.url, new BMap.Size(icon.width,icon.height));
				marker=new BMap.Marker(new BMap.Point(point.lon, point.lat),{icon:icon}); // 创建点
			}else{
				marker=new BMap.Marker(new BMap.Point(point.lon, point.lat)); // 创建点
			}
			self.map.addOverlay(marker);
			//添加到索引
			self.allPoints[point.id]=point;
		},
		/**
		 * 一个点的数组
		 * line [{lon:113.14562,lat:29.56852},{lon:113.14562,lat:29.56852}]
		 * render function 渲染线的具体方式，默认是地图本身的画线方式
		 */
		addLine:function(line,render){
			var self=this;
			var _arr=[];
			for(var i=0,len=line.length;i<len;i++){
				var point=line[i];
				if(!self.allPoints[point.id]){
					//不存在，则放索引表加一个
					self.allPoints[point.id]=point;
				}
				
				var _p=new BMap.Point(point.lon, point.lat);
				_arr.push(_p);
			}
			
			var config={
				strokeColor:"blue",//线的颜色
				strokeWeight:2,//线的大小
				strokeOpacity:0.5 //线的透明度
			};
			if(render){
				config=render();
			}
			
			if(_arr.length>1){
				var polyline=new BMap.Polyline(_arr,config);   //创建折线
				self.map.addOverlay(polyline);//增加折线
			}
		},
		/**
		 * point 圆心点 {lon:1113.6956,lat:2956}
		 * radius 半径
		 * render function 渲染圆的具体方式，默认是地图本身的画圆方式
		 */
		addCircle:function(point,radius,render){
			var self=this;
			var point=new BMap.Point(point.lon, point.lat);//圆心
			var config={
				strokeColor:"blue",//线的颜色
				strokeWeight:2,//线的大小
				strokeOpacity:0.5 //线的透明度
			};
			if(render){
				config=render();
			}
			var circle=new BMap.Circle(point,radius,config); //创建圆
			self.map.addOverlay(circle);//增加圆
		},
		/**
		 * points 多边形各个点的坐标[{lon:113.36985,lat:29.6875},{lon:113.36985,lat:29.6875},{lon:113.36985,lat:29.6875},......]
		 * render function 渲染多边形的具体方式，默认是地图本身的画多边形方式
		 */
		addPolygon:function(points,render){
			var self=this;
			var _arr=[];
			for(var i=0,len=points.length;i<len;i++){
				var point=points[i];
				var _p=new BMap.Point(point.lon, point.lat);
				_arr.push(_p);
			}
			var config={
				strokeColor:"blue",//线的颜色
				strokeWeight:2,//线的大小
				strokeOpacity:0.5 //线的透明度
			};
			if(render){
				config=render();
			}
			
			if(_arr.length>2){
				var polygon=new BMap.Polygon(_arr, config);  //创建多边形
				self.map.addOverlay(polygon);
			}
		},
		/**
		 * city  	城市
		 * district 区
		 */
		addBoundary:function(city,district,render){
			var self=this;
			
			var config={
				strokeColor:"blue",//线的颜色
				strokeWeight:2,//线的大小
				strokeOpacity:0.5 //线的透明度
			};
			if(render){
				config=render();
			}
				
			var bdary=new BMap.Boundary();
			bdary.get(city+district, function(rs){//获取行政区域
				//map.clearOverlays();//清除地图覆盖物       
				var count=rs.boundaries.length; //行政区域的点有多少个
				if(count===0){
					alert('未能获取当前输入行政区域');
					return ;
				}
	          	var pointArray=[];
				for(var i=0;i<count;i++){
					var ply=new BMap.Polygon(rs.boundaries[i],config); //建立多边形覆盖物
					self.map.addOverlay(ply);//添加覆盖物
					pointArray=pointArray.concat(ply.getPath());
				}    
				self.map.setViewport(pointArray);//调整视野  
			}); 
			
		},
		/**
		 * 唤起画笔
		 */
		invokeBrush:function(graphType,complete,render){
			var self=this;
			//先清除地图上所有的覆盖物
			self.map.clearOverlays();
			//实例化鼠标绘制工具
			if(!self.drawingManager){
				var config={
					strokeColor:"blue",    //边线颜色。
			        //fillColor:"red",      //填充颜色。当参数为空时，圆形将没有填充效果。
			        strokeWeight: 3,       //边线的宽度，以像素为单位。
			        strokeOpacity: 0.8,	   //边线透明度，取值范围0 - 1。
			        fillOpacity: 0.6,      //填充的透明度，取值范围0 - 1。
			        strokeStyle: 'solid' //边线的样式，solid或dashed。
				}
				
				if(render){
					config=render();
				}
				
				self.drawingManager=new BMapLib.DrawingManager(self.map, {
			        isOpen: false, //是否开启绘制模式
			        //enableDrawingTool: true, //是否显示工具栏
			        drawingToolOptions: {
			            anchor: BMAP_ANCHOR_TOP_RIGHT, //位置
			            offset: new BMap.Size(5, 5), //偏离值
			        },
			        circleOptions: config, //圆的样式
			        polylineOptions: config, //线的样式
			        polygonOptions: config, //多边形的样式
			        rectangleOptions: config //矩形的样式
			    });
				//绘画完成事件
				self.drawingManager.addEventListener('overlaycomplete', function(e){
					//关闭画笔功能
					self.drawingManager.close();
					
					var center=null;//中心点，多边形没有中心点
					if(e.overlay.getCenter){
						var c=e.overlay.getCenter();//中心点
						var r=e.overlay.getRadius();//半径
						center={
							lon:c.lng,
							lat:c.lat,
							radius:r
						}
					}
					
					var arr=e.overlay.getPath();//获取边框所有的点
					var list=[];
					for(var i=0,len=arr.length;i<len;i++){
						var p=arr[i];
						var pp={lon:p.lng,lat:p.lat};
						list.push(pp);
					}
					if(complete){
						complete(list,center);
					}
				});
				
			}
			
			if("rectangle"==graphType){
				//矩形
				self.drawingManager.setDrawingMode(BMAP_DRAWING_RECTANGLE);
			}else if("polygon"==graphType){
				//多边形
				self.drawingManager.setDrawingMode(BMAP_DRAWING_POLYGON);
			}else if("circle"==graphType){
				//圆形
				self.drawingManager.setDrawingMode(BMAP_DRAWING_CIRCLE);
			}
			//开启画笔功能
			self.drawingManager.open();
		},
		/**
		 * 给地图绑定一个事件
		 */
		bind:function(event,callback){
			var self=this;
			var _click=function(e){
				var point={lon:e.point.lng,lat:e.point.lat};
				if(callback){
					callback(point);
				}
			}
			self.map.addEventListener(event, _click);
		},
		/**
		 * 移除地图的绑定事件
		 */
		unbind:function(event,fn){
			var self=this;
			self.map.removeEventListener(event, fn);
		},
		/**
		 * 获取最新定位
		 */
		getLocation:function(callback){
			var self=this;
			
			var geolocation=new BMap.Geolocation();
			geolocation.getCurrentPosition(function(r){
				if(this.getStatus()==BMAP_STATUS_SUCCESS){
					//map.panTo(r.point);
					//alert('您的位置：'+r.point.lng+','+r.point.lat);
					if(callback){
						//console.info(r);
						/*
						 * 
						city : "深圳市"
						city_code : 0
						district : ""
						province : "广东省"
						street : ""
						street_number : ""
						 * */
						var address=r.address||{};
						var province=address.province;
						var city=address.city;
						var district=address.district;
//						/*
//						latitude : "22.54605355"
//						longitude : "114.02597366"
//						 * */
						var lon=r.longitude;
						var lat=r.latitude;
						
						var location={
							lon:lon,
							lat:lat,
							province:province,
							city:city,
							district:district
						};
						callback(location);
					}
				}else{
					//使用默认点
					var defaultInitPoint
					var location={
						lon:self.defaultInitPoint.lon,
						lat:self.defaultInitPoint.lat,
						province:"北京市",
						city:"北京市",
						district:""
					};
					callback(location);
					//alert('failed'+this.getStatus());
				}        
			},{enableHighAccuracy: true});
			//关于状态码
			//BMAP_STATUS_SUCCESS	检索成功。对应数值“0”。
			//BMAP_STATUS_CITY_LIST	城市列表。对应数值“1”。
			//BMAP_STATUS_UNKNOWN_LOCATION	位置结果未知。对应数值“2”。
			//BMAP_STATUS_UNKNOWN_ROUTE	导航结果未知。对应数值“3”。
			//BMAP_STATUS_INVALID_KEY	非法密钥。对应数值“4”。
			//BMAP_STATUS_INVALID_REQUEST	非法请求。对应数值“5”。
			//BMAP_STATUS_PERMISSION_DENIED	没有权限。对应数值“6”。(自 1.1 新增)
			//BMAP_STATUS_SERVICE_UNAVAILABLE	服务不可用。对应数值“7”。(自 1.1 新增)
			//BMAP_STATUS_TIMEOUT	超时。对应数值“8”。(自 1.1 新增)
			
		},
		/**
		 * 设置当前位置
		 */
		setLocation:function(point,render){
			var self=this;
			//画一个点
			self.addPoint(point,render);
			//跳转到对应的点
			self.panTo(point);
		},
		/**
		 * 跳转到某个点
		 */
		panTo:function(point){
			var self=this;
			var _p=new BMap.Point(point.lon, point.lat)
			self.map.panTo(_p);
		},
		/**
		 * 搜索关键字
		 */
		search:function(key,callback,pageSize){
			var self=this;
			var _pageSize=pageSize||10;
			var options = {
				pageCapacity:_pageSize,
				onSearchComplete: function(results) {
					//alert('Search Completed');
					if(localSearch.getStatus()==BMAP_STATUS_SUCCESS){
						//console.info(results);
						var arr=[];
						for(var i=0;i<results.getCurrentNumPois();i++){
							var poi=results.getPoi(i);
							/*
							address : "深圳市南山区深南大道9037号(欧陆风情街)"
							city : "深圳市"
							point : H lat : 22.542145 lng : 113.980442
							province : "广东省"
							ttile: "世界之窗"
							 * */
							var location={
								lon:poi.point.lng,
								lat:poi.point.lat,
								address:poi.address,
								province:poi.province,
								city:poi.city,
								title:poi.title
							}
							//console.info(results.getPoi(i));
							arr.push(location);
						}
						//可添加自定义回调函数
						if(callback){
							callback(arr);
						}
					}
					
				}
			};
			var localSearch=new BMap.LocalSearch(self.map, options);
			localSearch.search(key);
		},
		/**
		 * 添加标注点-单个
		 */
		addMarker:function(options,callback){
			var self = this;
			var options_default={
					panTo:true,
					enableDragging:true
			};
			options = $.extend({},options_default,options);
			//console.log(options);
			if(options.point&&options.point.lng&&options.point.lat){
				var _point=new BMap.Point(options.point.lng, options.point.lat);
				var marker=null;
				if(options.icon){
					var icon=new BMap.Icon(options.icon.url, 
							new BMap.Size(options.icon.width,options.icon.height));
					if(options.icon.anchor){
						icon=new BMap.Icon(options.icon.url, 
								new BMap.Size(options.icon.width,options.icon.height),
								{anchor:new BMap.Size(options.icon.anchor.width,options.icon.anchor.height)});
					}
					marker= new BMap.Marker(_point,{icon:icon});
				}else{
					marker = new BMap.Marker(_point);
				}
				if(options.enableDragging){
					marker.enableDragging(); //可拖拽
				}
				if(options.title){
					marker.setTitle(options.title); // 标题
				}
				
				if(options.panTo){
					self.map.panTo(_point); //跳转到标注点
				}
				if(options.label&&options.label.content){
					marker.setLabel(new BMap.Label(label.content));
				}
				if(options.events){ //添加监听
					for(var i in options.events){
						var event = options.events[i];
						if(event.name && typeof event.handler === 'function'){
							marker.addEventListener(event.name,event.handler);
						}
					}
				}
				
				self.map.addOverlay(marker);
				if(options.infowindow){ //添加信息窗口
					var infoWindow = null;
					if(options.infowindow.opts){
						infoWindow = new BMap.InfoWindow(options.infowindow.content,
								options.infowindow.opts);  // 创建信息窗口对象 
					}else{
						infoWindow=new BMap.InfoWindow(options.infowindow.content);  // 创建信息窗口对象 
					}
					marker.addEventListener('mouseover',function(){
						marker.openInfoWindow(infoWindow);
					});
					
				}
				
				setTimeout(function(){
					typeof callback === 'function' && callback(marker);
				}, 0);
			}
			
		},
		/**
		 * 根据坐标点解析地址
		 */
		locationPoint:function(point,callback){
			var self = this;
			if(point&&point.lng&&point.lat){
				if(!self.geocoder){
					self.geocoder= new BMap.Geocoder(); // 创建地址解析对象
				}
				self.geocoder.getLocation(new BMap.Point(point.lng,point.lat), function(result){
					//console.log(result);
					typeof callback === 'function' && callback(result);
				})
			}
		},
		/**
		 * 获取两地之间的驾车时间和距离,并绘制路线
		 */
		drivingDistance : function(data, callback) {
			var self = this;
			var transit = self.map.DrivingRoute(self.map, {
				onSearchComplete : function(results) {
					if (transit.getStatus() != BMAP_STATUS_SUCCESS){
						return ;
					}
					if(typeof callback=== 'function'){
						var plan = results.getPlan(0); //默认第一个规划优先
						var duration= plan.getDuration(false);   //获取时间 单位秒
						var distance= plan.getDistance(false);   //获取距离  单位米
						var result = {
								duration:duration,
								distance:distance
						}
						callback(result);
					}
				}
			});
			//option:{startCity:String,endCity:string,waypoints:array } waypoints途径点集合最多10个
			transit.search(data.start,data.end,data.option);
		}
	}
	
	//封装成jquery的控件
	$.fn.MapWidget=function(options){
		//生成uuid
		var uuid=(new Date()).getTime();
		var r=(Math.random()+"").replace(".","");
		uuid+=r;
		
		var _o=$.extend({},_map,_api);//后面的覆盖前面的
		_o.id="map"+uuid;
		_o.el=this;
		var _c=$.extend({},options,$.fn.MapWidget.defaults);//后面的覆盖前面的
		_o.init(_c);
		return _o;
    }
})();
