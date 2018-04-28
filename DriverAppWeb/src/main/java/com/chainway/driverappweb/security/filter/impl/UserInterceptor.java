package com.chainway.driverappweb.security.filter.impl;

import java.util.Map;

import org.apache.log4j.Logger;

import com.chainway.driverappweb.security.SecuritySession;
import com.chainway.driverappweb.security.filter.SecurityInterceptor;

public class UserInterceptor implements SecurityInterceptor {

	protected final Logger log=Logger.getLogger(this.getClass());
	
	private String code="user";
	
	@Override
	public boolean check(SecuritySession session) {
		Map<String,Object>user=session.getLoginUser();
		if(user==null||user.isEmpty()){
			log.info("用户未登录");
			return false;
		}
		return true;
	}

	@Override
	public String getCode() {
		return code;
	}

}
