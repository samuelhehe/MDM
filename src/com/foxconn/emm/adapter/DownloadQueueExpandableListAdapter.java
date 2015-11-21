package com.foxconn.emm.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.task.AbTask;
import com.ab.task.AbTaskItem;
import com.ab.task.AbTaskListener;
import com.ab.util.AbFileUtil;
import com.ab.util.AbStrUtil;
import com.foxconn.app.R;
import com.foxconn.emm.utils.Constant;
import com.foxconn.lib.download.AbDownloadProgressListener;
import com.foxconn.lib.download.AbDownloadThread;
import com.foxconn.lib.download.AbFileDownloader;
import com.foxconn.lib.download.DownFile;
import com.foxconn.lib.download.DownFileDao;

public class DownloadQueueExpandableListAdapter extends
		BaseExpandableListAdapter {

	private static final String TAG = "DownloadQueueExpandableListAdapter";
	private static final boolean D = Constant.DEBUG;
	private Context mContext;
	private ArrayList<ArrayList<DownFile>> mDownFileGroupList = null;
	private String[] mDownFileGroupTitle = null;
	public HashMap<String, AbFileDownloader> mFileDownloaders = null;
	private com.foxconn.lib.download.DownFileDao downFileDao = null;
	final ViewHolder holder = new ViewHolder();

	public DownloadQueueExpandableListAdapter(Context context,
			ArrayList<ArrayList<DownFile>> downFileGroupList,
			String[] downFileGroupTitle) {
		this.mContext = context;
		mDownFileGroupList = downFileGroupList;
		mDownFileGroupTitle = downFileGroupTitle;
		mFileDownloaders = new HashMap<String, AbFileDownloader>();
		downFileDao = new DownFileDao(context);
	}

	/**
	 * 获取指定组位置、指定子列表项处的子列表项数据
	 */
	@Override
	public DownFile getChild(int groupPosition, int childPosition) {
		return mDownFileGroupList.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mDownFileGroupList.get(groupPosition).size();
	}

	/**
	 * 该方法决定每个子选项的外观
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.down_items, parent, false);
		}
		holder.itemsIcon = (ImageView) convertView.findViewById(R.id.itemsIcon);
		holder.itemsTitle = (TextView) convertView
				.findViewById(R.id.itemsTitle);
		holder.itemsDesc = (TextView) convertView.findViewById(R.id.itemsDesc);
		holder.operateBtn = (Button) convertView.findViewById(R.id.operateBtn);
		holder.progress = (ProgressBar) convertView
				.findViewById(R.id.received_progress);
		holder.received_progress_percent = (TextView) convertView
				.findViewById(R.id.received_progress_percent);
		holder.received_progress_number = (TextView) convertView
				.findViewById(R.id.received_progress_number);
		holder.received_progressBar = (RelativeLayout) convertView
				.findViewById(R.id.received_progressBar);

		holder.itemsIcon.setFocusable(false);
		holder.operateBtn.setFocusable(false);
		holder.progress.setFocusable(false);

		final DownFile mDownFile = (DownFile) getChild(groupPosition,
				childPosition);
		if (mDownFile != null) {

			holder.itemsIcon.setImageDrawable(getIconDrawable(mDownFile));
			holder.itemsTitle.setText(mDownFile.getName());
			holder.itemsDesc.setText(mDownFile.getDescription());
			if (mDownFile.getState() == Constant.undownLoad) {
				holder.operateBtn.setBackgroundResource(R.drawable.down_load);
				holder.received_progressBar.setVisibility(View.GONE);
				holder.itemsDesc.setVisibility(View.VISIBLE);
				holder.progress.setProgress(0);
				holder.received_progress_percent.setText(0 + "%");
				holder.received_progress_number.setText("0KB/"
						+ AbStrUtil.getSizeDesc(mDownFile.getTotalLength()));
			} else if (mDownFile.getState() == Constant.downInProgress) {
				holder.operateBtn.setBackgroundResource(R.drawable.down_pause);
				if (mDownFile.getDownLength() != 0
						&& mDownFile.getTotalLength() != 0) {
					int c = (int) (mDownFile.getDownLength() * 100 / mDownFile
							.getTotalLength());
					holder.itemsDesc.setVisibility(View.GONE);
					holder.received_progressBar.setVisibility(View.VISIBLE);
					holder.progress.setProgress(c);
					holder.received_progress_percent.setText(c + "%");
					holder.received_progress_number
							.setText(AbStrUtil.getSizeDesc(mDownFile
									.getDownLength())
									+ "/"
									+ AbStrUtil.getSizeDesc(mDownFile
											.getTotalLength()));
				}
			} else if (mDownFile.getState() == Constant.downLoadPause) {
				holder.operateBtn.setBackgroundResource(R.drawable.down_load);
				// 下载了多少
				if (mDownFile.getDownLength() != 0
						&& mDownFile.getTotalLength() != 0) {
					int c = (int) (mDownFile.getDownLength() * 100 / mDownFile
							.getTotalLength());
					holder.itemsDesc.setVisibility(View.GONE);
					holder.received_progressBar.setVisibility(View.VISIBLE);
					holder.progress.setProgress(c);
					holder.received_progress_percent.setText(c + "%");
					holder.received_progress_number
							.setText(AbStrUtil.getSizeDesc(mDownFile
									.getDownLength())
									+ "/"
									+ AbStrUtil.getSizeDesc(mDownFile
											.getTotalLength()));
				} else {
					holder.itemsDesc.setVisibility(View.VISIBLE);
					holder.received_progressBar.setVisibility(View.GONE);
					holder.progress.setProgress(0);
					holder.received_progress_percent.setText(0 + "%");
					holder.received_progress_number
							.setText("0KB/"
									+ AbStrUtil.getSizeDesc(mDownFile
											.getTotalLength()));
				}
			} else if (mDownFile.getState() == Constant.downloadComplete) {
				holder.operateBtn.setBackgroundResource(R.drawable.down_delete);
				holder.received_progressBar.setVisibility(View.GONE);
				holder.itemsDesc.setVisibility(View.VISIBLE);
			}

			final AbDownloadProgressListener mDownloadProgressListener = new AbDownloadProgressListener() {
				// 实时获知文件已经下载的数据长度
				@Override
				public void onDownloadSize(final long size) {
					if (mDownFile.getTotalLength() == 0) {
						return;
					}
					final int c = (int) (size * 100 / mDownFile
							.getTotalLength());
					if (c != holder.progress.getProgress()) {
						holder.progress.post(new Runnable() {
							@Override
							public void run() {
								holder.progress.setProgress(c);
								holder.received_progress_percent.setText(c
										+ "%");
								holder.received_progress_number.setText(AbStrUtil
										.getSizeDesc(size)
										+ "/"
										+ AbStrUtil.getSizeDesc(mDownFile
												.getTotalLength()));
							}
						});
					}
					if (mDownFile.getTotalLength() == size) {
						if (D)
							Log.d(TAG, "下载完成:" + size);
						mDownFile.setState(Constant.downloadComplete);
						// 下载完成
						mDownFileGroupList.get(1).remove(mDownFile);
						mDownFileGroupList.get(0).add(mDownFile);
						holder.progress.post(new Runnable() {
							@Override
							public void run() {
								notifyDataSetChanged();
							}
						});

					}
				}
			};
			if (mDownFile.getState() == Constant.downInProgress) {
				mDownloadProgressListener.onDownloadSize(mDownFile.getDownLength());
				autoDownload(mDownFile, mDownloadProgressListener);
			}
			// 处理按钮事件
			holder.operateBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!Environment.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED)) {
						// 无sd卡
						((AbActivity) mContext).showToast("没找到存储卡");
						return;
					}

					if (mDownFile.getState() == Constant.undownLoad
							|| mDownFile.getState() == Constant.downLoadPause) {
						// 下载

						// holder.itemsDesc.setVisibility(View.GONE);
						// holder.received_progressBar.setVisibility(View.VISIBLE);
						// holder.operateBtn
						// .setBackgroundResource(R.drawable.down_pause);
						// mDownFile.setState(Constant.downInProgress);
						// AbTask mAbTask = new AbTask();
						// final AbTaskItem item = new AbTaskItem();
						// item.setListener(new AbTaskListener() {
						//
						// @Override
						// public void update() {
						// }
						//
						// @Override
						// public void get() {
						// try {
						// // 检查文件总长度
						// int totalLength = AbFileUtil
						// .getContentLengthFormUrl(mDownFile
						// .getDownUrl());
						// mDownFile.setTotalLength(totalLength);
						// // 开始下载文件
						// AbFileDownloader loader = new AbFileDownloader(
						// mContext, mDownFile, 2);
						// mFileDownloaders.put(
						// mDownFile.getDownUrl(), loader);
						// loader.download(mDownloadProgressListener);
						// } catch (Exception e) {
						// e.printStackTrace();
						// }
						// };
						// });
						// mAbTask.execute(item);
						autoDownload(mDownFile, mDownloadProgressListener);

					} else if (mDownFile.getState() == Constant.downInProgress) {
						// 暂停
						holder.operateBtn
								.setBackgroundResource(R.drawable.down_load);
						mDownFile.setState(Constant.undownLoad);
						AbFileDownloader mFileDownloader = mFileDownloaders
								.get(mDownFile.getDownUrl());
						// 释放原来的线程
						if (mFileDownloader != null) {
							mFileDownloader.setFlag(false);
							AbDownloadThread mDownloadThread = mFileDownloader
									.getThreads();
							if (mDownloadThread != null) {
								mDownloadThread.setFlag(false);
								mFileDownloaders.remove(mDownFile.getDownUrl());
								mDownloadThread = null;
							}
							mFileDownloader = null;
						}
					} else if (mDownFile.getState() == Constant.downloadComplete) {
						// 删除
						mDownFileGroupList.get(0).remove(mDownFile);
						mFileDownloaders.remove(mDownFile.getDownUrl());
						downFileDao.delete(mDownFile.getDownUrl());
						// mDownFile.setState(Constant.undownLoad);
						// mDownFileGroupList.get(1).add(mDownFile);
						notifyDataSetChanged();
					}

				}
			});

		}
		return convertView;
	}

	private void autoDownload(final DownFile mDownFile,
			final AbDownloadProgressListener mDownloadProgressListener) {
		holder.itemsDesc.setVisibility(View.GONE);
		holder.received_progressBar.setVisibility(View.VISIBLE);
		holder.operateBtn.setBackgroundResource(R.drawable.down_pause);
		mDownFile.setState(Constant.downInProgress);
		AbTask mAbTask = new AbTask();
		final AbTaskItem item = new AbTaskItem();
		item.setListener(new AbTaskListener() {

			@Override
			public void update() {
			}

			@Override
			public void get() {
				try {
					// 检查文件总长度
					int totalLength = AbFileUtil
							.getContentLengthFormUrl(mDownFile.getDownUrl());
					mDownFile.setTotalLength(totalLength);
					// 开始下载文件
					AbFileDownloader loader = new AbFileDownloader(mContext,
							mDownFile, 2);
					mFileDownloaders.put(mDownFile.getDownUrl(), loader);
					loader.download(mDownloadProgressListener);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		});
		mAbTask.execute(item);
	}

	private Drawable getIconDrawable(DownFile mDownFile) {

		String suffix = mDownFile.getSuffix();
		if (TextUtils.isEmpty(suffix)) {
			return this.mContext.getResources().getDrawable(
					R.drawable.default_pic);
		}
		if (suffix.contains("pdf")) {
			return this.mContext.getResources().getDrawable(
					R.drawable.file_icon_pdf);
		}
		if (suffix.contains("png")) {
			return this.mContext.getResources().getDrawable(
					R.drawable.file_icon_picture);
		}
		if (suffix.contains("jpg") || suffix.contains("jpeg")) {
			return this.mContext.getResources().getDrawable(
					R.drawable.file_icon_jpg);
		}
		if (suffix.contains("doc") || suffix.contains("docx")) {
			return this.mContext.getResources().getDrawable(
					R.drawable.file_icon_doc);
		}
		if (suffix.contains("ppt") || suffix.contains("pptx")) {
			return this.mContext.getResources().getDrawable(
					R.drawable.file_icon_ppt);
		}

		if (suffix.contains("xls") || suffix.contains("xlsx")) {
			return this.mContext.getResources().getDrawable(
					R.drawable.file_icon_xls);
		}
		return this.mContext.getResources().getDrawable(R.drawable.default_pic);
	}

	/**
	 * 获取指定组位置处的组数据
	 */
	@Override
	public Object getGroup(int groupPosition) {
		return mDownFileGroupList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mDownFileGroupList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	/**
	 * 该方法决定每个组选项的外观
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.down_title, parent, false);
		}
		TextView mTextView = (TextView) convertView
				.findViewById(R.id.title_text);
		mTextView.setText(mDownFileGroupTitle[groupPosition]);
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	/**
	 * 描述：释放线程
	 */
	public void releaseThread() {
		Iterator it = mFileDownloaders.entrySet().iterator();
		AbFileDownloader mFileDownloader = null;
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry) it.next();
			mFileDownloader = (AbFileDownloader) e.getValue();
			// System.out.println("Key: " + e.getKey() + "; Value: " +
			// e.getValue());
			if (mFileDownloader != null) {
				mFileDownloader.setFlag(false);
				AbDownloadThread mDownloadThread = mFileDownloader.getThreads();
				if (mDownloadThread != null) {
					mDownloadThread.setFlag(false);
					mDownloadThread = null;
				}
				mFileDownloader = null;
			}
		}
	}

	public class ViewHolder {
		public ImageView itemsIcon;
		public TextView itemsTitle;
		public TextView itemsDesc;
		public Button operateBtn;
		public ProgressBar progress;
		public TextView received_progress_percent;
		public TextView received_progress_number;
		public RelativeLayout received_progressBar;
	}
}
