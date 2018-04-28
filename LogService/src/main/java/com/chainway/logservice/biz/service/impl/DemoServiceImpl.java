package com.chainway.logservice.biz.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chainway.logservice.annotation.WriteDataSource;
import com.chainway.logservice.biz.dao.DemoDao;
import com.chainway.logservice.biz.service.DemoService;

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
