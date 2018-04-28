package com.chainway.carrierservice.excetion;

public class ExceptionCode {

	/*
	 * 服务异常 1开头为baseService的异常 2开头为mileageOilService异常 3开头为fileService异常
	 * 
	 */

	// 公共异常:异常号只有3位1000-9999
	public final static int ERROR_SYSTEM_DEFAULT = 1000;// 系统异常
	public final static int ERROR_IO = 1001;// 网络异常
	public final static int ERROR_ROLE_EXCEPTION = 1002;// 角色错误
	public final static int ERROR_UNAUTHORIZED = 1003;// 没权限
	public final static int ERROR_SESSION_EXPIRED = 1004;// session失效
	public final static int ERROR_USER_PASSWORD_WRONG = 1005;// 用户名密码错误
	public final static int ERROR_SQL_INJECT = 1006;// sql注入

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

	public final static int ERROR_ROLE_NAME_EXIST = 10017;// 角色名称重复
	public final static int ERROR_PARENT_DEPT_NOT_EXIST = 10018;// 父级部门不存在
	public final static int ERROR_DEPT_PHONE_EXIST = 10019;// 部门联系人电话已经存在
	public final static int ERROR_DEPT_EMAIL_EXIST = 10020;// 部门联系人邮箱已经存在
	public final static int ERROR_SYSTEM_ROLE_CAN_NOT_DELETE = 10021;// 系统角色不能删除
	public final static int ERROR_SYSTEM_ROLE_CAN_NOT_EDIT = 10022;// 系统角色不能编辑
	public final static int ERROR_PLATE_NO_EXIST = 10023;// 车牌号已存在
	public final static int ERROR_CAR_NOT_EXIST = 10024;// 车辆不存在

	public final static int ERROR_SYSTEM_USER_ACCOUNT_ISLOCK = 10025;// 账号被锁住
	public final static int ERROR_SYSTEM_USER_ACCOUNT_ISERROR = 10026;// 账号密码错误
	public final static int ERROR_SYSTEM_USER_ACCOUNT_ISEXITS = 10027;// 账号已经存在
	public final static int ERROR_SYSTEM_USER_PHONE_ISEXITS = 10028;// 手机号码已经存在
	public final static int ERROR_SYSTEM_USER_EMAIL_ISEXITS = 10029;// 邮箱已经存在
	public final static int ERROR_EXCEL_EQUIPMENT_TYPE_ERROR = 10030;// 导入设备类型不正确
	public final static int ERROR_EQUIPMENT_PN_EXIST_ERROR = 10031;// 导入设备已存在
	public final static int ERROR_EQUIPMENT_SAVE_ERROR = 10032;// 新增设备失败
	public final static int ERROR_EQUIPMENT_BIND_VEHICLE_ERROR = 10033;// 删除有绑定车辆的设备

	public final static int ERROR_IMPORT_REQUIRED = 10034;// 导入必填
	public final static int ERROR_IMPORT_VEHICLE_NOTEXITS = 10035;// 导入车辆不存在
	public final static int ERROR_IMPORT_DEPT_NOTEXITS = 10037;// 导入部门不存在
	public final static int ERROR_IMPORT_PHONE_REPEAT = 10037;// 导入手机号码有重复

	public final static int ERROR_REMIND_SETTING_EXIST = 10040;// 提醒设置已存在
	public final static int ERROR_LABEL_NAME_EXIST = 10041;// 标签名称已存在
	public final static int ERROR_LABEL_NOT_EXIST = 10042;// 标签不存在
	public final static int ERROR_PRODUCT_LINE_NAME_EXIST = 10043;// 产品线名称已存在
	public final static int ERROR_PRODUCT_LINE_NOT_EXIST = 10044;// 产品线不存在
	public final static int ERROR_MERCHANT_NAME_EXIST = 10045;// 商户名称已存在
	public final static int ERROR_MERCHANT_NOT_EXIST = 10046;// 商户不存在

}
