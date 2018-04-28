package com.chainway.dispatcherweb.common;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.chainway.cacheService.biz.service.CacheService;
import com.chainway.dispatchercore.dto.User;

import chainway.frame.exception.SecuritysException;
import chainway.frame.security.SecurityUtil;
import net.sf.cglib.beans.BeanMap;

@Component 
public class CommonUtils {
	
	protected static final Logger log=Logger.getLogger(CommonUtils.class);
	
	public static String getLangueInCookie(HttpServletRequest request){
		
		String lang="zh";//默认是英语
		String _i18n_lang="_iov_langue_";
		//获取cookie
		Cookie[] cookies=request.getCookies();
		if(cookies!=null&&cookies.length>0){
			boolean got=false;
			for(Cookie c:cookies){
				if(_i18n_lang.equals(c.getName())){
					lang=c.getValue();
					got=true;
					break;
				}
			}
			if(!got){
				//从cookie拿不到，则从request中拿
				String l=request.getParameter(_i18n_lang);
				if(StringUtils.isNotEmpty(l)){
					lang=l;
				}
			}
		}else{
			//从cookie拿不到，则从request中拿
			String l=request.getParameter(_i18n_lang);
			if(StringUtils.isNotEmpty(l)){
				lang=l;
			}
		}
		
		return lang;
	}
	
	public static String getCookie(String cookieName,HttpServletRequest request){
		String cookieValue="";
		if(StringUtils.isEmpty(cookieName)){
			return cookieValue;
		}
		//获取cookie
		Cookie[] cookies=request.getCookies();
		if(cookies!=null&&cookies.length>0){
			for(Cookie c:cookies){
				if(cookieName.equals(c.getName())){
					cookieValue=c.getValue();
					break;
				}
			}
		}
				
		return cookieValue;
	}
	
	public static void setCookie(String cookieName,String cookieValue,HttpServletResponse response){
		Cookie cookie=new Cookie(cookieName, cookieValue);//(key,value)
	    cookie.setPath("/iov/");// 这个要设置
	    //cookie.setDomain(".aotori.com");//这样设置，能实现两个网站共用
	    //cookie.setMaxAge(365 * 24 * 60 * 60);// 不设置的话，则cookies不写入硬盘,而是写在内存,只在当前页面有用,以秒为单位
	    response.addCookie(cookie);
	}
	
