package com.foxconn.emm.utils;

import java.io.File;

import com.foxconn.emm.xmpp.XmppManager;

import android.os.Environment;
import android.text.TextUtils;

public class EMMContants {

	public static final boolean DEBUG = false;

	public static final boolean SHOW_WELCOME_license_flag = Boolean.FALSE;

	/**
	 * emm system configuration
	 */
	public static class SYSCONF {

		public static final int SYS_WELCOME_DELAY = 1500;

		// public static final String BASE_HOST = "http://mdmss.foxconn.com/";
		// public static final String BASE_HOST = "http://emm.foxconn.com:8080";

		// public static final String BASE_HOST_ONLY = "http://10.207.248.21";
		// public static final String BASE_HOST_ONLY = "http://emm.foxconn.com";
		public static final String BASE_HOST_ONLY = "http://mdmss.foxconn.com";

		public static final String BASE_HOST_PORT_8080 = ":8080/";

		public static final String BASE_HOST_PORT_80 = ":80/";

		public static final String BASE_HOST_8080 = BASE_HOST_ONLY
				+ BASE_HOST_PORT_8080;

		public static final String BASE_HOST_80 = BASE_HOST_ONLY
				+ BASE_HOST_PORT_80;

		/**
		 * http://10.207.248.21:8080/emm/
		 */
		public static final String SERVER_URL = BASE_HOST_80 + "emm/";

		/**
		 * http://10.207.248.21/version/version.test.xml
		 */
		 public static final String SYS_APP_UPDATE_URL = BASE_HOST_80+
		 "version/version_outer.xml";

//		public static final String SYS_APP_UPDATE_URL = BASE_HOST_80
//				+ "version/version_inner.xml";
		// / version_inner.xml

		/**
		 * http://10.207.248.21:8080/emm/deviceService_register.action?
		 * phoneNumber=15238005983&userId=ceshi003&deviceName=Infocusaa&
		 * manufacturer=1&platefromVer=1&model=1&os=1&plateFromSDK=1&
		 * registWifiSsid=1&registWifiIp=1&registWifiMac=1&simNetwork=1&
		 * mac=00:51:ss:af:fs:vf&emmPnId=0051ssaffsgvf&passWord=123
		 * 
		 */
		public static final String REQ_ENTROLL = SERVER_URL
				+ "deviceService_register.action?";

		/**
		 * http://10.207.248.21:8080/emm/deviceService_saveLocation.action?
		 * 
		 * mac=00:51:ss:af:fs:vf&location=555.454,4788454.45
		 */
		public static final String UPDATE_LOCATION = SERVER_URL
				+ "deviceService_saveLocation.action?";

		/**
		 * 
		 * sync_syncPolicyInfo.action?mac=00:51:ss:saf:fs:vf
		 */
		public static final String SYNCPOLICY_MAC = SERVER_URL
				+ "sync_syncPolicyInfo.action?";

		/**
		 * 
		 * 
		 * <b>进行设备端流量信息的同步</b>
		 */
		public static final String SYNC_DEVICETRAFFIC = SERVER_URL
				+ "sync_syncdevicetraffic.action?";

		/**
		 * http://10.207.248.21:8080/emm/sync_syncapptraffic.action?
		 * mac=DEV00000000000001310& appTraffics=[
		 * {"PACKAGE_NAME":"com.foxconn.app", "APP_NAME":"weixin"
		 * ,"RECEIVEDTOTAL":"200", "UPLOADTOTAL":"200", "TRAFFIC_TDAY":"20",
		 * "TRAFFIC_TMONTH":"9", "TRAFFIC_TYEAR":"2014" } , {
		 * "PACKAGE_NAME":"com.foxconn.app", "APP_NAME":"QQ"
		 * ,"RECEIVEDTOTAL":"200", "UPLOADTOTAL":"250", "TRAFFIC_TDAY":"20",
		 * "TRAFFIC_TMONTH":"9", "TRAFFIC_TYEAR":"2014" } ]
		 * <b>进行设备端app的流量信息同步</b>
		 */
		public static final String SYNC_APPSTRAFFIC = SERVER_URL
				+ "sync_syncapptraffic.action?";

		/**
		 * http://10.207.248.21:8080/emm/sync_syncTrafficInfo.action?mac=64:09:
		 * 80:c4:9a:73 进行当前设备流量设定的同步.
		 * 
		 */
		public static final String SYNC_TRAFFICINFO_LIMIT = SERVER_URL
				+ "sync_syncTrafficInfo.action?";

