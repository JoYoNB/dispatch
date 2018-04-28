package com.chainway.dispatcherweb.security.filter;

import com.chainway.dispatcherweb.security.SecuritySession;

public interface SecurityInterceptor {

	public boolean check(SecuritySession session);
	public String getCode();
}
