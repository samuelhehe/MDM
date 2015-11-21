package com.foxconn.emm.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.foxconn.emm.bean.MsgInfo;
import com.foxconn.emm.db.DBHelper;

public class DBMsgHelper {

	private DBHelper dbHelper = null;
	private SQLiteDatabase db = null;
	private ContentValues values = null;

	public static final String SEND_GCM = "sendgcm";
	public static final String SENDFILEURL = "sendfileurl";
	public static final String CALENDAR = "sendcalendar";
	public static final String SEND_POLICY = "sendpolicy";
	public static final String SYNCPOLICY = "syncpolicy";

	/**
	 * filename, code, receive_date
	 */

	private String[] querycolumns = { MsgInfo.ID, MsgInfo.COMMAND,
			MsgInfo.FNAME_TITLE, MsgInfo.CODE_PLACE, MsgInfo.RECEIVE_DATE,
			MsgInfo.MSG_CONTENT, MsgInfo.ISALLDAY, MsgInfo.DATE_START,
			MsgInfo.DATE_END, MsgInfo.FOLDER_NAME, MsgInfo.DESPW,
			MsgInfo.DESCONTACT };

	public DBMsgHelper(Context context) {
		dbHelper = new DBHelper(context);
	}

	public long insertMsgInfo(MsgInfo msgInfo) {

		db = dbHelper.getWritableDatabase();
		values = new ContentValues();
		if (msgInfo.getCommand().equals(SEND_GCM)) {
			values.put(MsgInfo.COMMAND, msgInfo.getCommand());
			values.put(MsgInfo.MSG_CONTENT, msgInfo.getMsg_content());
			values.put(MsgInfo.RECEIVE_DATE, msgInfo.getReceive_date());
		} else if (msgInfo.getCommand().equals(SENDFILEURL)) {
			values.put(MsgInfo.COMMAND, msgInfo.getCommand());
			values.put(MsgInfo.FNAME_TITLE, msgInfo.getFname_title());
			values.put(MsgInfo.CODE_PLACE, msgInfo.getCode_place());
			values.put(MsgInfo.RECEIVE_DATE, msgInfo.getReceive_date());
			values.put(MsgInfo.MSG_CONTENT, msgInfo.getMsg_content());
			values.put(MsgInfo.FOLDER_NAME, msgInfo.getFolder_name());
			values.put(MsgInfo.DESPW, msgInfo.getDespw());
			values.put(MsgInfo.DESCONTACT, msgInfo.getDescontact());
		} else if (msgInfo.getCommand().equals(CALENDAR)) {
			values.put(MsgInfo.COMMAND, msgInfo.getCommand());
			values.put(MsgInfo.FNAME_TITLE, msgInfo.getFname_title());
			values.put(MsgInfo.MSG_CONTENT, msgInfo.getMsg_content());
			values.put(MsgInfo.ISALLDAY, msgInfo.getIsallday());
			values.put(MsgInfo.DATE_START, msgInfo.getDate_start());
			values.put(MsgInfo.DATE_END, msgInfo.getDate_end());
			values.put(MsgInfo.CODE_PLACE, msgInfo.getCode_place());
			values.put(MsgInfo.RECEIVE_DATE, msgInfo.getReceive_date());
		} else if (msgInfo.getCommand().equals(SEND_POLICY)
				|| msgInfo.getCommand().equals(SYNCPOLICY)) {
			values.put(MsgInfo.COMMAND, msgInfo.getCommand());
			values.put(MsgInfo.MSG_CONTENT, msgInfo.getMsg_content());
			values.put(MsgInfo.RECEIVE_DATE, msgInfo.getReceive_date());
		}

		long rowId = db.insert(MsgInfo.TB_NAME, null, values);
		db.close();

		return rowId;
	}

	public List<MsgInfo> showMsg(int start, int end) {
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();
		int flag = 0;

		db = this.dbHelper.getReadableDatabase();
		Cursor cursor = db.query(MsgInfo.TB_NAME, querycolumns, null, null,
				null, null, null);
		if (null != cursor) {
			if (cursor.getCount() > 0) {
				if (cursor.moveToLast()) {
					do {
						MsgInfo msgInfo = new MsgInfo();
						String command = cursor.getString(cursor
								.getColumnIndex(MsgInfo.COMMAND));
						if (!command.equals(SENDFILEURL)) {
							flag++;
							if (flag >= start) {
								msgInfo.setId(cursor.getString(cursor
										.getColumnIndex(MsgInfo.ID)));
								msgInfo.setCommand(cursor.getString(cursor
										.getColumnIndex(MsgInfo.COMMAND)));
								msgInfo.setFname_title(cursor.getString(cursor
										.getColumnIndex(MsgInfo.FNAME_TITLE)));
								msgInfo.setCode_place(cursor.getString(cursor
										.getColumnIndex(MsgInfo.CODE_PLACE)));
								msgInfo.setReceive_date(cursor.getString(cursor
										.getColumnIndex(MsgInfo.RECEIVE_DATE)));
								msgInfo.setMsg_content(cursor.getString(cursor
										.getColumnIndex(MsgInfo.MSG_CONTENT)));
								msgInfo.setIsallday(cursor.getString(cursor
										.getColumnIndex(MsgInfo.ISALLDAY)));
								msgInfo.setDate_start(cursor.getString(cursor
										.getColumnIndex(MsgInfo.DATE_START)));
								msgInfo.setDate_end(cursor.getString(cursor
										.getColumnIndex(MsgInfo.DATE_END)));
								msgInfo.setFolder_name(cursor.getString(cursor
										.getColumnIndex(MsgInfo.FOLDER_NAME)));
								msgInfo.setDespw(cursor.getString(cursor
										.getColumnIndex(MsgInfo.DESPW)));
								msgInfo.setDescontact(cursor.getString(cursor
										.getColumnIndex(MsgInfo.DESCONTACT)));
								msgInfos.add(msgInfo);
							}
						}
					} while (cursor.moveToPrevious() && flag < end);
				}
			}
			cursor.close();
			db.close();
		}
		System.out.println("msgInfos" + msgInfos);
		return msgInfos;
	}

