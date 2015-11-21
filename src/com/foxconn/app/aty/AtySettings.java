package com.foxconn.app.aty;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.view.sliding.AbSlidingButton;
import com.ab.view.titlebar.AbTitleBar;
import com.foxconn.app.App;
import com.foxconn.app.R;
import com.foxconn.emm.lock.GuideGesturePasswordActivity;
import com.foxconn.emm.lock.UnlockGesturePasswordActivity;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.utils.EMMReqParamsUtils;
import com.foxconn.emm.utils.HttpClientUtil;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.view.CommonMsgInfoDialog;

public class AtySettings extends EMMBaseActivity implements
		OnCheckedChangeListener, OnClickListener {
	private AbSlidingButton sys_seting_device_safe_content_tbtn;
	private LinearLayout location_code_dialog;
	private EditText code;
	private TextView sys_seting_device_serialno_title_tv;
	private Button sys_seting_device_serialno_content_btn;
	private Button sys_delete_device_serialno_content_btn;
	private Button sys_seting_aboutus_btn;
	private String num;
	/**
	 * 重新设置标识
	 */
	public static final int RESET_SECURITY_ID = 6;

	/**
	 * 关闭标识
	 */
	public static final int CLOSE_SECURITY_ID = 8;

	/**
	 * 设置标识
	 */
	public static final int SET_SECURITY_ID = 3;

	/**
	 * 时常解锁使用标记
	 */
	public static final int UNlOCK_OFFEN = 9;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.aty_setting);

		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("设置");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(android.R.color.holo_blue_light);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		initView();
	}

	private void initView() {
		location_code_dialog = (LinearLayout) getLayoutInflater().inflate(
				R.layout.location_code_dialog, null);
		code = (EditText) location_code_dialog.findViewById(R.id.code);
		// / 设备编号标题
		sys_seting_device_serialno_title_tv = (TextView) this
				.findViewById(R.id.sys_seting_device_serialno_title_tv);
		sys_seting_device_serialno_content_btn = (Button) this
				.findViewById(R.id.sys_seting_device_serialno_content_btn);
		sys_seting_device_serialno_content_btn.setOnClickListener(this);

		sys_seting_device_safe_content_tbtn = (com.ab.view.sliding.AbSlidingButton) this
				.findViewById(R.id.sys_seting_device_safe_content_tbtn);
		sys_seting_device_safe_content_tbtn.setChecked(EMMPreferences.getSecurityStatus(this));
		sys_seting_device_safe_content_tbtn.setPressed(false);
		sys_seting_device_safe_content_tbtn.setImageResource(
				R.drawable.btn_bottom, R.drawable.btn_frame,
				R.drawable.btn_mask, R.drawable.btn_unpressed,
				R.drawable.btn_pressed);
		sys_seting_device_safe_content_tbtn.setOnCheckedChangeListener(this);

		sys_seting_aboutus_btn = (Button) this
				.findViewById(R.id.sys_seting_aboutus_btn);
		sys_seting_aboutus_btn.setOnClickListener(this);

		sys_delete_device_serialno_content_btn = (Button) this
				.findViewById(R.id.sys_delete_device_serialno_content_btn);
		sys_delete_device_serialno_content_btn.setOnClickListener(this);
		if (EMMPreferences.getLocationCode(this) == ""
				|| EMMPreferences.getLocationCode(this) == null) {
			sys_seting_device_serialno_title_tv
					.setText(R.string.sys_seting_device_serialno_title_tv_hint);
		} else {
			code.setText(EMMPreferences.getLocationCode(this));
			sys_seting_device_serialno_title_tv.setText("设备编号："
					+ EMMPreferences.getLocationCode(this));
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		L.i(this.getClass(), "onResume");
		initView();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(!buttonView.isPressed())return;
			if (isChecked) {
				if (EMMPreferences.getSecurityStatus(getApplicationContext())) {

					startActivity(new Intent(this,
							UnlockGesturePasswordActivity.class)
							.setFlags(RESET_SECURITY_ID));
				} else {
					startActivity(new Intent(this,GuideGesturePasswordActivity.class).setFlags(SET_SECURITY_ID));
				}
			} else {
				if (EMMPreferences.getSecurityStatus(getApplicationContext())) {
					startActivity(new Intent(this,
							UnlockGesturePasswordActivity.class)
							.setFlags(CLOSE_SECURITY_ID));
				} else {
					App.getInstance().getLockPatternUtils().clearLock();
					EMMPreferences.setSecurityStatus(getApplicationContext(),false);
					sys_seting_device_safe_content_tbtn
							.setChecked(EMMPreferences
									.getSecurityStatus(getApplicationContext()),true);
				}

			}
	}

	private void openLocationCodeChange() {
		new AlertDialog.Builder(AtySettings.this).setTitle("设置")
				.setView(location_code_dialog).setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						num = code.getText().toString();
						// 调用接口
						if (!TextUtils.isEmpty(num)) {
							SaveDeviceNum sd = new SaveDeviceNum();
							sd.execute(num);
						}
						initView();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						initView();
					}
				}).show();
	}

	private void deleteLocationCode() {
		if (EMMPreferences.getLocationCode(AtySettings.this) != "") {
			code.setText("");
			num = "";
			SaveDeviceNum sd = new SaveDeviceNum();
			sd.execute(num);
		}
	}

	private void openAtyAbout() {
		Intent intent = new Intent(this, AtyAbout.class);
		startActivity(intent);
	}

	private void parseAsyncTaskResult(String result) {
		try {
			if (TextUtils.isEmpty(result)) {
				Toast.makeText(this, "设备编号存储失败，请重试", Toast.LENGTH_LONG).show();
			}
			JSONObject jsonObject = new JSONObject(result);
			String string = jsonObject.getString("success");
			L.d(this.getClass(), string);
			if (string.equalsIgnoreCase("true")) {
				EMMPreferences.setLocationCode(AtySettings.this, num);
				initView();
				if (num != "")
					Toast.makeText(this, "存储成功！", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(this, "删除成功！", Toast.LENGTH_SHORT).show();
			} else {
				if (num != "")
					Toast.makeText(this, "存储失败", Toast.LENGTH_LONG).show();
				else
					Toast.makeText(this, "删除失败", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.showToast("数据解析异常,请稍后重试.");
		}
	}

	protected class SaveDeviceNum extends AsyncTask<String, Void, String> {

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				parseAsyncTaskResult(result);
			}
		}

		@Override
		protected String doInBackground(String... params) {

			try {
				String requrl = EMMContants.SYSCONF.UPDATE_DEVICE_NUMBER;
				String reqparams = EMMReqParamsUtils.getSyncDeviceSerialNoStr(
						AtySettings.this, params[0]);
				return HttpClientUtil.returnData(requrl, reqparams);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	protected class AtySettingDialog extends CommonMsgInfoDialog {

		public AtySettingDialog(Context context, int flag) {
			super(context, flag);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void deleteInfo() {
			// TODO Auto-generated method stub
			super.deleteInfo();
			deleteLocationCode();
		}
	}

	private void deleteDialog() {
		AtySettingDialog dialog = new AtySettingDialog(this,
				EMMContants.DialogFlag.DELETE);
		dialog.show();
		dialog.setTwoBtnDialog(EMMContants.DialogText.DELETETITLE,
				EMMContants.DialogText.DELETELOCATION,
				EMMContants.DialogText.POSITIVEBUTTON,
				EMMContants.DialogText.NEGATIVEBUTTON);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		// //修改设备编号..
		case R.id.sys_seting_device_serialno_content_btn:
			openLocationCodeChange();
			break;

		// // 删除设备编号
		case R.id.sys_delete_device_serialno_content_btn:
			deleteDialog();

			break;

		case R.id.sys_seting_aboutus_btn:
			openAtyAbout();
			break;
		default:
			break;
		}

	}

}
