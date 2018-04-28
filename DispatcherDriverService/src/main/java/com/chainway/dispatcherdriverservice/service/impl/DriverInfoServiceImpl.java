package com.chainway.dispatcherdriverservice.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.chainway.dispatchercore.dto.Driver;
import com.chainway.dispatcherdriverservice.annotation.WriteDataSource;
import com.chainway.dispatcherdriverservice.biz.dao.DriverInfoDao;
import com.chainway.dispatcherdriverservice.service.DriverInfoService;

@Service
@Component
public class DriverInfoServiceImpl implements DriverInfoService {

	@Autowired
	private DriverInfoDao driverInfodao;
	
	
	@Override
	public Driver getDriver(Driver driver) {
		return driverInfodao.getDriver(driver);
	}

	@Override
	public Map<String, Object> getDriverInfo(Driver driver) {
		return driverInfodao.getDriverInfo(driver);
	}
	@Override
	public Map<String, Object> getDriverStatis(Driver driver) {
		return driverInfodao.getDriverStatis(driver);
	}
	@Override
	public Map<String, Object> getVehicleInfo(Driver driver) {
		return driverInfodao.getVehicleInfo(driver);
	}

	@Override
	@WriteDataSource
	public void updateOnlineStatus(Map<String, Object> param) {
		driverInfodao.updateOnlineStatus(param);
	}

	

	
}
