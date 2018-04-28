package com.chainway.driverappweb.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chainway.dispatchercore.annotation.TimeZone;
import com.chainway.dispatchercore.common.CommonUtils;
import com.chainway.dispatchercore.common.OrderStatus;
import com.chainway.dispatchercore.dto.Driver;
import com.chainway.dispatchercore.dto.JsonResult;
import com.chainway.dispatcherdriverservice.service.CommonService;
import com.chainway.dispatcherdriverservice.service.MonitorService;
import com.chainway.dispatcherdriverservice.service.OrderService;
import com.chainway.driverappweb.annotation.Log;
import com.chainway.driverappweb.annotation.ValidateFiled;
import com.chainway.driverappweb.annotation.ValidateGroup;
import com.chainway.driverappweb.biz.service.LocalService;
import com.chainway.driverappweb.common.Constant;
import com.chainway.driverappweb.exception.BizException;
import com.chainway.fileservice.service.FileService;

import sun.misc.BASE64Decoder;

@Controller
@RequestMapping(value="/order")
public class OrderController {
	@Autowired
	private LocalService localService;
	
	@Reference(check=false,timeout=60000)
	private CommonService commonService;
	
	@Reference(check=false,timeout=60000)
	private OrderService orderService;
	
	@Reference(check=false,timeout=60000)
	private MonitorService monitorService;
	
	@Reference(timeout=60000, check=false)
	private FileService fileService;
	
	
	/**
	 * 订单列表
	 * @param request
	 * @return
	 */
	@Log
	@TimeZone(type="return")
	@ResponseBody
	@RequestMapping(value="/getOrderList.json")
	public JsonResult getOrderList(HttpServletRequest request) throws Exception{
		Driver driverInSession = localService.getUserInSession(request);
		String orderStatus = request.getParameter("orderStatus");
		List<String> orderStatusList=new ArrayList<String>();
		if(StringUtils.isEmpty(orderStatus)) {
			//默认查询
			orderStatusList.add(OrderStatus.ASSIGNED+"");//待确认
			orderStatusList.add(OrderStatus.WAIT_FOR_PICK_UP+"");//待提货
			orderStatusList.add(OrderStatus.IN_TRANSIT+"");//配送中
		}else {
			//根据前端传的订单状态查询
			String[] orderStatusArray = orderStatus.split(",");
			orderStatusList = Arrays.asList(orderStatusArray);
		}
		Map<String, Object>param=new HashMap<String,Object>();
		param.put("driverId", driverInSession.getDriverId());
		param.put("orderStatusList", orderStatusList);
		param.put("offset", CommonUtils.getOffset(request));
		param.put("pageSize", CommonUtils.getPageSize(request));
		JsonResult jsonResult = new JsonResult();
	    List<Map<String, Object>> orderList = orderService.getOrderList(param);
	    Integer total = orderService.getOrderListCount(param);
	    Map<String, Object>ret=new HashMap<String,Object>();
	    ret.put("list", orderList);
	    ret.put("total", total);
	    jsonResult.setData(ret);
		jsonResult.setCode(JsonResult.CODE_SUCCESS);
		return jsonResult;
	}
	/**
	 * 订单详情
	 * @param request
	 * @return
	 */
	@Log
	@TimeZone(type="return")
	@ResponseBody
	@ValidateGroup(fileds= {//校验字段信息
			@ValidateFiled(index=0,notNull=true,filedName="orderNo")
	})
	@RequestMapping(value="/getOrderInfo.json")
	public JsonResult getOrderInfo(HttpServletRequest request) throws Exception{
		Driver driverInSession = localService.getUserInSession(request);
		String orderNo = request.getParameter("orderNo");
		Map<String, Object>param=new HashMap<String,Object>();
		param.put("driverId", driverInSession.getDriverId());
		param.put("orderNo", orderNo);
		Map<String, Object> orderInfo = orderService.getOrderInfo(param);
		JsonResult jsonResult = new JsonResult();
		jsonResult.setData(orderInfo);
		jsonResult.setCode(JsonResult.CODE_SUCCESS);
		return jsonResult;
	}
	
