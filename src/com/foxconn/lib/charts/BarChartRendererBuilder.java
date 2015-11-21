package com.foxconn.lib.charts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.ab.view.chart.BarChart;
import com.ab.view.chart.CategorySeries;
import com.ab.view.chart.ChartFactory;
import com.ab.view.chart.PointStyle;
import com.ab.view.chart.XYMultipleSeriesDataset;
import com.ab.view.chart.XYMultipleSeriesRenderer;
import com.ab.view.chart.XYSeriesRenderer;

/**
 * 
 * 创建一个条形图
 * 
 * @author samuel
 */
public class BarChartRendererBuilder {

	private XYMultipleSeriesRenderer xyMultipleSeriesRenderer;

	public BarChartRendererBuilder() {
		this.setXyMultipleSeriesRenderer(new XYMultipleSeriesRenderer());
	}

	/**
	 * 
	 * @param context
	 * 
	 * @param layoutParams
	 *            new LayoutParams(LayoutParams.MATCH_PARENT, 500)
	 * @param mXYMultipleSeriesDataset
	 *            数据初始化
	 * @param renderer
	 *            渲染器初始化
	 * @return
	 */
	public View generateChartView(Context context, LayoutParams layoutParams,
			XYMultipleSeriesDataset mXYMultipleSeriesDataset,
			XYMultipleSeriesRenderer renderer) {

		if (context == null) {
			throw new RuntimeException("初始化异常");
		}

		if (xyMultipleSeriesRenderer == null) {
			throw new RuntimeException("初始化异常");
		}

		if (mXYMultipleSeriesDataset == null) {
			throw new RuntimeException("数据初始化异常");
		}
		if (renderer == null) {
			throw new RuntimeException("渲染器初始化异常");
		}

		// 线图
		View chart = ChartFactory.getBarChartView(context,
				mXYMultipleSeriesDataset, renderer, BarChart.Type.DEFAULT);
		chart.setLayoutParams(layoutParams);
		return chart;
	}

	/**
	 * 
	 * @param title
	 *            说明文字 "数据流量"
	 * @param values
	 *            设置一系列的值
	 * @param explains
	 *            设置一系列值的解释
	 * @return
	 */
	public XYMultipleSeriesDataset buildSingleSeriesDataset(String title,
			double[] values, String[] explains) {
		XYMultipleSeriesDataset mXYMultipleSeriesDataset = new XYMultipleSeriesDataset();
		CategorySeries series = new CategorySeries(title);
		int seriesLength = values.length;
		for (int k = 0; k < seriesLength; k++) {
			series.add(values[k], 0, explains[k]);
		}
		mXYMultipleSeriesDataset.addSeries(series.toXYSeries());
		return mXYMultipleSeriesDataset;
	}

	/**
	 * XYMultipleSeriesRenderer
	 * 
	 * @return
	 */
	public XYMultipleSeriesRenderer buildDefaultXYSeriesRenderer() {

		// 柱体或者线条颜色
		int[] mSeriescolors = new int[] { Color.rgb(209, 209, 211) };

		return buildXYSeriesRenderer(mSeriescolors, true, PointStyle.CIRCLE, 1,
				16);
	}

	/**
	 * 设置渲染器颜色
	 * 
	 * @param mSeriesColors
	 * @param fillPoints
	 * @param style
	 * @param lineWidth
	 * @param textSize
	 * @return
	 */
	public XYMultipleSeriesRenderer buildXYSeriesRenderer(int[] mSeriesColors,
			boolean fillPoints, PointStyle style, float lineWidth,
			float textSize) {

		int length = mSeriesColors.length;
		for (int i = 0; i < length; i++) {
			// 创建SimpleSeriesRenderer单一渲染器
			XYSeriesRenderer r = new XYSeriesRenderer();
			// SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			// 设置渲染器颜色
			r.setColor(mSeriesColors[i]);
			r.setFillPoints(fillPoints);// / true
			r.setPointStyle(style);// PointStyle.CIRCLE
			r.setLineWidth(lineWidth);// /1 def
			r.setChartValuesTextSize(textSize); // 16 def
			// 加入到集合中
			xyMultipleSeriesRenderer.addSeriesRenderer(r);
		}

		return xyMultipleSeriesRenderer;
	}

