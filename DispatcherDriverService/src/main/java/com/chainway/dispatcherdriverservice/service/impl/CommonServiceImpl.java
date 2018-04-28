package com.chainway.dispatcherdriverservice.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.chainway.dispatcherdriverservice.biz.dao.CommonDao;
import com.chainway.dispatcherdriverservice.biz.service.DemoService;
import com.chainway.dispatcherdriverservice.service.CommonService;

@Component  
@Service
public class CommonServiceImpl implements CommonService {

	@Autowired
	private DemoService demoService;
	
	@Autowired
	private CommonDao commonDao;
	
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

	@Override
	public void updateVehicleLoadRate(Map<String, Object> param) {
		commonDao.updateVehicleLoadRate(param);
	}

}
