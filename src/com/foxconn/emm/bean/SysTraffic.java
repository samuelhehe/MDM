package com.foxconn.emm.bean;

public class SysTraffic {

	public static class TAG {

		public static final String tag_TD = "TD";
		public static final String tag_TW = "TW";
		public static final String tag_UTD = "UTD";
		public static final String tag_TTIME = "TTIME";
		public static final String tag_TDAY = "TDAY";
		public static final String tag_TMONTH = "TMONTH";
		public static final String tag_TYEAR = "TYEAR";

	}
	
	/**
	 * 当天的总数据流量
	 */
	private long td;

	/**
	 * 当天的总Wlan流量
	 */
	private long tw;

	/**
	 * 已经使用的总的数据流量
	 */
	private long utd;

	/**
	 * 本次统计的时间
	 */
	private long tTime;

	/**
	 * 当天日期
	 */
	private int tday;

	/**
	 * 当天月份
	 */
	private int tmonth;
	
	
	/**
	 * 当天所属的年份
	 */
	private int tyear ; 
	
	

	public String getTd() {
		return String.valueOf(td);
	}

	public void setTd(long td) {
		this.td = td;
	}


	@Override
	public String toString() {
		return "SysTraffic [td=" + td + ", tw=" + tw + ", utd=" + utd
				+ ", tTime=" + tTime + ", tday=" + tday + ", tmonth=" + tmonth
				+ ", tyear=" + tyear + "]";
	}

	public String getTw() {
		return String.valueOf(tw);
	}

	public void setTw(long tw) {
		this.tw = tw;
	}

	public String getUtd() {
		return String.valueOf(utd);
	}

	public void setUtd(long utd) {
		this.utd = utd;
	}

	public String gettTime() {
		return String.valueOf(td);
	}

	public void settTime(long tTime) {
		this.tTime = tTime;
	}

	public int getTday() {
		return tday;
	}

	public void setTday(int tday) {
		this.tday = tday;
	}

	public int getTmonth() {
		return tmonth;
	}

	public void setTmonth(int tmonth) {
		this.tmonth = tmonth;
	}

	public int getTyear() {
		return tyear;
	}

	public void setTyear(int tyear) {
		this.tyear = tyear;
	}

}
