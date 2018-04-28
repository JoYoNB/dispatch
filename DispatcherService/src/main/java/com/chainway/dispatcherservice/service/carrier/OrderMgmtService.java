package com.chainway.dispatcherservice.service.carrier;

import java.util.List;
import java.util.Map;

import com.chainway.dispatchercore.dto.OrderLifecycle;

public interface OrderMgmtService {
	public List<Map<String,Object>> getOrderList(Map<String, Object> maps);

	public int getOrderListCount(Map<String, Object> param);

	public String export(Map<String, Object> params);

	public Map<String, Object> getOrder(Map<String, Object> params);

	public List<Map<String, Object>> listOrderSites(Map<String, Object> params);

	public List<OrderLifecycle> listOrderLifeCirclys(Map<String, Object> params);
	
}
