package com.chainway.dispatchercore.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 订单生命周期
 * @author chainwayits
 * @date 2018年3月27日
 */
public class OrderLifecycle implements Serializable {

	/**序列化*/
	private static final long serialVersionUID = -7357670519333632553L;
	/**订单编号*/
	private String orderNo;
	/**操作*/
	private Integer operation;
	/**操作时间*/
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date operateTime;
	/**操作人ID*/
	private Integer operatorId;
	/**操作人姓名*/
	private String operatorName;
	/**操作人电话*/
	private String operatorPhone;
	/**来源IP*/
	private String ip;
	/**内容*/
	private String content;
	/**操作后订单的状态*/
	private Integer orderStatus;
	/**操作后订单的支付状态*/
	private Integer payStatus;
	
	
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public Integer getOperation() {
		return operation;
	}
	public void setOperation(Integer operation) {
		this.operation = operation;
	}
	@JsonSerialize(using=YYYYMMddHHmm.class)
	public Date getOperateTime() {
		return operateTime;
	}
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}
	public Integer getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(Integer operatorId) {
		this.operatorId = operatorId;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
	public Integer getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}
	public String getOperatorPhone() {
		return operatorPhone;
	}
	public void setOperatorPhone(String operatorPhone) {
		this.operatorPhone = operatorPhone;
	}
}