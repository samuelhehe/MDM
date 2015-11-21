/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foxconn.emm.xmpp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.xmpp.listener.PhoneStateChangeListener;

/**
 * Service that continues to run in background and respond to the push
 * notification events from the server. This should be registered as service in
 * AndroidManifest.xml.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class NotificationService extends Service {


	public static final String SERVICE_NAME = "com.foxconn.emm.xmpp.NotificationService";

	private TelephonyManager telephonyManager;


	private BroadcastReceiver connectivityReceiver;

	private PhoneStateListener phoneStateListener;

	private ExecutorService executorService;

	private TaskSubmitter taskSubmitter;

	private TaskTracker taskTracker;

	private XmppManager xmppManager;

	private SharedPreferences sharedPrefs;


	

	public NotificationService() {
		connectivityReceiver = new ConnectivityReceiver(this);
		phoneStateListener = new PhoneStateChangeListener(this);
		executorService = Executors.newSingleThreadExecutor();
		taskSubmitter = new TaskSubmitter(this);
		taskTracker = new TaskTracker(this);
		
	}

	@Override
	public void onCreate() {
		L.d(NotificationService.class, "onCreate()...");
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		sharedPrefs = getSharedPreferences(EMMContants.XMPPCONF.SHARED_PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		xmppManager = new XmppManager(this);
		// 将xmppManager对象放入全局变量中，方便其他地方使用
//		Constants.xmppManager = xmppManager;
		taskSubmitter.submit(new Runnable() {
			public void run() {
				NotificationService.this.start();
			}
		});
	}

	@Override
	public void onStart(Intent intent, int startId) {
		L.d(NotificationService.class, "onStart()...");
	}

	@Override
	public void onDestroy() {
		L.d(NotificationService.class, "onDestroy()...");
		stop();
	}

	@Override
	public IBinder onBind(Intent intent) {
		L.d(NotificationService.class, "onBind()...");
		return null;
	}

	@Override
	public void onRebind(Intent intent) {
		L.d(NotificationService.class, "onRebind()...");
	}

	@Override
	public boolean onUnbind(Intent intent) {
		L.d(NotificationService.class, "onUnbind()...");
		return true;
	}

	public static Intent getIntent() {
		return new Intent(SERVICE_NAME);
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public TaskSubmitter getTaskSubmitter() {
		return taskSubmitter;
	}

	public TaskTracker getTaskTracker() {
		return taskTracker;
	}

	public XmppManager getXmppManager() {
		return xmppManager;
	}

	public SharedPreferences getSharedPreferences() {
		return sharedPrefs;
	}


	public void connect() {
		L.d(NotificationService.class, "connect()...");
		taskSubmitter.submit(new Runnable() {
			public void run() {
				NotificationService.this.getXmppManager().connect();
			}
		});
	}

	public void disconnect() {
		L.d(NotificationService.class, "disconnect()...");
		taskSubmitter.submit(new Runnable() {
			public void run() {
				NotificationService.this.getXmppManager().disconnect();
			}
		});
	}


	private void registerConnectivityReceiver() {
		L.d(NotificationService.class, "registerConnectivityReceiver()...");
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
		IntentFilter filter = new IntentFilter();
		// filter.addAction(android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(connectivityReceiver, filter);
	}

	private void unregisterConnectivityReceiver() {
		L.d(NotificationService.class, "unregisterConnectivityReceiver()...");
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_NONE);
		unregisterReceiver(connectivityReceiver);
	}

	private void start() {
		L.d(NotificationService.class, "start()...");
		registerConnectivityReceiver();
//		 Intent intent = getIntent();
//		 startService(intent);
		xmppManager.connect();
	}

	private void stop() {
		L.d(NotificationService.class, "stop()...");
		unregisterConnectivityReceiver();
		xmppManager.disconnect();
		executorService.shutdown();
	}

	/**
	 * Class for summiting a new runnable task.
	 */
	public class TaskSubmitter {

		final NotificationService notificationService;

		public TaskSubmitter(NotificationService notificationService) {
			this.notificationService = notificationService;
		}

		@SuppressWarnings("unchecked")
		public Future submit(Runnable task) {
			Future result = null;
			if (!notificationService.getExecutorService().isTerminated()
					&& !notificationService.getExecutorService().isShutdown()
					&& task != null) {
				result = notificationService.getExecutorService().submit(task);
			}
			return result;
		}

	}

	/**
	 * Class for monitoring the running task count.
	 */
	public class TaskTracker {

		final NotificationService notificationService;

		public int count;

		public TaskTracker(NotificationService notificationService) {
			this.notificationService = notificationService;
			this.count = 0;
		}

		public void increase() {
			synchronized (notificationService.getTaskTracker()) {
				notificationService.getTaskTracker().count++;
				L.d(NotificationService.class, "Incremented task count to " + count);
			}
		}

		public void decrease() {
			synchronized (notificationService.getTaskTracker()) {
				notificationService.getTaskTracker().count--;
				L.d(NotificationService.class, "Decremented task count to " + count);
			}
		}

	}



}
