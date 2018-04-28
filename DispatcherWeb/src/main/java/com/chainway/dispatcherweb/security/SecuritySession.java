/*
 * 维护登录用户的session信息
 * */
package com.chainway.dispatcherweb.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.chainway.dispatchercore.dto.Auth;
import com.chainway.dispatchercore.dto.Role;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatcherweb.biz.service.LocalService;
import com.chainway.dispatcherweb.security.filter.SecurityInterceptor;

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
		User user=localService.getUserInSession(token);
		if(user==null){
			return null;
		}
		
		loginUser=new HashMap<String,Object>();
		loginUser.put("name", user.getName());
		loginUser.put("account", user.getAccount());
		loginUser.put("roleCode", user.getRoleCode());
		
		Role role=user.getRole();
		if(role!=null){
			List<Auth>authList=role.getAuthList();
			if(authList!=null&&!authList.isEmpty()){
				List<String>permissions=new ArrayList<String>();
				for(Auth a:authList){
					permissions.add(a.getCode());
				}
				loginUser.put("permissions", permissions);
			}
		}
		
		return loginUser;
	}
	
	public boolean accept(SecurityInterceptor securityInterceptor){
		return securityInterceptor.check(this);
	}
}
