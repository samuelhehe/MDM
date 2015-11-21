package com.foxconn.emm.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.foxconn.emm.bean.NotificationInfo;
import com.foxconn.emm.db.DBHelper;
import com.foxconn.emm.db.DBInfo;
import com.foxconn.emm.utils.L;

public class NotificationInfoDao {
	private Context context;
	private DBHelper dbHelper;

	private String[] queryColumns = { NotificationInfo.TAG.ID,
			NotificationInfo.TAG.CONTENT, NotificationInfo.TAG.MSGTYPE,
			NotificationInfo.TAG.SENDTIME };

	public NotificationInfoDao(Context context) {
		this.context = context;
		dbHelper = new DBHelper(context);
	}

	public void add(NotificationInfo notificationInfo) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(NotificationInfo.TAG.CONTENT, notificationInfo.getContent());
		values.put(NotificationInfo.TAG.MSGTYPE, notificationInfo.getMsgType());
		values.put(NotificationInfo.TAG.SENDTIME,
				notificationInfo.getSendTime());
		db.insert(DBInfo.Table.NOTIFI_TB_NAME, null, values);
		db.close();
	}

	public List<NotificationInfo> findAll() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<NotificationInfo> notificationInfos = new ArrayList<NotificationInfo>();
		Cursor cursor = db.query(DBInfo.Table.NOTIFI_TB_NAME, queryColumns,
				null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				if (cursor.moveToLast()) {
					do {
						NotificationInfo notificationInfo = new NotificationInfo();
						notificationInfo.setId(cursor.getString(cursor
								.getColumnIndex(NotificationInfo.TAG.ID)));
						notificationInfo.setContent(cursor.getString(cursor
								.getColumnIndex(NotificationInfo.TAG.CONTENT)));
						notificationInfo.setMsgType(cursor.getString(cursor
								.getColumnIndex(NotificationInfo.TAG.MSGTYPE)));
						notificationInfo
								.setSendTime(cursor.getString(cursor
										.getColumnIndex(NotificationInfo.TAG.SENDTIME)));
						notificationInfos.add(notificationInfo);
						L.d(this.getClass(), notificationInfos.toString());
					} while (cursor.moveToPrevious());
				}
			}
		}
		cursor.close();
		db.close();
		return notificationInfos;
	}

	public int delete(String id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int i = db.delete(DBInfo.Table.NOTIFI_TB_NAME, NotificationInfo.TAG.ID
				+ " =? ", new String[] { id });
		db.close();
		return i;
	}

}
