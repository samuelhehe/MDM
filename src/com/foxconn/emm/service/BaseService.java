package com.foxconn.emm.service;

import java.util.HashMap;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;

import com.foxconn.app.R;
import com.foxconn.app.aty.AtyUpdate;
import com.foxconn.emm.bean.UpdateInfo;
import com.foxconn.emm.utils.L;

public abstract class BaseService extends Service {

	private static final String TAG = "BaseService";
	private static final String APP_NAME = "EMM3.0";
	private static final int MAX_TICKER_MSG_LEN = 50;
	protected static int SERVICE_NOTIFICATION = 1;

	private NotificationManager mNotificationManager;
	private Notification mNotification;
	private Intent mNotificationIntent;
	private Vibrator mVibrator;
	protected WakeLock mWakeLock;

	private Map<String, Integer> mNotificationCount = new HashMap<String, Integer>(2);
	private Map<String, Integer> mNotificationId = new HashMap<String, Integer>(2);
	private int mLastNotificationId = 2;

	@Override
	public IBinder onBind(Intent arg0) {
		L.i(TAG, "called onBind()");
		return null;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		L.i(TAG, "called onUnbind()");
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		L.i(TAG, "called onRebind()");
		super.onRebind(intent);
	}

	@Override
	public void onCreate() {
		L.i(TAG, "called onCreate()");
		super.onCreate();
		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		mWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
				.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, APP_NAME);
		addNotificationMGR();
	}

	@Override
	public void onDestroy() {
		L.i(TAG, "called onDestroy()");
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		L.i(TAG, "called onStartCommand()");
		return START_STICKY;
	}

	private void addNotificationMGR() {
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotificationIntent = new Intent(this,AtyUpdate.class);
	}
	protected void notifyUpdateInfo(String fromJid, String fromUserName,
			String message, boolean showNotification, UpdateInfo updateInfo) {
		if (!showNotification) {
			return;
		}
		mWakeLock.acquire();
		setNotification(fromJid, fromUserName, message, updateInfo);
		setLEDNotification();

		int notifyId = 0;
		if (mNotificationId.containsKey(fromJid)) {
			notifyId = mNotificationId.get(fromJid);
		} else {
			mLastNotificationId++;
			notifyId = mLastNotificationId;
			mNotificationId.put(fromJid, Integer.valueOf(notifyId));
		}
		boolean vibraNotify = true ;
//		boolean vibraNotify = PreferenceUtils.getPrefBoolean(this,
//				PreferenceConstants.VIBRATIONNOTIFY, true);
		if (vibraNotify) {
			mVibrator.vibrate(new long[] { 100L,200L,100L,200L}, -1);
		}
		mNotificationManager.notify(notifyId, mNotification);

		mWakeLock.release();
	}

	private void setNotification(String fromJid, String fromUserId,
			String message, UpdateInfo updateInfo) {

		int mNotificationCounter = 0;
		if (mNotificationCount.containsKey(fromJid)) {
			mNotificationCounter = mNotificationCount.get(fromJid);
		}
		mNotificationCounter++;
		mNotificationCount.put(fromJid, mNotificationCounter);
		String author;
		if (null == fromUserId || fromUserId.length() == 0) {
			author = fromJid;
		} else {
			author = fromUserId;
		}
		String title = author;
		String ticker;
		boolean isTicker = true;
//		boolean isTicker = PreferenceUtils.getPrefBoolean(this,
//				PreferenceConstants.TICKER, true);
		if (isTicker) {
			int newline = message.indexOf('\n');
			int limit = 0;
			String messageSummary = message;
			if (newline >= 0)
				limit = newline;
			if (limit > MAX_TICKER_MSG_LEN
					|| message.length() > MAX_TICKER_MSG_LEN)
				limit = MAX_TICKER_MSG_LEN;
			if (limit > 0)
				messageSummary = message.substring(0, limit) + " [...]";
			ticker = title + ":\n" + messageSummary;
		} else
			ticker = author;
		mNotification = new Notification(R.drawable.emm_notification_icon, ticker,
				System.currentTimeMillis());
		
		
		Uri userNameUri = Uri.parse(fromJid);
		mNotificationIntent.setData(userNameUri);
		mNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		mNotificationIntent.putExtra(UpdateInfo.TAG.UPDATEINFO,updateInfo);
		// need to set flag FLAG_UPDATE_CURRENT to get extras transferred
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				mNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mNotification.setLatestEventInfo(this, title, message, pendingIntent);
		if (mNotificationCounter > 1)
			mNotification.number = mNotificationCounter;
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
	}

	private void setLEDNotification() {
//		boolean isLEDNotify = PreferenceUtils.getPrefBoolean(this,
//				PreferenceConstants.LEDNOTIFY, true);
		boolean isLEDNotify = true;
		if (isLEDNotify) {
			mNotification.ledARGB = Color.MAGENTA;
			mNotification.ledOnMS = 300;
			mNotification.ledOffMS = 1000;
			mNotification.flags |= Notification.FLAG_SHOW_LIGHTS;
		}
	}

	public void resetNotificationCounter(String userJid) {
		mNotificationCount.remove(userJid);
	}

	public void clearNotification(String Jid) {
		int notifyId = 0;
		if (mNotificationId.containsKey(Jid)) {
			notifyId = mNotificationId.get(Jid);
			mNotificationManager.cancel(notifyId);
		}
	}

}
