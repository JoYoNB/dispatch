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
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatcherservice.dto.ChargeRule;
import com.chainway.dispatcherservice.service.ChargeRuleService;
import com.chainway.dispatcherservice.service.UserService;
import com.chainway.dispatcherweb.annotation.Log;
import com.chainway.dispatcherweb.annotation.ValidateFiled;
import com.chainway.dispatcherweb.annotation.ValidateGroup;
import com.chainway.dispatcherweb.biz.service.LocalService;
import com.chainway.dispatcherweb.dto.JsonResult;

/**
 * 计价规则管理
 * @author chainwayits
 * @date 2018年3月28日
 */
@Controller
@RequestMapping("/chargeRule")
public class ChargeRuleController {

	protected final Logger log=Logger.getLogger(this.getClass());
	
	@Autowired
	private LocalService localService;
	
	@Reference(timeout=60000, check=false)
	private UserService userService;
	
	@Reference(timeout=60000, check=false)
	private ChargeRuleService chargeRuleService;
	
	/**
	 * 新增计价规则
	 * @param chargeRule
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@ValidateGroup(fileds = {//校验字段信息
        @ValidateFiled(index=0,notNull=true,filedName="startingMileage",dataType="double",minVal=1,maxVal=9999999),
        @ValidateFiled(index=0,notNull=true,filedName="startingPrice",dataType="double",minVal=1,maxVal=9999999),
        @ValidateFiled(index=0,notNull=true,filedName="price",dataType="double",minVal=1,maxVal=9999999)
    })
	@Log
	@RequestMapping(value="/add.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult addDept(ChargeRule chargeRule,HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		chargeRule.setCreaterId(user.getId());
		chargeRule.setUpdaterId(user.getId());
		chargeRuleService.add(chargeRule);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	/**
	 * 删除计价规则
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@ValidateGroup(fileds = {//校验字段信息
        @ValidateFiled(index=0,notNull=true,filedName="id[]")
    })
	@Log
	@RequestMapping(value="/delete.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult deleteDeptList(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		String[]ids=request.getParameterValues("id[]");
		for(String id:ids){
			chargeRuleService.delete(Integer.parseInt(id));
		}
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	/**
	 * 修改计价规则
	 * @param chargeRule
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@ValidateGroup(fileds = {//校验字段信息
		@ValidateFiled(index=0,notNull=true,filedName="startingMileage",dataType="double",minVal=1,maxVal=9999999),
	    @ValidateFiled(index=0,notNull=true,filedName="startingPrice",dataType="double",minVal=1,maxVal=9999999),
	    @ValidateFiled(index=0,notNull=true,filedName="price",dataType="double",minVal=1,maxVal=9999999)
    })
	@Log
	@RequestMapping(value="/update.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult updateDept(ChargeRule chargeRule,HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		chargeRule.setUpdaterId(user.getId());
		chargeRuleService.update(chargeRule);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	/**
	 * 查询计价规则列表
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@TimeZone(type="return")
	@RequestMapping(value="/list.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult list(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		
		Map<String,Object>param=new HashMap<String,Object>();
		String areaName=request.getParameter("areaName");
		param.put("areaName", areaName);
		String vehicleTypeName=request.getParameter("vehicleTypeName");
		param.put("vehicleTypeName", vehicleTypeName);
		
		//页码
		int pageSize=CommonUtils.getPageSize(request);
		int offset=CommonUtils.getOffset(request);
		param.put("pageSize", pageSize);
		param.put("offset", offset);
		
		List<ChargeRule>list=chargeRuleService.getList(param);
		int total=chargeRuleService.getListCount(param);
		
		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("list", list);
		ret.put("total", total);
		result.setData(ret);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="id",dataType="int",minVal=0,maxVal=999999999)
    })
	@TimeZone(type="return")
	@RequestMapping(value="/info.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult info(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		ChargeRule chargeRule=chargeRuleService.getInfo(Integer.parseInt(request.getParameter("id")));
		result.setData(chargeRule);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
}
