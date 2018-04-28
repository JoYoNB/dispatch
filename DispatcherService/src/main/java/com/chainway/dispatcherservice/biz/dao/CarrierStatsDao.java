package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;

public interface CarrierStatsDao { 
	/**
	 * 获取部门全部完成订单数量
	 * @param map
	 * @return
	 */
	int getTotalFinishedOrderNum(Map<String, Object> map);
	
	/**
	 * 获取货主订单排行
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> listConsignorOrderRanking(Map<String, Object> map);
	
	/**
	 * 获取完成订单天分布
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> listFinishedOrderDist(Map<String, Object> map);
	
	/**
	 * 获取子部门完成订单分布
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> listDeptOrderDist(Map<String, Object> map);
	
	/**
	 * 结算统计
	 * @param paramMap
	 * @return
	 */
	List<Map<String, Object>> statsSettlement(Map<String, Object> paramMap);
	
	/**
	 * 获取当前部门子部门
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> listSubDepts(Map<String, Object> map);
	
	/**
	 * 获取子部门日完成订单分布
	 * @param paramMap
	 * @return
	 */
	List<Map<String, Object>> listDeptDayOrderDist(Map<String, Object> paramMap);

	int getConsignorOrderRankingCount(Map<String, Object> paramMap);

	int getCustomerCount(Map<String, Object> paramMap);
	
	int getSettleStatsCount(Map<String, Object> paramMap);
}
