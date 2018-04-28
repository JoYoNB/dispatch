package com.chainway.dispatcherweb.web.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chainway.dispatchercore.annotation.TimeZone;
import com.chainway.dispatchercore.common.CommonUtils;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatchercore.dto.Vehicle;
import com.chainway.dispatchercore.excetion.ExceptionCode;
import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherservice.service.VehicleService;
import com.chainway.dispatcherweb.annotation.Log;
import com.chainway.dispatcherweb.annotation.ValidateFiled;
import com.chainway.dispatcherweb.annotation.ValidateGroup;
import com.chainway.dispatcherweb.biz.service.LocalService;
import com.chainway.dispatcherweb.dto.JsonResult;

@Controller
@RequestMapping(value="/vehicle")
public class VehicleController {
	protected final Logger log=Logger.getLogger(this.getClass());
	
	@Reference(timeout=60000, check=false)
	private VehicleService vehicleService;
	
	@Autowired
	private LocalService localService;
	
	@Log
	@ResponseBody
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="plateNo"),
            @ValidateFiled(index=0,notNull=true,filedName="vehicleTypeId"),
            @ValidateFiled(index=0,notNull=true,filedName="carryTypeIds"),  
            @ValidateFiled(index=0,notNull=false,filedName="equipmentPn"),
            @ValidateFiled(index=0,notNull=false,filedName="weigthUseFactor"),
            @ValidateFiled(index=0,notNull=false,filedName="swerveRadiusMin"),
            @ValidateFiled(index=0,notNull=false,filedName="powerRate"),
            @ValidateFiled(index=0,notNull=true,filedName="vehicleInsideLength"),
            @ValidateFiled(index=0,notNull=true,filedName="vehicleInsideWidth"),
            @ValidateFiled(index=0,notNull=true,filedName="vehicleInsideHeight"),
            @ValidateFiled(index=0,notNull=true,filedName="vehicleWeightMax"),
            @ValidateFiled(index=0,notNull=true,filedName="carryWeigthMax"),
            @ValidateFiled(index=0,notNull=true,filedName="vehicleLength"),
            @ValidateFiled(index=0,notNull=false,filedName="remark")
    })
	@RequestMapping(value="/addVehicle.json",method= {RequestMethod.POST})
	public JsonResult addVehicle(Vehicle vehicle,HttpServletRequest request,HttpServletResponse response) throws Exception {
		JsonResult result = new JsonResult();	
		User user=localService.getUserInSession(request);
		String ip=request.getRemoteAddr();
		//封装参数
		Map<String,Object>paramMap=new HashMap<String,Object>();
		paramMap.put("ip", ip);
		paramMap.put("user", user);
		paramMap.put("vehicle", vehicle);
		String msg = vehicleService.add(paramMap);
		result.setCode(JsonResult.CODE_SUCCESS);
		result.setMsg(msg);
		return result;
	}
	
	@Log
	@ResponseBody
	@TimeZone(type="return")
	@RequestMapping(value="/getVehicle.json",method= {RequestMethod.POST,RequestMethod.GET})
	@ValidateGroup(fileds= {@ValidateFiled(index=0,notNull=true,filedName="vehicleId") })
	public JsonResult getVehicle(HttpServletRequest request,HttpServletResponse response)  throws Exception {
		JsonResult result = new JsonResult();
		String vehicleId = request.getParameter("vehicleId");
		User user=localService.getUserInSession(request);
		HashMap<String, Object> paramMap = new HashMap<>();
		String deptDNA = user.getDeptDNA();
		paramMap.put("deptDNA", deptDNA);
		paramMap.put("vehicleId",vehicleId);
		Map<String, Object> vehicleInfo = vehicleService.getVehicle(paramMap);
		result.setCode(0);
		result.setData(vehicleInfo);
		return result;
	}
	@Log
	@ResponseBody
	@TimeZone(type="return")
	@ValidateGroup(fileds= {@ValidateFiled(index=0,notNull=false,filedName="vehicleTypeId"),
			@ValidateFiled(index=0,notNull=false,filedName="equipmentPn"),
			@ValidateFiled(index=0,notNull=false,filedName="plateNo"),
			@ValidateFiled(index=0,notNull=false,filedName="pageNum"),
			@ValidateFiled(index=0,notNull=false,filedName="pageSize")})
	@RequestMapping(value="/getVehicleList.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult getVehicleList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		JsonResult result = new JsonResult();
		User user=localService.getUserInSession(request);
		Map<String, Object> paramMap = new HashMap<>();
		String deptDNA = user.getDeptDNA();
		int pageSize=CommonUtils.getPageSize(request);
		int offset=CommonUtils.getOffset(request);
		paramMap.put("pageSize", pageSize);
		paramMap.put("offset", offset);
		paramMap.put("vehicleTypeId", request.getParameter("vehicleTypeId"));
		paramMap.put("equipmentPn", request.getParameter("equipmentPn"));
		paramMap.put("plateNo", request.getParameter("plateNo"));
		paramMap.put("deptDNA", deptDNA);
		String vehicleId = request.getParameter("vehicleId");
		paramMap.put("vehicleId", vehicleId);
		List<Map<String, Object>> vehicleList = vehicleService.getVehicleList(paramMap);
		Integer total = vehicleService.getVehicleListCount(paramMap);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("total", total);
		resultMap.put("list", vehicleList);
		result.setCode(0);
		result.setData(resultMap);
		return result;
	}
	/**
	 * 下拉选择车辆列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@Log
	@ResponseBody
	@TimeZone(type="return")
	@RequestMapping(value="/getCommonVehicles.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult getCommonVehicles(HttpServletRequest request,HttpServletResponse response,JsonResult jsonResult) throws Exception {
		User user=localService.getUserInSession(request);
		Map<String, Object> paramMap = new HashMap<>();
		String deptDNA = user.getDeptDNA();
		paramMap.put("deptDNA", deptDNA);
		List<Map<String, Object>> vehicleList = vehicleService.getCommonVehicles(paramMap);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("list", vehicleList);
		jsonResult.setCode(0);
		jsonResult.setData(resultMap);
		return jsonResult;
	}
	@Log
	@ResponseBody
	@ValidateGroup(fileds = {//校验字段信息
			@ValidateFiled(index=0,notNull=true,filedName="vehicleId"),
            @ValidateFiled(index=0,notNull=false,filedName="plateNo"),
            @ValidateFiled(index=0,notNull=false,filedName="vehicleTypeId"),
            @ValidateFiled(index=0,notNull=false,filedName="carryTypeIds"),  
            @ValidateFiled(index=0,notNull=false,filedName="equipmentPn"),
            @ValidateFiled(index=0,notNull=false,filedName="weigthUseFactor"),
            @ValidateFiled(index=0,notNull=false,filedName="swerveRadiusMin"),
            @ValidateFiled(index=0,notNull=false,filedName="powerRate"),
            @ValidateFiled(index=0,notNull=false,filedName="vehicleInsideLength"),
            @ValidateFiled(index=0,notNull=false,filedName="vehicleInsideWidth"),
            @ValidateFiled(index=0,notNull=false,filedName="vehicleInsideHeight"),
            @ValidateFiled(index=0,notNull=false,filedName="vehicleWeightMax"),
            @ValidateFiled(index=0,notNull=false,filedName="carryWeigthMax"),
            @ValidateFiled(index=0,notNull=false,filedName="vehicleLength"),
            @ValidateFiled(index=0,notNull=false,filedName="remark")
    })
	@RequestMapping(value="/updateVehicle.json")
	public JsonResult updateVehicle(Vehicle vehicle,HttpServletRequest request,HttpServletResponse response) throws Exception {
		JsonResult result = new JsonResult();
		//判断车牌号是否重复
		if(null!=vehicle.getPlateNo()) {
			Map<String,Object>para=new HashMap<String,Object>();
			para.put("plateNo", vehicle.getPlateNo());
			List<Map<String, Object>> vehicleList = vehicleService.getVehicleList(para);
			if (null != vehicleList && vehicleList.size()>0) {
				if(!vehicle.getVehicleId().equals(vehicleList.get(0).get("vehicleId"))) {
					throw new ServiceException(ExceptionCode.ERROR_VIHICLE_ADD_PLATENO_REPEAT, "车牌号重复");
				}
			}
		}
		vehicleService.update(vehicle);
		result.setCode(JsonResult.CODE_SUCCESS);
		result.setMsg("修改成功");
		return result;
	}
	@Log
	@ResponseBody
	@ValidateGroup(fileds = {@ValidateFiled(index=0,notNull=true,filedName="vehicleId")})
	@RequestMapping(value="/deleteVehicle.json")
	public JsonResult deleteVehicle(HttpServletRequest request,HttpServletResponse response) throws Exception {
		JsonResult result = new JsonResult();
		Integer vehicleId=Integer.parseInt(request.getParameter("vehicleId"));
		//判断该车辆是否有关联的司机
		Map<String, Object> param = new HashMap<>();
		param.put("vehicleId", vehicleId);
		Map<String, Object> vehicle = vehicleService.getVehicle(param);
		if(vehicle!=null&&vehicle.get("driverId")!=null&&vehicle.get("driverId")!="") {
			 throw new ServiceException(ExceptionCode.ERROR_VIHICLE_DELETE__FAIL, "该车辆有关联司机");
		}
		Boolean isSuccess = vehicleService.delete(vehicleId);
		if(isSuccess) {
			result.setCode(JsonResult.CODE_SUCCESS);
			result.setMsg("删除成功");
		}else {
			result.setCode(JsonResult.CODE_FAIL);
			result.setMsg("删除失败");
		}
		return result;
	}
	@Log
	@ResponseBody
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="id[]")
    })
	@RequestMapping(value="/deleteVehicleList.json")
	public JsonResult deleteVehicleList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		JsonResult result = new JsonResult();
		String[]ids=request.getParameterValues("id[]");
		for(String id:ids){
			//判断该车辆是否有关联的司机
			Map<String, Object> param = new HashMap<>();
			param.put("vehicleId", id);
			Map<String, Object> vehicle = vehicleService.getVehicle(param);
			if(vehicle!=null&&vehicle.get("driverId")!=null&&vehicle.get("driverId")!="") {
				 throw new ServiceException(ExceptionCode.ERROR_VIHICLE_DELETE__FAIL, "该车辆有关联司机");
			}
			Integer vehicelId = Integer.parseInt(id);
			vehicleService.delete(vehicelId);
		}
		result.setCode(JsonResult.CODE_SUCCESS);
		return result;
	}
}
