package com.chainway.dispatcherappweb.exception.handler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.chainway.dispatcherappweb.exception.BizException;
import com.chainway.dispatchercore.excetion.ServiceException;

public class BizExceptionHandler implements HandlerExceptionResolver {

	protected final Logger log=Logger.getLogger(this.getClass());
	
	private String defaultErrorPage;
	private String ajaxErrorPage;
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		//先打出堆栈，方便查异常
		ex.printStackTrace();
				
		HandlerMethod h=(HandlerMethod) handler;
		Method method=h.getMethod();		
		Map<String, Object> model = new HashMap<String, Object>();
		
		log.error("异常请求路径:"+request.getRequestURI());
		
		//如果是系统封装的异常
		if(ex instanceof BizException){
			BizException e=(BizException) ex;
			model.put("code", e.getCode());//后续封装异常后，从异常中获取
	        model.put("msg", e.getMsg());
	        model.put("data", e.getData()!=null?e.getData().toString():null);
	        log.error("code="+e.getCode()+",msg="+e.getMsg()+",data="+e.getData());
		}if(ex instanceof ServiceException){
			ServiceException e=(ServiceException) ex;
			model.put("code", e.getCode());//后续封装异常后，从异常中获取
	        model.put("msg", e.getMsg());
	        model.put("data", e.getData()!=null?e.getData().toString():null);
	        log.error("code="+e.getCode()+",msg="+e.getMsg()+",data="+e.getData());
		}else{
			model.put("code", 1);//后续封装异常后，从异常中获取
	        model.put("msg", ex.getMessage()==null?"系统异常":ex.getMessage());
		}
		
		if(method.isAnnotationPresent(org.springframework.web.bind.annotation.ResponseBody.class)){
			//如果是有@ResponseBody注解的，则是ajax请求
        	//是ajax请求
			return new ModelAndView(ajaxErrorPage, model);
		}else{
			//返回页面
			return new ModelAndView(defaultErrorPage, model);
		}
	}

	public String getDefaultErrorPage() {
		return defaultErrorPage;
	}

	public void setDefaultErrorPage(String defaultErrorPage) {
		this.defaultErrorPage = defaultErrorPage;
	}

	public String getAjaxErrorPage() {
		return ajaxErrorPage;
	}

	public void setAjaxErrorPage(String ajaxErrorPage) {
		this.ajaxErrorPage = ajaxErrorPage;
	}
}
