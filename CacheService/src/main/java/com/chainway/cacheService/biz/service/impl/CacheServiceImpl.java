package com.chainway.cacheService.biz.service.impl;



import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.chainway.cacheService.biz.redis.RedisDao;
import com.chainway.cacheService.biz.service.CacheService;

@Component  
@Service
public class CacheServiceImpl implements CacheService {

	@Autowired
	private RedisDao jedisDao;
	
	@Override
	public String getStringData(String key) {
		return jedisDao.getStringData(key);
	}
	
	@Override
	public Object getData(String key) {
		return jedisDao.getData(key);
	}

	@Override
	public void setStringData(String key, String value,Integer expirtTime) {
		if(expirtTime!=null){
			jedisDao.setData(key, value, expirtTime);
		}else{
			jedisDao.setData(key, value);
		}
	}

	@Override
	public void deleteData(String key) {
		jedisDao.deleteData(key);
	}

	@Override
	public List<Object> getDataList(String keyPrefix) {
		return jedisDao.getDataList(keyPrefix);
	}

	@Override
	public List<String> getStringDataList(String keyPrefix) {
		return jedisDao.getStringList(keyPrefix);
	}

	@Override
	public void setData(String key, Object value, Integer expireTime) {
		jedisDao.setData(key, value, expireTime);
		
	}

	@Override
	public void deleteByPrex(String key) {
		jedisDao.deleteByPrex(key);
	}

	@Override
	public Set<String> getKeysByPrex(String keyPrefix) {
		return jedisDao.getKeysByPrex(keyPrefix);
		
	}

	

	

}
