package com.chainway.dispatcherappweb.security.filter;

import com.chainway.dispatcherappweb.security.SecuritySession;

public interface SecurityInterceptor {

	public boolean check(SecuritySession session);
	public String getCode();
}
