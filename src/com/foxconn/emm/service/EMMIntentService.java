package com.foxconn.emm.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.foxconn.emm.bean.AppInfo;
import com.foxconn.emm.bean.CalendarInfo;
import com.foxconn.emm.bean.FileInfo;
import com.foxconn.emm.bean.LimitListInfo;
import com.foxconn.emm.bean.MsgCommonInfo;
import com.foxconn.emm.bean.NotificationInfo;
import com.foxconn.emm.bean.PicInfo;
import com.foxconn.emm.bean.SysTraffic;
import com.foxconn.emm.bean.UidTraffic;
import com.foxconn.emm.bean.WebPageInfo;
import com.foxconn.emm.dao.AppInfoDao;
import com.foxconn.emm.dao.CalendarInfoDao;
import com.foxconn.emm.dao.FileInfoDao;
import com.foxconn.emm.dao.NotificationInfoDao;
import com.foxconn.emm.dao.PicInfoDao;
import com.foxconn.emm.dao.TrafficAppsOptDao;
import com.foxconn.emm.dao.TrafficOptDao;
import com.foxconn.emm.dao.WebPageInfoDao;
import com.foxconn.emm.receiver.EMMMessageCallBackReceiver;
import com.foxconn.emm.tools.PolicyControl;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.utils.EMMReqParamsUtils;
import com.foxconn.emm.utils.HttpClientUtil;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.xmpp.NotificationIQ;
import com.foxconn.emm.xmpp.listener.NotificationPacketListener;

/**
 * EMMIntentService 主要用于处理Server端发送各类指令的处理, 可考虑将Policy,消息,日历,清除用户信息等处理放置此进行处理
 * 
 * 
 */
public class EMMIntentService extends EMMBaseIntentService {

	PolicyControl policyControl;

	ExecutorService executorService;

	private TrafficAppsOptDao trafficAppsOptDao;

	private TrafficOptDao trafficOptDao;

	public EMMIntentService() {
		this(EMMIntentService.class.getSimpleName());
	}

	public EMMIntentService(String name) {
		super(name);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		policyControl = new PolicyControl(this);
		executorService = Executors.newFixedThreadPool(2);
		trafficAppsOptDao = new TrafficAppsOptDao(this);
		trafficOptDao = new TrafficOptDao(this);

	}

	/**
	 * 接收到消息的主要处理逻辑方法
	 * 
	 * 
	 * 
	 */
	@Override
	protected void onMessage(Context context, Intent intent) throws Exception {
		NotificationIQ notificationIQ = (NotificationIQ) intent
				.getSerializableExtra(NotificationPacketListener.FLAG_NOTIFICATION_PACKETNAME);
		String message = notificationIQ.getMessage();

		String msgType = getMsgType(message);
		L.d(this.getClass(), message);
		if (msgType != null) {
			if (msgType.equals(EMMContants.MsgType.SENDCALENDAR)) {
				long l = parseCalendar(message, context);
				if (l > 0)
					L.d(this.getClass(), "存储日历成功");
			} else if (msgType.equals(EMMContants.MsgType.SENDNOTIFICATION)) {
				parseNotification(message, context);
			} else if (msgType.equals(EMMContants.MsgType.SENDFILE)) {
				parseFile(message, context);
			} else if (msgType.equals(EMMContants.MsgType.SENDPAGE)) {
				parseWebPage(message, context);
			} else if (msgType.equals(EMMContants.MsgType.SENDBLACKLIST)) {
				parseLimitListSend(message, context);
			} else if (msgType
					.equalsIgnoreCase(EMMContants.MsgType.SEND_SETBLACKLISTAPP)) {
				parseLimitListSend(message, context);
			} else if (msgType.equals(EMMContants.MsgType.SENDWHITELIST)) {
				parseLimitListSend(message, context);
			} else if (msgType.equals(EMMContants.MsgType.SENDLIMITLIST)) {
				parseLimitListSend(message, context);
			} else if (msgType.equals(EMMContants.MsgType.SENDCLEANINFO)) {
				parseCommonMsg(message, context);
			} else if (msgType.equals(EMMContants.MsgType.LOCKDEV)) {
				parseCommonMsg(message, context);
			} else if (msgType.equals(EMMContants.MsgType.REMOVEBLACKLIST)) {
				parseLimitListRemove(message, context);
			} else if (msgType.equals(EMMContants.MsgType.REMOVEWHITELIST)) {
				parseLimitListRemove(message, context);
			} else if (msgType.equals(EMMContants.MsgType.REMOVELIMITLIST)) {
				parseLimitListRemove(message, context);
			} else if (msgType.equals(EMMContants.MsgType.REMOVECONTROL)) {
				parseCommonMsg(message, context);
			} else if (msgType.equals(EMMContants.MsgType.RESET)) {
				parseCommonMsg(message, context);
			} else if (msgType.equals(EMMContants.MsgType.SYNCINFO)) {
				parseCommonMsg(message, context);
				if (executorService == null || executorService.isShutdown()
						|| executorService.isTerminated()) {
					executorService = Executors.newFixedThreadPool(2);
				}
				executorService.submit(new SyncDeviceSoftwareTask());
				L.d(this.getClass(), "SyncDeviceSoftwareTask  submit ... ");
			} else if (msgType.equals(EMMContants.MsgType.UNLOCK)) {
				parseCommonMsg(message, context);
			} else if (msgType.equals(EMMContants.MsgType.UPDATELOCATION)) {
				if (executorService == null || executorService.isShutdown()
						|| executorService.isTerminated()) {
					executorService = Executors.newFixedThreadPool(2);
				}
				executorService.submit(new UpdateLocationTask());
				L.d(this.getClass(), "update location task submit ... ");
			} else if (msgType.equals(EMMContants.MsgType.SENDIMAGE)) {
				parseImage(message, context);
			} else if (msgType.equals(EMMContants.MsgType.SENDAPPS)) {
				parseApps(message, context);

			} else if (msgType.equals(EMMContants.MsgType.SEND_TRAFFICLIMIT)) {
				EMMPreferences.setTrafficInfoLimit(context, message);
				L.d(this.getClass(), " set device traffic limit succes ");
			} else if (msgType.equals(EMMContants.MsgType.REMOVE_TRAFFICLIMIT)) {
				EMMPreferences.setTrafficInfoLimit(context, "");
				L.d(this.getClass(), " remove device traffic limit succes ");
			} else if (msgType
					.equalsIgnoreCase(EMMContants.MsgType.SEND_POLICY)) {
				if (TextUtils.isEmpty(message)
						|| message.equalsIgnoreCase("null")) {
					L.i(this.getClass(),
							"sendpolicy error ,cause  message is null ");
					return;
				} else {
					EMMPreferences.setPolicyData(context, message);
					policyControl.enablePolicy(message);
				}
			} else if (msgType
					.equalsIgnoreCase(EMMContants.MsgType.DELETE_POLICY)) {
				if (TextUtils.isEmpty(message)
						|| message.equalsIgnoreCase("null")) {
					L.i(this.getClass(),
							"deletepolicy error ,cause  message is null ");
					return;
				} else {
					EMMPreferences.setPolicyData(context, "");
					policyControl.enableDefaultPolicy();
				}
			}
		}

	}

