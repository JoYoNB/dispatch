package com.chainway.dispatcherweb.exception;

import org.apache.commons.lang.StringUtils;

import com.chainway.dispatcherweb.common.ReturnCodeConstant;

public class BizException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5712653197892721508L;

	private int code;
	private String msg;
	private Object data;
	
	private void build(int code,String msg,Object data){
		this.code=code;
		this.msg=msg;
		this.data=data;
	}
	
	public BizException(int code,String msg,Object data){
		super();
		build(code, msg, data);
	}
	
	public BizException(int code,String msg){
		super();
		build(code, msg, null);
	}
	
	public BizException(Exception e,int code,String msg,Object data){
		super();
		String _msg=msg;
		if(StringUtils.isEmpty(_msg)){
			_msg=e.getMessage();
		}
		build(code, _msg, data);
	}
	
	public BizException(Exception e,int code,String msg){
		super();
		String _msg=msg;
		if(StringUtils.isEmpty(_msg)){
			_msg=e.getMessage();
		}
		build(code, _msg, null);
	}
	
	public BizException(Exception e){
		super();
		build(ReturnCodeConstant.ERROR_SYSTEM_DEFAULT, e.getMessage(), null);
	}
	
	public BizException(){
		super();
		build(ReturnCodeConstant.ERROR_SYSTEM_DEFAULT, "系统异常", null);
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
