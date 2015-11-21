package com.foxconn.emm.utils;

public class Constant {

	public static final boolean DEBUG = true;

	// 连接超时
	public static final int timeOut = 12000;
	// 建立连接
	public static final int connectOut = 12000;
	// 获取数据
	public static final int getOut = 60000;

	/**
	 * 1表示已下载完成
	 */
	public static final int downloadComplete = 1;
	/**
	 * 0表示未开始下载
	 */
	public static final int undownLoad = 0;
	/**
	 * 2表示已开始下载
	 */
	public static final int downInProgress = 2;
	/**
	 * 3表示下载暂停
	 */
	public static final int downLoadPause = 3;

}
