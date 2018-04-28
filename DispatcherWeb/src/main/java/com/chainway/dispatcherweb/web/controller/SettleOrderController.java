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
import com.chainway.dispatchercore.common.CusAccessObjectUtil;
import com.chainway.dispatchercore.dto.FileTemplate;
import com.chainway.dispatchercore.dto.OrderLifecycle;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatcherservice.dto.Order;
import com.chainway.dispatcherservice.service.SettleOrderService;
import com.chainway.dispatcherservice.service.UserService;
import com.chainway.dispatcherweb.annotation.Log;
import com.chainway.dispatcherweb.annotation.ValidateFiled;
import com.chainway.dispatcherweb.annotation.ValidateGroup;
import com.chainway.dispatcherweb.biz.service.LocalService;
import com.chainway.dispatcherweb.dto.JsonResult;
import com.chainway.fileservice.service.FileService;

/**
 * 计价规则管理
 * @author chainwayits
 * @date 2018年3月28日
 */
@Controller
@RequestMapping("/settleOrder")
public class SettleOrderController {

	protected final Logger log=Logger.getLogger(this.getClass());
	
	@Autowired
	private LocalService localService;
	
	@Reference(timeout=60000, check=false)
	private UserService userService;
	
	@Reference(timeout=60000, check=false)
	private FileService fileService;
	
	@Reference(timeout=60000, check=false)
	private SettleOrderService settleOrderService;
	
	@Log
	@RequestMapping(value="/receipt.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult receipt(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("operatorId", user.getId());
		param.put("ip",CusAccessObjectUtil.getIpAddress(request));
		String[]orderNos=request.getParameterValues("orderNos[]");
		for(String orderNo:orderNos){
			param.put("orderNo", orderNo);
			settleOrderService.receipt(param);
		}
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	/**
	 * 结算
	 * @param chargeRule
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@Log
	@RequestMapping(value="/settle.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult settle(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("operatorId", user.getId());
		param.put("ip",CusAccessObjectUtil.getIpAddress(request));
		String[]orderNos=request.getParameterValues("orderNos[]");
		for(String orderNo:orderNos){
			param.put("orderNo", orderNo);
			settleOrderService.settle(param);
		}
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	/**
	 * 结算订单列表
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
		param.put("orderNo", request.getParameter("orderNo"));
		param.put("startTime", request.getParameter("startTime"));
		param.put("endTime", request.getParameter("endTime"));
		param.put("goodsTypeId", request.getParameter("goodsTypeId"));
		param.put("payStatus", request.getParameter("payStatus"));
		
		//页码
		int pageSize=CommonUtils.getPageSize(request);
		int offset=CommonUtils.getOffset(request);
		param.put("pageSize", pageSize);
		param.put("offset", offset);
		
		List<Order>list=settleOrderService.getList(param);
		int total=settleOrderService.getListCount(param);
		
		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("list", list);
		ret.put("total", total);
		result.setData(ret);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	/**
	 * 结算订单列表导出
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/exp.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult exp(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		User user=localService.getUserInSession(request);
		Map<String,Object>param=new HashMap<String,Object>();
		param.put("orderNo", request.getParameter("orderNo"));
		param.put("startTime", request.getParameter("startTime"));
		param.put("endTime", request.getParameter("endTime"));
		param.put("goodsTypeId", request.getParameter("goodsTypeId"));
		param.put("payStatus", request.getParameter("payStatus"));
		
		//页码
		param.put("pageSize", 100000);
		param.put("offset", 0);
		
		List<Order>list=settleOrderService.getList(param);
		FileTemplate ftl = new FileTemplate();
		ftl.setCode("settle_order_export");
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
	
	/**
	 * 订单基础信息详情
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="orderNo")
    })
	@TimeZone(type="return")
	@RequestMapping(value="/getOrderDetails.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult getOrderDetails(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		Map<String, Object> order=settleOrderService.getOrderInfo(request.getParameter("orderNo"));
		result.setData(order);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
	
	/**
	 * 订单生命周期
	 * @param request
	 * @param response
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@ValidateGroup(fileds = {//校验字段信息
            @ValidateFiled(index=0,notNull=true,filedName="orderNo")
    })
	@TimeZone(type="return")
	@RequestMapping(value="/getOrderLifecycle.json", method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JsonResult getOrderLifecycle(HttpServletRequest request, HttpServletResponse response,JsonResult result) throws Exception{
		Map<String,Object>param=new HashMap<String,Object>();
		param.put("orderNo", request.getParameter("orderNo"));
		List<OrderLifecycle>list=settleOrderService.getOrderLifecycle(param);

		Map<String,Object>ret=new HashMap<String,Object>();
		ret.put("list", list);
		result.setData(ret);
		result.setCode(JsonResult.CODE_SUCCESS);
        return result;
	}
}