	public static String getRequestIp(HttpServletRequest request){
		String ip=request.getHeader("x-forwarded-for");
		
		if(ip==null||ip.length()==0||"unknown".equalsIgnoreCase(ip)){
		    ip=request.getHeader("Proxy-Client-IP");
		}
		if(ip==null||ip.length()==0||"unknown".equalsIgnoreCase(ip)){
			ip=request.getHeader("HTTP_CLIENT_IP");
		}
		if(ip==null||ip.length()==0||"unknown".equalsIgnoreCase(ip)){
			ip=request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if(ip==null||ip.length()==0||"unknown".equalsIgnoreCase(ip)){
			ip=request.getRemoteAddr();
		}
		return ip;
	}
	
	
	public static String genUserPassword(String userAccount,String orgiPassword){
		//目前先用md5加密
		//String pwd = DigestUtils.md5Hex(orgiPassword);
		String pwd=genUserPasswordSHA2_256(userAccount, orgiPassword);
		return pwd;
	}
	
	public static String genUserPasswordSHA2_256(String userAccount,String orgiPassword){
		String encryPassword=null;
		try {
			encryPassword=SecurityUtil.encryptionSHA2_256(orgiPassword);
			encryPassword=encryPassword.toLowerCase();
		} catch (SecuritysException e) {
			e.printStackTrace();
		}
		return encryPassword;
	}
	
	public static String genPlatformSecretkey(String id,String password){
		String PASSKEY="Apay+123-789=045";
		String DATAALGORITHM="AES";
		Key key;
		try {
			key=SecurityUtil.getKey(null, DATAALGORITHM, PASSKEY);
			String secretkey=SecurityUtil.encryption(key, id+"#"+password,DATAALGORITHM);
			//String decode=SecurityUtil.decrypt(key, secretkey, DATAALGORITHM);
			//System.out.println(decode);
			return secretkey;
		} catch (SecuritysException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	public static String getPlatIdFromSecretkey(String secretkey){
		if(secretkey==null)return null;
		String platId=null;
		String PASSKEY="Apay+123-789=045";
		String DATAALGORITHM="AES";
		Key key;
		try {
			key=SecurityUtil.getKey(null, DATAALGORITHM, PASSKEY);
			String str=SecurityUtil.decrypt(key, secretkey,DATAALGORITHM);
			if(str!=null){
				String[] strings=str.split("#");
				if(strings.length==2){
					platId=strings[0]; 
				}
			}
		} catch (SecuritysException e) {
			e.printStackTrace();
		}
		return platId;
	}
	
	//sql注入拦截，方便其他地方用，因为导入，上传部分，不能在框架层拦截
	private static StringBuffer KEY_WORD_STR=new StringBuffer("select,")
			.append("and,")
			.append("exec,")
			.append("execute,")
			.append("insert,")
			.append("delete,")
			.append("update,")
			.append("drop,")
			.append("master,")
			.append("truncate,")
			.append("declare,")
			.append("net user,")
			.append("xp_cmdshell,")
			.append("or,")
			.append("create,")
			.append("table,")
			.append("grant,")
			.append("use db,")
			.append("group_concat,")
			.append("column_name,")
			.append("information_schema.columns,")
			.append("table_schema,")
			.append("union,")
			.append("like");
	private static String[]KEY_WORDS=KEY_WORD_STR.toString().split(",");
	public static boolean checkSqlInject(String uri,String name,String str){
		for(String v:KEY_WORDS){
			//System.out.println("校验关键字:"+v);
			int _si=str.indexOf(v);
			if(_si<0){
				//System.out.println("不存在:"+v);
				continue;
			}
			//System.out.println(_si);
			int _ei=_si+v.length();
			
			//判断靠近关键字的前后一个单词是否是空格和加号，如果是，则认为是关键字，如果不是则可以通过
			String _ps="";//前一个字符
			if(_si>0){
				_ps=str.substring(_si-1, _si);
			}
			//System.out.println("前一个字符:"+_ps);
			
			String _ns="";//后一个字符
			if(_ei<(str.length())){
				_ns=str.substring(_ei, _ei+1);
			}
			//System.out.println("后一个字符:"+_ns);
			//System.out.println((_ei)+"  "+str.length());
			
			//先判断前一个字符
			if(!" ".equals(_ps)&&!"+".equals(_ps)&&_si!=0){//还要注意是否是第一个字母
				continue;
			}
			//再判断后一个字符
			if(!" ".equals(_ns)&&!"+".equals(_ns)&&_ei!=str.length()){
				continue;
			}
			log.error("sql注入已拦截:url:"+uri+" -> param:"+name+" -> value:"+v);
			return false;
			//throw new IovException(ReturnCodeConstant.ERROR_SQL_INJECT,"sql inject");
		}
		return true;
	}
	/**
	 * 字符串格式验证
	 * @param filed
	 * @param isNull
	 * @param minLen
	 * @param maxLen
	 * @param reg
	 * @return
	 */
	public static String checkField(String filed,boolean isNull,Integer minLen,Integer maxLen,String reg){
		if(filed!=null)filed=filed.trim();
		if(isNull){
			if(filed==null||"".equals(filed)){
				return "required";
			}
		}
		if(filed!=null&&!"".equals(filed)){
			if(minLen!=null){
				if(filed.length()<minLen){
					return "maxLen";
				}
			}
			if(maxLen!=null){
				if(filed.length()>maxLen){
					return "maxLen";
				}
			}
			if(reg!=null&&!"".equals(reg)){
				Pattern p = Pattern.compile(reg); 
	    		Matcher m = p.matcher(filed); 
	    		if(!m.matches()){
	    			return "reg";
	    		}
			}
		}
		return null;
	}
	
	public static String cleanXSS(String value){
		//value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		//value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
		//value = value.replaceAll("'", "&#39;");
		value = value.replaceAll("eval\\((.*)\\)", "");
		value = value.replaceAll("EVAL\\((.*)\\)", "");
		value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
		value = value.replaceAll("[\\\"\\\'][\\s]*JAVASCRIPT:(.*)[\\\"\\\']", "\"\"");
		value = value.replaceAll("[\\\"\\\'][\\s]*script:(.*)[\\\"\\\']", "\"\"");
		value = value.replaceAll("[\\\"\\\'][\\s]*SCRIPT:(.*)[\\\"\\\']", "\"\"");
		return value;
	}
	
	/** 
	 * 将对象装换为map 
	 * @param bean 
	 * @return 
	 */  
	public static <T> Map<String, Object> beanToMap(T bean) {
	    Map<String, Object>map=new HashMap<String,Object>();
	    if(bean!=null){
	        BeanMap beanMap=BeanMap.create(bean);
	        for(Object key:beanMap.keySet()){
	            map.put(key+"", beanMap.get(key));
	        }
	    }
	    return map;
	}  
	  
	/** 
	 * 将map装换为javabean对象 
	 * @param map 
	 * @param bean 
	 * @return 
	 */  
	public static <T> T mapToBean(Map<String, Object> map,T bean) {
	    BeanMap beanMap=BeanMap.create(bean);
	    beanMap.putAll(map);
	    return bean;
	}
	
	public static void main(String[]args){
		//System.out.println(genUserPassword("", "12345"));
		//System.out.println(genPlatformSecretkey("1234567900", "4568"));
		
		System.out.println(genUserPasswordSHA2_256("", "s1234567"));
	}
}
