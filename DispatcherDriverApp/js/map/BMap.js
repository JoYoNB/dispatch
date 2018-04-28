(function(window, undefined){

	window.ZMap = ZMap;

	function ZMap(ele, opts){
		if(!document.getElementById(ele)){
			return;
		}
		var self = this;
		this.options = extend({}, {
			center: {lng: 114.02597366, lat: 22.54605355},		//-	默认地图中心点
			icon_car: 'images/map/icon_car.png',				//-	默认车辆标注
			icon_curPs: 'images/map/icon_marker.png',		//-	默认当前位置标注
			init: {},					//-	初始化地图参数
			zoom: 13,					//-	初始化地图缩放比率
			iconSize: 0.5,				//-	标注缩放比率
			mapInit: null,				//-	地图初始化回调方法
			isGetLocation: false,		//-	是否获取当前位置
			isDrawCurMarker: false,		//-	是否绘制当前位置标注
			drawCurMarkerCallback: null,//-	绘制当前位置标注回调方法
			getCurLocationError: null,	//-	获取当前位置失败处理方法
			getCurLocationSuccess: null	//-	获取当前位置成功处理方法
		}, opts);

		if(!window.BMap){
			//-	加载线上百度地图插件
			var script = document.createElement('script');
			script.src = 'http://api.map.baidu.com/getscript?v=2.0&ak=fgc5b5YD78xZoo9A7RLDsvXY';//
			document.head.appendChild(script);

			script.onload = function(){
				self.init(ele);
			}
		}else{
			self.init(ele);
		}

		return this;
	}

	ZMap.prototype = {
		constructor: ZMap,
		init: function(ele){
			var self = this;
			var opt = this.options;
			var o = extend({}, {
				minZoom: 3,					//-	地图允许展示的最小级别
				maxZoom: 19,				//-	地图允许展示的最大级别
				mapType: BMAP_NORMAL_MAP,	//-	地图类型
				enableHighResolution: true,	//-	是否启用使用高分辨率地图
				enableAutoResize: true,		//-	是否自动适应地图容器变化
				enableMapClick: true		//-	是否开启底图可点功能
			}, this.options.init);
			var map = this.map = new BMap.Map(ele, o);
			//-	开启鼠标滚轮缩放
			map.enableScrollWheelZoom();

			if( this.options.isGetLocation ){
				this.geolocation(function(res){
					opt.center = res.point;
					map.centerAndZoom(self.getPoint(opt.center), opt.zoom);
				}, function(type, err){
					map.centerAndZoom(self.getPoint(opt.center), opt.zoom);
					typeof opt.getCurLocationError === 'function' && opt.getCurLocationError(type, err);
				});
			}else{
				map.centerAndZoom(this.getPoint(opt.center), opt.zoom);
			}

			typeof opt.mapInit === 'function' && opt.mapInit();
		},
		//-	清除覆盖物
		clearOverlaysFn: function(){
			this.map.clearOverlays();
		},
		//-	返回坐标点
		getPoint: function(point){
			if(!point || !point.lng || !point.lat){
				return false;
			}
			if(point instanceof BMap.Point){
				return point;
			}
			return new BMap.Point(point.lng, point.lat);
		},
		//-	以像素表示一个矩形区域的大小
		getSize: function(x, y){
			return new BMap.Size(x, y);
		},
		//-	获取当前位置  不建议使用: 1.客户端限制  2.定位不准
		geolocation: function(success, error, opt){
			var self = this;
			var o = extend({}, {
				enableHighAccuracy: true,
				timeout: 3000,
				maximumAge: 0
			}, opt);
			var location = new BMap.Geolocation();
			location.getCurrentPosition(function(res){
				var s = this.getStatus();
				/*
					s = 0: 检索成功
						1: 城市列表
						2: 位置结果未知
						3: 导航结果未知
						4: 非法密钥
						5: 非法请求
						6: 没有权限
						7: 服务不可用
						8: 超时
				*/
				if(s==0){
					typeof success === 'function' && success(res);
					self.options.isDrawCurMarker && self.drawMarker(null, function(marker){
						self.curMarker = marker;
						typeof self.options.drawCurMarkerCallback === 'function' && self.options.drawCurMarkerCallback(marker, res);
					});
					typeof self.options.getCurLocationSuccess === 'function' && self.options.getCurLocationSuccess(res);
				}else{
					typeof error === 'function' && error(s, res);
				}
			}, o);
		},
		//-	创建图像标注
		drawMarker: function(opt, callback){
			opt = opt || {};
			var self = this;
			var point = this.getPoint(opt) || this.map.getCenter();
			var marker = {};
			var ico = opt.icon || this.options.icon_curPs;
			var img = new Image();
			img.src = ico;
			img.onload = function(){
				opt.x = this.width;
				opt.y = this.height;

				var _icon = self.getIcon(ico, opt);
				var o = extend({}, opt.mkInit);
				o.icon = _icon;
				// 创建标注
				marker = new BMap.Marker(point, o);
				self.map.addOverlay(marker);
				//-	延迟执行
				setTimeout(function(){
					typeof callback === 'function' && callback(marker);
				}, 0);
			};
		},
		//-	获取覆盖物图注
		getIcon: function(url, opt){
			opt = opt || {};
			
			var ico = url || this.options.icon_curPs;
			var iconSize = opt.iconSize || this.options.iconSize;
			var _w = opt.x || 0;
			var _h = opt.y || 0;

			var size = {
				w: opt.size && opt.size.w ? opt.size.w : _w, 
				h: opt.size && opt.size.h ? opt.size.h : _h
			};
			var anchor = {
				x: opt.anchor && opt.anchor.x ? opt.anchor.x : Math.round(_w/2), 
				y: opt.anchor && opt.anchor.y ? opt.anchor.y : Math.round(_h/2),
			};
			var imageSize = {
				w: opt.imageSize && opt.imageSize.w ? opt.imageSize.w : _w, 
				h: opt.imageSize && opt.imageSize.h ? opt.imageSize.h : _h
			};
			var imageOffset = {
				x: opt.imageOffset && opt.imageOffset.x ? opt.imageOffset.x : 0, 
				y: opt.imageOffset && opt.imageOffset.y ? opt.imageOffset.y : 0
			};

			var _icon = new BMap.Icon(ico, this.getSize(size.w*iconSize, size.h*iconSize), {
				anchor: this.getSize(anchor.x*iconSize, anchor.y*iconSize),
				imageSize: this.getSize(imageSize.w*iconSize, imageSize.h*iconSize),
				imageOffset: this.getSize(imageOffset.x*iconSize, imageOffset.y*iconSize)
			});

			return _icon;
		},
		//-	坐标转换
		convertor: function(opt, callback){
			/*
				1：GPS设备获取的角度坐标，wgs84坐标;
				2：GPS获取的米制坐标、sogou地图所用坐标;
				3：google地图、soso地图、aliyun地图、mapabc地图和amap地图所用坐标，国测局（gcj02）坐标;
				4：3中列表地图坐标对应的米制坐标;
				5：百度地图采用的经纬度坐标;
				6：百度地图采用的米制坐标;
				7：mapbar地图坐标;
				8：51地图坐标
			*/
			var convertor = new BMap.Convertor();
			opt = extend({}, {from: 1, to: 5, points: []}, opt);
			convertor.translate(opt.points, opt.from, opt.to, function(res){
				//-	{"status":0,"points":[{"lng":113.95356474272349,"lat":22.557705175191746}]}
				typeof callback === 'function' && callback(res);
			})
		},
		//-	根据经纬度解析地址
		getGeocoder: function(point, callback){
			var geocoder = new BMap.Geocoder();
			geocoder.getLocation(this.getPoint(point), callback)
		},
		/*	调整地图视野
		 *	@param  [{lng: (经度), lat: (纬度)},...]
		 */
		setViewportFn: function(arr){
			if(!arr || arr.length == 0){
				return;
			}
			var pointsArr = [];
			for(var m = 0; m < arr.length; m++){
				var a = arr[m];
				if(!a.lng || !a.lat){
					continue;
				}
    			pointsArr.push( this.getPoint( a ) );
    		}
			this.map.setViewport(pointsArr);
		},
		//-	返回两点间的距离
		getDistance: function(start, end){
			return this.map.getDistance( this.getPoint(start), this.getPoint(end) );
		},
		/*	点的地理坐标是否位于地图内
		 *	@param offset: 地图视图的偏移量(单位: px)  [左, 下, 右, 上]
		 */
		isContainsPoint: function(point, offset){
			offset = offset || [50, 50, 50, 50];
			var _size = this.map.getSize();
			var _sw = this.map.pixelToPoint(new BMap.Pixel(offset[0], _size.height-offset[1]));
			var _ne = this.map.pixelToPoint(new BMap.Pixel(_size.width-offset[2], offset[3]));
			var bounds = new BMap.Bounds(_sw, _ne);
			return bounds.containsPoint( point );
		},
		/*	电子围栏: 设置当前绘制工具
		 *	@param type 绘制的模式 	  1: 画圆  2: 画矩形  3: 画多边形  4: 画点  5: 画折线  6: 行政区域  其他: 拖动地图
		 *	@param opts 绘制的样式	  参考百度地图API 
		 *	@param callback 回调参数  res: 绘制结果处理  e: 绘制完成后，派发总事件的接口  drawingManager: 绘制 管理类
		 */
		drawFence: function(type, callback, opts){
			var self = this;

			if(!window.BMapLib || !window.BMapLib.DrawingManager){
				var script = document.createElement('script');
				script.src = 'http://api.map.baidu.com/library/DrawingManager/1.4/src/DrawingManager_min.js';
				document.head.appendChild(script);

				script.onload = function(){
					self._drawFence(type, callback, opts);
				};
			}else{
				self._drawFence(type, callback, opts);
			}
		},
		_drawFence: function(type, callback, opts){
			var self = this;
			var _type = type==1 ? 'circle' :
						type==2 ? 'rectangle' : 
						type==3 ? 'polygon' : 
						type==4 ? 'marker' :
						type==5 ? 'polyline' :
						'hander';

			var styleOptions = extend({}, {
		        strokeWeight: 3,       	//边线的宽度，以像素为单位。
		        strokeStyle: 'solid', 	//边线的样式，solid或dashed。
		        strokeColor:"#52b561",  //边线颜色。
		        strokeOpacity: 1,	   	//边线透明度，取值范围0 - 1。
		        fillColor:"#000",    	//填充颜色。当参数为空时，圆形将没有填充效果。
		        fillOpacity: 0.3      	//填充的透明度，取值范围0 - 1。 
		    }, opts);

			var drawingManager = new BMapLib.DrawingManager(this.map, {
		        isOpen: false, 						//是否开启绘制模式
		        enableDrawingTool: true, 			//是否显示工具栏
		        drawingToolOptions: {
		            anchor: BMAP_ANCHOR_TOP_RIGHT, 	//位置
		            offset: new BMap.Size(5, 5) 	//偏离值
		        },
		        markerOptions: styleOptions, 		//点的样式
		        circleOptions: styleOptions, 		//圆的样式
		        polylineOptions: styleOptions, 		//线的样式
		        polygonOptions: styleOptions, 		//多边形的样式
		        rectangleOptions: styleOptions 		//矩形的样式
		    });
		    drawingManager.setDrawingMode(_type);
			if(_type == 'hander'){
    			drawingManager.close();
	    		this.map.setDefaultCursor("url('bird.cur')");
	    		return;
	    	}else{
	    		drawingManager.open();
	    	}
		    //添加鼠标绘制工具监听事件，用于获取绘制结果
			drawingManager.addEventListener('overlaycomplete', function(e){
		        typeof callback == 'function' && callback( self.getDrawingManagerRes(e.overlay, _type), e );
		        drawingManager.close();
		        drawingManager = null;
		    });
		},
		//-	电子围栏: 返回当前绘制结果
		/*	lng: 地理经度
		 *	lat: 地理纬度
		 */
		getDrawingManagerRes: function(overlay, type){
			if(!overlay){
				return;
			}
			var res = [];
			switch(type){
				//-	圆形
				case 'circle': 		
					var ct = overlay.getCenter();
					var c = ct.lng + ',' + ct.lat;
					var r = overlay.getRadius();
					res = [c, r];
					break;
				//-	多边形、矩形
				case 'polygon': case 'rectangle':
					var arr = overlay.getPath();
					for(var i = 0; i < arr.length; i++){
						res.push(arr[i].lng + ',' + arr[i].lat);
					}
					break;
				//-	点
				case 'marker':
					var ct = overlay.point;
					var c = ct.lng + ',' + ct.lat;
					res = [c];
					break;
				//-	线
				case 'polyline':
					var arr = overlay.ia;
					for(var i = 0; i < arr.length; i++){
						res.push(arr[i].lng + ',' + arr[i].lat);
					}
					break;
			}
			return res;
		},
		//-	返回行政区域的边界
		getBoundary: function(name, callback, errorFn, opts){
			var self = this;
			var styleOptions = extend({}, {
		        strokeWeight: 2,       	//边线的宽度，以像素为单位。
		        strokeStyle: 'dashed', 	//边线的样式，solid或dashed。
		        strokeColor:"#52b561",  //边线颜色。
		        strokeOpacity: 1,	   	//边线透明度，取值范围0 - 1。
		        fillColor:"#000",    	//填充颜色。当参数为空时，圆形将没有填充效果。
		        fillOpacity: 0.2      	//填充的透明度，取值范围0 - 1。 
		    }, opts);

			var boundary = new BMap.Boundary();
			var overlays = [];		//覆盖物
			var pointsRes = [];		//path
			boundary.get(name, function(res){
				if( !res || !res.boundaries || res.boundaries.length == 0){
					typeof errorFn === 'function' && errorFn();
					return;
				}
				var pathsArr = [];
				for(var j = 0, len = res.boundaries.length; j < len; j++){
					var boundaries = res.boundaries[j].split(';');
					pathsArr[j] = [];

			    	for(var i = 0; i < boundaries.length; i++){
			    		pathsArr[j].push(boundaries[i].split(/,\s*/g));
			    	}
			    	//-	地图绘制行政区域
			    	var o = self.mapShowFence(6, pathsArr[j], null, styleOptions);
			    	pointsRes = pointsRes.concat( self.getDrawingManagerRes(o, 'polygon') );
			    	overlays.push( o );
				}

				setTimeout(function(){
					//-	调整地图显示视野
					if(len>1){
						var pointsArr = [];
				    	for(var n = 0; n < pathsArr.length; n++){
				    		var pathArr = pathsArr[n];
				    		for(var m = 0; m < pathArr.length; m++){
				    			pointsArr.push( {lng: pathArr[m][0], lat: pathArr[m][1]} );
				    		}
				    	}
				    	self.setViewportFn(pointsArr);
					}

					typeof callback == 'function' && callback(pointsRes, overlays);
				}, 0);
			});
		},
		//-	地图上显示围栏
		mapShowFence: function(type, pathArr, callback, opts){
		    var self = this;
			var defaule = {
		        strokeWeight: 3,       	//边线的宽度，以像素为单位。
		        strokeStyle: 'solid', 	//边线的样式，solid或dashed。
		        strokeColor: "#52b561",  //边线颜色。
		        strokeOpacity: 1,	   	//边线透明度，取值范围0 - 1。
		        fillColor: "#000",    	//填充颜色。当参数为空时，圆形将没有填充效果。
		        fillOpacity: 0.3      	//填充的透明度，取值范围0 - 1。 
		    };
		    if(type == 6){ defaule.strokeStyle = 'dashed'; defaule.strokeWeight = 2; defaule.fillOpacity = 0.2; }
			var styleOptions = extend({}, defaule, opts);
		    var overlay = null;
			switch(type){
				//-	圆形
				case 1: 
					var p = self.getPoint({lng: pathArr[0][0], lat: pathArr[0][1]});
					var r = pathArr[1][0];
					overlay = new BMap.Circle(p, r, styleOptions);
					break;
				//-	矩形、多边形、行政区域
				case 2: case 3: case 6:
					var ps = [];
					for(var i = 0; i < pathArr.length; i ++){
						ps.push(self.getPoint( {lng: pathArr[i][0], lat: pathArr[i][1]} ));
					}
					overlay = new BMap.Polygon(ps, styleOptions);
					break;
				//-	点
				case 4: 
					var p = self.getPoint({lng: pathArr[0][0], lat: pathArr[0][1]});
					overlay = new BMap.Marker(p);
					self.map.panTo(p);
					break;
				//-	折线
				case 5: 
					var ps = [];
					for(var i = 0; i < pathArr.length; i ++){
						ps.push(self.getPoint( {lng: pathArr[i][0], lat: pathArr[i][1]} ));
					}
					overlay = new BMap.Polyline(ps, styleOptions);
					break;
			}
			self.map.addOverlay(overlay);

			setTimeout(function(){
		    	var points = overlay && overlay.ia ? overlay.ia : [];
				self.setViewportFn(points);
				typeof callback == 'function' && callback(overlay);
			}, 0);
			return overlay;
		},
		//-	添加事件监听函数
		bind: function(ev, handler){
			this.map.addEventListener(ev, handler);
		},
		//-	移除事件监听函数
		unbind: function(ev, handler){
			this.map.removeEventListener(ev, handler);
		},
		//-	返回两点间的坐标点(直线): 用于轨迹回放和监控页面
		getLineByTwoPoints: function(obj1, obj2, opts){
			obj1 = this.getPoint(obj1);
			obj2 = this.getPoint(obj2);
			if(!obj1 || !obj2){
				return null;
			}

		    opts = opts || {};
		    var distance = opts.distance || 5,	//默认5米取一个坐标点
		    	divide = opts.divide || 50,		//默认将该段距离分成50份
		    	index = opts.index || 0,
		    	count = [];

		    var lat1 = parseFloat(obj1.lat);
		    var lat2 = parseFloat(obj2.lat);
		    var lng1 = parseFloat(obj1.lng);
		    var lng2 = parseFloat(obj2.lng);

		    var l = this.getDistance(obj1, obj2),	//-	两点间长度
		    	angle = 0;							//-	两点间的角度
			if (lat2 == lat1) {
				angle = 0;
			} else if (lng2 == lng1) {
				angle = lat2>lat1 ? 90 : -90;
			} else {
				angle = Math.round(360*Math.atan((lat2 - lat1) / (lng2 - lng1))/(2*Math.PI));
			}
			angle = lng2 > lng1 ? (270-angle) : (90-angle);

			//-	方案1: 按固定距离取点，相当于匀速
			// for(var i = 0; i < l-distance+1; i += distance){
			// 	var p = this.getPoint( {lng: lng1+i*(lng2-lng1)/l, lat: lat1+i*(lat2-lat1)/l} );
			// 	p.angle = angle;
			// 	count.push(p);
			// }
			// count.push(obj2);

			//-	方案2: 按固定份额取点，相当于变速
			for(var i = 0; i < l*(1-1/divide);){
				var p = this.getPoint( {lng: lng1+i*(lng2-lng1)/l, lat: lat1+i*(lat2-lat1)/l} );
				p.angle = angle;
				count.push(p);
				i += l/divide
			}
			count.push(obj2);
			
			return count;
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
			localSearch.search(string)
			return localSearch;
		}
	};

	function extend(){
		if(window.$){
			return $.extend.apply($, arguments);
		}
		var copy, options
			target = arguments[0] || {},
			i = 1,
			len = arguments.length;

		for(; i < len; i++){
			if( (options = arguments[i]) != null){
				for(var name in options){
					var copy = options[name];
					var src = target[name];
					if(src === copy){
						continue;
					}
					if(copy != undefined){
						target[name] = copy;
					}
				}
			}
		}
		return target;
	}

})(window);