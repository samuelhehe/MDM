package com.foxconn.app.aty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.foxconn.emm.adapter.AppAdapter;
import com.foxconn.emm.bean.AppInfo;
import com.foxconn.emm.dao.AppInfoDao;
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

/**
 * 
 * 应用推送的性质与推送文件图片不同 1.应用推送是属于用户必须进行下载安装的,不需要用户进行点击下载. 2.需要用户进行点击安装. 实现思路
 * 1.需要开启一个线程进行后台下载,在ServiceIntentService中. 2.可以选择提醒用户进行安装 3.如果可以,选择静默安装.
 * 
 */
public class FrgApp extends Fragment implements IMessageReceiveCallback {
	private List<AppInfo> appInfoList;
	private AppAdapter appAdapter;
	private SwipeMenuListView mListView;
	private AppInfoDao appInfoDao;

	private View parentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		parentView = inflater.inflate(R.layout.frg_app, container, false);
		initView();
		initData();
		return parentView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		AtyInfoCenter.messageReceiveListeners.add(this);
		L.d(this.getClass(), "onAttach FrgApp ");
	}

	private void initView() {
		if (parentView != null) {

			mListView = (SwipeMenuListView) parentView
					.findViewById(R.id.app_listview);

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
					openItem.setTitle(EMMContants.DialogText.INSTALL);
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
				appInfoList = new ArrayList<AppInfo>();
				appInfoDao = new AppInfoDao(getActivity());
				appInfoList = appInfoDao.findAll();
				L.d(this.getClass(), appInfoList.toString());
				if (appInfoList != null && appInfoList.size() > 0) {
					handler.sendMessage(handler.obtainMessage(
							EMMContants.FrgNotiContants.LOAD_FINISH,
							appInfoList));
				} else {
					handler.sendMessage(handler.obtainMessage(
							EMMContants.FrgNotiContants.LOAD_ZERO, null));
				}
			}
		}).start();
	}

	public void rebuildView() {
		initView();
		initData();
	}

	@Override
	public void onMessageReceived(String messageType) {
		rebuildView();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EMMContants.FrgNotiContants.LOAD_FINISH:
				appAdapter = new AppAdapter(getActivity(), appInfoList,
						R.layout.frg_apps_item);
				mListView.setAdapter(appAdapter);
				mListView.onRefreshComplete();
				break;

			case EMMContants.FrgNotiContants.LOAD_ZERO:
//				ToastUtils.show(getActivity(), "暂无可下载应用！");
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

	private void open(int position) {
		AppInfoDialog dialog = new AppInfoDialog(getActivity(), position,
				EMMContants.DialogFlag.SHOW);
		dialog.show();
		dialog.setTwoBtnDialog(appInfoList.get(position).getAppName(),
				EMMContants.DialogText.DOWNLOADAPP,
				EMMContants.DialogText.DOWNLOAD,
				EMMContants.DialogText.NEGATIVEBUTTON);
	}

	/**
	 * 删除某条通知
	 * 
	 * @param position
	 */
	private void delete(int position) {
		File file = new File(EMMContants.LocalConf.getEMMLocalHost_dirPath()
				+ EMMContants.LocalConf.UpdateApp_dirpath, appInfoList.get(
				position).getPackageName()
				+ ".apk");
		if (file.exists()) {
			file.delete();
		}
		if (appInfoDao.delete(appInfoList.get(position).getId()) > 0) {
			appInfoList.remove(position);
			appAdapter.notifyDataSetChanged();
			Toast.makeText(getActivity(), EMMContants.ToastText.DELETSUCCESS,
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getActivity(), EMMContants.ToastText.DELETFail,
					Toast.LENGTH_SHORT).show();
		}
	}

	final class AppInfoDialog extends CommonMsgInfoDialog {
		private int position;

		public AppInfoDialog(Context context, int position, int flag) {
			super(context, position, flag);
			// TODO Auto-generated constructor stub
			this.position = position;
		}

		@Override
		public void showInfo() {
			// TODO Auto-generated method stub
			super.showInfo();
			// 加入下载队列
		}

		@Override
		public void deleteInfo() {
			// TODO Auto-generated method stub
			super.deleteInfo();
			delete(position);
		}
	}

	/**
	 * install app
	 * 
	 * @param context
	 * @param filePath
	 * @return whether apk exist
	 */
	public static boolean install(Context context, String filePath) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		File file = new File(filePath);
		if (file != null && file.length() > 0 && file.exists() && file.isFile()) {
			i.setDataAndType(Uri.parse("file://" + filePath),
					"application/vnd.android.package-archive");
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
			return true;
		}
		return false;
	}

	private void installApp(int position) {
		File file = new File(EMMContants.LocalConf.getEMMLocalHost_dirPath()
				+ EMMContants.LocalConf.UpdateApp_dirpath, appInfoList.get(
				position).getPackageName()
				+ ".apk");
		if (file.exists()) {
			if (file.length() >= Long.valueOf(appInfoList.get(position)
					.getFileSize()) * 1024) {
				install(getActivity(),
						EMMContants.LocalConf.getEMMLocalHost_dirPath()
								+ EMMContants.LocalConf.UpdateApp_dirpath
								+ appInfoList.get(position).getPackageName()
								+ ".apk");
			} else {
				AppInfoDialog dialog = new AppInfoDialog(getActivity(),
						position, EMMContants.DialogFlag.DELETE);
				dialog.show();
				dialog.setOneBtnDialog(appInfoList.get(position).getAppName(),
						"正在下载中，请稍后", EMMContants.DialogText.NEGATIVEBUTTON);
			}
		} else {
			AppInfoDialog dialog = new AppInfoDialog(getActivity(), position,
					EMMContants.DialogFlag.DELETE);
			dialog.show();
			dialog.setTwoBtnDialog(EMMContants.DialogText.DELETETITLE,
					"该APP已不存在，是否删除此记录？", EMMContants.DialogText.POSITIVEBUTTON,
					EMMContants.DialogText.NEGATIVEBUTTON);
		}
	}

	final class MenuItemClickListener implements OnMenuItemClickListener {

		@Override
		public void onMenuItemClick(int position, SwipeMenu menu, int index) {
			// TODO Auto-generated method stub
			switch (index) {
			case 0:
				// / 检测,如果apk文件下载好了,直接调用安装 ,否则提醒用户添加安装.
				installApp(position);
				break;

			case 1:
				AppInfoDialog dialog = new AppInfoDialog(getActivity(),
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
			// open(position);
			installApp(position - 1);
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
			AppInfoDialog dialog = new AppInfoDialog(getActivity(),
					position - 1, EMMContants.DialogFlag.DELETE);
			dialog.show();
			dialog.setTwoBtnDialog(EMMContants.DialogText.DELETETITLE,
					EMMContants.DialogText.DELETECONTECT,
					EMMContants.DialogText.POSITIVEBUTTON,
					EMMContants.DialogText.NEGATIVEBUTTON);
			return false;
		}
	}

}
