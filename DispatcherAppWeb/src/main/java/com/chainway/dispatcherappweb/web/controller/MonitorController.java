package com.chainway.dispatcherappweb.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chainway.dispatcherappweb.annotation.Log;
import com.chainway.dispatcherappweb.biz.service.LocalService;
import com.chainway.dispatchercore.annotation.TimeZone;
import com.chainway.dispatchercore.dto.JsonResult;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatcherservice.service.VehicleService;
import com.chainway.dispatcherservice.service.carrier.MonitorService;


/**
 * @Author: chainway
 *
 * @Date: 2018年3月23日
 * @Description:承运商配送监控
 */
@Controller
@RequestMapping(value="/dispatcherMonitor")
public class MonitorController {
	
	@Autowired
	private LocalService localService;
	
	@Reference(timeout=60000, check=false)
	private MonitorService monitorService;
	
	@Reference(timeout=60000, check=false)
	private VehicleService vehicleService;
	
	/**
	 * 地图展示车辆信息
	 * @param request
	 * @return
	 */
	@Log
	@ResponseBody
	@TimeZone(type="return")
	@RequestMapping(value="/listVehiclePositions.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult getVehiclesLocation(HttpServletRequest request) throws Exception {
		JsonResult result = new JsonResult();	
		// 接收参数
		String loadRate = request.getParameter("loadRate");
		User user = localService.getUserInSession(request);
		String deptDNA = user.getDeptDNA();
		int carrierDept = user.getMerchantDeptId();
		Map<String, Object> paramMap = new HashMap<>();
		// 封装参数
		paramMap.put("deptDNA", deptDNA);
		paramMap.put("loadRate", loadRate);
		paramMap.put("carrierDept", carrierDept);
		// 返回结果
		List<Map<String,Object>> data = monitorService.listVehiclePositions(paramMap);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("list", data);
		result.setData(resultMap);
		result.setCode(JsonResult.CODE_SUCCESS);
		return result;
	}
	
	/**
	 * 获取订单起点位置信息
	 * @param request
	 * @param response
	 * @return
	 */
	@Log
	@ResponseBody
	@TimeZone(type="return")
	@RequestMapping(value="/listOrderPositions.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult listOrderPositions(HttpServletRequest request,HttpServletResponse response) throws Exception {
		JsonResult result = new JsonResult();	
		// 接收参数
		User user = localService.getUserInSession(request);
		Integer carrierDept = user.getMerchantDeptId();
		Map<String, Object> paramMap = new HashMap<>();
		// 封装参数
		paramMap.put("carrierDept", carrierDept);
		List<Map<String,Object>> data = monitorService.listOrderPositions(paramMap);
		Map<String, Object> resultMap = new HashMap<>();
		// 返回结果
		resultMap.put("list", data);
		result.setData(resultMap);
		result.setCode(JsonResult.CODE_SUCCESS);
		return result;
	}
}
