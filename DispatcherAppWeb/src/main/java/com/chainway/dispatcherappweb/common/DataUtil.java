package com.chainway.dispatcherappweb.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;


public class DataUtil {
	/**
	 * 日期格式:yyyy-MM-dd
	 */
	private static final String PATTERN_DATE = "yyyy-MM-dd";
	private static final String STRING_ZERO = "0";
	/**
	 * 日期格式:yyyy-MM-dd hh:MM:ss
	 */
	public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
	
	
	

	/**
	 * 身份证号码十五位转十八位 转换过程: 先将十五位转换成完整年份的17位数字本体码,再对17位数字本体码依次加权求和后mod(11)取得模余数,依模余数到modResultsArray找对应的值做为校验码数字放到最后一位
	 * 即:18位新号码=17位数字本体码+1位校验码
	 * 
	 * @param oldId15
	 * @return String
	 */
	public static String getNewCardId(String oldId15) {
		final int[] weight = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };// 权
		final String[] modResultsArray = { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2" };// 模余数对应值
		int summer = 0;
		int modResultIndex;

		String newId17;
		String newId18;
		newId17 = oldId15.substring(0, 6) + "19" + oldId15.substring(6, oldId15.length());
		for (int i = 0; i < newId17.length(); i++) {
			summer = summer + Integer.parseInt(newId17.substring(i, i + 1)) * weight[i];
		}
		modResultIndex = summer % 11;
		newId18 = newId17 + modResultsArray[modResultIndex]; // 18位新号码=17位数字本体码+1位校验码
		return newId18;
	}

	/**
	 * 判断字符串是否为空,单纯的空格字符串定义为空
	 * 
	 * @param target
	 * @return boolean
	 */
	public static boolean isEmpty(String target) {
		if (StringUtils.isEmpty(target)) {
			return true;
		}
		return StringUtils.isEmpty(target.trim());
	}
	public static boolean isEmpty_0(String target) {
		if (StringUtils.isEmpty(target)||target.equals("0")) {
			return true;
		}
		return StringUtils.isEmpty(target.trim());
	}
	public static boolean isEmpty(Object target) {
		if(target==null){
			return true;
		}
		if (StringUtils.isEmpty(target.toString())) {
			return true;
		}
		return StringUtils.isEmpty(target.toString().trim());
	}
	/**
	 * 字符串的空值处理
	 * 
	 * @param target
	 * @return String
	 */
	public static String changeNullStringToEmpty(String target) {
		if ((target == null) || (target.equals("null"))) {
			return "";
		} else {
			return target.trim();
		}
	}



	/**
	 * 将空字符和null串变为指定的值
	 * 
	 * @param target
	 * @param replacement
	 * @return String
	 */
	public static String changeEmptyStringToValue(String target, String replacement) {
		if (target == null || "".equals(target.trim())) {
			return replacement;
		} else {
			return target.trim();
		}
	}

