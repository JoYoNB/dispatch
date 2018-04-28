package com.chainway.dispatcherweb.web.controller;

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
import com.chainway.dispatchercore.common.CommonUtils;
import com.chainway.dispatchercore.dto.Driver;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatchercore.excetion.ExceptionCode;
import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherservice.service.DriverService;
import com.chainway.dispatcherservice.service.UserService;
import com.chainway.dispatcherweb.annotation.Log;
import com.chainway.dispatcherweb.annotation.ValidateFiled;
import com.chainway.dispatcherweb.annotation.ValidateGroup;
import com.chainway.dispatcherweb.biz.service.LocalService;
import com.chainway.dispatcherweb.common.AuthUtils;
import com.chainway.dispatcherweb.dto.JsonResult;

@Controller
@RequestMapping(value="/driver")
public class DriverController {
	@Reference(timeout=60000, check=false)
	private DriverService driverService;
	
	@Autowired
	private LocalService localService;
	@Reference(timeout=60000, check=false)
	private UserService userSerive;
	
	@Log
	@ResponseBody
	@TimeZone(type="both")
	@ValidateGroup(fileds= {//校验字段信息
			@ValidateFiled(index=0,notNull=true,filedName="driverName"),
			@ValidateFiled(index=0,notNull=true,filedName="deptId"),
			@ValidateFiled(index=0,notNull=true,filedName="vehicleId"),
            @ValidateFiled(index=0,notNull=true,filedName="phoneNo",regStr="phone"),  
            @ValidateFiled(index=0,notNull=true,filedName="entryTime")
    })
	@RequestMapping(value="/addDriver.json",method= {RequestMethod.POST})
	public JsonResult addVehicle(Driver driver,HttpServletRequest request,HttpServletResponse response) throws Exception {
		JsonResult result = new JsonResult();	
		//接收参数
		User user=localService.getUserInSession(request);
		String gmtZone = user.getGmtZone();
		//判断车辆id是否已被关联司机
		Map<String, Object>param=new HashMap<String,Object>();
		param.put("vehicleId", driver.getVehicleId());
		List<Map<String, Object>> driverList = driverService.getDriverList(param);
		if(driverList!=null && driverList.size()>0) {
			 throw new ServiceException(ExceptionCode.ERROR_DRIVER_ADD__FAIL, "该车辆已被其他司机关联");
		}
		//封装参数
		driver.setGmtZone(gmtZone);
		driver.setPassword(AuthUtils.getPassword("123456", 10));//默认密码为123456
		driverService.add(driver);
		result.setCode(JsonResult.CODE_SUCCESS);
		return result;
	}
	
