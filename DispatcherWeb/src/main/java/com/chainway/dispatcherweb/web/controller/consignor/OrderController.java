package com.chainway.dispatcherweb.web.controller.consignor;

import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chainway.cacheService.biz.service.CacheService;
import com.chainway.dispatchercore.annotation.TimeZone;
import com.chainway.dispatchercore.common.CusAccessObjectUtil;
import com.chainway.dispatchercore.common.TimeUtil;
import com.chainway.dispatchercore.common.pingxx.PingFactory;
import com.chainway.dispatchercore.common.pingxx.WebhooksVerifyExample;
import com.chainway.dispatchercore.common.pingxx.bean.ChargeBean;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatchercore.excetion.ExceptionCode;
import com.chainway.dispatchercore.excetion.ServiceException;
import com.chainway.dispatcherservice.dto.OrderParam;
import com.chainway.dispatcherservice.service.ConsignorService;
import com.chainway.dispatcherweb.annotation.Log;
import com.chainway.dispatcherweb.annotation.ValidateFiled;
import com.chainway.dispatcherweb.annotation.ValidateGroup;
import com.chainway.dispatcherweb.biz.service.LocalService;
import com.chainway.dispatcherweb.dto.JsonResult;
import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Event;
import com.pingplusplus.model.Webhooks;

@Controller
@RequestMapping("/consignor/order")
public class OrderController {
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Reference(timeout=60000, check=false)
	private ConsignorService consignorService;
	@Autowired
	private LocalService localService;
	
	@Reference(timeout=60000, check=false)
	private CacheService cacheService;