		/**
		 * http://10.207.248.21:8080/emm/UploadUserImg
		 */
		public static final String UPLOAD_HEADIMG = SERVER_URL
				+ "UploadUserImg";

		/**
		 * http://mdmss.foxconn.com/selfservice/
		 */
		public static final String SERVER_URL2 = BASE_HOST_8080
				+ "selfservice/";

		/**
		 * http://10.207.248.21:8080/emm/sync_syncUserInfo.action?userId=
		 * ceshi001
		 */
		public static final String REQ_USERINFO = SERVER_URL
				+ "sync_syncUserInfo.action?";

		/**
		 * 
		 * http://10.207.248.21:8080/emm/sync_syncdevicetraffic.action?
		 * mac=DEV00000000000001310&receivedTotal=122&
		 * uploadTotal=345&totalData=456&totalWlan=678&trafficTday=22&
		 * trafficTmonth=9&trafficTyear=2014
		 * 
		 */
		public static final String SYNC_DEVICE_TRAFFIC = SERVER_URL
				+ "sync_syncdevicetraffic.action?";

		/**
		 * http://10.207.248.21:8080/emm/deviceService_saveLocation.action?mac=
		 * 00:51:ss:af:fs:vf&location=555.454,4788454.45 X,Y
		 */
		public static final String SYNC_UPDATELOCATION = SERVER_URL
				+ "deviceService_saveLocation.action?";

		/**
		 * 
		 * http://10.207.248.21:8080/emm/sync_syncDeviceLimitApp.action?mac=
		 * DEV00000000000001310&limitListApp=[ { "PACKAGE_LIMIT_TYPE":"0",
		 * "LIMIT_TYPE":"", "LIMIT_PWD_TIME":"", "SOFTWARE_NAME":"qq",
		 * "SOFTWARE_PACKAGENAME":"com.qq", "SOFTWARE_VERSION":"5.2",
		 * "SOFTWARE_SIZE":"21M", "SOFTWARE_RUNNING":"1" },{
		 * "PACKAGE_LIMIT_TYPE":"1", "LIMIT_TYPE":"", "LIMIT_PWD_TIME":"",
		 * "SOFTWARE_NAME":"weixin", "SOFTWARE_PACKAGENAME":"com.weixin",
		 * "SOFTWARE_VERSION":"2.6", "SOFTWARE_SIZE":"15M",
		 * "SOFTWARE_RUNNING":"1" },{ "PACKAGE_LIMIT_TYPE":"2",
		 * "LIMIT_TYPE":"1", "LIMIT_PWD_TIME":"30", "SOFTWARE_NAME":"feiqiu",
		 * "SOFTWARE_PACKAGENAME":"com.fe", "SOFTWARE_VERSION":"1.2",
		 * "SOFTWARE_SIZE":"16M", "SOFTWARE_RUNNING":"1" },{
		 * "PACKAGE_LIMIT_TYPE":"2", "LIMIT_TYPE":"0",
		 * "LIMIT_PWD_TIME":"asd123", "SOFTWARE_NAME":"cloud",
		 * "SOFTWARE_PACKAGENAME":"com.cloud", "SOFTWARE_VERSION":"3.2",
		 * "SOFTWARE_SIZE":"11M", "SOFTWARE_RUNNING":"1" } ]
		 * 
		 */
		public static final String SYNC_LIMITLIST = SERVER_URL
				+ "sync_syncDeviceLimitApp.action?";

		/**
		 * http://10.207.248.21:8080/emm/sync_syncDeviceSoftWare.action?mac=
		 * DEV00000000000001310&softWare=[ {"SOFTWARE_NAME":"qq",
		 * "SOFTWARE_PACKAGENAME":"com.qq", "SOFTWARE_VERSION":"5.2",
		 * "SOFTWARE_SIZE":"21M", "SOFTWARE_RUNNING":"1" },
		 * {"SOFTWARE_NAME":"weixin", "SOFTWARE_PACKAGENAME":"com.weixin",
		 * "SOFTWARE_VERSION":"2.5", "SOFTWARE_SIZE":"15M",
		 * "SOFTWARE_RUNNING":"0" } ]
		 * 
		 */
		public static final String SYNC_DEVICE_SOFTWARE = SERVER_URL
				+ "sync_syncDeviceSoftWare.action?";

