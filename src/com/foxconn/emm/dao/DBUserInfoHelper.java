package com.foxconn.emm.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.foxconn.emm.bean.UserInfo;
import com.foxconn.emm.db.DBHelper;
import com.foxconn.emm.utils.L;

public class DBUserInfoHelper {

	public static final int FHEAD = 0;
	public static final int FDEVICE = 1;
	private DBHelper dbHelper = null;
	private SQLiteDatabase db = null;
	private ContentValues values = null;

	private String[] querycolumns = { UserInfo.TAG.USER_ID,
			UserInfo.TAG.USER_NAME_CHI, UserInfo.TAG.USER_NAME_ENG,
			UserInfo.TAG.BU_GROUP, UserInfo.TAG.HEAD_ICON,
			UserInfo.TAG.MODIFYBY, UserInfo.TAG.CREATEDATE,
			UserInfo.TAG.CREATEBY, UserInfo.TAG.ISDISABLE,
			UserInfo.TAG.USERMAIL, UserInfo.TAG.MODIFYDATE,UserInfo.TAG.TELEXTENSION,
			UserInfo.TAG.PHONE_NO };

	public DBUserInfoHelper(Context context) {
		dbHelper = new DBHelper(context);
	}

	/**
	 * @param user
	 * @return
	 */
	public long insertUserInfo(UserInfo user) {

		db = dbHelper.getWritableDatabase();
		values = new ContentValues();
		values.put(UserInfo.TAG.USER_ID, user.getUser_id());
		values.put(UserInfo.TAG.USER_NAME_CHI, user.getUser_name_chi());
		values.put(UserInfo.TAG.USER_NAME_ENG, user.getUser_name_eng());
		values.put(UserInfo.TAG.ISDISABLE, user.isDisable() ? "1" : "0");
		values.put(UserInfo.TAG.USERMAIL, user.getUserMail());
		values.put(UserInfo.TAG.MODIFYDATE, user.getModifyDate());
		values.put(UserInfo.TAG.MODIFYBY, user.getModifyBy());
		values.put(UserInfo.TAG.CREATEDATE, user.getCreateDate());
		values.put(UserInfo.TAG.CREATEBY, user.getCreateBy());
		values.put(UserInfo.TAG.BU_GROUP, user.getBu_group());
		values.put(UserInfo.TAG.PHONE_NO, user.getPhone_no());
		values.put(UserInfo.TAG.HEAD_ICON, user.getUserImg());
		long rowId = db.replace(UserInfo.TAG.TB_NAME, null, values);
		db.close();
		L.d(this.getClass(), user.toString());
		return rowId;
	}

	/**
	 * 
	 * @param userId
	 * @return
	 */
	public UserInfo findUserByUserId(String userId) {
		UserInfo userInfo = null;
		db = this.dbHelper.getReadableDatabase();
		Cursor cursor = db.query(UserInfo.TAG.TB_NAME, querycolumns,
				UserInfo.TAG.USER_ID + " = ? ", new String[] { userId }, null,
				null, null);
		if (null != cursor) {
			if (cursor.getCount() > 0) {
				if (cursor.moveToFirst()) {
					userInfo = new UserInfo();
					String uId = cursor.getString(cursor
							.getColumnIndex(UserInfo.TAG.USER_ID));
					String uname = cursor.getString(cursor
							.getColumnIndex(UserInfo.TAG.USER_NAME_CHI));
					userInfo.setBu_group(cursor.getString(cursor
							.getColumnIndex(UserInfo.TAG.BU_GROUP)));
					userInfo.setPhone_no(cursor.getString(cursor
							.getColumnIndex(UserInfo.TAG.PHONE_NO)));
					String dbhdfilePath = cursor.getString(cursor
							.getColumnIndex(UserInfo.TAG.HEAD_ICON));
					userInfo.setUserImg(dbhdfilePath);
					String isDisable = cursor.getString(cursor
							.getColumnIndex(UserInfo.TAG.ISDISABLE));
					if (!TextUtils.isEmpty(isDisable)) {
						userInfo.setDisable(isDisable == "1" ? true : false);
					}
					userInfo.setUser_id(uId);
					userInfo.setUser_name_chi(uname);
					userInfo.setTelExtension(cursor.getString(cursor
							.getColumnIndex(UserInfo.TAG.TELEXTENSION)));
					userInfo.setCreateBy(cursor.getString(cursor
							.getColumnIndex(UserInfo.TAG.CREATEBY)));
					userInfo.setCreateDate(cursor.getString(cursor
							.getColumnIndex(UserInfo.TAG.CREATEDATE)));
					userInfo.setModifyBy(cursor.getString(cursor
							.getColumnIndex(UserInfo.TAG.MODIFYBY)));
					userInfo.setModifyDate(cursor.getString(cursor
							.getColumnIndex(UserInfo.TAG.MODIFYDATE)));
					userInfo.setUserMail(cursor.getString(cursor
							.getColumnIndex(UserInfo.TAG.USERMAIL)));
				}
			}
			cursor.close();
			db.close();
		}
		return userInfo;
	}

	/**
	 * del user
	 * 
	 * @param userId
	 * @return
	 */
	public int delUserByUserId(String userId) {
		db = this.dbHelper.getReadableDatabase();
		int count = db.delete(UserInfo.TAG.TB_NAME, UserInfo.TAG.USER_ID
				+ " = ? ", new String[] { userId });
		return count;
	}

}
