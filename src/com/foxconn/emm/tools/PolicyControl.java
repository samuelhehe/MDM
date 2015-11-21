package com.foxconn.emm.tools;

import java.io.File;

import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.foxconn.emm.bean.PolicyInfo;
import com.foxconn.emm.bean.QRPolicyInfo;
import com.foxconn.emm.db.AppDBControlHelper;
import com.foxconn.emm.receiver.DeviceAdminSampleReceiver;
import com.foxconn.emm.tools.WiFiAutoConnManager.WifiCipherType;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.utils.TextFormater;

public class PolicyControl {

	private Context context;
	private ComponentName mDeviceAdminSample;
	private DevicePolicyManager mDPM;
	private WifiManager wifiManager;
	
	private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

	public static final long MS_PER_MINUTE = 60 * 1000;
	public static final long MS_PER_HOUR = 60 * MS_PER_MINUTE;
	public static final long MS_PER_DAY = 24 * MS_PER_HOUR;

	public PolicyControl(Context context) {
		this.context = context;
		mDPM = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		mDeviceAdminSample = new ComponentName(context,
				DeviceAdminSampleReceiver.class);
		wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
	}

	/**
	 * @param jsonPolicy
	 * @return
	 */
	public String enablePolicy(String jsonPolicy) {
		if (TextUtils.isEmpty(jsonPolicy) || jsonPolicy.contains("null")) {
			PolicyInfo defaultPolicy = new PolicyInfo().getDefaultPolicy();
			return enablePolicy(defaultPolicy);
		}
		if ((jsonPolicy.contains(QRPolicyInfo.TAG.tag_sendnote))
				&& (jsonPolicy.contains(QRPolicyInfo.TAG.tag_itemFlag))) {
			PolicyInfo qrPolicyInfo = new PolicyInfo()
					.getPolicyInfoFromQRJson(jsonPolicy);
			if (qrPolicyInfo != null) {
				return enableQRPolicy(qrPolicyInfo);
			} else {
				return enableDefaultPolicy();
			}
		} else {
			PolicyInfo policyInfo = new PolicyInfo()
					.getPolicyInfoFromJson(jsonPolicy);
			if (policyInfo != null) {
				return enablePolicy(policyInfo);
			} else {
				PolicyInfo defaultPolicy = new PolicyInfo().getDefaultPolicy();
				return enablePolicy(defaultPolicy);
			}
		}
	}

	public String enableCurrentPolicy() {
		String currentJsonPolicy = EMMPreferences.getPolicyData(context);
		return enablePolicy(currentJsonPolicy);
	}
	/**
	 * 返回Policy的控制信息
	 * 
	 * @param policyInfo
	 * @return
	 */
	public String enablePolicy(PolicyInfo policyInfo) {

		String enableInfo = "";
		if (policyInfo == null) {
			enableInfo = new PolicyControl(context).enableDefaultPolicy();
			L.i(getClass(), "policyinfo is null, auto enable defaultpolicy  ");
			return enableInfo;
		}
		return enableQRPolicy(policyInfo);
	}

	/**
	 * 
	 * enableDefaultPolicy
	 * 
	 * @return
	 */
	public String enableDefaultPolicy() {
		return enablePolicy(new PolicyInfo().getDefaultPolicy());
	}

