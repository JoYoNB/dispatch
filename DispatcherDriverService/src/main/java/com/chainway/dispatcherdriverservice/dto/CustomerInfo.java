package com.chainway.dispatcherdriverservice.dto;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.chainway.dispatchercore.dto.User;
import com.chainway.dispatchercore.dto.YYYYMMddHHmm;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 订单规则实体
 * @author chainwayits
 * @date 2018年3月19日
 */
public class CustomerInfo extends User{

	/**序列化*/
	private static final long serialVersionUID = -3182790439322891220L;
	/**注册资金*/
	private Double registeredCapital;
	/**社会信用代码*/
	private String scc;
	/**营业执照照片地址*/
	private String businessLicence;
	/**最小运输里程*/
	private Integer mileageMin;
	/**最大运输里程*/
	private Integer mileageMax;
	/**服务区域集合*/
	private List<Area> Areas;
	/**货物类型集合*/
	private List<GoodsType> goodsTypes;
	/**创建人姓名*/
	private String createrName;
	/**修改人ID*/
	private String updaterName;
	/**创建时间*/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	/**更新时间*/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	public Double getRegisteredCapital() {
		return registeredCapital;
	}
	public void setRegisteredCapital(Double registeredCapital) {
		this.registeredCapital = registeredCapital;
	}
	public String getScc() {
		return scc;
	}
	public void setScc(String scc) {
		this.scc = scc;
	}
	public String getBusinessLicence() {
		return businessLicence;
	}
	public void setBusinessLicence(String businessLicence) {
		this.businessLicence = businessLicence;
	}
	public Integer getMileageMin() {
		return mileageMin;
	}
	public void setMileageMin(Integer mileageMin) {
		this.mileageMin = mileageMin;
	}
	public Integer getMileageMax() {
		return mileageMax;
	}
	public void setMileageMax(Integer mileageMax) {
		this.mileageMax = mileageMax;
	}
	public List<Area> getAreas() {
		return Areas;
	}
	public void setAreas(List<Area> areas) {
		Areas = areas;
	}
	public List<GoodsType> getGoodsTypes() {
		return goodsTypes;
	}
	public void setGoodsTypes(List<GoodsType> goodsTypes) {
		this.goodsTypes = goodsTypes;
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
}
