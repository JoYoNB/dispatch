package com.chainway.dispatcherweb.web.controller;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chainway.cacheService.biz.service.CacheService;
import com.chainway.dispatchercore.common.PropertiesUtil;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatchercore.excetion.ExceptionCode;
import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherservice.service.CommonService;
import com.chainway.dispatcherservice.service.UserService;
import com.chainway.dispatcherweb.annotation.Log;
import com.chainway.dispatcherweb.annotation.ValidateFiled;
import com.chainway.dispatcherweb.annotation.ValidateGroup;
import com.chainway.dispatcherweb.biz.service.LocalService;
import com.chainway.dispatcherweb.common.Constant;
import com.chainway.dispatcherweb.common.ReturnCodeConstant;
import com.chainway.dispatcherweb.dto.JsonResult;
import com.chainway.dispatcherweb.exception.BizException;

@Controller
@RequestMapping("/common")
public class CommonController {

	protected final Logger log=Logger.getLogger(this.getClass());
	
	@Autowired
	private LocalService localService;
	
	@Reference(timeout=60000, check=false)
	private UserService userService;
	
	@Reference(timeout=60000, check=false)
	private CacheService cacheService;
	
	@Reference(timeout=60000, check=false)
	private CommonService commonService;
	
	@RequestMapping(value = "/captcha.json", method = RequestMethod.GET)
	public void captcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// Set to expire far in the past.
		response.setDateHeader("Expires", 0);
		// Set standard HTTP/1.1 no-cache headers.
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		// Set standard HTTP/1.0 no-cache header.
		response.setHeader("Pragma", "no-cache");
		// return a jpeg
		response.setContentType("image/jpeg");
		// create the text for the image
		ServletOutputStream out = response.getOutputStream();
		// write the data out
		String token=request.getParameter("r");
		BufferedImage bi=localService.genValidateImage(token);
		ImageIO.write(bi, "jpg", out);
		try {
			out.flush();
		} finally {
			out.close();
		}
	}
	
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=1,notNull=true,filedName="account"),
            @ValidateFiled(index=1,notNull=true,filedName="randomCode"),
            @ValidateFiled(index=1,notNull=true,filedName="validateCode" ,maxLen=6,checkMaxLen=true,minLen=4,checkMinLen=true),
            @ValidateFiled(index=1,notNull=true,filedName="password")
    })
	@Log
	@RequestMapping(value="/login.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult login(User user,HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		//先校验验证码
		String account=request.getParameter("account");
		String password=request.getParameter("password");
		//先看是否锁定了账号
		Integer failTimes=localService.getLoginFailTime(account);
		Integer countLimit=PropertiesUtil.getInteger("fail.count.limit");
		if(failTimes>=countLimit){
			//已经锁住了账号
			throw new BizException(ReturnCodeConstant.ERROR_LOGIN_USER_LOCKED,"登录用户已经锁住");
		}
		//校验验证码
		String validateCode=request.getParameter("validateCode");
		String randomCode=request.getParameter("randomCode");
		if(!localService.checkValidateCode(validateCode, randomCode)){
			localService.lockUser(account, failTimes);
			throw new BizException(ReturnCodeConstant.ERROR_LOGIN_VALIDATE_ERROR,"验证码错误");
		}
		//验证完，马上失效验证码
		cacheService.deleteData(Constant.VALIDATE_IMAGE_CACHE_KEY+randomCode);
		
		User userInDB=new User();
		userInDB.setAccount(account);
		userInDB.setStatus(1);
		userInDB=userService.getUser(userInDB);
		if(userInDB==null){
			//用户不存在
			throw new BizException(ReturnCodeConstant.ERROR_USER_PASSWORD_WRONG,"用户名密码错误");
		}
		//对比用户密码
		String passwordInDB=userInDB.getPassword();
		if(!password.equals(passwordInDB)){
			//密码不对
			throw new BizException(ReturnCodeConstant.ERROR_USER_PASSWORD_WRONG,"用户名密码错误");
		}
		
		//通过密码验证，生成token，把用户数据保存在redis缓存中（key=token,value=user），超时时间为30分钟
		String token=localService.addUserInSession(userInDB);
		/*User userInSession=localService.getUserInSession(token);
		System.out.println(userInSession);
		if(userInSession!=null) System.out.println(userInSession.getAccount());*/
		
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
	
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="token")
    })
	@Log
	@RequestMapping(value="/logout.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult logout(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		String token=request.getParameter("token");
		User user=localService.getUserInSession(token);
		request.setAttribute("loginUser", user);//给记录日志使用
		localService.deleteUserInSession(token);
		
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@RequestMapping(value="/unauthorized.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult unauthorized(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		result.setCode(ReturnCodeConstant.ERROR_UNAUTHORIZED);
		return result;
	}
	
	@RequestMapping(value="/getUser.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult getUser(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		User userInDB=new User();
		userInDB.setId(user.getId());
		userInDB=userService.getUser(userInDB);
		//清除明感信息
		userInDB.setRole(null);
		userInDB.setPassword(null);
		result.setData(userInDB);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@ValidateGroup(fileds = {//校验字段信息
			@ValidateFiled(index=0,filedName="name",checkMaxLen=true,maxLen=100),
            @ValidateFiled(index=0,filedName="email",checkMaxLen=true,maxLen=100,checkRegex=true,regStr="email"),
            @ValidateFiled(index=0,filedName="phone",checkMaxLen=true,maxLen=20,checkRegex=true,regStr="phone"),
            @ValidateFiled(index=0,filedName="gmtZone",checkMaxLen=true,maxLen=20)
    })
	@Log
	@RequestMapping(value="/updateUser.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult updateUser(User u,HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		u.setId(user.getId());
		u.setUpdater(user.getId());
		userService.updateUser(u);
		//修改完后，更新缓存信息
		boolean needUpdate=false;
		User retUser=new User();
		if(StringUtils.isNotEmpty(u.getName())&&!user.getName().equals(u.getName())){
			user.setName(u.getName());
			needUpdate=true;
			retUser.setName(u.getName());
		}
		if(StringUtils.isNotEmpty(u.getGmtZone())&&!user.getGmtZone().equals(u.getGmtZone())){
			user.setGmtZone(u.getGmtZone());
			needUpdate=true;
		}
		if(needUpdate){
			//更新缓存
			String token=request.getParameter("token");
			localService.updateUserInSession(token, user);
		}
		result.setData(retUser);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	/**
	 * 获取省市区树
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/getAreaByParentId.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult getAreaByParentId(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		String parent=request.getParameter("parent");
		String key="Area-"+parent;
		List<Map<String, Object>>list=null;
		Object obj=cacheService.getData(key);
		if(obj!=null) {
			list=(List<Map<String, Object>>) obj;
		}else {
			list=commonService.getAreaByParentId(parent);
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
	
	@ValidateGroup(fileds = {//校验字段信息
			@ValidateFiled(index=0,notNull=true,filedName="password")
    })
	@Log
	@RequestMapping(value="/updateUserPassword.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult updateUserPassword(User u,HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		//和当前密码比较
		User userInDB=new User();
		userInDB.setId(user.getId());
		userInDB=userService.getUser(userInDB);
		if(userInDB==null){
			throw new ServiceException(ExceptionCode.ERROR_USER_NOTEXIST,"用户信息错误");
		}
		//判断新密码和旧密码是否一致
		if(u.getPassword().equals(userInDB.getPassword())){
			throw new ServiceException(ExceptionCode.ERROR_NEWPASSWORD_SAME_WITH_OLDPASSWORD,"新密码不能和旧密码一致");
		}
		User updateUser=new User();
		updateUser.setId(user.getId());
		updateUser.setUpdater(user.getId());
		updateUser.setPassword(u.getPassword());
		userService.updateUser(updateUser);
		//删除用户缓存信息
		String token=request.getParameter("token");
		request.setAttribute("loginUser", user);//给记录日志使用
		localService.deleteUserInSession(token);
		
		result.setCode(JsonResult.CODE_SUCCESS);
		return result;
	}
	/**
	 * 获取货物类型列表
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/getGoodsTypeList.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult getGoodsTypeList(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		String key="GoodsType";
		List<Map<String, Object>>list=null;
		Object obj=cacheService.getData(key);
		if(obj!=null) {
			list=(List<Map<String, Object>>) obj;
		}else {
			list=commonService.getGoodsTypeList();
			cacheService.setData(key, list, 6000);
		}
		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("list", list);
		result.setData(ret);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	/**
	 * 获取车辆载货类型列表
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
}
