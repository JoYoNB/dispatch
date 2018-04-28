package com.chainway.dispatcherappweb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.METHOD)
public @interface TimeZone {

	/**
	 * 如果是return，则对返回的值进行时区转换
	 * 如果是preHandle，则对传入的参数进行时区转换
	 * 如果是both，则是处理传入参数和输出参数
	 * @return
	 */
	public String type() default "return";
}
