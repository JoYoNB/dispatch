package com.chainway.dispatcherservice.dto;

import java.io.Serializable;


public class SiteParam implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8583131375139639189L;  
	
	private String name; //站点名称
	private Integer deptId; //用户部门id
	private Integer consignorDept; //货主一级部门
	private String linkMan; //联系人名称
	private String linkPhone;//联系电话
	private Integer linkId; //联系人id
	private String coordinate; //经纬度坐标点
	private String address; //站点位置
	private String province; //省名称
	private String provinceId; //省字典id
	private String city; //城市名称
	private String cityId; //城市字典id
	private String districtId; //地区字典id
	private String district; //区名称
	private Integer siteId; //配送点ID
	private Integer siteType; //站点类型（1：起点，2：中途卸货点，3：终点）
	private Integer uploadNum; //卸货数量
	private Double uploadWeight; //卸货重量
	private Double uploadVolume; //卸货体积
	private String orderNo; //订单编号
	private Integer idxNo; //站点序号(起点默认0，配送点依次递增）
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getDeptId() {
		return deptId;
	}
	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}
	public String getLinkMan() {
		return linkMan;
	}
	public void setLinkMan(String linkMan) {
		this.linkMan = linkMan;
	}
	public String getLinkPhone() {
		return linkPhone;
	}
	public void setLinkPhone(String linkPhone) {
		this.linkPhone = linkPhone;
	}
	public String getCoordinate() {
		return coordinate;
	}
	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public Integer getSiteId() {
		return siteId;
	}
	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public Integer getUploadNum() {
		return uploadNum;
	}
	public void setUploadNum(Integer uploadNum) {
		this.uploadNum = uploadNum;
	}
	public Double getUploadWeight() {
		return uploadWeight;
	}
	public void setUploadWeight(Double uploadWeight) {
		this.uploadWeight = uploadWeight;
	}
	public Double getUploadVolume() {
		return uploadVolume;
	}
	public void setUploadVolume(Double uploadVolume) {
		this.uploadVolume = uploadVolume;
	}
	public Integer getLinkId() {
		return linkId;
	}
	public void setLinkId(Integer linkId) {
		this.linkId = linkId;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public Integer getSiteType() {
		return siteType;
	}
	public void setSiteType(Integer siteType) {
		this.siteType = siteType;
	}
	public Integer getIdxNo() {
		return idxNo;
	}
	public void setIdxNo(Integer idxNo) {
		this.idxNo = idxNo;
	}
	public String getProvinceId() {
		return provinceId;
	}
	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public Integer getConsignorDept() {
		return consignorDept;
	}
	public void setConsignorDept(Integer consignorDept) {
		this.consignorDept = consignorDept;
	}
	public String getDistrictId() {
		return districtId;
	}
	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
}
