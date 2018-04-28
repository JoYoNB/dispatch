package com.chainway.dispatcherservice.dto;

import java.util.List;

/**
 * 承运商订单匹配规则
 * @author Administrator
 */
public class CarrierMatchRule {
	// 区域编号
	List<String> districts;
	// 运输最小里程
	double mileageMin;
	// 运输最大里程
	double mileageMax;
	// 可接货物类型
	List<Integer> goodsTypes;

	public List<String> getDistricts() {
		return districts;
	}

	public void setDistricts(List<String> districts) {
		this.districts = districts;
	}

	public double getMileageMin() {
		return mileageMin;
	}

	public void setMileageMin(double mileageMin) {
		this.mileageMin = mileageMin;
	}

	public double getMileageMax() {
		return mileageMax;
	}

	public void setMileageMax(double mileageMax) {
		this.mileageMax = mileageMax;
	}

	public List<Integer> getGoodsTypes() {
		return goodsTypes;
	}

	public void setGoodsTypes(List<Integer> goodsTypes) {
		this.goodsTypes = goodsTypes;
	}
	public boolean isMatchedOrder(Order order){
		// 1. 判断货物类型是否满足
		if(goodsTypes == null || !goodsTypes.contains(order.getGoodsType())){
			return false;
		}
		// 2. 判断所属区域是否满足
		if(districts == null){
			return false;
		}
		String districtId = order.getDistrictId();
		boolean districtMatched = false;
		for(String d : districts){
			if(districtId.startsWith(d)){
				districtMatched = true;
				break;
			}
		}
		if(!districtMatched){
			return false;
		}
		// 3. 判断里程是否满足
		double distance = order.getDistance();
		if(distance < mileageMin && distance > mileageMax){
			return false;
		}
		return true;
	}

}
