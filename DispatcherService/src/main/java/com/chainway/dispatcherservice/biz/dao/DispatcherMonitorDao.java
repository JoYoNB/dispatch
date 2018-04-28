package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;

/**
 * @Author: chainway
 *
 * @Date: 2018年3月23日
 * @Description:承运商-配送监控
 */
public interface DispatcherMonitorDao {
	/**
	 * 查询订单配送起点的位置信息
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> getOrderStartSiteLocation(Map<String, Object>param);
}
