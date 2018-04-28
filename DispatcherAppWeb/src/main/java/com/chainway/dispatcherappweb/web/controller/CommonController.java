package com.chainway.dispatcherappweb.web.controller;

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
import com.chainway.cacheService.biz.service.CacheService;
import com.chainway.dispatcherappweb.annotation.Log;
import com.chainway.dispatcherappweb.annotation.ValidateFiled;
import com.chainway.dispatcherappweb.annotation.ValidateGroup;
import com.chainway.dispatcherappweb.biz.service.LocalService;
import com.chainway.dispatcherappweb.exception.BizException;
import com.chainway.dispatchercore.common.PropertiesUtil;
import com.chainway.dispatchercore.dto.Dept;
import com.chainway.dispatchercore.dto.JsonResult;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatchercore.excetion.ExceptionCode;
import com.chainway.dispatcherservice.service.CommonService;
import com.chainway.dispatcherservice.service.ConsignorService;
import com.chainway.dispatcherservice.service.UserService;
import com.chainway.dispatcherservice.service.carrier.CarrierStatsService;


@Controller
@RequestMapping(value="/common")
public class CommonController {
	
	@Autowired
	private LocalService localService;
	
	@Reference(timeout=60000, check=false)
	private UserService userService;
	
	@Reference(timeout=60000, check=false)
	private CacheService cacheService;
	
	@Reference(timeout=60000, check=false)
	private CommonService commonService;
	
	@Reference(timeout=60000, check=false)
	private CarrierStatsService carrierStatsService;

	@Reference(timeout=60000, check=false)
	private ConsignorService consignorService;
	
	@Log
	@ResponseBody
	@ValidateGroup(fileds = {//校验字段信息
	        @ValidateFiled(index=1,notNull=true,filedName="account"),
	        @ValidateFiled(index=1,notNull=true,filedName="password")
	})
	@RequestMapping(value="/login.json", method={RequestMethod.GET,RequestMethod.POST})
	public JsonResult login(User user,HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		String account=request.getParameter("account");
		String password=request.getParameter("password");
		//先看是否锁定了账号
		Integer failTimes=localService.getLoginFailTime(account);
		Integer countLimit=PropertiesUtil.getInteger("fail.count.limit");
		if(failTimes>=countLimit){
			//已经锁住了账号
			throw new BizException(ExceptionCode.ERROR_LOGIN_USER_LOCKED,"登录用户已经锁住");
		}
		
		User userInDB=new User();
		userInDB.setAccount(account);
		userInDB.setStatus(1);
		userInDB=userService.getUser(userInDB);
		if(userInDB==null){
			//用户不存在
			throw new BizException(ExceptionCode.ERROR_USER_PASSWORD_WRONG,"用户名密码错误");
		}
		//对比用户密码
		String passwordInDB=userInDB.getPassword();
		if(!password.equals(passwordInDB)){
			//密码不对
			throw new BizException(ExceptionCode.ERROR_USER_PASSWORD_WRONG,"用户名密码错误");
		}
		//互踢：删除上一位登录者的缓存
		localService.deleteUserByPreKey(userInDB);
		
		// TODO 个推通知用户下线
		//通过密码验证，生成token，把用户数据保存在redis缓存中（key=token,value=user），设置为永不过期
		String token=localService.addUserInSession(userInDB);
		
		//返回数据token，用户基本数据（用户名，用户Id，用户角色编码，用户操作权限编码）
		Map<String,Object>retUser=new HashMap<String,Object>();
		retUser.put("token", token);
		retUser.put("id", userInDB.getId());
		retUser.put("account", userInDB.getAccount());
		retUser.put("name", userInDB.getName());
		retUser.put("role", userInDB.getRole());
		retUser.put("roleCode", userInDB.getRoleCode());
		//因为登录的时候，其实是没有token在request的param中的，所以要先设置进request的attribute中
		request.setAttribute("loginUser", userInDB);
		result.setCode(JsonResult.CODE_SUCCESS);
        result.setData(retUser);
        return result;
	}
	
	
	@Log
	@ResponseBody
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="token")
    })
	@RequestMapping(value="/logout.json", method={RequestMethod.GET,RequestMethod.POST})
	public JsonResult logout(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		String token=request.getParameter("token");
		User user=localService.getUserInSession(token);
		request.setAttribute("loginUser", user);//给记录日志使用
		localService.deleteUserInSession(token);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	/**
	 * 新增计价规则
	 * @param chargeRule
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@ValidateGroup(fileds = {//校验字段信息
			 @ValidateFiled(index=0,notNull=true,filedName="feedback",maxLen=300,checkMaxLen=true),
    })
	@Log
	@RequestMapping(value="/addFeedback.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult addFeedback(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		String feedback=request.getParameter("feedback");
		String phone=request.getParameter("phone");
		String email=request.getParameter("email");
		Map<String, Object>param=new HashMap<>();
		param.put("feedback", feedback);
		param.put("phone", phone);
		param.put("email", email);
		param.put("creater", user.getId());
		commonService.addFeedback(param);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@RequestMapping(value="/myInfo.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult myInfo(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		result.setData(commonService.myInfo(user));
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@ResponseBody
	@RequestMapping(value="getCarrierOrderTotal.json",method= {RequestMethod.POST,RequestMethod.GET})
	public JsonResult getCarrierOrderTotal(HttpServletRequest request,HttpServletResponse response) {
		JsonResult result = new JsonResult();	
		// 接收参数
		User user = localService.getUserInSession(request);
		String deptDNA = user.getDeptDNA();
		Map<String, Object> paramMap = new HashMap<>();
		// 封装参数
		paramMap.put("deptDNA", deptDNA);
		int totalOrder = carrierStatsService.getTotalFinishedOrderNum(paramMap);
		// 返回结果
		HashMap<String, Object> data = new HashMap<>();
		data.put("totalOrder", totalOrder);
		result.setData(data);
		result.setCode(JsonResult.CODE_SUCCESS);
		return result;
	}
	
	@RequestMapping("/getConsignorOrderTotal.json")
	@ResponseBody
	public JsonResult getConsignorOrderTotal(HttpServletRequest request, JsonResult result) throws Exception {
		User user=localService.getUserInSession(request);
		Map<String, Object> map = new HashMap<>();
		map.put("deptDNA", user.getDeptDNA());
		result.setData(consignorService.totalStatistics(map));
		result.setCode(JsonResult.CODE_SUCCESS);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/aboutUs.json")
	@ResponseBody
	public JsonResult aboutUs(HttpServletRequest request, JsonResult result) throws Exception {
		String key="aboutUs";
		Object obj=cacheService.getData(key);
		Map<String, Object> map=null;
		if(obj!=null) {
			map=(Map<String, Object>) obj;
		}else {
			map=commonService.aboutUs();
			cacheService.setData(key, map, 6000);
		}
		result.setData(map);
		result.setCode(JsonResult.CODE_SUCCESS);
		return result;
	}
	
	@RequestMapping(value="/getDeptList.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult getDeptList(HttpServletRequest request, JsonResult result) throws Exception {
		User user=localService.getUserInSession(request);
		//查询用户管辖部门所有的部门信息
		Map<String,Object>param=new HashMap<String,Object>();
		param.put("dna", user.getDeptDNA());
		List<Dept>list=userService.getDeptList(param);
		result.setData(list);
		result.setCode(JsonResult.CODE_SUCCESS);
		return result;
	}
}
