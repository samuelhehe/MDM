package com.foxconn.app.aty;

import java.util.Date;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;

import com.foxconn.app.App;
import com.foxconn.app.R;
import com.foxconn.emm.bean.LimitListInfo;
import com.foxconn.emm.bean.SysLicenseResult;
import com.foxconn.emm.bean.UserInfo;
import com.foxconn.emm.dao.DBUserInfoHelper;
import com.foxconn.emm.dao.LimitListDao;
import com.foxconn.emm.receiver.DeviceAdminSampleReceiver;
import com.foxconn.emm.service.ServiceManager;
import com.foxconn.emm.utils.ChangeLog;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.utils.EMMReqParamsUtils;
import com.foxconn.emm.utils.HttpClientUtil;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.utils.NetUtil;
import com.foxconn.emm.utils.NetUtil.NetWorkState;
import com.foxconn.emm.utils.TextFormater;
import com.foxconn.emm.utils.ToastUtils;
import com.foxconn.emm.view.SysLicenseDialog2;
import com.foxconn.lib.welcome.license.AtyFirstLoadWelcomePage;
import com.google.gson.Gson;

public class AtyWelcome extends Activity {

	public static final int REQUEST_CODE_ENABLE_ADMIN = 1;
	public static final String FLAG_FROM_ATY = "FLAG_FROM_ATY";
	public static final String FLAG_FROM_ATY_VALUE = "AtyWelcome";

	public Handler mHandler = new Handler();

