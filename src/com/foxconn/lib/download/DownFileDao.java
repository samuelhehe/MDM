/*
 * Copyright (C) 2012 www.amsoft.cn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foxconn.lib.download;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.foxconn.emm.dao.AbBasicDBDao;
import com.foxconn.emm.db.DBHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class DownFileDao.
 */
public class DownFileDao extends AbBasicDBDao {

	/** The open helper. */
	private DBHelper openHelper;

	/** The m context. */
	public static Context mContext = null;
	// 单例
	/** The m down file dao. */
	public static DownFileDao mDownFileDao = null;

	/**
	 * Instantiates a new down file dao.
	 * 
	 * @param context
	 *            the context
	 */
	public DownFileDao(Context context) {
		openHelper = new DBHelper(context);
	}

	/**
	 * Gets the single instance of DownFileDao.
	 * 
	 * @param context
	 *            the context
	 * @return single instance of DownFileDao
	 */
	public static DownFileDao getInstance(Context context) {
		mContext = context;
		if (mDownFileDao == null) {
			mDownFileDao = new DownFileDao(context);
		}
		return mDownFileDao;
	}

	/**
	 * 获取已经下载的文件的信息.
	 * 
	 * @param path
	 *            the path
	 * @return the down file
	 */
	public DownFile getDownFile(String path) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		DownFile mDownFile = null;
		try {
			db = openHelper.getReadableDatabase();
			String where = "DOWNURL = ?";
			String[] whereValue = { path };
			cursor = db.query("FILEDOWN", null, where, whereValue, null, null,
					null);
			if (cursor.moveToFirst()) {
				mDownFile = new DownFile();
				mDownFile.set_ID(getIntColumnValue("_ID", cursor));
				mDownFile.setName(getStringColumnValue("NAME", cursor));
				mDownFile.setDescription(getStringColumnValue("DESCRIPTION",
						cursor));
				mDownFile.setPakageName(getStringColumnValue("PAKAGENAME",
						cursor));
				mDownFile.setDownUrl(getStringColumnValue("DOWNURL", cursor));
				mDownFile.setDownPath(getStringColumnValue("DOWNPATH", cursor));
				mDownFile.setState(getIntColumnValue("STATE", cursor));
				mDownFile
						.setDownLength(getIntColumnValue("DOWNLENGTH", cursor));
				mDownFile.setTotalLength(getIntColumnValue("TOTALLENGTH",
						cursor));
				mDownFile.setSuffix(getStringColumnValue("DOWNSUFFIX", cursor));

			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			closeDatabase(cursor, db);
		}
		return mDownFile;
	}

	
	/**
	 * get all down files  in downFile db ;
	 * 
	 * 
	 * @return  a list of down files in dbtable  ,return type : List<DownFile>  
	 */
	public List<DownFile> getAllDownFiles() {

		SQLiteDatabase db = null;
		Cursor cursor = null;
		List<DownFile> mDownFiles = null;
		try {
			db = openHelper.getReadableDatabase();
			cursor = db.query("FILEDOWN", null, null, null, null, null, null);
			if (cursor != null) {
				mDownFiles = new ArrayList<DownFile>();
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					DownFile mDownFile = new DownFile();
					mDownFile.set_ID(getIntColumnValue("_ID", cursor));
					mDownFile.setName(getStringColumnValue("NAME", cursor));
					mDownFile.setDescription(getStringColumnValue("DESCRIPTION", cursor));
					mDownFile.setPakageName(getStringColumnValue("PAKAGENAME",cursor));
					mDownFile.setDownUrl(getStringColumnValue("DOWNURL", cursor));
					mDownFile.setDownPath(getStringColumnValue("DOWNPATH",cursor));
					mDownFile.setState(getIntColumnValue("STATE", cursor));
					mDownFile.setDownLength(getIntColumnValue("DOWNLENGTH",cursor));
					mDownFile.setTotalLength(getIntColumnValue("TOTALLENGTH",cursor));
					mDownFile.setSuffix(getStringColumnValue("DOWNSUFFIX",cursor));
					mDownFiles .add(mDownFile);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			closeDatabase(cursor, db);
		}
		return mDownFiles;
	}

	/**
	 * 保存线程已经下载的文件信息.
	 * 
	 * @param mDownFile
	 *            the m down file
	 * @return the long
	 */
	public synchronized long save(DownFile mDownFile) {
		
		if(find(mDownFile)){
			return -1;
		}
		SQLiteDatabase db = null;
		long row = 0;
		try {
			db = openHelper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put("NAME", mDownFile.getName());
			cv.put("DESCRIPTION", mDownFile.getDescription());
			cv.put("PAKAGENAME", mDownFile.getPakageName());
			cv.put("DOWNURL", mDownFile.getDownUrl());
			cv.put("DOWNPATH", mDownFile.getDownPath());
			cv.put("STATE", mDownFile.getState());
			cv.put("DOWNLENGTH", mDownFile.getDownLength());
			cv.put("TOTALLENGTH", mDownFile.getTotalLength());
			cv.put("DOWNSUFFIX", mDownFile.getSuffix());
			row = db.insert("FILEDOWN", null, cv);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDatabase(null, db);
		}
		return row;
	}

	/**
	 * 查询是否存在某一条下载数据
	 * @param mDownFile
	 * @return
	 */
	public synchronized boolean find(DownFile mDownFile){
		List<DownFile> allDownFiles = getAllDownFiles();
		for (DownFile downFile : allDownFiles) {
			if(TextUtils.isEmpty(downFile.getDownUrl())){
				continue;
			}
			if(downFile.getDownUrl().equalsIgnoreCase(mDownFile.getDownUrl())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 实时更新线程已经下载的文件长度.
	 * 
	 * @param mDownFile
	 *            the m down file
	 * @return the long
	 */
	public synchronized long update(DownFile mDownFile) {
		long row = -1;
		SQLiteDatabase db = null;
		try {
			db = openHelper.getWritableDatabase();
			String where = "DOWNURL = ? ";
			String[] whereValue = { mDownFile.getDownUrl() };
			ContentValues cv = new ContentValues();
			cv.put("STATE", mDownFile.getState());
			cv.put("DOWNLENGTH", mDownFile.getDownLength());
			cv.put("TOTALLENGTH", mDownFile.getTotalLength());
			cv.put("DOWNPATH", mDownFile.getDownPath());
			row = db.update("FILEDOWN", cv, where, whereValue);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDatabase(null, db);
		}
		return row;
	}

	/**
	 * 删除对应的下载记录.
	 * 
	 * @param path
	 *            the path
	 * @return the long
	 */
	public synchronized long delete(String path) {
		long row = -1;
		SQLiteDatabase db = null;
		try {
			db = openHelper.getWritableDatabase();
			String where = "DOWNURL = ? ";
			String[] whereValue = { path };
			row = db.delete("FILEDOWN", where, whereValue);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDatabase(null, db);
		}
		return row;
	}

}
