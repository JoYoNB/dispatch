package com.chainway.dispatcherservice.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.chainway.dispatcherservice.biz.dao.TransportDataDao;
import com.chainway.dispatcherservice.service.TransportDataService;

@Component  
@Service
public class TransportDataServiceImpl implements TransportDataService {

	@Autowired
	private TransportDataDao transportDataDao;

	@Override
	public List<Map<String, Object>> getList(Map<String, Object> param) {
		return transportDataDao.getList(param);
	}

	@Override
	public int getListCount(Map<String, Object> param) {
		return transportDataDao.getListCount(param);
	}
}
