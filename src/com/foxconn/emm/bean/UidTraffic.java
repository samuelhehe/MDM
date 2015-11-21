package com.foxconn.emm.bean;


/**
 *  UID TEXT,
 *  PACKAGE_NAME TEXT, 
 *  RECEIVED_TOTAL INTEGER, 
 *  UPLOAD_TOTAL INTEGER,   
 *  TTIME  TEXT, 
 *  THOUR INTEGER, 
 *  TDAY  INTEGER, 
 *  TMONTH INTEGER , 
 *  TYEAR INTEGER 
 * 
 */
public class UidTraffic {

	
	public static final class UidTrafficTag{
		
		public static final String tag_RECEIVED_TOTAL = "RECEIVED_TOTAL";
		public static final String tag_UPLOAD_TOTAL = "UPLOAD_TOTAL";
		public static final String tag_UID = "UID";
		public static final String tag_PACKAGE_NAME = "PACKAGE_NAME";
		public static final String tag_TDAY = "TDAY";
		public static final String tag_THOUR= "THOUR";
		public static final String tag_TMONTH = "TMONTH";
		public static final String tag_TYEAR = "TYEAR";
		public static final String tag_APPNAME = "APPNAME";
		
	}
	
	

	/**
	 * App在系统中的UID
	 */
	private int uid;

	
	public UidTraffic(){}
	


	@Override
	public String toString() {
		return "UidTraffic [uid=" + uid + ", packageName=" + packageName
				+ ", appName=" + appName + ", received_total=" + received_total
				+ ", uploaded_total=" + uploaded_total + ", TTIME=" + TTIME
				+ ", THOUR=" + THOUR + ", TDAY=" + TDAY + ", TMONTH=" + TMONTH
				+ ", TYEAR=" + TYEAR + "]";
	}


	public UidTraffic(int uid, String packageName, int received_total,
			int uploaded_total, long tTIME, int tHOUR, int tDAY, int tMONTH,
			int tYEAR ,String appName) {
		this.uid = uid;
		this.packageName = packageName;
		this.received_total = received_total;
		this.uploaded_total = uploaded_total;
		this.appName = appName ; 
		TTIME = tTIME;
		THOUR = tHOUR;
		TDAY = tDAY;
		TMONTH = tMONTH;
		TYEAR = tYEAR;
	}


	/**
	 * App 的packagename
	 */
	private String packageName ;

	
	private String appName; 

	/**
	 * App 接收到的流量大小
	 */
	private long received_total ; 
	
	
	/**
	 * App 上传的流量大小
	 */
	private long uploaded_total ; 
	
	
	/**
	 * 本次记录的时间
	 */
	private long TTIME; 
	
	
	/**
	 * 本次记录的小时  0-23
	 */
	private int THOUR; 
	
	
	/**
	 * 本次记录的天数 2014/09/08 值为8 
	 */
	private int TDAY ; 
	
	
	
	/**
	 * 本次记录的月份 
	 */
	private int TMONTH; 
	
	
	/**
	 * 本次记录的年份
	 */
	private int TYEAR; 

	
	
	public String getPackageName() {
		return packageName;
	}


	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}


	public long getReceived_total() {
		return received_total;
	}


	public void setReceived_total(long received) {
		this.received_total = received;
	}


	public long getUploaded_total() {
		return uploaded_total;
	}


	public void setUploaded_total(long uploaded_total) {
		this.uploaded_total = uploaded_total;
	}


	public long getTTIME() {
		return TTIME;
	}


	public void setTTIME(long tTIME) {
		TTIME = tTIME;
	}


	public int getTHOUR() {
		return THOUR;
	}


	public void setTHOUR(int tHOUR) {
		THOUR = tHOUR;
	}


	public int getTDAY() {
		return TDAY;
	}


	public void setTDAY(int tDAY) {
		TDAY = tDAY;
	}


	public int getTMONTH() {
		return TMONTH;
	}


	public void setTMONTH(int tMONTH) {
		TMONTH = tMONTH;
	}


	public int getTYEAR() {
		return TYEAR;
	}


	public void setTYEAR(int tYEAR) {
		TYEAR = tYEAR;
	}


	public int getUid() {
		return uid;
	}


	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

}
