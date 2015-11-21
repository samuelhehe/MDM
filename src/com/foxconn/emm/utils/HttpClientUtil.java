package com.foxconn.emm.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.text.TextUtils;
import android.util.Log;

public class HttpClientUtil {

	protected static final String TAG = "HttpClientUtil";
	protected static final boolean DEBUG = EMMContants.DEBUG;

	public static String getRequest(final String BASE_URL,
			final String reqparams) throws InterruptedException,
			ExecutionException {
		String result = null;
		try {
			HttpGet get = new HttpGet(BASE_URL + reqparams);
			HttpClient httpClientUtil = new DefaultHttpClient();
			HttpResponse httpResponse = httpClientUtil.execute(get);
			if (DEBUG)
				Log.d(TAG, "url-->> " + BASE_URL + reqparams
						+ "\n respcode is  "
						+ httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(httpResponse.getEntity());
				if (result != null && result.length() > 0) {
					return result;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String getRequestJsonParamsJson(final String BASE_URL,
			final String jsonStr) throws InterruptedException,
			ExecutionException {
		String result = null;
		try {
			if (DEBUG)
				Log.d(TAG, "url-->> " + BASE_URL + "\n jsonStr-->>>" + jsonStr);
			HttpPost post = new HttpPost(BASE_URL);
			post.setEntity(new StringEntity(jsonStr.trim(), "UTF-8"));
			HttpClient httpClient = new DefaultHttpClient();
			// 请求超时
			httpClient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
			// 读取超时
			httpClient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 5000);
			HttpResponse response = httpClient.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());
				if (result != null && result.length() > 4) {
					return result;
				}
			} else {
				return result;
			}
		} catch (Exception e) {
			return result;
		}
		return null;
	}

	public static InputStream getRequestXML(final String requestUrl)
			throws InterruptedException, ExecutionException {
		try {
			if (!TextUtils.isEmpty(requestUrl)) {
				HttpGet get = new HttpGet(requestUrl);
				HttpClient httpClientUtil = new DefaultHttpClient();
				HttpResponse httpResponse = httpClientUtil.execute(get);
				if (DEBUG)
					Log.d(TAG, "url-->> " + requestUrl + "respcode is  "
							+ httpResponse.getStatusLine().getStatusCode());
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					return httpResponse.getEntity().getContent();
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getRequest2(final String BASE_URL,
			final String reqparams) throws InterruptedException,
			ExecutionException {
		FutureTask<String> task = new FutureTask<String>(
				new Callable<String>() {
					public String call() throws Exception {
						System.out.println(BASE_URL + reqparams);
						HttpGet get = new HttpGet(BASE_URL + reqparams);
						HttpClient httpClientUtil = new DefaultHttpClient();
						HttpResponse httpResponse = httpClientUtil.execute(get);
						System.out.println("respcode is  "
								+ httpResponse.getStatusLine().getStatusCode());
						if (httpResponse.getStatusLine().getStatusCode() == 200) {
							String result = EntityUtils.toString(httpResponse
									.getEntity());
							return result;
						}
						return null;
					}
				});

		new Thread(task).start();
		return task.get();
	}

	/**
	 * 从流中获得服务器返回的字符串数据
	 * 
	 * @param response
	 * @return String
	 * @throws IOException
	 */
	public static String getDataStr(HttpResponse response) throws IOException {
		InputStream is = response.getEntity().getContent();
		Reader reader = new BufferedReader(new InputStreamReader(is), 4000);
		StringBuilder buffer = new StringBuilder((int) response.getEntity()
				.getContentLength());
		try {
			char[] tmp = new char[1024];
			int l;
			while ((l = reader.read(tmp)) != -1) {
				buffer.append(tmp, 0, l);
			}
		} finally {
			reader.close();
		}
		return buffer.toString();
	}

	
	public static String returnData(String requrl, String params) {
		String returnData = "", output = "";
		try {
			URL url = new URL(requrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");// 提交模式
			conn.setDoOutput(true);// 是否输入参数
			if (DEBUG) {
				Log.d(TAG, "url-->>  "+url+ " input--->> "+params);
			}
			OutputStream outputStream = conn.getOutputStream();
			outputStream.write(params.getBytes());
			outputStream.flush();

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			while ((output = br.readLine()) != null) {
				returnData += output;
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnData;
	}
	
	
	
	public static String postRequest(final String url,
			final Map<String, String> rawParams) throws InterruptedException,
			ExecutionException {
		try {
			HttpPost post = new HttpPost(url);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (String key : rawParams.keySet()) {
				params.add(new BasicNameValuePair(key, rawParams.get(key)));
			}
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			HttpClient httpClientUtil = new DefaultHttpClient();
			HttpResponse httpResponse = httpClientUtil.execute(post);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String resut = EntityUtils.toString(httpResponse.getEntity());
				return resut;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String postRequest2(final String url,
			final Map<String, String> rawParams) throws InterruptedException,
			ExecutionException {
		FutureTask<String> task = new FutureTask<String>(
				new Callable<String>() {
					public String call() throws Exception {
						HttpPost post = new HttpPost(url);
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						for (String key : rawParams.keySet()) {
							params.add(new BasicNameValuePair(key, rawParams
									.get(key)));
						}
						post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
						HttpClient httpClientUtil = new DefaultHttpClient();
						HttpResponse httpResponse = httpClientUtil
								.execute(post);
						if (httpResponse.getStatusLine().getStatusCode() == 200) {
							String resut = EntityUtils.toString(httpResponse
									.getEntity());
							return resut;
						}
						return null;
					}
				});
		new Thread(task).start();
		return task.get();
	}

	public static void sendHttpRequest(final String urladdress,
			final HttpCallbackListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					if (DEBUG)
						Log.i(TAG, "" + urladdress);
					URL url = new URL(urladdress);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					if (listener != null) {
						// 回调onFinish()方法
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					if (listener != null) {
						// 回调onError()方法
						listener.onError(e);
					}
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}

	public interface HttpCallbackListener {
		void onFinish(String response);

		void onError(Exception e);
	}

	
	
	
	public static int ok = 200;
	public static int Partial_Content = 206;
	public static int Not_Found = 404;

	public static int check(int check) {
		switch (check) {
		case 200:
			return 200;
		case 206:
			return 206;
		case 404:
			throw new RuntimeException("未连接到服务器");
		}
		return -1;
	}
}
