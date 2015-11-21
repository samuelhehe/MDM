package com.foxconn.emm.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.SystemClock;

import com.foxconn.emm.bean.UidTraffic;
import com.foxconn.emm.bean.UidTraffic.UidTrafficTag;
import com.foxconn.emm.db.DBHelper;
import com.foxconn.emm.db.DBInfo;
import com.foxconn.emm.utils.TextFormater;
import com.foxconn.emm.utils.Traffic_Constants;

/**
 * 这个主要操作的数据库对象是 针对设备中所有应用流量的一个统计
 * 
 * 存在问题: 1.单天单个应用流量统计中计算方法, 如果该天没有结束,直接关机了,流量需要记录下来, 接下来再开机,
 * a.如果是当天开机则当天的流量信息不是最后一次流量减去前一天流量的最后一条记录
 * b.如果是第二天开机则当天的流量信息不是当天最后一次流量信息减去前一天流量的最后一次记录,而是当天流量信息的最后一条记录
 * 当然还需要判断是否是在当天开关机了
 * 
 */
public class TrafficAppsOptDao {
	private static final String TAG = "TrafficOptDao";
	protected Context context;
	private DBHelper dbHelper;

	public TrafficAppsOptDao(Context context) {
		this.context = context;
		dbHelper = new DBHelper(context);
	}