	/**
	 * add blacklist to db by contentprovider
	 * 
	 * @param packName
	 */
	public void addToLimitList(LimitListInfo limitListInfo, Context context) {
		ContentValues values = new ContentValues();
		values.put(LimitListInfo.TAG.PACKNAME, limitListInfo.getPackName());
		values.put(LimitListInfo.TAG.DESC, limitListInfo.getDesc());
		values.put(LimitListInfo.TAG.LIMITDISABLETIME,
				limitListInfo.getLimitDisableTime());
		values.put(LimitListInfo.TAG.LIMITENABLETIME,
				limitListInfo.getLimitEnableTime());
		values.put(LimitListInfo.TAG.LIMITLIMITTYPE,
				limitListInfo.getLimitLimitType());
		values.put(LimitListInfo.TAG.LIMITPWDTIME,
				limitListInfo.getLimitPwdTime());
		values.put(LimitListInfo.TAG.LIMITTYPE, limitListInfo.getLimitType());
		context.getContentResolver().insert(
				Uri.parse("content://com.foxconn.emm.unlockprovider/insert"),
				values);
	}

	/**
	 * remove blacklist from db by contentprovider
	 * 
	 * @param packName
	 */
	public void removeLimitList(String packName, String limitType,
			Context context) {
		// ContentValues values = new ContentValues();
		// values.put("packname", packName);

		String[] values = new String[] { packName, limitType };
		context.getContentResolver().delete(
				Uri.parse("content://com.foxconn.emm.unlockprovider/delete"),
				"packname=? and limitType =? ", values);
	}

