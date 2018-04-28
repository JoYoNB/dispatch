package com.chainway.cacheService.biz.service;

import java.util.List;
import java.util.Set;

public interface CacheService {

	public String getStringData(String key);
	public void setStringData(String key,String value,Integer expirtTime);
	public Object getData(String key);
	public void setData(String key,Object value,Integer expireTime);
	public List<Object>getDataList(String keyPrefix);
	public List<String>getStringDataList(String keyPrefix);
	public void deleteData(String key);
	public void deleteByPrex(String key);
	public Set<String> getKeysByPrex(String keyPrefix);
}
