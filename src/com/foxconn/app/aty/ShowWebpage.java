package com.foxconn.app.aty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.foxconn.app.R;
import com.foxconn.emm.bean.WebPageInfo;

public class ShowWebpage extends Activity {
	private TextView textView1;
	private WebView webView1;

	private WebPageInfo webPageInfo = new WebPageInfo();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_web);
		Intent i = getIntent();
		webPageInfo.setSubject(i.getStringExtra(WebPageInfo.TAG.SUBJECT));
		webPageInfo.setPageUrl(i.getStringExtra(WebPageInfo.TAG.PAGEURL));
		initView();
	}

	private void initView() {
		textView1 = (TextView) findViewById(R.id.common_dialog_prompt_tv);
		webView1 = (WebView) findViewById(R.id.webView1);
		webView1.setWebViewClient(new WebPageClient());
		webView1.setWebChromeClient(new MyWebChromeClient());
		loadData();
	}

	private void loadData() {
		textView1.setText(webPageInfo.getSubject());
		webView1.loadUrl(webPageInfo.getPageUrl());
	}

	public class WebPageClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			// if(url.indexOf("tel:")<0){//页面上有数字会导致连接电话

			view.loadUrl(url);

			// }
			return true;
		}

		public boolean onJsAlert(WebView view, String url, String message,
				final JsResult result) {

			// message就是wave函数里alert的字符串，这样你就可以在android客户端里对这个数据进行处理

			result.confirm();

			return true;
		}
	}

	public class MyWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			// TODO Auto-generated method stub
			result.confirm();

			return true;
		}
	}

	public boolean onKeyDown(int keyCoder, KeyEvent event) {
		if (webView1.canGoBack() && keyCoder == KeyEvent.KEYCODE_BACK) {
			webView1.goBack(); // goBack()表示返回webView的上一页面

			return true;
		}
		return false;
	}

	public void Click(View v) {
		this.finish();
	}

}
