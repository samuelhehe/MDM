package com.foxconn.app.aty;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.foxconn.app.IMessageReceiveCallback;
import com.foxconn.app.R;
import com.foxconn.emm.adapter.LimitListAdapter;
import com.foxconn.emm.bean.InstalledAppInfo;
import com.foxconn.emm.dao.LimitListDao;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.utils.ToastUtils;

public class FrgBlackApp extends Fragment implements IMessageReceiveCallback {

	private static final int LOAD_COMPLETE = 1;

	private static final int LOAD_FAIL = 0;

	private ListView frg_limit_app_black_lv;

	private View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.frg_black_app, container, true);
		initView();
		initData();

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		AtyAppLimitMgr.messageReceiveListeners.add(this);
		L.d(this.getClass(), "onAttach FrgBlackApp ");
	}

	private void initView() {
		if (rootView != null) {
			frg_limit_app_black_lv = (ListView) rootView
					.findViewById(R.id.frg_limit_app_black_lv);
			frg_limit_app_black_lv.setVisibility(View.INVISIBLE);
		}
	}

	private void initData() {
		loadData();
	}

	private void rebuildView() {

		initView();
		initData();
	}

	protected Handler handler = new Handler() {

		private List<InstalledAppInfo> datas;
		private LimitListAdapter adapter;

		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case LOAD_COMPLETE:
				if (frg_limit_app_black_lv != null) {
					datas = (List<InstalledAppInfo>) msg.obj;
					adapter = new LimitListAdapter(getActivity(), datas,
							R.layout.frg_limitlist_item);
					frg_limit_app_black_lv.setAdapter(adapter);
					frg_limit_app_black_lv.setVisibility(View.VISIBLE);
				}
				break;

			case LOAD_FAIL:

//				ToastUtils.show(getActivity(), "黑名单列表为空");
				break;
			}
		};

	};

	/**
	 * 
	 * load data the listView required
	 */
	private void loadData() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					LimitListDao blackListDao = new LimitListDao(getActivity());
					List<InstalledAppInfo> datas = blackListDao.getFilteredPackageLimitList(blackListDao
							.getLimitListbyType(EMMContants.LIMITLIST_CONTANT.LT_BLACK_LIST));
					if (datas != null && datas.size() > 0) {
						handler.sendMessage(handler.obtainMessage(
								LOAD_COMPLETE, datas));
					} else {
						handler.sendMessage(handler.obtainMessage(LOAD_FAIL));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void onMessageReceived(String messageType) {
		rebuildView();
	}
}
