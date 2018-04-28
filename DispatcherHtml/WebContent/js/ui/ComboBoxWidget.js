/*
 *	可配置的下拉选择 
 * 	
 * 	例子：
 * 	var combobox=$("#id").ComboBoxWidget({
 * 		//url:"",//url和data是互斥的
 * 		textField:"name",
 * 		valueField:"value",
 * 		width:250,//单位px,可选
 * 		showText:"请选择",//如果不填写，默认就是请选择
 * 		onSelected:function(value,text){
 * 			//选择事件
 * 			alert(value);
 * 		},
 * 		data:[{
 * 			name:"深圳",
 * 			value:"shenzhen"
 * 		},{
 * 			name:"广州",
 * 			value:"guangzhou"
 * 		},{
 * 			name:"北京",
 * 			value:"beijing"	
 * 		}]
 * 
 * 	});
 * 
 **/
(function(){
	var _comboBox={
		id:null,
		el:null,
		config:null,
		_defaultShowText:"请选择",
		_defaultTextField:"name",
		_defaultValueField:"value",
		renderOK:false,
		
		_afterRenderEvent:[],
		
		init:function(c){
			var self=this;
			
			var _c=c||{};
			self.config=_c;
			
			
			self._render();
			self._bind();
			//self._event();
		},
		_render:function(){
			var self=this;
			
			var c=self.config;
			
			var _showText=c.showText||self._defaultShowText;
			var _width=c.width;
			var w='';
			if(_width){
				w='style="width:'+_width+'px;"';
			}
			var _datas='';
			if(c.data&&c.data.length>0){
				_datas=self._genItems(c.data);
			}
			
			var _html='<select id="'+self.id+'" class="city-ipt-select" '+w+'>'
					/*+'onmousedown="if(this.options.length>10){this.size=11}"'
					+'onblur="this.size=0" onchange="this.size=0">'*/
					+'<option value="">'+_showText+'</option>'
					+_datas;
					+'</select>';
			$(self.el).html(_html);
			
			setTimeout(function(){
				self._afterRender();
			},200);
		},
		_bind:function(){
			var self=this;
			var c=self.config;
			
			if(c.onSelected&& typeof c.onSelected==='function'){
				//绑定选择事件
				$(self.el).on("change",'#'+self.id,function(){
					//console.log($(this).val());
					var text=$(this).find("option:selected").text();
					var value=$(this).val();
					
					c.onSelected(value,text,self);
				});
			}
		},
		_event:function(){
			var self=this;
			//加载数据完成事件
			self.afterLoad=document.createEvent(self.id+"AfterLoad");
			//初始化事件
			self.afterLoad.initCustomEvent(self.id+"AfterLoad",false,false,"AfterLoad");
			
			
		},
		_genItems:function(list,selectValue){
			var self=this;
			var c=self.config;
			
			var _textField=c.textField||self._defaultTextField;
			var _valueField=c.valueField||self._defaultValueField;
			var _datas='';
			var _selcetValue='';
			if(selectValue){
				_selcetValue=selectValue;
			}
			for(var i=0,len=list.length;i<len;i++){
				var item=list[i];
				if(_selcetValue==item[_valueField]){
					_datas+='<option value="'+item[_valueField]+'" selected="selected">'+item[_textField]+'</option>';
				}else{
					_datas+='<option value="'+item[_valueField]+'">'+item[_textField]+'</option>';
				}
				
			}
			
			return _datas;
		},
		_afterRender:function(){
			var self=this;
			self.renderOK=true;
			var c=self.config;
			if(c.url&&c.url!=""){
				self.loadUrl(c.url);
			}
		},
		loadUrl:function(url,selectValue){
			var self=this;
			var c=self.config;
			var _type=c.type||"POST";
			
			CommonUtils.async({
				url:url,
				type:_type,
				data:{},
				success:function(result){
					if(result.code==0){
						var ret=result.data||{};
						var list=ret.list||[];
						self.loadData(list,selectValue);
					}
				}
			});
		},
		loadData:function(list,selectValue){
			var self=this;
			var c=self.config;
			var _showText=c.showText||self._defaultShowText;
			var _datas='<option value="">'+_showText+'</option>';
			_datas+=self._genItems(list,selectValue);
			
			$("#"+self.id).html(_datas);
			//var _id=$(self.el).attr("id");
			//document.getElementById(_id).dispatchEvent(self.afterLoad);
			/*if(self._afterRenderEvent.length>0){
				setTimeout(function(){
					for(var i=0,len=self._afterRenderEvent.length;i<len;i++){
						var _event=self._afterRenderEvent[i];
						_event.invoke(self);
					}
				},200);
			}*/
			if(window[self.id+"_value"]&&window[self.id+"_value"]!=""){
				setTimeout(function(){
					$("#"+self.id).val(window[self.id+"_value"]);
				},200);
			}
		},
		setValue:function(value){
			var self=this;
			if(self.renderOK){
				$("#"+self.id).val(value);
			}else{
				/*var _event={
					param:value,
					invoke:function(obj){
						$("#"+obj.id).val(this.param);
					}
				}
				self._afterRenderEvent.push(_event);*/
				//console.info(self.id);
				window[self.id+"_value"]=value;
				//console.info(value);
				/*setTimeout(function(){
					$("#"+self.id).val(value);
				},500);*/
			}
		},
		getValue:function(){
			var self=this;
			return $("#"+self.id).val();
		},
		setText:function(text){
			var self=this;
			//console.info(text)
			if(self.renderOK){
				$("#"+self.id).find("option").each(function(){
				    var tx=$(this).text();
				    if(tx==text){
				    	$(this).attr("selected",true);
				    }
				});
			}else{
				setTimeout(function(){
					//$("#"+self.id+" option[text='"+text+"']").attr("selected",true); 
					//console.info($("#"+self.id).find("option[text='"+text+"']"));
					//$("#"+self.id).find("option[text='"+text+"']").attr("selected","selected");
					$("#"+self.id).find("option").each(function(){
					    var tx=$(this).text();
					    if(tx==text){
					    	$(this).attr("selected",true);
					    }
					});
				},200);
			}
		},
		getText:function(){
			var self=this;
			var text=$("#"+self.id).find("option:selected").text();
			return text;
		},
		disabled:function(){//设置为不可选状态
			var self = this;
			self.el.find('select').attr('disabled','disabled');
		},
		removeDisabled:function(){
			var self = this;
			self.el.find('select').removeAttr('disabled');
		}
	}
	
	//封装成jquery的控件
	$.fn.ComboBoxWidget=function(options){
		//生成uuid
		var uuid=(new Date()).getTime();
		var r=(Math.random()+"").replace(".","");
		uuid+=r;
		
		var _o=$.extend({},_comboBox);//后面的覆盖前面的
		_o.id="cbb"+uuid;
		_o.el=this;
		
		var _c=$.extend({},options,$.fn.ComboBoxWidget.defaults);//后面的覆盖前面的
		_o.init(_c);
		return _o;
    }
})();

