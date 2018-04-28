package com.chainway.dispatcherservice.service;

import java.util.List;
import java.util.Map;

import com.chainway.dispatchercore.dto.Vehicle;

import sun.security.util.PropertyExpander.ExpandException;


/**
 * @Author: chainway
 *
 * @Date: 2018年3月19日
 * @Description:车辆sevice接口
 */
public interface VehicleService {
	/**
	 * 新增车辆
	 * @param param
	 * @return
	 */
	public String add(Map<String, Object> paramMap) throws Exception;
	/**
	 * 查询单辆车辆
	 * @param vhicleInfo
	 * @return
	 */
	public Map<String, Object> getVehicle(Map<String, Object> paramMap);
	/**
	 * 查询车辆列表
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> getVehicleList(Map<String, Object> paramMap);
	/**
	 * 查询车辆列表总数
	 * @param paramMap
	 * @return
	 */
	public Integer getVehicleListCount(Map<String, Object> paramMap);
	/**
	 * 修改
	 * @param terInfo
	 * @return
	 */
	public void update(Vehicle vehicle) throws Exception;
	/**
	 * 删除
	 * @param terInfo
	 * @return
	 */
	public Boolean delete(Integer vehicleId);
	/**
	 * 下拉显示车辆列表
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getCommonVehicles(Map<String, Object> paramMap);
}
