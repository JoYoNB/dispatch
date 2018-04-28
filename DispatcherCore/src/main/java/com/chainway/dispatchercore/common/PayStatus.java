package com.chainway.dispatchercore.common;

public class PayStatus {
	/** 未付款 */
	public static final int UNPAID = 10;
	/** 已付款 */
	public static final int PAID = 20;
	/** 已结算 */
	public static final int SETTLED = 30;
	/** 待退款 */
	public static final int WAIT_FOR_REFUND = 40;
	/** 已退款 */
	public static final int REFUNDED = 50;
}
