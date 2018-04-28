package com.chainway.dispatcherdriverservice.aop;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chainway.dispatcherdriverservice.annotation.TimeZone;
import com.chainway.dispatcherdriverservice.common.CommonUtils;
import com.chainway.dispatcherdriverservice.common.Constant;

import chainway.frame.util.TimeUtil;


@Component
@Aspect
public class TimeZoneAop extends BaseAop{

	public final static String TYPE_BOTH="both";
	public final static String TYPE_RETURN="return";
	public final static String TYPE_PREHANDLE="preHandle";
	@Autowired  
	HttpServletRequest request;
	
	protected final Logger log=Logger.getLogger(this.getClass());
	
	@Around("@annotation(com.chainway.dispatcherdriverservice.annotation.TimeZone)")
	public Object validateAround(ProceedingJoinPoint joinPoint) throws Throwable{
		
		String timeZone="";
		timeZone=StringUtils.isEmpty(timeZone)?Constant.GLOBAL_TIMEZONE:timeZone;
		
		Object target=joinPoint.getTarget();
		Method method=getMethodByClassAndName(target.getClass(), joinPoint.getSignature().getName());//得到拦截的方法
		TimeZone an=(TimeZone) getAnnotationByMethod(method ,TimeZone.class);
		String type=an.type();
		if(TYPE_PREHANDLE.equals(type)){
			//如果不是返回类型的，则不处理返回值
			preHandle(joinPoint, timeZone);
			return joinPoint.proceed();
		}else if(TYPE_BOTH.equals(type)){
			preHandle(joinPoint, timeZone);
		}
		
		//把0时区时间转成用户时区时间
		Object obj=joinPoint.proceed();    //运行doSth()，返回值用一个Object类型来接收  
		if(obj==null){
			return null;
		}
		if(obj instanceof List){
			List<Object>list=(List<Object>)obj;
			for(Object o:list){
				excuteEntity(o,Constant.GLOBAL_TIMEZONE,timeZone);
			}
		}else{
			excuteEntity(obj,Constant.GLOBAL_TIMEZONE,timeZone);
		}
        return obj;
	}
	
	private void preHandle(ProceedingJoinPoint joinPoint,String userTimeZone)throws Exception{
		//把用户时间转成0时区时间
		//执行方法前执行
		Object[]args=joinPoint.getArgs();//方法的参数
		if(args==null){
			return;
		}
		
		for(int i=0;i<args.length;i++){
			if(args[i] instanceof Map){
				Map<String,Object>map=(Map<String, Object>) args[i];
				excuteMap(map, userTimeZone,Constant.GLOBAL_TIMEZONE);
			}else{
				//是实体类
				excuteEntity(args[i], userTimeZone,Constant.GLOBAL_TIMEZONE);
			}
		}
	}
	
	private void excuteMap(Map<String,Object>map,String oldTimeZone,String newTimeZone){
		if(map==null){
			return;
		}
		for(Map.Entry<String, Object>entry:map.entrySet()){
			if(entry.getValue() instanceof Date){
				Date date=(Date) entry.getValue();
				Date d=changeTimeZone(date,oldTimeZone,newTimeZone);//得到转换过时区的时间
				entry.setValue(d);
			}
		}
	}
	
	private void excuteEntity(Object bean,String oldTimeZone,String newTimeZone)throws Exception{
		if(bean instanceof Map){
			//如果是map
			Map<String,Object>map=(Map<String, Object>) bean;
			excuteMap(map, oldTimeZone,newTimeZone);
			return;
		}
		
		Class cl=(Class) bean.getClass();
		//获取所有的属性
		Field[]fs=cl.getDeclaredFields();
		for(int i=0;i<fs.length;i++){
			Field f=fs[i];
            f.setAccessible(true); //设置些属性是可以访问的 
            if(f.getType()==Date.class){
            	//如果是日期类型
            	Date date=(Date) f.get(bean);//得到此属性的值
            	Date d=changeTimeZone(date,oldTimeZone,newTimeZone);//得到转换过时区的时间
            	f.set(bean, d);
            }
            
		}
	}
	
	private Date changeTimeZone(Date date,String oldTimeZone,String newTimeZone){
		//Constant.GLOBAL_TIMEZONE
		if(date==null){
			return null;
		}
		try {
			String timeStr=TimeUtil.changeZoneTime(CommonUtils.date2Str(date, TimeUtil.FORMAT_TIME), TimeUtil.FORMAT_TIME, oldTimeZone, TimeUtil.FORMAT_TIME, newTimeZone);
			Date newDate=CommonUtils.str2Date(timeStr, TimeUtil.FORMAT_TIME);
			return newDate;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Date();
	}
	
}
