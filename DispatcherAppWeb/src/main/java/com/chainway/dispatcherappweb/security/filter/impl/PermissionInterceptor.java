package com.chainway.dispatcherappweb.security.filter.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.chainway.dispatcherappweb.security.SecuritySession;
import com.chainway.dispatcherappweb.security.filter.SecurityInterceptor;

public class PermissionInterceptor implements SecurityInterceptor {

	protected final Logger log=Logger.getLogger(this.getClass());
	
	private String code="permission";
	private List<String> permissionCode;
	
	public PermissionInterceptor(List<String> permissionCode){
		this.permissionCode=permissionCode;
	}
	
	@Override
	public boolean check(SecuritySession session) {
		UserInterceptor userInterceptor=new UserInterceptor();
		boolean isLogin=userInterceptor.check(session);
		if(!isLogin){
			return false;
		}
		Map<String,Object>loginUser=session.getLoginUser();
		List<String> permissionList=(List<String>) loginUser.get("permissions");
		if(permissionList==null||permissionList.isEmpty()){
			log.info("user pemissionList=[],url permissionCode="+this.getCode());
			return false;
		}
		for(String p:this.permissionCode){
			//符合任意一个都通过
			if(permissionList.contains(p)){
				return true;
			}
		}
		log.info("user pemissionList="+permissionList+",url permissionCode="+this.permissionCode);
		return false;
	}

	@Override
	public String getCode() {
		return code;
	}

}
