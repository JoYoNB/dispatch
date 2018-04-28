package com.chainway.dispatcherservice.service;

import java.util.List;
import java.util.Map;

import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherservice.dto.CustomerInfo;

/**
 * 客户资料管理
 * @author chainwayits
 * @date 2018年3月20日
 */
public interface CustomerInfoService {
	/**
	 * 修改
	 * @param orderRule
	 * @throws ServiceException 
	 */
	public void add(CustomerInfo customerInfo) throws ServiceException;
	/**
	 * 修改
	 * @param orderRule
	 * @throws ServiceException 
	 */
	public void update(CustomerInfo customerInfo) throws ServiceException;
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
	public CustomerInfo getInfo(int userId);
	/**
	 * 删除
	 * @param id
	 * @throws ServiceException 
	 */
	public void delete(CustomerInfo customerInfo) throws ServiceException;
}
