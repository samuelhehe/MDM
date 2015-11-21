package com.foxconn.emm.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings.System;
import android.text.TextUtils;

import com.foxconn.emm.bean.SysLicenseResult;
import com.foxconn.emm.bean.UpdateInfo;
import com.foxconn.emm.bean.UserInfo;
import com.foxconn.emm.tools.PolicyControl;
import com.foxconn.emm.tools.UploadHeadImgTools;
import com.foxconn.emm.utils.ChangeLog;
import com.foxconn.emm.utils.DateUtil;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.utils.EMMReqParamsUtils;
import com.foxconn.emm.utils.HttpClientUtil;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.utils.NetUtil;
import com.foxconn.emm.utils.NetUtil.NetWorkState;
import com.foxconn.emm.utils.ParseXmlUtils;
import com.foxconn.emm.utils.TextFormater;
import com.google.gson.Gson;

/**
 * 用于协助EMMService进行任务处理的Mgr 10-4.301.注册设备 a.在线注册 b.离线注册 2.获取个人信息.将信息存储到DB中.
 * 3.检查是否上传拍照,没有就上传拍照. 4.检查更新 ,提醒用户更新 5.检查NotificationService的状态,如果停止则启动.
 * 6.检查EMMMonitorService的状态,如果停止则启动. 7.同步Policy /// 同步时间: 指令同步, 注册时同步,网络切换同步.
 * 8.同步设备信息 ,软硬件信息 /// 同步时间: 注册同步,指令同步, 9.修改设备编号 /// 10.同步设备流量信息,以及设备App的流量信息
 * 同步时间: 指令同步, 网络切换同步. 11.同步当前的位置信息(这个可以考虑分离出来, 两处调用. 即时更新,指令更新) /// 指令同步.
 * 12.同步黑白限制名单. /// 指令同步 . 13.同步策略 设备流量限制设定同步 ///注册同步 网络切换, 指令同步.
 * 
 */
public class EMMProcessManager {

	private EMMService emmService;
	private ArrayList<Runnable> taskList;
	private boolean running = false;
	private Future<?> futureTask;
	private EMMService.TaskSubmitter taskSubmitter;

	private EMMService.TaskTracker taskTracker;

	private SharedPreferences sharedPrefs;

	private PolicyControl policyControl;

	public EMMProcessManager(EMMService emmService) {
		this.emmService = emmService;
		taskList = new ArrayList<Runnable>();
		taskSubmitter = emmService.getTaskSubmitter();
		taskTracker = emmService.getTaskTracker();
		sharedPrefs = emmService.getSharedPrefs();
		policyControl = emmService.getPolicyControl();
	}

	/**
	 * 放置一些常态任务.
	 * 
	 */
	public void startUpdateProcess() {
		/**
		 * 判断License 是否可用
		 */
		submitCheckUpdateTask();
		if (!EMMPreferences.getDeviceEnrolled(emmService.getContext())
				.equalsIgnoreCase(EMMContants.EMMPrefContant.ENROLL_STATUS_N)) {
			submitSyncPolicyTask();
			submitSyncDeviceTrafficLimitInfoTask();
			if (EMMPreferences.getIsLicenseEnable(getEmmService().getContext())) {

				submitSysLicenseValidateTask();
			}
		}
		this.runTask();
	}

	/**
	 * 放置一些常态任务.
	 * 
	 */
	public void startLoginProcess() {
		submitSyncPolicyTask();
		if (new ChangeLog(emmService.getContext()).firstRunEver())
			submitSyncDeviceSoftwareTask();
		this.runTask();
	}

	public void stopProcess() {

	}

	/**
	 * 应用检查更新策略是 上次检查日期,与这次检查日期对比如果小于一天.则不进行检查更新.
	 */
	private void submitCheckUpdateTask() {
		L.d(this.getClass(), "CheckUpdateTask()...");
		addTask(new CheckUpdateTask());

	}

	/**
	 * 同步Policy /// 同步时间: 指令同步, 注册时同步, 网络切换同步.
	 */
	public void submitSyncPolicyTask() {
		L.d(this.getClass(), "SyncPolicyTask()...");
		addTask(new SyncPolicyTask());
	}

