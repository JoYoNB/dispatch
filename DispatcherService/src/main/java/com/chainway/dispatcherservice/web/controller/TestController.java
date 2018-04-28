package com.chainway.dispatcherservice.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chainway.dispatcherservice.biz.service.DemoService;

@Controller
@RequestMapping("/test")
public class TestController {

	protected final Logger log=Logger.getLogger(this.getClass());
	
	@Autowired
	private DemoService demoService;
	
	@RequestMapping(value="/test.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Map<String,Object>test(HttpServletRequest request, HttpServletResponse response){
		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("s", "s");
		ret.put("i", 1);
		ret.put("b", true);
		ret.put("d", new Date());
		
		Map<String,Object>param=new HashMap<String,Object>();
		ret.putAll(demoService.test(param));
		
		demoService.testTransactional();
		
		return ret;
	}
	
	@RequestMapping(value="/printAuthSql.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Map<String,Object>printAuthSql(HttpServletRequest request, HttpServletResponse response){
		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("s", "s");
		ret.put("i", 1);
		ret.put("b", true);
		ret.put("d", new Date());
		
		String idStr=request.getParameter("id");
		String roleIdStr=request.getParameter("rid");
		if(StringUtils.isNotEmpty(idStr)&&StringUtils.isNotEmpty(roleIdStr)){
			String sql=demoService.printAuthSql(Integer.parseInt(roleIdStr),Integer.parseInt(idStr));
			System.out.println(sql);
		}
		return ret;
	}
}
