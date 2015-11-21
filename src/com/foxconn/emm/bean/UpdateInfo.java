package com.foxconn.emm.bean;

import java.io.Serializable;

import com.foxconn.emm.utils.VersionUtil;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class UpdateInfo implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7597659843573100942L;

	private int versionCode; // 版本

	private String versionName;

	// 新版本存放 url 路径
	private String url;
	// 更新说明,如新增什么功能特性等
	private String description;

	// 是否必须更新
	private String required;

	private int localVersionCode;

	private String localVersionName;

	public UpdateInfo(Context context) {
		try {
			this.localVersionCode = VersionUtil.getVersionCode(context);
			this.localVersionName = VersionUtil.getVersionName(context);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public UpdateInfo() {
	}

	public static class TAG {

		public static final String tag_VERSIONCODE = "version";
		public static final String tag_VERSIONNAME = "versionname";
		public static final String tag_URL = "url";
		public static final String tag_DESCRIPTION = "description";
		public static final String tag_UP2D = "up2d";
		public static final String tag_REQUIRED = "required";

		public static final String tag_req_Y = "Y";
		public static final String tag_req_N = "N";
		public static final String UPDATEINFO = "UPDATEINFO";

	}

	public boolean isNeedUpdate(Context context) {
		
		try {
			this.localVersionCode = VersionUtil.getVersionCode(context);
			this.localVersionName = VersionUtil.getVersionName(context);
			if (localVersionCode >= 1 && versionCode >= 1) {
				if (localVersionCode < versionCode) {
					return true;
				} else if (localVersionCode >= versionCode) {
					return false;
				}
			} else {
				return false;
			}

		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getVersion() {
		return versionCode;
	}

	public void setVersion(int version) {
		this.versionCode = version;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	@Override
	public String toString() {
		return "UpdateInfo [versionCode=" + versionCode + ", versionName="
				+ versionName + ", url=" + url + ", description=" + description
				+ ", required=" + required + ", localVersionCode="
				+ localVersionCode + ", localVersionName=" + localVersionName
				+ "]";
	}

	public String getRequired() {
		return required;
	}

	public void setRequired(String required) {
		this.required = required;
	}

	public int getLocalVersionCode() {
		return localVersionCode;
	}

	public void setLocalVersionCode(int localVersionCode) {
		this.localVersionCode = localVersionCode;
	}

	public String getLocalVersionName() {
		return localVersionName;
	}

	public void setLocalVersionName(String localVersionName) {
		this.localVersionName = localVersionName;
	}
}
