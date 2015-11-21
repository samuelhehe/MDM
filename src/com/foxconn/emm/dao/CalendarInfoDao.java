package com.foxconn.emm.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.foxconn.emm.bean.CalendarInfo;
import com.foxconn.emm.db.DBHelper;
import com.foxconn.emm.db.DBInfo;

public class CalendarInfoDao {
	private Context context;
	private DBHelper dbHelper;

	private String[] queryColumns = { CalendarInfo.TAG.ID,
			CalendarInfo.TAG.CONTENT, CalendarInfo.TAG.ENDTIME,
			CalendarInfo.TAG.ISALLDAY, CalendarInfo.TAG.MSGTYPE,
			CalendarInfo.TAG.PLACE, CalendarInfo.TAG.SENDTIME,
			CalendarInfo.TAG.STARTTIME, CalendarInfo.TAG.SUBJECT };

	public CalendarInfoDao(Context context) {
		this.context = context;
		dbHelper = new DBHelper(context);
	}

	public long add(CalendarInfo calendarInfo) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(CalendarInfo.TAG.MSGTYPE, calendarInfo.getMsgType());
		values.put(CalendarInfo.TAG.CONTENT, calendarInfo.getContent());
		values.put(CalendarInfo.TAG.ENDTIME, calendarInfo.getEndTime());
		values.put(CalendarInfo.TAG.ISALLDAY, calendarInfo.getIsAllDay());
		values.put(CalendarInfo.TAG.PLACE, calendarInfo.getPlace());
		values.put(CalendarInfo.TAG.SENDTIME, calendarInfo.getSendTime());
		values.put(CalendarInfo.TAG.STARTTIME, calendarInfo.getStartTime());
		values.put(CalendarInfo.TAG.SUBJECT, calendarInfo.getSubject());
		long l = db.insert(DBInfo.Table.CALENDAR_TB_NAME, null, values);
		db.close();
		return l;
	}

	public List<CalendarInfo> findAll() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<CalendarInfo> calendarInfos = new ArrayList<CalendarInfo>();
		Cursor cursor = db.query(DBInfo.Table.CALENDAR_TB_NAME, queryColumns,
				null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				if (cursor.moveToLast()) {
					do {
						CalendarInfo calendarInfo = new CalendarInfo();
						calendarInfo.setId(cursor.getString(cursor
								.getColumnIndex(CalendarInfo.TAG.ID)));
						calendarInfo.setContent(cursor.getString(cursor
								.getColumnIndex(CalendarInfo.TAG.CONTENT)));
						calendarInfo.setMsgType(cursor.getString(cursor
								.getColumnIndex(CalendarInfo.TAG.MSGTYPE)));
						calendarInfo.setEndTime(cursor.getString(cursor
								.getColumnIndex(CalendarInfo.TAG.ENDTIME)));
						calendarInfo.setIsAllDay(cursor.getString(cursor
								.getColumnIndex(CalendarInfo.TAG.ISALLDAY)));
						calendarInfo.setPlace(cursor.getString(cursor
								.getColumnIndex(CalendarInfo.TAG.PLACE)));
						calendarInfo.setSendTime(cursor.getString(cursor
								.getColumnIndex(CalendarInfo.TAG.SENDTIME)));
						calendarInfo.setStartTime(cursor.getString(cursor
								.getColumnIndex(CalendarInfo.TAG.STARTTIME)));
						calendarInfo.setSubject(cursor.getString(cursor
								.getColumnIndex(CalendarInfo.TAG.SUBJECT)));
						calendarInfos.add(calendarInfo);
					} while (cursor.moveToPrevious());
				}
			}
		}
		cursor.close();
		db.close();
		return calendarInfos;
	}

	public int delete(String id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int i = db.delete(DBInfo.Table.CALENDAR_TB_NAME, CalendarInfo.TAG.ID
				+ " =? ", new String[] { id });
		db.close();
		return i;
	}
}
