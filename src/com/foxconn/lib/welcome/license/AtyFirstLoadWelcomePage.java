package com.foxconn.lib.welcome.license;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.foxconn.app.R;
import com.foxconn.app.aty.AtyWelcome;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.view.SysLicenseDialog;

public class AtyFirstLoadWelcomePage extends FragmentActivity implements
		OnClickListener {

	protected static final String TAG = "AtyFirstLoadWelcomePage";
	private TestFragmentAdapter mAdapter;
	private ViewPager mPager;
	private UnderlinePageIndicator mIndicator;

	private TextView emm_sys_welcomepage_bottom_start_tv,
			emm_sys_welcomepage_bottom_next_tv;

	private int currentPageItem = 0;

	private int pagerCount = 2;

	private View emm_sys_welcomepage_bottom_deliver;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_underlines);
		mAdapter = new TestFragmentAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		mPager.setCurrentItem(currentPageItem);
		emm_sys_welcomepage_bottom_next_tv = (TextView) findViewById(R.id.emm_sys_welcomepage_bottom_next_tv);
		emm_sys_welcomepage_bottom_next_tv.setOnClickListener(this);
		emm_sys_welcomepage_bottom_start_tv = (TextView) findViewById(R.id.emm_sys_welcomepage_bottom_start_tv);
		emm_sys_welcomepage_bottom_start_tv.setOnClickListener(this);
		emm_sys_welcomepage_bottom_deliver = findViewById(R.id.emm_sys_welcomepage_bottom_deliver);

		mIndicator = (UnderlinePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
		mIndicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int item) {

			}

			@Override
			public void onPageScrolled(int item, float arg1, int arg2) {
				currentPageItem = item;
				if (currentPageItem < pagerCount) {
					emm_sys_welcomepage_bottom_next_tv
							.setVisibility(View.VISIBLE);
					emm_sys_welcomepage_bottom_deliver
							.setVisibility(View.VISIBLE);
				}
				if (currentPageItem == pagerCount) {
					emm_sys_welcomepage_bottom_next_tv.setVisibility(View.GONE);
					emm_sys_welcomepage_bottom_deliver.setVisibility(View.GONE);
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.emm_sys_welcomepage_bottom_next_tv:
			if (currentPageItem < pagerCount) {
				mPager.setCurrentItem(++currentPageItem);
			}
			break;
		case R.id.emm_sys_welcomepage_bottom_start_tv:
//			Toast.makeText(AtyFirstLoadWelcomePage.this, "welcome", 1).show();
			SysLicenseDialog licenseDialog = new SysLicenseDialog(AtyFirstLoadWelcomePage.this);
			licenseDialog.show();
			break;
		}

	}
	
	public void turn(){
		startActivity(new Intent(this, AtyWelcome.class));
		this.finish();
	}
	
	
	
	
	
	
	
	
}