		/**
		 * http://10.207.248.21:8080/emm/sync_syncTrafficInfo.action?mac=
		 * DEV00000000000001310
		 */
		public static final String SYNC_DEVICE_TRAFFICLIMIT = SERVER_URL
				+ "sync_syncTrafficInfo.action?";

		/**
		 * http://10.207.248.21:8080/emm/deviceService_saveDeviceNum.action?mac=
		 * 00:51:ss:saf:fs:vf&deviceOwnerNum=14141454546546546546
		 */
		public static final String UPDATE_DEVICE_NUMBER = SERVER_URL
				+ "deviceService_saveDeviceNum.action?";

		public static final String FILE_URL = BASE_HOST_8080 + "myfile";
		// public static final String FILE_URL = "http://10.207.248.21/myfile";

		/**
		 * http://mdmss.foxconn.com/help/help.html
		 */
		public static final String USER_SPECIFICATION_URL = BASE_HOST_80
				+ "help/help.html";

		/**
		 * 系统崩溃发送错误报告至该email cloud-mdm@mail.foxconn.com
		 */
		public static final String SYS_CREASH_ERROR_REPSORT_MAIL = "cloud-mdm@mail.foxconn.com";

		public static final String SYS_CREASH_ERROR_REPORT_URL = "http://mdmss.foxconn.com/activiti-rest2/service/sendmail";

		/**
		 * License首次登陆验证接口
		 * license_checkLicenseAction.action?LicenseNo=1000200030004
		 * &EnterpriseName
		 * =Foxconn&DeviceNo=10251200&DeviceName=B1&AppVersion=1.0
		 * .2&DeviceOS=IOS
		 * 
		 */
		public static final String SYS_LICENSE_CHECKATCION = SERVER_URL
				+ "license_checkLicenseAction.action?";

		/**
		 * License 非首次验证,用于验证License是否过期. deviceNo 设备编号.
		 * license_checkfailuerTimeAction
		 * .action?LicenseNo=1000200030004&EnterpriseName
		 * =Foxconn&DeviceNo=10251200
		 * &DeviceName=B1&AppVersion=1.0.2&DeviceOS=IOS
		 */
		public static final String SYS_LICENSE_USUAL_CHECKACTION = SERVER_URL
				+ "license_checkfailuerTimeAction.action?";

		/**
		 * License 重新激活接口
		 * 
		 * license_reactivate.action?LicenseNo=1000200030004&EnterpriseName=
		 * Foxconn&DeviceNo=10251200&DeviceName=B1&AppVersion=1.0.2&DeviceOS=IOS
		 */
		public static final String SYS_LICENSE_REACTIVATEACTION = SERVER_URL
				+ "license_reactivate.action?";

	}

	/**
	 * XMPP 的配置常量
	 * 
	 */
	public static class XMPPCONF {

		public static final String SHARED_PREFERENCE_NAME = "client_preferences";

		// PREFERENCE KEYS

		public static final String XMPP_BASEHOST = SYSCONF.BASE_HOST_8080;

		public static final int XMPP_BASEPORT = 5222;

		public static final String XMPP_APIKEY = "123456789";

		public static final String API_KEY = "API_KEY";

		public static final String XMPP_HOST = "XMPP_HOST";

		public static final String XMPP_PORT = "XMPP_PORT";

		public static final String XMPP_USERNAME = "XMPP_USERNAME";

		public static final String XMPP_PASSWORD = "XMPP_PASSWORD";

		public static final String USER_ID = "USER_ID";

		public static final String SETTINGS_SOUND_ENABLED = "SETTINGS_SOUND_ENABLED";

		public static final String SETTINGS_VIBRATE_ENABLED = "SETTINGS_VIBRATE_ENABLED";

		public static final String SETTINGS_TOAST_ENABLED = "SETTINGS_TOAST_ENABLED";

		// NOTIFICATION FIELDS

		public static final String NOTIFICATION_ID = "NOTIFICATION_ID";

		public static final String NOTIFICATION_API_KEY = "NOTIFICATION_API_KEY";

		public static final String NOTIFICATION_TITLE = "NOTIFICATION_TITLE";

		public static final String NOTIFICATION_MESSAGE = "NOTIFICATION_MESSAGE";

