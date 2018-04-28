package com.chainway.dispatcherappweb.annotation;

import java.lang.annotation.ElementType;  
import java.lang.annotation.Retention;  
import java.lang.annotation.RetentionPolicy;  
import java.lang.annotation.Target;  
 
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.METHOD)  
public @interface ValidateFiled {  
     
   /** 
    * 参数索引位置 
    */  
   public int index() default -1 ;  
     
   /** 
    * 如果参数是基本数据类型或String ，就不用指定该参数，如果参数是对象，要验证对象里面某个属性，就用该参数指定属性名 
    */  
   public String filedName() default "" ;  
     
   /** 
    * 正则验证 
    */  
   public String regStr() default "";  
     
   /** 
    * 是否能为空  ， 为true表示不能为空 ， false表示能够为空 
    */  
   public boolean notNull() default false;  
     
   /** 
    * 是否能为空  ， 为true表示不能为空 ， false表示能够为空 
    */  
   public int maxLen() default -1 ;  
     
   /** 
    * 最小长度 ， 用户验证字符串 
    */  
   public int minLen() default -1 ;  
     
   /** 
    *最大值 ，用于验证数字类型数据 
    */  
   public double maxVal() default -1 ;  
     
   /** 
    *最小值 ，用于验证数值类型数据 
    */  
   public double minVal() default -1 ;  
   
   /**
    * 别名
    */
   public String alias() default "";
   
   /**
    * 数据库别名
    */
   public String dataAlias() default "";
   /**
    * 是否检查最大长度
    */
   public boolean checkMaxLen() default false;
   /**
    * 是否检查最小长度
    */
   public boolean checkMinLen() default false;
   /**
    * 是否检查正则
    */
   public boolean checkRegex() default false;
   /**
    * 字段数据类型
    */
   public String dataType() default "string";
}