	/**
	 * 创建渲染器 设定相关效果
	 * 
	 * @param axisTitleTextSize
	 *            坐标轴标题文字大小
	 * @param labelsTextSize
	 *            轴线上标签文字大小
	 * @param legendTextSize
	 *            说明文字大小
	 * @param xTitle
	 *            X轴标题
	 * @param yTitle
	 *            Y轴标题
	 * @param xAxisMin
	 *            X轴最小坐标点
	 * @param xAxisMax
	 *            X轴最大坐标点
	 * @param yAxisMin
	 *            y轴最小坐标点
	 * @param yAxisMax
	 *            y轴最大坐标点
	 * @param axesColor
	 *            坐标轴颜色
	 * @param xLabels
	 *            显示屏幕可见取区的XY分割数
	 * @param yLabels
	 *            显示屏幕可见取区的XY分割数
	 * @param scaleRectHeight
	 *            标尺开启设置标尺提示框高
	 * @param scaleRectWidth
	 *            设置标尺提示框宽
	 * @param scaleRectColor
	 *            设置标尺提示框背景色
	 * @param scaleLineColor
	 *            设置标尺提示框字体色
	 * @param explainTextSize1
	 *            第一行文字的大小
	 * @return XYMultipleSeriesRenderer
	 */
	public void buildRender(float axisTitleTextSize, float labelsTextSize,
			float legendTextSize, String xTitle, String yTitle, int xAxisMin,
			int xAxisMax, int yAxisMin, int yAxisMax, int axesColor,
			int xLabels, int yLabels, int scaleRectHeight, int scaleRectWidth,
			int scaleRectColor, int scaleLineColor, int explainTextSize1) {

		// 创建渲染器
		// XYMultipleSeriesRenderer xyMultipleSeriesRenderer = new
		// XYMultipleSeriesRenderer();

		// 点的大小
		// xyMultipleSeriesRenderer.setPointSize(2f);
		// 坐标轴标题文字大小
		xyMultipleSeriesRenderer.setAxisTitleTextSize(axisTitleTextSize);// 16
																			// def
		// 图形标题文字大小
		// xyMultipleSeriesRenderer.setChartTitleTextSize(18);
		// 轴线上标签文字大小
		xyMultipleSeriesRenderer.setLabelsTextSize(labelsTextSize); // / 15
																	// def
		// 说明文字大小
		xyMultipleSeriesRenderer.setLegendTextSize(legendTextSize); // / 15
																	// def
		// 图表标题
		// xyMultipleSeriesRenderer.setChartTitle("QQ使用流量统计");
		// X轴标题
		xyMultipleSeriesRenderer.setXTitle(xTitle);// "日期(天)"
		// Y轴标题
		xyMultipleSeriesRenderer.setYTitle(yTitle);// "流量(M)"
		// 设置图表上标题与X轴与Y轴的说明文字颜色
		xyMultipleSeriesRenderer.setLabelsColor(Color.BLACK);// /def
		// X轴最小坐标点
		xyMultipleSeriesRenderer.setXAxisMin(xAxisMin); // 0
		// X轴最大坐标点
		xyMultipleSeriesRenderer.setXAxisMax(xAxisMax);// 31
		// Y轴最小坐标点
		xyMultipleSeriesRenderer.setYAxisMin(yAxisMin);// 0
		// Y轴最大坐标点
		xyMultipleSeriesRenderer.setYAxisMax(yAxisMax); // 3
		// 坐标轴颜色
		xyMultipleSeriesRenderer.setAxesColor(axesColor);// Color.GRAY
		xyMultipleSeriesRenderer.setXLabelsColor(axesColor);// Color.GRAY
		xyMultipleSeriesRenderer.setYLabelsColor(0, axesColor);// Color.GRAY
		// xyMultipleSeriesRenderer.setGridColor(Color.GRAY);
		// 设置字体加粗
		// xyMultipleSeriesRenderer.setTextTypeface("sans_serif",
		// Typeface.BOLD);
		// 设置在图表上是否显示值标签
		// xyMultipleSeriesRenderer.getSeriesRendererAt(0).setDisplayChartValues(true);
		// xyMultipleSeriesRenderer.getSeriesRendererAt(1).setDisplayChartValues(true);
		// 显示屏幕可见取区的XY分割数
		xyMultipleSeriesRenderer.setXLabels(xLabels); // /2 def
		xyMultipleSeriesRenderer.setYLabels(yLabels);// / 5 def
		// X刻度标签相对X轴位置
		xyMultipleSeriesRenderer.setXLabelsAlign(Align.CENTER);
		// Y刻度标签相对Y轴位置
		xyMultipleSeriesRenderer.setYLabelsAlign(Align.LEFT);
		// /图标是否可以移动
		xyMultipleSeriesRenderer.setPanEnabled(false, false);

		xyMultipleSeriesRenderer.setZoomEnabled(false);
		xyMultipleSeriesRenderer.setZoomButtonsVisible(false);
		xyMultipleSeriesRenderer.setZoomRate(1.1f);
		// / 设置条形图的宽度
		xyMultipleSeriesRenderer.setBarSpacing(4.0);

		// 标尺开启
		xyMultipleSeriesRenderer.setScaleLineEnabled(true);
		// 标尺开启设置标尺提示框高
		xyMultipleSeriesRenderer.setScaleRectHeight(scaleRectHeight); // /
																		// 30
																		// def
		// 设置标尺提示框宽
		xyMultipleSeriesRenderer.setScaleRectWidth(scaleRectWidth); // / 80
																	// def
		// 设置标尺提示框背景色
		xyMultipleSeriesRenderer.setScaleRectColor(scaleRectColor);// /
																	// Color.argb(150,
																	// 43,
																	// 43,
																	// 43)
		xyMultipleSeriesRenderer.setScaleLineColor(scaleLineColor);// /
																	// Color.argb(255,
																	// 255,
																	// 255,
																	// 255)
		// xyMultipleSeriesRenderer.setScaleCircleRadius(20);
		// 第一行文字的大小
		xyMultipleSeriesRenderer.setExplainTextSize1(explainTextSize1);// /
																		// 13
																		// def
		// 第二行文字的大小
		xyMultipleSeriesRenderer.setExplainTextSize2(explainTextSize1);// /
																		// 13
																		// def

		// 临界线
		// double[] limit = new double[] { 15000, 12000, 4000, 9000 };
		// xyMultipleSeriesRenderer.setmYLimitsLine(limit);
		// int[] colorsLimit = new int[] { Color.rgb(100, 255, 255),
		// Color.rgb(100, 255, 255), Color.rgb(0, 255, 255),
		// Color.rgb(0, 255, 255) };
		// xyMultipleSeriesRenderer.setmYLimitsLineColor(colorsLimit);

		// 显示表格线
		xyMultipleSeriesRenderer.setShowGrid(true);
		// / 图例
		// xyMultipleSeriesRenderer.setShowLegend(true);
		// 如果值是0是否要显示
		xyMultipleSeriesRenderer.setDisplayValue0(true);
		// 背景
		xyMultipleSeriesRenderer.setApplyBackgroundColor(true);
		xyMultipleSeriesRenderer.setBackgroundColor(Color.rgb(255, 255, 255));
		xyMultipleSeriesRenderer.setMarginsColor(Color.rgb(255, 255, 255));

	}

