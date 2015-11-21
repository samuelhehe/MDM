package com.foxconn.app.aty;

import java.io.IOException;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.foxconn.app.App;
import com.foxconn.app.R;
import com.foxconn.emm.bean.UserInfo;
import com.foxconn.emm.receiver.DeviceAdminSampleReceiver;
import com.foxconn.emm.tools.UploadHeadImgTools;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.utils.ImageUtils;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.utils.TestNetUtils;
import com.foxconn.emm.utils.ToastUtils;

public class AtyMainCamera extends AbActivity implements OnClickListener {

	public static final String KEY_PHOTO_PATH = "photo_path";
	private static final int FLAG_TACK_PHOTO = 6;
	public static final int FLAG_RETAKE_PHOTO = 3;

	public static final int FLAG_PROCESS_OK = 1;

	private boolean isSaved = false;
	private DevicePolicyManager mDPM;

	private ComponentName mDeviceAdminSample;
	private Uri photoUri;

	Button sys_takephoto_main_take_btn;

	Button sys_takephoto_main_process_btn;

	Button sys_takephoto_main_confirm_btn;

	ImageView sys_takephoto_main_pic_iv;

	String finalIconPath;

	private String tmpPicpath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.takephoto_main);
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("照片采集");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(android.R.color.holo_blue_light);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		initView();
		App.getInstance().addActivity(this);
		// sys_takephoto_main_confirm_btn.setClickable(true);//此行发布时需删除
		int flag = getIntent().getFlags();
		switch (flag) {
		case FLAG_RETAKE_PHOTO:
			takePhoto();
			break;

		case FLAG_PROCESS_OK:
			finalIconPath = getIntent().getStringExtra(
					AtyTempPhoto.FLAG_PIC_TMP_PATH);
			if (TextUtils.isEmpty(finalIconPath)) {

				// //数据异常,请重拍

			} else {
				// / set ImageView
				sys_takephoto_main_pic_iv.setImageBitmap(BitmapFactory
						.decodeFile(finalIconPath));
				sys_takephoto_main_take_btn.setClickable(true);
				sys_takephoto_main_process_btn.setClickable(true);
				sys_takephoto_main_confirm_btn.setClickable(true);
			}
			break;
		}

	}

	private void initView() {

		sys_takephoto_main_take_btn = (Button) this
				.findViewById(R.id.sys_takephoto_main_take_btn);
		sys_takephoto_main_take_btn.setOnClickListener(this);

		sys_takephoto_main_process_btn = (Button) this
				.findViewById(R.id.sys_takephoto_main_process_btn);
		sys_takephoto_main_process_btn.setOnClickListener(this);
		// / 如果图片为null
		sys_takephoto_main_process_btn.setClickable(false);

		sys_takephoto_main_confirm_btn = (Button) this
				.findViewById(R.id.sys_takephoto_main_confirm_btn);
		sys_takephoto_main_confirm_btn.setOnClickListener(this);
		sys_takephoto_main_confirm_btn.setClickable(false);

		sys_takephoto_main_pic_iv = (ImageView) this
				.findViewById(R.id.sys_takephoto_main_pic_iv);

	}

	public void onClick(View v) {
		switch (v.getId()) {
		// / takephoto
		case R.id.sys_takephoto_main_take_btn:
			checkCamera(true);// /打开可能禁用的相机.
			takePhoto();// 调用启动相机
			break;

		case R.id.sys_takephoto_main_process_btn:

			// / start tmp aty
			Intent intent = new Intent(this, AtyTempPhoto.class);
			intent.putExtra(AtyTempPhoto.FLAG_PIC_TMP_PATH, tmpPicpath);
			startActivity(intent);

			break;

		case R.id.sys_takephoto_main_confirm_btn:
			// //0.compress to sdcard spercific path
			// //1. update db
			// / 2. disablecamera
			// / 3. return

			if (!TextUtils.isEmpty(finalIconPath)) {
				String userId = EMMPreferences
						.getUserID(getApplicationContext());
				try {
					ImageUtils.saveToLocal(
							BitmapFactory.decodeFile(finalIconPath),
							UserInfo.getHeadIconFilePath(userId));
				} catch (IOException e) {
					ToastUtils.show(this, "拍照信息存储失败,请确认SdCard处于正常状态");
					e.printStackTrace();
				}
				checkCamera(true);
				isSaved = true;

				// / 启动当前的Policy
				// PolicyControl policyControl = new PolicyControl(this);
				// TimerJSONPolicy timerJSONPolicy =
				// EMMPreferences.getCurrentPolicyData(this);
				// policyControl.enablePolicy(timerJSONPolicy);

				// / 启动上传线程...
				new Thread(runnable).start();
				Intent goMainIntent = new Intent(this, AtyMain.class);
				startActivity(goMainIntent);
				this.finish();
			} else {
				Toast.makeText(AtyMainCamera.this, "请采集照片", Toast.LENGTH_LONG)
						.show();
			}
			break;
		}
	}

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (isSaved) {
				if (TestNetUtils.checkNetWorkInfo(getApplicationContext())) {
					String userId = EMMPreferences
							.getUserID(getApplicationContext());
					String headImgFilepath;
					try {
						headImgFilepath = UserInfo.getHeadIconFilePath(userId);
						if (UserInfo.hasHeadIcon(headImgFilepath, userId)) {
							UploadHeadImgTools.uploadImgToServer2(
									headImgFilepath,
									UserInfo.getHeadIconName(userId), userId);
						}
						EMMPreferences.setHASHEADIMG_UPLOAD(
								getApplicationContext(), true);
					} catch (IOException e) {
						L.i(this.getClass(),
								"headimg saved error " + e.getMessage());
						EMMPreferences.setHASHEADIMG_UPLOAD(AtyMainCamera.this,
								false);
						e.printStackTrace();
					}
				} else {
					EMMPreferences.setHASHEADIMG_UPLOAD(
							getApplicationContext(), false);
				}
			}
		}
	};


	protected void checkCamera(boolean flag) {
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mDeviceAdminSample = new ComponentName(this,
				DeviceAdminSampleReceiver.class);
		mDPM.setCameraDisabled(mDeviceAdminSample, !flag);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			sys_takephoto_main_pic_iv.setImageBitmap(null);
			tmpPicpath = getPhotoPath(FLAG_TACK_PHOTO, data);
			if (TextUtils.isEmpty(tmpPicpath)) {
				ToastUtils.show(AtyMainCamera.this, "拍照故障,请联系工作人员");
			} else {

				// / 设置显示图片的大小... 这里可以添加一个动态生成的width, height ..
				Bitmap newBitmap = ImageUtils.zoomBitmap(
						BitmapFactory.decodeFile(tmpPicpath), 480, 640);

				sys_takephoto_main_pic_iv.setImageBitmap(newBitmap);
				finalIconPath = tmpPicpath = ImageUtils.saveToLocal(newBitmap,
						tmpPicpath);

				if (TextUtils.isEmpty(tmpPicpath)) {
					sys_takephoto_main_process_btn.setClickable(false);
					sys_takephoto_main_confirm_btn.setClickable(false);
				} else {
					sys_takephoto_main_take_btn.setClickable(false);
					sys_takephoto_main_process_btn.setClickable(true);
					sys_takephoto_main_confirm_btn.setClickable(true);
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 拍照获取图片
	 */
	private void takePhoto() {
		if (TextUtils.isEmpty(EMMContants.LocalConf.getEMMLocalHost_dirPath())) {
			Toast.makeText(this, "存储卡不存在,请确认SD卡正常后,重试", Toast.LENGTH_LONG)
					.show();
		} else {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// "android.media.action.IMAGE_CAPTURE"
			ContentValues values = new ContentValues();
			try {
				photoUri = this.getContentResolver().insert(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
				intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
						photoUri);
				intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
				intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION,
						Configuration.ORIENTATION_PORTRAIT);
				startActivityForResult(intent, FLAG_TACK_PHOTO);
			} catch (Exception e) {
				ToastUtils.show(AtyMainCamera.this, "相机启动失败,请确认相机或者SD卡正常后,请重试");
			}
		}
	}

	/**
	 * 选择图片后，获取图片的路径
	 * 
	 * @param requestCode
	 * @param data
	 */
	private String getPhotoPath(int requestCode, Intent data) {
		String[] pojo = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
		if (cursor != null) {
			int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
			cursor.moveToFirst();
			String picPath = cursor.getString(columnIndex);
			// String picPath = "1401181151021.jpg";
			cursor.close();
			if (!TextUtils.isEmpty(picPath)) {
				L.d("imagePath = " + picPath);
				return picPath;
			}
		}
		return null;
	}
}