	/**
	 * 上传用户照片 判断如果用户的头像没有上传则进行上传头像task
	 */
	public void submitUploadImgTask() {
		L.d(this.getClass(), "UploadImgTask()...");
		addTask(new UpLoadImgTask());
	}

	public void submitSyncDeviceSoftwareTask() {

		L.d(this.getClass(), "SyncDeviceSoftwareTask()...");
		addTask(new SyncDeviceSoftwareTask());
	}

	public void submitSyncDeviceTrafficLimitInfoTask() {

		L.d(this.getClass(), "DeviceTrafficLimitTask()...");
		addTask(new DeviceTrafficLimitTask());
	}

	public void submitSysLicenseValidateTask() {

		L.d(this.getClass(), "SysLicenseValidateTask()...");
		addTask(new SysLicenseValidateTask());
	}

	/**
	 * A runnable task to CheckUpdate APP VERSION IS NEED UDPATE 进行App版本的检查更新,
	 * 如需要更新则进行提醒.
	 * 
	 * 检查更新 ,提醒用户更新
	 */
	private class CheckUpdateTask implements Runnable {
		final EMMProcessManager emmProcessManager;

		private CheckUpdateTask() {
			this.emmProcessManager = EMMProcessManager.this;
		}

		public void run() {
			L.i(this.getClass(), "CheckUpdateTask.run()...");
			try {
				if (emmProcessManager.isNeedCheckUpdate()) {
					if (NetUtil.checkNetWorkInfo(getEmmService()
							.getApplication())) {
						String requestUrl = EMMContants.SYSCONF.SYS_APP_UPDATE_URL;
						UpdateInfo updateInfo = ParseXmlUtils
								.getUpdataInfo(HttpClientUtil
										.getRequestXML(requestUrl));
						if (updateInfo
								.isNeedUpdate(emmService.getApplication())) {
							emmService.notifyUpdateInfo("10010_10086",
									"EMM系统更新提醒",
									"系统有版本更新: " + updateInfo.getVersionName(),
									true, updateInfo);
						}
						L.d(this.getClass(), "" + updateInfo.toString());
						// / 记录本次完成检查更新时间
						emmProcessManager.setCheckUpdateTime();
					} else {
						L.i(this.getClass(),
								"CheckUpdateTask.run() is not need udpate.  return .. ");
					}
				} else {
					L.i(this.getClass(),
							"CheckUpdateTask.run() is not need udpate.  return .. ");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				emmProcessManager.runTask();
			}
		}
	}

	/**
	 * 同步Policy /// 同步时间: 指令同步, 注册时同步, 网络切换同步.
	 */
	private class SyncPolicyTask implements Runnable {

		final EMMProcessManager emmProcessManager;

		private SyncPolicyTask() {
			this.emmProcessManager = EMMProcessManager.this;
		}

		public void run() {
			L.i(this.getClass(), "SyncPolicyTask.run()...");

			if (NetUtil.checkNetWorkInfo(getEmmService().getApplication())) {
				if (EMMPreferences.getDeviceEnrolled(emmService.getContext())
						.equalsIgnoreCase(
								EMMContants.EMMPrefContant.ENROLL_STATUS_N)) {
					emmProcessManager.runTask();
					return;
				}
				String reqUrl = EMMContants.SYSCONF.SYNCPOLICY_MAC;
				String params = EMMReqParamsUtils
						.getSyncPolicyStr(getEmmService());
				String returnString = HttpClientUtil.returnData(reqUrl, params);
				L.d(this.getClass(), returnString);

				if (TextUtils.isEmpty(returnString)
						|| returnString.equalsIgnoreCase("null")) {
					emmProcessManager.runTask();
					return;
				} else {
					if (returnString.contains("false")) {
						if (policyControl == null) {
							policyControl = emmService.getPolicyControl();
						}
						EMMPreferences.setPolicyData(emmService, "");
						policyControl.enableDefaultPolicy();
						L.i(this.getClass(),
								"syscPolicy enable defpolicy complete !");
						emmProcessManager.runTask();
						return;
					}
					EMMPreferences.setPolicyData(emmService, returnString);
					if (policyControl == null) {
						policyControl = emmService.getPolicyControl();
					}
					policyControl.enablePolicy(returnString);
					L.i(this.getClass(), "syscPolicy enable policy complete !");
				}
			} else {
				L.d(this.getClass(), "Network is error , enable local policy ");
				policyControl.enablePolicy(EMMPreferences
						.getPolicyData(getEmmService()));
			}
			emmProcessManager.runTask();
		}
	}

