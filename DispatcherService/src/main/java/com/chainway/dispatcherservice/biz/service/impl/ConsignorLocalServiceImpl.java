package com.chainway.dispatcherservice.biz.service.impl;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.chainway.dispatchercore.common.MD5Util;
import com.chainway.dispatchercore.dto.OrderLifecycle;
import com.chainway.dispatchercore.excetion.ExceptionCode;
import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherservice.annotation.WriteDataSource;
import com.chainway.dispatcherservice.biz.dao.OrderDao;
import com.chainway.dispatcherservice.biz.dao.OrderLifecycleDao;
import com.chainway.dispatcherservice.biz.service.ConsignorLocalService;
import com.chainway.dispatcherservice.dto.OrderParam;

@Service
public class ConsignorLocalServiceImpl implements ConsignorLocalService {

	@Autowired
	OrderDao orderDao;
	@Autowired
	OrderLifecycleDao logDao;

	@Override
	@Transactional
	@WriteDataSource
	public void createOrder(OrderParam param) throws ServiceException{
		//生成订单号
		String orderNo = MD5Util.getMD5String(UUID.randomUUID().toString());
		param.setOrderNo(orderNo);
		//添加订单站点关联信息
		if(param.getSites()==null||param.getSites().size()<2){
			throw new ServiceException(ExceptionCode.ERROR_ORDER_NO_SITES,"订单站点信息不正确");
		}
		for(int i=0;i<param.getSites().size();i++){
			param.getSites().get(i).setOrderNo(orderNo);
		}
		//添加订单信息
		orderDao.createOrder(param);
		orderDao.createOrderSites(param.getSites());
		//添加订单日志
		OrderLifecycle orderLifecycle = new OrderLifecycle();
		orderLifecycle.setIp(param.getIp());
		orderLifecycle.setOperation(1);
		orderLifecycle.setOperatorId(param.getCreaterId());
		orderLifecycle.setOrderNo(orderNo);
		orderLifecycle.setOrderStatus(10);
		orderLifecycle.setPayStatus(10);
		orderLifecycle.setContent("{}");
		logDao.add(orderLifecycle);
	}
	
	@Override
	@Transactional
	@WriteDataSource
	public void modifyOrder(OrderParam param) throws ServiceException {
		//查询订单状态
		Map<String, Integer> statusmap = orderDao.getOrderStatus(param.getOrderNo());
		Integer status = statusmap.get("order_status");
		if(status==null){
			throw new ServiceException(ExceptionCode.ERROR_ORDER_NOT_EXISTS,"订单不存在",param.getOrderNo());
		}
		OrderLifecycle orderLifecycle = new OrderLifecycle();
		orderLifecycle.setIp(param.getIp());
		orderLifecycle.setOperatorId(param.getCreaterId());
		orderLifecycle.setOrderNo(param.getOrderNo());
		orderLifecycle.setOrderStatus(status.intValue());
		if(status.intValue()==10){//待发布状态可以修改
			//先修改订单主表
			orderDao.modifyOrder(param);
			//修改订单站点关联表
			if(param.getSites()==null||param.getSites().size()<=0){
				//没有站点信息不更新关联表
			}else if(param.getSites().size()<2){
				//站点至少有两个，起点、终点
				throw new ServiceException(ExceptionCode.ERROR_ORDER_NO_SITES,"订单站点数量不正确");
			}else {
				//先删除原来的关联关系
				orderDao.deleteOrderSites(param.getOrderNo());
				//再新增关联关系
				orderDao.createOrderSites(param.getSites());
			}
			if(param.getPayStatus()!=null&&param.getPayStatus()==20){//发布订单				
				orderLifecycle.setOperation(7);
				orderLifecycle.setPayStatus(20);
				orderLifecycle.setContent("{}");
			}else {
				orderLifecycle.setOperation(2);
				orderLifecycle.setPayStatus(10);
				orderLifecycle.setContent("{}");
			}
		}else if (status.intValue()==30||status.intValue()==60) {//已取消和已失效状态只能编辑要求提货时间
			OrderParam order = new OrderParam();
			order.setOrderNo(param.getOrderNo());
			order.setPickupTime(param.getPickupTime());
			orderDao.modifyOrder(order);
			orderLifecycle.setOperation(2);
			orderLifecycle.setPayStatus(10);
			orderLifecycle.setContent("{}");
		}else {//其他状态不能编辑
			throw new ServiceException(ExceptionCode.ERROR_ORDER_NO_MODIFY,"订单当前状态不支持编辑",status);
		}
		//添加订单日志
		logDao.add(orderLifecycle);
	}
	
	@Override
	@Transactional
	@WriteDataSource
	public void deleteOrder(OrderParam param) throws ServiceException {
		//查询订单状态
		Map<String, Integer> statusmap = orderDao.getOrderStatus(param.getOrderNo());
		Integer status = statusmap.get("order_status");
		Integer payStatus = statusmap.get("pay_status");
		if(status==null||payStatus==null){//订单不存在，默认成功,未获取到支付状态不能删除
			//throw new ServiceException(ExceptionCode.ERROR_ORDER_NOT_EXISTS,"订单不存在",orderNo);
			return;
		}else if(status==10||((status==30||status==60)&&payStatus==50)){
			//1.待发布可以删除
			//2.已失效、已取消状态完成退款的可以删除
			OrderParam order = new OrderParam();
			order.setStatus(2);//状态更新为已删除
			order.setOrderNo(param.getOrderNo());
			orderDao.modifyOrder(order);
			//添加订单日志
			OrderLifecycle orderLifecycle = new OrderLifecycle();
			orderLifecycle.setIp(param.getIp());
			orderLifecycle.setOperation(3);
			orderLifecycle.setOperatorId(param.getCreaterId());
			orderLifecycle.setOrderNo(param.getOrderNo());
			orderLifecycle.setOrderStatus(status);
			orderLifecycle.setPayStatus(payStatus);
			orderLifecycle.setContent("{}");
			logDao.add(orderLifecycle);
		}else {
			throw new ServiceException(ExceptionCode.ERROR_ORDER_NO_DELETE,"订单当前状态不能删除",statusmap);
		}
		
	}
}
