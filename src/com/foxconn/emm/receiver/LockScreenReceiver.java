package com.foxconn.emm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.foxconn.emm.utils.TaskUtil;

public class LockScreenReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences sp = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);

		if (sp != null) {
			boolean killprocess = sp.getBoolean("killprocess", false);

			if (killprocess) {
				TaskUtil.killAllProcess(context);
			}
		}

	}

}
