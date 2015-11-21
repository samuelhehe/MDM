package com.foxconn.emm.bean;

import java.io.File;

import com.foxconn.emm.utils.EMMContants;

public class PicInfo {
	/**
	 * {"content":"afraew","img_list":
	 * "http://10.206.20.158/sendImg/20140924153630.jpg,http://10.206.20.158/sendImg/20140924153632.jpg,"
	 * ,"subject":"asfaas","sendTime":"20140924153645","msgType":"sendImage",
	 * "password":"","contact":"","deadline":""}
	 */

	private String id;
	private String content;
	private String img_list;
	private String subject;
	private String sendTime;
	private String msgType;
	private String password;
	private String contact;
	private String deadline; // 截止时间 （可空 不空即为当前时间前可查看）

	public class TAG {
		public static final String ID = "id";
		public static final String CONTENT = "content";
		public static final String IMG_LIST = "img_list";
		public static final String SUBJECT = "subject";
		public static final String SENDTIME = "sendTime";
		public static final String MSGTYPE = "msgType";
		public static final String PASSWORD = "password";
		public static final String CONTACT = "contact";
		public static final String DEADLINE = "deadline";
		public static final String URL = "url"; // DB中替换img_list,File替换fileUrl
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

	public String getImg_list() {
		return img_list;
	}

	public void setImg_list(String img_list) {
		this.img_list = img_list;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public String getPicFilePath() {
		String picPath = "";
		picPath = EMMContants.LocalConf.getEMMLocalHost_dirPath()
				+ EMMContants.LocalConf.download_dirpath;
		if (!new File(picPath).exists()) {
			new File(picPath).mkdirs();
		}
		return picPath;
	}

	/**
	 * "/.emm/.downloads/"文件名201410301042"/文件名(201410301042)1.png"
	 * 
	 * @param subject
	 * @param i
	 * @param suffix
	 * @param sendTime
	 * @return
	 */
	public String getPicPath(String subject, int i, String suffix,
			String sendTime) {
		String picPath = null;
		picPath = getPicFilePath() + getFileName(subject, sendTime) + "/"
				+ subject + "(" + sendTime + ")" + i + suffix;

		if (!new File(getPicFilePath() + subject + sendTime).exists()) {
			new File(getPicFilePath() + subject + sendTime).mkdir();
		}
		return picPath;
	}

	/**
	 * "文件名201410301042"
	 * 
	 * @param subject
	 * @param sendTime
	 * @return
	 */
	public String getFileName(String subject, String sendTime) {
		String filePath = null;
		filePath = subject + sendTime;
		return filePath;
	}

	public String getPicName(String subject, int i, String sendTime) {
		String picName = null;
		picName = subject + "(" + sendTime + ")" + i;
		return picName;
	}

	@Override
	public String toString() {
		return "PicInfo [id=" + id + ", content=" + content + ", img_list="
				+ img_list + ", subject=" + subject + ", sendTime=" + sendTime
				+ ", msgType=" + msgType + ", password=" + password
				+ ", contact=" + contact + ", deadline=" + deadline + "]";
	}

}
