package com.chainway.dispatcherappweb.web.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.chainway.dispatcherappweb.common.CommonUtils;
import com.chainway.dispatcherappweb.common.PropertiesUtil;
import com.chainway.dispatcherappweb.common.ReturnCodeConstant;

public class AttackFilter implements Filter{

	protected final Logger log=Logger.getLogger(this.getClass());
	
	FilterConfig filterConfig=null;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig=filterConfig;
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		//先拦截sql注入
		HttpServletRequest request=(HttpServletRequest) servletRequest;
		HttpServletResponse response=(HttpServletResponse) servletResponse;
		String uri=request.getRequestURI();
		// 获得所有请求参数名
		Enumeration params=request.getParameterNames();
		while(params.hasMoreElements()) {
			// 得到参数名
			String name=params.nextElement().toString();
			// System.out.println("name===========================" + name +
			// 得到参数对应值
			String[]value=request.getParameterValues(name);
			for(int i=0;i<value.length;i++){
				//校验
				if(!CommonUtils.checkSqlInject(uri, name, value[i])){
					//有sql注入,则直接返回
					setReturnWhenException(request, response);
					return;
				}
			}
		}
		
		chain.doFilter(new ExtHttpServletRequestWrapper(request), servletResponse);
	}

	@Override
	public void destroy() {
		this.filterConfig=null;
	}
	
	private void setReturnWhenException(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String requestType=request.getHeader("X-Requested-With");
		log.info("是否是ajax："+requestType);//如果是ajax则显示XMLHttpRequest，如果是页面则显示null
		if(requestType!=null&&"XMLHttpRequest".equalsIgnoreCase(requestType)){
			//是ajax请求
			java.io.PrintWriter out=response.getWriter();
			out.println("{\"code\":"+ReturnCodeConstant.ERROR_SQL_INJECT+",\"msg\":\"Sql注入\",\"data\":null}");
			return;
		}
		request.setAttribute("msg", "Sql注入");
		String path=request.getContextPath();
		String errorUrl=PropertiesUtil.getString("error.page");
		if(errorUrl==null||"".equals(errorUrl)){
			errorUrl="/html/errorPage.jsp";//default page
		}
		response.sendRedirect(path+errorUrl);
	}

}
