package com.foxconn.emm.adapter;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foxconn.app.R;
import com.foxconn.emm.bean.InstalledAppInfo;
import com.foxconn.emm.utils.EMMContants;

public class LimitListAdapter extends BaseAdapter {

	protected Context context;
	private List<InstalledAppInfo> data;
	protected int listviewItem;
	protected LayoutInflater inflater;

	public LimitListAdapter(Context context, List<InstalledAppInfo> data,
			int listviewItem) {
		this.context = context;
		this.data = data;
		this.listviewItem = listviewItem;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView item_name = null;
		TextView item_detail = null;
		ImageView cicon;
		TextView category_name = null;
		if (convertView == null) {
			convertView = inflater.inflate(listviewItem, null);
			RelativeLayout item_body = (RelativeLayout) convertView
					.findViewById(R.id.item_body);
			item_name = (TextView) item_body.findViewById(R.id.item_name);
			item_detail = (TextView) item_body.findViewById(R.id.item_detail);
			cicon = (ImageView) item_body.findViewById(R.id.icon);
			category_name = (TextView) convertView
					.findViewById(R.id.category_name);
			convertView.setTag(new DataWrapper(item_name, item_detail, cicon,
					category_name));
		} else {
			DataWrapper dataWrapper = (DataWrapper) convertView.getTag();
			item_name = dataWrapper.item_name;
			item_detail = dataWrapper.item_detail;
			cicon = dataWrapper.imageView;
			category_name = dataWrapper.category_name;
		}
		InstalledAppInfo installedAppInfo = data.get(position);
		// item_name.setVisibility(View.GONE);
		item_name.setText(installedAppInfo.getAppName());
		item_detail.setText("版本: " + installedAppInfo.getVersionName());
		item_detail.setVisibility(View.VISIBLE);
		cicon.setImageDrawable(context.getResources().getDrawable(
				R.drawable.app_icon_tbd));
		cicon.setTag(installedAppInfo.getPackageName());
		// loadAppIcon(cicon, context, blackappInfo);
		cicon.setImageDrawable(installedAppInfo.getDrawable(context));
		category_name.setVisibility(View.GONE);
//		setType(installedAppInfo, category_name);
		return convertView;
	}

	private void setType(InstalledAppInfo blackappInfo, TextView category_name) {
		int typeflag = Integer.valueOf(blackappInfo.getLimittype());
		switch (typeflag) {
		case EMMContants.LIMITLIST_CONTANT.LT_WHITE_LIST:
			category_name.setText("白名单应用");
			break;
		case EMMContants.LIMITLIST_CONTANT.LT_BLACK_LIST:
			category_name.setText("黑名单应用");
			break;

		case EMMContants.LIMITLIST_CONTANT.LLT_FLAG_TIME:
			category_name.setText("按时间限制");

		case EMMContants.LIMITLIST_CONTANT.LLT_FLAG_PASSWORD:
			category_name.setText("按密码限制");
			break;
		default:
			category_name.setText("限制应用");
			break;
		}

	}

	protected ExecutorService singleThreadExecutor = Executors
			.newFixedThreadPool(10);

	private void loadAppIcon(ImageView cicon, Context context,
			InstalledAppInfo blackappInfo) {

		Future<Drawable> submit = singleThreadExecutor
				.submit(new LoadIconTaskCallable(context, blackappInfo));
		try {
			if (submit.get() != null) {
				Looper.prepare();
				cicon.setImageDrawable(submit.get());
				Looper.loop();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

	}

	protected final class LoadIconTaskCallable implements Callable<Drawable> {
		private Context context;
		private InstalledAppInfo blackAppInfo;

		public LoadIconTaskCallable(Context context,
				InstalledAppInfo blackAppInfo) {
			this.context = context;
			this.blackAppInfo = blackAppInfo;
		}

		@Override
		public Drawable call() throws Exception {
			return blackAppInfo.getDrawable(context);
		}
	}

	private static class DataWrapper {

		TextView item_name = null;
		TextView item_detail = null;
		ImageView imageView;
		TextView category_name;

		public DataWrapper(TextView item_name, TextView item_detail,
				ImageView imageView, TextView category_name) {
			super();
			this.item_name = item_name;
			this.imageView = imageView;
			this.item_detail = item_detail;
			this.category_name = category_name;
		}
	}

}
