package com.foxconn.emm.bean;

public class MsgCommonInfo {
	/**
	 * {"sendTime":"20140924165159","msgType":"reset"}
	 * {"lockPwd":"asd123","sendTime":"20140924164444","msgType":"lockDev"}
	 * "msgType":"lockDev","reset","unLock","updateLocation","removeControl","syncInfo"
	 */
	private String msgType;
	private String sendTime;
	private String lockPwd;

	public static final class TAG {
		public static final String MSGTYPE = "msgType";
		public static final String SENDTIME = "sendTime";
		public static final String LOCKPWD = "lockPwd";
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getLockPwd() {
		return lockPwd;
	}

	public void setLockPwd(String lockPwd) {
		this.lockPwd = lockPwd;
	}

	@Override
	public String toString() {
		return "MsgCommonInfo [msgType=" + msgType + ", sendTime=" + sendTime
				+ ", lockPwd=" + lockPwd + "]";
	}

}
