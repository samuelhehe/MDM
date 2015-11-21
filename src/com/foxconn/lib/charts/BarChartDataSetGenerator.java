package com.foxconn.lib.charts;

import java.util.Calendar;

import com.foxconn.emm.utils.TextFormater;

public class BarChartDataSetGenerator {

	/**
	 * 今天的数据流量 表示天大于10
	 */
	public static final int TYPE_DATA_today = 11;
	/**
	 * 昨天的数据流量 表示天大于10
	 */
	public static final int TYPE_DATA_yesterday = 12;
	/**
	 * 这个月的数据流量
	 */
	public static final int TYPE_DATA_tomonth = 3;
	/**
	 * 上个月的数据流量
	 */
	public static final int TYPE_DATA_lastmonth = 4;

	/**
	 * 今天的WLAN流量 表示天大于20
	 */
	public static final int TYPE_WLAN_today = 25;
	/**
	 * 昨天的WLAN流量 表示天大于20
	 */
	public static final int TYPE_WLAN_yesterday = 26;
	/**
	 * 这个月的WLAN流量
	 */
	public static final int TYPE_WLAN_tomonth = 7;
	/**
	 * 上个月的WLAN流量 
	 */
	public static final int TYPE_WLAN_lastmonth = 8;
	private int type;
	private double[] values;

	public BarChartDataSetGenerator(int type, double[] values) {
		this.type = type;
		this.values = generateSerialDatas(values);
	}

	/**
	 * 
	 * // 数据 List<double[]> values = new ArrayList<double[]>(); // 每个数据点的颜色
	 * List<int[]> colors = new ArrayList<int[]>(); // 每个数据点的简要 说明
	 * List<String[]> explains = new ArrayList<String[]>();
	 * 
	 * values.add(new double[] { 1.4230, 1.1, 2.3, 1.0, 1.900, 1.720,
	 * 1.2030, 1.720, 1.2030, 1.720, 1.2030, 1.0, 1.900, 1.720, 1.0, 1.900,
	 * 1.720, 1.0, 1.900, 1.720, 1.4230, 1.1, 2.3, 1.4230, 1.1, 2.3, 1.4230,
	 * 1.1, 2.3, 1.52 });
	 * 
	 * 
	 */

	public String[] generateExplains() {

		String[] explains;
		// /是表示天的 0：00-24：00
		if (type > 10) {
			explains = new String[] { "0:00-1:00", "1:00-2:00",
					"2:00-3:00", "3:00-4:00", "4:00-5:00", "5:00-6:00",
					"6:00-7:00", "7:00-8:00", "8:00-9:00", "9:00-10:00",
					"10:00-11:00", "11:00-12:00", "12:00-13:00",
					"13:00-14:00", "14:00-15:00", "15:00-16:00",
					"16:00-17:00", "17:00-18:00", "18:00-19:00",
					"19:00-20:00", "20:00-21:00", "21:00-22:00",
					"22:00-23:00", "23:00-24:00" };
			// / 表示当然是月份的天数了
		} else {

			int length = values.length;
			if (length > 31 || length < 28) {
				throw new RuntimeException("数据天数异常");
			}

			explains = new String[length];
			int month = Calendar.getInstance().get(Calendar.MONTH);
			if(type == TYPE_DATA_lastmonth ){
				month -=1;
			}
			for (int i = 0; i < length; i++) {
				explains[i] = month+"月"+(i + 1) + "日";
			}
		}
		return explains;
	}

	/**
	 * 生成线性数据，数组 增加一些数字的处理，最后一位如果是0就舍去，保留两位小数最好
	 * 
	 * 好像没什么用
	 * 
	 * @param values
	 * @return
	 */
	private double[] generateSerialDatas(double[] values) {

		for (int i = 0; i < values.length; i++) {
			values[i] = TextFormater.left2(values[i]);
		}
		return values;
	}

	/**
	 * 生成线性数据，数组 增加一些数字的处理，最后一位如果是0就舍去，保留两位小数最好
	 * 
	 * @return
	 */
	public double[] generateSerialDatas() {
		return values;
	}

	/**
	 * 根据类型生成X轴上的标识
	 * 
	 * @param type
	 * @return
	 */
	public String generateXTitleData() {

		String xTitle = new String();
		switch (type) {
		case TYPE_DATA_today:
		case TYPE_WLAN_today:
			xTitle = "当天(时)";
			break;
		case TYPE_DATA_yesterday:
		case TYPE_WLAN_yesterday:

			xTitle = "昨天(时)";
			break;
		case TYPE_DATA_tomonth:
		case TYPE_WLAN_tomonth:
			xTitle = "本月(天)";
			break;
		case TYPE_DATA_lastmonth:
		case TYPE_WLAN_lastmonth:
			xTitle = "上个月(天)";
			break;
		default:
			xTitle = "暂无数据";

		}
		return xTitle;
	}

	/**
	 * 相当于图例的一个标题， 根据type 获取类别标题
	 * 
	 * @param type
	 * @return
	 */
	public String generateTitle() {

		String title = new String();
		switch (type) {
		case TYPE_DATA_today:
		case TYPE_DATA_yesterday:
		case TYPE_DATA_tomonth:
		case TYPE_DATA_lastmonth:
			title = "数据流量";
			break;
		case TYPE_WLAN_today:
		case TYPE_WLAN_yesterday:
		case TYPE_WLAN_tomonth:
		case TYPE_WLAN_lastmonth:
			title = "WLAN流量";
			break;
		default:
			title = "暂无数据";

		}
		return title;

	}

	/**
	 * 根据类型生成Y轴上的标识
	 * 
	 * @param type
	 * @return
	 */
	public String generateYTitleData() {

		String yTitle = new String();
		switch (type) {
		case TYPE_DATA_today:
		case TYPE_DATA_yesterday:
		case TYPE_DATA_tomonth:
		case TYPE_DATA_lastmonth:
			yTitle = "数据流量(M)";
			break;
		case TYPE_WLAN_today:
		case TYPE_WLAN_yesterday:
		case TYPE_WLAN_tomonth:
		case TYPE_WLAN_lastmonth:
			yTitle = "WLAN流量(M)";
			break;
		default:
			yTitle = "暂无数据";
		}
		return yTitle;
	}

	/**
	 * 获取Y轴数据的标识点最大值
	 * 
	 * @param arr
	 * @return
	 */
	public int getYMaxLabels() {
		double maxValues = getMax(values);
		return (int) (maxValues+(int) Math.ceil(maxValues % 10));
	}

	/**
	 * 获取X轴数据的标识点最大值
	 * 
	 * @param arr
	 * @return
	 */
	public int getXMaxLables() {
		return values.length + 1;
	}

	/**
	 * 30天/月 6个点 24小时/天 6个点 31天/月 6个点都行吧
	 * 
	 * @param arr
	 * @return
	 */
	public int getXLables() {
		return 2;
	}

	/**
	 * 
	 * @param arr
	 * @return
	 */
	public int getYLables() {
		return 2;
	}

	/**
	 * 返回Max of SerialValues
	 * 
	 * @param arr
	 * @return
	 */
	private double getMax(double[] arr) {
		double max = arr[0];
		for (int x = 1; x < arr.length; x++) {
			if (arr[x] > max)
				max = arr[x];
		}
		return max;
	}

}
