package com.foxconn.app.aty;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.view.listener.AbOnProgressListener;
import com.ab.view.progress.AbCircleProgressBar;
import com.ab.view.titlebar.AbTitleBar;
import com.foxconn.app.R;
import com.foxconn.emm.bean.DeviceTrafficLimitInfo;
import com.foxconn.emm.dao.TrafficOptDao;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.utils.TextFormater;

/**
 * 流量统计的首页
 */
public class AtyTrafficMgr extends EMMBaseActivity implements OnClickListener {


	// 最大200M流量
	private int max = 0;
	// /tmp多少M

	private int progress = 0;

	private int step;
	
	private TextView numberText, maxText;
	private ImageView tv_traffic_paihang;
	private ImageView traffic_mgr_iv;
	private TextView trafficmgr_rl_time_tv;
	private AbCircleProgressBar mAbProgressBar;
	private DeviceTrafficLimitInfo deviceTrafficLimitInfo;
	private TextView trafficmgr_rl_limit_desc, trafficmgr_rl_limit_tv;
	private Button  sys_aty_trafficmar_check_btn ; 

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.aty_residemenu_trafficmgr);
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("流量管理");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(android.R.color.holo_blue_light);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		initView();
		initTrafficData();
	}

	private void initTrafficData() {
		deviceTrafficLimitInfo = DeviceTrafficLimitInfo
				.getInstanceFromJson(EMMPreferences.getTrafficInfoLimit(this));
		if (deviceTrafficLimitInfo != null) {
			if(TextFormater.isEmpty(deviceTrafficLimitInfo.getTrafficLimitDesc())){
				trafficmgr_rl_limit_desc.setText("限制说明: 暂无限制说明");
			}else{
				trafficmgr_rl_limit_desc.setText("限制说明: "+deviceTrafficLimitInfo.getTrafficLimitDesc());
			}
			if(TextFormater.isEmpty(deviceTrafficLimitInfo.getTrafficLimit())){
				trafficmgr_rl_limit_desc.setText("限制说明: 暂无流量限制");
			}else{
				trafficmgr_rl_limit_tv.setText("流量限制: "+ deviceTrafficLimitInfo.getTrafficLimit());
			}
		}
		
		String maxstr = EMMPreferences.getTotalData(this);
		String progressStr = new TrafficOptDao(this)
				.getUsedTotalTrafficToCurrentDay();

		if (TextUtils.isEmpty(progressStr)||progressStr.contains("null")||progressStr.equals("0")) {
			progress = 0;
		}else{
			progress = Integer.valueOf(TextFormater.getStringToMBFromByte(Long
					.valueOf(progressStr)));
		}
		if (TextUtils.isEmpty(maxstr)||maxstr.contains("null")||maxstr.equals("0")) {
			max = progress;
			maxText.setText("暂无设定信息");
		}else{
			max = Integer.valueOf(TextFormater.getStringToMBFromByte(Long
					.valueOf(maxstr)));
			maxText.setText("剩余" + String.valueOf(max - progress) + "M");
			if (isWarning(progress, max)) {
				maxText.setTextColor(getResources().getColor(
						android.R.color.holo_orange_light));
			}
		}
		if(numberText!=null){
			numberText.setText(""+progress);
		}
		mAbProgressBar.setMax(max);
		mAbProgressBar.setProgress(progress);
	
	}

	private void initView() {
		
		RelativeLayout trafficmgr_current_left_rl = (RelativeLayout) this.findViewById(R.id.trafficmgr_current_left_rl);
		mAbProgressBar = (AbCircleProgressBar) trafficmgr_current_left_rl.findViewById(R.id.circleProgressBar);
		numberText = (TextView) trafficmgr_current_left_rl.findViewById(R.id.numberText);
		maxText = (TextView) trafficmgr_current_left_rl.findViewById(R.id.maxText);
		tv_traffic_paihang = (ImageView) this
				.findViewById(R.id.traffic_statitatics_iv);
		sys_aty_trafficmar_check_btn  = (Button) this.findViewById(R.id.sys_aty_trafficmar_check_btn);
		sys_aty_trafficmar_check_btn.setOnClickListener(this);
		tv_traffic_paihang.setOnClickListener(this);

		trafficmgr_rl_time_tv = (TextView) this
				.findViewById(R.id.trafficmgr_rl_time_tv);
		trafficmgr_rl_time_tv.setText(TextFormater
				.getCurrentDateStringForHome());

		traffic_mgr_iv = (ImageView) this.findViewById(R.id.traffic_mgr_iv);
		traffic_mgr_iv.setOnClickListener(this);

		trafficmgr_rl_limit_tv = (TextView) this
				.findViewById(R.id.trafficmgr_rl_limit_tv);
		trafficmgr_rl_limit_tv.setText("流量限制: 暂无设定");

		trafficmgr_rl_limit_desc = (TextView) this
				.findViewById(R.id.trafficmgr_rl_limit_desc);
		trafficmgr_rl_limit_desc.setText("限制说明: 暂无设定说明");
		mAbProgressBar.setMax(max);
		mAbProgressBar.setProgress(progress);
		mAbProgressBar.setAbOnProgressListener(new AbOnProgressListener() {
			@Override
			public void onProgress(int progress) {

			}

			@Override
			public void onComplete(int progress) {

			}
		});
	}

	/**
	 * 判断是否超出了当月流量的总的80%
	 * 
	 * @param progress
	 * @param max
	 * @return
	 */
	private boolean isWarning(int progress, int max) {

		int warning = (int) (max * 0.8f);
		if (progress > warning) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.traffic_statitatics_iv:
			startActivity(new Intent(this, AtyTrafficApps.class));

			break;

		case R.id.traffic_mgr_iv:

			startActivity(new Intent(this, AtyTrafficMgrInfo.class));

			break;
		case R.id.sys_aty_trafficmar_check_btn:
			
			this.showToast("规划中...");
			break;

		}

	}

}
