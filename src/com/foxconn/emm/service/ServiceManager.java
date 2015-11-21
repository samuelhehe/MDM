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
package com.foxconn.emm.service;

import java.util.Properties;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.xmpp.NotificationService;

/**
 * 
 * 1. 加载配置信息 2. 启动NotificaitonService
 * 
 */
public final class ServiceManager {

	private Context context;

	private SharedPreferences sharedPrefs;

	private Properties props;

	private String apiKey;

	private String xmppHost;

	private String xmppPort;

	public ServiceManager(Context context) {
		this.context = context;

		props = loadProperties();
		apiKey = props.getProperty("apiKey", EMMContants.XMPPCONF.XMPP_APIKEY);
		xmppHost = props.getProperty("xmppHost",
				EMMContants.XMPPCONF.XMPP_BASEHOST);
		xmppPort = props.getProperty("xmppPort",String.valueOf(EMMContants.XMPPCONF.XMPP_BASEPORT));

//		L.d(ServiceManager.class,"apiKey=" + apiKey);
//		L.d(ServiceManager.class,"xmppHost=" + xmppHost);
//		L.d(ServiceManager.class,"xmppPort=" + xmppPort);

		sharedPrefs = context.getSharedPreferences(
				EMMContants.XMPPCONF.SHARED_PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		Editor editor = sharedPrefs.edit();
		editor.putString(EMMContants.XMPPCONF.API_KEY, apiKey);
		editor.putString(EMMContants.XMPPCONF.XMPP_HOST, xmppHost);
		editor.putInt(EMMContants.XMPPCONF.XMPP_PORT,
				Integer.parseInt(xmppPort));
		editor.commit();
	}

	public  void checkKeepRunningService(){
		startNotificationService();
		startEMMMonitorService();
	}
	
	/**
	 * 启动NotificationService
	 */
	public void startNotificationService() {
		Thread serviceThread = new Thread(new Runnable() {
			@Override
			public void run() {
				Intent intent = NotificationService.getIntent();
				context.startService(intent);
			}
		});
		serviceThread.start();
	}

	public void stopNotificationService() {
		Intent intent = NotificationService.getIntent();
		context.stopService(intent);
	}

	
	
	/**
	 * 启动EMMMonitorService
	 * 
	 */
	public void startEMMMonitorService() {
		Thread serviceThread = new Thread(new Runnable() {
			@Override
			public void run() {
				Intent intent = EMMMonitorService.getIntent();
				context.startService(intent);
			}
		});
		serviceThread.start();
	}
	
	public void stopEMMMonitorService() {
		Intent intent = EMMMonitorService.getIntent();
		context.stopService(intent);
	}
	
	
	/**
	 * 启动 EMMService
	 * 
	 */
	public void startEMMService() {
		Thread serviceThread = new Thread(new Runnable() {
			@Override
			public void run() {
				Intent intent = EMMService.getIntent();
				context.startService(intent);
			}
		});
		serviceThread.start();
	}
	
	public void stopEMMService() {
		Intent intent = EMMService.getIntent();
		context.stopService(intent);
	}
	
	
	
	private Properties loadProperties() {
		Properties props = new Properties();
		try {
			int id = context.getResources().getIdentifier("androidpn", "raw",
					context.getPackageName());
			props.load(context.getResources().openRawResource(id));
		} catch (Exception e) {
			L.e(ServiceManager.class, "Could not find the properties file.", e);
		}
		return props;
	}

}
