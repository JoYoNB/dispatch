package com.chainway.dispatcherdriverservice.biz.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chainway.dispatcherdriverservice.annotation.WriteDataSource;
import com.chainway.dispatcherdriverservice.biz.dao.DemoDao;
import com.chainway.dispatcherdriverservice.biz.service.DemoService;


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
