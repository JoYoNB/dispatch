package com.chainway.driverappweb.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chainway.dispatcherdriverservice.service.CommonService;
import com.chainway.driverappweb.annotation.Log;
import com.chainway.driverappweb.annotation.ValidateFiled;
import com.chainway.driverappweb.annotation.ValidateGroup;


@Controller
@RequestMapping("/test")
public class TestController {
	
	protected final Logger log=Logger.getLogger(this.getClass());
	
	@Reference(timeout=60000, check=false)
	private CommonService commonService;
	
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="p1"),
            @ValidateFiled(index=0,filedName="p2",maxLen=10)
    })
	@Log
	@RequestMapping(value="/test.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Map<String,Object>test(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object>result=new HashMap<String,Object>();
        result.put("a", "a");
        result.put("b", 1);
        result.put("c", false);
        result.put("d", new Date());
        //远程调用dubbo服务
        Map<String,Object>param=new HashMap<String,Object>();
        Map<String,Object>ret=commonService.test(param);
        log.info(ret);
        result.putAll(ret);
        
		return result;
	}
}
