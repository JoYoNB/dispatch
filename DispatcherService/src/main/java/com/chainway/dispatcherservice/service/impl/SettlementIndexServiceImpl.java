package com.chainway.dispatcherservice.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.chainway.dispatcherservice.biz.dao.SettlementIndexDao;
import com.chainway.dispatcherservice.service.SettlementIndexService;

@Component  
@Service
public class SettlementIndexServiceImpl implements SettlementIndexService {

	@Autowired
	private SettlementIndexDao settlementIndexDao;

	@Override
	public Map<String, Object> orderSum(Map<String, Object> param) {
		return settlementIndexDao.orderSum(param);
	}

	@Override
	public List<Map<String, Object>> orderRank(Map<String, Object> param) {
		 List<Map<String, Object>>list=settlementIndexDao.orderRank(param);
		return list;
	}

	@Override
	public List<Map<String, Object>> orderBar(Map<String, Object> param) {
		return settlementIndexDao.orderBar(param);
	}
}
