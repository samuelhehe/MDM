package com.foxconn.app.aty;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.ab.view.titlebar.AbTitleBar;
import com.foxconn.app.App;
import com.foxconn.app.R;
import com.foxconn.emm.bean.UpdateInfo;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.utils.HttpClientUtil;
import com.foxconn.emm.utils.ParseXmlUtils;
import com.foxconn.emm.utils.TestNetUtils;
import com.foxconn.emm.utils.ToastUtils;
import com.foxconn.emm.utils.VersionUtil;
import com.foxconn.emm.view.UpdateDialog;
import com.foxconn.emm.view.UpdateProgress;

public class AtyAbout extends EMMBaseActivity implements OnClickListener {
	private ExecutorService updateAppthreads = Executors
			.newSingleThreadExecutor();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.aty_about);
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("关于");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(android.R.color.holo_blue_light);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		initView();
		App.getInstance().addActivity(this);
		
	}

	private void initView() {
		findViewById(R.id.sys_about_checkupdate_ll).setOnClickListener(this);
		findViewById(R.id.sys_about_specification_ll).setOnClickListener(this);
		findViewById(R.id.sys_about_aboutus_ll).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.sys_about_checkupdate_ll:

			if (updateAppthreads.isShutdown()
					|| updateAppthreads.isTerminated()) {
				updateAppthreads = Executors.newSingleThreadExecutor();
			}
			if (TestNetUtils.checkNetWorkInfo(this)) {
				checkUpdate();
			} else {
				ToastUtils.show(this, "网络连接不可用,请检查网络连接");
			}
			break;
		case R.id.sys_about_specification_ll:

			startActivity(new Intent(this, AtySpecification.class));

			break;
		case R.id.sys_about_aboutus_ll:
			startActivity(new Intent(this, AtyAboutus.class));
			break;
		}
	}

	UpdateInfo updatedInfo;

	// // check update version information
	private void checkUpdate() {
		CheckUpdateVersion checkUpdateVersion = new CheckUpdateVersion();
		checkUpdateVersion.execute(AtyAbout.this);
	}

	public void setCheckUpdateTime() {
		EMMPreferences.setAppUpdateTime(AtyAbout.this,
				String.valueOf(new Date().getTime()));
	}

	/**
	 * A runnable task to CheckUpdate APP VERSION IS NEED UDPATE 进行App版本的检查更新,
	 * 如需要更新则进行提醒.
	 * 
	 * 检查更新 ,提醒用户更新
	 */
	protected class CheckUpdateVersion extends
			AsyncTask<Context, String, Boolean> {
		UpdateProgress pd = new UpdateProgress(AtyAbout.this, "正在检测新版本，请稍后！");
		UpdateInfo info = new UpdateInfo();

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected Boolean doInBackground(Context... context) {
			// TODO Auto-generated method stub
			try {
				updatedInfo = ParseXmlUtils.getUpdataInfo(HttpClientUtil
						.getRequestXML(EMMContants.SYSCONF.SYS_APP_UPDATE_URL));
				if (updatedInfo != null) {
					updatedInfo.setLocalVersionCode(VersionUtil
							.getVersionInfo(context[0]).versionCode);
					updatedInfo.setLocalVersionName(VersionUtil
							.getVersionInfo(context[0]).versionName);
					return true;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			AtyAbout.this.setCheckUpdateTime();
			if (result) {
				if (updatedInfo.isNeedUpdate(AtyAbout.this)) {
					pd.dismiss();
					UpdateDialog updateDialog = new UpdateDialog(AtyAbout.this,
							updatedInfo);
					updateDialog.show();
				} else {
					pd.setMessage("恭喜您的系统是最新版本!");
					pd.hideProgress();
					dismissProgress();
				}
			} else {
				pd.setMessage("连接超时，请稍后重试！");
				pd.hideProgress();
				dismissProgress();
			}
		}

		private void dismissProgress() {
			Timer time = new Timer();
			time.schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					pd.dismiss();
				}
			}, 2000);
		}
	}
}
