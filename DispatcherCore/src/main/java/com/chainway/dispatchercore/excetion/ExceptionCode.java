package com.chainway.dispatchercore.excetion;

public class ExceptionCode {

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
	
	public final static int ERROR_NO_DEPT_AUTH=10051;//没有该部门权限
	public final static int ERROR_NO_ROLE_AUTH=10052;//没有该角色权限
	public final static int ERROR_NO_USER_AUTH=10053;//没有该用户权限
	public final static int ERROR_NO_VEHICLE_AUTH=10054;//没有该车辆权限
	public final static int ERROR_NO_DRIVER_AUTH=10055;//没有该车辆权限
	public final static int ERROR_FILE_SERVER_TOKEN_ERROR=10056; //文件服务token校验失败
	public final static int ERROR_FILE_TEMPLATE_UNMATCHED_DATA=10057;//模板列和上传数据列不匹配
	
	//xuxiantong  110000-119999
	public final static int ERROR_NO_AUTH_CREATE_ROOT_DEPT=110000;//没有权限新建承运商/货主部门
	public final static int ERROR_SAME_PARENT_HAD_DEPT=110001;//同级部门存在同名部门
	public final static int ERROR_DEPT_PHONE_HAD_EXIST=110002;//部门联系人手机号已经存在
	public final static int ERROR_DEPT_EMAIL_HAD_EXIST=110003;//部门联系人邮箱已经存在
	public final static int ERROR_ADD_THIRD_PARTY_DEPT_FAIL=110004;//添加2.0部门失败
	
	public final static int ERROR_DEPT_REF_SUB_DEPT=110005;//部门还存在子部门
	public final static int ERROR_DEPT_REF_ROLE=110006;//部门还关联着角色
	public final static int ERROR_DEPT_REF_USER=110007;//部门还关联着用户
	public final static int ERROR_DEPT_REF_VEHICLE=110008;//部门还关联着车辆
	public final static int ERROR_DEPT_REF_DRIVER=110009;//部门还关联着司机
	
	public final static int ERROR_ROLE_AUTH_REQUIRED=110010;//角色权限不能为空
	public final static int ERROR_ROLE_AUTH_ERROR=110011;//角色权限错误
	public final static int ERROR_DEPT_ROLE_NAME_EXIST=110012;//同一部门下存在同名角色
	
	public final static int ERROR_USER_ACCOUNT_EXIST=110013;//已经存在相同账号
	public final static int ERROR_USER_EMAIL_EXIST=110014;//已经存在用户邮箱
	public final static int ERROR_USER_PHONE_EXIST=110015;//已经存在用户手机号
	public final static int ERROR_NEW_PW_SAME_WIDTH_OLD_PW=110016;//修改新密码不能和老密码一样
	public final static int ERROR_CARRIER_CONSIGNOR_DEPT_ERROR=110017;//承运商/货主部门必须是二级部门
	public final static int ERROR_ADMIN_USER_CAN_NOT_DELETE=110018;//管理员用户不能删除
	public final static int ERROR_NEWPASSWORD_SAME_WITH_OLDPASSWORD=110019;//新密码不能和旧密码一致
	
	//xubaocheng  120000-129999
	public final static int ERROR_ORDER_NO_SITES = 120000;// 订单没有站点信息
	public final static int ERROR_SITE_NO_PCD = 120001;// 站点省市区名称数据异常
	public final static int ERROR_SITE_BIND_ORDER = 120002;//站点有被订单使用
	public final static int ERROR_PARAM_INCOMPLETE = 120003;//参数不全
	public final static int ERROR_ORDER_NOT_EXISTS = 120004;//订单不存在
	public final static int ERROR_ORDER_NO_MODIFY = 120005;//订单不能修改
	public final static int ERROR_ORDER_NO_DELETE = 120006;//订单不能删除
	public final static int ERROR_ORDER_AMOUNT_DIFF = 120007;//实付与应付金额不匹配
	public final static int ERROR_CREATE_SITE_FAIL = 120008; //创建站点失败
	//xuhuanrun   130000-139999
	
	//zuopengwei  140000-149999

	/**同城市同车型的规则已存在*/
	public final static int ERROR_CITY_VEHICLETYPE_EXIST=140000;
	/**当前支付状态不能结算*/
	public final static int ERROR_PAYSTATUS_CANNT_SETTLE=140001;
	/**当前运输状态不能结算*/
	public final static int ERROR_ORDERSTATUS_CANNT_SETTLE=140002;
	//lvyang      150000-159999
	public final static int ERROR_ORDER_STATUS_HAS_CHANGED = 150000; // 订单状态已改变
	public final static int ERROR_ORDER_CARRIER_NOT_MATCH = 151001; // 订单承运商不匹配
	public final static int ERROR_DRIVER_VEHICLE_REL_HAS_CHANGED = 151002; // 司机车辆关系已改变
	public final static int ERROR_CANCEL_ORDER_STATUS_LIMIT=151003;//取消订单状态受限
	
	
	public static final int ERROR_VIHICLE_ADD_PLATENO_REPEAT = 130001; //车牌号重复
	public final static int ERROR_VIHICLE_ADD_OTHER__FAIL = 130002;// 调用其他平台新增车辆失败
	public static final int ERROR_VIHICLE_ADD__FAIL = 130003;//本平台新增车辆失败
	public static final int ERROR_VIHICLE_DELETE__FAIL=130004;//删除车辆失败-该车辆有绑定的司机
	public static final int ERROR_DRIVER_ADD__FAIL = 130006;//新增司机失败-该车辆已被其他司机关联
	public static final int ERROR_DRIVER_DELETE__FAIL = 130007;//司机有关联车辆
	public static final int ERROR_DRIVER_COMFIRM_ORDER_FAIL = 130010;//司机-确认订单失败
	public static final int ERROR_DRIVER_COMFIRM_ORDR_STATUS_FAIL = 130011;//司机-确认订单失败：订单状态非已分配
	public static final int ERROR_DRIVER_UPLOAD_RECEIPT_FAIL = 130015;//司机-上传签单失败
	
	
}
