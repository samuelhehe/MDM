package com.foxconn.emm.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.foxconn.emm.bean.InstalledAppInfo;
import com.foxconn.emm.bean.LimitListInfo;
import com.foxconn.emm.bean.UserInfo;
import com.foxconn.emm.db.DBHelper;
import com.foxconn.emm.tools.PakageInfoService;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.L;

/**
 * 
 * 动态更新限制名单类别策略: 1.根据从Server端发送过来的LimitList<LimitlistInfo> 根据packagename 进行查找.
 * 2.根据查找出来的信息和发送过来的进行类型对比 3.如果不一样则更新DB中对应的LimitList信息,为从Server端发送过来的列表对应的值.
 */
public class LimitListDao {
	protected Context context;
	private DBHelper dbHelper;

	public LimitListDao(Context context) {
		this.context = context;
		dbHelper = new DBHelper(context);
	}

	/**
	 * 
	 * 查找所有限制名单的列表.
	 * 
	 * @return
	 */
	public List<LimitListInfo> getLimitList() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<LimitListInfo> packnames = new ArrayList<LimitListInfo>();
		if (db.isOpen()) {
			Cursor cursor = db
					.rawQuery(
							"select packname , limitType , limitLimitType ,limitPwdTime ,limitEnableTime , limitDisableTime , desc from limitList ",
							null);
			LimitListInfo limitListInfo;
			while (cursor.moveToNext()) {
				limitListInfo = new LimitListInfo();
				limitListInfo.setPackName(cursor.getString(cursor
						.getColumnIndex(LimitListInfo.TAG.PACKNAME)));
				limitListInfo.setLimitType(cursor.getString(cursor
						.getColumnIndex(LimitListInfo.TAG.LIMITTYPE)));
				limitListInfo.setLimitLimitType(cursor.getString(cursor
						.getColumnIndex(LimitListInfo.TAG.LIMITLIMITTYPE)));
				limitListInfo.setLimitPwdTime(cursor.getString(cursor
						.getColumnIndex(LimitListInfo.TAG.LIMITPWDTIME)));
				limitListInfo.setLimitEnableTime(cursor.getString(cursor
						.getColumnIndex(LimitListInfo.TAG.LIMITENABLETIME)));
				limitListInfo.setLimitDisableTime(cursor.getString(cursor
						.getColumnIndex(LimitListInfo.TAG.LIMITDISABLETIME)));
				packnames.add(limitListInfo);
			}
			cursor.close();
			db.close();
		}
		return packnames;
	}

	/**
	 * 根据限制名单的类型进行查询
	 * 
	 * @param type
	 *            限制名单的类型 0:黑名单 1:白名单 2:限制名单 <br>
	 *            限制名单的限制类别: 1:按照时间限制 0:按照密码进行限制
	 * 
	 * @return
	 */
	public List<LimitListInfo> getLimitListbyType(int type) {
		if (type >= 0 && type <= 2) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			List<LimitListInfo> packnames = new ArrayList<LimitListInfo>();
			if (db.isOpen()) {
				Cursor cursor = db
						.rawQuery(
								"select packname , limitType , limitLimitType ,limitPwdTime ,limitEnableTime , limitDisableTime , desc from limitList  where limitType = ? ",
								new String[] { type + "" });

				LimitListInfo limitListInfo;
				while (cursor.moveToNext()) {
					limitListInfo = new LimitListInfo();
					limitListInfo.setPackName(cursor.getString(cursor
							.getColumnIndex(LimitListInfo.TAG.PACKNAME)));
					limitListInfo.setLimitType(cursor.getString(cursor
							.getColumnIndex(LimitListInfo.TAG.LIMITTYPE)));
					limitListInfo.setLimitLimitType(cursor.getString(cursor
							.getColumnIndex(LimitListInfo.TAG.LIMITLIMITTYPE)));
					limitListInfo.setLimitPwdTime(cursor.getString(cursor
							.getColumnIndex(LimitListInfo.TAG.LIMITPWDTIME)));
					limitListInfo
							.setLimitEnableTime(cursor.getString(cursor
									.getColumnIndex(LimitListInfo.TAG.LIMITENABLETIME)));
					limitListInfo
							.setLimitDisableTime(cursor.getString(cursor
									.getColumnIndex(LimitListInfo.TAG.LIMITDISABLETIME)));
					packnames.add(limitListInfo);
				}
				cursor.close();
				db.close();
			}
			return packnames;
		} else {
			return null;
		}
	}

	/**
	 * 
	 * 获取过滤过的黑名单列表,在系统中已经安装的黑名单列表
	 * 
	 * @param packageNameList
	 * @return
	 */
	public List<InstalledAppInfo> getFilteredPackageName(
			List<String> packageNameList) {

		List<InstalledAppInfo> installedAppInfos = PakageInfoService
				.getAppInfos(context);
		List<InstalledAppInfo> filteredPackageList = new ArrayList<InstalledAppInfo>();
		for (String packFilterBefore : packageNameList) {
			for (InstalledAppInfo installedAppInfo : installedAppInfos) {
				if (installedAppInfo.getPackageName().equalsIgnoreCase(
						packFilterBefore)) {
					filteredPackageList.add(installedAppInfo);
				} else {
					continue;
				}
			}
		}
		return filteredPackageList;
	}

	/**
	 * 获取过滤过的过滤限制名单列表,在系统中已经安装的限制名单列表
	 * 
	 * @param limitListInfos
	 * @return
	 */
	public List<InstalledAppInfo> getFilteredPackageLimitList(
			List<LimitListInfo> limitListInfos) {
		if (limitListInfos != null && limitListInfos.size() >= 1) {
			List<InstalledAppInfo> installedAppInfos = PakageInfoService
					.getAppInfos(context);
			List<InstalledAppInfo> filteredPackageList = new ArrayList<InstalledAppInfo>();
			for (InstalledAppInfo installedAppInfo : installedAppInfos) {
				InstalledAppInfo filteredInstalledAppInfo = filter(
						installedAppInfo, limitListInfos);
				if (filteredInstalledAppInfo != null) {
					filteredPackageList.add(filteredInstalledAppInfo);
				}
			}
			return filteredPackageList;
		} else {
			return null;
		}
	}

	private InstalledAppInfo filter(InstalledAppInfo installedAppInfo,
			List<LimitListInfo> limitListInfos) {

		if (limitListInfos != null && limitListInfos.size() > 0) {

			for (LimitListInfo limitListInfo : limitListInfos) {
				if (limitListInfo != null
						&& !TextUtils.isEmpty(limitListInfo.getPackName())) {
					if (limitListInfo.getPackName().equalsIgnoreCase(
							installedAppInfo.getPackageName())) {
						// //查找到之后移除,降低时间空间复杂度
						limitListInfos.remove(limitListInfo);
						installedAppInfo
								.setLimittype(verifyLimitType(limitListInfo));
						return installedAppInfo;
					}
				}
			}

		} else {
			return null;
		}
		return null;
	}

	private int verifyLimitType(LimitListInfo limitListInfo) {

		if (limitListInfo.getLimitType().equals(
				EMMContants.LIMITLIST_CONTANT.LT_LIMIT_LIST + "")) {
			if (limitListInfo.getLimitLimitType().equals(
					EMMContants.LIMITLIST_CONTANT.LLT_PASSWORD)) {
				return EMMContants.LIMITLIST_CONTANT.LLT_FLAG_PASSWORD;
			} else if (limitListInfo.getLimitLimitType().equals(
					EMMContants.LIMITLIST_CONTANT.LLT_TIME)) {
				return EMMContants.LIMITLIST_CONTANT.LLT_FLAG_TIME;
			}
		} else if (limitListInfo.getLimitType().equals(
				EMMContants.LIMITLIST_CONTANT.LT_BLACK_LIST)) {
			return EMMContants.LIMITLIST_CONTANT.LT_BLACK_LIST;
		} else if (limitListInfo.getLimitType().equals(
				EMMContants.LIMITLIST_CONTANT.LT_WHITE_LIST)) {
			return EMMContants.LIMITLIST_CONTANT.LT_WHITE_LIST;
		}
		return -1;
	}

	/**
	 * 查找具体限制类型的对应包名,是否存在. 查找逻辑: 1.当黑名单发送过来,需要查询同包名的,白名单,限制名单的个数.
	 * 2.当白名单发送过来,需要查询同包名的,黑名单,限制名单的个数. 3.当限制名单发送过来,需要查询同包名的,黑名单,白名单的 个数.
	 * 
	 * 
	 * @param number
	 * @return
	 */
	public boolean find(LimitListInfo limitListInfo) {
		boolean result = false;
		if (TextUtils.isEmpty(limitListInfo.getPackName())) {
			return false;
		}
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db
					.rawQuery(
							"select * from limitList where packname= ? ",
							new String[] { limitListInfo.getPackName() });
			if (cursor.moveToNext()) {
				result = true;
			}
			cursor.close();
			db.close();
		}
		return result;
	}

	public void add(LimitListInfo limitListInfo) {
		// / 查找到了的话直接更新.
		if (find(limitListInfo)) {
			update(limitListInfo);
			return;
		} else {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			if (db.isOpen()) {
				db.execSQL(
						"replace  into limitList ( packname , limitType ,limitLimitType ,limitPwdTime ,limitEnableTime , limitDisableTime , desc) values (?,?,?,?,?,?,?)",
						new Object[] { limitListInfo.getPackName(),
								limitListInfo.getLimitType(),
								limitListInfo.getLimitLimitType(),
								limitListInfo.getLimitPwdTime(),
								limitListInfo.getLimitEnableTime(),
								limitListInfo.getLimitDisableTime(),
								limitListInfo.getDesc() });
				db.close();
			}
		}
	}

	public int update(LimitListInfo limitListInfo) {

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues(7);
		
		values.put(LimitListInfo.TAG.PACKNAME, limitListInfo.getPackName());
		values.put(LimitListInfo.TAG.LIMITTYPE, limitListInfo.getLimitType());
		values.put(LimitListInfo.TAG.LIMITLIMITTYPE,
				limitListInfo.getLimitLimitType());
		values.put(LimitListInfo.TAG.LIMITPWDTIME,
				limitListInfo.getLimitPwdTime());
		values.put(LimitListInfo.TAG.LIMITENABLETIME,
				limitListInfo.getLimitEnableTime());
		values.put(LimitListInfo.TAG.LIMITDISABLETIME,
				limitListInfo.getLimitDisableTime());
		values.put(LimitListInfo.TAG.DESC, limitListInfo.getDesc());
		int count = db.update("limitList", values, "packname" + " = ?",
				new String[] { limitListInfo.getPackName() });
		db.close();
		L.d(this.getClass(), "limit list info is update");
		return count;
	}

	/**
	 * 删除指定包名,指定限制类型的一行记录
	 * 
	 * @param packname
	 */
	public void delete(String packname, String limitType) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL(
					"delete from limitList where packname=? and  limitType = ? ",
					new Object[] { packname, limitType });
			db.close();
		}
	}

	/**
	 * 删除指定包名的一行记录
	 */
	public void deleteByPackageName(String packageName) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("delete from limitList where packname=?  ",
					new Object[] { packageName });
			db.close();
		}
	}

}
