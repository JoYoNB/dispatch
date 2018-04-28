package com.chainway.dispatcherservice.service.impl.carrier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.chainway.dispatchercore.common.IVCApiUtils;
import com.chainway.dispatchercore.common.LocationUtils;
import com.chainway.dispatchercore.common.OrderStatus;
import com.chainway.dispatchercore.dto.Dept;
import com.chainway.dispatchercore.dto.Driver;
import com.chainway.dispatchercore.dto.OrderLifecycle;
import com.chainway.dispatchercore.excetion.ExceptionCode;
import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherservice.annotation.WriteDataSource;
import com.chainway.dispatcherservice.biz.dao.AssignmentDao;
import com.chainway.dispatcherservice.biz.dao.CarrierOrderDao;
import com.chainway.dispatcherservice.biz.dao.DeptDao;
import com.chainway.dispatcherservice.biz.dao.DriverDao;
import com.chainway.dispatcherservice.biz.dao.OrderLifecycleDao;
import com.chainway.dispatcherservice.biz.dao.VehicleDao;
import com.chainway.dispatcherservice.dto.CarrierMatchRule;
import com.chainway.dispatcherservice.dto.Order;
import com.chainway.dispatcherservice.service.carrier.AssignmentService;


@Component  
@Service
public class AssignmentServiceImpl implements AssignmentService {
	@Autowired
	private AssignmentDao assignmentDao;
	
	@Autowired
	private CarrierOrderDao orderDao;
	
	@Autowired
	private OrderLifecycleDao orderLifecycleDao;
	
	@Autowired
	VehicleDao vehicleDao;
	
	@Autowired
	DriverDao driverDao;
	
	@Autowired
	DeptDao deptDao;
	
	@Override
	public CarrierMatchRule getCarrierMatchRuleByDept(int carrierDept) {
		CarrierMatchRule rule = new CarrierMatchRule();
		Map<String, Object> map = assignmentDao.getCarrierMileages(carrierDept);
		if(map != null){
			if(map.get("mileageMax") != null){
				rule.setMileageMax((double)map.get("mileageMax"));
			}
			if(map.get("mileageMin") != null){
				rule.setMileageMin((double)map.get("mileageMin"));
			}
		}
		List<Map<String, Object>> areas = assignmentDao.listCarrierAreas(carrierDept);
		if(areas != null){
			List<String> districts = new ArrayList<>();
			for(Map<String, Object> area : areas){
				String areaId = "";
				if(area.get("districtId") != null){
					areaId = (String)area.get("districtId");
				} else 	if(area.get("cityId") != null){
					areaId = (String)area.get("cityId");
				} else if(area.get("provinceId") != null){
					areaId = (String)area.get("provinceId");
				}
				// 去掉结尾的00
				areaId = areaId.replaceAll("(00)+$", "");
				districts.add(areaId);
			}
			rule.setDistricts(districts);
		}
		List<Map<String, Object>> types = assignmentDao.listCarrierGoodsType(carrierDept);
		if(types != null){
			List<Integer> goodsTypes = new ArrayList<>();
			for(Map<String, Object> type : types){
				int typeId = (int) type.get("typeId");
				goodsTypes.add(typeId);
			}
			rule.setGoodsTypes(goodsTypes);
		}
		return rule;
	}
	
	@Override
	public List<Map<String, Object>> listPublishedOrders(Map<String, Object> map) {
		int carrierDept = (int) map.get("carrierDept");
		List<Map<String, Object>> list =  assignmentDao.listPublishedOrders();
		CarrierMatchRule rule =  getCarrierMatchRuleByDept(carrierDept);
		List<Map<String, Object>> results = new ArrayList<>();
		for(Map<String, Object>  m : list){
			if(m != null){
				Order order = getOrderByMap(m);
				// 校验订单是否满足规则
				if(rule.isMatchedOrder(order)){
					results.add(m);
				}
			}
		}
		return results;
	}

	private Order getOrderByMap(Map<String, Object> m) {
		Order order = new Order();
		order.setGoodsType((int)m.get("goodsType"));
		order.setDistrictId((String)m.get("districtId"));
		order.setDistance((double)m.get("distance"));
		return order;
	}

	@Override
	@Transactional
	@WriteDataSource
	public void accept(Map<String, Object> map) throws ServiceException {
		int carrierDept = (int) map.get("carrierDept");
		String orderNo = (String) map.get("orderNo");
		int userId = (int) map.get("userId");
		String operatorName = (String) map.get("userName");
		String ip = (String) map.get("ip");
		CarrierMatchRule rule =  getCarrierMatchRuleByDept(carrierDept);
		Map<String, Object> orderMap = orderDao.getAnyOrder(map);
		Order order = getOrderByMap(orderMap);
		// 承运商订单规则不匹配
		if(!rule.isMatchedOrder(order)){
			throw new ServiceException(ExceptionCode.ERROR_ORDER_CARRIER_NOT_MATCH,"订单不满足承运商匹配规则");
		}
		int orderStatus = orderDao.getOrderStatusAndLock(orderNo);
		// 不是已发布状态
		if(orderStatus != OrderStatus.PUBLISHED){
			throw new ServiceException(ExceptionCode.ERROR_ORDER_STATUS_HAS_CHANGED,"订单状态已改变");
		}
		map.put("status", 1);
		assignmentDao.recordOrderCarrierRel(map);
		OrderLifecycle orderLifecycle = new OrderLifecycle();
		orderLifecycle.setOrderNo(orderNo);
		orderLifecycle.setOperatorId(userId);
		orderLifecycle.setOperatorName(operatorName);
		orderLifecycle.setOperation(8);
		orderLifecycle.setIp(ip);
		orderLifecycle.setOrderStatus(orderStatus);
		orderLifecycle.setPayStatus((int)orderMap.get("payStatus"));
		Dept dept = new Dept();
		dept.setId(carrierDept);
		dept = deptDao.getDept(dept);
		// to do
		String content = "{carrierDeptName:'"+ dept.getName() +"'}";
		orderLifecycle.setContent(content);
		orderLifecycleDao.add(orderLifecycle);
	}

