package com.chainway.dispatcherservice.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.chainway.cacheService.biz.service.CacheService;
import com.chainway.dispatchercore.common.IVCApiUtils;
import com.chainway.dispatchercore.common.MD5Util;
import com.chainway.dispatchercore.common.OrderStatus;
import com.chainway.dispatchercore.dto.FileTemplate;
import com.chainway.dispatchercore.dto.OrderLifecycle;
import com.chainway.dispatchercore.excetion.ExceptionCode;
import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherservice.annotation.WriteDataSource;
import com.chainway.dispatcherservice.biz.dao.ChargeRuleDao;
import com.chainway.dispatcherservice.biz.dao.OrderDao;
import com.chainway.dispatcherservice.biz.dao.OrderLifecycleDao;
import com.chainway.dispatcherservice.biz.dao.SiteDao;
import com.chainway.dispatcherservice.biz.service.ConsignorLocalService;
import com.chainway.dispatcherservice.dto.ChargeRule;
import com.chainway.dispatcherservice.dto.OrderParam;
import com.chainway.dispatcherservice.dto.SiteParam;
import com.chainway.dispatcherservice.service.ConsignorService;
import com.chainway.fileservice.service.FileService;


@Component
@Service
@Transactional
public class ConsignorServiceImpl  implements ConsignorService{
	@Autowired
	OrderDao orderDao;
	@Autowired
	SiteDao siteDao;  
	@Autowired
	ConsignorLocalService localService;
	@Autowired
	ChargeRuleDao chargeRuleDao;
	@Autowired
	OrderLifecycleDao logDao;
	
	@Reference(timeout=60000, check=false)
	FileService fileService;
	@Reference(timeout=60000, check=false)
	CacheService cacheService;
	
	@Override
	public List<Map<String, Object>> orderStatistics(Map<String, Object> param) {
		return orderDao.orderStatistics(param);
	}

	@Override
	public List<Map<String, Object>> cargoStatistics(Map<String, Object> param) {
		return orderDao.cargoStatistics(param);
	}

	@Override
	public List<Map<String, Object>> orderRank(Map<String, Object> param) {
		return orderDao.orderRank(param);
	}

	@Override
	public List<Map<String, Object>> deliveryCargoRank(Map<String, Object> param) {
		return orderDao.deliveryCargoRank(param);
	}

	@Override
	public Map<String, Object> totalStatistics(Map<String, Object> param) {
		return orderDao.totalStatistics(param);
	}

	@Override
	public List<Map<String, Object>> mapOrderList(Map<String, Object> param) {
		List<Map<String, Object>> orderList=orderDao.mapOrderList(param);
		String vehicleIds = "";
		if(orderList!=null&&orderList.size()>0){
			Map<String, Object> queryMap = new HashMap<>();
			queryMap.put("userTimeZone", param.get("userZone"));
			for(int i=0;i<orderList.size();i++){//查找订单日志
				String orderNo = (String)orderList.get(i).get("orderNo");
				if(orderNo==null){
					continue;
				}
				queryMap.put("orderNo", orderNo);
				List<OrderLifecycle> logs=logDao.query(queryMap);
				orderList.get(i).put("logs", logs);
			}
			Map<String, Map<String, Object>> tempOrder = new HashMap<>();//临时订单车辆对应表
			Iterator<Map<String, Object>> iterator = orderList.iterator();
			//收集vehicleId去2.0获取最新位置
			while (iterator.hasNext()) {
				Map<String, Object> order=iterator.next();
				Integer vId = order.get("vehicleId")==null?-1:Integer.parseInt(order.get("vehicleId").toString());
				if(vId>0){//防止订单没有绑定车辆时影响查询
					vehicleIds += vId+",";
					tempOrder.put(order.get("vehicleId").toString(), order);//添加车辆订单对应表，方便合并车辆最新位置信息
					orderList.remove(order);//移除正常数据，其他数据仍然保留在原表中，便于重复利用orderList对象
				}
			}
			if(!"".equals(vehicleIds)){
				vehicleIds = vehicleIds.substring(0,vehicleIds.lastIndexOf(","));
				//请求ivci获取车辆位置信息
				IVCApiUtils utils = IVCApiUtils.getInstance();
				List<Map<String, Object>> vehicleInfos=utils.getVehicleLastLocation(vehicleIds);
				if (vehicleInfos!=null&&vehicleInfos.size()>0) {
						for(Map<String, Object> vehicle:vehicleInfos){
							Map<String, Object> map = tempOrder.get(vehicle.get("vehicle_id"));
							tempOrder.remove(map); //移除临时数据，便于判断返回车辆数是否符合查询数
							map.put("vehicleInfo", vehicle);//车辆位置信息
							orderList.add(map);
						}
					if(!tempOrder.isEmpty()){//临时表还有数据，说明查询实际返回和查询不符，部分订单没有查到车辆信息，订单数据仍然要保留
						for(String key:tempOrder.keySet()){
							orderList.add(tempOrder.get(key));
						}
					}
				}
			}				
			return orderList;
		}
		return null;
	}

