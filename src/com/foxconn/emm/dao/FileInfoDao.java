package com.foxconn.emm.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.foxconn.emm.bean.FileInfo;
import com.foxconn.emm.bean.MsgInfo;
import com.foxconn.emm.db.DBHelper;
import com.foxconn.emm.db.DBInfo;
import com.foxconn.emm.utils.EMMContants;

public class FileInfoDao {
	private Context context;
	private DBHelper dbHelper;

	private String[] queryColumns = { FileInfo.TAG.ID, FileInfo.TAG.CONTENT,
			FileInfo.TAG.DEADLINE, FileInfo.TAG.URL, FileInfo.TAG.MSGTYPE,
			FileInfo.TAG.PASSWORD, FileInfo.TAG.SENDTIME, FileInfo.TAG.SUBJECT,
			FileInfo.TAG.CONTACT };

	public FileInfoDao(Context context) {
		this.context = context;
		dbHelper = new DBHelper(context);
	}

	public void add(FileInfo fileInfo) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(FileInfo.TAG.CONTACT, fileInfo.getContact());
		values.put(FileInfo.TAG.CONTENT, fileInfo.getContent());
		values.put(FileInfo.TAG.DEADLINE, fileInfo.getDeadline());
		values.put(FileInfo.TAG.URL, fileInfo.getFileUrl());
		values.put(FileInfo.TAG.MSGTYPE, fileInfo.getMsgType());
		values.put(FileInfo.TAG.PASSWORD, fileInfo.getPassword());
		values.put(FileInfo.TAG.SENDTIME, fileInfo.getSendTime());
		values.put(FileInfo.TAG.SUBJECT, fileInfo.getSubject());
		db.insert(DBInfo.Table.FILEURL_IMG_TB_NAME, null, values);
		db.close();
	}

	public List<FileInfo> findAll() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<FileInfo> fileInfos = new ArrayList<FileInfo>();
		Cursor cursor = db.query(DBInfo.Table.FILEURL_IMG_TB_NAME,
				queryColumns, null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				if (cursor.moveToLast()) {
					do {
						FileInfo fileInfo = new FileInfo();
						String msgType = cursor.getString(cursor
								.getColumnIndex(FileInfo.TAG.MSGTYPE));
						if (msgType.equals(EMMContants.MsgType.SENDFILE)) {
							fileInfo.setId(cursor.getString(cursor
									.getColumnIndex(FileInfo.TAG.ID)));
							fileInfo.setContent(cursor.getString(cursor
									.getColumnIndex(FileInfo.TAG.CONTENT)));
							fileInfo.setDeadline(cursor.getString(cursor
									.getColumnIndex(FileInfo.TAG.DEADLINE)));
							fileInfo.setFileUrl(cursor.getString(cursor
									.getColumnIndex(FileInfo.TAG.URL)));
							fileInfo.setMsgType(cursor.getString(cursor
									.getColumnIndex(FileInfo.TAG.MSGTYPE)));
							fileInfo.setPassword(cursor.getString(cursor
									.getColumnIndex(FileInfo.TAG.PASSWORD)));
							fileInfo.setSendTime(cursor.getString(cursor
									.getColumnIndex(FileInfo.TAG.SENDTIME)));
							fileInfo.setSubject(cursor.getString(cursor
									.getColumnIndex(FileInfo.TAG.SUBJECT)));
							fileInfo.setContact(cursor.getString(cursor
									.getColumnIndex(FileInfo.TAG.CONTACT)));
							fileInfos.add(fileInfo);
						}
					} while (cursor.moveToPrevious());
				}
			}
		}
		cursor.close();
		db.close();
		return fileInfos;
	}

	public List<FileInfo> findFilesByKey(String key) {
		List<FileInfo> fileInfos = new ArrayList<FileInfo>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(DBInfo.Table.FILEURL_IMG_TB_NAME,
				queryColumns, FileInfo.TAG.SUBJECT + " like ? ",
				new String[] { "%" + key + "%" }, null, null, null);
		if (null != cursor) {
			if (cursor.getCount() > 0) {
				if (cursor.moveToLast()) {
					do {
						FileInfo fileInfo = new FileInfo();
						String msgType = cursor.getString(cursor
								.getColumnIndex(FileInfo.TAG.MSGTYPE));
						if (msgType.equals(EMMContants.MsgType.SENDFILE)) {
							fileInfo.setId(cursor.getString(cursor
									.getColumnIndex(FileInfo.TAG.ID)));
							fileInfo.setContent(cursor.getString(cursor
									.getColumnIndex(FileInfo.TAG.CONTENT)));
							fileInfo.setDeadline(cursor.getString(cursor
									.getColumnIndex(FileInfo.TAG.DEADLINE)));
							fileInfo.setFileUrl(cursor.getString(cursor
									.getColumnIndex(FileInfo.TAG.URL)));
							fileInfo.setMsgType(cursor.getString(cursor
									.getColumnIndex(FileInfo.TAG.MSGTYPE)));
							fileInfo.setPassword(cursor.getString(cursor
									.getColumnIndex(FileInfo.TAG.PASSWORD)));
							fileInfo.setSendTime(cursor.getString(cursor
									.getColumnIndex(FileInfo.TAG.SENDTIME)));
							fileInfo.setSubject(cursor.getString(cursor
									.getColumnIndex(FileInfo.TAG.SUBJECT)));
							fileInfo.setContact(cursor.getString(cursor
									.getColumnIndex(FileInfo.TAG.CONTACT)));
							fileInfos.add(fileInfo);
						}
					} while (cursor.moveToPrevious());
				}
			}
			cursor.close();
			db.close();
		}
		return fileInfos;
	}

	public int delete(String id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int i = db.delete(DBInfo.Table.FILEURL_IMG_TB_NAME, FileInfo.TAG.ID
				+ " =? ", new String[] { id });
		db.close();
		return i;
	}
}
