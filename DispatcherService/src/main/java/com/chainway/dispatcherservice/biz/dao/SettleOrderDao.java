package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;

import com.chainway.dispatcherservice.dto.Order;


/**
 * 订单结算管理
 * @author chainwayits
 * @date 2018年3月23日
 */
public interface SettleOrderDao {
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
	public Map<String,Object> getOrderInfo(String orderNo);
	/**
	 * 结算
	 * @param param
	 */
	public void settle(String orderNo);
	/**
	 * 收款
	 * @param param
	 */
	public void receipt(String orderNo);
}
