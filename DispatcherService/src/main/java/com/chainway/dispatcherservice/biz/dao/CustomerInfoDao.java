package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;

import com.chainway.dispatcherservice.dto.CustomerInfo;

public interface CustomerInfoDao {
	/**
	 * 新增客户信息
	 * @param customerInfo
	 * @return
	 */
	public int addCustomerInfo(CustomerInfo customerInfo);
	
	/**
	 * 修改客户信息表
	 * @param orderRule
	 */
	public void updateCustomerInfo(CustomerInfo customerInfo);
	/**
	 * 删除客户-货物类型关系
	 * @param orderRule
	 */
	public void deleteCustomerGoodsType(CustomerInfo customerInfo);
	/**
	 * 删除客户-地区关系
	 * @param orderRule
	 */
	public void deleteCustomerArea(CustomerInfo customerInfo);
	/**
	 * 写入客户-货物类型关系
	 * @param param
	 */
	public void addCustomerGoodsType(CustomerInfo customerInfo);
	/**
	 * 写入客户-地区关系
	 * @param param
	 */
	public void addCustomerArea(CustomerInfo customerInfo);
	/**
	 * 查询列表
	 * @param orderRule
	 * @return
	 */
	public List<CustomerInfo> getList(Map<String, Object> param);
	/**
	 * 查询数目
	 * @param param
	 * @return
	 */
	public int getListCount(Map<String,Object>param);
	/**
	 * 查询详情
	 * @param userId
	 * @return
	 */
	public CustomerInfo getInfo(int id);
}
