package com.foxconn.emm.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * 获取当前已安装的版本
 * 
 * @author samuel
 * 
 */
public class VersionUtil {
	public static int getVersionCode(Context context)
			throws NameNotFoundException {
		// 获取 PackageManager 实例
		PackageManager packageManager = context.getPackageManager();
		// 获得 context 所属类的包名, 0 表示获取版本信息
		PackageInfo packageInfo;
		packageInfo = packageManager
				.getPackageInfo(context.getPackageName(), 0);
		return packageInfo.versionCode;

	}

	public static PackageInfo getVersionInfo(Context context ) throws NameNotFoundException{
		// 获取 PackageManager 实例
		PackageManager packageManager = context.getPackageManager();
		// 获得 context 所属类的包名, 0 表示获取版本信息
		PackageInfo packageInfo;
		packageInfo = packageManager
				.getPackageInfo(context.getPackageName(), 0);
		return packageInfo;
	}
	
	public static String getVersionName(Context context)
			throws NameNotFoundException {
		// 获取 PackageManager 实例
		PackageManager packageManager = context.getPackageManager();
		// 获得 context 所属类的包名, 0 表示获取版本信息
		PackageInfo packageInfo;
		packageInfo = packageManager
				.getPackageInfo(context.getPackageName(), 0);
		return packageInfo.versionName;

	}
}