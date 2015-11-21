package com.foxconn.emm.service;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.WindowManager;

import com.foxconn.app.App;
import com.foxconn.app.IConnectionStatusCallback;
import com.foxconn.app.R;
import com.foxconn.app.aty.AtyEnroll;
import com.foxconn.app.aty.AtyMain;
import com.foxconn.app.aty.EMMBaseActivity;
import com.foxconn.app.aty.EMMBaseActivity.BackPressHandler;
import com.foxconn.emm.bean.RegResult;
import com.foxconn.emm.bean.UserInfo;
import com.foxconn.emm.dao.DBUserInfoHelper;
import com.foxconn.emm.dao.OffLineDatabase;
import com.foxconn.emm.receiver.EMMBroadcastReceiver;
import com.foxconn.emm.receiver.EMMBroadcastReceiver.EventHandler;
import com.foxconn.emm.tools.PolicyControl;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.utils.EMMReqParamsUtils;
import com.foxconn.emm.utils.HttpClientUtil;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.utils.NetUtil;
import com.foxconn.emm.utils.ToastUtils;
import com.foxconn.emm.utils.NetUtil.NetWorkState;
import com.foxconn.emm.xmpp.NotificationService;
import com.google.gson.Gson;

public class EMMService extends BaseService implements EventHandler,
		BackPressHandler {

	private Binder emmBinder = new EMMBinder();

	private ExecutorService processorExecutors;

	private IConnectionStatusCallback mConnectionStatusCallback;

	private TaskSubmitter taskSubmitter;

	private TaskTracker taskTracker;

	private EMMProcessManager emmProcessManager;

	public static final int CONNECTED = 0; // 已经连接
	public static final int FAILCONNECTED = -1; // 断开
	public static final int CONNECTING = 1; // 连接中

	public static final String NETWORK_ERROR = "网络链接不可用";// 网络错误
	public static final String LOGIN_FAILED = "login failed";// 登录失败

	private static final String SERVICE_NAME = "com.foxconn.service.EMMService";

	private static final int SERVICE_NOTIFICATION = 1;

	private Handler mMainHandler = new Handler();

	private int mConnectedState = FAILCONNECTED; // 是否已经连接

	private SharedPreferences sharedPrefs;

	private DBUserInfoHelper dbuserInfoHelper;

	private Context context;

	private ServiceManager serviceManager;

	private PolicyControl policyControl;

	private AlarmManager mAlarmManager;

	private PendingIntent mPendingIntent;

	private ActivityManager mActivityManager;

	@Override
	public void onCreate() {
		super.onCreate();
		L.v(this.getClass(), "onCreate");
		processorExecutors = Executors.newSingleThreadExecutor();
		taskSubmitter = new TaskSubmitter(this);
		taskTracker = new TaskTracker(this);
		emmProcessManager = new EMMProcessManager(this);
		// /随后更改 pref的名字..防止后期的信息混乱.
		sharedPrefs = getSharedPreferences(
				EMMContants.XMPPCONF.SHARED_PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		dbuserInfoHelper = new DBUserInfoHelper(this);
		context = EMMService.this;
		mActivityManager = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
		policyControl = new PolicyControl(this.getApplication());
		EMMBroadcastReceiver.mListeners.add(this);
		EMMBaseActivity.mListeners.add(this);
		this.serviceManager = new ServiceManager(this);
		checkService();
		protectServiceProcess();
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		/**
		 * 设置开机启动
		 */
		if (intent != null
				&& intent.getAction() != null
				&& TextUtils.equals(intent.getAction(),
						EMMBroadcastReceiver.BOOT_COMPLETED_ACTION)) {
			checkService();
		}
		return START_STICKY;
	};

	public class EMMBinder extends Binder {
		public EMMService getService() {
			return EMMService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		L.v(EMMService.class, "[SERVICE] onBind");
		String action = intent.getAction();
		if (!TextUtils.isEmpty(action)) {
			if (action.equals(EMMContants.EMMAction.EMM_CHECK_APP_UPDATE)
					|| action.equals(EMMContants.EMMAction.SYNCPOLICY_ACTION)) {
				emmProcessManager.startUpdateProcess();
			} else if (action.equals(EMMContants.EMMAction.LOGIN_ACTION)) {
				emmProcessManager.startLoginProcess();
			}

		}
		return emmBinder;
	}

	public static Intent getIntent() {
		return new Intent(SERVICE_NAME);
	}

	/**
	 * 启动NotificationService
	 */
	public void checkService() {
		serviceManager.startNotificationService();
		serviceManager.startEMMMonitorService();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return true;
	}

	public void loginEMM(String username, String password) {
		if (!EMMPreferences.getDeviceEnrolled(getContext()).equalsIgnoreCase(
				EMMContants.EMMPrefContant.ENROLL_STATUS_N)) {
			return;
		}

		// 判断网络状态,如果没有网络则,转离线注册.
		if (!NetUtil.checkNetWorkInfo(getApplicationContext())) {
			postConnectionFailed(NETWORK_ERROR + ", 将转为离线注册");
			taskSubmitter.submit(new OfflineLoginTaskCallable(username,
					password));
			return;
		}
		postConnecting();
		Future<RegResult> result = EMMService.this.getProcessorExecutors()
				.submit(new LoginTaskCallable(username, password));
		/**
		 * { "code": "0", "msg": "该用户信息不存在,如需注册请联系管理员579 25632", "success":
		 * "false" } { "code": "1", "msg": "用户密码输入有误", "success": "false" } {
		 * "code": "2", "msg": "设备注册成功", "success": "true" } { "code": "3",
		 * "msg": "该设备不受管控，如需注册请联系管理员579 25632", "success": "false" } { "code":
		 * "4", "msg": "设备重新注册成功", "success": "true" } { "code": "5", "msg":
		 * "请使用原有用户工号注册", "success": "false" }
		 */
		try {
			RegResult regResult = result.get();// / 注册结果
			if (regResult != null) {
				if (regResult.getSuccess().contains("true")) {
					postConnectionScuessed(regResult.getMsg());
					Future<UserInfo> extraUserinfo = getProcessorExecutors()
							.submit(new ExtraUserInfoTaskCallable(username));
					UserInfo userInfo = extraUserinfo.get();
					/**
					 * 用户信息注册同时如果因为网络原因没有同步成功,则进行后续同步.
					 */
					if (userInfo == null
							|| (TextUtils.isEmpty(userInfo.getUser_id()))) {
						// / 用户信息为空, 工号为空,

					} else {
						long ir = dbuserInfoHelper.insertUserInfo(userInfo);
						if (ir > 0) {
							// postConnectionScuessed("用户信息同步成功!");
							EMMPreferences.setDeviceEnrolled(getContext(),
									EMMContants.EMMPrefContant.ENROLL_STATUS_Y);
							EMMPreferences.setUserID(getContext(),
									userInfo.getUser_id());
							EMMPreferences.setUserBGID(getContext(),
									userInfo.getBu_group());
							EMMPreferences.setIMEI(getContext(),
									EMMReqParamsUtils.getWifiMac(getContext()));
							EMMPreferences.setSecurityStatus(getContext(),
									false);
							L.i(this.getClass(),
									"save userinfo complete, sync userinfo success  !");
						}
					}

				} else if (regResult.getSuccess().contains("false")) {
					postConnectionFailed(regResult.getMsg());
				}
				// / 记录注册状态. 获取用户信息...
				// / 转而进行信息存储...值DB

			} else {
				postConnectionFailed(NETWORK_ERROR);
			}
		} catch (InterruptedException e) {
			postConnectionFailed(e.getMessage());
			e.printStackTrace();
		} catch (ExecutionException e) {
			postConnectionFailed(e.getMessage());
			e.printStackTrace();
		}
		// //通知设备端注册成功
		// / 如果网络连通则进行Policy同步... , 离线注册..
		if (NetUtil.checkNetWorkInfo(getApplicationContext())) {
			// / 同步Policy task
			if (!EMMPreferences.getDeviceEnrolled(getContext())
					.equalsIgnoreCase(
							EMMContants.EMMPrefContant.ENROLL_STATUS_N)) {
				emmProcessManager.submitSyncPolicyTask();
			}
		}

	}

	/**
	 * 可以进行检查Service的状态
	 */
	public void reFreshEMM() {
		// / 启动NotificationService
		checkService();
		// / 检测启动MonitorService
		// /

	}

	/**
	 * 防止Service被kill
	 */
	public void protectServiceProcess() {
		Intent intent = new Intent(getApplicationContext(), EMMService.class);
		mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		mPendingIntent = PendingIntent.getService(this, 0, intent,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		long now = System.currentTimeMillis();
		mAlarmManager.setInexactRepeating(AlarmManager.RTC, now, 60 * 1000,
				mPendingIntent);
	}

	/**
	 * 注册状态变化回调
	 * 
	 * @param cb
	 */
	public void registerConnectionStatusCallback(IConnectionStatusCallback cb) {
		mConnectionStatusCallback = cb;
	}

	public void unRegisterConnectionStatusCallback() {
		mConnectionStatusCallback = null;
	}

	public ExecutorService getProcessorExecutors() {
		if (processorExecutors == null || processorExecutors.isShutdown()
				|| processorExecutors.isTerminated()) {
			processorExecutors = Executors.newSingleThreadExecutor();
		}
		return processorExecutors;
	}

	public void setProcessorExecutors(ExecutorService processorExecutors) {
		this.processorExecutors = processorExecutors;
	}

	/**
	 * Class for summiting a new runnable task.
	 */
	public class TaskSubmitter {

		final EMMService notificationService;

		public TaskSubmitter(EMMService notificationService) {
			this.notificationService = notificationService;
		}

		public Future submit(Runnable task) {
			Future result = null;
			if (!notificationService.getProcessorExecutors().isTerminated()
					&& !notificationService.getProcessorExecutors()
							.isShutdown() && task != null) {
				result = notificationService.getProcessorExecutors().submit(
						task);
			}
			return result;
		}

	}

	/**
	 * Class for monitoring the running task count.
	 */
	public class TaskTracker {

		final EMMService emmService;

		public int count;

		public TaskTracker(EMMService notificationService) {
			this.emmService = notificationService;
			this.count = 0;
		}

		public void increase() {
			synchronized (emmService.getTaskTracker()) {
				emmService.getTaskTracker().count++;
				L.d(this.getClass(), "Incremented task count to " + count);
			}
		}

		public void decrease() {
			synchronized (emmService.getTaskTracker()) {
				emmService.getTaskTracker().count--;
				L.d(this.getClass(), "Decremented task count to " + count);
			}
		}

	}

	/**
	 * 在线注册
	 */
	protected final class LoginTaskCallable implements Callable<RegResult> {

		private String password;
		private String username;

		public LoginTaskCallable(String username, String password) {
			this.username = username;
			this.password = password;
		}

		@Override
		public RegResult call() throws Exception {
			L.d(this.getClass(), "Device onLine register is running ");

			String url = EMMContants.SYSCONF.REQ_ENTROLL;
			String params = EMMReqParamsUtils.getEntrollStrData(
					EMMService.this, username, password);

			L.d(this.getClass(), params);
			String requestresult = HttpClientUtil.returnData(url, params);
			L.d(this.getClass(), requestresult);

			/**
			 * 注册失败, 可能引用到该标签 <script>parent.location.href='login.jsp';</script>
			 */
			if (TextUtils.isEmpty(requestresult)
					|| requestresult.contains("<script>")) {
				return null;
			}
			return new Gson().fromJson(requestresult, RegResult.class);
		}
	}

	/**
	 * 
	 * 离线注册
	 * 
	 */
	protected final class OfflineLoginTaskCallable implements Runnable {

		private String password;
		private String username;

		public OfflineLoginTaskCallable(String username, String password) {
			this.username = username;
			this.password = password;
		}

		@Override
		public void run() {
			if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
				return;
			}
			L.i(this.getClass(), "Offline login task is running ");
			postConnecting();
			UserInfo userinfo = new UserInfo();
			OffLineDatabase offDB = new OffLineDatabase(EMMService.this);
			userinfo = offDB.findUserByUserId(username);
			if (userinfo != null && !TextUtils.isEmpty(userinfo.getUser_id())) {
				long ir = dbuserInfoHelper.insertUserInfo(userinfo);
				if (ir > 0) {

					EMMPreferences.setDeviceEnrolled(getContext(),
							EMMContants.EMMPrefContant.ENROLL_STATUS_P);
					EMMPreferences.setUserID(getContext(),
							userinfo.getUser_id());
					EMMPreferences.setUserBGID(getContext(),
							userinfo.getBu_group());
					EMMPreferences.setIMEI(getContext(),
							EMMReqParamsUtils.getWifiMac(getContext()));
					EMMPreferences.setSecurityStatus(getContext(), false);
					L.i(this.getClass(), "save userinfo complete !");
					postConnectionScuessed("离线注册成功!");
				}
			} else {
				postConnectionFailed("该帐号不存在,请重试或联系工作人员");
			}
		}

	}

	/**
	 * get userinfo from server
	 * 
	 */
	protected final class ExtraUserInfoTaskCallable implements
			Callable<UserInfo> {

		private String username;

		public ExtraUserInfoTaskCallable(String username) {
			this.username = username;
		}

		@Override
		public UserInfo call() throws Exception {
			if (new NetUtil(getApplication()).getConnectState() == NetWorkState.NONE) {
				return null;
			}
			String uri = EMMContants.SYSCONF.REQ_USERINFO;
			String entrollData = EMMReqParamsUtils.getSyncUserInfoStr(
					getApplicationContext(), username);
			L.d(this.getClass(), entrollData);
			String resultStr = HttpClientUtil.returnData(uri, entrollData);

			// L.d(this.getClass(), "request result--- >>>" + resultStr);
			if (TextUtils.isEmpty(resultStr)) {
				return null;
			}
			return new Gson().fromJson(resultStr, UserInfo.class);
		}

	}

	protected void startPendingService() {
		AlarmManager alarmManager = (AlarmManager) this
				.getSystemService(Service.ALARM_SERVICE);
		Intent intent = new Intent(this, NotificationService.class);
		final PendingIntent pendingIntent = PendingIntent.getService(
				this.getApplicationContext(), 0, intent, 0);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 0, 60 * 1000,
				pendingIntent);
		L.i("网络异常,开启定时间隔启动NotificationService");
	}

	/**
	 * 当网络发生变化的时候.
	 */
	@Override
	public void onNetChange() {
		if (!NetUtil.checkNetWorkInfo(getApplicationContext())) {// 如果是网络断开，不作处理
//			connectionFailed(NETWORK_ERROR);
			L.w(getClass(), "network is error ");
			return;
		}
		// / 如果网络连通则进行Policy同步... , 离线注册..
		if (NetUtil.checkNetWorkInfo(getApplicationContext())) {
			emmProcessManager.startUpdateProcess();
			if (EMMPreferences.getDeviceEnrolled(getContext()).equalsIgnoreCase(
							EMMContants.EMMPrefContant.ENROLL_STATUS_P)) {
				checkUserInfoIsAuthorized();
			}
			if (EMMPreferences.getDeviceEnrolled(getContext())
					.equalsIgnoreCase(
							EMMContants.EMMPrefContant.ENROLL_STATUS_Y)) {
				checkUserUploadUserImage();
			}
		}
	}

	private void checkUserUploadUserImage() {
		// / upload img task 如果没有上传头像,则上传
		if (!EMMPreferences.isHASHEADIMG_UPLOAD(getContext()))
			emmProcessManager.submitUploadImgTask();
	}

	/**
	 * 用户注册状态如果不为Y,则进行注册.
	 */
	private void checkUserInfoIsAuthorized() {
		String userId = EMMPreferences.getUserID(getContext());
		String password = EMMContants.ReqContants.reg_OffLine_PWD;
		Future<RegResult> result = getProcessorExecutors().submit(
				new LoginTaskCallable(userId, password));
		try {
			if (result != null) {
				RegResult regResult = result.get();
				if (regResult == null
						|| regResult.getSuccess().contains("false")) {
					// / 如果系统登入账号与离线注册的账户不匹配,则退出系统.强制用户重新登录.
					EMMPreferences.setDeviceEnrolled(getContext(),
							EMMContants.EMMPrefContant.ENROLL_STATUS_N);
					// / 弹出一个对话框告诉用户,注册不正确.
					postAuthorFailed();
				} else {
					Future<UserInfo> extraUserinfo = getProcessorExecutors()
							.submit(new ExtraUserInfoTaskCallable(userId));
					UserInfo userInfo = extraUserinfo.get();
					/**
					 * 用户信息注册同时如果因为网络原因没有同步成功,则进行后续同步.
					 * 
					 */
					if (userInfo == null|| (TextUtils.isEmpty(userInfo.getUser_id()))) {
						// / 用户信息为空, 工号为空,提醒用户重新注册.
						postAuthorFailed();
					} else {
						long ir = dbuserInfoHelper.insertUserInfo(userInfo);
						if (ir > 0) {
							EMMPreferences.setDeviceEnrolled(getContext(),
									EMMContants.EMMPrefContant.ENROLL_STATUS_N);
							EMMPreferences.setUserID(getContext(),
									userInfo.getUser_id());
							EMMPreferences.setUserBGID(getContext(),
									userInfo.getBu_group());
							EMMPreferences.setIMEI(getContext(),
									EMMReqParamsUtils.getWifiMac(getContext()));
							L.d(this.getClass(),
									"save userinfo complete, sync userinfo success  !");
						}
					}
				}
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	public void postAuthorFailed() {
		mMainHandler.post(new Runnable() {
			public void run() {
				userAuthorFailed(getApplication());
			}
		});
	}

	private void userAuthorFailed(final Context context) {
		AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.personnal_user_offline_online_authorized_title);
		builder.setMessage(R.string.personnal_user_offline_online_authorized_fail_msg);
		builder.setPositiveButton(R.string.emm_device_entroll_reg_btn,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						try {
							ToastUtils.show(context, "当前用户有误,请退出后手动重新注册");
							Intent intent = new Intent(context, AtyEnroll.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(intent);

						} catch (Exception e) {
							ToastUtils.show(context, "系统出错,请退出后手动重新注册");
						}
						// finally {
						// dialog.dismiss();
						// // 退出
						// App.getInstance().exit();
						// android.os.Process.killProcess(android.os.Process.myPid());
						// System.exit(1);
						// }
					}
				});
		builder.setNegativeButton(R.string.emm_device_entroll_reg_btn_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 退出
						App.getInstance().exit();
						android.os.Process.killProcess(android.os.Process
								.myPid());
						System.exit(1);
					}
				});
		dialog = builder.create();
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();
	}

	public void postConnectionFailed(final String reason) {
		mMainHandler.post(new Runnable() {
			public void run() {
				connectionFailed(reason);
			}
		});
	}

	private void connectionFailed(String reason) {
		L.i(EMMService.class, "connectionFailed: " + reason);
		mConnectedState = FAILCONNECTED;// 更新当前连接状态
		if (mConnectionStatusCallback != null) {
			mConnectionStatusCallback.connectionStatusChanged(mConnectedState,
					reason);
			return;
		}
	}

	private void postConnectionScuessed(final String message) {
		mMainHandler.post(new Runnable() {
			public void run() {
				connectionScuessed(message);
			}
		});
	}

	private void connectionScuessed(String message) {
		mConnectedState = CONNECTED;// 已经连接上
		if (mConnectionStatusCallback != null)
			mConnectionStatusCallback.connectionStatusChanged(mConnectedState,
					"" + message);
	}

	// 连接中，通知界面线程做一些处理
	private void postConnecting() {
		mMainHandler.post(new Runnable() {
			public void run() {
				connecting();
			}
		});
	}

	private void connecting() {
		mConnectedState = CONNECTING;// 连接中
		if (mConnectionStatusCallback != null)
			mConnectionStatusCallback.connectionStatusChanged(mConnectedState,
					"");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (processorExecutors != null) {
			processorExecutors.shutdown();
		}
		L.i(this.getClass(), "onDestroy");
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		App.getInstance().onLowMemory();
	}

	/**
	 * @return the taskSubmitter
	 */
	public TaskSubmitter getTaskSubmitter() {
		return taskSubmitter;
	}

	/**
	 * @param taskSubmitter
	 *            the taskSubmitter to set
	 */
	public void setTaskSubmitter(TaskSubmitter taskSubmitter) {
		this.taskSubmitter = taskSubmitter;
	}

	/**
	 * @return the taskTracker
	 */
	public TaskTracker getTaskTracker() {
		return taskTracker;
	}

	/**
	 * @param taskTracker
	 *            the taskTracker to set
	 */
	public void setTaskTracker(TaskTracker taskTracker) {
		this.taskTracker = taskTracker;
	}

	/**
	 * @return the emmProcessManager
	 */
	public EMMProcessManager getEmmProcessManager() {
		return emmProcessManager;
	}

	/**
	 * @param emmProcessManager
	 *            the emmProcessManager to set
	 */
	public void setEmmProcessManager(EMMProcessManager emmProcessManager) {
		this.emmProcessManager = emmProcessManager;
	}

	/**
	 * @return the sharedPrefs
	 */
	public SharedPreferences getSharedPrefs() {
		return sharedPrefs;
	}

	/**
	 * @param sharedPrefs
	 *            the sharedPrefs to set
	 */
	public void setSharedPrefs(SharedPreferences sharedPrefs) {
		this.sharedPrefs = sharedPrefs;
	}

	/**
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * @return the serviceManager
	 */
	public ServiceManager getServiceManager() {
		return serviceManager;
	}

	/**
	 * @param serviceManager
	 *            the serviceManager to set
	 */
	public void setServiceManager(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	/**
	 * @return the policyControl
	 */
	public PolicyControl getPolicyControl() {
		return policyControl;
	}

	/**
	 * @param policyControl
	 *            the policyControl to set
	 */
	public void setPolicyControl(PolicyControl policyControl) {
		this.policyControl = policyControl;
	}

	/**
	 * 更新通知栏
	 * 
	 * @param message
	 */
	public void updateServiceNotification(String message) {
		// if (!PreferenceUtils.getPrefBoolean(this,
		// PreferenceConstants.FOREGROUND, true))
		// return;
		String title = EMMPreferences.getUserID(getApplicationContext());
		if (TextUtils.isEmpty(title)) {
			title = getString(R.string.emm_keep_running_background_title_hint);
		}
		Notification n = new Notification(R.drawable.emm_notification_icon,
				title, System.currentTimeMillis());
		n.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
		Intent notificationIntent = new Intent(this, AtyMain.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		n.contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		n.setLatestEventInfo(this, title, message, n.contentIntent);
		startForeground(SERVICE_NOTIFICATION, n);
	}

	// 判断程序是否在后台运行的任务
	Runnable monitorStatus = new Runnable() {
		public void run() {
			try {
				L.i(EMMService.class, "monitor Status is running... "
						+ getPackageName());
				mMainHandler.removeCallbacks(monitorStatus);
				// 如果在后台并且连接上了
				if (!isAppOnForeground()) {
					L.i(EMMService.class, "app run in background...");
					// if (isAuthenticated())
					updateServiceNotification(getString(R.string.emm_keep_running_background_content));
					return;
				} else {
					stopForeground(true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 应用正在前台运行
	 * 
	 * @return
	 */
	public boolean isAppOnForeground() {
		List<RunningTaskInfo> taskInfos = mActivityManager.getRunningTasks(1);
		if (taskInfos.size() > 0
				&& TextUtils.equals(getPackageName(),
						taskInfos.get(0).topActivity.getPackageName())) {
			return true;
		}

		// List<RunningAppProcessInfo> appProcesses = mActivityManager
		// .getRunningAppProcesses();
		// if (appProcesses == null)
		// return false;
		// for (RunningAppProcessInfo appProcess : appProcesses) {
		// // L.i("liweiping", appProcess.processName);
		// // The name of the process that this object is associated with.
		// if (appProcess.processName.equals(mPackageName)
		// && appProcess.importance ==
		// RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
		// return true;
		// }
		// }
		return false;
	}

	@Override
	public void activityOnResume() {
		L.i(this.getClass(), "activity onResume ...");
		mMainHandler.post(monitorStatus);
	}

	@Override
	public void activityOnPause() {
		L.i(this.getClass(), "activity onPause ...");
		mMainHandler.postDelayed(monitorStatus, 1000L);
	}

}
