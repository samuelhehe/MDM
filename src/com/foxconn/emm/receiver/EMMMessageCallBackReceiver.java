package com.foxconn.emm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.foxconn.app.IMessageReceiveCallback;
import com.foxconn.emm.utils.L;

public class EMMMessageCallBackReceiver extends BroadcastReceiver {

	public static String EMM_INFOCENTER_ACTION = "com.foxconn.emm.InfoCenterACTION";

	public static String EMM_MAM_ACTION = "com.foxconn.emm.MAMACTION";
	
	public static String EMM_SAFEPOLICY_ACTION = "com.foxconn.emm.safepolicyACTION";
	

	public static String EMM_MSG_TYPE = "messagetype";
	
	private IMessageReceiveCallback iMessageReceiveCallback;

	public EMMMessageCallBackReceiver(IMessageReceiveCallback iMessageReceiveCallback) {
		this.iMessageReceiveCallback = iMessageReceiveCallback;

	}
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		L.d("action = " + action);
		if (iMessageReceiveCallback != null) {
			iMessageReceiveCallback.onMessageReceived(action);
		}
	}
}
