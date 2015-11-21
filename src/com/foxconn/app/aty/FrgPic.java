package com.foxconn.app.aty;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

import com.ab.util.AbFileUtil;
import com.charon.pulltorefreshlistview.PullToRefreshListView.OnRefreshListener;
import com.foxconn.app.App;
import com.foxconn.app.IMessageReceiveCallback;
import com.foxconn.app.R;
import com.foxconn.emm.adapter.PicAdapter;
import com.foxconn.emm.bean.PicInfo;
import com.foxconn.emm.dao.PicInfoDao;
import com.foxconn.emm.swipemenulistview.SwipeMenu;
import com.foxconn.emm.swipemenulistview.SwipeMenuCreator;
import com.foxconn.emm.swipemenulistview.SwipeMenuItem;
import com.foxconn.emm.swipemenulistview.SwipeMenuListView;
import com.foxconn.emm.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.foxconn.emm.swipemenulistview.SwipeMenuListView.OnSwipeListener;
import com.foxconn.emm.utils.Constant;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.utils.TextFormater;
import com.foxconn.emm.utils.ToastUtils;
import com.foxconn.emm.view.CommonMsgInfoDialog;
import com.foxconn.lib.download.AbFileDownloader;
import com.foxconn.lib.download.DownFile;
import com.foxconn.lib.download.DownFileDao;

public class FrgPic extends Fragment implements IMessageReceiveCallback {
	private List<PicInfo> picInfoList;
	private PicAdapter picAdapter;
	private SwipeMenuListView mListView;
	private PicInfoDao picInfoDao;

