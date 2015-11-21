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
import android.os.Message;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
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
import com.charon.pulltorefreshlistview.PullToRefreshListView.OnRefreshListener;
import com.foxconn.app.R;
import com.foxconn.emm.adapter.CalendarAdapter;
import com.foxconn.emm.bean.CalendarInfo;
import com.foxconn.emm.dao.CalendarInfoDao;
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

public class FrgNotifiCalendar extends Fragment implements
		IMessageReceiveCallback {
	private List<CalendarInfo> calendarInfoList;
	private CalendarAdapter calendarAdapter;
	private SwipeMenuListView mListView;
	private CalendarInfoDao calendarDao;

	private View parentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.frg_notification_calendar,
				container, false);
		initView();
		initData();
		return parentView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		AtyInfoCenter.messageReceiveListeners.add(this);
		L.d(this.getClass(), "onAttach FrgNotifiCalendar ");
	}

	private void initView() {
		if (parentView != null) {
			mListView = (SwipeMenuListView) parentView
					.findViewById(R.id.calendar_listview);
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
					openItem.setTitle(EMMContants.DialogText.SETTINGCALENDAR);
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
				calendarInfoList = new ArrayList<CalendarInfo>();
				calendarDao = new CalendarInfoDao(getActivity());
				calendarInfoList = calendarDao.findAll();
				L.d(this.getClass(), calendarInfoList.toString());
				if (calendarInfoList != null && calendarInfoList.size() > 0) {
					handler.sendMessage(handler.obtainMessage(
							EMMContants.FrgNotiContants.LOAD_FINISH,
							calendarInfoList));
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
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EMMContants.FrgNotiContants.LOAD_FINISH:
				calendarAdapter = new CalendarAdapter(getActivity(),
						calendarInfoList, R.layout.info_center_item);
				mListView.setAdapter(calendarAdapter);
				mListView.onRefreshComplete();
				break;

			case EMMContants.FrgNotiContants.LOAD_ZERO:
				// ToastUtils.show(getActivity(), "暂无日历信息！");
				break;
			}
		}
	};

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

	/**
	 * 查看详细信息
	 * 
	 * @param position
	 */
	private void open(final int position) {
		CalendarInfoDialog dialog = new CalendarInfoDialog(getActivity(),
				position, EMMContants.DialogFlag.SHOW);
		dialog.show();
		dialog.setTwoBtnDialog(calendarInfoList.get(position).getSubject(),
				parseContent(calendarInfoList.get(position)),
				EMMContants.DialogText.SETTINGCALENDAR,
				EMMContants.DialogText.NEGATIVEBUTTON);
	}

	private String parseContent(CalendarInfo calendarInfo) {
		String content = "內容: " + calendarInfo.getContent() + '\n' + "是否全天: "
				+ (calendarInfo.getIsAllDay().equals(1 + "") ? "是" : "否")
				+ '\n' + "地点: " + calendarInfo.getPlace() + '\n' + "开始时间: "
				+ calendarInfo.getStartTime() + '\n' + "结束时间: "
				+ calendarInfo.getEndTime();
		return content;
	}

	/**
	 * 删除某条通知
	 * 
	 * @param position
	 */
	private void delete(int position) {
		if (calendarDao.delete(calendarInfoList.get(position).getId()) > 0) {
			calendarInfoList.remove(position);
			calendarAdapter.notifyDataSetChanged();
			Toast.makeText(getActivity(), EMMContants.ToastText.DELETSUCCESS,
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getActivity(), EMMContants.ToastText.DELETFail,
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 设置日历
	 * 
	 * @param calendarInfo
	 */
	private void setCalendar(CalendarInfo calendarInfo) {
		boolean allDay = Boolean.parseBoolean(calendarInfo.getIsAllDay());
		Intent i = new Intent(Intent.ACTION_INSERT)
				.setData(Events.CONTENT_URI)
				.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
						TextFormater.getTimeToLong(calendarInfo.getStartTime()))
				.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
						TextFormater.getTimeToLong(calendarInfo.getEndTime()))
				.putExtra(Events.TITLE, calendarInfo.getSubject())
				.putExtra(Events.ALL_DAY, allDay)
				.putExtra(Events.DESCRIPTION, calendarInfo.getSubject())
				.putExtra(Events.EVENT_LOCATION, calendarInfo.getPlace())
				.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
		// .putExtra(Intent.EXTRA_EMAIL,
		// "rowan@example.com,trevor@example.com");
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getActivity().startActivity(i);
	}

	final class CalendarInfoDialog extends CommonMsgInfoDialog {
		private int position;
		private int flag;

		public CalendarInfoDialog(Context context, int position, int flag) {
			super(context, position, flag);
			// TODO Auto-generated constructor stub
			this.position = position;
			this.flag = flag;
		}

		@Override
		public void showInfo() {
			// TODO Auto-generated method stub
			super.showInfo();
			setCalendar(calendarInfoList.get(position));
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
				CalendarInfoDialog dialog = new CalendarInfoDialog(
						getActivity(), position, EMMContants.DialogFlag.DELETE);
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
			CalendarInfoDialog dialog = new CalendarInfoDialog(getActivity(),
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
