package com.foxconn.app.aty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ab.util.AbFileUtil;
import com.charon.pulltorefreshlistview.PullToRefreshListView.OnRefreshListener;
import com.foxconn.app.App;
import com.foxconn.app.IMessageReceiveCallback;
import com.foxconn.app.R;
import com.foxconn.emm.adapter.FileAdapter;
import com.foxconn.emm.bean.FileInfo;
import com.foxconn.emm.dao.FileInfoDao;
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

public class FrgFile extends Fragment implements OnClickListener,
		OnFocusChangeListener, OnKeyListener, IMessageReceiveCallback {
	private List<FileInfo> fileInfoList;
	private FileAdapter fileAdapter;
	private SwipeMenuListView mListView;
	private FileInfoDao fileInfoDao;
	private App app;
	private DownFileDao downFileDao;
	private SwipeMenuItem openItem;
	private EditText et_search_keyword;
	private Button btn_clear_text;
	private View parentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		parentView = inflater.inflate(R.layout.frg_file, container, false);
		initView();
		initData();
		return parentView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		AtyInfoCenter.messageReceiveListeners.add(this);
		L.d(this.getClass(), "onAttach FrgFile ");

	}

	private void initView() {
		if (parentView != null) {

			mListView = (SwipeMenuListView) parentView.findViewById(R.id.list);
			et_search_keyword = (EditText) parentView
					.findViewById(R.id.et_search_keyword);
			btn_clear_text = (Button) parentView
					.findViewById(R.id.btn_clear_text);

			// step 1. create a MenuCreator
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
					// openItem.setTitle("下载");
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
			et_search_keyword.setInputType(InputType.TYPE_NULL);
			et_search_keyword.addTextChangedListener(new TextChangedListener());
			et_search_keyword.setOnTouchListener(new TouchListener());
			et_search_keyword.setOnFocusChangeListener(this);
			btn_clear_text.setOnClickListener(this);
		}
	}

	private void initData() {
		downFileDao = App.getInstance().getmDownFileDao();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				fileInfoList = new ArrayList<FileInfo>();
				fileInfoDao = new FileInfoDao(getActivity());
				fileInfoList = fileInfoDao.findAll();
				L.d(getClass(), fileInfoList.toString());
				if (fileInfoList != null && fileInfoList.size() > 0) {
					handler.sendMessage(handler.obtainMessage(
							EMMContants.FrgNotiContants.LOAD_FINISH,
							fileInfoList));
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
				fileAdapter = new FileAdapter(getActivity(), fileInfoList,
						R.layout.info_center_item);
				mListView.setAdapter(fileAdapter);
				mListView.onRefreshComplete();
				break;

			case EMMContants.FrgNotiContants.LOAD_ZERO:
//				ToastUtils.show(getActivity(), "暂无可下载文件！");
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
			initData();
		}
	};

	private void open(int position) {
		if (fileInfoList.get(position).getDeadline().equals("")
				|| System.currentTimeMillis() < TextFormater
						.getTimeToLong(fileInfoList.get(position).getDeadline())) {

			if (fileIsExists(position)) {
				// openItem.setTitle(EMMContants.DialogFlag.OPEN);
				if (fileInfoList.get(position).getPassword().equals("")) {
					FileInfoDialog dialog = new FileInfoDialog(getActivity(),
							position, EMMContants.DialogFlag.OPEN, fileInfoList
									.get(position).getSendTime());
					dialog.show();
					dialog.setOpenFielDailog(fileInfoList.get(position)
							.getSubject(), fileInfoList.get(position)
							.getContent(), EMMContants.DialogText.OPEN,
							EMMContants.DialogText.NEGATIVEBUTTON, ".pdf");
				} else {
					FileInfoDialog dialog = new FileInfoDialog(getActivity(),
							position, EMMContants.DialogFlag.INPUT_PWD,
							fileInfoList.get(position).getSendTime());
					dialog.show();
					dialog.setInputPwdDailog(fileInfoList.get(position)
							.getSubject(), EMMContants.DialogText.CONTACT
							+ fileInfoList.get(position).getContact(),
							EMMContants.DialogText.OPEN,
							EMMContants.DialogText.NEGATIVEBUTTON, fileInfoList
									.get(position).getPassword(), ".pdf");
				}
			} else {
				FileInfoDialog dialog = new FileInfoDialog(getActivity(),
						position, EMMContants.DialogFlag.SHOW);
				dialog.show();
				dialog.setTwoBtnDialog(fileInfoList.get(position).getSubject(),
						fileInfoList.get(position).getContent(),
						EMMContants.DialogText.DOWNLOAD,
						EMMContants.DialogText.NEGATIVEBUTTON);
			}
		} else {
			FileInfoDialog dialog = new FileInfoDialog(getActivity(), position,
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
	private boolean fileIsExists(int position) {
		File localFile = new File(new FileInfo().getFilePath(),
				new FileInfo()
						.getFileName(fileInfoList.get(position).getSubject(),
								fileInfoList.get(position).getSendTime()));
		DownFileDao downFileDao = new DownFileDao(getActivity());
		DownFile downFile = new DownFile();
		downFile = downFileDao.getDownFile(fileInfoList.get(position)
				.getFileUrl());
		if (localFile.exists()
				&& downFile.getState() == Constant.downloadComplete) {
			return true;
		} else if (downFile != null && localFile.exists()
				&& downFile.getState() == Constant.downloadComplete) {
			return true;
		}
		return false;
	}

	/**
	 * 删除某条通知
	 * 
	 * @param position
	 */
	private void delete(int position) {
		DownFileDao dao = new DownFileDao(getActivity());
		dao.delete(fileInfoList.get(position).getFileUrl());
		File file = new File(
				new FileInfo().getFilePath()
						+ new FileInfo().getFileName(fileInfoList.get(position)
								.getSubject(), fileInfoList.get(position)
								.getSendTime()));
		if (file.exists()) {
			file.delete();
		}
		if (fileInfoDao.delete(fileInfoList.get(position).getId()) > 0) {
			fileInfoList.remove(position);
			fileAdapter.notifyDataSetChanged();
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
			FileInfoDialog dialog = new FileInfoDialog(getActivity(), position,
					EMMContants.DialogFlag.DELETE);
			dialog.show();
			dialog.setTwoBtnDialog(EMMContants.DialogText.DELETETITLE,
					EMMContants.DialogText.DELETECONTECT,
					EMMContants.DialogText.POSITIVEBUTTON,
					EMMContants.DialogText.NEGATIVEBUTTON);
		}
	}

	final class FileInfoDialog extends CommonMsgInfoDialog {
		private int position;

		public FileInfoDialog(Context context, int position, int flag) {
			super(context, position, flag);
			// TODO Auto-generated constructor stub
			this.position = position;
		}

		public FileInfoDialog(Context context, int position, int flag,
				String sendTime) {
			super(context, position, flag, sendTime);
			// TODO Auto-generated constructor stub
			this.position = position;
		}

		@Override
		public void showInfo() {
			// TODO Auto-generated method stub
			super.showInfo();
			// 加入下载队列
			addDLQueue(position);
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
			FileInfoDialog dialog = new FileInfoDialog(getActivity(),
					position - 1, EMMContants.DialogFlag.DELETE);
			dialog.show();
			dialog.setTwoBtnDialog(EMMContants.DialogText.DELETETITLE,
					EMMContants.DialogText.DELETECONTECT,
					EMMContants.DialogText.POSITIVEBUTTON,
					EMMContants.DialogText.NEGATIVEBUTTON);
			return false;
		}
	}

	private void addDLQueue(int position) {
		String urlList = fileInfoList.get(position).getFileUrl();
		DownFile downFile = new DownFile();
		downFile.setName(new FileInfo().getFileName(fileInfoList.get(position)
				.getSubject(), fileInfoList.get(position).getSendTime()));
		downFile.setState(Constant.undownLoad);
		downFile.setDescription(fileInfoList.get(position).getContent());
		downFile.setDownUrl(urlList);
		downFile.setState(Constant.downInProgress);
		String suffix = getSuffix(urlList);
		if (!TextUtils.isEmpty(suffix)) {
			downFile.setSuffix(suffix);
			downFile.setDownPath(new FileInfo().getFilePath()
					+ new FileInfo().getFileName(fileInfoList.get(position)
							.getSubject(), fileInfoList.get(position)
							.getSendTime()));
		} else {
			// /默认类型.
			downFile.setSuffix(".pdf");
			downFile.setDownPath(new FileInfo().getFilePath()
					+ new FileInfo().getFileName(fileInfoList.get(position)
							.getSubject(), fileInfoList.get(position)
							.getSendTime()));
		}
		if (!EMMContants.LocalConf.isSdPresent()) {
			ToastUtils.show(getActivity(), "SD卡不存在!");
		}
		L.d(this.getClass(), urlList);
		long save = downFileDao.save(downFile);
		if (save > 0) {
			ToastUtils.show(getActivity(), "文件已成功添加至下载队列");
		} else {
			ToastUtils.show(getActivity(), "文件未成功添加至下载队列");
		}
		AutoDownloadTask download = new AutoDownloadTask();
		download.execute(downFile);
	}

	/**
	 * 获取文件总长度 开始下载
	 * 
	 * @author H2601985
	 */
	protected class AutoDownloadTask extends
			AsyncTask<DownFile, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(DownFile... params) {
			// TODO Auto-generated method stub
			try {
				// 检查文件总长度
				int totalLength = AbFileUtil.getContentLengthFormUrl(params[0]
						.getDownUrl());
				params[0].setTotalLength(totalLength);
				// 开始下载文件
				AbFileDownloader loader = new AbFileDownloader(getActivity(),
						params[0], 2);
				// mFileDownloaders.put(
				// mDownFile.getDownUrl(), loader);
				loader.download(null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}

	private final class TouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			et_search_keyword.setInputType(InputType.TYPE_CLASS_TEXT);
			et_search_keyword.onTouchEvent(event); // call native handler
			return true;
		}

	}

	/**
	 * 监听搜索
	 * 
	 * @author H2601985
	 * 
	 */
	private final class TextChangedListener implements TextWatcher {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

			String key = et_search_keyword.getText().toString().trim();
			if (key != null && key != "" & key.length() >= 1) {
				btn_clear_text.setVisibility(View.VISIBLE);
				// searchFile(key);
				btn_clear_text.setVisibility(View.VISIBLE);
				SearchTask st = new SearchTask();
				st.execute(key);
			} else {
				initData();
				btn_clear_text.setVisibility(View.INVISIBLE);
			}

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

	}

	protected class SearchTask extends AsyncTask<String, Void, List<FileInfo>> {

		@Override
		protected List<FileInfo> doInBackground(String... params) {
			String searchKey = params[0].toString().trim();
			if (searchKey != null && searchKey != "" && searchKey.length() >= 1) {
				System.out.println("searchKey -------->>> " + searchKey);
				fileInfoDao = new FileInfoDao(getActivity());
				List<FileInfo> list = fileInfoDao.findFilesByKey(searchKey);
				return list;
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<FileInfo> result) {
			super.onPostExecute(result);

			if (result != null) {
				fileInfoList = result;
				fileAdapter = new FileAdapter(getActivity(), fileInfoList,
						R.layout.info_center_item);
				mListView.setAdapter(fileAdapter);

			} else {
				fileInfoList.clear();
				fileAdapter = new FileAdapter(getActivity(), fileInfoList,
						R.layout.info_center_item);
				mListView.setAdapter(fileAdapter);
				Toast.makeText(getActivity(), "获取应用列表为空,请更换关键字后重试",
						Toast.LENGTH_SHORT).show();
			}
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

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub

		if (hasFocus) {
			et_search_keyword.setCursorVisible(true);

		} else {
			et_search_keyword.setCursorVisible(false);
			// btn_clear_text.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btn_clear_text) {
			et_search_keyword.setText("");
			btn_clear_text.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onMessageReceived(String messageType) {
		rebuildView();
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
}