	/**
	 * 上传用户照片 判断如果用户的头像没有上传则进行上传头像task
	 */
	private class UpLoadImgTask implements Runnable {

		final EMMProcessManager emmProcessManager;

		private UpLoadImgTask() {
			this.emmProcessManager = EMMProcessManager.this;
		}

		public void run() {
			if (!(new NetUtil(getEmmService().getApplication())
					.getConnectState() == NetWorkState.NONE)) {
				L.i(this.getClass(), "UpLoadImgTask.run()...");
				String userId = EMMPreferences.getUserID(getEmmService());
				String headImgFilepath;
				try {
					headImgFilepath = UserInfo.getHeadIconFilePath(userId);
					if (UserInfo.hasHeadIcon(headImgFilepath, userId)) {
						UploadHeadImgTools.uploadImgToServer2(headImgFilepath,
								UserInfo.getHeadIconName(userId), userId);
					}
					EMMPreferences.setHASHEADIMG_UPLOAD(getEmmService(), true);
				} catch (IOException e) {
					L.i(this.getClass(),
							"headimg upload error " + e.getMessage());
					EMMPreferences.setHASHEADIMG_UPLOAD(getEmmService(), false);
					e.printStackTrace();
				}
				emmProcessManager.runTask();
			}
		}
	}

	/**
	 * 同步设备信息 ,软硬件信息 /// 同步时间: 注册同步,指令同步,
	 */
	protected class SyncDeviceSoftwareTask implements Runnable {
		final EMMProcessManager emmProcessManager;

		private SyncDeviceSoftwareTask() {
			this.emmProcessManager = EMMProcessManager.this;
		}

		@Override
		public void run() {
			if (EMMPreferences.getDeviceEnrolled(emmService.getContext())
					.equalsIgnoreCase(
							EMMContants.EMMPrefContant.ENROLL_STATUS_N)) {
				emmProcessManager.runTask();
				return;
			}
			if (!(new NetUtil(getEmmService().getApplication())
					.getConnectState() == NetWorkState.NONE)) {

				String requrl = EMMContants.SYSCONF.SYNC_DEVICE_SOFTWARE;
				String params = EMMReqParamsUtils
						.getSyncDeviceSoftWareStr(emmService.getApplication());
				String returnData = HttpClientUtil.returnData(requrl, params);
				L.d(this.getClass(), returnData);
				emmProcessManager.runTask();
			}
		}

	}

