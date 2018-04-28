package com.chainway.dispatcherservice.service.impl.carrier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.chainway.dispatchercore.dto.FileTemplate;
import com.chainway.dispatcherservice.biz.dao.CarrierStatsDao;
import com.chainway.dispatcherservice.service.carrier.CarrierStatsService;
import com.chainway.fileservice.service.FileService;

@Component
@Service
public class CarrierStatServiceImpl implements CarrierStatsService {

	@Autowired
	private CarrierStatsDao carrierStatsDao;
	
	@Reference(timeout=60000, check=false)  
	FileService fileService;
	
	@Override
	public int getTotalFinishedOrderNum(Map<String, Object> map) {
		return carrierStatsDao.getTotalFinishedOrderNum(map);
	}

	@Override
	public List<Map<String, Object>> listConsignorOrderRanking(Map<String, Object> map) {
		return carrierStatsDao.listConsignorOrderRanking(map);
	}

	@Override
	public List<Map<String, Object>> listFinishedOrderDist(Map<String, Object> map) {
		return carrierStatsDao.listFinishedOrderDist(map);
	}

	@Override
	public Map<String, Object> listDeptOrderDist(Map<String, Object> map) {
		Map<String, Object> result = new HashMap<>();
		String deptDNA = (String) map.get("deptDNA");
		int length = getDnaLength(deptDNA);
		map.put("length", length);
		List<Map<String,Object>> deptOrders = carrierStatsDao.listDeptOrderDist(map);
		result.put("deptOrders", deptOrders);
		List<Map<String, Object>> temp =  carrierStatsDao.listSubDepts(map);
		List<String> subDepts = new ArrayList<String>();
		if(temp != null){
			for(Map<String, Object> t : temp){
				subDepts.add((String)t.get("deptName"));
			}
		}
		result.put("subDepts", subDepts);
		return result;
	}

	private int getDnaLength(String deptDNA) {
		int len = 0;
		for(int i=0; i<deptDNA.length(); i++){
			if(deptDNA.charAt(i) == '-'){
				len++;
			}
		}
		return len;
	}

	@Override
	public List<Map<String, Object>> settleStats(Map<String, Object> param) {
		return carrierStatsDao.statsSettlement(param);
	}

	@Override
	public List<Map<String, Object>> listDeptDayOrderDist(Map<String, Object> param) {
		String deptDNA = (String) param.get("deptDNA");
		int length = getDnaLength(deptDNA);
		param.put("length", length);
		return carrierStatsDao.listDeptDayOrderDist(param);
	}

	@Override
	public List<Map<String, Object>> listSubDepts(Map<String, Object> param) {
		return carrierStatsDao.listSubDepts(param);
	}

	@Override
	public Map<String, Object> exportOrderStats(Map<String, Object> param) throws Exception {
		Map<String, Object> ret = new HashMap<>();
		List<Object> list = new ArrayList<>();
		List<Map<String, Object>> orderList = structureTableDist(param);
		if (orderList != null && orderList.size() > 0) {
			for (Map<String, Object> map : orderList) {
				list.add(map);
			}
		}
		FileTemplate tpl = new FileTemplate();
		tpl.setCode("carrier_order_stats");
		
		// 生成导出订单个规则
		String name = "订单统计.xlsx";
		String cells = "[{textField:\"日期\", valueField:\"day\"}";
		List<Map<String, Object>> depts = carrierStatsDao.listSubDepts(param);
		for(int i=0; i<depts.size(); i++){
			Map<String, Object> dept = depts.get(i);
			cells += ",{textField:\""+ dept.get("deptName") +"\", valueField:\""+ dept.get("deptId") +"\"}";
		}
		cells += ",{textField:\"总计\", valueField:\"sum\"}";
		cells += "]";
		String verifyRule = "{name:"+name+", cells:"+ cells+"}";
		
		
		tpl.setVerifyRule(verifyRule);
		String token  = "12345678";
		Map<String, Object> map = new HashMap<>();
		map.put("token", token);
		map.put("timeZone", param.get("timeZone"));
		String url = fileService.export(tpl, list, map);
		ret.put("url", url);
		return ret;
	}

	@Override
	public List<Map<String, Object>> structureTableDist(Map<String, Object> param) {
		List<Map<String, Object>> list = listDeptDayOrderDist(param);
		List<Map<String, Object>> subDepts = listSubDepts(param);
		String daysArrStr = (String) param.get("daysArrStr");
		List<Map<String, Object>> newList = new ArrayList<Map<String,Object>>();
		// 根据前台传来日期，构造表格数据
		if(StringUtils.isNotEmpty(daysArrStr)){
			String[] daysArr = daysArrStr.split(",");
			for(int i=0; i<daysArr.length; i++){
				String day = daysArr[i];
				Map<String,Object> row = new HashMap<>();
				row.put("day", day);
				int sum = 0;
				for(Map<String, Object> subDept : subDepts){
					String deptName = String.valueOf(subDept.get("deptName"));
					String deptId = String.valueOf(subDept.get("deptId"));
					boolean deptDataExist = false;
					for(Map<String, Object> map : list){
						// 日期和部门名称相同
						if(map.get("day").equals(day) && map.get("deptName").equals(deptName)){
							long amount = (long)map.get("amount");
							row.put(deptId, amount);
							sum += amount;
							deptDataExist = true;
							break;
						}
					}
					// 子部门数据不存在设为0
					if(!deptDataExist){
						row.put(deptId, 0);
						sum += 0;
					}
				}
				row.put("sum", sum);
				newList.add(row);
			}
		}
		return newList;
	}

	@Override
	public int getConsignorOrderRankingCount(Map<String, Object> param) {
		return carrierStatsDao.getConsignorOrderRankingCount(param);
	}

	@Override
	public int getCustomerCount(Map<String, Object> param) {
		return carrierStatsDao.getCustomerCount(param);
	}

	@Override
	public Map<String, Object> exportCustomerStats(Map<String, Object> param) throws Exception {
		Map<String, Object> ret = new HashMap<>();
		List<Object> list = new ArrayList<>();
		List<Map<String, Object>> orderList = listConsignorOrderRanking(param);
		if (orderList != null && orderList.size() > 0) {
			for (Map<String, Object> map : orderList) {
				list.add(map);
			}
		}
		FileTemplate tpl = new FileTemplate();
		tpl.setCode("carrier_customer_stats");
		String token  = "12345678";
		Map<String, Object> map = new HashMap<>();
		map.put("token", token);
		map.put("timeZone", param.get("timeZone"));
		String url = fileService.export(tpl, list, map);
		ret.put("url", url);
		return ret;
	}


	@Override
	public int getSettleStatsCount(Map<String, Object> paramMap) {
		return carrierStatsDao.getSettleStatsCount(paramMap);
	}
}
