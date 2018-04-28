package com.chainway.dispatcherweb.web.controller.carrier;

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
import com.chainway.dispatchercore.annotation.TimeZone;
import com.chainway.dispatchercore.common.Constant;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatcherservice.service.carrier.AssignmentService;
import com.chainway.dispatcherservice.service.carrier.CarrierStatsService;
import com.chainway.dispatcherweb.annotation.ValidateFiled;
import com.chainway.dispatcherweb.annotation.ValidateGroup;
import com.chainway.dispatcherweb.biz.service.LocalService;
import com.chainway.dispatcherweb.dto.JsonResult;
/**
 * 承运商首页
 */
@Controller
@RequestMapping(value="/carrier/index")
public class CarrierIndexController {
	@Reference(timeout=60000, check=false)
	private CarrierStatsService carrierStatsService;
	
	@Autowired
	private LocalService localService;
	
	@Reference(timeout=60000, check=false)
	private AssignmentService assignmentService;
	
	
	/**
	 * 累计完成订单统计接口
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="getTotalAmount.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult getTotalAmount(HttpServletRequest request,HttpServletResponse response) {
		JsonResult result = new JsonResult();	
		try {
			// 接收参数
			User user = localService.getUserInSession(request);
			String deptDNA = user.getDeptDNA();
			Map<String, Object> paramMap = new HashMap<>();
			// 封装参数
			paramMap.put("deptDNA", deptDNA);
			int amount = carrierStatsService.getTotalFinishedOrderNum(paramMap);
			// 返回结果
			HashMap<String, Object> data = new HashMap<>();
			data.put("amount", amount);
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
	 * 客户订单排行接口
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@TimeZone(type="both")
	@RequestMapping(value="/listConsignorRanking.json",method= {RequestMethod.POST,RequestMethod.GET})
	@ValidateGroup(fileds= {@ValidateFiled(index=0,notNull=true,filedName="startTime",regStr="datetime"),
			@ValidateFiled(index=0,notNull=true,filedName="endTime",regStr="datetime")})
	public JsonResult listConsignorRanking(HttpServletRequest request,HttpServletResponse response) {
		JsonResult result = new JsonResult();
		try {
			//接收参数
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			User user = localService.getUserInSession(request);
			String deptDNA = user.getDeptDNA();
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("deptDNA", deptDNA);
			paramMap.put("startTime", startTime);
			paramMap.put("endTime", endTime);
			// 最多展示10条
			paramMap.put("maxNum", 10);
			List<Map<String, Object>> data = carrierStatsService.listConsignorOrderRanking(paramMap);
			Map<String, Object> ret = new HashMap<String, Object>();
			ret.put("list", data);
			result.setCode(JsonResult.CODE_SUCCESS);
			result.setData(ret);
		} catch (Exception e) {
			result.setCode(JsonResult.CODE_FAIL);
			result.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 部门订单分布接口
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@ValidateGroup(fileds= {@ValidateFiled(index=0,notNull=true,filedName="startTime",regStr="datetime"),
			@ValidateFiled(index=0,notNull=true,filedName="endTime",regStr="datetime")})
	@TimeZone(type="preHandle")
	@RequestMapping(value="/listDeptOrderDist.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult listDeptOrderDist(HttpServletRequest request,HttpServletResponse response) {
		JsonResult result = new JsonResult();
		try {
			User user = localService.getUserInSession(request);
			//接收参数
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			String deptDNA = user.getDeptDNA();
			int deptId = user.getDeptId();
			//封装参数
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("startTime", startTime);
			paramMap.put("endTime", endTime);
			paramMap.put("deptDNA", deptDNA);
			paramMap.put("deptId", deptId);
			Map<String, Object> data = carrierStatsService.listDeptOrderDist(paramMap);
			result.setCode(JsonResult.CODE_SUCCESS);
			result.setData(data);
		} catch (Exception e) {
			result.setCode(JsonResult.CODE_FAIL);
			result.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 订单按时间分布统计接口
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@ValidateGroup(fileds= {@ValidateFiled(index=0,notNull=true,filedName="startTime",regStr="datetime"),
			@ValidateFiled(index=0,notNull=true,filedName="endTime",regStr="datetime")})
	@TimeZone(type="prehandle")
	@RequestMapping(value="/listFinishedOrderDist.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult listFinishedOrderDist(HttpServletRequest request,HttpServletResponse response) {
		JsonResult result = new JsonResult();
		try {
			User user = localService.getUserInSession(request);
			//接收参数
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			String deptDNA = user.getDeptDNA();
			String gmtZone = user.getGmtZone();
			//封装参数
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("startTime", startTime);
			paramMap.put("endTime", endTime);
			paramMap.put("deptDNA", deptDNA);
			paramMap.put("gmtZone", Constant.timeZoneMap.get(gmtZone));
			List<Map<String, Object>> data = carrierStatsService.listFinishedOrderDist(paramMap);
			result.setCode(JsonResult.CODE_SUCCESS);
			result.setData(data);
		} catch (Exception e) {
			result.setCode(JsonResult.CODE_FAIL);
			result.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 获取新订单消息接口
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@TimeZone(type="both")
	@RequestMapping(value="/listOrders.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult listOrders(HttpServletRequest request,HttpServletResponse response) {
		JsonResult result = new JsonResult();
		try {
			User user = localService.getUserInSession(request);
			//接收参数
			int carrierDept = user.getMerchantDeptId();
			//封装参数
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("carrierDept", carrierDept);
			List<Map<String, Object>> data = assignmentService.listPublishedOrders(paramMap);
			result.setCode(JsonResult.CODE_SUCCESS);
			result.setData(data);
		} catch (Exception e) {
			result.setCode(JsonResult.CODE_FAIL);
			result.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
}
