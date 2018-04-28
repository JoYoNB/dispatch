package com.chainway.dispatchercore.common.pingxx.bean;

import java.util.Map;

public class Object {
	private String id;
	private String object;
	private boolean livemode;
	private boolean paid;
	private boolean refunded;
	private boolean reversed;
	private String app;
	private String channel;
	private String client_ip;
	private String order_no;
	private Integer amount;
	private Integer amount_settle;
	private String currency;
	private String subject;
	private String body;
	private Map<String, Object> extra;
	private Long time_paid;
	private Long time_expire;
	private String transaction_no;
	private Long created;
	private boolean succeed;
	private String status;
	private Long time_succeed;
	private String description;
	private String failure_code;
	private String failure_msg;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public boolean isLivemode() {
		return livemode;
	}

	public void setLivemode(boolean livemode) {
		this.livemode = livemode;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public boolean isRefunded() {
		return refunded;
	}

	public void setRefunded(boolean refunded) {
		this.refunded = refunded;
	}

	public boolean isReversed() {
		return reversed;
	}

	public void setReversed(boolean reversed) {
		this.reversed = reversed;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getClient_ip() {
		return client_ip;
	}

	public void setClient_ip(String client_ip) {
		this.client_ip = client_ip;
	}

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Integer getAmount_settle() {
		return amount_settle;
	}

	public void setAmount_settle(Integer amount_settle) {
		this.amount_settle = amount_settle;
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

	public Long getTime_paid() {
		return time_paid;
	}

	public void setTime_paid(Long time_paid) {
		this.time_paid = time_paid;
	}

	public Long getTime_expire() {
		return time_expire;
	}

	public void setTime_expire(Long time_expire) {
		this.time_expire = time_expire;
	}

	public String getTransaction_no() {
		return transaction_no;
	}

	public void setTransaction_no(String transaction_no) {
		this.transaction_no = transaction_no;
	}

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public boolean isSucceed() {
		return succeed;
	}

	public void setSucceed(boolean succeed) {
		this.succeed = succeed;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getTime_succeed() {
		return time_succeed;
	}

	public void setTime_succeed(Long time_succeed) {
		this.time_succeed = time_succeed;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFailure_code() {
		return failure_code;
	}

	public void setFailure_code(String failure_code) {
		this.failure_code = failure_code;
	}

	public String getFailure_msg() {
		return failure_msg;
	}

	public void setFailure_msg(String failure_msg) {
		this.failure_msg = failure_msg;
	}

}
