package com.foxconn.app.aty;

import android.app.Activity;
import android.os.Bundle;

import com.foxconn.emm.bean.UpdateInfo;
import com.foxconn.emm.utils.TestNetUtils;
import com.foxconn.emm.utils.ToastUtils;
import com.foxconn.emm.view.UpdateDialog;

public class AtyUpdate extends Activity {
	UpdateInfo updateInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		updateInfo = (UpdateInfo) getIntent().getSerializableExtra(
				UpdateInfo.TAG.UPDATEINFO);
		if (updateInfo != null) {
			if (TestNetUtils.checkNetWorkInfo(this)) {
				if (updateInfo != null) {
					if (updateInfo.isNeedUpdate(this)) {
						UpdateDialog updateDialog = new UpdateDialog(this, updateInfo);
						updateDialog.show();
					} else {
						ToastUtils.show(this, "恭喜您的系统是最新版本!");
					}
				} else {
					// / check version error
					ToastUtils.show(this, "服务器内部错误,请稍候重试");
				}
			} else {
				ToastUtils.show(this, "网络连接不可用,请检查网络连接");
			}
		} else {
			ToastUtils.show(AtyUpdate.this, "更新信息有误,请在关于页面进行更新");
			this.finish();
		}
	}


	


}
