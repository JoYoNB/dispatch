package com.chainway.dispatcherweb.web.controller.carrier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chainway.dispatchercore.annotation.TimeZone;
import com.chainway.dispatchercore.common.CommonUtils;
import com.chainway.dispatchercore.dto.Dept;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatchercore.excetion.ExceptionCode;
import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherservice.service.UserService;
import com.chainway.dispatcherservice.service.carrier.MonitorService;
import com.chainway.dispatcherweb.biz.service.LocalService;
import com.chainway.dispatcherweb.dto.JsonResult;

/**
 * 配送监控
 */
@Controller
@RequestMapping(value="/carrier/monitor")
public class MonitorController {
	
	@Reference(timeout=60000, check=false)
	private MonitorService monitorService;
	
	@Autowired
	private LocalService localService;
	
	@Reference(timeout=60000, check=false)
	private UserService userService;
	/**
	 * 获取车辆位置接口
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@TimeZone(type="both")
	@RequestMapping(value="listVehiclePositions.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult listVehiclePositions(HttpServletRequest request,HttpServletResponse response) {
		JsonResult result = new JsonResult();	
		try {
			
			Map<String, Object> paramMap = getQueryParamsMap(request);
			
			int pageSize = CommonUtils.getPageSize(request);
			int offset = CommonUtils.getOffset(request);
			paramMap.put("pageSize", pageSize);
			paramMap.put("offset", offset);
			
			List<Map<String,Object>> data = monitorService.listVehiclePositions(paramMap);
			
			result.setData(data);
			result.setCode(JsonResult.CODE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(JsonResult.CODE_FAIL);
			result.setMsg(e.getMessage());
		}
		return result;
	}
	
	
	/**
	 * 获取车辆数量
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@TimeZone(type="both")
	@RequestMapping(value="getVehicleCount.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult getVehicleCount(HttpServletRequest request,HttpServletResponse response) {
		JsonResult result = new JsonResult();	
		try {
			Map<String, Object> paramMap = getQueryParamsMap(request);
			
			Map<String,Object> data = monitorService.getVehicleCount(paramMap);
			// 返回结果
			result.setData(data);
			result.setCode(JsonResult.CODE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(JsonResult.CODE_FAIL);
			result.setMsg(e.getMessage());
		}
		return result;
	}


	private Map<String, Object> getQueryParamsMap(HttpServletRequest request) throws ServiceException {
		// 接收参数
		User user = localService.getUserInSession(request);
		int carrierDept = user.getMerchantDeptId();
		String content = request.getParameter("content");
		String fieldId = request.getParameter("fieldId");
		String queryDeptId = request.getParameter("queryDeptId");
		
		// 封装参数
		Map<String, Object> paramMap = new HashMap<>();
		if (fieldId != null && content != null && !"".equals(fieldId) && !"".equals(content)) {
			if("0".equals(fieldId)){ // 订单号
				paramMap.put("orderNo", content);
			}
			else if("1".equals(fieldId)){ // 车牌号
				paramMap.put("plateNo", content);
			}
			else if("2".equals(fieldId)){ // 驾驶员
				paramMap.put("driverName", content);
			}
		}
		
		if(StringUtils.isNotEmpty(queryDeptId)){
			Integer deptId=Integer.parseInt(queryDeptId);
			//根据部门Id和用户dna去查询数据，判断是否越权
			Dept d = userService.checkDeptDataAuth(deptId, user.getDeptDNA());
			if (d == null) {
				throw new ServiceException(ExceptionCode.ERROR_NO_DEPT_AUTH, "没有该部门的操作权限");
			}
			paramMap.put("deptDNA", d.getDna());
		}else{
			//没有传部门ID过来，则默认是用户本身部门
			paramMap.put("deptDNA", user.getDeptDNA());
		}
		paramMap.put("carrierDept", carrierDept);
		return paramMap;
	}
	
	/**
	 * 获取订单位置接口
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@TimeZone(type="both")
	@RequestMapping(value="listOrderPositions.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult listOrderPositions(HttpServletRequest request,HttpServletResponse response) {
		JsonResult result = new JsonResult();	
		try {
			// 接收参数
			User user = localService.getUserInSession(request);
			int carrierDept = user.getMerchantDeptId();
			Map<String, Object> paramMap = new HashMap<>();
			// 封装参数
			paramMap.put("carrierDept", carrierDept);
			List<Map<String,Object>> data = monitorService.listOrderPositions(paramMap);
			// 返回结果
			result.setData(data);
			result.setCode(JsonResult.CODE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(JsonResult.CODE_FAIL);
			result.setMsg(e.getMessage());
		}
		return result;
	}
	
	
	/**
	 * 获取订单位置接口
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@TimeZone(type="both")
	@RequestMapping(value="listSitePositions.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult listSitePositions(HttpServletRequest request,HttpServletResponse response) {
		JsonResult result = new JsonResult();	
		try {
			// 接收参数
			User user = localService.getUserInSession(request);
			int carrierDept = user.getMerchantDeptId();
			Map<String, Object> paramMap = new HashMap<>();
			// 封装参数
			paramMap.put("carrierDept", carrierDept);
			List<Map<String,Object>> data = monitorService.listSitePositions(paramMap);
			// 返回结果
			result.setData(data);
			result.setCode(JsonResult.CODE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(JsonResult.CODE_FAIL);
			result.setMsg(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 获取订单运输路线
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@TimeZone(type="both")
	@RequestMapping(value="getOrderTransportRoute.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult getOrderTransportRoute(HttpServletRequest request,HttpServletResponse response) {
		JsonResult result = new JsonResult();	
		try {
			// 接收参数
			User user = localService.getUserInSession(request);
			String deptDNA = user.getDeptDNA();
			String orderNo = request.getParameter("orderNo");
			Map<String, Object> paramMap = new HashMap<>();
			// 封装参数
			paramMap.put("deptDNA", deptDNA);
			paramMap.put("orderNo", orderNo);
			Map<String,Object> data = monitorService.getOrderTransportRoute(paramMap);
			// 返回结果
			result.setData(data);
			result.setCode(JsonResult.CODE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(JsonResult.CODE_FAIL);
			result.setMsg(e.getMessage());
		}
		return result;
	}
	
}
