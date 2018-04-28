package com.chainway.dispatcherappweb.web.controller;

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

import com.alibaba.dubbo.config.annotation.Reference;
import com.chainway.dispatcherservice.service.CommonService;
import com.chainway.dispatcherappweb.annotation.Log;
import com.chainway.dispatcherappweb.annotation.ValidateFiled;
import com.chainway.dispatcherappweb.annotation.ValidateGroup;
import com.chainway.dispatcherappweb.biz.service.LocalService;
import com.chainway.dispatchercore.dto.JsonResult;
import com.chainway.dispatchercore.dto.User;


@Controller
@RequestMapping("/test")
public class TestController {
	
	protected final Logger log=Logger.getLogger(this.getClass());
	
	@Reference(timeout=60000, check=false)
	private CommonService commonService;
	
	@Autowired
	private LocalService localService;
	
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="p1"),
            @ValidateFiled(index=0,filedName="p2",maxLen=10)
    })
	@Log
	@RequestMapping(value="/test.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult test(HttpServletRequest request, HttpServletResponse response,JsonResult result){
		User user=localService.getUserInSession(request);
		result.setData(user);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
}
