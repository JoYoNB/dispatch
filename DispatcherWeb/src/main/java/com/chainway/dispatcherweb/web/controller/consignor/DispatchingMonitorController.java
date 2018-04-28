package com.chainway.dispatcherweb.web.controller.consignor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chainway.dispatchercore.annotation.TimeZone;
import com.chainway.dispatchercore.common.Constant;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatcherservice.service.ConsignorService;
import com.chainway.dispatcherweb.annotation.Log;
import com.chainway.dispatcherweb.annotation.ValidateFiled;
import com.chainway.dispatcherweb.annotation.ValidateGroup;
import com.chainway.dispatcherweb.biz.service.LocalService;
import com.chainway.dispatcherweb.dto.JsonResult;

@Controller
@RequestMapping("/consignor/dispatchingMonitor")
public class DispatchingMonitorController {
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Reference(timeout=60000, check=false)
	private ConsignorService consignorService;
	@Autowired
	private LocalService localService;

	@Log
	/*@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "pageNum"),
			@ValidateFiled(index = 0, notNull = true, filedName = "pageSize") })*/
	@TimeZone(type="return")
	@RequestMapping("/orderList.json")
	@ResponseBody
	public JsonResult orderList(HttpServletRequest request, JsonResult result) throws Exception {
		User user=localService.getUserInSession(request);
		Map<String, Object> map = new HashMap<>();
		//Integer offset = 0;
		//Integer pageNum = Integer.parseInt(request.getParameter("pageNum"));
		//Integer pageSize = Integer.parseInt(request.getParameter("pageSize"));
		//offset = (pageNum-1)*pageSize;
		//map.put("offset", offset);
		//map.put("pageSize", pageSize);
		map.put("startSiteName",request.getParameter("startSiteName"));
		map.put("middleOrEnd",request.getParameter("middleOrEnd"));
		map.put("driver",request.getParameter("driver"));
		map.put("orderNo",request.getParameter("orderNo"));
		map.put("plateNo",request.getParameter("plateNo"));
		map.put("deptDNA", user.getDeptDNA());
		map.put("userZone",Constant.timeZoneMap.get(user.getGmtZone()));
		logger.info("货主配送监控地图订单列表查询（orderList），入参："+map);
		result.setData(consignorService.mapOrderList(map));
		return result;
	}
	
	@Log
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "orderNo")})
	@RequestMapping("/orderDetail.json")
	@ResponseBody
	public JsonResult orderDetail(HttpServletRequest request, JsonResult result) throws Exception {
		User user=localService.getUserInSession(request);
		Map<String, Object> map = new HashMap<>();
		map.put("orderNo", request.getParameter("orderNo"));
		map.put("deptDNA", user.getDeptDNA());
		logger.info("货主订单详情查询（orderDetail），入参："+map);
		//result.setData(consignorService.cargoStatistics(map));
		return result;
	}
}
