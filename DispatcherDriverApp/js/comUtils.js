var utils = {
	/*获取dom对象
	 * @ str: 元素名，class 或者 id
	 * @falg : 为true 表示返回所有的元素，为false 表示返回第一个，默认返回第一个
	 */
	getEl:function(str,flag){
		if(flag){
			return document.querySelectorAll(str);
		}else{
			return document.querySelector(str);
		}
	},

	/* 加载数据
	 * @options.url : 请求的地址
	 * @options.data: 数据
	 * @options.success: 请求成功执行的函数
	 * @options.error: 请求失败执行的函数
	 */
	loadData:function(options) {
		var _data =  {
			//固定的参数 ，token 之类的
			token: localStorage.getItem("token"),
			lang: 'zh'
		};
		if(! options.data){
			options.data = {};
		};
		mui.extend(_data,options.data);
		console.log(JSON.stringify(_data));
		console.log(options.url);
		mui.ajax(options.url,{
			data:_data,
			dataType:'json',//服务器返回json格式数据
			type:options.type || 'post',//HTTP请求类型
			timeout:10000,//超时时间设置为10秒；
//			headers:{'Content-Type':'application/json'},	              
			success:function(result){
				options.success(result);
			},
			error:function(xhr,type,errorThrown){
				//异常处理；
				console.log(type);
				if(options.error){
					options.error();
				}
			}
		});
	},

	/* 加载新的页面
	 * 
	 * @options.url : 要打开的页面
	 * @options.id : 页面的id
	 */
	openNewWindow:function(options){
		var datas = options.datas || {};
		mui.openWindow({
			url: options.url,
			id: options.id,
			styles: {
				top: '0', //新页面顶部位置
				bottom: '0' //新页面底部位置
			},
			extras:datas,
			show: {
				aniShow: 'slide-in-right',
				duration: 150
			},
			waiting: {
				autoShow: false
			},
			createNew:false,
			styles: {
				hardwareAccelerated: true
			}
		});
	}
	
}