	/**
	 * 
	 * {"sendTime":"20141014141439","msgType":"sendWhiteList","whiteListApp":
	 * "com.qq.weixin,com.baidu.iqiyi,"
	 * ,"enableTime":"2014-10-08 14:25:04","disableTime"
	 * :"2014-10-14 14:25:02","desc":"白名单测试,2014/10/14"}
	 * 
	 * { "sendTime": "20141014143529", "msgType": "sendLimitList",
	 * "limitListApp":
	 * "com.tencent.mobileqq,com.moji.mjweather ,com.UCMobile ,com.tencent.mm ,com.ss.android.article.news ,com.sds.android.ttpod ,"
	 * , "limitType": "2", "limitLimitType": "0", "limitPwdTime": "123456",
	 * "limitEnableTime": "2014-10-14 14:45:51", "limitDisableTime":
	 * "2014-10-15 14:45:53", "desc": "限制名单测试使用, 2014/10/14 14:46" } {
	 * "sendTime": "20141014143800", "msgType": "sendBlackList", "blackListApp":
	 * "com.tencent.mobileqq,com.moji.mjweather ,com.UCMobile ,com.tencent.mm ,"
	 * , "enableTime": "2014-10-14 14:48:45", "disableTime":
	 * "2014-10-15 14:48:46", "desc": "黑名单测试, 2014/10/14 14:49" }
	 * {"sendTime":"20141121102505"
	 * ,"msgType":"sendWhiteList","whiteListApp":"com.tencent.mobileqq,"
	 * ,"enableTime"
	 * :"2014-11-21 09:52:17","disableTime":"2014-11-22 09:52:19","desc"
	 * :"白名单,黑名单,限制名单混合测试 "}
	 * 
	 * 
	 * @param message
	 * @param context
	 */
	private void parseLimitListSend(String message, Context context)
			throws JSONException {
		LimitListInfo limitListInfo = new LimitListInfo();
		JSONObject jsonObject = new JSONObject(message);
		String messageType = getMsgType(message);
		if (messageType.equalsIgnoreCase(EMMContants.MsgType.SENDWHITELIST)) {
			String limitpackages = jsonObject.getString("whiteListApp");
			List<String> packages = splitLimitPackages(limitpackages);
			if (packages != null && packages.size() >= 1) {
				for (String string : packages) {
					if (TextUtils.isEmpty(string)) {
						continue;
					}
					limitListInfo.setSendTime(jsonObject
							.getString(LimitListInfo.TAG.SENDTIME));
					limitListInfo.setDesc(jsonObject
							.getString(LimitListInfo.TAG.DESC));
					limitListInfo.setLimitDisableTime(jsonObject
							.getString(LimitListInfo.TAG.DISABLE_TIME));
					limitListInfo.setLimitEnableTime(jsonObject
							.getString(LimitListInfo.TAG.ENABLE_TIME));
					limitListInfo.setMsgType(messageType);
					limitListInfo.setPackName(string);
					limitListInfo
							.setLimitType(EMMContants.LIMITLIST_CONTANT.LT_WHITE_LIST
									+ "");
					addToLimitList(limitListInfo, context);
				}
			} else {
				L.w(getClass(), "white list app is null ");
			}
		} else if (messageType
				.equalsIgnoreCase(EMMContants.MsgType.SENDLIMITLIST)) {
			String limitpackages = jsonObject.getString("limitListApp");
			List<String> packages = splitLimitPackages(limitpackages);
			if (packages != null && packages.size() >= 1) {
				for (String string : packages) {
					if (TextUtils.isEmpty(string)) {
						continue;
					}
					limitListInfo.setSendTime(jsonObject
							.getString(LimitListInfo.TAG.SENDTIME));
					limitListInfo.setDesc(jsonObject
							.getString(LimitListInfo.TAG.DESC));
					limitListInfo.setLimitDisableTime(jsonObject
							.getString(LimitListInfo.TAG.LIMITDISABLETIME));
					limitListInfo.setLimitEnableTime(jsonObject
							.getString(LimitListInfo.TAG.LIMITENABLETIME));
					limitListInfo.setLimitLimitType(jsonObject
							.getString(LimitListInfo.TAG.LIMITLIMITTYPE));
					limitListInfo.setMsgType(messageType);
					limitListInfo.setPackName(string);
					limitListInfo
							.setLimitType(EMMContants.LIMITLIST_CONTANT.LT_LIMIT_LIST
									+ "");
					limitListInfo.setLimitPwdTime(jsonObject
							.getString(LimitListInfo.TAG.LIMITPWDTIME));
					addToLimitList(limitListInfo, context);
				}
			} else {
				L.i(getClass(), "limit list app is null ");
			}
		} else if (messageType
				.equalsIgnoreCase(EMMContants.MsgType.SENDBLACKLIST)) {
			String limitpackages = jsonObject.getString("blackListApp");
			List<String> packages = splitLimitPackages(limitpackages);
			if (packages != null && packages.size() >= 1) {
				for (String string : packages) {
					if (TextUtils.isEmpty(string)) {
						continue;
					}
					limitListInfo.setSendTime(jsonObject
							.getString(LimitListInfo.TAG.SENDTIME));
					limitListInfo.setDesc(jsonObject
							.getString(LimitListInfo.TAG.DESC));
					limitListInfo.setLimitDisableTime(jsonObject
							.getString(LimitListInfo.TAG.DISABLE_TIME));
					limitListInfo.setLimitEnableTime(jsonObject
							.getString(LimitListInfo.TAG.ENABLE_TIME));
					limitListInfo.setMsgType(messageType);
					limitListInfo.setPackName(string);
					limitListInfo
							.setLimitType(EMMContants.LIMITLIST_CONTANT.LT_BLACK_LIST
									+ "");
					addToLimitList(limitListInfo, context);
				}
			} else {
				L.i(getClass(), "black list app is null ");
			}
		} else if (messageType
				.equalsIgnoreCase(EMMContants.MsgType.SEND_SETBLACKLISTAPP)) {
			// /{"sendTime":"20141027103043","msgType":"setBlackApp","packageName":"com.mxtech.videoplayer.ad"}
			String setblackListApp = jsonObject.getString("packageName");
			List<String> packages = splitLimitPackages(setblackListApp);
			if (packages != null && packages.size() >= 1) {
				for (String string : packages) {
					if (TextUtils.isEmpty(string)) {
						continue;
					}
					limitListInfo.setSendTime(jsonObject
							.getString(LimitListInfo.TAG.SENDTIME));
					limitListInfo.setMsgType(messageType);
					limitListInfo.setPackName(string);
					limitListInfo
							.setLimitType(EMMContants.LIMITLIST_CONTANT.LT_BLACK_LIST
									+ "");
					addToLimitList(limitListInfo, context);
				}
			} else {
				L.i(getClass(), " set black list app is null ");
			}

		}
		if (executorService == null || executorService.isShutdown()
				|| executorService.isTerminated()) {
			executorService = Executors.newFixedThreadPool(2);
		}
		executorService.submit(new SyncLimitListTask());
	}

