package com.foxconn.emm.tools;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;

public class NetWorkTools {
	private static final String TAG = "NetWorkTools";

	/**
	 * 检测网络是否可用
	 * 
	 * @param context
	 *            上下文
	 * @return true 表示有网络连接 false表示没有可用网络连接
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						if (info[i].getType() == ConnectivityManager.TYPE_WIFI) {
							Log.d(TAG, "WiFi网络连接正常");
						}
						if (info[i].getType() == ConnectivityManager.TYPE_MOBILE) {
							Log.d(TAG, "3G网络连接正常");
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 测试ConnectivityManager ConnectivityManager主要管理和网络连接相关的操作
	 * 相关的TelephonyManager则管理和手机、运营商等的相关信息；WifiManager则管理和wifi相关的信息。
	 * 想访问网络状态，首先得添加权限<uses-permission
	 * android:name="android.permission.ACCESS_NETWORK_STATE"/>
	 * NetworkInfo类包含了对wifi和mobile两种网络模式连接的详细描述,通过其getState()方法获取的State对象则代表着
	 * 连接成功与否等状态。
	 * 
	 */
	public void testConnectivityManager(Context context) {

		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 获取代表联网状态的NetWorkInfo对象
		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		// 获取当前的网络连接是否可用
		boolean available = networkInfo.isAvailable();
		if (available) {
			Log.i("通知", "当前的网络连接可用");
		} else {
			Log.i("通知", "当前的网络连接不可用");
		}
		if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			// 判断WIFI网
		} else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			// 判断3G网
		}
		State state = connManager.getNetworkInfo(
				ConnectivityManager.TYPE_MOBILE).getState();
		if (State.CONNECTED == state) {
			Log.i("通知", "GPRS网络已连接");
		}

		state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (State.CONNECTED == state) {
			Log.i("通知", "WIFI网络已连接");
		}

		// 跳转到网络设置界面
		// startActivity(new
		// Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
		// 跳转到wifi网络设置界面
		// startActivity(new
		// Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));

	}

	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	// // 获取本地IP
	public static String getIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& inetAddress instanceof Inet4Address) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * WifiManager wifi_service = (WifiManager)getSystemService(WIFI_SERVICE);
	 * WifiInfo wifiInfo = wifi_service.getConnectionInfo(); 其中wifiInfo有以下的方法：
	 * wifiinfo.getBSSID()； wifiinfo.getSSID()； wifiinfo.getIpAddress()；获取IP地址。
	 * wifiinfo.getMacAddress()；获取MAC地址。 wifiinfo.getNetworkId()；获取网络ID。
	 * wifiinfo.getLinkSpeed()；获取连接速度，可以让用户获知这一信息。
	 * wifiinfo.getRssi()；获取RSSI，RSSI就是接受信号强度指示。在这可以直
	 * 接和华为提供的Wi-Fi信号阈值进行比较来提供给用户， 让用户对网络或地理位置做出调整来获得最好的连接效果。
	 * 这里得到信号强度就靠wifiinfo.getRssi()；这个方法。
	 */

	private static String intToIp(int ip) {
		return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "."
				+ ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);
	}
}
