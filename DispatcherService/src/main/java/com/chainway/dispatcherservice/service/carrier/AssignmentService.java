package com.chainway.dispatcherservice.service.carrier;

import java.util.List;
import java.util.Map;

import com.chainway.dispatcherservice.dto.CarrierMatchRule;

public interface AssignmentService {
	/**
	 * 新订单消息
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> listPublishedOrders(Map<String, Object> map);
	
	/**
	 * 承运商接单
	 * @param map
	 * @return
	 * @throws ServiceException 
	 */
	void accept(Map<String, Object> map) throws Exception;
	
	
	
	/**
	 * 分配司机订单
	 * @param map
	 * @return
	 * @throws ServiceException 
	 */
	void assign(Map<String, Object> map) throws Exception;
	
	/**
	 * 获取司机列表
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> listSuitableVehicles(Map<String, Object> map);
	
	/**
	 * 获取司机车辆个数
	 * @param paramMap
	 * @return
	 */
	int getSuitableVehiclesCount(Map<String, Object> paramMap);
	
	/**
	 * 获取承运商匹配规则
	 * @param carrierDept
	 * @return
	 */
	public CarrierMatchRule getCarrierMatchRuleByDept(int carrierDept);
}
