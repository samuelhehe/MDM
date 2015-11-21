package com.foxconn.emm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.foxconn.emm.bean.PolicyInfo;
import com.foxconn.emm.tools.PolicyControl;
import com.foxconn.emm.utils.EMMPreferences;

public class WifiStateReceiver extends BroadcastReceiver {
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			PolicyControl policyControl = new PolicyControl(context);
			// get new wifi state
			final int wifiState = intent.getIntExtra(
					WifiManager.EXTRA_WIFI_STATE,
					WifiManager.WIFI_STATE_ENABLING);
			final WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			if (wifiManager != null) {
				// if enabling, check if thats okay
				if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
					PolicyInfo policyInfo = new PolicyInfo()
							.getPolicyInfoFromJson(EMMPreferences
									.getPolicyData(context));
					policyControl.enablePolicy(policyInfo);
				}
			}
		}

	}
}