package com.chainway.driverappweb.web.listener;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.chainway.driverappweb.security.AuthManager;
import com.chainway.driverappweb.security.SecurityManager;

public class ApplicationInitListener implements ApplicationListener<ContextRefreshedEvent>{

	protected final Logger log=Logger.getLogger(this.getClass());
	
	@Autowired
	private AuthManager authManager;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		log.info("************系统初始化完成**************");
		String urls=authManager.getUrlPatterns();
		log.info("系统权限清单");
		log.info(urls);
		SecurityManager securityManager=SecurityManager.getInstance();
		securityManager.setAuthManager(authManager);
	}

}
