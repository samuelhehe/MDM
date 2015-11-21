package com.foxconn.emm.db;

import com.foxconn.emm.bean.AppInfo;
import com.foxconn.emm.bean.CalendarInfo;
import com.foxconn.emm.bean.FileInfo;
import com.foxconn.emm.bean.LimitListInfo;
import com.foxconn.emm.bean.NotificationInfo;
import com.foxconn.emm.bean.WebPageInfo;

/**
 * 数据库的信息 常量字段
 */
public class DBInfo {

	public static class DB {

		public static final String DB_NAME = "emm.db";

		public static final int VERSION = 4;

	}

	public static class Table {

		/**
		 * userinfo表
		 */
		public static final String USER_INFO_TB_NAME = "userinfo";

		public static final String USER_INFO_CREATE = "CREATE TABLE IF NOT EXISTS  "
				+ USER_INFO_TB_NAME
				+ " ( user_id TEXT PRIMARY KEY, user_name TEXT, telExtension TEXT, userMail TEXT,createBy TEXT,  createDate TEXT, modifyBy TEXT,  modifyDate TEXT, isDisable TEXT,  userNameEng TEXT, bu_group TEXT, head_icon text,  phone_no text  )";

		public static final String USER_INFO_DROP = "DROP TABLE "
				+ USER_INFO_TB_NAME;
		/**
		 * ID 信息流水号 INTEGER SEND_TIME 发送时间 TEXT COMMAND 指令名称 TEXT TRAFFIC_LIMIT
		 * 流量限制类别 TEXT LIMIT_DESC 对该限制的描述说明 TEXT
		 */
		public static final String TRAFFIC_LIST_TB_NAME = "trafficlimit";
		public static final String TRAFFIC_LIST_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ TRAFFIC_LIST_TB_NAME
				+ "( id INTEGER PRIMARY KEY autoincrement, COMMAND  TEXT, SEND_TIME TEXT , TRAFFIC_LIMIT TEXT , LIMIT_DESC TEXT )";
		public static final String TRAFFIC_LIST_DROP = "DROP TABLE "
				+ TRAFFIC_LIST_TB_NAME;

		/**
		 * ID 信息流水号 SEND_TIME 发送时间 COMMAND 指令名称 PACKNAME APP包名 LIMIT_LIMIT_TYPE
		 * 限制名单的限制类别 LIMIT_PWD_TIME 限制名单的时间或密码 LIMIT_ENABLE_TIME 生效时间
		 * LIMIT_DISABLE_TIME 失效时间 DESC 内容说明
		 * 
		 * 
		 * limitList
		 * 
		 * 
		 * 
		 * CREATE TABLE IF NOT EXISTS limitList
		 * 
		 * ( id INTEGER PRIMARY KEY autoincrement, 
		 * 消息类别
		 * msgType TEXT, 
		 * 消息的发送时间
		 * sendTime TEXT, 
		 * packagename
		 * packname TEXT, 
		 * 限制名单类别: 0: 黑名单, 1:白名单 , 2:限制名单
		 * limitType TEXT, 
		 * 限制名单的限制类别:  1：按照時間限制， 0：按照密碼進行限制
		 * limitLimitType TEXT, 
		 * 限制名单限制的时间,或者密码的值.
		 * limitPwdTime TEXT, 
		 * 限制名单生效时间,可以为空
		 * limitEnableTime TEXT, 
		 * 限制名单的失效时间, 可以为空
		 * limitDisableTime TEXT, 
		 *  限制名单的描述
		 * desc TEXT )
		 * 
		 */

		public static final String LIMITLIST_TB_NAME = "limitList";
		public static final String LIMITLIST_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ LIMITLIST_TB_NAME
				+ "( id INTEGER PRIMARY KEY autoincrement, "
				+ LimitListInfo.TAG.MSGTYPE
				+ " TEXT, "
				+ LimitListInfo.TAG.SENDTIME
				+ " TEXT, "
				+ LimitListInfo.TAG.PACKNAME
				+ " TEXT  UNIQUE , "
				+ LimitListInfo.TAG.LIMITTYPE
				+ " TEXT, "
				+ LimitListInfo.TAG.LIMITLIMITTYPE
				+ " TEXT, "
				+ LimitListInfo.TAG.LIMITPWDTIME
				+ " TEXT, "
				+ LimitListInfo.TAG.LIMITENABLETIME
				+ " TEXT, "
				+ LimitListInfo.TAG.LIMITDISABLETIME
				+ " TEXT, "
				+ LimitListInfo.TAG.DESC 
				+ " TEXT )";
		public static final String LIMITLIST_DROP = "DROP TABLE "
				+ LIMITLIST_TB_NAME;

