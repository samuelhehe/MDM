package com.foxconn.app;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MyLocationData;
import com.foxconn.app.aty.AtyUpdate;
import com.foxconn.emm.bean.UpdateInfo;
import com.foxconn.emm.lock.LockPatternUtils;
import com.foxconn.emm.receiver.EMMBroadcastReceiver;
import com.foxconn.emm.receiver.EMMBroadcastReceiver.EventHandler;
import com.foxconn.emm.tools.CrashHandler;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.utils.NetUtil;
import com.foxconn.emm.utils.NetUtil.NetWorkState;
import com.foxconn.emm.utils.VersionUtil;
import com.foxconn.lib.download.DownFileDao;
import com.foxconn.lib.network.control.Api;

public class App extends Application implements EventHandler {
	private static App mInstance;
	private LockPatternUtils mLockPatternUtils;
	private static final int SHOWREQID = 250;
	private LocationClient mLocClient;

	private DownFileDao mDownFileDao = null;
	private EMMLocationListenner myListener = new EMMLocationListenner();

	private NotificationManager mNotificationManager;

	private onLocationReceiveListener locationReceiveListener;
	private ActivityManager activitymgr;

	@Override
	public void onCreate() {
		super.onCreate();
		SDKInitializer.initialize(this);
		Api.setEnabled(this, true);
		activitymgr = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		setmDownFileDao(DownFileDao.getInstance(this));
		mInstance = this;
		mLockPatternUtils = new LockPatternUtils(this);
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		startLoc();
		EMMBroadcastReceiver.mListeners.add(this);
	}

	public LockPatternUtils getLockPatternUtils() {
		return mLockPatternUtils;
	}

	private List<Activity> mList = new LinkedList<Activity>();
	private Notification mNotification;
	public double mLatitude;
	public double mLongitude;
	public BDLocation bdLocation;

	public synchronized static App getInstance() {
		if (null == mInstance) {
			mInstance = new App();
		}
		return mInstance;
	}

	// add Activity
	public void addActivity(Activity activity) {
		mList.add(activity);
	}

