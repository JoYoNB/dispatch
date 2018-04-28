package com.chainway.driverappweb.common;

public class ReturnCodeConstant {

	// 公共异常:异常号只有3位1000-9999
	public final static int ERROR_SYSTEM_DEFAULT = 1000;// 系统异常
	public final static int ERROR_IO = 1001;// 网络异常
	public final static int ERROR_ROLE_EXCEPTION = 1002;// 角色错误
	public final static int ERROR_UNAUTHORIZED = 1003;// 没权限
	public final static int ERROR_SESSION_EXPIRED = 1004;// session失效
	public final static int ERROR_USER_PASSWORD_WRONG = 1005;// 用户名密码错误
	public final static int ERROR_SQL_INJECT = 1006;// sql注入
	public final static int ERROR_LOGIN_CAPTCHA = 10008;// 登陆验证码错误
	public final static int ERROR_PARAMETER_LENGTH_LIMITATION = 10005;// 参数长度限制
	public final static int ERROR_ROLE_INFO_EXCEPTION = 10024;// 用户角色信息错误
	public final static int ERROR_USER_NOTEXIST = 10025;// 用户不存在
	public final static int ERROR_USER_OLD_PASSWORD_WRONG = 10040;// 用户旧密码不正确

	// 业务异常:异常号有5位10000-99999
	public final static int ERROR_LOGIN_PASSWORD_ERROR = 10001;
	public final static int ERROR_LOGIN_USER_ACCOUNT_NOT_EXIST = 10002;
	public final static int ERROR_LOGIN_USER_IS_LOCKED = 10003;

	public final static int ERROR_PARAM_IS_REQUIRED = 10004;// 参数必填
	public final static int ERROR_PARAM_MAXLEN_LIMIT = 10005;// 参数最大长度限制
	public final static int ERROR_PARAM_MINLEN_LIMIT = 10006;// 餐数最小长度限制
	public final static int ERROR_PARAM_FORMAT_ERROR = 10007;// 参数格式错误
	public final static int ERROR_PARAM_MINVAL_LIMIT = 100012;// 参数值过小限制
	public final static int ERROR_PARAM_MAXVAL_LIMIT = 100013;// 参数值过大限制
	public final static int ERROR_LOGIN_VALIDATE_ERROR = 10008;// 登录验证码错误
	public final static int ERROR_UPLOAD_FILE_REQUIRED = 10009;// 上次文件为空
	public final static int ERROR_UPLOAD_FILE_TYPE_ILLEGAL = 10010;// 上传文件格式不合法
	public final static int ERROR_EXCEL_TEMPLATE_CELS_ERROR = 10011;// excel模板格式错误

	public final static int ERROR_EXCEL_CELL_DATA_EMPTY = 10012;// excel单元格数据为空
	public final static int ERROR_EXCEL_CELL_DATA_IS_REQUIRE = 10013;// 单元格数据必填
	public final static int ERROR_EXCEL_CELL_VALUE_MAX_VALUE_LIMIT = 10014;// 单元格数值超过最大值
	public final static int ERROR_EXCEL_CELL_VALUE_MIN_VALUE_LIMIT = 10015;// 单元格数值小于最大值
	public final static int ERROR_EXCEL_CELL_VALUE_FORMAT_ERROR = 10016;// 单元格值格式不正确
	public final static int ERROR_EXCEL_CELL_VALUE_MAX_LENGTH_LIMIT = 10047;// 单元格值长度超过最大值
	public final static int ERROR_EXCEL_CELL_VALUE_MIN_LENGTH_LIMIT = 10048;// 单元格值长度小于最小值限制
	public final static int ERROR_EXCEL_CELL_VALUE_REGX_LIMIT = 10049;// 单元格值不符合规范，特殊字符限制
	public final static int ERROR_LOGIN_USER_LOCKED = 10050;//登录用户已经锁住
	public final static int ERROR_CAR_NOT_EXIST = 10024;// 车辆不存在
	/** 文件无数据 */
	public static final int CODE_FILE_SIZE_EMPTY = 10025;
	/** 文件格式错误 */
	public static final int CODE_FILE_EXT_ERROR = 10026;
	/** 文件大小超限 */
	public static final int CODE_FILE_SIZE_LIMIT = 10027;
}
