package com.foxconn.emm.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.foxconn.emm.bean.SysTraffic;
import com.foxconn.emm.db.DBHelper;
import com.foxconn.emm.utils.DateUtil;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.utils.Traffic_Constants;

/**
 * 这个主要操作的数据库对象是 针对设备总体流量的一个统计
 * 
 */
public class TrafficOptDao {
	protected Context context;
	private DBHelper dbHelper;

	public TrafficOptDao(Context context) {
		this.context = context;
		dbHelper = new DBHelper(context);
	}

	/**
	 * 
	 * 获取当前系统使用的数据流量总量
	 * 
	 * @return
	 */
	public String getUsedTotalTrafficToCurrentDay() {
		Calendar calendar = Calendar.getInstance();
		int tmonth = calendar.get(Calendar.MONTH)+1;

		int tyear = calendar.get(Calendar.YEAR);
		TreeMap<Integer, String> trafficDataByMonth = getTrafficDataByMonth(
				tmonth , tyear);
		long dataTrafficCount = 0L;
		if (trafficDataByMonth != null) {
			Set<Integer> keySet = trafficDataByMonth.keySet();
			int day_Of_Month = calendar.get(Calendar.DAY_OF_MONTH);
			for (int i = 1; i <= day_Of_Month; i++) {
				if (keySet.contains(i)) {
					dataTrafficCount += Long.valueOf(trafficDataByMonth.get(i));
				}
			}
		}
		// / 说明记录出错，或者没有流量信息。
		return String.valueOf(dataTrafficCount);
	}

	/**
	 * 查询对应月份的DB中所有的天数以及天数所对应的数据流量信息 1.获取当月流量分布 a.每天流量数据 1.获取某月的所有拥有流量的天数列表信息
	 * 2.根据所拥有流量天数信息列表进行单个查询某一天的具体流量信息 3.返回当月流量天数信息与流量信息对应的Map
	 * 4.根据Map与BarChart进行交互产生Barchart
	 * 
	 * getSysTrafficDataByMonth
	 * 
	 * getTrafficSingleSysDataByDay
	 * 
	 */
	public TreeMap<Integer, String> getTrafficDataByMonth(int tmonth, int tyear) {
		List<SysTraffic> sysTrafficDataByMonth = getSysTrafficDataByMonth(tmonth, tyear);
		if (sysTrafficDataByMonth != null) {
			TreeMap<Integer, String> values = new TreeMap<Integer, String>();

			for (Iterator<SysTraffic> iterator = sysTrafficDataByMonth
					.iterator(); iterator.hasNext();) {
				SysTraffic sysTraffic = iterator.next();
				Calendar calendar = Calendar.getInstance();
				//// 月份之前存储的是按照实际月份存储的.查询时需要-1
				calendar.set(sysTraffic.getTyear(), sysTraffic.getTmonth()-1,
						sysTraffic.getTday());
				if (sysTraffic != null) {
					SysTraffic trafficSingleSysDataByDay = getTrafficSingleSysDataByDay(calendar);
					if(trafficSingleSysDataByDay!=null){
					values.put(trafficSingleSysDataByDay.getTday(),
							trafficSingleSysDataByDay.getTd());
					}
				}
			}
			return values;
		} else {
			return null;
		}
	}

	/**
	 * 获取某一天最后一条的系统流量信息
	 * 
	 * @param tday
	 * @param tmonth
	 * @param tyear
	 * @return
	 */
	public SysTraffic getSpecificSysTrafficDataByDay(Calendar calendar) {
		int tday = calendar.get(Calendar.DAY_OF_MONTH);
		int tmonth = calendar.get(Calendar.MONTH) + 1;
		int tyear = calendar.get(Calendar.YEAR);
		return getSpecificSysTrafficDataByDay(tday, tmonth, tyear);
	}

