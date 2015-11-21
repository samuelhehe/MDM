package com.foxconn.emm.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.foxconn.emm.bean.SysTraffic;
import com.foxconn.emm.bean.UidTraffic;
import com.foxconn.emm.dao.TrafficAppsOptDao;
import com.foxconn.emm.dao.TrafficOptDao;
import com.foxconn.emm.service.EMMMonitorService.TaskSubmitter;
import com.foxconn.emm.service.EMMMonitorService.TaskTracker;
import com.foxconn.emm.tools.TrafficTools;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.utils.EMMReqParamsUtils;
import com.foxconn.emm.utils.HttpClientUtil;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.utils.NetUtil;
import com.foxconn.emm.utils.TextFormater;
import com.foxconn.emm.utils.NetUtil.NetWorkState;
import com.foxconn.lib.network.control.NetworkControlTool;

/**
 * 主要对系统进行监控操作.分为两部分: 1.主要是进行设备流量信息的统计,对设备产生流量的app进行监控. 2.对设备运行的app进行监控.
 */
public class EMMMonitorProcessManager {

	private EMMMonitorService emmMonitorService;

	private ArrayList<Runnable> taskList;

	private boolean running = false;

	private Future<?> futureTask;

	private TaskSubmitter taskSubmitter;

	private TaskTracker taskTracker;

	private SharedPreferences sharedPrefs;

	private TrafficAppsOptDao trafficAppsOptDao;

	private TrafficOptDao trafficOptDao;

	private NetworkControlTool networkControlTool;

	public EMMMonitorProcessManager(EMMMonitorService emmMonitorService) {
		this.emmMonitorService = emmMonitorService;
		taskList = new ArrayList<Runnable>();
		taskSubmitter = emmMonitorService.getTaskSubmitter();
		taskTracker = emmMonitorService.getTaskTracker();
		sharedPrefs = emmMonitorService.getSharedPrefs();
		trafficAppsOptDao = emmMonitorService.getTrafficAppsOptDao();
		trafficOptDao = emmMonitorService.getTrafficOptDao();
		networkControlTool = new NetworkControlTool(
				emmMonitorService.getApplication());
	}

	/**
	 * Running all traffic app informations collectors tasks
	 */
	public void startCollectAppsTraffic() {
		submitCollectAppsTrafficTask();
	}

	/**
	 * Running all traffic sys informations collectors tasks
	 */
	public void startCollectSysTrafficTask() {
		submitCollectSysTrafficTask();
	}

	/**
	 * collect sys traffic only
	 */
	private void submitCollectSysTrafficTask() {
		L.i(this.getClass(), "CollectSysTrafficTask()...");
		addTask(new CollectSysTrafficTask());
	}

	/**
	 * collect apps traffic only
	 */
	private void submitCollectAppsTrafficTask() {
		L.i(this.getClass(), "CollectAppsTrafficTask()...");
		addTask(new CollectAppsTrafficTask());
	}

	/**
	 * collect all traffic include apps traffics and system traffic
	 */
	public void submitCollectAllTrafficTask() {
		L.i(this.getClass(), "CollectSysTrafficTask()...");
		submitCollectAppsTrafficTask();
		addTask(new CollectSysTrafficTask());
		submitMonitorDeviceTrafficTask();
		this.runTask();
	}

	/**
	 * monitor device traffic overflow limit data traffic
	 */
	public void submitMonitorDeviceTrafficTask() {
		L.i(this.getClass(), "MonitorDeviceTrafficTask()...");
		addTask(new MonitorDeviceTrafficTask());
	}

	/**
	 * sync device traffic collected all traffic include system traffic
	 */
	public void submitSyncDeviceTrafficTask() {
		L.i(this.getClass(), "SyncDeviceTrafficTask()...");
		addTask(new SyncDeviceTrafficTask());
	}

	/**
	 * 
	 * sync device traffic collected all traffic include apps traffics
	 * 
	 */
	public void submitSyncAppsTrafficTask() {
		L.i(this.getClass(), "SyncAppsTrafficTask()...");
		addTask(new SyncAppsTrafficTask());
	}

	/**
	 * 
	 * sync device traffic collected all traffic include apps traffics and
	 * system traffic
	 */
	public void submitSyncAllTrafficTask() {
		L.i(this.getClass(), "SyncAllTrafficTask()...");
		submitSyncDeviceTrafficTask();
		addTask(new SyncAppsTrafficTask());
		this.runTask();
	}

	/**
	 * 进行单个app的流量统计
	 * 
	 */
	protected class CollectAppsTrafficTask implements Runnable {

		private EMMMonitorProcessManager emmMonitorProcessManager;

		public CollectAppsTrafficTask() {
			this.emmMonitorProcessManager = EMMMonitorProcessManager.this;
		}

