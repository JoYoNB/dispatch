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
import com.chainway.dispatchercore.common.Constant;
import com.chainway.dispatchercore.dto.Dept;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatchercore.excetion.ExceptionCode;
import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherservice.service.UserService;
import com.chainway.dispatcherservice.service.carrier.CarrierStatsService;
import com.chainway.dispatcherweb.annotation.ValidateFiled;
import com.chainway.dispatcherweb.annotation.ValidateGroup;
import com.chainway.dispatcherweb.biz.service.LocalService;
import com.chainway.dispatcherweb.dto.JsonResult;

/**
 * 统计报表
 * @author Administrator
 */
@Controller
@RequestMapping(value="/carrier/stats")
public class StatsController {
	@Reference(timeout=60000, check=false)
	private CarrierStatsService carrierStatsService;
	
	@Autowired
	private LocalService localService;
	
	
	@Reference(timeout=60000, check=false)
	private UserService userService;
	
	
	@ResponseBody
	@RequestMapping(value="/getFinishedOrderCount.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult getFinishedOrderCount(HttpServletRequest request,HttpServletResponse response) {
		JsonResult result = new JsonResult();	
		try {
			String queryDeptId = request.getParameter("queryDeptId");
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			User user = localService.getUserInSession(request);
			Map<String, Object> paramMap = new HashMap<>();
			
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
			
			int total = carrierStatsService.getTotalFinishedOrderNum(paramMap);
			
			
	
			paramMap.put("startTime", StringUtils.stripToNull(startTime));
			paramMap.put("endTime", StringUtils.stripToNull(endTime));
			int incNum = carrierStatsService.getTotalFinishedOrderNum(paramMap);
			
			Map<String, Object>  data = new HashMap<>();
			data.put("totalNum", total);
			data.put("incNum", incNum);
			result.setData(data);
			result.setCode(JsonResult.CODE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(JsonResult.CODE_FAIL);
			result.setMsg(e.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value="/listDeptDayOrderDist.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult listDeptDayOrderDist(HttpServletRequest request,HttpServletResponse response) {
		JsonResult result = new JsonResult();	
		try {
			User user = localService.getUserInSession(request);
			//接收参数
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			String queryDeptId = request.getParameter("queryDeptId");
			//封装参数
			Map<String, Object> paramMap = new HashMap<>();
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
			String gmtZone = user.getGmtZone();
			
			int deptId = user.getDeptId();
			if(StringUtils.isNotBlank(queryDeptId)){
				deptId = Integer.parseInt(queryDeptId);
			}
			
			paramMap.put("startTime", StringUtils.stripToNull(startTime));
			paramMap.put("endTime", StringUtils.stripToNull(endTime));
			paramMap.put("gmtZone", Constant.timeZoneMap.get(gmtZone));
			paramMap.put("deptId", deptId);
			
			List<Map<String, Object>> totalDist = carrierStatsService.listFinishedOrderDist(paramMap);
			List<Map<String, Object>> deptDist = carrierStatsService.listDeptDayOrderDist(paramMap);
			List<Map<String, Object>> subDepts = carrierStatsService.listSubDepts(paramMap);
			Map<String,Object> ret = new HashMap<>();
			ret.put("totalDist", totalDist);
			ret.put("deptDist", deptDist);
			ret.put("subDepts", subDepts);
			result.setData(ret);
			result.setCode(JsonResult.CODE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(JsonResult.CODE_FAIL);
			result.setMsg(e.getMessage());
		}
		return result;
	}
	
	
	@ResponseBody
	@RequestMapping(value="/listDeptDayOrderDist2.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult listDeptDayOrderDist2(HttpServletRequest request,HttpServletResponse response) {
		JsonResult result = new JsonResult();	
		try {
			User user = localService.getUserInSession(request);
			//接收参数
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			String queryDeptId = request.getParameter("queryDeptId");
			Map<String, Object> paramMap = new HashMap<>();
			if(StringUtils.isNotEmpty(queryDeptId)){
				Integer deptId=Integer.parseInt(queryDeptId);
				//根据部门Id和用户dna去查询数据，判断是否越权
				Dept d = userService.checkDeptDataAuth(deptId, user.getDeptDNA());
				if (d == null) {
					throw new ServiceException(ExceptionCode.ERROR_NO_DEPT_AUTH, "没有该部门的操作权限");
				}
				paramMap.put("deptDNA", d.getDna());
				paramMap.put("deptId", d.getId());
			}else{
				//没有传部门ID过来，则默认是用户本身部门
				paramMap.put("deptDNA", user.getDeptDNA());
				paramMap.put("deptId", user.getDeptId());
			}
			String gmtZone = user.getGmtZone();
			String daysArrStr = request.getParameter("daysArr");
			paramMap.put("daysArrStr", daysArrStr);
			paramMap.put("startTime", StringUtils.stripToNull(startTime));
			paramMap.put("endTime", StringUtils.stripToNull(endTime));
			paramMap.put("gmtZone", Constant.timeZoneMap.get(gmtZone));
			
			List<Map<String, Object>> list = carrierStatsService.structureTableDist(paramMap);
			
			Map<String,Object> ret = new HashMap<>();
			ret.put("list", list);
			result.setData(ret);
			result.setCode(JsonResult.CODE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(JsonResult.CODE_FAIL);
			result.setMsg(e.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value="/listSubDepts.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult listSubDepts(HttpServletRequest request,HttpServletResponse response) {
		JsonResult result = new JsonResult();	
		try {
			User user = localService.getUserInSession(request);
			String queryDeptId = request.getParameter("queryDeptId");
			int deptId = user.getDeptId();
			if(StringUtils.isNotEmpty(queryDeptId)){
				deptId = Integer.parseInt(queryDeptId);
			}
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("deptId", deptId);
			List<Map<String, Object>> subDepts = carrierStatsService.listSubDepts(paramMap);
			result.setData(subDepts);
			result.setCode(JsonResult.CODE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(JsonResult.CODE_FAIL);
			result.setMsg(e.getMessage());
		}
		return result;
	}
	
	@TimeZone(type="preHandle")
	@ResponseBody
	@RequestMapping("/exportOrderStats.json")
	public JsonResult exportOrderStats(HttpServletRequest request, JsonResult result) throws Exception {
		try {
			User user = localService.getUserInSession(request);
			//接收参数
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			String queryDeptId = request.getParameter("queryDeptId");
			String gmtZone = user.getGmtZone();
			//封装参数
			Map<String, Object> paramMap = new HashMap<>();
			if(StringUtils.isNotEmpty(queryDeptId)){
				Integer deptId=Integer.parseInt(queryDeptId);
				//根据部门Id和用户dna去查询数据，判断是否越权
				Dept d = userService.checkDeptDataAuth(deptId, user.getDeptDNA());
				if (d == null) {
					throw new ServiceException(ExceptionCode.ERROR_NO_DEPT_AUTH, "没有该部门的操作权限");
				}
				paramMap.put("deptDNA", d.getDna());
				paramMap.put("deptId", d.getId());
			}else{
				//没有传部门ID过来，则默认是用户本身部门
				paramMap.put("deptDNA", user.getDeptDNA());
			}
			String daysArrStr = request.getParameter("daysArr");
			paramMap.put("daysArrStr", daysArrStr);
			paramMap.put("startTime", startTime);
			paramMap.put("endTime", endTime);
			paramMap.put("gmtZone", Constant.timeZoneMap.get(gmtZone));
			Map<String, Object> data = carrierStatsService.exportOrderStats(paramMap);
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
	@RequestMapping(value="/listConsignorRanking.json",method= {RequestMethod.POST,RequestMethod.GET})
	@ValidateGroup(fileds= {@ValidateFiled(index=0,notNull=false,filedName="startTime"),
			@ValidateFiled(index=0,notNull=false,filedName="endTime")})
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
			paramMap.put("maxNum", 10);
			
			
			List<Map<String, Object>> data = carrierStatsService.listConsignorOrderRanking(paramMap);
			result.setData(data);
			result.setCode(JsonResult.CODE_SUCCESS);
		} catch (Exception e) {
			result.setCode(JsonResult.CODE_FAIL);
			result.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * 客户订单数量列表接口
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/listCustomerOrderCount.json",method= {RequestMethod.POST,RequestMethod.GET})
	@ValidateGroup(fileds= {@ValidateFiled(index=0,notNull=false,filedName="startTime"),
			@ValidateFiled(index=0,notNull=false,filedName="endTime")})
	public JsonResult listCustomerOrderCount(HttpServletRequest request,HttpServletResponse response) {
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
			
			// 页码
			int pageSize = CommonUtils.getPageSize(request);
			int offset = CommonUtils.getOffset(request);
			paramMap.put("pageSize", pageSize);
			paramMap.put("offset", offset);
			
			List<Map<String, Object>> data = carrierStatsService.listConsignorOrderRanking(paramMap);
			int total = carrierStatsService.getConsignorOrderRankingCount(paramMap);
			Map<String, Object> ret = new HashMap<String, Object>();
			ret.put("list", data);
			ret.put("total", total);
			result.setCode(JsonResult.CODE_SUCCESS);
			result.setData(ret);
		} catch (Exception e) {
			result.setCode(JsonResult.CODE_FAIL);
			result.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value="/getCustomerCount.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult getCustomerCount(HttpServletRequest request,HttpServletResponse response) {
		JsonResult result = new JsonResult();	
		try {
			String queryDeptId = request.getParameter("queryDeptId");
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			User user = localService.getUserInSession(request);
			Map<String, Object> paramMap = new HashMap<>();
			
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
			
			int totalNum = carrierStatsService.getCustomerCount(paramMap);
			paramMap.put("startTime", StringUtils.stripToNull(startTime));
			paramMap.put("endTime", StringUtils.stripToNull(endTime));
			int incNum = carrierStatsService.getCustomerCount(paramMap);
			
			Map<String, Object>  data = new HashMap<>();
			data.put("totalNum", totalNum);
			data.put("incNum", incNum);
			result.setData(data);
			result.setCode(JsonResult.CODE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(JsonResult.CODE_FAIL);
			result.setMsg(e.getMessage());
		}
		return result;
	}
	
	@TimeZone(type="preHandle")
	@ResponseBody
	@RequestMapping("/exportCustomerStats.json")
	public JsonResult exportCustomerStats(HttpServletRequest request, JsonResult result) throws Exception {
		try {
			User user = localService.getUserInSession(request);
			//接收参数
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			String queryDeptId = request.getParameter("queryDeptId");
			String gmtZone = user.getGmtZone();
			//封装参数
			Map<String, Object> paramMap = new HashMap<>();
			if(StringUtils.isNotEmpty(queryDeptId)){
				Integer deptId=Integer.parseInt(queryDeptId);
				//根据部门Id和用户dna去查询数据，判断是否越权
				Dept d = userService.checkDeptDataAuth(deptId, user.getDeptDNA());
				if (d == null) {
					throw new ServiceException(ExceptionCode.ERROR_NO_DEPT_AUTH, "没有该部门的操作权限");
				}
				paramMap.put("deptDNA", d.getDna());
				paramMap.put("deptId", d.getId());
			}else{
				//没有传部门ID过来，则默认是用户本身部门
				paramMap.put("deptDNA", user.getDeptDNA());
			}
			String daysArrStr = request.getParameter("daysArr");
			paramMap.put("daysArrStr", daysArrStr);
			paramMap.put("startTime", startTime);
			paramMap.put("endTime", endTime);
			paramMap.put("gmtZone", Constant.timeZoneMap.get(gmtZone));
			Map<String, Object> data = carrierStatsService.exportCustomerStats(paramMap);
			result.setData(data);
			result.setCode(JsonResult.CODE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(JsonResult.CODE_FAIL);
			result.setMsg(e.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value="/settleStats.json",method= {RequestMethod.POST,RequestMethod.GET})
	@ValidateGroup(fileds= {@ValidateFiled(index=0,notNull=false,filedName="startTime"),
			@ValidateFiled(index=0,notNull=false,filedName="endTime")})
	public JsonResult settleStats(HttpServletRequest request,HttpServletResponse response) {
		JsonResult result = new JsonResult();
		try {
			//接收参数
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			User user = localService.getUserInSession(request);
			String gmtZone = user.getGmtZone();
			String deptIdStr=request.getParameter("queryDeptId");
			Map<String, Object> paramMap = new HashMap<>();
			if(StringUtils.isNotEmpty(deptIdStr)){
				Integer deptId=Integer.parseInt(deptIdStr);
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
			paramMap.put("gmtZone", Constant.timeZoneMap.get(gmtZone));
			paramMap.put("startTime",StringUtils.stripToNull(startTime));
			paramMap.put("endTime", StringUtils.stripToNull(endTime));
			
			// 页码
			int pageSize = CommonUtils.getPageSize(request);
			int offset = CommonUtils.getOffset(request);
			paramMap.put("pageSize", pageSize);
			paramMap.put("offset", offset);
			
			List<Map<String, Object>> data = carrierStatsService.settleStats(paramMap);
			int total = carrierStatsService.getSettleStatsCount(paramMap);
			Map<String, Object> ret = new HashMap<>();
			ret.put("list", data);
			ret.put("total", total);
			result.setCode(JsonResult.CODE_SUCCESS);
			result.setData(ret);
		} catch (Exception e) {
			result.setCode(JsonResult.CODE_FAIL);
			result.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
}
