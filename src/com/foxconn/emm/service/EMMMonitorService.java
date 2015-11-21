package com.foxconn.emm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

import com.foxconn.app.aty.AtyLimitApps;
import com.foxconn.emm.bean.LimitListInfo;
import com.foxconn.emm.dao.LimitListDao;
import com.foxconn.emm.dao.TrafficAppsOptDao;
import com.foxconn.emm.dao.TrafficOptDao;
import com.foxconn.emm.receiver.ConnectivityReceiver;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.utils.NetUtil;
import com.foxconn.emm.utils.TextFormater;

/**
 * 该类主要用于进行系统监控主类
 * 
 */
public class EMMMonitorService extends Service {

	private static final String SERVICE_NAME = "com.foxconn.emm.service.EMMMonitorService";

	private EMMMonitorBinder monitorBinder;

	private PackageManager packageManager;
	private AlarmManager mAlarmManager;
	private PendingIntent mPendingIntent;

	private TaskSubmitter taskSubmitter;

	private TaskTracker taskTracker;
	private SharedPreferences sharedPrefs;
	private Context context;
	private ServiceManager serviceManager;
	private EMMMonitorProcessManager emmProcessManager;
	// //该线程池主要用于进行app监控...
	private ExecutorService processorExecutors;

	// //该线程组主要用于进行流量统计启动
	private ScheduledExecutorService scheduledExecutorService;

	private TrafficAppsOptDao trafficAppsOptDao;
	private TrafficOptDao trafficOptDao;

	private LimitListDao limitListDao;


	private BroadcastReceiver connBroadcastReceiver;

	/**
	 * 限制名单列表
	 */
	public List<LimitListInfo> limitLists;

	/**
	 * 临时停止限制列表
	 */
	private LimitListInfo tempStoplimitListInfo;

	private ActivityManager activitymgr;

	private Intent atyLimitappintent;

	private boolean flag = true;

	public KeyguardManager keyguardManager;

