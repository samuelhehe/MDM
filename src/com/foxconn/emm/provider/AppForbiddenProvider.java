package com.foxconn.emm.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.foxconn.emm.bean.LimitListInfo;
import com.foxconn.emm.dao.LimitListDao;

public class AppForbiddenProvider extends ContentProvider {
	private static final int INSERT = 10;
	private static final int DELETE = 11;
	private static Uri chageurl = Uri
			.parse("content://com.foxconn.emm.unlockprovider");
	private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
	private LimitListDao limitListDao;
	static {
		matcher.addURI("com.foxconn.emm.unlockprovider", "insert", INSERT);
		matcher.addURI("com.foxconn.emm.unlockprovider", "delete", DELETE);
	}

	@Override
	public boolean onCreate() {
		limitListDao = new LimitListDao(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return null;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int resule = matcher.match(uri);
		if (resule == INSERT) {
			LimitListInfo limitListInfo = new LimitListInfo();
			String packname = (String) values.get(LimitListInfo.TAG.PACKNAME);
			limitListInfo.setPackName(packname);
			String desc = (String) values.get(LimitListInfo.TAG.DESC);
			limitListInfo.setDesc(desc);
			String limitdisabletime = (String) values
					.get(LimitListInfo.TAG.LIMITDISABLETIME);
			limitListInfo.setLimitDisableTime(limitdisabletime);
			String limitEnableTime = (String) values
					.get(LimitListInfo.TAG.LIMITENABLETIME);
			limitListInfo.setLimitEnableTime(limitEnableTime);
			String limitLimitType = (String) values
					.get(LimitListInfo.TAG.LIMITLIMITTYPE);
			limitListInfo.setLimitLimitType(limitLimitType);
			String limitPwdTime = (String) values
					.get(LimitListInfo.TAG.LIMITPWDTIME);
			limitListInfo.setLimitPwdTime(limitPwdTime);
			String limitType = (String) values.get(LimitListInfo.TAG.LIMITTYPE);
			limitListInfo.setLimitType(limitType);
			limitListDao.add(limitListInfo);
			getContext().getContentResolver().notifyChange(chageurl, null);
		} else {
			throw new IllegalArgumentException("uri exception  ");
		}
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int result = matcher.match(uri);
		if (result == DELETE) {
			String packname = selectionArgs[0];
			String limitType = selectionArgs[1];
			limitListDao.delete(packname, limitType);
			getContext().getContentResolver().notifyChange(chageurl, null);
		} else {
			throw new IllegalArgumentException("uri exception  ");
		}
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

}
