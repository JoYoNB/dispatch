package com.chainway.dispatcherappweb.web.controller.carrier;

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
import com.chainway.dispatcherappweb.annotation.Log;
import com.chainway.dispatcherappweb.annotation.ValidateFiled;
import com.chainway.dispatcherappweb.annotation.ValidateGroup;
import com.chainway.dispatcherappweb.biz.service.LocalService;
import com.chainway.dispatchercore.annotation.TimeZone;
import com.chainway.dispatchercore.common.CommonUtils;
import com.chainway.dispatchercore.common.CusAccessObjectUtil;
import com.chainway.dispatchercore.common.OrderStatus;
import com.chainway.dispatchercore.dto.JsonResult;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatcherservice.service.carrier.AssignmentService;
import com.chainway.dispatcherservice.service.carrier.OrderMgmtService;

@Controller
@RequestMapping("/carrier/order")
public class CarrierOrderController {

	@Reference(timeout = 60000, check=false)
	AssignmentService assignmentService;

	@Reference(timeout = 60000, check=false)
	OrderMgmtService orderMgmtService;

	@Autowired
	LocalService localService;

	@RequestMapping(value = "/recommend.json", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	@Log
	public JsonResult listRecommendedOrder(HttpServletRequest request, JsonResult result) throws Exception {
		User user = localService.getUserInSession(request);
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("carrierDept", user.getMerchantDeptId());
		List<Map<String, Object>> data = assignmentService.listPublishedOrders(paramMap);
		result.setCode(JsonResult.CODE_SUCCESS);
		result.setData(data);
		return result;
	}

	@RequestMapping(value = "/filter.json", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	@Log
	public JsonResult listFilteredOrder(HttpServletRequest request, JsonResult result) throws Exception {

		Map<String, Object> params = getQueryParams(request);
		int pageSize = CommonUtils.getPageSize(request);
		int offset = CommonUtils.getOffset(request);
		params.put("pageSize", pageSize);
		params.put("offset", offset);

		List<Map<String, Object>> list = orderMgmtService.getOrderList(params);
		int total = orderMgmtService.getOrderListCount(params);

		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("list", list);
		ret.put("total", total);
		result.setData(ret);
		result.setCode(JsonResult.CODE_SUCCESS);
		return result;

	}

	@RequestMapping(value = "/accept.json", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	@Log
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "orderNo", maxLen = 50) })
	public JsonResult acceptOrder(HttpServletRequest request, JsonResult result) throws Exception {
		User user = localService.getUserInSession(request);
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("deptDNA", user.getDeptDNA());
		paramMap.put("orderNo", request.getParameter("orderNo"));
		paramMap.put("carrierDept", user.getMerchantDeptId());
		paramMap.put("userId", user.getId());
		paramMap.put("userName", user.getName());
		paramMap.put("ip", CusAccessObjectUtil.getIpAddress(request));
		assignmentService.accept(paramMap);
		result.setCode(JsonResult.CODE_SUCCESS);
		return result;
	}

	@RequestMapping(value = "/cancel.json", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	@Log
	public JsonResult cancelOrder(JsonResult result) throws Exception {

		result.setCode(JsonResult.CODE_SUCCESS);
		return result;
	}

	@RequestMapping(value = "/distribute.json", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	@Log
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "orderNo", maxLen = 50),
			@ValidateFiled(index = 0, notNull = true, filedName = "vehicleId", dataType = "int", maxLen = 10),
			@ValidateFiled(index = 0, notNull = true, filedName = "driverId", dataType = "int", maxLen = 10) })
	public JsonResult distributeOrder(HttpServletRequest request, JsonResult result) throws Exception {
		User user = localService.getUserInSession(request);
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("deptDNA", user.getDeptDNA());
		paramMap.put("userId", user.getId());
		paramMap.put("userName", user.getName());
		paramMap.put("orderNo", request.getParameter("orderNo"));
		paramMap.put("vehicleId", Integer.valueOf(request.getParameter("vehicleId")));
		paramMap.put("driverId", Integer.valueOf(request.getParameter("driverId")));
		paramMap.put("ip", CusAccessObjectUtil.getIpAddress(request));
		assignmentService.assign(paramMap);
		result.setCode(JsonResult.CODE_SUCCESS);
		return result;
	}

	@RequestMapping(value = "/detail.json", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	@Log
	public JsonResult getOrder(JsonResult result) throws Exception {

		result.setCode(JsonResult.CODE_SUCCESS);
		return result;
	}

	@RequestMapping(value = "/listVehicle.json", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	@Log
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "orderNo", maxLen = 50) })
	public JsonResult listVehicle(HttpServletRequest request, JsonResult result) throws Exception {
		User user = localService.getUserInSession(request);
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("deptDNA", user.getDeptDNA());
		paramMap.put("orderNo", request.getParameter("orderNo"));
		paramMap.put("carryTypeId", request.getParameter("carryTypeId"));
		paramMap.put("vehicleType", request.getParameter("vehicleType"));
		paramMap.put("content", request.getParameter("content"));
		paramMap.put("pageSize", CommonUtils.getPageSize(request));
		paramMap.put("offset", CommonUtils.getOffset(request));
		List<Map<String, Object>> data = assignmentService.listSuitableVehicles(paramMap);
		result.setData(data);
		result.setCode(JsonResult.CODE_SUCCESS);
		return result;
	}
	/**
	 * 查询已接单的订单
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getOrderList.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	@TimeZone(type="return")
	public JsonResult getOrderList(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		Map<String, Object> params = getQueryParams(request);
		// 页码
		int pageSize = CommonUtils.getPageSize(request);
		int offset = CommonUtils.getOffset(request);
		params.put("pageSize", pageSize);
		params.put("offset", offset);
		params.put("orderStatus", OrderStatus.ACCEPTED);
		
		List<Map<String, Object>> list = orderMgmtService.getOrderList(params);
		int total = orderMgmtService.getOrderListCount(params);
		
		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("list", list);
		ret.put("total", total);
		result.setData(ret);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	private Map<String, Object> getQueryParams(HttpServletRequest request) {
		User user = localService.getUserInSession(request);
		Map<String, Object> params = new HashMap<>();
		String orderNo = request.getParameter("orderNo");
		String orderStatus = request.getParameter("orderStatus");
		String goodsType = request.getParameter("goodsType");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String startSiteName = request.getParameter("startSiteName");
		String endSiteName = request.getParameter("endSiteName");
		String plateNo = request.getParameter("plateNo");
		String driverName = request.getParameter("driverName");
		
		int carrierDept = user.getMerchantDeptId();
		params.put("carrierDept", carrierDept);
		params.put("orderNo", StringUtils.stripToNull(orderNo));
		params.put("orderStatus", StringUtils.stripToNull(orderStatus));
		params.put("goodsType", StringUtils.stripToNull(goodsType));
		params.put("startTime", StringUtils.stripToNull(startTime));
		params.put("endTime", StringUtils.stripToNull(endTime));
		params.put("startSiteName", StringUtils.stripToNull(startSiteName));
		params.put("endSiteName", StringUtils.stripToNull(endSiteName));
		params.put("plateNo", StringUtils.stripToNull(plateNo));
		params.put("driverName", StringUtils.stripToNull(driverName));
		return params;
	}

}
