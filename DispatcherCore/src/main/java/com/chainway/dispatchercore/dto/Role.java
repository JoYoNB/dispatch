package com.chainway.dispatchercore.dto;

import java.io.Serializable;
import java.util.List;

public class Role implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4571178680841876410L;
	//角色类型（system-系统角色,common-普通角色，custom-自定义）
	public final static String TYPE_SYSTEM="system";
	public final static String TYPE_COMMON="common";
	public final static String TYPE_CUSTOM="custom";
	
	public final static String CODE_ADMIN="admin";
	public final static String CODE_CARRIER="carrier";
	public final static String CODE_CONSIGNOR="consignor";
	public final static String CODE_DUAL="dual";
	
	private Integer id;
	private String code;
	private String name;
	private Integer status;
	private String type;
	private Integer deptId;
	private String remark;
	private Integer creater;
	private Integer updater;
	
	private String deptName;
	
	private List<Auth>authList;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getDeptId() {
		return deptId;
	}
	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public List<Auth> getAuthList() {
		return authList;
	}
	public void setAuthList(List<Auth> authList) {
		this.authList = authList;
	}
	public Integer getCreater() {
		return creater;
	}
	public void setCreater(Integer creater) {
		this.creater = creater;
	}
	public Integer getUpdater() {
		return updater;
	}
	public void setUpdater(Integer updater) {
		this.updater = updater;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	
}
