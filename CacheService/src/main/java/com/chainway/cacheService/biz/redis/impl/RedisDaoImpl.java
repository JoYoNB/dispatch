package com.chainway.cacheService.biz.redis.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import com.chainway.cacheService.biz.redis.RedisDao;
import com.chainway.cacheService.biz.redis.SerializationUtil;
import com.chainway.cacheService.common.CommonUtils;


@Service("jedisDao")
public class RedisDaoImpl implements RedisDao{

protected final Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	public void setData(final String key, final Object value) {
		setData(key, value, -1, CommonUtils.getBaseRedisDBNum());//-1是永不过期，0是选择第0个DB
	}

	public void setData(final String key, final Object value, final Integer expirtTime) {
		setData(key, value, expirtTime, CommonUtils.getBaseRedisDBNum());//默认第0个DB
	}

	public void setData(final String key, final Object value, final Integer expirtTime, final Integer dbIndex) {
		boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {  
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer serializer = getRedisSerializer();
				RedisSerializer defaultSerializer=getRedisDefaultSerializer();
				
				byte[]bKey=serializer.serialize(key);
				byte[]bValue=null;
				
				if(value instanceof String){
					//如果是value是String类型，则沿用spring redis原始的
					bValue = serializer.serialize(value);
				}else{
					//如果是其他类型，则使用自己本身的序列化解析
					//bValue=valueSerializer.serialize(value);
					bValue=defaultSerializer.serialize(value);
				}
                if(bValue==null){
                	//如果是空，则不放进缓存
                	return false;
                }
                //指定DB
                connection.select(dbIndex);
                //设置缓存
                boolean b=connection.setNX(bKey, bValue);
                logger.info("insert cache :"+b);
                //设置过期时间
                if(expirtTime>0){
                	long t=expirtTime.longValue();
                    boolean b2=connection.expire(bKey, t);
                    logger.info("set cache expire time :"+b2);
                }
                
                return true;
			}
        });
	}

	public void setDataToSpecifiedDB(final String key, final Object value, final Integer dbIndex) {
		setData(key, value, -1, dbIndex);//-1是永不过期
	}

	public String getStringData(final String key) {
		return getStringData(key,CommonUtils.getBaseRedisDBNum());//默认选择第0个DB
	}

	public String getStringData(final String key, final Integer dbIndex) {
		String o=redisTemplate.execute(new RedisCallback<String>() {
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] bKey = serializer.serialize(key);
				//指定DB
				connection.select(dbIndex);
				//获取数据
				byte[] bValue = connection.get(bKey);
                if (bValue==null) {  
                    return null;
                }  
                String value = serializer.deserialize(bValue);
				return value;
			}
		});
		return o;
	}

	public Object getData(String key) {
		return getData(key, CommonUtils.getBaseRedisDBNum());//指定第0个DB
	}

	public Object getData(final String key, final Integer dbIndex) {
		Object o=redisTemplate.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				RedisSerializer defaultSerializer=getRedisDefaultSerializer();
				byte[] bKey = serializer.serialize(key);
				//指定DB
				connection.select(dbIndex);
				//获取数据
				byte[] bValue = connection.get(bKey);
                if (bValue==null){
                    return null;
                }
                Object value=defaultSerializer.deserialize(bValue);
				return value;
			}
		});
		return o;
	}

	public void increaseInt(String key, Integer expirTime) {
		// TODO Auto-generated method stub
		
	}

	public void deleteData(String key) {
		redisTemplate.delete(key);
	}

	/**
	 * 根据前缀匹配删除对应key
	 * @param prex
	 */
	public void deleteByPrex(String prex) {
        Set<String> keys=redisTemplate.keys(prex+"*");
        redisTemplate.delete(keys);
	}
    /**
     * 根据后缀删除对应key
     * @param suffix
     */
    public void deleteBySuffix(String suffix) {
        Set<String> keys=redisTemplate.keys("*"+suffix);
        redisTemplate.delete(keys);
    }
    
    /**
     * 根据前缀模糊查找keys
     * @param prex
     * @return
     */
    public Set<String> getKeysByPrex(String prex){
    	 Set<String> keys=redisTemplate.keys(prex+"*");
    	 return keys;
    }
	
	public void deleteData(final String key, final Integer dbIndex) {
		Object o=redisTemplate.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] bKey = serializer.serialize(key);
				//指定DB
				connection.select(dbIndex);
				//获取数据
				Long l=connection.del(bKey);
                if (bKey==null){
                    return null;
                }
				return l;
			}
		});
	}

	private RedisSerializer<String> getRedisSerializer() {
        return redisTemplate.getStringSerializer();
    }
	
	private RedisSerializer<Object> getRedisSerializerOfValue() {
        return (RedisSerializer<Object>) redisTemplate.getValueSerializer();
    }
	
	private RedisSerializer getRedisDefaultSerializer() {
        return redisTemplate.getDefaultSerializer();
    }
	
	public static void main(String[]args){
		Integer n1=-1;
		long l=n1.longValue();
		System.out.println(l);
		
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"applicationContext.xml"});
		// an ApplicationContext is also a BeanFactory (via inheritance)
		BeanFactory factory = (BeanFactory) context;
		
		RedisDaoImpl t=new RedisDaoImpl();
		RedisTemplate<?, ?> redisTemplate=(RedisTemplate<?, ?>) factory.getBean("redisTemplate");
		//t.redisTemplate=redisTemplate;
		
		RedisSerializer<String> serializer=t.getRedisSerializer();
		
		String s="hello world 你好世界";
		
		byte[]bValue = serializer.serialize(s);
		byte[]bb=SerializationUtil.serialize(s);
		
		System.out.println(SerializationUtil.deserialize(bValue));
		System.out.println(SerializationUtil.deserialize(bb));
	}

	public void hSetData(final String key, final String field,final Object value, final Integer expirtTime, final Integer dbIndex) {
		boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {  
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer serializer = getRedisSerializer();
				RedisSerializer defaultSerializer=getRedisDefaultSerializer();
				
				byte[]bKey=serializer.serialize(key);
				byte[]bValue=null;
				
				if(value instanceof String){
					//如果是value是String类型，则沿用spring redis原始的
					bValue = serializer.serialize(value);
				}else{
					//如果是其他类型，则使用自己本身的序列化解析
					//bValue=valueSerializer.serialize(value);
					bValue=defaultSerializer.serialize(value);
				}
                if(bValue==null){
                	//如果是空，则不放进缓存
                	return false;
                }
                //指定DB
                connection.select(dbIndex);
                //设置缓存
                byte[]bField=serializer.serialize(field);
                boolean b=connection.hSetNX(bKey, bField, bValue);
                logger.info("insert cache :"+b);
                //设置过期时间
                if(expirtTime>0){
                	long t=expirtTime.longValue();
                    boolean b2=connection.expire(bKey, t);
                    logger.info("set cache expire time :"+b2);
                }
                
                return true;
			}
        });
	}

	public String hGetStringData(final String key, final String field,final Integer dbIndex) {
		String o=redisTemplate.execute(new RedisCallback<String>() {
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] bKey = serializer.serialize(key);
				//指定DB
				connection.select(dbIndex);
				//获取数据
				byte[]bField=serializer.serialize(field);
				byte[] bValue = connection.hGet(bKey,bField);
                if (bValue==null) {  
                    return null;
                }  
                String value = serializer.deserialize(bValue);
				return value;
			}
		});
		return o;
	}

	public void hDeleteData(final String key, final String field, final Integer dbIndex) {
		Long o=redisTemplate.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] bKey = serializer.serialize(key);
				//指定DB
				connection.select(dbIndex);
				//获取数据
				byte[]bField=serializer.serialize(field);
				Long l=connection.hDel(bKey, bField);
				return l;
			}
		});
	}

	@Override
	public List<Object> getDataList(final String keyPrefix,final Integer dbIndex) {
		Object o=redisTemplate.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				RedisSerializer defaultSerializer=getRedisDefaultSerializer();
				byte[] bKey = serializer.serialize(keyPrefix+"*");
				connection.select(dbIndex);
				//获取数据
				Set<byte[]>set=connection.keys(bKey);
                if (set==null){
                    return null;
                }
                List<Object>list=new ArrayList<Object>();
                
                for(byte[]b:set){
                	if(b==null){
                		continue;
                	}
                	//获取数据
    				byte[] bValue=connection.get(b);
    				if(bValue==null){
    					continue;
    				}
                	Object value=defaultSerializer.deserialize(bValue);
                	list.add(value);
                }
				return list;
			}
		});
		
		if(o!=null){
			return (List)o;
		}
		
		return null;
	}

	@Override
	public List<Object> getDataList(String keyPrefix) {
		return getDataList(keyPrefix,CommonUtils.getBaseRedisDBNum());
	}

	@Override
	public List<String> getStringList(String keyPrefix) {
		return getStringList(keyPrefix,CommonUtils.getBaseRedisDBNum());
	}

	@Override
	public List<String> getStringList(final String keyPrefix, final Integer dbIndex) {
		Object o=redisTemplate.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = getRedisSerializer();
				byte[] bKey = serializer.serialize(keyPrefix+"*");
				connection.select(dbIndex);
				//获取数据
				Set<byte[]>set=connection.keys(bKey);
                if (set==null){
                    return null;
                }
                List<Object>list=new ArrayList<Object>();
                
                for(byte[]b:set){
                	if(b==null){
                		continue;
                	}
                	//获取数据
    				byte[] bValue=connection.get(b);
                    if(bValue==null){
                    	continue;
                    }
                    String o=serializer.deserialize(bValue);
                	list.add(o);
                }
				return list;
			}
		});
		
		if(o!=null){
			return (List)o;
		}
		
		return null;
	}
}
