package com.foxconn.emm.bean;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.foxconn.emm.utils.L;
import com.foxconn.emm.utils.TextFormater;

public class PolicyInfo {

	private String sendTime;

	private String sendIp;

	private String sendnote;

	public class Auto_Synch {

		private String itemFlag;

		private String itemValue;

		public String getItemFlag() {
			return itemFlag;
		}

		public String getItemValue() {
			return itemValue;
		}

		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}

		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		@Override
		public String toString() {
			return "Auto_Synch [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
		}

	}

	public class Enable_Bluetooth {

		private String itemFlag;

		private String itemValue;

		public String getItemFlag() {
			return itemFlag;
		}

		public String getItemValue() {
			return itemValue;
		}

		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}

		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		@Override
		public String toString() {
			return "Enable_Bluetooth [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
		}
	}

	public class Enable_Camera {

		private String itemFlag;

		private String itemValue;

		public String getItemFlag() {
			return itemFlag;
		}

		public String getItemValue() {
			return itemValue;
		}

		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}

		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		@Override
		public String toString() {
			return "Enable_Camera [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
		}

	}

	public class Enable_Google_play {

		private String itemFlag;

		private String itemValue;

		public String getItemFlag() {
			return itemFlag;
		}

		public String getItemValue() {
			return itemValue;
		}

		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}

		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		@Override
		public String toString() {
			return "Enable_Google_play [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
		}

	}

	public class Enable_SSID {

		private String itemFlag;

		private String itemValue;

		public String getItemFlag() {
			return itemFlag;
		}

		public String getItemValue() {
			return itemValue;
		}

		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}

		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		@Override
		public String toString() {
			return "Enable_SSID [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
		}
	}

	public class Enable_USB {

		private String itemFlag;

		private String itemValue;

		public String getItemFlag() {
			return itemFlag;
		}

		public String getItemValue() {
			return itemValue;
		}

		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}

		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		@Override
		public String toString() {
			return "Enable_USB [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
		}

	}

	public class Enable_Wifi {

		private String itemFlag;

		private String itemValue;

		public String getItemFlag() {
			return itemFlag;
		}

		public String getItemValue() {
			return itemValue;
		}

		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}

		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		@Override
		public String toString() {
			return "Enable_Wifi [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
		}
	}

	public class Hidden_SSID {

		private String itemFlag;

		private String itemValue;

		public String getItemFlag() {
			return itemFlag;
		}

		public String getItemValue() {
			return itemValue;
		}

		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}

		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		@Override
		public String toString() {
			return "Hidden_SSID [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
		}
	}

	public class PW_Error_Count {

		private String itemFlag;

		private String itemValue;

		public String getItemFlag() {
			return itemFlag;
		}

		public String getItemValue() {
			return itemValue;
		}

		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}

		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		@Override
		public String toString() {
			return "PW_Error_Count [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
		}
	}

	public class PW_Format {

		private String itemFlag;

		private String itemValue;

		public String getItemFlag() {
			return itemFlag;
		}

		public String getItemValue() {
			return itemValue;
		}

		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}

		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		@Override
		public String toString() {
			return "PW_Format [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
		}
	}

	public class PW_Length {
		private String itemFlag;

		private String itemValue;

		public String getItemFlag() {
			return itemFlag;
		}

		public String getItemValue() {
			return itemValue;
		}

		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}

		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		@Override
		public String toString() {
			return "PW_Length [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
		}
	}

	public class PW_Lock_Time {

		private String itemFlag;

		private String itemValue;

		public String getItemFlag() {
			return itemFlag;
		}

		public String getItemValue() {
			return itemValue;
		}

		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}

		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		@Override
		public String toString() {
			return "PW_Lock_Time [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
		}
	}

	public class PW_Reset_Period {

		private String itemFlag;

		private String itemValue;

		public String getItemFlag() {
			return itemFlag;
		}

		public String getItemValue() {
			return itemValue;
		}

		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}

		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		@Override
		public String toString() {
			return "PW_Reset_Period [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
		}
	}

	public class Required_PW {

		private String itemFlag;

		private String itemValue;

		public String getItemFlag() {
			return itemFlag;
		}

		public String getItemValue() {
			return itemValue;
		}

		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}

		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		@Override
		public String toString() {
			return "Required_PW [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
		}

	}

	public class Scan_Bluetooth {

		private String itemFlag;

		private String itemValue;

		public String getItemFlag() {
			return itemFlag;
		}

		public String getItemValue() {
			return itemValue;
		}

		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}

		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		@Override
		public String toString() {
			return "Scan_Bluetooth [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
		}

	}

	public class Scan_Devices {

		private String itemFlag;

		private String itemValue;

		public String getItemFlag() {
			return itemFlag;
		}

		public String getItemValue() {
			return itemValue;
		}

		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}

		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		@Override
		public String toString() {
			return "Scan_Devices [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
		}
	}

	public class SSID {

		private String itemFlag;

		private String itemValue;

		public String getItemFlag() {
			return itemFlag;
		}

		public String getItemValue() {
			return itemValue;
		}

		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}

		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		@Override
		public String toString() {
			return "SSID [itemValue=" + itemValue + ", itemFlag=" + itemFlag
					+ "]";
		}
	}

	public class SSID_Pre_sharedkey {
		private String itemFlag;

		private String itemValue;

		public String getItemFlag() {
			return itemFlag;
		}

		public String getItemValue() {
			return itemValue;
		}

		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}

		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		@Override
		public String toString() {
			return "SSID_Pre_sharedkey [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
		}
	}

	public class SSID_Security {
		private String itemFlag;

		private String itemValue;

		public String getItemFlag() {
			return itemFlag;
		}

		public String getItemValue() {
			return itemValue;
		}

		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}

		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		@Override
		public String toString() {
			return "SSID_Security [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
		}
	}

	public class SSID_WEP_Key1 {

		private String itemFlag;

		private String itemValue;

		public String getItemFlag() {
			return itemFlag;
		}

		public String getItemValue() {
			return itemValue;
		}

		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}

		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		@Override
		public String toString() {
			return "SSID_WEP_Key1 [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
		}
	}

	public class Unload_Emm {

		private String itemFlag;

		private String itemValue;

		/**
		 * @return the itemFlag
		 */
		public String getItemFlag() {
			return itemFlag;
		}

		/**
		 * @param itemFlag
		 *            the itemFlag to set
		 */
		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}

		/**
		 * @return the itemValue
		 */
		public String getItemValue() {
			return itemValue;
		}

		/**
		 * @param itemValue
		 *            the itemValue to set
		 */
		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		@Override
		public String toString() {
			return "Unload_Emm [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";

		}
	}

	public class Limit_Time {

		private String itemFlag;

		private String itemValue;

		/**
		 * @return the itemFlag
		 */
		public String getItemFlag() {
			return itemFlag;
		}

		/**
		 * @param itemFlag
		 *            the itemFlag to set
		 */
		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}

		/**
		 * @return the itemValue
		 */
		public String getItemValue() {
			return itemValue;
		}

		/**
		 * @param itemValue
		 *            the itemValue to set
		 */
		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}

		@Override
		public String toString() {
			return "Limit_Time [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";

		}
	}
	public class Date_Scope {
		
		private String itemFlag;
		
		private String itemValue;
		
		/**
		 * @return the itemFlag
		 */
		public String getItemFlag() {
			return itemFlag;
		}
		
		/**
		 * @param itemFlag
		 *            the itemFlag to set
		 */
		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}
		
		/**
		 * @return the itemValue
		 */
		public String getItemValue() {
			return itemValue;
		}
		
		/**
		 * @param itemValue
		 *            the itemValue to set
		 */
		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}
		
		@Override
		public String toString() {
			return "Date_Scope [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
			
		}
	}
	public class Time_Scope {
		
		private String itemFlag;
		
		private String itemValue;
		
		/**
		 * @return the itemFlag
		 */
		public String getItemFlag() {
			return itemFlag;
		}
		
		/**
		 * @param itemFlag
		 *            the itemFlag to set
		 */
		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}
		
		/**
		 * @return the itemValue
		 */
		public String getItemValue() {
			return itemValue;
		}
		
		/**
		 * @param itemValue
		 *            the itemValue to set
		 */
		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}
		
		@Override
		public String toString() {
			return "Time_Scope [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
			
		}
	}
	public class Limit_Place {
		
		private String itemFlag;
		
		private String itemValue;
		
		/**
		 * @return the itemFlag
		 */
		public String getItemFlag() {
			return itemFlag;
		}
		
		/**
		 * @param itemFlag
		 *            the itemFlag to set
		 */
		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}
		
		/**
		 * @return the itemValue
		 */
		public String getItemValue() {
			return itemValue;
		}
		
		/**
		 * @param itemValue
		 *            the itemValue to set
		 */
		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}
		
		@Override
		public String toString() {
			return "Limit_Place [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
			
		}
	}
	public class Place_Scope {

		private String itemFlag;
		
		private String itemValue;
		
		/**
		 * @return the itemFlag
		 */
		public String getItemFlag() {
			return itemFlag;
		}
		
		/**
		 * @param itemFlag
		 *            the itemFlag to set
		 */
		public void setItemFlag(String itemFlag) {
			this.itemFlag = itemFlag;
		}
		
		/**
		 * @return the itemValue
		 */
		public String getItemValue() {
			return itemValue;
		}
		
		/**
		 * @param itemValue
		 *            the itemValue to set
		 */
		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}
		
		@Override
		public String toString() {
			return "Place_Scope [itemValue=" + itemValue + ", itemFlag="
					+ itemFlag + "]";
			
		}
	}
	/**
	 * 
	 * 
	 是否允许卸载 Unload_Emm 时间限制 Limit_Time 日期范围 Date_Scope 时间范围 Time_Scope 地点限制
	 * Limit_Place 所选范围 Place_Scope
	 * 
	 * 
	 */

	public static class TAG {
		public static final String tag_Auto_Synch = "Auto_Synch";

		public static final String tag_COMMAND = "COMMAND";
		public static final String tag_Enable_Bluetooth = "Enable_Bluetooth";
		public static final String tag_Enable_Camera = "Enable_Camera";
		public static final String tag_Enable_GoolgePlay = "Enable_GoolgePlay";
		public static final String tag_Enable_SSID = "Enable_SSID";
		public static final String tag_Enable_USB = "Enable_USB";
		public static final String tag_Enable_Wifi = "Enable_Wifi";
		public static final String tag_Hidden_SSID = "Hidden_SSID";
		public static final String tag_itemFlag = "itemFlag";
		public static final String tag_itemValue = "itemValue";
		public static final String tag_policyId = "policyId";
		public static final String tag_PW_Error_Count = "PW_Error_Count";
		public static final String tag_PW_Format = "PW_Format";
		public static final String tag_PW_Length = "PW_Length";
		public static final String tag_PW_Lock_Time = "PW_Lock_Time";
		public static final String tag_PW_Reset_Period = "PW_Reset_Period";
		public static final String tag_Required_PW = "Required_PW";
		public static final String tag_Scan_Bluetooth = "Scan_Bluetooth";
		public static final String tag_Scan_Devices = "Scan_Devices";
		public static final String tag_SSID = "SSID";
		public static final String tag_SSID_Pre_sharedkey = "SSID_Pre_sharedkey";
		public static final String tag_SSID_Security = "SSID_Security";
		public static final String tag_SSID_WEP_Key1 = "SSID_WEP_Key1";
		public static final String tag_success = "success";
		public static final String tag_sendnote = "note";
		public static final String tag_sendip = "IP";
		public static final String tag_sendtime = "sendTime";
		/**
		 * 是否可以卸载EMM
		 */
		public static final String tag_unload_emm = "Unload_Emm";

		/**
		 * 限制时间
		 */
		public static final String tag_limit_time = "Limit_Time";
		/**
		 * 日期范围
		 */
		public static final String tag_date_scope = "Date_Scope";

		/**
		 * 限制时间范围
		 */
		public static final String tag_time_scope = "Time_Scope";
		/**
		 * 限制地点
		 */
		public static final String tag_limit_place = "Limit_Place";
		/**
		 * 所选范围: "113.82922,34.041388~113.82922,34.041388
		 * 西南------------------东北"
		 */
		public static final String tag_place_scope = "Place_Scope";

	}

	public static final String DEFAULTPOLICYNO = "P0000000000000000000";

	public PolicyInfo getDefaultPolicy() {
		PolicyInfo policyInfo = new PolicyInfo();
		policyInfo.POLICY_ID = "P0000000000000000000";

		// /// enable_bluetooth
		policyInfo.enable_Bluetooth = new PolicyInfo.Enable_Bluetooth();
		policyInfo.enable_Bluetooth.itemFlag = "2";
		policyInfo.enable_Bluetooth.itemValue = "";

		// // autoSync 0:不同步, 1: 同步
		policyInfo.auto_Synch = new PolicyInfo.Auto_Synch();
		policyInfo.auto_Synch.itemFlag = "1";
		policyInfo.auto_Synch.itemValue = "";

		// // enable_camera 0:禁用, 1: 开启, 2:不管控
		policyInfo.enable_Camera = new PolicyInfo.Enable_Camera();
		policyInfo.enable_Camera.itemFlag = "2";
		policyInfo.enable_Camera.itemValue = "";

		// // enable_Google_play 0:不可打开, 1:必须打开, 2: 不管控
		policyInfo.enable_Google_play = new PolicyInfo.Enable_Google_play();
		policyInfo.enable_Google_play.itemFlag = "2";
		policyInfo.enable_Google_play.itemValue = "";

		// // enable_SSID 0:禁用, 1: 开启, 2:不管控
		policyInfo.enable_SSID = new PolicyInfo.Enable_SSID();
		policyInfo.enable_SSID.itemFlag = "2";
		policyInfo.enable_SSID.itemValue = "";

		// // Enable_USB 0:禁用, 1: 开启, 2:不管控
		policyInfo.enable_USB = new PolicyInfo.Enable_USB();
		policyInfo.enable_USB.itemFlag = "2";
		policyInfo.enable_USB.itemValue = "";

		// // enable_wifi 0:禁用, 1: 开启, 2:不管控
		policyInfo.enable_Wifi = new PolicyInfo.Enable_Wifi();
		policyInfo.enable_Wifi.itemFlag = "2";
		policyInfo.enable_Wifi.itemValue = "";

		// // hidden_SSID 0:不使用, 1: 使用
		policyInfo.hidden_SSID = new PolicyInfo.Hidden_SSID();
		policyInfo.hidden_SSID.itemFlag = "0";
		policyInfo.hidden_SSID.itemValue = "";

		// // error_Count 0:禁用, 1: 开启, 2:不管控
		policyInfo.error_Count = new PolicyInfo.PW_Error_Count();
		policyInfo.error_Count.itemFlag = "2";
		policyInfo.error_Count.itemValue = "";

		// // PW_Format 0:不使用, 1:使用 无:0 纯数字密码: 1 纯字母密码:2 , 数字+字母密码 :3
		// 数字+字母+标点混合密码 : 4
		policyInfo.pw_format = new PolicyInfo.PW_Format();
		policyInfo.pw_format.itemFlag = "0";
		policyInfo.pw_format.itemValue = "0";

		// // PW_Length 0:不使用, 1:使用
		policyInfo.pwLength = new PolicyInfo.PW_Length();
		policyInfo.pwLength.itemFlag = "0";
		policyInfo.pwLength.itemValue = "";

		// // PW_Lock_Time 0:不使用, 1:使用
		policyInfo.lock_Time = new PolicyInfo.PW_Lock_Time();
		policyInfo.lock_Time.itemFlag = "0";
		policyInfo.lock_Time.itemValue = "";

		// // PW_Reset_Period 0:不使用, 1:使用
		policyInfo.reset_Period = new PolicyInfo.PW_Reset_Period();
		policyInfo.reset_Period.itemFlag = "0";
		policyInfo.reset_Period.itemValue = "";

		// Required_PW 0:不设定, 1:设定 不设定没有值
		policyInfo.required_PW = new PolicyInfo.Required_PW();
		policyInfo.required_PW.itemFlag = "0";
		policyInfo.required_PW.itemValue = "";

		// // Scan_Bluetooth 0:禁用, 1: 开启, 2:不管控
		policyInfo.scan_Bluetooth = new PolicyInfo.Scan_Bluetooth();
		policyInfo.scan_Bluetooth.itemFlag = "2";
		policyInfo.scan_Bluetooth.itemValue = "";

		// // Scan_Devices 0:不允许, 1:允许
		policyInfo.scan_Devices = new PolicyInfo.Scan_Devices();
		policyInfo.scan_Devices.itemFlag = "0";
		policyInfo.scan_Devices.itemValue = "";

		// // SSID 0:不使用, 1:使用
		policyInfo.ssid = new PolicyInfo.SSID();
		policyInfo.ssid.itemFlag = "0";
		policyInfo.ssid.itemValue = "0";

		// SSID_Pre_sharedkey
		policyInfo.pre_sharedkey = new PolicyInfo.SSID_Pre_sharedkey();
		policyInfo.pre_sharedkey.itemFlag = "0";
		policyInfo.pre_sharedkey.itemValue = "0";

		// // SSID_Security 0:不使用, 1:使用 OPEN :0 WEP: 1 WPA/WPA2 PSK: 2 802.1x
		// EAP :3
		// ///設備開放wifi的網路安全性類型None、WEP、WPA/WPA2 PSK、802.1x Enterprise
		policyInfo.ssid_security = new PolicyInfo.SSID_Security();
		policyInfo.ssid_security.itemFlag = "0";
		policyInfo.ssid_security.itemValue = "2";

		// // enable_SSID 0:不自动连接, 1:自动连接
		policyInfo.wep_Key1 = new PolicyInfo.SSID_WEP_Key1();
		policyInfo.wep_Key1.itemFlag = "0";
		policyInfo.wep_Key1.itemValue = "";

		/// unloademm 默认不可卸载 0
		policyInfo.unload_Emm  = new PolicyInfo.Unload_Emm();
		policyInfo.unload_Emm.itemFlag="0";
		policyInfo.unload_Emm.itemValue="";
		
		/// limit_time 时间限制 0
		policyInfo.limit_Time = new PolicyInfo.Limit_Time();
		policyInfo.limit_Time.itemFlag ="0";
		policyInfo.limit_Time.itemValue="";
		// datescope 日期限制范围 默认为空  2014-12-01,2014-12-31
		policyInfo.date_Scope = new PolicyInfo.Date_Scope();
		policyInfo.date_Scope.itemFlag= "";
		policyInfo.date_Scope.itemValue="";
		
		// timescope 时间限制范围 默认为空 15:45:00,15:47:00
		policyInfo.time_Scope = new PolicyInfo.Time_Scope();
		policyInfo.time_Scope.itemFlag = "";
		policyInfo.time_Scope.itemValue="";
		
		
		/// limitplace 地点限制 默认为0 不管控 
		policyInfo.limit_Place = new PolicyInfo.Limit_Place();
		policyInfo.limit_Place.itemFlag = "0";
		policyInfo.limit_Place.itemValue= "";
		
		
		/// place_scope 地点限制 默认为空 不管控  113.836766,34.539255~113.864434,34.551802
		policyInfo.place_Scope = new PolicyInfo.Place_Scope();
		policyInfo.place_Scope.itemFlag = "";
		policyInfo.place_Scope.itemValue = "";
		
		
		
		policyInfo.setSendnote("当前Policy为默认Policy");

		policyInfo.setSendIp("mdmss.foxconn.com");

		policyInfo.setSendTime(TextFormater.getCurrentDateStringForHome());

		
		
		
		
		
		
		L.d("defPolicyinfo ", policyInfo.toString());
		return policyInfo;
	}

	/**
	 * 
	 * 
	 */

	public PolicyInfo getPolicyInfoFromJson(String jsonPolicy) {
		if (TextUtils.isEmpty(jsonPolicy)
				|| "null".equalsIgnoreCase(jsonPolicy)) {
			// throw new IllegalArgumentException("jsonPolicy is null ");
			return null;
		}
		PolicyInfo policyInfo = new PolicyInfo();

		try {
			JSONObject jsonObject = new JSONObject(jsonPolicy);

			// /// enable_bluetooth

			if (jsonPolicy.contains(PolicyInfo.TAG.tag_Enable_Bluetooth)) {

				JSONObject enable_bluetooth = (JSONObject) jsonObject
						.get(PolicyInfo.TAG.tag_Enable_Bluetooth);
				policyInfo.enable_Bluetooth = new PolicyInfo.Enable_Bluetooth();
				policyInfo.enable_Bluetooth.itemFlag = enable_bluetooth
						.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.enable_Bluetooth.itemValue = enable_bluetooth
						.getString(PolicyInfo.TAG.tag_itemValue);
			}

			if (jsonPolicy.contains(PolicyInfo.TAG.tag_Auto_Synch)) {

				// // autoSync
				JSONObject autoSyncObject = (JSONObject) jsonObject
						.get(PolicyInfo.TAG.tag_Auto_Synch);
				policyInfo.auto_Synch = new PolicyInfo.Auto_Synch();
				policyInfo.auto_Synch.itemFlag = autoSyncObject
						.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.auto_Synch.itemValue = autoSyncObject
						.getString(PolicyInfo.TAG.tag_itemValue);
			}

			// // enable_camera
			if (jsonPolicy.contains(PolicyInfo.TAG.tag_Enable_Camera)) {
				JSONObject enable_camera = (JSONObject) jsonObject
						.get(PolicyInfo.TAG.tag_Enable_Camera);
				policyInfo.enable_Camera = new PolicyInfo.Enable_Camera();
				policyInfo.enable_Camera.itemFlag = enable_camera
						.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.enable_Camera.itemValue = enable_camera
						.getString(PolicyInfo.TAG.tag_itemValue);

			}
			// // enable_Google_play
			if (jsonPolicy.contains(PolicyInfo.TAG.tag_Enable_GoolgePlay)) {

				JSONObject enable_goolgeplay = (JSONObject) jsonObject
						.get(PolicyInfo.TAG.tag_Enable_GoolgePlay);
				policyInfo.enable_Google_play = new PolicyInfo.Enable_Google_play();
				policyInfo.enable_Google_play.itemFlag = enable_goolgeplay
						.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.enable_Google_play.itemValue = enable_goolgeplay
						.getString(PolicyInfo.TAG.tag_itemValue);

			}
			// // enable_SSID
			if (jsonPolicy.contains(PolicyInfo.TAG.tag_Enable_SSID)) {

				JSONObject enable_ssid = (JSONObject) jsonObject
						.get(PolicyInfo.TAG.tag_Enable_SSID);
				policyInfo.enable_SSID = new PolicyInfo.Enable_SSID();
				policyInfo.enable_SSID.itemFlag = enable_ssid
						.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.enable_SSID.itemValue = enable_ssid
						.getString(PolicyInfo.TAG.tag_itemValue);

			}
			// // Enable_USB
			if (jsonPolicy.contains(PolicyInfo.TAG.tag_Enable_USB)) {

				JSONObject enable_usb = (JSONObject) jsonObject
						.get(PolicyInfo.TAG.tag_Enable_USB);
				policyInfo.enable_USB = new PolicyInfo.Enable_USB();
				policyInfo.enable_USB.itemFlag = enable_usb
						.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.enable_USB.itemValue = enable_usb
						.getString(PolicyInfo.TAG.tag_itemValue);

			}
			// // enable_wifi
			if (jsonPolicy.contains(PolicyInfo.TAG.tag_Enable_Wifi)) {

				JSONObject enable_wifi = (JSONObject) jsonObject
						.get(PolicyInfo.TAG.tag_Enable_Wifi);
				policyInfo.enable_Wifi = new PolicyInfo.Enable_Wifi();
				policyInfo.enable_Wifi.itemFlag = enable_wifi
						.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.enable_Wifi.itemValue = enable_wifi
						.getString(PolicyInfo.TAG.tag_itemValue);
			}
			// // hidden_SSID
			if (jsonPolicy.contains(PolicyInfo.TAG.tag_Hidden_SSID)) {
				JSONObject hidden_SSID = (JSONObject) jsonObject
						.get(PolicyInfo.TAG.tag_Hidden_SSID);
				policyInfo.hidden_SSID = new PolicyInfo.Hidden_SSID();
				policyInfo.hidden_SSID.itemFlag = hidden_SSID
						.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.hidden_SSID.itemValue = hidden_SSID
						.getString(PolicyInfo.TAG.tag_itemValue);
			}
			// // error_Count
			if (jsonPolicy.contains(PolicyInfo.TAG.tag_PW_Error_Count)) {

				JSONObject pw_error_count = (JSONObject) jsonObject
						.get(PolicyInfo.TAG.tag_PW_Error_Count);
				policyInfo.error_Count = new PolicyInfo.PW_Error_Count();
				policyInfo.error_Count.itemFlag = pw_error_count
						.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.error_Count.itemValue = pw_error_count
						.getString(PolicyInfo.TAG.tag_itemValue);
			}
			// // PW_Format
			if (jsonPolicy.contains(PolicyInfo.TAG.tag_PW_Format)) {
				JSONObject pw_format = (JSONObject) jsonObject
						.get(PolicyInfo.TAG.tag_PW_Format);
				policyInfo.pw_format = new PolicyInfo.PW_Format();
				policyInfo.pw_format.itemFlag = pw_format
						.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.pw_format.itemValue = pw_format
						.getString(PolicyInfo.TAG.tag_itemValue);
			}
			// // PW_Length
			if (jsonPolicy.contains(PolicyInfo.TAG.tag_PW_Length)) {
				JSONObject pw_Length = (JSONObject) jsonObject
						.get(PolicyInfo.TAG.tag_PW_Length);
				policyInfo.pwLength = new PolicyInfo.PW_Length();
				policyInfo.pwLength.itemFlag = pw_Length
						.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.pwLength.itemValue = pw_Length
						.getString(PolicyInfo.TAG.tag_itemValue);
			}
			// // PW_Lock_Time
			if (jsonPolicy.contains(PolicyInfo.TAG.tag_PW_Lock_Time)) {
				JSONObject pw_lock_time = (JSONObject) jsonObject
						.get(PolicyInfo.TAG.tag_PW_Lock_Time);
				policyInfo.lock_Time = new PolicyInfo.PW_Lock_Time();
				policyInfo.lock_Time.itemFlag = pw_lock_time
						.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.lock_Time.itemValue = pw_lock_time
						.getString(PolicyInfo.TAG.tag_itemValue);

			}
			// // PW_Reset_Period
			if (jsonPolicy.contains(PolicyInfo.TAG.tag_PW_Reset_Period)) {
				JSONObject pw_reset_period = (JSONObject) jsonObject
						.get(PolicyInfo.TAG.tag_PW_Reset_Period);
				policyInfo.reset_Period = new PolicyInfo.PW_Reset_Period();
				policyInfo.reset_Period.itemFlag = pw_reset_period
						.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.reset_Period.itemValue = pw_reset_period
						.getString(PolicyInfo.TAG.tag_itemValue);
			}

			// // Required_PW
			if (jsonPolicy.contains(PolicyInfo.TAG.tag_Required_PW)) {
				JSONObject required_pw = (JSONObject) jsonObject
						.get(PolicyInfo.TAG.tag_Required_PW);
				policyInfo.required_PW = new PolicyInfo.Required_PW();
				policyInfo.required_PW.itemFlag = required_pw
						.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.required_PW.itemValue = required_pw
						.getString(PolicyInfo.TAG.tag_itemValue);
			}

			// // Scan_Devices
			if (jsonPolicy.contains(PolicyInfo.TAG.tag_Scan_Devices)) {
				JSONObject scan_devices = (JSONObject) jsonObject
						.get(PolicyInfo.TAG.tag_Scan_Devices);
				policyInfo.scan_Devices = new PolicyInfo.Scan_Devices();
				policyInfo.scan_Devices.itemFlag = scan_devices
						.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.scan_Devices.itemValue = scan_devices
						.getString(PolicyInfo.TAG.tag_itemValue);
			}

			// // SSID
			if (jsonPolicy.contains(PolicyInfo.TAG.tag_SSID)) {
				JSONObject ssid = (JSONObject) jsonObject
						.get(PolicyInfo.TAG.tag_SSID);
				policyInfo.ssid = new PolicyInfo.SSID();
				policyInfo.ssid.itemFlag = ssid
						.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.ssid.itemValue = ssid
						.getString(PolicyInfo.TAG.tag_itemValue);
			}

			// // SSID_Pre_sharedkey
			if (jsonPolicy.contains(PolicyInfo.TAG.tag_SSID_Pre_sharedkey)) {

				JSONObject ssid_pre_sharedkey = (JSONObject) jsonObject
						.get(PolicyInfo.TAG.tag_SSID_Pre_sharedkey);
				policyInfo.pre_sharedkey = new PolicyInfo.SSID_Pre_sharedkey();
				policyInfo.pre_sharedkey.itemFlag = ssid_pre_sharedkey
						.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.pre_sharedkey.itemValue = ssid_pre_sharedkey
						.getString(PolicyInfo.TAG.tag_itemValue);
			}

			// // SSID_Security
			if (jsonPolicy.contains(PolicyInfo.TAG.tag_SSID_Security)) {

				JSONObject ssid_security = (JSONObject) jsonObject
						.get(PolicyInfo.TAG.tag_SSID_Security);
				policyInfo.ssid_security = new PolicyInfo.SSID_Security();
				policyInfo.ssid_security.itemFlag = ssid_security
						.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.ssid_security.itemValue = ssid_security
						.getString(PolicyInfo.TAG.tag_itemValue);
			}

			// // enable_SSID
			if (jsonPolicy.contains(PolicyInfo.TAG.tag_SSID_WEP_Key1)) {

				JSONObject ssid_wep_key1 = (JSONObject) jsonObject
						.get(PolicyInfo.TAG.tag_SSID_WEP_Key1);
				policyInfo.wep_Key1 = new PolicyInfo.SSID_WEP_Key1();
				policyInfo.wep_Key1.itemFlag = ssid_wep_key1
						.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.wep_Key1.itemValue = ssid_wep_key1
						.getString(PolicyInfo.TAG.tag_itemValue);
			}
			/// limit_Time 
			if(jsonPolicy.contains(PolicyInfo.TAG.tag_limit_time)){
				JSONObject limit_Time = (JSONObject) jsonObject.get(PolicyInfo.TAG.tag_limit_time);
				policyInfo.limit_Time = new PolicyInfo.Limit_Time();
				policyInfo.limit_Time.itemFlag = limit_Time.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.limit_Time.itemValue = limit_Time.getString(PolicyInfo.TAG.tag_itemValue);
			}
			/// date_scope 
			if(jsonPolicy.contains(PolicyInfo.TAG.tag_date_scope)){
				JSONObject date_scope = (JSONObject) jsonObject.get(PolicyInfo.TAG.tag_date_scope);
				policyInfo.date_Scope = new PolicyInfo.Date_Scope();
				policyInfo.date_Scope.itemFlag = date_scope.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.date_Scope.itemValue = date_scope.getString(PolicyInfo.TAG.tag_itemValue);
			}
			
			/// time_scope
			if(jsonPolicy.contains(PolicyInfo.TAG.tag_time_scope)){
				JSONObject time_scope = (JSONObject) jsonObject.get(PolicyInfo.TAG.tag_time_scope);
				policyInfo.time_Scope = new PolicyInfo.Time_Scope();
				policyInfo.time_Scope.itemFlag = time_scope.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.time_Scope.itemValue = time_scope.getString(PolicyInfo.TAG.tag_itemValue);
			}
			/// limit_place
			if(jsonPolicy.contains(PolicyInfo.TAG.tag_limit_place)){
				JSONObject limit_place = (JSONObject) jsonObject.get(PolicyInfo.TAG.tag_limit_place);
				policyInfo.limit_Place = new PolicyInfo.Limit_Place();
				policyInfo.limit_Place.itemFlag = limit_place.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.limit_Place.itemValue = limit_place.getString(PolicyInfo.TAG.tag_itemValue);
			}
			
			/// place_scope 
			if(jsonPolicy.contains(PolicyInfo.TAG.tag_place_scope)){
				JSONObject place_scope = (JSONObject) jsonObject.get(PolicyInfo.TAG.tag_place_scope);
				policyInfo.place_Scope = new PolicyInfo.Place_Scope();
				policyInfo.place_Scope.itemFlag = place_scope.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.place_Scope.itemValue = place_scope.getString(PolicyInfo.TAG.tag_itemValue);
			}
			
			/// unload_emm 
			if(jsonPolicy.contains(PolicyInfo.TAG.tag_unload_emm)){
				JSONObject unload_emm = (JSONObject) jsonObject.get(PolicyInfo.TAG.tag_unload_emm);
				policyInfo.unload_Emm = new PolicyInfo.Unload_Emm();
				policyInfo.unload_Emm.itemFlag = unload_emm.getString(PolicyInfo.TAG.tag_itemFlag);
				policyInfo.unload_Emm.itemValue = unload_emm.getString(PolicyInfo.TAG.tag_itemValue);
			}
			// // 这里区分是sendpolicy和syncpolicy的字段为 note ，
			if (jsonPolicy.contains(TAG.tag_sendnote)) {
				policyInfo.setSendnote(jsonObject.getString(TAG.tag_sendnote));
				policyInfo.setSendTime(jsonObject.getString(TAG.tag_sendtime));
				policyInfo.setSendIp(jsonObject.getString(TAG.tag_sendip));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// L.d("decode Policyinfo ", policyInfo.toString());
		return policyInfo;
	}

	/**
	 * 
	 * 
	 */
	public PolicyInfo getPolicyInfoFromQRJson(String jsonPolicy) {
		if (TextUtils.isEmpty(jsonPolicy)
				|| !(jsonPolicy.contains(QRPolicyInfo.TAG.tag_sendnote))) {
			return null;
		}

		/**
		 * 
		 * 	
		 *  { "nT": "fcycfgh", "a": { "iV": "", "iF": "1" }, "b": { "iV": "2", "iF":
		 * "1" }, "c": { "iV": "5", "iF": "1" }, "d": { "iV": "5", "iF": "1" }, "e":
		 * { "iV": "5", "iF": "1" }, "u": { "iV": "5", "iF": "1" }, "g": { "iV": "",
		 * "iF": "1" }, "h": { "iV": "", "iF": "1" }, "w": { "iV": "jay", "iF": ""
		 * }, "j": { "iV": "", "iF": "1" }, "k": { "iV": "1", "iF": "" }, "y": {
		 * "iV": "ss", "iF": "" }, "m": { "iV": "ss", "iF": "" }, "n": { "iV": "",
		 * "iF": "1" }, "o": { "iV": "", "iF": "1" }, "x": { "iV": "", "iF": "1" },
		 * "q": { "iV": "", "iF": "0" }, "r": { "iV": "", "iF": "0" }, "s": { "iV":
		 * "", "iF": "1" }, "LT": { "iV": "", "iF": "1" }, "DS": { "iV":
		 * "2014-12- 16,2015-01-08", "iF": "" }, "TS": { "iV": "15:36:00,15:37:30",
		 * "iF": "" }, "LP": { "iV": "", "iF": "1" }, "PS": { "iV":
		 * "113.839065,34.530631~113.880891,34.549781", "iF": "" }, "UE": { "iV":
		 * "", "iF": "0" } }
		 * 
		 * 
		 */
		PolicyInfo policyInfo = new PolicyInfo();

		try {
			JSONObject jsonObject = new JSONObject(jsonPolicy);

			if (jsonPolicy.contains(QRPolicyInfo.TAG.tag_Enable_Bluetooth)) {
				// /// enable_bluetooth
				JSONObject enable_bluetooth = (JSONObject) jsonObject
						.get(QRPolicyInfo.TAG.tag_Enable_Bluetooth);
				policyInfo.enable_Bluetooth = new PolicyInfo.Enable_Bluetooth();
				policyInfo.enable_Bluetooth.itemFlag = enable_bluetooth
						.getString(QRPolicyInfo.TAG.tag_itemFlag);
				policyInfo.enable_Bluetooth.itemValue = enable_bluetooth
						.getString(QRPolicyInfo.TAG.tag_itemValue);
			}

			// // autoSync
			if (jsonPolicy.contains(QRPolicyInfo.TAG.tag_Auto_Synch)) {

				JSONObject autoSyncObject = (JSONObject) jsonObject
						.get(QRPolicyInfo.TAG.tag_Auto_Synch);
				policyInfo.auto_Synch = new PolicyInfo.Auto_Synch();
				policyInfo.auto_Synch.itemFlag = autoSyncObject
						.getString(QRPolicyInfo.TAG.tag_itemFlag);
				policyInfo.auto_Synch.itemValue = autoSyncObject
						.getString(QRPolicyInfo.TAG.tag_itemValue);
			}
			// // enable_camera
			if (jsonPolicy.contains(QRPolicyInfo.TAG.tag_Enable_Camera)) {

				JSONObject enable_camera = (JSONObject) jsonObject
						.get(QRPolicyInfo.TAG.tag_Enable_Camera);
				policyInfo.enable_Camera = new PolicyInfo.Enable_Camera();
				policyInfo.enable_Camera.itemFlag = enable_camera
						.getString(QRPolicyInfo.TAG.tag_itemFlag);
				policyInfo.enable_Camera.itemValue = enable_camera
						.getString(QRPolicyInfo.TAG.tag_itemValue);
			}

			// // sendnote
			if (jsonPolicy.contains(QRPolicyInfo.TAG.tag_sendnote)) {
				policyInfo.setSendnote(jsonObject
						.getString(QRPolicyInfo.TAG.tag_sendnote));
			}

			// // enable_Google_play
			if (jsonPolicy.contains(QRPolicyInfo.TAG.tag_Enable_GoolgePlay)) {

				JSONObject enable_goolgeplay = (JSONObject) jsonObject
						.get(QRPolicyInfo.TAG.tag_Enable_GoolgePlay);
				policyInfo.enable_Google_play = new PolicyInfo.Enable_Google_play();
				policyInfo.enable_Google_play.itemFlag = enable_goolgeplay
						.getString(QRPolicyInfo.TAG.tag_itemFlag);
				policyInfo.enable_Google_play.itemValue = enable_goolgeplay
						.getString(QRPolicyInfo.TAG.tag_itemValue);
			}

			// // Enable_USB

			if (jsonPolicy.contains(QRPolicyInfo.TAG.tag_Enable_USB)) {

				JSONObject enable_usb = (JSONObject) jsonObject
						.get(QRPolicyInfo.TAG.tag_Enable_USB);
				policyInfo.enable_USB = new PolicyInfo.Enable_USB();
				policyInfo.enable_USB.itemFlag = enable_usb
						.getString(QRPolicyInfo.TAG.tag_itemFlag);
				policyInfo.enable_USB.itemValue = enable_usb
						.getString(QRPolicyInfo.TAG.tag_itemValue);
			}
			// // enable_wifi

			if (jsonPolicy.contains(QRPolicyInfo.TAG.tag_Enable_Wifi)) {

				JSONObject enable_wifi = (JSONObject) jsonObject
						.get(QRPolicyInfo.TAG.tag_Enable_Wifi);
				policyInfo.enable_Wifi = new PolicyInfo.Enable_Wifi();
				policyInfo.enable_Wifi.itemFlag = enable_wifi
						.getString(QRPolicyInfo.TAG.tag_itemFlag);
				policyInfo.enable_Wifi.itemValue = enable_wifi
						.getString(QRPolicyInfo.TAG.tag_itemValue);

			}
			// // hidden_SSID
			if (jsonPolicy.contains(QRPolicyInfo.TAG.tag_Hidden_SSID)) {

				JSONObject hidden_SSID = (JSONObject) jsonObject
						.get(QRPolicyInfo.TAG.tag_Hidden_SSID);
				policyInfo.hidden_SSID = new PolicyInfo.Hidden_SSID();
				policyInfo.hidden_SSID.itemFlag = hidden_SSID
						.getString(QRPolicyInfo.TAG.tag_itemFlag);
				policyInfo.hidden_SSID.itemValue = hidden_SSID
						.getString(QRPolicyInfo.TAG.tag_itemValue);
			}

			// // error_Count
			if (jsonPolicy.contains(QRPolicyInfo.TAG.tag_PW_Error_Count)) {

				JSONObject pw_error_count = (JSONObject) jsonObject
						.get(QRPolicyInfo.TAG.tag_PW_Error_Count);
				policyInfo.error_Count = new PolicyInfo.PW_Error_Count();
				policyInfo.error_Count.itemFlag = pw_error_count
						.getString(QRPolicyInfo.TAG.tag_itemFlag);
				policyInfo.error_Count.itemValue = pw_error_count
						.getString(QRPolicyInfo.TAG.tag_itemValue);
			}

			// // PW_Format
			if (jsonPolicy.contains(QRPolicyInfo.TAG.tag_PW_Format)) {
				JSONObject pw_format = (JSONObject) jsonObject
						.get(QRPolicyInfo.TAG.tag_PW_Format);
				policyInfo.pw_format = new PolicyInfo.PW_Format();
				policyInfo.pw_format.itemFlag = pw_format
						.getString(QRPolicyInfo.TAG.tag_itemFlag);
				policyInfo.pw_format.itemValue = pw_format
						.getString(QRPolicyInfo.TAG.tag_itemValue);
			}

			// // PW_Length
			if (jsonPolicy.contains(QRPolicyInfo.TAG.tag_PW_Length)) {

				JSONObject pw_Length = (JSONObject) jsonObject
						.get(QRPolicyInfo.TAG.tag_PW_Length);
				policyInfo.pwLength = new PolicyInfo.PW_Length();
				policyInfo.pwLength.itemFlag = pw_Length
						.getString(QRPolicyInfo.TAG.tag_itemFlag);
				policyInfo.pwLength.itemValue = pw_Length
						.getString(QRPolicyInfo.TAG.tag_itemValue);
			}

			// // PW_Lock_Time

			if (jsonPolicy.contains(QRPolicyInfo.TAG.tag_PW_Lock_Time)) {
				JSONObject pw_lock_time = (JSONObject) jsonObject
						.get(QRPolicyInfo.TAG.tag_PW_Lock_Time);
				policyInfo.lock_Time = new PolicyInfo.PW_Lock_Time();
				policyInfo.lock_Time.itemFlag = pw_lock_time
						.getString(QRPolicyInfo.TAG.tag_itemFlag);
				policyInfo.lock_Time.itemValue = pw_lock_time
						.getString(QRPolicyInfo.TAG.tag_itemValue);
			}

			// // PW_Reset_Period
			if (jsonPolicy.contains(QRPolicyInfo.TAG.tag_PW_Reset_Period)) {

				JSONObject pw_reset_period = (JSONObject) jsonObject
						.get(QRPolicyInfo.TAG.tag_PW_Reset_Period);
				policyInfo.reset_Period = new PolicyInfo.PW_Reset_Period();
				policyInfo.reset_Period.itemFlag = pw_reset_period
						.getString(QRPolicyInfo.TAG.tag_itemFlag);
				policyInfo.reset_Period.itemValue = pw_reset_period
						.getString(QRPolicyInfo.TAG.tag_itemValue);
			}

			// // Required_PW
			if (jsonPolicy.contains(QRPolicyInfo.TAG.tag_Required_PW)) {

				JSONObject required_pw = (JSONObject) jsonObject
						.get(QRPolicyInfo.TAG.tag_Required_PW);
				policyInfo.required_PW = new PolicyInfo.Required_PW();
				policyInfo.required_PW.itemFlag = required_pw
						.getString(QRPolicyInfo.TAG.tag_itemFlag);
				policyInfo.required_PW.itemValue = required_pw
						.getString(QRPolicyInfo.TAG.tag_itemValue);
			}
			// // Scan_Devices
			if (jsonPolicy.contains(QRPolicyInfo.TAG.tag_Scan_Devices)) {
				JSONObject scan_devices = (JSONObject) jsonObject
						.get(QRPolicyInfo.TAG.tag_Scan_Devices);
				policyInfo.scan_Devices = new PolicyInfo.Scan_Devices();
				policyInfo.scan_Devices.itemFlag = scan_devices
						.getString(QRPolicyInfo.TAG.tag_itemFlag);
				policyInfo.scan_Devices.itemValue = scan_devices
						.getString(QRPolicyInfo.TAG.tag_itemValue);
			}
			// // SSID
			if (jsonPolicy.contains(QRPolicyInfo.TAG.tag_SSID)) {
				JSONObject ssid = (JSONObject) jsonObject
						.get(QRPolicyInfo.TAG.tag_SSID);
				policyInfo.ssid = new PolicyInfo.SSID();
				policyInfo.ssid.itemFlag = ssid
						.getString(QRPolicyInfo.TAG.tag_itemFlag);
				policyInfo.ssid.itemValue = ssid
						.getString(QRPolicyInfo.TAG.tag_itemValue);
			}

			// // SSID_Pre_sharedkey
			if (jsonPolicy.contains(QRPolicyInfo.TAG.tag_SSID_Pre_sharedkey)) {
				JSONObject ssid_pre_sharedkey = (JSONObject) jsonObject
						.get(QRPolicyInfo.TAG.tag_SSID_Pre_sharedkey);
				policyInfo.pre_sharedkey = new PolicyInfo.SSID_Pre_sharedkey();
				policyInfo.pre_sharedkey.itemFlag = ssid_pre_sharedkey
						.getString(QRPolicyInfo.TAG.tag_itemFlag);
				policyInfo.pre_sharedkey.itemValue = ssid_pre_sharedkey
						.getString(QRPolicyInfo.TAG.tag_itemValue);
			}

			// // SSID_Security
			if (jsonPolicy.contains(QRPolicyInfo.TAG.tag_SSID_Security)) {
				JSONObject ssid_security = (JSONObject) jsonObject
						.get(QRPolicyInfo.TAG.tag_SSID_Security);
				policyInfo.ssid_security = new PolicyInfo.SSID_Security();
				policyInfo.ssid_security.itemFlag = ssid_security
						.getString(QRPolicyInfo.TAG.tag_itemFlag);
				policyInfo.ssid_security.itemValue = ssid_security
						.getString(QRPolicyInfo.TAG.tag_itemValue);

			}
			// // enable_SSID
			if (jsonPolicy.contains(QRPolicyInfo.TAG.tag_SSID_WEP_Key1)) {
				JSONObject ssid_wep_key1 = (JSONObject) jsonObject
						.get(QRPolicyInfo.TAG.tag_SSID_WEP_Key1);
				policyInfo.wep_Key1 = new PolicyInfo.SSID_WEP_Key1();
				policyInfo.wep_Key1.itemFlag = ssid_wep_key1
						.getString(QRPolicyInfo.TAG.tag_itemFlag);
				policyInfo.wep_Key1.itemValue = ssid_wep_key1
						.getString(QRPolicyInfo.TAG.tag_itemValue);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		L.d("decode Policyinfo from qrjson  ", policyInfo.toString());
		return policyInfo;
	}

	private Auto_Synch auto_Synch;

	private String COMMAND;

	private String DAY_OF_WEEK;

	private Enable_Bluetooth enable_Bluetooth;

	private Enable_Camera enable_Camera;

	private Enable_Google_play enable_Google_play;

	private Enable_SSID enable_SSID;

	private Enable_USB enable_USB;

	private Enable_Wifi enable_Wifi;

	private String END_TIME;

	private PW_Error_Count error_Count;

	private Hidden_SSID hidden_SSID;

	private PW_Lock_Time lock_Time;

	private String POLICY_ID;

	private SSID_Pre_sharedkey pre_sharedkey;

	private PW_Format pw_format;

	private PW_Length pwLength;

	private Required_PW required_PW;

	private PW_Reset_Period reset_Period;

	private Scan_Bluetooth scan_Bluetooth;

	private Scan_Devices scan_Devices;

	private String SEND_TIME;

	private SSID ssid;

	private SSID_Security ssid_security;

	private String START_TIME;

	private SSID_WEP_Key1 wep_Key1;

	private Unload_Emm unload_Emm;
	
	private Limit_Time limit_Time;
	
	private Date_Scope date_Scope;
	
	private Time_Scope time_Scope;
	
	private Limit_Place limit_Place;
	
	private Place_Scope place_Scope;
	
	
	
	
	/**
	 * @return the auto_Synch
	 */
	public Auto_Synch getAuto_Synch() {
		return auto_Synch;
	}

	/**
	 * @return the enable_Bluetooth
	 */
	public Enable_Bluetooth getEnable_Bluetooth() {
		return enable_Bluetooth;
	}

	/**
	 * @return the enable_Camera
	 */
	public Enable_Camera getEnable_Camera() {
		return enable_Camera;
	}

	/**
	 * @return the enable_Google_play
	 */
	public Enable_Google_play getEnable_Google_play() {
		return enable_Google_play;
	}

	/**
	 * @return the enable_SSID
	 */
	public Enable_SSID getEnable_SSID() {
		return enable_SSID;
	}

	/**
	 * @return the enable_USB
	 */
	public Enable_USB getEnable_USB() {
		return enable_USB;
	}

	/**
	 * @return the enable_Wifi
	 */
	public Enable_Wifi getEnable_Wifi() {
		return enable_Wifi;
	}

	/**
	 * @return the error_Count
	 */
	public PW_Error_Count getError_Count() {
		return error_Count;
	}

	/**
	 * @return the hidden_SSID
	 */
	public Hidden_SSID getHidden_SSID() {
		return hidden_SSID;
	}

	/**
	 * @return the lock_Time
	 */
	public PW_Lock_Time getLock_Time() {
		return lock_Time;
	}

	/**
	 * @return the pre_sharedkey
	 */
	public SSID_Pre_sharedkey getPre_sharedkey() {
		return pre_sharedkey;
	}

	/**
	 * @return the pw_format
	 */
	public PW_Format getPw_format() {
		return pw_format;
	}

	/**
	 * @return the pwLength
	 */
	public PW_Length getPwLength() {
		return pwLength;
	}

	/**
	 * @return the required_PW
	 */
	public Required_PW getRequired_PW() {
		return required_PW;
	}

	/**
	 * @return the reset_Period
	 */
	public PW_Reset_Period getReset_Period() {
		return reset_Period;
	}

	/**
	 * @return the scan_Bluetooth
	 */
	public Scan_Bluetooth getScan_Bluetooth() {
		return scan_Bluetooth;
	}

	/**
	 * @return the scan_Devices
	 */
	public Scan_Devices getScan_Devices() {
		return scan_Devices;
	}

	/**
	 * @return the ssid
	 */
	public SSID getSsid() {
		return ssid;
	}

	/**
	 * @return the ssid_security
	 */
	public SSID_Security getSsid_security() {
		return ssid_security;
	}

	/**
	 * @return the wep_Key1
	 */
	public SSID_WEP_Key1 getWep_Key1() {
		return wep_Key1;
	}

	/**
	 * @param auto_Synch
	 *            the auto_Synch to set
	 */
	public void setAuto_Synch(Auto_Synch auto_Synch) {
		this.auto_Synch = auto_Synch;
	}

	/**
	 * @param enable_Bluetooth
	 *            the enable_Bluetooth to set
	 */
	public void setEnable_Bluetooth(Enable_Bluetooth enable_Bluetooth) {
		this.enable_Bluetooth = enable_Bluetooth;
	}

	/**
	 * @param enable_Camera
	 *            the enable_Camera to set
	 */
	public void setEnable_Camera(Enable_Camera enable_Camera) {
		this.enable_Camera = enable_Camera;
	}

	/**
	 * @param enable_Google_play
	 *            the enable_Google_play to set
	 */
	public void setEnable_Google_play(Enable_Google_play enable_Google_play) {
		this.enable_Google_play = enable_Google_play;
	}

	/**
	 * @param enable_SSID
	 *            the enable_SSID to set
	 */
	public void setEnable_SSID(Enable_SSID enable_SSID) {
		this.enable_SSID = enable_SSID;
	}

	/**
	 * @param enable_USB
	 *            the enable_USB to set
	 */
	public void setEnable_USB(Enable_USB enable_USB) {
		this.enable_USB = enable_USB;
	}

	/**
	 * @param enable_Wifi
	 *            the enable_Wifi to set
	 */
	public void setEnable_Wifi(Enable_Wifi enable_Wifi) {
		this.enable_Wifi = enable_Wifi;
	}

	/**
	 * @param error_Count
	 *            the error_Count to set
	 */
	public void setError_Count(PW_Error_Count error_Count) {
		this.error_Count = error_Count;
	}

	/**
	 * @param hidden_SSID
	 *            the hidden_SSID to set
	 */
	public void setHidden_SSID(Hidden_SSID hidden_SSID) {
		this.hidden_SSID = hidden_SSID;
	}

	/**
	 * @param lock_Time
	 *            the lock_Time to set
	 */
	public void setLock_Time(PW_Lock_Time lock_Time) {
		this.lock_Time = lock_Time;
	}

	/**
	 * @param pre_sharedkey
	 *            the pre_sharedkey to set
	 */
	public void setPre_sharedkey(SSID_Pre_sharedkey pre_sharedkey) {
		this.pre_sharedkey = pre_sharedkey;
	}

	/**
	 * @param pw_format
	 *            the pw_format to set
	 */
	public void setPw_format(PW_Format pw_format) {
		this.pw_format = pw_format;
	}

	/**
	 * @param pwLength
	 *            the pwLength to set
	 */
	public void setPwLength(PW_Length pwLength) {
		this.pwLength = pwLength;
	}

	/**
	 * @param required_PW
	 *            the required_PW to set
	 */
	public void setRequired_PW(Required_PW required_PW) {
		this.required_PW = required_PW;
	}

	/**
	 * @param reset_Period
	 *            the reset_Period to set
	 */
	public void setReset_Period(PW_Reset_Period reset_Period) {
		this.reset_Period = reset_Period;
	}

	/**
	 * @param scan_Bluetooth
	 *            the scan_Bluetooth to set
	 */
	public void setScan_Bluetooth(Scan_Bluetooth scan_Bluetooth) {
		this.scan_Bluetooth = scan_Bluetooth;
	}

	/**
	 * @param scan_Devices
	 *            the scan_Devices to set
	 */
	public void setScan_Devices(Scan_Devices scan_Devices) {
		this.scan_Devices = scan_Devices;
	}

	/**
	 * @param ssid
	 *            the ssid to set
	 */
	public void setSsid(SSID ssid) {
		this.ssid = ssid;
	}

	/**
	 * @param ssid_security
	 *            the ssid_security to set
	 */
	public void setSsid_security(SSID_Security ssid_security) {
		this.ssid_security = ssid_security;
	}

	/**
	 * @param wep_Key1
	 *            the wep_Key1 to set
	 */
	public void setWep_Key1(SSID_WEP_Key1 wep_Key1) {
		this.wep_Key1 = wep_Key1;
	}

	

	@Override
	public String toString() {
		return "PolicyInfo [sendTime=" + sendTime + ", sendIp=" + sendIp
				+ ", sendnote=" + sendnote + ", auto_Synch=" + auto_Synch
				+ ", COMMAND=" + COMMAND + ", DAY_OF_WEEK=" + DAY_OF_WEEK
				+ ", enable_Bluetooth=" + enable_Bluetooth + ", enable_Camera="
				+ enable_Camera + ", enable_Google_play=" + enable_Google_play
				+ ", enable_SSID=" + enable_SSID + ", enable_USB=" + enable_USB
				+ ", enable_Wifi=" + enable_Wifi + ", END_TIME=" + END_TIME
				+ ", error_Count=" + error_Count + ", hidden_SSID="
				+ hidden_SSID + ", lock_Time=" + lock_Time + ", POLICY_ID="
				+ POLICY_ID + ", pre_sharedkey=" + pre_sharedkey
				+ ", pw_format=" + pw_format + ", pwLength=" + pwLength
				+ ", required_PW=" + required_PW + ", reset_Period="
				+ reset_Period + ", scan_Bluetooth=" + scan_Bluetooth
				+ ", scan_Devices=" + scan_Devices + ", SEND_TIME=" + SEND_TIME
				+ ", ssid=" + ssid + ", ssid_security=" + ssid_security
				+ ", START_TIME=" + START_TIME + ", wep_Key1=" + wep_Key1
				+ ", unload_Emm=" + unload_Emm + ", limit_Time=" + limit_Time
				+ ", date_Scope=" + date_Scope + ", time_Scope=" + time_Scope
				+ ", limit_Place=" + limit_Place + ", place_Scope="
				+ place_Scope + "]";
	}

	/**
	 * @return the sendTime
	 */
	public String getSendTime() {
		return sendTime;
	}

	/**
	 * @param sendTime
	 *            the sendTime to set
	 */
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	/**
	 * @return the sendIp
	 */
	public String getSendIp() {
		return sendIp;
	}

	/**
	 * @param sendIp
	 *            the sendIp to set
	 */
	public void setSendIp(String sendIp) {
		this.sendIp = sendIp;
	}

	/**
	 * @return the sendnote
	 */
	public String getSendnote() {
		return sendnote;
	}

	/**
	 * @param sendnote
	 *            the sendnote to set
	 */
	public void setSendnote(String sendnote) {
		this.sendnote = sendnote;
	}

	/**
	 * @return the unload_Emm
	 */
	public Unload_Emm getUnload_Emm() {
		return unload_Emm;
	}

	/**
	 * @param unload_Emm the unload_Emm to set
	 */
	public void setUnload_Emm(Unload_Emm unload_Emm) {
		this.unload_Emm = unload_Emm;
	}

	/**
	 * @return the limit_Time
	 */
	public Limit_Time getLimit_Time() {
		return limit_Time;
	}

	/**
	 * @param limit_Time the limit_Time to set
	 */
	public void setLimit_Time(Limit_Time limit_Time) {
		this.limit_Time = limit_Time;
	}

	/**
	 * @return the date_Scope
	 */
	public Date_Scope getDate_Scope() {
		return date_Scope;
	}

	/**
	 * @param date_Scope the date_Scope to set
	 */
	public void setDate_Scope(Date_Scope date_Scope) {
		this.date_Scope = date_Scope;
	}

	/**
	 * @return the time_Scope
	 */
	public Time_Scope getTime_Scope() {
		return time_Scope;
	}

	/**
	 * @param time_Scope the time_Scope to set
	 */
	public void setTime_Scope(Time_Scope time_Scope) {
		this.time_Scope = time_Scope;
	}

	/**
	 * @return the limit_Place
	 */
	public Limit_Place getLimit_Place() {
		return limit_Place;
	}

	/**
	 * @param limit_Place the limit_Place to set
	 */
	public void setLimit_Place(Limit_Place limit_Place) {
		this.limit_Place = limit_Place;
	}

	/**
	 * @return the place_Scope
	 */
	public Place_Scope getPlace_Scope() {
		return place_Scope;
	}

	/**
	 * @param place_Scope the place_Scope to set
	 */
	public void setPlace_Scope(Place_Scope place_Scope) {
		this.place_Scope = place_Scope;
	}
}