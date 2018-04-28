package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;

public interface DemoDao {

	public Map<String,Object>test(Map<String,Object>param);
	public List<Map<String,Object>>getAuthList(Integer value);
}
