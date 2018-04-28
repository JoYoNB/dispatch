package com.chainway.dispatcherservice.service.impl.carrier;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.chainway.dispatchercore.common.IVCApiUtils;
import com.chainway.dispatchercore.common.OrderStatus;
import com.chainway.dispatchercore.common.PropertiesUtil;
import com.chainway.dispatchercore.common.TimeUtil;
import com.chainway.dispatcherservice.biz.dao.CarrierMonitorDao;
import com.chainway.dispatcherservice.biz.dao.CarrierOrderDao;
import com.chainway.dispatcherservice.service.carrier.MonitorService;

@Component("carrierMonitorService")  
@Service
public class MonitorServiceImpl implements MonitorService {
	@Autowired
	private CarrierMonitorDao monitorDao;
	
	@Autowired
	private CarrierOrderDao orderDao; 

	@Override
	public List<Map<String, Object>> listVehiclePositions(Map<String, Object> map) {
		//最大显示车辆数量
		Integer maxVehicleNum = PropertiesUtil.getInteger("max.vehicle.num");
		map.put("maxVehicleNum", maxVehicleNum);
		List<Map<String, Object>> list =  monitorDao.listVehicles(map);
		String vIds = "";
		for(Map<String, Object> vehicle : list){
			vIds += vehicle.get("tVehicleIdOhter") + ",";
		}
		if("".equals(vIds)||vIds.length()<1) return null;//无车辆
		vIds = vIds.substring(0, vIds.length() - 1);
		//调用接口获取车辆最新位置
		IVCApiUtils apiUtils = IVCApiUtils.getInstance();
		List<Map<String, Object>> locations = apiUtils.getVehicleLastLocation(vIds);
		for(int i=0; i<list.size(); i++){
			Map<String, Object> vehicle = list.get(i);
			//其他平台车辆id不为空，则匹配其经纬度位置
			Integer tVehicleIdOhter = (Integer) vehicle.get("tVehicleIdOhter");
			if(null!=tVehicleIdOhter) {
				Boolean locExist=false;
				for (Map<String, Object> location : locations) {
					if(tVehicleIdOhter.equals(location.get("vehicle_id"))) {
						vehicle.putAll(location);
						locExist=true;
						break;
					}
				}
				if(!locExist) {
					list.remove(i);
				}
			}
		}
		return list;
	}
	private List<Map<String, Object>> testApis(String vehicleIds) {
		String[] vehicles = vehicleIds.split(",");
		List<Map<String, Object>> list = new ArrayList<>();
		for(int i=0; i<vehicles.length; i++){
			Map<String, Object> map = new HashMap<>();
			map.put("position", "测试地址" + i);
			map.put("vehicle_id", vehicles[i]);
			Random rnd = new Random();
			map.put("lon", 114.07 + (rnd.nextInt(99))/1000.0);
			map.put("lat", 22.62+ (rnd.nextInt(99))/1000.0);
			map.put("state", rnd.nextInt(2));
			map.put("speed", rnd.nextInt(100));
			list.add(map);
		}
 		return list;
	}

	@Override
	public List<Map<String, Object>> listOrderPositions(Map<String, Object> map) {
		List<Map<String, Object>> list =  monitorDao.listOrders(map);
		return list;
	}

	@Override
	public Map<String, Object> getOrderTransportRoute(Map<String, Object> map) {
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> tracks= null;
		String orderNo = (String) map.get("orderNo");
		int orderStatus = orderDao.getOrderStatus(orderNo);
		// 获取各配送点位置
		List<Map<String, Object>> sites = orderDao.listOrderSites(map);
		result.put("sites", sites);
		if (orderStatus == OrderStatus.IN_TRANSIT || orderStatus == OrderStatus.FINISHED) {
			Map<String, Object> qInfo = monitorDao.getTraceQueryInfo(orderNo);
			if(qInfo.get("vehicleOtherId") != null){
				int vehicleId = (int) qInfo.get("vehicleOtherId");
				Date startTime = qInfo.get("pickupTime") == null ? new Date() :(Date) qInfo.get("pickupTime");
				Date endTime = qInfo.get("finishTime") == null ?endTime = new Date() : (Date) qInfo.get("finishTime");
				// 获取车辆轨迹
				IVCApiUtils apiUtils = IVCApiUtils.getInstance();
				String vId = String.valueOf(vehicleId);
				String startTimeStr = TimeUtil.time2String(startTime, TimeUtil.FORMAT_TIME);
				String endTimeStr = TimeUtil.time2String(endTime, TimeUtil.FORMAT_TIME);
				int pageNum = map.get("pageNum") == null ? 1: (int)map.get("pageNum");
				tracks = apiUtils.getVehicleTrajectory(vId, startTimeStr, endTimeStr, pageNum);
			}
		} 
		return tracks;
	}

	@Override
	public Map<String, Object> getVehicleCount(Map<String, Object> paramMap) {
		paramMap.put("loadStatus", "total");
		int total = monitorDao.getVehicleCount(paramMap);
		paramMap.put("loadStatus", "noLoad");
		int noLoad = monitorDao.getVehicleCount(paramMap);
		paramMap.put("loadStatus", "halfLoad");
		int halfLoad = monitorDao.getVehicleCount(paramMap);
		paramMap.put("loadStatus", "fullLoad");
		int fullLoad = monitorDao.getVehicleCount(paramMap);
		
		int offLine = monitorDao.getOffLineDriverCount(paramMap);
		Map<String, Object> result = new HashMap<>();
		result.put("total", total);
		result.put("noLoad", noLoad);
		result.put("halfLoad", halfLoad);
		result.put("fullLoad", fullLoad);
		result.put("offLine", offLine);
		
		return result;
	}
	@Override
	public List<Map<String, Object>> listSitePositions(Map<String, Object> map) {
		List<Map<String, Object>> list =  monitorDao.listSites(map);
		return list;
	}

}
