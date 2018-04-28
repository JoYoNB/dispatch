package com.chainway.dispatcherweb.web.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.chainway.dispatcherweb.common.CommonUtils;

public class ExtHttpServletRequestWrapper extends HttpServletRequestWrapper{

	private Enhance enhance;
	
	public ExtHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}
	
	public String[] getParameterValues(String parameter) {
	    String[]values=super.getParameterValues(parameter);
	    if(values==null){
	    	return null;
	    }
	    int count=values.length;
	    String[]encodedValues=new String[count];
	    for(int i=0;i<count;i++){
	    	encodedValues[i]=CommonUtils.cleanXSS(values[i]);
	    }
	    return encodedValues;
	}
	public String getParameter(String parameter) {
        String value=super.getParameter(parameter);
        if(value==null){
        	return null;
        }
        if(enhance!=null){
        	value=enhance.invoke(parameter,value);
        }
        return CommonUtils.cleanXSS(value);
	}
	public String getHeader(String name) {
        String value=super.getHeader(name);
        if(value==null)
        	return null;
        return CommonUtils.cleanXSS(value);
	}

	public Enhance getEnhance() {
		return enhance;
	}

	public void setEnhance(Enhance enhance) {
		this.enhance = enhance;
	}

	
	
}
