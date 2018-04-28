/*
 * http://www.bootcss.com/p/bootstrap-datetimepicker/
 * */
(function(){
	var _timeWidget={
		id:null,
		el:null,
		config:null,
		picker:null,
		defaultLanguage:"zh-CN",
		defaultFormat:"yyyy-mm-dd hh:ii:ss",
		defaultWidth:"198px",
		defaultShowText:"请选择时间",
		
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
			//先给容器加上必要样式
			if(!$(self.el).hasClass("input-group")){
				$(self.el).addClass("input-group")
			}
			if(!$(self.el).hasClass("date")){
				$(self.el).addClass("date")
			}
			if(!$(self.el).hasClass("form_date")){
				$(self.el).addClass("form_date")
			}
			//动态加上宽度
			var _width=c.width||self.defaultWidth;
			$(self.el).css("width",_width);
			
			//自动引入语言包
			var _lang=c.language||self.defaultLanguage;
			if("zh-CN"==_lang){
				$.getScript("/js/datetimepicker/locales/bootstrap-datetimepicker.zh-CN.js");
			}
			
			var _showText=c.showText||self.defaultShowText;
			
			//加上input
			var _input='<input id="show_'+self.id+'" class="form-control" type="text" value="" placeholder="'+_showText+'" readonly>';
			//加上值域
			var _value='<input type="hidden" id="value_'+self.id+'">';
			//加上icon
			var _icon='<span class="input-group-addon"><span class="glyphicon glyphicon-th"></span></span>';
			
			$(self.el).html(_input+_value+_icon);
			
			var _format=c.format||self.defaultFormat;
			var _minView=0;
			if("yyyy-mm-dd"==_format){
				_minView=2;//精确到月/天的视图
			}else{
				_minView=0;//精确到秒的视图
			}
			
			var _option={
				language:_lang,
				weekStart:1,//一周从哪一天开始。0（星期日）到6（星期六）
				todayBtn:1,//如果此值为true 或 "linked"，则在日期时间选择器组件的底部显示一个 "Today"
				autoclose:1,//当选择一个日期之后是否立即关闭此日期时间选择器
				todayHighlight:1,//如果为true, 高亮当前日期。
				/*
				日期时间选择器打开之后首先显示的视图。 可接受的值：
				0 or 'hour' for the hour view
				1 or 'day' for the day view
				2 or 'month' for month view (the default)
				3 or 'year' for the 12-month overview
				4 or 'decade' for the 10-year overview. Useful for date-of-birth datetimepickers.
				 * */
				startView:2,
				minView:_minView,//日期时间选择器所能够提供的最精确的时间选择视图。
				/*
				 * 当选择器关闭的时候，是否强制解析输入框中的值。
				 * 也就是说，当用户在输入框中输入了不正确的日期，选择器将会尽量解析输入的值，并将解析后的正确值按照给定的格式format设置到输入框中。
				 * */
				forceParse:0,
				/*
				yyyy-mm-dd
				yyyy-mm-dd hh:ii
				yyyy-mm-ddThh:ii
				yyyy-mm-dd hh:ii:ss
				yyyy-mm-ddThh:ii:ssZ
				 * */
				format:_format,
				linkField:"value_"+self.id,//关联的值
				linkFormat:_format
			}
			_option = $.extend(_option,c.option);
			setTimeout(function(){
				if(c.event&&c.event.name&&c.event.callback){
					self.picker=$(self.el).datetimepicker(_option).on(c.event.name,function(ev){
						typeof c.event.callback==='function' &&c.event.callback(ev);
					});
				}else{
					self.picker=$(self.el).datetimepicker(_option);
				}
				
			},200);
			
		},
		_bind:function(){
			var self=this;
			var c=self.config;
			/*$(self.el).datetimepicker().on('changeDate', function(ev){
			    var _value=$("#value_"+self.id).val();
			    console.info(_value);
			    self.picker.hide();
			});*/
			
			$(self.el).on("change","#value_"+self.id,function(){
				var _value=$(this).val();
				//console.info(_value);
				if(c.onSelected){
					c.onSelected(_value);
				}
			});
			
		},
		getValue:function(){
			var self=this;
			return $("#value_"+self.id).val();
		},
		setValue:function(time){
			var self=this;
			//先设置值
			$("#show_"+self.id).val(time);
			$("#value_"+self.id).val(time);
			//再更新控件的值
			//$(self.el).datetimepicker('update');
		},
		reset:function(){
			var self = this;
			//先设置值
			var _showText=self.config.showText||self.defaultShowText;
			$("#show_"+self.id).val(_showText);
			$("#value_"+self.id).val(null);
			//再更新控件的值
			//$(self.el).datetimepicker('setStartDate');
		}
	}
	
	
	//封装成jquery的控件
	$.fn.TimeWidget=function(options){
		//生成uuid
		var uuid=(new Date()).getTime();
		var r=(Math.random()+"").replace(".","");
		uuid+=r;
		
		var _o=$.extend({},_timeWidget);//后面的覆盖前面的
		_o.id="time"+uuid;
		_o.el=this;
		
		var _c=$.extend({},options,$.fn.TimeWidget.defaults);//后面的覆盖前面的
		_o.init(_c);
		return _o;
    }
})();