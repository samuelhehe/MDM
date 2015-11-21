package com.foxconn.emm.bean;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
//import com.foxconn.emm.utils.ImageUtils;

public class MsgInfo implements Serializable {

	/**
	 * id INTEGER PRIMARY KEY autoincrement, command TEXT, //// command
	 * fname_title TEXT, ///// 文件名或者日历主题 code_place TEXT, /////
	 * pdf解析后的图片名称列表(0.png,1.png,2.png,3.png...etc) , 日历内容的地点 receive_date TEXT,
	 * ///// 接收时间 msg_content TEXT, ///// Notification的信息内容 或者 日历内容 或者文件内容
	 * isallday TEXT, ///// 日历的内容是否是一整天 date_start TEXT, ///// 日历开始时间 date_end
	 * TEXT, ///// 日历结束时间 folder_name TEXT, ///// 文件夹名称 despw TEXT, ///// 文件解密密码
	 * descontact TEXT //// 文件解密电话号码
	 */
	private static final long serialVersionUID = 3457506791287451948L;

	private String id;

	private String command;

	private String fname_title;

	private String code_place;

	private String receive_date;

	private String msg_content;

	private String isallday;

	private String date_start;

	private String date_end;

	private String folder_name;

	private String despw;

	private String descontact;

	public static final String TB_NAME = "msginfo";

	public static final String ID = "id";

	public static final String COMMAND = "command";

	public static final String FNAME_TITLE = "fname_title";

	public static final String CODE_PLACE = "code_place";

	public static final String RECEIVE_DATE = "receive_date";

	public static final String MSG_CONTENT = "msg_content";

	public static final String ISALLDAY = "isallday";

	public static final String DATE_START = "date_start";

	public static final String DATE_END = "date_end";

	public static final String FOLDER_NAME = "folder_name";

	public static final String DESPW = "despw";

	public static final String DESCONTACT = "descontact";

	public static final String FILE_PATH = "/.mdmfile/";

	public static final String DECRYPT_PATH = "/.mdmfile/decrypt/";

	@Override
	public String toString() {
		return "MsgInfo [id=" + id + ", command=" + command + ", fname_title="
				+ fname_title + ", code_place=" + code_place
				+ ", receive_date=" + receive_date + ", msg_content="
				+ msg_content + ", isallday=" + isallday + ", date_start="
				+ date_start + ", date_end=" + date_end + ", folder_name="
				+ folder_name + ", despw=" + despw + "]";
	}

	public static String getFILE_Path() {
		String filePath = "";
		try {
			filePath = android.os.Environment.getExternalStorageDirectory()
					.getCanonicalPath() + FILE_PATH;
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!new File(filePath).exists()) {
			new File(filePath).mkdirs();
		}

		return filePath;
	}

	public static String getDECRYPT_Path() {
		String decyptPath = "";
		try {
			decyptPath = android.os.Environment.getExternalStorageDirectory()
					.getCanonicalPath() + DECRYPT_PATH;
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!new File(decyptPath).exists()) {
			new File(decyptPath).mkdirs();
		}

		return decyptPath;
	}

	public Drawable getFileImg(String filePath) {
		if (filePath == null || filePath.length() == 0) {
			return null;
		}
		Bitmap img = BitmapFactory.decodeFile(filePath);
		
//		Bitmap img = ImageUtils.decodeSampledBitmapFromFilePath(filePath, 500, 400);
		
		Drawable fileImg = new BitmapDrawable(img);
		return fileImg;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMsg_content() {
		return msg_content;
	}

	public void setMsg_content(String msg_content) {
		this.msg_content = msg_content;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getFname_title() {
		return fname_title;
	}

	public void setFname_title(String fname_title) {
		this.fname_title = fname_title;
	}

	public String getCode_place() {
		return code_place;
	}

	public void setCode_place(String code_place) {
		this.code_place = code_place;
	}

	public String getReceive_date() {
		return receive_date;
	}

	public void setReceive_date(String receive_date) {
		this.receive_date = receive_date;
	}

	public String getIsallday() {
		return isallday;
	}

	public void setIsallday(String isallday) {
		this.isallday = isallday;
	}

	public String getDate_start() {
		return date_start;
	}

	public void setDate_start(String date_start) {
		this.date_start = date_start;
	}

	public String getDate_end() {
		return date_end;
	}

	public void setDate_end(String date_end) {
		this.date_end = date_end;
	}

	public String getFolder_name() {
		return folder_name;
	}

	public void setFolder_name(String folder_name) {
		this.folder_name = folder_name;
	}

	public String getDespw() {
		return despw;
	}

	public void setDespw(String despw) {
		this.despw = despw;
	}

	public String getDescontact() {
		return descontact;
	}

	public void setDescontact(String descontact) {
		this.descontact = descontact;
	}
}
