package com.foxconn.app.aty;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ab.view.titlebar.AbTitleBar;
import com.foxconn.app.IMessageReceiveCallback;
import com.foxconn.app.R;
import com.foxconn.emm.receiver.EMMMessageCallBackReceiver;
import com.foxconn.emm.service.EMMIntentService;
import com.foxconn.emm.service.EMMMonitorService;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.L;

public class AtyInfoCenter extends EMMBaseActivity implements OnClickListener,
		IMessageReceiveCallback {

	private FrameLayout lay_notification, lay_file, lay_pic_web, lay_app;

	private ImageView img_notification, img_file, img_pic_web, img_app;

	private Fragment notifi_frg, notifi_calendar_frg, file_frg, pic_frg,
			web_frg, app_frg;

	private AbTitleBar mAbTitleBar;

	private Button notification_btn, calendar_btn;

	private int current_frg = 0;
	public static List<IMessageReceiveCallback> messageReceiveListeners = new ArrayList<IMessageReceiveCallback>();
	private BroadcastReceiver messageReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.aty_residemenu_infocenter);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("消息中心");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(android.R.color.holo_blue_light);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		messageReceiver = new EMMMessageCallBackReceiver(this);
		registerOnMessageReceiver();
		initView();
		Intent intent = getIntent();
		String notification_flag_msgtype = intent
				.getStringExtra(EMMIntentService.NOTIFICATION_FLAG_MSGTYPE);

		int[] currentFrgFlag2 = getCurrentFrgFlag(notification_flag_msgtype);
		if (currentFrgFlag2[0] != 0) {
			turn(currentFrgFlag2);
		} else {
			addCenterView(R.id.lay_notification);
			current_frg = R.id.lay_notification;
		}
	}

	private void registerOnMessageReceiver() {
		L.d(EMMMonitorService.class, "registerOnMessageReceiver()...");
		IntentFilter filter = new IntentFilter();
		filter.addAction(EMMMessageCallBackReceiver.EMM_INFOCENTER_ACTION);
		registerReceiver(messageReceiver, filter);
	}

	private void unregisterOnMessageReceiver() {
		L.d(EMMMonitorService.class, "unregisterOnMessageReceiver()...");
		unregisterReceiver(messageReceiver);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		String notification_flag_msgtype = intent
				.getStringExtra(EMMIntentService.NOTIFICATION_FLAG_MSGTYPE);

		int[] currentFrgFlag2 = getCurrentFrgFlag(notification_flag_msgtype);
		if (currentFrgFlag2[0] != 0) {
			turn(currentFrgFlag2);
		}
	}

	private void initView() {
		lay_notification = (FrameLayout) findViewById(R.id.lay_notification);
		lay_file = (FrameLayout) findViewById(R.id.lay_file_img);
		lay_pic_web = (FrameLayout) findViewById(R.id.lay_web);
		lay_app = (FrameLayout) findViewById(R.id.lay_app);

		img_notification = (ImageView) findViewById(R.id.img_notification);
		img_file = (ImageView) findViewById(R.id.img_file);
		img_pic_web = (ImageView) findViewById(R.id.img_pic_web);
		img_app = (ImageView) findViewById(R.id.img_app);

		notifi_frg = (Fragment) getSupportFragmentManager().findFragmentById(
				R.id.notifi_frg);
		notifi_calendar_frg = (Fragment) getSupportFragmentManager()
				.findFragmentById(R.id.notifi_calendar_frg);
		file_frg = (Fragment) getSupportFragmentManager().findFragmentById(
				R.id.file_frg);
		pic_frg = (Fragment) getSupportFragmentManager().findFragmentById(
				R.id.pic_frg);
		web_frg = (Fragment) getSupportFragmentManager().findFragmentById(
				R.id.web_frg);
		app_frg = (Fragment) getSupportFragmentManager().findFragmentById(
				R.id.app_frg);

		img_notification.setSelected(true);
		lay_notification.setOnClickListener(this);
		lay_file.setOnClickListener(this);
		lay_pic_web.setOnClickListener(this);
		lay_app.setOnClickListener(this);

	}

	/**
	 * 添加标题中间按钮
	 */
	private void addCenterView(int id) {
		View centerViewNotification = mInflater.inflate(
				R.layout.notification_tab_header_left, null);
		notification_btn = (Button) centerViewNotification
				.findViewById(R.id.notification_btn);
		notification_btn.setSelected(true);
		notification_btn.setOnClickListener(this);
		View centerViewCalendar = mInflater.inflate(
				R.layout.calendar_tab_header_right, null);
		calendar_btn = (Button) centerViewCalendar
				.findViewById(R.id.calendar_btn);
		calendar_btn.setOnClickListener(this);
		mAbTitleBar.clearCenterView();
		mAbTitleBar.addCenterView(centerViewNotification);
		mAbTitleBar.addCenterView(centerViewCalendar);
		switch (id) {
		case R.id.lay_notification:
			notification_btn.setText("通知");
			calendar_btn.setText("日历");
			break;

		case R.id.lay_file_img:
			notification_btn.setText("文件");
			calendar_btn.setText("图片");
			break;
		}
	}

	/**
	 * 
	 * @param currentFrgFlag
	 */
	private void turn(int[] currentFrgFlag) {

		switch (currentFrgFlag[0]) {
		case R.id.lay_notification:
			getSupportFragmentManager().beginTransaction().show(notifi_frg)
					.commit();
			getSupportFragmentManager().beginTransaction()
					.hide(notifi_calendar_frg).commit();
			getSupportFragmentManager().beginTransaction().hide(file_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(web_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(app_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(pic_frg)
					.commit();
			img_notification.setSelected(true);
			img_file.setSelected(false);
			img_pic_web.setSelected(false);
			img_app.setSelected(false);

			addCenterView(R.id.lay_notification);
			current_frg = R.id.lay_notification;
			changeFrg(currentFrgFlag[1]);

			break;

		case R.id.lay_file_img:
			getSupportFragmentManager().beginTransaction().hide(notifi_frg)
					.commit();
			getSupportFragmentManager().beginTransaction()
					.hide(notifi_calendar_frg).commit();
			getSupportFragmentManager().beginTransaction().show(file_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(web_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(app_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(pic_frg)
					.commit();
			img_notification.setSelected(false);
			img_file.setSelected(true);
			img_pic_web.setSelected(false);
			img_app.setSelected(false);
			mAbTitleBar.clearCenterView();
			current_frg = R.id.lay_file_img;
			addCenterView(R.id.lay_file_img);
			changeFrg(currentFrgFlag[1]);
			break;

		case R.id.lay_web:
			getSupportFragmentManager().beginTransaction().hide(notifi_frg)
					.commit();
			getSupportFragmentManager().beginTransaction()
					.hide(notifi_calendar_frg).commit();
			getSupportFragmentManager().beginTransaction().hide(file_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().show(web_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(app_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(pic_frg)
					.commit();
			img_notification.setSelected(false);
			img_file.setSelected(false);
			img_pic_web.setSelected(true);
			img_app.setSelected(false);
			mAbTitleBar.clearCenterView();
			current_frg = 0;
			break;

		case R.id.lay_app:
			getSupportFragmentManager().beginTransaction().hide(notifi_frg)
					.commit();
			getSupportFragmentManager().beginTransaction()
					.hide(notifi_calendar_frg).commit();
			getSupportFragmentManager().beginTransaction().hide(file_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(web_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().show(app_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(pic_frg)
					.commit();

			img_notification.setSelected(false);
			img_file.setSelected(false);
			img_pic_web.setSelected(false);
			img_app.setSelected(true);
			mAbTitleBar.clearCenterView();
			current_frg = 0;
			break;
		}

	}

	/**
	 * 
	 * 
	 * 
	 * @param msgTypeflag
	 * @return
	 */
	private int[] getCurrentFrgFlag(String msgTypeflag) {
		if (TextUtils.isEmpty(msgTypeflag)
				|| msgTypeflag.equalsIgnoreCase("null")) {
			return new int[2];
		}
		int[] frg_flag = new int[2];
		if (EMMContants.MsgType.SENDNOTIFICATION.equalsIgnoreCase(msgTypeflag)) {
			frg_flag[0] = R.id.lay_notification;
			return frg_flag;
		}
		if (EMMContants.MsgType.SENDCALENDAR.equalsIgnoreCase(msgTypeflag)) {
			frg_flag[0] = R.id.lay_notification;
			frg_flag[1] = R.id.calendar_btn;
			return frg_flag;
		}
		if (EMMContants.MsgType.SENDFILE.equalsIgnoreCase(msgTypeflag)) {
			frg_flag[0] = R.id.lay_file_img;
			return frg_flag;
		}

		if (EMMContants.MsgType.SENDIMAGE.equalsIgnoreCase(msgTypeflag)) {
			frg_flag[0] = R.id.lay_file_img;
			frg_flag[1] = R.id.calendar_btn;
			return frg_flag;
		}
		if (EMMContants.MsgType.SENDAPPS.equalsIgnoreCase(msgTypeflag)) {
			frg_flag[0] = R.id.lay_app;
			return frg_flag;
		}
		if (EMMContants.MsgType.SENDPAGE.equalsIgnoreCase(msgTypeflag)) {
			frg_flag[0] = R.id.lay_web;
			return frg_flag;
		}
		return new int[2];
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.lay_notification:
			getSupportFragmentManager().beginTransaction().show(notifi_frg)
					.commit();
			getSupportFragmentManager().beginTransaction()
					.hide(notifi_calendar_frg).commit();
			getSupportFragmentManager().beginTransaction().hide(file_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(web_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(app_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(pic_frg)
					.commit();
			img_notification.setSelected(true);
			img_file.setSelected(false);
			img_pic_web.setSelected(false);
			img_app.setSelected(false);

			addCenterView(R.id.lay_notification);
			current_frg = R.id.lay_notification;
			break;

		case R.id.lay_file_img:
			getSupportFragmentManager().beginTransaction().hide(notifi_frg)
					.commit();
			getSupportFragmentManager().beginTransaction()
					.hide(notifi_calendar_frg).commit();
			getSupportFragmentManager().beginTransaction().show(file_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(web_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(app_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(pic_frg)
					.commit();
			img_notification.setSelected(false);
			img_file.setSelected(true);
			img_pic_web.setSelected(false);
			img_app.setSelected(false);
			mAbTitleBar.clearCenterView();
			current_frg = R.id.lay_file_img;
			addCenterView(R.id.lay_file_img);
			break;

		case R.id.lay_web:
			getSupportFragmentManager().beginTransaction().hide(notifi_frg)
					.commit();
			getSupportFragmentManager().beginTransaction()
					.hide(notifi_calendar_frg).commit();
			getSupportFragmentManager().beginTransaction().hide(file_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().show(web_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(app_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(pic_frg)
					.commit();
			img_notification.setSelected(false);
			img_file.setSelected(false);
			img_pic_web.setSelected(true);
			img_app.setSelected(false);
			mAbTitleBar.clearCenterView();
			current_frg = 0;
			break;

		case R.id.lay_app:
			getSupportFragmentManager().beginTransaction().hide(notifi_frg)
					.commit();
			getSupportFragmentManager().beginTransaction()
					.hide(notifi_calendar_frg).commit();
			getSupportFragmentManager().beginTransaction().hide(file_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(web_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().show(app_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(pic_frg)
					.commit();

			img_notification.setSelected(false);
			img_file.setSelected(false);
			img_pic_web.setSelected(false);
			img_app.setSelected(true);
			mAbTitleBar.clearCenterView();
			current_frg = 0;
			break;

		case R.id.notification_btn:
			changeFrg(R.id.notification_btn);

			break;

		case R.id.calendar_btn:
			changeFrg(R.id.calendar_btn);
			break;
		}
	}

	private void changeFrg(int id) {
		switch (current_frg) {
		case R.id.lay_notification:
			if (id == R.id.notification_btn) {
				getSupportFragmentManager().beginTransaction().show(notifi_frg)
						.commit();
				getSupportFragmentManager().beginTransaction()
						.hide(notifi_calendar_frg).commit();
				notification_btn.setSelected(true);
				calendar_btn.setSelected(false);
			} else if (id == R.id.calendar_btn) {
				getSupportFragmentManager().beginTransaction().hide(notifi_frg)
						.commit();
				getSupportFragmentManager().beginTransaction()
						.show(notifi_calendar_frg).commit();
				notification_btn.setSelected(false);
				calendar_btn.setSelected(true);
			}
			break;
		case R.id.lay_file_img:
			if (id == R.id.notification_btn) {
				getSupportFragmentManager().beginTransaction().show(file_frg)
						.commit();
				getSupportFragmentManager().beginTransaction().hide(pic_frg)
						.commit();
				notification_btn.setSelected(true);
				calendar_btn.setSelected(false);
			} else if (id == R.id.calendar_btn) {
				getSupportFragmentManager().beginTransaction().hide(file_frg)
						.commit();
				getSupportFragmentManager().beginTransaction().show(pic_frg)
						.commit();
				notification_btn.setSelected(false);
				calendar_btn.setSelected(true);
			}
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterOnMessageReceiver();
		messageReceiveListeners.clear();
	}

	public void onMessageReceived(String messageType) {
		if (messageReceiveListeners.size() > 0) {
			for (IMessageReceiveCallback receiveCallback : messageReceiveListeners) {
				receiveCallback.onMessageReceived(messageType);
			}
		}
	}
}