	/**
	 * 锁设备
	 * 
	 * @param pwd
	 *            密码
	 */
	public void lockDevice(String pwd) {

		L.d(this.getClass(), "password : " + pwd);
		if (pwd.equals("")) { // 無密碼直接關螢幕
			mDPM.lockNow();
		} else {
			mDPM.setPasswordQuality(mDeviceAdminSample,
					DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED); // 修改密碼前需先將密碼複雜程度設為"Unspecified"後，再將密碼設為""包住之空字串，才能設定成功。
			mDPM.resetPassword(pwd,
					DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
			mDPM.lockNow();
		}
	}

	/**
	 * 清除设备数据.
	 */
	public void removeWipe() {
		L.i(this.getClass(), "wraning remove wipe ");

		mDPM.wipeData(0); // Wipe internal data, like a factory reset.
		mDPM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
	}

	/**
	 * 移除设备密码. 设备解锁.
	 */
	public void unLockDevice() {
		mDPM.setPasswordMinimumLength(mDeviceAdminSample, 0);
		mDPM.setPasswordQuality(mDeviceAdminSample,
				DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED); // 移除密碼前需先將密碼複雜程度設為"Unspecified"後，再將密碼設為""包住之空字串，才能設定成功。
		mDPM.resetPassword("", DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY); // 移除密碼
	}

	/**
	 * 打开wifi
	 */
	public void enableWifi() {
		if (wifiManager != null) {
			wifiManager.setWifiEnabled(true);
		} else {
			wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			wifiManager.setWifiEnabled(true);
		}

	}

	/**
	 * 打开Wifi网卡
	 */
	public void openNetCard() {
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
	}

	/**
	 * 关闭wifi
	 */
	public void disableWifi() {
		if (wifiManager != null) {
			wifiManager.setWifiEnabled(false);
		} else {
			wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			wifiManager.setWifiEnabled(false);
		}
	}

	/**
	 * 连接至指定的SSID 的WiFi
	 * 
	 * @param ssid
	 * @param wepKey
	 */
	public void connectToSpecifySSID(String ssid, String wepKey) {
		L.d(getClass(),"connectToSpecifySSID "+ ssid +"WepKey "+ wepKey);
		if (TextUtils.isEmpty(ssid)) {
			L.d(this.getClass(), "connectTo : " + ssid);
			return;
		}
		if (wifiManager == null) {
			wifiManager = (WifiManager) (context.getSystemService(Context.WIFI_SERVICE));
		}
		WiFiAutoConnManager wiFiAutoConnManager = new WiFiAutoConnManager(wifiManager);
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		State	wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		State	mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		if (wifiState != null && mobileState != null&& State.CONNECTED != wifiState&& State.CONNECTED == mobileState) {
			watingenabling();
			wiFiAutoConnManager.connect(ssid, wepKey,WifiCipherType.WIFICIPHER_WPA);
		} else if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED != mobileState) {
//			// 手机没有任何的网络
			watingenabling();
			wiFiAutoConnManager.connect(ssid, wepKey,WifiCipherType.WIFICIPHER_WPA);
		} else if (wifiState != null && State.CONNECTED == wifiState) {
			// 无线网络连接成功
			checkCurrentNetworkSSID(wiFiAutoConnManager,ssid,wepKey);
		}
	}