		/**
		 * WEB PAGE ...
		 * 
		 * ID 信息流水号 SEND_TIME 发送时间 COMMAND 指令名称 WEBURL 网页地址 SUBJECT 主题 DESC 内容说明
		 * DEADLINE 截止时间
		 * 
		 */
		public static final String WEBPAGE_TB_NAME = "webpage";
		public static final String WEBPAGE_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ WEBPAGE_TB_NAME
				+ "( id INTEGER PRIMARY KEY autoincrement, "
				+ WebPageInfo.TAG.MSGTYPE
				+ " TEXT, "
				+ WebPageInfo.TAG.SENDTIME
				+ " TEXT, "
				+ WebPageInfo.TAG.PAGEURL
				+ " TEXT, "
				+ WebPageInfo.TAG.SUBJECT
				+ " TEXT, "
				+ WebPageInfo.TAG.CONTENT
				+ " TEXT, "
				+ WebPageInfo.TAG.DEADLINE + " TEXT  )";
		public static final String WEBPAGE_DROP = "DROP TABLE "
				+ WEBPAGE_TB_NAME;

		/**
		 * notifi
		 * 
		 * ID 信息流水号 SEND_TIME 发送时间 MSGTYPE 指令名称 CONTENT 通知内容
		 * 
		 * 
		 */
		public static final String NOTIFI_TB_NAME = "notifi";
		public static final String NOTIFI_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ NOTIFI_TB_NAME
				+ "( id INTEGER PRIMARY KEY autoincrement, "
				+ NotificationInfo.TAG.MSGTYPE
				+ " TEXT, "
				+ NotificationInfo.TAG.SENDTIME
				+ " TEXT, "
				+ NotificationInfo.TAG.CONTENT + " TEXT )";
		public static final String NOTIFI_DROP = "DROP TABLE " + NOTIFI_TB_NAME;

		/**
		 * app ID 信息流水号
		 */
		public static final String APPS_TB_NAME = "apps";
		public static final String APPS_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ APPS_TB_NAME + "( id INTEGER PRIMARY KEY autoincrement, "
				+ AppInfo.TAG.APPNAME + " TEXT, " + AppInfo.TAG.FILEURL
				+ " TEXT, " + AppInfo.TAG.MSGTYPE + " TEXT, "
				+ AppInfo.TAG.PACKAGENAME + " TEXT, " + AppInfo.TAG.SENDTIME
				+ " TEXT, " + AppInfo.TAG.FILESIZE + " TEXT )";
		public static final String APPS_DROP = "DROP TABLE " + APPS_TB_NAME;

		/**
		 * calendar ID 信息流水号 SEND_TIME 发送时间 COMMAND 指令名称 TITLE 主题 CONTENT 内容
		 * ISALLDAY 是否全天 DATE_START 开始时间 DATE_END 结束时间 PLACE 地点
		 * 
		 * 
		 */
		public static final String CALENDAR_TB_NAME = "calendar";
		public static final String CALENDAR_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ CALENDAR_TB_NAME
				+ "( id INTEGER PRIMARY KEY autoincrement, "
				+ CalendarInfo.TAG.MSGTYPE
				+ "  TEXT, "
				+ CalendarInfo.TAG.SENDTIME
				+ " TEXT , "
				+ CalendarInfo.TAG.SUBJECT
				+ " TEXT ,"
				+ CalendarInfo.TAG.CONTENT
				+ " TEXT ,"
				+ CalendarInfo.TAG.ISALLDAY
				+ " TEXT , "
				+ CalendarInfo.TAG.STARTTIME
				+ " TEXT , "
				+ CalendarInfo.TAG.ENDTIME
				+ " TEXT , "
				+ CalendarInfo.TAG.PLACE + " TEXT )";
		public static final String CALENDAR_DROP = "DROP TABLE "
				+ CALENDAR_TB_NAME;

		/**
		 * fileurl_img
		 * 
		 * ID 信息流水号 COMMAND 指令名称 SEND_TIME 发送时间 SUBJECT 主题 DESC 内容 FILENAME 文件名称
		 * FILEURL 文件URL CONTACT 联系方式 KEY 文件解密密码
		 * 
		 * 
		 */