	public void exit() {
		try {
			for (Activity activity : mList) {
				if (activity != null)
					activity.finish();
			}
//			resetIsCurrentProcessPause();
			EMMPreferences.setSecurityNeedAuthorized(this,true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mLocClient != null) {
				mLocClient.stop();
			}
			System.exit(0);
		}
	}
	protected void resetIsCurrentProcessPause() {
		/**
		 * 如果是当前进程,则不需要进行验证.否则需要进行安全验证.
		 */
		if (isCurrentProcessPause()) {
			EMMPreferences.setSecurityNeedAuthorized(this,false);
		} else {
			EMMPreferences.setSecurityNeedAuthorized(this,true);
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
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}
	

	@SuppressWarnings("deprecation")
	public void showNotification(UpdateInfo updateInfo) {

		// Notification
		mNotification = new Notification();
		// mNotification.flags = Notification.FLAG_ONGOING_EVENT;

		mNotification.icon = R.drawable.icon;
		mNotification.defaults = Notification.DEFAULT_LIGHTS;
		// mNotification.contentView = Notification.notification;

		// mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

		try {
			mNotification.sound = Uri
					.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
							+ VersionUtil.getVersionInfo(this).packageName
							+ "/raw/office");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		mNotification.when = System.currentTimeMillis();
		// mNotification.tickerText = "AppMarket客户端有可用更新";
		// Intent intent = new Intent(this, MainActivity.class);

		Intent intent = new Intent(this, AtyUpdate.class);
		intent.putExtra(UpdateInfo.TAG.UPDATEINFO, updateInfo);

		PendingIntent contentIntent = PendingIntent.getActivity(this,
				SHOWREQID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		mNotification.setLatestEventInfo(this, "升级提醒", "EMM客户端有可用更新",
				contentIntent);
		// 指定内容意图
		// mNotification.contentIntent = contentIntent;
		mNotificationManager.notify(SHOWREQID, mNotification);
	}

	/**
	 * 初始化地图相关
	 */
	public void startLoc() {
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setPriority(LocationClientOption.GpsFirst);// 设置网络优先(不设置，默认是gps优先)
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setScanSpan(15 * 1000);// 设置发起定位请求的间隔时间为10s(小于1秒则一次定位)
		mLocClient.setLocOption(option);
		mLocClient.start();
		L.i(this.getClass(), "start location engine ... ");
	}

	public void stopLoc() {

		// / 如果不为空, 坐标的值也不为空, 回调Location更新接口
		if (mLocClient != null) {
			if (mLatitude > 0 && mLongitude > 0) {
				/**
				 * Latitude,Longitude
				 */
				EMMPreferences.setDEVICELocation(this, "" + mLatitude + ","
						+ mLongitude);
				if (mLocClient.isStarted()) {
					mLocClient.stop();
					L.i(this.getClass(), "stop location engine ... ");
				}
			}
		}
	}

	/**
	 * 获取当前的设备地址信息,如果不为空则直接返回设备地址信息, 否则启动设备定位Service进行定位.返回null
	 * 
	 * @return
	 */
	public BDLocation getCurrentLocation() {

		if (bdLocation != null) {
			return bdLocation;
		} else {
			startLoc();
			return null;
		}
	}

	public onLocationReceiveListener getLocationReceiveListener() {
		return locationReceiveListener;
	}

	public void setLocationReceiveListener(
			onLocationReceiveListener locationReceiveListener) {
		this.locationReceiveListener = locationReceiveListener;
	}

	public DownFileDao getmDownFileDao() {
		return mDownFileDao;
	}

	public void setmDownFileDao(DownFileDao mDownFileDao) {
		this.mDownFileDao = mDownFileDao;
	}

	/**
	 * 定位功能在EMM中应用有两种模式, 1.直接获取设备的位置信息,用于信息统计等功能 2.用于设备位置的追踪, 用于设备查找功能 实现思路:
	 * 可以定时进行更新位置,如果是前者,获取到位置信息后直接stop, 如果属于设备位置跟踪则需要进行连续更新位置信息.
	 * 
	 */
	public interface onLocationReceiveListener {

		/**
		 * 当设备接收到最新的位置信息 当设备的最新位置信息改变时
		 * 
		 * @param bdLocation
		 */
		public void onReceiveLocation(BDLocation bdLocation);
	}

	/**
	 * 定位SDK监听函数
	 */
	public class EMMLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null)
				return;

			bdLocation = location;
			// 此处设置开发者获取到的方向信息，顺时针0-360
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius()).direction(100)
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			// float speed = locData.speed;
			// float direction = locData.direction;
			//
			// int satellitesNum = locData.satellitesNum;
			// Log.i("direction: " + direction, "speed: " + speed
			// + "satellitesNum: " + satellitesNum);

			String address = location.getAddrStr();

			if (!TextUtils.isEmpty(address)) {
				EMMPreferences.setDEVICELocationStreet(App.this, address);
			}
			mLatitude = location.getLatitude();
			mLongitude = location.getLongitude();
//			 L.i(this.getClass(), mLatitude + "==" + mLongitude);
//			 L.i(this.getClass(), "getProvince" + "==" +
//			 location.getProvince());
//			 L.i(this.getClass(), "getCity" + "==" + location.getCity());
//			 L.i(this.getClass(), "getRadius" + "==" + location.getRadius());
//			 L.i(this.getClass(), "getStreet" + "==" + location.getStreet());
//			 L.i(this.getClass(), "getCityCode" + "==" +
//			 location.getCityCode());

			if (locationReceiveListener != null) {
				locationReceiveListener.onReceiveLocation(location);
			}
			// / 获取停止定位.
			stopLoc();

		}

		@Override
		public void onReceivePoi(BDLocation bdLocation) {

		}

	}

	/**
	 * 
	 * 当网络变化,并且网络连接成功. 则进行位置更新
	 * 可以在这里进行网络切换.
	 * 
	 */
	@Override
	public void onNetChange() {

		/**
		 * 在公司内部WiFi 网络环境一般连接不上外网, 无论是WiFi还是手机数据网络
		 */
		if (new NetUtil(getInstance()).getConnectState()!=NetWorkState.NONE) {
			startLoc();
			return;
		}
//		new PolicyControl(getInstance()).enableCurrentPolicy();
	}

}