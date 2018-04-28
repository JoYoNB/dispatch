var WinRepository={};
var WinWidget={};

(function(_WinWidget,_WinRepository){
	var _win={
		id:null,
		el:null,
		config:null,
		_defaultTitle:"窗口",
		_defaultWidth:"500px",
		_defaultHeight:"400px",
		
		init:function(c){
			var self=this;
			var _c=c||{};
			self.config=_c;
			
			self._render();
			//self._bind();
		},
		_render:function(){
			var self=this;
			var c=self.config;
			var _content='';
			if(c.content&&c.content!=""){
				_content=c.content;
			}
			var _title=c.title||self._defaultTitle;
			var _width=c.width||self._defaultWidth;
			var _height=c.height||self._defaultHeight;
			
			var _mask='<div id="mask_'+self.id+'" class="pop-module-mask"></div>';
			var _body='<div id="winbody_'+self.id+'" class="pop-module edit-cost-rule" style="width:'+_width+';height:'+_height+';">'
				+'	<div class="pop-header clearfix">'
				+'		<ul class="city-tap fl clearfix">'
				+'			<li class="tap-item active"><a href="javascript:;">'+_title+'</a></li>'
				+'		</ul>'
				+'		<span id="closeWin_'+self.id+'" class="pop-close fr"><i class="pop-close-icon">x</i></span>'
				+'	</div>'
				+'	<div id="main_'+self.id+'" class="pop-main clearfix">'
				+_content //窗体业务内容
				+'	</div>'
				
				+'</div>';
			
			$("body").append(_mask+_body);
			self._afterRender();
		},
		_bind:function(){
			var self=this;
			
			$("#closeWin_"+self.id).unbind("click").click(function(){
				self.close();
			});
		},
		_afterRender:function(){
			var self=this;
			var c=self.config;
			
			setTimeout(function(){
				self._bind();
				if(c.url&&c.url!=""){
					$("#main_"+self.id).load(c.url);
				}
			},200);
		},
		close:function(){
			var self=this;
			$("#mask_"+self.id).remove();
			$("#winbody_"+self.id).remove();
		}
		
	}
	
	_WinWidget.create=function(options){
		var uuid=(new Date()).getTime();
		var r=(Math.random()+"").replace(".","");
		uuid+=r;
		
		var _o=$.extend({},_win);//后面的覆盖前面的
		_o.id="win"+uuid;
		_o.init(options);
		//放进仓库
		_WinRepository[_o.id]=_o;
		return _o;
	}
})(WinWidget,WinRepository);