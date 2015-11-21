package com.foxconn.app.aty;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.ab.activity.AbActivity;
import com.ab.view.chart.XYMultipleSeriesDataset;
import com.ab.view.chart.XYMultipleSeriesRenderer;
import com.ab.view.titlebar.AbTitleBar;
import com.foxconn.app.R;
import com.foxconn.lib.charts.BarChartDataSetGenerator;
import com.foxconn.lib.charts.BarChartRendererBuilder;

public class BarChartSingleActivity extends AbActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setAbContentView(R.layout.chart);

		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(R.string.chart_bar);
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg);
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		mAbTitleBar.setLogoLine(R.drawable.line);
		// 要显示图形的View
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.chart01);
		// values.add(new double[] { 1.4230, 1.1, 2.3, 1.0, 1.900, 1.720,
		// 1.2030,
		// 1.720, 1.2030, 1.720, 1.2030, 1.0, 1.900, 1.720, 1.0, 1.900,
		// 1.720, 1.0, 1.900, 1.720, 1.4230, 1.1, 2.3, 1.4230, 1.1, 2.3,
		// 1.4230, 1.1, 2.3, 1.52 });

		double[] values = new double[] { 50.4230, 10.1, 20.3, 30.0, 40.900,
				10.720, 12.2030, 17.720, 15.2030, 15.720, 10.2030, 1.0, 1.900,
				10.720, 1.0, 10.900, 15.720, 13.0, 15.900, 1.720, 15.4230,
				16.1, 28.3, 19.4230, 15.1, 2.3, 10.4230, 1.1, 2.3, 1.52 };

		// , 1.1,2.3, 1.4230, 1.1, 2.3, 1.52
		BarChartDataSetGenerator barChartDataSetGenerator = new BarChartDataSetGenerator(
				BarChartDataSetGenerator.TYPE_DATA_tomonth, values);

		// // datas
		double[] generateSerialDatas = barChartDataSetGenerator
				.generateSerialDatas();
		// / explains
		String[] explains = barChartDataSetGenerator.generateExplains();

		// /X轴上的标识
		String generateXTitleData = barChartDataSetGenerator
				.generateXTitleData();
		// /Y轴上的标识
		String generateYTitleData = barChartDataSetGenerator
				.generateYTitleData();

		// / 根据type 获取类别标题
		String generateTitle = barChartDataSetGenerator.generateTitle();

		// /获取Y轴数据的标识点最大值
		int yMaxLabels = barChartDataSetGenerator.getYMaxLabels();

		// /获取X轴数据的标识点最大值
		int xMaxLables = barChartDataSetGenerator.getXMaxLables();

		int xLables = barChartDataSetGenerator.getXLables();

		int yLables = barChartDataSetGenerator.getYLables();

		BarChartRendererBuilder barChartRendererBuilder = new BarChartRendererBuilder();
		barChartRendererBuilder.buildDefaultXYMultipleSeriesRenderer(
				generateXTitleData, generateYTitleData, xMaxLables, yMaxLabels,
				xLables, yLables);
		XYMultipleSeriesRenderer buildDefaultXYSeriesRenderer = barChartRendererBuilder
				.buildDefaultXYSeriesRenderer();
		XYMultipleSeriesDataset buildSingleSeriesDataset = barChartRendererBuilder
				.buildSingleSeriesDataset(generateTitle, generateSerialDatas,
						explains);
		View generateChartView = barChartRendererBuilder.generateChartView(
				BarChartSingleActivity.this, new LayoutParams(
						LayoutParams.MATCH_PARENT, 500),
				buildSingleSeriesDataset, buildDefaultXYSeriesRenderer);
		linearLayout.addView(generateChartView);

	}
}
