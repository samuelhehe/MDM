package com.foxconn.emm.bean;

import java.io.File;
import java.io.IOException;

import com.foxconn.emm.utils.EMMContants;

public class FileInfo {
	/**
	 * {"content":"satf w","subject":"dsgsadhrt ","sendTime":"20140916115823",
	 * "msgType":"sendFile","password":"","contact":"","deadline":"","fileUrl":
	 * "http://10.206.20.158/sendFile/20140916115823.pdf"}
	 */

	private String id;
	private String content;
	private String subject;
	private String sendTime;
	private String msgType;
	private String password;
	private String contact;
	private String deadline;
	private String fileUrl;

	public static final class TAG {
		public static final String ID = "id";
		public static final String CONTENT = "content";
		public static final String SUBJECT = "subject";
		public static final String SENDTIME = "sendTime";
		public static final String MSGTYPE = "msgType";
		public static final String PASSWORD = "password";
		public static final String CONTACT = "contact";
		public static final String DEADLINE = "deadline";
		public static final String FILEURL = "fileUrl";
		public static final String URL = "url";
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

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	@Override
	public String toString() {
		return "FileInfo [id=" + id + ", content=" + content + ", subject="
				+ subject + ", sendTime=" + sendTime + ", msgType=" + msgType
				+ ", password=" + password + ", contact=" + contact
				+ ", deadline=" + deadline + ", fileUrl=" + fileUrl + "]";
	}

	public String getFilePath() {
		String filePath = "";
		filePath = EMMContants.LocalConf.getEMMLocalHost_dirPath()
				+ EMMContants.LocalConf.download_dirpath;
		if (!new File(filePath).exists()) {
			new File(filePath).mkdirs();
		}

		return filePath;
	}

	public String getFileName(String subject, String sendTime) {
		String fileName = "";
		if (!subject.equals("") || !subject.equals("null")) {
			fileName = subject + "(" + sendTime + ")" + ".pdf";
		}
		return fileName;
	}

}
