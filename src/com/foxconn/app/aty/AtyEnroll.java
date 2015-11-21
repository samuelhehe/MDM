package com.foxconn.app.aty;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.foxconn.app.App;
import com.foxconn.app.IConnectionStatusCallback;
import com.foxconn.app.R;
import com.foxconn.emm.service.EMMService;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.utils.T;

public class AtyEnroll extends AbActivity implements OnClickListener,
		IConnectionStatusCallback, OnFocusChangeListener {

	private Button button_login;
	private Button button_register;
	private AutoCompleteTextView edittext_account;
	private EditText edittext_password;
	private ImageView button_pwd_clear, button_account_clear;

	protected static final int LOGIN_OUT_TIME = 1;

	private AutoCompleteTextView username_et;

	private EditText password_et;

	private Dialog mLoginDialog;

	private ConnectionOutTimeProcess mLoginOutTimeProcess;

	private EMMService mEMMService;

	private String muserId;
	private String mpassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 startService(new Intent(this, EMMService.class));
		L.i(this.getClass(), "onCreate");
		// String stringExtra =
		// getIntent().getStringExtra(AtyWelcome.FLAG_FROM_ATY);
		// if(TextUtils.isEmpty(stringExtra)||stringExtra.equalsIgnoreCase("null")){
		// finish();
		// }
		setContentView(R.layout.activity_enroll);
		initView();
		App.getInstance().addActivity(this);
		bindEMMService();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

	}

	@Override
	protected void onResume() {

		L.i(getClass(), "onResume");
		super.onResume();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		L.i(getClass(), "onRestart is called ");
	}

	private void initView() {
		button_login = (Button) findViewById(R.id.button_login);
		button_register = (Button) findViewById(R.id.button_register);
		edittext_account = (AutoCompleteTextView) findViewById(R.id.edittext_account);
		edittext_password = (EditText) findViewById(R.id.edittext_password);
		button_account_clear = (ImageView) findViewById(R.id.button_account_clear);
		button_pwd_clear = (ImageView) findViewById(R.id.button_pwd_clear);
		button_login.setOnClickListener(this);
		button_register.setOnClickListener(this);
		edittext_account.setOnFocusChangeListener(this);
		edittext_password.setOnFocusChangeListener(this);
		button_account_clear.setOnClickListener(this);
		button_pwd_clear.setOnClickListener(this);
		username_et = (AutoCompleteTextView) this
				.findViewById(R.id.edittext_account);
		password_et = (EditText) this.findViewById(R.id.edittext_password);

		button_pwd_clear = (ImageView) this.findViewById(R.id.button_pwd_clear);
		button_pwd_clear.setOnClickListener(this);

		button_account_clear = (ImageView) this
				.findViewById(R.id.button_account_clear);
		button_account_clear.setOnClickListener(this);

		mLoginDialog = getLoginDialog(this);
		mLoginOutTimeProcess = new ConnectionOutTimeProcess();

	}

	public static Dialog getLoginDialog(Activity context) {

		final Dialog dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.custom_progress_dialog);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();

		int screenW = getScreenWidth(context);
		lp.width = (int) (0.6 * screenW);
		TextView titleTxtv = (TextView) dialog.findViewById(R.id.dialogText);
		titleTxtv.setText("登陆中...");
		return dialog;
	}

	public static int getScreenWidth(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOGIN_OUT_TIME:
				if (mLoginOutTimeProcess != null
						&& mLoginOutTimeProcess.running)
					mLoginOutTimeProcess.stop();
				if (mLoginDialog != null && mLoginDialog.isShowing())
					mLoginDialog.dismiss();

				T.showShort(AtyEnroll.this, "连接超时,请确认网络连接可用 ...  ");
				break;

			default:
				break;
			}
		}

	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_login:
			onLoginClick();
			break;
		case R.id.button_pwd_clear:
			password_et.setText("");
			password_et.requestFocus();
			break;
		case R.id.button_account_clear:
			username_et.setText("");
			username_et.requestFocus();
			break;
		case R.id.button_register:
			onBackPressed();
			
			break;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.edittext_account:
			if (hasFocus) {
				button_account_clear.setVisibility(View.VISIBLE);
			} else {
				button_account_clear.setVisibility(View.GONE);
			}
			break;

		case R.id.edittext_password:
			if (hasFocus) {
				button_pwd_clear.setVisibility(View.VISIBLE);
			} else {
				button_pwd_clear.setVisibility(View.GONE);
			}
			break;
		}
	}

	private void bindEMMService() {
		L.i(AtyEnroll.class, "[SERVICE] bind");
		Intent mServiceIntent = new Intent(this, EMMService.class);
		mServiceIntent.setAction(EMMContants.EMMAction.LOGIN_ACTION);
		// bindService(mServiceIntent, mServiceConnection,
		// Context.BIND_AUTO_CREATE);
		bindService(mServiceIntent, mServiceConnection,
				Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
	}

	private void unbindEMMService() {
		try {
			L.i(AtyEnroll.class, "[SERVICE] Unbind");
			unbindService(mServiceConnection);
		} catch (IllegalArgumentException e) {
			L.e(AtyEnroll.class, "Service wasn't bound!");
		}
	}

	private void onLoginClick() {
		muserId = username_et.getText().toString();
		mpassword = password_et.getText().toString();
		// / 如果为空
		if (TextUtils.isEmpty(muserId)) {
			T.showShort(this, "用户名不能为空");
			username_et.setError("用户名不能为空");
			return;
		}

		// / 如果为空
		if (TextUtils.isEmpty(mpassword)) {
			T.showShort(this, "密码不能为空");
			password_et.setError("密码不能为空");
			return;
		}
		// // 检测登陆超时是否运行, 启动检测线程
		if (mLoginOutTimeProcess != null && !mLoginOutTimeProcess.running)
			mLoginOutTimeProcess.start();
		// / 检测Service call login method
		// / 对话框是否显示
		if (mLoginDialog != null && !mLoginDialog.isShowing()) {
			mLoginDialog.show();
		}

		if (mEMMService != null) {
			mEMMService.loginEMM(muserId, mpassword);
		}
	}

	ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mEMMService = ((EMMService.EMMBinder) service).getService();
			mEMMService.registerConnectionStatusCallback(AtyEnroll.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mEMMService.unRegisterConnectionStatusCallback();
			mEMMService = null;
		}
	};

	// 登录超时处理线程
	class ConnectionOutTimeProcess implements Runnable {
		public boolean running = false;
		private long startTime = 0L;
		private Thread thread = null;

		ConnectionOutTimeProcess() {
		}

		public void run() {
			while (true) {
				if (!this.running)
					return;
				if (System.currentTimeMillis() - this.startTime > 10 * 1000L) {
					mHandler.sendEmptyMessage(LOGIN_OUT_TIME);
				}
				try {
					Thread.sleep(10L);
				} catch (Exception localException) {

				}
			}
		}

		public void start() {
			try {
				this.thread = new Thread(this);
				this.running = true;
				this.startTime = System.currentTimeMillis();
				this.thread.start();
			} finally {
			}
		}

		public void stop() {
			try {
				this.running = false;
				this.thread = null;
				this.startTime = 0L;
			} finally {
			}
		}
	}

	@Override
	public void connectionStatusChanged(int connectedcode, String message) {

		if (mLoginOutTimeProcess != null && mLoginOutTimeProcess.running) {
			mLoginOutTimeProcess.stop();
			mLoginOutTimeProcess = null;
		}
		if (connectedcode == EMMService.CONNECTING) {
			L.i(this.getClass(), "登录中...");
			// / 登录中,如果登录框没有显示则提示用户设备正在注册中...
			if (mLoginDialog != null && !mLoginDialog.isShowing())
				mLoginDialog.show();
		}

		// / 如果登陆成功
		if (connectedcode == EMMService.CONNECTED) {
			L.i(this.getClass(), "登录成功...");
			T.showShort(AtyEnroll.this, "登录成功 " + message);
			if (mLoginDialog != null && mLoginDialog.isShowing())
				mLoginDialog.dismiss();
			startActivity(new Intent(this, AtyMainCamera.class));
			finish();
		}

		if (connectedcode == EMMService.FAILCONNECTED) {
			if (mLoginDialog != null && mLoginDialog.isShowing())
				mLoginDialog.dismiss();
			T.showLong(AtyEnroll.this, "注册失败: " + message);
			L.i(this.getClass(), "注册失败...");
			// / 分为两种情况,1.在线注册失败, 2.离线注册失败..
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		L.i(getClass(), "onDestroy...");
		if (mLoginOutTimeProcess != null) {
			mLoginOutTimeProcess.stop();
			mLoginOutTimeProcess = null;
		}
		unbindEMMService();
	}


	@Override
	public void onBackPressed() {
		super.onBackPressed();
		App.getInstance().exit();
	}

}
