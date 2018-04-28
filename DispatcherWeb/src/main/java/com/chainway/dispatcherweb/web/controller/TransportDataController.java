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
import com.chainway.dispatchercore.common.CommonUtils;
import com.chainway.dispatchercore.dto.FileTemplate;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatcherservice.dto.Order;
import com.chainway.dispatcherservice.service.TransportDataService;
import com.chainway.dispatcherweb.biz.service.LocalService;
import com.chainway.dispatcherweb.dto.JsonResult;
import com.chainway.fileservice.service.FileService;

@Controller
@RequestMapping("/transportData")
public class TransportDataController {

	protected final Logger log=Logger.getLogger(this.getClass());
	@Autowired
	private LocalService localService;
	
	@Reference(timeout=60000, check=false)
	private TransportDataService transportDataService;
	
	@Reference(timeout=60000, check=false)
	private FileService fileService;
	
	@TimeZone(type="return")
	@RequestMapping(value="/getTransportDataList.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult getTransportDataList(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		Map<String,Object>param=new HashMap<String,Object>();
		String startTime=request.getParameter("startTime");
		param.put("startTime", startTime);
		String endTime=request.getParameter("endTime");
		param.put("endTime", endTime);
		String name=request.getParameter("name");
		param.put("name", name);
		//页码
		int pageSize=CommonUtils.getPageSize(request);
		int offset=CommonUtils.getOffset(request);
		param.put("pageSize", pageSize);
		param.put("offset", offset);
		
		List<Map<String, Object>>list=transportDataService.getList(param);
		int total=transportDataService.getListCount(param);
		
		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("list", list);
		ret.put("total", total);
		result.setData(ret);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@RequestMapping(value="/exp.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult exp(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		Map<String,Object>param=new HashMap<String,Object>();
		String startTime=request.getParameter("startTime");
		param.put("startTime", startTime);
		String endTime=request.getParameter("endTime");
		param.put("endTime", endTime);
		String name=request.getParameter("name");
		param.put("name", name);
		
		//页码
		param.put("pageSize", 100000);
		param.put("offset", 0);
		
		List<Map<String, Object>>list=transportDataService.getList(param);
		FileTemplate ftl = new FileTemplate();
		ftl.setCode("transport_data_expor");
		Map<String, Object> map=new HashMap<>();
		map.put("token", "123456");
		map.put("timeZone", user.getGmtZone());
		String url=fileService.export(ftl, list, map);
		
		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("url", url);
		result.setData(ret);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
}
