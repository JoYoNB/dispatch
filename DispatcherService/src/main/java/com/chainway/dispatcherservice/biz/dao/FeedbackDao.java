package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;

import com.chainway.dispatcherservice.dto.ChargeRule;

/**
 * 意见反馈
 * @author chainwayits
 * @date 2018年3月21日
 */
public interface FeedbackDao {
	/**
	 * 新增
	 * @param chargeRule
	 */
	public void add(Map<String, Object> param);
}