	/**
	 * 获取某一天最后一条的系统流量信息
	 * 
	 * @param tday
	 * @param tmonth
	 * @param tyear
	 * @return
	 */
	public SysTraffic getSpecificSysTrafficDataByDay(int tday, int tmonth,
			int tyear) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = Traffic_Constants.subquery.sql_statistic_singledaysystraffics_byday;
		Cursor daycursor = db.rawQuery(sql,
				new String[] { String.valueOf(tyear), String.valueOf(tmonth),
						String.valueOf(tday) });
		if (null != daycursor) {
			if (daycursor.getCount() > 0) {
				SysTraffic sysTraffic = new SysTraffic();
				for (daycursor.moveToFirst(); !daycursor.isAfterLast(); daycursor
						.moveToNext()) {
					String totaldata = daycursor.getString(daycursor
							.getColumnIndex(SysTraffic.TAG.tag_TD));
					String totalWlan = daycursor.getString(daycursor
							.getColumnIndex(SysTraffic.TAG.tag_TW));
					sysTraffic.setTd(Long.valueOf(totaldata));
					sysTraffic.setTw(Long.valueOf(totalWlan));
					sysTraffic.setUtd(0L);
					sysTraffic.setTmonth(tmonth);
					sysTraffic.setTyear(tyear);
					sysTraffic.setTday(tday);
				}
				return sysTraffic;
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
	 * 获取某一天的流量信息,
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
	@Deprecated
	public SysTraffic getTrafficSingleSysDataByDay(int tday, int tmonth,
			int tyear) {
		SysTraffic existsAppDataByDaypackagename = getSpecificSysTrafficDataByDay(
				tday - 1, tmonth, tyear);
		// / 判断前一天是否为null
		if (existsAppDataByDaypackagename != null) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			String sql = Traffic_Constants.subquery.sql_statistic_systraffics_byday;
			Cursor daycursor = db.rawQuery(
					sql,
					new String[] { String.valueOf(tyear),
							String.valueOf(tmonth), String.valueOf(tday),
							String.valueOf(tyear), String.valueOf(tmonth),
							String.valueOf(tday - 1) });
			if (null != daycursor) {
				if (daycursor.getCount() > 0) {
					SysTraffic sysTraffic = new SysTraffic();
					for (daycursor.moveToFirst(); !daycursor.isAfterLast(); daycursor
							.moveToNext()) {
						String todayData = daycursor.getString(daycursor
								.getColumnIndex(SysTraffic.TAG.tag_TD));
						String todayWlan = daycursor.getString(daycursor
								.getColumnIndex(SysTraffic.TAG.tag_TW));
						int dbtday = daycursor.getInt(daycursor
								.getColumnIndex(SysTraffic.TAG.tag_TDAY));
						int dbtmonth = daycursor.getInt(daycursor
								.getColumnIndex(SysTraffic.TAG.tag_TMONTH));
						int dbtyear = daycursor.getInt(daycursor
								.getColumnIndex(SysTraffic.TAG.tag_TYEAR));
						sysTraffic.setTd(Long.valueOf(todayData));
						sysTraffic.setTw(Long.valueOf(todayWlan));
						sysTraffic.setTday(dbtday);
						sysTraffic.setTmonth(dbtmonth);
						sysTraffic.setTyear(dbtyear);
					}
					return sysTraffic;
				}
				daycursor.close();
				db.close();
			} else {
				// / 说明没有数据
				return null;
			}
			// / 如果不存在则查询当天最后一次记录的时间对应的流量信息作为该应用的当天的流量信息
		} else {
			return getSpecificSysTrafficDataByDay(tday, tmonth, tyear);
		}
		return null;
	}

	/**
	 * 获取某一天的流量信息,
	 * 
	 * 存在的问题::: 1. 如果前一天不存在数据可能会导致这一天的数据不存在. 2.tday 修正方案 ::::
	 * 1.可以提前查询一下前一天是否存在该应用的数据,如果不存在,则该应用的流量信息就按照当天最后记录时间对应的数据 ::::
	 * 
	 * @param calendar 查询的时期,直接的日期不需要进行转换,添加月份.
	 * @return
	 */
	public SysTraffic getTrafficSingleSysDataByDay(Calendar calendar) {
		int tday = calendar.get(Calendar.DAY_OF_MONTH);
		int tmonth = calendar.get(Calendar.MONTH)+1;
		int tyear = calendar.get(Calendar.YEAR);

		Calendar calendar2 = DateUtil.minusDay(calendar, 1);
		SysTraffic sysTrafficDataByDay = getSpecificSysTrafficDataByDay(calendar2);
		// / 判断前一天是否为null
		if (sysTrafficDataByDay != null) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			String sql = Traffic_Constants.subquery.sql_statistic_systraffics_byday;
			Cursor daycursor = db
					.rawQuery(
							sql,
							new String[] {
									String.valueOf(tyear),
									String.valueOf(tmonth),
									String.valueOf(tday),
									String.valueOf(calendar2.get(Calendar.YEAR)),
									String.valueOf(calendar2.get(Calendar.MONTH)+1),
									String.valueOf(calendar2.get(Calendar.DAY_OF_MONTH)) });
			if (null != daycursor) {
				if (daycursor.getCount() > 0) {
					SysTraffic sysTraffic = new SysTraffic();
					for (daycursor.moveToFirst(); !daycursor.isAfterLast(); daycursor
							.moveToNext()) {
						String todayData = daycursor.getString(daycursor
								.getColumnIndex(SysTraffic.TAG.tag_TD));
						String todayWlan = daycursor.getString(daycursor
								.getColumnIndex(SysTraffic.TAG.tag_TW));
//						int dbtday = daycursor.getInt(daycursor
//								.getColumnIndex(SysTraffic.TAG.tag_TDAY));
//						int dbtmonth = daycursor.getInt(daycursor
//								.getColumnIndex(SysTraffic.TAG.tag_TMONTH));
//						int dbtyear = daycursor.getInt(daycursor
//								.getColumnIndex(SysTraffic.TAG.tag_TYEAR));
						sysTraffic.setTd(Long.valueOf(todayData));
						sysTraffic.setTw(Long.valueOf(todayWlan));
						sysTraffic.setTday(tday);
						sysTraffic.setTmonth(tmonth);
						sysTraffic.setTyear(tyear);
					}
					return sysTraffic;
				}
				daycursor.close();
				db.close();
			} else {
				// / 说明没有数据
				return null;
			}
			// / 如果不存在则查询当天最后一次记录的时间对应的流量信息作为该应用的当天的流量信息
		} else {
			return getSpecificSysTrafficDataByDay(tday, tmonth, tyear);
		}
		return null;
	}

