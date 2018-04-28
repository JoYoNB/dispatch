package com.chainway.dispatcherservice.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.chainway.dispatchercore.common.IVCApiUtils;
import com.chainway.dispatchercore.dto.Vehicle;
import com.chainway.dispatcherservice.biz.dao.DispatcherMonitorDao;
import com.chainway.dispatcherservice.service.DispatcherMonitorService;

@Component
@Service
public class DispatcherMonitorServiceImpl implements DispatcherMonitorService {

	@Autowired
	private DispatcherMonitorDao dispatcherMonitorDao;
	
	@Override
	public List<Map<String, Object>> getVehiclesLocation(List<Vehicle> vehicles) {
		StringBuilder sb = new StringBuilder();
		for (Vehicle vehicle : vehicles) {
			sb.append(vehicle.getVehicleId()).append(",");
		}
		//调用平台接口查询车辆实时位置
		IVCApiUtils ivcApiUtils = IVCApiUtils.getInstance();
		List<Map<String, Object>> vehicleLastLocation = ivcApiUtils.getVehicleLastLocation(sb.toString());
		//拼装载货状态
		for (Vehicle vehicle : vehicles) {
			for (Map<String, Object> map : vehicleLastLocation) {
				if(vehicle.getVehicleId().equals(map.get("vehicleId"))) {
					map.put("loadRate", vehicle.getLoadRate());
					break;
				}
			}
			
		}
		return vehicleLastLocation;
	}

	@Override
	public List<Map<String, Object>> getOrderStartSiteLocation(Map<String, Object> param) {
		return dispatcherMonitorDao.getOrderStartSiteLocation(param);
	}

}
