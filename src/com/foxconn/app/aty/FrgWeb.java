package com.foxconn.app.aty;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

import com.charon.pulltorefreshlistview.PullToRefreshListView.OnRefreshListener;
import com.foxconn.app.IMessageReceiveCallback;
import com.foxconn.app.R;
import com.foxconn.emm.adapter.WebPageAdapter;
import com.foxconn.emm.bean.WebPageInfo;
import com.foxconn.emm.dao.WebPageInfoDao;
import com.foxconn.emm.swipemenulistview.SwipeMenu;
import com.foxconn.emm.swipemenulistview.SwipeMenuCreator;
import com.foxconn.emm.swipemenulistview.SwipeMenuItem;
import com.foxconn.emm.swipemenulistview.SwipeMenuListView;
import com.foxconn.emm.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.foxconn.emm.swipemenulistview.SwipeMenuListView.OnSwipeListener;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.utils.TextFormater;
import com.foxconn.emm.utils.ToastUtils;
import com.foxconn.emm.view.CommonMsgInfoDialog;

public class FrgWeb extends Fragment implements IMessageReceiveCallback {
	private List<WebPageInfo> webPageInfoList;
	private WebPageAdapter webPageAdapter;
	private SwipeMenuListView mListView;
	private WebPageInfoDao webPageInfoDao;
	private View parentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.frg_web, container, false);
		initView();
		initData();
		return parentView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		AtyInfoCenter.messageReceiveListeners.add(this);
		L.d(this.getClass(), "onAttach FrgWeb ");
	}

	private void initView() {
		if (parentView != null) {
			mListView = (SwipeMenuListView) parentView.findViewById(R.id.web_listview);
			// step 1. create a MenuCreator
			SwipeMenuCreator creator = new SwipeMenuCreator() {
				@Override
				public void create(SwipeMenu menu) {
					// create "open" item
					SwipeMenuItem openItem = new SwipeMenuItem(getActivity()
							.getApplicationContext());
					// set item background
					openItem.setBackground(new ColorDrawable(Color.rgb(0xC9,
							0xC9, 0xCE)));
					// set item width
					openItem.setWidth(dp2px(90));
					// set item title
					openItem.setTitle(EMMContants.DialogText.OPEN);
					// set item title fontsize
					openItem.setTitleSize(18);
					// set item title font color
					openItem.setTitleColor(Color.WHITE);
					// add to menu
					menu.addMenuItem(openItem);

					// create "delete" item
					SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity()
							.getApplicationContext());
					// set item background
					deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
							0x3F, 0x25)));
					// set item width
					deleteItem.setWidth(dp2px(90));
					// set a icon
					deleteItem.setIcon(R.drawable.ic_delete);
					// add to menu
					menu.addMenuItem(deleteItem);
				}
			};

			mListView.setMenuCreator(creator);
			mListView.setOnMenuItemClickListener(new MenuItemClickListener());
			mListView.setOnItemClickListener(new ItemClickListener());
			mListView.setOnItemLongClickListener(new ItemLongClickListener());
			mListView.setOnSwipeListener(new ItemSwipeListener());
			mListView.setOnRefreshListener(refreshMore);
		}
	
	}

	private void initData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				webPageInfoList = new ArrayList<WebPageInfo>();
				webPageInfoDao = new WebPageInfoDao(getActivity());
				webPageInfoList = webPageInfoDao.findAll();
				L.d(this.getClass(), webPageInfoList.toString());
				if (webPageInfoList != null && webPageInfoList.size() > 0) {
					handler.sendMessage(handler.obtainMessage(
							EMMContants.FrgNotiContants.LOAD_FINISH,
							webPageInfoList));
				} else {
					handler.sendMessage(handler.obtainMessage(
							EMMContants.FrgNotiContants.LOAD_ZERO, null));
				}
			}
		}).start();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case EMMContants.FrgNotiContants.LOAD_FINISH:
				webPageAdapter = new WebPageAdapter(getActivity(),
						webPageInfoList, R.layout.info_center_item);
				mListView.setAdapter(webPageAdapter);
				mListView.onRefreshComplete();
				break;

			case EMMContants.FrgNotiContants.LOAD_ZERO:
