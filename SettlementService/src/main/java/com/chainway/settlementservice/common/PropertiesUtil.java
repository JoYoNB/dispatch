package com.chainway.settlementservice.common;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

public class PropertiesUtil {
	
	public static String getString(String key) {
		return prop.getProperty(key);
	}
	
	public static Integer getInteger(String key) {
		String intStr=prop.getProperty(key);
		if(StringUtils.isEmpty(intStr)){
			return null;
		}
		return Integer.parseInt(intStr);
	}
	
	private static Properties prop = new Properties();
	
	static {
		try {
			prop.load(new ClassPathResource("configure.properties").getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
