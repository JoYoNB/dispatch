package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;

import com.chainway.dispatchercore.dto.Vehicle;




public interface VehicleDao {
	/**
	 * 新增车辆
	 * @param param
	 * @return
	 */
	public int add(Vehicle vehicle);
	/**
	 * 查询单辆车辆
	 * @param paramMap
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
	public int update(Vehicle vehicle);
	/**
	 * 删除
	 * @param terInfo
	 * @return
	 */
	public int delete(Integer vehicleId);
	/**
	 * 新增车辆与载货类型关系
	 */
	public void addVehicleCarryTypeRel(Map<String, Object> paramMap);
	/**
	 * 删除车辆与载货类型关系
	 * @param paramMap
	 */
	public void deleteVehicleCarryTypeRel(Integer vehicleId);
	/**
	 * 下拉显示车辆列表
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> getCommonVehicles(Map<String, Object> paramMap);
}
