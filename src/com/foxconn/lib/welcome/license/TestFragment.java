package com.foxconn.lib.welcome.license;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foxconn.app.R;

public final class TestFragment extends Fragment {
	private static final String KEY_CONTENT = "TestFragment:Content";

	public static TestFragment newInstance(String content) {
		TestFragment fragment = new TestFragment();
		fragment.mContent = content;
		return fragment;
	}

	private int content;

	public static TestFragment newInstance(int content) {
		TestFragment fragment = new TestFragment();
		fragment.content = content;
		return fragment;
	}

	private String mContent = "???";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.welcome_content,null);

		TextView welcome_content_subject_tv , welcome_content_subject_detail_tv; 
		
		welcome_content_subject_tv = (TextView) layout.findViewById(R.id.welcome_content_subject_tv);
		
		welcome_content_subject_detail_tv = (TextView) layout.findViewById(R.id.welcome_content_subject_detail_tv);
		
		ImageView  welcome_content_iconlay = (ImageView) layout.findViewById(R.id.welcome_content_iconlay).findViewById(R.id.welcome_content_iconlay_iv);
		
		switch (content) {
		case 0:
			layout.setBackgroundResource(R.drawable.qo_startup_screen1);
			welcome_content_iconlay.setImageResource(R.drawable.icon);
			break;
		case 1:
			layout.setBackgroundResource(R.drawable.qo_startup_screen2);
			
			welcome_content_subject_tv.setText(getString(R.string.emm_license_welcome_pager2_title));
			welcome_content_subject_detail_tv.setText(getString(R.string.emm_license_welcome_pager2_content));
			break;
		case 2:
			layout.setBackgroundResource(R.drawable.qo_startup_screen3);
			welcome_content_iconlay.setImageResource(R.drawable.icon);
			welcome_content_subject_tv.setText(getString(R.string.emm_license_welcome_pager3_title));
			welcome_content_subject_detail_tv.setText(getString(R.string.emm_license_welcome_pager3_content));
			break;
		default:
			break;
		}

		return layout;
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT, mContent);
	}
}
