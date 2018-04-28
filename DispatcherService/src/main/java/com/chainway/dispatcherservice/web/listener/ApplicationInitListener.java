package com.chainway.dispatcherservice.web.listener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.chainway.dispatchercore.common.IVCApiUtils;
import com.chainway.dispatchercore.common.PropertiesUtil;


public class ApplicationInitListener implements ApplicationListener<ContextRefreshedEvent>{

	protected final Logger log=Logger.getLogger(this.getClass());
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		log.info("************DispatcherService 初始化完成**************");
		//初始化api工具类
		String host=PropertiesUtil.getString("api.host");
		String merchantId=PropertiesUtil.getString("api.merchant");
		String user=PropertiesUtil.getString("api.user");
		String rsa=PropertiesUtil.getString("api.rsa.key");
		IVCApiUtils utils=IVCApiUtils.getInstance();
		utils.init(host, merchantId, user, rsa);
		
		
		
	}

}
