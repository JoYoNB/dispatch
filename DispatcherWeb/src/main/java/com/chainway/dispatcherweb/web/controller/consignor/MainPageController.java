package com.chainway.dispatcherweb.web.controller.consignor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chainway.dispatchercore.annotation.TimeZone;
import com.chainway.dispatchercore.common.Constant;
import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatcherservice.service.ConsignorService;
import com.chainway.dispatcherweb.annotation.Log;
import com.chainway.dispatcherweb.annotation.ValidateFiled;
import com.chainway.dispatcherweb.annotation.ValidateGroup;
import com.chainway.dispatcherweb.biz.service.LocalService;
import com.chainway.dispatcherweb.dto.BaseParam;
import com.chainway.dispatcherweb.dto.JsonResult;

@Controller
@RequestMapping("/consignor/mainpage")
public class MainPageController {
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Reference(timeout=60000, check=false)
	private ConsignorService consignorService;
	@Autowired
	private LocalService localService;

	@Log
	@TimeZone(type = "preHandle")
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "startTime"),
			@ValidateFiled(index = 0, notNull = true, filedName = "endTime") })
	@ResponseBody
	@RequestMapping("/orderStatistics.json")
	public JsonResult orderStatistics(BaseParam param, HttpServletRequest request, JsonResult result) throws Exception {
		User user=localService.getUserInSession(request);
		Map<String, Object> map = new HashMap<>();
		map.put("startTime", param.getStartTime());
		map.put("endTime", param.getEndTime());
		map.put("userZone",Constant.timeZoneMap.get(user.getGmtZone()));
		map.put("deptDNA", user.getDeptDNA());
		logger.info("货主首页订单统计（orderStatistics），入参："+map);
		List<Map<String, Object>> datas=consignorService.orderStatistics(map);
		if(datas!=null&&datas.size()>0){
			Map<Object, Object> rMap = new HashMap<>();
			for(Map<String, Object> data:datas){
				rMap.put(data.get("day"), data.get("total"));
			}
			result.setData(rMap);
		}
		return result;
	}
	
	@Log
	@TimeZone(type = "preHandle")
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "startTime"),
			@ValidateFiled(index = 0, notNull = true, filedName = "endTime") })
	@ResponseBody
	@RequestMapping("/cargoStatistics.json")
	public JsonResult cargoStatistics(BaseParam param, HttpServletRequest request, JsonResult result) throws Exception {
		User user=localService.getUserInSession(request);
		Map<String, Object> map = new HashMap<>();
		map.put("startTime", param.getStartTime());
		map.put("endTime", param.getEndTime());
		map.put("userZone",Constant.timeZoneMap.get(user.getGmtZone()));
		map.put("deptDNA", user.getDeptDNA());
		logger.info("货主首页货物统计（cargoStatistics），入参："+map);
		List<Map<String, Object>> datas = consignorService.cargoStatistics(map);
		if(datas!=null&&datas.size()>0){
			Map<Object, Object> rMap = new HashMap<>();
			Map<Object, Object> piece = new HashMap<>();
			Map<Object, Object> side = new HashMap<>();
			Map<Object, Object> ton = new HashMap<>();
			for(Map<String, Object> data:datas){
				piece.put(data.get("day"), data.get("totalNum"));
				side.put(data.get("day"), data.get("totalVolume"));
				ton.put(data.get("day"), data.get("totalWeight"));
			}
			rMap.put("piece", piece);
			rMap.put("side", side);
			rMap.put("ton", ton);
			result.setData(rMap);
		}
		return result;
	}
	
	@Log
	@TimeZone(type = "preHandle")
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "startTime"),
			@ValidateFiled(index = 0, notNull = true, filedName = "endTime") })
	@ResponseBody
	@RequestMapping("/orderRank.json")
	public JsonResult orderRank(BaseParam param, HttpServletRequest request, JsonResult result) throws Exception {
		User user=localService.getUserInSession(request);
		Map<String, Object> map = new HashMap<>();
		map.put("startTime", param.getStartTime());
		map.put("endTime", param.getEndTime());
		map.put("deptDNA", user.getDeptDNA());
		logger.info("货主首页订单排行（orderRank），入参："+map);
		Map<String, Object> rMap = new HashMap<>();
		rMap.put("list", consignorService.orderRank(map));
		result.setData(rMap);
		return result;
	}
	
	@Log
	@TimeZone(type = "preHandle")
	@ValidateGroup(fileds = { @ValidateFiled(index = 0, notNull = true, filedName = "startTime"),
			@ValidateFiled(index = 0, notNull = true, filedName = "endTime") })
	@ResponseBody
	@RequestMapping("/deliveryCargoRank.json")
	public JsonResult deliveryCargoRank(HttpServletRequest request, JsonResult result) throws Exception {
		User user=localService.getUserInSession(request);
		Map<String, Object> map = new HashMap<>();
		map.put("startTime", request.getParameter("startTime"));
		map.put("endTime",request.getParameter("endTime"));
		map.put("type",request.getParameter("type"));
		map.put("deptDNA", user.getDeptDNA());
		logger.info("货主首页货物排行（deliveryCargoRank），入参："+map);
		Map<String, Object> rMap = new HashMap<>();
		rMap.put("list", consignorService.deliveryCargoRank(map));
		result.setData(rMap);
		return result;
	}
	
	@Log
	@RequestMapping("/totalStatistics.json")
	@ResponseBody
	public JsonResult totalStatistics(HttpServletRequest request, JsonResult result) throws Exception {
		User user=localService.getUserInSession(request);
		Map<String, Object> map = new HashMap<>();
		map.put("deptDNA", user.getDeptDNA());
		logger.info("货主首页头部总数统计（totalStatistics），入参："+map);
		result.setData(consignorService.totalStatistics(map));
		return result;
	}
}
