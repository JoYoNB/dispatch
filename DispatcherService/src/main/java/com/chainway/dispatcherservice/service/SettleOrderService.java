package com.chainway.dispatcherservice.service;

import java.util.List;
import java.util.Map;

import com.chainway.dispatchercore.dto.OrderLifecycle;
import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherservice.dto.Order;

/**
 * 订单结算管理
 * @author chainwayits
 * @date 2018年3月27日
 */
public interface SettleOrderService {
	/**
	 * 查询列表
	 * @param param
	 * @return
	 */
	public List<Order> getList(Map<String, Object> param);
	/**
	 * 查询数目
	 * @param param
	 * @return
	 */
	public int getListCount(Map<String,Object>param); 
	/**
	 * 查询详情
	 * @param orderNo
	 * @return
	 */
	public Map<String, Object> getOrderInfo(String orderNo);
	/**
	 * 结算
	 * @param param
	 * @throws ServiceException 
	 */
	public void settle(Map<String,Object>param) throws ServiceException;
	/**
	 * 收款
	 * @param param
	 * @throws ServiceException 
	 */
	public void receipt(Map<String,Object>param) throws ServiceException;
	
	/**
	 * 订单生命周期
	 * @param param
	 * @return
	 */
	public List<OrderLifecycle> getOrderLifecycle(Map<String,Object>param);
}
