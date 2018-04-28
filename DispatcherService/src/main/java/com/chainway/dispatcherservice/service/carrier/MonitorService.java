package com.chainway.dispatcherservice.service.carrier;

import java.util.List;
import java.util.Map;

public interface MonitorService {
	/**
	 * 获取车辆位置
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> listVehiclePositions(Map<String, Object> map);
	
	/**  
	 * 获取订单位置
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> listOrderPositions(Map<String, Object> map);
	
	/**
	 * 获取运输线路
	 * @param map
	 * @return
	 */
	Map<String, Object> getOrderTransportRoute(Map<String, Object> map);
	
	/**
	 * 获取车辆数量
	 * @param paramMap
	 * @return
	 */
	Map<String, Object> getVehicleCount(Map<String, Object> paramMap);
	
	/**
	 * 获取已接订单站点位置
	 * @param paramMap
	 * @return
	 */
	List<Map<String, Object>> listSitePositions(Map<String, Object> paramMap);
	
}
