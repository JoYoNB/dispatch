package com.chainway.dispatcherservice.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.chainway.dispatchercore.excetion.ExceptionCode;
import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherservice.annotation.WriteDataSource;
import com.chainway.dispatcherservice.biz.dao.ChargeRuleDao;
import com.chainway.dispatcherservice.dto.ChargeRule;
import com.chainway.dispatcherservice.service.ChargeRuleService;

@Component  
@Service
public class ChargeRuleServiceImpl implements ChargeRuleService {

	@Autowired
	private ChargeRuleDao chargeRuleDao;
	
	@Override
	@WriteDataSource
	public void add(ChargeRule chargeRule) throws ServiceException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("cityId", chargeRule.getCityId());
		param.put("vehicleTypeId", chargeRule.getVehicleTypeId());
		List<ChargeRule>list=chargeRuleDao.getList(param);
		if(list!=null&&!list.isEmpty()){
			throw new ServiceException(ExceptionCode.ERROR_CITY_VEHICLETYPE_EXIST,"同城市同车型的规则已存在");
		}
		chargeRuleDao.add(chargeRule);
	}

	@Override
	@WriteDataSource
	public void delete(int id) {
		chargeRuleDao.delete(id);
	}

	@Override
	@WriteDataSource
	public void update(ChargeRule chargeRule) throws ServiceException {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("cityId", chargeRule.getCityId());
		param.put("vehicleTypeId", chargeRule.getVehicleTypeId());
		List<ChargeRule>list=chargeRuleDao.getList(param);
		if(list!=null&&!list.isEmpty()){
			ChargeRule cr=list.get(0);
			if(chargeRule.getId()!=cr.getId()) {
				throw new ServiceException(ExceptionCode.ERROR_CITY_VEHICLETYPE_EXIST,"同城市同车型的规则已存在");
			}
		}
		chargeRuleDao.update(chargeRule);
	}

	@Override
	public List<ChargeRule> getList(Map<String, Object> param) {
		return chargeRuleDao.getList(param);
	}

	@Override
	public int getListCount(Map<String, Object> param) {
		// TODO Auto-generated method stub
		return chargeRuleDao.getListCount(param);
	}

	@Override
	public ChargeRule getInfo(int id) {
		// TODO Auto-generated method stub
		return chargeRuleDao.getInfo(id);
	}
}
