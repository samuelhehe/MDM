package com.foxconn.emm.dao;

import java.util.ArrayList;
import java.util.List;

import com.foxconn.emm.bean.AppInfo;
import com.foxconn.emm.db.DBHelper;
import com.foxconn.emm.db.DBInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AppInfoDao {
	private Context context;
	private DBHelper dbHelper;
	private String[] queryColumns = { AppInfo.TAG.APPNAME,
			AppInfo.TAG.FILESIZE, AppInfo.TAG.FILEURL, AppInfo.TAG.ID,
			AppInfo.TAG.MSGTYPE, AppInfo.TAG.PACKAGENAME, AppInfo.TAG.SENDTIME };

	public AppInfoDao(Context context) {
		this.context = context;
		dbHelper = new DBHelper(context);
	}

	public void add(AppInfo appInfo) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(AppInfo.TAG.APPNAME, appInfo.getAppName());
		values.put(AppInfo.TAG.FILESIZE, appInfo.getFileSize());
		values.put(AppInfo.TAG.FILEURL, appInfo.getFileUrl());
		values.put(AppInfo.TAG.MSGTYPE, appInfo.getMsgType());
		values.put(AppInfo.TAG.PACKAGENAME, appInfo.getPackageName());
		values.put(AppInfo.TAG.SENDTIME, appInfo.getSendTime());
		db.insert(DBInfo.Table.APPS_TB_NAME, null, values);
		db.close();
	}

	public List<AppInfo> findAll() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		Cursor cursor = db.query(DBInfo.Table.APPS_TB_NAME, queryColumns, null,
				null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				if (cursor.moveToLast()) {
					do {
						AppInfo appInfo = new AppInfo();
						appInfo.setAppName(cursor.getString(cursor
								.getColumnIndex(AppInfo.TAG.APPNAME)));
						appInfo.setFileSize(cursor.getString(cursor
								.getColumnIndex(AppInfo.TAG.FILESIZE)));
						appInfo.setFileUrl(cursor.getString(cursor
								.getColumnIndex(AppInfo.TAG.FILEURL)));
						appInfo.setId(cursor.getString(cursor
								.getColumnIndex(AppInfo.TAG.ID)));
						appInfo.setMsgType(cursor.getString(cursor
								.getColumnIndex(AppInfo.TAG.MSGTYPE)));
						appInfo.setPackageName(cursor.getString(cursor
								.getColumnIndex(AppInfo.TAG.PACKAGENAME)));
						appInfo.setSendTime(cursor.getString(cursor
								.getColumnIndex(AppInfo.TAG.SENDTIME)));
						appInfos.add(appInfo);
					} while (cursor.moveToPrevious());
				}
			}
		}
		cursor.close();
		db.close();
		return appInfos;
	}

	public int delete(String id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int i = db.delete(DBInfo.Table.APPS_TB_NAME, AppInfo.TAG.ID + " =? ",
				new String[] { id });
		db.close();
		return i;
	}
}
