package com.foxconn.app.aty;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.foxconn.app.App;
import com.foxconn.app.R;
import com.foxconn.emm.utils.EMMContants;

public class AtySpecification extends AbActivity {

	private WebView about_us;
	private int mDensity;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setAbContentView(R.layout.activity_about_specification);
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("使用说明");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(android.R.color.holo_blue_light);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);

		about_us = (WebView) this.findViewById(R.id.about_us);
		WebSettings mWebSettings = about_us.getSettings();
		mWebSettings.setBuiltInZoomControls(true); // 支持页面放大缩小按钮
		mWebSettings.setSupportZoom(true);
		mWebSettings.setTextSize(TextSize.LARGER);
//		about_us.zoomIn();//放大
//		about_us.zoomOut();//缩小
		mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		mWebSettings.setAppCacheEnabled(false);
		mWebSettings.setUseWideViewPort(true);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mDensity = metrics.densityDpi;

		if (mDensity == 240) { // 可以让不同的density的情况下，可以让页面进行适配
			mWebSettings.setDefaultZoom(ZoomDensity.FAR);
		} else if (mDensity == 160) {
			mWebSettings.setDefaultZoom(ZoomDensity.MEDIUM);
		} else if (mDensity == 120) {
			mWebSettings.setDefaultZoom(ZoomDensity.CLOSE);
		}
		about_us.getSettings().setJavaScriptEnabled(true); 
		 final Activity activity = this; 
		 about_us.setWebChromeClient(new WebChromeClient() {   
			 public void onProgressChanged(WebView view, int progress) { 
				 activity.setProgress(progress * 1000);   
				 } }); 
		 about_us.setWebViewClient(new WebViewClient() {  
			 public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {   
				 Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();  
				 } });
		 about_us.loadUrl(EMMContants.SYSCONF.USER_SPECIFICATION_URL);
		
		App.getInstance().addActivity(this);
	}


//	@Override
//	protected void onResume() {
//		super.onResume();
//		if (!TextFormater.isEmpty(EMMContants.SYSCONF.USER_SPECIFICATION_URL)) {
//			about_us.loadUrl(EMMContants.SYSCONF.USER_SPECIFICATION_URL);
//		} else {
//			about_us.loadUrl(EMMContants.SYSCONF.SERVER_URL2);
//			about_us.setContentDescription("网页被外星人带走了， Sorry ^0^");
//		}
//	}

}