		@Override
		public void run() {
			L.i(this.getClass(), "CollectAppsTrafficTask()...   start ");

			try {

				List<UidTraffic> uidInfosTraffics = TrafficStatisticsCollector
						.getUidInfosTraffics(getEmmMonitorService()
								.getApplication());
				for (Iterator<UidTraffic> iterator = uidInfosTraffics
						.iterator(); iterator.hasNext();) {
					UidTraffic uidTraffic = iterator.next();

					if (uidTraffic != null) {
						trafficAppsOptDao.insertTrafficAppsData(uidTraffic);
					}
				}
				L.i(this.getClass(), "CollectAppsTrafficTask()...   end ");
			} catch (Exception e) {
				e.printStackTrace();
			}
			emmMonitorProcessManager.runTask();
		}
	}

	/**
	 * 进行设备流量信息收集
	 * 
	 */
	protected class CollectSysTrafficTask implements Runnable {

		private EMMMonitorProcessManager emmMonitorProcessManager;

		public CollectSysTrafficTask() {
			this.emmMonitorProcessManager = EMMMonitorProcessManager.this;
		}

		@Override
		public void run() {
			L.i(this.getClass(), "CollectSysTrafficTask()... start ");
			SysTraffic sysTraffics = TrafficStatisticsCollector
					.getSysTraffics(emmMonitorService);
			if (sysTraffics != null) {
				trafficOptDao.insertTrafficSysData(sysTraffics);
				L.i(this.getClass(), "CollectSysTrafficTask()... end ");
			}
			emmMonitorProcessManager.runTask();
		}
	}

	/**
	 * 
	 * 监控当前使用的数据流量是否超过了限制的流量大小，如果超过则直接关闭。
	 * 
	 */
	protected class MonitorDeviceTrafficTask implements Runnable {

		private EMMMonitorProcessManager emmMonitorProcessManager;

		public MonitorDeviceTrafficTask() {
			this.emmMonitorProcessManager = EMMMonitorProcessManager.this;
		}

		@Override
		public void run() {
			L.i(this.getClass(), "MonitorDeviceTrafficTask()... start ");
			String usedTotalTrafficToCurrentDay = trafficOptDao
					.getUsedTotalTrafficToCurrentDay();
			String limitDataTrafficStr = EMMPreferences.getTrafficInfoLimit(getEmmMonitorService()
							.getApplication());
			if (TextUtils.isEmpty(usedTotalTrafficToCurrentDay)
					|| usedTotalTrafficToCurrentDay.equalsIgnoreCase("null")) {
				L.i(this.getClass(),
						"usedTotalTrafficToCurrentDay is empty .. end ");
				emmMonitorProcessManager.runTask();
				return;
			}
			if (TextUtils.isEmpty(limitDataTrafficStr)
					|| limitDataTrafficStr.equalsIgnoreCase("null")) {
				L.i(this.getClass(), "limitDataTrafficStr is empty .. end ");
				emmMonitorProcessManager.runTask();
				return;
			}
			long usedDataTraffic = Long.valueOf(usedTotalTrafficToCurrentDay);
			long limitDataTraffic = Long.valueOf(limitDataTrafficStr);
			if (usedDataTraffic >= limitDataTraffic) {
				L.i(this.getClass(), "current data traffic is overflow .. ");
				networkControlTool.shutdownDataNetwork();
				L.i(this.getClass(), "traffic is shut down  complete  ");
				emmMonitorProcessManager.runTask();
				return;
			}
			L.i(this.getClass(), "MonitorDeviceTrafficTask()... end ");
			emmMonitorProcessManager.runTask();
		}
	}

	/**
	 * 进行流量收集的类.
	 */
	protected static class TrafficStatisticsCollector {

