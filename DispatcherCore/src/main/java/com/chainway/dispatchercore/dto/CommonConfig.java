package com.chainway.dispatchercore.dto;

import java.io.Serializable;

public class CommonConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3798259985978766471L;

	private Integer id;
	private String key;
	private String value;
	private String i18nKey;
	private String type;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getI18nKey() {
		return i18nKey;
	}
	public void setI18nKey(String i18nKey) {
		this.i18nKey = i18nKey;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
