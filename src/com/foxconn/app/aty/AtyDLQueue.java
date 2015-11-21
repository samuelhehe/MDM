package com.foxconn.app.aty;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ExpandableListView;

import com.ab.view.titlebar.AbTitleBar;
import com.artifex.mupdfdemo.MuPDFActivity;
import com.foxconn.app.App;
import com.foxconn.app.R;
import com.foxconn.emm.adapter.DownloadQueueExpandableListAdapter;
import com.foxconn.emm.utils.Constant;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.lib.download.DownFile;
import com.foxconn.lib.download.DownFileDao;

public class AtyDLQueue extends EMMBaseActivity {
	private static final int FLIP_DISTANCE = 230;
	private GestureDetector detector;
	protected static final String TAG = "DownListActivity";
	protected static final boolean DEBUG = EMMContants.DEBUG;
	private App app;
	private DownFileDao downFileDao;

	private ArrayList<DownFile> mDownFileList1 = null;
	private ArrayList<DownFile> mDownFileList2 = null;
	private ArrayList<ArrayList<DownFile>> mGroupDownFileList = null;
	private DownloadQueueExpandableListAdapter mExpandableListAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.aty_residemenu_downloadqueue);
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("下载队列");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(android.R.color.holo_blue_light);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		initView();
		app = (App) abApplication;
		downFileDao = app.getmDownFileDao();

		mDownFileList1 = new ArrayList<DownFile>();
		mDownFileList2 = new ArrayList<DownFile>();
		mGroupDownFileList = new ArrayList<ArrayList<DownFile>>();
		mGroupDownFileList.add(mDownFileList1);
		mGroupDownFileList.add(mDownFileList2);

		String[] mDownFileGroupTitle = new String[] {
				this.getResources().getString(R.string.download_complete_title),
				this.getResources().getString(R.string.undownLoad_title) };

		// 创建一个BaseExpandableListAdapter对象
		mExpandableListAdapter = new DownloadQueueExpandableListAdapter(this,
				mGroupDownFileList, mDownFileGroupTitle);
		ExpandableListView mExpandListView = (ExpandableListView) findViewById(R.id.mexpandablelistview);
		mExpandListView.setAdapter(mExpandableListAdapter);
		// Indicator靠右
		int width = getWindowManager().getDefaultDisplay().getWidth();
		mExpandListView.setIndicatorBounds(width - 40, width - 25);
		mExpandListView.setChildIndicatorBounds(5, 53);
//		mExpandListView.setOnChildClickListener(new OnChildClickListener() {
//			@Override
//			public boolean onChildClick(ExpandableListView parent, View v,
//					int groupPosition, int childPosition, long id) {
//				DownFile downFile = mExpandableListAdapter.getChild(
//						groupPosition, childPosition);
//				if (TextUtils.isEmpty(downFile.getDownPath())) {
//					
//					showToast("文件打开失败...请检查是否下载完成");
//					return true;
//				}
//				if (DEBUG)
//					Log.i(TAG,
//							"downFile--->>>" + downFile.getSuffix() + "  "
//									+ downFile.getDownPath() + " "
//									+ downFile.getDownUrl() + " "
//									+ downFile.getTotalLength() + "  "
//									+ new File(downFile.getDownPath()).length());
//				open(downFile);
//				return true;
//			}
//		});
		initDownList();
	}

	public void open(DownFile downFile) {

		String path = EMMContants.LocalConf.getEMMLocalHost_dirPath()
				+ EMMContants.LocalConf.download_dirpath + downFile.getName()
				+ downFile.getSuffix();
		Uri uri = Uri.parse(path);
		Intent intent = new Intent(this, MuPDFActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(uri);
		startActivity(intent);
	}

	private void initDownList() {
		if (downFileDao != null) {
			List<DownFile> allDownFiles = downFileDao.getAllDownFiles();
			if (allDownFiles != null) {
				for (DownFile downFile : allDownFiles) {
					if (downFile != null) {
						// // 下载的长度和已经下载的长度相等
						if (downFile.getDownLength() == downFile
								.getTotalLength()
								&& downFile.getTotalLength() != 0) {
							downFile.setState(Constant.downloadComplete);
							mDownFileList1.add(downFile);
							mExpandableListAdapter.notifyDataSetChanged();
						} else {
							// / 不等或者为0的情况
							if (downFile.getDownLength() < downFile
									.getTotalLength()
									&& downFile.getTotalLength() > 0) {
								// 显示为暂停状态
//								downFile.setState(Constant.downLoadPause);
								mDownFileList2.add(downFile);
								mExpandableListAdapter.notifyDataSetChanged();
							} else if (downFile.getTotalLength() <= 0) {
//								downFile.setState(Constant.undownLoad);
								mDownFileList2.add(downFile);
								mExpandableListAdapter.notifyDataSetChanged();
							}
						}
					} else {
					}
				}
				// / 说明下载队列中没有任务
			} else {
				this.showToast("暂时没有文件下载记录");
			}
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		app.onLowMemory();
	}

	@Override
	public void finish() {
		super.finish();
		// 释放所有的下载线程
		mExpandableListAdapter.releaseThread();
	}

	private void initView() {
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

			public boolean onTouch(View v, MotionEvent event) {
				return detector.onTouchEvent(event);
			}
		});
	}

}
