package com.chainway.cacheService.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/test")
public class TestController {

	@RequestMapping(value="/test.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Map<String,Object>test(HttpServletRequest request, HttpServletResponse response){
		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("s", "s");
		ret.put("i", 1);
		ret.put("b", true);
		ret.put("d", new Date());
		return ret;
	}
}
