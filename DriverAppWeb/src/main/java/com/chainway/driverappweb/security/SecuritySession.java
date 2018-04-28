/*
 * 维护登录用户的session信息
 * */
package com.chainway.driverappweb.security;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.chainway.dispatchercore.dto.Driver;
import com.chainway.driverappweb.biz.service.LocalService;
import com.chainway.driverappweb.security.filter.SecurityInterceptor;

public class SecuritySession {

	private String token;
	private LocalService localService;
	
	private Map<String,Object>loginUser;
	
	public SecuritySession(String token,LocalService localService){
		this.token=token;
		this.localService=localService;
	}
	
	public String getToken(){
		return this.token;
	}
	
	public Map<String,Object>getLoginUser(){
		if(StringUtils.isEmpty(token)){
			return null;
		}
		Driver user=localService.getUserInSession(token);
		if(user==null){
			return null;
		}
		
		loginUser=new HashMap<String,Object>();
		loginUser.put("driverName", user.getDriverName());
		loginUser.put("phoneNo", user.getPhoneNo());
		
		return loginUser;
	}
	
	public boolean accept(SecurityInterceptor securityInterceptor){
		return securityInterceptor.check(this);
	}
}
