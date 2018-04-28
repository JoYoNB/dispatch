package com.chainway.dispatchercore.common;

import java.security.Key;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

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
	
	public static int getPageSize(HttpServletRequest request){
		Integer pageSize=20;
		String pageSizeStr=request.getParameter("pageSize");
		if(StringUtils.isNotEmpty(pageSizeStr)){
			try{
				pageSize=Integer.parseInt(pageSizeStr);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if(pageSize.intValue()!=10&&pageSize.intValue()!=20
				&&pageSize.intValue()!=50&&pageSize.intValue()!=100&&pageSize.intValue()!=200){
			pageSize=20;
		}
		return pageSize;
	}
	
	public static int getOffset(HttpServletRequest request){
		int pageSize=getPageSize(request);
		String pageNumStr=request.getParameter("pageNum");
		Integer pageNum=1;
		if(StringUtils.isNotEmpty(pageNumStr)){
			try{
				pageNum=Integer.parseInt(pageNumStr);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if(pageNum<1){
			pageNum=1;
		}
		int offset=(pageNum-1)*pageSize;
		return offset;
	}
	
}
