package com.foxconn.emm.bean;

public class CalendarInfo {
	/**
	 * "startTime": "2014-09-24 16:18:00", "content": "测试日历的发送 哈哈哈", "isAllDay":
	 * "1", "subject": "测试日历", "sendTime": "20140924161819", "msgType":
	 * "sendCalendar", "endTime": "2014-09-26 16:18:03", "place": "D12 4 楼"
	 */
	private String id;
	private String msgType;
	private String isAllDay;
	private String content;
	private String subject;
	private String sendTime;
	private String startTime;
	private String endTime;
	private String place;

	public static class TAG {
		public static final String ID = "id";
		public static final String MSGTYPE = "msgType";
		public static final String ISALLDAY = "isAllDay";
		public static final String CONTENT = "content";
		public static final String SUBJECT = "subject";
		public static final String SENDTIME = "sendTime";
		public static final String STARTTIME = "startTime";
		public static final String ENDTIME = "endTime";
		public static final String PLACE = "place";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getIsAllDay() {
		return isAllDay;
	}

	public void setIsAllDay(String isAllDay) {
		this.isAllDay = isAllDay;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	@Override
	public String toString() {
		return "CalendarInfo [id=" + id + ", msgType=" + msgType
				+ ", isAllDay=" + isAllDay + ", content=" + content
				+ ", subject=" + subject + ", sendTime=" + sendTime
				+ ", startTime=" + startTime + ", endTime=" + endTime
				+ ", place=" + place + "]";
	}

}
