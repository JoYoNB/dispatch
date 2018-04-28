package com.chainway.dispatcherservice.biz.dao;

import java.util.List;
import java.util.Map;

import com.chainway.dispatchercore.dto.CommonConfig;

public interface CommonConfigDao {

	public CommonConfig getCommonConfig(CommonConfig config);
	public List<CommonConfig>getCommonConfigList(Map<String,Object>param);
}
