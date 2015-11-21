package com.foxconn.app.aty;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ab.view.titlebar.AbTitleBar;
import com.foxconn.app.IMessageReceiveCallback;
import com.foxconn.app.R;
import com.foxconn.emm.receiver.EMMMessageCallBackReceiver;
import com.foxconn.emm.service.EMMMonitorService;
import com.foxconn.emm.utils.L;

public class AtyAppLimitMgr extends EMMBaseActivity implements OnClickListener,
		IMessageReceiveCallback {

	private static final int FLIP_DISTANCE = 230;
	private GestureDetector detector;
	private FrameLayout lay_black_app, lay_white_app, lay_limit_app;
	private ImageView img_black_app, img_white_app, img_limit_app;
	private Fragment black_app_frg, white_app_frg, limit_app_frg;
	public static List<IMessageReceiveCallback> messageReceiveListeners = new ArrayList<IMessageReceiveCallback>();
	private BroadcastReceiver messageAppMgrrecevier;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.aty_residemenu_applimitmgr);
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("应用管控");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(android.R.color.holo_blue_light);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		messageAppMgrrecevier = new EMMMessageCallBackReceiver(this);
		registerOnMessageAppMgrReceiver();
		detector = new GestureDetector(this, new OnGestureListener() {
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if ((e2.getX() - e1.getX()) > FLIP_DISTANCE) {
					onBackPressed();
					return true;
				}
				return false;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				return false;
			}
		});
		View relativeLayout = this.findViewById(R.id.sildingfinishlayout);
		relativeLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return detector.onTouchEvent(event);
			}
		});

		initView();
	}

	private void registerOnMessageAppMgrReceiver() {
		L.d(EMMMonitorService.class, "registerOnMessageAppMgrReceiver()...");
		IntentFilter filter = new IntentFilter();
		filter.addAction(EMMMessageCallBackReceiver.EMM_MAM_ACTION);
		registerReceiver(messageAppMgrrecevier, filter);
	}

	private void unregisterOnMessageAppMgrReceiver() {
		L.d(EMMMonitorService.class, "unregisterOnMessageAppMgrReceiver()...");
		unregisterReceiver(messageAppMgrrecevier);
	}

	private void initView() {
		lay_black_app = (FrameLayout) findViewById(R.id.lay_black_app);
		lay_white_app = (FrameLayout) findViewById(R.id.lay_white_app);
		lay_limit_app = (FrameLayout) findViewById(R.id.lay_limit_app);

		img_black_app = (ImageView) findViewById(R.id.img_black_app);
		img_white_app = (ImageView) findViewById(R.id.img_white_app);
		img_limit_app = (ImageView) findViewById(R.id.img_limit_app);

		black_app_frg = (Fragment) getSupportFragmentManager()
				.findFragmentById(R.id.black_app_frg);
		white_app_frg = (Fragment) getSupportFragmentManager()
				.findFragmentById(R.id.white_app_frg);
		limit_app_frg = (Fragment) getSupportFragmentManager()
				.findFragmentById(R.id.limit_app_frg);

		img_black_app.setSelected(true);

		lay_black_app.setOnClickListener(this);
		lay_white_app.setOnClickListener(this);
		lay_limit_app.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lay_black_app:
			getSupportFragmentManager().beginTransaction().show(black_app_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(white_app_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(limit_app_frg)
					.commit();

			img_black_app.setSelected(true);
			img_white_app.setSelected(false);
			img_limit_app.setSelected(false);
			break;

		case R.id.lay_white_app:
			getSupportFragmentManager().beginTransaction().hide(black_app_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().show(white_app_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(limit_app_frg)
					.commit();

			img_black_app.setSelected(false);
			img_white_app.setSelected(true);
			img_limit_app.setSelected(false);
			break;

		case R.id.lay_limit_app:
			getSupportFragmentManager().beginTransaction().hide(black_app_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().hide(white_app_frg)
					.commit();
			getSupportFragmentManager().beginTransaction().show(limit_app_frg)
					.commit();

			img_black_app.setSelected(false);
			img_white_app.setSelected(false);
			img_limit_app.setSelected(true);
			break;

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterOnMessageAppMgrReceiver();
		messageReceiveListeners.clear();
	}

	@Override
	public void onMessageReceived(String messageType) {
		if (messageReceiveListeners.size() > 0) {
			for (IMessageReceiveCallback receiveCallback : messageReceiveListeners) {
				receiveCallback.onMessageReceived(messageType);
			}
		}
	}
}
