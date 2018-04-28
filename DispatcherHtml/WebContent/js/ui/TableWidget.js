/*
 * var t=$("id1").TableWidget({
 * 		url:"/xxx/ddd/cc.json",
 * 		param:{a:1,b:2},
 * 		//data:[],//与url互斥
 * 		cells:[{
 * 			field:"",
 * 			text:"",
 * 			render:function(value,row){
 * 				return "";
 * 			}
 * 		}],
 * 		showPagination:false
 * 		pageSize:10
 * });
 * 
 * */

(function(){
	var _table={
		id:null,//控件本身Id
		el:null,//控件容器dom
		config:{},
		_defaultPageSize:20,
		param:null,//缓存每次的查询参数
		pageCount:0,
		
		
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
			
			var cells=c.cells||[];
			
			//给主容器加上样式
			if(!$(self.el).hasClass("city-table-main")){
				$(self.el).addClass("city-table-main");
			}
			
			//是否有过滤的缓存列
			var _filtersCache=self._getFilterCache();
			
			var _h1='';
			//默认是有复选框
			var hasCheckbox='';
			if(c.showSelect==false){
				hasCheckbox='hide';
			}
			//是否显示序号，默认不显示
			var hasNum='hide';
			if(c.showNum==true){
				hasNum='';
			}
			var _h2='<th class="'+hasCheckbox+'"><label class="city-ipt-label"><input id="selectAll_'+self.id+'" type="checkbox"><span class="city-ipt-checkbox"></span></label></th><th class="'+hasNum+'">序号</th>';
			for(var i=0,len=cells.length;i<len;i++){
				var cell=cells[i];
				if(!_filtersCache){
					//不存在缓存，不过滤
					_h1+='<li><label class="city-ipt-label"><input class="filterColumn" type="checkbox" checked="checked" value="'+cell.field+'"><span class="city-ipt-checkbox"></span>'+cell.text+'</label></li>';
					_h2+='<td class="'+cell.field+self.id+'"><div class="limit" title="'+cell.text+'">'+cell.text+'</div></td>';
				}else{
					if(_filtersCache[cell.field]){
						//存在缓存，则只显示勾选的列
						_h1+='<li><label class="city-ipt-label"><input class="filterColumn" type="checkbox" checked="checked" value="'+cell.field+'"><span class="city-ipt-checkbox"></span>'+cell.text+'</label></li>';
						_h2+='<td class="'+cell.field+self.id+'"><div class="limit" title="'+cell.text+'">'+cell.text+'</div></td>';
					}else{
						//不显示没勾选的列
						_h1+='<li><label class="city-ipt-label"><input class="filterColumn" type="checkbox" value="'+cell.field+'"><span class="city-ipt-checkbox"></span>'+cell.text+'</label></li>';
						_h2+='<td class="hide '+cell.field+self.id+'"><div class="limit" title="'+cell.text+'">'+cell.text+'</div></td>';
					}
				}
			}
			
			
			//过滤某些列(需要动态加)
			var _filter='';
			if(c.showFilter!=false){
				_filter='<a id="showFilterBtn_'+self.id+'" href="javascript:void(0);" class="city-table-tap"><i class="iconfont iconfont-tap"></i></a>'
					+'<div id="showFilterColumn_'+self.id+'" class="city-table-items hide">'
					+'<ul id="filter_'+self.id+'">'
					//+'<li><label class="city-ipt-label"><input type="checkbox" checked="checked"><span class="city-ipt-checkbox"></span>订单编号</label></li>'
					+_h1
					+'</ul>'
					+'</div>';
			}
			//表格主体
			var _table='<div class="city-table-box">'
				+'<table class="city-table">'
				+'	<thead>'
				+'		<tr>'
				//具体列
				+_h2
				+'		</tr>'
				+'	</thead>'
				+'	<tbody id="tbody_'+self.id+'">'
				//内容列
				+'	</tbody>'
				+'</table>'
				+'</div>';
			
			var _pageSize=self._getPageSize();
			var _h3='';
			var _pageSizeArr=[10,20,50,100,200];
			for(var j=0,jl=_pageSizeArr.length;j<jl;j++){
				if(_pageSizeArr[j]==_pageSize){
					_h3+='<option value="'+_pageSizeArr[j]+'" selected >'+_pageSizeArr[j]+'</option>';
				}else{
					_h3+='<option value="'+_pageSizeArr[j]+'">'+_pageSizeArr[j]+'</option>';
				}
			}
			
			var _footer='<div class="city-table-pages clearfix">'
				+'<div class="city-pages-left fl">'
				+'	<span class="fl">每页显示</span>'
				+'		<select id="pageSizeList_'+self.id+'" class="city-ipt-select fl">'
				+_h3
				+'		</select>'
				+'<span class="fl">条,共 <span id="totalCount_'+self.id+'">0</span>条</span>'
				+'</div>'
				+'<div class="city-pages-right fr">'
				+'	<ul id="pageNums_'+self.id+'" class="clearfix">'
				//具体页码动态生成
				+'		<li id="prev_'+self.id+'" class="prev fl hide"><a href="javascript:;" title="上一页"><i class="iconfont iconfont-prev"></i></a></li>'
				//+'		<li class="page fl active"><a href="javascript:;">1</a></li>'
				+'		<li id="next_'+self.id+'" class="next fl hide"><a href="javascript:;" title="下一页"><i class="iconfont iconfont-next"></i></a></li>'
				+'	</ul>'
				+'</div>'
				+'</div>';
			
			var _html=_filter+_table;
			if(c.showPagination==false){
				//不显示分页器
			}else{
				_html+=_footer;
			}
			$(self.el).html(_html);
			
			setTimeout(function(){
				self._afterRender();
			},200);
		},
		_bind:function(){
			var self=this;
			//绑定每个页码的点击事件
			$(self.el).on("click", ".page", function(){ 
				//此处的$(this)指$( "#testDiv")，而非$(document) 
				var _pageNum=$(this).text();
				self.gotoPageNum(_pageNum*1);
			});
			
			//上一页
			$("#prev_"+self.id).unbind("click").click(function(){
				//获取当前页
				var _currentPageNum=$("#pageNums_"+self.id).find(".active").text();
				//alert(_currentPageNum);
				_currentPageNum=_currentPageNum*1;
				var _prevPageNum=_currentPageNum-1;
				if(_prevPageNum<1){
					_prevPageNum=1;
				}
				self.gotoPageNum(_prevPageNum);
			});
			//下一页
			$("#next_"+self.id).unbind("click").click(function(){
				//获取当前页
				var _currentPageNum=$("#pageNums_"+self.id).find(".active").text();
				//alert(_currentPageNum);
				_currentPageNum=_currentPageNum*1;
				var _nextPageNum=_currentPageNum+1;
				if(_nextPageNum>self.pageCount){
					_nextPageNum=self.pageCount;
				}
				self.gotoPageNum(_nextPageNum);
			});
			//全选
			$("#selectAll_"+self.id).unbind("click").click(function(){
				var isSelectAll=$(this).attr('checked');
				if(isSelectAll){
					//选择全部
					$(".rowCheckBox").attr('checked',true);
				}else{
					//取消全部
					$(".rowCheckBox").attr('checked',false);
				}
			});
			//显示/收起操作列按钮点击事件
			$(self.el).on("click",".act-box .select-icon-box",function(){
				$(this).siblings("ul").toggleClass("hide");
			});
			
			//展示表格过滤列
			$(self.el).on("click", "#showFilterBtn_"+self.id, function(){
				//showFilterColumn_
				if($("#showFilterColumn_"+self.id).hasClass("hide")){
					$("#showFilterColumn_"+self.id).removeClass("hide");
				}else{
					$("#showFilterColumn_"+self.id).addClass("hide");
				}
			});
			
			//勾选过滤列触发事件
			$(self.el).on("click", ".filterColumn", function(){
				var f=$(this).val();
				if($(this).attr('checked')){
					//已经勾选,取消勾选
					self.showColumn(f);
				}else{
					//还没勾选,勾选
					self.hideColumn(f);
				}
			});
			
			//页码选择事件
			$("#pageSizeList_"+self.id).unbind("change").change(function(){
				var _pageSize=$(this).val();
				//alert(_pageSize);
				self.gotoPageNum(1);//重新查第一页
			});
			
			
		},
		_getPageSize:function(){
			var self=this;
			var c=self.config;
			var _pageSize=$("#pageSizeList_"+self.id).val();
			if(!_pageSize){
				_pageSize=self._defaultPageSize;
			}else if(_pageSize!=10&&_pageSize!=20&&_pageSize!=50&&_pageSize!=100&&_pageSize!=200){
				_pageSize=self._defaultPageSize;
			}
			return _pageSize;
		},
		_afterRender:function(){
			var self=this;
			var c=self.config;
			var _param=c.param||{};
			if(c.data){
				//优先加载data
				self.loadData(c.data);
			}else if(c.url){
				self.loadUrl(_param);
			}
		},
		/**
		 * 只负责查询
		 */
		loadUrl:function(pa){
			var self=this;
			var c=self.config;
			var _param=pa||{};
			
			//如果false则不传page参数
			if(self.showPagination!=false){
				//没有页码，则默认是第一页
				if(!_param.pageNum){
					_param.pageNum=1;
				}
				//如果没有页大小，则默认是20条
				_param.pageSize=self._getPageSize();
			}
			var _type=c.type||"POST";
			CommonUtils.async({
				url:c.url,
				type:_type,
				data:_param,
				success:function(result){
					if(result.code==0){
						var ret=result.data||{};
						var total=ret.total||0;
						var list=ret.list||[];
						var extParam=null;
						if(c.extParam){
							extParam=ret[c.extParam];
						}
						
						self.loadData(list,extParam);
						if(self.showPagination==false){
							//如果是false，则不设置分页器
						}else{
							//删除页码
							$(".page").remove();
							self.intPagination(_param.pageNum,total);
						}
					}
				}
			});
			
		},
		loadData:function(list,extParam){
			var self=this;
			var c=self.config;
			var cells=c.cells||[];
			
			var hasCheckbox='';
			if(c.showSelect==false){
				hasCheckbox='hide';
			}
			
			var hasNum='hide';
			if(c.showNum==true){
				hasNum='';
			}
			//缓存起来的过滤列
			var _filtersCache=self._getFilterCache();
			
			var _html='';
			for(var i=0,len=list.length;i<len;i++){
				var data=list[i];
				//有些额外返回的数据，附加给rowData，有些数据不是total和list
				if(c.extParam){
					data[c.extParam]=extParam;
				}
				var numClass='num';
				if(i<3){
					numClass='num top';
				}
				_html+='<tr><th class="'+hasCheckbox+'"><label class="city-ipt-label"><input type="checkbox" class="rowCheckBox" value="'+data.id+'"><span class="city-ipt-checkbox"></span></label></th><td class="'+hasNum+'"><span class="'+numClass+'">'+(i+1)+'</span></td>';
				//组装每列
				for(var j=0,jl=cells.length;j<jl;j++){
					var cell=cells[j];
					var field=cell.field;
					var value=data[field];
					if(typeof cell.render === 'function'){
						value=cell.render(value,data);
					}
					//自定义样式
					var _style=cell.style;
					var _s='';
					if(_style&&_style!=''){
						_s='style="'+_style+'"';
					}
					
					var _columnHide='';
					if(!_filtersCache){
						_columnHide='';
					}else{
						if(_filtersCache[field]){
							//显示勾选的
							_columnHide='';
						}else{
							//没勾选的，不显示
							_columnHide='hide';
						}
					}
					
					if("operation"==field){
						var _btns='';
						//如果存在render，则buttons的配置则失效
						if(typeof cell.render === 'function'){
							/*里面类似这种
							 * 
							 * <li><a href="javascript:xxx('+row.id+');">编辑</a></li>
							 * <li><a href="javascript:xxx('+row.id+');">删除</a></li>
							 * <li><a href="javascript:xxx('+row.id+');">失效</a></li>
							 * */
							_btns=cell.render(value,data);
							var patt = /<a[^>]*>[^<]*<\/a>/;
							var _button = patt.exec(_btns)[0];
							var button0= _button.replace('<a','<a class="first-act" ');
							_html+='<td class="'+_columnHide+' '+field+self.id+'"><div class="act-box">'
							+button0
							+'<ul class="hide">'
							+_btns.replace("<li>"+_button+"</li>","")
							+'</ul>'
							+'<span class="select-icon-box"><i class="select-icon icon-arrDown-fill icon"></i></span>'
							+'</div></td>';
						}else{
							//操作列不一样，是定义按钮组
							var buttons=cell.buttons;
							var text=cell.text;
							var _btn0=buttons[0];
							var _btn0Click=_btn0.click;
							for(var m=1,mLen=buttons.length;m<mLen;m++){
								var button=buttons[m];
								var authCode=button.authCode;//错做的权限code
								//如果登录用户无此权限则不显示该按钮
								if(authCode&&authCode!=""&&!CommonUtils.hasAuth(authCode)){
									continue;
								}
								var _btnClick=button.click;
								_btnName=button.name;
								if(button.render){
									_btnName=button.render(data);
								}
								_btns+='<li><a href="javascript:('+_btnClick+')('+data.id+');">'+_btnName+'</a></li>';
							}
							_html+='<td class="'+_columnHide+' '+field+self.id+'"><div class="act-box">'
							+'	<a class="first-act" href="javascript:('+_btn0Click+')('+data.id+');">'+_btn0.name+'</a>'
							+'	<ul class="hide">'
							+_btns
							+'	</ul>'
							+'	<span class="select-icon-box"><i class="select-icon icon-arrDown-fill icon"></i></span>'
							+'</div></td>';
						}
					}else{
						if(typeof(value) == 'undefined' || value == null){
							value = "";
						}
						_html+='<td '+_s+' class="'+_columnHide+' '+field+self.id+'"><div class="limit" title="'+data[field]+'">'+value+'</div></td>';
					}
					
				}
				_html+='</tr>';
			}
			$("#tbody_"+self.id).html(_html);
		},
		intPagination:function(currentPageNum,total){
			var self=this;
			//设置总条数
			$("#totalCount_"+self.id).html(total);
			var _pageSize=self._getPageSize();
			//根据总行数和页大小初始化分页器
			var pageCount=Math.ceil((total/_pageSize));
			self.pageCount=pageCount;
			currentPageNum=currentPageNum*1;
			
			if(currentPageNum<=5){
				//前5条
				//没有上一页
				$("#prev_"+self.id).addClass("hide");
				var hasMorePage=false;
				var showCount=pageCount;//最多显示10个页码
				if(showCount>10){
					showCount=10;
					hasMorePage=true;
				}
				
				var _html='';
				for(var i=1;i<=showCount;i++){
					var _active='';
					if(i==currentPageNum){
						_active='active';
					}
					_html+='<li class="page fl '+_active+'"><a href="javascript:void(0);">'+i+'</a></li>';
				}
				$("#prev_"+self.id).after(_html);
				//如果还有更多的页，则显示下一页按钮
				if(hasMorePage){
					$("#next_"+self.id).removeClass("hide");
				}
			}else if(currentPageNum>=(pageCount-5)){
				//倒数第6个时
				//没有下一页按钮
				$("#next_"+self.id).addClass("hide");
				var e=pageCount;
				var s=pageCount-9;
				var hasPre=false;
				if(s<1){
					s=1;
				}
				if(s==1){
					hasPre=false;
				}else{
					hasPre=true;
				}
				var _html='';
				for(var i=s;i<=e;i++){
					var _active='';
					if(i==currentPageNum){
						_active='active';
					}
					_html+='<li class="page fl '+_active+'"><a href="javascript:void(0);">'+i+'</a></li>';
				}
				$("#prev_"+self.id).after(_html);
				if(hasPre){
					$("#prev_"+self.id).removeClass("hide");
				}else{
					$("#prev_"+self.id).addClass("hide");
				}
			}else{
				//中间的页码
				var s=currentPageNum-4;
				var e=currentPageNum+5;
				if(s<1){
					s=1;
				}
				if(e>pageCount){
					e=pageCount;
				}
				if(s==1){
					$("#prev_"+self.id).addClass("hide");
				}else{
					$("#prev_"+self.id).removeClass("hide");
				}
				if(e==pageCount){
					$("#next_"+self.id).addClass("hide");
				}else{
					$("#next_"+self.id).removeClass("hide");
				}
				var _html='';
				for(var i=s;i<=e;i++){
					var _active='';
					if(i==currentPageNum){
						_active='active';
					}
					_html+='<li class="page fl '+_active+'"><a href="javascript:void(0);">'+i+'</a></li>';
				}
				$("#prev_"+self.id).after(_html);
			}
			
		},
		/**
		 * 用于翻页
		 * @param pageNum
		 * @returns
		 */
		gotoPageNum:function(pageNum){
			var self=this;
			//用上次的查询条件
			var c=self.config;
			var _param=self.param||{};
			_param.pageNum=pageNum;
			self.loadUrl(_param);
		},
		/**
		 * 用于刷新
		 */
		reload:function(){
			var self=this;
			var c=self.config;
			//清空上次查询的参数
			self.param={};
			var _param={};
			_param.pageNum=1;
			
			self.loadUrl(_param);
		},
		/**
		 * 用于选择条件查询
		 */
		query:function(param){
			var self=this;
			//替换上次查询参数
			var c=self.config;
			var _param=param||{};
			self.param=_param;
			_param.pageNum=1;
			self.loadUrl(_param);
		},
		/**
		 * 获取选中的id
		 * @returns
		 */
		getSelectedRowIds:function(){
			var self=this;
			var ids=[];
			$.each($('.rowCheckBox:checkbox:checked'),function(){
				ids.push($(this).val());
            });
			return ids;
		},
		hideColumn:function(field){
			var self=this;
			$("."+field+self.id).addClass("hide");
			//缓存选择列
			self._setFilterCache();
		},
		showColumn:function(field){
			var self=this;
			$("."+field+self.id).removeClass("hide");
			//缓存选择列
			self._setFilterCache();
		},
		_setFilterCache:function(){
			var self=this;
			if(self.config.filterKey){
				//获取当前勾选的列
				var selectdColumns=$(self.el).find(".filterColumn:checked");
				//console.info(selectdColumns.length);
				var _arr=[];
				$(self.el).find(".filterColumn:checked").each(function(){
				    //alert($(this).text())
				    _arr.push($(this).val());
				});
				//console.info(_arr);
				var _value=_arr.join(",");
				//console.info(_value);
				var userId=CommonUtils.Cookie.get(Constant.PROJECT_NAME+"_user_id");
				var _key=Constant.PROJECT_NAME+"_"+userId+"_"+self.config.filterKey;//项目名+用户id+filterKey
				CommonUtils.Cookie.add(_key,_value);
			}
		},
		_getFilterCache:function(){
			var self=this;
			
			if(self.config.filterKey){
				var userId=CommonUtils.Cookie.get(Constant.PROJECT_NAME+"_user_id");
				var _key=Constant.PROJECT_NAME+"_"+userId+"_"+self.config.filterKey;//项目名+用户id+filterKey
				var _cache=CommonUtils.Cookie.get(_key);
				if(!_cache||_cache==""){
					return null;
				}else{
					var _arr=_cache.split("%2C");
					var _ret={};
					for(var i=0,len=_arr.length;i<len;i++){
						_ret[_arr[i]]=_arr[i];
					}
					return _ret;
				}
			}
			return null;
		}
		
		
	}
	
	//封装成jquery的控件
	$.fn.TableWidget=function(options){
		//生成uuid
		var uuid=(new Date()).getTime();
		var r=(Math.random()+"").replace(".","");
		uuid+=r;
		
		var _o=$.extend({},_table);//后面的覆盖前面的
		_o.id="t"+uuid;
		_o.el=this;
		
		var _c=$.extend({},options,$.fn.TableWidget.defaults);//后面的覆盖前面的
		_o.init(_c);
		return _o;
    }
	

})();