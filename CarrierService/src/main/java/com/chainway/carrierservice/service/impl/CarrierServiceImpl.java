package com.chainway.carrierservice.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.chainway.carrierservice.biz.service.DemoService;
import com.chainway.carrierservice.service.CarrierService;

@Component  
@Service
public class CarrierServiceImpl implements CarrierService {

	@Autowired
	private DemoService demoService;
	
	@Override
	public Map<String, Object> test(Map<String, Object> param) {
		
		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("a", 1);
		ret.put("b", false);
		ret.put("c", "c");
		ret.put("d", new Date());
		
		ret.putAll(demoService.test(param));
		
		return ret;
	}

}
