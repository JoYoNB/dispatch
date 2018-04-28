package com.chainway.dispatcherdriverservice.biz.dao;

import java.util.Map;

public interface CommonDao {
	/**
	 * 更新车辆负载状态
	 * @param param
	 */
	void updateVehicleLoadRate(Map<String,Object>param);
}
