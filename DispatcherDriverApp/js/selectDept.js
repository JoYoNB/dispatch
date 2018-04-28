var level_num = 1;

//初始化区域滚动
mui('.mui-scroll-wrapper').scroll({indicators: false});

mui.plusReady(function() {
	
	//初始化单页view
	var viewApi = mui('#app').view({
		defaultPage: '#dept_level_1'
	});
});


//改变部门选择展示框的宽度
function changeBoxWidth(){
	var w = 0;
	$(".dept-select-name").each(function(){
		w += $(this).width();	
	});
	$(".dept-levelshow-box").width(w+20);
}


//生成下一级部门
function createNextDept(data){
	if(! data){
		return;
	}
	level_num++;
	
	var _deptHtml = '<div id="dept_level_1" class="mui-page dept-content" >\
						<div class="mui-page-content">\
							<div class="mui-scroll-wrapper">\
								<div class="mui-scroll">\
									<ul>\
										<li class="mui-table-view-cell" data-id="1">\
									    	<label class="radio_list">\
									    		<span class="dept-radio">\
									    			<input name="radio" type="radio">\
													<span class="_radio"></span>\
									    		</span>\
												<div class="dept-item-box">\
													<span class="item-title">成为智能</span>\
												</div>\
											</label>\
											<a href="#dept_level_2" class="next-level mui-navigate-right">\
												<span>下级</span>\
											</a>\
									    </li>\
									</ul>\
								</div>\
							</div>	\
						</div>\
					</div>'	;
	
}
