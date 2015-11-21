package com.foxconn.app.aty;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.foxconn.app.IMessageReceiveCallback;
import com.foxconn.app.R;
import com.foxconn.emm.bean.PolicyInfo;
import com.foxconn.emm.bean.QRPolicyInfo;
import com.foxconn.emm.receiver.EMMMessageCallBackReceiver;
import com.foxconn.emm.tools.PolicyControl;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.utils.TextFormater;


/**
 * 
 * 政策内容显示策略:
 * 1.锁屏密码相关  不显示
 * 2.网络相关设定 WiFi状态显示 , SSID状态不显示
 * 3.设备连接设定 ,自动同步政策 不显示,
 * 蓝牙状态显示, USB状态不显示
 * 4.设备应用限制: 相机设定显示 , Googleplay 不显示
 * 后期添加:
 * 1.是否允许卸载 ,显示
 * 
 * 
 *
 */
public class AtySafePolicy extends EMMBaseActivity implements OnClickListener,
		IMessageReceiveCallback {

	private PolicyControl policyControl;

	private PolicyInfo policyInfo;

	private String policycontent;

	private BroadcastReceiver broadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.aty_residemenu_safepolicy);
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("安全政策");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(android.R.color.holo_blue_light);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		broadcastReceiver = new EMMMessageCallBackReceiver(this);
		registerOnPolicyMessageReceiver();
		initData();
		initView();
	}

	public void rebuildView() {
		initData();
		initView();
	}

	private void registerOnPolicyMessageReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(EMMMessageCallBackReceiver.EMM_SAFEPOLICY_ACTION);
		registerReceiver(broadcastReceiver, filter);
	}

	private void unregisterOnPolicyMessageReceiver() {
		unregisterReceiver(broadcastReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterOnPolicyMessageReceiver();
	}

	// //Policy内容.
	private TextView policy_content_content;
	// /Policy 更新日期
	private TextView update_date_content;
	// //blue tooth 状态
	private TextView bt_status_content;
	// / wifi status
	private TextView wifi_status_content;
	// / camera status
	private TextView camara_status_content;

	private TextView is_lock_content;

	private TextView policy_qrcode;

	private ImageView policy_qrcode_img;

	private void initView() {
		if (policyInfo == null) {
			policyInfo = new PolicyInfo().getDefaultPolicy();
			policycontent = policyControl.enablePolicy(policyInfo);
		}

		policy_qrcode = (TextView) this.findViewById(R.id.policy_qrcode);
		policy_qrcode.setOnClickListener(this);

		policy_qrcode_img = (ImageView) this
				.findViewById(R.id.policy_qrcode_img);
		policy_qrcode_img.setOnClickListener(this);

		View sys_safepolicy_info_il = this
				.findViewById(R.id.sys_safepolicy_info_il);

		policy_content_content = (TextView) sys_safepolicy_info_il
				.findViewById(R.id.policy_content_content);
		update_date_content = (TextView) sys_safepolicy_info_il
				.findViewById(R.id.update_date_content);
		bt_status_content = (TextView) sys_safepolicy_info_il
				.findViewById(R.id.bt_status_content);
		wifi_status_content = (TextView) sys_safepolicy_info_il
				.findViewById(R.id.wifi_status_content);
		camara_status_content = (TextView) sys_safepolicy_info_il
				.findViewById(R.id.camara_status_content);
		is_lock_content = (TextView) sys_safepolicy_info_il
				.findViewById(R.id.is_lock_content);

		if (TextUtils.isEmpty(policycontent)) {
			policy_content_content.setText("暂无政策管控");
		}
		policy_content_content.setText(policycontent);
		update_date_content.setText(TextFormater.getCurrentDateStringForHome());
		if (policyInfo.getEnable_Bluetooth() != null) {
			// / 如果为1,0管控
			if ("1".equals(policyInfo.getEnable_Bluetooth().getItemFlag())
					|| "0".equals(policyInfo.getEnable_Bluetooth()
							.getItemFlag())) {
				// /开启
				if ("1".equalsIgnoreCase(policyInfo.getEnable_Bluetooth()
						.getItemFlag())) {
					bt_status_content.setText("开启");
				} else if ("0".equalsIgnoreCase(policyInfo
						.getEnable_Bluetooth().getItemFlag())) {
					bt_status_content.setText("关闭");
				}
				// / 如果为2则为不管控
			} else if ("2".equals(policyInfo.getEnable_Bluetooth()
					.getItemFlag())) {
				bt_status_content.setText("不管控");
			}
		} else {
			bt_status_content.setText("不管控");
		}
		if (policyInfo.getEnable_Wifi() != null) {
			// / 如果为1,0管控
			if ("1".equals(policyInfo.getEnable_Wifi().getItemFlag())
					|| "0".equals(policyInfo.getEnable_Wifi().getItemFlag())) {
				// /开启
				if ("1".equalsIgnoreCase(policyInfo.getEnable_Wifi()
						.getItemFlag())) {
					wifi_status_content.setText("开启");
				} else if ("0".equalsIgnoreCase(policyInfo.getEnable_Wifi()
						.getItemFlag())) {
					wifi_status_content.setText("关闭");
				}
				// / 如果为0则为不管控
			} else if ("2".equalsIgnoreCase(policyInfo.getEnable_Wifi()
					.getItemFlag())) {
				wifi_status_content.setText("不管控");
			}
		} else {
			wifi_status_content.setText("不管控");
		}
		// / 如果为1,0 管控
		if (policyInfo.getEnable_Camera() != null) {
			if ("1".equals(policyInfo.getEnable_Camera().getItemFlag())
					|| "0".equals(policyInfo.getEnable_Camera().getItemFlag())) {
				// /开启
				if ("1".equalsIgnoreCase(policyInfo.getEnable_Camera()
						.getItemFlag())) {
					camara_status_content.setText("开启");
				} else if ("0".equalsIgnoreCase(policyInfo.getEnable_Camera()
						.getItemFlag())) {
					camara_status_content.setText("关闭");
				}

				// / 如果为2则为不管控
			} else if ("2".equalsIgnoreCase(policyInfo.getEnable_Wifi()
					.getItemFlag())) {
				camara_status_content.setText("不管控");
			}
		} else {

			camara_status_content.setText("不管控");
		}
		// / 如果为1
		if (policyInfo.getRequired_PW() != null) {
			if ("1".equals(policyInfo.getRequired_PW().getItemFlag())) {
				// /开启
				if ("1".equalsIgnoreCase(policyInfo.getRequired_PW()
						.getItemFlag())) {
					is_lock_content.setText("需要");
				} else {
					is_lock_content.setText("不需要");
				}
				// / 如果为0则为不管控
			} else {
				is_lock_content.setText("不管控");
			}
		} else {
			is_lock_content.setText("不管控");
		}

	}

	private void initData() {
		policyControl = new PolicyControl(this);
		String jsonpolicy = EMMPreferences.getPolicyData(this);
		policyInfo = getPolicyInfoFromJson(jsonpolicy);
		policycontent = policyControl.enablePolicy(policyInfo);
	}

	private PolicyInfo getPolicyInfoFromJson(String jsonpolicy) {
		if (TextUtils.isEmpty(jsonpolicy)
				|| jsonpolicy.equalsIgnoreCase("null")) {
			return null;
		}
		if (jsonpolicy.contains(QRPolicyInfo.TAG.tag_sendnote)
				&& jsonpolicy.contains(QRPolicyInfo.TAG.tag_itemFlag)) {
			return new PolicyInfo().getPolicyInfoFromQRJson(jsonpolicy);
		} else if (jsonpolicy.contains(PolicyInfo.TAG.tag_itemFlag)) {
			return new PolicyInfo().getPolicyInfoFromJson(jsonpolicy);
		} else {
			return null;
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.policy_qrcode_img:
		case R.id.policy_qrcode:
			startActivity(new Intent(this, CaptureActivity.class));
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onMessageReceived(String messageType) {
		if (this != null) {
			rebuildView();
		}
	}
}
