(function(){
	var _columnWidget={
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
			
			var _xData=c.xData||[];
			var _yData=c.yData||[];
			if(_xData&&_xData.length>0&&_yData&&_yData.length>0){
				self._renderChart(_xData,_yData);
			}else if(c.url){
				self.loadUrl();
			}
		},
		_bind:function(){
			
		},
		_renderChart:function(xData,yData){
			var self=this;
			var c=self.config;
			var _title=c.title||"";
			var _subTitle=c.subTitle||"";
			
			var _width=c.width||800;
			
			var _yAxisTitle=c.yAxisTitle||"";//Y轴的标题
			var _yUnit=c.yUnit||"";//Y轴单位
			
			var _data=[];
			
			for(var i=0,len=yData.length;i<len;i++){
				var item=yData[i];
				
				var name=item.name||"";
				var type=item.type||"column";//默认是柱状图
				
				var data=item.data||{};
				var _arr=[];
				//遍历取出数据，如果没有则根据x轴补满数据
				for(var j=0,jLen=xData.length;j<jLen;j++){
					var key=xData[j];
					var value=data[key];
					if(!value){
						//没有对应的x轴点的数据，则自动补0
						_arr.push(0);
					}else{
						_arr.push(value*1);
					}
				}
				
				var serie={
					name:name,
					type:type,
					data:_arr
				}
				
				_data.push(serie);
			}
			//固定10种颜色值
			Highcharts.setOptions({
		        colors: ['#058DC7', '#50B432', '#ED561B', '#DDDF00', '#24CBE5', '#64E572', '#FF9655', '#FFF263', '#6AF9C4', '#003366']
		    });
			
			self.chart=Highcharts.chart($(self.el).attr("id"), {
				chart: {
					width:_width,
					backgroundColor:null
				},
			    title: {
			        text: _title
			    },
			    subtitle: {
			        text: _subTitle
			    },
			    /*
			     * x轴数据
			     * 格式：['1月','2月','3月']
			     * */
			    xAxis: {
			        categories: xData,
			        crosshair: true
			    },
			    yAxis: {
			        min: 0,
			        title: {
			            text: _yAxisTitle
			        }
			    },
			    tooltip: {
			        headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
			        pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
			            '<td style="padding:0"><b>{point.y} '+_yUnit+'</b></td></tr>',
			        footerFormat: '</table>',
			        shared: true,
			        useHTML: true
			    },
			    plotOptions: {
			        column: {
			            pointPadding: 0.2,
			            borderWidth: 0
			        }
			    },
			    /*
			     * Y轴数据
			     * 格式：[{name:'Tokyo',data:[49.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4]}]
			     * */
			    series: _data
			});
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
	$.fn.ColumnChartWidget=function(options){
		//生成uuid
		var uuid=(new Date()).getTime();
		var r=(Math.random()+"").replace(".","");
		uuid+=r;
		
		var _o=$.extend({},_columnWidget);//后面的覆盖前面的
		_o.id="column"+uuid;
		_o.el=this;
		
		var _c=$.extend({},options,$.fn.ColumnChartWidget.defaults);//后面的覆盖前面的
		_o.init(_c);
		return _o;
    }
})();