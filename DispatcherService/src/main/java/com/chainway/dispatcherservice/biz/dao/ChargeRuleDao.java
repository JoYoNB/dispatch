package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;

import com.chainway.dispatcherservice.dto.ChargeRule;

/**
 * 计价规则管理
 * @author chainwayits
 * @date 2018年3月21日
 */
public interface ChargeRuleDao {
	/**
	 * 新增
	 * @param chargeRule
	 */
	public void add(ChargeRule chargeRule);
	/**
	 * 删除
	 * @param param
	 */
	public void delete(int param);
	/**
	 * 修改
	 * @param chargeRule
	 */
	public void update(ChargeRule chargeRule);
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
