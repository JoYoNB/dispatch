package com.chainway.logservice.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.chainway.logservice.biz.dao.LogDao;
import com.chainway.logservice.biz.service.DemoService;
import com.chainway.logservice.dto.LogInfo;
import com.chainway.logservice.service.LogService;

@Component  
@Service
public class LogServiceImpl implements LogService {

	@Autowired
	private DemoService demoService;
	@Autowired
	private LogDao logDao;
	
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
	public List<LogInfo> getLogList(Map<String, Object> param) {
		return logDao.getLogList(param);
	}

	@Override
	public int addLog(LogInfo logInfo) {
		return logDao.addLog(logInfo);
	}

	
}
