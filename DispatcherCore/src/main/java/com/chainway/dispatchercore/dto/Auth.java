package com.chainway.dispatchercore.dto;

import java.io.Serializable;

public class Auth implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6370658659084874440L;

	private Integer id;
	private String name;
	private String code;
	private Integer parentId;
	private String i18nKey;
	
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public String getI18nKey() {
		return i18nKey;
	}
	public void setI18nKey(String i18nKey) {
		this.i18nKey = i18nKey;
	}
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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

}