		public static final String FILEURL_IMG_TB_NAME = "fileurl_img";
		public static final String FILEURL_IMG_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ FILEURL_IMG_TB_NAME
				+ "( id INTEGER PRIMARY KEY autoincrement, "
				+ FileInfo.TAG.MSGTYPE
				+ " TEXT, "
				+ FileInfo.TAG.SENDTIME
				+ " TEXT, "
				+ FileInfo.TAG.CONTENT
				+ " TEXT,"
				+ FileInfo.TAG.SUBJECT
				+ " TEXT, "
				+ FileInfo.TAG.URL
				+ " TEXT, "
				+ FileInfo.TAG.CONTACT
				+ " TEXT, "
				+ FileInfo.TAG.DEADLINE
				+ " TEXT, "
				+ FileInfo.TAG.PASSWORD
				+ " TEXT )";

		public static final String FILEURL_IMG_DROP = "DROP TABLE "
				+ FILEURL_IMG_TB_NAME;

		public static final String EMM_TRAFFIC_INFO_TB_NAME = "EMM_SYS_TRAF";
		/**
		 * CREATE TABLE IF NOT EXISTS EMM_SYS_TRAF (_id INTEGER PRIMARY KEY
		 * autoincrement , TD TEXT, 当天的总数据流量 TW TEXT, 当天的Wlan总流量 UTD TEXT ,
		 * 已使用的数据流量 TTIME TEXT, 本次记录的时间 TDAY INTEGER, 日期 TMONTH INTEGER 月份 );
		 * TYEAR INTEGER 年份
		 */
		public static final String EMM_TRAFFIC_INFO_CREATE = "CREATE TABLE IF NOT EXISTS  "
				+ EMM_TRAFFIC_INFO_TB_NAME
				+ " (_id INTEGER PRIMARY KEY autoincrement ,  TD  TEXT  DEFAULT ('0') , TW TEXT DEFAULT ('0'),  UTD TEXT DEFAULT ('0'),  TTIME  TEXT,  TDAY  INTEGER, TMONTH INTEGER ,TYEAR INTEGER  )";
		public static final String EMM_TRAFFIC_INFO_DROP = "DROP TABLE "
				+ EMM_TRAFFIC_INFO_TB_NAME;

		public static final String EMM_TRAFFIC_UID_INFO_TB_NAME = "EMM_SYS_TRAF_UID";
		/**
		 * ID ID自增 UID App在系统中的UID RT 接收的流量包括数据流量和Wlan流量 receivedTotal UT
		 * 上行的流量包括数据流量和Wlan流量 upload total RD 下行数据流量 UD 上行数据流量 RW 下行Wlan流量 UW
		 * 上行Wlan流量 UTD 已使用的数据流量 TTIME 最后一次记录时间 THOUR 小时 TDAY 日期 TMONTH 月份
		 * 
		 * ============================ new format APPNAME PACKAGE_NAME 應用包名
		 * VARCHAR2(20) ReceivedTotal 接收到的流量大小 number(10) UploadTotal 上傳的流量大小
		 * number(10) TRAFFIC_TDAY 日期天數 number(10) TRAFFIC_TYEAR 日期年份 number(10)
		 * TRAFFIC_TMONTH 日期月份 number(10)
		 * 
		 */
		public static final String EMM_TRAFFIC_UID_INFO_CREATE = "CREATE TABLE IF NOT EXISTS  "
				+ EMM_TRAFFIC_UID_INFO_TB_NAME
				+ " (_id INTEGER PRIMARY KEY autoincrement ,  UID INTEGER, PACKAGE_NAME TEXT, APPNAME TEXT,  RECEIVED_TOTAL TEXT DEFAULT ('0') , UPLOAD_TOTAL TEXT  DEFAULT ('0') ,   TTIME  TEXT, THOUR INTEGER, TDAY  INTEGER, TMONTH INTEGER , TYEAR INTEGER  )";
		public static final String EMM_TRAFFIC_UID_INFO_DROP = "DROP TABLE "
				+ EMM_TRAFFIC_UID_INFO_TB_NAME;
		
		public static final String FOXCONN_LIB_DOWNLOAD_CREATE = "CREATE TABLE IF NOT EXISTS FILEDOWN (_ID INTEGER PRIMARY KEY AUTOINCREMENT,ICON TEXT,NAME TEXT,DESCRIPTION TEXT, PAKAGENAME TEXT ,DOWNURL TEXT,DOWNPATH TEXT,STATE INTEGER,DOWNLENGTH INTEGER,TOTALLENGTH INTEGER,DOWNSUFFIX TEXT)";
		
		public static final String FOXCONN_LIB_DOWNLOAD_DROP = "DROP TABLE IF EXISTS FILEDOWN";
		
		

	}

}
