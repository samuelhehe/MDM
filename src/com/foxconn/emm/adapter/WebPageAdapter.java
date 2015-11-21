package com.foxconn.emm.adapter;

import java.util.List;

import com.foxconn.app.R;
import com.foxconn.emm.adapter.PicAdapter.DataWrapper;
import com.foxconn.emm.bean.NotificationInfo;
import com.foxconn.emm.bean.PicInfo;
import com.foxconn.emm.bean.WebPageInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WebPageAdapter extends BaseAdapter {

	private Context context;
	private List<WebPageInfo> webPageInfoList;
	private int item;
	private LayoutInflater inflater;

	public WebPageAdapter(Context context, List<WebPageInfo> webPageInfoList,
			int item) {
		this.context = context;
		this.webPageInfoList = webPageInfoList;
		this.item = item;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return webPageInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return webPageInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView info_center_command = null;
		TextView info_center_content = null;
		TextView info_center_receive_date = null;
		if (convertView == null) {
			convertView = inflater.inflate(item, null);
			info_center_command = (TextView) convertView
					.findViewById(R.id.info_center_command);
			info_center_content = (TextView) convertView
					.findViewById(R.id.info_center_content);
			info_center_receive_date = (TextView) convertView
					.findViewById(R.id.info_center_receive_date);
			convertView.setTag(new DataWrapper(info_center_command,
					info_center_content, info_center_receive_date));
		} else {
			DataWrapper dataWrrapper = (DataWrapper) convertView.getTag();
			info_center_command = dataWrrapper.info_center_command;
			info_center_content = dataWrrapper.info_center_content;
			info_center_receive_date = dataWrrapper.info_center_receive_date;
		}

		WebPageInfo webPageInfo = webPageInfoList.get(position);
		info_center_command.setText(webPageInfo.getSubject());
		info_center_content.setText(webPageInfo.getContent());
		info_center_receive_date.setText(parseSendTime(webPageInfo
				.getSendTime()));

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

	public class DataWrapper {
		TextView info_center_command = null;
		TextView info_center_content = null;
		TextView info_center_receive_date = null;

		public DataWrapper(TextView info_center_command,
				TextView info_center_content, TextView info_center_receive_date) {
			this.info_center_command = info_center_command;
			this.info_center_content = info_center_content;
			this.info_center_receive_date = info_center_receive_date;
		}
	}

}
