package com.chainway.dispatcherweb.web.controller.consignor;

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
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatchercore.excetion.ExceptionCode;
import com.chainway.dispatcherservice.dto.SiteParam;
import com.chainway.dispatcherservice.service.ConsignorService;
import com.chainway.dispatcherweb.annotation.Log;
import com.chainway.dispatcherweb.annotation.ValidateFiled;
import com.chainway.dispatcherweb.annotation.ValidateGroup;
import com.chainway.dispatcherweb.biz.service.LocalService;
import com.chainway.dispatcherweb.dto.JsonResult;

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
		Map<String, Object> map=consignorService.createSite(site);
		if(map.get("id")==null||Integer.parseInt(map.get("id").toString())<=0){
			result.setCode(ExceptionCode.ERROR_CREATE_SITE_FAIL);
		}else {
			result.setData(map);
		}
		return result;
	}
	
	@Log
	@ValidateGroup(fileds = {@ValidateFiled(index = 0, notNull = true, filedName = "pageNum"),
			@ValidateFiled(index = 0, notNull = true, filedName = "pageSize")})
	@TimeZone(type="return")
	@RequestMapping("/queryList.json")
	@ResponseBody
	public JsonResult queryList(HttpServletRequest request, JsonResult result) throws Exception {
		User user=localService.getUserInSession(request);
		Map<String, Object> map = new HashMap<>();
		
		Integer pageNum = Integer.parseInt(request.getParameter("pageNum"));
		Integer pageSize = Integer.parseInt(request.getParameter("pageSize"));
		Integer offset = (pageNum-1)*pageSize;
		
		map.put("offset", offset);
		map.put("pageSize", pageSize);
		map.put("name", request.getParameter("name"));
		map.put("deptId", request.getParameter("deptId"));
		map.put("linkMan", request.getParameter("linkMan"));
		map.put("linkPhone", request.getParameter("linkPhone"));
		map.put("deptDNA", user.getDeptDNA());
		
		logger.info("货主查询站点列表（queryList），入参："+map);
		result.setData(consignorService.getSiteList(map));
		return result;
	}
	
	@Log
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
	
	@Log
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "siteId")})
	@RequestMapping("/delete.json")
	@ResponseBody
	public JsonResult delete(HttpServletRequest request, JsonResult result) throws Exception {
		Integer siteId = Integer.parseInt(request.getParameter("siteId"));
		logger.info("货主站点删除（delete），入参："+siteId);
		consignorService.deleteSite(siteId);
		return result;
	}
	
	@Log
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "siteId[]")})
	@RequestMapping("/deleteList.json")
	@ResponseBody
	public JsonResult deleteList(HttpServletRequest request, JsonResult result) throws Exception {
		String[] siteIds = request.getParameterValues("siteId[]");
		logger.info("货主站点批量删除（deleteList），入参："+siteIds);
		for(String id:siteIds){
			Integer siteId = Integer.parseInt(id);
			consignorService.deleteSite(siteId);
		}
		return result;
	}
	
	@Log
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
	
	@Log
	@RequestMapping("/getSiteForSelect.json")
	@ResponseBody
	public JsonResult getSiteForSelect(HttpServletRequest request, JsonResult result) throws Exception {
			User user=localService.getUserInSession(request);
			Map<String, Object> map = new HashMap<>();
			map.put("deptDNA", user.getDeptDNA());
			logger.info("货主查询站点信息-下拉选择使用（getSiteForSelect），入参："+map);
			Map<String, Object> resultData = new HashMap<>();
			resultData.put("list", consignorService.getSiteForSelect(map));
			result.setData(resultData);
		return result;
	}
}
