package com.chainway.driverappweb.common;
import java.security.Key;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.apache.poi.util.SystemOutLogger;
//import com.chainway.ivc.util.SecurityUtil;
import org.springframework.beans.BeansException; 
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory; 
import chainway.frame.exception.SecuritysException;
import chainway.frame.security.SecurityUtil;
public class EncodePropertyFile extends PropertyPlaceholderConfigurer{
	/*解密解数据(PASSKEY)库密码的key*/
	public static final String ROOTKEY="chainway@tuanche";
	/*解密数据库密码的key*/
	public static final String PASSKEY="szcwits@chainway";
	/*数据算法*/
	public static final String DATAALGORITHM="AES/CBC/PKCS5Padding";
	@Override 
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) 
                    throws BeansException { 
			/*获得解密应用程序密码的秘钥*/
            String passkey = props.getProperty("keypass"); 
            //Key key;
			try {
				/*passkey本身也是密文,需要根秘钥解密*/
				
			   Key	key = SecurityUtil.getKey(null, DATAALGORITHM,ROOTKEY);
			   /*秘钥明文*/
			   String dePasskey=SecurityUtil.decrypt(key, passkey,DATAALGORITHM);
			   key=SecurityUtil.getKey(null, DATAALGORITHM, dePasskey);
			   /*解密mysql密码*/
			   String mysqlR=props.getProperty("jdbcPasswordR");
			   String strR=SecurityUtil.decrypt(key, mysqlR,DATAALGORITHM);
			   props.setProperty("jdbcPasswordR", SecurityUtil.decrypt(key, mysqlR,DATAALGORITHM));
			   
			   String mysqlRW=props.getProperty("jdbcPasswordRW");
			   String strRW=SecurityUtil.decrypt(key, mysqlRW,DATAALGORITHM);
			   props.setProperty("jdbcPasswordRW", SecurityUtil.decrypt(key, mysqlRW,DATAALGORITHM));
			   /*解密redis密码*/
			   String redisPassword=props.getProperty("redisAuth");
			   props.setProperty("redisAuth", SecurityUtil.decrypt(key, redisPassword,DATAALGORITHM));			   
			} catch (SecuritysException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            super.processProperties(beanFactory, props); 

    } 
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Key key;
		try {			
			key=SecurityUtil.getKey(null, DATAALGORITHM, PASSKEY);
			System.out.println("加密数据库密码root="+SecurityUtil.encryption(key, "root",DATAALGORITHM));
			System.out.println("加密redis密码redis="+SecurityUtil.encryption(key, "pisAdmin",DATAALGORITHM));
						
			System.out.println("解密数据库密码root="+SecurityUtil.decrypt(key, "103931393039313136353732343935383048E5789D01445B8145E907D84EBF26AB", DATAALGORITHM));
			System.out.println("解密redis密码="+SecurityUtil.decrypt(key, "10323733343935383836333138393038370640D738176C05D22E4D5CC2F8EB3024", DATAALGORITHM));			
		} catch (SecuritysException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
