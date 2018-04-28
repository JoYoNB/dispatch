package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;

public interface AssignmentDao {
	/**
	 * 获取已发布订单
	 * @return
	 */
	List<Map<String, Object>> listPublishedOrders();
	
	/**
	 * 记录接单承运商
	 * @param map
	 */
	void recordOrderCarrierRel(Map<String, Object> map);
	
	/**
	 * 记录接单车辆
	 * @param map
	 */
	void recordOrderVehicleRel(Map<String, Object> map);
	
	/**
	 * 获取承运商接单最大最小里程
	 * @param carrierDept
	 * @return
	 */
	Map<String, Object> getCarrierMileages(int carrierDept);
	
	/**
	 * 获取承运商接单区域
	 * @param carrierDept
	 * @return
	 */
	List<Map<String, Object>> listCarrierAreas(int carrierDept);
	
	/**
	 * 获取承运商的货物类型
	 * @param carrierDept
	 * @return
	 */
	List<Map<String, Object>> listCarrierGoodsType(int carrierDept);
	
	/**
	 * 获取可派单车辆
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> listReadyVehicles(Map<String, Object> map);
	
	/**
	 * 获取可派单车辆总数
	 * @param map
	 * @return
	 */
	int listReadyVehiclesCount(Map<String, Object> map);
}
