package com.chainway.driverappweb.security.filter;

import com.chainway.driverappweb.security.SecuritySession;

public interface SecurityInterceptor {

	public boolean check(SecuritySession session);
	public String getCode();
}
