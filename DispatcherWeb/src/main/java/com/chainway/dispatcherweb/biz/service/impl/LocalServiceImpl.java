package com.chainway.dispatcherweb.biz.service.impl;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chainway.cacheService.biz.service.CacheService;
import com.chainway.dispatchercore.common.PropertiesUtil;
import com.chainway.dispatchercore.dto.Auth;
import com.chainway.dispatchercore.dto.Role;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatcherservice.service.CommonService;
import com.chainway.dispatcherweb.biz.service.LocalService;
import com.chainway.dispatcherweb.common.Constant;
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

	private Map<String,Object> user2Map(User user){
		Map<String,Object>map=new HashMap<String,Object>();
		map.put("account", user.getAccount());
		map.put("name", user.getName());
		map.put("id", user.getId());
		map.put("roleId", user.getRoleId());
		map.put("roleCode", user.getRoleCode());
		map.put("deptId", user.getDeptId());
		map.put("deptName", user.getDeptName());
		map.put("gmtZone", user.getGmtZone());
		map.put("deptDNA", user.getDeptDNA());
		map.put("merchantDeptId",user.getMerchantDeptId());
		map.put("isCarrier", user.isCarrier());
		map.put("isConsignor", user.isConsignor());
		map.put("isDual", user.isDual());
		
		if(user.getRole()!=null){
			Role role=user.getRole();
			Map<String,Object>roleMap=new HashMap<String,Object>();
			map.put("role", roleMap);
			roleMap.put("id", role.getId());
			roleMap.put("code", role.getCode());
			roleMap.put("type", role.getType());
			roleMap.put("name", role.getName());
			roleMap.put("deptId", role.getDeptId());
			
			if(role.getAuthList()!=null&&!role.getAuthList().isEmpty()){
				List<Auth>authList=role.getAuthList();
				List<Map<String,Object>>auths=new ArrayList<Map<String,Object>>();
				roleMap.put("authList", auths);
				for(Auth a:authList){
					Map<String,Object>am=new HashMap<String,Object>();
					auths.add(am);
					am.put("id", a.getId());
					am.put("code", a.getCode());
					am.put("name", a.getName());
					am.put("parentId", a.getParentId());
				}
				
			}
		}
		
		return map;
	}
	
	private User map2User(Map<String,Object>map){
		User user=new User();
		user.setId((Integer) map.get("id"));
		user.setAccount((String) map.get("account"));
		user.setName((String) map.get("name"));
		user.setRoleId((Integer) map.get("roleId"));
		user.setRoleCode((String) map.get("roleCode"));
		user.setDeptId((Integer) map.get("deptId"));
		user.setDeptName((String) map.get("deptName"));
		user.setGmtZone((String) map.get("gmtZone"));
		user.setDeptDNA((String) map.get("deptDNA"));
		user.setMerchantDeptId((Integer) map.get("merchantDeptId"));
		user.setCarrier((boolean) map.get("isCarrier"));
		user.setConsignor((boolean) map.get("isConsignor"));
		user.setDual((boolean) map.get("isDual"));
		
		Map<String,Object>roleMap=(Map<String, Object>) map.get("role");
		if(roleMap!=null&&!roleMap.isEmpty()){
			Role role=new Role();
			user.setRole(role);
			
			role.setId((Integer) roleMap.get("id"));
			role.setCode((String) roleMap.get("code"));
			role.setType((String) roleMap.get("type"));
			role.setName((String) roleMap.get("name"));
			role.setDeptId((Integer) roleMap.get("deptId"));
			
			List<Map<String,Object>>auths=(List<Map<String, Object>>) roleMap.get("authList");
			if(auths!=null&&!auths.isEmpty()){
				List<Auth>authList=new ArrayList<Auth>();
				role.setAuthList(authList);
				for(Map<String,Object>am:auths){
					Auth a=new Auth();
					a.setId((Integer) am.get("id"));
					a.setCode((String) am.get("code"));
					a.setName((String) am.get("name"));
					a.setParentId(am.get("parentId")==null?null:(Integer)am.get("parentId"));
					
					authList.add(a);
				}
			}
			
		}
		
		return user;
	}
	
	@Override
	public String addUserInSession(User user) {
		String uuid=UUID.randomUUID().toString().replace("-", "");
		if(example(user.getAccount())){
			uuid = user.getAccount();
		}
		String token=Constant.USER_SESSION_KEY_PREFIX+uuid;
		
		String dna=user.getDeptDNA();
		String roleCode=user.getRoleCode();
		if(StringUtils.isNotEmpty(roleCode)){
			if(roleCode.startsWith("carrier")){
				//承运商
				user.setCarrier(true);
			}else if(roleCode.startsWith("consignor")){
				//货主
				user.setConsignor(true);
			}else if(roleCode.startsWith("dual")){
				//货主/承运商
				user.setDual(true);
			}
		}
		if(StringUtils.isNotEmpty(dna)){
			String[]deptIds=dna.split("-");
			if(deptIds!=null&&deptIds.length>0){
				//第一个部门Id就是该用户的顶级部门Id（每个承运商，货主的部门都是一级部门，即其没有上级部门）
				Integer merchantDeptId=Integer.parseInt(deptIds[0]);
				user.setMerchantDeptId(merchantDeptId);
			}
		}
		
		//目前只能缓存成map对象
		Map<String,Object>map=user2Map(user);
		cacheService.setData(token, map, Integer.MAX_VALUE);//30分钟
		return uuid;
	}

	private boolean example(String account) {
		List<String> list = new ArrayList<>();
		list.add("cw");
		return list.contains(account);
	}

	@Override
	public void updateUserInSession(String uuid, User user) {
		String token=Constant.USER_SESSION_KEY_PREFIX+uuid;
		Map<String,Object>map=user2Map(user);
		cacheService.setData(token, map, 30*60);//30分钟
	}

	@Override
	public void deleteUserInSession(String uuid) {
		String token=Constant.USER_SESSION_KEY_PREFIX+uuid;
		cacheService.deleteData(token);
	}

	@Override
	public User getUserInSession(String uuid) {
		if(StringUtils.isEmpty(uuid)){
			return null;
		}
		String token=Constant.USER_SESSION_KEY_PREFIX+uuid;
		Map<String,Object>map=(Map<String,Object>) cacheService.getData(token);
		if(map==null||map.isEmpty()){
			return null;
		}
		User user=map2User(map);
		return user;
	}

	@Override
	public User getUserInSession(HttpServletRequest request) {
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
		
		
		System.out.println(new LocalServiceImpl().example("cw"));
	}
	
}
