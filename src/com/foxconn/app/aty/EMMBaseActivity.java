package com.foxconn.app.aty;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import com.ab.activity.AbActivity;
import com.foxconn.emm.lock.UnlockGesturePasswordActivity;
import com.foxconn.emm.service.EMMService;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.utils.L;

public class EMMBaseActivity extends AbActivity {

	public static ArrayList<BackPressHandler> mListeners = new ArrayList<BackPressHandler>();

	@Override
	protected void onResume() {
		super.onResume();
		if (mListeners.size() > 0)
			for (BackPressHandler handler : mListeners) {
				handler.activityOnResume();
			}
		if (EMMPreferences.getSecurityNeedAuthorized(getApplication())) {
			if (EMMPreferences.getSecurityStatus(getApplication())) {
				startActivity(new Intent(this,UnlockGesturePasswordActivity.class).setFlags(AtySettings.UNlOCK_OFFEN));
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mListeners.size() > 0)
			for (BackPressHandler handler : mListeners) {
				handler.activityOnPause();
			}
		resetIsCurrentProcessPause();
	}

	protected void resetIsCurrentProcessPause() {
		/**
		 * 如果是当前进程,则不需要进行验证.否则需要进行安全验证.
		 */
		if (isCurrentProcessPause()) {
			EMMPreferences.setSecurityNeedAuthorized(getApplicationContext(),false);
		} else {
			EMMPreferences.setSecurityNeedAuthorized(getApplicationContext(),true);
		}
	}

	/**
	 * 判断当前执行的进程是否是EMM,是返回true
	 * 
	 * @return
	 */
	private boolean isCurrentProcessPause() {
		if (activitymgr != null) {
			List<RunningTaskInfo> taskinfos = activitymgr.getRunningTasks(1);
			if (taskinfos != null && taskinfos.size() > 0) {
				RunningTaskInfo currenttask = taskinfos.get(0);
				String packname = currenttask.baseActivity.getPackageName();
				return TextUtils.equals(this.getPackageName(), packname);
			}
		}
		return false;
	}

	public static abstract interface BackPressHandler {

		public abstract void activityOnResume();

		public abstract void activityOnPause();

	}

	protected EMMService mEMMService;
	private ActivityManager activitymgr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activitymgr = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		bindEMMService();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindEMMService();
//		resetIsCurrentProcessPause();
	}

	ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mEMMService = ((EMMService.EMMBinder) service).getService();
			// mEMMService.registerConnectionStatusCallback(EMMBaseActivity.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mEMMService.unRegisterConnectionStatusCallback();
			mEMMService = null;
		}
	};

	private void bindEMMService() {
		L.i(AtyEnroll.class, "[SERVICE] Unbind");
		Intent mServiceIntent = new Intent(this, EMMService.class);
		// / 启动检查更新.同步Policy
		mServiceIntent.setAction(EMMContants.EMMAction.EMM_CHECK_APP_UPDATE);
		bindService(mServiceIntent, mServiceConnection,
				Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
	}

	private void unbindEMMService() {
		try {
			unbindService(mServiceConnection);
			L.i(AtyEnroll.class, "[SERVICE] Unbind");
		} catch (IllegalArgumentException e) {
			L.e(AtyEnroll.class, "Service wasn't bound!");
		}
	}

}
