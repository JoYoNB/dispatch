package com.chainway.driverappweb.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chainway.cacheService.biz.service.CacheService;
import com.chainway.dispatchercore.common.PropertiesUtil;
import com.chainway.dispatchercore.dto.Driver;
import com.chainway.dispatchercore.dto.JsonResult;
import com.chainway.dispatcherdriverservice.service.DriverInfoService;
import com.chainway.dispatcherservice.service.CommonService;
import com.chainway.dispatcherservice.service.VehicleService;
import com.chainway.driverappweb.annotation.Log;
import com.chainway.driverappweb.annotation.ValidateFiled;
import com.chainway.driverappweb.annotation.ValidateGroup;
import com.chainway.driverappweb.biz.service.LocalService;
import com.chainway.driverappweb.common.AuthUtils;
import com.chainway.driverappweb.common.Constant;
import com.chainway.driverappweb.common.ReturnCodeConstant;
import com.chainway.driverappweb.exception.BizException;

@Controller
@RequestMapping(value="/common")
public class CommonController {
	@Autowired
	private LocalService localService;
	
	@Reference(timeout=60000, check=false)
	private DriverInfoService driverInfoService;
	
	@Reference(timeout=60000, check=false)
	private VehicleService vehicleService;
	
	@Reference(timeout=60000, check=false)
	private CacheService cacheService;
	
	@Reference(timeout=60000, check=false)
	private CommonService commonService;
	/**
	 * 登录
	 * @param driver
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@Log
	@ResponseBody
	@ValidateGroup(fileds= {//校验字段信息
			@ValidateFiled(index=0,notNull=true,filedName="phoneNo",checkMaxLen=true,maxLen=20,checkRegex=true,regStr="phone"),
			@ValidateFiled(index=0,notNull=true,filedName="password")
	})
	@RequestMapping(value="/login.json", method={RequestMethod.GET,RequestMethod.POST})
	public JsonResult login(Driver driver, HttpServletRequest request,HttpServletResponse response) throws Exception {
		//账号是否被锁定
		Integer failTimes=localService.getLoginFailTime(driver.getPhoneNo());
		Integer countLimit=PropertiesUtil.getInteger("fail.count.limit");
		if(failTimes>=countLimit){
			throw new BizException(ReturnCodeConstant.ERROR_LOGIN_USER_LOCKED,"登录用户已经锁住");
		}
		//判断用户是否存在
		Driver driverInDB=driverInfoService.getDriver(driver);
		if(null==driverInDB) {
			//用户不存在
			throw new BizException(ReturnCodeConstant.ERROR_USER_NOTEXIST,"用户不存在");
		}
		
		//校验账号密码
		String password = driver.getPassword();
		String passwordInDB=driverInDB.getPassword();
		if(!password.equals(passwordInDB)){
			throw new BizException(ReturnCodeConstant.ERROR_USER_PASSWORD_WRONG,"用户名密码错误");
		}
		//互踢：删除上一位登录者的缓存
		localService.deleteUserByPreKey(driverInDB);
		
		// TODO 个推通知用户下线
		//通过密码验证，生成token，把用户数据保存在redis缓存中（key=token,value=user），超时时间为30分钟
		String token=localService.addUserInSession(driverInDB);
		
		// 更新司机在线状态
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("onlineStatus", Constant.DRIVER_STATUS_ONLINE);
		param.put("driverId", driverInDB.getDriverId());
		driverInfoService.updateOnlineStatus(param);
		
		//返回数据token，用户基本数据
		Map<String,Object>retUser=new HashMap<String,Object>();
		retUser.put("token", token);
		retUser.put("deptId", driverInDB.getDeptId());
		retUser.put("phoneNo", driverInDB.getPhoneNo());
		retUser.put("driverName", driverInDB.getDriverName());
		//转发携带
		request.setAttribute("loginUser", driverInDB);
		JsonResult result = new JsonResult();
		result.setCode(JsonResult.CODE_SUCCESS);
        result.setData(retUser);
		//登录日志
        //TODO 单独在日志的工程中实现
		return result;
	}
	/**
	 * 退出登录
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="token")
    })
	@Log
	@RequestMapping(value="/logout.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult logout(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		String token=request.getParameter("token");
		Driver user=localService.getUserInSession(token);
		request.setAttribute("loginUser", user);//给记录日志使用
		localService.deleteUserInSession(token);
		// 更新司机在线状态
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("onlineStatus", Constant.DRIVER_STATUS_OFFLINE);
		param.put("driverId", user.getDriverId());
		driverInfoService.updateOnlineStatus(param);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	
	/**
	 * 获取车辆列表
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="token")
    })
	@Log
	@RequestMapping(value="/getCommonVehicles.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult getCommonVehicles(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		String token=request.getParameter("token");
		Driver driver=localService.getUserInSession(token);
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("deptDNA",driver.getDeptDNA());
		List<Map<String, Object>> vehicleList = vehicleService.getCommonVehicles(paramMap);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("list", vehicleList);
		result.setCode(0);
		result.setData(resultMap);
		return result;
	}
	/**
	 * 获取载货类型列表
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/getCarryTypeList.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult getCarryTypeList(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		String key="CarryTypes";
		List<Map<String, Object>>list=null;
		Object obj=cacheService.getData(key);
		if(obj!=null) {
			list=(List<Map<String, Object>>) obj;
		}else {
			list=commonService.getCarryTypeList();
			cacheService.setData(key, list, 6000);
		}
		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("list", list);
		result.setData(ret);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	/**
	 * 获取车型列表
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/getVehicleTypeList.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult getVehicleTypeList(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		String key="VehicleType";
		List<Map<String, Object>>list=null;
		Object obj=cacheService.getData(key);
		if(obj!=null) {
			list=(List<Map<String, Object>>) obj;
		}else {
			list=commonService.getVehicleTypeList();
			cacheService.setData(key, list, 6000);
		}
		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("list", list);
		result.setData(ret);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	/**
	 * 切换在线状态
	 */
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="token"),
            @ValidateFiled(index=0,notNull=true,filedName="onlineStatus"),
    })
	@RequestMapping(value="/changeOnlineStatus.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult changeOnlineStatus(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		String token=request.getParameter("token");
		Driver driver=localService.getUserInSession(token);
		// 更新司机在线状态 ：0为离线 1为在线
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("onlineStatus", request.getParameter("onlineStatus"));
		param.put("driverId", driver.getDriverId());
		driverInfoService.updateOnlineStatus(param);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	
	public static void main(String[] args) {
		String md5Hex = DigestUtils.md5Hex("123456");
		System.out.println(md5Hex);
	}
}
