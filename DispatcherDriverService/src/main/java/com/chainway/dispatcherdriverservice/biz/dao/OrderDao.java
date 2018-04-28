package com.chainway.dispatcherdriverservice.biz.dao;

import java.util.List;
import java.util.Map;

import com.chainway.dispatchercore.dto.OrderLifecycle;

public interface OrderDao {
	/**
	 * 订单列表
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> getOrderList(Map<String, Object> param);
	/**
	 * 订单列表数量
	 * @param param
	 * @return
	 */
	public Integer getOrderListCount(Map<String, Object> param);
	/**
	 * 订单详情
	 * @param param
	 * @return
	 */
	public Map<String, Object> getOrderInfo(Map<String, Object> param);
	/**
	 * 查询订单状态并锁住行
	 * @param param
	 * @return
	 */
	public int getOrderStatusAndLock(Map<String, Object> param);
	/**
	 * 确认订单
	 * @return
	 */
	public int confirmOrder(Map<String, Object>param);
	/**
	 * 提货
	 * @return
	 */
	public int pickup(Map<String, Object>param);
	/**
	 * 记录操作订单信息
	 * @param param
	 */
	public void addOrderLifecycle(OrderLifecycle orderLifecycle);
	/**
	 * 上传回单
	 * @param param
	 * @return
	 */
	public int uploadReceipt(Map<String, Object> param);
	/**
	 * 回单列表
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> getReceiptList(Map<String, Object> param);
	/**
	 * 查询订单状态
	 * @param orderNo
	 * @return
	 */
	public int getOrderStatus(String orderNo);
	/**
	 * 结束订单
	 * @param param
	 * @return
	 */
	void finishOrder(String orderNo);
	
}
