package com.foxconn.emm.bean;

/**
 * 
 * {
    "failureTime": "2015-11-26 11:46:27",
    "code": "1",
    "success": "false",
    "nowTime": "2014-11-26 11:46:26"
}
{
    "failureTime": "2015-11-26 11:46:27",
    "code": "2",
    "success": "false",
    "nowTime": "2014-11-26 11:46:26"
}
{
    "failureTime": "2015-11-26 11:46:27",
    "code": "0",
    "success": "true",
    "nowTime": "2014-11-26 11:46:26"
}

 *
 */
public class SysLicenseResult {
	
	/**
	 * 0：成功代码
	 */
	public static final String tag_code_success = "0";
	/**
	 * 1：LicenseNo无效
	 */
	public static final String tag_code_fail_invalid ="1";
	/**
	 * 2：达到上限
	 */
	public static final String tag_code_fail_uplimit = "2";
	/**
	 * 3：时间过期
	 */
	public static final String tag_code_fail_expire ="3";
	
	/**
	 * true 验证成功
	 */
	public static final String tag_success_success = "true";

	/**
	 * false 验证失败
	 */
	public static final String tag_success_fail = "false";
	
	
	/**
	 * failureTime 
	 */
	private String failureTime ;
	
	
	/**
	 * current time 
	 * 
	 */
	private String nowTime ; 

	
	/**
	 * varlidate code 
	 * 
	 */
	private String code ; 
	
	/**
	 *  success ;
	 * 
	 */
	private String success; 

	
	/**
	 * @return the failureTime
	 */
	public String getFailureTime() {
		return failureTime;
	}

	/**
	 * @param failureTime the failureTime to set
	 */
	public void setFailureTime(String failureTime) {
		this.failureTime = failureTime;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the success
	 */
	public String getSuccess() {
		return success;
	}

	/**
	 * @param success the success to set
	 */
	public void setSuccess(String success) {
		this.success = success;
	}

	/**
	 * @return the nowTime
	 */
	public String getNowTime() {
		return nowTime;
	}

	/**
	 * @param nowTime the nowTime to set
	 */
	public void setNowTime(String nowTime) {
		this.nowTime = nowTime;
	} 
	
	
	
	
	
	
}