	/**
	 * 创建渲染器 设定相关效果
	 * 
	 * @param xTitle
	 *            X轴标题
	 * @param yTitle
	 *            y轴标题
	 * @param xAxisMax
	 *            X轴最大坐标点
	 * @param yAxisMax
	 *            Y轴最大坐标点
	 * @param xLabels
	 *            显示屏幕可见取区的XY分割数
	 * @param yLabels
	 *            显示屏幕可见取区的XY分割数
	 * @return XYMultipleSeriesRenderer
	 */
	public void buildDefaultXYMultipleSeriesRenderer(String xTitle,
			String yTitle, int xAxisMax, int yAxisMax, int xLabels, int yLabels) {
		buildRender(16, 15, 15, xTitle, yTitle, 0, xAxisMax, 0, yAxisMax,
				Color.GRAY, xLabels, yLabels, 60, 80,
				Color.argb(150, 43, 43, 43), Color.argb(255, 255, 255, 255), 13);
	}

	public XYMultipleSeriesRenderer getXyMultipleSeriesRenderer() {
		return xyMultipleSeriesRenderer;
	}

	public void setXyMultipleSeriesRenderer(
			XYMultipleSeriesRenderer xyMultipleSeriesRenderer) {
		this.xyMultipleSeriesRenderer = xyMultipleSeriesRenderer;
	}

}