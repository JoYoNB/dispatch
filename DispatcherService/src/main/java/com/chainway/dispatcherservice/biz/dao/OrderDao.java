package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;

import com.chainway.dispatcherservice.dto.OrderParam;
import com.chainway.dispatcherservice.dto.SiteParam;

public interface OrderDao {
	
	/**
	 * 订单统计
	 * @param param
	 * @return
	 */
	List<Map<String,Object>> orderStatistics(Map<String, Object> param);
	
	/**
	 * 货物统计
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> cargoStatistics(Map<String, Object> param);
	
	/**
	 * 订单排行
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> orderRank(Map<String, Object> param);
	
	/**
	 * 送达货物排行
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> deliveryCargoRank(Map<String, Object> param);
	
	/**
	 * 累计数据统计
	 * @param param
	 * @return
	 */
	Map<String, Object> totalStatistics(Map<String, Object> param);
	
	/**
	 * 地图订单列表查询
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> mapOrderList(Map<String, Object> param);
	
	/**
	 * 订单列表查询
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> orderList(Map<String, Object> param);
	
	/**
	 * 获取订单详情
	 * @param param
	 * @return
	 */
	Map<String, Object> getOrderDetails(Map<String, Object> param);
	
	/**
	 * 获取订单站点详情
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> getOrderSiteDetails(Map<String, Object> param);
	
	/**
	 * 创建订单
	 * @param order
	 */
	void createOrder(OrderParam order);
	
	/**
	 * 更新订单
	 * @param order
	 */
	void modifyOrder(OrderParam order);
	
	/**
	 * 添加订单站点关联表信息
	 * @param site
	 */
	void createOrderSites(List<SiteParam> sites);
	
	/**
	 * 根据订单号删除订单站点关联关系
	 * @param orderNo
	 */
	void deleteOrderSites(String orderNo);
	
	/**
	 * 通过名称获取省市区的字典id
	 * @param param
	 * @return
	 */
	Map<String, String> getPCDIdByName(Map<String, Object> param);
	
	/**
	 * 根据订单号查询订单状态、支付状态
	 * @param orderNo
	 * @return
	 */
	Map<String, Integer> getOrderStatus(String orderNo);


	/**
	 * 订单列表按条件查询总数
	 * @param param
	 * @return
	 */
	Integer totalOrder(Map<String, Object> param);
	
	
	/**
	 * 添加订单日志
	 * @param orderLog
	 * @return
	 */
	public int createOrderLog(Map<String,Object>orderLog);
	
	/**
	 * 删除订单与承运商关系
	 * @param param
	 * @return
	 */
	public int deleteOrderCarrierRel(Map<String,Object>param);
	
	/**
	 * 删除订单与车辆关系
	 * @param param
	 * @return
	 */
	public int deleteOrderVehicleRel(Map<String,Object>param);
}
