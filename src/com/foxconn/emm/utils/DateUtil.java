package com.foxconn.emm.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.text.TextUtils;

public class DateUtil {
	public static final long HOURS_MILLIS = 60 * 60 * 1000;
	public static final long HALF_DAY_MILLIS = HOURS_MILLIS * 12;
	public static final long DAY_MILLIS = HALF_DAY_MILLIS * 2;
	public static final long WEEK_MILLIS = DAY_MILLIS * 7;
	public static final long MONTH_MILLIS = WEEK_MILLIS * 30;
	/**
	 * yyyyMMdd
	 */
	public static final String DATE_DEFAULT_FORMATE = "yyyyMMdd";
	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static final String DATE_FORMATE_ALL = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMATE_RATE = "MM-dd HH:mm:ss";
	/**
	 * dd/MM/yyyy, hh:mm
	 */
	public static final String DATE_FORMATE_TRANSACTION = "dd/MM/yyyy, hh:mm";
	/**
	 * MM/dd HH:mm
	 */
	public static final String DATE_FORMATE_DAY_HOUR_MINUTE = "MM/dd HH:mm";
	public static final String DATE_FORMATE_HISTORY = "MM-dd";
	/**
	 * HH:mm
	 */
	public static final String DATE_FORMATE_HOUR_MINUTE = "HH:mm";

	public static SimpleDateFormat dateFormate = new SimpleDateFormat();

	/**
	 * 
	 * @param milliseconds
	 * @return the time formated by "yyyy-MM-dd HH:mm:ss"
	 */
	public static String toTime(long milliseconds) {
		return toTime(new Date(milliseconds), null);
	}

	/**
	 * 
	 * @param milliseconds
	 * @param pattern
	 * @return the time formated
	 */
	public static String toTime(long milliseconds, String pattern) {
		return toTime(new Date(milliseconds), pattern);
	}

	/**
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String toTime(Date date, String pattern) {
		if (TextUtils.isEmpty(pattern)) {
			pattern = DATE_FORMATE_ALL;
		}
		dateFormate.applyPattern(pattern);
		if (date == null) {
			date = new Date();
		}
		return dateFormate.format(date);
	}

	/**
	 * 
	 * @param context
	 * @param createTime
	 *            the time to be formated
	 * @return like X seconds/minutes/hours/weeks ago ,
	 */
	public static String getTimeAgo(Context context, long createTime) {
		long curTime = System.currentTimeMillis();
		long between = (curTime - createTime) / 1000;
		String str = "";

		long day = between / (24 * 3600);
		long hour = between % (24 * 3600) / 3600;
		long minute = between % 3600 / 60;
		if (day >= 1) {
			if (day > 7) {
				str = 1 + " 周 前";
			} else {
				str = day + " 天前";
			}
		} else if (hour > 0) {
			str = hour + " 小时 前";
		} else if (minute > 0) {
			str = ((minute == 0) ? 1 : minute) + " 分钟 前";
		} else {
			str = between + " 秒 前";
		}

		return str;
	}

	public static Calendar minusDay(Calendar calendar, int minusDay) {
		int dateOfYear = calendar.get(Calendar.DAY_OF_YEAR);
		Calendar newCalendar = Calendar.getInstance();
		newCalendar.setTimeInMillis(calendar.getTimeInMillis());
		newCalendar.set(Calendar.DAY_OF_YEAR, dateOfYear + minusDay);
		return newCalendar;
	}

	public static Calendar addDay(Calendar calendar, int addDay) {
		int dateOdYear = calendar.get(Calendar.DAY_OF_YEAR);
		Calendar newCalendar = Calendar.getInstance();
		newCalendar.setTimeInMillis(calendar.getTimeInMillis());
		newCalendar.set(Calendar.DAY_OF_YEAR, dateOdYear + addDay);
		return newCalendar;
	}

	
	public static int compareDays(Calendar calendar1, Calendar calendar2){
		int dayOfyear1 = calendar1.get(Calendar.DAY_OF_YEAR);
		int dayOfyear2 = calendar2.get(Calendar.DAY_OF_YEAR);
		return Math.abs(dayOfyear2-dayOfyear1);
	}
	

}
