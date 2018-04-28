package com.chainway.carrierservice.common;

import java.security.Key;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;


import chainway.frame.security.SecurityUtil;

public class CommonUtils {

	/*解密解数据(PASSKEY)库密码的key*/
	private static final String ROOTKEY="chainway@tuanche";
	/*解密数据库密码的key*/
	private static final String PASSKEY="szcwits@chainway";
	/*数据算法*/
	private static final String DATAALGORITHM="AES/CBC/PKCS5Padding";
	private static Key key=null;
	
	public static String decodeProperties(String encrypt,String passkey){
		try{
			if(key==null){
				key=SecurityUtil.getKey(null, DATAALGORITHM,ROOTKEY);
				/*秘钥明文*/
				String dePasskey=SecurityUtil.decrypt(key, passkey,DATAALGORITHM);
				key=SecurityUtil.getKey(null, DATAALGORITHM, dePasskey);
			}
			return SecurityUtil.decrypt(key, encrypt,DATAALGORITHM);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private static Integer BASE_DB=null;
	public static Integer getBaseRedisDBNum(){
		if(BASE_DB==null){
			BASE_DB=PropertiesUtil.getInteger("redis.base.db");
			if(BASE_DB==null){
				BASE_DB=0;
			}
		}
		return BASE_DB;
	}
	
	
	public static String date2Str(Date date,String format){
		if(date==null)return null;
	    SimpleDateFormat fmt=new SimpleDateFormat (format);
	    return fmt.format(date);
	}
	
	public static Date str2Date(String str,String format){
		SimpleDateFormat fmt=new SimpleDateFormat (format);
		try {
			return fmt.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
