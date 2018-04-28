package com.chainway.driverappweb.aop;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

import com.chainway.dispatchercore.annotation.TimeZone;
import com.chainway.dispatchercore.dto.Driver;
import com.chainway.dispatchercore.dto.JsonResult;
import com.chainway.driverappweb.biz.service.LocalService;
import com.chainway.driverappweb.common.Constant;
import com.chainway.driverappweb.common.TimeUtil;
import com.chainway.driverappweb.web.filter.ExtHttpServletRequestWrapper;




@Component
@Aspect
public class TimeZoneAop extends BaseAop{

	public final static String TYPE_BOTH="both";
	public final static String TYPE_RETURN="return";
	public final static String TYPE_PREHANDLE="preHandle";
	@Autowired  
	HttpServletRequest request;
	@Autowired
	private LocalService localService;
	
	protected final Logger log=Logger.getLogger(this.getClass());
	
	@Around("@annotation(com.chainway.dispatchercore.annotation.TimeZone)")
	public Object validateAround(ProceedingJoinPoint joinPoint) throws Throwable{
		if(request==null){
			return joinPoint.proceed();
		}
		//获取用户信息
		Driver user=localService.getUserInSession(request);
		if(user==null){
			return joinPoint.proceed();
		}
		String timeZone=user.getGmtZone();
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
		if(obj instanceof JsonResult){
			//只处理这种结果的值，其他的不处理
			JsonResult result=(JsonResult) obj;
			Object data=result.getData();
			if(data!=null){
				if(data instanceof List){
					List<Object>list=(List<Object>)data;
					for(Object o:list){
						if(o instanceof Map) {
							excuteMap((Map<String, Object>) o, Constant.GLOBAL_TIMEZONE,timeZone);
						}else {
							excuteEntity(o,Constant.GLOBAL_TIMEZONE,timeZone);
						}
					}
				}else if(data instanceof Map){
					Map<String,Object>ret=(Map<String, Object>) data;
					excuteMap(ret, Constant.GLOBAL_TIMEZONE,timeZone);
					List<Object>list=(List<Object>) ret.get("list");
					if(list!=null&&!list.isEmpty()){
						for(Object o:list){
							if(o instanceof Map) {
								excuteMap((Map<String, Object>) o, Constant.GLOBAL_TIMEZONE,timeZone);
							}else {
								excuteEntity(o,Constant.GLOBAL_TIMEZONE,timeZone);
							}
						}
					}
				}else{
					excuteEntity(data,Constant.GLOBAL_TIMEZONE,timeZone);
				}
			}
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
		//约定好，只处理第一个参数
		Object arg0=args[0];
		if(arg0!=null){
			if(arg0 instanceof ExtHttpServletRequestWrapper){
				//是request
				ExtHttpServletRequestWrapper request=(ExtHttpServletRequestWrapper) arg0;
				TimeZoneEnhance timeZoneEnhance=new TimeZoneEnhance(userTimeZone,Constant.GLOBAL_TIMEZONE);
				request.setEnhance(timeZoneEnhance);
			}else{
				//是实体bean
				excuteEntity(arg0, userTimeZone,Constant.GLOBAL_TIMEZONE);
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
				String newDate=TimeUtil.date2Str(d, TimeUtil.FORMAT_TIME);
				entry.setValue(newDate);
			}
		}
	}
	
	private void excuteEntity(Object bean,String oldTimeZone,String newTimeZone)throws Exception{
		//只处理bean的第一层属性
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
			String timeStr=TimeUtil.changeZoneTime(TimeUtil.date2Str(date, TimeUtil.FORMAT_TIME), TimeUtil.FORMAT_TIME, oldTimeZone, TimeUtil.FORMAT_TIME, newTimeZone);
			Date newDate=TimeUtil.str2Date(timeStr, TimeUtil.FORMAT_TIME);
			return newDate;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Date();
	}
	public static void main(String[] args) {
		List<Map<String, Object>>list=new ArrayList<>();
		Map<String, Object> map=new HashMap<String, Object>();
		
	}
	
}
