var level_num = 1;
var deptData;
//初始化区域滚动
mui('.mui-scroll-wrapper').scroll({indicators: false});

mui.plusReady(function() {
	
	//初始化单页view
	// var viewApi = mui('#app').view({
	// 	defaultPage: '#dept_level_1'
	// });

});

	
//$("#searchDept").on("tap",function(){
//	return false;
//});
//改变部门选择展示框的宽度
function changeBoxWidth(){
	var w = 0;
	$(".dept-select-name").each(function(){
		w += $(this).width();	
	});
	$(".dept-levelshow-box").width(w+20);
}

var viewApi,view;
var deptData = {
//	url:httpServer + 'DispatcherAppWeb/common/getDeptList.json',
	url:'./js/data/deptdata01.json',
	data:{},
	type:"get",
	success:function(result){
		if(result.code == 0 && result.data){
			console.log(result.data);
			var deptData = toTreeData(result.data);
			console.log("deptData");
			console.log(deptData);
			var html = '';
			var str = createDept(result.data,0,html);
			$("body").append(str);
			//初始化第一页
			var viewApi = mui('#app').view({
				defaultPage: '#dept_level_0'
			});
			
			deptData = getTreeArray(result.data,-1,[]);
			$.selectSuggest('searchDept',deptData);
			
			//测试代码
			$("#app").on("tap",".dept-select-name",function(){
				if (viewApi.canBack()) { //如果view可以后退，则执行view的后退
					console.log($(this).find("span").attr("data-href"))
			　　　　   viewApi.backTo($(this).find("span").attr("data-href"));
			　　    } 
				var self = this;
				setTimeout(function(){
					$(self).nextAll().remove();
					$(self).remove();
					if($(".dept-select-name").length == 0){
						$(".dept-select-level .dept-levelshow-box").append('<span class="dept-select-name first-page-title" >首页</span>');
					}
				},300);
			});
		}
	},
	error:function(error){console.log(error)}
};
utils.ajaxFn(deptData);

//点击下一级，生成头部显示名称
$("#app").on("tap",".next-level",function(){
	if($(".dept-select-level").find(".first-page-title").length){
		$(".first-page-title").remove();
	}
	var deptName =  $(this).siblings(".radio_list").find(".item-title").text();
	var linkLevel = $(this).parents(".dept-content").attr("id");
	if($(".dept-select-name").length > 0){
		// var str = '<span class="dept-select-name"><a href="#'+ linkLevel +'"><em>></em>'+ deptName +'</a></span>';
		var str = '<span class="dept-select-name"><span data-href="#'+ linkLevel +'"><em>></em>'+ deptName +'</span></span>';
	}else{
		var str = '<span class="dept-select-name"><span data-href="#'+ linkLevel +'">'+ deptName +'</span></span>';
	}
	$(".dept-select-level .dept-levelshow-box").append(str);
	changeBoxWidth();
});

//点击上面的导航返回之前的页面
// $("#app").on("tap",".dept-select-name",function(){
// 	// window.open(this.href);
// 	if (viewApi.canBack()) { //如果view可以后退，则执行view的后退
// 　　　　viewApi.backTo($(this).attr("href"));
// 　　} 
// 	var self = this;
// 	setTimeout(function(){
// 		$(self).nextAll().remove();
// 		$(self).remove();
// 		if($(".dept-select-name").length == 0){
// 			$(".dept-select-level .dept-levelshow-box").append('<span class="dept-select-name first-page-title" >首页</span>');
// 		}
// 	},300);
// 	return false;
// });


