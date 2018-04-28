package com.chainway.driverappweb.biz.service.impl;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chainway.cacheService.biz.service.CacheService;
import com.chainway.dispatchercore.common.PropertiesUtil;
import com.chainway.dispatchercore.dto.Driver;
import com.chainway.dispatcherdriverservice.service.CommonService;
import com.chainway.driverappweb.biz.service.LocalService;
import com.chainway.driverappweb.common.AuthUtils;
import com.chainway.driverappweb.common.Constant;
import com.chainway.logservice.dto.LogInfo;
import com.chainway.logservice.service.LogService;
import com.google.code.kaptcha.impl.DefaultKaptcha;

@Service
public class LocalServiceImpl implements LocalService {

	@Reference(timeout=60000, check=false)
	private CommonService commonService;
	@Reference(timeout=60000, check=false)
	private CacheService cacheService;
	@Reference(timeout=60000, check=false)
	private LogService logService;
	
	@Autowired
	private DefaultKaptcha captchaProducer;
	
	@Override
	public Map<String, Object> getUser(String token) {
		System.out.println("commonService"+commonService);
		return null;
	}

	@Override
	public BufferedImage genValidateImage(String token) {
		String capText=captchaProducer.createText();
		String key=Constant.VALIDATE_IMAGE_CACHE_KEY+token;
		//放进缓存
		cacheService.setStringData(key, capText, 2*60);//2分钟失效
		BufferedImage bi=captchaProducer.createImage(capText);
		return bi;
	}

	@Override
	public int getLoginFailTime(String account) {
		//Integer t=1;
		//cacheService.setData(account, t, 1*60);
		Integer c=(Integer) cacheService.getData(Constant.LOCK_USER_KEY_PREFIX+account);
		if(c==null){
			return 0;
		}
		return c.intValue();
	}

	@Override
	public boolean checkValidateCode(String validateCode, String validateCodeToken) {
		String key=Constant.VALIDATE_IMAGE_CACHE_KEY+validateCodeToken;
		String capText=cacheService.getStringData(key);
		if(StringUtils.isEmpty(capText)){
			//验证码失效
			return false;
		}
		//删除验证码，防止验证码二次使用
		cacheService.deleteData(key);
		if(capText.equals(validateCode)){
			//验证码正确
			return true;
		}
		return false;
	}

	@Override
	public void lockUser(String account, int currentFailTime) {
		Integer c=currentFailTime+1;
		Integer lockTime=PropertiesUtil.getInteger("fail.lock.time");
		cacheService.setData(Constant.LOCK_USER_KEY_PREFIX+account, c, lockTime*60);
	}

	private Map<String, Object> driver2Map(Driver driver){
		Map<String,Object>map=new HashMap<String,Object>();
		map.put("driverId", driver.getDriverId());
		map.put("driverName", driver.getDriverName());
		map.put("password", driver.getPassword());
		map.put("gmtZone", driver.getGmtZone());
		map.put("vehicleId", driver.getVehicleId());
		map.put("phoneNo", driver.getPhoneNo());
		map.put("deptId", driver.getDeptId());
		map.put("status", driver.getStatus());
		map.put("entryTime", driver.getEntryTime());
		map.put("createTime", driver.getCreateTime());
		map.put("updateTime", driver.getUpdateTime());
		map.put("onlineStatus", driver.getOnlineStatus());
		map.put("deptDNA", driver.getDeptDNA());
		return map;
	}
	private Driver map2Driver(Map<String,Object>map){
		Driver driver=new Driver();
		driver.setDriverId((Integer)map.get("driverId"));
		driver.setDriverName((String)map.get("driverName"));
		driver.setPassword(null);
		driver.setGmtZone((String)map.get("gmtZone"));
		driver.setVehicleId((Integer)map.get("vehicleId"));
		driver.setPhoneNo((String)map.get("phoneNo"));
		driver.setDeptId((Integer)map.get("deptId"));
		driver.setStatus((Integer)map.get("status"));
		driver.setEntryTime((Date)map.get("entryTime"));
		driver.setCreateTime((Date)map.get("createTime"));
		driver.setUpdateTime((Date)map.get("updateTime"));
		driver.setOnlineStatus((Integer)map.get("onlineStatus"));
		driver.setDeptDNA((String)map.get("deptDNA"));
		return driver;
	}
	
	@Override
	public String addUserInSession(Driver driver) {
		String uuid=UUID.randomUUID().toString().replace("-", "");
		String phone= AuthUtils.MD5(driver.getPhoneNo());
		uuid=phone+uuid;
		String token=Constant.USER_SESSION_KEY_PREFIX+uuid;
		//目前只能缓存成map对象
		Map<String,Object>map=driver2Map(driver);
		cacheService.setData(token, map, -1);//-1：永久有效
		return uuid;
	}

	@Override
	public void updateUserInSession(String uuid,Driver driver) {
		String token=Constant.USER_SESSION_KEY_PREFIX+uuid;
		Map<String,Object>map=driver2Map(driver);
		cacheService.setData(token, map, 30*60);//30分钟
	}

	@Override
	public void deleteUserInSession(String uuid) {
		String token=Constant.USER_SESSION_KEY_PREFIX+uuid;
		cacheService.deleteData(token);
	}

	@Override
	public Driver getUserInSession(String uuid) {
		if(StringUtils.isEmpty(uuid)){
			return null;
		}
		String token=Constant.USER_SESSION_KEY_PREFIX+uuid;
		Map<String,Object>map=(Map<String,Object>) cacheService.getData(token);
		if(map==null||map.isEmpty()){
			return null;
		}
		Driver driver=map2Driver(map);
		return driver;
	}

	@Override
	public Driver getUserInSession(HttpServletRequest request) {
		String uuid=request.getParameter("token");
		return getUserInSession(uuid);
	}

	@Override
	public void refreshUserInSession(String uuid) {
		String token=Constant.USER_SESSION_KEY_PREFIX+uuid;
		//先拿到缓存
		Map<String,Object>user=(Map<String, Object>) cacheService.getData(token);
		//再更新缓存
		cacheService.setData(token, user, 30*60);//30分钟
	}

	@Override
	public void addLog(LogInfo logInfo) {
		logService.addLog(logInfo);
	}

	
	public static void main(String[]args){
		String s="1-2-";
		String[]ss=s.split("-");
		System.out.println(ss.length);
	}

	@Override
	public void deleteUserByPreKey(Driver driver) {
		String phoneEncrypt= AuthUtils.MD5(driver.getPhoneNo());
		String preToken=Constant.USER_SESSION_KEY_PREFIX+phoneEncrypt;
		cacheService.deleteByPrex(preToken);
	}

	@Override
	public Set<String> getKeysByPrex(String phoneNo) {
		String phoneEncrypt= AuthUtils.MD5(phoneNo);
		String preToken=Constant.USER_SESSION_KEY_PREFIX+phoneEncrypt;
		return cacheService.getKeysByPrex(preToken);
	}

	@Override
	public void setData(String key, Object value, Integer expirtTime) {
		Map<String,Object>user=	new HashMap<>();
		user.put("status", value);
		cacheService.setData(key, user, expirtTime);
	}
	
}
