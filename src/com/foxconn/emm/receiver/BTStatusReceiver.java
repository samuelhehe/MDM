package com.foxconn.emm.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.foxconn.emm.bean.PolicyInfo;
import com.foxconn.emm.tools.PolicyControl;
import com.foxconn.emm.utils.EMMPreferences;

public class BTStatusReceiver extends BroadcastReceiver {
private PolicyControl policyControl;
	
	public void onReceive(final Context context, final Intent intent) {
		policyControl  = new PolicyControl(context);
		// get new wifi state
		final int bluetoothState = intent.getIntExtra(
				BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_ON);
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter != null) {
			if (bluetoothState == BluetoothAdapter.STATE_ON) {
				PolicyInfo policyInfo = new PolicyInfo()
						.getPolicyInfoFromJson(EMMPreferences
								.getPolicyData(context));
				policyControl.enablePolicy(policyInfo);
			}

		}
	}
}
