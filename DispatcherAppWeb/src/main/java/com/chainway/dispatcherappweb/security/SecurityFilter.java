package com.chainway.dispatcherappweb.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.chainway.dispatcherappweb.biz.service.LocalService;
import com.chainway.dispatcherappweb.common.ReturnCodeConstant;
import com.chainway.dispatcherappweb.security.filter.SecurityInterceptor;
import com.chainway.dispatchercore.common.PropertiesUtil;

public class SecurityFilter implements Filter{
	protected final Logger log=Logger.getLogger(this.getClass());
	
	@Autowired(required=true)
	private LocalService localService;
	
	@Override
	public void destroy() {
		
	}
	
	@Override
	public void init(FilterConfig fc) throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, fc.getServletContext());
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request=(HttpServletRequest)servletRequest;
		HttpServletResponse response=(HttpServletResponse)servletResponse;
		
		//System.out.println("localService"+localService);
		//localService.getUser(null);
		
		String uri=request.getRequestURI();
		log.info("被验证的url="+uri);
		String contextPath=request.getContextPath();
		if(uri!=null){
			uri=uri.replace(contextPath, "");
		}
		//根据url匹配出拦截器
		SecurityManager securityManager=SecurityManager.getInstance();
		List<SecurityInterceptor>interceptorList=securityManager.getInterceptor(uri);
		if(interceptorList!=null){
			//匹配出拦截器
			SecuritySession securitySession=securityManager.getSession(request,localService);
			for(SecurityInterceptor securityInterceptor:interceptorList){
				boolean pass=securitySession.accept(securityInterceptor);
				if(!pass){
					log.info("被拦截的url="+uri);
					log.info("token="+securitySession.getToken());
					
					//不通过，被拦截下来
					if(securitySession.getLoginUser()==null){
						setReturnWhenException(request, response, ReturnCodeConstant.ERROR_SESSION_EXPIRED, "session失效");
						return;
					}else if("user".equals(securityInterceptor.getCode())){
						setReturnWhenException(request, response, ReturnCodeConstant.ERROR_SESSION_EXPIRED, "session失效");
						return;
					}else{
						log.info("被拦截的用户="+securitySession.getLoginUser());
						setReturnWhenException(request, response, ReturnCodeConstant.ERROR_UNAUTHORIZED, "没有权限");
						return;
					}
				}else{
					break;
				}
			}
		}
		//每次成功的请求都刷新下session的存活时间，除了某些指定的url外（比如，登录的，退出登录的,没有权限的，验证码的,定时循环的）
		/*String exclude="/common/login.json,"
				+"/common/logout.json,"
				+"/common/unauthorized.json,"
				+"/common/captcha.json";
		
		List<String>excludeList=Arrays.asList(exclude.split(","));
		if(!excludeList.contains(uri)){
			String token=request.getParameter("token");
			if(StringUtils.isNotEmpty(token)){
				log.info("token="+token+"续命30分钟");
				localService.refreshUserInSession(token);
			}
		}*/
		log.info("通过权限拦截");
		chain.doFilter(servletRequest, servletResponse);
	}

	private void setReturnWhenException(HttpServletRequest request,HttpServletResponse response,int code,String msg) throws IOException{
		String requestType=request.getHeader("X-Requested-With");
		log.info("是否是ajax："+requestType);//如果是ajax则显示XMLHttpRequest，如果是页面则显示null
		if(requestType!=null&&"XMLHttpRequest".equalsIgnoreCase(requestType)){
			//是ajax请求
			response.setCharacterEncoding("UTF-8");
			java.io.PrintWriter out=response.getWriter();
			out.println("{\"code\":"+code+",\"msg\":\""+msg+"\",\"data\":null}");
			return;
		}
		request.setAttribute("code", code);
		request.setAttribute("msg", msg);
		String path=request.getContextPath();
		String errorUrl=PropertiesUtil.getString("error.page");
		if(errorUrl==null||"".equals(errorUrl)){
			errorUrl="/html/errorPage.jsp";//default page
		}
		response.sendRedirect(path+errorUrl);
	}
}
