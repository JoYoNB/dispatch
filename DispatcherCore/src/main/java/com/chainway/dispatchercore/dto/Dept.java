package com.chainway.dispatchercore.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Dept implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9046167211295023366L;

	//部门角色，1结算后台，2货主，3承运商，4承运商&货主
	public final static int ROLE_SYSTEM=1;
	public final static int ROLE_CONSIGNOR=2;
	public final static int ROLE_CARRIER=3;
	public final static int ROLE_DUAL=4;
	
	private Integer id;
	private String name;
	private Integer parentId;
	private Integer status;
	private String dna;
	private String contacter;
	private String phone;
	private String email;
	private Integer creater;
	private Integer updater;
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	private Integer thirdPartyDept;
	private Integer level;
	
	private String parentName;
	
	private Boolean isCarrier;
	private Boolean isConsignor;
	private Boolean isDual;
	
	private Integer role;//部门角色，1结算后台，2货主，3承运商，4承运商&货主
	
	@JsonSerialize(using=YYYYMMddHHmm.class)
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
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
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getDna() {
		return dna;
	}
	public void setDna(String dna) {
		this.dna = dna;
	}
	public String getContacter() {
		return contacter;
	}
	public void setContacter(String contacter) {
		this.contacter = contacter;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	public Integer getThirdPartyDept() {
		return thirdPartyDept;
	}
	public void setThirdPartyDept(Integer thirdPartyDept) {
		this.thirdPartyDept = thirdPartyDept;
	}
	public Boolean getIsCarrier() {
		return isCarrier;
	}
	public void setIsCarrier(Boolean isCarrier) {
		this.isCarrier = isCarrier;
	}
	public Boolean getIsConsignor() {
		return isConsignor;
	}
	public void setIsConsignor(Boolean isConsignor) {
		this.isConsignor = isConsignor;
	}
	public Boolean getIsDual() {
		return isDual;
	}
	public void setIsDual(Boolean isDual) {
		this.isDual = isDual;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Integer getRole() {
		return role;
	}
	public void setRole(Integer role) {
		this.role = role;
	}
	
	
}