//				ToastUtils.show(getActivity(), "暂无网页信息！");
				break;
			}
		}
	};

	private void rebuildView() {
		initView();
		initData();
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	private OnRefreshListener refreshMore = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			// TODO Auto-generated method stub
			initData();
		}
	};

	private void open(int position) {
		if (webPageInfoList.get(position).getDeadline().equals("")
				|| System.currentTimeMillis() < TextFormater
						.getTimeToLong(webPageInfoList.get(position)
								.getDeadline())) {
			WebInfoDailog dialog = new WebInfoDailog(getActivity(), position,
					EMMContants.DialogFlag.SHOW);
			dialog.show();
			dialog.setTwoBtnDialog(webPageInfoList.get(position).getSubject(),
					webPageInfoList.get(position).getContent(),
					EMMContants.DialogText.OPEN,
					EMMContants.DialogText.NEGATIVEBUTTON);
		} else {
			WebInfoDailog dialog = new WebInfoDailog(getActivity(), position,
					EMMContants.DialogFlag.DELETE);
			dialog.show();
			dialog.setTwoBtnDialog(EMMContants.DialogText.PROMPT,
					EMMContants.DialogText.PROMPT_CONTENT,
					EMMContants.DialogText.DELETETITLE,
					EMMContants.DialogText.NEGATIVEBUTTON);
		}
	}

	/**
	 * 打开网页
	 * 
	 * @param position
	 */
	private void openWebPage(int position) {
		Intent i = new Intent(getActivity(), ShowWebpage.class);
		i.putExtra(WebPageInfo.TAG.SUBJECT, webPageInfoList.get(position)
				.getSubject());
		i.putExtra(WebPageInfo.TAG.PAGEURL, webPageInfoList.get(position)
				.getPageUrl());
		startActivity(i);
	}

	/**
	 * 删除某条通知
	 * 
	 * @param position
	 */
	private void delete(int position) {
		if (webPageInfoDao.delete(webPageInfoList.get(position).getId()) > 0) {
			webPageInfoList.remove(position);
			webPageAdapter.notifyDataSetChanged();
			Toast.makeText(getActivity(), EMMContants.ToastText.DELETSUCCESS,
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getActivity(), EMMContants.ToastText.DELETFail,
					Toast.LENGTH_SHORT).show();
		}
	}

	final class WebInfoDailog extends CommonMsgInfoDialog {
		private int position;

		public WebInfoDailog(Context context, int position, int flag) {
			super(context, position, flag);
			// TODO Auto-generated constructor stub
			this.position = position;
		}

		@Override
		public void showInfo() {
			// TODO Auto-generated method stub
			super.showInfo();
			openWebPage(position);
		}

		@Override
		public void deleteInfo() {
			// TODO Auto-generated method stub
			super.deleteInfo();
			delete(position);
		}

	}

	final class MenuItemClickListener implements OnMenuItemClickListener {

		@Override
		public void onMenuItemClick(int position, SwipeMenu menu, int index) {
			// TODO Auto-generated method stub
			switch (index) {
			case 0:
				open(position);
				break;

			case 1:
				WebInfoDailog dialog = new WebInfoDailog(getActivity(),
						position, EMMContants.DialogFlag.DELETE);
				dialog.show();
				dialog.setTwoBtnDialog(EMMContants.DialogText.DELETETITLE,
						EMMContants.DialogText.DELETECONTECT,
						EMMContants.DialogText.POSITIVEBUTTON,
						EMMContants.DialogText.NEGATIVEBUTTON);
				break;
			}
		}
	}

	final class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			open(position - 1);
		}
	}

	final class ItemSwipeListener implements OnSwipeListener {

		@Override
		public void onSwipeStart(int position) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSwipeEnd(int position) {
			// TODO Auto-generated method stub

		}

	}

	final class ItemLongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				final int position, long id) {
			// TODO Auto-generated method stub
			WebInfoDailog dialog = new WebInfoDailog(getActivity(),
					position - 1, EMMContants.DialogFlag.DELETE);
			dialog.show();
			dialog.setTwoBtnDialog(EMMContants.DialogText.DELETETITLE,
					EMMContants.DialogText.DELETECONTECT,
					EMMContants.DialogText.POSITIVEBUTTON,
					EMMContants.DialogText.NEGATIVEBUTTON);
			return false;
		}
	}

	@Override
	public void onMessageReceived(String messageType) {
		rebuildView();
	}
}
