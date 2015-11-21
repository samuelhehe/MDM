package com.foxconn.emm.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;

public class TextFormater {

	public static String getDataSize(long size) {
		DecimalFormat formater = new DecimalFormat("####.00");
		if (size < 1024) {
			return size + "bytes";
		} else if (size < 1024 * 1024) {
			float kbsize = size / 1024f;
			return formater.format(kbsize) + "KB";
		} else if (size < 1024 * 1024 * 1024) {
			float mbsize = size / 1024f / 1024f;
			return formater.format(mbsize) + "MB";
		} else {
			return "size: error";
		}
	}

	public static String getKBDataSize(long size) {
		return getDataSize(size * 1024);
	}

	public static String dataSizeFormat(long size) {
		DecimalFormat formater = new DecimalFormat("####.00");
		if (size < 1024) {
			return size + "byte";
		} else if (size < (1 << 20)) // 左移20位，相当于1024 * 1024
		{
			float kSize = size >> 10; // 右移10位，相当于除以1024
			return formater.format(kSize) + "KB";
		} else if (size < (1 << 30)) // 左移30位，相当于1024 * 1024 * 1024
		{
			float mSize = size >> 20; // 右移20位，相当于除以1024再除以1024
			return formater.format(mSize) + "MB";
		} else if (size < (1 << 40)) {
			float gSize = size >> 30;
			return formater.format(gSize) + "GB";
		} else {
			return "size : error";
		}
	}

	public static String getStringToMBFromByte(long size) {
		DecimalFormat formater = new DecimalFormat("####");
		double getedSize = size >> 20;
		return formater.format(getedSize);
	}

	public static String simpleDateFormat = "yyyyMMdd";

	public static String getDateString(Date date, String simpleDateFormat) {
		SimpleDateFormat myFmt1 = new SimpleDateFormat(simpleDateFormat);
		L.i("Dateformat : " + myFmt1.format(date));
		return myFmt1.format(date);
	}

	public static String simpleDateFormat2 = "yyyy/MM/dd hh:MM:sss";

	/**
	 * 
	 * yyyy/MM/dd hh:MM:sss
	 * 
	 * @return
	 */
	public static String getCurrentDateStringForErrorReport() {
		return getDateString(new Date());
	}
	
	/**
	 * yyyy/MM/dd hh:MM:sss
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateString(Date date){
		return getDateString(date, simpleDateFormat2);
	}
	

	/**
	 * yyyyMMdd
	 * 
	 * @return
	 */
	public static String getCurrentDateString() {
		return getDateString(new Date(), simpleDateFormat);
	}

	/**
	 * 保存关机的时间状态 格式为 年月日 : yyyyMMdd
	 * 
	 * @param context
	 */
	public static void saveCurrentDateString(Context context) {
		PreferencesUtils.putString(context,
				Traffic_Constants.sys_isshutdown_date, getCurrentDateString());
	}

	/**
	 * 2014/09/11 14:59:32
	 * 
	 */
	public static final String simpleDateFormat_Home = "yyyy年MM月dd日 HH:mm:ss";

	/**
	 * 2014/09/11 14:59:32
	 * 获取当前时间 
	 */
	public static String getCurrentDateStringForHome() {
		return getDateStringForHome(new Date());
	}

	/**
	 * 2014/09/11 14:59:32
	 * @param date
	 * @return
	 */
	public static String getDateStringForHome(Date date) {
		return getDateString(date, simpleDateFormat_Home);
	}

	/**
	 * 获取关机时的日期装填, 如果再次开机统计则从上次关机的日期算起
	 * 
	 * @param context
	 * @return
	 */
	public static String getShutdownDateString(Context context) {
		return PreferencesUtils.getString(context,
				Traffic_Constants.sys_isshutdown_date, "");
	}

	/**
	 * "". 没有关机 否则 获取关机的时间 "1". 表示当天关机,也就是说不用累加了
	 * 
	 * @param context
	 * @return
	 */
	public static String isHasShutdown(Context context) {
		if (getShutdownDateString(context) == "") {
			return "";
		} else if (getShutdownDateString(context).equalsIgnoreCase(
				getCurrentDateString())) {
			return "1";
		} else {
			return getShutdownDateString(context);
		}
	}

	/**
	 * 转换TEXT to long 类型
	 * 
	 * @param longtxt
	 * @return
	 */
	public static long convertStringToLong(String longtxt) {
		if (longtxt != null && longtxt != "" && longtxt != "null") {
			return Math.abs(Long.valueOf(longtxt));
		}
		return 0;
	}

	public static String dataFormat(long size) {
		DecimalFormat formater = new DecimalFormat("####.00");
		if (size < 1024) {
			return size + "byte";
		} else if (size < (1 << 20)) // 左移20位，相当于1024 * 1024
		{
			float kSize = size >> 10; // 右移10位，相当于除以1024
			return formater.format(kSize) + "KB";
		} else if (size < (1 << 30)) // 左移30位，相当于1024 * 1024 * 1024
		{
			float mSize = size >> 20; // 右移20位，相当于除以1024再除以1024
			return formater.format(mSize) + "MB";
		} else if (size < (1 << 40)) {
			float gSize = size >> 30;
			return formater.format(gSize) + "GB";
		} else {
			return "size : error";
		}
	}

	public static String getSizeFromKB(long kSize) {
		return dataSizeFormat(kSize << 10);
	}

	public static Double left2(double d) {

		DecimalFormat df = new DecimalFormat("#.00");
		return Double.valueOf(df.format(d));
	}

	public static boolean isEmpty(Object str) {
		if (str instanceof String) {
			str = (String) str;
			if (str != null && str != "null" && str != ""
					&& str.toString().length() > 0) {
				return false;
			} else {
				return true;
			}
		}
		return true;
	}

	/**
	 * yyyy-mm-dd hh:MM:ss
	 * 
	 * @param parseTime
	 * @return
	 */
	public static long getTimeToLong(String parseTime) {
		Calendar time = Calendar.getInstance();
		int sy = Integer.parseInt(parseTime.substring(0, 4).trim());
		int sm = Integer.parseInt(parseTime.substring(5, 7).trim());
		int sd = Integer.parseInt(parseTime.substring(8, 10).trim());
		int sh = Integer.parseInt(parseTime.substring(11, 13).trim());
		int smin = Integer.parseInt(parseTime.substring(14, 16).trim());
		time.set(sy, sm - 1, sd, sh, smin);
		long l = time.getTimeInMillis();
		return l;
	}
	/**
	 * 
	 * 
	 * @param parseTime  formate : 2014 12 15 09 09 09 
	 * @return
	 */
	public static long getCommonTimeToLong (String parseTime){
		Calendar time = Calendar.getInstance();
		int sy = Integer.parseInt(parseTime.substring(0, 4).trim());
		int sm = Integer.parseInt(parseTime.substring(4, 6).trim());
		int sd = Integer.parseInt(parseTime.substring(6, 8).trim());
		int sh = Integer.parseInt(parseTime.substring(8, 10).trim());
		int smin = Integer.parseInt(parseTime.substring(10, 12).trim());
		int sscends  = Integer.parseInt(parseTime.substring(12, 14).trim());
		time.set(sy, sm - 1, sd, sh, smin, sscends);
		long l = time.getTimeInMillis();
		return l ;
	}
	
	
	
	

}
