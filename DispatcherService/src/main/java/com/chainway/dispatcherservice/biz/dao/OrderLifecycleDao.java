package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;

import com.chainway.dispatchercore.dto.OrderLifecycle;

/**
 * 订单生命周期
 * @author chainwayits
 * @date 2018年3月27日
 */
public interface OrderLifecycleDao {
	/**
	 * 新增
	 * @param chargeRule
	 */
	public void add(OrderLifecycle orderLifecycle);
	/**
	 * 查询
	 * @param orderNo
	 * @return
	 */
	public List<OrderLifecycle> query(Map<String,Object> param);
}