		public static final String ACTION_PROCESS_NOTIFICATION = "org.androidpn.client.SHOW_NOTIFICATION";

		public static final String ACTION_NOTIFICATION_CLICKED = "org.androidpn.client.NOTIFICATION_CLICKED";

		public static final String ACTION_NOTIFICATION_CLEARED = "org.androidpn.client.NOTIFICATION_CLEARED";

		public static XmppManager xmppManager = null;
	}

	/**
	 * 对限制名单类别的描述 * 限制名单的类型 0:黑名单 1:白名单 2:限制名单 <br>
	 * 限制名单的限制类别: 1:按照时间限制 0:按照密码进行限制
	 * 
	 */
	public static class LIMITLIST_CONTANT {
		/**
		 * 0:黑名单
		 */
		public static final int LT_BLACK_LIST = 0;
		/**
		 * 1:白名单
		 */
		public static final int LT_WHITE_LIST = 1;
		/**
		 * 2:限制名单
		 */
		public static final int LT_LIMIT_LIST = 2;

		/**
		 * 限制名单的限制类别: 1:按照时间限制
		 */
		public static final int LLT_TIME = 1;

		/**
		 * 限制名单的限制类别: 0:按照密码进行限制
		 */
		public static final int LLT_PASSWORD = 0;

		/**
		 * 按照时间限制的标识
		 */
		public static final int LLT_FLAG_TIME = 21;

		/**
		 * 按照密码限制的标识
		 */
		public static final int LLT_FLAG_PASSWORD = 20;

	}

	/**
	 * 本地信息的一些配置
	 * 
	 */
	public static class LocalConf {

		static {

			/** 初始化下载头像的路径. */
			initImageDownFullDir();
			/** 初始化下载Apk的文件路径 */
			initAppDownFullDir();
			/** 初始化下载文件的加密路径 */
			initEncryptionFileDownFullDir();
			/** 初始化下载文件的解密路径 */
			initDecryptionFileDownFullDir();
			/** 初始化下载文件的路径,包括图片,pdf文档. */
			initDownloadsFileFillDir();
		}