	@Log
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "cargoType"),
			@ValidateFiled(index = 0, notNull = true, filedName = "vehicleType"),
			@ValidateFiled(index = 0, notNull = true, filedName = "startSiteId"),
			@ValidateFiled(index = 0, notNull = true, filedName = "districtId"),
			@ValidateFiled(index = 0, notNull = true, filedName = "pickupTime"),
			@ValidateFiled(index = 0, notNull = true, filedName = "arriveTime"),
			@ValidateFiled(index = 0, notNull = true, filedName = "feeType"),
			@ValidateFiled(index = 0, notNull = true, filedName = "fee"),
			@ValidateFiled(index = 0, notNull = true, filedName = "orderStatus"),
			@ValidateFiled(index = 0, notNull = true, filedName = "sites"),
			@ValidateFiled(index = 0, notNull = true, filedName = "distance")})
	@TimeZone(type="preHandle")
	@ResponseBody
	@RequestMapping("/create.json")
	public JsonResult create(@RequestBody OrderParam order,HttpServletRequest request, JsonResult result) throws Exception {
		User user=localService.getUserInSession(request);
		order.setCreaterId(user.getId());
		order.setConsignorDept(user.getMerchantDeptId());//
		//logger.info("货主创建订单（create），入参："+map);
		order.setDeptId(user.getDeptId());
		order.setIp(CusAccessObjectUtil.getIpAddress(request));
		consignorService.createOrder(order);
		return result;
	}
	
	@Log
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "orderNo")})
	@TimeZone(type="return")
	@ResponseBody
	@RequestMapping("/orderDetail.json")
	public JsonResult orderDetail(HttpServletRequest request, JsonResult result) throws Exception {
		User user=localService.getUserInSession(request);
		Map<String, Object> map = new HashMap<>();
		map.put("orderNo", request.getParameter("orderNo"));
		map.put("deptDNA", user.getDeptDNA());
		map.put("timeZone", user.getGmtZone());
		map.put("ifLog", request.getParameter("ifLog"));
		logger.info("货主订单详情（orderDetail），入参："+map);
		result.setData(consignorService.getOrderDetails(map));
		return result;
	}
	
	@Log
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "pageNum"),
			@ValidateFiled(index = 0, notNull = true, filedName = "pageSize"),
			/*@ValidateFiled(index = 0, notNull = true, filedName = "startTime"),
			@ValidateFiled(index = 0, notNull = true, filedName = "endTime")*/})
	@TimeZone(type="both")
	@ResponseBody
	@RequestMapping("/orderList.json")
	public JsonResult orderList(HttpServletRequest request, JsonResult result) throws Exception {
		User user=localService.getUserInSession(request);
		Map<String, Object> map = new HashMap<>();
		Integer pageNum = Integer.parseInt(request.getParameter("pageNum"));
		Integer pageSize = Integer.parseInt(request.getParameter("pageSize"));
		Integer offset = (pageNum-1)*pageSize;
		map.put("offset", offset);
		map.put("pageSize", pageSize);
		map.put("startTime", request.getParameter("startTime"));
		map.put("endTime", request.getParameter("endTime"));
		map.put("startSiteName",request.getParameter("startSiteName"));
		map.put("middleOrEnd",request.getParameter("middleOrEnd"));
		map.put("driver",request.getParameter("driver"));
		map.put("orderNo",request.getParameter("orderNo"));
		map.put("plateNo",request.getParameter("plateNo"));
		map.put("cargoType",request.getParameter("cargoType"));
		map.put("status", request.getParameter("status"));
		map.put("deptDNA", user.getDeptDNA());
		logger.info("货主订单列表查询（orderList），入参："+map);
		result.setData(consignorService.orderList(map));
		return result;
	}
	
	@Log
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "orderNo")})
	@TimeZone(type="preHandle")
	@ResponseBody
	@RequestMapping("/update.json")
	public JsonResult update(@RequestBody OrderParam order,HttpServletRequest request, JsonResult result) throws Exception {
		User user=localService.getUserInSession(request);
		order.setCreaterId(user.getId());
		order.setIp(CusAccessObjectUtil.getIpAddress(request));
		consignorService.modifyOrder(order);
		return result;
	}
	
	@Log
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "orderNo")})
	@ResponseBody
	@RequestMapping("/delete.json")
	public JsonResult delete(OrderParam order,HttpServletRequest request, JsonResult result) throws Exception {
		User user=localService.getUserInSession(request);
		order.setCreaterId(user.getId());
		order.setIp(CusAccessObjectUtil.getIpAddress(request));
		consignorService.deleteOrder(order);
		return result;
	}
	
	@Log
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "orderNo[]")})
	@ResponseBody
	@RequestMapping("/deleteList.json")
	public JsonResult deleteList(HttpServletRequest request, JsonResult result) throws Exception {
		OrderParam order = new OrderParam();
		User user=localService.getUserInSession(request);
		order.setCreaterId(user.getId());
		order.setIp(CusAccessObjectUtil.getIpAddress(request));
		String[] orderNos = request.getParameterValues("orderNo[]");
		logger.info("货主订单批量删除（deleteList），入参："+orderNos);
		for(String id:orderNos){
			order.setOrderNo(id);
			consignorService.deleteOrder(order);
		}
		return result;
	}
	
	@Log
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "vehicleTypeId"),
			@ValidateFiled(index = 0, notNull = true, filedName = "cityId"),
			@ValidateFiled(index = 0, notNull = true, filedName = "distance")})
	@RequestMapping("/calculatePrice.json")
	@ResponseBody
	public JsonResult calculatePrice(HttpServletRequest request, JsonResult result) throws Exception {
		Map<String, Object> param = new HashMap<>();
		param.put("vehicleTypeId", request.getParameter("vehicleTypeId"));
		param.put("cityId", request.getParameter("cityId"));
		param.put("distance", request.getParameter("distance"));
		result.setData(consignorService.calculatePrice(param));
		return result;
	}
	
	@Log
	/*@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "startTime"),
			@ValidateFiled(index = 0, notNull = true, filedName = "endTime")})*/
	@TimeZone(type="preHandle")
	@ResponseBody
	@RequestMapping("/export.json")
	public JsonResult export(HttpServletRequest request, JsonResult result) throws Exception {
		User user=localService.getUserInSession(request);
		Map<String, Object> map = new HashMap<>();
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		map.put("startSiteName",request.getParameter("startSiteName"));
		map.put("middleOrEnd",request.getParameter("middleOrEnd"));
		map.put("driver",request.getParameter("driver"));
		map.put("orderNo",request.getParameter("orderNo"));
		map.put("plateNo",request.getParameter("plateNo"));
		map.put("cargoType",request.getParameter("cargoType"));
		map.put("status", request.getParameter("status"));
		map.put("deptDNA", user.getDeptDNA());
		map.put("userId", user.getId());
		map.put("timeZone", user.getGmtZone());
		
		if(endTime!=null||startTime!=null){
			java.util.TimeZone timeZone = java.util.TimeZone.getTimeZone("UTC");
			SimpleDateFormat format = new SimpleDateFormat(TimeUtil.FORMAT_DATE);
			format.setTimeZone(timeZone);
			String now = format.format(new Date());//得到当前时间的临时区时间
			String endDate = null;
			String startDate = null;
			if(endTime!=null){
				endDate = endTime.substring(0, 10);//2018-04-16
			}
			if(startTime!=null){
				startDate =startTime.substring(0, 10);
			}
			if(now.equals(endDate)||now.equals(startDate)){//包含今天的查询不查缓存
				map.put("loadCache",false);
			}
		}
		
		
		logger.info("货主订单导出查询（export），入参："+map);
		result.setData(consignorService.exportOrder(map));
		return result;
	}
	
	/**
	 * 获取订单支付对象
	 * @param request
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@Log
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "channel"),
			@ValidateFiled(index = 0, notNull = true, filedName = "amount"),
			@ValidateFiled(index = 0, notNull = true, filedName = "currency"),
			@ValidateFiled(index = 0, notNull = true, filedName = "subject"),
			@ValidateFiled(index = 0, notNull = true, filedName = "body"),
			@ValidateFiled(index = 0, notNull = true, filedName = "clientIp"),
			@ValidateFiled(index = 0, notNull = true, filedName = "orderNo"),
			@ValidateFiled(index = 0, notNull = true, filedName = "successUrl")})
	@ResponseBody
	@RequestMapping("/charge.json")
	public JsonResult charge(ChargeBean bean,HttpServletRequest request, JsonResult result) throws Exception {
		User user=localService.getUserInSession(request);
		String orderNo = new Date().getTime() + "u" + user.getId();
		if (orderNo.length() > 20) {// 超过20位从前面截取，保留用户ID信息
			orderNo = orderNo.substring(orderNo.length() - 20);
		}
		bean.setOrderNo(orderNo);
		Charge charge = PingFactory.getInstance().getChargeExample().createCharge(bean);
		logger.info("货主生成charge（charge），入参："+bean);
		result.setData(charge);
		return result;
	}
	
	
	
	@Log
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "chargeId"),
			@ValidateFiled(index = 0, notNull = true, filedName = "amount"),
			@ValidateFiled(index = 0, notNull = true, filedName = "orderNo")})
	@RequestMapping("/payResult.json")
	@ResponseBody
	public JsonResult payResult(ChargeBean charge,HttpServletRequest request, JsonResult result) throws Exception {
		//User user=localService.getUserInSession(request);
		String amounts = cacheService.getStringData(charge.getChargeId());
		logger.info("========>amountCache:"+amounts+" ========>amount:"+charge.getAmount());
		if(amounts!=null){
			int amoutsInt = Integer.parseInt(amounts);
			if(amoutsInt==charge.getAmount()){//付款成功，更新订单支付状态
				//更新订单状态放到webhooks回调中
			}else {
				throw new ServiceException(ExceptionCode.ERROR_ORDER_AMOUNT_DIFF,"实付与应付金额不匹配","实付："+amoutsInt+"|应付："+charge.getAmount());
			}
		}else {//暂未收到webhooks异步通知
			result.setCode(201);
		}
		return result;
	}
	
	/**
	 * 接收ping++返回的支付events事件，用于异步通知支付结果使用
	 * 
	 * @param param
	 *            请求参数json String
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/chargeNotice.json", method = RequestMethod.POST)
	public void events(@RequestBody String data, HttpServletResponse response, HttpServletRequest request) {
		System.out.println(data);
		Enumeration header = request.getHeaderNames();
		String signature = null;
		while (header.hasMoreElements()) {
			String name = header.nextElement().toString();
			// System.out.println(name);
			name = name.toLowerCase();
			System.out.println("=====>" + name);
			if (name.equals("X-Pingplusplus-Signature".toLowerCase())) {
				signature = request.getHeader(name);
			}
		}

		if (signature == null) {
			response.setStatus(500);
			return;
		}
		boolean verify = false;
		try {
			PublicKey pubKey = WebhooksVerifyExample.getPubKey();
			verify = WebhooksVerifyExample.verifyData(data, signature, pubKey);//
			// 验证webhooks数据
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(500);
			return;
		}
		if (!verify) {
			response.setStatus(500);
			return;
		}

		Event event = Webhooks.eventParse(data);
		if (event.getType().equals("charge.succeeded")) {
			// 页面获取为true得知支付成功 只处理微信扫码和支付宝pc支付成功
			Charge charge = (Charge) event.getData().getObject();
			if (charge.getChannel().equals("wx_pub_qr") || charge.getChannel().equals("alipay_pc_direct")) {
				cacheService.setStringData(charge.getId(), charge.getAmount() + "", 24 * 3600);// 保存24小时
				
				//修改订单状态
				Map<String, Object> metadata = charge.getMetadata();
				OrderParam param = new OrderParam();
				param.setOrderNo(metadata.get("business_order_no").toString());
				param.setPayStatus(20);
				param.setCreaterId(Integer.parseInt(metadata.get("user_id").toString()));
				try {
					consignorService.modifyOrder(param);
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				
				logger.info(
						"---------------------------------chargeNotice---------------------------------orderId="
								+ charge.getId() + " amount=" + charge.getAmount());
			}
		}
	}
}
