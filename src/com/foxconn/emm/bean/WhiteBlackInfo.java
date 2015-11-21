package com.foxconn.emm.bean;

public class WhiteBlackInfo {
	/**
	 * {"desc":"%E5%95%8A","sendTime":"20140925100801","msgType":
	 * "removeWhiteList/sendWhiteList"
	 * ,"enableTime":"2014-09-23 10:33:00","whiteListApp"
	 * :"ceshi,"("blackListApp":"") ,"disableTime":"2014-09-23 10:33:01"}
	 */
	private String desc;
	private String sendTime;
	private String msgType;
	private String enableTime;
	private String whiteListApp;
	private String disableTime;
	private String blackListApp;

	public static final class TAG {
		public static final String DESC = "desc";
		public static final String SENDTIME = "sendTime";
		public static final String MSGTYPE = "msgType";
		public static final String ENABLETIME = "enableTime";
		public static final String WHITELISTAPP = "whiteListApp";
		public static final String DISABLETIME = "disableTime";
		public static final String BLACKLISTAPP = "blackListApp";
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
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

	public String getEnableTime() {
		return enableTime;
	}

	public void setEnableTime(String enableTime) {
		this.enableTime = enableTime;
	}

	public String getWhiteListApp() {
		return whiteListApp;
	}

	public void setWhiteListApp(String whiteListApp) {
		this.whiteListApp = whiteListApp;
	}

	public String getDisableTime() {
		return disableTime;
	}

	public void setDisableTime(String disableTime) {
		this.disableTime = disableTime;
	}

	public String getBlackListApp() {
		return blackListApp;
	}

	public void setBlackListApp(String blackListApp) {
		this.blackListApp = blackListApp;
	}

	@Override
	public String toString() {
		return "WhiteBlackInfo [desc=" + desc + ", sendTime=" + sendTime
				+ ", msgType=" + msgType + ", enableTime=" + enableTime
				+ ", whiteListApp=" + whiteListApp + ", disableTime="
				+ disableTime + ", blackListApp=" + blackListApp + "]";
	}

}
