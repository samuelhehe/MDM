package com.foxconn.emm.tools;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.foxconn.emm.bean.InstalledAppInfo;
import com.foxconn.emm.bean.InstalledAppInputParams;

/**
 * 
 * PakageInfo Service
 * 
 * @author samuel
 * @1.0.1
 * @date 2014/2/25
 */
public class PakageInfoService {

	/**
	 * 获取系统中所有已安装应用,排除系统应用和包括系统升级后的第三方应用
	 * @param context
	 * @return
	 */
	public static List<InstalledAppInfo> getAppInfos(Context context) {
		List<InstalledAppInfo> appInfos;
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> pakageinfos = pm
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		appInfos = new ArrayList<InstalledAppInfo>();
		for (PackageInfo packageInfo : pakageinfos) {
			if (!filterApp(packageInfo.applicationInfo)) {
				continue;
			}
			InstalledAppInfo appInfo = new InstalledAppInfo();
			appInfo.setIsUserApp(filterApp(packageInfo.applicationInfo));
			appInfo.setAppName(packageInfo.applicationInfo.loadLabel(pm).toString());
			appInfo.setVersionCode(packageInfo.versionCode);
			appInfo.setVersionName(TextUtils.isEmpty(packageInfo.versionName)?"1.0.1":packageInfo.versionName);
//			appInfo.setDrawable(packageInfo.applicationInfo.loadIcon(pm));
			appInfo.setPackageName(packageInfo.packageName);
			appInfos.add(appInfo);
			appInfo = null;
		}
		return appInfos;
	}
	
//	public void queryPacakgeSize(Context context, String pkgName) throws Exception {
//		if (pkgName != null) {
//			// 使用放射机制得到PackageManager类的隐藏函数getPackageSizeInfo
//			PackageManager pm = context.getPackageManager(); // 得到pm对象
//			try {
//				// 通过反射机制获得该隐藏函数
//				Method getPackageSizeInfo = pm.getClass().getDeclaredMethod(
//						"getPackageSizeInfo", String.class,
//						IPackageStatsObserver.class);
//				// 调用该函数，并且给其分配参数 ，待调用流程完成后会回调PkgSizeObserver类的函数
//				getPackageSizeInfo.invoke(pm, pkgName, new PkgSizeObserver());
//			} catch (Exception ex) {
//				L.e(this.getClass(), "NoSuchMethodException");
//				ex.printStackTrace();
//				throw ex; // 抛出异常
//			}
//		}
//	}
	/**
	 * 
	 * 
	 * @return
	 */
	public static List<InstalledAppInputParams> getInstalledAppInputParams(
			Context context) {
		List<InstalledAppInfo> installedAppInfos = getAppInfos(context);
		List<InstalledAppInputParams> inputParams = new ArrayList<InstalledAppInputParams>();
		for (InstalledAppInfo appInfo : installedAppInfos) {
			InstalledAppInputParams inputParam = new InstalledAppInputParams(
					appInfo.getPackageName(), appInfo.getVersionCode());
			inputParam.setAppName(appInfo.getAppName());
			inputParam.setIsRunning("N");
			inputParam.setPackageName(appInfo.getPackageName());
			inputParam.setVersionName(appInfo.getVersionName());
			inputParam.setVersionCode(appInfo.getVersionCode());
			inputParams.add(inputParam);
		}
		return inputParams;
	}

	/**
	 * @param inputInstalledAppInputParams
	 * @return
	 */
	public static String getInstalledAppInputParamsJsonStr(
			List<InstalledAppInputParams> inputInstalledAppInputParams) {
		JSONArray jsonArrays = new JSONArray();
		try {
			for (InstalledAppInputParams installedAppInputParam : inputInstalledAppInputParams) {
				JSONObject obj = new JSONObject();
				obj.put(InstalledAppInputParams.TAG.software_name,
						installedAppInputParam.getAppName());
				obj.put(InstalledAppInputParams.TAG.software_version,
						installedAppInputParam.getVersionName());
				obj.put(InstalledAppInputParams.TAG.software_size, "0");
				obj.put(InstalledAppInputParams.TAG.software_running, "N");
				obj.put(InstalledAppInputParams.TAG.packagename,
						installedAppInputParam.getPackageName());
				jsonArrays.put(obj);
			}
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}

		System.out.println("jsonArrays: " + jsonArrays.toString());

		return jsonArrays.toString();
	}

	/**
	 * 三方应用程序的过滤器
	 * 
	 * @param info
	 * @return true 三方应用 false 系统应用
	 */
	public static boolean filterApp(ApplicationInfo info) {
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			// 代表的是系统的应用,但是被用户升级了. 用户应用
			return true;
		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
			// 代表的用户的应用
			return true;
		}
		return false;
	}

}