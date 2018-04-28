package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;


public interface CarrierOrderDao {
	
	int getOrderStatusAndLock(String orderNo);

	int getOrderStatus(String orderNo);
	
	List<Map<String,Object>> getOrderList(Map<String, Object> maps);

	int getOrderListCount(Map<String, Object> param);
	
	/**
	 * 获取订单详情
	 * @param param
	 * @return
	 */
	Map<String, Object> getOrder(Map<String, Object> param);
	
	
	/**
	 * 获取任意订单详情（无部门限制）
	 * @param param
	 * @return
	 */
	Map<String, Object> getAnyOrder(Map<String, Object> param);
	
	
	/**
	 * 获取订单站点信息
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> listOrderSites(Map<String, Object> params);
}
