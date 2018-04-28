package com.chainway.dispatchercore.dto;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2394184071041189216L;
	
	private String account;
	private String name;
	private String password;
	
	private Integer id;
	private Integer status;
	private Integer deptId;
	private String email;
	private String phone;
	private Integer roleId;
	private String gmtZone;
	private Integer creater;
	private Integer updater;
	
	private String deptDNA;
	private String deptName;
	private String roleCode;
	private String roleName;
	private Role role;
	
	private Integer merchantDeptId;//用户商户部门（即承运商/货主的顶级部门，如果是后台的用户，则是1）
	
	private boolean isCarrier;
	private boolean isConsignor;
	private boolean isDual;
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getDeptId() {
		return deptId;
	}
	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	public String getGmtZone() {
		return gmtZone;
	}
	public void setGmtZone(String gmtZone) {
		this.gmtZone = gmtZone;
	}
	public String getDeptDNA() {
		return deptDNA;
	}
	public void setDeptDNA(String deptDNA) {
		this.deptDNA = deptDNA;
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
	public Integer getMerchantDeptId() {
		return merchantDeptId;
	}
	public void setMerchantDeptId(Integer merchantDeptId) {
		this.merchantDeptId = merchantDeptId;
	}
	public boolean isCarrier() {
		return isCarrier;
	}
	public void setCarrier(boolean isCarrier) {
		this.isCarrier = isCarrier;
	}
	public boolean isConsignor() {
		return isConsignor;
	}
	public void setConsignor(boolean isConsignor) {
		this.isConsignor = isConsignor;
	}
	public boolean isDual() {
		return isDual;
	}
	public void setDual(boolean isDual) {
		this.isDual = isDual;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
}
