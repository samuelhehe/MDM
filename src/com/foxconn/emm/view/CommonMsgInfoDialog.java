package com.foxconn.emm.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.foxconn.app.R;
import com.foxconn.emm.bean.FileInfo;
import com.foxconn.emm.bean.PicInfo;
import com.foxconn.emm.tools.FileDES;
import com.foxconn.emm.utils.EMMContants;

public class CommonMsgInfoDialog extends Dialog implements
		View.OnClickListener, OnFocusChangeListener {

	private Context context;
	private int position;
	private int flag;
	private String pwd;
	private String subject;
	private String suffix;
	private String sendTime;
	private Button common_dialog_positive_btn;
	private Button common_dialog_negative_btn;
	private Button common_dialog_pic_list_btn;
	private TextView common_dialog_title_tv;
	private TextView common_dialog_connect_tv;
	private TextView common_dialog_prompt_tv;
	private EditText common_dialog_input_pwd_et;
	private LinearLayout input_pwd_lay;
	private LinearLayout common_dialog_info_lay;
	private LinearLayout common_dialog_pic_list_lay;
	private ListView common_dialog_pic_list_lv;
	private List<String> picPathList;
	private Boolean[] exists;
	
	public CommonMsgInfoDialog(Context context, int flag){
		super(context, R.style.Theme_CustomDialog);
		this.context = context;
		this.flag = flag;
	}

	public CommonMsgInfoDialog(Context context, int position, int flag) {
		super(context, R.style.Theme_CustomDialog);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.position = position;
		this.flag = flag;
	}

	public CommonMsgInfoDialog(Context context, int position, int flag,
			String sendTime) {
		super(context, R.style.Theme_CustomDialog);
		this.context = context;
		this.position = position;
		this.flag = flag;
		this.sendTime = sendTime;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_msginfo_dialog);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		initView();
	}

	private void initView() {
		common_dialog_positive_btn = (Button) findViewById(R.id.common_dialog_positive_btn);
		common_dialog_negative_btn = (Button) findViewById(R.id.common_dialog_negative_btn);
		common_dialog_pic_list_btn = (Button) findViewById(R.id.common_dialog_pic_list_btn);
		common_dialog_title_tv = (TextView) findViewById(R.id.common_dialog_title_tv);
		common_dialog_connect_tv = (TextView) findViewById(R.id.common_dialog_connect_tv);
		common_dialog_prompt_tv = (TextView) findViewById(R.id.common_dialog_prompt_tv);
		common_dialog_input_pwd_et = (EditText) findViewById(R.id.common_dialog_input_pwd_et);
		input_pwd_lay = (LinearLayout) findViewById(R.id.input_pwd_lay);
		common_dialog_info_lay = (LinearLayout) findViewById(R.id.common_dialog_info_lay);
		common_dialog_pic_list_lay = (LinearLayout) findViewById(R.id.common_dialog_pic_list_lay);
		common_dialog_pic_list_lv = (ListView) common_dialog_pic_list_lay
				.findViewById(R.id.common_dialog_pic_list_lv);

		common_dialog_positive_btn.setOnClickListener(this);
		common_dialog_negative_btn.setOnClickListener(this);
		common_dialog_pic_list_btn.setOnClickListener(this);
		common_dialog_input_pwd_et.setOnFocusChangeListener(this);
		common_dialog_pic_list_lv.setOnItemClickListener(new ItemClick());
	}

	@Override
	public void show() {
		super.show();
		setCancelable(false);
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

	@Override
	public void setTitle(CharSequence title) {
		// TODO Auto-generated method stub
		super.setTitle(title);
	}

	@Override
	public void setCancelable(boolean flag) {
		super.setCancelable(flag);
	}

	public void setTwoBtnDialog(String title, String msg, String positiveBtn,
			String negativeBtn) {
		common_dialog_title_tv.setText(title);
		common_dialog_connect_tv.setText(msg);
		common_dialog_positive_btn.setText(positiveBtn);
		common_dialog_negative_btn.setText(negativeBtn);
	}

	public void setOneBtnDialog(String title, String msg, String negativeBtn) {
		common_dialog_title_tv.setText(title);
		common_dialog_connect_tv.setText(msg);
		common_dialog_positive_btn.setVisibility(View.GONE);
		common_dialog_negative_btn.setText(negativeBtn);
	}

	public void setOpenFielDailog(String title, String msg, String positiveBtn,
			String negativeBtn, String suffix) {
		this.subject = title;
		this.suffix = suffix;
		common_dialog_title_tv.setText(title);
		common_dialog_connect_tv.setText(msg);
		common_dialog_positive_btn.setText(positiveBtn);
		common_dialog_negative_btn.setText(negativeBtn);
	}

	public void setInputPwdDailog(String title, String msg, String positiveBtn,
			String negativeBtn, String pwd, String suffix) {
		this.pwd = pwd;
		this.subject = title;
		this.suffix = suffix;
		common_dialog_title_tv.setText(title);
		common_dialog_connect_tv.setText(msg);
		common_dialog_positive_btn.setText(positiveBtn);
		common_dialog_negative_btn.setText(negativeBtn);
		input_pwd_lay.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.common_dialog_positive_btn:
			clickPositiveBtn();

			break;
		case R.id.common_dialog_negative_btn:
			dismiss();
			break;
		case R.id.common_dialog_pic_list_btn:
			dismiss();
			break;
		}
	}

	public void clickPositiveBtn() {
		switch (flag) {
		case EMMContants.DialogFlag.SHOW:
			showInfo();
			dismiss();
			break;

		case EMMContants.DialogFlag.DELETE:
			deleteInfo();
			dismiss();
			break;
		case EMMContants.DialogFlag.OPEN:
			open(getFilePath(), false);
			break;
		case EMMContants.DialogFlag.INPUT_PWD:
			judgePwd();
			break;
		}
	}

	/**
	 * 显示消息详细信息
	 */
	public void showInfo() {

	}

	/**
	 * 删除选中消息
	 */
	public void deleteInfo() {

	}

	/**
	 * 打开下载文件
	 */
	public void open(String path, boolean flag) {
		if (suffix.equals(".pdf")) {
			if (flag) {
				desFile(new FileInfo().getFileName(subject, sendTime));
			}
			Uri uri = Uri.parse(path
					+ new FileInfo().getFileName(subject, sendTime));
			Intent intent = new Intent(context, MuPDFActivity.class);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(uri);
			context.startActivity(intent);
			dismiss();
		} else {
			StringTokenizer st = new StringTokenizer(suffix, ",", true);
			String picSuffix = null;
			if (st.countTokens() == 2) {
				picSuffix = getSuffix(suffix.substring(0, suffix.length() - 1));
				Uri uri;
				if (!TextUtils.isEmpty(picSuffix)) {
					if (flag) {
						mkdirsPicDesPath();
						desFile(getRelativePicPath(1, picSuffix));
					}
					uri = Uri.parse(path + getRelativePicPath(1, picSuffix));
				} else {
					if (flag) {
						mkdirsPicDesPath();
						desFile(getRelativePicPath(1, ".png"));
					}
					uri = Uri.parse(path + getRelativePicPath(1, ".png"));
				}
				Intent intent = new Intent(context, MuPDFActivity.class);
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(uri);
				context.startActivity(intent);
				dismiss();
			} else {
				int count = st.countTokens() / 2;
				String str[] = new String[count];
				List<String> list = new ArrayList<String>();
				picPathList = new ArrayList<String>();
				exists = new Boolean[count];
				int i = 0;
				while (st.hasMoreElements()) {
					String url = st.nextToken();
					if (!url.equals(",")) {
						i++;
						picSuffix = getSuffix(url);
						String picPath;
						if (!TextUtils.isEmpty(picSuffix)) {
							// if (flag) {
							// mkdirsPicDesPath();
							// desFile(getRelativePicPath(i, picSuffix));
							// }
							picPath = path + getRelativePicPath(i, picSuffix);
							picPathList.add(picPath);
							if (isExists(getFilePath()
									+ getRelativePicPath(i, picSuffix))) {
								str[i - 1] = "打开第" + i + "张图片";
								list.add(str[i - 1]);
								exists[i - 1] = true;
							} else {
								str[i - 1] = "请至下载列表中重新下载第" + i + "张图片";
								list.add(str[i - 1]);
								exists[i - 1] = false;
							}
						} else {
							// if (flag) {
							// mkdirsPicDesPath();
							// desFile(getRelativePicPath(1, ".png"));
							// }
							picPath = path + getRelativePicPath(i, ".png");
							picPathList.add(picPath);
							if (isExists(getFilePath()
									+ getRelativePicPath(i, ".png"))) {
								str[i - 1] = "打开第" + i + "张图片";
								list.add(str[i - 1]);
								exists[i - 1] = true;
							} else {
								str[i - 1] = "请至下载列表中重新下载第" + i + "张图片";
								list.add(str[i - 1]);
								exists[i - 1] = false;
							}
						}

					}
				}
				displayPicList(list, exists);
			}
		}

	}

	private void displayPicList(List<String> str, Boolean[] exists) {
		common_dialog_info_lay.setVisibility(View.GONE);
		common_dialog_pic_list_lay.setVisibility(View.VISIBLE);
		common_dialog_pic_list_lv.setAdapter(new ArrayAdapter<String>(context,
				R.layout.simple_list_item, str));
	}

	/**
	 * 解密并打开文件
	 */
	public void desFile(String relativeUrl) {
		try {
			FileDES fileDES = new FileDES(pwd);
			fileDES.doDecryptFile(getFilePath() + relativeUrl, getDesFilePath()
					+ relativeUrl); // 解密
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 判断密码是否正确
	 */
	public void judgePwd() {
		if (pwd.equals(common_dialog_input_pwd_et.getText().toString().trim())) {
			open(getDesFilePath(), true);
		} else {
			common_dialog_prompt_tv.setVisibility(View.VISIBLE);
			common_dialog_input_pwd_et.setText("");
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if (hasFocus) {
			common_dialog_prompt_tv.setVisibility(View.GONE);
		}
	}

	private String getFilePath() {
		String filePath = null;
		filePath = EMMContants.LocalConf.getEMMLocalHost_dirPath()
				+ EMMContants.LocalConf.download_dirpath;
		return filePath;
	}

	private String getDesFilePath() {
		String desFilePath = null;
		desFilePath = EMMContants.LocalConf.getEMMLocalHost_dirPath()
				+ EMMContants.LocalConf.decryption_dirpath;
		return desFilePath;
	}

	/**
	 * 文件名201410301042/文件名(201410301042)1.png
	 * 
	 * @param i
	 * @param suffix
	 * @return
	 */
	private String getRelativePicPath(int i, String suffix) {
		String path = null;
		path = new PicInfo().getFileName(subject, sendTime) + "/"
				+ new PicInfo().getPicName(subject, i, sendTime) + suffix;
		return path;
	}

	private boolean isExists(String path) {
		File file = new File(path);
		boolean isExists = file.exists();
		return isExists;
	}

	private void mkdirsPicDesPath() {
		File file = new File(getDesFilePath()
				+ new PicInfo().getFileName(subject, sendTime));
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	private String getSuffix(String fileurl) {
		String suffix = "";
		if (fileurl.lastIndexOf(".") != -1) {
			suffix = fileurl.substring(fileurl.lastIndexOf("."));
			if (suffix.indexOf("/") != -1 || suffix.indexOf("?") != -1
					|| suffix.indexOf("&") != -1) {
				suffix = null;
			}
		}
		return suffix;
	}

	final class ItemClick implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			if (exists[position]) {
				if (TextUtils.isEmpty(pwd) || pwd == "null") {

				} else {
					mkdirsPicDesPath();
					desFile(picPathList.get(position).replace(getDesFilePath(),
							""));
					String s = picPathList.get(position).replace(
							getDesFilePath(), "");
					System.out.println(s);
				}
				Uri uri = Uri.parse(picPathList.get(position));
				Intent intent = new Intent(context, MuPDFActivity.class);
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(uri);
				context.startActivity(intent);
			}
		}
	}
}
