package com.chainway.dispatcherweb.aop;

import com.chainway.dispatcherweb.common.TimeUtil;
import com.chainway.dispatcherweb.web.filter.Enhance;

public class TimeZoneEnhance implements Enhance {

	private String oldTimeZone;
	private String newTimeZone;
	
	public TimeZoneEnhance(String oldTimeZone,String newTimeZone){
		this.oldTimeZone=oldTimeZone;
		this.newTimeZone=newTimeZone;
	}
	
	@Override
	public String invoke(String parameter,String value) {
		if(value==null){
			return null;
		}
		String _value=value;
		if("startTime".equals(parameter)&&value.length()==19){
			try {
				_value=TimeUtil.changeZoneTime(value, TimeUtil.FORMAT_TIME, oldTimeZone, TimeUtil.FORMAT_TIME, newTimeZone);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if("endTime".equals(parameter)&&value.length()==19){
			try {
				_value=TimeUtil.changeZoneTime(value, TimeUtil.FORMAT_TIME, oldTimeZone, TimeUtil.FORMAT_TIME, newTimeZone);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return _value;
	}

}
