package com.foxconn.emm.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.foxconn.emm.bean.TimerJSONPolicy;
import com.foxconn.emm.utils.EMMContants.EMMPrefContant;

public class EMMPreferences {

	private static SharedPreferences settings;
	private static SharedPreferences policyData;
	private static SharedPreferences fileStatus;

	private static SharedPreferences getSP(Context c) {
		return c.getSharedPreferences(EMMContants.EMMPrefContant.SYS_STATUS, 0);
	}

	private static SharedPreferences getPolicySP(Context c) {
		return c.getSharedPreferences(EMMContants.EMMPrefContant.POLICY_DATA, 0);
	}

	private static SharedPreferences getFileSU(Context c) {
		return c.getSharedPreferences(EMMContants.EMMPrefContant.FILE_STATUS, 0);
	}

	/**
	 * 
	 * @param c
	 * @param policyJSON
	 */
	public static void setPolicyData(Context c, String policyJSON) {
		policyData = getPolicySP(c);
		policyData.edit().putString("policydata", policyJSON).commit();
	}

	public static String getPolicyData(Context c) {
		policyData = getPolicySP(c);
		return policyData.getString("policydata", "");
	}

	/**
	 * 获取设备的总流量信息
	 * 
	 * @param c
	 * @return
	 */
	public static String getTotalData(Context c) {
		settings = getSP(c);
		return settings.getString(EMMPrefContant.TOTAL_DEVICE_TRAFFIC, "");
	}

	/**
	 * 设置设备总流量信息
	 * 
	 * @param c
	 * @param totalData
	 *            LONG 类型
	 */
	public static void setTotalData(Context c, String totalData) {
		settings = getSP(c);
		settings.edit()
				.putString(EMMPrefContant.TOTAL_DEVICE_TRAFFIC, totalData)
				.commit();
	}

	public static String getTrafficInfoLimit(Context c) {
		settings = getSP(c);
		return settings.getString(EMMPrefContant.DEVICE_TRAFFIC_LIMIT, "");
	}

	/**
	 * 设置设备流量限制信息
	 * 
	 * @param c
	 * @param trafficLImitStr
	 *            json
	 */
	public static void setTrafficInfoLimit(Context c, String trafficLImitStr) {
		settings = getSP(c);
		settings.edit()
				.putString(EMMPrefContant.DEVICE_TRAFFIC_LIMIT, trafficLImitStr)
				.commit();
	}

	public static void setCurrentPolicyData(Context c, String policyJson) {

		policyData = getPolicySP(c);
		policyData
				.edit()
				.putString(EMMContants.EMMPrefContant.CURRENT_POLICY_DATA,
						policyJson).commit();
	}

	public static TimerJSONPolicy getCurrentPolicyData(Context c) {
		policyData = getPolicySP(c);
		String policyDataJson = policyData.getString(
				EMMPrefContant.CURRENT_POLICY_DATA, "");
		if (TextUtils.isEmpty(policyDataJson)) {
			return TimerJSONPolicy.getDefPolicy();
		} else {
			return TimerJSONPolicy.getTimerJSONPolicy(policyDataJson);
		}
	}

	/**
	 * 是否需要认证. true需要. false 不需要.
	 * 
	 * @param c
	 * @return
	 */
	public static Boolean getSecurityNeedAuthorized(Context c) {
		settings = getSP(c);
		return settings.getBoolean(
				EMMContants.EMMPrefContant.IS_SECURITY_AUTHORIZED, false);
	}

	/**
	 * 是否需要认证. true需要. false 不需要.
	 * 
	 * @param c
	 * @param is_need
	 */
	public static void setSecurityNeedAuthorized(Context c, Boolean is_need) {
		settings = getSP(c);
		settings.edit()
				.putBoolean(EMMContants.EMMPrefContant.IS_SECURITY_AUTHORIZED,
						is_need).commit();
	}

	/**
	 * 获取设备锁是否开启. true开启. false 关闭.
	 * 
	 * @param c
	 * @return
	 */
	public static Boolean getSecurityStatus(Context c) {
		settings = getSP(c);
		return settings.getBoolean(
				EMMContants.EMMPrefContant.IS_SECURITY_OPENED, false);
	}

	/**
	 * 设置设备锁是否开启. true开启. false 关闭.
	 * 
	 * @param c
	 * @param is_security_opened
	 */
	public static void setSecurityStatus(Context c, Boolean is_security_opened) {
		settings = getSP(c);
		settings.edit()
				.putBoolean(EMMContants.EMMPrefContant.IS_SECURITY_OPENED,
						is_security_opened).commit();
	}

	public static String getDeviceID(Context c) {
		settings = getSP(c);
		return settings.getString(EMMPrefContant.DEVICE_SERAIL_ID, "");
	}

	public static void setDeviceID(Context c, String deviceId) {
		settings = getSP(c);
		settings.edit().putString(EMMPrefContant.DEVICE_SERAIL_ID, deviceId)
				.commit();
	}

