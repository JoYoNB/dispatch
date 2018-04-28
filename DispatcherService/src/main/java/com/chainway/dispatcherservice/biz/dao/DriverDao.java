package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;

import com.chainway.dispatchercore.dto.Driver;


public interface DriverDao {
	/**
	 * 新增司机
	 * @param param
	 * @return
	 */
	public Integer add(Driver driver) throws Exception;
	/**
	 * 查询单个司机
	 * @param vhicleInfo
	 * @return
	 */
	public Driver getDriver(Map<String, Object> paramMap);
	/**
	 * 查询司机列表
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> getDriverList(Map<String, Object> paramMap);
	/**
	 * 查询司机列表总数
	 * @param paramMap
	 * @return
	 */
	public Integer getDriverListCount(Map<String, Object> paramMap);
	/**
	 * 修改
	 * @param terInfo
	 * @return
	 */
	public Integer update(Driver driver) throws Exception;
	/**
	 * 删除
	 * @param terInfo
	 * @return
	 */
	public Integer delete(Map<String, Object> paramMap);
}
