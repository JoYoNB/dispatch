package com.chainway.driverappweb.security.filter.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.chainway.driverappweb.security.SecuritySession;
import com.chainway.driverappweb.security.filter.SecurityInterceptor;

public class RoleInterceptor implements SecurityInterceptor {

	protected final Logger log=Logger.getLogger(this.getClass());
	
	private String code="role";
	private List<String> roleCode;
	
	public RoleInterceptor(List<String> roleCode){
		this.roleCode=roleCode;
	}
	
	@Override
	public boolean check(SecuritySession session) {
		UserInterceptor userInterceptor=new UserInterceptor();
		boolean isLogin=userInterceptor.check(session);
		if(!isLogin){
			return false;
		}
		Map<String,Object>loginUser=session.getLoginUser();
		String roleCode=(String) loginUser.get("roleCode");
		
		for(String r:this.roleCode){
			//符合任意一个都通过
			if(r.equals(roleCode)){
				return true;
			}
		}
		log.info("userRole="+roleCode+",urlRole="+this.roleCode);
		return false;
	}

	@Override
	public String getCode() {
		return code;
	}

}