	private ComponentName emmmDeviceAdmin;
	private DevicePolicyManager devicePolicyManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * 判断License 是否可用
		 */
		if (EMMPreferences.getIsLicenseEnable(this)) {
			if (!EMMPreferences.getIsLicensePass(this)) {
				if (new ChangeLog(getApplicationContext()).firstRun()) {
					startActivity(new Intent(this,
							AtyFirstLoadWelcomePage.class));
					this.finish();
				}
			}
		}
		setContentView(R.layout.aty_welcome);
		initProcess();
		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		emmmDeviceAdmin = new ComponentName(this,
				DeviceAdminSampleReceiver.class);
		App.getInstance().addActivity(this);
		// /// 测试时使用该数据, 正式发布版本需注释.
	}

	public void validateturn() {
		String isEnrolled = EMMPreferences
				.getDeviceEnrolled(getApplicationContext());
		if (isEnrolled
				.equalsIgnoreCase(EMMContants.EMMPrefContant.ENROLL_STATUS_Y)
				|| isEnrolled
						.equalsIgnoreCase(EMMContants.EMMPrefContant.ENROLL_STATUS_P)) {
			mHandler.postDelayed(entersys,1000);
		} else if (isEnrolled
				.equalsIgnoreCase(EMMContants.EMMPrefContant.ENROLL_STATUS_N)) {
			mHandler.postDelayed(enrollsys,1000);
		}
	}

	class ValidateLicenseTask extends AsyncTask<String, Void, SysLicenseResult> {

		private String enterpriseName;
		private String licenseNo;
		private CountDownTimer countDownTimer;

		public ValidateLicenseTask(String enterpriseName, String licenseNo) {
			this.enterpriseName = enterpriseName;
			this.licenseNo = licenseNo;
			countDownTimer = new CountDownTimer(5 * 1000, 1000) {
				public void onTick(long millisUntilFinished) {
				}

				public void onFinish() {
					L.d(this.getClass(), "count down finish ");
					ValidateLicenseTask.this.cancel(true);
					validateturn();
				}
			};
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			/**
			 * 在WiFi的情况下验证
			 */
			if (new NetUtil(AtyWelcome.this).getConnectState() != NetWorkState.WIFI) {
				L.d(this.getClass(), "current is not wifi net status ");
				///  offline validate 
				SysLicenseResult sysLicenseResult = new SysLicenseResult();
				sysLicenseResult.setFailureTime(EMMPreferences.getSysLicenseDeadLine(AtyWelcome.this));
				sysLicenseResult.setNowTime(EMMPreferences.getSysLicenseCurrentTime(AtyWelcome.this));
				if(canContinueUse(sysLicenseResult)){
					validateturn();
					ValidateLicenseTask.this.cancel(true);
				}else{
					L.d(getClass(), "offline validate fail enter dialog2..");
					sysLicenseResult.setCode(SysLicenseResult.tag_code_fail_expire);
					SysLicenseDialog2 sysLicenseDialog2 = new SysLicenseDialog2(AtyWelcome.this, sysLicenseResult);
					sysLicenseDialog2.show();
					ValidateLicenseTask.this.cancel(true);
				}
			}else{
				countDownTimer.start();
			}
		}

		protected SysLicenseResult doInBackground(String... params) {

			String url = EMMContants.SYSCONF.SYS_LICENSE_CHECKATCION;
			String reqparams = EMMReqParamsUtils.getSysLicenseUsualValidate(AtyWelcome.this);
			String returnData = HttpClientUtil.returnData(url, reqparams);
			L.d(this.getClass(), returnData);
			if (TextFormater.isEmpty(returnData)) {
				return null;
			}
			return new Gson().fromJson(returnData, SysLicenseResult.class);
		}

		@Override
		protected void onPostExecute(SysLicenseResult result) {
			super.onPostExecute(result);
			countDownTimer.cancel();
			if (result != null) {
				if (result.getSuccess().equalsIgnoreCase(
						SysLicenseResult.tag_success_success)) {
					save2SP(result);
					validateturn();
				} else if (result.getSuccess().equalsIgnoreCase(SysLicenseResult.tag_success_fail)) {
					L.d(this.getClass(), "validate fail enter dialog2..");
					SysLicenseDialog2 sysLicenseDialog2 = new SysLicenseDialog2(AtyWelcome.this, result);
					sysLicenseDialog2.show();
				}
			} else {
				// / 验证失败: 可能是网络原因.可能是其他原因 ,但是不能让用户进不去系统.所以还是可以进入系统的.
				validateturn();
			}
		}

		private void save2SP(SysLicenseResult result) {
			EMMPreferences.setSysLicenseEnterpriseName(AtyWelcome.this,enterpriseName);
			EMMPreferences.setSysLicenseNo(AtyWelcome.this, licenseNo);
			EMMPreferences.setSysLicenseDeadLine(AtyWelcome.this,result.getFailureTime());
			EMMPreferences.setSysLicenseCurrentTime(AtyWelcome.this, result.getNowTime());
			L.d(this.getClass(), "license revalidate  saved complete !");
		}
		/**
		 * 返回true 就不能够继续使用了
		 * 
		 * @param result
		 * @return
		 */
		private boolean canContinueUse(SysLicenseResult sysLicenseResult) {
			long deadline = TextFormater.getTimeToLong(sysLicenseResult.getFailureTime());
			long nowline = TextFormater.getTimeToLong(sysLicenseResult.getNowTime());
			long diff = Math.abs(deadline - nowline);
			int days = (int) (diff / (1000 * 60 * 60 * 24));
			if (days >= 15) {
				return true;
			}
			return false;
		}

	}

	private void fillLimitlistData() {
		LimitListDao limitListDao = new LimitListDao(this);

		LimitListInfo limitListInfo = new LimitListInfo();

		limitListInfo.setDesc("2014/10/8 11:55:30测试");
		limitListInfo.setLimitDisableTime("2014-12-09 08:49:17");
		limitListInfo.setLimitEnableTime("2014-11-28 08:49:17");
		limitListInfo.setLimitLimitType("0");// //按照密码限制.
		limitListInfo.setLimitType("2"); // / 限制名单
		limitListInfo.setLimitPwdTime("123456");
		limitListInfo.setMsgType("limitlist");
		limitListInfo.setPackName("com.tencent.mm");
		limitListInfo.setSendTime(new Date().getTime() + "");

		limitListDao.add(limitListInfo);

		LimitListInfo limitListInfo1 = new LimitListInfo();

		limitListInfo1.setDesc("2014/10/8 11:55:30测试");
		limitListInfo1.setLimitDisableTime("2014-11-01 08:49:17");
		limitListInfo1.setLimitEnableTime("2014-10-28 08:49:17");
		limitListInfo1.setLimitLimitType("1");// //按照时间限制
		limitListInfo1.setLimitType("2"); // / 限制名单
		limitListInfo1.setLimitPwdTime("24");
		limitListInfo1.setMsgType("limitlist");
		limitListInfo1.setPackName("com.tencent.news");
		limitListInfo1.setSendTime(new Date().getTime() + "");
		limitListDao.add(limitListInfo1);

		LimitListInfo limitListInfo2 = new LimitListInfo();

		limitListInfo2.setDesc("2014/10/8 11:55:30测试");
		limitListInfo2.setLimitDisableTime("2014-11-01 08:49:17");
		limitListInfo2.setLimitEnableTime("2014-10-28 08:49:17");
		limitListInfo2.setLimitLimitType("1");
		limitListInfo2.setLimitType("1"); // / 白名单.
		limitListInfo2.setLimitPwdTime("123456");
		limitListInfo2.setMsgType("limitlist");
		limitListInfo2.setPackName("com.tencent.mobileqq");
		limitListInfo2.setSendTime(new Date().getTime() + "");
		limitListDao.add(limitListInfo2);

		LimitListInfo limitListInfo3 = new LimitListInfo();

		limitListInfo3.setDesc("2014/10/8 11:55:30测试");
		limitListInfo3.setLimitDisableTime("2014-11-01 08:49:17");
		limitListInfo3.setLimitEnableTime("2014-10-28 08:49:17");
		limitListInfo3.setLimitLimitType("1");
		limitListInfo3.setLimitType("0"); // / 黑名单
		limitListInfo3.setLimitPwdTime("123456");
		limitListInfo3.setMsgType("limitlist");
		limitListInfo3.setPackName("com.kingsoft");
		limitListInfo3.setSendTime(new Date().getTime() + "");
		limitListDao.add(limitListInfo3);
	}

	private void initProcess() {
		createShotCut();
		ServiceManager serviceManager = new ServiceManager(this);
		// serviceManager.startEMMService();
		String isEnrolled = EMMPreferences
				.getDeviceEnrolled(getApplicationContext());
		if (isEnrolled
				.equalsIgnoreCase(EMMContants.EMMPrefContant.ENROLL_STATUS_Y)
				|| isEnrolled
						.equalsIgnoreCase(EMMContants.EMMPrefContant.ENROLL_STATUS_P)) {
			serviceManager.startEMMMonitorService();
			serviceManager.startNotificationService();
		}
	}

	/**
	 * 判断是否注册通过,分别导向不同的页面
	 * 
	 * 在这里进行跳转页面. 可以考虑添加License的认证后多次认证
	 * 
	 */
	public void turn() {

		/** 是否需要添加License模块 , 不管认证是否通过 都需要进行二次认证 */
		if (EMMPreferences.getIsLicenseEnable(this)) {
			String enterpriseName = EMMPreferences
					.getSysLicenseEnterpriseName(AtyWelcome.this);
			String licenseNo = EMMPreferences.getSysLicenseNo(AtyWelcome.this);
			L.d(this.getClass(), "License get start  enterpriseName : "
					+ enterpriseName + "licenseNo : " + licenseNo);
			ValidateLicenseTask validateLicenseTask = new ValidateLicenseTask(
					enterpriseName, licenseNo);
			validateLicenseTask.execute();

		} else {
			String isEnrolled = EMMPreferences
					.getDeviceEnrolled(getApplicationContext());
			if (isEnrolled
					.equalsIgnoreCase(EMMContants.EMMPrefContant.ENROLL_STATUS_Y)
					|| isEnrolled
							.equalsIgnoreCase(EMMContants.EMMPrefContant.ENROLL_STATUS_P)) {
				mHandler.post(entersys);
			} else if (isEnrolled
					.equalsIgnoreCase(EMMContants.EMMPrefContant.ENROLL_STATUS_N)) {
				mHandler.post(enrollsys);
			}
		}
	}

	/**
	 * device enrolled enter sys
	 */
	Runnable entersys = new Runnable() {
		@Override
		public void run() {
			DBUserInfoHelper dbUserInfoHelper = new DBUserInfoHelper(
					getApplicationContext());
			String userId = EMMPreferences.getUserID(getApplicationContext());
			if (TextUtils.isEmpty(userId)) {
				ToastUtils.show(AtyWelcome.this, "用户信息丢失或损坏,请重新注册");
				mHandler.postDelayed(enrollsys,
						EMMContants.SYSCONF.SYS_WELCOME_DELAY);
			} else {
				// if
				// (EMMPreferences.getSecurityStatus(getApplicationContext())) {
				// Intent goUnlock = new Intent(AtyWelcome.this,
				// UnlockGesturePasswordActivity.class);
				// startActivity(goUnlock);
				// finish();
				// } else {
				UserInfo contentUserinfo = dbUserInfoHelper
						.findUserByUserId(userId);
				if (contentUserinfo != null) {
					Intent goMainIntent = new Intent(AtyWelcome.this,
							AtyMain.class);
					goMainIntent
							.putExtra(UserInfo.TAG.TB_NAME, contentUserinfo);
					startActivity(goMainIntent);
					finish();
				} else {
					L.i(getClass(),
							"userinfo is null device will reenroll ... ");
					ToastUtils.show(AtyWelcome.this, "用户信息丢失或损坏,请重新注册");
					mHandler.postDelayed(enrollsys,
							EMMContants.SYSCONF.SYS_WELCOME_DELAY);
				}
				// }
			}
		}
	};

	/**
	 * device has not enrolled enter enroll device
	 */
	Runnable enrollsys = new Runnable() {

		@Override
		public void run() {
			Intent intent = new Intent(AtyWelcome.this, AtyEnroll.class);
			intent.putExtra(FLAG_FROM_ATY, FLAG_FROM_ATY_VALUE);
			startActivity(intent);
			finish();
		}
	};

	protected void onResume() {
		super.onResume();
		if (!isActiveAdmin()) {
			launchDeviceAdmin();
		} else {
			turn();
		}
	};

	protected void launchDeviceAdmin() {
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, emmmDeviceAdmin);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				getString(R.string.add_admin_extra_app_text));
		startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE_ENABLE_ADMIN:
			if (resultCode == RESULT_OK) {
				turn();
			} else if (resultCode == RESULT_CANCELED) {
				finish();
			}
			break;
		}
	}

	public void createShotCut() {

		if (!hasInstallShortcut()) {
			addShortcut();
		}
	}

	public void createShortCut(Activity app, String componetName,
			String appName, int icon) {

		SharedPreferences sp = app.getSharedPreferences("CreateShortcut", 0);
		// 这种创建方法可以在程序卸载的时候，快捷方式自动 删除！
		ComponentName comp = new ComponentName(app.getApplicationContext(),
				componetName);
		Intent shortcutIntent = new Intent(
				new Intent(Intent.ACTION_MAIN).setComponent(comp));

		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(
				app, icon);
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
		// 不创建重复快捷方式
		intent.putExtra("duplicate", false);
		// 添加快捷方式
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		app.sendBroadcast(intent);
		sp.edit().putString("create", "yes").commit();

	}

	/**
	 * Helper to determine if we are an active admin
	 */
	private boolean isActiveAdmin() {
		return devicePolicyManager.isAdminActive(emmmDeviceAdmin);
	}

	/**
	 * 判断是否已安装快捷方式
	 */
	private boolean hasInstallShortcut() {
		boolean hasInstall = false;
		// Android2.2以前的Launcher查询
		// final String AUTHORITY1 = "com.android.launcher.settings";
		// Android2.2以后的Launcher查询
		final String AUTHORITY2 = "com.android.launcher2.settings";
		// Uri CONTENT_URI1 = Uri.parse("content://" + AUTHORITY1
		// + "/favorites?notify=true");
		Uri CONTENT_URI2 = Uri.parse("content://" + AUTHORITY2
				+ "/favorites?notify=true");
		Cursor cursor = getContentResolver().query(CONTENT_URI2,
				new String[] { "_id", "title", "iconResource" }, "title=?",
				new String[] { getString(R.string.app_name) }, null);
		// 如果Android2.2以前的不能查到，就采用Android2.2以后的Launcher查询
		if (cursor == null) {
			cursor = getContentResolver().query(CONTENT_URI2,
					new String[] { "_id", "title", "iconResource" }, "title=?",
					new String[] { getString(R.string.app_name) }, null);
		}
		// 如果查到有快捷方式，则不创建;如果没有，则引导用户创建快捷方式
		if (cursor != null && cursor.getCount() > 0) {
			hasInstall = true;
		}
		return hasInstall;
	}

	/**
	 * create shortcut in home screen
	 */
	private void addShortcut() {
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");

		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.app_name));
		shortcut.putExtra("duplicate", false);

		// 指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer
		// 这里必须为Intent设置一个action，可以任意(但安装和卸载时该参数必须一致)
		Intent respondIntent = new Intent(this, this.getClass());
		respondIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, respondIntent);
		// 快捷方式的图标
		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(
				this, R.drawable.icon);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
		sendBroadcast(shortcut);
	}

	/**
	 * create shortcut in home screen
	 */
	private void addShortcut_old() {
		// Adding shortcut for MainActivity
		// on Home screen
		Intent shortcutIntent = new Intent(getApplicationContext(),
				AtyWelcome.class);
		shortcutIntent.setAction(Intent.ACTION_MAIN);
		Intent addIntent = new Intent();
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.app_name));
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(
						getApplicationContext(), R.drawable.icon));
		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		getApplicationContext().sendBroadcast(addIntent);
	}

}
