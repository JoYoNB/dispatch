package com.chainway.dispatcherservice.dto;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.chainway.dispatchercore.dto.Dept;
import com.chainway.dispatchercore.dto.YYYYMMddHHmm;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 订单规则实体
 * @author chainwayits
 * @date 2018年3月19日
 */
public class CustomerInfo extends Dept{
	/**序列化*/
	private static final long serialVersionUID = -5158627951062063073L;
	/**管理用户ID*/
	private Integer userId; 
	/**管理用户账号*/
	private String account;
	/**客户公司logo*/
	private String logo;
	/**是否认证*/
	private Integer authStatus;
	/**管理用户简介*/
	private String remark;
	/**时区*/
	private String gmtZone;
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
	/**更新时间*/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	/**结算类型 1单结，2月结*/
	private Integer settleType;
	
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
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getGmtZone() {
		return gmtZone;
	}
	public void setGmtZone(String gmtZone) {
		this.gmtZone = gmtZone;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public Integer getAuthStatus() {
		return authStatus;
	}
	public void setAuthStatus(Integer authStatus) {
		this.authStatus = authStatus;
	}
	public Integer getSettleType() {
		return settleType;
	}
	public void setSettleType(Integer settleType) {
		this.settleType = settleType;
	}
}
