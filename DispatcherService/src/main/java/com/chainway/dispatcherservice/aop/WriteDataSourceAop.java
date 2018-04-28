package com.chainway.dispatcherservice.aop;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.chainway.dispatcherservice.biz.datasource.DBContextHolder;


@Component  
@Aspect
public class WriteDataSourceAop extends BaseAop{

	protected final Logger logger=Logger.getLogger(this.getClass());
	
	@Before("@annotation(com.chainway.dispatcherservice.annotation.WriteDataSource)")
    public void before()throws Throwable {
		logger.debug("切换到写数据库源");
		DBContextHolder.setDbType(DBContextHolder.DB_TYPE_RW);
    }
	
	@After("@annotation(com.chainway.dispatcherservice.annotation.WriteDataSource)")
	public void after(JoinPoint joinPoint)throws Throwable{
		logger.debug("切换到读数据库源");
		DBContextHolder.setDbType(DBContextHolder.DB_TYPE_R);
	}
}
