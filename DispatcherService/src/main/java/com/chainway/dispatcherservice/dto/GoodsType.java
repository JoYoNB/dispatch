package com.chainway.dispatcherservice.dto;

import java.io.Serializable;

/**
 * 货物类型实体
 * @author chainwayits
 * @date 2018年3月19日
 */
public class GoodsType implements Serializable {

	/**序列化*/
	private static final long serialVersionUID = -179217066386070792L;

	/**货物类型ID*/
	private Integer id;
	/**货物类型名称*/
	private String name;
	/**货物类型名称国际化key*/
	private String i18nKey;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getI18nKey() {
		return i18nKey;
	}
	public void setI18nKey(String i18nKey) {
		this.i18nKey = i18nKey;
	}
}