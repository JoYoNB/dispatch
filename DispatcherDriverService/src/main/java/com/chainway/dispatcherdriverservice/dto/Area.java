package com.chainway.dispatcherdriverservice.dto;

import java.io.Serializable;

/**
 * 车型实体
 * @author chainwayits
 * @date 2018年3月19日
 */
public class Area implements Serializable {

	/**序列化*/
	private static final long serialVersionUID = 6894228260598051890L;
	/**省ID*/
	private String provinceId;
	/**省名称*/
	private String provinceName;
	/**省名称国际化Key*/
	private String provinceI18nKey;
	/**城市ID*/
	private String cityId;
	/**城市名称*/
	private String cityName;
	/**城市名称国际化Key*/
	private String cityI18nKey;
	/**县区ID*/
	private String districtId;
	/**县区名称*/
	private String districtName;
	/**县区名称国际化Key*/
	private String districtI18nKey;
	public String getProvinceId() {
		return provinceId;
	}
	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getDistrictId() {
		return districtId;
	}
	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}
	public String getDistrictName() {
		return districtName;
	}
	public void setDistrictName(String districtName) {
		this.districtName = districtName;
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
	public String getDistrictI18nKey() {
		return districtI18nKey;
	}
	public void setDistrictI18nKey(String districtI18nKey) {
		this.districtI18nKey = districtI18nKey;
	}
}
