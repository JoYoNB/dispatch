package com.chainway.dispatchercore.common;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class DecodePropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer{

	@Override 
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) throws BeansException {
		super.processProperties(beanFactory, props);
		/*获得解密应用程序密码的秘钥*/
        String passkey=props.getProperty("keypass");
        
        
        
        
	}
	
}
