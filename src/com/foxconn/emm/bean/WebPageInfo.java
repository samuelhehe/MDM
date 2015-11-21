package com.foxconn.emm.bean;

public class WebPageInfo {
	/**
	 * {"content":"发送网页测试","pageUrl":"http://www.baidu.com","subject":"测试网页",
	 * "sendTime"
	 * :"20140924165935","msgType":"sendPage","deadline":"2014-09-26 16:59:30"}
	 * content 网页内容描述 pageUrl 网页路径 deadline 截止时间 （可空 不空即为当前时间前可查看）
	 */

	private String id;
	private String content;
	private String pageUrl;
	private String subject;
	private String sendTime;
	private String msgType;
	private String deadline;// 截止时间 （可空 不空即为当前时间前可查看）

	public static final class TAG {
		public static final String ID = "id";
		public static final String CONTENT = "content";
		public static final String PAGEURL = "pageUrl";
		public static final String SUBJECT = "subject";
		public static final String SENDTIME = "sendTime";
		public static final String MSGTYPE = "msgType";
		public static final String DEADLINE = "deadline";
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

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
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

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	@Override
	public String toString() {
		return "WebPageInfo [id=" + id + ", content=" + content + ", pageUrl="
				+ pageUrl + ", subject=" + subject + ", sendTime=" + sendTime
				+ ", msgType=" + msgType + ", deadline=" + deadline + "]";
	}

}
