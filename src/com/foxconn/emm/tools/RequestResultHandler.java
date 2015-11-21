package com.foxconn.emm.tools;

import org.json.JSONException;
import org.json.JSONObject;

import com.foxconn.emm.bean.RegResult;

public class RequestResultHandler {

	public static RegResult parseRegResult(JSONObject jsonObj)
			throws JSONException {
		RegResult regResult = new RegResult();
		regResult.setSuccess(jsonObj.getString(RegResult.TAG.SUCCESS));
		regResult.setMsg(jsonObj.getString(RegResult.TAG.MSG));
		if (jsonObj.toString().contains(RegResult.TAG.CODE)) {
			regResult.setCode(jsonObj.getString(RegResult.TAG.CODE));
		}
		if (jsonObj.toString().contains(RegResult.TAG.FLAG)) {
			regResult.setFlag(jsonObj.getString(RegResult.TAG.FLAG));
		}
		return regResult;
	}

}