		/**
		 * 收集系统的所有流量信息
		 * 
		 * @param context
		 * @return
		 */
		public static SysTraffic getSysTraffics(Context context) {
			TrafficTools trafficTools = new TrafficTools();
			SysTraffic sysTraffic = new SysTraffic();
			long td = trafficTools.getTotalDataTraffic();
			long tw = trafficTools.getTotalWlanTraffic();
			long tTime = System.currentTimeMillis();

			if (td <= 0 && tw <= 0) {
				return null;
			}
			Calendar calendar = Calendar.getInstance();
			int tday = calendar.get(Calendar.DAY_OF_MONTH);
			int tmonth = calendar.get(Calendar.MONTH) + 1;
			int tyear = calendar.get(Calendar.YEAR);
			// //1. 进行日期判断, 读取关机的那天的日期数值,进行查询关机那天的最后一条流量信息的值.进行与当天的流量信息进行累加.
			String hasShutdown = TextFormater.isHasShutdown(context);
			// / 如果没有关机则
			if (hasShutdown == null || hasShutdown.equalsIgnoreCase("")
					|| hasShutdown.equalsIgnoreCase("1")) {
				sysTraffic.setTd(td);
				sysTraffic.setTw(tw);
			} else {
				int lastTday = getSplitTday(hasShutdown);
				int lastTmonth = getSplitTmonth(hasShutdown);
				TrafficOptDao trafficOptDao = new TrafficOptDao(context);
				SysTraffic specificSysTrafficData = trafficOptDao
						.getSpecificSysTrafficDataByDay(lastTday, lastTmonth,
								tyear);
				if (specificSysTrafficData != null) {
					sysTraffic.setTd(Long.valueOf(specificSysTrafficData
							.getTd()) + td);
					sysTraffic.setTw(Long.valueOf(specificSysTrafficData
							.getTw()) + tw);
				}
			}
			sysTraffic.setTday(tday);
			sysTraffic.setTmonth(tmonth);
			sysTraffic.settTime(tTime);
			sysTraffic.setTyear(tyear);
			// /使用的数据流量无法统计
			sysTraffic.setUtd(td);
			return sysTraffic;
		}

		/**
		 * 收集所有有数据流量使用量的应用列表以及各自的流量大小 除系统应用外
		 * 
		 * @param context
		 * @return
		 */
		public static List<UidTraffic> getUidInfosTraffics(Context context) {
			List<UidTraffic> uidTraffics = new ArrayList<UidTraffic>();
			PackageManager pm = context.getPackageManager();
			List<PackageInfo> pakageinfos = pm
					.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
			TrafficTools trafficTools = new TrafficTools();
			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int month = calendar.get(Calendar.MONTH) + 1;

			for (PackageInfo packageInfo : pakageinfos) {
				if (!filterApp(packageInfo.applicationInfo)) {
					continue;
				}
				long received = trafficTools
						.getSingleUidReceivedData(packageInfo.applicationInfo.uid);
				long transmitted = trafficTools
						.getSingleUidTransmittedData(packageInfo.applicationInfo.uid);
				// / 如果没有产生流量则不统计
				if (received <= 0 || transmitted <= 0) {
					continue;
				}
				// //1. 进行日期判断, 读取关机的那天的日期数值,进行查询关机那天的最后一条流量信息的值.进行与当天的流量信息进行累加.
				String hasShutdown = TextFormater.isHasShutdown(context);
				// / 如果没有关机则
				UidTraffic uidTraffic = new UidTraffic();
				uidTraffic.setUid(packageInfo.applicationInfo.uid);
				uidTraffic
						.setPackageName(packageInfo.applicationInfo.packageName);
				uidTraffic.setAppName(String.valueOf(pm
						.getApplicationLabel(packageInfo.applicationInfo)));
				if (hasShutdown.equalsIgnoreCase("")
						|| hasShutdown.equalsIgnoreCase("1")) {
					uidTraffic.setReceived_total(received);
					uidTraffic.setUploaded_total(transmitted);
				} else {
					TrafficAppsOptDao trafficAppsOptDao = new TrafficAppsOptDao(
							context);
					int lastTday = getSplitTday(hasShutdown);
					int lastTmonth = getSplitTmonth(hasShutdown);
					UidTraffic lastappTrafficdata = trafficAppsOptDao
							.getSpecificAppDataByDaypackagename(lastTday,
									lastTmonth, year,
									packageInfo.applicationInfo.packageName);
					if (lastappTrafficdata != null) {
						uidTraffic.setReceived_total(received
								+ lastappTrafficdata.getReceived_total());// 上次关机时的流量信息
																			// +
																			// 这次的统计流量信息
						uidTraffic.setUploaded_total(transmitted
								+ lastappTrafficdata.getUploaded_total());
					}
				}
				uidTraffic.setTDAY(day);
				uidTraffic.setTHOUR(hour);
				uidTraffic.setTMONTH(month);
				uidTraffic.setTYEAR(year);
				uidTraffic.setTTIME(calendar.getTimeInMillis());
				uidTraffics.add(uidTraffic);
			}
			return uidTraffics;
		}

		/**
		 * 截取月份值
		 * 
		 * @param hasShutdown
		 * @return
		 */
		private static int getSplitTmonth(String hasShutdown) {
			return Integer.valueOf(hasShutdown.substring(
					hasShutdown.length() - 4, hasShutdown.length() - 2));
		}

		/**
		 * 截取日期值
		 * 
		 * @param hasShutdown
		 * @return
		 */
		private static int getSplitTday(String hasShutdown) {
			return Integer.valueOf(hasShutdown.substring(
					hasShutdown.length() - 2, hasShutdown.length()));
		}

