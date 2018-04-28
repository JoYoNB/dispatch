package com.chainway.dispatcherdriverservice.dto;

import java.util.List;

/**
 * 承运商订单匹配规则
 * 
 * @author Administrator
 *
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
	public boolean canOrderMatched(Order order){
		// 1. 
		return false;
	}

}
