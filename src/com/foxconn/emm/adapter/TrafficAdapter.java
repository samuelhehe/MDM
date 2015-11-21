package com.foxconn.emm.adapter;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.foxconn.app.R;
import com.foxconn.emm.bean.UidTraffic;
import com.foxconn.emm.utils.TextFormater;

public class TrafficAdapter extends BaseAdapter {

	private Context context;
	private List<UidTraffic> trafficInfos;

	private PackageManager packageManager; 
	public TrafficAdapter(Context context, List<UidTraffic> trafficInfos) {
		this.context = context;
		this.trafficInfos = trafficInfos;
		this.packageManager = context.getPackageManager();
		
		
	}

	@Override
	public int getCount() {
		return trafficInfos.size();
	}

	@Override
	public UidTraffic getItem(int position) {
		return trafficInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder;
		UidTraffic info = trafficInfos.get(position);
		if (convertView == null) {
			view = View.inflate(context, R.layout.traffic_manager_item, null);
			holder = new ViewHolder();
			holder.iv_traffic_icon = (ImageView) view
					.findViewById(R.id.iv_traffic_icon);
			holder.tv_traffic_name = (TextView) view
					.findViewById(R.id.tv_traffic_name);
			holder.tv_traffic_received = (TextView) view
					.findViewById(R.id.tv_traffic_received);
			holder.tv_traffic_transmitted = (TextView) view
					.findViewById(R.id.tv_traffic_transmitted);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		try {
		holder.iv_traffic_icon.setImageDrawable(packageManager.getApplicationIcon(info.getPackageName()));
		holder.tv_traffic_name.setText(info.getAppName());
		holder.tv_traffic_received.setText(TextFormater.dataSizeFormat(info.getReceived_total()));
		holder.tv_traffic_transmitted.setText(TextFormater.dataSizeFormat(info.getUploaded_total()));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return view;
	}

	private class ViewHolder {
		ImageView iv_traffic_icon;
		TextView tv_traffic_name;
		TextView tv_traffic_received;
		TextView tv_traffic_transmitted;
	}
}