	private void watingenabling() {
		while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
			try {
				// 为了避免程序一直while循环，让它睡个100毫秒检测……
				Thread.sleep(500);
			} catch (InterruptedException ie) {
			}
		}
	}

	private void checkCurrentNetworkSSID(WiFiAutoConnManager wiFiAutoConnManager, String ssid, String wepkey) {
	
		watingenabling();
		WifiInfo mWifiInfo = wifiManager.getConnectionInfo();
		if (mWifiInfo != null) {
			String currentSSID = mWifiInfo.getSSID();
			if (!TextFormater.isEmpty(currentSSID)) {
				if (currentSSID.equalsIgnoreCase("\""+ssid+"\"")) {
						L.d(getClass(),"connected current is correct ");
				} else {
					disableWifi();
					if (wiFiAutoConnManager != null) {
						wiFiAutoConnManager.connect(ssid, wepkey,WifiCipherType.WIFICIPHER_WEP);
					}
				}
			}
		}

	}
	
	
	
	/**
	 * 相机可用
	 */
	public void enableCamera() {

		mDPM.setCameraDisabled(mDeviceAdminSample, false);
		L.i(this.getClass(), "camera is enabled ...");
	}

	/**
	 * 相机不可用
	 */
	public void disableCamera() {
		mDPM.setCameraDisabled(mDeviceAdminSample, true);
		L.i(this.getClass(), "camera is disabled ...");
	}

	/**
	 * 蓝牙可用
	 */
	public void enableBlueTooth() {
		if (btAdapter == null) {
			L.e("此设备不支持蓝牙! ");
		} else {
			btAdapter.enable();

			L.i(this.getClass(), " bluetooth is enabled  ...");
		}
	}

	/**
	 * 蓝牙可用
	 */
	public void enableBlueTooth(boolean scanable) {
		if (btAdapter == null) {
			L.e("此设备不支持蓝牙! ");
		} else {
			btAdapter.enable();
			if (scanable) {
				btAdapter.startDiscovery();
			}
			L.i(this.getClass(), " bluetooth is enabled  ...");
		}
	}

	/**
	 * 蓝牙不可用
	 */
	public void disableBlueTooth() {
		if (btAdapter == null) {
			L.e("此设备不支持蓝牙! ");
		} else {
			btAdapter.disable();
			L.i(this.getClass(), " bluetooth is disabled  ...");
		}
	}

	/**
	 * 1.清除企业数据. 2.账号清除 3.Server端更改是否控制标识 4.Server端允许控制,设备才能够注册.
	 */
	public void removeControl() {
		File file = new File(EMMContants.LocalConf.getEMMLocalHost_dirPath()
				+ EMMContants.LocalConf.LocalHost_dirpath);
		File[] listFiles = file.listFiles();
		for (int i = 0; i < listFiles.length; i++) {
			File subfile = listFiles[i];
			subfile.delete();
		}

		EMMPreferences.setUserID(context, "");
		EMMPreferences.setDeviceEnrolled(context,
				EMMContants.EMMPrefContant.ENROLL_STATUS_N);
		EMMPreferences.setPolicyData(context, "");

		// / 删除数据库所有数据
		AppDBControlHelper appDBControlHelper = new AppDBControlHelper(context);
		appDBControlHelper.dropAllDataTable();

	}

	/**
	 * 1.clean enterprise data only include send files , img , apk 2.
	 * 
	 */
	public void cleanInfo() {
		File file = new File(EMMContants.LocalConf.getEMMLocalHost_dirPath()
				+ EMMContants.LocalConf.LocalHost_dirpath);
		File[] listFiles = file.listFiles();
		for (int i = 0; i < listFiles.length; i++) {
			File subfile = listFiles[i];
			if (subfile.getAbsolutePath().contains("emmimg")) {
				continue;
			}
			subfile.delete();
		}
		AppDBControlHelper appDBControlHelper = new AppDBControlHelper(context);
		appDBControlHelper.dropAllDataForCleanInfo();

	}

	public String enableQRPolicy(PolicyInfo qrPolicyInfo) {
		String enableInfo = "";
		if (!isNull(qrPolicyInfo)) {
			if (!isNull(qrPolicyInfo.getRequired_PW())) {
				if (qrPolicyInfo.getRequired_PW().getItemFlag().equals("1")) {
					// enableInfo = "设备锁定密码长度: "
					// + qrPolicyInfo.getPwLength().getItemValue()
					// + "个字符\n 密码可输入错误次数："
					// + qrPolicyInfo.getError_Count().getItemValue()
					// + "次 \n 锁定时间："
					// + qrPolicyInfo.getLock_Time().getItemValue()
					// + "秒  \n";

					// / 无:0 , 纯数字密码: 1, 纯字母密码: 2 , 数字+字母密码 : 3, 数字+字母+标点混合密码 :
					// 4

					switch (Integer.parseInt(qrPolicyInfo.getPw_format()
							.getItemValue())) {
					case 1:
						mDPM.setPasswordQuality(mDeviceAdminSample,
								DevicePolicyManager.PASSWORD_QUALITY_SOMETHING);
						break;
					case 2:
						mDPM.setPasswordQuality(mDeviceAdminSample,
								DevicePolicyManager.PASSWORD_QUALITY_NUMERIC);
						break;
					case 3:
						mDPM.setPasswordQuality(mDeviceAdminSample,
								DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC);
						break;
					case 4:
						mDPM.setPasswordQuality(
								mDeviceAdminSample,
								DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC);
						break;
					case 5:
						mDPM.setPasswordQuality(mDeviceAdminSample,
								DevicePolicyManager.PASSWORD_QUALITY_COMPLEX);
						break;
					}

					int pwdLength = Integer.parseInt(qrPolicyInfo.getPwLength()
							.getItemValue());
					// if (pwdLength <= 4)
					// pwdLength = 4;
					mDPM.setPasswordMinimumLength(mDeviceAdminSample, pwdLength); // 設定密碼長度
					int errTime = 3;
					if (qrPolicyInfo.getError_Count().getItemFlag()
							.equalsIgnoreCase("1")) {
						errTime = Integer.parseInt(qrPolicyInfo
								.getError_Count().getItemValue());
						// if (errTime <= 3)
						// errTime = 3;
						DeviceAdminSampleReceiver.ERR_TIME = errTime; // 設定錯誤次數
						mDPM.setMaximumFailedPasswordsForWipe(
								mDeviceAdminSample, errTime);
					}
					if (qrPolicyInfo.getLock_Time().getItemFlag()
							.equalsIgnoreCase("1")) {
						int value = Integer.parseInt(qrPolicyInfo
								.getLock_Time().getItemValue());
						// 設定上鎖時間(分鐘)
						mDPM.setMaximumTimeToLock(mDeviceAdminSample, value
								* MS_PER_MINUTE);
					}
				} else {
					mDPM.setPasswordQuality(mDeviceAdminSample,
							DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
				}
			}
			if (!isNull(qrPolicyInfo.getEnable_Bluetooth())) {

				if ("1".equalsIgnoreCase(qrPolicyInfo.getEnable_Bluetooth()
						.getItemFlag())) {
					if (btAdapter == null) {
						L.e("此设备不支持蓝牙! ");
					} else {
						enableInfo += "启用蓝牙 \n";
						enableBlueTooth();
						if (!isNull(qrPolicyInfo.getScan_Devices())) {
							if ("1".equalsIgnoreCase(qrPolicyInfo
									.getScan_Devices().getItemFlag())) {
								enableBlueTooth(true);
							}
						}

					}
				} else if ("0".equalsIgnoreCase(qrPolicyInfo
						.getEnable_Bluetooth().getItemFlag())) {
					if (btAdapter == null) {
						L.e("此设备不支持蓝牙! ");
					} else {
						enableInfo += "禁用蓝牙 \n";
						disableBlueTooth();
					}
				} else {
					enableInfo += "不管控蓝牙 \n";
				}
			}
			if (!isNull(qrPolicyInfo.getEnable_Wifi())) {
				if (qrPolicyInfo.getEnable_Wifi().getItemFlag().equals("1")) {
					enableWifi();
				
				} else if (qrPolicyInfo.getEnable_Wifi().getItemFlag()
						.equals("0")) {
					enableInfo += "禁用WiFi \n";
					disableWifi();
				} else {
					enableInfo += "不管控WiFi \n";
				}
			}
			
			
			if (!isNull(qrPolicyInfo.getEnable_SSID())) {

				if (!isNull(qrPolicyInfo.getEnable_Wifi())) {
					if (qrPolicyInfo.getEnable_Wifi().getItemFlag().equals("0")) {
						disableWifi();
					} else {
						if ("1".equalsIgnoreCase(qrPolicyInfo.getEnable_SSID().getItemFlag())) {
							// //// 连接到指定的ssid ,指定的加密方式
							String ssid = qrPolicyInfo.getSsid().getItemValue();
							if (!TextUtils.isEmpty(ssid)) {
								connectToSpecifySSID(ssid, qrPolicyInfo.getPre_sharedkey().getItemValue());
								enableInfo += "WiFi SSID：" + ssid + "  \n";
							} else {
								L.w(this.getClass(), "SSID is null ");
							}
						}
						
					}
				}
				
			
			}
				
			if (!isNull(qrPolicyInfo.getEnable_Camera())) {

				// / 0 禁用,1启用,2不管控
				if (!qrPolicyInfo.getEnable_Camera().getItemFlag().equals("0")) {
					if (qrPolicyInfo.getEnable_Camera().getItemFlag()
							.equals("1")) {
						enableInfo += "开启相机  \n";
					}
					enableCamera();
				} else {
					disableCamera();
					enableInfo += "禁用相机 \n";
				}
			}
			if (TextUtils.isEmpty(enableInfo)) {
				return "不管控";
			}
			return enableInfo;
		} else {
			return "不管控";
		}
	}

	public boolean isNull(Object object) {
		if (object != null) {
			return false;
		}
		return true;
	}

}
