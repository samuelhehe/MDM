package com.foxconn.lib.residemenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foxconn.app.R;

public class ResideMenuInfo extends LinearLayout {

	/** menu item icon */
	private ImageView iv_icon;
	/** menu item title */
	private TextView tv_username;

	private TextView tv_dengji;

	public ResideMenuInfo(Context context) {
		super(context);
		initViews(context);
	}

	public ResideMenuInfo(Context context, int icon, String title, String dengji) {
		super(context);
		initViews(context);
		iv_icon.setImageResource(icon);
		tv_username.setText(title);
		tv_dengji.setText(dengji);
	}

	private void initViews(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.residemenu_info, this);
		iv_icon = (ImageView) findViewById(R.id.image_icon);
		tv_username = (TextView) findViewById(R.id.tv_username);
		tv_dengji = (TextView) findViewById(R.id.tv_dengji);
	}

	/**
	 * set the icon color;
	 * 
	 * @param icon
	 */
	public void setIcon(int icon) {
		iv_icon.setImageResource(icon);
	}

	/**
	 * set the title with string;
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		tv_username.setText(title);
	}

	/**
	 * set the title with string;
	 * 
	 * @param dengji
	 */
	public void setDengJi(String dengji) {
		tv_dengji.setText(dengji);
	}
}