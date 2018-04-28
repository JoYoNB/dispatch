package com.chainway.dispatcherweb.biz.service;

import java.awt.image.BufferedImage;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.chainway.dispatchercore.dto.User;
import com.chainway.logservice.dto.LogInfo;

public interface LocalService {

	public Map<String,Object>getUser(String token);
	
	public BufferedImage genValidateImage(String token);
	
	public int getLoginFailTime(String account);
	
	public boolean checkValidateCode(String validateCode,String validateCodeToken);
	
	public void lockUser(String account,int currentFailTime);
	
	public String addUserInSession(User user);
	public void updateUserInSession(String token,User user);
	public void deleteUserInSession(String token);
	public User getUserInSession(String token);
	public User getUserInSession(HttpServletRequest request);
	public void refreshUserInSession(String token);
	public void addLog(LogInfo logInfo);
}
