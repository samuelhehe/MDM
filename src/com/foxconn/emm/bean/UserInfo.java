package com.foxconn.emm.bean;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import android.text.TextUtils;

import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.TextFormater;

/**
 * 
 * 头像处理方式:
 * 统一存放路径:  sdcard:/.mdm/.mdmimg/H2601XXX.png
 * 
 * 1.表中的headicon字段存储的是Server端同步过来的url  不再保存本地的imagefilepath
 * 2.从表中的headicon字段中提取出来的url , 根据url拼接host 进行组合出url ,进行server端获取image
 * 3.存储至指定的headicon filepath中
 * 4.注册时可进行code 判断,去Server端进行交互,下载原来的头像img.判断是否需要进行头像采集
 * 5.采集或下载的头像文件可覆盖原文件路径.
 * 
 * @author samuel
 * @date 2013/11/19
 * @version 1.0.1
 */
public class UserInfo implements Serializable {

	/**
	 * { "userId": "ceshi001", "userNameChi": "张三  ", "userNameEng": "ceshi001",
	 * "buGroup": "SHZBG", "userImg": "xx.png", "userPw":
	 * "202cb962ac59075b964b07152d234b70", "userMail": "ceshi001@qq.com",
	 * "telExtension": "25632", "cellPhone": "15238005984", "isDisable": "1",
	 * "createBy": "admin", "createDate": "2014-09-04 14:45:34", "modifyBy":
	 * "admin", "modifyDate": "2014-09-04 14:45:34" }
	 */
	private static final long serialVersionUID = -6671941193514959422L;

	@Override
	public String toString() {
		return "UserInfo [userId=" + userId + ", user_name_eng="
				+ userNameEng + ", buGroup=" + buGroup + ", user_name_chi="
				+ userNameChi + ", userImg=" + userImg + ", cellPhone="
				+ cellPhone + ", telExtension=" + telExtension + ", userMail="
				+ userMail + ", createBy=" + createBy + ", createDate="
				+ createDate + ", modifyBy=" + modifyBy + ", modifyDate="
				+ modifyDate + ", isDisable=" + isDisable + "]";
	}

	private String userId;

	private String userNameEng;

	private String buGroup;

	private String userNameChi;

	private String userImg;

	private String cellPhone;

	private String telExtension;

	private String userMail;

	private String createBy;

	private String createDate;

	private String modifyBy;

	private String modifyDate;
	
	private boolean isDisable;

	public static final class TAG {

		public static final String TB_NAME = "userinfo";

		public static final String _ID = "_id";

		public static final String USER_ID = "user_id";

		public static final String USER_NAME_ENG = "userNameEng";

		public static final String USER_NAME_CHI = "user_name";

		public static final String BU_GROUP = "bu_group";

		public static final String HEAD_ICON = "head_icon";

		public static final String PHONE_NO = "phone_no";

		public static final String TELEXTENSION = "telExtension";

		public static final String USERMAIL = "userMail";
		public static final String CREATEBY = "createBy";
		public static final String CREATEDATE = "createDate";
		public static final String MODIFYBY = "modifyBy";
		public static final String MODIFYDATE = "modifyDate";
		public static final String ISDISABLE = "isDisable";

	}

	
	
	/**
	 *  获取头像存储的路径
	 * @param userId
	 * @return  sdcard/.mdm/.mdmimg/H26019XX.png
	 */
	public static String getHeadIconFilePath(String userId) throws IOException {
		String hdpath = "";
		hdpath = EMMContants.LocalConf.getEMMLocalHost_dirPath()+ EMMContants.LocalConf.HeadIcon_dirpath;
		if (!new File(hdpath).exists()) {
			new File(hdpath).mkdirs();
		}
		return hdpath + getHeadIconName(userId);
	}

	
	/**
	 * 获取头像名称.
	 * @param userId
	 * @return
	 */
	public static String getHeadIconName(String userId) {
		return userId + ".png";
	}

	

	/**
	 * 判断头像文件是否存在.
	 *  1. 判断用户头像的路径中文件名是否含有 userId 2. 判断指定用户是否有HeadIcon file  3. 判断该文件是否为空
	 * @param headIconPath
	 * @param userId
	 * @return  
	 */
	public static boolean hasHeadIcon(String headIconPath, String userId) {
		if (TextUtils.isEmpty(headIconPath) || TextUtils.isEmpty(userId)) {
			return false;
		}
		if (headIconPath.contains(userId)) {
			File headIcon = new File(headIconPath);
			if (headIcon.exists()) {
				return headIcon.isFile() && headIcon.length() > 0;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}


	public UserInfo() {
	}

	public String getUser_id() {
		return userId;
	}

	public void setUser_id(String user_id) {
		this.userId = user_id;
	}

	public String getBu_group() {
		return buGroup;
	}

	public void setBu_group(String bu_group) {
		this.buGroup = bu_group;
	}

	public String getPhone_no() {
		return cellPhone;
	}

	public void setPhone_no(String phone_no) {
		this.cellPhone = phone_no;
	}

	/**
	 * @return the telExtension
	 */
	public String getTelExtension() {
		return telExtension;
	}

	/**
	 * @param telExtension
	 *            the telExtension to set
	 */
	public void setTelExtension(String telExtension) {
		this.telExtension = telExtension;
	}

	/**
	 * @return the user_name_chi
	 */
	public String getUser_name_chi() {
		return userNameChi;
	}

	/**
	 * @param user_name_chi
	 *            the user_name_chi to set
	 */
	public void setUser_name_chi(String user_name_chi) {
		this.userNameChi = user_name_chi;
	}

	/**
	 * @return the userMail
	 */
	public String getUserMail() {
		return userMail;
	}

	/**
	 * @param userMail
	 *            the userMail to set
	 */
	public void setUserMail(String userMail) {
		this.userMail = userMail;
	}

	/**
	 * @return the userImg
	 */
	public String getUserImg() {
		return userImg;
	}

	/**
	 * @param userImg
	 *            the userImg to set
	 */
	public void setUserImg(String userImg) {
		this.userImg = userImg;
	}

	/**
	 * @return the user_name_eng
	 */
	public String getUser_name_eng() {
		return userNameEng;
	}

	/**
	 * @param user_name_eng
	 *            the user_name_eng to set
	 */
	public void setUser_name_eng(String user_name_eng) {
		this.userNameEng = user_name_eng;
	}

	/**
	 * @return the createBy
	 */
	public String getCreateBy() {
		return createBy;
	}

	/**
	 * @param createBy
	 *            the createBy to set
	 */
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	/**
	 * @return the createDate
	 */
	public String getCreateDate() {
		return createDate;
	}

	/**
	 * @param createDate
	 *            the createDate to set
	 */
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	/**
	 * @return the modifyBy
	 */
	public String getModifyBy() {
		return modifyBy;
	}

	/**
	 * @param modifyBy
	 *            the modifyBy to set
	 */
	public void setModifyBy(String modifyBy) {
		this.modifyBy = modifyBy;
	}

	/**
	 * @return the modifyDate
	 */
	public String getModifyDate() {
		return modifyDate;
	}

	/**
	 * @param modifyDate
	 *            the modifyDate to set
	 */
	public void setModifyDate(String modifyDate) {
		this.modifyDate = modifyDate;
	}

	/**
	 * @return the isDisable
	 */
	public boolean isDisable() {
		return isDisable;
	}

	/**
	 * @param isDisable the isDisable to set
	 */
	public void setDisable(boolean isDisable) {
		this.isDisable = isDisable;
	}
}
