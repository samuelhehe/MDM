package com.foxconn.app.aty;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.foxconn.app.App;
import com.foxconn.app.R;
import com.foxconn.emm.bean.LimitListInfo;
import com.foxconn.emm.service.EMMMonitorService;
import com.foxconn.emm.service.IMonitorService;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.utils.TextFormater;

/**
 * 
 * 该Activity 主要用于提醒用户关于限制名单的一些管控提醒信息.
 * 
 */
public class AtyLimitApps extends AbActivity implements
		android.view.View.OnClickListener {

	private ImageView iv_app_lock_pwd_icon;
	private TextView tv_app_lock_pwd_name;

	private TextView et_app_lock_pwd;

	private String packname;

	private ActivityManager manager;
	protected Button forbidden_confirm_btn;

	protected LimitListInfo limitListInfo;
	protected EditText emm_forbidden_limitcontent_type_pwd_et;

	private LimitListConnection limitListConnection;
	private IMonitorService iMonitorService;
	private int errorCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forbidden_app_activity);
		manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		limitListInfo = (LimitListInfo) getIntent().getSerializableExtra(
				"limitlistinfo");
		if (limitListInfo == null) {
			L.e(this.getClass(), "limitListInfo is null error !");
			this.finish();
		}
		packname = getIntent().getStringExtra("packname");
		initView();
		App.getInstance().addActivity(this);
		limitListConnection = new LimitListConnection();
		Intent intent = new Intent(this, EMMMonitorService.class);
		bindService(intent, limitListConnection, BIND_AUTO_CREATE);
	}

	private void initView() {
		iv_app_lock_pwd_icon = (ImageView) findViewById(R.id.iv_app_lock_icon);
		tv_app_lock_pwd_name = (TextView) findViewById(R.id.tv_app_lock_pwd_name);
		et_app_lock_pwd = (TextView) findViewById(R.id.et_app_lock_pwd);
		forbidden_confirm_btn = (Button) this
				.findViewById(R.id.forbidden_confirm_btn);
		forbidden_confirm_btn.setOnClickListener(this);
		if (!TextUtils.isEmpty(limitListInfo.getLimitType())) {
			View emmLimitContent = this
					.findViewById(R.id.emm_forbidden_limitcontent_rl);
			TextView limitType_tv = (TextView) emmLimitContent
					.findViewById(R.id.emm_forbidden_limitcontent_type_tv);
			TextView emm_forbidden_limitcontent_type_pwd_time_tv = (TextView) emmLimitContent
					.findViewById(R.id.emm_forbidden_limitcontent_type_pwd_time_tv);
			emm_forbidden_limitcontent_type_pwd_et = (EditText) emmLimitContent
					.findViewById(R.id.emm_forbidden_limitcontent_type_pwd_et);
			TextView emm_forbidden_limitcontent_type_time_tv = (TextView) emmLimitContent
					.findViewById(R.id.emm_forbidden_limitcontent_type_time_tv);
			String temp = "禁用";
			// /0: 黑名单, 1:白名单 , 2:限制名单
			if (limitListInfo.getLimitType().equalsIgnoreCase("2")) {
				temp = "限制名单";
				emmLimitContent.setVisibility(View.VISIBLE);
			} else {
				emmLimitContent.setVisibility(View.GONE);
			}
			String temps = getResources().getString(
					R.string.forbidden_warning_hint);
			et_app_lock_pwd.setText(String.format(temps, temp));
			// / 1：按照時間限制， 0：按照密碼進行限制
			if (!TextUtils.isEmpty(limitListInfo.getLimitLimitType())) {

				if (limitListInfo.getLimitLimitType().equalsIgnoreCase("0")) {
					limitType_tv.setText("限制类别: 按照密码进行限制");
					emm_forbidden_limitcontent_type_pwd_et
							.setVisibility(View.VISIBLE);
					emm_forbidden_limitcontent_type_time_tv
							.setVisibility(View.INVISIBLE);

				} else if (limitListInfo.getLimitLimitType().equalsIgnoreCase(
						"1")) {
					limitType_tv.setText("限制类别: 按照时间进行限制");
					emm_forbidden_limitcontent_type_pwd_et
							.setVisibility(View.INVISIBLE);
					emm_forbidden_limitcontent_type_pwd_time_tv
							.setText("开始时间: "
									+ limitListInfo.getLimitEnableTime());
					emm_forbidden_limitcontent_type_time_tv.setText("结束时间: "
							+ limitListInfo.getLimitDisableTime());
				}
			}
		}
		try {
			ApplicationInfo appinfo = getPackageManager().getPackageInfo(
					packname, 0).applicationInfo;
			Drawable appicon = appinfo.loadIcon(getPackageManager());
			String appname = appinfo.loadLabel(getPackageManager()).toString();
			iv_app_lock_pwd_icon.setImageDrawable(appicon);
			tv_app_lock_pwd_name.setText(appname);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private class LimitListConnection implements ServiceConnection {

		public void onServiceConnected(ComponentName name, IBinder service) {
			iMonitorService = (IMonitorService) service;
			L.d(this.getClass(), " onServiceConnected...");
		}

		public void onServiceDisconnected(ComponentName name) {
			iMonitorService = null;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void onHomeOrLock() {
		// // 如果用户按home键 则Process kill
			manager.killBackgroundProcesses(packname);
			Intent backhome = new Intent("android.intent.action.MAIN");
			backhome.addCategory("android.intent.category.HOME");
			backhome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(backhome);
			App.getInstance().exit();
	}

	public void noconfirm(View view) {

		// // 如果限制类型不知道,为安全起见直接Kill
		if (!TextUtils.isEmpty(limitListInfo.getLimitType())) {

			// / 限制名单.
			if (limitListInfo.getLimitType().equalsIgnoreCase("2")) {

				// / 按照密码限制.
				if (limitListInfo.getLimitLimitType().equalsIgnoreCase("0")) {

					if (!TextUtils.isEmpty(limitListInfo.getLimitPwdTime())) {
						String inputedpwd = emm_forbidden_limitcontent_type_pwd_et
								.getText().toString().trim();
						if (TextUtils.isEmpty(inputedpwd)) {
							emm_forbidden_limitcontent_type_pwd_et
									.setError("限制密码不能为空");
						}
						if (inputedpwd.equals(limitListInfo.getLimitPwdTime())) {
							// / 关闭当前Activity ,进入用户操作的应用.
							L.d(this.getClass(),
									"inputPwd " + inputedpwd + " limitListPwd "
											+ limitListInfo.getLimitPwdTime());
							iMonitorService.addTempStopLimit(limitListInfo);
							launchApp(this, limitListInfo.getPackName());
							this.finish();
							return;

						} else {
							// / 制作一个计数器, 输入错误超过3次,则进行kill process.
							errorCount++;
							L.d(this.getClass(), "errorCount " + errorCount);
							this.showToast("限制密码输入错误");
							if (errorCount > 3) {
								this.showToast("限制密码输入错误已经3次,系统自动退出.");
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								killSpecificProcessByPackageName();
							}
						}
					} else {
						// //如果限制密码为空,则不限制.
						// / 不做处理
						L.d(this.getClass(), "限制密码为空, 则不限制.");
						this.finish();
					}
					// // 按照时间限制. 如果当前时间段在设定的时间内, 则关闭当前Activity,
					// 允许用户进入用户操作的应用界面.否则,则直接kill process.
				} else if (limitListInfo.getLimitLimitType().equalsIgnoreCase(
						"1")) {
					if (TextUtils.isEmpty(limitListInfo.getLimitDisableTime())
							|| (System.currentTimeMillis() < TextFormater
									.getTimeToLong(limitListInfo
											.getLimitDisableTime()))) {
						// // 在这里可以添加一些时间的判定逻辑
						iMonitorService.addTempStopLimit(limitListInfo);
						launchApp(this, limitListInfo.getPackName());
						this.finish();
					} else {
						this.showToast("当前时间不在该应用限制使用的时间范围内");
						// / kill 掉指定的packagename 的process
						killSpecificProcessByPackageName();
					}
				}

				// //能够进入这个Activity时, 有限制名单,黑名单
			} else {
				// / kill 黑名单. packagename
				// / kill 掉指定的packagename 的process
				killSpecificProcessByPackageName();
			}

		} else {
			// / kill 掉指定的packagename 的process
			killSpecificProcessByPackageName();
		}

	}

	private HomeWatcherReceiver mHomeKeyReceiver = null;

	private void registerHomeKeyReceiver(Context context) {
		L.i("register", "registerHomeKeyReceiver");
		mHomeKeyReceiver = new HomeWatcherReceiver();
		final IntentFilter homeFilter = new IntentFilter(
				Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		context.registerReceiver(mHomeKeyReceiver, homeFilter);
	}

	private void unregisterHomeKeyReceiver(Context context) {
		Log.i("register", "unregisterHomeKeyReceiver");
		if (null != mHomeKeyReceiver) {
			context.unregisterReceiver(mHomeKeyReceiver);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerHomeKeyReceiver(this);
	}

	@Override
	protected void onPause() {
		L.d(getClass(), "AtyLimitApps on pause ");
		unregisterHomeKeyReceiver(this);
		super.onPause();
	}

	private void killSpecificProcessByPackageName() {
		if (!TextUtils.isEmpty(packname)) {
			L.d(this.getClass(), "Kill process: " + packname);
			manager.killBackgroundProcesses(packname);
			Intent backhome = new Intent("android.intent.action.MAIN");
			backhome.addCategory("android.intent.category.HOME");
			backhome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(backhome);
			this.finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(limitListConnection);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.forbidden_confirm_btn:
			noconfirm(v);
			break;
		}
	}

	public void launchApp(Context context, String packageName) {
		Intent mIntent = context.getPackageManager().getLaunchIntentForPackage(
				packageName);
		if (mIntent != null) {
			try {
				context.startActivity(mIntent);
			} catch (ActivityNotFoundException err) {
				this.showToast("系统检测到该应用出错,请稍后重试,或重装该应用");
			}
		}
	}

	class HomeWatcherReceiver extends BroadcastReceiver {
		private static final String LOG_TAG = "HomeReceiver";
		private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
		private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
		private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
		private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
		private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			L.i("HomeWatcherReceiver", "onReceive: action: " + action);
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				// android.intent.action.CLOSE_SYSTEM_DIALOGS
				String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
				L.i(LOG_TAG, "reason: " + reason);

				if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
					// 短按Home键
					L.i(LOG_TAG, "homekey");
					onHomeOrLock();

				} else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
					// 长按Home键 或者 activity切换键
					L.i(LOG_TAG, "long press home key or activity switch");
					onHomeOrLock();

				} else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {
					// 锁屏
					L.i(LOG_TAG, "lock");
					onHomeOrLock();
				} else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {
					// samsung 长按Home键
					L.i(LOG_TAG, "assist");
				}

			}
		}

	}
}
