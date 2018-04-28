package com.chainway.dispatcherappweb.security;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.chainway.dispatcherappweb.biz.service.LocalService;
import com.chainway.dispatcherappweb.security.filter.SecurityInterceptor;

public class SecurityManager {

	private static SecurityManager securityManager;
	
	private AuthManager authManager;
	
	private SecurityManager(){
		
	}
	
	public static SecurityManager getInstance(){
		if(securityManager==null){
			securityManager=new SecurityManager();
		}
		return securityManager;
	}
	
	public SecuritySession getSession(HttpServletRequest request,LocalService localService){
		String token=request.getParameter("token");
		return new SecuritySession(token,localService);
	}
	
	public void setAuthManager(AuthManager authManager){
		this.authManager=authManager;
	}
	public AuthManager getAuthManager(){
		return this.authManager;
	}
	
	public List<SecurityInterceptor>getInterceptor(String uri){
		//根据url正则匹配出拦截器
		Map<String,List<SecurityInterceptor>>urls=this.authManager.getUrls();
		for(Map.Entry<String, List<SecurityInterceptor>>entry:urls.entrySet()){
			String regEx=entry.getKey().trim();
			
			//转义一些正则特殊符号,TODO 还有很多比如\w,\d
			regEx=regEx.replace("?", "\\?");
			
			regEx=regEx.replace("**", "[\\w\\.\\?=&-/#%]*");
			//regEx=regEx.replace("**", "[\\w\\W\\s\\S\\D\\d]");
		    // 编译正则表达式
		    Pattern pattern=Pattern.compile(regEx);
		    // 忽略大小写的写法
		    // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
		    Matcher matcher=pattern.matcher(uri);
		    // 字符串是否与正则表达式相匹配
		    boolean f=matcher.matches();
			if(f){
				return entry.getValue();
			}
		}
		
		return null;
	}
}
