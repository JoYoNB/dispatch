package com.chainway.dispatcherservice.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.chainway.dispatchercore.dto.CommonConfig;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatcherservice.annotation.WriteDataSource;
import com.chainway.dispatcherservice.biz.dao.AreaDao;
import com.chainway.dispatcherservice.biz.dao.CarryTypeDao;
import com.chainway.dispatcherservice.biz.dao.CommonConfigDao;
import com.chainway.dispatcherservice.biz.dao.FeedbackDao;
import com.chainway.dispatcherservice.biz.dao.GoodsTypeDao;
import com.chainway.dispatcherservice.biz.dao.UserDao;
import com.chainway.dispatcherservice.biz.dao.VehicleTypeDao;
import com.chainway.dispatcherservice.biz.service.DemoService;
import com.chainway.dispatcherservice.service.CommonService;

@Component  
@Service
public class CommonServiceImpl implements CommonService {

	@Autowired
	private DemoService demoService;
	@Autowired
	private AreaDao areaDao;
	@Autowired
	private VehicleTypeDao vehicleTypeDao;
	@Autowired
	private GoodsTypeDao goodsTypeDao;
	@Autowired
	private CarryTypeDao carryTypeDao;
	@Autowired
	private FeedbackDao feedbackDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private CommonConfigDao commonConfigDao;
	
	@Override
	public Map<String, Object> test(Map<String, Object> param) {
		
		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("a", 1);
		ret.put("b", false);
		ret.put("c", "c");
		ret.put("d", new Date());
		
		ret.putAll(demoService.test(param));
		
		return ret;
	}

	@Override
	public List<Map<String, Object>> getAreaByParentId(String parent) {
		return areaDao.getAreaByParentId(parent);
	}

	@Override
	public List<Map<String, Object>> getVehicleTypeList() {
		return vehicleTypeDao.getVehicleTypeList();
	}
	@Override
	public List<Map<String, Object>> getGoodsTypeList() {
		return goodsTypeDao.getGoodsTypeList();
	}

	@Override
	public List<Map<String, Object>> getCarryTypeList() {
		return carryTypeDao.getCarryTypeList();
	}

	@Override
	@WriteDataSource
	public void addFeedback(Map<String, Object> param) {
		feedbackDao.add(param);
	}

	@Override
	public Map<String, Object> myInfo(User user) {
		return userDao.myInfo(user);
	}
	
	@Override
	public Map<String, Object> aboutUs(){
		Map<String, Object> param=new HashMap<>();
		String[] keyList= {"companyName","icp","copyright"};
		param.put("keyList", keyList);
		List<CommonConfig> list=commonConfigDao.getCommonConfigList(param);
		Map<String, Object> map=new HashMap<>();
		for (CommonConfig commonConfig : list) {
			map.put(commonConfig.getKey(), commonConfig.getValue());
			map.put(commonConfig.getKey()+"I18nKey", commonConfig.getI18nKey());
		}
		return map;
	}
}
