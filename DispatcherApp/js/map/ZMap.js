document.write("<script language=javascript src='js/map/baidumap.js'></script>");


var ZMap = function(){
	var map = null;
	this.load = false;
	
	this.defaulData = {
		zoom: 13,
		empty_icon:"images/empty_icon.png",
		half_icon:"images/half_icon.png",
		full_icon:"images/full_icon.png",
		offline_icon:"images/offline_icon.png",
		goods_site_icon:"images/get_goods_icon.png"
	};
//	初始化地图
	this.init = function(div){
		map = new BMap.Map(div, {
			enableMapClick: false
		});
		map.centerAndZoom(new BMap.Point(113.95395, 22.55958),this.defaulData.zoom);

//		map.enableInertialDragging(); //开启缓动效果
		map.enableAutoResize(); //启用自动适应容器尺寸变化，默认启用。
		
		//支持鼠标滚轮缩放地图
		map.enableScrollWheelZoom();
		this.load = true;
		map.setDefaultCursor("default");
	};
	//获取当前地图显示范围
	this.getBounds = function() {
		if (!this.load) return;
		var bs = map.getBounds(); //获取可视区域
		var bssw = bs.getSouthWest(); //可视区域左下角
		var bsne = bs.getNorthEast(); //可视区域右上角
		//console.log("当前地图可视范围是：" + bssw.lng + "," + bssw.lat + "到" + bsne.lng + "," + bsne.lat);
		return [bssw.lng, bssw.lat, bsne.lng, bsne.lat];
	};
	this.getmyBounds = function() {
		return map.getBounds(); //获取可视区域
	};
	this.getmyZoom = function() {
			return map.getZoom();
	};
	/**设置地图中心和比例
	 * @param center 地图中心点坐标TMLonLat对象
	 * @param zoom   地图比例
	 */
	this.setCenterZoom = function(lon, lat, zoom) {
		map.centerAndZoom(new BMap.Point(lon, lat), zoom);

	};
	
	/**绘制车辆相关方法**/
	//清空所有覆盖物
	this.clearOverlays = function() {
		map.clearOverlays();
	};
	
	/**添加标记物
	 * @point: 坐标
	 * @iconName: icon 的名称， 与defaulData 里面的icon 对应
	 * @detailsData: 车辆或者 提货点的 详细信息
	 * @clikHandler : 点击此标记物是触发的函数
	 */
	this.setMarker = function(pointObj,iconName,detailsData,clickHandler){
		if(!pointObj){
			pointObj={lon:map.getCenter().lng,lat:map.getCenter().lat};
		}
		console.info(JSON.stringify(pointObj));
		var icon = new BMap.Icon(this.defaulData[iconName], new BMap.Size(32,32));
		console.log(this.defaulData[iconName]);
		var point = new BMap.Point(pointObj.lon,pointObj.lat);
		var marker = new BMap.Marker(point,{icon:icon}); 
		
		if(clickHandler){
			marker.addEventListener("click", function(e){clickHandler(e,detailsData);});
		}
		map.addOverlay(marker); 

		var label = new BMap.Label({id:detailsData.id},{offset:new BMap.Size(0,0)});
		label.setStyle({display: "none"});
		marker.setLabel(label);
	};
	
	/*
	 * 获取所有的标记物
	 */
	this.getAllMarker = function(){
		return map.getOverlays();
	};
	
	/*
	 *  隐藏marker
	 */
	this.hideMarker = function(marker){
		 map.hdieOverlay(marker);
	};
	/*
	 *  删除marker
	 */
	this.removeMarker = function(marker){
		map.removeOverlay(marker);
	};

	/*
	 * 更新marker 位置和信息
	 * @newPoint :新的坐标
	 * @detailsData： 新的详细数据
	 *
	 */
	 this.updateMarker = function(marker,newPoint,detailsData,clickHandler){
	 	if( !marker ){
	 		return;
	 	}
	 	var point = new BMap.Point(newPoint.lon,newPoint.lat);
	 	marker.setPosition(point);
	 	if(clickHandler){
			marker.addEventListener("click", function(e){clickHandler(e,detailsData);});
		}
	 }
	/*
	 * 定位
	 */
	this.getLocation = function() {
		 var geolocation = new BMap.Geolocation();
	     geolocation.getCurrentPosition(
	     	function(r){
		        if(this.getStatus() == BMAP_STATUS_SUCCESS){
		            var mk = new BMap.Marker(r.point);
		            map.addOverlay(mk);//标出所在地
		            map.panTo(r.point);//地图中心移动
		            var point = new BMap.Point(r.point.lng,r.point.lat);//用所定位的经纬度查找所在地省市街道等信息
		            var gc = new BMap.Geocoder();
		            gc.getLocation(point, function(rs){
		               var addComp = rs.addressComponents; console.log(rs.address);//地址信息
		               console.log(rs.address);//地址
		            });
		        }else {
		            alert('failed'+this.getStatus());
		        }        
	    },{enableHighAccuracy: true})
	};
	
	this.getPOI=function(r,n,callBack){//参数：r半径，n最多查找的点数量
		var _r=r||100;
		var _n=n||3;
		var mOption = {
			poiRadius : _r,//半径为r米内的POI,
			numPois : _n//最多只有12个
			}
		var ponits_=[];//经纬度和地址信息
		var myGeo=new BMap.Geocoder();
		var mPoint=map.getCenter();
		var lat=mPoint.lat;
		var lng=mPoint.lng;
		var _result=[];
		myGeo.getLocation(mPoint,function mCallback(rs){
			//获取全部POI(半径R的范围 最多12个点)
            var allPois = rs.surroundingPois; 
            if(allPois==null || allPois==""){
                return;
            }
            //获取地址信息
            var addComp = rs.addressComponents;
            if(addComp==null || addComp==""){
                return;
            }
			var province = addComp.province;
			var city = addComp.city;
			var district = addComp.district;
            var disMile=[];//储存周围的点和指定点的距离
            for(i=0;i<allPois.length;i++){//计算得到的POI坐标和指定坐标的距离
                var pointA=new BMap.Point(allPois[i].point.lng,allPois[i].point.lat);
				allPois[i].distance=map.getDistance(pointA, mPoint);//加入poi与定位点之间距离
				allPois[i].province=province;//加入省市区信息
                allPois[i].city=city;
                allPois[i].district=district;
                disMile.push(allPois[i]) ;
            }
            _result=_arrBubble(disMile);//disMile进行升序排列后的数组
            callBack(_result,mPoint);
        },mOption);
	};
	this.addEventListener=function(e,callBack){
		map.addEventListener(e, function(evt){
			callBack(evt);
		});
	};
//排序

	_arrBubble=function(arr){
	    for(var i=0;i<arr.length;i++){
	        for(var j=0;j<arr.length-1;j++){
	            if(arr[j+1].distance<arr[j].distance){
	                 var temp=arr[j];
	                    arr[j]=arr[j+1];
	                    arr[j+1]=temp;
	            }       
	        }   
	    }
	    return arr;
	};

}
