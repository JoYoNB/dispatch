package com.chainway.dispatcherservice.biz.service;

import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherservice.dto.OrderParam;

/**
 * 货主订单管理
 * @author xubao
 * @date 2018年3月23日
 */
public interface ConsignorLocalService {
	/**
	 * 创建订单，事务
	 * @param param
	 */
	void createOrder(OrderParam param)throws ServiceException;
	
	/**
	 * 修改订单
	 * @param param
	 * @throws ServiceException
	 */
	void modifyOrder(OrderParam param)throws ServiceException;
	
	/**
	 * 根据订单编号删除订单
	 * @param orderNo
	 * @throws ServiceException
	 */
	void deleteOrder(OrderParam param)throws ServiceException;
}
