package com.chainway.dispatcherdriverservice.service;

import java.util.Map;

public interface MonitorService {
	/**
	 * 参数：orderNo 订单编号
	 *     driverId 司机id
	 * 获取运输线路
	 * @param map
	 * @return
	 */
	Map<String, Object> getOrderTransportRoute(Map<String, Object> map) throws Exception;
	
}
