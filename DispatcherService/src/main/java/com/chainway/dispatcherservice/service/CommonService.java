package com.chainway.dispatcherservice.service;

import java.util.List;
import java.util.Map;

import com.chainway.dispatchercore.dto.User;

public interface CommonService {

	public Map<String,Object>test(Map<String,Object>param);
	/**
	 * 获取地区
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> getAreaByParentId(String parent); 
	/**
	 * 获取车型
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> getVehicleTypeList(); 
	/**
	 * 获取货物类型
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> getGoodsTypeList(); 
	
	/**
	 * 获取车辆载货类型
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> getCarryTypeList();
	
	/**
	 * 新增意见反馈
	 * @param param
	 */
	public void addFeedback(Map<String, Object> param);
	
	/**
	 * 个人信息-APP
	 * @param param
	 */
	public Map<String, Object> myInfo(User user);

	/**
	 * 关于我们
	 * @param param
	 */
	public Map<String, Object> aboutUs();
}
