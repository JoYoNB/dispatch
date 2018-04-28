(function($, window, undefined){

	//-	接口服务器地址
	var httpServer = "http://192.168.0.189:8086";

	(function(w) {
		var immersed = 0;
		var ms = (/Html5Plus\/.+\s\(.*(Immersed\/(\d+\.?\d*).*)\)/gi).exec(navigator.userAgent);
		if(ms && ms.length >= 3) {
			immersed = parseFloat(ms[2]);
		}
		w.immersed = immersed;
		if(!immersed) {
			return;
		}
		var headerClass = document.querySelector('.mui-bar.mui-bar-nav');
		headerClass && (headerClass.style.paddingTop = immersed + 'px', headerClass.style.height = immersed + 56 + 'px');

		var contentClass = document.querySelector('.mui-bar-nav ~ .mui-content');
		contentClass && (contentClass.style.paddingTop = immersed + 56 + 'px');
	})(window);

	//-	公共方法
	var CommonUtils = {
		token:localStorage.getItem("_disp_" + "token"),
		sessionKey: '1155561651616',
		sign: '48946313131351',
		async: function(opts){
			if(!opts || !opts.url){
				return;
			}
			var url = (opts.url).indexOf('http')==0 ? opts.url : httpServer+opts.url;
			var _opts = {};
			this.sign && (_opts.sign = this.sign);
			this.sessionKey && (_opts.sessionKey = this.sessionKey);
			this.token && (_opts.token = this.token);
			console.info(JSON.stringify(_opts));
			var data = $.extend({}, _opts, opts.data);
			// console.log('\"'+url+'\" | 参数: '+JSON.stringify(data));
			// plus.nativeUI.showWaiting();
			$.ajax(url, {
				data: data,
				dataType: opts.dataType || 'json',
				type: opts.type || 'get',
				timeout: opts.timeout || 30000,
				success: function(res){
					 console.log(url+' | success: '+JSON.stringify(res));
					if(res.code == 0){
						typeof opts.success === 'function' && opts.success(res);
					}else if(res.code==1003){
						mui.toast("权限不足"); 
					}else if(res.code==1004){
						mui.toast("请重新登录"); 
					}else{
						typeof opts.error === 'function' && opts.error(res);
					}
				},
				error: function(res){
					console.log(url+' | error: '+JSON.stringify(arguments));
					typeof opts.error === 'function' && opts.error(res);
				},
				complete: function(){
					// plus.nativeUI.closeWaiting();
					typeof opts.complete === 'function' && opts.complete();
				}
			});
		},
		//-	创建公共的头部窗口来打开页面
		headWebview: function(opts){
			opts = opts || {};
			var t = opts.url || new Date().getTime();
			var _webview_ = $.openWindow('head.html', 'head_'+t, {
				extras: {options: opts},
				show: {autoShow: false},
				waiting: {autoShow: false}
			});
			setTimeout(function(){
				_webview_.show();
			}, 0);
		},
		//-	获取日期
		getDateFn: function(time, format){
			format = format || 'yyyy-mm-dd';
			var now = time ? new Date(time) : new Date();
			var y = now.getFullYear();
			var m = now.getMonth()+1;
			var d = now.getDate();
			var h = now.getHours();
			var min = now.getMinutes();
			var s = now.getSeconds();
			m = m<10 ? '0'+m : m;
			d = d<10 ? '0'+d : d;
			h = h<10 ? '0'+h : h;
			min = min<10 ? '0'+min : min;
			s = s<10 ? '0'+s : s;

			var res = '';
			switch(format){
				case 'yyyy': 				res = y; 									break;
				case 'mm': 					res = m; 									break;
				case 'dd': 					res = d; 									break;
				case 'hh': 					res = h; 									break;
				case 'ii': 					res = i; 									break;
				case 'ss': 					res = s; 									break;
				case 'mm-dd': 				res = m+'-'+d; 								break;
				case 'hh:ii': 				res = h+':'+min;							break;
				case 'hh:ii:ss': 			res = h+':'+min+':'+s;						break;
				case 'yyyy-mm': 			res = y+'-'+m; 								break;
				case 'yyyy-mm-dd': 			res = y+'-'+m+'-'+d; 						break;
				case 'yyyy-mm-dd hh:ii': 	res = y+'-'+m+'-'+d+' '+h+':'+min; 			break;
				case 'yyyy-mm-dd hh:ii:ss': res = y+'-'+m+'-'+d+' '+h+':'+min+':'+s;	break;
			}
			return res;
		},
		//-	获取每周前后日期
		getWeekDay: function(num, time){
			var now = time ? new Date(time) : new Date();
			var start, end;
			var d = now.getDay();
			start = this.getDateFn( now.getTime() - (d-1-7*num)*24*60*60*1000 );
			if(num == 0){
				end = this.getDateFn();
			}else{
				end = this.getDateFn( now.getTime() + (7-d+7*num)*24*60*60*1000 );
			}
			return [start, end];
		},
		//-	获取每月前后日期
		getMonthDay: function(time){
			if(typeof time == 'string' && time!=''){
				time = time.substring(0, 7);
				time = time.replace(/-/g, '/') + '/01';
			}
			var now = time ? new Date(time) : new Date();
			var y = now.getFullYear();
			var m = now.getMonth()+1;
			var start = this.getDateFn( (new Date(y, m-1, 1)).getTime() );
			var end = this.getDateFn( (new Date(y, m, 0)).getTime() );
			return [start, end];
		},
		//-	获取往后x天日期
		getNextSevenDay: function(time, num){
			num = num || 7;
			var days = [];
			var now = time ? new Date(time).getTime() : new Date().getTime();
			for(var i = 0; i < num; i++){
				days.push( this.getDateFn( now + i*1000*60*60*24, 'mm-dd' ) );
			}
			return days;
		},
		//-	七日时间选择
		popPickerSevenDays: function(opts, callback){
			opts = opts || {ele: document.body};	//, selected: [0, 0]
			var picker = opts.ele.picker;
			if(!picker){
				var picker = opts.ele.picker = new $.PopPicker({layer: 2});
			}
			var times = [
				{value:'0',text:'00:00-01:00'},
				{value:'1',text:'01:00-02:00'},
				{value:'2',text:'02:00-03:00'},
				{value:'3',text:'03:00-04:00'},
				{value:'4',text:'04:00-05:00'},
				{value:'5',text:'05:00-06:00'},
				{value:'6',text:'06:00-07:00'},
				{value:'7',text:'07:00-08:00'},
				{value:'8',text:'08:00-09:00'},
				{value:'9',text:'09:00-10:00'},
				{value:'10',text:'10:00-11:00'},
				{value:'11',text:'11:00-12:00'},
				{value:'12',text:'12:00-13:00'},
				{value:'13',text:'13:00-14:00'},
				{value:'14',text:'14:00-15:00'},
				{value:'15',text:'15:00-16:00'},
				{value:'16',text:'16:00-17:00'},
				{value:'17',text:'17:00-18:00'},
				{value:'18',text:'18:00-19:00'},
				{value:'19',text:'19:00-20:00'},
				{value:'20',text:'20:00-21:00'},
				{value:'21',text:'21:00-22:00'},
				{value:'22',text:'22:00-23:00'},
				{value:'23',text:'23:00-24:00'},
			]
			var days = this.getNextSevenDay(opts.sTime);
			var nowDay = this.getDateFn(null, 'mm-dd');
			var data = [];
			for(var i = 0; i < days.length; i++){
				var d = days[i].replace('-', '月')+'日';
				var n = i+'';
				data.push({value: n,text: d, day: days[i], children: times});
				if(i==0 && days[i]==nowDay){
					var hh = this.getDateFn(null, 'hh');
					var _times_ = times.slice(hh);
					data[0].children = _times_;
				}
			}
			picker.setData(data);
			picker.show(function(item){
				typeof callback==='function' && callback(item);
			});
		},
		//-	货物类型
		popPickerGoods: function(opts, callback){
			opts = opts || {ele: document.body};
			var picker = opts.ele.picker;
			if(!picker){
				var picker = opts.ele.picker = new $.PopPicker();
			}
			var data = [
				{value:'1',text:'货物类型1'},
				{value:'2',text:'货物类型2'},
				{value:'3',text:'货物类型3'},
				{value:'4',text:'货物类型4'},
				{value:'5',text:'货物类型5'},
			]
			picker.setData(data);
			picker.show(function(item){
				typeof callback==='function' && callback(item);
			});
		},
		//-	验证信息
		proxyValid: function(val, arr){
			var p = new ProxyValid();
		    p.add(val,arr);
		    var msg = p.start();
		    p = null;
		    return msg;
		},
		//-	获取表单的信息  含有name属性
		serializeFn: function(ele, str){
			str = str || 'input, select';
			var array = {};
			var eles = document.querySelector(ele).querySelectorAll(str);
			for(var i = 0; i < eles.length; i++){
				var item = eles[i];
				var name = item.getAttribute('name');
				if(!!name){
					if(item.type=='radio'){
						if(!array[name]){
							array[name] = '';
						}
						if(item.checked){
							array[name] = item.value;
						}
					}else if(item.type=='checkbox'){
						if(!array[name]){
							array[name] = [];
						}
						if(item.checked){
							array[name].push(item.value);
						}
					}else{
						array[name] = item.value;
					}
				}
			}
			return array;
		}
	};

	$.fn.closest = function(selector){
		var el = this;
		var matchesSelector = el.matches || el.webkitMatchesSelector || el.mozMatchesSelector || el.msMatchesSelector;

	    while (el) {
	        if (matchesSelector.call(el, selector)) {
	            break;
	        }
	        el = el.parentElement;
	    }
	    return el;
	}


	var Strategy = {
		//判断是否为空
		empty: function(value, ele, errormsg){
	        if(value == ''){
	            return errormsg;
	        }
	    },
	    //判断是否是email
	    isEmail: function(value,errormsg){
	        if(value!='' && !/^([\w]+)([\w_\.\-])+\@(([\w\-])+\.)+([\w]{2,4})+$/i.test(value)){
	            return errormsg;
	        }
	    },
	    //是否是正确的手机号码,5到20个数字
	    isPhone: function(value,errormsg){
	        if(value!='' && !/^([0-9]){5,20}$/.test(value)){
	            return errormsg;
	        }
	    },
	    //是否是整数
	    isInteger: function(value,errormsg){
	        if(value!='' && !/^[\d]+$/.test(value)){
	            return errormsg;
	        }
	    },
	    //是否是数字
	    isNumber: function(value,errormsg){
	        if(value!='' && !/^\-?[\d]+\.?[\d]*$/.test(value)){
	            return errormsg;
	        }
	    },
	    //是否是正数
	    isPosNum: function(value,errormsg){
	        if(value!='' && !/^[\d]+\.?[\d]*$/.test(value)){
	            return errormsg;
	        }
	    },
	    //是否是日期  格式：2017-10-16 12:00:00
	    isDate: function(value,errormsg){
	        if(value!='' && !/^(\d{4}-\d{1,2}-\d{1,2})(\s+\d{1,2}:\d{1,2}(:\d{1,2})?)?$/.test(value)){
	            return errormsg;
	        }
	    },
	    //两个字符串是否一致
	    isIdentical: function(value, reValue, errormsg){
	    	if(value!='' && value!=reValue){
	    		return errormsg;
	    	}
	    },
	    //两个数值大小关系
	    numsSize: function(value, num1, num2, errormsg){
	    	num1 = Number(num1);
	    	num2 = Number(num2);
	    	if(value!='' && num1>=num2){
	    		return errormsg;
	    	}
	    },
	    //小数精度限制
	    decimalPre: function(value,len,errormsg){
	    	len = Number(len);
	        if(value!=''){
	        	var index = value.indexOf('.');
	        	var decimal = value.substring(index+1);
	        	if(index>-1 && decimal.length > len){
	        		return errormsg;
	        	}
	        }
	    },
	    //密码是大写字母、小写字母、数字、特殊字符，其中两种及以上的组合，不包括空白字符
	    password: function(value,errormsg){
	        var len = value.length;
	        if(value!='' && !/^(?![A-Z]+$)(?![a-z]+$)(?!\d+$)(?![\W]+$)\S+$/.test(value)){
	            return errormsg;
	        }
	    },
	    //字符串最大长度
	    maxLen: function(value,len,errormsg){
	    	var len = Number(len);
	    	if(value!='' && value.length>len){
	            return errormsg;
	        }
	    },
	    //字符串最小长度
	    minLen: function(value,len,errormsg){
	    	var len = Number(len);
	    	if(value!='' && value.length<len){
	            return errormsg;
	        }
	    },
	    //字符串长度某个范围之内
	    lenRange: function(value, min, max, errormsg){
	    	var _min = Number(min);
	    	var _max = Number(max);
	    	if(value!=''){
	    		if(value.length<=_min || value.length>=_max){
					return errormsg;
	    		}
	        }
	    },
	    //最大数值
	    maxCount: function(value,len,errormsg){
	    	var len = Number(len);
	    	if(value!=''){
	    		value = Number(value);
	    		if(value>len){
					return errormsg;
	    		}
	        }
	    },
	    //最小数值
	    minCount: function(value,len,errormsg){
	    	var len = Number(len);
	    	if(value!=''){
	    		value = Number(value);
	    		if(value<len){
					return errormsg;
	    		}
	        }
	    },
	    //数值某个范围之内
	    countRange: function(value, min, max, errormsg){
	    	var _min = Number(min);
	    	var _max = Number(max);
	    	if(value!=''){
	    		value = Number(value);
	    		if(value<=_min || value>=_max){
					return errormsg;
	    		}
	        }
	    },
	    //机构编码格式为2-20个字母或数字
	    deptCode: function(value,errormsg){
	    	if(value!='' && !/^((([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[\w]*)$/.test(value) ){
	            return errormsg;
	        }
	    },
	    //格式限制中文或英文组合
	    userName: function(value,errormsg){
	    	if(value!='' && !/^[a-zA-Z\u2E80-\u9FFF]+$/.test(value)){
	            return errormsg;
	        }
	    },
	    //服务平台编码
	    code: function(value,errormsg){
	    	if(value!='' && !/^[H|S|Z|h|s|z]{1}[G|T|g|t]{1}[0-9]{2}$/.test(value)){
	            return errormsg;
	        }
	    },
	    //图片文件类型是jpg,png
	    picFiletype: function(value,errormsg){
	    	if(value!='' && !/(jpg|png|jpeg|bmp)$/.test(value)){
	    		return errormsg;
	    	}
	    }
	}

	//- 代理验证信息
	function ProxyValid(){
	    this.cache = [];
	};

	ProxyValid.prototype.add = function(val,rules){
	    var self = this;
	    for(var i = 0, rule; rule = rules[i++] ;){
	        (function(rule){
				var arr = rule.s.split(':');
	            var key = arr.shift();
	            arr.unshift( val );
	            if(rule.status && rule.status==1){
	                for(var i in rule.fns){
	                    arr.push(rule.fns[i]);
	                }
	            }else{
	                arr.push(rule.msg);
	            }
	            self.cache.push(function(){
	                return Strategy[key].apply(self,arr);
	            });
	        })(rule);
	    }
	}

	ProxyValid.prototype.start = function(){
	    for(var i = 0, rule; rule = this.cache[i++]; ){
	        var msg = rule();
	        if(msg){
	            return msg;
	        }
	    }
	}

	$(document).on('tap', 'label', function(){
		var c = this.querySelector('input[type="checkbox"], input[type="radio"]').checked;
		var a = c ? 'remove' : 'add';
		this.querySelector('.ct-ipt-checkbox, .ct-ipt-radio').classList[a]('checked');
	})

	window.CommonUtils = CommonUtils;

})(mui, window);