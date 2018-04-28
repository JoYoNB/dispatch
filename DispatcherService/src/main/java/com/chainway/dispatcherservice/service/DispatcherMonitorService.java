package com.chainway.dispatcherservice.service;

import java.util.List;
import java.util.Map;

import com.chainway.dispatchercore.dto.Vehicle;


/**
 * @Author: chainway
 *
 * @Date: 2018年3月23日
 * @Description:承运商配送监控
 */
public interface DispatcherMonitorService {
	/**
	 * 根据车辆查询车辆实时位置
	 * @param vehicles
	 * @return
	 */
	public List<Map<String, Object>> getVehiclesLocation(List<Vehicle> vehicles);
	
	/**
	 * 查询订单配送起点的位置信息
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> getOrderStartSiteLocation(Map<String, Object>param);
}
