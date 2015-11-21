package com.foxconn.emm.bean;

public class NotificationInfo {
	/**
	 * {"content":"afseg","sendTime":"20140916115425","msgType":
	 * "sendNotification"}
	 */
	private String id;
	private String content;
	private String sendTime;
	private String msgType;

	public static class TAG {
		public static final String ID = "id";
		public static final String CONTENT = "content";
		public static final String SENDTIME = "sendTime";
		public static final String MSGTYPE = "msgType";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	@Override
	public String toString() {
		return "NotificationInfo [id=" + id + ", content=" + content
				+ ", sendTime=" + sendTime + ", msgType=" + msgType + "]";
	}

}
