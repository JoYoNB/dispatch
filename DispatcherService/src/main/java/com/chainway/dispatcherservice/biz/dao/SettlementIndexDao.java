package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;

/**
 * 结算后台首页
 * @author chainwayits
 * @date 2018年3月21日
 */
public interface SettlementIndexDao {
	/**
	 * 统计
	 * @param param
	 * @return
	 */
	public Map<String, Object> orderSum(Map<String, Object> param); 
	/**
	 * 排名
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> orderRank(Map<String, Object> param); 
	/**
	 * 柱状图
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> orderBar(Map<String, Object> param); 
}
