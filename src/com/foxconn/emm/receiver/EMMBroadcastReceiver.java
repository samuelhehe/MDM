package com.foxconn.emm.receiver;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.foxconn.emm.service.EMMMonitorService;
import com.foxconn.emm.service.EMMService;
import com.foxconn.emm.tools.PolicyControl;
import com.foxconn.emm.utils.L;

public class EMMBroadcastReceiver extends BroadcastReceiver {

	public static final String BOOT_COMPLETED_ACTION = "com.foxconn.emm.action.BOOT_COMPLETED";
	public static final String SYSTEM_SHUTDOWN_ACTION = "com.foxconn.emm.action.SYSTETMSHUTDOWN";
	public static ArrayList<EventHandler> mListeners = new ArrayList<EventHandler>();

	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		L.i("action = " + action);

		if (TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION)) {
			if (mListeners.size() > 0)// 通知接口完成加载
				for (EventHandler handler : mListeners) {
					handler.onNetChange();
				}
			new PolicyControl(context).enableCurrentPolicy();
			
		} else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
			L.d("System shutdown, stopping service.");
			Intent emmServiceIntent = new Intent(context, EMMService.class);
//			intent.setAction(SYSTEM_SHUTDOWN_ACTION);
			context.stopService(emmServiceIntent);
			//// 需要进行最后一笔流量信息的记录当前关机的结余
			
			Intent emmMonitorServiceIntent = new Intent(context, EMMMonitorService.class);
//			intent.setAction(SYSTEM_SHUTDOWN_ACTION);
			context.stopService(emmMonitorServiceIntent);
			//// 需要进行最后一笔流量信息的记录当前关机的结余
			

		} else {
				/// 开机启动EMMService 
				Intent i = new Intent(context, EMMService.class);
				i.setAction(BOOT_COMPLETED_ACTION);
				context.startService(i);
		}
	}

	public static abstract interface EventHandler {

		public abstract void onNetChange();
	}
}
