package com.chainway.dispatcherappweb.security.filter.impl;

import org.apache.log4j.Logger;

import com.chainway.dispatcherappweb.security.SecuritySession;
import com.chainway.dispatcherappweb.security.filter.SecurityInterceptor;

public class AnonInterceptor implements SecurityInterceptor {

	protected final Logger log=Logger.getLogger(this.getClass());
	
	private String code="anon";
	
	@Override
	public boolean check(SecuritySession session) {
		log.info("anon不需要拦截");
		return true;
	}

	@Override
	public String getCode() {
		return code;
	}

}
