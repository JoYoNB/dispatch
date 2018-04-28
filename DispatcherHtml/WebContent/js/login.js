var Login={
	_randomToken:null,
	_useCookiePw:false,
	_pwInCookie:null,
	
	init:function(){
		var self=this;
		//加载验证码
		self._updateValidateCode();
		
		self._bind();
	},
	_bind:function(){
		var self=this;
		
		//点击切换验证码
		$("#validateCodeImg").unbind("click").click(function(){
			self._updateValidateCode();
		});
		
		$("#loginBtn").unbind("click").click(function(){
			self._login();
		});
		
		//自动获取密码
		$("#account:text").bind("input propertychange",function(){
			var _account=$(this).val();
			//console.info(_account);
			var _password=CommonUtils.Cookie.get(Constant.PROJECT_NAME+"_pw_"+_account);
			//console.info(_password);
			self._useCookiePw=false;
			self._pwInCookie=null;
			if(_password&&_password!=""){
				$("#password").val("********");
				$('#rememberMe').attr('checked','checked');
				//$('#rememberMe').prop("checked",true);
				//$("#rememberMe").find("input[type='checkbox']").attr("checked","checked");
				//console.info(11111);
				self._useCookiePw=true;
				self._pwInCookie=_password;
			}else{
				$("#password").val("");
			}
		});
		$("#password:password").bind("input propertychange",function(){
			self._useCookiePw=false;
			self._pwInCookie=null;
		});
		
		//-回车登录事件
		$(document).bind('keypress',function(event){  
            if(event.keyCode=="13"){
                self._login();
            }
        });
	},
	_showMsg:function(msg){
		MessageUtil.alert(msg);
	},
	_login:function(){
		var self=this;
		
		var account=$("#account").val();
		if(!account||account==""){
			self._showMsg("账号不能为空");
			return;
		}
		var password=$("#password").val();
		if(!password||password==""){
			self._showMsg("密码不能为空");
			return;
		}
		var validateCode=$("#validateCode").val();
		if(!validateCode||validateCode==""){
			self._showMsg("验证码不能为空");
			return;
		}
		//如果密码超过了20位，则认为是cookie中的密码，则不用再加密
		if(!self._useCookiePw){
			password=SHA256(password);
		}else{
			password=self._pwInCookie;
			//再验证一次密码
			if(!password||password==""){
				Login._showMsg("密码不能为空");
				return;
			}
		}
		
		var param={
			account:account,
			password:password,
			validateCode:validateCode,
			randomCode:self._randomToken
		};
		
		CommonUtils.async({
			url:"/DispatcherWeb/common/login.json",
			data:param,
			complete:function(){
				//刷新验证码
				self._updateValidateCode();
			},
			success:function(result){
				if(result.code==0){
					//登录成功，保存用户信息进入cookie
					var user=result.data;
					
					var token=user.token;
					var name=user.name;
					var roleCode=user.roleCode;
					var id=user.id;
					var _account=user.account;
					
					var role=user.role||{};
					var authList=role.authList||[];
					
					var _auths="";
					for(var i=0,len=authList.length;i<len;i++){
						var _a=authList[i];
						var _code=_a.code;
						if(i==0){
							_auths+=_code;
						}else{
							_auths+=","+_code;
						}
					}
					
					CommonUtils.Cookie.add(Constant.PROJECT_NAME+"_token",token);
					CommonUtils.Cookie.add(Constant.PROJECT_NAME+"_user_name",name);
					CommonUtils.Cookie.add(Constant.PROJECT_NAME+"_user_id",id);
					CommonUtils.Cookie.add(Constant.PROJECT_NAME+"_user_role",roleCode);
					CommonUtils.Cookie.add(Constant.PROJECT_NAME+"_user_authList",_auths);
					var isRememberMe=$('#rememberMe').attr('checked');
					if(isRememberMe){
						CommonUtils.Cookie.add(Constant.PROJECT_NAME+"_pw_"+_account,password);
					}
					
					//缓存完后，500毫秒后，跳转index页面，防止没缓存后就跳转丢失cookie信息
					setTimeout(function(){
						if(roleCode.indexOf('consignor')>-1){
							//货主
							location.href="/"+Constant.PROJECT_NAME+"/consignor/index.html";
						} else if(roleCode.indexOf('carrier')>-1 ){
							//承运商
							location.href="/"+Constant.PROJECT_NAME+"/carrier/index.html";
						} else if(roleCode=="admin"||roleCode.indexOf("common")>-1){
							//结算后台
							location.href="/"+Constant.PROJECT_NAME+"/settlement/index.html";
						}else if(roleCode.indexOf('dual')>-1){
							//货主+承运商
							location.href="/"+Constant.PROJECT_NAME+"/carrier/index.html";
						}
					},500);
				}else if(result.code==10050){
					//账户被锁住
					self._showMsg("登录用户已经锁住");
				}else if(result.code==10008){
					//验证码不对
					self._showMsg("验证码不对");
				}else if(result.code==1005){
					//用户名密码错误
					self._showMsg("用户名密码错误");
				}else{
					self._showMsg("登录失败");
				}
			},
			error:function(result){
				self._showMsg("登录失败");
			}
		});
	},
	_updateValidateCode:function(){
		var self=this;
		
		var token=(new Date()).getTime();
		var r=Math.random()+"";
		token=token+r.replace(".","");
		self._randomToken=token;
		
		$("#validateCodeImg").attr("src","/DispatcherWeb/common/captcha.json?r="+token);
	}
}
$(function(){
	Login.init();
});