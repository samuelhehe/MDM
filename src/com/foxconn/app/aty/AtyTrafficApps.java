package com.foxconn.app.aty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.foxconn.app.R;
import com.foxconn.emm.adapter.TrafficAdapter;
import com.foxconn.emm.bean.UidTraffic;
import com.foxconn.emm.dao.TrafficAppsOptDao;
import com.foxconn.emm.utils.ApplicationDetailInfo;

/**
 * 该页面主要进行 流量信息的统计,以ListView的形式展现出来 数据主要从数据库获取
 */
public class AtyTrafficApps extends AbActivity implements OnItemClickListener {
	private static final int SUCCESS = 1;
	private static final int ERROR = 0;
	protected static final int EMPTY = 2;
	private ListView lv_traffic_content;
	private TrafficAdapter adapter;
	private Timer timer;
	private TimerTask timerTask;
	private List<UidTraffic> trafficAppsByDay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.traffic_manager);
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("流量排行");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(android.R.color.holo_blue_light);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		lv_traffic_content = (ListView) findViewById(R.id.lv_traffic_content);
		loadData();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// startActivity(new Intent(this, BarChartSingleActivity.class));

		if(hasTrafficAppsByDay!=null||hasTrafficAppsByDay.size()>0){
			String packageName = hasTrafficAppsByDay.get(position).getPackageName();
			if(TextUtils.isEmpty(packageName)){
				this.showToast("数据异常,请稍后重试");
				return;
			}
			ApplicationDetailInfo appDetailInfo = new ApplicationDetailInfo(AtyTrafficApps.this);
			appDetailInfo.showInstalledAppDetails(packageName);
		}
	}

	private List<UidTraffic> hasTrafficAppsByDay = new ArrayList<UidTraffic>();

	private void loadData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				TrafficAppsOptDao trafficAppsOptDao = new TrafficAppsOptDao(
						AtyTrafficApps.this);
				Calendar calendar = Calendar.getInstance();
				int tday = calendar.get(Calendar.DAY_OF_MONTH);
				int tmonth = calendar.get(Calendar.MONTH) + 1;
				int tyear = calendar.get(Calendar.YEAR);
				hasTrafficAppsByDay = trafficAppsOptDao.getHasTrafficAppsByDay(
						tday, tmonth, tyear);
				if (hasTrafficAppsByDay != null) {

					trafficAppsByDay = new ArrayList<UidTraffic>();
					for (UidTraffic uidTraffics : hasTrafficAppsByDay) {
						UidTraffic uidTraffic = trafficAppsOptDao
								.getTrafficSingleAppDataByDaypackagename(tday,
										uidTraffics.getPackageName(), tmonth,
										tyear);
						if (uidTraffic == null) {
							continue;
						}
						uidTraffic.setAppName(uidTraffics.getAppName());
						uidTraffic.setPackageName(uidTraffics.getPackageName());
						uidTraffic.setUid(uidTraffics.getUid());
						uidTraffic.setTDAY(uidTraffics.getTDAY());
						uidTraffic.setTHOUR(uidTraffics.getTHOUR());
						uidTraffic.setTMONTH(uidTraffics.getTMONTH());
						uidTraffic.setTTIME(uidTraffics.getTTIME());
						uidTraffic.setTYEAR(uidTraffics.getTYEAR());
						trafficAppsByDay.add(uidTraffic);
					}
					Message msg = Message.obtain();
					if (trafficAppsByDay != null) {
						msg.obj = trafficAppsByDay;
						msg.what = SUCCESS;
					} else {
						msg.obj = null;
						msg.what = EMPTY;
					}
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCESS:
				trafficAppsByDay = (List<UidTraffic>) msg.obj;
				if (trafficAppsByDay != null) {
					adapter = new TrafficAdapter(AtyTrafficApps.this,
							trafficAppsByDay);
					lv_traffic_content.setAdapter(adapter);
					lv_traffic_content
							.setOnItemClickListener(AtyTrafficApps.this);
				}
				break;
			case ERROR:

				break;

			case EMPTY:
				break;
			default:
				loadData();
				if (adapter != null) {
					adapter.notifyDataSetChanged();
				}
				break;
			}
		}
	};

	@Override
	protected void onStart() {
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				Message msg = Message.obtain();
				msg.what = 3;
				handler.sendMessage(msg);
			}
		};
		timer.schedule(timerTask, 1000, 10 * 1000);
		super.onStart();
	}

	@Override
	protected void onStop() {
		if (timer != null) {
			timer.cancel();
			timer = null;
			timerTask = null;
		}
		super.onStop();
	}
}
