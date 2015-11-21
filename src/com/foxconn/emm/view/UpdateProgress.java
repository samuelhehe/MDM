package com.foxconn.emm.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.foxconn.app.R;

public class UpdateProgress extends ProgressDialog {
	private Context context;
	private String msg;
	private TextView sys_updating_progress_status_tv;
	private ProgressBar sys_updating_pb;

	public UpdateProgress(Context context, String msg) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.msg = msg;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sys_common_update_progress_dialog);
		initView();
		initData();
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
	}

	@Override
	public void setCancelable(boolean flag) {
		// TODO Auto-generated method stub
		super.setCancelable(flag);
	}

	public void hideProgress() {
		sys_updating_pb.setVisibility(View.GONE);
	}

	@Override
	public void setMessage(CharSequence message) {
		// TODO Auto-generated method stub
		super.setMessage(message);
		sys_updating_progress_status_tv.setText(message);
	}

	private void initView() {
		sys_updating_progress_status_tv = (TextView) findViewById(R.id.sys_updating_progress_status_tv);
		sys_updating_pb = (ProgressBar) findViewById(R.id.sys_updating_pb);
	}

	private void initData() {
		sys_updating_progress_status_tv.setText(msg);
	}
}
