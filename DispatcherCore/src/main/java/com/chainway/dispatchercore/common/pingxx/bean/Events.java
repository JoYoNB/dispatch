package com.chainway.dispatchercore.common.pingxx.bean;

public class Events {
	private String id; // 事件对象 id ，由 Ping++ 生成，28 位长度字符串。
	private String object;// 值为 "event"。
	private boolean livemode; // 事件是否发生在生产环境
	private long created; // 事件发生的时间 timestamp
	private int pending_webhooks; // 推送未成功的 webhooks 数量
	private String type; // 事件类型 charge.succeeded(支付对象，支付成功时触发)
	private String request;// API Request ID。值 "null" 表示该事件不是由 API 请求触发的
	private Data data; // 绑定在事件上的数据对象。

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

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public int getPending_webhooks() {
		return pending_webhooks;
	}

	public void setPending_webhooks(int pending_webhooks) {
		this.pending_webhooks = pending_webhooks;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

}
