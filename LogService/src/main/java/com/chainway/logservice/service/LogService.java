package com.chainway.logservice.service;

import java.util.List;
import java.util.Map;

import com.chainway.logservice.dto.LogInfo;

public interface LogService {

	public Map<String,Object>test(Map<String,Object>param);
	
	public List<LogInfo>getLogList(Map<String,Object>param);
	
	public int addLog(LogInfo logInfo);
}
