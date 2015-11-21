package com.foxconn.emm.lock;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.foxconn.app.App;
import com.foxconn.app.R;
import com.foxconn.app.aty.AtyEnroll;
import com.foxconn.app.aty.AtyMain;
import com.foxconn.app.aty.AtySettings;
import com.foxconn.emm.bean.UserInfo;
import com.foxconn.emm.dao.DBUserInfoHelper;
import com.foxconn.emm.lock.LockPatternView.Cell;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.utils.ToastUtils;

public class UnlockGesturePasswordActivity extends Activity implements
		OnClickListener {
	private LockPatternView mLockPatternView;
	private int mFailedPatternAttemptsSinceLastTimeout = 0;
	private CountDownTimer mCountdownTimer = null;
	private Handler mHandler = new Handler();
	private TextView mHeadTextView;
	private Animation mShakeAnim;

	private Toast mToast;

	private int flag;
	
	private boolean canbackable = true;

	private void showToast(CharSequence message) {
		if (null == mToast) {
			mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			mToast.setText(message);
		}
		mToast.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gesturepassword_unlock);
		mLockPatternView = (LockPatternView) this.findViewById(R.id.gesturepwd_unlock_lockview);
		mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
		mLockPatternView.setTactileFeedbackEnabled(true);
		mHeadTextView = (TextView) findViewById(R.id.gesturepwd_unlock_text);
		mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);
		flag = getIntent().getFlags();
		switch (flag) {
		
		case AtySettings.UNlOCK_OFFEN:
			canbackable = false;
			break;
		}
		// // forget password
		findViewById(R.id.gesturepwd_unlock_forget).setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!App.getInstance().getLockPatternUtils().savedPatternExists()) {
			startActivity(new Intent(this, GuideGesturePasswordActivity.class));
			finish();
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if(!canbackable){
				App.getInstance().exit();
				return true;
			}
			 	super.onBackPressed();
			 	return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mCountdownTimer != null)
			mCountdownTimer.cancel();
	}

	private Runnable mClearPatternRunnable = new Runnable() {
		public void run() {
			mLockPatternView.clearPattern();
		}
	};

	protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {

		public void onPatternStart() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
			patternInProgress();
		}

		public void onPatternCleared() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
		}

		public void onPatternDetected(List<LockPatternView.Cell> pattern) {
			if (pattern == null)
				return;
			if (App.getInstance().getLockPatternUtils().checkPattern(pattern)) {
				mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);

				switch (flag) {
				case AtySettings.UNlOCK_OFFEN:
					UnlockGesturePasswordActivity.this.finish();
					break;
				
				case AtySettings.RESET_SECURITY_ID:
					startActivity(new Intent(UnlockGesturePasswordActivity.this,
							CreateGesturePasswordActivity.class));
					
					finish();
					break;
				case AtySettings.CLOSE_SECURITY_ID:
					EMMPreferences.setSecurityStatus(UnlockGesturePasswordActivity.this,false);
					finish();
					break;

				default:
					showToast("解锁成功");
					DBUserInfoHelper dbUserInfoHelper = new DBUserInfoHelper(
							getApplicationContext());
					String userId = EMMPreferences
							.getUserID(getApplicationContext());
					UserInfo contentUserinfo = dbUserInfoHelper
							.findUserByUserId(userId);
					if (contentUserinfo != null) {
						Intent gosettingIntent = new Intent(
								UnlockGesturePasswordActivity.this,
								AtyMain.class);
						gosettingIntent.putExtra(UserInfo.TAG.TB_NAME,
								contentUserinfo);
						startActivity(gosettingIntent);
						finish();
					} else {
						L.d(this.getClass(), "userInfo is null ");
						ToastUtils.show(UnlockGesturePasswordActivity.this,"用户信息丢失或损坏,请重新注册");
						mHandler.postDelayed(enrollsys, 1000);
					}
					finish();
					break;
				}

			} else {
				mLockPatternView
						.setDisplayMode(LockPatternView.DisplayMode.Wrong);
				if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
					mFailedPatternAttemptsSinceLastTimeout++;
					int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT
							- mFailedPatternAttemptsSinceLastTimeout;
					if (retry >= 0) {
						if (retry == 0)
							showToast("您已5次输错密码，请30秒后再试");
						mHeadTextView.setText("密码错误，还可以再输入" + retry + "次");
						mHeadTextView.setTextColor(Color.RED);
						mHeadTextView.startAnimation(mShakeAnim);
					}

				} else {
					showToast("输入长度不够，请重试");
				}
				if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) {
					mHandler.postDelayed(attemptLockout, 2000);
				} else {
					mLockPatternView.postDelayed(mClearPatternRunnable, 2000);
				}
			}
		}

		public void onPatternCellAdded(List<Cell> pattern) {

		}

		private void patternInProgress() {
		}
	};

	/**
	 * device has not enrolled enter enroll device
	 */
	Runnable enrollsys = new Runnable() {

		@Override
		public void run() {
			Intent intent = new Intent();
			intent.setClass(UnlockGesturePasswordActivity.this, AtyEnroll.class);
			startActivity(intent);
			finish();
		}
	};

	Runnable attemptLockout = new Runnable() {

		@Override
		public void run() {
			mLockPatternView.clearPattern();
			mLockPatternView.setEnabled(false);
			mCountdownTimer = new CountDownTimer(
					LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS + 1, 1000) {

				@Override
				public void onTick(long millisUntilFinished) {
					int secondsRemaining = (int) (millisUntilFinished / 1000) - 1;
					if (secondsRemaining > 0) {
						mHeadTextView.setText(secondsRemaining + " 秒后重试");
					} else {
						mHeadTextView.setText("请绘制手势密码");
						mHeadTextView.setTextColor(Color.WHITE);
					}
				}

				@Override
				public void onFinish() {
					mLockPatternView.setEnabled(true);
					mFailedPatternAttemptsSinceLastTimeout = 0;
				}
			}.start();
		}
	};

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.gesturepwd_unlock_forget:
			ToastUtils.show(this, "由于您忘记密码,请重新注册设备!");
			mHandler.postAtTime(enrollsys, 1000);
			break;

		default:
			break;
		}

	}

}
