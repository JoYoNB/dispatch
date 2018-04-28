package com.chainway.dispatcherservice.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class OrderParam implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3910842947558354913L; 
	
	private String orderNo; //订单号 16位随机串
	private Integer cargoType; //货物类型ID
	private Integer vehicleType; //车辆类型ID
	private Double weight; //货物重量（吨）
	private Double volume; //体积（方）
	private Integer amount; //数量（件）
	private Integer startSiteId; //起点站点ID
	private Integer endSiteId; //终点站点id
	private String senderName; //发货人姓名
	private String senderPhone; //发货人电话号码
	private String districtId; //起点行政区域id
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date pickupTime; //提货时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date arriveTime; //送达时间（预计到达时间）
	private Integer feeType; //费用类型 0核算、1约定
	private Double fee; //订单费用
	private Integer orderStatus; //订单状态
	private Integer status; //订单状态（0无效、1有效、2已删除）
	private Integer payStatus;//订单支付状态（10未付款、20已付款、30已结算、40待退款、50已退款）
	private String remark; //备注
	private List<SiteParam> sites; //配送点信息列表
	
	private Integer createrId; //创建用户id
	private Integer consignorDept; //货主一级部门id
	private Integer deptId; //货主用户部门id
	private Double distance; //路线距离（公里）
	private String ip;
	
	private String orderOperation;//订单操作
	
	public Integer getCargoType() {
		return cargoType;
	}
	public void setCargoType(Integer cargoType) {
		this.cargoType = cargoType;
	}
	public Integer getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(Integer vehicleType) {
		this.vehicleType = vehicleType;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public Double getVolume() {
		return volume;
	}
	public void setVolume(Double volume) {
		this.volume = volume;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public Integer getStartSiteId() {
		return startSiteId;
	}
	public void setStartSiteId(Integer startSiteId) {
		this.startSiteId = startSiteId;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getSenderPhone() {
		return senderPhone;
	}
	public void setSenderPhone(String senderPhone) {
		this.senderPhone = senderPhone;
	}
	public String getdistrictId() {
		return districtId;
	}
	public void setdistrictId(String districtId) {
		this.districtId = districtId;
	}
	public Date getPickupTime() {
		return pickupTime;
	}
	public void setPickupTime(Date pickupTime) {
		this.pickupTime = pickupTime;
	}
	public Date getArriveTime() {
		return arriveTime;
	}
	public void setArriveTime(Date arriveTime) {
		this.arriveTime = arriveTime;
	}
	public Integer getFeeType() {
		return feeType;
	}
	public void setFeeType(Integer feeType) {
		this.feeType = feeType;
	}
	public Double getFee() {
		return fee;
	}
	public void setFee(Double fee) {
		this.fee = fee;
	}
	public Integer getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public List<SiteParam> getSites() {
		return sites;
	}
	public void setSites(List<SiteParam> sites) {
		this.sites = sites;
	}
	public Integer getCreaterId() {
		return createrId;
	}
	public void setCreaterId(Integer createrId) {
		this.createrId = createrId;
	}
	public Integer getConsignorDept() {
		return consignorDept;
	}
	public void setConsignorDept(Integer consignorDept) {
		this.consignorDept = consignorDept;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public Integer getEndSiteId() {
		return endSiteId;
	}
	public void setEndSiteId(Integer endSiteId) {
		this.endSiteId = endSiteId;
	}
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	public Integer getDeptId() {
		return deptId;
	}
	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getOrderOperation() {
		return orderOperation;
	}
	public void setOrderOperation(String orderOperation) {
		this.orderOperation = orderOperation;
	}

}