		/**
		 * 三方应用程序的过滤器
		 * 
		 * @param info
		 * @return true 三方应用 false 系统应用
		 */
		public static boolean filterApp(ApplicationInfo info) {
			if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
				// 代表的是系统的应用,但是被用户升级了. 用户应用
				return true;
			} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				// 代表的用户的应用
				return true;
			}
			return false;
		}

	}

	/**
	 * 
	 * 
	 * 同步设备流量信息 同步策略:指令同步, 网络切换同步.
	 * 
	 */
	protected class SyncDeviceTrafficTask implements Runnable {
		private EMMMonitorProcessManager emmMonitorProcessManager;

		public SyncDeviceTrafficTask() {
			this.emmMonitorProcessManager = EMMMonitorProcessManager.this;
		}

		@Override
		public void run() {
			if (!(new NetUtil(getEmmMonitorService().getApplication())
					.getConnectState() == NetWorkState.NONE)) {

				SysTraffic trafficSingleSysDataByDay = trafficOptDao
						.getTrafficSingleSysDataByDay(Calendar.getInstance());
				if (trafficSingleSysDataByDay != null) {
					String requrl = EMMContants.SYSCONF.SYNC_DEVICE_TRAFFIC;
					String params = EMMReqParamsUtils.getSyncDeviceTrafficStr(
							emmMonitorService.getApplication(),
							trafficSingleSysDataByDay);
					String returnData = HttpClientUtil.returnData(requrl,
							params);
					L.d(this.getClass(), returnData);
				} else {
					L.d(this.getClass(), "trafficSingleSysDataByDay");
				}
				// //
			}
			emmMonitorProcessManager.runTask();
		}

	}

	/**
	 * 同步Apps流量信息 同步策略:指令同步, 网络切换同步.
	 */
	protected class SyncAppsTrafficTask implements Runnable {

		private EMMMonitorProcessManager emmMonitorProcessManager;

		public SyncAppsTrafficTask() {
			this.emmMonitorProcessManager = EMMMonitorProcessManager.this;
		}

		@Override
		public void run() {
			if (!(new NetUtil(getEmmMonitorService().getApplication())
					.getConnectState() == NetWorkState.NONE)) {
				Calendar calendar = Calendar.getInstance();
				int tday = calendar.get(Calendar.DAY_OF_MONTH);
				int tmonth = calendar.get(Calendar.MONTH) + 1;
				int tyear = calendar.get(Calendar.YEAR);
				// L.d(this.getClass(), "SyncAppsTrafficTask " + tday +
				// " tmonth "
				// + tmonth + "tyear " + tyear);
				List<UidTraffic> hasTrafficAppsByDay = trafficAppsOptDao
						.getHasTrafficAppsByDay(tday, tmonth, tyear);
				if (hasTrafficAppsByDay != null) {

					String requrl = EMMContants.SYSCONF.SYNC_APPSTRAFFIC;
					String params = EMMReqParamsUtils.getSyncAppsTrafficStr(
							emmMonitorService.getApplication(),
							hasTrafficAppsByDay);
					String returnData = HttpClientUtil.returnData(requrl,
							params);
					L.d(this.getClass(), returnData);
				} else {
					L.d(this.getClass(), "hasTrafficAppsByDay");
				}
			}
			emmMonitorProcessManager.runTask();
		}

	}

	public void runTask() {
		L.d(this.getClass(), "runTask()...");
		synchronized (taskList) {
			running = false;
			futureTask = null;
			if (!taskList.isEmpty()) {
				Runnable runnable = (Runnable) taskList.get(0);
				taskList.remove(0);
				running = true;
				futureTask = taskSubmitter.submit(runnable);
				if (futureTask == null) {
					taskTracker.decrease();
				}
			}
		}
		taskTracker.decrease();
		L.d(this.getClass(), "runTask()...done");
	}

	private void addTask(Runnable runnable) {
		L.d(this.getClass(), "addTask(runnable)...");
		taskTracker.increase();
		synchronized (taskList) {
			if (taskList.isEmpty() && !running) {
				running = true;
				futureTask = taskSubmitter.submit(runnable);
				if (futureTask == null) {
					taskTracker.decrease();
				}
			} else {
				taskList.add(runnable);
			}
		}
		L.d(this.getClass(), "addTask(runnable)... done");
	}

	/**
	 * @return the emmMonitorService
	 */
	public EMMMonitorService getEmmMonitorService() {
		return emmMonitorService;
	}

	/**
	 * @param emmMonitorService
	 *            the emmMonitorService to set
	 */
	public void setEmmMonitorService(EMMMonitorService emmMonitorService) {
		this.emmMonitorService = emmMonitorService;
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

}
