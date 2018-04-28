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
import com.chainway.dispatchercore.common.CommonUtils;
import com.chainway.dispatchercore.common.CusAccessObjectUtil;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherservice.service.carrier.AssignmentService;
import com.chainway.dispatcherweb.annotation.Log;
import com.chainway.dispatcherweb.annotation.ValidateFiled;
import com.chainway.dispatcherweb.annotation.ValidateGroup;
import com.chainway.dispatcherweb.biz.service.LocalService;
import com.chainway.dispatcherweb.dto.JsonResult;
/**
 * 承运商接单、分配订单
 * @author Administrator
 */
@Controller
@RequestMapping(value="/carrier/assignment")
public class AssignmentController {
	
	@Autowired
	private LocalService localService;
	
	@Reference(timeout=60000, check=false)
	private AssignmentService assignmentService;
	
	/**
	 * 接单
	 * @param request
	 * @param response
	 * @return
	 */
	@Log
	@ResponseBody
	@RequestMapping(value="accept.json",method= {RequestMethod.POST,RequestMethod.GET})
	@ValidateGroup(fileds= {@ValidateFiled(index=0,notNull=true,filedName="orderNo",maxLen=50)})
	public JsonResult accept(HttpServletRequest request,HttpServletResponse response) {
		JsonResult result = new JsonResult();	
		try {
			// 接收参数
			User user = localService.getUserInSession(request);
			String deptDNA = user.getDeptDNA();
			String orderNo = request.getParameter("orderNo");
			int carrierDept = user.getMerchantDeptId();
			int userId = user.getId();
			String userName = user.getName();
			String ip = CusAccessObjectUtil.getIpAddress(request);
			Map<String, Object> paramMap = new HashMap<>();
			// 封装参数
			paramMap.put("deptDNA", deptDNA);
			paramMap.put("orderNo", orderNo);
			paramMap.put("carrierDept", carrierDept);
			paramMap.put("userId", userId);
			paramMap.put("userName", userName);
			paramMap.put("ip", ip);
			assignmentService.accept(paramMap);
			result.setCode(JsonResult.CODE_SUCCESS);
		} catch (ServiceException e) {
			e.printStackTrace();
			result.setCode(e.getCode());
			result.setMsg(e.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(JsonResult.CODE_FAIL);
			result.setMsg(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 获取车辆列表
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="listSuitableVehicles.json",method= {RequestMethod.POST,RequestMethod.GET})
	@ValidateGroup(fileds= {@ValidateFiled(index=0,notNull=true,filedName="orderNo",maxLen=50)})
	public JsonResult listSuitableVehicles(HttpServletRequest request,HttpServletResponse response) {
		JsonResult result = new JsonResult();	
		try {
			// 接收参数
			User user = localService.getUserInSession(request);
			String deptDNA = user.getDeptDNA();
			String orderNo = request.getParameter("orderNo");
			String carryTypeId = request.getParameter("carryTypeId");
			String vehicleType = request.getParameter("vehicleType");
			String content = request.getParameter("content");
			// 封装参数
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("deptDNA", deptDNA);
			paramMap.put("orderNo", StringUtils.stripToNull(orderNo));
			paramMap.put("carryTypeId", StringUtils.stripToNull(carryTypeId));
			paramMap.put("vehicleType", StringUtils.stripToNull(vehicleType));
			paramMap.put("content", StringUtils.stripToNull(content));
			// 页码
			int pageSize = CommonUtils.getPageSize(request);
			int offset = CommonUtils.getOffset(request);
			paramMap.put("pageSize", pageSize);
			paramMap.put("offset", offset);
			// 返回结果
			List<Map<String,Object>> list = assignmentService.listSuitableVehicles(paramMap);
			int total = assignmentService.getSuitableVehiclesCount(paramMap);
			Map<String, Object> ret = new HashMap<>();
			ret.put("list", list);
			ret.put("total", total);
			result.setData(ret);
			result.setCode(JsonResult.CODE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(JsonResult.CODE_FAIL);
			result.setMsg(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 派单
	 * @param request
	 * @param response
	 * @return
	 */
	@Log
	@ResponseBody
	@RequestMapping(value="assign.json",method= {RequestMethod.POST,RequestMethod.GET})
	@ValidateGroup(fileds= {@ValidateFiled(index=0,notNull=true,filedName="orderNo",maxLen=50),
			@ValidateFiled(index=0,notNull=true,filedName="vehicleId",dataType="int", maxLen=10),
			@ValidateFiled(index=0,notNull=true,filedName="driverId",dataType="int", maxLen=10)})
	public JsonResult assgin(HttpServletRequest request,HttpServletResponse response) {
		JsonResult result = new JsonResult();	
		try {
			String orderNo = request.getParameter("orderNo");
			int vehicleId = Integer.valueOf(request.getParameter("vehicleId"));
			int driverId = Integer.valueOf(request.getParameter("driverId"));
			String ip =  CusAccessObjectUtil.getIpAddress(request);
			// 接收参数
			User user = localService.getUserInSession(request);
			String deptDNA = user.getDeptDNA();
			int userId = user.getId();
			String userName = user.getName();
			Map<String, Object> paramMap = new HashMap<>();
			// 封装参数
			paramMap.put("deptDNA", deptDNA);
			paramMap.put("userId", userId);
			paramMap.put("userName", userName);
			paramMap.put("orderNo", orderNo);
			paramMap.put("vehicleId", vehicleId);
			paramMap.put("driverId", driverId);
			paramMap.put("ip", ip);
			// 返回结果
			assignmentService.assign(paramMap);
			result.setCode(JsonResult.CODE_SUCCESS);
		} catch (ServiceException e) {
			result.setCode(e.getCode());
			result.setMsg(e.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(JsonResult.CODE_FAIL);
			result.setMsg(e.getMessage());
		}
		return result;
	}
}
