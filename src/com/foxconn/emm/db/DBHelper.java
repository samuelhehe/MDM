package com.foxconn.emm.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DBHelper
 */
public class DBHelper extends SQLiteOpenHelper {

	protected List<String> dropDataTableSqlList = new ArrayList<String>();
	
	public DBHelper(Context context) {
		super(context, DBInfo.DB.DB_NAME, null, DBInfo.DB.VERSION);
		dropDataTableSqlList.add(DBInfo.Table.USER_INFO_DROP);
		dropDataTableSqlList.add(DBInfo.Table.TRAFFIC_LIST_DROP);
		dropDataTableSqlList.add(DBInfo.Table.LIMITLIST_DROP);
		dropDataTableSqlList.add(DBInfo.Table.WEBPAGE_DROP);
		dropDataTableSqlList.add(DBInfo.Table.NOTIFI_DROP);
		dropDataTableSqlList.add(DBInfo.Table.APPS_DROP);
		dropDataTableSqlList.add(DBInfo.Table.CALENDAR_DROP);
		dropDataTableSqlList.add(DBInfo.Table.FILEURL_IMG_DROP);
		dropDataTableSqlList.add(DBInfo.Table.EMM_TRAFFIC_INFO_DROP);
		dropDataTableSqlList.add(DBInfo.Table.EMM_TRAFFIC_UID_INFO_DROP);
		dropDataTableSqlList.add(DBInfo.Table.FOXCONN_LIB_DOWNLOAD_DROP);
		
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DBInfo.Table.USER_INFO_CREATE);
		db.execSQL(DBInfo.Table.TRAFFIC_LIST_CREATE);
		db.execSQL(DBInfo.Table.LIMITLIST_CREATE);
		db.execSQL(DBInfo.Table.WEBPAGE_CREATE);
		db.execSQL(DBInfo.Table.NOTIFI_CREATE);
		db.execSQL(DBInfo.Table.CALENDAR_CREATE);
		db.execSQL(DBInfo.Table.FILEURL_IMG_CREATE);
		db.execSQL(DBInfo.Table.EMM_TRAFFIC_INFO_CREATE);
		db.execSQL(DBInfo.Table.EMM_TRAFFIC_UID_INFO_CREATE);
		db.execSQL(DBInfo.Table.APPS_CREATE);
		db.execSQL(DBInfo.Table.FOXCONN_LIB_DOWNLOAD_CREATE);
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DBInfo.Table.USER_INFO_DROP);

		db.execSQL(DBInfo.Table.TRAFFIC_LIST_DROP);
		db.execSQL(DBInfo.Table.LIMITLIST_DROP);
		db.execSQL(DBInfo.Table.WEBPAGE_DROP);
		db.execSQL(DBInfo.Table.NOTIFI_DROP);
		db.execSQL(DBInfo.Table.APPS_DROP);
		db.execSQL(DBInfo.Table.CALENDAR_DROP);
		db.execSQL(DBInfo.Table.FILEURL_IMG_DROP);
		db.execSQL(DBInfo.Table.EMM_TRAFFIC_INFO_DROP);
		db.execSQL(DBInfo.Table.EMM_TRAFFIC_UID_INFO_DROP);
		db.execSQL(DBInfo.Table.FOXCONN_LIB_DOWNLOAD_DROP);
		
		onCreate(db);
	}

}
