package com.foxconn.emm.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

public class IconImgUtils {

	/**
	 * get headicon from server
	 * 
	 * @return bitmap or null
	 */
	public static Bitmap getImgFromServer(String imgUri) {

		Bitmap bimg = null;
		try {
			URL url = new URL(imgUri);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestProperty(
					"Accept",
					"image/gif, image/jpeg, image/pjpeg, image/pjpeg,application/x-shockwave-flash, application/xaml+xml,application/vnd.ms-xpsdocument, application/x-ms-xbap,application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword");
			conn.setRequestProperty("Charset", "UTF-8");
			int code = conn.getResponseCode();
			if (code == 200) {
				InputStream is = conn.getInputStream();
				bimg = BitmapFactory.decodeStream(is);
				is.close();
				return bimg;
			} else {
				return bimg;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return bimg;
	}

	/**
	 * 
	 * http://10.207.248.21/userImg/ceshi001.png
	 * 
	 * @param imageUrl
	 * @return
	 */
	public static String getImageNameIncludeType(String imageUrl) {
		if (TextUtils.isEmpty(imageUrl)) {
			L.d("IconImgUtils", "imageUrl is null ");
			return null;
		}
		String subresult = imageUrl.substring(imageUrl.lastIndexOf("/")+1,
				imageUrl.lastIndexOf("."));
		L.d("IconImgUtils", subresult);
		return subresult;
	}

	/**
	 * 对比client端的用户头像和SelfService端的用户头像名称是否一样.
	 * 
	 * @param clientUserImgName
	 * @param serverUserImgName
	 * @return
	 */
	public static boolean compare2ImageName(String clientUserImgName,
			String serverUserImgName) {
		return TextUtils.equals(clientUserImgName, serverUserImgName);
	}

}
