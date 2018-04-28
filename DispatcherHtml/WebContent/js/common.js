/*
 * 公共常量
 * */
var Constant={
	PROJECT_NAME:"dsp",//项目名
	TOKEN_KEY:"token",
	LANGUE_IN_COOKIE:"_dsp_langue_",
	NUMBER:["0","1","2","3","4","5","6","7","8","9"],
	WORDS:["a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"]
}

var TokenUtil={
	getToken:function(){
		//获取用户的登录令牌
		var token=CommonUtils.Cookie.get(Constant.PROJECT_NAME+"_token");
		return token||"";
	}
}

var CommonUtils={
	/**
	 * 统一ajax调用方法
	 * @param param
	 */
	async:function(param){
		var _param=param||{};
		var _url=param.url;
		//如果url不存在，则退出
		if(!_url||_url==""){
			return;
		}
		
		var _dataType=_param.dataType||"json";//默认是json
		var _post=_param.type||"POST";//默认是post
		var _data=_param.data||{};
		var _async=param.async;
		if(typeof(_async)=="undefined"){
			_async=true;
		}
		
		//所有方法都加上token
		var _token=TokenUtil.getToken();
		if(!_data.token||_data.token==""){//防止iframe cookie丢失
			_data.token=_token;
		}
		if(!_data.token||_data.token==""){
			//尝试从父级窗口拿
			if(window.parent.TokenUtil){
				_data.token=window.parent.TokenUtil.getToken();
			}
		}
		//如果是登录接口，则不需要带token过去，防止token混乱
		if(_url.indexOf("login.json")>-1){
			_data.token=null;
		}
		//防止后台cookie丢失，加上语言参数
		var _lang=CommonUtils.Cookie.get(Constant.LANGUE_IN_COOKIE);
		if(!_lang||_lang==""){
			_lang="zh";
		}
		_data.lang=_lang;
		
		var _success=_param.success;//成功的执行函数
		var _error=_param.error;//失败的执行函数
		var _complete=_param.complete;//完成时执行的函数
		
		var _contentType_common="application/x-www-form-urlencoded;charset=UTF-8";//传普通参数
		var _contentType_json="application/json;charset=UTF-8";//复杂对象
		var _contentType=_param.contentType||"common";
		if("json"==_contentType){
			_contentType=_contentType_json;
			//这种情况，需要把token和lang放到url上
			_url+="?token="+_data.token+"&lang="+_data.lang;
			//要移除token和lang属性
			delete _data["token"];
			delete _data["lang"];
			_data=JSON.stringify(_data);
			//console.info(_data);
			//var ss='{"name":"1111","authList":[{"code":"11111"}]}';
			//var ss='{"name":"666","remark":"6666","authList":[{"code":"deptManagement"},{"code":"addDept"},{"code":"deleteDept"},{"code":"updateDept"}]}';
			//_data=ss;
		}else{
			_contentType=_contentType_common;
		}
		
		$.ajax({
			type:_post,
			url:_url,
			contentType:_contentType,
			dataType:_dataType,
			data:_data,
			async:_async,
			success:function(result) {
				//统一处理返回的错误结果
				if(!result || result ==""){
					MessageUtil.alert("没有结果返回");
					return;
				}else if(result.code==1004){
					//未登录
					location.href="/"+Constant.PROJECT_NAME+"/login.html";
				}else if(result.code==1003){
					//未授权
					MessageUtil.alert("没有权限",function(){
						location.href="/"+Constant.PROJECT_NAME+"/login.html";
					});
				}else if(result.code==1006){
					//sql注入拦截
					if(!(typeof(art)=="undefined")){
						MessageUtil.alert("攻击拦截",function(){
							location.href="/"+Constant.PROJECT_NAME+"/login.html";
						});
					}else{
						_success(result);
					}
				}else{
					if(typeof _success === 'function'){
						_success(result);
					}
				}
			},
			error:function(XMLHttpRequest, textStatus, errorThrown){
				if(typeof _error === 'function'){
					_error(XMLHttpRequest);
				}
			},
			complete:function(XMLHttpRequest){
				if(typeof _complete === 'function'){
					_complete(XMLHttpRequest);
				}
			}
		});
	},
	uploadFile:function(param){
		var _param=param||{};
		
		var _url=param.url;
		if(!_url||_url==""){
			return;
		}
		var _dataType=_param.dataType||"json";//默认是json
		var _post=_param.type||"POST";//默认是post
		var _data=_param.data||{};
		var _async=param.async;
		if(typeof(_async)=="undefined"){
			_async=true;
		}
		
		//加上token
		var _token=TokenUtil.getToken();
		if(!_data.token||_data.token==""){//防止iframe cookie丢失
			_data.token=_token;
		}
		//防止后台cookie丢失，加上语言参数
		var _lang=CommonUtils.Cookie.get(Constant.LANGUE_IN_COOKIE);
		if(!_lang||_lang==""){
			_lang="zh";
		}
		_data.lang=_lang;
		_url+="?token="+_data.token;
		//添加文本方式参数
		var formData=new FormData();
		for(var k in _data){
			formData.append(k,_data[k]);
		}
		
		//如果有文件files:[{id:fileElId,name:fileName}]
		var _files=_param.files||[];
		for(var i=0,len=_files.length;i<len;i++){
			var _file=_files[i];
			var fileObj=document.getElementById(_file.id).files[0]; // js 获取文件对象
			formData.append(_file.name,fileObj);
		}
		
		var _dataType=_param.dataType||"json";
		
		var _success=_param.success;//成功的执行函数
		var _error=_param.error;//失败的执行函数
		
		$.ajax({
			type:_post,
			url:_url,
			processData:false,
			data:formData,
			async:_async,
			success:function(result) {
				//统一处理返回的错误结果
				if(!result || result ==""){
					MessageUtil.alert("没有结果返回");
					return;
				}else if(result.code==1004){
					//未登录
					location.href="/"+Constant.PROJECT_NAME+"/login.html";
				}else if(result.code==1003){
					//未授权
					MessageUtil.alert("没有权限",function(){
						location.href="/"+Constant.PROJECT_NAME+"/login.html";
					});
				}else if(result.code==1006){
					//sql注入拦截
					if(!(typeof(art)=="undefined")){
						MessageUtil.alert("攻击拦截",function(){
							location.href="/"+Constant.PROJECT_NAME+"/login.html";
						});
					}else{
						_success(result);
					}
				}else{
					if(typeof _success === 'function'){
						_success(result);
					}
				}
			},
			error:function(XMLHttpRequest, textStatus, errorThrown){
				if(typeof _error === 'function'){
					_error(XMLHttpRequest);
				}
			},
			complete:function(XMLHttpRequest){
				if(typeof _complete === 'function'){
					_complete(XMLHttpRequest);
				}
			}
		});
	},
	/**
	 * 上传文件
	 * 依赖 jquery.form.min.js
	 * @param param
	 */
	uploadFile2:function(param){
		var _param=param||{};
		
		var _url=param.url;
		if(!_url||_url==""){
			return;
		}
		var uuid=(new Date()).getTime()+""+Math.random();
		uuid=uuid.replace(".","");
		//新生成一个form表单
		var _formId='_form_'+uuid;
		var _form=$('<form id="'+_formId+'" method="post" enctype="multipart/form-data"></form>').appendTo($("body"));
		//如果有其他参数，则加进去
		var _data=_param.data||{};
		//加上token
		var _token=TokenUtil.getToken();
		if(!_data.token||_data.token==""){//防止iframe cookie丢失
			_data.token=_token;
		}
		//防止后台cookie丢失，加上语言参数
		var _lang=CommonUtils.Cookie.get(Constant.LANGUE_IN_COOKIE);
		if(!_lang||_lang==""){
			_lang="zh";
		}
		_data.lang=_lang;
		
		for(var k in _data){
			$('<input name="'+k+'" type="hidden" value="'+_data[k]+'">').appendTo(_form);
		}
		_url+="?token="+_data.token;
		
		//如果有文件files:[{id:fileElId,name:fileName}]
		var _files=_param.files||[];
		for(var i=0,len=_files.length;i<len;i++){
			var file=_files[i];
			//克隆原始对象
			var cloneEl=$("#"+file.id).clone();
			//改变克隆对象的Id和name
			var _id=Math.random()+"";
			_id=_id.replace(".","");
			$(cloneEl).attr('id', _id);
			$(cloneEl).attr('name', file.name);
			//克隆对象添加到form表单
			$(cloneEl).appendTo(_form);
			//$('<input name="'+file.name+'" type="file" value="'+file.value+'" />').appendTo(_form);
		}
		
		var _cache=_param.cache||false;
		var _dataType=_param.dataType||"json";
		
		var _success=_param.success;//成功的执行函数
		var _error=_param.error;//失败的执行函数
		
		$('#'+_formId).ajaxSubmit({
			url:_url,
		    cache:_cache,
		    dataType:_dataType,
		    success: function(result) {
		    	if(!result || result ==""){
					MessageUtil.alert("没有结果返回");
					return;
				}else if(result.code==1004){
					//未登录
					location.href="/"+Constant.PROJECT_NAME+"/login.html";
				}else if(result.code==1003){
					//未授权
					MessageUtil.alert("没有权限",function(){
						location.href="/"+Constant.PROJECT_NAME+"/login.html";
					});
				}else if(result.code==1006){
					//sql注入拦截
					if(!(typeof(art)=="undefined")){
						MessageUtil.alert("攻击拦截",function(){
							location.href="/"+Constant.PROJECT_NAME+"/login.html";
						});
					}else{
						_success(result);
					}
				}else{
					if(typeof _success==='function'){
						_success(result);
					}
				}
		    	//执行完后，删除form表单
		    	$("#"+_formId).remove();
		    },
		    error:function(XMLHttpRequest, textStatus, errorThrown){
		    	if(typeof _error === 'function'){
					_error(XMLHttpRequest);
				}
		    	//执行完后，删除form表单
		    	$("#"+_formId).remove();
		    }
		});
	},
	/**
	 * 统一ajax导出方法
	 * @param param
	 */
	postExport:function(param){
		var self=this;
		param=param||{};
		var _url=param.url||"";
		if(_url==""){
			return;
		}
		var _data=param.data||{};
		//加上token
		var _token=TokenUtil.getToken();
		_data.token=_token;
		//防止后台cookie丢失，加上语言参数
		var _lang=CommonUtils.Cookie.get(Constant.LANGUE_IN_COOKIE);
		if(!_lang||_lang==""){
			_lang="zh";
		}
		_data.lang=_lang;
		
		var _uuid=new Date().getTime();
		var _iframe=document.createElement("iframe");
		_iframe.id=_uuid+"_return";
		_iframe.name=_uuid+"_return";
		_iframe.style.display="none";
		document.body.appendChild(_iframe);
		
		var _form=document.createElement("form");
		var _formId=_uuid+"_form";
		_form.id=_formId;
		_form.action=_url;
		_form.method="post";
		_form.style.display="none";
		_form.target=_uuid+"_return";//装载返回值,因为跨域，TODO　目前还没有好的办法
		
		for(var x in _data){
			var opt=document.createElement("textarea");
			opt.name=x;
			opt.value=_data[x];
			_form.appendChild(opt);
		}
		document.body.appendChild(_form);
		_form.submit();
		$("#"+_formId).remove();
	},
	/**
	 * 统一消息提醒
	 */
	Msg:{
		confirm:function(title,msg,callback){
			alert("TODO");
		},
		alert:function(msg,seconds,callback){
			alert(msg);
		},
		info:function(msg,seconds,callback){
			alert(msg);
		}
	},
	/**
	 * 获取url参数方法
	 * @param str
	 * @param name
	 * @returns
	 */
	getParam:function(name,str){
		var _str=str||location.search;
		if(!_str||!name){
			return null;
		}
		_str=_str.replace("?","");
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)","i"); 
		var r = _str.match(reg);
		if (r!=null) return (r[2]); return null;
	},
	
	//当前登录用户是否有此权限
	hasAuth:function(authCode){
		var authListStr=CommonUtils.Cookie.get(Constant.PROJECT_NAME+"_user_authList");
		if(!authListStr||authListStr==""){
			return false;
		}
		var authList=authListStr.split("%2C");
		if($.inArray(authCode, authList)<0){
			return false;
		}else{
			return true;
		}
	},
	/**
	 * 日期转换工具
	 */
	Date:{
		DAY:"day",
		MONTH:"month",
		YEAR:"year",
		FORMAT_YYYYMMDDHHMMSS:"yyyy-MM-dd HH:mm:ss",
		
		add:function(date,type,num){
			var _date=date||(new Date());
			
			switch(type){
				case "day":{
					_date.setDate(_date.getDate()+num);
					break;
				}case "month":{
					_date.setMonth(_date.getMonth() + 1 + num);
					break;
				}case "year":{
					_date.setYear(_date.getYear()+num);
					break;
				}
			}
			return _date;
		},
		dateDiff:function(date1,date2){
			if(date1==null||date2==null){
				return 0;
			}
			var iDays = parseInt(Math.abs(date1 - date2) / 1000 / 60 / 60 / 24); //把相差的毫秒数转换为天数
			return iDays;
		},
		date2String:function(date,format){
			var self = this;

			var _year = date.getFullYear();
			var _month = CommonUtils.padleft0((date.getMonth() + 1));
			var _day = CommonUtils.padleft0(date.getDate());
			var _hour = CommonUtils.padleft0(date.getHours());
			var _minute = CommonUtils.padleft0(date.getMinutes());
			var _second = CommonUtils.padleft0(date.getSeconds());

			//if (_month < 10) {
			//    _month = "0" + _month;
			//}
			//if (_day < 10) {
			//    _day = "0" + _day;
			//}
			var ret="";
			var _format=format||self.FORMAT_YYYYMMDDHHMMSS;//默认格式
			switch(format){
				case "yyyy-MM-dd":{
					ret=_year+"-"+_month+"-"+_day;
					break;
				}case "yyyy-MM-dd HH":{
					ret=_year+"-"+_month+"-"+_day+" "+_hour;
					break;
				}case "yyyy-MM-dd HH:mm":{
					ret=_year+"-"+_month+"-"+_day+" "+_hour+":"+_minute;
					break;
				}case "yyyy-MM-dd HH:mm:ss":{
					ret=_year+"-"+_month+"-"+_day+" "+_hour+":"+_minute+":"+_second;
					break;
				}case "MM-dd HH:mm":{
					ret=_month+"-"+_day+" "+_hour+":"+_minute;
					break;
				}case "MM-dd":{
					ret=_month+"-"+_day;
					break;
				}
			}
			return ret;
		},
		string2Date:function(str,format){
			var self=this;
			if(!str||str==""){
				return null;
			}
			format=format||self.FORMAT_YYYYMMDDHHMMSS;
			
			if(self.FORMAT_YYYYMMDDHHMMSS==format){
				return new Date(Date.parse(str.replace(/-/g, "/")));
			}
			return null;
		},
		getTimestamp:function(){
			return (new Date()).getTime();
		},
		/**
		 * 获取本周、本季度、本月、上月的开始日期、结束日期
		 */
		_now:null,
		_nowDayOfWeek:null,
		_nowDay:null,
		_nowMonth:null,
		_nowYear:null,
		_lastMonthDate:null,
		_lastYear:null,
		_lastMonth:null,
		init:function(){
			var self = this;
			self._now = new Date(); //当前日期
			self._nowDayOfWeek = self._now.getDay(); //今天本周的第几天
			self._nowDay = self._now.getDate(); //当前日
			self._nowMonth = self._now.getMonth(); //当前月
			self._nowYear = self._now.getYear(); //当前年
			self._nowYear += (self._nowYear < 2000) ? 1900 : 0; //
			self._lastMonthDate = new Date(); //上月日期
			self._lastMonthDate.setDate(1);
			self._lastMonthDate.setMonth(self._lastMonthDate.getMonth() - 1);
			self._lastYear = self._lastMonthDate.getYear();
			self._lastMonth = self._lastMonthDate.getMonth();
		},
		//格式化日期：yyyy-MM-dd
		formatDate: function(date) {
			var self = this;
		    var myyear = date.getFullYear();
		    var mymonth = date.getMonth() + 1;
		    var myweekday = date.getDate();
		    if (mymonth < 10) {
		        mymonth = "0" + mymonth;
		    }
		    if (myweekday < 10) {
		        myweekday = "0" + myweekday;
		    }
		    return (myyear + "-" + mymonth + "-" + myweekday);
		},
		//获得某月的天数
		getMonthDays : function(myMonth) {
			var self = this;
		    var monthStartDate = new Date(self._nowYear, myMonth, 1);
		    var monthEndDate = new Date(self._nowYear, myMonth + 1, 1);
		    var days = (monthEndDate - monthStartDate) / (1000 * 60 * 60 * 24);
		    return days;
		},
		//获得本季度的开始月份
		getQuarterStartMonth:function() {
			var self = this;
		    var quarterStartMonth = 0;
		    if (self._nowMonth < 3) {
		        quarterStartMonth = 0;
		    }
		    if (2 < self._nowMonth && self._nowMonth < 6) {
		        quarterStartMonth = 3;
		    }
		    if (5 < self._nowMonth && self._nowMonth < 9) {
		        quarterStartMonth = 6;
		    }
		    if (self._nowMonth > 8) {
		        quarterStartMonth = 9;
		    }
		    return quarterStartMonth;
		},
		//获得本周的开始日期
		getWeekStartDate: function() {
			var self = this;
		    var weekStartDate = new Date(self._nowYear, self._nowMonth, self._nowDay - self._nowDayOfWeek + 1);
		    return self.formatDate(weekStartDate) + ' 00:00:00';
		},
		//获得本周的结束日期
		getWeekEndDate : function() {
			var self = this;
		    var weekEndDate = new Date(self._nowYear, self._nowMonth, self._nowDay + (7 - self._nowDayOfWeek));
		    return self.formatDate(weekEndDate) + " 23:59:59";
		},
		//获得上周的开始日期
		getLastWeekStartDate: function() {
			var self = this;
		    var weekStartDate = new Date(self._nowYear, self._nowMonth, self._nowDay - self._nowDayOfWeek - 6);
		    return self.formatDate(weekStartDate) + ' 00:00:00';
		},
		//获得上周的结束日期
		getLastWeekEndDate: function() {
			var self = this;
		    var weekEndDate = new Date(self._nowYear, self._nowMonth, self._nowDay - self._nowDayOfWeek);
		    return self.formatDate(weekEndDate) + " 23:59:59";
		},
		//获得本月的开始日期
		getMonthStartDate: function() {
			var self = this;
		    var monthStartDate = new Date(self._nowYear, self._nowMonth, 1);
		    return self.formatDate(monthStartDate) + ' 00:00:00';
		},
		//获得本月的结束日期
		getMonthEndDate: function() {
			var self = this;
		    var monthEndDate = new Date(self._nowYear, self._nowMonth, self.getMonthDays(self._nowMonth));
		    return self.formatDate(monthEndDate) + " 23:59:59";
		},
		//获得上月开始时间
		getLastMonthStartDate: function() {
			var self = this;
		    var lastMonthStartDate = new Date(self._nowYear, self._lastMonth, 1);
		    return self.formatDate(lastMonthStartDate) + ' 00:00:00';
		},
		//获得上月结束时间
		getLastMonthEndDate: function() {
			var self = this;
		    var lastMonthEndDate = new Date(self._nowYear, self._lastMonth, self.getMonthDays(self._lastMonth));
		    return self.formatDate(lastMonthEndDate) + " 23:59:59";
		},
		//获得本季度的开始日期
		getQuarterStartDate: function() {
			var self = this;
		    var quarterStartDate = new Date(self._nowYear, getQuarterStartMonth(), 1);
		    return self.formatDate(quarterStartDate);
		},
		//或的本季度的结束日期
		getQuarterEndDate: function() {
			var self = this;
		    var quarterEndMonth = getQuarterStartMonth() + 2;
		    var quarterStartDate = new Date(self._nowYear, quarterEndMonth,
		            self.getMonthDays(quarterEndMonth));
		    return self.formatDate(quarterStartDate);
		},
		// 开始时间结束时间获取天数组
		getDaysArr: function(startTime, endTime, format) {
			var self = this;
	        var startTime = new Date(startTime);
	        var endTime = new Date(endTime);
	        var length =(endTime.getDate() - startTime.getDate()) + 1;

	        var days = new Array(length);
	        if(!format){
	        	format = 'yyyy-MM-dd';
	        }
	        	
	        days[0] =  self.date2String(startTime, format);
	        for( var i = 1; i < length; i++ ) {
                startTime.setDate( startTime.getDate() + 1 );
                days[i] = self.date2String(startTime, format);
	        }

	        return days;

	    },
	    // 0本周、1上周、2本月、3上月、4今天、5昨天
	    getTimeObject:function(timeFlag){
	    	var self = this;
			var timeObj = {};
			var startTime = '';
			var endTime = '';
			// 本周
			if(timeFlag == 0){
				startTime = self.getWeekStartDate();
				endTime = self.getWeekEndDate();
			}
			// 上周
			else if(timeFlag == 1){
				startTime = self.getLastWeekStartDate();
				endTime = self.getLastWeekEndDate();
			}
			// 本月
			else if(timeFlag == 2){
				startTime = self.getMonthStartDate();
				endTime = self.getMonthEndDate();
			}
			// 上月
			else if(timeFlag == 3){
				startTime = self.getLastMonthStartDate();
				endTime = self.getLastMonthEndDate();
			}
			// 今天
			else if(timeFlag == 4){
				var d = new Date();
				startTime = self.formatDate(d) + " 00:00:00";
				endTime = self.formatDate(d) + " 23:59:59";
			}
			// 昨天
			else if(timeFlag == 5){
				var d = new Date();
				d.setDate(d.getDate() - 1);
				startTime = self.formatDate(d) + " 00:00:00";
				endTime = self.formatDate(d) + " 23:59:59";
			}
			return {startTime:startTime, endTime:endTime};
		},
	    timestampToTime:function(timestamp){
	        var datetime = new Date();
		    datetime.setTime(timestamp);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
	        year = datetime.getFullYear(),
		    month = (datetime.getMonth() + 1 < 10) ? '0' + (datetime.getMonth() + 1):datetime.getMonth() + 1,
		    day = datetime.getDate() < 10 ? '0' + datetime.getDate() : datetime.getDate(),
		    hour = datetime.getHours() < 10 ? '0' + datetime.getHours() : datetime.getHours(),
		    min = datetime.getMinutes() < 10 ? '0' + datetime.getMinutes() : datetime.getMinutes(),
		    sec = datetime.getSeconds() < 10 ? '0' + datetime.getSeconds() : datetime.getSeconds();
	        return year + '-' + month + '-' + day + ' ' + hour + ':' + min + ':' + sec;
	    },
	    timestampToDate:function(timestamp){
	        var datetime = new Date();
		    datetime.setTime(timestamp);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
	        year = datetime.getFullYear(),
		    month = (datetime.getMonth() + 1 < 10) ? '0' + (datetime.getMonth() + 1):datetime.getMonth() + 1,
		    day = datetime.getDate() < 10 ? '0' + datetime.getDate() : datetime.getDate()
	        return year + '-' + month + '-' + day;
	    }
	},
	padleft0: function (obj) {////补齐两位数
		return obj.toString().replace(/^[0-9]{1}$/, "0" + obj);
	},
	/**
	 * Cookie工具类
	 */
	Cookie:{
		add:function(name, value, expires,path,domain,secure){
			var _name=name;
			var _value=value||"";
			var _expires=expires||"";
			if(_expires!=""){
				_expires=";expires="+expires.toGMTString();
			}
			var _path=";path=/";
			/*if(_path!=""){
				_path=";path=/";
			}*/
			var _domain=domain||"";
			if(_domain!=""){
				_domain=";domain="+_domain;
			}
			var _secure=secure||"";
			if(_secure!=""){
				_secure=";secure="+_secure;
			}
			document.cookie=name+"="+escape(value)
				+_expires
				+_path
				+_domain
				+_secure;
		},
		del:function(name){
			CommonUtils.Cookie.add(name,"");
		},
		get:function(cname){
			var name=cname+"=";
		    var ca=document.cookie.split(';');
		    for(var i=0,len=ca.length;i<len;i++) {
		        var c=ca[i];
		        while (c.charAt(0)==' ') 
		        	c=c.substring(1);
		        if(c.indexOf(name) != -1) 
		        	return c.substring(name.length, c.length);
		    }
		    return "";
		}
	},
	/**
	 * 安全类
	 */
	Random:{
		/**
		 * 指定范围产生随机数
		 */
		getRandomNumber:function(under, over){
			 switch(arguments.length){ 
		     case 1: return parseInt(Math.random()*under+1);
		     case 2: return parseInt(Math.random()*(over-under+1) + under);
		     default: return 0; 
		   }
		},
		/**
		 * 生成数字字母的字符串
		 */
		genKeyOfAlphanumeric:function(len){
			var _len=len||0;
			if(_len<1){
				return "";
			}
			var ret="";
			var arr=$.merge(Constant.NUMBER,Constant.WORDS);
			for(var i=0;i<len;i++){
				ret+=arr[CommonUtils.Security.getRandomNumber(36)];
			}
			return ret;
		}
	},
	/**
	 * 验证类
	 */
	Validate:{
		/**
		 * 数字验证
		 */
		number:function(str){
			var reg=/^[0-9]+$/; 
			var f=reg.test(str);
			return f;
		},
		/**
		 * 金额校验
		 */
		amount:function(str){
			var reg=/^\d+(\.\d+)?$/;
			var f=reg.test(str);
			return f;
		},
		/**
		 * 小数精度限制
		 */
		decimalsLimit:function(str,n){
			var regStr="/^\\d+(\\.\\d{1,"+n+"})?$/";
			eval("var reg="+regStr);
			var f=reg.test(str);
			return f;
		},
		/**
		 * 字母验证
		 */
		monogram:function(str){
			var reg=/^[a-zA-Z]+$/;
			var f=reg.test(str);
			return f;
		},
		/**
		 * 数字或字母验证
		 */
		alphanumeric:function(str){
			var reg=/^[A-Za-z0-9]+$/;
			var f=reg.test(str);
			return f;
		},
		/**
		 * 数字和字母组合
		 */
		numberAndMonogram:function(str){
			var reg=/^(?=.*\d.*)(?=.*[a-zA-Z].*).{8,10}$/;
			var f=reg.test(str);
			return f;
		},
		/**
		 * 最大值限制
		 */
		maxValLmit:function(num,maxVal){
			if(num>maxVal){
				return false;
			}
			return true;
		},
		/**
		 * 最小值限制
		 */
		minValLimit:function(num,minVal){
			if(num<minVal){
				return false;
			}
			return true;
		},
		/**
		 * 数字格式限制
		 * num:要校验的数字
		 * maxLen:数字最大长度
		 * n:保留多少位小数
		 */
		decimals:function(num,maxVal,n){
			//先校验是否是数字
			if(!CommonUtils.Validate.amount(num)){
				return false;
			}
			//再校验最大值
			if(!CommonUtils.Validate.maxValLmit(num,maxVal)){
				return false;
			}
			//再校验小数精度
			if(!CommonUtils.Validate.decimalsLimit(num,n)){
				return false;
			}
			
			return true;
		},
		phone:function(str){
			var reg=/^([0-9]{5,20})$/;
			var f=reg.test(str);
			return f;
		},
		email:function(str){
			var reg=/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
			var f=reg.test(str);
			return f;
		},
		/**
		 * yyyy-MM-dd 日期格式校验
		 */
		dateValidate:function(str){
			var reg = /^[1-9]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$/;
			var f=reg.test(str);
			return f;
		},
		/**
		 * yyyy-MM-dd hh:mm:ss 日期格式校验
		 */
		date2Validate:function(str){
			var reg = /^[1-9]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\s+(20|21|22|23|[0-1]\d):[0-5]\d:[0-5]\d$/;
			var f=reg.test(str);
			return f;
		},
		/**
		 * hh:mm:ss 时间格式校验
		 */
		timeValidate:function(str){
			var reg = /^(20|21|22|23|[0-1]\d):[0-5]\d:[0-5]\d$/;
			var f=reg.test(str);
			return f;
		},
		/**
		 * 长/宽/高  货箱内部尺寸格式校验
		 */
		size:function(str){
			var reg = /^\d+\/\d+\/\d+$/;
			var f=reg.test(str);
			return f;
		},
		/**
		 * 自定义正则，校验
		 */
		customization:function(regStr,str){
			eval("var reg="+regStr);
			var f=reg.test(str);
			return f;
		}
		
	},
	/**
	 * 浏览器工具
	 */
	Browser:{
		getType:function(){
			var explorer =navigator.userAgent;
			//ie 
			if (explorer.indexOf("MSIE") >= 0) {
				return "ie";
			}
			//firefox 
			else if (explorer.indexOf("Firefox") >= 0) {
				return "Firefox";
			}
			//Chrome
			else if(explorer.indexOf("Chrome") >= 0){
				return "Chrome";
			}
			//Opera
			else if(explorer.indexOf("Opera") >= 0){
				return "Opera";
			}
			//Safari
			else if(explorer.indexOf("Safari") >= 0){
				return "Safari";
			} 
			//Netscape
			else if(explorer.indexOf("Netscape")>= 0) { 
				return 'Netscape'; 
			} 
		}
	}
}

