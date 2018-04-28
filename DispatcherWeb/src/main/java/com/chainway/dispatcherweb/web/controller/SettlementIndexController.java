package com.chainway.dispatcherweb.web.controller;

import java.util.HashMap;
import java.util.List;
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
import com.chainway.dispatchercore.annotation.TimeZone;
import com.chainway.dispatchercore.common.Constant;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatcherservice.service.SettlementIndexService;
import com.chainway.dispatcherservice.service.UserService;
import com.chainway.dispatcherweb.biz.service.LocalService;
import com.chainway.dispatcherweb.dto.JsonResult;

/**
 * 计价规则管理
 * @author chainwayits
 * @date 2018年3月28日
 */
@Controller
@RequestMapping("/settlementIndex")
public class SettlementIndexController {

	protected final Logger log=Logger.getLogger(this.getClass());
	
	@Autowired
	private LocalService localService;
	
	@Reference(timeout=60000, check=false)
	private UserService userService;
	
	@Reference(timeout=60000, check=false)
	private SettlementIndexService settlementIndexService;
	
	/**
	 * 统计
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@TimeZone(type="preHandle")
	@RequestMapping(value="/orderSum.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult orderSum(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		
		Map<String,Object>param=new HashMap<String,Object>();
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		param.put("startTime", startTime);
		param.put("endTime", endTime);
		
		Map<String,Object>data=settlementIndexService.orderSum(param);
		result.setData(data);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	/**
	 * 排名
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@TimeZone(type="preHandle")
	@RequestMapping(value="/orderRank.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult orderRank(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		
		Map<String,Object>param=new HashMap<String,Object>();
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		param.put("startTime", startTime);
		param.put("endTime", endTime);
		
		List<Map<String,Object>>list=settlementIndexService.orderRank(param);
		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("list", list);
		result.setData(ret);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	/**
	 * 图表
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@TimeZone(type="preHandle")
	@RequestMapping(value="/orderBar.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult orderBar(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		
		Map<String,Object>param=new HashMap<String,Object>();
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		param.put("startTime", startTime);
		param.put("endTime", endTime);
		User user=localService.getUserInSession(request);
		String gmtZone=Constant.timeZoneMap.get(user.getGmtZone());
		param.put("gmtZone", gmtZone);
		List<Map<String,Object>>list=settlementIndexService.orderBar(param);
		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("list", list);
		result.setData(ret);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
}
