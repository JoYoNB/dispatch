package com.chainway.dispatcherdriverservice.service;

import java.util.List;
import java.util.Map;

public interface OrderService {
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
	 * 确认订单
	 * @return
	 */
	public void confirmOrder(Map<String, Object>param) throws Exception;
	/**
	 * 提货
	 * @return
	 */
	public void pickup(Map<String, Object>param) throws Exception;
	/**
	 * 上传回单
	 * @param param
	 * @return
	 */
	public void uploadReceipt(Map<String, Object> param) throws Exception;
	/**
	 * 回单列表
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> getReceiptList(Map<String, Object> param);
	
	/**
	 * 结束订单
	 * @param param
	 * @return
	 */
	void finishOrder(String orderNo)  throws Exception;
}