var MessageUtil={
	_defaultDelay:3,//单位秒
	
	info:function(msg,callback,delay){
		var self=this;
		var uuid=(new Date()).getTime();
		var r=(Math.random()+"").replace(".","");
		uuid+=r;
		var id="msg"+uuid;

		var _html='<div id="'+id+'" class="city-message">'
			+'<span>'
			+'	<div class="city-msg-notice">'
			+'		<div class="city-msg-notice-content success">'
			+'			<div class="city-message-custom-content">'
			+'				<i class="iconfont iconfont-msgNotice"></i>'
			+'				<span id="MessageUtilContent">'+msg+'</span>'
			+'			</div>'
			+'		</div>'
			+'	</div>'
			+'</span>'
			+'</div>';
		
		$("body").append(_html);
		var _d=delay||self._defaultDelay;
		setTimeout(function(){
			$("#"+id).remove();
			if(typeof callback === 'function'){
				callback();
			}
		},_d*1000);
	},
	alert:function(msg,callback,delay){
		var self=this;
		var uuid=(new Date()).getTime();
		var r=(Math.random()+"").replace(".","");
		uuid+=r;
		var id="msg"+uuid;

		var _html='<div id="'+id+'" class="city-message">'
			+'<span>'
			+'	<div class="city-msg-notice">'
			+'		<div class="city-msg-notice-content alarm">'
			+'			<div class="city-message-custom-content">'
			+'				<i class="iconfont iconfont-msgNotice"></i>'
			+'				<span id="MessageUtilContent">'+msg+'</span>'
			+'			</div>'
			+'		</div>'
			+'	</div>'
			+'</span>'
			+'</div>';
		
		$("body").append(_html);
		var _d=delay||self._defaultDelay;
		setTimeout(function(){
			$("#"+id).remove();
			if(typeof callback === 'function'){
				callback();
			}
		},_d*1000);
	},
	confirm:function(msg,yesFn,noFn){
		var self=this;
		var uuid=(new Date()).getTime();
		var r=(Math.random()+"").replace(".","");
		uuid+=r;
		var id="msg"+uuid;

		var _html='<div id="'+id+'" class="city-modal-box">'
			+'<div class="city-modal-mask"></div>'
			+'<div class="city-modal-wrap">'
			+'	<div class="city-modal">'
			+'		<div class="city-modal-content">'
			+'			<div class="city-modal-body">'
			+'				<div class="clearfix">'
			+'					<div class="city-confirm-body alarm">'
			+'						<i class="iconfont iconfont-msgNotice"></i>'
			+'						<span class="city-confirm-title">'+msg+'</span>'
			+'					</div>'
			+'					<div class="city-confirm-btns">'
			+'						<a id="no_'+id+'" href="javascript:void(0);" class="city-btn">取 消</a>'
			+'						<a id="yes_'+id+'" href="javascript:void(0);" class="city-btn active">确 定</a>'
			+'					</div>'
			+'				</div>'
			+'			</div>'
			+'		</div>'
			+'	</div>'
			+'</div>'
			+'</div>';
		
		$("body").append(_html);
		$(document).on("click", "#no_"+id, function(){ 
			//此处的$(this)指$( "#testDiv")，而非$(document) 
			if(typeof noFn==='function'){
				noFn();
			}
			$("#"+id).remove();
		});
		$(document).on("click", "#yes_"+id, function(){ 
			//此处的$(this)指$( "#testDiv")，而非$(document) 
			if(typeof yesFn==='function'){
				yesFn();
			}
			$("#"+id).remove();
		});
	}
	
}

//全屏方法
function launchFullscreen(){
	var element=document.documentElement;
	if(element.requestFullscreen){
		element.requestFullscreen();
	}else if(element.mozRequestFullScreen){
		element.mozRequestFullScreen();
	}else if(element.webkitRequestFullscreen){
		element.webkitRequestFullscreen();
	}else if(element.msRequestFullscreen) {
		element.msRequestFullscreen();
	}
}

CommonUtils.Date.init();
$(function(){
	//查询区域的固定于展示
	$(document).off("click",".select-box  .change-status").on("click",".select-box  .change-status",function(){
		var w = $(".table-area").width();
		$(".select-box").toggleClass("fixed-box");
		if($(".select-box").hasClass("fixed-box")){
			if($(".city-main").length != 0){
				$(".city-main").css("margin-right","270px");
			}
		}else{
			$(".city-main").css("margin-right","0px");
		}
	});
});
