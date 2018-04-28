package com.chainway.dispatcherservice.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.chainway.dispatchercore.dto.Driver;
import com.chainway.dispatcherservice.annotation.WriteDataSource;
import com.chainway.dispatcherservice.biz.dao.DriverDao;
import com.chainway.dispatcherservice.service.DriverService;

@Component 
@Service
public class DriverServiceImpl implements DriverService {

	@Autowired
	private DriverDao driverDao;
	
	@Override
	@WriteDataSource
	public Boolean add(Driver driver) throws Exception {
		Integer num = driverDao.add(driver);
		return num>0?true:false;
	}

	@Override
	public Driver getDriver(Map<String, Object> paramMap) {
		return driverDao.getDriver(paramMap);
	}

	@Override
	public List<Map<String, Object>> getDriverList(Map<String, Object> paramMap) {
		List<Map<String, Object>> driverList = driverDao.getDriverList(paramMap);
		return driverList;
	}

	@Override
	public Integer getDriverListCount(Map<String, Object> paramMap) {
		return driverDao.getDriverListCount(paramMap);
	}

	@Override
	@WriteDataSource
	public Boolean update(Driver driver) throws Exception {
		Integer num = driverDao.update(driver);
		return num>0?true:false;

	}

	@Override
	@WriteDataSource
	public Boolean delete(Map<String, Object> paramMap) {
		Integer num = driverDao.delete(paramMap);
		return num>0?true:false;
	}

}
