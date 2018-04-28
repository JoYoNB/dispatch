package com.chainway.driverappweb.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chainway.dispatchercore.annotation.TimeZone;
import com.chainway.dispatchercore.dto.Driver;
import com.chainway.dispatchercore.dto.JsonResult;
import com.chainway.dispatchercore.dto.Vehicle;
import com.chainway.dispatchercore.excetion.ExceptionCode;
import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherdriverservice.service.DriverInfoService;
import com.chainway.dispatcherservice.service.DriverService;
import com.chainway.dispatcherservice.service.VehicleService;
import com.chainway.driverappweb.annotation.Log;
import com.chainway.driverappweb.annotation.ValidateFiled;
import com.chainway.driverappweb.annotation.ValidateGroup;
import com.chainway.driverappweb.biz.service.LocalService;
import com.chainway.fileservice.service.FileService;

import sun.misc.BASE64Decoder;

@Controller
@RequestMapping(value="/information")
public class InfomationController {
	
	@Autowired
	private LocalService localService;

	@Reference(timeout=60000, check=false)
	private DriverInfoService driverInfoService;
	
	@Reference(timeout=60000, check=false)
	private DriverService driverService;
	
	@Reference(timeout=60000, check=false)
	private VehicleService vehicleService;
	
	@Reference(timeout=60000, check=false)
	private FileService fileService;
	
	/**
	 * 司机信息
	 * @param request
	 * @return
	 */
	@Log
	@ResponseBody
	@TimeZone(type="return")
	@RequestMapping(value="/getDriverInfo.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult getDriverInfo(HttpServletRequest request) throws Exception {
		//根据token获取到登录的司机信息
		Driver driverInSession = localService.getUserInSession(request);
		//获取查询对象
		JsonResult jsonResult = new JsonResult();
		Map<String, Object>driverInfo=driverInfoService.getDriverInfo(driverInSession);
	    jsonResult.setCode(JsonResult.CODE_SUCCESS);
	    jsonResult.setData(driverInfo);
		return jsonResult;
	}
	
	/**
	 * 修改司机信息
	 * @param request
	 * @return
	 */
	@Log
	@ResponseBody
	@TimeZone(type="both")
	@ValidateGroup(fileds= {
			@ValidateFiled(index=0,notNull=false,filedName="driverName"),
            @ValidateFiled(index=0,notNull=false,filedName="vehicleId"),
            @ValidateFiled(index=0,notNull=false,filedName="phoneNo",regStr="phone"),  
            @ValidateFiled(index=0,notNull=false,filedName="entryTime")
	})
	@RequestMapping(value="/updateDriverInfo.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult updateDriverInfo(Driver driver,HttpServletRequest request,JsonResult jsonResult) throws Exception {
		//根据token获取到登录的司机信息
		Driver driverInSession = localService.getUserInSession(request);
		driver.setDriverId(driverInSession.getDriverId());
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
	    jsonResult.setCode(JsonResult.CODE_SUCCESS);
		return jsonResult;
	}
	/**
	 * 统计信息
	 * @param request
	 * @return
	 */
	@Log
	@ResponseBody
	@TimeZone(type="return")
	@RequestMapping(value="/getDriverStatis.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult getDriverStatis(HttpServletRequest request) throws Exception {
		//根据token获取到登录的司机信息
		Driver driverInSession = localService.getUserInSession(request);
		//获取查询对象
		JsonResult jsonResult = new JsonResult();
		Map<String, Object>driverInfo=driverInfoService.getDriverStatis(driverInSession);
	    jsonResult.setCode(JsonResult.CODE_SUCCESS);
	    jsonResult.setData(driverInfo);
		return jsonResult;
	}
	
	/**
	 * 车辆信息
	 * @return
	 */
	@Log
	@ResponseBody
	@RequestMapping(value="/getVehicleInfo.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult getVehicleInfo(HttpServletRequest request,JsonResult jsonResult) throws Exception{
		//根据token获取到登录的司机信息
		Driver driverInSession = localService.getUserInSession(request);
		Map<String, Object> vehicleInfo = driverInfoService.getVehicleInfo(driverInSession);
		jsonResult.setCode(JsonResult.CODE_SUCCESS);
		jsonResult.setData(vehicleInfo);
		return jsonResult;
	}
	
	/**
	 * 修改车辆信息
	 * @return
	 */
	@Log
	@ResponseBody
	@ValidateGroup(fileds = {//校验字段信息
			@ValidateFiled(index=0,notNull=true,filedName="vehicleId"),
    })
	@RequestMapping(value="/updateVehicleInfo.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult updateVehicleInfo(Vehicle vehicle,HttpServletRequest request,JsonResult jsonResult) throws Exception{
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
		//上传车辆图片
		String vehicleImage = request.getParameter("vehicleImage");
		if(StringUtils.isNotEmpty(vehicleImage)) {
			//Base64解码图片
			BASE64Decoder decoder = new BASE64Decoder();
	        byte[] bytes = decoder.decodeBuffer(vehicleImage);
	        for (int i = 0; i < bytes.length; ++i) {
		        if (bytes[i] < 0) {// 调整异常数据
		          bytes[i] += 256;
		        }
	        }
	        //调用文件上传服务
			long l = System.currentTimeMillis();
			String imageUrl=fileService.uploadFile( l + ".jpg",bytes,"123456789");
			vehicle.setImageUrl(imageUrl);
		}
		//更新数据
		vehicleService.update(vehicle);
		jsonResult.setCode(JsonResult.CODE_SUCCESS);
		jsonResult.setMsg("修改成功");
		return jsonResult;
	}
}