		/**
		 * 描述：获取默认的图片保存全路径.
		 * 
		 * @return 完成的存储目录
		 */
		private static void initImageDownFullDir() {
			try {
				String emmLocalHost_dirPath = getEMMLocalHost_dirPath();
				if (TextUtils.isEmpty(emmLocalHost_dirPath)) {
					return;
				}
				// 初始化图片保存路径
				File dirFile = new File(emmLocalHost_dirPath + HeadIcon_dirpath);
				if (!dirFile.exists()) {
					dirFile.mkdirs();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * 描述：获取默认的文件保存全路径.
		 * 
		 * @return 完成的存储目录
		 */
		private static void initAppDownFullDir() {
			try {
				String emmLocalHost_dirPath = getEMMLocalHost_dirPath();
				if (TextUtils.isEmpty(emmLocalHost_dirPath)) {
					return;
				}

				File dirFile = new File(emmLocalHost_dirPath
						+ UpdateApp_dirpath);
				if (!dirFile.exists()) {
					dirFile.mkdirs();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * 描述：获取默认的加密文件保存全路径.
		 * 
		 * @return 完成的存储目录
		 */
		private static void initEncryptionFileDownFullDir() {
			try {
				String emmLocalHost_dirPath = getEMMLocalHost_dirPath();
				if (TextUtils.isEmpty(emmLocalHost_dirPath)) {
					return;
				}

				File dirFile = new File(emmLocalHost_dirPath
						+ encryption_dirpath);
				if (!dirFile.exists()) {
					dirFile.mkdirs();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * 描述：获取默认的解密文件保存全路径.
		 * 
		 * @return 完成的存储目录
		 */
		private static void initDecryptionFileDownFullDir() {
			try {
				String emmLocalHost_dirPath = getEMMLocalHost_dirPath();
				if (TextUtils.isEmpty(emmLocalHost_dirPath)) {
					return;
				}

				File dirFile = new File(emmLocalHost_dirPath
						+ decryption_dirpath);
				if (!dirFile.exists()) {
					dirFile.mkdirs();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private static void initDownloadsFileFillDir() {
			try {
				String emmLocalHost_dirPath = getEMMLocalHost_dirPath();
				if (TextUtils.isEmpty(emmLocalHost_dirPath)) {
					return;
				}
				File dirFile = new File(emmLocalHost_dirPath + download_dirpath);
				if (!dirFile.exists()) {
					dirFile.mkdirs();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public static boolean isSdPresent() {
			return android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED);
		}

		/**
		 * 获取EMM本地存储的根路径.
		 * 
		 * @return 如果SD卡不存在,或状态不可用则返回""; 可用则返回SD卡根目录路径.
		 */
		public static String getEMMLocalHost_dirPath() {
			if (isSdPresent()) {
				return Environment.getExternalStorageDirectory()
						.getAbsolutePath();
			} else {
				// / SD 卡不存在
				throw new IllegalArgumentException("sd card is not exists..");
			}
		}

		/**
		 * 本地目录存储的根目录
		 */
		public static final String LocalHost_dirpath = "/.emm";

		/**
		 * 头像存储的目录
		 */
		public static final String HeadIcon_dirpath = LocalHost_dirpath
				+ "/.emmimg/";

		/**
		 * app 更新 apk存放的目录
		 */
		public static final String UpdateApp_dirpath = LocalHost_dirpath
				+ "/.updateapk/";

		/**
		 * 文件下载的加密目录
		 */
		public static final String encryption_dirpath = LocalHost_dirpath
				+ "/.encryptionfiles/";
		/**
		 * 文件解密的解密目录
		 */
		public static final String decryption_dirpath = LocalHost_dirpath
				+ "/.decryptionfiles/";

		/**
		 * 放置下载完成的图片,文件.
		 */
		public static final String download_dirpath = LocalHost_dirpath
				+ "/.downloads/";

	}

	public static class EMMAction {

		/**
		 * Enroll Server (off-line)
		 */
		public static final String ENROLLSERVER_ACTION = "com.foxconn.app.EnrollTaskReceiver";

		/**
		 * Intent's extra that contains the message to be displayed.
		 */
		public static final String EXTRA_MESSAGE = "message";

		/**
		 * Syncpolicy action .
		 * 
		 */
		public static final String SYNCPOLICY_ACTION = "com.foxconn.app.ProcessTaskReceiver";

		/**
		 * 监测EMM NotificationService 是否停止,进行重连操作.
		 */
		public static final String EMM_CHECK_NOTIFICAITONSERVICE = "com.foxconn.app.emm.CheckNotificationService";

		/**
		 * 检查更新
		 */
		public static final String EMM_CHECK_APP_UPDATE = "com.foxconn.app.emm.CheckAppUpdate";

		/**
		 * login
		 * 
		 */
		public static final String LOGIN_ACTION = "com.emm.action.LOGIN";
		/**
		 * 监测监测Service是否停止工作.进行启动操作.
		 */
		public static final String EMM_CHECK_MONITORSERVICE = "com.foxconn.app.emm.CheckMonitorService";

	}

	public static class EMMPrefContant {

		/**
		 * 设备注册已注册
		 */
		public static final String ENROLL_STATUS_Y = "Y";
		/**
		 * 设备未注册
		 */
		public static final String ENROLL_STATUS_N = "N";
		/**
		 * 设备使用的离线注册,没有进行在线注册.
		 */
		public static final String ENROLL_STATUS_P = "P";

		public static final String SYS_STATUS = "sys_status"; // 記錄系統資訊(登入狀態、後台網址)

		public static final String POLICY_DATA = "policy_data";

		public static final String QRPOLICY_DATA = "qr_policy_data";

		public static final String FILE_STATUS = "file_status";
		// System Status
		public static final String ENROLLSTATUS = "EnrollStatus";

		public static final String CURRENT_POLICY_DATA = "CURRENT_POLICY_DATA";

		public static final String ENROLL_SERVER_URL = "ENROLL_SERVER_URL";

		public static final String USER_ID = "USER_ID";

		public static final String DEVICE_LOCATION = "DEVICE_LOCATION";

		public static final String DEVICE_STREET_LOCATION = "DEVICE_STREET_LOCATION";

		public static final String USERBG_ID = "USERBG_ID";

		public static final String USERINFO_SYNCTIME = "USERINFO_SYNCTIME";

		/**
		 * get the license verify result is pass or not
		 * 
		 */
		public static final String IS_LICENSE_PASS = "IS_LICENSE_PASS";
		/**
		 * is enable sys license
		 */
		public static final String IS_LICENSE_ENABLE = "IS_LICENSE_ENABLE";
		/**
		 * Sys License no
		 * 
		 */
		public static final String SYS_LICENSE_NO = "SYS_LICENSENO";

		/**
		 * Sys License enterprise name
		 */
		public static final String SYS_LICENSE_ENTERPRISENAME = "SYS_LICENSE_ENTERPRISE";

		/**
		 * sys license deadline
		 */
		public static final String SYS_LICENSE_DEADLINE = "SYS_LICENSE_DEADLINE";

		/**
		 * sys license current time
		 */
		public static final String SYS_LICENSE_CURRENTIME = "SYS_LICENSE_CURRENTIME";

		/**
		 * 记录上次检测更新的时间.
		 */
		public static final String CHECKUPDATE_TIME = "CHECKUPDATE_TIME";

		public static final String DEVICE_SERAIL_ID = "DEVICE_SERAIL_ID";

		/**
		 * 设备流量限制.
		 * 
		 */
		public static final String DEVICE_TRAFFIC_LIMIT = "DEVICE_TRAFFIC_LIMIT";

		/**
		 * 设备的流量大小
		 */
		public static final String TOTAL_DEVICE_TRAFFIC = "TOTAL_DEVICE_TRAFFIC";

		public static final String IMEI = "IMEI";

		public static final String IS_SECURITY_OPENED = "IS_SECURITY_OPENED";

		public static final String IS_SECURITY_AUTHORIZED = "IS_SECURITY_AUTHORIZED";

		// 位置编号 20140506
		public static final String LOCATION_CODE = "LOCATION_CODE";

		// record current network status boolean type
		public static final String NETWORK_STATUS = "network_status";

		// off line enroll 20140507
		public static final String OFFLINE_ENROLL_STATUS = "offline_enroll_status";

		// take head imgae 20140508
		public static final String HASHEADIMG_UPLOAD = "hasheadimg_upload";

		public static final String POLICY_JSON = "policy_json";

		public static final String POLICY_ID = "policy_id";
		public static final String PRIORITY = "priority";
		public static final String REQUIRED_PW = "required_pw";
		public static final String PW_FORMAT = "pw_format";
		public static final String PW_LENGTH = "pw_length";
		public static final String PW_ERROR_COUNT = "pw_error_count";
		public static final String PW_LOCK_TIME = "pw_lock_tiem";
		public static final String PW_RESET_PERIOD = "pw_reset_period";
		public static final String AUTO_SYNCH = "auto_synch";
		public static final String AROAMING = "aroaming";
		public static final String ENABLE_BLUETOOTH = "enable_bluetotoh";
		public static final String SCAN_DEVICES = "scan_devices";
		public static final String ENABLE_WIFI = "enable_wifi";
		public static final String ENABLE_SSID = "enable_ssid";
		public static final String SSID = "SSID";
		public static final String HIDDEN_SSID = "hidden_ssid";
		public static final String SSID_SECURITY = "ssid_security";
		public static final String SSID_PRE_SHAREDKEY = "ssid_pre_sharedkey";
		public static final String SSID_WEP_KEY1 = "ssid_wep_key1";
		public static final String ENABLE_CAMERA = "enable_camera";
		public static final String ENABLE_GOOGLE_PLAY = "enable_google_play";
		public static final String ENABLE_USB = "enable_usb";
		public static final String HIDDEN_APP = "hidden_app";

		// fileStatus
		public static final String IS_INTENT = "is_intent";

	}

	public static class ReqContants {

		// //reg entroll device
		public static final String reg_USER_ID = "USER_ID";
		public static final String reg_PASSWORD = "PASSWORD";
		public static final String reg_DEVICE = "DEVICE";
		public static final String reg_OS = "OS";
		public static final String reg_MANUFACTURER = "MANUFACTURER";
		public static final String reg_MODEL = "MODEL";
		public static final String reg_IMEI = "IMEI";
		public static final String reg_DEVICE_OWNER = "DEVICE_OWNER";
		public static final String reg_STATUS = "STATUS";
		public static final String reg_ISDEL = "ISDEL";
		public static final String reg_PHONE_NUMBER = "PHONE_NUMBER";
		public static final String reg_PLATFORM_VER = "PLATFORM_VER";
		public static final String reg_CURRENT_NETWORK = "CURRENT_NETWORK";
		public static final String reg_SIM_NETWORK = "SIM_NETWORK";
		public static final String reg_GCMID = "GCMID";

		public static final String reg_OffLine_PWD = "MDMService-GDSBG-SMI-20140221";

	}

	public static class RefListContants {
		public static final String TAG = "listview";

		public final static int RELEASE_To_REFRESH = 0;
		public final static int PULL_To_REFRESH = 1;
		public final static int REFRESHING = 2;
		public final static int DONE = 3;
		public final static int LOADING = 4;

		// 实际的padding的距离与界面上偏移距离的比例
		public final static int RATIO = 3;
	}

	public static class MsgType {
		public static final String MSGTYPE = "msgType";
		public static final String SENDFILE = "sendFile";
		public static final String SENDCALENDAR = "sendCalendar";
		public static final String SENDNOTIFICATION = "sendNotification";
		public static final String SENDPAGE = "sendPage";
		public static final String SENDIMAGE = "sendImage";
		public static final String SENDAPPS = "sendapps";
		public static final String SENDWHITELIST = "sendWhiteList";
		public static final String SENDBLACKLIST = "sendBlackList";
		public static final String SENDLIMITLIST = "sendLimitList";
		public static final String SENDCLEANINFO = "cleanInfo";
		public static final String REMOVEWHITELIST = "removeWhiteList";
		public static final String REMOVEBLACKLIST = "removeBlackList";
		public static final String REMOVELIMITLIST = "removeLimitList";
		public static final String RESET = "reset";
		public static final String REMOVECONTROL = "removeControl";
		public static final String LOCKDEV = "lockDev";
		public static final String UNLOCK = "unLock";
		public static final String SYNCINFO = "syncInfo";
		public static final String UPDATELOCATION = "updateLocation";

		public static final String SEND_SETBLACKLISTAPP = "setBlackApp";
		// / 发送流量限制。
		public static final String SEND_TRAFFICLIMIT = "sendtrafficlimit";
		// / 发送移除流量限制。
		public static final String REMOVE_TRAFFICLIMIT = "removetrafficlimit";

		public static final String SEND_POLICY = "sendpolicy";

		public static final String DELETE_POLICY = "deletepolicy";

		public static final String NOTIFICATION = "通知";
	}

	public static class ToastText {
		public static final String DELETSUCCESS = "删除成功！";
		public static final String DELETFail = "删除失败！";
	}

	public static class DialogText {
		public static final String DELETETITLE = "删除";
		public static final String DELETECONTECT = "确定要删除此条信息吗？";
		public static final String DELETELOCATION = "确定要删除设备编号吗？";
		public static final String NEGATIVEBUTTON = "返回";
		public static final String POSITIVEBUTTON = "确定";
		public static final String SETTINGCALENDAR = "设置";
		public static final String DOWNLOAD = "下载";
		public static final String OPEN = "打开";
		public static final String INSTALL = "安装";
		public static final String DOWNLOADAPP = "是否下载此应用？";
		public static final String PROMPT = "提示";
		public static final String PROMPT_CONTENT = "此信息已过期，是否删除？";
		public static final String CONTACT = "联系人：";
	}

	public static class DialogFlag {
		public static final int SHOW = 0;
		public static final int DELETE = 1;
		public static final int OPEN = 2;
		public static final int INPUT_PWD = 3;
	}

	public static class FrgNotiContants {
		public static final String TAG = "FrgNotificationMgr";
		public static final int LOAD_FINISH = 1;
		public static final int LOAD_FILE = 3;
		public static final int LOAD_ERROR = 0;
		public static final int LOAD_ZERO = 2;

		public static final String SEND_GCM = "sendgcm";
		public static final String SENDFILEURL = "sendfileurl";
		public static final String CALENDAR = "sendcalendar";
		public static final String SEND_POLICY = "sendpolicy";
		public static final String SYNCPOLICY = "syncpolicy";
		public static final String MESSAGE = "通知";
		public static final String FILE = "文件";
		public static final String calendar = "日历";
		public static final String POLICY = "安全管控通知";

		public static final int DEL = 0;
	}

	public static class EMMUSERINFO {

		public static final String USERID_YDZZ = "YUNDA-ZZ";

		public static final String USERID_IEZZ = "IE-ZZ";

		public static final String USERID_NORMAIL = "USER_ID_NORMAIL";

	}

}
