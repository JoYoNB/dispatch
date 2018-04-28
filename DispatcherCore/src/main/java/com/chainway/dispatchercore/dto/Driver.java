package com.chainway.dispatchercore.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 描述:t_driver表的实体类
 * @version
 * @author:  Administrator
 * @创建时间: 2018-03-22
 */
public class Driver implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2516329426148438341L;

	/** 司机id */
    private Integer driverId;

    /** 司机名称 */
    private String driverName;

    /** 密码 */
    private String password;

    /** 时区 */
    private String gmtZone;

    /**车辆id  */
    private Integer vehicleId;

    /** 手机号码 */
    private String phoneNo;

    /** 部门id */
    private Integer deptId;

    /** 状态(0-无效，1-有效，2-删除) */
    private Integer status;

    /** 入职时间 */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date entryTime;

    /** 创建时间 */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新时间 */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /** 备注 */
    private String remark;
    /**部门dna */
    private String deptDNA;

    /** 司机在线状态 0-离线 1-在线 */
    private Integer onlineStatus;

    /**
     * @return driver_id 司机id
     */
    public Integer getDriverId() {
        return driverId;
    }

    public String getDeptDNA() {
		return deptDNA;
	}

	public void setDeptDNA(String deptDNA) {
		this.deptDNA = deptDNA;
	}

	/**
     * @param driverId 司机id
     */
    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    /**
     * @return driver_name 司机名称
     */
    public String getDriverName() {
        return driverName;
    }

    /**
     * @param driverName 司机名称
     */
    public void setDriverName(String driverName) {
        this.driverName = driverName == null ? null : driverName.trim();
    }

    /**
     * @return password 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    /**
     * @return gmt_zone 时区
     */
    public String getGmtZone() {
        return gmtZone;
    }

    /**
     * @param gmtZone 时区
     */
    public void setGmtZone(String gmtZone) {
        this.gmtZone = gmtZone == null ? null : gmtZone.trim();
    }

    /**
     * @return vehicle_id 
     */
    public Integer getVehicleId() {
        return vehicleId;
    }

    /**
     * @param vehicleId 
     */
    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    /**
     * @return phone_no 手机号码
     */
    public String getPhoneNo() {
        return phoneNo;
    }

    /**
     * @param phoneNo 手机号码
     */
    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo == null ? null : phoneNo.trim();
    }

    /**
     * @return dept_id 部门id
     */
    public Integer getDeptId() {
        return deptId;
    }

    /**
     * @param deptId 部门id
     */
    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    /**
     * @return status 状态(0-无效，1-有效，2-删除)
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status 状态(0-无效，1-有效，2-删除)
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return entry_time 入职时间
     */
    @JsonSerialize(using=YYYYMMddHHmmss.class)
    public Date getEntryTime() {
        return entryTime;
    }

    /**
     * @param entryTime 入职时间
     */
    public void setEntryTime(Date entryTime) {
        this.entryTime = entryTime;
    }

    /**
     * @return create_time 创建时间
     */
    @JsonSerialize(using=YYYYMMddHHmmss.class)
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return update_time 更新时间
     */
    @JsonSerialize(using=YYYYMMddHHmmss.class)
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * @return remark 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * @return online_status 司机在线状态 0-离线 1-在线
     */
    public Integer getOnlineStatus() {
        return onlineStatus;
    }

    /**
     * @param onlineStatus 司机在线状态 0-离线 1-在线
     */
    public void setOnlineStatus(Integer onlineStatus) {
        this.onlineStatus = onlineStatus;
    }
}