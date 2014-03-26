package com.hyx.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author vicen E-mail:hailors@qq.com
 * @version 创建时间：2014-2-8 上午11:17:28 TODO
 */
public class DateUtil {

	/**
	 * 返回当前系统时间
	 * 
	 * @param pattern
	 * @return
	 */
	public static String getCurrentTime(String pattern) {
		if ("".equals(pattern) || null == pattern) {
			pattern = "yyyy-MM-dd HH:mm:ss";
		}
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(new Date());
	}

	/**
	 * 返回当前系统时间,默认格式"yyyy-MM-dd HH:mm:ss"
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date());
	}

	/**
	 * 获取当前时间加/减N天
	 * 
	 * @param num
	 * @return yyyy-MM-dd
	 */
	public static String getCurTimeAddND(int num) {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DATE, num);
		Date date = cal.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date);
	}

	/**
	 * 将str转成date
	 * 
	 * @param strDate
	 *            时间形的String （如：20110124）
	 * @param patten
	 *            格式 （如：yyyMMdd）
	 * @return Date
	 * @throws ParseException
	 */
	public static Date str2Date(String strDate, String patten)
			throws ParseException {
		DateFormat format = new SimpleDateFormat(patten);
		Date d = null;
		d = format.parse(strDate);
		return d;
	}

	/**
	 * 获取当前时间
	 * 
	 * @param dateformat
	 *            如:yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getCurTime(String dateformat) {
		Calendar calendar = new GregorianCalendar();
		Date date = calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat(dateformat);
		return format.format(date);
	}

}
