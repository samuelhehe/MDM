package com.foxconn.emm.bean;

public class AppInfo {
	/**
	 * {"msgType":"sendapps","sendTime":"20141008113532","appList":[{"appName":
	 * "QQ音乐"
	 * ,"fileUrl":"http://10.206.20.158/sendApp/20141008113517-68430634.apk"
	 * ,"packageName"
	 * :"com.tencent.qqmusic","fileSize":"7054"},{"appName":"ZAKER"
	 * ,"fileUrl":"http://10.206.20.158/sendApp/20141008113524-1650437311.apk"
	 * ,"packageName"
	 * :"com.myzaker.ZAKER_Phone","fileSize":"6271"}],"groupIds":"grp002,"
	 * ,"deviceIds":""}
	 */

	private String id;
	private String msgType;
	private String sendTime;
	private String appList;
	private String appName;
	private String fileUrl;
	private String packageName;
	private String fileSize;

	public class TAG {
		public static final String ID = "id";
		public static final String MSGTYPE = "msgType";
		public static final String SENDTIME = "sendTime";
		public static final String APPLIST = "appList";
		public static final String APPNAME = "appName";
		public static final String FILEURL = "fileUrl";
		public static final String PACKAGENAME = "packageName";
		public static final String FILESIZE = "fileSize";
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

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getAppList() {
		return appList;
	}

	public void setAppList(String appList) {
		this.appList = appList;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	@Override
	public String toString() {
		return "AppInfo [id=" + id + ", msgType=" + msgType + ", sendTime="
				+ sendTime + ", appList=" + appList + ", appName=" + appName
				+ ", fileUrl=" + fileUrl + ", packageName=" + packageName
				+ ", fileSize=" + fileSize + "]";
	}
}
