package com.wanda.ccs.sqlasm.expression;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	
	static private SimpleDateFormat mediumDateTimeFmt = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	static private SimpleDateFormat mediumDateMillTimeFmt = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");
	static private SimpleDateFormat mediumDateFmt = new SimpleDateFormat(
			"yyyy-MM-dd");


	/**
	 * Date对象 转化为 格式如："2003-03-18 20:35" 的String
	 * 
	 * @param date
	 * @return string repesent the date
	 */
	public static final String dateTimeToMediumStr(Date date) {
		if (date == null)
			return null;

		try {
			return mediumDateTimeFmt.format(date);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Date对象 转化为 格式如："2003-03-18 20:35" 的String
	 * 
	 * @param date
	 * @return string repesent the date
	 */
	public static final String dateMillTimeToMediumStr(Date date) {
		if (date == null)
			return null;

		try {

			return mediumDateMillTimeFmt.format(date);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Date对象 转化为 格式如："2003-03-18" 的String
	 * 
	 * @param date
	 * @return string repesent the date
	 */
	public static final String dateToMediumStr(Date date) {
		if (date == null)
			return null;

		try {
			return mediumDateFmt.format(date);
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * parse String convert it to Date object <br>
	 * For example: calling parseMediumDate("2003-03-18") <br>
	 * will return a date object representation this date
	 * 
	 * @param value
	 * @return
	 */
	public static final Date parseMediumDate(String value) {
		try {
			Date date = mediumDateFmt.parse(value);
			return date;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * parse String convert it to Date object <br>
	 * For example: calling parseMediumDate("2003-03-18 12:20") <br>
	 * will return a date object representation this date
	 * 
	 * @param value
	 * @return
	 */
	public static final Date parseMediumDateTime(String value) {
		try {
			Date date = mediumDateTimeFmt.parse(value);
			return date;
		} catch (Exception e) {
			return null;
		}
	}
	
	
	public static final Date parseMediumDateMillTime(String value) {
		try {
			Date date = mediumDateMillTimeFmt.parse(value);
			return date;
		} catch (Exception e) {
			return null;
		}
	}
	
	
}
