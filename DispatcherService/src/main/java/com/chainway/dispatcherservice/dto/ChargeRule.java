package com.chainway.dispatcherservice.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.chainway.dispatchercore.dto.YYYYMMddHHmm;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 计价规则实体类
 * @author chainwayits
 * @date 2018年3月21日
 */
public class ChargeRule implements Serializable {

	/**序列化*/
	private static final long serialVersionUID = 5114705847531627170L;
	/**规则ID*/
	private Integer id;
	/**车型ID*/
	private Integer vehicleTypeId;
	/**车型名称*/
	private String vehicleTypeName;
	/**车型名称国际化key*/
	private String vehicleTypeI18nKey;
	/**省ID*/
	private Integer provinceId;
	/**省名称*/
	private String provinceName;
	/**省名称国际化key*/
	private String provinceI18nKey;
	/**城市ID*/
	private Integer cityId;
	/**城市名称*/
	private String cityName;
	/**城市名称国际化key*/
	private String cityI18nKey;
	/**起步里程*/
	private Double startingMileage;
	/**起步价*/
	private Double startingPrice;
	/**超过过起步里程后单价 单位：元/公里*/
	private Double price;
	/**创建人ID*/
	private Integer createrId;
	/**创建人姓名*/
	private String createrName;
	/**修改人ID*/
	private Integer updaterId;
	/**修改人姓名*/
	private String updaterName;
	/**创建时间*/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	/**更新时间*/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@JsonSerialize(using=YYYYMMddHHmm.class)
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@JsonSerialize(using=YYYYMMddHHmm.class)
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getCreaterName() {
		return createrName;
	}
	public void setCreaterName(String createrName) {
		this.createrName = createrName;
	}
	public String getUpdaterName() {
		return updaterName;
	}
	public void setUpdaterName(String updaterName) {
		this.updaterName = updaterName;
	}
	public Integer getCreaterId() {
		return createrId;
	}
	public void setCreaterId(Integer createrId) {
		this.createrId = createrId;
	}
	public Integer getUpdaterId() {
		return updaterId;
	}
	public void setUpdaterId(Integer updaterId) {
		this.updaterId = updaterId;
	}
	public Integer getVehicleTypeId() {
		return vehicleTypeId;
	}
	public void setVehicleTypeId(Integer vehicleTypeId) {
		this.vehicleTypeId = vehicleTypeId;
	}
	public String getVehicleTypeName() {
		return vehicleTypeName;
	}
	public void setVehicleTypeName(String vehicleTypeName) {
		this.vehicleTypeName = vehicleTypeName;
	}
	public Integer getProvinceId() {
		return provinceId;
	}
	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public Integer getCityId() {
		return cityId;
	}
	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public Double getStartingMileage() {
		return startingMileage;
	}
	public void setStartingMileage(Double startingMileage) {
		this.startingMileage = startingMileage;
	}
	public Double getStartingPrice() {
		return startingPrice;
	}
	public void setStartingPrice(Double startingPrice) {
		this.startingPrice = startingPrice;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getVehicleTypeI18nKey() {
		return vehicleTypeI18nKey;
	}
	public void setVehicleTypeI18nKey(String vehicleTypeI18nKey) {
		this.vehicleTypeI18nKey = vehicleTypeI18nKey;
	}
	public String getProvinceI18nKey() {
		return provinceI18nKey;
	}
	public void setProvinceI18nKey(String provinceI18nKey) {
		this.provinceI18nKey = provinceI18nKey;
	}
	public String getCityI18nKey() {
		return cityI18nKey;
	}
	public void setCityI18nKey(String cityI18nKey) {
		this.cityI18nKey = cityI18nKey;
	} 
}
