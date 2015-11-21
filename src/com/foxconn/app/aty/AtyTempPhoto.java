package com.foxconn.app.aty;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.foxconn.app.App;
import com.foxconn.app.R;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.ImageUtils;
import com.foxconn.emm.utils.ToastUtils;
import com.foxconn.lib.cropimage.CropImage;

public class AtyTempPhoto extends AbActivity implements OnClickListener {

	public static final String FLAG_PIC_TMP_PATH = "FLAG_PIC_TMP_PATH";
	private static final int PHOTO_CROP = 8;
	private static final String TAG = "AtyTempPhoto";
	private static final boolean DEBUG = EMMContants.DEBUG;
	Button sys_takephoto_main_cut_btn, sys_takephoto_main_rotate_btn,
			sys_takephoto_main_turn_btn, sys_takephoto_main_retake_btn,
			sys_takephoto_main_confirm_btn;
	ImageView sys_takephoto_main_pic_iv;

	Bitmap tmpBmp;

	String tmpPicPath;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.takephoto_temp);
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("照片修整");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(android.R.color.holo_blue_light);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);

		initView();
		initData();
		App.getInstance().addActivity(this);
	}

	private void initData() {

		tmpPicPath = getIntent().getStringExtra(FLAG_PIC_TMP_PATH);
		if (TextUtils.isEmpty(tmpPicPath)) {
			ToastUtils.show(this, "照片美化引擎初始化失败,请重新拍照");
			sys_takephoto_main_retake_btn.setClickable(true);
			sys_takephoto_main_cut_btn.setClickable(false);
			sys_takephoto_main_rotate_btn.setClickable(false);
			sys_takephoto_main_turn_btn.setClickable(false);
			sys_takephoto_main_confirm_btn.setClickable(false);

		} else {
			tmpBmp = BitmapFactory.decodeFile(tmpPicPath);
			sys_takephoto_main_pic_iv.setImageBitmap(BitmapFactory
					.decodeFile(tmpPicPath));
			sys_takephoto_main_retake_btn.setClickable(true);
			sys_takephoto_main_cut_btn.setClickable(true);
			sys_takephoto_main_rotate_btn.setClickable(true);
			sys_takephoto_main_turn_btn.setClickable(true);
			sys_takephoto_main_confirm_btn.setClickable(true);
		}
	}

	private void initView() {

		sys_takephoto_main_cut_btn = (Button) this
				.findViewById(R.id.sys_takephoto_main_cut_btn);
		sys_takephoto_main_cut_btn.setOnClickListener(this);

		sys_takephoto_main_rotate_btn = (Button) this
				.findViewById(R.id.sys_takephoto_main_rotate_btn);
		sys_takephoto_main_rotate_btn.setOnClickListener(this);

		sys_takephoto_main_turn_btn = (Button) this
				.findViewById(R.id.sys_takephoto_main_turn_btn);
		sys_takephoto_main_turn_btn.setOnClickListener(this);

		sys_takephoto_main_retake_btn = (Button) this
				.findViewById(R.id.sys_takephoto_main_retake_btn);
		sys_takephoto_main_retake_btn.setOnClickListener(this);

		sys_takephoto_main_confirm_btn = (Button) this
				.findViewById(R.id.sys_takephoto_main_confirm_btn);
		sys_takephoto_main_confirm_btn.setOnClickListener(this);

		sys_takephoto_main_pic_iv = (ImageView) this
				.findViewById(R.id.sys_takephoto_main_pic_iv);

	}

	/**
	 * Constructs an intent for image cropping. 调用图片剪辑程序 剪裁后的图片跳转到新的界面
	 */
	public static Intent getCropImageIntent(Uri photoUri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(photoUri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 80);
		intent.putExtra("outputY", 80);
		intent.putExtra("return-data", true);
		return intent;
	}

	protected void doCropPhoto(File f) {
		try {
			// // 启动gallery去剪辑这个照片
			// final Intent intent = getCropImageIntent(Uri.fromFile(f));
			// startActivityForResult(intent, PHOTO_CROP);

			this.showToast("跳往裁剪页面");
			Intent intent = new Intent(this, CropImage.class);
			// here you have to pass absolute path to your file
			intent.putExtra("image-path", f.getPath());
			intent.putExtra("scale", true);
			startActivityForResult(intent, PHOTO_CROP);
		} catch (Exception e) {
			Toast.makeText(this, "失败", Toast.LENGTH_LONG).show();
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		if (data != null) {
			if (requestCode == PHOTO_CROP) {
				Uri cropedUri = data.getParcelableExtra("PATH_URI");
				if (DEBUG)
					Log.i(TAG, "croped Imaged path --->>" + cropedUri.getPath());
				tmpBmp = BitmapFactory.decodeFile(cropedUri.getPath());
				sys_takephoto_main_pic_iv.setImageBitmap(tmpBmp);
			}
		} else {
			ToastUtils.show(AtyTempPhoto.this, "图片裁剪出错,请重试或联系工作人员");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sys_takephoto_main_cut_btn:
			// / 裁剪
			if (TextUtils.isEmpty(tmpPicPath)) {

			} else if (tmpBmp != null) {
				ImageUtils.saveToLocal(tmpBmp, tmpPicPath);
				doCropPhoto(new File(tmpPicPath));
			}
			break;
		case R.id.sys_takephoto_main_rotate_btn:
			// / 旋转
			if (tmpBmp != null) {
				tmpBmp = ImageUtils.rightRotate(tmpBmp);
				sys_takephoto_main_pic_iv.setImageBitmap(tmpBmp);
			}
			break;
		case R.id.sys_takephoto_main_turn_btn:
			// / 翻转
			if (tmpBmp != null) {
				tmpBmp = ImageUtils.reverseBitmap(tmpBmp, 0);
				sys_takephoto_main_pic_iv.setImageBitmap(tmpBmp);
			}
			break;
		case R.id.sys_takephoto_main_retake_btn:
			// / 重拍
			startActivity(new Intent(AtyTempPhoto.this, AtyMainCamera.class)
					.setFlags(AtyMainCamera.FLAG_RETAKE_PHOTO));
			this.finish();
			break;
		case R.id.sys_takephoto_main_confirm_btn:
			// / 确定
			if (!TextUtils.isEmpty(tmpPicPath)) {
				ImageUtils.saveToLocal(tmpBmp, tmpPicPath);
				Intent intent = new Intent(this, AtyMainCamera.class);
				intent.putExtra(AtyTempPhoto.FLAG_PIC_TMP_PATH, tmpPicPath);
				intent.setFlags(AtyMainCamera.FLAG_PROCESS_OK);
				startActivity(intent);
				finish();
			} else {
				ToastUtils.show(this, "处理出错,请重拍");
				sys_takephoto_main_retake_btn.setClickable(true);
				sys_takephoto_main_cut_btn.setClickable(false);
				sys_takephoto_main_rotate_btn.setClickable(false);
				sys_takephoto_main_turn_btn.setClickable(false);
				sys_takephoto_main_confirm_btn.setClickable(false);
			}
			break;
		}
	}

}
