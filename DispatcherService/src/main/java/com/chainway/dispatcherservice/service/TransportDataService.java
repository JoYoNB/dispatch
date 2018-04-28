package com.chainway.dispatcherservice.service;

import java.util.List;
import java.util.Map;

/**
 * 运输数据
 * @author chainwayits
 * @date 2018年3月20日
 */
public interface TransportDataService {
	/**
	 * 查询列表
	 * @param chargeRule
	 * @return
	 */
	public List<Map<String, Object>> getList(Map<String, Object> param); 
	/**
	 * 查询数目
	 * @param param
	 * @return
	 */
	public int getListCount(Map<String,Object>param);
}
