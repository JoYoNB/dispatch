package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;

/**
 * 地区查询
 * @author chainwayits
 * @date 2018年3月21日
 */
public interface AreaDao {
	public List<Map<String, Object>> getAreaByParentId(String parent); 
	public Map<String, Object> getAreaById(String parent); 
}
