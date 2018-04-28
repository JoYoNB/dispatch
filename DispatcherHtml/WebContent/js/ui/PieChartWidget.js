(function(){
	var _pieWidget={
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
			
			var _title=c.title||"";
			var _seriesName=c.seriesName||"占比";
			
			var _width=c.width||800;
			
			//固定10种颜色值
			Highcharts.setOptions({
		        colors: ['#058DC7', '#50B432', '#ED561B', '#DDDF00', '#24CBE5', '#64E572', '#FF9655', '#FFF263', '#6AF9C4', '#003366']
		    });
			
			self.chart=Highcharts.chart($(self.el).attr("id"), {
				chart: {
			        plotBackgroundColor: null,
			        plotBorderWidth: null,
			        plotShadow: false,
			        width: _width,
			        type: 'pie'
			    },
			    title: {
			        text: _title
			    },
			    tooltip: {
			        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
			    },
			    plotOptions: {
			        pie: {
			            allowPointSelect: true,
			            cursor: 'pointer',
			            dataLabels: {
			                enabled: true,
			                format: '<b>{point.name}</b>: {point.percentage:.1f} %',
			                style: {
			                    color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
			                }
			            }
			        }
			    },
		        series: [{
		            name: _seriesName,
		            colorByPoint: true,
		            data: []
		        }]
		    });
			
			var _data=c.data||[];
			if(_data&&_data.length>0){
				self.loadData(_data);
			}
			if(c.url){
				self.loadUrl();
			}
		},
		_bind:function(){
			
		},
		loadData:function(data){
			var self=this;
			var c=self.config;
			var _textField=c.textField||"name";
			var _valueField=c.valueField||"value";
			//把data转成固定规格的数据
			var _data=[];
			for(var i=0,len=data.length;i<len;i++){
				var item=data[i];
				var name=item[_textField];
				var value=item[_valueField];
				
				_data.push({name:name,y:value});
			}
			
			self.chart.series[0].setData(_data);
		},
		loadUrl:function(param){
			var self=this;
			var c=self.config;
			var _param=param||{};
			var _type=c.type||"POST";
			
			CommonUtils.async({
				url:c.url,
				type:_type,
				data:_param,
				success:function(result){
					if(result.code==0){
						var data=result.data||[];
						self.loadData(data);
					}
				}
			});
		}
	}
	
	//封装成jquery的控件
	$.fn.PieChartWidget=function(options){
		//生成uuid
		var uuid=(new Date()).getTime();
		var r=(Math.random()+"").replace(".","");
		uuid+=r;
		
		var _o=$.extend({},_pieWidget);//后面的覆盖前面的
		_o.id="pie"+uuid;
		_o.el=this;
		
		var _c=$.extend({},options,$.fn.PieChartWidget.defaults);//后面的覆盖前面的
		_o.init(_c);
		return _o;
    }
})();