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
import com.chainway.dispatchercore.dto.OrderLifecycle;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatcherservice.service.carrier.OrderMgmtService;
import com.chainway.dispatcherweb.annotation.ValidateGroup;
import com.chainway.dispatcherweb.biz.service.LocalService;
import com.chainway.dispatcherweb.dto.JsonResult;

/**
 * 订单管理
 * @author Administrator
 */
@Controller
@RequestMapping(value="/carrier/order")
public class OrderMgmtController {
	@Autowired
	private LocalService localService;
	
	@Reference(timeout=60000, check=false)
	private OrderMgmtService  orderMgmtService;
	
	@RequestMapping(value="/getOrderList.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	@TimeZone(type="both")
	public JsonResult getOrderList(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		Map<String, Object> params = getQueryParams(request);
		// 页码
		int pageSize = CommonUtils.getPageSize(request);
		int offset = CommonUtils.getOffset(request);
		params.put("pageSize", pageSize);
		params.put("offset", offset);
		
		List<Map<String, Object>> list = orderMgmtService.getOrderList(params);
		int total = orderMgmtService.getOrderListCount(params);
		
		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("list", list);
		ret.put("total", total);
		result.setData(ret);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@RequestMapping(value="/export.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult export(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		Map<String, Object> params = getQueryParams(request);
		String url = orderMgmtService.export(params);
		result.setData(url);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@RequestMapping(value="/getOrder.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	@TimeZone(type="both")
	public JsonResult getOrder(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		Map<String, Object> params = getQueryParams(request);
		
		Map<String, Object> order = orderMgmtService.getOrder(params);
		List<Map<String, Object>> sites = orderMgmtService.listOrderSites(params);
		List<OrderLifecycle> circlys = orderMgmtService.listOrderLifeCirclys(params);
		Map<String, Object> ret = new HashMap<>();
		ret.put("order", order);
		ret.put("sites", sites);
		ret.put("circlys", circlys);
		result.setData(ret);
		
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	private Map<String, Object> getQueryParams(HttpServletRequest request) {
		User user = localService.getUserInSession(request);
		String orderNo = request.getParameter("orderNo");
		String orderStatus = request.getParameter("orderStatus");
		String goodsType = request.getParameter("goodsType");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String startSiteName = request.getParameter("startSiteName");
		String endSiteName = request.getParameter("endSiteName");
		String plateNo = request.getParameter("plateNo");
		String driverName = request.getParameter("driverName");
		String deptDNA = user.getDeptDNA();
		int carrierDept = user.getMerchantDeptId();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("deptDNA", deptDNA);
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
