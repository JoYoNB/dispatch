package com.chainway.dispatcherdriverservice.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.chainway.dispatchercore.common.IVCApiUtils;
import com.chainway.dispatchercore.common.OrderStatus;
import com.chainway.dispatchercore.common.TimeUtil;
import com.chainway.dispatcherdriverservice.biz.dao.OrderDao;
import com.chainway.dispatcherdriverservice.service.MonitorService;


@Component("carrierMonitorService")  
@Service
public class MonitorServiceImpl implements MonitorService {
	
	@Autowired
	private OrderDao orderDao;

	@Override
	public Map<String, Object> getOrderTransportRoute(Map<String, Object> param) throws Exception {
		//接收参数
		Map<String, Object> orderInfo = orderDao.getOrderInfo(param);
		Integer orderStatus=(Integer) orderInfo.get("orderStatus");
		int pageNum = param.get("pageNum") == null ? 1: (int)param.get("pageNum");
		if (orderStatus < OrderStatus.IN_TRANSIT) {
			return null;//没有轨迹
		} 
		//其他平台车辆id
		String vehicleOtherId = (String) orderInfo.get("vehicleOtherId");
		Date now = new Date();
		Date startTime = (Date) orderInfo.get("pickupTime")==null?now:(Date) orderInfo.get("pickupTime");
		Date endTime = (Date) orderInfo.get("finishTime")==null?now:(Date) orderInfo.get("finishTime");
		String format="yyyy-MM-dd HH:mm:ss";
	    String zeroZONE="utc-0000003";
		SimpleDateFormat df=new SimpleDateFormat(format);
		//转成用户时区时间
		String _startTime=TimeUtil.changeZoneTime(df.format(startTime), format, zeroZONE, format, "utc+0800000");
		String _endTime=TimeUtil.changeZoneTime(df.format(endTime), format, zeroZONE, format, "utc+0800000");
		// 获取车辆轨迹
		IVCApiUtils apiUtils = IVCApiUtils.getInstance();
		Map<String, Object> tracks = apiUtils.getVehicleTrajectory(vehicleOtherId, _startTime,_endTime,pageNum);
		return tracks;
	}

}
