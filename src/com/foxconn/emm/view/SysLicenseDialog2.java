package com.foxconn.emm.view;


import java.util.Date;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foxconn.app.App;
import com.foxconn.app.R;
import com.foxconn.app.aty.AtyWelcome;
import com.foxconn.emm.bean.SysLicenseResult;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.utils.EMMReqParamsUtils;
import com.foxconn.emm.utils.HttpClientUtil;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.utils.TextFormater;
import com.foxconn.lib.welcome.license.AtyFirstLoadWelcomePage;
import com.google.gson.Gson;

public class SysLicenseDialog2 extends Dialog implements View.OnClickListener,
		OnFocusChangeListener  {

	private EditText sys_license_dialog_enterprisename_tv;

	private EditText sys_license_dialog_licenseno_tv;

	private ImageView sys_license_button_clear;
	private ImageView sys_license_key_button_clear;

	private Button sys_license_dialog_varlidate_btn;
	
	private TextView sys_license_dialog_varlidate_showdeadline_tv;
	
	private RelativeLayout sys_license_dialog_varlidate_showresult_rl;
	
	private ImageView sys_license_dialog_varlidate_btn_left_iv;
	
	private ProgressBar sys_license_dialog_varlidate_btn_right_pb;
	
	private Button sys_license_dialog_varlidate_close_btn; 
	
	private TextView sys_license_dialog_msg_content_tv;
	
	/**
	 * 在延期15内部
	 * 
	 * 
	 */
	private static final int inner_exprises = 1;
	
	/**
	 * 不在延期15内部
	 * 
	 */
	private static final int outter_exprises = 2;
	
	/**
	 * 
	 */
	private static final int reactive_license =3;
	
	
	private   int current_condition ;
	
	
	public boolean is_varlidatepass;
	
	public boolean canentersys ; 
	
	public boolean is_revalidate  = true; 

	private Context context;

	private SysLicenseResult sysLicenseResult;

	private String enterpriseName ;
	private String licenseNo ;

	private AtyWelcome atywelcome; 

	public static final String CONSTANT_LICENSENO="***************";
	
	public SysLicenseDialog2(Context context) {
		super(context, R.style.Theme_CustomDialog);
		this.setContext(context);
	}
	public SysLicenseDialog2(AtyWelcome atyWelcome, SysLicenseResult result) {
		this(atyWelcome);
		this.atywelcome = atyWelcome;
		this.sysLicenseResult = result;
		initCurrentcondition(result);
		enterpriseName = EMMPreferences.getSysLicenseEnterpriseName(atyWelcome);
		licenseNo = EMMPreferences.getSysLicenseNo(atyWelcome);
	}
	
	private void initCurrentcondition(SysLicenseResult result) {
		if(result.getCode().equalsIgnoreCase(SysLicenseResult.tag_code_fail_expire)){
			 int leftDay = getLeftDay(result);
			 if(leftDay>=1&&leftDay<=15){
				 setCurrent_condition(inner_exprises);
			 }else{
				 setCurrent_condition(outter_exprises);
			 }
		}else{
			setCurrent_condition(outter_exprises);
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_syslicense_dialog2);
		initView();
	}
	
	private void initView() {
		
		sys_license_dialog_enterprisename_tv = (EditText)findViewById(R.id.sys_license_dialog_enterprisename_tv);
		sys_license_dialog_licenseno_tv = (EditText) findViewById(R.id.sys_license_dialog_licenseno_tv);
		sys_license_button_clear = (ImageView) findViewById(R.id.sys_license_button_clear);
		sys_license_key_button_clear = (ImageView)findViewById(R.id.sys_license_key_button_clear);
		sys_license_dialog_varlidate_btn = (Button) findViewById(R.id.sys_license_dialog_varlidate_btn);
		sys_license_dialog_varlidate_close_btn = (Button) findViewById(R.id.sys_license_dialog_varlidate_close_btn);
		sys_license_dialog_varlidate_showresult_rl = (RelativeLayout) findViewById(R.id.sys_license_dialog_varlidate_showresult_rl);
		sys_license_dialog_varlidate_showdeadline_tv = (TextView)findViewById(R.id.sys_license_dialog_varlidate_showdeadline_tv);
		sys_license_dialog_varlidate_btn_left_iv = (ImageView) findViewById(R.id.sys_license_dialog_varlidate_btn_left_iv);
		sys_license_dialog_varlidate_btn_right_pb = (ProgressBar) findViewById(R.id.sys_license_dialog_varlidate_btn_right_pb);
		sys_license_dialog_msg_content_tv  = (TextView) findViewById(R.id.sys_license_dialog_msg_content_tv);
		sys_license_dialog_licenseno_tv.setOnFocusChangeListener(this);
		sys_license_dialog_licenseno_tv.setText(CONSTANT_LICENSENO);
		sys_license_dialog_licenseno_tv.setEnabled(false);
		sys_license_key_button_clear.setOnClickListener(this);
		sys_license_key_button_clear.setEnabled(false);
		sys_license_button_clear.setOnClickListener(this);
		sys_license_button_clear.setEnabled(false);
		sys_license_dialog_enterprisename_tv.setOnFocusChangeListener(this);
		sys_license_dialog_enterprisename_tv.setText(enterpriseName);
		sys_license_dialog_enterprisename_tv.setEnabled(false);
		sys_license_dialog_varlidate_btn.setOnClickListener(this);
		sys_license_dialog_varlidate_close_btn.setOnClickListener(this);
		sys_license_dialog_varlidate_showresult_rl.setVisibility(View.VISIBLE);

		sys_license_dialog_msg_content_tv.setText("企业序列号过期延期使用提醒");
		if(getCurrent_condition()==inner_exprises){
			sys_license_dialog_varlidate_btn.setText("确认");
			sys_license_dialog_varlidate_close_btn.setVisibility(View.GONE);
		}else{
//			sys_license_dialog_msg_content_tv.setText(context.getResources().getString(R.string.sys_license_dialog_msg_content_tv_hint));
			sys_license_dialog_varlidate_btn.setText("重新验证");
			sys_license_dialog_varlidate_close_btn.setVisibility(View.VISIBLE);
		}
		
		sys_license_dialog_varlidate_showdeadline_tv.setText(getCause(sysLicenseResult));
		sys_license_dialog_varlidate_showdeadline_tv.setTextColor(context.getResources().getColor(android.R.color.holo_orange_light));
		sys_license_dialog_varlidate_btn_left_iv.setImageDrawable(getbtnLeftivDrawable(sysLicenseResult));
		sys_license_dialog_varlidate_btn_right_pb.setVisibility(View.GONE);
		SysLicenseDialog2.this.setCancelable(false);
	}

	private Drawable getbtnLeftivDrawable(SysLicenseResult sysLicenseResult2) {
		
		int leftDay = getLeftDay(sysLicenseResult2);
		if(leftDay>=15||leftDay>=1){
			canentersys = true;
			return context.getResources().getDrawable(R.drawable.sys_license_validate_btn_left_warning);
		}else{
			canentersys = false;
			return context.getResources().getDrawable(R.drawable.sys_license_validate_btn_left_error);
		}
	}
	
	private CharSequence getCause(SysLicenseResult result) {
		if(result.getCode().equalsIgnoreCase(SysLicenseResult.tag_code_fail_expire)){
			return "序列号已过期,延期使用15天 , 剩余"+getLeftDay(result)+"天";
		}else if(result.getCode().equalsIgnoreCase(SysLicenseResult.tag_code_fail_invalid)){
			canentersys = false;
			return "LicenseNo无效";
		}else if(result.getCode().equalsIgnoreCase(SysLicenseResult.tag_code_fail_uplimit)){
			canentersys = false;
			return "注册数已达到上限";
		}else{
			canentersys = false;
			return "访问请求超时,请检查网络重试";
		}
	}
	
	private int getLeftDay(SysLicenseResult result) {
		long failureTime = TextFormater.getTimeToLong(result.getFailureTime());
		long nowTime = TextFormater.getTimeToLong(result.getNowTime());
		if((nowTime-failureTime)>0){
			int leftday =   (int)(15-(Math.abs(nowTime -failureTime)/(24*3600*1000)));
			if(leftday>0){
				return leftday;
			}else {
				return 0;
			}
		}
		return 0;
	}
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.sys_license_dialog_enterprisename_tv:
			if (hasFocus) {
				sys_license_button_clear.setVisibility(View.VISIBLE);
			} else {
				sys_license_button_clear.setVisibility(View.GONE);
			}
			break;
		case R.id.sys_license_dialog_licenseno_tv:
			if (hasFocus) {
				sys_license_key_button_clear.setVisibility(View.VISIBLE);
			} else {
				sys_license_key_button_clear.setVisibility(View.GONE);
			}
			break;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sys_license_dialog_varlidate_btn:
			onclickturn();
			break;
			/// 退出整个系统
		case R.id.sys_license_dialog_varlidate_close_btn:
			App.getInstance().exit();
			break;
		case R.id.sys_license_key_button_clear:
			sys_license_dialog_licenseno_tv.setText("");
			break;
		case R.id.sys_license_button_clear:
			sys_license_dialog_enterprisename_tv.setText("");
			break;
		}
	}


	
	/**
	 * 分为两种情况: 
	 * 
	 * 1.设备License 在延期内部
	 * 	a.
	 * 
	 * 2.设备License已经超出延期15天
	 * 
	 */
	private void onclickturn() {
		if(canentersys){
			canentersys = false;
			atywelcome.validateturn();
			cancel();
		}
		if(is_revalidate){
			sys_license_dialog_enterprisename_tv.setEnabled(true);
			sys_license_dialog_licenseno_tv.setEnabled(true);
			sys_license_dialog_enterprisename_tv.setText("");
			sys_license_dialog_licenseno_tv.setText("");
			sys_license_button_clear.setEnabled(true);
			sys_license_key_button_clear.setEnabled(true);
			sys_license_dialog_varlidate_close_btn.setVisibility(View.VISIBLE);
			sys_license_dialog_varlidate_btn.setText("验证");
			sys_license_dialog_msg_content_tv.setText(context.getResources().getString(R.string.sys_license_dialog_msg_content_tv_hint));
			is_revalidate = false;
		}
		if(is_varlidatepass){
			EMMPreferences.setLicenseIsPass(getContext(), true);
			atywelcome.validateturn();
			cancel();
		}
		validateLicense();
	}
	private void validateLicense() {
		String enterpriseName = sys_license_dialog_enterprisename_tv.getText().toString().trim();
		if(TextUtils.isEmpty(enterpriseName)){
			sys_license_dialog_enterprisename_tv.setError("企业名称不能为空",getContext().getResources().getDrawable(R.drawable.sys_license_varlidate_btn_left_error));
			return;
		}
		String licenseNo  = sys_license_dialog_licenseno_tv.getText().toString().trim();
		if(TextUtils.isEmpty(licenseNo)){
			sys_license_dialog_licenseno_tv.setError("序列号不合法",getContext().getResources().getDrawable(R.drawable.sys_license_varlidate_btn_left_error));
			return;
		}
		ValidateLicenseTask	varlidateTask = new ValidateLicenseTask(enterpriseName,licenseNo);
		varlidateTask.execute();
	}
	
	class ValidateLicenseTask extends AsyncTask<String, Void, SysLicenseResult>{

		private String enterpriseName;
		private String licenseNo;
		private CountDownTimer countDownTimer ;
		
		public ValidateLicenseTask(String enterpriseName , String licenseNo){
			this.enterpriseName = enterpriseName;
			this.licenseNo = licenseNo;
			countDownTimer =  new CountDownTimer(10*1000, 1000) {
			     public void onTick(long millisUntilFinished) {
			     }
			     public void onFinish() {
			    	ValidateLicenseTask.this.cancel(true);
			     }
			  };
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
				sys_license_key_button_clear.setEnabled(true);
			    sys_license_button_clear.setEnabled(true);
			    sys_license_dialog_licenseno_tv.setEnabled(true);
				sys_license_dialog_enterprisename_tv.setEnabled(true);
				sys_license_dialog_varlidate_btn_right_pb.setVisibility(View.GONE);
				sys_license_dialog_varlidate_btn.setEnabled(true);
				sys_license_dialog_varlidate_showdeadline_tv.setText("验证失败.错误原因:访问超时");
				sys_license_dialog_varlidate_showdeadline_tv.setTextColor(getContext().getResources().getColor(R.color.ems_red));
				sys_license_dialog_varlidate_btn_left_iv.setImageDrawable(getContext().getResources().getDrawable(R.drawable.sys_license_varlidate_btn_left_error));
				sys_license_dialog_varlidate_showresult_rl.setVisibility(View.VISIBLE);
				sys_license_dialog_varlidate_btn.setText("重新验证");
				is_varlidatepass = false;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			sys_license_key_button_clear.setEnabled(false);
			sys_license_button_clear.setEnabled(false);
			sys_license_dialog_licenseno_tv.setEnabled(false);
			sys_license_dialog_enterprisename_tv.setEnabled(false);
			sys_license_dialog_varlidate_btn_right_pb.setVisibility(View.VISIBLE);
			sys_license_dialog_varlidate_btn.setText("验证中");
			sys_license_dialog_varlidate_btn.setEnabled(false);
			sys_license_dialog_varlidate_showresult_rl.setVisibility(View.GONE);
	
			countDownTimer.start();
		}
		protected SysLicenseResult doInBackground(String... params) {
			String url = EMMContants.SYSCONF.SYS_LICENSE_CHECKATCION;
			String reqparams = EMMReqParamsUtils.getSysLicenseValidate(getContext(), licenseNo, enterpriseName);
			String returnData = HttpClientUtil.returnData(url, reqparams);
			L.d(this.getClass(), returnData);
			if(TextFormater.isEmpty(returnData)){
				return null;
			}
			return new Gson().fromJson(returnData, SysLicenseResult.class);
		}
		
		@Override
		protected void onPostExecute(SysLicenseResult result) {
			super.onPostExecute(result);
			sys_license_dialog_licenseno_tv.setEnabled(true);
			sys_license_dialog_enterprisename_tv.setEnabled(true);
			sys_license_dialog_varlidate_btn_right_pb.setVisibility(View.GONE);
			sys_license_dialog_varlidate_btn.setEnabled(true);
			sys_license_key_button_clear.setEnabled(true);
			sys_license_button_clear.setEnabled(true);
			if(result!=null){
				if(result.getSuccess().equalsIgnoreCase(SysLicenseResult.tag_success_success)){
				sys_license_dialog_varlidate_showdeadline_tv.setText("恭喜,验证通过.有效期至:"+result.getFailureTime());
				sys_license_dialog_varlidate_showdeadline_tv.setTextColor(getContext().getResources().getColor(R.color.ems_green));
				sys_license_dialog_varlidate_btn_left_iv.setImageDrawable(getContext().getResources().getDrawable(R.drawable.sys_license_varlidate_btn_left_ok));
				sys_license_dialog_varlidate_showresult_rl.setVisibility(View.VISIBLE);
				save2SP(result);
				sys_license_dialog_varlidate_btn.setText("点击继续");
				is_varlidatepass = true;
				}else if(result.getSuccess().equalsIgnoreCase(SysLicenseResult.tag_success_fail)){
					sys_license_dialog_varlidate_showdeadline_tv.setText("验证失败.错误原因:"+getcauseStr(result));
					sys_license_dialog_varlidate_showdeadline_tv.setTextColor(getContext().getResources().getColor(R.color.ems_red));
					sys_license_dialog_varlidate_btn_left_iv.setImageDrawable(getContext().getResources().getDrawable(R.drawable.sys_license_varlidate_btn_left_error));
					sys_license_dialog_varlidate_showresult_rl.setVisibility(View.VISIBLE);
					sys_license_dialog_varlidate_btn.setText("重新验证");
					is_varlidatepass = false;
				}
			}else{
				sys_license_dialog_varlidate_btn_left_iv.setImageDrawable(getContext().getResources().getDrawable(R.drawable.sys_license_varlidate_btn_left_error));
				sys_license_dialog_varlidate_showdeadline_tv.setText("验证失败.请重新验证");
				sys_license_dialog_varlidate_showdeadline_tv.setTextColor(getContext().getResources().getColor(R.color.ems_red));
				sys_license_dialog_varlidate_showresult_rl.setVisibility(View.VISIBLE);
				sys_license_dialog_varlidate_btn.setText("重新验证");
				is_varlidatepass = false;
			}
		}

		private String getcauseStr(SysLicenseResult result) {
			if(result.getCode().equalsIgnoreCase(SysLicenseResult.tag_code_fail_expire)){
				return "序列号已过期";
			}else if(result.getCode().equalsIgnoreCase(SysLicenseResult.tag_code_fail_invalid)){
				return "LicenseNo无效";
			}else if(result.getCode().equalsIgnoreCase(SysLicenseResult.tag_code_fail_uplimit)){
				return "注册数已达到上限";
			}else{
				return "访问请求超时,请检查网络重试";
			}
		}

		private void save2SP(SysLicenseResult result) {
			EMMPreferences.setSysLicenseEnterpriseName(getContext(),enterpriseName );
			EMMPreferences.setSysLicenseNo(getContext(), licenseNo);
			EMMPreferences.setSysLicenseDeadLine(getContext(), result.getFailureTime());
			L.d(this.getClass(), "license saved complete !");
		}
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
	}

	@Override
	public void cancel() {
		super.cancel();
		if(this.getContext() instanceof AtyFirstLoadWelcomePage){
			AtyFirstLoadWelcomePage atyWelcome = (AtyFirstLoadWelcomePage) this.getContext();
			atyWelcome.finish();
		}
	}
	/**
	 * @param context the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}
	/**
	 * @return the current_condition
	 */
	public int getCurrent_condition() {
		return current_condition;
	}
	/**
	 * @param current_condition the current_condition to set
	 */
	public void setCurrent_condition(int current_condition) {
		this.current_condition = current_condition;
	}

}
