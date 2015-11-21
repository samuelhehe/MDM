package com.foxconn.emm.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.foxconn.app.R;
import com.foxconn.app.aty.AtyUpdate;
import com.foxconn.emm.bean.UpdateInfo;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.utils.ToastUtils;

public class UpdateDialog extends Dialog implements
		android.view.View.OnClickListener {

	private static final int DOWNLOAD_PREPARE = 0; // / 下载之前
	private static final int DOWNLOAD_WORK = 1; // / 下载中
	private static final int DOWNLOAD_OK = 2; // / 下载完成
	private static final int DOWNLOAD_ERROR = 3; // // 下载出错
	private static final int DOWNLOAD_CLOSE = 4;

	private static final String TAG = "UpdateDialog";
	private Context mContext;
	private TextView sys_updating_progress_status_tv;
	private TextView button1; // // ok
	private ProgressBar pb; // // progress bar
	private TextView button2; // / cancel
	private UpdateInfo updateInfo; // / updateinfo

	private String filePath;

	private String getPathDir;

	public UpdateDialog(Context context, UpdateInfo updateInfo) {
		super(context, R.style.Theme_CustomDialog);
		this.mContext = context;
		this.updateInfo = updateInfo;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sys_common_updating_dialog);
		initView();
		initData();
	}

	// / 初始化数据,初始化UI
	private void initData() {

		if (updateInfo != null && !TextUtils.isEmpty(updateInfo.getUrl())) {
			sys_updating_progress_status_tv.setText("当前版本: "
					+ updateInfo.getLocalVersionName() + "\n最新版本: "
					+ updateInfo.getVersionName() + "\n新版本描述: "
					+ updateInfo.getDescription());
			button1.setText("安全更新");
			button1.setTag(DOWNLOAD_PREPARE);
			button2.setText("取消");
			pb.setVisibility(View.GONE);

			setFilePath(getUpdateAppDirPath() + mContext.getPackageName()
					+ updateInfo.getVersionName() + ".apk");

		} else {
			sys_updating_progress_status_tv.setText("获取更新信息失败,稍后请重试");
			sys_updating_progress_status_tv.setTextColor(mContext
					.getResources().getColor(R.color.ems_red));
			button1.setTag(DOWNLOAD_CLOSE);
			button1.setText("确定");
			button2.setText("取消");
			pb.setVisibility(View.GONE);
		}
	}

	/**
	 * handler
	 */
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOAD_PREPARE:
				Toast.makeText(mContext, "准备下载", Toast.LENGTH_SHORT).show();
				break;
			case DOWNLOAD_WORK:
				sys_updating_progress_status_tv.setText("正在下载更新文件..."
						+ msg.arg1 + "%");
				pb.setVisibility(View.VISIBLE);
				button1.setText("下载中");
				button1.setTag(DOWNLOAD_WORK);
				button1.setClickable(false);
				button2.setText("取消");
				break;

			case DOWNLOAD_OK:
				sys_updating_progress_status_tv.setText("更新文件已下载完成,请点击进行安装");
				pb.setVisibility(View.GONE);
				button1.setText("安装");
				button1.setTag(DOWNLOAD_OK);
				button1.setClickable(true);
				button2.setText("取消");
				Toast.makeText(mContext, "下载成功", Toast.LENGTH_SHORT).show();
				break;
			case DOWNLOAD_ERROR:

				sys_updating_progress_status_tv.setText("获取更新文件失败,稍后请重试");
				sys_updating_progress_status_tv.setTextColor(mContext
						.getResources().getColor(R.color.ems_red));
				button1.setTag(DOWNLOAD_ERROR);
				button1.setText("确定");

				button2.setText("取消");
				pb.setVisibility(View.GONE);
				Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
				break;
			}
			super.handleMessage(msg);
		}
	};
	protected int fileSize;

	/**
	 * 获取更新App的路径.
	 * 
	 * @return
	 */
	public String getUpdateAppDirPath() {
		String appPath = EMMContants.LocalConf.getEMMLocalHost_dirPath()
				+ EMMContants.LocalConf.UpdateApp_dirpath;
		if (!new File(appPath).exists()) {
			new File(appPath).mkdirs();
		}
		return appPath;
	}

	private void initView() {
		sys_updating_progress_status_tv = (TextView) findViewById(R.id.sys_updating_progress_status_tv);
		button1 = (TextView) findViewById(R.id.button1);
		button1.setOnClickListener(this);
		button2 = (TextView) findViewById(R.id.button2);
		button2.setOnClickListener(this);
		pb = (ProgressBar) findViewById(R.id.sys_updating_pb);
		pb.setVisibility(View.GONE);
	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.button1:

			buttonOper();

			break;

		case R.id.button2:
			cancel();
			break;
		}
	}

	@Override
	public void cancel() {
		super.cancel();
		// cleanFile();/// 残余,下载完成的文件清除
		
		if(this.mContext instanceof AtyUpdate){
			AtyUpdate atyUpdate = (AtyUpdate) this.mContext;
			atyUpdate.finish();
		}
		
		
	}

	private void buttonOper() {

		int button1Tag = (Integer) button1.getTag();
		switch (button1Tag) {
		case DOWNLOAD_PREPARE:

			// / 初始化状态 点击后启动下载线程.progressbar 动态显示. 文字变化
			new Thread(runnable).start();
			// / 主线程中发消息
			sendMessage(DOWNLOAD_WORK);
			break;
		case DOWNLOAD_WORK:
			ToastUtils.show(mContext, "下载中,等一下就好...");
			break;

		case DOWNLOAD_CLOSE:
			cancel();
			break;

		case DOWNLOAD_OK:

			// / download complete

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(getFilePath())),
					"application/vnd.android.package-archive");
			mContext.startActivity(intent);

			cancel();
			break;
		case DOWNLOAD_ERROR:

			cancel();
			break;

		}

	}

	private void cleanFile() {
		File file = new File(getFilePath());
		file.delete();
	}

	Runnable runnable = new Runnable() {

		@Override
		public void run() {

			downloadFile();
		}

		/**
		 * 下载文件
		 */
		private void downloadFile() {
			try {
				if (TextUtils.isEmpty(updateInfo.getUrl())) {
					sendMessage(DOWNLOAD_ERROR);
					return;
				}
				URL u = new URL(updateInfo.getUrl());
				URLConnection conn = u.openConnection();
				InputStream is = conn.getInputStream();
				fileSize = conn.getContentLength();
				if (fileSize < 1 || is == null) {
					sendMessage(DOWNLOAD_ERROR);
					return;
				} else {
					// sendMessage(DOWNLOAD_PREPARE);
					if (TextUtils.isEmpty(getFilePath())) {
						sendMessage(DOWNLOAD_ERROR);
						return;
					}
					FileOutputStream fos = new FileOutputStream(getFilePath());
					byte[] bytes = new byte[1024];
					int len = -1;
					int downloadSize = 0;
					while ((len = is.read(bytes)) != -1) {
						fos.write(bytes, 0, len);
						fos.flush();
						downloadSize += len;

						L.v(TAG, "文件下载中>>>>"
								+ (downloadSize * 100 / fileSize));
						sendMessage(DOWNLOAD_WORK,
								(downloadSize * 100 / fileSize));
					}
					sendMessage(DOWNLOAD_OK);
					is.close();
					fos.close();
				}
			} catch (Exception e) {
				sendMessage(DOWNLOAD_ERROR);
				e.printStackTrace();
			}
		}

	};

	private void sendMessage(int what, int percent) {

		Message m = Message.obtain(handler);
		m.what = what;
		m.arg1 = percent;
		handler.sendMessage(m);
	}

	/**
	 * @param what
	 */
	private void sendMessage(int what) {
		Message m = Message.obtain(handler);
		m.what = what;
		handler.sendMessage(m);
	}

	public String getGetPathDir() {
		return getPathDir;
	}

	public void setGetPathDir(String getPathDir) {
		this.getPathDir = getPathDir;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
