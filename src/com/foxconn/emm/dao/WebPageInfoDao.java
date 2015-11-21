package com.foxconn.emm.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.foxconn.emm.bean.WebPageInfo;
import com.foxconn.emm.db.DBHelper;
import com.foxconn.emm.db.DBInfo;

public class WebPageInfoDao {
	private Context context;
	private DBHelper dbHelper;
	private String[] queryColumns = { WebPageInfo.TAG.ID,
			WebPageInfo.TAG.CONTENT, WebPageInfo.TAG.DEADLINE,
			WebPageInfo.TAG.MSGTYPE, WebPageInfo.TAG.PAGEURL,
			WebPageInfo.TAG.SENDTIME, WebPageInfo.TAG.SUBJECT };

	public WebPageInfoDao(Context context) {
		this.context = context;
		dbHelper = new DBHelper(context);
	}

	public void add(WebPageInfo webPageInfo) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(WebPageInfo.TAG.CONTENT, webPageInfo.getContent());
		values.put(WebPageInfo.TAG.DEADLINE, webPageInfo.getDeadline());
		values.put(WebPageInfo.TAG.MSGTYPE, webPageInfo.getMsgType());
		values.put(WebPageInfo.TAG.PAGEURL, webPageInfo.getPageUrl());
		values.put(WebPageInfo.TAG.SENDTIME, webPageInfo.getSendTime());
		values.put(WebPageInfo.TAG.SUBJECT, webPageInfo.getSubject());
		db.insert(DBInfo.Table.WEBPAGE_TB_NAME, null, values);
		db.close();
	}

	public List<WebPageInfo> findAll() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<WebPageInfo> webPageInfos = new ArrayList<WebPageInfo>();
		Cursor cursor = db.query(DBInfo.Table.WEBPAGE_TB_NAME, queryColumns,
				null, null, null, null, null);

		if (cursor != null) {
			if (cursor.getCount() > 0) {
				if (cursor.moveToLast()) {
					do {
						WebPageInfo webPageInfo = new WebPageInfo();
						webPageInfo.setContent(cursor.getString(cursor
								.getColumnIndex(WebPageInfo.TAG.CONTENT)));
						webPageInfo.setDeadline(cursor.getString(cursor
								.getColumnIndex(WebPageInfo.TAG.DEADLINE)));
						webPageInfo.setId(cursor.getString(cursor
								.getColumnIndex(WebPageInfo.TAG.ID)));
						webPageInfo.setMsgType(cursor.getString(cursor
								.getColumnIndex(WebPageInfo.TAG.MSGTYPE)));
						webPageInfo.setPageUrl(cursor.getString(cursor
								.getColumnIndex(WebPageInfo.TAG.PAGEURL)));
						webPageInfo.setSendTime(cursor.getString(cursor
								.getColumnIndex(WebPageInfo.TAG.SENDTIME)));
						webPageInfo.setSubject(cursor.getString(cursor
								.getColumnIndex(WebPageInfo.TAG.SUBJECT)));
						webPageInfos.add(webPageInfo);
					} while (cursor.moveToPrevious());
				}
			}
		}
		cursor.close();
		db.close();
		return webPageInfos;
	}

	public int delete(String id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int i = db.delete(DBInfo.Table.WEBPAGE_TB_NAME, WebPageInfo.TAG.ID
				+ " = ? ", new String[] { id });
		db.close();
		return i;
	}

}
