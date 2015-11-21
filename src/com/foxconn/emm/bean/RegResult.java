package com.foxconn.emm.bean;

public class RegResult {
	private String success;
	private String msg;
	private String code;
	private String flag;

	public static class TAG{
		public static final String SUCCESS= "success";
		public static final String MSG= "msg";
		public static final String CODE= "code";
		public static final String FLAG= "flag";
		
		public static final String TRUE= "true";
		public static final String FALSE= "false";
		
		
		/**
		 * 首次注册成功
		 */
		public static final String code_userinfo_notexists_0 = "0";
		/**
		 * 帐密错误
		 */
		public static final String code_registererror_1 = "1";
		/**
		 * 缺少栏位
		 */
		public static final String code_registersuccess_2 = "2";
		/**
		 * 删除后再次注册
		 */
		public static final String code_notundercontrol_3 = "3";
		/**
		 * 重新注册 
		 */
		public static final String code_reregister_4 = "4";
		
		/**
		 * 使用原注册名进行注册.
		 */
		public static final String code_reoldusername_5 = "5";
		
		/**
		 * 
		 * {
    "code": "0",
    "msg": "该用户信息不存在,如需注册请联系管理员579 25632",
    "success": "false"
}
{
    "code": "1",
    "msg": "用户密码输入有误",
    "success": "false"
}
{
    "code": "2",
    "msg": "设备注册成功",
    "success": "true"
}
{
    "code": "3",
    "msg": "该设备不受管控，如需注册请联系管理员579 25632",
    "success": "false"
}
{
    "code": "4",
    "msg": "设备重新注册成功",
    "success": "true"
}
{
    "code": "5",
    "msg": "请使用原有用户工号注册",
    "success": "false"
}
		 * 
		 */
		
		
	}
	
	public void setSuccess(String success) {
		this.success = success;
	}

	public String getSuccess() {
		return success;
	}

	@Override
	public String toString() {
		return "RegResult [success=" + success + ", msg=" + msg + ", code="
				+ code + ", flag=" + flag + "]";
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}