	@Override
	public List<Map<String,Object>> listSuitableVehicles(Map<String, Object> map) {
		Map<String, Object> order = orderDao.getAnyOrder(map);
		String startCoordinate = String.valueOf(order.get("startCoordinate"));
		double orderLon = Double.valueOf(startCoordinate.split(",")[0]);
		double orderLat = Double.valueOf(startCoordinate.split(",")[1]);
		List<Map<String, Object>> list = assignmentDao.listReadyVehicles(map);
		String vehicleIds = "";
		for(Map<String, Object> m : list){
			vehicleIds += String.valueOf(m.get("vehicleIdOther")) + ",";
		}
		if(vehicleIds.length() > 0){
			vehicleIds = vehicleIds.substring(0, vehicleIds.length() - ",".length());
		}
		
		//调用接口获取车辆最新位置
		IVCApiUtils apiUtils = IVCApiUtils.getInstance();
		// List<Map<String, Object>> locations = apiUtils.getVehicleLastLocation(vehicleIds);
		List<Map<String, Object>> locations = testApis(vehicleIds);
		for(int i=0; i<list.size(); i++){
			Map<String, Object> vehicle = list.get(i);
			Map<String, Object> loc = locations.get(i);
			double lon = (double) loc.get("lon");
			double lat = (double) loc.get("lat");
			double distance = LocationUtils.getDistance(orderLon, orderLat, lon, lat);
			vehicle.put("distance", distance);
			vehicle.putAll(loc);
		}
		list.sort(new Comparator<Map<String,Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				double distance1 = (double) o1.get("distance");
				double distance2 = (double) o2.get("distance");
				if(distance1 > distance2){
					return 1;
				} else if(distance1 < distance2){
					return -1;
				}
				return 0;
			}
		});
		
		int pageSize = (int) map.get("pageSize");
		int offset = (int) map.get("offset");
		int endIdx = offset+pageSize;
		if(endIdx >= list.size()){
			endIdx = list.size();
		}
		list = list.subList(offset, endIdx);
		return list;
	}
	
	@Override
	public int getSuitableVehiclesCount(Map<String, Object> map) {
		return assignmentDao.listReadyVehiclesCount(map);
	}
	private List<Map<String, Object>> testApis(String vehicleIds) {
		String[] vehicles = vehicleIds.split(",");
		List<Map<String, Object>> list = new ArrayList<>();
		for(int i=0; i<vehicles.length; i++){
			Map<String, Object> map = new HashMap<>();
			map.put("position", "测试地址" + i);
			map.put("vehicle_id", vehicles[i]);
			Random rnd = new Random();
			map.put("lon", 114.07 + (rnd.nextInt(999))/1000.0);
			map.put("lat", 22.62+ (rnd.nextInt(999))/1000.0);
			map.put("state", 1);
			map.put("speed", rnd.nextInt(100));
			list.add(map);
		}
 		return list;
	}

	@Override
	@Transactional
	@WriteDataSource
	public void assign(Map<String, Object> map) throws ServiceException {
		String orderNo = (String) map.get("orderNo");
		int userId = (int)map.get("userId");
		String deptDNA = (String) map.get("deptDNA");
		String operatorName = (String) map.get("userName");
		String ip =  String.valueOf(map.get("ip"));
		int vehicleId = (int) map.get("vehicleId");
		int driverId = (int) map.get("driverId");
		Map<String,Object> order = orderDao.getAnyOrder(map);
		int orderStatus = orderDao.getOrderStatusAndLock(orderNo);
		// 不是已发布状态
		if(orderStatus != OrderStatus.ACCEPTED){
			throw new ServiceException(ExceptionCode.ERROR_ORDER_STATUS_HAS_CHANGED, "订单状态已改变");
		}
		Map<String, Object> paramVcl = new HashMap<>();
		paramVcl.put("deptDNA", deptDNA);
		paramVcl.put("vehicleId", vehicleId);
		Map<String, Object> vehicle = vehicleDao.getVehicle(paramVcl);
		Map<String, Object> paramDrv = new HashMap<>();
		paramDrv.put("driverId", driverId);
		Driver driver = driverDao.getDriver(paramDrv);
		// 司机车辆关系已改变
		if(driver.getVehicleId() != vehicleId){
			throw new ServiceException(ExceptionCode.ERROR_DRIVER_VEHICLE_REL_HAS_CHANGED, "司机车辆关系已改变");
		}
		map.put("driver", driver);
		map.put("vehicle", vehicle);
		assignmentDao.recordOrderVehicleRel(map);

		OrderLifecycle orderLifecycle = new OrderLifecycle();
		orderLifecycle.setOrderNo(orderNo);
		orderLifecycle.setOperatorId(userId);
		orderLifecycle.setOperatorName(operatorName);
		orderLifecycle.setOperateTime(new Date());
		orderLifecycle.setOperation(8);
		orderLifecycle.setIp(ip);
		orderLifecycle.setOrderStatus(orderStatus);
		orderLifecycle.setPayStatus((int)order.get("payStatus"));
		String content = "{plateNo:'" + vehicle.get("plateNo") + "',driverName:'" + driver.getDriverName()
				+ "', driverPhone:" + driver.getPhoneNo() + "}";
		orderLifecycle.setContent(content);
		orderLifecycleDao.add(orderLifecycle);
	}

	
}