	@Log
	@ResponseBody
	@TimeZone(type="return")
	@RequestMapping(value="/getDriver.json",method= {RequestMethod.POST,RequestMethod.GET})
	@ValidateGroup(fileds= {@ValidateFiled(index=0,notNull=true,filedName="driverId") })
	public JsonResult getDriver(HttpServletRequest request,HttpServletResponse response)  throws Exception{
		JsonResult result = new JsonResult();
		HashMap<String, Object> paramMap = new HashMap<>();
		paramMap.put("driverId",request.getParameter("driverId"));
		Driver driver = driverService.getDriver(paramMap);
		result.setCode(0);
		result.setData(driver);
		return result;
	}
	@Log
	@ResponseBody
	@ValidateGroup(fileds= {@ValidateFiled(index=0,notNull=false,filedName="startTime"),
			@ValidateFiled(index=0,notNull=false,filedName="endTime"),
			@ValidateFiled(index=0,notNull=false,filedName="deptId"),
			@ValidateFiled(index=0,notNull=false,filedName="vehicleTypeId"),
			@ValidateFiled(index=0,notNull=false,filedName="plateNo"), 
			@ValidateFiled(index=0,notNull=false,filedName="equipmentPn"), 
			@ValidateFiled(index=0,notNull=false,filedName="driverId"), 
			@ValidateFiled(index=0,notNull=false,filedName="pageSize"), 
			@ValidateFiled(index=0,notNull=false,filedName="pageNum")})
	@TimeZone(type="both")
	@RequestMapping(value="/getDriverList.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult getDriverList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		JsonResult result = new JsonResult();
		User user=localService.getUserInSession(request);
		//接收参数
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String deptId = request.getParameter("deptId");
		String vehicleTypeId = request.getParameter("vehicleTypeId");
		String plateNo = request.getParameter("plateNo");
		String equipmentPn = request.getParameter("equipmentPn");
		String driverId = request.getParameter("driverId");
		String driverName = request.getParameter("driverName");
		String phoneNo = request.getParameter("phoneNo");
		String deptDNA = user.getDeptDNA();
		int pageSize=CommonUtils.getPageSize(request);
		int offset=CommonUtils.getOffset(request);
		//封装参数
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		paramMap.put("deptId", deptId);
		paramMap.put("vehicleTypeId", vehicleTypeId);
		paramMap.put("plateNo", plateNo);
		paramMap.put("equipmentPn", equipmentPn);
		paramMap.put("driverId", driverId);
		paramMap.put("driverName", driverName);
		paramMap.put("phoneNo", phoneNo);
		paramMap.put("deptDNA", deptDNA);
		paramMap.put("offset",offset);
		paramMap.put("pageSize", pageSize);
		List<Map<String, Object>> driverList = driverService.getDriverList(paramMap);
		Integer total = driverService.getDriverListCount(paramMap);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("total", total);
		resultMap.put("list", driverList);
		result.setCode(0);
		result.setData(resultMap);
		return result;
	}
	@Log
	@ResponseBody
	@TimeZone(type="both")
	@ValidateGroup(fileds = {//校验字段信息
			@ValidateFiled(index=0,notNull=true,filedName="driverId"),
			@ValidateFiled(index=0,notNull=false,filedName="driverName"),
            @ValidateFiled(index=0,notNull=false,filedName="deptId"),
            @ValidateFiled(index=0,notNull=false,filedName="vehicleId"),
            @ValidateFiled(index=0,notNull=false,filedName="phoneNo",regStr="phone"),  
            @ValidateFiled(index=0,notNull=false,filedName="entryTime")
    })
	@RequestMapping(value="/updateDriver.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult updateDriver(Driver driver,HttpServletRequest request,HttpServletResponse response) throws Exception {
		JsonResult result = new JsonResult();
		//判断车辆id是否已被关联司机
		Map<String, Object>param=new HashMap<String,Object>();
		Integer vehicleId = driver.getVehicleId();
		if(null!=vehicleId) {
			param.put("vehicleId", driver.getVehicleId());
			List<Map<String, Object>> driverList = driverService.getDriverList(param);
			if(driverList!=null && driverList.size()>0) {
				if(!driverList.get(0).get("driverId").equals(driver.getDriverId())) {
					throw new ServiceException(ExceptionCode.ERROR_DRIVER_ADD__FAIL, "该车辆已被其他司机关联");
				}
			}
		}
		driverService.update(driver);
		result.setCode(0);
		result.setMsg("修改成功");
		return result;
	}
	@Log
	@ResponseBody
	@ValidateGroup(fileds = {@ValidateFiled(index=0,notNull=true,filedName="driverId")})
	@RequestMapping(value="/deleteDriver.json")
	public JsonResult deleteDriver(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JsonResult result = new JsonResult();
		User user=localService.getUserInSession(request);
		//判断该司机是否有关联的车辆
		Map<String, Object> param = new HashMap<>();
		param.put("driverId", request.getParameter("driverId"));
		Driver driverInDB = driverService.getDriver(param);
		if(driverInDB!=null&&driverInDB.getVehicleId()!=null) {
			 throw new ServiceException(ExceptionCode.ERROR_DRIVER_DELETE__FAIL, "该司机有关联车辆");
		}
		String deptDNA = user.getDeptDNA();
		param.put("deptDNA", deptDNA);
		Boolean isSuccess = driverService.delete(param);
		if(isSuccess) {
			result.setCode(0);
			result.setMsg("删除成功");
			return result;
		}
		return result;
	}
	
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="id[]")
    })
	@Log
	@RequestMapping(value="/deleteDriverList.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult deleteDriverList(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		String[]ids=request.getParameterValues("id[]");
		for(String id:ids){
			//判断该司机是否有关联的车辆
			Map<String, Object> param = new HashMap<>();
			param.put("driverId",id);
			Driver driverInDB = driverService.getDriver(param);
			if(driverInDB!=null&&driverInDB.getVehicleId()!=null) {
				 throw new ServiceException(ExceptionCode.ERROR_DRIVER_DELETE__FAIL, "该司机有关联车辆");
			}
			String deptDNA = user.getDeptDNA();
			param.put("deptDNA", deptDNA);
			driverService.delete(param);
		}
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
}
