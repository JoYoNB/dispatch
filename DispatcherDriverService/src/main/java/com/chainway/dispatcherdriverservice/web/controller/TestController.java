package com.chainway.dispatcherdriverservice.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chainway.dispatcherdriverservice.biz.service.DemoService;

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
		
		return ret;
	}
}
