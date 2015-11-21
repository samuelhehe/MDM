package com.foxconn.emm.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class AppDBControlHelper extends DBHelper {

	public AppDBControlHelper(Context context) {
		super(context);
	}

	public SQLiteDatabase getWriteableDataBase() {
		return this.getWritableDatabase();
	}

	public SQLiteDatabase getReadableDatabase() {
		return this.getReadableDatabase();
	}

	public void dropAllDataTable() {
		if (dropDataTableSqlList != null && dropDataTableSqlList.size() >= 1)
			for (String dropTableSql : dropDataTableSqlList) {
				getWriteableDataBase().execSQL(dropTableSql);
			}
		onCreate(getWriteableDataBase());
	}
	
	public void dropAllDataForCleanInfo(){
		if (dropDataTableSqlList != null && dropDataTableSqlList.size() >= 1)
			for (String dropTableSql : dropDataTableSqlList) {
				if(dropTableSql.contains(DBInfo.Table.USER_INFO_TB_NAME)){
					continue;
				}
				getWriteableDataBase().execSQL(dropTableSql);
			}
		onCreate(getWriteableDataBase());
	}

}
