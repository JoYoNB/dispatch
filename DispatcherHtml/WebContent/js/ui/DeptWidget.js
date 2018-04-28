/*
 * 部门控件
 * 
 * */
(function(){
	var _deptWidget={
		id:null,
		el:null,
		config:null,
		param:null,//存储上次查询的条件
		_defaultPageSize:10,
		userDeptLevel:1,//用户部门的级别
		totalPageCount:0,
		selectedValue:"",
		selectedText:"",
		
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
			
			if(!self.el.hasClass("ipt_box")){
				//加上默认样式
				self.el.addClass("ipt_box")
			}
			var _html='';
			//外层显示text
			var _showHtml='<input id="showText_'+self.id+'" type="text" class="city-ipt-text edit-ipt-len dept-select" readonly="true" placeholder="请选择部门">';
			
			_html+=_showHtml;
			$(self.el).html(_html);
		},
		_bind:function(){
			var self=this;
			//点击弹出部门选择弹层
			$(self.el).on("click", "#showText_"+self.id, function(){ 
				//此处的$(this)指$( "#testDiv")，而非$(document) 
				self._createDeptHtml();
			});
			
			//关闭弹层
			$(document).off("click","#dptclose_"+self.id).on("click","#dptclose_"+self.id,function(){
				$("#dptDiv2_"+self.id).hide();
				$("#dptDiv1_"+self.id).hide();
				//$(".org-module").hide();
				//$(".pop-module-mask").hide();
				self._deleteDeptHtml();
			});
			
			//绑定每个页码的点击事件
			$(document).off("click",".dptPage").on("click", ".dptPage", function(){ 
				//此处的$(this)指$( "#testDiv")，而非$(document) 
				var _pageNum=$(this).text();
				self.gotoPageNum(_pageNum*1);
			});
			//上一页
			$(document).off("click","#deptprev_"+self.id).on("click", "#deptprev_"+self.id, function(){ 
				//此处的$(this)指$( "#testDiv")，而非$(document) 
				//获取当前页
				var _currentPageNum=$(".dptPage").find(".active").text();
				_currentPageNum=_currentPageNum*1;
				var _pageNum=_currentPageNum-1;
				if(_pageNum<1){
					_pageNum=1;
				}
				self.gotoPageNum(_pageNum);
			});
			//下一页
			$(document).off("click","#deptnext_"+self.id).on("click", "#deptnext_"+self.id, function(){ 
				//此处的$(this)指$( "#testDiv")，而非$(document) 
				//获取当前页
				var _currentPageNum=$(".dptPage").find(".active").text();
				_currentPageNum=_currentPageNum*1;
				var _pageNum=_currentPageNum+1;
				if(_pageNum>self.totalPageCount){
					_pageNum=self.totalPageCount;
				}
				self.gotoPageNum(_pageNum);
			});
			
			//选择显示条数
			$(document).off("click","#dptPageSize_"+self.id).on("click", "#dptPageSize_"+self.id, function(){ 
				//此处的$(this)指$( "#testDiv")，而非$(document) 
				self.query();
			});
			
			//点击查询按钮
			$(document).off("click","#deptSearchBtn_"+self.id).on("click", "#deptSearchBtn_"+self.id, function(){ 
				//此处的$(this)指$( "#testDiv")，而非$(document) 
				self.query();
			});
			
			//行点击事件
			$(document).on("click", ".deptRow", function(){ 
				//此处的$(this)指$( "#testDiv")，而非$(document) 
				var _id=$(this).attr("idata");
				var _text="";
				var children=$(this).children();
				if(children&&children.length>0){
					_text=$(children[0]).text();
				}
				self.selectedValue=_id;
				self.selectedText=_text;
				//回显到showText
				$("#showText_"+self.id).val(_text);
				//关闭弹层
				self._deleteDeptHtml();
				//执行回调函数
				if(typeof self.config.afterSelected==='function'){
					self.config.afterSelected(_id,_text);
				}
			});
			
		},
		_createDeptHtml:function(){
			var self=this;
			//清空上次的查询条件
			self.param=null;
			//蒙层
			var _deptHtml='<div id="dptDiv1_'+self.id+'" class="pop-module-mask"></div>'
				+'<div id="dptDiv2_'+self.id+'" class="pop-module org-module">'
				+'	<div class="pop-header clearfix">'
				+'		<h3 class="fl">部门选择</h3>'
				+'		<span id="dptclose_'+self.id+'" class="pop-close fr"><i class="pop-close-icon">x</i></span>'
				+'	</div>'
				+'	<div class="pop-search clearfix">'
				+'		<div class="ipt_box">'
				+'			<input id="deptName_'+self.id+'" type="text" class="ipt_text search-org"  placeholder="请输入要查找的部门名称">'
				+'			<span id="deptSearchBtn_'+self.id+'" class="search-org-btn"><i class="search-org-btn-icon iconfont icon-tubiao111"></i></span>'
				+'		</div>'
				+'		<div class="fr org-table-act">'
				+'			<div class="ipt_box">'
				+'				<span class="fl">显示级别:</span>'
				+'				<select id="deptLevel_'+self.id+'" class="city-ipt-select fl">'
				+'					<option value="1">1</option>'
				+'					<option value="2">2</option>'
				+'					<option value="3">3</option>'
				+'					<option value="4">4</option>'
				+'					<option value="5">5</option>'
				+'				</select>'
				+'			</div>'
				+'			<div class="ipt_box">'
				+'				<span class="fl">每页条数:</span>'
				+'				<select id="dptPageSize_'+self.id+'" class="city-ipt-select fl">'
				+'					<option value="10">10</option>'
				+'					<option value="20">20</option>'
				+'					<option value="50">50</option>'
				+'					<option value="100">100</option>'
				+'					<option value="200">200</option>'
				+'				</select>'
				+'			</div>'
				+'		</div>'
				+'	</div>'
				+'	<div class="pop-main clearfix">'
				+'		<div class="city-table-box">'
				+'			<table class="city-table">'
				+'				<thead>'
				+'					<tr>'
				+'						<th class="org-pop-thName"><div class="limit">机构名称</div></th>'
				+'						<th><div>上级机构</div></th>'
				+'					</tr>'
				+'				</thead>'
				+'				<tbody id="deptList_'+self.id+'">'
				//+'					<tr>'
				//+'						<td><div title="北京分公司">北京分公司</div></td>'
				//+'						<td><div></div></td>'
				//+'					</tr>'
				//+'					<tr>'
				//+'						<td><div title="硬件部"><i class="icon dept_level_icon icon_dept01"></i>硬件部</div></td>'
				//+'						<td><div>上海分公司</div></td>'
				//+'					</tr>'
				//+'					<tr>'
				//+'						<td><div title="四川太保商务车"><i class="icon dept_level_icon icon_dept02"></i>四川太保商务车</div></td>'
				//+'						<td><div>上海分公司>硬件部</div></td>'
				//+'					</tr>'
				+'				</tbody>'
				+'			</table>'
				+'		</div>'
				+'	</div>'
				+'	<div class="pop-footer">'
				+'		<div class="city-table-pages clearfix">'
				+'			<div class="city-pages-left fl">'
				+'				<span id="deptListCount_'+self.id+'" class="fl"></span>'
				+'			</div>'
				+'			<div class="city-pages-right fr">'
				+'				<ul class="clearfix">'
				+'					<li id="deptprev_'+self.id+'" class="prev fl hide"><a href="javascript:void(0);" title="上一页"><i class="iconfont iconfont-prev"></i></a></li>'
				//+'					<li class="page fl active"><a href="javascript:;">1</a></li>'
				//+'					<li class="page fl"><a href="javascript:;">2</a></li>'
				+'					<li id="deptnext_'+self.id+'" class="next fl hide"><a href="javascript:void(0);" title="下一页"><i class="iconfont iconfont-next"></i></a></li>'
				+'				</ul>'
				+'			</div>'
				+'		</div>'
				+'	</div>'
				+'</div>';
			
			$("body").append(_deptHtml);
			setTimeout(function(){
				self._afterRenderDeptSelector();
			},200);
		},
		_deleteDeptHtml:function(){
			var self=this;
			$("#dptDiv1_"+self.id).remove();
			$("#dptDiv2_"+self.id).remove();
		},
		_afterRenderDeptSelector:function(){
			var self=this;
			var param={pageNum:1};
			self.loadUrl(param);
		},
		loadUrl:function(param){
			var self=this;
			var c=self.config;
			var _param=param||{};
			
			var pageSize=$("#dptPageSize_"+self.id).val();
			_param.pageSize=pageSize;
			if(!_param.pageNum){
				_param.pageNum=1;
			}
			
			CommonUtils.async({
				url:"/DispatcherWeb/user/getDeptList.json",
				data:_param,
				success:function(result){
					if(result.code==0){
						var ret=result.data||{};
						var total=ret.total||0;
						var list=ret.list||[];
						self.userDeptLevel=ret.userDeptLevel||1;
						
						//填充表格
						var _html='';
						if(c.showAllItem){
							//如果要显示全部这一行
							_html+='<tr class="deptRow" idata=""><td><div title="全部">全部</div></td><td><div></div></td></tr>';
						}
						
						for(var i=0,len=list.length;i<len;i++){
							var dept=list[i];
							//根据不同的层级显示
							var level=dept.level||self.userDeptLevel;
							//比如用户本身部门是2级部门，则显示的时候，他本身部门就是1级部门，而他的子部门，往下迭代减1
							level=level-self.userDeptLevel+1;//减掉用户本身部门级别，再加一
							
							var _levelIcon='';
							if(level>1){
								var _l=level-1;
								_levelIcon='<i class="icon dept_level_icon icon_dept0'+_l+'"></i>';
							}
							
							_html+='<tr class="deptRow" idata="'+dept.id+'">'
								+'<td><div title="'+dept.name+'">'+_levelIcon+dept.name+'</div></td>'
								+'<td><div>'+(dept.parentName||'')+'</div></td>'
								+'</tr>';
						}
						$("#deptList_"+self.id).html(_html);
						self._genPagination(total,1);
					}
				}
			});
		},
		
		_genPagination:function(total,currentPageNum){
			var self=this;
			var c=self.config;
			//删除原来的页码
			$(".dptPage").remove();
			
			//显示总数
			$("#deptListCount_"+self.id).html("共 "+total+" 条");
			//最多只显示10个页码
			//var pageSize=c.pageSize||self._defaultPageSize;
			var pageSize=$("#dptPageSize_"+self.id).val();
			pageSize=pageSize*1;
			
			//计算总页码
			var pageCount=Math.ceil(total/pageSize);
			self.totalPageCount=pageCount;
			
			if(currentPageNum<=5){
				//没有上一页
				$("#deptprev_"+self.id).addClass("hide");
				
				var s=1;
				var e=pageCount;
				if(e>10){
					e=10;
					//还有下一页
					$("#deptnext_"+self.id).removeClass("hide");
				}else{
					//没有下一页
					$("#deptnext_"+self.id).addClass("hide");
				}
				var _html='';
				for(var i=s;i<=e;i++){
					var _active='';
					if(i==currentPageNum){
						_active='active';
					}
					//一个页面只能有一个部门选择 TODO
					_html+='<li class="page fl dptPage '+_active+'"><a href="javascript:void(0);">'+i+'</a></li>';
				}
				$("#deptprev_"+self.id).after(_html);
			}else if(currentPageNum>=(pageCount-5)){
				//倒数第6个时
				//没有下一页按钮
				$("#deptnext_"+self.id).addClass("hide");
				var s=pageCount-9;
				var e=pageCount;
				if(s<1){
					//没有上一页
					s=1;
					$("#deptprev_"+self.id).addClass("hide");
				}else{
					$("#deptprev_"+self.id).removeClass("hide");
				}
				var _html='';
				for(var i=s;i<=e;i++){
					var _active='';
					if(i==currentPageNum){
						_active='active';
					}
					//一个页面只能有一个部门选择 TODO
					_html+='<li class="page fl dptPage '+_active+'"><a href="javascript:void(0);">'+i+'</a></li>';
				}
				$("#deptprev_"+self.id).after(_html);
			}else{
				//中间的页码
				var s=currentPageNum-4;
				var e=currentPageNum+5;
				if(s<1){
					s=1;
					//没有上一页
					$("#deptprev_"+self.id).addClass("hide");
				}else{
					$("#deptprev_"+self.id).removeClass("hide");
				}
				if(e>pageCount){
					e=pageCount;
					//没有下一页
					$("#deptnext_"+self.id).addClass("hide");
				}else{
					$("#deptnext_"+self.id).removeClass("hide");
				}
				var _html='';
				for(var i=s;i<=e;i++){
					var _active='';
					if(i==currentPageNum){
						_active='active';
					}
					//一个页面只能有一个部门选择 TODO
					_html+='<li class="page fl dptPage '+_active+'"><a href="javascript:void(0);">'+i+'</a></li>';
				}
				$("#deptprev_"+self.id).after(_html);
			}
		},
		gotoPageNum:function(pageNum){
			var self=this;
			//用上次的查询条件
			var _param=self.param||{};
			_param.pageNum=pageNum;
			self.loadUrl(_param);
		},
		query:function(){
			var self=this;
			var deptName=$("#deptName_"+self.id).val();
			var _param={
				name:deptName
			};
			var level=$("#deptLevel_"+self.id).val();
			level=level*1;
			if(level>1){
				//查一级的，不用设置级别，其他的设置级别
				//比如用户本身部门是2级，但显示显示的是1级，所以他查询他的2级部门时，那么就是查询数据库中的3级部门
				level=self.userDeptLevel+level-1;
				_param.level=level;
			}
			
			//缓存查询条件
			self.param=_param;
			self.loadUrl(_param);
		},
		
		getValue:function(){
			var self=this;
			return self.selectedValue;
		},
		setValue:function(value){
			var self=this;
			self.selectedValue=value;
		},
		getText:function(){
			var self=this;
			return self.selectedText;
		},
		setText:function(text){
			var self=this;
			self.selectedText=text;
			$("#showText_"+self.id).val(text);
		}
	}
	
	//封装成jquery的控件
	$.fn.DeptWidget=function(options){
		//生成uuid
		var uuid=(new Date()).getTime();
		var r=(Math.random()+"").replace(".","");
		uuid+=r;
		
		var _o=$.extend({},_deptWidget);//后面的覆盖前面的
		_o.id="dept"+uuid;
		_o.el=this;
		
		var _c=$.extend({},options,$.fn.DeptWidget.defaults);//后面的覆盖前面的
		_o.init(_c);
		return _o;
    }
})();