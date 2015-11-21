package com.foxconn.app.aty;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.foxconn.app.R;
import com.foxconn.emm.bean.PolicyInfo;
import com.foxconn.emm.tools.PolicyControl;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.utils.L;
import com.foxconn.lib.qrcode.decode.DecodeThread;

public class ResultActivity extends AbActivity {

	private ImageView mResultImage;
	private TextView mResultText;
	private PolicyControl policyControl;
	private boolean qrPolicy_flag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_result);
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("扫描结果");
		mAbTitleBar.setTitleBarBackground(android.R.color.holo_blue_light);
		mAbTitleBar.setTitleTextMargin(20, 0, 0, 0);

		policyControl = new PolicyControl(this);
		Bundle extras = getIntent().getExtras();
		mResultImage = (ImageView) findViewById(R.id.result_image);
		mResultText = (TextView) findViewById(R.id.result_text);

		if (null != extras) {
			int width = extras.getInt("width");
			int height = extras.getInt("height");
			LayoutParams lps = new LayoutParams(width, height);
			lps.topMargin = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 30, getResources()
							.getDisplayMetrics());
			lps.leftMargin = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 20, getResources()
							.getDisplayMetrics());
			lps.rightMargin = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 20, getResources()
							.getDisplayMetrics());
			mResultImage.setLayoutParams(lps);
			String qrJsonPolicyStr = extras.getString("result");
			L.d(this.getClass(), qrJsonPolicyStr);
			PolicyInfo policyInfoFromQRJson = new PolicyInfo()
					.getPolicyInfoFromQRJson(qrJsonPolicyStr);
			if (policyInfoFromQRJson != null) {
				mResultText.setText("政策扫描成功!");
				mResultText
						.setTextColor(getResources().getColor(R.color.green));
				policyControl.enablePolicy(policyInfoFromQRJson);
				qrPolicy_flag = true;
				EMMPreferences.setPolicyData(this, "");
				EMMPreferences.setPolicyData(this, qrJsonPolicyStr);
			} else {
				mResultText.setTextColor(getResources().getColor(R.color.red));
				mResultText.setText("政策扫描错误,请重试或联系工作人员!");
				qrPolicy_flag = false;
			}
			Bitmap barcode = null;
			byte[] compressedBitmap = extras
					.getByteArray(DecodeThread.BARCODE_BITMAP);
			if (compressedBitmap != null) {
				barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0,
						compressedBitmap.length, null);
				// Mutable copy:
				barcode = barcode.copy(Bitmap.Config.RGB_565, true);
			}
			mResultImage.setImageBitmap(barcode);
			CountDownTimer mCountdownTimer = new CountDownTimer(3 * 1000, 1000) {
				@Override
				public void onTick(long millisUntilFinished) {
					if (qrPolicy_flag) {
						mResultText.setTextColor(getResources().getColor(
								R.color.green));
						mResultText.setText("正在启动安全政策: " + millisUntilFinished
								/ 1000 + "秒");
					} else {
						mResultText.setTextColor(getResources().getColor(
								R.color.red));
						mResultText.setText("扫描出错正在退出:" + millisUntilFinished
								/ 1000 + "秒 , 请重试或联系工作人员!");
					}
				}

				@Override
				public void onFinish() {
					startActivity(new Intent(ResultActivity.this,
							AtySafePolicy.class));
					ResultActivity.this.finish();
				}
			};
			mCountdownTimer.start();
		} else {
			this.showToast("扫描出错,请重试或联系工作人员");
		}
	}

}