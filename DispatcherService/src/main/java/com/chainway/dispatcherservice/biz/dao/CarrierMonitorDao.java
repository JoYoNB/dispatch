package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;


public interface CarrierMonitorDao { 
	/**
	 * 获取部门所属车辆及车辆运送中订单
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> listVehicles(Map<String, Object> map);
	
	/**
	 * 获取承运商已接订单
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> listOrders(Map<String, Object> map);
	
	/**
	 * 获取查询轨迹所需信息
	 * @param orderNo
	 * @return
	 */
	Map<String, Object> getTraceQueryInfo(String orderNo);

	/**
	 * 获取各负载率车辆数量
	 * @param paramMap
	 * @return
	 */
	int getVehicleCount(Map<String, Object> paramMap);

	/**
	 * 获取离线司机数量
	 * @param paramMap
	 * @return
	 */
	int getOffLineDriverCount(Map<String, Object> paramMap);

	/**
	 * 获取承运商已接订单所在部门信息
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> listSites(Map<String, Object> map);
}
