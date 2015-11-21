package com.foxconn.emm.tools;

import android.net.TrafficStats;

public class TrafficTools {
	/**
	 * 拿到Wifi的总流量包括Wifi上传的和下载的总流量
	 * 
	 * @return
	 */
	public long getTotalWlanTraffic() {
		// 拿到总共接收到的数据大小
		long total_received = TrafficStats.getTotalRxBytes();
		// 拿到总共发送的数据大小
		long total_transmitted = TrafficStats.getTotalTxBytes();
		// 拿到总数据大小
		long total = total_received + total_transmitted;
		// //拿到wifi的总数据大小
		long total_wifi = total - getTotalDataTraffic();

		return total_wifi;
	}

	/**
	 * 拿到2G和3G的总数据流量大小 包括接受的和发送的 从本次开机以来的
	 * 
	 * @return
	 */
	public long getTotalDataTraffic() {
		// 拿到2G和3G的总数据大小
		long total_2g_3g = getTotalDataReceived() + getTotalDataTransmitted();
		return total_2g_3g;
	}

	/**
	 * 拿到2G和3G的总共下载的数据大小 从本次开机以来的
	 * 
	 * @return
	 */
	public long getTotalDataReceived() {

		return TrafficStats.getMobileRxBytes();
	}

	/**
	 * 拿到2G和3G的总上行的数据大小 从本次开机以来的
	 * 
	 * @return
	 */
	public long getTotalDataTransmitted() {

		return TrafficStats.getMobileTxBytes();
	}

	/**
	 * 根据uid得到这个应用的接收数据大小 从本次开机以来的
	 * 
	 * @param uid
	 * @return
	 */
	public long getSingleUidReceivedData(int uid) {
		long received = TrafficStats.getUidRxBytes(uid);
		return received;
	}

	/**
	 * 根据uid得到这个应用的发送数据大小 从本次开机以来的
	 * 
	 * @param uid
	 * @return
	 */
	public long getSingleUidTransmittedData(int uid) {
		long transmitted = TrafficStats.getUidTxBytes(uid);
		return transmitted;
	}

}
