package com.chainway.dispatcherservice.service;

import java.util.List;
import java.util.Map;

import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherservice.dto.ChargeRule;

/**
 * 订单匹配规则管理
 * @author chainwayits
 * @date 2018年3月20日
 */
public interface ChargeRuleService {
	/**
	 * 新增
	 * @param chargeRule
	 * @throws ServiceException 
	 */
	public void add(ChargeRule chargeRule) throws ServiceException;
	/**
	 * 删除
	 * @param param
	 */
	public void delete(int id);
	/**
	 * 修改
	 * @param chargeRule
	 * @throws ServiceException 
	 */
	public void update(ChargeRule chargeRule) throws ServiceException;
	/**
	 * 查询列表
	 * @param chargeRule
	 * @return
	 */
	public List<ChargeRule> getList(Map<String, Object> param); 
	/**
	 * 查询数目
	 * @param param
	 * @return
	 */
	public int getListCount(Map<String,Object>param);
	/**
	 * 查询详情
	 * @param chargeRule
	 * @return
	 */
	public ChargeRule getInfo(int id);
}
