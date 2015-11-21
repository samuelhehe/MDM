package com.foxconn.emm.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

public class ProcessUtil {
	public static void killAllProcess(Context context) {
		// 拿到这个包管理器
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 拿到所有正在运行的进程信息
		List<RunningAppProcessInfo> list = activityManager
				.getRunningAppProcesses();
		// 进行遍历，然后杀死它们
		for (RunningAppProcessInfo runningAppProcessInfo : list) {
			activityManager
					.killBackgroundProcesses(runningAppProcessInfo.processName);
		}
	}

	public static String getProcessCount(Context context) {
		// 拿到这个包管理器
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 拿到所有正在运行的进程信息
		List<RunningAppProcessInfo> list = activityManager
				.getRunningAppProcesses();
		return list.size() + "";
	}

	public static String getAvailMemory(Context context) {
		// 拿到这个包管理器
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// new一个内存的对象
		MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		// 拿到现在系统里面的内存信息
		activityManager.getMemoryInfo(memoryInfo);
		return TextFormater.dataSizeFormat(memoryInfo.availMem);
	}

}
