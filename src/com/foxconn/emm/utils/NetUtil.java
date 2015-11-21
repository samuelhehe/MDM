package com.foxconn.emm.utils;



import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class NetUtil {

	private Context mContext;
	public State wifiState = null;
	public State mobileState = null;

	public NetUtil(Context context) {
		mContext = context;
	}

	public enum NetWorkState {
		WIFI, MOBILE, NONE;

	}

	/**
	 * 获取当前的网络状态
	 * 
	 * @return
	 */
	public NetWorkState getConnectState() {
		ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		manager.getActiveNetworkInfo();
		NetworkInfo wifi_networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobile_networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		
		if(wifi_networkInfo!=null){
			wifiState = wifi_networkInfo.getState();
		}
		if(mobile_networkInfo!=null){
			mobileState = mobile_networkInfo.getState();
		}
		if (wifiState != null && mobileState != null&& State.CONNECTED != wifiState&& State.CONNECTED == mobileState) {
			return NetWorkState.MOBILE;
		} else if (wifiState != null && mobileState != null&& State.CONNECTED != wifiState&& State.CONNECTED != mobileState) {
			return NetWorkState.NONE;
		} else if (wifiState != null && State.CONNECTED == wifiState) {
			return NetWorkState.WIFI;
		}
		
		return NetWorkState.NONE;
	}
	
	public static  boolean checkNetWorkInfo(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
			for (int i = 0; i < networkInfos.length; i++) {
				NetworkInfo.State state = networkInfos[i].getState();
				if (NetworkInfo.State.CONNECTED == state) {
					L.i(context.getClass(), networkInfos[i].getTypeName()+" is connected ..");
					return true;
				}
			}
		}
		return false;
	}
	
}
