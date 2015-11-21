package com.foxconn.emm.receiver;

import android.app.ActivityManager;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.foxconn.app.R;
import com.foxconn.emm.bean.PolicyInfo;
import com.foxconn.emm.utils.ApplicationDetailInfo;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.utils.L;

/**
 * 
 * 设备管理 Receiver The callback methods, like the base
 * BroadcastReceiver.onReceive() method, happen on the main thread of the
 * process. Thus long running operations must be done on another thread. Note
 * that because a receiver is done once returning from its receive function,
 * such long-running operations should probably be done in a Service.
 */
public class DeviceAdminSampleReceiver extends DeviceAdminReceiver {

	private static final String TAG = "DeviceAdminSampleReceiver";
	public static int ERR_TIME = 0;
	public static int ERR_TIME_TOTAL = 0;

	void showToast(Context context, String msg) {
		String status = context.getString(R.string.admin_receiver_status, msg);
		Toast.makeText(context, status, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onEnabled(Context context, Intent intent) {
		// showToast(context,
		// context.getString(R.string.admin_receiver_status_enabled));
		showToast(context, "EMM已与设备管理器绑定!");
	}

	@Override
	public synchronized CharSequence onDisableRequested(Context context, Intent intent) {
		PolicyInfo policyInfo  = new PolicyInfo().getPolicyInfoFromJson(EMMPreferences.getPolicyData(context));
		if(policyInfo!=null)
		if(policyInfo.getUnload_Emm().getItemFlag().contains("0")){
			ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			killSpecificProcessByPackageName(manager,"com.android.settings",context);
			/// 启动EMM
			new ApplicationDetailInfo(context).launchApp(context.getPackageName());
			try {
				Thread.sleep(6*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return context.getString(R.string.admin_receiver_status_disable_warning);
	}

	@Override
	public void onDisabled(Context context, Intent intent) {
		showToast(context,
				context.getString(R.string.admin_receiver_status_disabled));
	}
	private void killSpecificProcessByPackageName(ActivityManager manager,String packname,Context context) {
		if (!TextUtils.isEmpty(packname)) {
			L.d(this.getClass(), "Kill process: " + packname);
			manager.killBackgroundProcesses(packname);
			Intent backhome = new Intent("android.intent.action.MAIN");
			backhome.addCategory("android.intent.category.HOME");
			backhome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(backhome);
		}
	}
	@Override
	public void onPasswordChanged(Context context, Intent intent) {
		showToast(context,
				context.getString(R.string.admin_receiver_status_pw_changed));
	}

	@Override
	public void onPasswordFailed(Context context, Intent intent) {
		ERR_TIME_TOTAL += 1;
		if (ERR_TIME_TOTAL >= ERR_TIME) {
			showToast(context, "密码输入错误" + ERR_TIME_TOTAL + "次,清除系统资料!");
			System.out.println("密码错误已达:" + ERR_TIME_TOTAL);
		} else {
			showToast(context, "密码输入错误" + ERR_TIME_TOTAL + "次，连续错误" + ERR_TIME
					+ "次将清除系统资料!");
		}
		// showToast(context,
		// context.getString(R.string.admin_receiver_status_pw_failed));
	}

	@Override
	public void onPasswordSucceeded(Context context, Intent intent) {
		// showToast(context,
		// context.getString(R.string.admin_receiver_status_pw_succeeded));
		showToast(context, "密码输入成功!");
		ERR_TIME_TOTAL = 0;
	}

	@Override
	public void onPasswordExpiring(Context context, Intent intent) {
		DevicePolicyManager dpm = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		long expr = dpm.getPasswordExpiration(new ComponentName(context,
				DeviceAdminSampleReceiver.class));
		long delta = expr - System.currentTimeMillis();
		boolean expired = delta < 0L;
		String message = context
				.getString(expired ? R.string.expiration_status_past
						: R.string.expiration_status_future);
		showToast(context, message);
		Log.v(TAG, message);
	}
}