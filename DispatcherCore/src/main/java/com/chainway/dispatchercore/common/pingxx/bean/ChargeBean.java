package com.chainway.dispatchercore.common.pingxx.bean;

import java.util.Date;
import java.util.Map;

import com.chainway.dispatchercore.common.PropertiesUtil;
import com.chainway.dispatchercore.common.pingxx.Main;

/**
 * Ping++支付对象 具体参数参考https://www.pingxx.com/api#创建-charge-对象
 * 
 * @author xubaocheng
 *
 */
public class ChargeBean {
	private String chargeId; //生成charge对象的id，用于承接请求参数，不做请求charge用
	
	private String orderNo = new Date().getTime() + Main.randomString(7);//订单号，默认时间戳加随机字符串
	private String appId = PropertiesUtil.getString("ping_appId");
	private String channel;
	private Integer amount;
	private String clientIp;
	private String currency = "cny";
	private String subject;
	private String body;
	private Map<String, Object> extra;
	private Long time_expire;
	private String description;
	private String successUrl;
	private Integer userId; //创建用户id
	private String businessOrderNo; //业务订单编号

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Map<String, Object> getExtra() {
		return extra;
	}

	public void setExtra(Map<String, Object> extra) {
		this.extra = extra;
	}

	public Long getTime_expire() {
		return time_expire;
	}

	public void setTime_expire(Long time_expire) {
		this.time_expire = time_expire;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getAppId() {
		return appId;
	}

	public String getSuccessUrl() {
		return successUrl;
	}

	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}

	public String getChargeId() {
		return chargeId;
	}

	public void setChargeId(String chargeId) {
		this.chargeId = chargeId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getBusinessOrderNo() {
		return businessOrderNo;
	}

	public void setBusinessOrderNo(String businessOrderNo) {
		this.businessOrderNo = businessOrderNo;
	}

}
