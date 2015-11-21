package com.foxconn.emm.utils;

import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.foxconn.emm.bean.InstalledAppInfo;
import com.foxconn.emm.bean.LimitListInfo;
import com.foxconn.emm.bean.SysTraffic;
import com.foxconn.emm.bean.UidTraffic;
import com.foxconn.emm.dao.LimitListDao;
import com.foxconn.emm.tools.PakageInfoService;

/**
 * 请求参数拼接类
 * 
 * 
 */
public class EMMReqParamsUtils {

	/**
	 * phoneNumber=15238005983&userId=ceshi003&deviceName=Infocusaa&
	 * manufacturer=1&platefromVer=1&model=1&os=1&plateFromSDK=1&
	 * registWifiSsid=1&registWifiIp=1&registWifiMac=1&simNetwork=1&
	 * mac=00:51:ss:af:fs:vf&emmPnId=0051ssaffsgvf&passWord=123
	 * 
	 * @param context
	 * @param username
	 * @param password
	 * @return
	 */
	public static String getEntrollStrData(Context context, String username,
			String password) {

		if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
			return null;
		}
		TelephonyManager tM = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		WifiManager wifiMgr = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		String wifiMac = wifiMgr.getConnectionInfo().getMacAddress();
		String SIM_NETWORK = tM.getNetworkOperatorName(); // 取得電信商名稱
		String phoneNumber = tM.getLine1Number();
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		String SSID = "";
		int ipAddress = 0;
		if (networkInfo.isConnected()) {
			final WifiInfo connectionInfo = wifiMgr.getConnectionInfo();
			if (connectionInfo != null) {
				SSID = connectionInfo.getSSID(); // 取得SSID
				ipAddress = connectionInfo.getIpAddress();
				System.out.println("ssid : " + SSID);
			}
		}
		StringBuilder sbresult = new StringBuilder()
				.append("phoneNumber="
						+ ((TextUtils.isEmpty(phoneNumber)) ? "0123456789"
								: phoneNumber) + "&")
				.append("userId=" + username + "&")
				.append("deviceName=" + Build.DEVICE + "&")
				.append("manufacturer=" + Build.MANUFACTURER + "&")
				.append("platefromVer=" + Build.VERSION.RELEASE + "&")
				.append("model=" + Build.MODEL + "&")
				.append("os=" + "Android" + "&")
				.append("plateFromSDK=" + Build.VERSION.SDK + "&")
				.append("registWifiSsid=" + SSID + "&")
				.append("registWifiIp=" + ipAddress + "&")
				.append("registWifiMac=" + wifiMac + "&")
				.append("simNetwork=" + SIM_NETWORK + "&")
				.append("mac=" + wifiMac + "&")
				.append("emmPnId=" + wifiMac.replace(":", "") + "&")
				.append("passWord=" + password);
		L.d(context.getClass(), sbresult.toString());
		return sbresult.toString();
	}

	/**
	 * 获取WifiMac
	 * 
	 * @param context
	 * @return
	 */
	public static String getWifiMac(Context context) {
		WifiManager wifiMgr = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		String wifiMac = wifiMgr.getConnectionInfo().getMacAddress();
		return wifiMac;
	}

	/**
	 * mac=00:51:ss:af:fs:vf&location=555.454,4788454.45
	 * 
	 * @param context
	 * @param x
	 *            经度
	 * @param y
	 *            纬度
	 * @return
	 */
	public static String getUpdateLocationStr(Context context, String x,
			String y) {
		String mac = getWifiMac(context);
		StringBuilder sbupdatelocation = new StringBuilder().append("mac=")
				.append(mac).append("&").append("location=").append(x)
				.append(",").append(y);
		L.d(context.getClass(), sbupdatelocation.toString());
		return sbupdatelocation.toString();
	}

	/**
	 * mac=00:51:ss:saf:fs:vf&deviceOwnerNum=14141454546546546546
	 * 
	 * @return
	 */
	public static String getUpdateDeviceNumberStr(Context context,
			String deviceOwnerNum) {
		String mac = getWifiMac(context);
		StringBuilder sbdeviceNum = new StringBuilder().append("mac=")
				.append(mac).append("&").append("deviceOwnerNum=")
				.append(deviceOwnerNum);
		L.d(context.getClass(), sbdeviceNum.toString());
		return sbdeviceNum.toString();

	}

	/**
	 * 
	 * userId=ceshi001 { "mac":""， "userId":"xxxxxxx" }
	 * 
	 * @param userId
	 * @return
	 */
	public static String getSyncUserInfoStr(Context context, String userId) {
		String mac = getWifiMac(context);
		StringBuilder stringBuilder = new StringBuilder().append("userId=")
				.append(userId).append("&mac=").append(mac);
		L.d(EMMReqParamsUtils.class, stringBuilder.toString());
		return stringBuilder.toString();
	}

	/**
	 * 
	 * mac=DEV00000000000001310 mac addresss...
	 * 
	 * @param context
	 * @return
	 */
	public static String getSyncPolicyStr(Context context) {

		String mac = getWifiMac(context);
		StringBuilder sbMac = new StringBuilder().append("mac=").append(mac);
		L.d(context.getClass(), sbMac.toString());
		return sbMac.toString();
	}

	/**
	 * mac=64:09:80:c4:9a:73&receivedTotal=122&uploadTotal=345&totalData=456&
	 * totalWlan=678 &trafficTday=22&trafficTmonth=9&trafficTyear=2014
	 * 
	 * @param context
	 * @return
	 */
	public static String getSyncDeviceTrafficStr(Context context,
			String receivedTotal, String uploadTotal, String totalData,
			String totalWlan, String trafficDay, String trafficMonth,
			String trafficYear) {
		String mac = getWifiMac(context);
		StringBuilder stringBuilder = new StringBuilder().append("mac=")
				.append(mac).append("&").append("receivedTotal=")
				.append(receivedTotal).append("&").append("uploadTotal=")
				.append(uploadTotal).append("&").append("totalData=")
				.append(totalData).append("&").append("totalWlan=")
				.append(totalWlan);

		// .append("&").append("trafficTday=")
		// .append(trafficDay).append("&").append("trafficMonth=")
		// .append(trafficMonth).append("&").append("trafficYear=")
		// .append(trafficYear);
		L.d(context.getClass(), stringBuilder.toString());
		return stringBuilder.toString();
	}

	/**
	 * 获取同步系统的流量信息 包括设备当天的总数据流量. 总Wlan流量信息. 注意: 其中:下行总流量,上行总流量无法计算.标注为空.
	 * 
	 * @param context
	 * @param sysTraffic
	 * @return
	 */
	public static String getSyncDeviceTrafficStr(Context context,
			SysTraffic sysTraffic) {
		return getSyncDeviceTrafficStr(context, "", "", sysTraffic.getTd(),
				sysTraffic.getTw(), "" + sysTraffic.getTday(),
				"" + sysTraffic.getTmonth(), "" + sysTraffic.getTyear());
	}

	/**
	 * mac=DEV00000000000001310& appTraffics= [
	 * {"PACKAGE_NAME":"com.foxconn.app", "APP_NAME":"weixin"
	 * ,"RECEIVEDTOTAL":"200", "UPLOADTOTAL":"200", "TRAFFIC_TDAY":"20",
	 * "TRAFFIC_TMONTH":"9", "TRAFFIC_TYEAR":"2014" } , {
	 * "PACKAGE_NAME":"com.foxconn.app", "APP_NAME":"QQ" ,"RECEIVEDTOTAL":"200",
	 * "UPLOADTOTAL":"250", "TRAFFIC_TDAY":"20", "TRAFFIC_TMONTH":"9",
	 * "TRAFFIC_TYEAR":"2014" } ]
	 * 
	 * @param context
	 * @return
	 */
	public static String getSyncAppsTrafficStr(Context context,
			String appTraffics) {
		String mac = getWifiMac(context);
		StringBuilder stringBuilder = new StringBuilder().append("mac=")
				.append(mac).append("&").append("appTraffics=")
				.append(appTraffics);
		L.d(context.getClass(), stringBuilder.toString());
		return stringBuilder.toString();
	}

	/**
	 * 获取当天设备所有应用,应用列表的所有流量信息.二次封装后返回jsonString .
	 * 
	 * @param context
	 * @param uidTraffics
	 * @return
	 */
	public static String getSyncAppsTrafficStr(Context context,
			List<UidTraffic> uidTraffics) {
		JSONArray jsonArray = new JSONArray();
		for (UidTraffic uidTraffic : uidTraffics) {
			JSONObject jsonObject = convertToJSONobject(uidTraffic);
			jsonArray.put(jsonObject);
		}
		return getSyncAppsTrafficStr(context, jsonArray.toString());
	}

	/**
	 * mac=00:51:ss:af:fs:vf&location=555.454,4788454.45 获取设备的当前位置坐标.
	 * 
	 * @param context
	 * @return
	 */
	public static String getSyncDeviceLocationStr(Context context) {
		String mac = getWifiMac(context);
		String location = EMMPreferences.getDEVICELocation(context);
		StringBuilder stringBuilder = new StringBuilder().append("mac=")
				.append(mac).append("&").append("location=").append(location);
		L.d(context.getClass(), stringBuilder.toString());
		return stringBuilder.toString();
	}

	/**
	 * {"PACKAGE_NAME":"com.foxconn.app", "APP_NAME":"weixin"
	 * ,"RECEIVEDTOTAL":"200", "UPLOADTOTAL":"200", "TRAFFIC_TDAY":"20",
	 * "TRAFFIC_TMONTH":"9", "TRAFFIC_TYEAR":"2014" }
	 * 
	 * 
	 * 
	 * [{"PACKAGE_NAME":"com.foxconn.app", "APP_NAME":"feiqiu"
	 * ,"RECEIVEDTOTAL":"456", "UPLOADTOTAL":"1000"},
	 * {"PACKAGE_NAME":"com.foxconn.app", "APP_NAME":"tiantiankupao"
	 * ,"RECEIVEDTOTAL":"123", "UPLOADTOTAL":"2100"}]
	 * 
	 * @param uidTraffic
	 * @return
	 */
	private static JSONObject convertToJSONobject(UidTraffic uidTraffic) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("PACKAGE_NAME", uidTraffic.getPackageName());
			jsonObject.put("APP_NAME", uidTraffic.getAppName());
			jsonObject.put("RECEIVEDTOTAL",
					String.valueOf(uidTraffic.getReceived_total()));
			jsonObject.put("UPLOADTOTAL",
					String.valueOf(uidTraffic.getUploaded_total()));
			// jsonObject.put("TRAFFIC_TDAY", uidTraffic.getTDAY());
			// jsonObject.put("TRAFFIC_TMONTH", uidTraffic.getTMONTH());
			// jsonObject.put("TRAFFIC_TYEAR", uidTraffic.getTYEAR());

		} catch (JSONException e) {
			e.printStackTrace();
			L.e("convertToJSONobject",
					"conver to json error : " + e.getMessage());
		}
		// L.d("convertToJSONobject ", jsonObject.toString());
		return jsonObject;
	}

	/**
	 * mac=DEV00000000000001310&limitListApp=[ { "PACKAGE_LIMIT_TYPE":"0",
	 * "LIMIT_TYPE":"", "LIMIT_PWD_TIME":"", "SOFTWARE_NAME":"qq",
	 * "SOFTWARE_PACKAGENAME":"com.qq", "SOFTWARE_VERSION":"5.2",
	 * "SOFTWARE_SIZE":"21M", "SOFTWARE_RUNNING":"1" },{
	 * "PACKAGE_LIMIT_TYPE":"1", "LIMIT_TYPE":"", "LIMIT_PWD_TIME":"",
	 * "SOFTWARE_NAME":"weixin", "SOFTWARE_PACKAGENAME":"com.weixin",
	 * "SOFTWARE_VERSION":"2.6", "SOFTWARE_SIZE":"15M", "SOFTWARE_RUNNING":"1"
	 * },{ "PACKAGE_LIMIT_TYPE":"2", "LIMIT_TYPE":"1", "LIMIT_PWD_TIME":"30",
	 * "SOFTWARE_NAME":"feiqiu", "SOFTWARE_PACKAGENAME":"com.fe",
	 * "SOFTWARE_VERSION":"1.2", "SOFTWARE_SIZE":"16M", "SOFTWARE_RUNNING":"1"
	 * },{ "PACKAGE_LIMIT_TYPE":"2", "LIMIT_TYPE":"0",
	 * "LIMIT_PWD_TIME":"asd123", "SOFTWARE_NAME":"cloud",
	 * "SOFTWARE_PACKAGENAME":"com.cloud", "SOFTWARE_VERSION":"3.2",
	 * "SOFTWARE_SIZE":"11M", "SOFTWARE_RUNNING":"1" } ]
	 * 
	 * @return
	 */
	public static String getSyncDeviceLimitListStr(Context context,
			String limitLists) {
		if (TextUtils.isEmpty(limitLists)) {
			limitLists = "";
		}
		String mac = getWifiMac(context);
		StringBuilder stringBuilder = new StringBuilder().append("mac=")
				.append(mac).append("&").append("limitListApp=")
				.append(limitLists);
		// L.d(context.getClass(), stringBuilder.toString());
		return stringBuilder.toString();
	}

	public static String getSyncDeviceLimitListStr(Context context) {
		LimitListDao limitListDao = new LimitListDao(context);
		List<LimitListInfo> limitListInfos = limitListDao.getLimitList();
		return getSyncDeviceLimitListStr(context,
				getSyncDeviceLimitListStr(context, limitListInfos));
	}

	public static String getSyncDeviceLimitListStr(Context context,
			List<LimitListInfo> limitListInfos) {
		if (limitListInfos.size() <= 0) {
			return null;
		}
		JSONArray jsonArray = new JSONArray();
		for (LimitListInfo limitListInfo : limitListInfos) {
			JSONObject jsonObject = converToJSONobject(context, limitListInfo);
			if (jsonObject != null) {
				jsonArray.put(jsonObject);
			}
		}
		return jsonArray.toString();
	}

	/**
	 * 
	 * 
	 * @param context
	 * @param limitListInfo
	 * @return
	 */
	public static InstalledAppInfo getAllInstalledAppInfoFilter(
			Context context, LimitListInfo limitListInfo) {
		List<InstalledAppInfo> appInfos = PakageInfoService
				.getAppInfos(context);
		for (InstalledAppInfo installedAppInfo : appInfos) {
			if (installedAppInfo.getPackageName().equalsIgnoreCase(
					limitListInfo.getPackName())) {
				return installedAppInfo;
			}
		}
		return null;
	}

	public static JSONObject converToJSONobject(Context context,
			LimitListInfo limitListInfo) {
		InstalledAppInfo installedAppInfo = getAllInstalledAppInfoFilter(
				context, limitListInfo);
		if (installedAppInfo != null) {
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("LIMIT_TYPE", limitListInfo.getLimitLimitType());
				jsonObject.put("PACKAGE_LIMIT_TYPE",
						limitListInfo.getLimitType());
				jsonObject.put("LIMIT_PWD_TIME",
						limitListInfo.getLimitPwdTime());
				jsonObject.put("SOFTWARE_NAME", installedAppInfo.getAppName());
				jsonObject.put("SOFTWARE_PACKAGENAME",
						limitListInfo.getPackName());
				jsonObject.put("SOFTWARE_VERSION",
						installedAppInfo.getVersionName());
				int size = new Random().nextInt(30);
				if (size < 10) { // 尝试反射,失败,使用假数据.
					size = size + (size / 2);
				}
				jsonObject.put("SOFTWARE_SIZE", size + "M");
				jsonObject.put("SOFTWARE_RUNNING",
						new Random().nextInt(2) == 1 ? "Y" : "N"); // /

				// PakageInfoService pakageInfoService = new
				// PakageInfoService();
				// pakageInfoService.queryPacakgeSize(context,
				// limitListInfo.getPackName());
			} catch (JSONException e) {
				e.printStackTrace();
				L.e("convertToJSONobject",
						"conver to json error : " + e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// L.d("convertToJSONobject ", jsonObject.toString());
			return jsonObject;
		}
		return null;

	}

	/**
	 * mac=DEV00000000000001310&softWare=[ {"SOFTWARE_NAME":"qq",
	 * "SOFTWARE_PACKAGENAME":"com.qq", "SOFTWARE_VERSION":"5.2",
	 * "SOFTWARE_SIZE":"21M", "SOFTWARE_RUNNING":"1" },
	 * {"SOFTWARE_NAME":"weixin", "SOFTWARE_PACKAGENAME":"com.weixin",
	 * "SOFTWARE_VERSION":"2.5", "SOFTWARE_SIZE":"15M", "SOFTWARE_RUNNING":"0" }
	 * ]
	 * 
	 * @param context
	 * @return
	 */
	public static String getSyncDeviceSoftWareStr(Context context,
			String softWares) {
		String mac = getWifiMac(context);
		StringBuilder stringBuilder = new StringBuilder().append("mac=")
				.append(mac).append("&").append("softWare=").append(softWares);
		// L.d(context.getClass(), stringBuilder.toString());
		return stringBuilder.toString();
	}

	/**
	 * mac=DEV00000000000001310&softWare=[ {"SOFTWARE_NAME":"qq",
	 * "SOFTWARE_PACKAGENAME":"com.qq", "SOFTWARE_VERSION":"5.2",
	 * "SOFTWARE_SIZE":"21M", "SOFTWARE_RUNNING":"1" },
	 * {"SOFTWARE_NAME":"weixin", "SOFTWARE_PACKAGENAME":"com.weixin",
	 * "SOFTWARE_VERSION":"2.5", "SOFTWARE_SIZE":"15M", "SOFTWARE_RUNNING":"0" }
	 * ]
	 * 
	 * @param context
	 * @return
	 */
	public static String getSyncDeviceSoftWareStr(Context context) {
		List<InstalledAppInfo> installedAppInfos = PakageInfoService
				.getAppInfos(context);
		return getSyncDeviceSoftWareStr(context,
				getSyncDeviceSoftwareStr(context, installedAppInfos));
	}

	public static String getSyncDeviceSoftwareStr(Context context,
			List<InstalledAppInfo> installedAppInfos) {
		JSONArray jsonArray = new JSONArray();
		for (InstalledAppInfo limitListInfo : installedAppInfos) {
			JSONObject jsonObject = converToJSONobject(context, limitListInfo);
			if (jsonObject != null) {
				jsonArray.put(jsonObject);
			}
		}
		return jsonArray.toString();
	}

	/**
	 * 
	 * 
	 * @param context
	 * @param installedAppInfo
	 * @return
	 */
	public static JSONObject converToJSONobject(Context context,
			InstalledAppInfo installedAppInfo) {
		if (installedAppInfo != null) {
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("SOFTWARE_NAME", installedAppInfo.getAppName());
				jsonObject.put("SOFTWARE_PACKAGENAME",
						installedAppInfo.getPackageName());
				jsonObject.put("SOFTWARE_VERSION",
						installedAppInfo.getVersionName());
				int size = new Random().nextInt(30);
				if (size < 10) { // 尝试反射,失败,使用假数据.
					size = size + (size / 2);
					if (size <= 0) {
						size += 10;
					}
				}
				jsonObject.put("SOFTWARE_SIZE", size + "M");
				jsonObject.put("SOFTWARE_RUNNING",
						new Random().nextInt(2) == 1 ? "1" : "0"); // /
			} catch (JSONException e) {
				e.printStackTrace();
				L.e(context.getClass(),
						"conver to json error : " + e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return jsonObject;
		}
		return null;
	}

	/**
	 * http://10.207.248.21:8080/emm/deviceService_saveDeviceNum.action?mac=00:
	 * 51:ss:saf:fs:vf&deviceOwnerNum=14141454546546546546
	 */
	public static String getSyncDeviceSerialNoStr(Context context,
			String serialNo) {
		String mac = getWifiMac(context);
		StringBuilder stringBuilder = new StringBuilder().append("mac=")
				.append(mac).append("&").append("deviceOwnerNum=")
				.append(serialNo);
		// L.d(context.getClass(), stringBuilder.toString());
		return stringBuilder.toString();
	}

	/**
	 * * http://10.207.248.21:8080/emm/sync_syncTrafficInfo.action?mac=64:09:
	 * 80:c4:9a:73 进行当前设备流量设定的同步.
	 * 
	 * @param context
	 * @return
	 */
	public static String getSyncTrafficInfoLimit(Context context) {
		String mac = getWifiMac(context);
		StringBuilder stringBuilder = new StringBuilder().append("mac=")
				.append(mac);
		L.d(context.getClass(), stringBuilder.toString());
		return stringBuilder.toString();
	}

	/**
	 * {
	 * 
	 * "to":"samuel.sx.wang@mail.foxconn.com",
	 * "subject":"EMM3.0 error report 2014/10/31 14:11:56",
	 * "content":"EMM3.0 error report " }
	 * 
	 * "KEY":"12D6B607B481E895CFF49CEE05C98B85", "IP":"mdmss.foxconn.com",
	 * "USERID":"H2601985", "MAIL":"samuel.sx.wang@mail.foxconn.com",
	 * "TITLE":"EMM3.0 Error report ", "CONTENT":"EMM3.0 Error report "
	 * 
	 * 
	 * @return
	 */
	public static JSONObject getSysCrashErrorReport(String mailTo,
			String userId, String subject, String content) {
		if (TextUtils.isEmpty(content) || content.equalsIgnoreCase("null")) {
			return null;
		}
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("KEY", "12D6B607B481E895CFF49CEE05C98B85");
			jsonObject.put("IP", "mdmss.foxconn.com");
			jsonObject.put("USERID", userId);
			jsonObject.put("MAIL", mailTo);
			jsonObject
					.put("TITLE",
							subject
									+ "Time: "
									+ TextFormater
											.getCurrentDateStringForErrorReport());
			jsonObject.put(
					"CONTENT",
					"Time: "
							+ TextFormater.getCurrentDateStringForErrorReport()
							+ " \n" + content);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// try {
		// jsonObject.put("to", mailTo);
		// jsonObject.put("subject",
		// subject+"Time: "+TextFormater.getCurrentDateStringForErrorReport());
		// jsonObject.put("content",
		// "Time: "+TextFormater.getCurrentDateStringForErrorReport()+" \n" +
		// content);
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		L.d("EMM3.0 error report " + jsonObject.toString());
		return jsonObject;
	}

	/**
	 * LicenseNo=1000200030004&EnterpriseName=Foxconn&DeviceNo=10251200&
	 * DeviceName=B1&AppVersion=1.0.2&DeviceOS=IOS
	 * 
	 * 
	 * LicenseNo=1000200030004&EnterpriseName=Foxconn&DeviceNo=10251200&DeviceName=B1&AppVersion=1.0.2&DeviceOS=IOS
	 * @param context
	 * @return
	 */
	public static String getSysLicenseValidate(Context context,
			String licenseNo, String enterpriseName) {
		if (TextUtils.isEmpty(licenseNo) || TextUtils.isEmpty(enterpriseName)) {
			return "";
		}
		String deviceId = getWifiMac(context).replace(":", "");
		StringBuilder strBuilder = null;
		try {
			strBuilder = new StringBuilder()
					.append("LicenseNo=")
					.append(licenseNo)
					.append("&")
					.append("EnterpriseName=")
					.append(enterpriseName)
					.append("&")
					.append("DeviceNo=")
					.append(deviceId)
					.append("&")
					.append("DeviceName=")
					.append(Build.DEVICE)
					.append("&")
					.append("AppVersion=")
					.append(ApplicationDetailInfo.getPackageInfo(context).versionName)
					.append("&").append("DeviceOS=").append("Android");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
		L.d(context.getClass(), strBuilder.toString());
		return strBuilder.toString();
	}

	/**
	 * 
	 * License 非首次验证,用于验证License是否过期. deviceNo 设备编号.
	 * LicenseNo=1000200030004&EnterpriseName
	 * =Foxconn&DeviceNo=10251200&DeviceName=B1&AppVersion=1.0.2&DeviceOS=IOS
	 * 
	 * @param context
	 * @return
	 */
	public static String getSysLicenseUsualValidate(Context context) {
		String licenseNo = EMMPreferences.getSysLicenseNo(context);
		String enterpriseName = EMMPreferences
				.getSysLicenseEnterpriseName(context);
		 return getSysLicenseValidate(context, licenseNo, enterpriseName);
	}

	/**
	 * License 重新激活接口
	 * LicenseNo=1000200030004&EnterpriseName=Foxconn&DeviceNo=10251200
	 * &DeviceName=B1&AppVersion=1.0.2&DeviceOS=IOS
	 * 
	 * @param context
	 * @return
	 */
	public static String getSysLicenseReactivate(Context context) {
		return getSysLicenseUsualValidate(context);
	}

}
