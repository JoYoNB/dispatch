package com.chainway.settlementservice.biz.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chainway.settlementservice.annotation.WriteDataSource;
import com.chainway.settlementservice.biz.dao.DemoDao;
import com.chainway.settlementservice.biz.service.DemoService;

@Service
public class DemoServiceImpl implements DemoService {

	@Autowired
	private DemoDao demoDao;
	
	@Override
	@WriteDataSource
	public Map<String, Object> test(Map<String, Object> param) {
		Map<String,Object>ret=demoDao.test(param);
		return ret;
	}

}
