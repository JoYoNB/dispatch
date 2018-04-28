package com.chainway.dispatchercore.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.chainway.dispatchercore.dto.YYYYMMddHHmm;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @Author: chainway
 *
 * @Date: 2018年3月19日
 * @Description:车辆实体类
 */
public class Vehicle implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -901227712405024509L;
	//车辆id
	private Integer vehicleId;
	//对应其他平台的车辆id
	private Integer vehicleIdOther;
	//车牌号
	private String plateNo;
	//车辆类型id
	private Integer vehicleTypeId;
	//设备pn
	private String equipmentPn;
	//部门id
	private Integer deptId;
	//状态
	private Integer status;
	//运载百分比
	private Integer loadRate;
	//载质量利用系数
	private Double weigthUseFactor;
	//车辆最小转弯直径（m）
	private Integer swerveRadiusMin;
	//比功率（kW/t）
	private Double powerRate;
	//货厢内部长度（mm）
	private Integer vehicleInsideLength;
	//货厢内部宽度（mm）
	private Integer vehicleInsideWidth;
	//货厢内部高度（mm）
	private Integer vehicleInsideHeight;
	//最大允许总质量（kg）
	private Integer vehicleWeightMax;
	//最大载货重量（kg）
	private Integer carryWeigthMax;
	//整车总长（mm）
	private Integer vehicleLength;
	//车辆图片url
	private String imageUrl;
	//创建人
	private Integer creater;
	//创建时间
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	//更新人
	private Integer updater;
	//更新时间
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	//备注
	private String remark;
	//载货类型ids
	private List<Integer> carryTypeIds;
	
	public List<Integer> getCarryTypeIds() {
		return carryTypeIds;
	}
	public void setCarryTypeIds(List<Integer> carryTypeIds) {
		this.carryTypeIds = carryTypeIds;
	}
	public Integer getVehicleId() {
		return vehicleId;
	}
	public void setVehicleId(Integer vehicleId) {
		this.vehicleId = vehicleId;
	}
	public Integer getVehicleIdOther() {
		return vehicleIdOther;
	}
	public void setVehicleIdOther(Integer vehicleIdOther) {
		this.vehicleIdOther = vehicleIdOther;
	}
	public String getPlateNo() {
		return plateNo;
	}
	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
	}
	public Integer getVehicleTypeId() {
		return vehicleTypeId;
	}
	public void setVehicleTypeId(Integer vehicleTypeId) {
		this.vehicleTypeId = vehicleTypeId;
	}
	
	public String getEquipmentPn() {
		return equipmentPn;
	}
	public void setEquipmentPn(String equipmentPn) {
		this.equipmentPn = equipmentPn;
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
	public Integer getLoadRate() {
		return loadRate;
	}
	public void setLoadRate(Integer loadRate) {
		this.loadRate = loadRate;
	}
	public Double getWeigthUseFactor() {
		return weigthUseFactor;
	}
	public void setWeigthUseFactor(Double weigthUseFactor) {
		this.weigthUseFactor = weigthUseFactor;
	}
	public Integer getSwerveRadiusMin() {
		return swerveRadiusMin;
	}
	public void setSwerveRadiusMin(Integer swerveRadiusMin) {
		this.swerveRadiusMin = swerveRadiusMin;
	}
	public Double getPowerRate() {
		return powerRate;
	}
	public void setPowerRate(Double powerRate) {
		this.powerRate = powerRate;
	}
	public Integer getVehicleInsideLength() {
		return vehicleInsideLength;
	}
	public void setVehicleInsideLength(Integer vehicleInsideLength) {
		this.vehicleInsideLength = vehicleInsideLength;
	}
	public Integer getVehicleInsideWidth() {
		return vehicleInsideWidth;
	}
	public void setVehicleInsideWidth(Integer vehicleInsideWidth) {
		this.vehicleInsideWidth = vehicleInsideWidth;
	}
	public Integer getVehicleInsideHeight() {
		return vehicleInsideHeight;
	}
	public void setVehicleInsideHeight(Integer vehicleInsideHeight) {
		this.vehicleInsideHeight = vehicleInsideHeight;
	}
	public Integer getVehicleWeightMax() {
		return vehicleWeightMax;
	}
	public void setVehicleWeightMax(Integer vehicleWeightMax) {
		this.vehicleWeightMax = vehicleWeightMax;
	}
	public Integer getCarryWeigthMax() {
		return carryWeigthMax;
	}
	public void setCarryWeigthMax(Integer carryWeigthMax) {
		this.carryWeigthMax = carryWeigthMax;
	}
	public Integer getVehicleLength() {
		return vehicleLength;
	}
	public void setVehicleLength(Integer vehicleLength) {
		this.vehicleLength = vehicleLength;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getCreater() {
		return creater;
	}
	public void setCreater(Integer creater) {
		this.creater = creater;
	}
	@JsonSerialize(using=YYYYMMddHHmm.class)
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Integer getUpdater() {
		return updater;
	}
	public void setUpdater(Integer updater) {
		this.updater = updater;
	}
	@JsonSerialize(using=YYYYMMddHHmm.class)
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
}
