package com.chainway.settlementservice.dto;


import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.chainway.dispatchercore.dto.YYYYMMddHHmm;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 描述:t_order表的实体类
 * @version
 * @author:  Administrator
 * @创建时间: 2018-03-23
 */
public class Order implements Serializable{
	
	private static final long serialVersionUID = -7627037453937469240L;

	/** 订单编号 */
    private String orderNo;

    /** 订单状态（0无效、1有效、2已删除） */
    private Integer status;

    /** 订单状态（10待发布、20已发布、30已失效、40已接单、50已分配、60已取消、70待提货、80配送中、90已结束） */
    private Integer orderStatus;

    /** 支付状态（10未付款、20已付款、30已结算、40待退款、50已退款） */
    private Integer payStatus;

    /** 创建人id */
    private Integer creater;

    /** 创建时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 货主一级部门ID */
    private Integer consignorDept;
    
    /** 货主一级部门ID */
    private String consignorDeptName;

    /** 货物类型 */
    private Integer goodsType;
    /** 货物类型名称 */
    private String goodsTypeName;

    /** 车辆类型 */
    private Integer vehicleType;

    /** 重量 */
    private Double weight;

    /** 体积 */
    private Double volume;

    /** 件数 */
    private Integer packageNum;

    /** 发货人姓名 */
    private String senderName;

    /** 发货人电话 */
    private String senderPhone;

    /** 发货站点ID */
    private Integer startSiteId;
    /** 发货站点名称 */
    private String startSiteName;

    /** 终点站点id */
    private Integer endSiteId;
    /** 终点名称 */
    private String endSiteName;
    
    /** 行政区ID */
    private String districtId;

    /** 路线距离 */
    private Double distance;

    /** 费用类型（0核算、1约定） */
    private Integer feeType;

    /** 费用 */
    private Double fee;

    /** 预计提货时间 */
    private Date prePickupTime;

    /** 实际取货时间 */
    private Date pickupTime;

    /** 预计送达时间 */
    private Date preFinishTime;

    /** 结束时间 */
    private Date finishTime;

    /** 接单承运商一级部门 */
    private Integer carrierDept;
    /** 承运商一级部门名称*/
    private String carrierDeptName;
    /** 接单司机所在部门 */
    private Integer driverDept;

    /** 备注 */
    private String remark;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
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

    public Integer getConsignorDept() {
        return consignorDept;
    }

    public void setConsignorDept(Integer consignorDept) {
        this.consignorDept = consignorDept;
    }

    public Integer getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(Integer goodsType) {
        this.goodsType = goodsType;
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

    public Integer getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(Integer packageNum) {
        this.packageNum = packageNum;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName == null ? null : senderName.trim();
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone == null ? null : senderPhone.trim();
    }

    public Integer getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Integer startSiteId) {
        this.startSiteId = startSiteId;
    }

    public Integer getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Integer endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId == null ? null : districtId.trim();
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
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

    public Date getPrePickupTime() {
        return prePickupTime;
    }

    public void setPrePickupTime(Date prePickupTime) {
        this.prePickupTime = prePickupTime;
    }

    public Date getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(Date pickupTime) {
        this.pickupTime = pickupTime;
    }

    public Date getPreFinishTime() {
        return preFinishTime;
    }

    public void setPreFinishTime(Date preFinishTime) {
        this.preFinishTime = preFinishTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getCarrierDept() {
        return carrierDept;
    }

    public void setCarrierDept(Integer carrierDept) {
        this.carrierDept = carrierDept;
    }

    public Integer getDriverDept() {
        return driverDept;
    }

    public void setDriverDept(Integer driverDept) {
        this.driverDept = driverDept;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

	public String getConsignorDeptName() {
		return consignorDeptName;
	}

	public void setConsignorDeptName(String consignorDeptName) {
		this.consignorDeptName = consignorDeptName;
	}

	public String getCarrierDeptName() {
		return carrierDeptName;
	}

	public void setCarrierDeptName(String carrierDeptName) {
		this.carrierDeptName = carrierDeptName;
	}

	public String getGoodsTypeName() {
		return goodsTypeName;
	}

	public void setGoodsTypeName(String goodsTypeName) {
		this.goodsTypeName = goodsTypeName;
	}

	public String getStartSiteName() {
		return startSiteName;
	}

	public void setStartSiteName(String startSiteName) {
		this.startSiteName = startSiteName;
	}

	public String getEndSiteName() {
		return endSiteName;
	}

	public void setEndSiteName(String endSiteName) {
		this.endSiteName = endSiteName;
	}
}