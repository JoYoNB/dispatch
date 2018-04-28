package com.chainway.dispatcherdriverservice.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.springframework.core.Ordered;

public class BaseAop implements Ordered{
	public final Logger log = Logger.getLogger(this.getClass());
	/** 
	* 根据目标方法和注解类型  得到该目标方法的指定注解 
	*/  
	public Annotation getAnnotationByMethod(Method method , Class<?> annoClass){  
		Annotation all[] = method.getAnnotations();  
	    for (Annotation annotation : all) {  
	    	if (annotation.annotationType() == annoClass) {  
	    		return annotation;  
	        }  
	    }
	    return null;  
	 }
	 
	/** 
	* 根据类和方法名得到方法 
	*/  
	public Method getMethodByClassAndName(Class<?> c , String methodName){  
		Method[] methods = c.getDeclaredMethods();  
	    for (Method method : methods) {  
	    	if(method.getName().equals(methodName)){  
	    		return method ;  
	        }
	    }  
	    return null;  
	}

	public int getOrder() {
		//如果有多个层是，order越小，越优先执行
		return 0;
	}
}
