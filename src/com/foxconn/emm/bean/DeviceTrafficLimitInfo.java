package com.foxconn.emm.bean;

import org.json.JSONException;
import org.json.JSONObject;

import android.nfc.Tag;
import android.text.TextUtils;

/**
 * 
 * 
 * 同步设备流量的限制信息.
 * 
 * 
 * { "traffic": { "trafficLimitId": "WEM00000000000000044", "trafficLimitDesc":
 * "测试", "trafficLimitType": "0", "trafficLimit": "400", "trafficStatus": "0",
 * "sendTime": "2014-09-03 14:22:12", "createBy": "张三", "createDate":
 * "2014-08-27 14:24:11", "modifyBy": "", "modifyDate": "" }, "success": "true"
 * }
 * 
 * 
 * 设备流量的发送指令格式。 /// 两者解析区分使用 trafficStatus 区别。
 * 
 * "sendTime":"XXXXXXXXXXXX", "msgType":"sendtrafficlimit", "traffic_limit":"",
 * "limit_type":"", "limit_desc":"", }
 * 
 * 
 */
public class DeviceTrafficLimitInfo {

	private String createBy;

	private String createDate;

	private String modifyBy;

	private String modifyDate;

	private String sendTime;

	private String trafficLimit;

	private String trafficLimitDesc;

	private String trafficLimitId;

	private String trafficLimitType;

	private String trafficStatus;

	public static class TAG {

		public static String tag_createBy = "createBy";

		public static String tag_createDate = "createDate";

		public static String tag_modifyBy = "modifyBy";

		public static String tag_modifyDate = "modifyDate";

		public static String tag_sendTime = "sendTime";

		public static String tag_trafficLimit = "trafficLimit";

		public static String tag_trafficLimitDesc = "trafficLimitDesc";

		public static String tag_trafficLimitId = "trafficLimitId";

		public static String tag_trafficLimitType = "trafficLimitType";

		public static String tag_trafficStatus = "trafficStatus";

		public static String tag_msgType = "msgType";

	}

	public static  DeviceTrafficLimitInfo getInstanceFromJson(String jsonstr) {
		if (TextUtils.isEmpty(jsonstr)) {
			return null;
		}
		DeviceTrafficLimitInfo deviceTrafficLimitInfo = new DeviceTrafficLimitInfo();
		try {
			JSONObject jsonObject = new JSONObject(jsonstr);
			// // 说明是同步过来的信息
			if (jsonstr.contains(TAG.tag_trafficStatus)) {
				deviceTrafficLimitInfo.setTrafficLimitType(jsonObject.getString(TAG.tag_trafficLimitType));
				deviceTrafficLimitInfo.setTrafficLimitDesc(jsonObject.getString(TAG.tag_trafficLimitDesc));
				deviceTrafficLimitInfo.setTrafficLimit(jsonObject.getString(TAG.tag_trafficLimit));
				deviceTrafficLimitInfo.setTrafficStatus(jsonObject.getString(TAG.tag_trafficStatus));
				deviceTrafficLimitInfo.setCreateBy(jsonObject.getString(TAG.tag_createBy));
				deviceTrafficLimitInfo.setCreateDate(jsonObject.getString(TAG.tag_createDate));
				deviceTrafficLimitInfo.setModifyBy(jsonObject.getString(TAG.tag_modifyBy));
				deviceTrafficLimitInfo.setModifyDate(jsonObject.getString(TAG.tag_modifyDate));
				
			} else if (jsonstr.contains(TAG.tag_msgType)) {
				deviceTrafficLimitInfo.setTrafficLimitType(jsonObject.getString("limit_type"));
				deviceTrafficLimitInfo.setTrafficLimitDesc(jsonObject.getString("limit_desc"));
				deviceTrafficLimitInfo.setTrafficLimit(jsonObject.getString("traffic_limit"));
				deviceTrafficLimitInfo.setModifyDate(jsonObject.getString(TAG.tag_sendTime));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return deviceTrafficLimitInfo;
	}

	public DeviceTrafficLimitInfo(String trafficLimitId,
			String trafficLimitDesc, String trafficLimitType,
			String trafficLimit, String sendTime, String createBy,
			String createDate, String modifyBy, String modifyDate) {
		super();
		this.trafficLimitId = trafficLimitId;
		this.trafficLimitDesc = trafficLimitDesc;
		this.trafficLimitType = trafficLimitType;
		this.trafficLimit = trafficLimit;
		this.sendTime = sendTime;
		this.createBy = createBy;
		this.createDate = createDate;
		this.modifyBy = modifyBy;
		this.modifyDate = modifyDate;
	}

	public DeviceTrafficLimitInfo() {
		// TODO Auto-generated constructor stub
	}

	public String getCreateBy() {
		return createBy;
	}

	public String getCreateDate() {
		return createDate;
	}

	public String getModifyBy() {
		return modifyBy;
	}

	public String getModifyDate() {
		return modifyDate;
	}

	public String getSendTime() {
		return sendTime;
	}

	public String getTrafficLimit() {
		return trafficLimit;
	}

	public String getTrafficLimitDesc() {
		return trafficLimitDesc;
	}

	public String getTrafficLimitId() {
		return trafficLimitId;
	}

	public String getTrafficLimitType() {
		return trafficLimitType;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public void setModifyBy(String modifyBy) {
		this.modifyBy = modifyBy;
	}

	public void setModifyDate(String modifyDate) {
		this.modifyDate = modifyDate;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public void setTrafficLimit(String trafficLimit) {
		this.trafficLimit = trafficLimit;
	}

	public void setTrafficLimitDesc(String trafficLimitDesc) {
		this.trafficLimitDesc = trafficLimitDesc;
	}

	public void setTrafficLimitId(String trafficLimitId) {
		this.trafficLimitId = trafficLimitId;
	}

	public void setTrafficLimitType(String trafficLimitType) {
		this.trafficLimitType = trafficLimitType;
	}

	/**
	 * @return the trafficStatus
	 */
	public String getTrafficStatus() {
		return trafficStatus;
	}

	/**
	 * @param trafficStatus
	 *            the trafficStatus to set
	 */
	public void setTrafficStatus(String trafficStatus) {
		this.trafficStatus = trafficStatus;
	}

}
