package com.foxconn.emm.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.foxconn.emm.bean.UserInfo;

public class OffLineDatabase extends SQLiteAssetHelper {

	private static final String DATABASE_NAME = "mdmoffline.db";
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_TABLE = "userinfo";
	SQLiteDatabase db;

	
	/**
	 * 根据userid 进行查询的columns 
	 */
	private String[] querycolumns = { UserInfo.TAG.USER_ID, UserInfo.TAG.BU_GROUP,
			UserInfo.TAG.USER_NAME_CHI, UserInfo.TAG.PHONE_NO };

	public OffLineDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		db = getReadableDatabase();
	}


	/**
	 * 查询数据
	 * @param userId
	 * @return
	 */
	public UserInfo findUserByUserId(String userId) {
		UserInfo userInfo = null;
		Cursor cursor = db.query(DATABASE_TABLE, querycolumns,
				UserInfo.TAG.USER_ID + "=?", new String[] { userId }, null, null,null);
		if(cursor != null){
			if(cursor.getCount() > 0){
				cursor.moveToFirst();
				userInfo = new UserInfo();
				userInfo.setUser_id(userId);
				userInfo.setUser_name_chi(cursor.getString(cursor.getColumnIndex(UserInfo.TAG.USER_NAME_CHI)));
				userInfo.setBu_group(cursor.getString(cursor.getColumnIndex(UserInfo.TAG.BU_GROUP)));
				userInfo.setPhone_no(cursor.getString(cursor.getColumnIndex(UserInfo.TAG.PHONE_NO)));
			}
		}
		cursor.close();
		db.close();
		return userInfo;
	}
}
