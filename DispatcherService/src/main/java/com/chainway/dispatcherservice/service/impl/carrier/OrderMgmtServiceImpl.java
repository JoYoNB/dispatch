package com.chainway.dispatcherservice.service.impl.carrier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.chainway.dispatchercore.dto.FileTemplate;
import com.chainway.dispatchercore.dto.OrderLifecycle;
import com.chainway.dispatcherservice.biz.dao.CarrierOrderDao;
import com.chainway.dispatcherservice.biz.dao.OrderLifecycleDao;
import com.chainway.dispatcherservice.dto.CarrierMatchRule;
import com.chainway.dispatcherservice.service.carrier.AssignmentService;
import com.chainway.dispatcherservice.service.carrier.OrderMgmtService;
import com.chainway.fileservice.service.FileService;


@Component  
@Service
public class OrderMgmtServiceImpl implements OrderMgmtService {
	
	@Autowired
	private CarrierOrderDao orderDao;
	
	@Autowired  
	private AssignmentService assignmentService;
	
	
	@Autowired
	private OrderLifecycleDao orderLifecycleDao;
	
	@Reference(timeout=60000, check=false)
	private FileService fileService;
	
	@Override
	public List<Map<String,Object>> getOrderList(Map<String, Object> params) {
		setCarrierRule(params);
		return orderDao.getOrderList(params);
	}

	@Override
	public int getOrderListCount(Map<String, Object> params) {
		setCarrierRule(params);
		return orderDao.getOrderListCount(params);
	}

	private void setCarrierRule(Map<String, Object> params) {
		int carrierDept = (int) params.get("carrierDept");
		CarrierMatchRule rule = assignmentService.getCarrierMatchRuleByDept(carrierDept);
		params.put("mileageMin", rule.getMileageMin());
		params.put("mileageMax", rule.getMileageMax());
		params.put("districts", StringUtils.join(rule.getDistricts(),","));
		params.put("goodsTypes", StringUtils.join(rule.getGoodsTypes(),","));
	}

	@Override
	public String export(Map<String, Object> params) {
		FileTemplate ftl = new FileTemplate();
		ftl.setCode("carrier_order_export");
		List<Map<String, Object>> list = orderDao.getOrderList(params);
		String token = "123456";
		List<Object> data = new ArrayList<Object>();
		for(Map<String, Object> map : list){
			String startSiteName = String.valueOf(map.get("startSiteName"));
			String startAddr = String.valueOf(map.get("startAddr"));
			map.put("startName", startSiteName + "|" + startAddr);
			String endSiteName = String.valueOf(map.get("endSiteName"));
			String endAddr = String.valueOf(map.get("endAddr"));
			map.put("endName", endSiteName + "|" + endAddr);
			String packageNum = String.valueOf(map.get("packageNum") == null ? "" : map.get("packageNum"));
			String volume = String.valueOf(map.get("volume") == null ? "" : map.get("volume"));
			String weight = String.valueOf(map.get("weight") == null ? "" : map.get("weight"));
			String freightVolume ="";
			if(StringUtils.isNotEmpty(packageNum)){
				freightVolume += packageNum + "件|";
			}
			if(StringUtils.isNotEmpty(volume)){
				freightVolume += volume + "方|";
			}
			if(StringUtils.isNotEmpty(weight)){
				freightVolume += packageNum + "吨";
			}
			map.put("freightVolume", freightVolume);
			data.add(map);
		}
		try {
			return fileService.export(ftl, data, token);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public Map<String, Object> getOrder(Map<String, Object> params) {
		return orderDao.getOrder(params);
	}

	@Override
	public List<Map<String, Object>> listOrderSites(Map<String, Object> params) {
		return orderDao.listOrderSites(params);
	}

	@Override
	public List<OrderLifecycle> listOrderLifeCirclys(Map<String, Object> params) {
		return orderLifecycleDao.query(params);
	}

}