	@Override
	public Map<String, Object> getOrderDetails(Map<String, Object> param) {
		Map<String, Object> order = orderDao.getOrderDetails(param);
		if(order!=null){
			//查询站点详情
			List<Map<String, Object>> sites = orderDao.getOrderSiteDetails(param);
			order.put("sites", sites);
			if(param.get("ifLog")!=null&&param.get("ifLog").toString().equals("no")){	
			}else {
				List<OrderLifecycle> logs=logDao.query(param);
				order.put("list",logs);
			}
			int orderStatus = Integer.parseInt(order.get("orderStatus").toString());
			if(orderStatus == 70 || orderStatus == 80 ){ //待提货和配送中才查询车辆位置
				Integer vId = order.get("vehicleId")==null?-1:Integer.parseInt(order.get("vehicleId").toString());
				if(vId>0){//第三方平台车辆id不为空才查车辆位置
					String vehicleId = vId.toString();
					//请求ivci获取车辆位置信息
					IVCApiUtils utils = IVCApiUtils.getInstance();
					List<Map<String, Object>> vehicleInfos=utils.getVehicleLastLocation(vehicleId);
					if(vehicleInfos!=null && vehicleInfos.size()>0){
						order.put("vehicle", vehicleInfos.get(0));//车辆信息
					}
				}
			}
			return order;
		}
		return null;
	}

	@Override
	public void createOrder(OrderParam param) throws ServiceException {	
		localService.createOrder(param);
	}
	
	@Override
	public void modifyOrder(OrderParam param) throws ServiceException {
		localService.modifyOrder(param);
	}
	