	@Override
	public void onCreate() {
		super.onCreate();
		L.v(this.getClass(), "onCreate  ... ");
		activitymgr = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		processorExecutors = Executors.newFixedThreadPool(3);
		scheduledExecutorService = Executors.newScheduledThreadPool(1);
		taskSubmitter = new TaskSubmitter(this);
		taskTracker = new TaskTracker(this);
		// /随后更改 pref的名字..防止后期的信息混乱.
		sharedPrefs = getSharedPreferences(
				EMMContants.XMPPCONF.SHARED_PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		context = EMMMonitorService.this;
		trafficAppsOptDao = new TrafficAppsOptDao(context);
		trafficOptDao = new TrafficOptDao(context);

		emmProcessManager = new EMMMonitorProcessManager(this);
		connBroadcastReceiver = new ConnectivityReceiver(this);
		this.serviceManager = new ServiceManager(this);
		registerContentProvier();
		registerConnectivityReceiver();
		limitListDao = new LimitListDao(context);
		limitLists = new ArrayList<LimitListInfo>();
		tempStoplimitListInfo = new LimitListInfo();
		limitLists = limitListDao.getLimitList();
		monitorBinder = new EMMMonitorBinder();

		atyLimitappintent = new Intent(this, AtyLimitApps.class);
		atyLimitappintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		protectServiceProcess();
		// //启动监控Apps的线程.
		startMonitorAppsTask();
		// //启动收集流量信息的定时线程.
		startCollectTrafficTask();

	}

	private void registerContentProvier() {
		getApplication().getContentResolver().registerContentObserver(Uri.parse("content://com.foxconn.emm.unlockprovider"),
				true, new LimitListObserver(new Handler()));
	}

	private void registerConnectivityReceiver() {
		L.d(EMMMonitorService.class, "registerConnectivityReceiver()...");
		IntentFilter filter = new IntentFilter();
		filter.addAction(android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(connBroadcastReceiver, filter);
	}

	private void unregisterConnectivityReceiver() {
		L.d(EMMMonitorService.class, "unregisterConnectivityReceiver()...");
		unregisterReceiver(connBroadcastReceiver);
	}


	private void startMonitorAppsTask() {
		L.d(this.getClass(), "startMonitorAppsTask submit task ");
		getProcessorExecutors().submit(new MonitorAppsTask());
	}

	/**
	 * 防止Service被kill
	 */
	public void protectServiceProcess() {
		Intent intent = new Intent(getApplicationContext(),
				EMMMonitorService.class);
		mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		mPendingIntent = PendingIntent.getService(this, 0, intent,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		long now = System.currentTimeMillis();
		mAlarmManager.setInexactRepeating(AlarmManager.RTC, now, 60 * 1000,
				mPendingIntent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		L.v(EMMMonitorService.class, "[SERVICE] onStartCommand");
		// startMonitorAppsTask();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		L.v(EMMMonitorService.class, "[SERVICE] onBind");
		return monitorBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return true;
	}

	public class EMMMonitorBinder extends Binder implements IMonitorService {

		@Override
		public void removeTempStopLimit(LimitListInfo limitListInfo) {
			
			appRestartLimit(limitListInfo);
		}

		@Override
		public void addTempStopLimit(LimitListInfo limitListInfo) {
			appStopLimit(limitListInfo);
		}
	}

	/**
	 * 重新开启对应用程序的限制
	 */
	public void appRestartLimit(LimitListInfo limitListInfo) {
		if (limitListInfo != null) {
			String packName = limitListInfo.getPackName();
			if (!TextUtils.isEmpty(packName)) {
				if (packName.equalsIgnoreCase(tempStoplimitListInfo.getPackName())) {
					tempStoplimitListInfo = new LimitListInfo();
				}
			} else {
				tempStoplimitListInfo = new LimitListInfo();
			}
		}
	}

	/**
	 * 停止对应用程序的限制列表
	 */
	public void appStopLimit(LimitListInfo limitListInfo) {
		this.tempStoplimitListInfo = limitListInfo;
	}

	/**
	 * 启动收集流量信息的定时线程.
	 */
	public void startCollectTrafficTask() {

		if (getScheduledExecutorService().isShutdown()
				|| getScheduledExecutorService().isTerminated()) {
			this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
		}
		// // 两分钟启动一次收集线程. 设置默认30分钟

		getScheduledExecutorService().scheduleAtFixedRate(
				new StartCollectorTask(), 0, 30, TimeUnit.MINUTES);
	}

	public static Intent getIntent() {
		return new Intent(SERVICE_NAME);
	}

	/**
	 * 
	 * 启动收集流量信息的定时线程,将收集流量信息的子线程添加到线程池中进行.
	 */
	private class StartCollectorTask implements Runnable {
		@Override
		public void run() {
			if (emmProcessManager != null) {
				L.d(this.getClass(), "StartCollectorTask run.... ");
				emmProcessManager.submitCollectAllTrafficTask();
			}
			L.d(this.getClass(), "StartCollectorTask end.... ");
		}
	}

	/**
	 * 在这里进行网络判断,流量等其他信息的同步. 1.同步流量信息 a.同步系统流量信息 b.同步App的流量信息 2.同步限制名单的信息
	 */
	public void onNetChange() {

		L.d(this.getClass(), "EMMMonitorService onNetChange ...");
		// / 无网络直接返回,防止浪费资源.
		if (new NetUtil(getContext()).getConnectState()==NetUtil.NetWorkState.NONE) {
			return;
		}
		// 有wlan网络连接时进行同步操作.
		if (NetUtil.checkNetWorkInfo(getApplicationContext())) {
			if (emmProcessManager != null) {
				if (!EMMPreferences.getDeviceEnrolled(getContext()).equalsIgnoreCase(EMMContants.EMMPrefContant.ENROLL_STATUS_N)) {
					L.d(this.getClass(), "submitSyncAllTrafficTask run ...");
					emmProcessManager.submitSyncAllTrafficTask();
					L.d(this.getClass(), "submitSyncAllTrafficTask running ...");
				}
			}
		}

	}

	/**
	 * 进行系统AppStack的监控工作
	 */
	private class MonitorAppsTask implements Runnable {
		@Override
		public void run() {
			L.d(this.getClass(), "MonitorAppsTask is begin ...");
			while (flag) {
				try {
					keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
					if (keyguardManager.inKeyguardRestrictedInputMode()) {
						tempStoplimitListInfo = new LimitListInfo();
					}
					List<RunningTaskInfo> taskinfos = activitymgr.getRunningTasks(1);
						RunningTaskInfo currenttask = taskinfos.get(0);
					String	packname = currenttask.baseActivity.getPackageName();
//					L.d(this.getClass(), packname);
					if (!packname.equalsIgnoreCase(tempStoplimitListInfo.getPackName())) {
						appRestartLimit(tempStoplimitListInfo);
					}
					LimitListInfo containPackage = containPackage(packname,limitLists);
					if (containPackage != null) {
						L.d(this.getClass(),"limitType--->" + containPackage.getLimitType());
						if (!TextUtils.isEmpty(containPackage.getLimitType())) {
							// /0: 黑名单, 1:白名单 , 2:限制名单
							if (!containPackage.getLimitType().equals("1")) {
								if (packname.equalsIgnoreCase(tempStoplimitListInfo.getPackName())) {
									// / 说明暂时移除限制.
									L.d(this.getClass(), "temp stop limit : "+ packname);
									Thread.sleep(1000);
									continue;
								}
								atyLimitappintent.putExtra("packname", packname);
								// / 根据传入的限制名单参数. 可以直接启动Activity.
								atyLimitappintent.putExtra("limitlistinfo",containPackage);
								startActivity(atyLimitappintent);
							}
						}
						// / 如果不存在
					}
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 查找是否存在相应的限制名单,如果存在返回相应的限制名单的内容
	 * 
	 * @param packageName
	 * @param limitListInfos
	 * @return
	 */
	public LimitListInfo containPackage(String packageName,
			List<LimitListInfo> limitListInfos) {
		if(limitListInfos==null||limitListInfos.size()<=0){
			return null;
		}
		for (LimitListInfo limitListInfo : limitListInfos) {
			if (!TextUtils.isEmpty(limitListInfo.getPackName())) {
				if (limitListInfo.getPackName().equalsIgnoreCase(packageName)) {
					return limitListInfo;
				}
			}
		}
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		///关机时记录本次关机的日期
		TextFormater.saveCurrentDateString(context);
		getEmmProcessManager().submitCollectAllTrafficTask();
		unregisterConnectivityReceiver();
		if (scheduledExecutorService != null) {
			scheduledExecutorService.shutdown();
		}
		flag = false;
	}

	private class LimitListObserver extends ContentObserver {
		public LimitListObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			limitLists = limitListDao.getLimitList();
			L.d(this.getClass(), "LimitList DB onChange ...");
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		getApplication().onLowMemory();
	}

	/**
	 * @return the processorExecutors
	 */
	public ExecutorService getProcessorExecutors() {
		return processorExecutors;
	}

	/**
	 * @param processorExecutors
	 *            the processorExecutors to set
	 */
	public void setProcessorExecutors(ExecutorService processorExecutors) {
		this.processorExecutors = processorExecutors;
	}

	/**
	 * @return the emmProcessManager
	 */
	public EMMMonitorProcessManager getEmmProcessManager() {
		return emmProcessManager;
	}

	/**
	 * @param emmProcessManager
	 *            the emmProcessManager to set
	 */
	public void setEmmProcessManager(EMMMonitorProcessManager emmProcessManager) {
		this.emmProcessManager = emmProcessManager;
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
	 * @return the packageManager
	 */
	public PackageManager getPackageManager() {
		return packageManager;
	}

	/**
	 * @param packageManager
	 *            the packageManager to set
	 */
	public void setPackageManager(PackageManager packageManager) {
		this.packageManager = packageManager;
	}

	/**
	 * @return the scheduledExecutorService
	 */
	public ScheduledExecutorService getScheduledExecutorService() {
		return scheduledExecutorService;
	}

	/**
	 * @param scheduledExecutorService
	 *            the scheduledExecutorService to set
	 */
	public void setScheduledExecutorService(
			ScheduledExecutorService scheduledExecutorService) {
		this.scheduledExecutorService = scheduledExecutorService;
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
	 * @return the trafficAppsOptDao
	 */
	public TrafficAppsOptDao getTrafficAppsOptDao() {
		return trafficAppsOptDao;
	}

	/**
	 * @param trafficAppsOptDao
	 *            the trafficAppsOptDao to set
	 */
	public void setTrafficAppsOptDao(TrafficAppsOptDao trafficAppsOptDao) {
		this.trafficAppsOptDao = trafficAppsOptDao;
	}

	/**
	 * @return the trafficOptDao
	 */
	public TrafficOptDao getTrafficOptDao() {
		return trafficOptDao;
	}

	/**
	 * @param trafficOptDao
	 *            the trafficOptDao to set
	 */
	public void setTrafficOptDao(TrafficOptDao trafficOptDao) {
		this.trafficOptDao = trafficOptDao;
	}

	/**
	 * Class for summiting a new runnable task.
	 */
	public class TaskSubmitter {

		final EMMMonitorService notificationService;

		public TaskSubmitter(EMMMonitorService notificationService) {
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

		final EMMMonitorService emmService;

		public int count;

		public TaskTracker(EMMMonitorService emmMonitorService) {
			this.emmService = emmMonitorService;
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

}
