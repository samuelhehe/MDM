package com.foxconn.emm.utils;

public class Traffic_Constants {

	
	/**
	 * The total traffic data  sharedPreference key 		
	 */
	public static final String traffic_sh_datatotal = "traffic_sh_total";
	
	/**
	 * The left traffic data  sharedPreference key 		
	 */
	public static final String traffic_sh_dataleft = "traffic_sh_dataleft";
	
	
	/**
	 * 上次关机的日期
	 */
	public static final String sys_isshutdown_date = "sys_isshutdown_date";
	
	
	public static class subquery{
		
		/**
		 * 查询某一个uid的某两天的之间使用的流量,上传的和下载的流量 
		 * 注意每一个子查询有四个参数 年, 月 , 日,uid
		 * 
		 */
		public static final String sql_statistic_appsbydayuid = "SELECT   (A.RECEIVED_TOTAL -B.RECEIVED_TOTAL)  AS  RECEIVED_TOTAL ," +
				" (A.UPLOAD_TOTAL - B.UPLOAD_TOTAL) AS UPLOAD_TOTAL  " +
				"FROM  " +
				"(  SELECT  RECEIVED_TOTAL AS RECEIVED_TOTAL , UPLOAD_TOTAL AS UPLOAD_TOTAL  " +
				"FROM EMM_SYS_TRAF_UID  WHERE TYEAR =? AND TMONTH= ? AND TDAY =? AND UID =? ORDER BY TTIME DESC  LIMIT 1  " +
				") AS A , " +
				"(SELECT   RECEIVED_TOTAL  AS RECEIVED_TOTAL , UPLOAD_TOTAL AS UPLOAD_TOTAL   " +
				"FROM EMM_SYS_TRAF_UID  WHERE TYEAR =? AND TMONTH= ? AND TDAY =? AND UID =? ORDER BY TTIME DESC  LIMIT 1 " +
				") AS B  "; 
		
		/**
		 * 查询某一个packagename 的某两天的之间使用的流量,上传的和下载的流量 
		 * 注意每一个子查询有四个参数 年, 月 , 日,包名
		 */
		public static final String sql_statistic_appsbydaypackagename = "SELECT   (A.RECEIVED_TOTAL -B.RECEIVED_TOTAL)  AS  RECEIVED_TOTAL ," +
				" (A.UPLOAD_TOTAL - B.UPLOAD_TOTAL) AS UPLOAD_TOTAL  " +
				"FROM  " +
				"(  SELECT  RECEIVED_TOTAL AS RECEIVED_TOTAL , UPLOAD_TOTAL AS UPLOAD_TOTAL  " +
				"FROM EMM_SYS_TRAF_UID  WHERE TYEAR =? AND TMONTH= ? AND TDAY =? AND PACKAGE_NAME =? ORDER BY TTIME DESC  LIMIT 1  " +
				") AS A , " +
				"(SELECT   RECEIVED_TOTAL  AS RECEIVED_TOTAL , UPLOAD_TOTAL AS UPLOAD_TOTAL   " +
				"FROM EMM_SYS_TRAF_UID  WHERE TYEAR =? AND TMONTH= ? AND TDAY =? AND PACKAGE_NAME =? ORDER BY TTIME DESC  LIMIT 1 " +
				") AS B  "; 
		
		/**
		 * 查询某一天所有产生流量的app的信息,去重后的.
		 * 注意 查询中有三个参数 年 , 月 , 日   
		 */
		public static final String sql_statisticappscategory_byday = "SELECT  DISTINCT  *  " +
				"FROM EMM_SYS_TRAF_UID  " +
				"WHERE TYEAR =? AND TMONTH= ? AND TDAY =? " +
				"GROUP BY PACKAGE_NAME";
		
		
		/**
		 * 查询某一天某一个应用是否存在流量信息
		 */
		public static final String sql_statistic_appsexistsdata_byday = "SELECT  *   FROM EMM_SYS_TRAF_UID  WHERE TYEAR =? AND TMONTH= ? AND TDAY =? AND  PACKAGE_NAME = ? ";
		
		
		/**
		 * 查询某一天某一个应用的最后一次记录的流量信息
		 */
		public static final String sql_statistic_appstraffics_bydaypackagename = "SELECT  *  FROM EMM_SYS_TRAF_UID  WHERE TYEAR =? AND TMONTH= ? AND TDAY =?  AND PACKAGE_NAME = ? ORDER BY TTIME  DESC  LIMIT 1  ;";
		
		
		/**
		 * 查询某两天的之间使用的流量 数据流量,WLAN 流量 
		 * 
		 * 注意每一个子查询有四个参数   月 , 日
		 */
		public static final String sql_statistic_systraffics_byday = "SELECT   (A.TD -  B.TD)  AS  TD , " +
				"(A.TW - B.TW) AS TW  " +
				"FROM  " +
				"(  SELECT  TD AS TD , TW AS TW  " +
				"FROM EMM_SYS_TRAF  WHERE   TYEAR  = ? AND  TMONTH= ? AND TDAY =?  ORDER BY TTIME DESC  LIMIT 1  ) " +
				"AS A ,  " +
				"(SELECT  TD AS TD , TW AS TW  " +
				"FROM EMM_SYS_TRAF  WHERE   TYEAR  = ? AND  TMONTH= ? AND TDAY =?  ORDER BY TTIME DESC  LIMIT 1  ) " +
				"AS B ";
		
	
		/**
		 * 获取单天系统流量信息的最后一条记录
		 */
		public static final String sql_statistic_singledaysystraffics_byday = "SELECT * FROM EMM_SYS_TRAF  WHERE TYEAR  = ? AND   TMONTH= ? AND TDAY =?  ORDER BY TTIME DESC  LIMIT 1  ;";
		
		
		
		/**
		 * 插入表数据到系统流量信息统计中
		 */
		public static final String sql_statistic_systraffics_insert = "insert into  emm_sys_traf(td,tw,utd,ttime,tday,tmonth, TYEAR ) values (?,?,?,?,?,?,?) ";
		
		/**
		 * 查询DB中所拥有的系统流量信息 按照天分类 不重复
		 */
		public static final String sql_statistic_systraffics_days = "SELECT DISTINCT * FROM EMM_SYS_TRAF WHERE TYEAR  = ? AND   TMONTH= ? GROUP BY TDAY";
		
		
		
		
		
	}
}
