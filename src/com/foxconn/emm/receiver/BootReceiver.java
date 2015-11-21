package com.foxconn.emm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.foxconn.emm.service.EMMMonitorService;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 监听情景切换
		if (intent.getAction().equals("android.media.RINGER_MODE_CHANGED")) {
			Toast.makeText(context, "情景模式已切换", Toast.LENGTH_LONG).show();
		}
		// 设置开机启动
		Intent intent2 = new Intent(context, EMMMonitorService.class);
		intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startService(intent2);
	}
}
 