	private DownFileDao downFileDao;
	private SwipeMenuItem openItem;
	private View parentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.frg_pic, container, false);
		initView();
		initData();
		return parentView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		AtyInfoCenter.messageReceiveListeners.add(this);
		L.d(this.getClass(), "onAttach FrgPic ");
	}
	private void initView() {
		if (parentView != null) {
			mListView = (SwipeMenuListView) parentView.findViewById(R.id.pic_listview);
			SwipeMenuCreator creator = new SwipeMenuCreator() {
				@Override
				public void create(SwipeMenu menu) {
					// create "open" item
					// openItem = new SwipeMenuItem(getActivity()
					// .getApplicationContext());
					// // set item background
					// openItem.setBackground(new ColorDrawable(Color.rgb(0xC9,
					// 0xC9,
					// 0xCE)));
					// // set item width
					// openItem.setWidth(dp2px(90));
					// // set item title
					// openItem.setTitle(EMMContants.DialogText.DOWNLOAD);
					// // set item title fontsize
					// openItem.setTitleSize(18);
					// // set item title font color
					// openItem.setTitleColor(Color.WHITE);
					// // add to menu
					// menu.addMenuItem(openItem);

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
		downFileDao = App.getInstance().getmDownFileDao();
		new Thread(new Runnable() {
			@Override
			public void run() {
				picInfoList = new ArrayList<PicInfo>();
				picInfoDao = new PicInfoDao(getActivity());
				picInfoList = picInfoDao.findAll();
				L.d(this.getClass(), picInfoList.toString());
				if (picInfoList != null && picInfoList.size() > 0) {
					handler.sendMessage(handler.obtainMessage(
							EMMContants.FrgNotiContants.LOAD_FINISH,
							picInfoList));
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
				picAdapter = new PicAdapter(getActivity(), picInfoList,
						R.layout.info_center_item);
				mListView.setAdapter(picAdapter);
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
		if (picInfoList.get(position).getDeadline().equals("")
				|| System.currentTimeMillis() < TextFormater
						.getTimeToLong(picInfoList.get(position).getDeadline())) {
			if (picIsExists(position)) {
				if (picInfoList.get(position).getPassword().equals("")) {
					PicInfoDialog dialog = new PicInfoDialog(getActivity(),
							position, EMMContants.DialogFlag.OPEN, picInfoList
									.get(position).getSendTime());
					dialog.show();
					dialog.setOpenFielDailog(picInfoList.get(position)
							.getSubject(), picInfoList.get(position)
							.getContent(), EMMContants.DialogText.OPEN,
							EMMContants.DialogText.NEGATIVEBUTTON, picInfoList
									.get(position).getImg_list());
				} else {
					PicInfoDialog dialog = new PicInfoDialog(getActivity(),
							position, EMMContants.DialogFlag.INPUT_PWD,
							picInfoList.get(position).getSendTime());
					dialog.show();
					dialog.setInputPwdDailog(picInfoList.get(position)
							.getSubject(), EMMContants.DialogText.CONTACT
							+ picInfoList.get(position).getContact(),
							EMMContants.DialogText.OPEN,
							EMMContants.DialogText.NEGATIVEBUTTON, picInfoList
									.get(position).getPassword(), picInfoList
									.get(position).getImg_list());
				}
			} else {
				PicInfoDialog dialog = new PicInfoDialog(getActivity(),
						position, EMMContants.DialogFlag.SHOW);
				dialog.show();
				dialog.setTwoBtnDialog(picInfoList.get(position).getSubject(),
						picInfoList.get(position).getContent(),
						EMMContants.DialogText.DOWNLOAD,
						EMMContants.DialogText.NEGATIVEBUTTON);
			}
		} else {
			PicInfoDialog dialog = new PicInfoDialog(getActivity(), position,
					EMMContants.DialogFlag.DELETE);
			dialog.show();
			dialog.setTwoBtnDialog(EMMContants.DialogText.PROMPT,
					EMMContants.DialogText.PROMPT_CONTENT,
					EMMContants.DialogText.DELETETITLE,
					EMMContants.DialogText.NEGATIVEBUTTON);
		}

	}

	/**
	 * 判断文件是否存在并且已下载完成
	 * 
	 * @param position
	 * @return
	 */
	private boolean picIsExists(int position) {
		File localFile = new File(new PicInfo().getPicFilePath()
				+ new PicInfo().getFileName(picInfoList.get(position)
						.getSubject(), picInfoList.get(position).getSendTime()));
		if (localFile.exists()) {
			DownFileDao downFileDao = new DownFileDao(getActivity());
			StringTokenizer st = new StringTokenizer(picInfoList.get(position)
					.getImg_list(), ",", true);
			int count = st.countTokens() / 2;
			while (st.hasMoreElements()) {
				String url = st.nextToken();
				if (!url.equals(",")) {
					DownFile downFile = new DownFile();
					downFile = downFileDao.getDownFile(url);
					if (downFile == null) {
						count--;
					} else if (downFile.getState() == Constant.downloadComplete) {
						count--;
					}
				}
			}
			if (count == 0) {
				return true;
			}
		}
		return false;
	}

	public void addDLQueue(int position) {

		String urlList = picInfoList.get(position).getImg_list();
		urlList = urlList.substring(0, urlList.length() - 1);
		String[] split = urlList.split(",");
		List<String> list = Arrays.asList(split);
		int i = 0;
		List<DownFile> downFiles = new ArrayList<DownFile>();
		for (String url : list) {
			i++;
			DownFile downFile = new DownFile();
			downFile.setName(new PicInfo().getPicName(picInfoList.get(position)
					.getSubject(), i, picInfoList.get(position).getSendTime()));
			downFile.setState(Constant.undownLoad);
			downFile.setDescription(picInfoList.get(position).getContent());
			downFile.setDownUrl(url);
			downFile.setState(Constant.downInProgress);
			String suffix = getSuffix(url);
			if (!TextUtils.isEmpty(suffix)) {
				downFile.setSuffix(suffix);
				downFile.setDownPath(new PicInfo().getPicPath(
						picInfoList.get(position).getSubject(), i, suffix,
						picInfoList.get(position).getSendTime()));
			} else {
				// /默认类型.
				downFile.setSuffix(".png");
				downFile.setDownPath(new PicInfo().getPicPath(
						picInfoList.get(position).getSubject(), i, ".png",
						picInfoList.get(position).getSendTime()));
			}
			if (!EMMContants.LocalConf.isSdPresent()) {
				ToastUtils.show(getActivity(), "SD卡不存在!");
			}
			L.d(this.getClass(), url);
			long save = downFileDao.save(downFile);
			if (save > 0) {
				ToastUtils
						.show(getActivity(),
								"第"
										+ i
										+ "张图片"
										+ picInfoList.get(position)
												.getSubject() + "已成功添加至下载队列");
			} else {
				ToastUtils.show(getActivity(), "第" + i + "张图片"
						+ picInfoList.get(position).getSubject() + "添加至下载队列失败");
			}
			downFiles.add(downFile);
		}
		AutoDownloadTask download = new AutoDownloadTask();
		download.execute(downFiles);
	}

	protected class AutoDownloadTask extends
			AsyncTask<List<DownFile>, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(List<DownFile>... params) {
			// TODO Auto-generated method stub
			try {
				for (DownFile downFile : params[0]) {
					// 检查文件总长度
					int totalLength = AbFileUtil
							.getContentLengthFormUrl(downFile.getDownUrl());
					downFile.setTotalLength(totalLength);
					// 开始下载文件
					AbFileDownloader loader = new AbFileDownloader(
							getActivity(), downFile, 2);
					// mFileDownloaders.put(
					// mDownFile.getDownUrl(), loader);
					loader.download(null);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}

	private String getSuffix(String fileurl) {
		String suffix = "";
		if (fileurl.lastIndexOf(".") != -1) {
			suffix = fileurl.substring(fileurl.lastIndexOf("."));
			if (suffix.indexOf("/") != -1 || suffix.indexOf("?") != -1
					|| suffix.indexOf("&") != -1) {
				suffix = null;
			}
		}
		return suffix;
	}

	/**
	 * 删除某条通知
	 * 
	 * @param position
	 */
	private void delete(int position) {
		DownFileDao dao = new DownFileDao(getActivity());
		StringTokenizer st = new StringTokenizer(picInfoList.get(position)
				.getImg_list(), ",", true);
		while (st.hasMoreElements()) {
			String url = st.nextToken();
			if (!url.equals(",")) {
				dao.delete(url);
			}
		}
		File file = new File(new PicInfo().getPicFilePath()
				+ new PicInfo().getFileName(picInfoList.get(position)
						.getSubject(), picInfoList.get(position).getSendTime()));
		if (file.exists()) {
			delete(file);
		}
		if (picInfoDao.delete(picInfoList.get(position).getId()) > 0) {
			picInfoList.remove(position);
			picAdapter.notifyDataSetChanged();
			Toast.makeText(getActivity(), EMMContants.ToastText.DELETSUCCESS,
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getActivity(), EMMContants.ToastText.DELETFail,
					Toast.LENGTH_SHORT).show();
		}
	}

	private void delete(File file) {
		// TODO Auto-generated method stub
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				delete(files[i]);
			} else {
				files[i].delete();
			}
		}
	}

	final class PicInfoDialog extends CommonMsgInfoDialog {
		private int position;
		private int flag;

		public PicInfoDialog(Context context, int position, int flag) {
			super(context, position, flag);
			// TODO Auto-generated constructor stub
			this.position = position;
			this.flag = flag;
		}

		public PicInfoDialog(Context context, int position, int flag,
				String sendTime) {
			super(context, position, flag, sendTime);
			// TODO Auto-generated constructor stub
			this.position = position;
			this.flag = flag;
		}

		@Override
		public void showInfo() {
			// TODO Auto-generated method stub
			super.showInfo();
			addDLQueue(position);
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
			PicInfoDialog dialog = new PicInfoDialog(getActivity(), position,
					EMMContants.DialogFlag.DELETE);
			dialog.show();
			dialog.setTwoBtnDialog(EMMContants.DialogText.DELETETITLE,
					EMMContants.DialogText.DELETECONTECT,
					EMMContants.DialogText.POSITIVEBUTTON,
					EMMContants.DialogText.NEGATIVEBUTTON);
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
			PicInfoDialog dialog = new PicInfoDialog(getActivity(),
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
