package com.chainway.dispatcherservice.biz.service;

import java.util.Map;

public interface DemoService {

	public Map<String,Object>test(Map<String,Object>param);
	
	public void testTransactional();
	
	public String printAuthSql(Integer roleId,Integer authId);
}