	/**
	 * 
	 { "sendTime": "20141014143951", "msgType": "removeBlackList",
	 * "blackListApp": "com.tencent.mobileqq,", "enableTime":
	 * "2014-10-14 14:50:53", "disableTime": "2014-10-15 08:27:23", "desc":
	 * "移除黑名单测试 2017/10/14 14:50" } { "sendTime": "20141014144200", "msgType":
	 * "removeLimitList", "limitListApp":
	 * "com.tencent.mobileqq,com.moji.mjweather ,com.UCMobile ,", "limitType":
	 * "2", "limitLimitType": "1", "limitPwdTime": "3", "limitEnableTime":
	 * "2014-10-08 14:38:02", "limitDisableTime": "2014-10-08 14:38:05", "desc":
	 * "移除限制名单测试 2014/10/14 14:52" } { "sendTime": "20141014144402", "msgType":
	 * "removeWhiteList", "whiteListApp":
	 * "com.tencent.mobileqq,com.moji.mjweather ,", "enableTime":
	 * "2014-10-14 14:54:31", "disableTime": "2014-10-15 14:54:36", "desc":
	 * "移除白名单测试,2014/10/14 14:51" }
	 * 
	 */
	private void parseLimitListRemove(String message, Context context)
			throws JSONException {
		// LimitListDao limitListDao = new LimitListDao(context);
		JSONObject jsonObject = new JSONObject(message);
		String msgType = jsonObject.getString(LimitListInfo.TAG.MSGTYPE);
		if (msgType.equals(EMMContants.MsgType.REMOVEBLACKLIST)) {
			String limitpackages = jsonObject.getString("blackListApp");
			List<String> packages = splitLimitPackages(limitpackages);
			for (String string : packages) {
				if (TextUtils.isEmpty(string)) {
					continue;
				}
				removeLimitList(string,
						EMMContants.LIMITLIST_CONTANT.LT_BLACK_LIST + "",
						context);
			}
		} else if (msgType.equals(EMMContants.MsgType.REMOVEWHITELIST)) {
			String limitpackages = jsonObject.getString("whiteListApp");
			List<String> packages = splitLimitPackages(limitpackages);
			for (String string : packages) {
				if (TextUtils.isEmpty(string)) {
					continue;
				}
				removeLimitList(string,
						EMMContants.LIMITLIST_CONTANT.LT_WHITE_LIST + "",
						context);
			}
		} else if (msgType.equals(EMMContants.MsgType.REMOVELIMITLIST)) {
			String limitpackages = jsonObject.getString("limitListApp");
			List<String> packages = splitLimitPackages(limitpackages);
			for (String string : packages) {
				if (TextUtils.isEmpty(string)) {
					continue;
				}
				removeLimitList(string,
						EMMContants.LIMITLIST_CONTANT.LT_LIMIT_LIST + "",
						context);
			}
		}
		if (executorService == null || executorService.isShutdown()
				|| executorService.isTerminated()) {
			executorService = Executors.newFixedThreadPool(2);
		}
		executorService.submit(new SyncLimitListTask());
	}

	/**
	 * 
	 * 
	 * @param limitpackages
	 * @return
	 */
	private List<String> splitLimitPackages(String limitpackages) {
		if (TextUtils.isEmpty(limitpackages)) {
			return null;
		} else {
			String[] split = limitpackages.split(",");
			return Arrays.asList(split);
		}
	}

	/**
	 * 消息处理过程中出现问题的异常处理 . 可以交由crashHandler进行处理
	 */
	@Override
	protected void onError(Context context, String errormsg) {
		// L.e(context.getClass(), errormsg);
	}

	/**
	 * xmpp 注册完成调用, 目前没有使用.
	 */
	@Override
	protected void onRegistered(Context context, String registrationId) {

	}

	/**
	 * 提取消息type ,类型 提取消息: 发送消息, 发送文件 日历 通知 文件 图片 网页 企業應用分發 白名单 黑名单 同步黑白限制名单 限制名单
	 * policy管控 同步policy 更新当前网络 锁设备,重设密码 解锁 更新位置 恢复出厂设置 移除控制 相机不可用 相机可用 wifi可用
	 * Wifi不可用 蓝牙可用 蓝牙不可用 同步软件信息 修改保存设备编号
	 * 
	 * 
	 */
	@Override
	protected void onPreMessage(Context context, Intent intent) {

	}