	protected class DownloadUpdateApk extends
			AsyncTask<UpdateInfo, Integer, String> {
		/**
		 * 下载网络文件到SD卡中.如果SD中存在同名文件将不再下载
		 * 
		 * @param url
		 *            要下载文件的网络地址
		 * @return 下载好的本地文件地址
		 */
		public String downFileToSD(String url, String dirPath, String fileName) {
			InputStream in = null;
			FileOutputStream fileOutputStream = null;
			HttpURLConnection connection = null;
			String downFilePath = null;
			File file = null;
			try {
				if (!EMMContants.LocalConf.isSdPresent()) {
					return null;
				}
				// 先判断SD卡中有没有这个文件，不比较后缀部分比较
				File parentFile = new File(dirPath);
				File[] files = parentFile.listFiles();
				for (int i = 0; i < files.length; ++i) {
					String fns = files[i].getName();
					String name = fns.substring(0, fns.lastIndexOf("."));
					if (name.equals(fileName)) {
						// 文件已存在
						return files[i].getPath();
					}
				}
				URL mUrl = new URL(url);
				connection = (HttpURLConnection) mUrl.openConnection();
				connection.connect();
				// 获取文件名，下载文件
				file = new File(dirPath, fileName);
				downFilePath = file.getPath();
				if (!file.exists()) {
					file.createNewFile();
				} else {
					// 文件已存在
					return file.getPath();
				}
				in = connection.getInputStream();
				fileOutputStream = new FileOutputStream(file);
				byte[] b = new byte[1024];
				int temp = 0;
				while ((temp = in.read(b)) != -1) {

					fileOutputStream.write(b, 0, temp);
				}
			} catch (Exception e) {
				e.printStackTrace();
				L.e(this.getClass(), "文件下载出错了");
				// 检查文件大小,如果文件为0B说明网络不好没有下载成功，要将建立的空文件删除
				file.delete();
				downFilePath = null;
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if (connection != null) {
						connection.disconnect();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return downFilePath;
		}

		@Override
		protected String doInBackground(UpdateInfo... params) {
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}
	}

	/**
	 * 同步设备流量的限制信息.
	 * 
	 * 
	 * { "traffic": { "trafficLimitId": "WEM00000000000000044",
	 * "trafficLimitDesc": "测试", "trafficLimitType": "0", "trafficLimit": "400",
	 * "trafficStatus": "0", "sendTime": "2014-09-03 14:22:12", "createBy":
	 * "张三", "createDate": "2014-08-27 14:24:11", "modifyBy": "", "modifyDate":
	 * "" }, "success": "true" }
	 * 
	 * 
	 * 
	 */
	protected class DeviceTrafficLimitTask implements Runnable {

		final EMMProcessManager emmProcessManager;

		private DeviceTrafficLimitTask() {
			this.emmProcessManager = EMMProcessManager.this;
		}

		@Override
		public void run() {
				if (!(new NetUtil(getEmmService().getApplication())
						.getConnectState() == NetWorkState.NONE)) {
					String requrl = EMMContants.SYSCONF.SYNC_TRAFFICINFO_LIMIT;
					String params = EMMReqParamsUtils
							.getSyncTrafficInfoLimit(emmService
									.getApplication());
					String returnData = HttpClientUtil.returnData(requrl,
							params);
					try {
						JSONObject jsonObject = new JSONObject(returnData);
						String success = jsonObject.getString("success");
						L.d(this.getClass(), returnData);
						if ("true".equalsIgnoreCase(success)) {
							EMMPreferences.setTrafficInfoLimit(
									emmService.getApplication(), returnData);
							L.d(this.getClass(),
									" set device traffic limit succes ");
						}

					} catch (JSONException e) {
						L.d(this.getClass(),
								"device traffic limit parse error ");
						e.printStackTrace();
					} finally {
						emmProcessManager.runTask();
					}
				}
		}
	}

	protected class SysLicenseValidateTask implements Runnable {

		final EMMProcessManager emmProcessManager;

		private SysLicenseValidateTask() {
			this.emmProcessManager = EMMProcessManager.this;
		}

		@Override
		public void run() {
			try {
					if (!(new NetUtil(getEmmService().getApplication()).getConnectState() == NetWorkState.NONE)) {
						String url = EMMContants.SYSCONF.SYS_LICENSE_USUAL_CHECKACTION;
						String reqparams = EMMReqParamsUtils
								.getSysLicenseUsualValidate(getEmmService());
						if (TextUtils.isEmpty(reqparams)) {
							L.d(getClass(), "req params is  null ");
							return;
						}
						String returnData = HttpClientUtil.returnData(url,
								reqparams);
						L.d(this.getClass(), returnData);
						if (TextFormater.isEmpty(returnData)) {
							L.d(getClass(), "returndate is  null ");
							return;
						}
						SysLicenseResult result = new Gson().fromJson(
								returnData, SysLicenseResult.class);
						if (result != null) {
							if (result.getSuccess().equalsIgnoreCase(
									SysLicenseResult.tag_success_success)) {
								save2SP(result);
							} else if (result.getSuccess().equalsIgnoreCase(
									SysLicenseResult.tag_success_fail)) {
								if (result.getCode().equalsIgnoreCase(
										SysLicenseResult.tag_code_fail_expire)) {
									save2SP(result);
									L.d(getClass(), "tag_code_fail_expire");
								} else if (result.getCode().equalsIgnoreCase(
										SysLicenseResult.tag_code_fail_invalid)) {
									L.d(getClass(), "tag_code_fail_invalid");
									EMMPreferences.setLicenseIsPass(
											getEmmService(), false);
									// / 该序列号无效
								} else if (result.getCode().equalsIgnoreCase(
										SysLicenseResult.tag_code_fail_uplimit)) {
									L.d(getClass(), "tag_code_fail_uplimit");
									EMMPreferences.setLicenseIsPass(
											getEmmService(), false);
									// / 达到上限
								} else {
									L.d(this.getClass(),
											"unknown error occured ");
								}
							}
						}
					}
			} finally {
				emmProcessManager.runTask();
			}
		}


		private void save2SP(SysLicenseResult result) {
			EMMPreferences.setSysLicenseDeadLine(getEmmService(),
					result.getFailureTime());
			EMMPreferences.setSysLicenseCurrentTime(emmService,
					result.getNowTime());
			L.d(this.getClass(), "license deadline  saved complete !");
		}

	}

	public void setCheckUpdateTime() {
		EMMPreferences.setAppUpdateTime(getEmmService().getContext(),
				java.lang.System.currentTimeMillis() + "");
	}

	public String getLastCheckUpdateTime() {
		return EMMPreferences.getAppUpdateTime(getEmmService().getContext());
	}

	/**
	 * 是否需要检查更新, 检查时间间隔设定为1天
	 * 
	 * @return
	 */
	public boolean isNeedCheckUpdate() {
		Calendar calendar1 =Calendar.getInstance();
		calendar1.setTimeInMillis(Long.valueOf(getLastCheckUpdateTime()));
		Calendar calendar2 =Calendar.getInstance();
		int minus =DateUtil.compareDays(calendar1, calendar2);
		if (minus < 1) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @return the emmService
	 */
	public EMMService getEmmService() {
		return emmService;
	}

	/**
	 * @param emmService
	 *            the emmService to set
	 */
	public void setEmmService(EMMService emmService) {
		this.emmService = emmService;
	}

	public void runTask() {
		L.d(this.getClass(), "runTask()...");
		synchronized (taskList) {
			running = false;
			futureTask = null;
			if (!taskList.isEmpty()) {
				Runnable runnable = (Runnable) taskList.get(0);
				taskList.remove(0);
				running = true;
				futureTask = taskSubmitter.submit(runnable);
				if (futureTask == null) {
					taskTracker.decrease();
				}
			}
		}
		taskTracker.decrease();
		L.d(this.getClass(), "runTask()...done");
	}

	private void addTask(Runnable runnable) {
		L.d(this.getClass(), "addTask(runnable)...");
		taskTracker.increase();
		synchronized (taskList) {
			if (taskList.isEmpty() && !running) {
				running = true;
				futureTask = taskSubmitter.submit(runnable);
				if (futureTask == null) {
					taskTracker.decrease();
				}
			} else {
				taskList.add(runnable);
			}
		}
		L.d(this.getClass(), "addTask(runnable)... done");
	}

	/**
	 * @return the policyControl
	 */
	public PolicyControl getPolicyControl() {
		return policyControl;
	}

	/**
	 * @param policyControl
	 *            the policyControl to set
	 */
	public void setPolicyControl(PolicyControl policyControl) {
		this.policyControl = policyControl;
	}

	/**
	 * @return the sharedPrefs
	 */
	public SharedPreferences getSharedPrefs() {
		return sharedPrefs;
	}

	/**
	 * @param sharedPrefs
	 *            the sharedPrefs to set
	 */
	public void setSharedPrefs(SharedPreferences sharedPrefs) {
		this.sharedPrefs = sharedPrefs;
	}

}
