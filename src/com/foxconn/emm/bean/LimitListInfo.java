package com.foxconn.emm.bean;

import java.io.Serializable;

public class LimitListInfo implements  Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * {"sendTime":"20140925101510","msgType":"sendLimitList/removeLimitList",
	 * "limitListApp":"", "limitType"
	 * :"2","limitLimitType":"0","limitPwdTime":"2 ","limitEnableTime"
	 * :"2014-09-17 15:49:42"
	 * ,"limitDisableTime":"2014-09-17 15:49:46","desc":"%E6%88%91"}
	 */

	private String sendTime;
	private String msgType;
	private String packName;
	private String limitType;
	private String limitLimitType;
	private String limitPwdTime;
	private String limitEnableTime;
	private String limitDisableTime;
	private String desc;

	public static final class TAG {
		public static final String SENDTIME = "sendTime";
		public static final String MSGTYPE = "msgType";
		public static final String LIMITLISTAPP = "limitListApp";
		public static final String LIMITTYPE = "limitType";
		public static final String LIMITLIMITTYPE = "limitLimitType";
		public static final String LIMITPWDTIME = "limitPwdTime";
		public static final String LIMITENABLETIME = "limitEnableTime";
		public static final String LIMITDISABLETIME = "limitDisableTime";
		public static final String DESC = "desc";
		public static final String PACKNAME = "packname";
		public static final String ENABLE_TIME = "enableTime";
		public static final String DISABLE_TIME = "disableTime";
		
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getPackName() {
		return packName;
	}

	public void setPackName(String packName) {
		this.packName = packName;
	}

	public String getLimitType() {
		return limitType;
	}

	public void setLimitType(String limitType) {
		this.limitType = limitType;
	}

	public String getLimitLimitType() {
		return limitLimitType;
	}

	public void setLimitLimitType(String limitLimitType) {
		this.limitLimitType = limitLimitType;
	}

	public String getLimitPwdTime() {
		return limitPwdTime;
	}

	public void setLimitPwdTime(String limitPwdTime) {
		this.limitPwdTime = limitPwdTime;
	}

	public String getLimitEnableTime() {
		return limitEnableTime;
	}

	public void setLimitEnableTime(String limitEnableTime) {
		this.limitEnableTime = limitEnableTime;
	}

	public String getLimitDisableTime() {
		return limitDisableTime;
	}

	public void setLimitDisableTime(String limitDisableTime) {
		this.limitDisableTime = limitDisableTime;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		return "LimitListInfo [sendTime=" + sendTime + ", msgType=" + msgType
				+ ", packName=" + packName + ", limitType=" + limitType
				+ ", limitLimitType=" + limitLimitType + ", limitPwdTime="
				+ limitPwdTime + ", limitEnableTime=" + limitEnableTime
				+ ", limitDisableTime=" + limitDisableTime + ", desc=" + desc
				+ "]";
	}

}
