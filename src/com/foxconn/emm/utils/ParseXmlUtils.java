package com.foxconn.emm.utils;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

import com.foxconn.emm.bean.UpdateInfo;

public class ParseXmlUtils {
	/**
	 * 远程服务器放置一个xml文件，用来说明当前新版本的信息。包括版本号，新版本功能说明，新版下载链接。
	 * 
	 * 通过 InputStream 解析文件 用 pull 解析器解析服务器返回的 xml 文件(xml文件封装了版本号)
	 * 
	 * pull 解析的特点: 事件驱动。当解析器发现元素开始、元素结束、文本、文档的开始或结束等时，发送事件，程序员编写响应这些事件的代码，保存数据。
	 * 优点：不用事先调入整个文档，占用资源少
	 * 缺点：不是持久的；事件过后，若没保存数据，那么数据就丢了；无状态性；从事件中只能得到文本，但不知该文本属于哪个元素；
	 * 使用场合：只需XML文档的少量内容，很少回头访问；一次性读取；机器内存少；
	 */
	public static UpdateInfo getUpdataInfo(InputStream is) throws Exception {
		if (is == null) {
			return null;
		}
		XmlPullParser parser = Xml.newPullParser();
		// 设置解析的数据源
		parser.setInput(is, "UTF-8");
		int type = parser.getEventType();
		// 实体类(自定义)
		UpdateInfo updateInfo = new UpdateInfo();
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_DOCUMENT:
				;
				break;
			case XmlPullParser.START_TAG:
				if (UpdateInfo.TAG.tag_VERSIONCODE.equals(parser.getName())) {
					// 获取版本号
					updateInfo.setVersion(Integer.valueOf(parser.nextText()
							.trim()));
				} else if (UpdateInfo.TAG.tag_VERSIONNAME.equals(parser
						.getName())) {
					updateInfo.setVersionName(parser.nextText().trim());
				} else if (UpdateInfo.TAG.tag_URL.equals(parser.getName())) {
					// 获取要升级的APK文件
					updateInfo.setUrl(parser.nextText());
				} else if (UpdateInfo.TAG.tag_DESCRIPTION.equals(parser
						.getName())) {
					updateInfo.setDescription(parser.nextText());
				} else if (UpdateInfo.TAG.tag_REQUIRED.equals(parser.getName())) {
					updateInfo.setRequired(parser.nextText());
				}
				break;
			}
			type = parser.next();
		}
		Log.i("xml", String.valueOf(updateInfo.getVersion()));
		Log.i("xml", updateInfo.getUrl());
		is.close();
		return updateInfo;
	}

}