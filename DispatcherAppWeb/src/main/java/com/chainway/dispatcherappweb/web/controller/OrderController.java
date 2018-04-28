package com.chainway.dispatcherappweb.web.controller;

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

import com.alibaba.dubbo.config.annotation.Reference;
import com.chainway.dispatcherappweb.annotation.Log;
import com.chainway.dispatcherappweb.annotation.ValidateFiled;
import com.chainway.dispatcherappweb.annotation.ValidateGroup;
import com.chainway.dispatcherappweb.biz.service.LocalService;
import com.chainway.dispatcherappweb.common.CommonUtils;
import com.chainway.dispatchercore.dto.JsonResult;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatcherservice.dto.OrderParam;
import com.chainway.dispatcherservice.dto.SiteParam;
import com.chainway.dispatcherservice.service.ConsignorService;
/**
 * 货主订单
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value="/order")
public class OrderController {

	protected final Logger log=Logger.getLogger(this.getClass());
	
	@Autowired
	private LocalService localService;
	
	@Reference(timeout=60000, check=false)
	private ConsignorService consignorService;
	
	@ResponseBody
	@RequestMapping(value="/getOrderList.json", method={RequestMethod.GET,RequestMethod.POST})
	public JsonResult getOrderList(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		Map<String,Object>param=new HashMap<String,Object>();
		param.put("deptDNA", user.getDeptDNA());
		String orderStatus=request.getParameter("orderStatus");
		if(StringUtils.isNotEmpty(orderStatus)){
			param.put("status", Integer.parseInt(orderStatus));
		}
		String pageNumStr=request.getParameter("pageNum");
		Integer pageNum=1;
		if(StringUtils.isNotEmpty(pageNumStr)){
			pageNum=Integer.parseInt(pageNumStr);
		}
		String pageSizeStr=request.getParameter("pageSize");
		Integer pageSize=10;
		if(StringUtils.isNotEmpty(pageSizeStr)){
			pageSize=Integer.parseInt(pageSizeStr);
		}
		param.put("pageNum", pageNum);
		param.put("pageSize", pageSize);
		
		Map<String,Object>ret=consignorService.orderListForApp(param);
		result.setData(ret);
		result.setCode(JsonResult.CODE_SUCCESS);
		return result;
	}
	
	@ValidateGroup(fileds = {//校验字段信息
	        @ValidateFiled(index=0,notNull=true,filedName="orderNo")
	})
	@Log
	@ResponseBody
	@RequestMapping(value="/cancelOrder.json", method={RequestMethod.GET,RequestMethod.POST})
	public JsonResult cancelOrder(OrderParam order,HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		order.setCreaterId(user.getId());
		order.setIp(CommonUtils.getRequestIp(request));
		consignorService.cancelOrder(order);
		result.setCode(JsonResult.CODE_SUCCESS);
		return result;
	}
	
	/**
	 * 选择站点
	 * @param request
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getSiteForSelect.json")
	@ResponseBody
	public JsonResult getSiteForSelect(HttpServletRequest request, JsonResult result) throws Exception {
			User user=localService.getUserInSession(request);
			Map<String, Object> map = new HashMap<>();
			map.put("deptDNA", user.getDeptDNA());
			Map<String, Object> resultData = new HashMap<>();
			resultData.put("list", consignorService.getSiteForSelect(map));
			result.setData(resultData);
		return result;
	}
	/**
	 * 创建站点
	 * @param site
	 * @param request
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@Log
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "name"),
/*			@ValidateFiled(index = 0, notNull = true, filedName = "deptId"),*/
			@ValidateFiled(index = 0, notNull = true, filedName = "coordinate"),
			@ValidateFiled(index = 0, notNull = true, filedName = "address"),
			@ValidateFiled(index = 0, notNull = true, filedName = "province"),
			@ValidateFiled(index = 0, notNull = true, filedName = "city"),
			@ValidateFiled(index = 0, notNull = true, filedName = "district")})
	@RequestMapping("/createSite.json")
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
	
}
