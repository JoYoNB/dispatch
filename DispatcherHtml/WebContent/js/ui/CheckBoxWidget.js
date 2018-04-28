(function(){
	var _checkBox={
		id:null,//控件本身Id
		el:null,//控件容器dom
		config:{},
		
		init:function(c){
			var self=this;
			var _c=c||{};
			self.config=_c;
			
			self._render();
		},
		_render:function(){
			var self=this;
			var c=self.config;
			
			if(c.data){
				//优先加载data
				self.loadData(c.data);
			}else if(c.url){
				self.loadUrl();
			}
		},
		/**
		 * 只负责查询
		 */
		loadUrl:function(pa){
			var self=this;
			var c=self.config;
			var _param=pa||{};
			
			var _type=c.type||"POST";
			CommonUtils.async({
				url:c.url,
				type:_type,
				data:_param,
				success:function(result){
					if(result.code==0){
						var ret=result.data||{};
						var list=ret.list||[];
						self.loadData(list);
					}
				}
			});
			
		},
		loadData:function(list){
			var self=this;
			var c=self.config;
			var cells=c.cells||[];
			
			var _html='';
			for(var i=0,len=list.length;i<len;i++){
				var data=list[i];
				_html+='<label class="city-ipt-label"><input type="checkbox" name="'+self.id+'" value="'+data.id+'"><span class="city-ipt-checkbox"></span>'+data.name+'</label>';
			}
			$(self.el).html(_html);
		},
		
		//获取被选中的值
		getSelectedValues:function(){
			var self=this;
			var ids=[];
			$.each($('input[name="'+self.id+'"]:checked'),function(){
				ids.push($(this).val());
            });
			return ids;
		},
		//设置被选中的值
		setSelectedValues:function(list){
			var self=this;
			$.each($('input[name="'+self.id+'"]'),function(){
				var temp=$(this).val();
				for(var i=0,len=list.length;i<len;i++){
					if(temp==list[i]){
						$(this).attr('checked', true);
					}
				}
            });
		}
	}
	
	//封装成jquery的控件
	$.fn.CheckBoxWidget=function(options){
		//生成uuid
		var uuid=(new Date()).getTime();
		var r=(Math.random()+"").replace(".","");
		uuid+=r;
		
		var _o=$.extend({},_checkBox);//后面的覆盖前面的
		_o.id="ckb"+uuid;
		_o.el=this;
		
		var _c=$.extend({},options,$.fn.CheckBoxWidget.defaults);//后面的覆盖前面的
		_o.init(_c);
		return _o;
    }
})();