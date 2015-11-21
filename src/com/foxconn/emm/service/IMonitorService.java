package com.foxconn.emm.service;

import com.foxconn.emm.bean.LimitListInfo;

public interface IMonitorService {

	/**
	 * 重新进行限制
	 * @param limitListInfo
	 */
	public void removeTempStopLimit(LimitListInfo limitListInfo);

	
	/**
	 * 暂停限制
	 * @param limitListInfo
	 */
	public void addTempStopLimit(LimitListInfo limitListInfo);
}
