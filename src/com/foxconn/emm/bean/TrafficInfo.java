package com.foxconn.emm.bean;

import android.graphics.drawable.Drawable;

public class TrafficInfo {
	private String name;
	private Drawable icon;
	private int uid;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

}
