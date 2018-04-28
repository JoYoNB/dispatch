package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;

/**
 * 运输数据
 * @author chainwayits
 * @date 2018年3月21日
 */
public interface TransportDataDao {
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
