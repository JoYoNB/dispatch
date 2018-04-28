package com.chainway.dispatcherdriverservice.service;

import java.util.Map;

import com.chainway.dispatchercore.dto.Driver;

/**
 * @Author: chainway
 *
 * @Date: 2018年3月27日
 * @Description:司机个人信息
 */
public interface DriverInfoService {
	/**
	 * 查询司机信息
	 * @param param
	 * @return
	 */
	public Driver getDriver(Driver driver);
	/**
	 * 查询司机信息及相关信息
	 * @param param
	 * @return
	 */
	public Map<String, Object> getDriverInfo(Driver driver);
	/**
	 * 查询司机统计信息
	 * @param param
	 * @return
	 */
	public Map<String, Object> getDriverStatis(Driver driver);
	/**
	 * 查询车辆信息
	 * @param param
	 * @return
	 */
	public Map<String, Object> getVehicleInfo(Driver driver);
	/**
	 * 更新司机在线状态
	 * @param param
	 */
	public void updateOnlineStatus(Map<String, Object>param)
;}
