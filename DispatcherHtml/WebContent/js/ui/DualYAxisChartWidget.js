(function(){
	var _dualWidget={
		id:null,
		el:null,
		config:null,
		chart:null,
		
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
			var xData=c.xData;
			var yData=c.yData;
			if(xData&&yData){
				self._renderChart(xData,yData);
			}else if(c.url){
				self.loadUrl();
			}
			
		},
		_bind:function(){
			var self=this;
			
		},
		_renderChart:function(xData,yData){
			var self=this;
			
			var self=this;
			var c=self.config;
			var _title=c.title||"";
			var _subTitle=c.subTitle||"";
			
			var _yAxis=c.yAxis||[];
			//解析出Y轴的配置
			var yAxis=self._adaptYAxisConfig(_yAxis);
			
			var _width=c.width||800;
			
			var _xAxis=self._adaptXData(xData);//适配出x轴数据
			var _yData=self._adaptYData(xData,yData);//适配出y轴数据
			
			//固定10种颜色值
			Highcharts.setOptions({
		        colors: ['#058DC7', '#50B432', '#ED561B', '#DDDF00', '#24CBE5', '#64E572', '#FF9655', '#FFF263', '#6AF9C4', '#003366']
		    });
			
			self.chart=Highcharts.chart($(self.el).attr("id"), {
				chart: {
					width:_width,
					zoomType: 'xy',
					backgroundColor:null
				},
			    title: {
			        text: _title
			    },
			    subtitle: {
			        text: _subTitle
			    },
			    xAxis: _xAxis,
			    yAxis: yAxis,
			    tooltip: {
			    	shared: true
			    },
			    series: _yData
			});
			
		},
		/**
		 * 适配出x轴数据
		 */
		_adaptXData:function(xData){
			var self=this;
			var _arr=[];
			var _item0=xData[0];
			if((typeof _item0=='string')&&_item0.constructor==String){
				//为字符串，单个数组
				var categories=[];
				for(var i=0,len=xData.length;i<len;i++){
					var item=xData[i];
					categories.push(item);
				}
				var x={
					crosshair: true,
					categories: categories
				}
				//只有一个x轴
				_arr.push(x);
				return _arr;
			}else{
				//为多个数组，相当于有多个x轴
				for(var i=0,len=xData.length;i<len;i++){
					var a=xData[i];//a为一个数组
					var categories=[];
					for(var j=0,jLen=a.length;j<jLen;j++){
						var item=a[j];
						categories.push(item);
					}
					var x={
						crosshair: true,
						categories: categories
					}
					_arr.push(x);
				}
				
				return _arr;
			}
		},
		_adaptYData:function(xData,yData){
			var self=this;
			var c=self.config;
			
			var _item0=xData[0];
			if((typeof _item0=='object')&&_item0.constructor==Array){
				alert("暂时不支持多个X轴");
				return;
			}
			var _yData=[];
			for(var i=0,len=yData.length;i<len;i++){
				var item=yData[i];
				var name=item.name;
				var type=item.type||"column";//默认数柱状图
				var data=item.data||{};
				
				var series={
					name:name,
					type:type
				};
				
				var yAxis=c.yAxis;//是一个数组，Y轴的配置项
				for(var j=0,jLen=yAxis.length;j<jLen;j++){
					var option=yAxis[j];
					if(option.name==name){
						//找到对应的Y轴配置
						series.yAxis=j;//对应的Y轴
						series.tooltip={
							valueSuffix:option.unit||"" //配置的单位
						}
						break;
					}
				}
				//补全数据
				var _data=[];
				for(var m=0,mLen=xData.length;m<mLen;m++){
					var key=xData[m];
					var value=data[key];
					if(!value){
						//没有对应的x轴点的数据，则自动补0
						_data.push(0);
					}else{
						_data.push(value*1);
					}
				}
				series.data=_data;
				//多个图形
				_yData.push(series);
			}
			
			return _yData;
		},
		_adaptYAxisConfig:function(yAxis){
			var self=this;
			
			var _yAxis=[];
			for(var i=0,len=yAxis.length;i<len;i++){
				var _item=yAxis[i];
				
				var _unit=_item.unit||"";
				var _text=_item.name||"";
				
				var _option={
					labels:{
						format: '{value}'+_unit,
		                style: {
		                    color: Highcharts.getOptions().colors[i]
		                }
					},
					title:{
						text: _text,
		                style: {
		                    color: Highcharts.getOptions().colors[i]
		                }
					}
				}
				if(i==len-1){
					//最后一个的Y轴在右边
					_option.opposite=true;
				}
				//Y轴刻度步长
				var tickInterval=_item.tickInterval;
				if(tickInterval){
					_option.tickInterval=tickInterval;
				}
				
				_yAxis.push(_option);
			}
			
			return _yAxis;
		},
		loadUrl:function(param){
			var self=this;
			var c=self.config;
			
			var _xField=c.xField||"xData";
			var _yField=c.yField||"yData";
			
			var _param=param||{};
			var _type=c.type||"POST";
			
			CommonUtils.async({
				url:c.url,
				type:_type,
				data:_param,
				success:function(result){
					if(result.code==0){
						var data=result.data||{};
						var _xData=data[_xField];
						var _yData=data[_yField];
						self._renderChart(_xData,_yData);
					}
				}
			});
		}
		
		
	}
	
	
	//封装成jquery的控件
	$.fn.DualYAxisChartWidget=function(options){
		//生成uuid
		var uuid=(new Date()).getTime();
		var r=(Math.random()+"").replace(".","");
		uuid+=r;
		
		var _o=$.extend({},_dualWidget);//后面的覆盖前面的
		_o.id="2ychart"+uuid;
		_o.el=this;
		
		var _c=$.extend({},options,$.fn.DualYAxisChartWidget.defaults);//后面的覆盖前面的
		_o.init(_c);
		return _o;
    }
})();