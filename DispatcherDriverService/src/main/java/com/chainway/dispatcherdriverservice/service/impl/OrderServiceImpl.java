package com.chainway.dispatcherdriverservice.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.chainway.dispatchercore.common.OrderStatus;
import com.chainway.dispatchercore.dto.OrderLifecycle;
import com.chainway.dispatchercore.excetion.ExceptionCode;
import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherdriverservice.annotation.WriteDataSource;
import com.chainway.dispatcherdriverservice.biz.dao.OrderDao;
import com.chainway.dispatcherdriverservice.service.OrderService;

@Service
@Component
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderDao orderDao;
	
	
	@Override
	public List<Map<String, Object>> getOrderList(Map<String, Object> param) {
		return orderDao.getOrderList(param);
	}

	@Override
	public Integer getOrderListCount(Map<String, Object> param) {
		return orderDao.getOrderListCount(param);
	}

	@Override
	public Map<String, Object> getOrderInfo(Map<String, Object> param) {
		return orderDao.getOrderInfo(param);
	}
	@Override
	@WriteDataSource
	@Transactional
	public void confirmOrder(Map<String, Object>param) throws Exception {
		//先查询订单状态，并锁住行
		int orderStatus = orderDao.getOrderStatusAndLock(param);
		//不是已分配的订单，则抛异常
		if(OrderStatus.ASSIGNED!=orderStatus) {
			throw new ServiceException(ExceptionCode.ERROR_ORDER_STATUS_HAS_CHANGED, "订单状态已改变");
		}
		orderDao.confirmOrder(param);
		//记录修改日志
		OrderLifecycle orderLifecycle = new OrderLifecycle();
		orderLifecycle.setOrderNo(param.get("orderNo")==null?"":(String)param.get("orderNo"));
		orderLifecycle.setOperatorId(param.get("driverId")==null?-1:(Integer)param.get("driverId"));
		orderLifecycle.setOperatorName(param.get("driverName")==null?"":(String)param.get("driverName"));
		orderLifecycle.setOperation(11);//11为司机确认
		orderLifecycle.setOrderStatus(OrderStatus.WAIT_FOR_PICK_UP);//订单确认后状态为待提货
		orderLifecycle.setIp(param.get("ip")==null?"":(String)param.get("ip"));
		
		//TODO 内容模板
		String content = "";
		orderLifecycle.setContent(content);
		orderDao.addOrderLifecycle(orderLifecycle);
	}
	
	@Override
	@WriteDataSource
	@Transactional
	public void pickup(Map<String, Object>param) throws Exception {
		//先查询订单状态，并锁住行
		int orderStatus = orderDao.getOrderStatusAndLock(param);
		//不是待提货的订单，则抛异常
		if(OrderStatus.WAIT_FOR_PICK_UP!=orderStatus) {
			throw new ServiceException(ExceptionCode.ERROR_ORDER_STATUS_HAS_CHANGED, "订单状态已改变");
		}
		orderDao.pickup(param);
		
		//记录修改日志
		OrderLifecycle orderLifecycle = new OrderLifecycle();
		orderLifecycle.setOrderNo(param.get("orderNo")==null?"":(String)param.get("orderNo"));
		orderLifecycle.setOperatorId(param.get("driverId")==null?-1:(Integer)param.get("driverId"));
		orderLifecycle.setOperatorName(param.get("driverName")==null?"":(String)param.get("driverName"));
		orderLifecycle.setOperation(12);//11为司机提货
		orderLifecycle.setOrderStatus(OrderStatus.IN_TRANSIT);//订单确认后状态为配送中
		orderLifecycle.setIp(param.get("ip")==null?"":(String)param.get("ip"));
		
		//TODO 内容模板
		String content = "";
		orderLifecycle.setContent(content);
		orderDao.addOrderLifecycle(orderLifecycle);
	}
	

	@Override
	public void uploadReceipt(Map<String, Object> param) throws Exception {
		int effectNum = orderDao.uploadReceipt(param);
		if(effectNum<=0) {
			throw new ServiceException(ExceptionCode.ERROR_DRIVER_UPLOAD_RECEIPT_FAIL,"上传签单失败");
		}
	}

	@Override
	public List<Map<String, Object>> getReceiptList(Map<String, Object> param) {
		return orderDao.getReceiptList(param);
	}

	@Override
	public void finishOrder(String orderNo) throws Exception {
		int orderStatus = orderDao.getOrderStatus(orderNo);
		if(OrderStatus.IN_TRANSIT==orderStatus) {
			orderDao.finishOrder(orderNo);
		}else {
			throw new ServiceException(ExceptionCode.ERROR_ORDER_STATUS_HAS_CHANGED,"订单状态已改变");
		}
	}

}
