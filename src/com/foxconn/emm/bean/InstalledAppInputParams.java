package com.foxconn.emm.bean;


public class InstalledAppInputParams {

	/**
	 * packagename String App的包名 是 versioncode int App的版本编号 是
	 * 
	 * 
	 */

	private String packageName;

	private int versionCode;
	
	private String appName ; 
	
	private String versionName;
	
	private String isRunning ;
	
	
	
	public static final class TAG{
		
		public static final String  software_name="SOFTWARE_NAME";
		public static final String  software_version="SOFTWARE_VERSION";
		public static final String  software_size="SOFTWARE_SIZE";
		public static final String  software_running="SOFTWARE_RUNNING";
		public static final String  packagename="PACKAGENAME";
		
	}
	
	@Override
	public String toString() {
		return "InstalledAppInputParams [packageName=" + packageName
				+ ", versionCode=" + versionCode + "]";
	}

	public InstalledAppInputParams(String packageName, int versionCode) {
		super();
		this.packageName = packageName;
		this.versionCode = versionCode;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getIsRunning() {
		return isRunning;
	}

	public void setIsRunning(String isRunning) {
		this.isRunning = isRunning;
	}
	

}
