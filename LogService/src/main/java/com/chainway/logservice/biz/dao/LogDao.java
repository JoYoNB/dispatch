package com.chainway.logservice.biz.dao;

import java.util.List;
import java.util.Map;

import com.chainway.logservice.dto.LogInfo;

public interface LogDao {

	public List<LogInfo>getLogList(Map<String,Object>param);
	
	public int addLog(LogInfo logInfo);
}
