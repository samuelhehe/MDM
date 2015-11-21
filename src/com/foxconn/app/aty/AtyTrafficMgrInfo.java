package com.foxconn.app.aty;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ab.view.titlebar.AbTitleBar;
import com.foxconn.app.R;
import com.foxconn.emm.bean.DeviceTrafficLimitInfo;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.utils.TextFormater;

/**
 * 流量统计的首页
 */
public class AtyTrafficMgrInfo extends EMMBaseActivity{

	private TextView traffic_mgr_info_type_tv, traffic_mgr_info_num_tv,
			traffic_mgr_info_time_tv, traffic_mgr_info_desc_tv;

	private DeviceTrafficLimitInfo deviceTrafficLimitInfo ;
	
	private TableLayout traffic_mgr_info_tl ; 
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.traffic_manager_info);
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("流量限制信息");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(android.R.color.holo_blue_light);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		initView();
		initData();
	}

	private void initData() {
		String deviceTrafficLimitStr = EMMPreferences.getTrafficInfoLimit(this);
		deviceTrafficLimitInfo = DeviceTrafficLimitInfo.getInstanceFromJson(deviceTrafficLimitStr);
		if(deviceTrafficLimitInfo!=null){
			if(TextUtils.isEmpty(deviceTrafficLimitInfo.getTrafficLimit())){
				this.showToast("数据异常当前暂无限制流量信息");
				finish();
			}
			traffic_mgr_info_desc_tv.setText(deviceTrafficLimitInfo.getTrafficLimitDesc()); 
			if(TextUtils.equals(deviceTrafficLimitInfo.getTrafficLimitType(), "1")){
				traffic_mgr_info_type_tv.setText("按天限制");
			}else if(TextUtils.equals(deviceTrafficLimitInfo.getTrafficLimitType(),"0")){
				traffic_mgr_info_type_tv.setText("按月限制");
			}
			traffic_mgr_info_num_tv.setText(deviceTrafficLimitInfo.getTrafficLimit());
			String modifyDateStr = 	deviceTrafficLimitInfo.getModifyDate();
			if(!TextUtils.isEmpty(modifyDateStr)){
				String dateStringForHome  =getUpdateTime(modifyDateStr);
				if(TextUtils.isEmpty(dateStringForHome)){
					traffic_mgr_info_time_tv.setText(TextFormater.getCurrentDateStringForHome());
				}
				traffic_mgr_info_time_tv.setText(dateStringForHome);
			}else{
				traffic_mgr_info_time_tv.setText(TextFormater.getCurrentDateStringForHome());
			}
			traffic_mgr_info_tl.setVisibility(View.VISIBLE);
		}else{
			this.showToast("当前暂无限制流量信息");
			finish();
		}
	}

	private void initView() {
		traffic_mgr_info_tl = (TableLayout) this.findViewById(R.id.traffic_mgr_info_tl);
		traffic_mgr_info_tl.setVisibility(View.GONE);
		traffic_mgr_info_desc_tv = (TextView) this.findViewById(R.id.traffic_mgr_info_desc_tv);
		traffic_mgr_info_type_tv = (TextView) this.findViewById(R.id.traffic_mgr_info_type_tv);
		traffic_mgr_info_num_tv = (TextView) this.findViewById(R.id.traffic_mgr_info_num_tv);
		traffic_mgr_info_time_tv = (TextView) this.findViewById(R.id.traffic_mgr_info_time_tv);
	}


	private String getUpdateTime(String parseTime){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(parseTime.substring(0, 4).trim())
		.append("年")
		.append(parseTime.substring(4, 6).trim())
		.append("月")
		.append(parseTime.substring(6, 8).trim())
		.append("日  ")
		.append(parseTime.substring(8, 10).trim())
		.append(":")
		.append(parseTime.substring(10, 12).trim())
		.append(":")
		.append(parseTime.substring(12, 14).trim());
		return stringBuilder.toString();
	}
	
	
}
