package com.foxconn.emm.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.foxconn.app.R;
import com.foxconn.emm.bean.AppInfo;

public class AppAdapter extends BaseAdapter {
	private Context context;
	private List<AppInfo> appInfoList;
	private int item;
	private LayoutInflater inflater;

	public AppAdapter(Context context, List<AppInfo> appInfoList, int item) {
		this.context = context;
		this.appInfoList = appInfoList;
		this.item = item;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return appInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return appInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView app_title = null;
		TextView app_receive_date = null;
		TextView app_size = null;
		if (convertView == null) {
			convertView = inflater.inflate(item, null);
			app_title = (TextView) convertView.findViewById(R.id.app_title);
			app_receive_date = (TextView) convertView
					.findViewById(R.id.app_receive_date);
			app_size = (TextView) convertView.findViewById(R.id.app_size);
			convertView.setTag(new DataWrapper(app_title, app_receive_date,
					app_size));
		} else {
			DataWrapper dataWrrapper = (DataWrapper) convertView.getTag();
			app_title = dataWrrapper.app_title;
			app_receive_date = dataWrrapper.app_receive_date;
			app_size = dataWrrapper.app_size;
		}

		AppInfo appInfo = appInfoList.get(position);
		app_title.setText(appInfo.getAppName());
		app_receive_date.setText(parseSendTime(appInfo.getSendTime()));
		app_size.setText(parseFileSize(appInfo.getFileSize()));
		return convertView;
	}

	/**
	 * 解析发送时间
	 * 
	 * @param sendTime
	 * @return
	 */
	private String parseSendTime(String sendTime) {
		String parseTime = sendTime.substring(0, 4) + "-"
				+ sendTime.substring(4, 6) + "-" + sendTime.substring(6, 8)
				+ " " + sendTime.substring(8, 10) + ":"
				+ sendTime.substring(10, 12);
		return parseTime;
	}

	private String parseFileSize(String fileSize) {
		String size = "大小：" + Integer.valueOf(fileSize) / 1024 + "."
				+ Integer.valueOf(fileSize) % 1024 + "M";
		return size;
	}

	public class DataWrapper {
		TextView app_title = null;
		TextView app_receive_date = null;
		TextView app_size = null;

		public DataWrapper(TextView app_title, TextView app_receive_date,
				TextView app_size) {
			this.app_title = app_title;
			this.app_receive_date = app_receive_date;
			this.app_size = app_size;
		}
	}
}
