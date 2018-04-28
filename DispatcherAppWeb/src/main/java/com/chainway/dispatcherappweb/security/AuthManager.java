package com.chainway.dispatcherappweb.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.chainway.dispatcherappweb.security.filter.SecurityInterceptor;
import com.chainway.dispatcherappweb.security.filter.impl.AnonInterceptor;
import com.chainway.dispatcherappweb.security.filter.impl.PermissionInterceptor;
import com.chainway.dispatcherappweb.security.filter.impl.RoleInterceptor;
import com.chainway.dispatcherappweb.security.filter.impl.UserInterceptor;


public class AuthManager {

	protected final Logger log=Logger.getLogger(this.getClass());
	
	private Map<String,List<SecurityInterceptor>>urls=new LinkedHashMap<String,List<SecurityInterceptor>>();
	
	private String urlPatterns;
	
	public String getUrlPatterns() {
		return urlPatterns;
	}
	public void setUrlPatterns(String urlPatterns) {
		this.urlPatterns = urlPatterns;
		//初始化外置的url pattern
		if(this.urlPatterns==null||"".equals(this.urlPatterns)){
			return;
		}
		//\r\n
		/*
		 * 例如:
		 *  /common/demo.json;=role[admin|opt]
		 *  /common/**;=role[acc],permission[add|update]
		 *  /common/login.json;=none
		 * 
		 * */
		String str=this.urlPatterns.replace("\n", "\r");
		String[]urls=str.split("\r");
		if(urls==null||urls.length==0){
			return;
		}
		for(String s:urls){
			if(StringUtils.isEmpty(s)){
				continue;
			}
			//解析出url和角色权限
			String[]items=s.split(";=");
			if(items==null||items.length<2){
				continue;
			}
			String url=items[0];
			String auth=items[1];
			if(StringUtils.isEmpty(auth)){
				continue;
			}
			String[]filters=auth.split(",");
			if(filters==null||filters.length==0){
				continue;
			}
			List<SecurityInterceptor>list=new ArrayList<SecurityInterceptor>();
			for(String f:filters){
				if("anon".equals(f)){
					SecurityInterceptor securityInterceptor=new AnonInterceptor();
					list.add(securityInterceptor);
				}else if("user".equals(f)){
					SecurityInterceptor securityInterceptor=new UserInterceptor();
					list.add(securityInterceptor);
				}else if(f.indexOf("role[")>-1){
					//角色过滤器
					String ss=f.replace("role[", "");
					ss=ss.replace("]", "");
					if(StringUtils.isEmpty(ss)){
						continue;
					}
					String[]sitems=ss.split("\\|");
					if(sitems==null||sitems.length==0){
						continue;
					}
					List<String>l=Arrays.asList(sitems);
					SecurityInterceptor securityInterceptor=new RoleInterceptor(l);
					list.add(securityInterceptor);
				}else if(f.indexOf("permission[")>-1){
					//角色过滤器
					String ss=f.replace("permission[", "");
					ss=ss.replace("]", "");
					if(StringUtils.isEmpty(ss)){
						continue;
					}
					String[]sitems=ss.split("\\|");
					if(sitems==null||sitems.length==0){
						continue;
					}
					List<String>l=Arrays.asList(sitems);
					SecurityInterceptor securityInterceptor=new PermissionInterceptor(l);
					list.add(securityInterceptor);
				}
			}
			
			if(!list.isEmpty()){
				this.urls.put(url, list);
			}
		}
	}
	
	public Map<String,List<SecurityInterceptor>> getUrls(){
		return this.urls;
	}
	
	public static void main(String[]args){
		AuthManager t=new AuthManager();
		
		String s="/common/test1.json;=anon"+"\r"
				+"/common/test2.json;=user"+"\r"
				+"/common/test3.json;=role[admin|opt]"+"\r"
				+"/common/test4.json;=permission[add|update|delete]"+"\r"
				+"/common/test5.json;=role[admin|opt],permission[add|update]";
		
		t.setUrlPatterns(s);
		System.out.println(t.urls);
		Map map=t.urls;
		System.out.println("111");
	}
}