	@Override
	public void deleteOrder(OrderParam param) throws ServiceException {	
		localService.deleteOrder(param);
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void cancelOrder(OrderParam param)throws Exception{
		Map<String,Integer>orderStatusMap=orderDao.getOrderStatus(param.getOrderNo());
		Integer orderStatus=orderStatusMap.get("order_status");
		Integer payStatus=orderStatusMap.get("pay_status");
		
		//订单状态（10待发布、20已发布、30已失效、40已接单、50已分配、60已取消、70待提货、80配送中、90已结束）
		//判断订单状态
		if(orderStatus.intValue()<OrderStatus.WAIT_FOR_PICK_UP){//待提货状态及前面的订单才能取消
			throw new ServiceException(ExceptionCode.ERROR_CANCEL_ORDER_STATUS_LIMIT,"取消订单状态受限制");
		}
		//改变订单状态
		OrderParam updateOrder=new OrderParam();
		updateOrder.setOrderNo(param.getOrderNo());
		updateOrder.setOrderStatus(OrderStatus.CANCELED);
		updateOrder.setOrderOperation("cancel");
		orderDao.modifyOrder(updateOrder);
		//添加订单日志
		OrderLifecycle orderLifecycle=new OrderLifecycle();
		orderLifecycle.setIp(param.getIp());
		//操作：0失效，1创建，2编辑，3删除，4付款，5申请退款，6退款，7发布，8接单，9取消，10分配，11司机确认，12提货，13签收，14结算，15收款
		orderLifecycle.setOperation(9);
		orderLifecycle.setOperatorId(param.getCreaterId());
		orderLifecycle.setOrderNo(param.getOrderNo());
		orderLifecycle.setOrderStatus(OrderStatus.CANCELED);//订单状态（10待发布、20已发布、30已失效、40已接单、50已分配、60已取消、70待提货、80配送中、90已结束）
		orderLifecycle.setPayStatus(payStatus);
		orderLifecycle.setContent("{}");
		logDao.add(orderLifecycle);
		
		Map<String,Object>clearMap=new HashMap<String,Object>();
		clearMap.put("orderNo", param.getOrderNo());
		//清空承运商于订单的关系
		orderDao.deleteOrderCarrierRel(clearMap);
		//清空车辆与订单的关系
		orderDao.deleteOrderVehicleRel(clearMap);
	}
	
	@Override
	public Map<String, Object> orderList(Map<String, Object> param) {
		List<Map<String, Object>> orderList=orderDao.orderList(param);
		Integer total = orderDao.totalOrder(param);
		Map<String, Object> map = new HashMap<>();
		map.put("list", collatOrder(orderList));
		map.put("total", total);
		return map;
	}
	
	@Override
	public Map<String, Object> orderListForApp(Map<String, Object> param) {
		Integer pageNum=(Integer) param.get("pageNum");
		pageNum=pageNum==null?1:pageNum;
		Integer pageSize=(Integer) param.get("pageSize");
		pageSize=pageSize==null?10:pageSize;
		param.put("pageSize", pageSize+1);//多查询一条
		int offset=(pageNum-1)*pageSize;
		param.put("offset", offset);
		
		List<Map<String, Object>>orderList=orderDao.orderList(param);
		Map<String, Object>map=new HashMap<>();
		map.put("list", collatOrder(orderList));
		if(orderList!=null&&!orderList.isEmpty()&&orderList.size()>pageSize){
			map.put("hasMore", true);
			orderList.remove(orderList.size()-1);//移除最后一条
		}else{
			map.put("hasMore", false);
		}
		return map;
	}
	
	@Override
	public Map<String, Object> exportOrder(Map<String, Object> param) throws Exception {
		Map<String, Object> ret = new HashMap<>();
		//生成key 用户+模块+参数
		String key = param.get("userId")+"_consignor_order_export_"+MD5Util.getMD5String(param.toString());
		Boolean loadCache = (Boolean)param.get("loadCache");
		if(loadCache==null||loadCache){//从缓存获取下载地址
			String urlCache = cacheService.getStringData(key);
			if(urlCache!=null){
				ret.put("url", urlCache);
				return ret;
			}
		}
		//List<Map<String, Object>> orderList=orderDao.orderList(param);
		List<Object> list = new ArrayList<>();
		List<Map<String, Object>> orderList=collatOrder(orderDao.orderList(param));
		if(orderList!=null&&orderList.size()>0){
			for(Map<String, Object> map:orderList){
				list.add(map);
			}
		}
		FileTemplate tpl = new FileTemplate();
		tpl.setCode("consignor_order_export");
		String token  = "12345678";
		Map<String, Object> map = new HashMap<>();
		map.put("token", token);
		map.put("timeZone", param.get("timeZone"));
		String url = fileService.export(tpl, list, map);
		if(loadCache==null||loadCache){
			cacheService.setStringData(key, url, 7*24*60*60); //设置过期时间7天
		}
		ret.put("url", url);
		return ret;
	}
	
	//整理orderlist
	private List<Map<String, Object>> collatOrder(List<Map<String, Object>> orderList){
		if(orderList!=null&&orderList.size()>0){
			for(int i=0;i<orderList.size();i++){
				Map<String, Object> map = orderList.get(i);
				String startName="--";
				String endName = "--";
				if(map.get("startSiteName")!=null){
					startName = map.get("startSiteName")+"";
					if(map.get("startSiteAddress")!=null){
						startName+="("+map.get("startSiteAddress")+")";
					}
				}
				if(map.get("endSiteName")!=null){
					endName = map.get("endSiteName")+"";
					if(map.get("endSiteAddress")!=null){
						endName+="("+map.get("endSiteAddress")+")";
					}
				}
				orderList.get(i).put("startName", startName);
				orderList.get(i).put("endName", endName);
				
				String freightVolume = "--";
				Double weight = map.get("weight")==null?0:Double.parseDouble(map.get("weight").toString());
				Double volume = map.get("volume")==null?0:Double.parseDouble(map.get("volume").toString());
				Integer packageNum = map.get("packageNum")==null?0:Integer.parseInt(map.get("packageNum").toString());
				if(weight>0){
					freightVolume = weight+"吨";
				}else if(volume>0){
					freightVolume = volume+"方";
				}else if (packageNum>0) {
					freightVolume = packageNum+"件";
				}
				String orderStatus = "--";
				Integer status = Integer.parseInt(map.get("orderStatus").toString());
				//订单状态（10待发布、20已发布、30已失效、40已接单、50已分配、60已取消、70待提货、80配送中、90已结束）
				switch (status) {
				case 10:orderStatus = "待发布";break;
				case 20:orderStatus = "已发布";break;
				case 30:orderStatus = "已失效";break;
				case 40:orderStatus = "已接单";break;
				case 50:orderStatus = "已分配";break;
				case 60:orderStatus = "已取消";break;
				case 70:orderStatus = "待提货";break;
				case 80:orderStatus = "配送中";break;
				case 90:orderStatus = "已结束";break;
				default:
					break;
				}
				
				orderList.get(i).put("statusName", orderStatus);
				orderList.get(i).put("freightVolume",freightVolume);
				orderList.get(i).remove("startSiteName");
				orderList.get(i).remove("startSiteAddress");
				orderList.get(i).remove("endSiteName");
				orderList.get(i).remove("endSiteAddress");
				orderList.get(i).remove("weight");
				orderList.get(i).remove("volume");
				orderList.get(i).remove("packageNum");
			}
		}
		return orderList;
	}

	
	@Override
	@WriteDataSource
	public Map<String, Object> createSite(SiteParam site) throws ServiceException {
		if(site.getLinkId()==null&&site.getLinkMan()!=null&&site.getLinkPhone()!=null){//新增联系人
			siteDao.createLinkMan(site);
		}else if (site.getLinkId()==null&(site.getLinkMan()==null||site.getLinkPhone()==null)) {
			throw new ServiceException(ExceptionCode.ERROR_PARAM_INCOMPLETE,"联系人信息不全");
		}
		Map<String, Object> map = new HashMap<>();
		map.put("province", site.getProvince());
		map.put("city", site.getCity());
		map.put("district", site.getDistrict());
		Map<String, String> pcd=orderDao.getPCDIdByName(map);
		if(pcd!=null&&pcd.get("pid")!=null&&pcd.get("cid")!=null&&pcd.get("did")!=null){
			System.out.println("================>"+pcd);
			String provinceId = pcd.get("pid")==null?null:pcd.get("pid").toString().replace(".0", "");
			site.setProvinceId(provinceId);
			site.setCityId(pcd.get("cid")==null?null:pcd.get("cid").toString().replace(".0", ""));
			site.setDistrictId(pcd.get("did")==null?null:pcd.get("did").toString().replace(".0", ""));
			siteDao.createSite(site);
			Map<String, Object> rMap = new HashMap<>();
			rMap.put("id", site.getSiteId());
			rMap.put("districtId", site.getDistrictId());
			return rMap;
		}else{
			throw new ServiceException(ExceptionCode.ERROR_SITE_NO_PCD,"省市区数据异常");
		}
	}

	@Override
	@WriteDataSource
	public void deleteSite(Integer siteId) throws ServiceException{
		//删除前先判断是否有绑定的订单
		Integer count = siteDao.getOrderNumBySiteId(siteId);
		if(count>0){
			throw new ServiceException(ExceptionCode.ERROR_SITE_BIND_ORDER,"站点已经被订单使用，不能删除",siteId);
		}
		//删除站点信息-硬删除
		siteDao.deleteSiteById(siteId);
	}
	

	@Override
	@WriteDataSource
	public void modifySite(SiteParam site) throws ServiceException {
		if(site.getLinkId()==null&&site.getLinkMan()!=null&&site.getLinkPhone()!=null){//新增联系人
			siteDao.createLinkMan(site);
		}
		if(site.getCoordinate()!=null){//修改站点坐标时
			Map<String, Object> map = new HashMap<>();
			map.put("province", site.getProvince());
			map.put("city", site.getCity());
			map.put("district", site.getDistrict());
			Map<String, String> pcd=orderDao.getPCDIdByName(map);
			if(pcd!=null&&pcd.get("pid")!=null&&pcd.get("cid")!=null&&pcd.get("did")!=null){
				site.setProvinceId(pcd.get("pid"));
				site.setCityId(pcd.get("cid"));
				site.setDistrictId(pcd.get("did"));
				siteDao.modifySite(site);
			}else{
				throw new ServiceException(ExceptionCode.ERROR_SITE_NO_PCD,"省市区数据异常");
			}
		}else {//不修改站点坐标时，直接更新
			siteDao.modifySite(site);
		}
		
	}

	@Override
	public Map<String, Object> getSiteById(Map<String, Object> param) {
		return siteDao.getSiteById(param);
	}

	@Override
	public Map<String, Object> getSiteList(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<>();
		map.put("list", siteDao.getSiteList(param));
		if(param.get("limit")==null) {//app查列表时不需要总页数，根据app查询特有的limit参数来判断
			map.put("total", siteDao.totalSiite(param));
		}
		return map;
	}

	@Override
	public List<Map<String, Object>> getLinkMan(Map<String, Object> param) {
		return siteDao.getLinkMan(param);
	}
	
	@Override
	public List<Map<String, Object>> getSiteForSelect(Map<String, Object> param) {
		return siteDao.getSiteForSelect(param);
	}
	
	@Override
	public Double calculatePrice(Map<String, Object> param) {
		List<ChargeRule> rules= chargeRuleDao.getList(param);
		if(rules!=null&&rules.size()>=1){
			ChargeRule rule = rules.get(0);
			Double distance = Double.parseDouble(param.get("distance").toString());
			if(distance<=rule.getStartingMileage()){
				return rule.getStartingPrice();
			}else {
				return rule.getStartingPrice()+(distance-rule.getStartingMileage())*rule.getPrice();
			}
		}
		return null;
	}
}
