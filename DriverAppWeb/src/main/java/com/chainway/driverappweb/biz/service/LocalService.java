package com.chainway.driverappweb.biz.service;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.chainway.dispatchercore.dto.Driver;
import com.chainway.logservice.dto.LogInfo;

public interface LocalService {

	public Map<String,Object>getUser(String token);
	
	public BufferedImage genValidateImage(String token);
	
	public int getLoginFailTime(String account);
	
	public boolean checkValidateCode(String validateCode,String validateCodeToken);
	
	public void lockUser(String account,int currentFailTime);
	
	public void deleteUserByPreKey(Driver driver);
	public String addUserInSession(Driver driver);
	public void updateUserInSession(String token,Driver driver);
	public void deleteUserInSession(String token);
	public Driver getUserInSession(String token);
	public Driver getUserInSession(HttpServletRequest request);
	public void refreshUserInSession(String token);
	public void addLog(LogInfo logInfo);
	public Set<String> getKeysByPrex(String phoneNo);
	public void setData(String key,Object value,Integer expirtTime);
}
