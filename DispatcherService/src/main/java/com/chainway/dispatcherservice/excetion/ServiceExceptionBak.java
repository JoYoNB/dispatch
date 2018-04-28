package com.chainway.dispatcherservice.excetion;

import org.apache.commons.lang.StringUtils;


public class ServiceExceptionBak extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4709960856824210179L;

	private int code;
	private String msg;
	private Object data;
	
	private void build(int code,String msg,Object data){
		this.code=code;
		this.msg=msg;
		this.data=data;
	}
	
	public ServiceExceptionBak(int code,String msg,Object data){
		super();
		build(code, msg, data);
	}
	
	public ServiceExceptionBak(int code,String msg){
		super();
		build(code, msg, null);
	}
	
	public ServiceExceptionBak(Exception e,int code,String msg,Object data){
		super();
		String _msg=msg;
		if(StringUtils.isEmpty(_msg)){
			_msg=e.getMessage();
		}
		build(code, _msg, data);
	}
	
	public ServiceExceptionBak(Exception e,int code,String msg){
		super();
		String _msg=msg;
		if(StringUtils.isEmpty(_msg)){
			_msg=e.getMessage();
		}
		build(code, _msg, null);
	}
	
	public ServiceExceptionBak(Exception e){
		super();
		build(ExceptionCodeBak.ERROR_SYSTEM_DEFAULT, e.getMessage(), null);
	}
	
	public ServiceExceptionBak(){
		super();
		build(ExceptionCodeBak.ERROR_SYSTEM_DEFAULT, "系统异常", null);
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
}