	public List<MsgInfo> showFile() {
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();
		db = this.dbHelper.getReadableDatabase();
		Cursor cursor = db.query(MsgInfo.TB_NAME, querycolumns, MsgInfo.COMMAND
				+ "=?", new String[] { SENDFILEURL }, null, null, null);
		if (null != cursor) {
			if (cursor.getCount() > 0) {
				if (cursor.moveToLast()) {
					do {
						MsgInfo msgInfo = new MsgInfo();
						msgInfo.setId(cursor.getString(cursor
								.getColumnIndex(MsgInfo.ID)));
						msgInfo.setCommand(cursor.getString(cursor
								.getColumnIndex(MsgInfo.COMMAND)));
						msgInfo.setFname_title(cursor.getString(cursor
								.getColumnIndex(MsgInfo.FNAME_TITLE)));
						msgInfo.setCode_place(cursor.getString(cursor
								.getColumnIndex(MsgInfo.CODE_PLACE)));
						msgInfo.setReceive_date(cursor.getString(cursor
								.getColumnIndex(MsgInfo.RECEIVE_DATE)));
						msgInfo.setMsg_content(cursor.getString(cursor
								.getColumnIndex(MsgInfo.MSG_CONTENT)));
						msgInfo.setFolder_name(cursor.getString(cursor
								.getColumnIndex(MsgInfo.FOLDER_NAME)));
						msgInfo.setDespw(cursor.getString(cursor
								.getColumnIndex(MsgInfo.DESPW)));
						msgInfo.setDescontact(cursor.getString(cursor
								.getColumnIndex(MsgInfo.DESCONTACT)));
						msgInfos.add(msgInfo);
					} while (cursor.moveToPrevious());
				}
			}
			cursor.close();
			db.close();
		}
		System.out.println("fileInfos" + msgInfos);
		return msgInfos;
	}

	public List<MsgInfo> findFile(String key) {
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();
		db = this.dbHelper.getReadableDatabase();
		Cursor cursor = db.query(MsgInfo.TB_NAME, querycolumns,
				MsgInfo.FNAME_TITLE + " like ? ", new String[] { "%" + key
						+ "%" }, null, null, null);
		if (null != cursor) {
			if (cursor.getCount() > 0) {
				if (cursor.moveToLast()) {
					do {
						MsgInfo msgInfo = new MsgInfo();
						msgInfo.setId(cursor.getString(cursor
								.getColumnIndex(MsgInfo.ID)));
						msgInfo.setCommand(cursor.getString(cursor
								.getColumnIndex(MsgInfo.COMMAND)));
						msgInfo.setFname_title(cursor.getString(cursor
								.getColumnIndex(MsgInfo.FNAME_TITLE)));
						msgInfo.setCode_place(cursor.getString(cursor
								.getColumnIndex(MsgInfo.CODE_PLACE)));
						msgInfo.setReceive_date(cursor.getString(cursor
								.getColumnIndex(MsgInfo.RECEIVE_DATE)));
						msgInfo.setMsg_content(cursor.getString(cursor
								.getColumnIndex(MsgInfo.MSG_CONTENT)));
						msgInfo.setFolder_name(cursor.getString(cursor
								.getColumnIndex(MsgInfo.FOLDER_NAME)));
						msgInfo.setDespw(cursor.getString(cursor
								.getColumnIndex(MsgInfo.DESPW)));
						msgInfo.setDescontact(cursor.getString(cursor
								.getColumnIndex(MsgInfo.DESCONTACT)));
						msgInfos.add(msgInfo);
					} while (cursor.moveToPrevious());
				}
			}
			cursor.close();
			db.close();
		}
		return msgInfos;
	}

	public int delMsg(String id) {
		int count = 0;
		db = this.dbHelper.getWritableDatabase();
		count = db.delete(MsgInfo.TB_NAME, MsgInfo.ID + " = " + id, null);
		db.close();

		return count;
	}
}