	/**
	 * 获取当月的所有拥有流量信息的天数列表
	 * 
	 * @param tday
	 * @param tmonth
	 * @param tyear
	 * @return
	 */
	public List<SysTraffic> getSysTrafficDataByMonth(int tmonth, int tyear) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = Traffic_Constants.subquery.sql_statistic_systraffics_days;
		Cursor daycursor = db.rawQuery(sql,
				new String[] { String.valueOf(tyear), String.valueOf(tmonth) });
		List<SysTraffic> sysTraffics = new ArrayList<SysTraffic>();
		if (null != daycursor) {
			if (daycursor.getCount() > 0) {
				for (daycursor.moveToFirst(); !daycursor.isAfterLast(); daycursor
						.moveToNext()) {
					int tday = daycursor.getInt(daycursor
							.getColumnIndex(SysTraffic.TAG.tag_TDAY));
					if (tday <= 0 || tday > 31) {
						continue;
					}
					SysTraffic sysTraffic = new SysTraffic();
					String todayData = daycursor.getString(daycursor
							.getColumnIndex(SysTraffic.TAG.tag_TD));
					String todayWlan = daycursor.getString(daycursor
							.getColumnIndex(SysTraffic.TAG.tag_TW));
					int dbtday = daycursor.getInt(daycursor
							.getColumnIndex(SysTraffic.TAG.tag_TDAY));
					int dbtmonth = daycursor.getInt(daycursor
							.getColumnIndex(SysTraffic.TAG.tag_TMONTH));
					int dbtyear = daycursor.getInt(daycursor
							.getColumnIndex(SysTraffic.TAG.tag_TYEAR));
					sysTraffic.setTd(Long.valueOf(todayData));
					sysTraffic.setTw(Long.valueOf(todayWlan));
					sysTraffic.setTday(dbtday);
					sysTraffic.setTmonth(dbtmonth);
					sysTraffic.setTyear(dbtyear);
					sysTraffics.add(sysTraffic);
				}
				return sysTraffics;
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
	 * TD TEXT, TW TEXT, UTD TEXT , TTIME TEXT, TDAY INTEGER, TMONTH INTEGER
	 * 
	 */
	public void insertTrafficSysData(SysTraffic sysTraffic) {
		if (sysTraffic != null) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			String sql = Traffic_Constants.subquery.sql_statistic_systraffics_insert;
			SQLiteStatement compileStatement = db.compileStatement(sql);
			db.beginTransaction();
			compileStatement.bindString(1, sysTraffic.getTd());
			compileStatement.bindString(2, sysTraffic.getTw());
			compileStatement.bindString(3, sysTraffic.getUtd());
			compileStatement.bindString(4, sysTraffic.gettTime());
			compileStatement
					.bindString(5, String.valueOf(sysTraffic.getTday()));
			compileStatement.bindString(6,
					String.valueOf(sysTraffic.getTmonth()));
			compileStatement.bindString(7,
					String.valueOf(sysTraffic.getTyear()));
			compileStatement.execute();
			compileStatement.clearBindings();
			compileStatement.close();
			db.setTransactionSuccessful();
			db.endTransaction();
			db.close();
			L.d(this.getClass(), "insert into traffic sysdata " + sysTraffic);
		}
	}

}
