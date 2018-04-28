package com.chainway.dispatchercore.common;

public class OrderStatus {
	/** 待发布 */
	public static final int WAIT_FOR_PUBLISHING = 10;
	/** 已发布 */
	public static final int PUBLISHED = 20;
	/** 已失效 */
	public static final int INVALID = 30;
	/** 已接单 */
	public static final int ACCEPTED = 40;
	/** 已分配 */
	public static final int ASSIGNED = 50;
	/** 已取消 */
	public static final int CANCELED = 60;
	/** 待提货 */
	public static final int WAIT_FOR_PICK_UP = 70;
	/** 配送中 */
	public static final int  IN_TRANSIT = 80;
	/** 已结束 */
	public static final int FINISHED = 90;
}
