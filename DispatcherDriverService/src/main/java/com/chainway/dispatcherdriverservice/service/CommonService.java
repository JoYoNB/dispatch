package com.chainway.dispatcherdriverservice.service;

import java.util.Map;

public interface CommonService {

	public Map<String,Object>test(Map<String,Object>param);
	/**
	 * 更新车辆负载状态
	 * @param param
	 */
	void updateVehicleLoadRate(Map<String,Object>param);
}
