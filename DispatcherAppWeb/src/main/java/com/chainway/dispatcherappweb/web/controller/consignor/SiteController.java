package com.chainway.dispatcherappweb.web.controller.consignor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chainway.dispatchercore.annotation.TimeZone;
import com.chainway.dispatchercore.common.CommonUtils;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatcherservice.dto.SiteParam;
import com.chainway.dispatcherservice.service.ConsignorService;
import com.chainway.dispatcherappweb.annotation.Log;
import com.chainway.dispatcherappweb.annotation.ValidateFiled;
import com.chainway.dispatcherappweb.annotation.ValidateGroup;
import com.chainway.dispatcherappweb.biz.service.LocalService;
import com.chainway.dispatcherappweb.common.ReturnCodeConstant;
import com.chainway.dispatchercore.dto.JsonResult;

@Controller
@RequestMapping("/consignor/site")
public class SiteController {
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Reference(timeout=60000, check=false)
	private ConsignorService consignorService;
	@Autowired
	private LocalService localService;

	@Log
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "name"),
/*			@ValidateFiled(index = 0, notNull = true, filedName = "deptId"),*/
			@ValidateFiled(index = 0, notNull = true, filedName = "coordinate"),
			@ValidateFiled(index = 0, notNull = true, filedName = "address"),
			@ValidateFiled(index = 0, notNull = true, filedName = "province"),
			@ValidateFiled(index = 0, notNull = true, filedName = "city"),
			@ValidateFiled(index = 0, notNull = true, filedName = "district")})
	@RequestMapping("/create.json")
	@ResponseBody
	public JsonResult create(SiteParam site,HttpServletRequest request, JsonResult result) throws Exception {
		User user=localService.getUserInSession(request);
		site.setConsignorDept(user.getMerchantDeptId());
		if(site.getDeptId()==null){ //默认为创建用户部门
			site.setDeptId(user.getDeptId());
		}
		consignorService.createSite(site);
		result.setCode(JsonResult.CODE_SUCCESS);
        result.setData(site);
        return result;
	}
	
	@ValidateGroup(fileds = {
			@ValidateFiled(index = 0, notNull = true, filedName = "limit",dataType="int")})
	@TimeZone(type="return")
	@RequestMapping("/queryList.json")
	@ResponseBody
	public JsonResult queryList(HttpServletRequest request, JsonResult result) throws Exception {
		User user=localService.getUserInSession(request);
		Map<String, Object> map = new HashMap<>();
		map.put("limit", Integer.parseInt(request.getParameter("limit")));
		map.put("downId", request.getParameter("downId"));
		map.put("upId", request.getParameter("upId"));
		map.put("deptDNA", user.getDeptDNA());
		
		logger.info("货主查询站点列表（queryList），入参："+map);
		result.setData(consignorService.getSiteList(map));
		return result;
	}
	
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "siteId")})
	@RequestMapping("/update.json")
	@ResponseBody
	public JsonResult update(SiteParam site,HttpServletRequest request, JsonResult result) throws Exception {
		User user=localService.getUserInSession(request);
		site.setConsignorDept(user.getMerchantDeptId());
		logger.info("货主站点信息更新（update），入参："+site);
		consignorService.modifySite(site);
		return result;
	}
	
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "siteId")})
	@RequestMapping("/delete.json")
	@ResponseBody
	public JsonResult delete(HttpServletRequest request, JsonResult result) throws Exception {
		Integer siteId = Integer.parseInt(request.getParameter("siteId"));
		logger.info("货主站点删除（delete），入参："+siteId);
		consignorService.deleteSite(siteId);
		return result;
	}
	
	@RequestMapping("/getLink.json")
	@ResponseBody
	public JsonResult getLink(HttpServletRequest request, JsonResult result) throws Exception {
		User user=localService.getUserInSession(request);
		String name = request.getParameter("name");
		String phone = request.getParameter("phone");
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("phone", phone);
		map.put("deptId", user.getMerchantDeptId());
		logger.info("货主查找站点常用联系人（getLink），入参："+map);
		result.setData(consignorService.getLinkMan(map));
		return result;
	}
	
	@RequestMapping("/getSiteInfo.json")
	@ResponseBody
	public JsonResult getSiteInfo(HttpServletRequest request, JsonResult result) throws Exception {
		User user=localService.getUserInSession(request);
		int siteId = Integer.parseInt(request.getParameter("siteId"));
		
		Map<String, Object> param = new HashMap<>();
		param.put("siteId", siteId);
		param.put("deptDNA", user.getDeptDNA());
		logger.info("订单详情（getSiteInfo），入参："+param);
		result.setData(consignorService.getSiteById(param));
		result.setCode(JsonResult.CODE_SUCCESS);
		return result;
	}
}