	/**
	 * 可以进行信息的存储或更新 消息处理完毕的一些后续处理
	 */
	protected void onPostedMessage(Context context, Intent intent) {
		NotificationIQ notificationIQ = (NotificationIQ) intent
				.getSerializableExtra(NotificationPacketListener.FLAG_NOTIFICATION_PACKETNAME);
		IQ result = new IQ() {
			@Override
			public String getChildElementXML() {
				return null;
			}
		};
		result.setType(Type.RESULT);
		result.setPacketID(notificationIQ.getPacketID());
		result.setTo(notificationIQ.getFrom());
		try {
			if (EMMContants.XMPPCONF.xmppManager != null) {
				EMMContants.XMPPCONF.xmppManager.getConnection().sendPacket(result);
			}
		} catch (Exception e) {
		}

		String message = notificationIQ.getMessage();
		String msgType = getMsgType(message);

		if (msgType.equals(EMMContants.MsgType.SENDCALENDAR)
				|| msgType.equals(EMMContants.MsgType.SENDNOTIFICATION)
				|| msgType.equals(EMMContants.MsgType.SENDFILE)
				|| msgType.equals(EMMContants.MsgType.SENDPAGE)
				|| msgType.equals(EMMContants.MsgType.SENDIMAGE)
				|| msgType.equals(EMMContants.MsgType.SENDAPPS)) {
			sendEMMInfoCenterBroadCast(context, msgType);
		} else if (msgType.equals(EMMContants.MsgType.SENDBLACKLIST)
				|| msgType.equals(EMMContants.MsgType.SENDWHITELIST)
				|| msgType.equals(EMMContants.MsgType.SENDLIMITLIST)
				|| msgType.equals(EMMContants.MsgType.REMOVEBLACKLIST)
				|| msgType.equals(EMMContants.MsgType.REMOVEWHITELIST)
				|| msgType.equals(EMMContants.MsgType.REMOVELIMITLIST)) {
			sendEMMAppMgrBroadCast(context, msgType);
		} else if (msgType.equalsIgnoreCase(EMMContants.MsgType.SEND_POLICY)
				|| msgType.equalsIgnoreCase(EMMContants.MsgType.DELETE_POLICY)) {
			sendEMMPolicyControlBroadCast(context, msgType);
		}

	}

	public static void sendEMMInfoCenterBroadCast(Context context,
			String messageType) {
		Intent intent = new Intent(
				EMMMessageCallBackReceiver.EMM_INFOCENTER_ACTION);
		intent.putExtra(EMMMessageCallBackReceiver.EMM_MSG_TYPE, messageType);
		context.sendBroadcast(intent);
		L.d(context.getClass(), "sending " + messageType
				+ "sendEMMInfoCenterBroadCast ");
	}

	public static void sendEMMAppMgrBroadCast(Context context,
			String messageType) {
		Intent intent = new Intent(EMMMessageCallBackReceiver.EMM_MAM_ACTION);
		intent.putExtra(EMMMessageCallBackReceiver.EMM_MSG_TYPE, messageType);
		context.sendBroadcast(intent);
		L.d(context.getClass(), "sending " + messageType
				+ "sendEMMAppMgrBroadCast ");
	}

	public static void sendEMMPolicyControlBroadCast(Context context,
			String messageType) {
		Intent intent = new Intent(
				EMMMessageCallBackReceiver.EMM_SAFEPOLICY_ACTION);
		intent.putExtra(EMMMessageCallBackReceiver.EMM_MSG_TYPE, messageType);
		context.sendBroadcast(intent);
		L.d(context.getClass(), "sending " + messageType
				+ "sendEMMPolicyControlBroadCast ");
	}