	/**
	 * 判断字符串是否在字符数组中
	 * 
	 * @param target
	 * @param array
	 * @return boolean
	 */
	public static boolean isStringInArray(String target, String[] array) {
		if (array == null) {
			return false;
		}
		for (int i = 0; i < array.length; i++) {
			if (target.equals(array[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 将字符串按分隔符转换为List
	 * 
	 * @param target
	 * @param separator
	 * @return List<String>
	 */
	public static List<String> split(String target, String separator) {
		List<String> result = new ArrayList<String>();
		if (StringUtils.isEmpty(target)) {
			return result;
		}
		String[] targetArray = target.split(separator);
		for (int i = 0; i < targetArray.length; i++) {
			result.add(targetArray[i]);
		}
		return result;
	}

	/**
	 * 将java.util.Date类型的时间转化成指定格式的字符串
	 * 
	 * @param dateTime 类型为java.util.Date
	 * @param pattern 值为PATTERN_DATE, PATTERN_DATETIME
	 * @return pattern格式的字符串
	 * @throws Exception
	 */
	public static String formatDateTime(java.util.Date dateTime, String pattern) throws Exception {
		if (dateTime == null) {
			return "";
		}
		try {
			java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat(pattern);
			return inputFormat.format(dateTime);
		} catch (Exception e) {
			throw new Exception("FssiUtil.formatDateTime: 参数(" + pattern + ")错误");
		}

	}
	/**
	 * 将指定格式的字符串转化成java.util.Date类型的时间
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 * @throws Exception Lenovo 2009-3-13 Date
	 */
	public static Date formatStringToDate(String date, String pattern) throws Exception {
	    if (isEmpty(date)) {
		return null;
	    }
	    try {
		java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat(pattern);
		
		return inputFormat.parse(date);
	    } catch (Exception e) {
		throw new Exception("FssiUtil.formatDateTime: 参数(" + pattern + ")错误");
	    }
	    
	}

	/**
	 * 将日期格式的字符串转换为指定格式的字符串
	 * 
	 * @param dateTimeStr 要转换的日期格式字符串
	 * @param inputPattern 输入字符串日期的格式
	 * @param outputPattern 输出字符串日期的格式
	 * @return String
	 * @throws Exception
	 */
	public static String formatDateString(String dateTimeStr, String inputPattern, String outputPattern) throws Exception {
		if (isEmpty(dateTimeStr)) {
			return "";
		}

		try {
			java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat(inputPattern);
			java.util.Date inputDate = inputFormat.parse(dateTimeStr);

			java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat(outputPattern);

			return outputFormat.format(inputDate);
		} catch (Exception e) {
			throw new Exception("FssiUtil.formatDateTime: 参数错误(" + inputPattern + "," + outputPattern + ")");
		}
	}

	/**
	 * 得到指定日期的年月
	 * 
	 * @param dataStr
	 * @return yyyy-MM开头的格式的日期字符串
	 * @throws Exception
	 */
	public static String getYearMonth(String dataStr) throws Exception {
		try {
			return dataStr == null ? null : dataStr.substring(0, 7);
		} catch (Exception e) {
			throw new Exception("FssiUtil.getYearMonth: 日期(" + dataStr + ")的格式不正确");
		}
	}

	/**
	 * 返回系统日期(取数据库时间)的后(前)year年month月day天
	 * 
	 * @param year 正数表示当前日期的后year年,负数表示当前日期的前year年
	 * @param month 正数表示当前日期的后month月,负数表示当前日期的前month月
	 * @param day 正数表示当前日期的后day天,负数表示当前日期的前day天
	 * @return yyyy-MM-dd格式的日期
	 * @throws Exception
	 */
	


	/**
	 * 返回指定日期的后(前)year年month月day天
	 * 
	 * @param dateStr yyyy-MM-dd格式的日期字符串
	 * @param year 正数表示指定日期的后year年,负数表示当前日期的前year年
	 * @param month 正数表示指定日期的后month月,负数表示当前日期的前month月
	 * @param day 正数表示指定日期的后day天,负数表示当前日期的前day天
	 * @return yyyy-MM-dd格式的日期
	 * @throws Exception
	 */
	public static String getAfterTime(String dateStr, int year, int month, int day) throws Exception {
		// 将日期转换为java.util.Date
		java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat(PATTERN_DATE);
		java.util.Date date = formater.parse(dateStr);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		calendar.add(Calendar.YEAR, year);
		calendar.add(Calendar.MONTH, month);
		calendar.add(Calendar.DATE, day);

		return formatDateTime(calendar.getTime(), PATTERN_DATE);
	}

	/**
	 * 比较两个日期(格式为以yyyy-MM或yyyyMM开头)相差的月份数,前者减后者
	 * 
	 * @param oneDateStr 以yyyy-MM或yyyyMM开头为格式的日期字符串
	 * @param anotherDateStr 以yyyy-MM或yyyyMM开头为格式的日期字符串
	 * @return 前者减后者的月份数
	 * @throws Exception
	 */
	public static int getDifferenceMonth(String oneDateStr, String anotherDateStr) throws Exception {
		try {
			int year1 = Integer.parseInt(oneDateStr.substring(0, 4));
			int month1 = oneDateStr.indexOf("-") < 0 ? Integer.parseInt(oneDateStr.substring(4, 6)) : Integer.parseInt(oneDateStr
					.substring(5, 7));

			int year2 = Integer.parseInt(anotherDateStr.substring(0, 4));
			int month2 = anotherDateStr.indexOf("-") < 0 ? Integer.parseInt(anotherDateStr.substring(4, 6)) : Integer
					.parseInt(anotherDateStr.substring(5, 7));

			int differenceMonth = (year1 - year2) * 12 + (month1 - month2);

			return differenceMonth;
		} catch (Exception e) {
			throw new Exception("FssiUtil.formatDateTime: 日期(" + oneDateStr + ", " + anotherDateStr + ")的格式不正确");
		}
	}

	/**
	 * 比较两个日期相差的小时数,前者减后者
	 * 
	 * @param oneDateTimeStr 以yyyy-MM-dd hh:mm:ss开头为格式的日期字符串
	 * @param anotherDateTimeStr 以yyyy-MM-dd hh:mm:ss开头为格式的日期字符串
	 * @return 前者减后者的小时数
	 * @throws Exception
	 */
	public static long getDifferenceHour(String oneDateTimeStr, String anotherDateTimeStr) throws Exception {
		try {
			return getDifferenceMiniSecond(oneDateTimeStr, anotherDateTimeStr) / 3600000;
		} catch (Exception e) {
			throw new Exception("FssiUtil.formatDateTime: 日期(" + oneDateTimeStr + ", " + anotherDateTimeStr + ")的格式不正确");
		}
	}

	public static long getDifferenceMiniSecond(String oneDateTimeStr, String anotherDateTimeStr) throws ParseException {
		java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat(PATTERN_DATETIME);
		java.util.Date oneDateTime = formater.parse(oneDateTimeStr);
		java.util.Date anotherDateTime = formater.parse(anotherDateTimeStr);

		long difference = oneDateTime.getTime() - anotherDateTime.getTime();
		return difference;
	}

	/**
	 * 比较两个日期相差的天数,前者减后者
	 * 
	 * @param oneDateTimeStr 以yyyy-MM-dd hh:mm:ss开头为格式的日期字符串
	 * @param anotherDateTimeStr 以yyyy-MM-dd hh:mm:ss开头为格式的日期字符串
	 * @return 前者减后者的天数
	 * @throws Exception
	 */
	public static long getDifferenceDay(String oneDateTimeStr, String anotherDateTimeStr) throws Exception {
		return getDifferenceHour(oneDateTimeStr, anotherDateTimeStr) / 24;
	}

	/**
	 * 比较两个日期(格式为yyyy-MM-dd)
	 * 
	 * @param oneDateStr 日期字符串
	 * @param anotherDateStr 日期字符串
	 * @return 1: oneDateStr大于anotherDateStr; 0: oneDateStr等于anotherDateStr; -1: oneDateStr小于anotherDateStr
	 * @throws Exception
	 */
	public static int compareDate(String oneDateStr, String anotherDateStr) throws Exception {
		try {
			java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat(PATTERN_DATE);
			java.util.Date oneDate = formater.parse(oneDateStr);
			java.util.Date anotherDate = formater.parse(anotherDateStr);

			return oneDate.compareTo(anotherDate);

		} catch (Exception e) {
			throw new Exception("日期的格式(" + oneDateStr + "," + anotherDateStr + ")不正确");
		}
	}

	/**
	 * 
	 * 比较两个日期，取出最大的那个日期 (格式为yyyy-MM-dd)
	 * 
	 * @param oneDateStr 日期字符串
	 * @param anotherDateStr 日期字符串
	 * @return 1: oneDateStr大于anotherDateStr 返回 oneDateStr; 0: oneDateStr等于anotherDateStr 返回 anotherDateStr; -1:
	 *         oneDateStr小于anotherDateStr 返回 anotherDateStr
	 * @throws Exception
	 */
	public static String compareDateGetTheLargest(String oneDateStr, String anotherDateStr) throws Exception {
		String thelargestdate = "";
		if (compareDate(oneDateStr, anotherDateStr) == 1) {
			thelargestdate = oneDateStr;
		} else if (compareDate(oneDateStr, anotherDateStr) == 0) {
			thelargestdate = anotherDateStr;
		} else if (compareDate(oneDateStr, anotherDateStr) == -1) {
			thelargestdate = anotherDateStr;
		}
		return thelargestdate;
	}

	/**
	 * 精确的小数位四舍五入处理
	 * 
	 * @param number 需要四舍五入的数
	 * @param scale 小数点后保留的位数
	 * @return 四舍五入后的结果
	 */
	public static double round(double number, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("参数scale不能为负");
		}

		BigDecimal b = new BigDecimal(Double.toString(number));
		BigDecimal one = new BigDecimal("1");

		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	/**
	 * 精确的小数位四舍五入处理
	 * 
	 * @param number 需要四舍五入的数
	 * @param scale 小数点后保留的位数
	 * @return 四舍五入后的结果
	 */
	public static double round(String number, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("参数scale不能为负");
		}

		BigDecimal b = new BigDecimal(number);
		BigDecimal one = new BigDecimal("1");

		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * 精确的小数位四舍五入处理
	 * 如果number为null,则默认为0
	 * @param number 需要四舍五入的数
	 * @param scale 小数点后保留的位数
	 * @return 四舍五入后的结果
	 */
	public static double roundIfNullToZero(String number, int scale){
		String tmp = number;
		if(isEmpty(tmp)){
			tmp = "0";
		}
		return round(tmp, scale);
	}
	/**
	 * 精确的小数位四舍五入处理
	 * 
	 * @param number 需要四舍五入的数
	 * @param scale 小数点后保留的位数
	 * @return 四舍五入后的结果
	 */
	public static String roundToString(double number, int scale) {
		return fillZeroToEnd(Double.toString(round(number, scale)), scale);
	}

	/**
	 * 小数点不足指定位数的补0
	 * 
	 * @param number
	 * @param scale
	 * @return
	 */
	private static String fillZeroToEnd(String number, int scale) {
		return number.substring(number.indexOf(".") + 1).length() < scale ? fillZeroToEnd(number + STRING_ZERO, scale) : number;
	}

	/**
	 * 字符串替换,将strSource中的strFrom替换为strTo
	 * 
	 * @param strSource
	 * @param strFrom 要补替换的字符串
	 * @param strTo 替换的字符串
	 * @return String
	 */
	public static String replace(String strSource, String strFrom, String strTo) {
		if (StringUtils.isEmpty(strSource)) {
			return strSource;
		}
		String result = "";
		int intFromLen = strFrom.length();
		int intPos;

		while ((intPos = strSource.indexOf(strFrom)) != -1) {
			result = result + strSource.substring(0, intPos);
			result = result + strTo;
			strSource = strSource.substring(intPos + intFromLen);
		}
		result = result + strSource;

		return result;
	}

	/**
	 * 克隆对象
	 * 
	 * @param obj
	 * @return Object
	 */
	public static Object cloneObject(Object obj) {
		try {
			java.io.ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.flush();
			byte buff[] = baos.toByteArray();
			oos.close();
			baos.close();
			java.io.ByteArrayInputStream bais = new ByteArrayInputStream(buff);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object cloneObj = ois.readObject();
			ois.close();
			bais.close();
			return cloneObj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将金额转换为中文格式的金额
	 * 
	 * @param digit 最多两位小数,支持最大金额为999999999999.99
	 * @return 中文格式的金额
	 */
	public static String parseMoneyToUpper(double digit) {
		// 将数据格式化为四位小数
		DecimalFormat df = new DecimalFormat("#.0000");
		StringBuffer sbDigit = new StringBuffer(df.format(digit));
		sbDigit.replace(sbDigit.length() - 2, sbDigit.length(), "00");
		String sDigit = "";// 将double转化为string
		sDigit = sbDigit.toString();
		sDigit = sDigit.substring(0, sDigit.length() - 5) + sDigit.substring(sDigit.length() - 4);// 去除小数点

		// 将字符串补齐16位，利于分组
		// sDigit = sDigit + "00";
		if (sDigit.length() > 16) {
			return "款项过大！";
		}

		if (sDigit.length() < 16) {
			int iLength = 16 - sDigit.length();
			for (int i = 0; i < iLength; i++) {
				sDigit = "0" + sDigit;
			}
		}
		if (sDigit.equals("0000000000000000")) {
			return "零元整";
		}
		String sChinese = sDigit;
		String sFour = "";// 每四位构造一个string
		boolean bPreStr = true;// 前一个string是否构造成功
		sDigit = "";// 总字符串
		// 将字符串分为四组，每一组单独处理，都处理完后串接
		for (int i = 0; i < 4; i++) {
			sFour = toChinese(sDigit, sChinese.substring(i * 4, i * 4 + 4), i, bPreStr);
			if (sFour.length() == 0 || sFour.length() == 1) {
				bPreStr = false;
			} else if (sFour.charAt(sFour.length() - 2) < '0' || sFour.charAt(sFour.length() - 2) > '9') {
				bPreStr = false;
			} else {
				bPreStr = true;
			}
			sDigit = sDigit + sFour;
		}
		// 去掉字符串最前面的‘0’
		for (;;) {
			if (sDigit.charAt(0) == '0') {
				sDigit = sDigit.substring(1);
			} else {
				break;
			}
		}
		sChinese = "";

		for (int i = 0; i < sDigit.length(); i++) {
			if (sDigit.charAt(i) >= '0' && sDigit.charAt(i) <= '9') {
				switch (sDigit.charAt(i)) {
					case '1': {
						sChinese = sChinese + "壹";
						break;
					}
					case '2': {
						sChinese = sChinese + "贰";
						break;
					}
					case '3': {
						sChinese = sChinese + "叁";
						break;
					}
					case '4': {
						sChinese = sChinese + "肆";
						break;
					}
					case '5': {
						sChinese = sChinese + "伍";
						break;
					}
					case '6': {
						sChinese = sChinese + "陆";
						break;
					}
					case '7': {
						sChinese = sChinese + "柒";
						break;
					}
					case '8': {
						sChinese = sChinese + "捌";
						break;
					}
					case '9': {
						sChinese = sChinese + "玖";
						break;
					}
					case '0': {
						sChinese = sChinese + "零";
						break;
					}
				}
			} else {
				sChinese = sChinese + sDigit.charAt(i);
			}
		}

		if (!sDigit.endsWith("分"))// 有"分"不加"整"
		{
			sChinese = sChinese + "整";
		}

		return sChinese;
	}

	/**
	 * 数字转换为中文金额
	 * 
	 * @param sStr
	 * @param sFour
	 * @param i
	 * @param bPre
	 * @return String
	 */
	private static String toChinese(String sStr, String sFour, int i, boolean bPre) {
		// 回传结果
		String result = "";

		for (int j = 0; j < 4; j++) {
			if (sFour.charAt(j) != '0') {// 处理每一位数值时，在前面是否需要加“零”
				if (j == 0) {// 处理千位
					if (!bPre) {
						result = result + '0';
					}
					result = result + sFour.charAt(j);
				} else {// 处理百、十、个位
					if (sFour.charAt(j - 1) == '0') {
						result = result + '0';
					}
					result = result + sFour.charAt(j);
				}

				switch (j) {// 单独处理“角”和“分”
					case 0: {
						if (i == 3) {
							result = result + '角';
						} else {
							result = result + '仟';
						}
						break;
					}
					case 1: {
						if (i == 3) {
							result = result + '分';
						} else {
							result = result + '佰';
						}
						break;
					}
					case 2: {
						result = result + '拾';
						break;
					}
					case 3: {
						if (!result.equals("")) {
							// 处理单位
							switch (i) {
								case 0: {
									result = result + "亿";
									break;
								}
								case 1: {
									result = result + "万";
									break;
								}
								case 2: {
									result = result + "元";
									break;
								}
							}
						}
					}
				}
			} else {
				// 当个位为零时，处理单位
				if (!result.equals("") && j == 3) {
					switch (i) {
						case 0: {
							result = result + "亿";
							break;
						}
						case 1: {
							result = result + "万";
							break;
						}
					}
				}
				// 是否加“元”字
				if (i == 2 && j == 3 && (!sStr.equals("") || !result.equals(""))) {
					result = result + "元";
				}
			}
		}
		return result;
	}

	/**
	 * 创建目录
	 * 
	 * @param path 目录路径
	 */
	public static void mkdir(String path) {
		if (path.endsWith("/") || path.endsWith("\\")) {
			path = path.substring(0, path.length() - 1);
			mkdir(path);
			return;
		}
		File dir = new File(path);
		if (dir.exists())
			return;
		int lastIndexOfSeperator = -1;
		int lastIndexOfSeperator1 = path.lastIndexOf("/");
		int lastIndexOfSeperator2 = path.lastIndexOf("\\");
		lastIndexOfSeperator = lastIndexOfSeperator1 > lastIndexOfSeperator2 ? lastIndexOfSeperator1 : lastIndexOfSeperator2;
		if (lastIndexOfSeperator < 0)
			return;
		String parentPath = path.substring(0, lastIndexOfSeperator);
		mkdir(parentPath);
		dir.mkdir();
	}

	/**
	 * 复制文件
	 * 
	 * @param srcFileName 源文件
	 * @param destFileName 目的文件
	 * @throws IOException
	 */
	public static void copyFile(String srcFileName, String destFileName) throws IOException {
		FileInputStream fis = new FileInputStream(srcFileName);
		FileOutputStream fos = new FileOutputStream(destFileName);
		byte[] buffer = new byte[1048576];
		while (true) {
			int readed = fis.read(buffer);
			if (readed <= 0) {
				break;
			}
			fos.write(buffer, 0, readed);
			if (readed < 2048) {
				break;
			}
		}
		fos.flush();
		fos.close();
		fis.close();
	}

	/**
	 * 将费款所属期格式(yyyyMM或yyyy-MM)的日期转换为yyyy-MM-dd格式的日期
	 * 
	 * @param date
	 * @return String
	 * @throws Exception
	 */
	public static String formatDate(String date) throws Exception {
		int length = date.length();
		if (length == 6) {
			return date.substring(0, 4) + "-" + date.substring(4) + "-01";
		} else if (length == 7) {
			return date + "-01";
		} else {
			throw new Exception("传入的参数不正确");
		}
	}

	/**
	 * 将费款所属期格式(yyyyMMdd的日期转换为yyyy-MM-dd格式的日期
	 * 
	 * @param date
	 * @return String
	 * @throws Exception
	 */
	public static String formatDateyyyyMMdd(String date) throws Exception {
		int length = date.length();
		if (length == 8) {
			return date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
		} else {
			throw new Exception("传入的参数不正确");
		}
	}

	/**
	 * 将费款所属期格式(yyyy-MM-dd)的日期转换为yyyy年MM月dd日格式的日期
	 * 
	 * @param date
	 * @return String
	 * @throws Exception
	 */
	public static String formatyyyyMMddToyyyy年MM月dd日(String date) throws Exception {
		int length = date.length();
		if (length == 10) {
			return date.substring(0, 4) + "年" + date.substring(5, 7) + "月" + date.substring(8, 10) + "日";
		} else {
			throw new Exception("传入的参数不正确");
		}
	}

	/**
	 * 将费款所属期格式(yyyy-MM-dd的日期转换为yyyy-MM格式的日期
	 * 
	 * @param date
	 * @return String
	 * @throws Exception
	 */
	public static String formatDateyyyy_MM(String date) throws Exception {
		int length = date.length();
		if (length == 10) {
			return date.substring(0, 7);
		} else {
			throw new Exception("传入的参数不正确");
		}
	}

	/**
	 * 将日期格式的字符串转换成业务所属期格式的字符串(yyyyMM)
	 * 
	 * @param date
	 * @return yyyyMM格式的字符串
	 * @throws Exception
	 */
	public static String formateDateToYWSSQ(String date) throws Exception {
		return date.replaceAll("-", "").substring(0, 6);
	}

	/**
	 * 将日期格式的字符串(yyyyMMdd)转换成业务所属期格式的字符串(yyyyMMdd)
	 * 
	 * @param date
	 * @return yyyyMM格式的字符串
	 * @throws Exception
	 */
	public static String formateDateToyyyyMMdd(String date) throws Exception {
		return date.replaceAll("-", "");
	}

	/**
	 * 将业务所属期格式(yyyyMM)的字符串转换成的yyyy-MM日期格式
	 * 
	 * @param date
	 * @return yyyy-MM格式日期
	 * @throws Exception
	 */
	public static String formateYWSSQToDate(String date) throws Exception {
		return date.substring(0, 4) + "-" + date.substring(4);
	}

	private static String NX_YEAR = "年";

	private static String NX_MONTH = "个月";

	/**
	 * 将*年*个月格式转换为月数
	 * 
	 * @param nx
	 * @return int
	 * @throws Exception
	 */
	public static int transformNxToMonth(String nx) throws Exception {
	    	
	    if("0".equals(nx)){
		return 0;
	    }
		int year = Integer.parseInt(nx.substring(0, nx.indexOf(NX_YEAR)));
		int month = Integer.parseInt(nx.substring(nx.indexOf(NX_YEAR) + 1, nx.indexOf(NX_MONTH)));
		return year * 12 + month;
	}

	/**
	 * 将月数转换为*年*个月格式
	 * 
	 * @param monthCount
	 * @return String
	 * @throws Exception
	 */
	public static String transformMonthToNx(int monthCount) throws Exception {
		StringBuffer nx = new StringBuffer();
		nx.append((monthCount / 12)).append(NX_YEAR).append((monthCount % 12)).append(NX_MONTH);
		return nx.toString();
	}

	/**
	 * 将月数转换为*年*个月格式
	 * 
	 * @param monthCount
	 * @return String
	 * @throws Exception
	 */
	public static String transformMonthToNx(String monthCount) throws Exception {
		return isEmpty(monthCount) ? "0年0月" : (Integer.parseInt(monthCount) / 12) + "年" + (Integer.parseInt(monthCount) % 12) + "月";
	}

	/**
	 * 将%数转换为小数（如：50% ——> 0.5或50 ——> 0.5）
	 * 
	 * @param percentage 百分数
	 * @return double
	 * @throws Exception
	 */
	public static double transformPercentageToDecimal(String percentage) throws Exception {
		double decimal = 0;
		if (percentage.contains("%")) {
			decimal = Double.parseDouble(percentage.substring(0, percentage.indexOf("%"))) / 100;
		} else {
			decimal = Double.parseDouble(percentage) / 100;
		}
		return decimal;
	}

	/**
	 * 将小数转换为%（如：0.5 ——> 50%）
	 * 
	 * @param decimal 小数
	 * @return String
	 * @throws Exception
	 */
	public static String transformDecimalToPercentage(double decimal) throws Exception {
		return Double.valueOf(decimal).toString() + "%";
	}

	
	
	
	


	/**
	 * 将List<String>转换为查询的in的条件
	 * 
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public static String transformListToQueryInCondition(List<String> list) throws Exception {
		StringBuffer condition = new StringBuffer();
		int size = list.size();
		if (size > 0) {
			condition.append("'").append(list.get(0)).append("'");
		}
		for (int i = 1; i < size; i++) {
			condition.append(",'").append(list.get(i)).append("'");
		}
		return condition.toString();
	}

	/**
	 * 将个人编号和单位编号补足10位(需求已变更,不再补足10位,本方法现改为直接返回入参)
	 * 
	 * @param bh
	 * @return
	 * @throws Exception
	 * 
	 */
	public static String fillBHToTen(String bh) throws Exception {
		return bh;
		// if (!FssiUtil.isEmpty(bh)) {
		// bh = bh.trim();
		// int a = 10 - bh.length();
		// if (a > 0) {
		// for (int i = 1; i <= a; i++) {
		// bh = "0" + bh;
		// }
		// }
		// }
		// return bh;
	}

	/**
	 * 将征收标识和基数标识补足8位
	 * 
	 * @param bh
	 * @return
	 * @throws Exception
	 */
	public static String fillBSToTen(String bs) throws Exception {
		if (!isEmpty(bs)) {
			bs = bs.trim();
			int a = 8 - bs.length();
			if (a > 0) {
				for (int i = 1; i <= a; i++) {
					bs = "0" + bs;
				}
			}
		}
		return bs;
	}

	

	
	/**
	 *  获取指定年月最后一个工作日
	 * @param yyyymm 200907
	 * @return
	 * @throws Exception db2admin Jul 22, 2009 String
	 */
	public static String getLastWorkDate(String yyyymm) throws Exception {
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
		int week = Calendar.SUNDAY;
		Calendar calendar1 = Calendar.getInstance();
		Date date = dateFormater.parse(yyyymm.substring(0,4)+"-"+yyyymm.substring(4,6)+"-01");
		calendar1.setTime(date);
		calendar1.add(Calendar.MONTH, 1);
		calendar1.set(Calendar.DATE, 1);
		calendar1.add(Calendar.DATE, -1);
		week = calendar1.get(Calendar.DAY_OF_WEEK);
		if (week == Calendar.SUNDAY) {
			calendar1.add(Calendar.DATE, -2);
		} else if (week == Calendar.SATURDAY) {
			calendar1.add(Calendar.DATE, -1);
		}
		return dateFormater.format(calendar1.getTime());
	}
	
	public static String decode(String s) throws Exception {
		return isEmpty(s) ? s : new String(s.getBytes("ISO-8859-1"), "GBK");
	}

	/**
	 * 对月份进行加减 格式 YYYYMM
	 * 
	 * @param YM
	 * @return liudl 2009-2-24 String
	 */
	public static String jjys(String YM, int M) {
		System.err.println("入参" + YM);
		String YMM = "";
		int Year = Integer.parseInt(YM.substring(0, 4)) * 100;
		int Month = Integer.parseInt(YM.substring(4, 6));
		Month = Month + M;
		if (Month > 12) {
			Month = 1;
			Year = Year + 100;
		} else if (Month < 1) {
			Month = 12;
			Year = Year - 100;
		}
		YMM = (Year + Month) + "";
		System.err.println("返回值:" + YMM);
		return YMM;
	}
	

	
	
	
	
	/**
	   * <p>
	   * 判断输入值是否为空
	   * </p>
	   *  
	   * <li>判断输入值是否为空（重写方法）</li><br>
	   * 
	   * @param str
	   * @return  
	   *
	   */
	  public static String handleNull(String str) {
	    if (str == null || str.equals(""))
	      return "";
	    return str.toString().trim();
	  }
	  
	/**
		* 判断字符串是否非空,单纯的空格字符串定义为空
		* 
		* @param target
		* @return boolean
		*/
	public static boolean isNotEmpty(String target) {
		return !isEmpty(target);
	}
	/**
	* 判断字符串是否为整数
	* 
	* @param target
	* @return boolean
	*/
	public static boolean isInteger(String input){  
        Matcher mer = Pattern.compile("^[+-]?[0-9]+$").matcher(input);  
        return mer.find();  
    }  
	/**
     * 
     * 获取对象属性，返回一个字符串数组
     * 
     * @param o对象
     * @return String[] 字符串数组
     */
 
//    private static String[] getFiledName(Object o) {
//        try {
//        	Field[] fields = o.getClass().getDeclaredFields();
//           // String[] fieldNames = new String[fields.length];
//            for (int i = 0; i < fields.length; i++) {
//            	if(DataUtil.isEmpty(fields[i].getName())){
//            		 fields[i].getName()=null;
//            	}
//                fieldNames[i] = fields[i].getName();
//            }
//            return fieldNames;
//        } catch (SecurityException e) {
//            e.printStackTrace();
//            System.out.println(e.toString());
//        }
//        return null;
// 
//    }
	
    public static String getexcelName(String fileName,HttpServletRequest request) throws UnsupportedEncodingException{
    	String downloadFileName=fileName;
		String userAgent=request.getHeader("User-Agent");
		if(userAgent.toLowerCase().indexOf("firefox")>-1){
			//FF
			downloadFileName=new String(fileName.getBytes("UTF-8"), "ISO8859-1");
			downloadFileName+=".xlsx";
			downloadFileName="\"" + downloadFileName +"\"";
			//在火狐情况下，用双引号引起来可解决空格的问题
			
        }else{
        	downloadFileName=URLEncoder.encode(fileName, "UTF-8");
        	downloadFileName=downloadFileName.replaceAll("\\+",  " ");
        	downloadFileName+=".xlsx";
        	//空格变加号，需要把+号变回来
        	
        }
		return downloadFileName;
    	
    }
    
    /**
	 * 获取2个String时间类型的值，相差的毫秒数,返回结果：分钟
	 * 
	 * @param smalldate 较小的时间
	 * @param bigdate   较大的时间
	 * @return
	 * @throws Exception Lenovo 2009-3-13 Date
	 */
	public static long getDifferMilliseconds(String smalldate, String bigdate) throws Exception {
	    long result = 0;
		if (isEmpty(smalldate) || isEmpty(bigdate)) {
		return result;
	    }
	    try {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  //此处会抛异常
		Date smalltime = sdf.parse(smalldate);
		Date bigtime = sdf.parse(bigdate);
		  //获取毫秒数
		long small = smalltime.getTime();
		long big = bigtime.getTime();
		
		result = (big - small)/(1000*60);
	    } catch (Exception e) {
		throw new Exception("FssiUtil.formatDateTime: 参数(" + smalldate + ")错误");
	    }
	    return result;
	}
	public static void main(String[] args) throws Exception {
		String aa = "2017-08-16 11:11:11";
		String bb = "2017-08-15 11:11:11";
		
		long result = getDifferMilliseconds(bb, aa);
		
		System.out.println("result="+result);
		
	}
}
