package com.chainway.dispatcherservice.service.carrier;

import java.util.List;
import java.util.Map;

public interface CarrierStatsService {
	/**
	 * 累计完成订单统计接口
	 * @param map
	 * @return
	 */
	int getTotalFinishedOrderNum(Map<String, Object> map);
	
	/**  
	 * 客户订单排行接口
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> listConsignorOrderRanking(Map<String, Object> map);
	
	/**
	 * 新增订单分布统计接口
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> listFinishedOrderDist(Map<String, Object> map);
	
	/**
	 * 部门订单分布接口
	 * @param map
	 * @return
	 */
	Map<String, Object> listDeptOrderDist(Map<String, Object> map);
	
	/**
	 * 结算统计接口
	 * @param paramMap
	 * @return
	 */
	List<Map<String, Object>> settleStats(Map<String, Object> paramMap);
	
	/**
	 * 部门天订单分布接口
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> listDeptDayOrderDist(Map<String, Object> paramMap);
	
	/**
	 * 获取子部门分布
	 * @param paramMap
	 * @return
	 */
	List<Map<String, Object>> listSubDepts(Map<String, Object> paramMap);
	
	/**
	 * 导出订单统计
	 * @param paramMap
	 * @return
	 * @throws Exception 
	 */
	Map<String, Object> exportOrderStats(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 构造部门天统计table
	 * @param paramMap
	 * @return
	 */
	List<Map<String, Object>> structureTableDist(Map<String, Object> paramMap);
	
	/**
	 * 获取客户订单排名数量
	 * @param paramMap
	 * @return
	 */
	int getConsignorOrderRankingCount(Map<String, Object> paramMap);

	int getCustomerCount(Map<String, Object> paramMap);

	Map<String, Object> exportCustomerStats(Map<String, Object> paramMap) throws Exception;

	int getSettleStatsCount(Map<String, Object> paramMap);
	
}