	/**
	 * 获取msgType
	 * 
	 * @param message
	 * @return
	 */
	private String getMsgType(String message) {
		if (TextUtils.isEmpty(message)) {
			return null;
		}
		try {
			JSONObject jsonObject = new JSONObject(message);
			return jsonObject.getString(EMMContants.MsgType.MSGTYPE);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 得到日历信息
	 * 
	 * @param message
	 * @param context
	 * @return
	 */
	private long parseCalendar(String message, Context context)
			throws JSONException {
		CalendarInfoDao db = new CalendarInfoDao(context);
		JSONObject jsonObject = new JSONObject(message);
		CalendarInfo calendarInfo = new CalendarInfo();
		calendarInfo.setMsgType(jsonObject.getString(CalendarInfo.TAG.MSGTYPE));
		calendarInfo.setSendTime(jsonObject
				.getString(CalendarInfo.TAG.SENDTIME));
		calendarInfo.setContent(jsonObject.getString(CalendarInfo.TAG.CONTENT));
		calendarInfo.setEndTime(jsonObject.getString(CalendarInfo.TAG.ENDTIME));
		calendarInfo.setIsAllDay(jsonObject
				.getString(CalendarInfo.TAG.ISALLDAY));
		calendarInfo.setPlace(jsonObject.getString(CalendarInfo.TAG.PLACE));
		calendarInfo.setStartTime(jsonObject
				.getString(CalendarInfo.TAG.STARTTIME));
		calendarInfo.setSubject(jsonObject.getString(CalendarInfo.TAG.SUBJECT)
				.trim());
		long l = db.add(calendarInfo);
		notifyClient(EMMContants.MsgType.SENDCALENDAR, "企业行事历设定",
				calendarInfo.getSubject(), true);
		return l;
	}

	/**
	 * @param message
	 * @param context
	 */
	private void parseWebPage(String message, Context context)
			throws JSONException {
		JSONObject jsonObject = new JSONObject(message);
		WebPageInfo webPageInfo = new WebPageInfo();
		webPageInfo.setContent(jsonObject.getString(WebPageInfo.TAG.CONTENT));
		webPageInfo.setDeadline(jsonObject.getString(WebPageInfo.TAG.DEADLINE));
		webPageInfo.setMsgType(jsonObject.getString(WebPageInfo.TAG.MSGTYPE));
		webPageInfo.setPageUrl(jsonObject.getString(WebPageInfo.TAG.PAGEURL));
		webPageInfo.setSendTime(jsonObject.getString(WebPageInfo.TAG.SENDTIME));
		webPageInfo.setSubject(jsonObject.getString(WebPageInfo.TAG.SUBJECT));
		WebPageInfoDao db = new WebPageInfoDao(context);
		db.add(webPageInfo);
		L.d(this.getClass(), webPageInfo.toString());
		notifyClient(EMMContants.MsgType.SENDPAGE, "企业网页推送",
				webPageInfo.getSubject(), true);
	}

	/**
	 * 解析文件指令
	 * 
	 * @param message
	 * @param context
	 */
	private void parseFile(String message, Context context)
			throws JSONException {
		JSONObject jsonObject = new JSONObject(message);
		FileInfo fileInfo = new FileInfo();
		fileInfo.setContent(jsonObject.getString(FileInfo.TAG.CONTENT));
		fileInfo.setContact(jsonObject.getString(FileInfo.TAG.CONTACT));
		fileInfo.setDeadline(jsonObject.getString(FileInfo.TAG.DEADLINE));
		fileInfo.setFileUrl(jsonObject.getString(FileInfo.TAG.FILEURL));
		fileInfo.setMsgType(jsonObject.getString(FileInfo.TAG.MSGTYPE));
		fileInfo.setPassword(jsonObject.getString(FileInfo.TAG.PASSWORD));
		fileInfo.setSendTime(jsonObject.getString(FileInfo.TAG.SENDTIME));
		fileInfo.setSubject(jsonObject.getString(FileInfo.TAG.SUBJECT));
		L.d(this.getClass(), fileInfo.toString());
		FileInfoDao db = new FileInfoDao(context);
		db.add(fileInfo);
		notifyClient(EMMContants.MsgType.SENDFILE, fileInfo.getSubject(),
				fileInfo.getContent(), true);
	}

	/**
	 * 解析notification
	 * 
	 * @param message
	 * @param context
	 */
	private void parseNotification(String message, Context context)
			throws JSONException {
		JSONObject jsonObject = new JSONObject(message);
		NotificationInfo notificationInfo = new NotificationInfo();
		notificationInfo.setContent(jsonObject
				.getString(NotificationInfo.TAG.CONTENT));
		notificationInfo.setMsgType(jsonObject
				.getString(NotificationInfo.TAG.MSGTYPE));
		notificationInfo.setSendTime(jsonObject
				.getString(NotificationInfo.TAG.SENDTIME));
		NotificationInfoDao db = new NotificationInfoDao(context);
		db.add(notificationInfo);

		notifyClient(EMMContants.MsgType.SENDNOTIFICATION, "企业消息推送",
				notificationInfo.getContent(), true);
	}

	private void parseImage(String message, Context context)
			throws JSONException {
		JSONObject jsonObject = new JSONObject(message);
		PicInfo picInfo = new PicInfo();
		picInfo.setContact(jsonObject.getString(PicInfo.TAG.CONTACT));
		picInfo.setContent(jsonObject.getString(PicInfo.TAG.CONTENT));
		picInfo.setDeadline(jsonObject.getString(PicInfo.TAG.DEADLINE));
		picInfo.setImg_list(jsonObject.getString(PicInfo.TAG.IMG_LIST));
		picInfo.setMsgType(jsonObject.getString(PicInfo.TAG.MSGTYPE));
		picInfo.setPassword(jsonObject.getString(PicInfo.TAG.PASSWORD));
		picInfo.setSendTime(jsonObject.getString(PicInfo.TAG.SENDTIME));
		picInfo.setSubject(jsonObject.getString(PicInfo.TAG.SUBJECT).trim());
		L.d(this.getClass(), picInfo.toString());
		PicInfoDao db = new PicInfoDao(context);
		db.add(picInfo);
		notifyClient(EMMContants.MsgType.SENDIMAGE, "企业图片分发",
				picInfo.getSubject(), true);
	}

	private void parseCommonMsg(String message, Context context)
			throws JSONException {
		JSONObject jsonObject = new JSONObject(message);
		MsgCommonInfo msgCommonInfo = new MsgCommonInfo();
		if (jsonObject.getString(MsgCommonInfo.TAG.MSGTYPE).equals(
				EMMContants.MsgType.LOCKDEV)) {
			msgCommonInfo.setMsgType(jsonObject
					.getString(MsgCommonInfo.TAG.MSGTYPE));
			msgCommonInfo.setLockPwd(jsonObject
					.getString(MsgCommonInfo.TAG.LOCKPWD));
			if (!TextUtils.isEmpty(msgCommonInfo.getLockPwd()))
				policyControl.lockDevice(msgCommonInfo.getLockPwd());
		} else if (jsonObject.getString(MsgCommonInfo.TAG.MSGTYPE).equals(
				EMMContants.MsgType.RESET)) {
			msgCommonInfo.setMsgType(jsonObject
					.getString(MsgCommonInfo.TAG.MSGTYPE));
			policyControl.removeWipe();
		} else if (jsonObject.getString(MsgCommonInfo.TAG.MSGTYPE).equals(
				EMMContants.MsgType.REMOVECONTROL)) {
			msgCommonInfo.setMsgType(jsonObject
					.getString(MsgCommonInfo.TAG.MSGTYPE));
			policyControl.removeControl();
		} else if (jsonObject.getString(MsgCommonInfo.TAG.MSGTYPE).equals(
				EMMContants.MsgType.SENDCLEANINFO)) {
			msgCommonInfo.setMsgType(jsonObject
					.getString(MsgCommonInfo.TAG.MSGTYPE));
			policyControl.cleanInfo();
		} else if (jsonObject.getString(MsgCommonInfo.TAG.MSGTYPE).equals(
				EMMContants.MsgType.UNLOCK)) {
			msgCommonInfo.setMsgType(jsonObject
					.getString(MsgCommonInfo.TAG.MSGTYPE));
			policyControl.unLockDevice();
		}
		msgCommonInfo.setSendTime(jsonObject
				.getString(MsgCommonInfo.TAG.SENDTIME));

	}

	private void parseApps(String message, Context context)
			throws JSONException {
		JSONObject jsonObject = new JSONObject(message);
		JSONArray array = new JSONArray(
				jsonObject.getString(AppInfo.TAG.APPLIST));
		AppInfoDao db = new AppInfoDao(context);
		for (int i = 0; i < array.length(); i++) {
			JSONObject jsonObject2 = array.getJSONObject(i);
			AppInfo appInfo = new AppInfo();
			appInfo.setMsgType(jsonObject.getString(AppInfo.TAG.MSGTYPE));
			appInfo.setSendTime(jsonObject.getString(AppInfo.TAG.SENDTIME));
			appInfo.setAppName(jsonObject2.getString(AppInfo.TAG.APPNAME));
			appInfo.setFileSize(jsonObject2.getString(AppInfo.TAG.FILESIZE));
			appInfo.setFileUrl(jsonObject2.getString(AppInfo.TAG.FILEURL));
			appInfo.setPackageName(jsonObject2
					.getString(AppInfo.TAG.PACKAGENAME));
			db.add(appInfo);
			if (executorService == null || executorService.isShutdown()
					|| executorService.isTerminated()) {
				executorService = Executors.newFixedThreadPool(2);
			}
			executorService.submit(new DownloadAppsTask(appInfo));
		}
		notifyClient(EMMContants.MsgType.SENDAPPS, "企业应用分发", "请注意查收", true);
	}

	/**
	 * updatelocation location format : x,y
	 */
	private class UpdateLocationTask implements Runnable {

		@Override
		public void run() {

			String requrl = EMMContants.SYSCONF.SYNC_UPDATELOCATION;
			String params = EMMReqParamsUtils
					.getSyncDeviceLocationStr(getApplication());
			String returnData = HttpClientUtil.returnData(requrl, params);
			L.d(this.getClass(), returnData);
		}
	}

	/**
	 * 
	 * 同步设备流量信息 同步策略:指令同步, 网络切换同步.
	 */
	protected class SyncDeviceTrafficTask implements Runnable {

		@Override
		public void run() {

			SysTraffic trafficSingleSysDataByDay = trafficOptDao
					.getTrafficSingleSysDataByDay(Calendar.getInstance());
			if (trafficSingleSysDataByDay != null) {
				String requrl = EMMContants.SYSCONF.SYNC_DEVICE_TRAFFIC;
				String params = EMMReqParamsUtils.getSyncDeviceTrafficStr(
						getApplication(), trafficSingleSysDataByDay);
				String returnData = HttpClientUtil.returnData(requrl, params);
				L.d(this.getClass(), returnData);
			} else {
				L.d(this.getClass(), "trafficSingleSysDataByDay");
			}
		}

	}

	/**
	 * 同步Apps流量信息 同步策略:指令同步, 网络切换同步.
	 */
	protected class SyncAppsTrafficTask implements Runnable {

		@Override
		public void run() {
			Calendar calendar = Calendar.getInstance();
			int tday = calendar.get(Calendar.DAY_OF_MONTH);
			int tmonth = calendar.get(Calendar.MONTH) + 1;
			int tyear = calendar.get(Calendar.YEAR);
			// L.d(this.getClass(), "SyncAppsTrafficTask " + tday + " tmonth "
			// + tmonth + "tyear " + tyear);
			List<UidTraffic> hasTrafficAppsByDay = trafficAppsOptDao
					.getHasTrafficAppsByDay(tday, tmonth, tyear);
			if (hasTrafficAppsByDay != null) {

				String requrl = EMMContants.SYSCONF.SYNC_APPSTRAFFIC;
				String params = EMMReqParamsUtils.getSyncAppsTrafficStr(
						getApplication(), hasTrafficAppsByDay);
				String returnData = HttpClientUtil.returnData(requrl, params);
				L.d(this.getClass(), returnData);
			} else {
				L.d(this.getClass(), "hasTrafficAppsByDay");
			}
		}

	}

	/**
	 * 同步设备的限制名单信息, 同步策略:指令同步.
	 * http://10.207.248.21:8080/emm/sync_syncDeviceLimitApp.action?mac=
	 * DEV00000000000001310&limitListApp=""
	 */
	protected class SyncLimitListTask implements Runnable {

		@Override
		public void run() {
			String requrl = EMMContants.SYSCONF.SYNC_LIMITLIST;
			String params = EMMReqParamsUtils
					.getSyncDeviceLimitListStr(getApplication());
			if (TextUtils.isEmpty(params)) {
				L.w(this.getClass(),
						"device limit list is null  sysnclimitTask is cancel ! ");
				return;
			}
			String returnData = HttpClientUtil.returnData(requrl, params);
			L.d(this.getClass(), returnData);
		}
	}

	/**
	 * 同步设备信息 ,软件信息 /// 同步时间: 注册同步,指令同步,
	 */
	protected class SyncDeviceSoftwareTask implements Runnable {

		@Override
		public void run() {
			String requrl = EMMContants.SYSCONF.SYNC_DEVICE_SOFTWARE;
			String params = EMMReqParamsUtils
					.getSyncDeviceSoftWareStr(getApplication());
			String returnData = HttpClientUtil.returnData(requrl, params);
			L.d(this.getClass(), returnData);
		}
	}

	/**
	 * 下载App
	 * 
	 */
	protected class DownloadAppsTask implements Runnable {

		private AppInfo appInfo;

		public DownloadAppsTask(AppInfo appInfo) {
			this.appInfo = appInfo;
		}

		@Override
		public void run() {
			String url = appInfo.getFileUrl();
			String dirpath = EMMContants.LocalConf.getEMMLocalHost_dirPath()
					+ EMMContants.LocalConf.UpdateApp_dirpath;
			String fileName = appInfo.getPackageName() + ".apk";
			L.d(getClass(), "url -->>" + url + "dirpath --->>" + dirpath
					+ "fileName--->>" + fileName);
			String downFileToSDPath = downFileToSD(url, dirpath, fileName);
			L.d(this.getClass(), downFileToSDPath + "App下载完毕");
		}

		/**
		 * 下载网络文件到SD卡中.如果SD中存在同名文件将不再下载
		 * 
		 * @param url
		 *            要下载文件的网络地址
		 * @return 下载好的本地文件地址
		 */
		public String downFileToSD(String url, String dirPath, String fileName) {
			InputStream in = null;
			FileOutputStream fileOutputStream = null;
			HttpURLConnection connection = null;
			String downFilePath = null;
			File file = null;
			try {
				if (!EMMContants.LocalConf.isSdPresent()) {
					return null;
				}
				// 先判断SD卡中有没有这个文件，不比较后缀部分比较
				File parentFile = new File(dirPath);
				File[] files = parentFile.listFiles();
				for (int i = 0; i < files.length; ++i) {
					String fns = files[i].getName();
					String name = fns.substring(0, fns.lastIndexOf("."));
					if (name.equals(fileName)) {
						// 文件已存在
						return files[i].getPath();
					}
				}
				URL mUrl = new URL(url);
				connection = (HttpURLConnection) mUrl.openConnection();
				connection.connect();
				// 获取文件名，下载文件
				file = new File(dirPath, fileName);
				downFilePath = file.getPath();
				if (!file.exists()) {
					file.createNewFile();
				} else {
					// 文件已存在
					return file.getPath();
				}
				in = connection.getInputStream();
				fileOutputStream = new FileOutputStream(file);
				byte[] b = new byte[1024];
				int temp = 0;
				while ((temp = in.read(b)) != -1) {

					fileOutputStream.write(b, 0, temp);
				}
			} catch (Exception e) {
				e.printStackTrace();
				L.e(this.getClass(), "文件下载出错了");
				// 检查文件大小,如果文件为0B说明网络不好没有下载成功，要将建立的空文件删除
				file.delete();
				downFilePath = null;
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if (connection != null) {
						connection.disconnect();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return downFilePath;
		}

	}

}
