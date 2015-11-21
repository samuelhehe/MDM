package com.foxconn.emm.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.foxconn.emm.bean.PicInfo;
import com.foxconn.emm.db.DBHelper;
import com.foxconn.emm.db.DBInfo;
import com.foxconn.emm.utils.EMMContants;

public class PicInfoDao {
	private Context context;
	private DBHelper dbHelper;

	private String[] queryColumns = { PicInfo.TAG.ID, PicInfo.TAG.CONTENT,
			PicInfo.TAG.CONTACT, PicInfo.TAG.DEADLINE, PicInfo.TAG.URL,
			PicInfo.TAG.MSGTYPE, PicInfo.TAG.PASSWORD, PicInfo.TAG.SENDTIME,
			PicInfo.TAG.SUBJECT };

	public PicInfoDao(Context context) {
		this.context = context;
		dbHelper = new DBHelper(context);
	}

	public void add(PicInfo picInfo) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(PicInfo.TAG.CONTENT, picInfo.getContent());
		values.put(PicInfo.TAG.CONTACT, picInfo.getContact());
		values.put(PicInfo.TAG.DEADLINE, picInfo.getDeadline());
		values.put(PicInfo.TAG.URL, picInfo.getImg_list());
		values.put(PicInfo.TAG.MSGTYPE, picInfo.getMsgType());
		values.put(PicInfo.TAG.PASSWORD, picInfo.getPassword());
		values.put(PicInfo.TAG.SENDTIME, picInfo.getSendTime());
		values.put(PicInfo.TAG.SUBJECT, picInfo.getSubject());
		db.insert(DBInfo.Table.FILEURL_IMG_TB_NAME, null, values);
		db.close();
	}

	public List<PicInfo> findAll() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<PicInfo> picInfos = new ArrayList<PicInfo>();
		Cursor cursor = db.query(DBInfo.Table.FILEURL_IMG_TB_NAME,
				queryColumns, null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				if (cursor.moveToLast()) {
					do {
						PicInfo picInfo = new PicInfo();
						String msgType = cursor.getString(cursor
								.getColumnIndex(PicInfo.TAG.MSGTYPE));
						if (msgType.equals(EMMContants.MsgType.SENDIMAGE)) {
							picInfo.setId(cursor.getString(cursor
									.getColumnIndex(PicInfo.TAG.ID)));
							picInfo.setContent(cursor.getString(cursor
									.getColumnIndex(PicInfo.TAG.CONTENT)));
							picInfo.setContact(cursor.getString(cursor
									.getColumnIndex(PicInfo.TAG.CONTACT)));
							picInfo.setDeadline(cursor.getString(cursor
									.getColumnIndex(PicInfo.TAG.DEADLINE)));
							picInfo.setImg_list(cursor.getString(cursor
									.getColumnIndex(PicInfo.TAG.URL)));
							picInfo.setMsgType(cursor.getString(cursor
									.getColumnIndex(PicInfo.TAG.MSGTYPE)));
							picInfo.setPassword(cursor.getString(cursor
									.getColumnIndex(PicInfo.TAG.PASSWORD)));
							picInfo.setSendTime(cursor.getString(cursor
									.getColumnIndex(PicInfo.TAG.SENDTIME)));
							picInfo.setSubject(cursor.getString(cursor
									.getColumnIndex(PicInfo.TAG.SUBJECT)));
							picInfos.add(picInfo);
						}
					} while (cursor.moveToPrevious());
				}
			}
		}
		cursor.close();
		db.close();
		return picInfos;
	}

	public int delete(String id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int i = db.delete(DBInfo.Table.FILEURL_IMG_TB_NAME, PicInfo.TAG.ID
				+ " =? ", new String[] { id });
		db.close();
		return i;
	}
}
