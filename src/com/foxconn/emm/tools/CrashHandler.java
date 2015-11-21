package com.foxconn.emm.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutionException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.view.WindowManager;

import com.foxconn.app.App;
import com.foxconn.app.R;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.utils.EMMReqParamsUtils;
import com.foxconn.emm.utils.HttpClientUtil;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.utils.ToastUtils;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
 * 
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {
	private Thread.UncaughtExceptionHandler mDefaultHandler;// 系统默认的UncaughtException处理类
	private static CrashHandler INSTANCE;// CrashHandler实例
	private Context mContext;// 程序的Context对象

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {

	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		if (INSTANCE == null)
			INSTANCE = new CrashHandler();
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();// 获取系统默认的UncaughtException处理器
		Thread.setDefaultUncaughtExceptionHandler(this);// 设置该CrashHandler为程序的默认处理器
	}

	/**
	 * 当UncaughtException发生时会转入该重写的方法来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果自定义的没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 *            异常信息
	 * @return true 如果处理了该异常信息;否则返回false.
	 */
	public boolean handleException(Throwable ex) {
		if (ex == null || mContext == null)
			return false;
		final String crashReport = getCrashReport(mContext, ex);
		try {
			new Thread(new SystemCrashErrorReportTask(crashReport)).start();
			new Thread() {
				public void run() {
					Looper.prepare();
					File file = save2File(crashReport);
					sendAppCrashReport(mContext, crashReport, file);
					Looper.loop();
				}
			}.start();
		} catch (Exception e) {
			L.d(this.getClass(), e.getMessage());
		} finally {
			// 退出
			App.getInstance().exit();
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}

		return true;
	}

	class SystemCrashErrorReportTask implements Runnable {

		private String crashReport;

		public SystemCrashErrorReportTask(String crashReport) {
			this.crashReport = crashReport;
		}

		@Override
		public void run() {
			String uri = EMMContants.SYSCONF.SYS_CREASH_ERROR_REPORT_URL;
			String reportJson = EMMReqParamsUtils.getSysCrashErrorReport(
					EMMContants.SYSCONF.SYS_CREASH_ERROR_REPSORT_MAIL,
					EMMPreferences.getUserID(mContext),
					"EMM3.0 error Report userId: "
							+ EMMPreferences.getUserID(mContext) + " ",
					crashReport).toString();
			try {
				String resultStr = HttpClientUtil.getRequestJsonParamsJson(uri,
						reportJson);
				L.d(this.getClass(), resultStr);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	public File save2File(String crashReport) {
		String fileName = "crash-" + System.currentTimeMillis() + ".txt";
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				File dir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ EMMContants.LocalConf.LocalHost_dirpath
						+ File.separator + "crash");
				if (!dir.exists())
					dir.mkdir();
				File file = new File(dir, fileName);
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(crashReport.toString().getBytes());
				fos.close();
				return file;
			} catch (Exception e) {
				System.out.println("save2File error:" + e.getMessage());
			}
		}
		return null;
	}

	private void sendAppCrashReport(final Context context,
			final String crashReport, final File file) {
		AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_error);
		builder.setMessage(R.string.app_error_message);
		builder.setPositiveButton(R.string.submit_report,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						try {
							Intent intent = new Intent(Intent.ACTION_SEND);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							String[] tos = { "samuel.sx.wang@mail.foxconn.com" };
							intent.putExtra(Intent.EXTRA_EMAIL, tos);

							intent.putExtra(Intent.EXTRA_SUBJECT,
									"EMM3.0客户端 - 错误报告");
							if (file != null) {
								intent.putExtra(Intent.EXTRA_STREAM,
										Uri.fromFile(file));
								intent.putExtra(Intent.EXTRA_TEXT,
										"请将此错误报告发送给我，以便我尽快修复此问题，谢谢合作！\n");
							} else {
								intent.putExtra(Intent.EXTRA_TEXT,
										"请将此错误报告发送给我，以便我尽快修复此问题，谢谢合作！\n"
												+ crashReport);
							}
							intent.setType("text/plain");
							intent.setType("message/rfc882");
							Intent.createChooser(intent, "Choose Email Client");
							context.startActivity(intent);
						} catch (Exception e) {
							ToastUtils.show(context,
									"There are no email clients installed.");
						} finally {
							dialog.dismiss();
							// 退出
							App.getInstance().exit();
							android.os.Process.killProcess(android.os.Process
									.myPid());
							System.exit(1);
						}
					}
				});
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 退出
						App.getInstance().exit();
						android.os.Process.killProcess(android.os.Process
								.myPid());
						System.exit(1);
					}
				});
		dialog = builder.create();
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();
	}

	/**
	 * 获取APP崩溃异常报告
	 * 
	 * @param ex
	 * @return
	 */
	private String getCrashReport(Context context, Throwable ex) {
		PackageInfo pinfo = getPackageInfo(context);
		StringBuffer exceptionStr = new StringBuffer();
		exceptionStr.append("Version: " + pinfo.versionName + "("
				+ pinfo.versionCode + ")\n");
		exceptionStr.append("Android: " + android.os.Build.VERSION.RELEASE
				+ "(" + android.os.Build.MODEL + ")\n");
		exceptionStr.append("Manufacturer: " + Build.MANUFACTURER + "\n");
		exceptionStr.append("Mac: " + EMMReqParamsUtils.getWifiMac(mContext)
				+ "\n");
		exceptionStr.append("Exception: " + ex.getMessage() + "\n");

		StackTraceElement[] elements = ex.getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			exceptionStr.append(elements[i].toString() + "\n");
		}
		return exceptionStr.toString();
	}

	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	private PackageInfo getPackageInfo(Context context) {
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// e.printStackTrace(System.err);
			System.out.println("getPackageInfo err = " + e.getMessage());
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

}