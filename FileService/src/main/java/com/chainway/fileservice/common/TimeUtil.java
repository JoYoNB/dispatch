package com.chainway.fileservice.common;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone; 

import org.apache.commons.lang.StringUtils;

import chainway.frame.util.ZoneUtil;


/**时间工具
 * @author chainway
 *
 */
public class TimeUtil {
	/**格式yyyy-MM-dd HH:mm:ss*/
	public static final String FORMAT_TIME = "yyyy-MM-dd HH:mm:ss";
    /**格式yyMMddHHmmss*/
    public static final String FORMAT_TIME1 = "yyMMddHHmmss";
    /**格式yyyyMMddHHmmss*/
    public static final String FORMAT_TIME2 = "yyyyMMddHHmmss";
    /**格式yyyy-MM-dd HH:mm*/
	public static final String FORMAT_TIME3 = "yyyy-MM-dd HH:mm";
	/**格式yyyy-MM-dd*/
	public static final String FORMAT_DATE = "yyyy-MM-dd";
	/**格式yyMMdd*/
	public static final String FORMAT_DATE1 = "yyMMdd";
	/**格式yyyyMMdd*/
	public static final String FORMAT_DATE2 = "yyyyMMdd";
	
	/**格式yyyy-MM*/
	public static final String FORMAT_DATE3 = "yyyy-MM";
	
	
    /**获得时区对象
	 * @param zone  时区编号(为空时返回当前系统时区)
	 * @return
	 * @throws Exception
	 */
	public static TimeZone getTimeZone(String zone) throws Exception{
		TimeZone timeZone=null;
		if(zone!=null&&!zone.equals("")){
			timeZone=ZoneUtil.getTimeZone(zone);
			if(timeZone==null)throw new Exception("TimeZone no "+zone);
		}else{
			return TimeZone.getDefault();
		}
		return timeZone;
	}
	/**获得时间对象
	 * @param zone 时区编号(为空时使用当前系统时区)
	 * @return
	 * @throws Exception
	 */
	public static Calendar getCalendar(String zone) throws Exception{
		Calendar calendar =Calendar.getInstance();
		if(zone!=null&&!zone.equals("")){
			calendar.setTimeZone(getTimeZone(zone));
		}
		return calendar;
	}
	/**获得指定时间是星期几
	 * @param calendar  时间对象
	 * @return 范围1-7(星期一到星期天)
	 * @throws Exception
	 */
	public static int getWeek(Calendar calendar) throws Exception{
		if(calendar==null)throw new Exception("Calendar is null");
		//此方式获得星期的顺序为["星期日","星期一","星期二","星期三","星期四","星期五","星期六"]
		int week=calendar.get(Calendar.DAY_OF_WEEK);
		if(week==1)week=7;
		else week--;
		return week;
	}
	/**获得指定时间的月最大天数
	 * @param calendar  时间对象
	 * @return 月最大天数,范围1-31
	 * @throws Exception
	 */
	public static int getMaxDay(Calendar calendar) throws Exception{
		if(calendar==null)throw new Exception("Calendar is null");
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	/**获得当前时间在指定时区的时间
	 * @param format 时间格式
	 * @param zone   时区编号
	 * @return [时间,毫秒数]
	 * @throws Exception
	 */
	public static Object[] getZone(String format,String zone) throws Exception{
		Calendar calendar =getCalendar(zone);
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(calendar.getTimeZone());
		return new Object[]{dateFormat.format(calendar.getTime()),calendar.getTimeInMillis()};
	}
	/**获得当前时间在指定时区的时间
	 * @param format 时间格式
	 * @param zone   时区编号
	 * @return
	 * @throws Exception
	 */
	public static String getZoneTime(String format,String zone) throws Exception{
		Calendar calendar =getCalendar(zone);
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(calendar.getTimeZone());
		return dateFormat.format(calendar.getTime());
	}
	/**获得当前时间在指定时区的时间毫秒数
	 * @param zone   时区编号
	 * @return
	 * @throws Exception
	 */
	public static long getZoneTimeInMillis(String zone) throws Exception{
		Calendar calendar =getCalendar(zone);
		return calendar.getTimeInMillis();
	}
	/**获得当前时间在指定时区是星期几
	 * @param zone   时区编号
	 * @return 范围1-7(星期一到星期天)
	 * @throws Exception
	 */
	public static int getZoneWeek(String zone) throws Exception{
		Calendar calendar =getCalendar(zone);
		return getWeek(calendar);
	}
	/**获得当前时间在指定时区的月最大天数
	 * @param zone   时区编号
	 * @return 月最大天数,范围1-31
	 * @throws Exception
	 */
	public static int getZoneMaxDay(String zone) throws Exception{
		Calendar calendar =getCalendar(zone);
		return getMaxDay(calendar);
	}
	/**获得当前时间
	 * @param format 时间格式
	 * @return [时间,毫秒数]
	 * @throws Exception
	 */
	public static Object[] getLocal(String format) throws Exception{
		return getZone(format,null);
	}
	/**获得当前时间
	 * @param format 时间格式
	 * @return
	 * @throws Exception
	 */
	public static String getLocalTime(String format) throws Exception{
		return getZoneTime(format,null);
	}
	/**获得当前时间毫秒数
	 * @return
	 * @throws Exception
	 */
	public static long getLocalTimeInMillis() throws Exception{
		return getZoneTimeInMillis(null);
	}
	/**获得当前时间是星期几
	 * @return 范围1-7(星期一到星期天)
	 * @throws Exception
	 */
	public static int getLocalWeek() throws Exception{
		return getZoneWeek(null);
	}
	/**获得当前时间的月最大天数
	 * @return 月最大天数,范围1-31
	 * @throws Exception
	 */
	public static int getLocalMaxDay() throws Exception{
		return getZoneMaxDay(null);
	}
	/**获得指定时间是星期几
	 * @param time   时间
	 * @param format 格式
	 * @return 范围1-7(星期一到星期天)
	 * @throws Exception
	 */
	public static int getWeek(String time,String format) throws Exception{
		Calendar calendar =getCalendar(null);
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		calendar.setTimeInMillis(dateFormat.parse(time).getTime());
		return getWeek(calendar);
	}
	/**获得指定时间的月最大天数
	 * @param time   时间
	 * @param format 格式
	 * @return 月最大天数,范围1-31
	 * @throws Exception
	 */
	public static int getMaxDay(String time,String format) throws Exception{
		Calendar calendar =getCalendar(null);
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		calendar.setTimeInMillis(dateFormat.parse(time).getTime());
		return getMaxDay(calendar);
	}
	/**将系统时区的时间转换为时间毫秒数
	 * @param time   时间
	 * @param format 格式
	 * @return
	 * @throws Exception
	 */
	public static long getTimeInMillis(String time,String format) throws Exception{
		return changeTimeInMillis(time,format,null);
	}
	/**将指定时区的时间转换为时间毫秒数
	 * @param time   时间
	 * @param format 格式
	 * @param zone   时区
	 * @return
	 * @throws Exception
	 */
	public static long changeTimeInMillis(String time,String format,String zone) throws Exception{
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		if(zone!=null&&!zone.equals(""))dateFormat.setTimeZone(getTimeZone(zone));
		return dateFormat.parse(time).getTime();
	}
	/**将时间毫秒数转换为新时区的时间
	 * @param time   时间
	 * @param format 新格式
	 * @param zone   新时区
	 * @return 
	 * @throws Exception
	 */
	public static String changeTimeInMillis(long time,String format,String zone) throws Exception{
		Date date=new Date(time);
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(getTimeZone(zone));
		return dateFormat.format(date);
	}
	/**将指定时区的时间转换为新时区的时间
	 * @param time      时间
	 * @param format    时间格式
	 * @param zone      时间时区
	 * @param newFormat 新格式
	 * @param newZone   新时区
	 * @return [时间,毫秒数]
	 * @throws Exception
	 */
	public static Object[] changeZone(String time,String format,String zone,String newFormat,String newZone) throws Exception{
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(getTimeZone(zone));
		Date date=dateFormat.parse(time);
		dateFormat.setTimeZone(getTimeZone(newZone));
		dateFormat.applyPattern(newFormat);
		return new Object[]{dateFormat.format(date),date.getTime()};
	}
	/**将指定时区的时间转换为新时区的时间
	 * @param time      时间
	 * @param format    时间格式
	 * @param zone      时间时区
	 * @param newFormat 新格式
	 * @param newZone   新时区
	 * @return 
	 * @throws Exception
	 */
	public static String changeZoneTime(String time,String format,String zone,String newFormat,String newZone) throws Exception{
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(getTimeZone(zone));
		Date date=dateFormat.parse(time);
		dateFormat.setTimeZone(getTimeZone(newZone));
		dateFormat.applyPattern(newFormat);
		return dateFormat.format(date);
	}
	/**将指定时区的时区时间转换为UTC时间
	 * @param zoneTime   时区时间
	 * @param zoneFormat 时区时间格式
	 * @param zone       时区编号
	 * @param utcFormat  UTC时间格式
	 * @return UTC时间
	 * @throws Exception
	 */
	public static String getZoneUTCTime(String zoneTime,String zoneFormat,String zone,String utcFormat) throws Exception{
		TimeZone timeZone=getTimeZone(zone);
		SimpleDateFormat dateFormat = new SimpleDateFormat(zoneFormat);
		dateFormat.setTimeZone(timeZone);
		Calendar calendar=getCalendar(null);
		calendar.setTimeInMillis(dateFormat.parse(zoneTime).getTime());
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		dateFormat.applyPattern(utcFormat);
		return dateFormat.format(calendar.getTime());
	}
	/**将指定时区的时区时间转换为UTC时间毫秒数
	 * @param zoneTime   时区时间
	 * @param zoneFormat 时区时间格式
	 * @param zone       时区编号
	 * @return UTC时间毫秒数
	 * @throws Exception
	 */
	public static long getZoneUTCTime(String zoneTime,String zoneFormat,String zone) throws Exception{
		TimeZone timeZone=getTimeZone(zone);
		SimpleDateFormat dateFormat = new SimpleDateFormat(zoneFormat);
		dateFormat.setTimeZone(timeZone);
		Calendar calendar=getCalendar(null);
		calendar.setTimeInMillis(dateFormat.parse(zoneTime).getTime());
		return calendar.getTimeInMillis();
	}
	
	public static String time2String(Date time,String format){
		String _f=format;
		if(StringUtils.isEmpty(_f)){
			_f=FORMAT_TIME;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(_f);
		String timeStr=dateFormat.format(time);
		
		return timeStr;
	}
	//获取某年某月的第一天的日期
	public static Date getStartMonthDate(int year, int month) {
          Calendar calendar = Calendar.getInstance();
          calendar.set(year, month - 1, 1);
          return calendar.getTime();
    }
	//获取某年某月的最后一天的日期
	public static Date getEndMonthDate(int year, int month) {
	        Calendar calendar = Calendar.getInstance();
	        calendar.set(year, month - 1, 1);
	        int day = calendar.getActualMaximum(5);
	        calendar.set(year, month - 1, day);
	        return calendar.getTime();
	}
	public static String getSqlTimeZone(String zone) throws Exception{
		TimeZone timezone=TimeUtil.getTimeZone(zone);
		long offset = timezone.getRawOffset();
		boolean flag = false;
		if(offset<0){
			offset*=-1;
			flag=true;
		}
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;
		
		long hour = offset / hh;
		long minute = (offset - hour * hh) / mi;
		
		String strHour = hour < 10 ? "0" + hour : "" + hour;//小时
		String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
		String result = strHour+":"+strMinute;
		if(flag){
			result="-"+result;
		}else{
			result="+"+result;
		}
		return result;
	}
	
	/**
	 * 获取时区和零时区的跨度
	 * @return
	 * @throws Exception 
	 */
	public static String getZoneSpan(String userZone) throws Exception{
		String format = "yyyy-MM-dd HH:mm:ss";
		String zeroZone = "utc-0000003";
		SimpleDateFormat sd = new SimpleDateFormat(format);
		Date _now=new Date();
		String newStr=TimeUtil.changeZoneTime(sd.format(_now), format,userZone , format, zeroZone);
	    long diff = _now.getTime() - sd.parse(newStr).getTime();
	    long nh = 1000*60*60;//一小时的毫秒数
	    long hour = diff/nh;//计算差多少小时
	    String zongstr="+00:00";
	    if(hour==-11){
	    	 zongstr="-11:00";
	    }else if(hour==-10){
	    	zongstr="-10:00";
	    }else if(hour==-9){
	    	zongstr="-09:00";
	    }else if(hour==-8){
	    	zongstr="-08:00";
	    }else if(hour==-7){
	    	zongstr="-07:00";
	    }else if(hour==-6){
	    	zongstr="-06:00";
	    }else if(hour==-5){
	    	zongstr="-05:00";
	    }else if(hour==-4){
	    	zongstr="-04:00";
	    }else if(hour==-3){
	    	zongstr="-03:00";
	    }else if(hour==-2){
	    	zongstr="-02:00";
	    }else if(hour==-1){
	    	zongstr="-01:00";
	    }else if(hour==0){
	    	zongstr="+00:00";
	    }else if(hour==1){
	    	zongstr="+01:00";
	    }else if(hour==2){
	    	zongstr="+02:00";
	    }else if(hour==3){
	    	zongstr="+03:00";
	    }else if(hour==4){
	    	zongstr="+04:00";
	    }else if(hour==5){
	    	zongstr="+05:00";
	    }else if(hour==6){
	    	zongstr="+06:00";
	    }else if(hour==7){
	    	zongstr="+07:00";
	    }else if(hour==8){
	    	zongstr="+08:00";
	    }else if(hour==9){
	    	zongstr="+09:00";
	    }else if(hour==10){
	    	zongstr="+10:00";
	    }else if(hour==11){
	    	zongstr="+11:00";
	    }else if(hour==12){
	    	zongstr="+12:00";
	    }
		return zongstr;
	}
	
	/**
	 * 获得指定时区月份 开始时间
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public static String getMonthStartTime(int year,int month,String zone) throws Exception{
		Calendar calendar =getCalendar(zone);
		String format="yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH,month-1);
		calendar.set(Calendar.DAY_OF_MONTH,1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		dateFormat.applyPattern(format);
		return dateFormat.format(calendar.getTime());
	}
	
 
	
	/**
	 * 获得指定时区月份 开始时间
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public static String getMonthEndTime(int year,int month,String zone) throws Exception{
		Calendar calendar =getCalendar(zone);
		String format="yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH,month-1);
		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		dateFormat.applyPattern(format);
		return dateFormat.format(calendar.getTime());
	}
	
	/**
	 * 获得指定年份 在零时区的开始时间
	 * @param year
	 * @param format
	 * @param zone
	 * @return
	 * @throws Exception
	 */
	public static String getYearStartTime(String year,String zone) throws Exception{
		String formatstr="01-01 00:00:00";
		String time=year+"-"+formatstr;
		String format="yyyy-MM-dd HH:mm:ss";
		TimeZone timeZone=getTimeZone(zone);
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(timeZone);
		Calendar calendar=getCalendar(null);
		calendar.setTimeInMillis(dateFormat.parse(time).getTime());
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		dateFormat.applyPattern(format);
		return dateFormat.format(calendar.getTime());
	}
	
	/**
	 * 获得指定年份 在零时区的开始时间
	 * @param year
	 * @param format
	 * @param zone
	 * @return
	 * @throws Exception
	 */
	public static String getYearEndTime(String year ,String zone) throws Exception{
		String formatstr="12-31 23:59:59";
		String time=year+"-"+formatstr;
		String format="yyyy-MM-dd HH:mm:ss";
		TimeZone timeZone=getTimeZone(zone);
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(timeZone);
		Calendar calendar=getCalendar(null);
		calendar.setTimeInMillis(dateFormat.parse(time).getTime());
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		dateFormat.applyPattern(format);
		return dateFormat.format(calendar.getTime());
	}
	
	public static String date2Str(Date date,String format){
		if(date==null)return null;
	    SimpleDateFormat fmt=new SimpleDateFormat (format);
	    return fmt.format(date);
	}
	
	public static Date str2Date(String str,String format){
		SimpleDateFormat fmt=new SimpleDateFormat (format);
		try {
			return fmt.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取新时间
	 * @return
	 * @throws ParseException 
	 */
	public static String getNewTime(String oldTime,Integer month) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date now = sdf.parse(oldTime.split(" ")[0]);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(Calendar.MONTH, month);
		System.out.println(sdf.format(calendar.getTime()));
		return sdf.format(calendar.getTime());
	}
	
	/**
	 * 获取新时间
	 * @return
	 * @throws ParseException 
	 */
	public static int  comparedate(String DATE1, String DATE2) throws ParseException{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	        try {
	            Date dt1 = df.parse(DATE1);
	            Date dt2 = df.parse(DATE2);
	            if (dt1.getTime() > dt2.getTime()) {
	                return 1;
	            } else if (dt1.getTime() < dt2.getTime()) {
	                return -1;
	            } else {
	                return 0;
	            }
	        } catch (Exception exception) {
	            exception.printStackTrace();
	        }
	        return 0;
	}
	/**
	 * 根据月份添加的时间结果
	 * @return
	 * @throws ParseException 
	 */
	public static String  yearDateAddMonth(String validatetime, int month) throws ParseException{
//		int renewalsdata = 6;
//		String validatetime = "20121110";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date now = sdf.parse(validatetime);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
//		System.out.println(sdf.format(calendar.getTime()));
		calendar.add(Calendar.MONTH, month);
		return sdf.format(calendar.getTime());
//		System.out.println();
	}
	
}