	/**
	 * 确认订单(订单状态：待确认->待提货)
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@Log
	@ResponseBody
	@ValidateGroup(fileds= {//校验字段信息
			@ValidateFiled(index=0,notNull=true,filedName="orderNo")
	})
	@RequestMapping(value="/confirmOrder.json")
	public JsonResult confirmOrder(HttpServletRequest request) throws Exception{
		Driver driverInSession = localService.getUserInSession(request);
		String orderNo = request.getParameter("orderNo");
		Map<String, Object>param=new HashMap<String,Object>();
		param.put("orderNo", orderNo);
		param.put("driverId", driverInSession.getDriverId());
		param.put("driverName", driverInSession.getDriverName());
		param.put("ip", request.getRemoteAddr());
		orderService.confirmOrder(param);
		JsonResult jsonResult = new JsonResult();
		jsonResult.setCode(JsonResult.CODE_SUCCESS);
		return jsonResult;
	}

	/**
	 * 司机提货(订单状态：待提货->配送中)
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@Log
	@ResponseBody
	@ValidateGroup(fileds= {//校验字段信息
			@ValidateFiled(index=0,notNull=true,filedName="orderNo"),
			@ValidateFiled(index=0,notNull=true,filedName="siteId")
	})
	@RequestMapping(value="/pickup.json")
	public JsonResult pickup(HttpServletRequest request) throws Exception{
		Driver driverInSession = localService.getUserInSession(request);
		String orderNo = request.getParameter("orderNo");
		String siteId = request.getParameter("siteId");
		Map<String, Object>param=new HashMap<String,Object>();
		param.put("orderNo", orderNo);
		param.put("siteId", siteId);
		param.put("driverId", driverInSession.getDriverId());
		param.put("driverName", driverInSession.getDriverName());
		param.put("ip", request.getRemoteAddr());
		orderService.pickup(param);
		//更新车辆的负载状态
		param.put("loadRate", Constant.DRIVER_LOAD_RATE_FULL);
		commonService.updateVehicleLoadRate(param);
		JsonResult jsonResult = new JsonResult();
		jsonResult.setCode(JsonResult.CODE_SUCCESS);
		return jsonResult;
	}
	
	
	
	/**
	 * 签收后上传回单(配送点卸货完成)
	 * @param request
	 * @return
	 * @throws BizException 
	 */
	@Log
	@ResponseBody
	@ValidateGroup(fileds= {//校验字段信息
			@ValidateFiled(index=0,notNull=true,filedName="receiptImage"),
			@ValidateFiled(index=0,notNull=true,filedName="orderNo"),
			@ValidateFiled(index=0,notNull=true,filedName="siteId"),
			@ValidateFiled(index=0,notNull=false,filedName="receiptRemark",checkMaxLen=true,maxLen=255)
	})
	@RequestMapping(value="/uploadReceipt.json")
	public JsonResult uploadReceipt(HttpServletRequest request) throws Exception{
		Driver driver = localService.getUserInSession(request);
		JsonResult jsonResult = new JsonResult();
        String receiptImage = request.getParameter("receiptImage");
        String orderNo = request.getParameter("orderNo");
        String siteId = request.getParameter("siteId");
        String receiptRemark = request.getParameter("receiptRemark");
        //Base64解码图片
		BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes = decoder.decodeBuffer(receiptImage);
        for (int i = 0; i < bytes.length; ++i) {
	        if (bytes[i] < 0) {// 调整异常数据
	          bytes[i] += 256;
	        }
        }
        //调用文件上传服务
		long l = System.currentTimeMillis();
		String receiptUrl=fileService.uploadFile( l + ".jpg",bytes,"123456789");
        Map<String, Object>param=new HashMap<String,Object>();
        param.put("orderNo",orderNo);
        param.put("siteId", siteId);
        param.put("receiptUrl", receiptUrl);
        param.put("receiptRemark", receiptRemark);
        orderService.uploadReceipt(param);
        //判断站点顺序，最后一个站点则需结束订单
        param.clear();
        param.put("driverId", driver.getDriverId());
		param.put("orderNo", orderNo);
		Map<String, Object> orderInfo = orderService.getOrderInfo(param);
		if(siteId.equals(orderInfo.get("endSiteId"))) {
			//该站点为结束站点,修改订单状态
			orderService.finishOrder(orderNo);
			//更新车辆负载 :空载
			param.put("loadRate", Constant.DRIVER_LOAD_RATE_EMPTY);
			commonService.updateVehicleLoadRate(param);
		}else {
			//更新车辆负载:半载
			param.put("loadRate", Constant.DRIVER_LOAD_RATE_HALF);
			commonService.updateVehicleLoadRate(param);
		}
        jsonResult.setCode(JsonResult.CODE_SUCCESS);
        jsonResult.setMsg("上传成功");
        return jsonResult;
	}
	
	/**
	 * 回单列表
	 * @param request
	 * @return
	 */
	@Log
	@ResponseBody
	@ValidateGroup(fileds= {//校验字段信息
			@ValidateFiled(index=0,notNull=true,filedName="orderNo")
	})
	@RequestMapping(value="/getReceiptList.json")
	public JsonResult getReceiptList(HttpServletRequest request){
		JsonResult jsonResult = new JsonResult();
		String orderNo = request.getParameter("orderNo");
		Map<String, Object>param=new HashMap<String,Object>();
		param.put("orderNo", orderNo);
		List<Map<String, Object>>receiptList=orderService.getReceiptList(param);
		jsonResult.setCode(JsonResult.CODE_SUCCESS);
		jsonResult.setData(receiptList);
		return jsonResult;
	}
	/**
	 * 获取订单路线
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@Log
	@ResponseBody
	@ValidateGroup(fileds= {//校验字段信息
			@ValidateFiled(index=0,notNull=true,filedName="orderNo")
	})
	@RequestMapping(value="/getOrderTransportRoute.json")
	public JsonResult getOrderTransportRoute(HttpServletRequest request) throws Exception{
		JsonResult jsonResult = new JsonResult();
		Driver driverInSession = localService.getUserInSession(request);
		String orderNo = request.getParameter("orderNo");
		Map<String, Object> param = new HashMap<String,Object>();
		param.put("driverId", driverInSession.getDriverId());
		param.put("orderNo", orderNo);
		Map<String, Object> orderTransportRoute = monitorService.getOrderTransportRoute(param);
		jsonResult.setCode(JsonResult.CODE_SUCCESS);
		jsonResult.setData(orderTransportRoute);
		return null;
	}
	
}
