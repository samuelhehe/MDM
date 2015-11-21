package com.foxconn.emm.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class TestNetUtils extends Thread {

	private Context context;

	public TestNetUtils(Context context) {
		this.context = context;
	}

	private static final String innerUrl = "http://zzhr.efoxconn.com";

	public void run() {
		try {
			URL url = new URL(innerUrl);
			System.out.println(url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestProperty(
					"Accept",
					"image/gif, image/jpeg, image/pjpeg, image/pjpeg,application/x-shockwave-flash, application/xaml+xml,application/vnd.ms-xpsdocument, application/x-ms-xbap,application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword");
			conn.setRequestProperty("Charset", "UTF-8");
			int code = conn.getResponseCode();
			System.out.println("TestNet===code" + code);
			// if (code == 200) {
//			new PolicyControl(context).enablePolicy(CommonUtilities
//					.getCurrentTaskPolicy());
			// } else {
			// new PolicyControl(context).enableDefaultPolicy();
			// }
//			new PolicyControl(context).enablePolicy(TimerJSONPolicy
//					.getNewPolicy());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
//			new PolicyControl(context).enablePolicy(TimerJSONPolicy
//					.getNewPolicy());
		}
	}
	
	public static  boolean checkNetWorkInfo(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
			for (int i = 0; i < networkInfos.length; i++) {
				NetworkInfo.State state = networkInfos[i].getState();
				if (NetworkInfo.State.CONNECTED == state) {
					System.out.println("------------> Network is ok");
					return true;
				}
			}
		}
		return false;

	}
	
};