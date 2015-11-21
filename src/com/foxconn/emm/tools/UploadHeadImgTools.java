package com.foxconn.emm.tools;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.foxconn.emm.utils.EMMContants;

public class UploadHeadImgTools {


	public static void uploadImgToServer2(final String uploadFile, final String newName, final String userId) {
		new Thread(new Runnable() {
			public void run() {
				uploadFile(EMMContants.SYSCONF.UPLOAD_HEADIMG, uploadFile, userId,
						newName);
			}
			/* 上传文件至Server的方法 */
			private void uploadFile(String actionUrl, String uploadFile,
					String userId, String newName) {
				String end = "\r\n";
				String twoHyphens = "--";
				String boundary = "*****";
				try {
					URL url = new URL(actionUrl);
					HttpURLConnection con = (HttpURLConnection) url
							.openConnection();
					/* 允许Input、Output，不使用Cache */
					con.setDoInput(true);
					con.setDoOutput(true);
					con.setUseCaches(false);
					/* 设置传送的method=POST */
					con.setRequestMethod("POST");
					/* setRequestProperty */
					con.setRequestProperty("Connection", "Keep-Alive");
					con.setRequestProperty("Charset", "UTF-8");
					con.setRequestProperty("Content-Type",
							"multipart/form-data;boundary=" + boundary);
					/* 设置DataOutputStream */
					DataOutputStream ds = new DataOutputStream(con
							.getOutputStream());
					ds.writeBytes(twoHyphens + boundary + end);
					
					ds.writeBytes("Content-Disposition: form-data; "
							+ "name=\"file1\";filename=\"" + newName
							+ "\";userId=\"" + userId + "\"" + end);
					ds.writeBytes(end);
					/* 取得文件的FileInputStream */
					FileInputStream fStream = new FileInputStream(uploadFile);
					/* 设置每次写入1024bytes */
					int bufferSize = 1024;
					byte[] buffer = new byte[bufferSize];
					int length = -1;
					/* 从文件读取数据至缓冲区 */
					while ((length = fStream.read(buffer)) != -1) {
						/* 将资料写入DataOutputStream中 */
						ds.write(buffer, 0, length);
					}
					ds.writeBytes(end);
					ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
					/* close streams */
					fStream.close();
					ds.flush();
					/* 取得Response内容 */
					InputStream is = con.getInputStream();
					int ch;
					StringBuffer b = new StringBuffer();
					while ((ch = is.read()) != -1) {
						b.append((char) ch);
					}
					/* 关闭DataOutputStream */
					ds.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}).start();
	}

}
