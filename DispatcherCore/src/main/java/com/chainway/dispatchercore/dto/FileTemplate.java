package com.chainway.dispatchercore.dto;

import java.io.Serializable;
import java.util.Date;

public class FileTemplate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1351716636747487465L;

	private Integer id;
	private String type;
	private String name;
	private String code;
	private String verifyRule;
	private Date createTime;
	private Date updateTime;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public String getVerifyRule() {
		return verifyRule;
	}
	public void setVerifyRule(String verifyRule) {
		this.verifyRule = verifyRule;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
}
