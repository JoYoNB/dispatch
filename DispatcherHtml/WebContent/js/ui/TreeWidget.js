(function(){
	
	var _tree={
		id:null,
		el:null,
		config:null,
		dataReady:false,
	
		init:function(c){
			var self=this;
			self.config=c||{};
			self._render();
			self._bind();
			
			if(c.data&&c.data.length>0){
				self.loadData(c.data);
			}else if(c.url){
				self.loadUrl();
			}
		},
		_render:function(){
			var self=this;
			
		},
		_bind:function(){
			var self=this;
			//控制展开和收起
			$(document).off("click",".authority-select .toggle-level-box").on("click",".authority-select .toggle-level-box",function(){
				if($(this).parent(".level-box").hasClass("close-child")){
					$(this).parent(".level-box").removeClass("close-child");
					$(this).find(".icon").removeClass("icon-arrLeft-fill").addClass("icon-arrDown-fill")
				}else{
					$(this).parent(".level-box").addClass("close-child");
					$(this).find(".icon").removeClass("icon-arrDown-fill").addClass("icon-arrLeft-fill")
				}
			});
			
			//checkbox点击事件
			$(self.el).off("click","input[type=checkbox]").on("click","input[type=checkbox]",function(){
				//alert($(this).val());
				//递归找出所有的孩子
				var parent=$(this).parent().parent().parent();
				var idata=$(this).attr("idata");
				if("1"==idata){
					//取消
					$(parent).find("input[type=checkbox]").attr("checked",false);
					$(parent).find("input[type=checkbox]").attr("idata","0");
					//如果是已经取消全部的子节点，则父节点也要取消
					//往上找出所有的父级checkbox
					var p2ul=$(parent).parent();//ul
					var p2checkboxList=$(p2ul).find("input[type=checkbox]:checked");
					if(p2checkboxList.length==0){
						//已经没有选中的,在取消这个父级节点
						var p2ulparent=$(p2ul).parent();//可能是li，可能是div
						//console.info($(p2ulparent).is("li"));
						if($(p2ulparent).is("li")){
							//第二级菜单
							var div=$(p2ulparent).children(":first");
							//console.info($(div).html());
							var clist=$(div).children();
							var p2ckbox=$(clist[1]).children(":first");
							$(p2ckbox).attr("checked",false);
							$(p2ckbox).attr("idata","0");
							
							var p1ul=$(p2ulparent).parent();
							var p1checkboxList=$(p1ul).find("input[type=checkbox]:checked");
							if(p1checkboxList.length==0){
								var p1ulparent=$(p1ul).parent();//可能是li，可能是div
								if($(p1ulparent).is("li")){
									//第一级菜单
									var div1=$(p1ulparent).children(":first");
									var clist1=$(div1).children();
									var p1ckbox=$(clist1[1]).children(":first");
									$(p1ckbox).attr("checked",false);
									$(p1ckbox).attr("idata","0");
								}
							}
							
						}
					}
					
				}else{
					//选中
					$(parent).find("input[type=checkbox]").attr("checked",true);
					$(parent).find("input[type=checkbox]").attr("idata","1");
					
					//往上找出所有的父级checkbox
					var p2ul=$(parent).parent();//ul
					var p2ulparent=$(p2ul).parent();//可能是li，可能是div
					//console.info($(p2ulparent).is("li"));
					if($(p2ulparent).is("li")){
						//第二级菜单
						var div=$(p2ulparent).children(":first");
						//console.info($(div).html());
						var clist=$(div).children();
						var p2ckbox=$(clist[1]).children(":first");
						$(p2ckbox).attr("checked",true);
						$(p2ckbox).attr("idata","1");
						
						var p1ul=$(p2ulparent).parent();
						var p1ulparent=$(p1ul).parent();//可能是li，可能是div
						if($(p1ulparent).is("li")){
							//第一级菜单
							var div1=$(p1ulparent).children(":first");
							var clist1=$(div1).children();
							var p1ckbox=$(clist1[1]).children(":first");
							$(p1ckbox).attr("checked",true);
							$(p1ckbox).attr("idata","1");
						}
					}
				}
				
				
			});
			
			
		},
		loadData:function(list){
			var self=this;
			
			var _html='<ul>';
			
			var _f=function(node){
				var _c=node.children||[];
				var _foldIcon='';
				if(_c.length>0){
					_foldIcon='<span class="toggle-level-box"><i class="icon icon-arrDown-fill"></i></span>';
				}
				
				var _h='<li>'
					+'	<div class="level-box">'
					+_foldIcon
					+'		<label class="city-ipt-label"><input id="cb_'+node.id+'" type="checkbox" value="'+node.code+'" idata="0"><i class="city-ipt-checkbox"></i>'+node.name+'</label>'
					+'	</div>';
				
				if(_c.length>0){
					var _ul='<ul>';
					for(var j=0,jl=_c.length;j<jl;j++){
						var _h2=_f(_c[j]);
						_ul+=_h2;
					}
					_ul+='</ul>';
					
					_h+=_ul;
				}
				
				_h+='</li>';
				
				return _h;
			}
			
			for(var i=0,len=list.length;i<len;i++){
				var _node=list[i];
				var _hh=_f(_node);
				_html+=_hh;
			}
			
			_html+='</ul>';
			
			self.dataReady=true;
			$(self.el).html(_html);
		},
		loadUrl:function(){
			var self=this;
			var c=self.config;
			
			CommonUtils.async({
				url:c.url,
				data:{},
				success:function(result){
					if(result.code==0){
						var list=result.data.leaf||[];
						self.loadData(list);
					}
				}
			});
			
		},
		getValue:function(){
			var self=this
			var ids=[];
			$.each($(self.el).find('input[type=checkbox]:checked'),function(){
				ids.push($(this).val());
            });
			return ids;
		},
		setValue:function(ids){
			var self=this;
			if(self.dataReady){
				for(var i=0,len=ids.length;i<len;i++){
					$("#cb_"+ids[i]).attr("checked",true);
				}
			}else{
				//自动触发
				setTimeout(function(){
					for(var i=0,len=ids.length;i<len;i++){
						$("#cb_"+ids[i]).attr("checked",true);
					}
				},500);
			}
			
		}
	}
	
	//封装成jquery的控件
	$.fn.TreeWidget=function(options){
		//生成uuid
		var uuid=(new Date()).getTime();
		var r=(Math.random()+"").replace(".","");
		uuid+=r;
		
		var _o=$.extend({},_tree);//后面的覆盖前面的
		_o.id="tree"+uuid;
		_o.el=this;
		
		var _c=$.extend({},options,$.fn.TreeWidget.defaults);//后面的覆盖前面的
		_o.init(_c);
		return _o;
    }
	
})();