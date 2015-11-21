package com.foxconn.app.aty;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
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

import com.foxconn.app.IMessageReceiveCallback;
import com.charon.pulltorefreshlistview.PullRefreshAndLoadMoreListView.OnLoadMoreListener;
import com.charon.pulltorefreshlistview.PullToRefreshListView.OnRefreshListener;
import com.foxconn.app.R;
import com.foxconn.emm.adapter.NotificationAdapter;
import com.foxconn.emm.bean.NotificationInfo;
import com.foxconn.emm.dao.NotificationInfoDao;
import com.foxconn.emm.swipemenulistview.SwipeMenu;
import com.foxconn.emm.swipemenulistview.SwipeMenuCreator;
import com.foxconn.emm.swipemenulistview.SwipeMenuItem;
import com.foxconn.emm.swipemenulistview.SwipeMenuListView;
import com.foxconn.emm.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.foxconn.emm.swipemenulistview.SwipeMenuListView.OnSwipeListener;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.utils.ToastUtils;
import com.foxconn.emm.view.CommonMsgInfoDialog;

public class FrgNotification extends Fragment implements
		IMessageReceiveCallback {
	private List<NotificationInfo> notifiInfoList;
	private NotificationAdapter mAdapter;
	private SwipeMenuListView mListView;
	private NotificationInfoDao notifiDao;
	private View parentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		parentView = inflater.inflate(R.layout.frg_notification, container,
				false);
		initView();
		initData();
		return parentView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		AtyInfoCenter.messageReceiveListeners.add(this);
		L.d(this.getClass(), "onAttach FrgNotification ");

	}

	private void initView() {
		if (parentView != null) {
			mListView = (SwipeMenuListView) parentView
					.findViewById(R.id.listView);

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
					SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity().getApplicationContext());
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
			mListView.setOnRefreshListener(notificationLoadMore);
		}
	}

	private void initData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				notifiInfoList = new ArrayList<NotificationInfo>();
				notifiDao = new NotificationInfoDao(getActivity());
				notifiInfoList = notifiDao.findAll();
				if (notifiInfoList.size() > 0 && notifiInfoList != null) {
					handler.sendMessage(handler.obtainMessage(
							EMMContants.FrgNotiContants.LOAD_FINISH,
							notifiInfoList));
				} else {
					handler.sendMessage(handler.obtainMessage(
							EMMContants.FrgNotiContants.LOAD_ZERO, null));
				}
			}
		}).start();
	}

	private void rebuildView() {
		initView();
		initData();

	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case EMMContants.FrgNotiContants.LOAD_FINISH:
				mAdapter = new NotificationAdapter(getActivity(),
						notifiInfoList, R.layout.info_center_item);
				mListView.setAdapter(mAdapter);
				mListView.onRefreshComplete();
				break;

			case EMMContants.FrgNotiContants.LOAD_ZERO:
//				ToastUtils.show(getActivity(), "暂无通知");
				break;
			}
		}
	};

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	private OnRefreshListener notificationLoadMore = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			// TODO Auto-generated method stub
			initData();
		}
	};

	/**
	 * 详细信息
	 * 
	 * @param position
	 */
	private void open(int position) {
		NotifiInfoDialog dialog = new NotifiInfoDialog(getActivity(), position,
				EMMContants.DialogFlag.SHOW);
		dialog.show();
		dialog.setOneBtnDialog(EMMContants.MsgType.NOTIFICATION, notifiInfoList
				.get(position).getContent(),
				EMMContants.DialogText.NEGATIVEBUTTON);
	}

	/**
	 * 删除某条通知
	 * 
	 * @param position
	 */
	private void delete(int position) {
		if (notifiDao.delete(notifiInfoList.get(position).getId()) > 0) {
			notifiInfoList.remove(position);
			mAdapter.notifyDataSetChanged();
			Toast.makeText(getActivity(), EMMContants.ToastText.DELETSUCCESS,
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getActivity(), EMMContants.ToastText.DELETFail,
					Toast.LENGTH_SHORT).show();
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
				NotifiInfoDialog dialog = new NotifiInfoDialog(getActivity(),
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

	final class NotifiInfoDialog extends CommonMsgInfoDialog {
		private int position;

		public NotifiInfoDialog(Context context, int position, int flag) {
			super(context, position, flag);
			// TODO Auto-generated constructor stub
			this.position = position;
		}

		@Override
		public void deleteInfo() {
			// TODO Auto-generated method stub
			super.deleteInfo();
			delete(position);
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
		}
		@Override
		public void onSwipeEnd(int position) {
		}
	}

	final class ItemLongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				final int position, long id) {
			NotifiInfoDialog dialog = new NotifiInfoDialog(getActivity(),
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
