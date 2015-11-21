package com.foxconn.emm.bean;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class TimerJSONPolicy {

	
	public static final String DEFAULTPOLICYNO = "P0000000000000000000";
	
	private String IP;
	private String COMMAND;

	private String POLICY_ID;

	private String PRIORITY;
	private String Required_PW;
	private String PW_Format;
	private String PW_Length;
	private String PW_Error_Count;
	private String PW_Lock_Time;
	private String PW_Reset_Period;
	private String Auto_Synch;
	private String ARoaming;
	private String Enable_Bluetooth;
	private String Scan_Devices;
	private String Enable_Wifi;
	private String Enable_SSID;
	private String SSID;
	private String Hidden_SSID;
	private String SSID_Security;
	private String SSID_Pre_sharedkey;
	private String SSID_WEP_Key1;
	private String Enable_Camera;
	private String Enable_Google_play;
	private String Enable_USB;
	private String Hidden_APP;
	private String UPDATE_DATE;
	private String SEND_TIME;

	private String DAY_OF_WEEK;

	private String START_TIME;

	private String END_TIME;

	/**
	 * "Enable_Bluetooth":"1","Scan_Devices":"0","Enable_Camera":"0",
	 * "Enable_Google_play"
	 * :"0","Enable_USB":"0","Hidden_APP":"0","Auto_Synch":"0"
	 * ,"PW_Error_Count":"0","P
	 * 
	 * W_Format":"0","PW_Length":"0","PW_Lock_Time":"0","PW_Reset_Period":"0","
	 * Required_PW
	 * ":"0","Roaming":"0","Enable_SSID":"0","Enable_Wifi":"0","Hidden_SSID
	 * ":"1","SSID
	 * 
	 * ":"0","SSID_Pre_sharedkey":"0","SSID_Security":"0","SSID_WEP_Key1":"0","
	 * PRIORITY
	 * ":"20","DAY_OF_WEEK":"1","START_TIME":"0200","END_TIME":"0700","COMMAND
	 * ":"sendpolicy","IP":"10.195.248.114","GCMID":"d02788fface3","POLICY_ID":"
	 * P00000000
	 * 
	 * 00000000551","SEND_TIME":"1397802738844"
	 * 
	 */

	/**
	 * reset values
	 * 
	 * @param jsonPolicy
	 */
	public static TimerJSONPolicy reSetVal(TimerJSONPolicy jsonPolicy) {

		String update_date = jsonPolicy.UPDATE_DATE;
		/**
		 * 如果設備沒有收到默認的policy 則重置默認的policy
		 * 
		 */
		// 20140122 改
		// if
		// (jsonPolicy.getPolicyID().equalsIgnoreCase("p0000000000000000000")) {
		// jsonPolicy = new JSONPolicy();
		// jsonPolicy.POLICY_ID = "p0000000000000000000";
		// jsonPolicy.PRIORITY = "0";
		// jsonPolicy.Required_PW = "0";
		// jsonPolicy.Enable_Bluetooth = "0";
		// jsonPolicy.Enable_Camera = "0";
		// jsonPolicy.Enable_Wifi = "1";
		// jsonPolicy.Auto_Synch = "1";
		// jsonPolicy.Enable_USB = "1";
		// jsonPolicy.Enable_Google_play = "1";
		// jsonPolicy.Hidden_APP = "0";
		// jsonPolicy.PW_Error_Count = "0";
		// jsonPolicy.PW_Format = "0";
		// jsonPolicy.PW_Length = "0";
		// jsonPolicy.PW_Lock_Time = "0";
		// jsonPolicy.PW_Reset_Period = "0";
		// jsonPolicy.ARoaming = "1";
		// jsonPolicy.Scan_Devices = "1";
		// jsonPolicy.SSID = "0";
		// jsonPolicy.Hidden_SSID = "0";
		// jsonPolicy.SSID_Security = "0";
		// jsonPolicy.SSID_Pre_sharedkey = "0";
		// jsonPolicy.SSID_WEP_Key1 = "0";
		// jsonPolicy.Enable_SSID = "0";
		// jsonPolicy.UPDATE_DATE = update_date;
		// return jsonPolicy;
		//
		// } else {

		if (jsonPolicy.Required_PW.isEmpty()) {
			jsonPolicy.Required_PW = "0";
		}
		if (jsonPolicy.Enable_Bluetooth.isEmpty()) {
			jsonPolicy.Enable_Bluetooth = "0";
		}
		if (jsonPolicy.Enable_Camera.isEmpty()) {
			jsonPolicy.Enable_Camera = "0";
		}
		if (jsonPolicy.Enable_Wifi.isEmpty()) {
			jsonPolicy.Enable_Wifi = "0";
		}
		if (jsonPolicy.Auto_Synch.isEmpty()) {
			jsonPolicy.Auto_Synch = "0";
		}
		if (jsonPolicy.Enable_USB.isEmpty()) {
			jsonPolicy.Enable_USB = "0";
		}
		if (jsonPolicy.Enable_Google_play.isEmpty()) {
			jsonPolicy.Enable_Google_play = "0";
		}
		if (jsonPolicy.Hidden_APP.isEmpty()) {
			jsonPolicy.Hidden_APP = "0";
		}
		// }
		return jsonPolicy;

	}

	/**
	 * getNewPolicy
	 * 
	 * @return
	 */
	public static TimerJSONPolicy getNewPolicy() {
		TimerJSONPolicy jsonPolicy = new TimerJSONPolicy();
		jsonPolicy.PW_Error_Count = "0";
		jsonPolicy.Enable_SSID = "0";
		jsonPolicy.PW_Reset_Period = "0";
		jsonPolicy.Auto_Synch = "1";
		jsonPolicy.ARoaming = "0";
		jsonPolicy.Enable_Bluetooth = "0";
		jsonPolicy.Scan_Devices = "0";
		jsonPolicy.Enable_Wifi = "1";
		jsonPolicy.SSID = "0";
		jsonPolicy.Hidden_SSID = "0";
		jsonPolicy.SSID_Security = "0";
		jsonPolicy.SSID_Pre_sharedkey = "0";
		jsonPolicy.SSID_WEP_Key1 = "0";
		jsonPolicy.Enable_Camera = "1";
		jsonPolicy.Enable_Google_play = "1";
		jsonPolicy.Enable_USB = "1";
		jsonPolicy.Hidden_APP = "0";
		jsonPolicy.PW_Lock_Time = "0";
		jsonPolicy.PW_Length = "0";
		jsonPolicy.PW_Format = "0";
		jsonPolicy.Required_PW = "0";
		return jsonPolicy;
	}

	/**
	 * get default policy ;
	 * 
	 * @return
	 */
	public static TimerJSONPolicy getDefPolicy() {
		TimerJSONPolicy jsonPolicy = new TimerJSONPolicy();
		jsonPolicy.POLICY_ID="P0000000000000000000";
		jsonPolicy.UPDATE_DATE="2014/05/29 15:29:36";
		jsonPolicy.PW_Error_Count = "0";
		jsonPolicy.Enable_SSID = "0";
		jsonPolicy.PW_Reset_Period = "0";
		jsonPolicy.Auto_Synch = "1";
		jsonPolicy.ARoaming = "0";
		jsonPolicy.Enable_Bluetooth = "0";
		jsonPolicy.Scan_Devices = "0";
		jsonPolicy.Enable_Wifi = "1";
		jsonPolicy.SSID = "0";
		jsonPolicy.Hidden_SSID = "0";
		jsonPolicy.SSID_Security = "0";
		jsonPolicy.SSID_Pre_sharedkey = "0";
		jsonPolicy.SSID_WEP_Key1 = "0";
		jsonPolicy.Enable_Camera = "1";
		jsonPolicy.Enable_Google_play = "1";
		jsonPolicy.Enable_USB = "1";
		jsonPolicy.Hidden_APP = "0";
		jsonPolicy.PW_Lock_Time = "0";
		jsonPolicy.PW_Length = "0";
		jsonPolicy.PW_Format = "0";
		jsonPolicy.Required_PW = "0";
		return jsonPolicy;
	}

	/**
	 * get default null policy ;
	 * 
	 * @return
	 */
	public static TimerJSONPolicy getNullPolicy() {
		TimerJSONPolicy jsonPolicy = new TimerJSONPolicy();
		jsonPolicy.POLICY_ID="P0000000000000000000";
		jsonPolicy.UPDATE_DATE="2014/05/29 15:29:36";
		jsonPolicy.PW_Error_Count = "0";
		jsonPolicy.Enable_SSID = "0";
		jsonPolicy.PW_Reset_Period = "0";
		jsonPolicy.Auto_Synch = "1";
		jsonPolicy.ARoaming = "0";
		jsonPolicy.Enable_Bluetooth = "0";
		jsonPolicy.Scan_Devices = "0";
		jsonPolicy.Enable_Wifi = "1";
		jsonPolicy.SSID = "0";
		jsonPolicy.Hidden_SSID = "0";
		jsonPolicy.SSID_Security = "0";
		jsonPolicy.SSID_Pre_sharedkey = "0";
		jsonPolicy.SSID_WEP_Key1 = "0";
		jsonPolicy.Enable_Camera = "1";
		jsonPolicy.Enable_Google_play = "1";
		jsonPolicy.Enable_USB = "1";
		jsonPolicy.Hidden_APP = "0";
		jsonPolicy.PW_Lock_Time = "0";
		jsonPolicy.PW_Length = "0";
		jsonPolicy.PW_Format = "0";
		jsonPolicy.Required_PW = "0";
		return jsonPolicy;
	}

	
	
	
	
	public static TimerJSONPolicy getAllUsePolicy() {
		TimerJSONPolicy jsonPolicy = new TimerJSONPolicy();
		jsonPolicy.PW_Error_Count = "0";
		jsonPolicy.Enable_SSID = "0";
		jsonPolicy.PW_Reset_Period = "0";
		jsonPolicy.Auto_Synch = "1";
		jsonPolicy.ARoaming = "0";
		jsonPolicy.Enable_Bluetooth = "0";
		jsonPolicy.Scan_Devices = "0";
		jsonPolicy.Enable_Wifi = "1";
		jsonPolicy.SSID = "0";
		jsonPolicy.Hidden_SSID = "0";
		jsonPolicy.SSID_Security = "0";
		jsonPolicy.SSID_Pre_sharedkey = "0";
		jsonPolicy.SSID_WEP_Key1 = "0";
		jsonPolicy.Enable_Camera = "1";
		jsonPolicy.Enable_Google_play = "1";
		jsonPolicy.Enable_USB = "1";
		jsonPolicy.Hidden_APP = "0";
		jsonPolicy.PW_Lock_Time = "0";
		jsonPolicy.PW_Length = "0";
		jsonPolicy.PW_Format = "0";
		jsonPolicy.Required_PW = "0";
		return jsonPolicy;
	}

	public static  TimerJSONPolicy getTimerJSONPolicy(String timerJsonPolicy) {
		return new Gson().fromJson(timerJsonPolicy, TimerJSONPolicy.class);
	}

	/**
	 * @param <E>
	 * @param listData
	 * @return
	 */
	public static <E> String convertToJson(String listData) {
		Gson gson = new Gson();
		String result = gson.toJson(listData);
		// System.out.println(result);
		return result;
	}

	/**
	 * @param obj
	 * @return
	 */
	public String convertToTreeJson(Object obj) {
		Gson gson = new Gson();
		// JsonObject myObj = new JsonObject();
		JsonElement jsonEle = gson.toJsonTree(obj);
		String result = gson.toJson(jsonEle);
		return result;
	}

	public String getUpdateDate() {
		return UPDATE_DATE;
	}

	public void setUpdateDate(String uPDATE_DATE) {
		UPDATE_DATE = uPDATE_DATE;
	}

	public void SetPolicyID(String s) {
		POLICY_ID = s;
	}

	public String getPolicyID() {
		return POLICY_ID;
	}

	public void setPriority(String s) {
		PRIORITY = s;
	}

	public String getPriority() {
		return PRIORITY;
	}

	public void setRequiredPW(String s) {
		Required_PW = s;
	}

	public String getRequiredPW() {
		return Required_PW;
	}

	public void setPWFormat(String s) {
		PW_Format = s;
	}

	public String getPWFormat() {
		return PW_Format;
	}

	public void setPWLength(String s) {
		PW_Length = s;
	}

	public String getPWLength() {
		return PW_Length;
	}

	public void setPWErrorCount(String s) {
		PW_Error_Count = s;
	}

	public String getPWErrorCount() {
		return PW_Error_Count;
	}

	public void setPWLockTime(String s) {
		PW_Lock_Time = s;
	}

	public String getPWLockTime() {
		return PW_Lock_Time;
	}

	public void setPWRestPeriod(String s) {
		PW_Reset_Period = s;
	}

	public String getPWRestPeriod() {
		return PW_Reset_Period;
	}

	public void setAutoSynch(String s) {
		Auto_Synch = s;
	}

	public String getAutoSynch() {
		return Auto_Synch;
	}

	public void setARoaming(String s) {
		ARoaming = s;
	}

	public String getARoaming() {
		return ARoaming;
	}

	public void setEnableBT(String s) {
		Enable_Bluetooth = s;
	}

	public String getEnableBT() {
		return Enable_Bluetooth;
	}

	public void setScanDevices(String s) {
		Scan_Devices = s;
	}

	public String getScanDevices() {
		return Scan_Devices;
	}

	public void setEnableWIFI(String s) {
		Enable_Wifi = s;
	}

	public String getEnableWIFI() {
		return Enable_Wifi;
	}

	public void setEnableSSID(String s) {
		Enable_SSID = s;
	}

	public String getEnableSSID() {
		return Enable_SSID;
	}

	public void setSSID(String s) {
		SSID = s;
	}

	public String getSSID() {
		return SSID;
	}

	public void setHiddenSSID(String s) {
		Hidden_SSID = s;
	}

	public String getHiddenSSID() {
		return Hidden_SSID;
	}

	public void setSSIDSecurity(String s) {
		SSID_Security = s;
	}

	public String getSSIDSecurity() {
		return SSID_Security;
	}

	public void setSSIDPresharedkey(String s) {
		SSID_Pre_sharedkey = s;
	}

	public String getSSIDPresharedkey() {
		return SSID_Pre_sharedkey;
	}

	public void setSSIDWepKey(String s) {
		SSID_WEP_Key1 = s;
	}

	public String getSSIDWepKey() {
		return SSID_WEP_Key1;
	}

	public void setEnableCamera(String s) {
		Enable_Camera = s;
	}

	public String getEnableCamera() {
		return Enable_Camera;
	}

	public void setEnableGooglePlay(String s) {
		Enable_Google_play = s;
	}

	public String getEnableGooglePlay() {
		return Enable_Google_play;
	}

	public void setEnableUSB(String s) {
		Enable_USB = s;
	}

	public String getEnableUSB() {
		return Enable_USB;
	}

	public void setHiddenAPP(String s) {
		Hidden_APP = s;
	}

	public String getHiddenAPP() {
		return Hidden_APP;
	}

	@Override
	public String toString() {
		return "TimerJSONPolicy [IP=" + IP + ", COMMAND=" + COMMAND
				+ ", POLICY_ID=" + POLICY_ID + ", PRIORITY=" + PRIORITY
				+ ", Required_PW=" + Required_PW + ", PW_Format=" + PW_Format
				+ ", PW_Length=" + PW_Length + ", PW_Error_Count="
				+ PW_Error_Count + ", PW_Lock_Time=" + PW_Lock_Time
				+ ", PW_Reset_Period=" + PW_Reset_Period + ", Auto_Synch="
				+ Auto_Synch + ", ARoaming=" + ARoaming + ", Enable_Bluetooth="
				+ Enable_Bluetooth + ", Scan_Devices=" + Scan_Devices
				+ ", Enable_Wifi=" + Enable_Wifi + ", Enable_SSID="
				+ Enable_SSID + ", SSID=" + SSID + ", Hidden_SSID="
				+ Hidden_SSID + ", SSID_Security=" + SSID_Security
				+ ", SSID_Pre_sharedkey=" + SSID_Pre_sharedkey
				+ ", SSID_WEP_Key1=" + SSID_WEP_Key1 + ", Enable_Camera="
				+ Enable_Camera + ", Enable_Google_play=" + Enable_Google_play
				+ ", Enable_USB=" + Enable_USB + ", Hidden_APP=" + Hidden_APP
				+ ", UPDATE_DATE=" + UPDATE_DATE + ", SEND_TIME=" + SEND_TIME
				+ ", DAY_OF_WEEK=" + DAY_OF_WEEK + ", START_TIME=" + START_TIME
				+ ", END_TIME=" + END_TIME + "]";
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getCOMMAND() {
		return COMMAND;
	}

	public void setCOMMAND(String cOMMAND) {
		COMMAND = cOMMAND;
	}

	public String getSEND_TIME() {
		return SEND_TIME;
	}

	public void setSEND_TIME(String sEND_TIME) {
		SEND_TIME = sEND_TIME;
	}

	public String getDAY_OF_WEEK() {
		return DAY_OF_WEEK;
	}

	public void setDAY_OF_WEEK(String dAY_OF_WEEK) {
		DAY_OF_WEEK = dAY_OF_WEEK;
	}

	public String getSTART_TIME() {
		return START_TIME;
	}

	public void setSTART_TIME(String sTART_TIME) {
		START_TIME = sTART_TIME;
	}

	public String getEND_TIME() {
		return END_TIME;
	}

	public void setEND_TIME(String eND_TIME) {
		END_TIME = eND_TIME;
	}

}