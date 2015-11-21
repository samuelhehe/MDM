package com.foxconn.app.aty;

import java.io.File;
import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.foxconn.app.App;
import com.foxconn.app.App.onLocationReceiveListener;
import com.foxconn.app.R;
import com.foxconn.emm.bean.UserInfo;
import com.foxconn.emm.dao.DBUserInfoHelper;
import com.foxconn.emm.service.ServiceManager;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.utils.EMMReqParamsUtils;
import com.foxconn.emm.utils.HttpClientUtil;
import com.foxconn.emm.utils.IconImgUtils;
import com.foxconn.emm.utils.ImageUtils;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.utils.T;
import com.foxconn.emm.utils.TextFormater;
import com.foxconn.lib.residemenu.ResideMenu;
import com.foxconn.lib.residemenu.ResideMenuInfo;
import com.foxconn.lib.residemenu.ResideMenuItem;
import com.google.gson.Gson;

public class AtyMain extends EMMBaseActivity implements View.OnClickListener,
		onLocationReceiveListener {

	private ResideMenu resideMenu;
	private ResideMenuItem menu_safepolicy;
	private ResideMenuItem menu_appmgr;
	private ResideMenuItem menu_trafficmgr;
	private ResideMenuItem menu_infocenter;
	private ResideMenuItem menu_downloadqueue;
	private ResideMenuInfo info;

	private boolean is_closed = false;
	private long mExitTime;

	private Button leftMenu;

	private TextView sys_common_residemenu_locaddress_tv,
			sys_common_residemenu_setting_tv;

	private ImageView sys_common_residemenu_setting_iv;

	// // 用户信息
	private TextView home_userinfo_synctime_tv;

	private TextView home_userinfo_userid_tv, home_userinfo_username_tv,
			home_detail_userinfo_bg_content_tv,
			home_detail_userinfo_phoneno_content_tv;

	// / 用户头像
	private ImageView home_userinfo_headicon_iv;

	/**
	 * 刷新按钮. 功能为:点击刷新进行用户信息同步
	 */
	private Button title_bar_right_menu_refresh;

	private App app;

	private DBUserInfoHelper dbUserInfoHelper;

	private UserInfo userInfo;

	private ServiceManager serviceManager;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_main);
		setUpMenu();
		setListener();
		app = App.getInstance();
		app.addActivity(this);
		app.setLocationReceiveListener(this);
		// initView();

		dbUserInfoHelper = new DBUserInfoHelper(this);

		String userId = EMMPreferences.getUserID(this);
		L.d(this.getClass(), "userId " + userId);
		if (!TextUtils.isEmpty(userId)) {
			userInfo = dbUserInfoHelper.findUserByUserId(userId);

			// /userid存在, dB中不存在,从Server端同步...
			if (userInfo == null || TextUtils.isEmpty(userInfo.getUser_id())) {

				// / call sysncUserinfo

			} else {
				L.d(this.getClass(), userInfo.toString());
				initView();
			}
		} else {
			// / 用户信息丢失...提醒用户重新注册...
		}
		serviceManager = new ServiceManager(this);
		serviceManager.checkKeepRunningService();
	}

	private void initView() {

		title_bar_right_menu_refresh = (Button) this
				.findViewById(R.id.title_bar_right_menu_refresh);
		title_bar_right_menu_refresh.setOnClickListener(this);

		home_userinfo_synctime_tv = (TextView) this
				.findViewById(R.id.home_userinfo_synctime_tv);

		home_userinfo_userid_tv = (TextView) this
				.findViewById(R.id.home_userinfo_userid_tv);

		home_userinfo_username_tv = (TextView) this
				.findViewById(R.id.home_userinfo_username_tv);

		home_detail_userinfo_bg_content_tv = (TextView) this
				.findViewById(R.id.home_detail_userinfo_bg_content_tv);

		home_detail_userinfo_phoneno_content_tv = (TextView) this
				.findViewById(R.id.home_detail_userinfo_phoneno_content_tv);

		home_userinfo_headicon_iv = (ImageView) this
				.findViewById(R.id.home_userinfo_headicon_iv);

		home_userinfo_userid_tv.setText("工号：" + userInfo.getUser_id());

		home_userinfo_username_tv.setText("姓名：" + userInfo.getUser_name_chi());

		home_detail_userinfo_bg_content_tv.setText(userInfo.getBu_group());

		home_detail_userinfo_phoneno_content_tv.setText(userInfo.getPhone_no());

		String userInfo_syncTime = EMMPreferences.getUserInfoSyncTime(this);

		if (TextUtils.isEmpty(userInfo_syncTime)) {
			home_userinfo_synctime_tv.setText(TextFormater
					.getCurrentDateStringForHome() + " 同步");
			EMMPreferences.setUserInfoSyncTime(this,
					TextFormater.getCurrentDateStringForHome());
		} else {
			home_userinfo_synctime_tv.setText(userInfo_syncTime + " 同步");
		}
		String lastStreet = EMMPreferences.getDEVICELocationStreet(this);
		if (TextUtils.isEmpty(lastStreet)) {
			sys_common_residemenu_locaddress_tv.setText("获取地址失败,网络连接不可用");
		} else {
			sys_common_residemenu_locaddress_tv.setText(lastStreet);
		}
		try {
			File headimgfile = new File(UserInfo.getHeadIconFilePath(userInfo
					.getUser_id()));
			/** 图片处理 小屏幕默认 96*128 ,为了适配大屏幕 原尺寸*1.5 */
			Bitmap bitmap = ImageUtils.getBitmapFromSDScale(headimgfile, 144,
					192);
			if (bitmap != null) {
				home_userinfo_headicon_iv.setImageBitmap(bitmap);
			} else {
				L.w(this.getClass(),
						"bitmap read is null  sysnc headicon from server  ");
				DownloadHeadIconTask downloadHeadIconTask = new DownloadHeadIconTask();
				downloadHeadIconTask.execute(userInfo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * 同步用户头像. // /0.如果头像从server端没有同步过来,则提醒用户进行拍照 // /1.跳转至拍照界面 //
	 * /2.将拍照上传的flag标识为没有上传.
	 */
	protected final class DownloadHeadIconTask extends
			AsyncTask<UserInfo, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(UserInfo... params) {
			UserInfo headimguserinfo = params[0];
			if (!TextUtils.isEmpty(headimguserinfo.getUserImg())) {
				L.d(this.getClass(), headimguserinfo.getUserImg());

				Bitmap imgFromServer = IconImgUtils
						.getImgFromServer(headimguserinfo.getUserImg());
				// /保存同步图片到本地
				try {
					ImageUtils.saveToLocal(imgFromServer, UserInfo
							.getHeadIconFilePath(headimguserinfo.getUser_id()));
				} catch (IOException e) {
					e.printStackTrace();
				}
				return imgFromServer;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (result != null) {
				L.d(this.getClass(), "" + (result == null));
				home_userinfo_headicon_iv.setImageBitmap(result);
			} else {
				showToast("系统检测到用户照片删除或损坏,请重新拍照");
				EMMPreferences.setHASHEADIMG_UPLOAD(AtyMain.this, false);
				Intent intent = new Intent(AtyMain.this, AtyMainCamera.class);
				startActivity(intent.setFlags(AtyMainCamera.FLAG_RETAKE_PHOTO));
				finish();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void setUpMenu() {
		leftMenu = (Button) findViewById(R.id.title_bar_left_menu);
		// attach to current activity;
		resideMenu = new ResideMenu(this);
		resideMenu.setBackground(R.drawable.enroll_background6);
		resideMenu.attachToActivity(this);
		resideMenu.setMenuListener(menuListener);
		// valid scale factor is between 0.0f and 1.0f. leftmenu'width is
		// 150dip.
		resideMenu.setScaleValue(0.6566f);
		resideMenu.setDirectionDisable(ResideMenu.DIRECTION_RIGHT);

		menu_safepolicy = new ResideMenuItem(this,
				R.drawable.sys_residemenu_safepolicy, "安全政策");
		menu_appmgr = new ResideMenuItem(this,
				R.drawable.sys_residemenu_app_control, "应用管控");
		menu_trafficmgr = new ResideMenuItem(this,
				R.drawable.sys_residemenu_ram, "流量管理");
		menu_infocenter = new ResideMenuItem(this,
				R.drawable.sys_residemenu_timer, "消息中心");
		menu_downloadqueue = new ResideMenuItem(this,
				R.drawable.sys_residemenu_timer, "下载队列");

		resideMenu.addMenuItem(menu_safepolicy, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(menu_infocenter, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(menu_appmgr, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(menu_trafficmgr, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(menu_downloadqueue, ResideMenu.DIRECTION_LEFT);

		info = new ResideMenuInfo(this, R.drawable.icon_profile, "上次同步时间",
				TextFormater.getCurrentDateStringForHome());

		sys_common_residemenu_locaddress_tv = resideMenu
				.getSys_common_residemenu_locaddress_tv();
		sys_common_residemenu_setting_tv = resideMenu
				.getSys_common_residemenu_setting_tv();

		sys_common_residemenu_setting_iv = resideMenu
				.getSys_common_residemenu_setting_iv();

		if (sys_common_residemenu_locaddress_tv != null) {
			sys_common_residemenu_locaddress_tv.setOnClickListener(this);
		}
		if (sys_common_residemenu_setting_tv != null) {
			sys_common_residemenu_setting_tv.setOnClickListener(this);
		}
		if (sys_common_residemenu_setting_iv != null) {
			sys_common_residemenu_setting_iv.setOnClickListener(this);
		}

	}

	private void setListener() {
		resideMenu.addMenuInfo(info);
		menu_safepolicy.setOnClickListener(this);
		menu_appmgr.setOnClickListener(this);
		menu_trafficmgr.setOnClickListener(this);
		menu_infocenter.setOnClickListener(this);
		menu_downloadqueue.setOnClickListener(this);

		info.setOnClickListener(this);
		leftMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
			}
		});
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return resideMenu.dispatchTouchEvent(ev);
	}

	@Override
	public void onClick(View view) {

		if (view == menu_safepolicy) {
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), AtySafePolicy.class);
			startActivity(intent);
		} else if (view == menu_appmgr) {
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), AtyAppLimitMgr.class);
			startActivity(intent);
		} else if (view == menu_trafficmgr) {
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), AtyTrafficMgr.class);
			startActivity(intent);
		} else if (view == menu_infocenter) {
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), AtyInfoCenter.class);
			startActivity(intent);
		} else if (view == menu_downloadqueue) {
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), AtyDLQueue.class);
			startActivity(intent);
		} else if (view == info) {
			// Intent intent = new Intent();
			// intent.setClass(getApplicationContext(),
			// AtyUserInfoDetails.class);
			// startActivity(intent);
		} else if (view.getId() == R.id.sys_common_residemenu_locaddress_tv) {
			// /
			// ToastUtils.show(this, "sys_common_residemenu_locaddress_tv");
		} else if ((view.getId() == R.id.sys_common_residemenu_setting_tv)
				|| (view.getId() == R.id.sys_common_residemenu_setting_iv)) {
			// /
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), AtySettings.class);
			startActivity(intent);
			// / 刷新事件...
		} else if (view.getId() == R.id.title_bar_right_menu_refresh) {

			if (serviceManager != null) {
				serviceManager.checkKeepRunningService();
			} else {
				serviceManager = new ServiceManager(this);
				serviceManager.checkKeepRunningService();
			}
			SYNCUserinfoTask userinfoTask = new SYNCUserinfoTask();
			userinfoTask.execute(userInfo.getUser_id());
		}
	}

	/**
	 * 同步用户信息,存储DB,更新界面.
	 * 
	 */
	private class SYNCUserinfoTask extends AsyncTask<String, Void, UserInfo> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			home_userinfo_synctime_tv.setText("同步中...");
		}

		@Override
		protected UserInfo doInBackground(String... params) {
			String uri = EMMContants.SYSCONF.REQ_USERINFO;
			String entrollData = EMMReqParamsUtils.getSyncUserInfoStr(AtyMain.this,params[0].toString());
			L.d(this.getClass(), entrollData);
			String resultStr = HttpClientUtil.returnData(uri, entrollData);

			L.d(this.getClass(), "request result--- >>>" + resultStr);
			/*
			 * Server端错误,会返回这个字符串
			 * <script>parent.location.href='login.jsp';</script>
			 */
			if (TextUtils.isEmpty(resultStr) || resultStr.contains("<script>")) {
				return null;
			}
			return new Gson().fromJson(resultStr, UserInfo.class);

		}

		@Override
		protected void onPostExecute(UserInfo userInfoFromServer) {
			super.onPostExecute(userInfoFromServer);
			if (userInfoFromServer == null
					|| TextUtils.isEmpty(userInfoFromServer.getUser_id())) {
				home_userinfo_synctime_tv.setText("同步失败");
				// / 记录同步用户信息的时间
				T.show(AtyMain.this, "同步失败,网络连接不可用,请稍后重试.", Toast.LENGTH_LONG);
				EMMPreferences.setUserInfoSyncTime(AtyMain.this,
						TextFormater.getCurrentDateStringForHome());
			} else {
				/**
				 * 对比手机端的头像名称与Server端的头像名称. 不同则进行下载更新.
				 */
				if (!IconImgUtils.compare2ImageName(IconImgUtils
						.getImageNameIncludeType(userInfo.getUserImg()),
						IconImgUtils.getImageNameIncludeType(userInfoFromServer
								.getUserImg()))) {
					DownloadHeadIconTask downloadHeadIconTask = new DownloadHeadIconTask();
					downloadHeadIconTask.execute(userInfoFromServer);
				}
				userInfo = userInfoFromServer;
				dbUserInfoHelper.insertUserInfo(userInfo);
				EMMPreferences.setUserInfoSyncTime(AtyMain.this,
						TextFormater.getCurrentDateStringForHome());
				initView();
			}
		}
	}

	private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
		@Override
		public void openMenu() {
			is_closed = false;
			leftMenu.setVisibility(View.GONE);
		}

		@Override
		public void closeMenu() {
			is_closed = true;
			leftMenu.setVisibility(View.VISIBLE);
		}
	};

	// What good method is to access resideMenu？
	public ResideMenu getResideMenu() {
		return resideMenu;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EMMPreferences.setSecurityNeedAuthorized(getApplicationContext(), true);
		serviceManager.checkKeepRunningService();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (is_closed) {
				if ((System.currentTimeMillis() - mExitTime) > 2000) {
					Toast.makeText(this, "再按一次退出EMM", Toast.LENGTH_SHORT)
							.show();
					mExitTime = System.currentTimeMillis();
				} else {
					L.v(getClass(), "emm exit by manually ");
					finish();
					// app.exit();
					// System.exit(0);
					super.onBackPressed();
				}
			} else {
				if (resideMenu.isOpened()) {
					resideMenu.closeMenu();
				} else {
					is_closed = true;
					L.d(this.getClass(), "is closed set true ...");
				}
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onReceiveLocation(BDLocation bdLocation) {

		if (bdLocation != null) {
			if (sys_common_residemenu_locaddress_tv != null) {
				String addrStr = bdLocation.getAddrStr();
				if (TextUtils.isEmpty(addrStr)) {

					String lastStreet = EMMPreferences
							.getDEVICELocationStreet(this);
					if (TextUtils.isEmpty(lastStreet)) {
						sys_common_residemenu_locaddress_tv
								.setText("获取地址失败,网络连接不可用");
					} else {
						sys_common_residemenu_locaddress_tv.setText(lastStreet);
					}
				} else {
					sys_common_residemenu_locaddress_tv.setText(addrStr);
					EMMPreferences.setDEVICELocationStreet(this, addrStr);
				}
			}
		}
	}

	public TextView getSys_common_residemenu_setting_tv() {
		return sys_common_residemenu_setting_tv;
	}

	public void setSys_common_residemenu_setting_tv(
			TextView sys_common_residemenu_setting_tv) {
		this.sys_common_residemenu_setting_tv = sys_common_residemenu_setting_tv;
	}
}