	public static String getUserID(Context c) {
		settings = getSP(c);
		return settings.getString(EMMPrefContant.USER_ID, "");
	}

	public static void setUserID(Context c, String id) {
		settings = getSP(c);
		settings.edit().putString(EMMPrefContant.USER_ID, id).commit();
	}

	/**
	 * location
	 * 
	 * @param c
	 * @return format x,y
	 */
	public static String getDEVICELocation(Context c) {
		settings = getSP(c);
		return settings.getString(EMMPrefContant.DEVICE_LOCATION, "");
	}

	/**
	 * set devicelocation
	 * 
	 * @param c
	 * @param location
	 */
	public static void setDEVICELocation(Context c, String location) {
		settings = getSP(c);
		settings.edit().putString(EMMPrefContant.DEVICE_LOCATION, location)
				.commit();
	}

	/**
	 * location 当前设备的地址信息,街道名称
	 * 
	 * @param c
	 * @return format x,y
	 */
	public static String getDEVICELocationStreet(Context c) {
		settings = getSP(c);
		return settings.getString(EMMPrefContant.DEVICE_STREET_LOCATION, "");
	}

	/**
	 * set devicelocation
	 * 
	 * @param c
	 * @param location
	 */
	public static void setDEVICELocationStreet(Context c, String location) {
		settings = getSP(c);
		settings.edit()
				.putString(EMMPrefContant.DEVICE_STREET_LOCATION, location)
				.commit();
	}

	/**
	 * 用户信息同步时间.
	 * 
	 * @param c
	 * @return
	 */
	public static String getUserInfoSyncTime(Context c) {
		settings = getSP(c);
		return settings.getString(EMMPrefContant.USERINFO_SYNCTIME, "");
	}

	public static void setUserInfoSyncTime(Context c, String userinfo_synctime) {
		settings = getSP(c);
		settings.edit()
				.putString(EMMPrefContant.USERINFO_SYNCTIME, userinfo_synctime)
				.commit();
	}

	/**
	 * get the license verify result
	 * 
	 * @param c
	 * @return
	 */
	public static boolean getIsLicensePass(Context c) {
		settings = getSP(c);
		return settings.getBoolean(EMMPrefContant.IS_LICENSE_PASS, false);
	}

	/**
	 * set the license verify result
	 * 
	 * @param c
	 * @param is_license_pass
	 */
	public static void setLicenseIsPass(Context c, boolean is_license_pass) {
		settings = getSP(c);
		settings.edit()
				.putBoolean(EMMPrefContant.IS_LICENSE_PASS, is_license_pass)
				.commit();
	}

	/**
	 * get the license is enable
	 * 
	 * @param c
	 * @return
	 */
	public static boolean getIsLicenseEnable(Context c) {
		settings = getSP(c);
		return settings.getBoolean(EMMPrefContant.IS_LICENSE_ENABLE, true);
	}

	/**
	 * set the license is enable
	 * 
	 * @param c
	 * @param is_license_Enable
	 */
	public static void setLicenseIsEnable(Context c, boolean is_license_Enable) {
		settings = getSP(c);
		settings.edit()
				.putBoolean(EMMPrefContant.IS_LICENSE_ENABLE, is_license_Enable)
				.commit();
	}

	/**
	 * 用户事业群
	 * 
	 * @param c
	 * @return
	 */
	public static String getUserBGID(Context c) {
		settings = getSP(c);
		return settings.getString(EMMPrefContant.USERBG_ID, "");
	}

	public static void setUserBGID(Context c, String id) {
		settings = getSP(c);
		settings.edit().putString(EMMPrefContant.USERBG_ID, id).commit();
	}

	/**
	 * App检查更新的时间.
	 * 
	 * @param c
	 * @return
	 */
	public static String getAppUpdateTime(Context c) {
		settings = getSP(c);
		return settings.getString(EMMPrefContant.CHECKUPDATE_TIME, String.valueOf(System.currentTimeMillis()-100000000L));
	}

	public static void setAppUpdateTime(Context c, String updateTime) {
		settings = getSP(c);
		settings.edit().putString(EMMPrefContant.CHECKUPDATE_TIME, updateTime)
				.commit();
	}

	public static String getIMEI(Context c) {
		settings = getSP(c);
		return settings.getString(EMMPrefContant.IMEI, "");
	}

	public static void setIMEI(Context c, String imei) {
		settings = getSP(c);
		settings.edit().putString(EMMPrefContant.IMEI, imei).commit();
	}

	// / get enroll status
	// /代表的是注册信息,是否注册
	public static String getDeviceEnrolled(Context c) {
		settings = getSP(c);
		return settings.getString(EMMPrefContant.ENROLLSTATUS,
				EMMContants.EMMPrefContant.ENROLL_STATUS_N);
	}