	/**
	 * 
	 * 获取某个UID一天各个小时内的总流量大小
	 * 
	 * @param tDay
	 * @param uid
	 * @param month
	 * @param year
	 * @return
	 */
	public TreeMap<Integer, String> getTrafficDataByUidAndDay(int tDay,
			int uid, int month, int year) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select * from "
				+ DBInfo.Table.EMM_TRAFFIC_UID_INFO_TB_NAME
				+ " where  UID = ? ,  TDAY =? , TMONTH= ? , TYEAR=? order by THOUR desc ";
		Cursor daycursor = db.rawQuery(sql,
				new String[] { String.valueOf(uid), String.valueOf(tDay),
						String.valueOf(month), String.valueOf(year) });
		if (null != daycursor) {
			if (daycursor.getCount() > 0) {
				TreeMap<Integer, String> values = new TreeMap<Integer, String>();
				for (daycursor.moveToFirst(); !daycursor.isAfterLast(); daycursor
						.moveToNext()) {
					int dbthour = daycursor.getInt(daycursor
							.getColumnIndex(UidTrafficTag.tag_THOUR));
					String toReceived = daycursor.getString(daycursor
							.getColumnIndex(UidTrafficTag.tag_RECEIVED_TOTAL));
					String totalUpload = daycursor.getString(daycursor
							.getColumnIndex(UidTrafficTag.tag_UPLOAD_TOTAL));
					long totalData = TextFormater
							.convertStringToLong(totalUpload)
							+ TextFormater.convertStringToLong(toReceived);
					values.put(dbthour, String.valueOf(totalData));
				}
				return values;
			}
			daycursor.close();
			db.close();

		} else {
			// / 说明没有数据
			return null;
		}
		return null;
	}

	/**
	 * 
	 * 获取某个UID一天各个小时内的上传,下载流量大小,包括包名
	 * 
	 * @param tDay
	 * @param uid
	 * @param month
	 * @param year
	 * @return
	 */
	public TreeMap<Integer, UidTraffic> getTrafficDetailsDataByUidAndDay(
			int tDay, int uid, int month, int year) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select * from "
				+ DBInfo.Table.EMM_TRAFFIC_UID_INFO_TB_NAME
				+ " where  UID = ? ,  TDAY =? , TMONTH= ? , TYEAR=? order by THOUR desc ";
		Cursor daycursor = db.rawQuery(sql,
				new String[] { String.valueOf(uid), String.valueOf(tDay),
						String.valueOf(month), String.valueOf(year) });
		if (null != daycursor) {
			if (daycursor.getCount() > 0) {
				TreeMap<Integer, UidTraffic> values = new TreeMap<Integer, UidTraffic>();
				for (daycursor.moveToFirst(); !daycursor.isAfterLast(); daycursor
						.moveToNext()) {
					int dbthour = daycursor.getInt(daycursor
							.getColumnIndex(UidTrafficTag.tag_THOUR));
					String toReceived = daycursor.getString(daycursor
							.getColumnIndex(UidTrafficTag.tag_RECEIVED_TOTAL));
					String totalUpload = daycursor.getString(daycursor
							.getColumnIndex(UidTrafficTag.tag_UPLOAD_TOTAL));
					String packageName = daycursor.getString(daycursor
							.getColumnIndex(UidTrafficTag.tag_PACKAGE_NAME));
					String appName = daycursor.getString(daycursor
							.getColumnIndex(UidTrafficTag.tag_APPNAME));
					UidTraffic uidTraffic = new UidTraffic();
					uidTraffic.setReceived_total(TextFormater
							.convertStringToLong(toReceived));
					uidTraffic.setUploaded_total(TextFormater
							.convertStringToLong(totalUpload));
					uidTraffic.setPackageName(packageName);
					uidTraffic.setAppName(appName);
					values.put(dbthour, uidTraffic);
				}
				return values;
			}

			daycursor.close();
			db.close();
		} else {
			// / 说明没有数据
			return null;
		}
		return null;
	}

	/**
	 * 
	 * 判断指定某一天的某一个包名是否存在流量信息
	 * 
	 * @param tday
	 * @param tmonth
	 * @param tyear
	 * @param packagename
	 * @return
	 */
	public UidTraffic getSpecificAppDataByDaypackagename(int tday, int tmonth,
			int tyear, String packagename) {
		String sql = Traffic_Constants.subquery.sql_statistic_appstraffics_bydaypackagename;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor daycursor = db.rawQuery(sql,
				new String[] { String.valueOf(tyear), String.valueOf(tmonth),
						String.valueOf(tday), packagename });
		if (null != daycursor) {
			if (daycursor.getCount() > 0) {
				UidTraffic uidTraffic = new UidTraffic();
				for (daycursor.moveToFirst(); !daycursor.isAfterLast(); daycursor
						.moveToNext()) {
					int uid = daycursor.getInt(daycursor
							.getColumnIndex(UidTrafficTag.tag_UID));
					if (uid <= 0) {
						continue;
					}
					String toReceived = daycursor.getString(daycursor
							.getColumnIndex(UidTrafficTag.tag_RECEIVED_TOTAL));
					String totalUpload = daycursor.getString(daycursor
							.getColumnIndex(UidTrafficTag.tag_UPLOAD_TOTAL));
					String packageName = daycursor.getString(daycursor
							.getColumnIndex(UidTrafficTag.tag_PACKAGE_NAME));
					String appName = daycursor.getString(daycursor
							.getColumnIndex(UidTrafficTag.tag_APPNAME));
					uidTraffic.setPackageName(packageName);
					uidTraffic.setUid(uid);
					uidTraffic.setReceived_total(TextFormater
							.convertStringToLong(toReceived));
					uidTraffic.setUploaded_total(TextFormater
							.convertStringToLong(totalUpload));
					uidTraffic.setTDAY(tday);
					uidTraffic.setTMONTH(tmonth);
					uidTraffic.setTYEAR(tyear);
					uidTraffic.setAppName(appName);
				}
				daycursor.close();
				db.close();
				return uidTraffic;
			}
			return null;
		}
		return null;
	}

	/**
	 * 获取某一天的某一个应用的流量信息,
	 * 
	 * 存在的问题::: 1. 如果前一天不存在数据可能会导致这一天的数据不存在. 2. 不会通过跨月流量计算 ,tday 修正方案 ::::
	 * 1.可以提前查询一下前一天是否存在该应用的数据,如果不存在,则该应用的流量信息就按照当天最后记录时间对应的数据 ::::
	 * 2.可以进行根据日期进行分析判断获取合适的日期方法,采取跨月流量计算
	 * 
	 * @param tDay
	 * @param uid
	 * @param month
	 * @param year
	 * @return
	 */
	public UidTraffic getTrafficSingleAppDataByDaypackagename(int tday,
			String packagename, int tmonth, int tyear) {
		UidTraffic existsAppDataByDaypackagename = getSpecificAppDataByDaypackagename(
				tday - 1, tmonth, tyear, packagename);
		// / 判断前一天是否为null
		if (existsAppDataByDaypackagename != null) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			String sql = Traffic_Constants.subquery.sql_statistic_appsbydaypackagename;
			Cursor daycursor = db.rawQuery(
					sql,
					new String[] { String.valueOf(tyear),
							String.valueOf(tmonth), String.valueOf(tday),
							packagename, String.valueOf(tyear),
							String.valueOf(tmonth), String.valueOf(tday - 1),
							packagename });
			if (null != daycursor) {
				if (daycursor.getCount() > 0) {
					UidTraffic uidTraffic = new UidTraffic();
					for (daycursor.moveToFirst(); !daycursor.isAfterLast(); daycursor
							.moveToNext()) {
						String toReceived = daycursor
								.getString(daycursor
										.getColumnIndex(UidTrafficTag.tag_RECEIVED_TOTAL));
						String totalUpload = daycursor
								.getString(daycursor
										.getColumnIndex(UidTrafficTag.tag_UPLOAD_TOTAL));
						uidTraffic.setPackageName(packagename);
						uidTraffic.setReceived_total(TextFormater
								.convertStringToLong(toReceived));
						uidTraffic.setUploaded_total(TextFormater
								.convertStringToLong(totalUpload));
						uidTraffic.setTDAY(tday);
						uidTraffic.setTMONTH(tmonth);
						uidTraffic.setTYEAR(tyear);
					}
					return uidTraffic;
				}
				daycursor.close();
				db.close();
			} else {
				// / 说明没有数据
				return null;
			}
			// / 如果不存在则查询当天最后一次记录的时间对应的流量信息作为该应用的当天的流量信息
		} else {

			return getSpecificAppDataByDaypackagename(tday, tmonth, tyear,
					packagename);
		}
		return null;
	}

	/**
	 * 获取当天设备中所有产生流量信息的app列表信息
	 * 
	 * @param tday
	 * @param tmonth
	 * @param tyear
	 * @return
	 */
	public List<UidTraffic> getTrafficAppsByDay(int tday, int tmonth, int tyear) {

		SQLiteDatabase db = dbHelper.getReadableDatabase();

		String sql = "select * from "
				+ DBInfo.Table.EMM_TRAFFIC_UID_INFO_TB_NAME
				+ " where TDAY =?  and TMONTH= ?  and TYEAR=? order by RECEIVED_TOTAL desc ";
		Cursor daycursor = db.rawQuery(sql,
				new String[] { String.valueOf(tday), String.valueOf(tmonth),
						String.valueOf(tyear) });
		List<UidTraffic> uidTraffics = new ArrayList<UidTraffic>();
		if (null != daycursor) {
			if (daycursor.getCount() > 0) {
				for (daycursor.moveToFirst(); !daycursor.isAfterLast(); daycursor
						.moveToNext()) {
					int uid = daycursor.getInt(daycursor
							.getColumnIndex(UidTrafficTag.tag_UID));
					if (uid <= 0) {
						continue;
					}
					String toReceived = daycursor.getString(daycursor
							.getColumnIndex(UidTrafficTag.tag_RECEIVED_TOTAL));
					String totalUpload = daycursor.getString(daycursor
							.getColumnIndex(UidTrafficTag.tag_UPLOAD_TOTAL));
					String packageName = daycursor.getString(daycursor
							.getColumnIndex(UidTrafficTag.tag_PACKAGE_NAME));
					String appName = daycursor.getString(daycursor
							.getColumnIndex(UidTrafficTag.tag_APPNAME));
					UidTraffic uidTraffic = new UidTraffic();
					uidTraffic.setPackageName(packageName);
					uidTraffic.setUid(uid);
					uidTraffic.setReceived_total(TextFormater
							.convertStringToLong(toReceived));
					uidTraffic.setUploaded_total(TextFormater
							.convertStringToLong(totalUpload));
					uidTraffic.setTDAY(tday);
					uidTraffic.setTMONTH(tmonth);
					uidTraffic.setTYEAR(tyear);
					uidTraffic.setAppName(appName);
					uidTraffics.add(uidTraffic);
				}
				return uidTraffics;
			}
			daycursor.close();
			db.close();

		} else {
			// / 说明没有数据
			return null;
		}
		return null;

	}

	/**
	 * 获取指定天数产生流量的App列表
	 * 
	 * @param tday
	 * @param tmonth
	 * @param tyear
	 * @return
	 */
	public List<UidTraffic> getHasTrafficAppsByDay(int tday, int tmonth,
			int tyear) {

		SQLiteDatabase db = dbHelper.getReadableDatabase();

		String sql = Traffic_Constants.subquery.sql_statisticappscategory_byday;
		Cursor daycursor = db.rawQuery(sql,
				new String[] { String.valueOf(tyear), String.valueOf(tmonth),
						String.valueOf(tday) });
		List<UidTraffic> uidTraffics = new ArrayList<UidTraffic>();
		if (null != daycursor) {
			if (daycursor.getCount() > 0) {
				for (daycursor.moveToFirst(); !daycursor.isAfterLast(); daycursor
						.moveToNext()) {
					int uid = daycursor.getInt(daycursor
							.getColumnIndex(UidTrafficTag.tag_UID));
					if (uid <= 0) {
						continue;
					}
					String toReceived = daycursor.getString(daycursor
							.getColumnIndex(UidTrafficTag.tag_RECEIVED_TOTAL));
					String totalUpload = daycursor.getString(daycursor
							.getColumnIndex(UidTrafficTag.tag_UPLOAD_TOTAL));
					if(TextFormater.convertStringToLong(totalUpload)<=0&&TextFormater.convertStringToLong(toReceived)<=0){
						continue;
					}
					String packageName = daycursor.getString(daycursor
							.getColumnIndex(UidTrafficTag.tag_PACKAGE_NAME));
					String appName = daycursor.getString(daycursor
							.getColumnIndex(UidTrafficTag.tag_APPNAME));
					UidTraffic uidTraffic = new UidTraffic();
					uidTraffic.setPackageName(packageName);
					uidTraffic.setUid(uid);
					uidTraffic.setReceived_total(TextFormater
							.convertStringToLong(toReceived));
					uidTraffic.setUploaded_total(TextFormater
							.convertStringToLong(totalUpload));
					uidTraffic.setTDAY(tday);
					uidTraffic.setTMONTH(tmonth);
					uidTraffic.setTYEAR(tyear);
					uidTraffic.setAppName(appName);
					uidTraffics.add(uidTraffic);
				}
				return uidTraffics;
			}
			daycursor.close();
			db.close();
		} else {
			// / 说明没有数据
			return null;
		}
		return null;

	}

	/**
	 * UID TEXT, PACKAGE_NAME TEXT, RECEIVED_TOTAL INTEGER, UPLOAD_TOTAL
	 * INTEGER, TTIME TEXT, THOUR INTEGER, TDAY INTEGER, TMONTH INTEGER , TYEAR
	 * INTEGER
	 */
	public void insertTrafficAppsData(UidTraffic uidTraffic) {
		if (uidTraffic != null) {
			if(uidTraffic.getReceived_total()<=0&&uidTraffic.getUploaded_total()<=0){
				return ; 
			}
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			String sql = "insert into "
					+ DBInfo.Table.EMM_TRAFFIC_UID_INFO_TB_NAME
					+ " (UID,PACKAGE_NAME,APPNAME, RECEIVED_TOTAL,UPLOAD_TOTAL,TTIME,THOUR,TDAY,TMONTH,TYEAR) values (?,?,?,?,?,?,?,?,?,?) ";
			SQLiteStatement compileStatement = db.compileStatement(sql);
			db.beginTransaction();
			compileStatement.bindString(1, String.valueOf(uidTraffic.getUid()));
			compileStatement.bindString(2, uidTraffic.getPackageName());
			compileStatement.bindString(3, uidTraffic.getAppName());
			compileStatement.bindString(4,String.valueOf(uidTraffic.getReceived_total()));
			compileStatement.bindString(5,String.valueOf(uidTraffic.getUploaded_total()));
			compileStatement.bindString(6,String.valueOf(SystemClock.currentThreadTimeMillis()));
			compileStatement.bindString(7,String.valueOf(uidTraffic.getTHOUR()));
			compileStatement.bindString(8, String.valueOf(uidTraffic.getTDAY()));
			compileStatement.bindString(9,String.valueOf(uidTraffic.getTMONTH()));
			compileStatement.bindString(10,String.valueOf(uidTraffic.getTYEAR()));
			compileStatement.execute();
			compileStatement.clearBindings();
			compileStatement.close();
			db.setTransactionSuccessful();
			db.endTransaction();
//			L.d(this.getClass(), "insert into EMM_TRAFFIC_UID_INFO_TB_NAME sysdata "+ uidTraffic);
			db.close();
		}
	}

	/**
	 * 将收集到的UIdS的流量信息 插入数据库
	 * 
	 * @param uidTraffics
	 */
	public void saveToAppTrafficDB(List<UidTraffic> uidTraffics) {
		for (Iterator<UidTraffic> iterator = uidTraffics.iterator(); iterator
				.hasNext();) {
			UidTraffic uidTraffic = iterator.next();
			insertTrafficAppsData(uidTraffic);
		}
	}


	/**
	 * ����
	 * 
	 * @param olderNumber
	 * @param newNumber
	 */
	public void update(String olderNumber, String newNumber) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("update blacknumber set number=? where number=?",
					new Object[] { newNumber, olderNumber });
			db.close();
		}
	}
}
