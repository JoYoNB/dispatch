package com.chainway.dispatcherservice.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.chainway.dispatchercore.common.OrderStatus;
import com.chainway.dispatchercore.common.PayStatus;
import com.chainway.dispatchercore.dto.OrderLifecycle;
import com.chainway.dispatchercore.excetion.ExceptionCode;
import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherservice.annotation.WriteDataSource;
import com.chainway.dispatcherservice.biz.dao.OrderLifecycleDao;
import com.chainway.dispatcherservice.biz.dao.SettleOrderDao;
import com.chainway.dispatcherservice.dto.Order;
import com.chainway.dispatcherservice.service.SettleOrderService;

@Component  
@Service
public class SettleOrderServiceImpl implements SettleOrderService {
	
	@Autowired
	private SettleOrderDao settleOrderDao;
	@Autowired
	private OrderLifecycleDao orderLifecycleDao;

	@Override
	public List<Order> getList(Map<String, Object> param) {
		return settleOrderDao.getList(param);
	}

	@Override
	public int getListCount(Map<String, Object> param) {
		return settleOrderDao.getListCount(param);
	}

	@Override
	public Map<String, Object> getOrderInfo(String orderNo) {
		return settleOrderDao.getOrderInfo(orderNo);
	}

	@Override
	@Transactional
	@WriteDataSource
	public void settle(Map<String, Object> param) throws ServiceException {
		String orderNo=(String) param.get("orderNo");
		Map<String, Object> order=settleOrderDao.getOrderInfo(orderNo);
		int payStatus=(int) order.get("payStatus");
		int orderStatus=(int) order.get("orderStatus");
		double fee=(double) order.get("fee");
		int[] allowPayStatus={PayStatus.PAID};//允许结算的支付类型
		int[] allowOrderStatus={OrderStatus.FINISHED};//允许结算的运输类型
		boolean allow=false;
		for (int i : allowOrderStatus) {
			if(i==orderStatus) {
				allow=true;
				break;
			}
		}
		if(!allow) throw new ServiceException(ExceptionCode.ERROR_ORDERSTATUS_CANNT_SETTLE,"当前运输状态不能结算");
		allow=false;
		for (int i : allowPayStatus) {
			if(i==payStatus) {
				allow=true;
				break;
			}
		}
		if(!allow) throw new ServiceException(ExceptionCode.ERROR_PAYSTATUS_CANNT_SETTLE,"当前支付状态不能结算");
		OrderLifecycle orderLifecycle=new OrderLifecycle();
		JSONObject content=new JSONObject();
		content.put("fee", fee);
		orderLifecycle.setContent(content.toJSONString());
		orderLifecycle.setIp((String)param.get("ip"));
		orderLifecycle.setOperation(14);
		orderLifecycle.setOperatorId((Integer)param.get("operatorId"));
		orderLifecycle.setOrderNo(orderNo);
		orderLifecycle.setOrderStatus(orderStatus);
		orderLifecycle.setPayStatus(payStatus);
		orderLifecycleDao.add(orderLifecycle);
		settleOrderDao.settle(orderNo);
	}

	@Override
	public List<OrderLifecycle> getOrderLifecycle(Map<String, Object> param) {
		return orderLifecycleDao.query(param);
	}

	@Override
	public void receipt(Map<String, Object> param) throws ServiceException {
		String orderNo=(String) param.get("orderNo");
		Map<String, Object> order=settleOrderDao.getOrderInfo(orderNo);
		int payStatus=(int) order.get("payStatus");
		int orderStatus=(int) order.get("orderStatus");
		double fee=(double) order.get("fee");
		int[] allowPayStatus={PayStatus.UNPAID};//允许收款的支付类型
		boolean allow=false;
		for (int i : allowPayStatus) {
			if(i==payStatus) {
				allow=true;
				break;
			}
		}
		if(!allow) throw new ServiceException(ExceptionCode.ERROR_PAYSTATUS_CANNT_SETTLE,"当前支付状态不能收款");
		OrderLifecycle orderLifecycle=new OrderLifecycle();
		JSONObject content=new JSONObject();
		content.put("fee", fee);
		orderLifecycle.setContent(content.toJSONString());
		orderLifecycle.setIp((String)param.get("ip"));
		orderLifecycle.setOperation(15);
		orderLifecycle.setOperatorId((Integer)param.get("operatorId"));
		orderLifecycle.setOrderNo(orderNo);
		orderLifecycle.setOrderStatus(orderStatus);
		orderLifecycle.setPayStatus(payStatus);
		orderLifecycleDao.add(orderLifecycle);
		settleOrderDao.receipt(orderNo);
	}
}