	public static void setDeviceEnrolled(Context c, String enrollStatus) {
		settings = getSP(c);
		settings.edit().putString(EMMPrefContant.ENROLLSTATUS, enrollStatus)
				.commit();
	}

	public static String getLocationCode(Context c) {
		settings = getSP(c);
		return settings.getString(EMMPrefContant.LOCATION_CODE, "");
	}

	public static void setLocationCode(Context c, String lcode) {
		settings = getSP(c);
		settings.edit().putString(EMMPrefContant.LOCATION_CODE, lcode).commit();
	}

	public static Boolean getOffLineEnrollStatus(Context c) {
		settings = getSP(c);
		return settings.getBoolean(EMMPrefContant.OFFLINE_ENROLL_STATUS, false);
	}

	// set MDM is enroll server
	public static void setOffLineEnrollStatus(Context c, Boolean oes) {
		settings = getSP(c);
		settings.edit().putBoolean(EMMPrefContant.OFFLINE_ENROLL_STATUS, oes)
				.commit();
	}

	// //HASHEADIMG_UPLOAD
	public static boolean isHASHEADIMG_UPLOAD(Context c) {
		settings = getSP(c);
		return settings.getBoolean(
				EMMContants.EMMPrefContant.HASHEADIMG_UPLOAD, false);
	}

	// /// set HASHEADIMG_UPLOAD
	public static void setHASHEADIMG_UPLOAD(Context c, boolean hasheadimg_upload) {
		settings = getSP(c);
		settings.edit()
				.putBoolean(EMMContants.EMMPrefContant.HASHEADIMG_UPLOAD,
						hasheadimg_upload).commit();
	}

	// set is intent AtyFileImg
	public static void setIsIntentAtyFImg(Context c, boolean is_intent) {
		fileStatus = getFileSU(c);
		fileStatus.edit()
				.putBoolean(EMMContants.EMMPrefContant.IS_INTENT, is_intent)
				.commit();
	}

	public static boolean getIsIntentAtyFImg(Context c) {
		fileStatus = getFileSU(c);
		return fileStatus.getBoolean(EMMContants.EMMPrefContant.IS_INTENT,
				false);
	}

	/**
	 * 获取系统的LicenseNO 如果为空 则默认为""
	 * 
	 * @param context
	 * @return
	 */
	public static String getSysLicenseNo(Context context) {
		settings = getSP(context);
		return settings.getString(EMMPrefContant.SYS_LICENSE_NO, "");
	}

	/**
	 * 设置系统注册License时的LicenseNo
	 * 
	 * @param licenseNo
	 * @param context
	 */
	public static void setSysLicenseNo(Context context, String licenseNo) {

		settings = getSP(context);
		settings.edit().putString(EMMPrefContant.SYS_LICENSE_NO, licenseNo)
				.commit();

	}

	/**
	 * 获取系统设置LicenseNo 时候的企业名称 默认为""
	 * 
	 * @param context
	 * @return
	 */
	public static String getSysLicenseEnterpriseName(Context context) {
		settings = getSP(context);
		return settings
				.getString(EMMPrefContant.SYS_LICENSE_ENTERPRISENAME, "");
	}

	/**
	 * 
	 * 设置系统设置LicenseNO时候的 企业名称 enterpriseName .
	 * 
	 * @param c
	 * @param enterpriseName
	 */
	public static void setSysLicenseEnterpriseName(Context c,
			String enterpriseName) {
		settings = getSP(c);
		settings.edit()
				.putString(EMMPrefContant.SYS_LICENSE_ENTERPRISENAME,
						enterpriseName).commit();
	}

	/**
	 * 获取License的失效日期.
	 * 
	 * @param context
	 * @return
	 */
	public static String getSysLicenseDeadLine(Context context) {
		settings = getSP(context);
		return settings.getString(EMMPrefContant.SYS_LICENSE_DEADLINE, "");
	}

	/**
	 * 
	 * 设置当前License的失效日期, 系统默认设置失效后15天仍然可以使用 .
	 * 
	 * @param c
	 * @param deadline
	 */
	public static void setSysLicenseDeadLine(Context c, String deadline) {
		settings = getSP(c);
		settings.edit()
				.putString(EMMPrefContant.SYS_LICENSE_DEADLINE, deadline)
				.commit();
	}

	/**
	 * 获取Server端的现在的日期.
	 * 
	 * @param context
	 * @return
	 */
	public static String getSysLicenseCurrentTime(Context context) {
		settings = getSP(context);
		return settings.getString(EMMPrefContant.SYS_LICENSE_CURRENTIME,
				TextFormater.getCurrentDateStringForErrorReport());
	}

	/**
	 * 
	 * 设置当前Server current time .
	 * 
	 * @param c
	 * @param deadline
	 */
	public static void setSysLicenseCurrentTime(Context c, String deadline) {
		settings = getSP(c);
		settings.edit()
				.putString(EMMPrefContant.SYS_LICENSE_CURRENTIME, deadline)
				.commit();
	}

}