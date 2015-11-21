package com.foxconn.emm.service;

import java.util.HashMap;
import java.util.Map;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.util.Log;

import com.foxconn.app.R;
import com.foxconn.app.aty.AtyInfoCenter;
import com.foxconn.emm.utils.EMMContants;

public abstract class EMMBaseIntentService extends IntentService {
	// Java lock used to synchronize access to sWakelock
	private static final Object LOCK = EMMBaseIntentService.class;

	public static  final String NOTIFICATION_FLAG_MSGTYPE = "INTENT_EXTRA_MsgType";
	
	
	private static PowerManager.WakeLock sWakeLock;

	// wakelock
	private static final String WAKELOCK_KEY = "EMMBaseIntentService";

	private static final String TAG = "EMMBaseIntentService";
	// private static final String APP_NAME = "EMM";
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

	public EMMBaseIntentService(String name) {
		super(name);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initNotification();
	}

	protected void initNotification() {
		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		addNotificationMGR();
	}

	private void addNotificationMGR() {
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// /// 需要进行消息提醒Activity 替换...
		mNotificationIntent = new Intent(this, AtyInfoCenter.class);
	}

	/**
	 * Called when a cloud message has been received.
	 * 
	 * @param context
	 *            application's context.
	 * @param intent
	 *            intent containing the message payload as extras.
	 */
	protected abstract void onMessage(Context context, Intent intent) throws Exception ;


	/**
	 * 
	 * @param context
	 * @param intent
	 */
	protected abstract void onPreMessage(Context context, Intent intent);

	/**
	 * 
	 * @param context
	 * @param intent
	 */
	protected abstract void onPostedMessage(Context context, Intent intent);

	
	
	@Override
	protected void onHandleIntent(Intent intent) {

		Context context = null;

		try {
			context = getApplicationContext();
			String action = intent.getAction();
			// / 发送到的消息进行处理
			if (action.equals(EMMContants.XMPPCONF.ACTION_PROCESS_NOTIFICATION)) {
				onPreMessage(context, intent);
				onMessage(context, intent);
			}
		} catch (Exception e) {
//			if (context != null) {
//				onError(context, e.getMessage());
//			}
		} finally {
			onPostedMessage(context, intent);
			
			// Release the power lock, so phone can get back to sleep.
			// The lock is reference-counted by default, so multiple
			// messages are ok.

			// If onMessage() needs to spawn a thread or do something else,
			// it should use its own lock.
			synchronized (LOCK) {
				// sanity check for null as this is a public method
				if (sWakeLock != null) {
					sWakeLock.release();
				} else {
					// should never happen during normal workflow
					Log.e(TAG, "Wakelock reference is null");
				}

			}
		}
	}

	/**
	 * Called on received  or handling  error.
	 * 
	 * @param context
	 *            application's context.
	 * @param errorstr
	 *            error id returned by the GCM service.
	 */
	protected abstract void onError(Context context, String errorstr);

	/**
	 * Called after a device has been registered.
	 * 
	 * @param context
	 *            application's context.
	 * @param registrationId
	 *            the registration id returned by the GCM service.
	 */
	protected abstract void onRegistered(Context context, String registrationId);

	/**
	 * Called from the broadcast receiver.
	 * <p>
	 * Will process the received intent, call handleMessage(), registered(),
	 * etc. in background threads, with a wake lock, while keeping the service
	 * alive.
	 */
	public static void runIntentInService(Context context, Intent intent,
			String className) {
		synchronized (LOCK) {
			if (sWakeLock == null) {
				// This is called from BroadcastReceiver, there is no init.
				PowerManager pm = (PowerManager) context
						.getSystemService(Context.POWER_SERVICE);
				sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
						WAKELOCK_KEY);
			}
			sWakeLock.acquire();
		}
		intent.setClassName(context, className);
		context.startService(intent);
	}

	/**
	 * 
	 * 消息提醒
	 * 
	 * @param fromId
	 * @param fromMsgType
	 * @param message
	 * @param showNotification
	 */
	protected void notifyClient(String fromId, String fromMsgType,
			String message, boolean showNotification) {
		if (!showNotification) {
			return;
		}
		setNotification(fromId, fromMsgType, message);
		setLEDNotification();

		int notifyId = 0;
		if (mNotificationId.containsKey(fromId)) {
			notifyId = mNotificationId.get(fromId);
		} else {
			mLastNotificationId++;
			notifyId = mLastNotificationId;
			mNotificationId.put(fromId, Integer.valueOf(notifyId));
		}

		// If vibration is set to true, add the vibration flag to
		// the notification and let the system decide.
		// boolean vibraNotify = PreferenceUtils.getPrefBoolean(this,
		// PreferenceConstants.VIBRATIONNOTIFY, true);

		boolean vibraNotify = true;

		if (vibraNotify) {
			mVibrator.vibrate(new long[] { 100L,200L,100L,200L}, -1);
		}
		mNotificationManager.notify(notifyId, mNotification);
	}

	private void setNotification(String fromMsgtype, String fromMsgTitle,
			String message) {

		int mNotificationCounter = 0;
		if (mNotificationCount.containsKey(fromMsgtype)) {
			mNotificationCounter = mNotificationCount.get(fromMsgtype);
		}
		mNotificationCounter++;
		mNotificationCount.put(fromMsgtype, mNotificationCounter);
		String author;
		if (null == fromMsgTitle || fromMsgTitle.length() == 0) {
			author = fromMsgtype;
		} else {
			author = fromMsgTitle;
		}
		String title = author;
		String ticker;
		// boolean isTicker = PreferenceUtils.getPrefBoolean(this,
		// PreferenceConstants.TICKER, true);
		boolean isTicker = true;
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
		mNotificationIntent.putExtra(NOTIFICATION_FLAG_MSGTYPE, fromMsgtype);
		mNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// need to set flag FLAG_UPDATE_CURRENT to get extras transferred
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				mNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mNotification.setLatestEventInfo(this, title, message, pendingIntent);
		if (mNotificationCounter > 1)
			mNotification.number = mNotificationCounter;
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
	}

	private void setLEDNotification() {
		// boolean isLEDNotify = PreferenceUtils.getPrefBoolean(this,
		// PreferenceConstants.LEDNOTIFY, true);
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
