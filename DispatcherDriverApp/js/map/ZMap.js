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

     	//map.enableInertialDragging(); //开启缓动效果
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
	 * @id: 车辆或者 提货点的 id
	 * @clikHandler : 点击此标记物是触发的函数
	 */
	this.setMaker = function(pointObj,iconName,id,type,clickHandler){
		var icon = new BMap.Icon(this.defaulData[iconName], new BMap.Size(32,32));
		console.log(this.defaulData[iconName])
		var point = new BMap.Point(pointObj.lon,pointObj.lat);
		var marker = new BMap.Marker(point,{icon:icon}); 
		
		if(clickHandler){
			marker.addEventListener("click", function(e){clickHandler(e, id, type);});
		}
		map.addOverlay(marker); 
	};
}
