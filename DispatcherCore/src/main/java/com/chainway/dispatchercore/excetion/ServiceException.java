package com.chainway.dispatchercore.excetion;

import org.apache.commons.lang.StringUtils;


public class ServiceException extends Exception {

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
	
	public ServiceException(int code,String msg,Object data){
		super();
		build(code, msg, data);
	}
	
	public ServiceException(int code,String msg){
		super();
		build(code, msg, null);
	}
	
	public ServiceException(Exception e,int code,String msg,Object data){
		super();
		String _msg=msg;
		if(StringUtils.isEmpty(_msg)){
			_msg=e.getMessage();
		}
		build(code, _msg, data);
	}
	
	public ServiceException(Exception e,int code,String msg){
		super();
		String _msg=msg;
		if(StringUtils.isEmpty(_msg)){
			_msg=e.getMessage();
		}
		build(code, _msg, null);
	}
	
	public ServiceException(Exception e){
		super();
		build(ExceptionCode.ERROR_SYSTEM_DEFAULT, e.getMessage(), null);
	}
	
	public ServiceException(){
		super();
		build(ExceptionCode.ERROR_SYSTEM_DEFAULT, "系统异常", null);
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