//树形结构生成dom
function createDept(result,levelNum,deptHtml){
	if(result && result.length){
		var str = '';
		var _html = '<div id="dept_level_'+( levelNum ? levelNum : 0)+'" class="mui-page dept-content" >\
							<div class="mui-page-content">\
								<div class="mui-scroll-wrapper">\
									<div class="mui-scroll">\
										<ul>';

		for(var i = 0;i<result.length;i++){
			var children = result[i].children;
			if(!children || !children.length){
				_html += createDeptLi(result[i]);
			}else{
				str += createDeptLi(result[i]);
				var _levelNum = Number(result[i].deptId) + 1;
				deptHtml += createDept(result[i].children,_levelNum,'');
			}
		}
		_html += str;
		_html += 				'</ul>\
							</div>\
						</div>\
					</div>\
				</div>'	;

		deptHtml += _html;
	}
	return deptHtml;
}
function createDeptLi(data){
	var _html = '';
	if(data){
		var nextHtml = '';
		if(data.children && data.children.length){
			if(data.deptId){
				nextHtml = '<a href="#dept_level_'+ (data.deptId+1) +'" class="next-level mui-navigate-right">\
					<span>下级</span>\
				</a>';
			}else{
				nextHtml = '<a href="#dept_level_'+ (1) +'" class="next-level mui-navigate-right">\
					<span>下级</span>\
				</a>';
			}
			
		}
		_html += '<li class="" data-id="'+ data.deptId +'">\
			    	<label class="radio_list">\
			    		<span class="dept-radio">\
			    			<input name="radio" type="radio">\
							<span class="_radio"></span>\
			    		</span>\
						<div class="dept-item-box">\
							<span class="item-title">'+ data.deptName +'</span>\
						</div>\
					</label>' + nextHtml +
				  '</li>';
	}
	return _html;
}

//测试数据
var zNodes=[
	{id:0,pId:-1,name:"Aaaa"},
    {id:1,pId:0,name:"A"},
    {id:11,pId:1,name:"A1"},
    {id:12,pId:1,name:"A2"},
    {id:13,pId:1,name:"A3"},
    {id:2,pId:0,name:"B"},
    {id:21,pId:2,name:"B1"},
    {id:22,pId:2,name:"B2"},
    {id:23,pId:2,name:"B3"},
    {id:3,pId:0,name:"C"},
    {id:31,pId:3,name:"C1"},
    {id:32,pId:3,name:"C2"},
    {id:33,pId:3,name:"C3"},
    {id:34,pId:31,name:"x"},
    {id:35,pId:31,name:"y"},  
    {id:36,pId:31,name:"z"},
    {id:37,pId:36,name:"z1123"} ,
    {id:38,pId:37,name:"z123123123"}   
];
console.log(toTreeData(zNodes));

//生成树形结构的数据
function toTreeData(data){  
    var pos={};  
    var tree=[];  
    var i=0;  
    while(data.length!=0){  
        if(data[i].pId== -1){  
            tree.push({  
                id:data[i].id,  
                name:data[i].name,  
                children:[]  
            });  
            pos[data[i].id]=[tree.length-1];      
            data.splice(i,1);  
            i--;  
        }else{  
            var posArr=pos[data[i].pId];  
            if(posArr!=undefined){  
                var obj=tree[posArr[0]];  
                for(var j=1;j<posArr.length;j++){  
                    obj=obj.children[posArr[j]];  
                }  
                obj.children.push({  
                    id:data[i].id,  
                    name:data[i].name,  
                    children:[]  
                });  
                pos[data[i].id]=posArr.concat([obj.children.length-1]);  
                data.splice(i,1);  
                i--;  
            }  
        }  
        i++;  
        if(i>data.length-1){  
            i=0;  
        }  
    }  
    return tree;  
}  

//树形结构的数据解析为普通数据
function getTreeArray(data,pId,treeList){
    for (var i in data) {
    	if(data[i]){
    		var obj = {
    			id:data[i].deptId,
    			pId:pId,
    			text:data[i].deptName,
    		};
    		treeList.push(obj);
    	}
    	if(data[i].children){
    		getTreeArray(data[i].children,data[i].id,treeList);
    	}
    }
    return treeList;
}

// var testData = toTreeData(zNodes);
// console.log(getTreeArray(testData,-1,[]));



