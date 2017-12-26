package com.dkzy.areaparty.phone.fragment01;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dkzy.areaparty.phone.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by boris on 2016/11/29.
 *
 */

public class computerFragment01 extends Fragment {

    public static final int chartDataNum = 52;
    //

    private TextView memoryPercent_tv;
    private TextView memoryUsed_tv;
    private TextView memoryTotal_tv;
    private TextView memoryCurrent_tv;
    private TextView cpuCurrent_tv;

    private PieChartView memoryPieChart;
    private LineChartView memoryLineChart;
    private LineChartView cpuLineChart;

    private List<PointValue> memoryPointValues;
    private List<PointValue> cpuPointValues;
    private List<Line> memoryLines;
    private List<Line> cpuLines;
    private Line memoryLine;
    private Line cpuLine;
    private Axis axisX;
    private Axis axisY;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab01_computermonitor_tab1fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void onDestroyView() {
        initChartsAndLines(memoryLineChart);
        super.onDestroyView();
    }

    private void initView() {
        cpuCurrent_tv    = (TextView) getActivity().findViewById(R.id.computerFragment01_CpuCurrent_tv);
        memoryUsed_tv    = (TextView) getActivity().findViewById(R.id.computerFragment01_MemoryUsed_tv);
        memoryTotal_tv   = (TextView) getActivity().findViewById(R.id.computerFragment01_MemoryTotal_tv);
        memoryCurrent_tv = (TextView) getActivity().findViewById(R.id.computerFragment01_MemoryCurrent_tv);
        memoryPercent_tv = (TextView) getActivity().findViewById(R.id.computerFragment01_MemoryPercent_tv);

        memoryPieChart  = (PieChartView) getActivity().findViewById(R.id.computerFragment01_MemoryPieChart);
        memoryLineChart = (LineChartView) getActivity().findViewById(R.id.memoryLineChart);
        cpuLineChart    = (LineChartView) getActivity().findViewById(R.id.cpuLineChart);

        initPieChart(memoryPieChart);
        initChartsAndLines(memoryLineChart);
        initChartsAndLines(cpuLineChart);
    }

    private void initPieChart(PieChartView pieChart) {
        List<SliceValue> values = new ArrayList<>();
        values.add(new SliceValue(100, Color.parseColor("#a8a8a8")));
        PieChartData data = new PieChartData(values);
        data.setHasLabels(false);
        data.setHasLabelsOnlyForSelected(true);
        data.setSlicesSpacing(2);
        pieChart.setZoomEnabled(false);
        pieChart.setPieChartData(data);
    }

    private void initChartsAndLines(LineChartView lineChart) {
        LineChartData lineChartData = new LineChartData();
        List<PointValue> initPointValues = new ArrayList<>();
        List<AxisValue>  axisXValues = new ArrayList<>();
        List<Line> lines = new ArrayList<>();

        // 初始化X轴标签及其间隔
        for(int i = 0; i < chartDataNum; i++) {
            if(i % 3 == 0){
                axisXValues.add(new AxisValue(i).setLabel(""));
            }
        }
        // 添加一条透明的线(用于保证初始布局)
        initPointValues.add(new PointValue(0, 0));
        initPointValues.add(new PointValue(chartDataNum - 1, 100));
        Line line = new Line(initPointValues).setColor(Color.parseColor("#00ffffff"));

        line.setCubic(true);
        line.setFilled(true);
        line.setHasLines(true);
        line.setStrokeWidth(1);
        line.setHasPoints(false);
        lines.add(line);
        // 初始化X轴属性
        axisX = new Axis();
        axisX.setHasTiltedLabels(true);
        axisX.setTextSize(10);
        axisX.setValues(axisXValues);
        axisX.setHasLines(true);
        axisX.setHasSeparationLine(true);

        // 初始化Y轴属性
        axisY = Axis.generateAxisFromRange(0, 100, 10);
        axisY.setName("百分比%");
        axisY.setTextColor(Color.BLACK);
        axisY.setHasLines(true);
        axisY.setTextSize(10);

        lineChartData.setAxisYLeft(axisY);
        lineChartData.setAxisXBottom(axisX);
        lineChartData.setAxisXTop(axisX);
        lineChartData.setLines(lines);

        lineChart.setLineChartData(lineChartData);
        lineChart.setZoomEnabled(false);
    }

    /**
     * <summary>
     *  刷新内存饼状图
     * </summary>
     * <param name="data">饼状图使用的百分比</param>
     */
    private void refreshMemoryPercentView(int data) {
        List<SliceValue> values = new ArrayList<>();
        values.add(new SliceValue(100 - data, Color.parseColor("#a8a8a8")));
        values.add(new SliceValue(data, Color.parseColor("#E65757")));
        PieChartData pieChartData = new PieChartData(values);
        pieChartData.setHasLabels(false);
        pieChartData.setHasLabelsOnlyForSelected(true);
        pieChartData.setSlicesSpacing(2);
        memoryPieChart.setZoomEnabled(false);
        memoryPieChart.setPieChartData(pieChartData);
    }

    /**
     * <summary>
     *  刷新内存折线图
     * </summary>
     * <param name="data[]">折线图数据列表</param>
     */
    private void refreshMemoryView(int[] data) {
        List<PointValue> initPointValues = new ArrayList<>();
        memoryPointValues = new ArrayList<>();
        List<AxisValue>  axisXValues = new ArrayList<>();
        memoryLines = new ArrayList<>();
        LineChartData lineChartData = new LineChartData();

        // 初始化X轴标签及其间隔
        for(int i = 0; i < chartDataNum; i++) {
            if(i % 3 == 0){
                axisXValues.add(new AxisValue(i).setLabel(""));
            }
            memoryPointValues.add(new PointValue(i, data[i]));
        }
        // 添加一条透明的线(用于保证初始布局)
        initPointValues.add(new PointValue(0, 0));
        initPointValues.add(new PointValue(chartDataNum - 1, 100));
        Line line = new Line(initPointValues).setColor(Color.parseColor("#00ffffff"));

        line.setCubic(true);
        line.setFilled(true);
        line.setHasLines(true);
        line.setStrokeWidth(1);
        line.setHasPoints(false);
        memoryLines.add(line);

        memoryLine = new Line(memoryPointValues).setColor(Color.parseColor("#E65757"));
        memoryLine.setCubic(false);
        memoryLine.setFilled(true);
        memoryLine.setHasLines(true);
        memoryLine.setStrokeWidth(1);
        memoryLine.setHasPoints(false);
        memoryLines.add(memoryLine);
        // 初始化X轴属性
        axisX = new Axis();
        axisX.setHasTiltedLabels(true);
        axisX.setTextSize(10);
        axisX.setValues(axisXValues);
        axisX.setHasLines(true);
        axisX.setHasSeparationLine(true);

        // 初始化Y轴属性
        axisY = Axis.generateAxisFromRange(0, 100, 10);
        axisY.setName("百分比%");
        axisY.setTextColor(Color.BLACK);
        axisY.setHasLines(true);
        axisY.setTextSize(10);

        lineChartData.setAxisYLeft(axisY);
        lineChartData.setAxisXBottom(axisX);
        lineChartData.setAxisXTop(axisX);
        lineChartData.setLines(memoryLines);

        memoryLineChart.setLineChartData(lineChartData);
        memoryLineChart.setZoomEnabled(false);

    }

    /**
     * <summary>
     *  刷新CPU折线图
     * </summary>
     * <param name="data[]">CPU百分比数据列表</param>
     * <returns></returns>
     */
    private void refreshCpuView(int[] data) {
        List<PointValue> initPointValues = new ArrayList<>();
        cpuPointValues = new ArrayList<>();
        List<AxisValue>  axisXValues = new ArrayList<>();
        cpuLines = new ArrayList<>();
        LineChartData lineChartData = new LineChartData();

        // 初始化X轴标签及其间隔
        for(int i = 0; i < chartDataNum; i++) {
            if(i % 3 == 0){
                axisXValues.add(new AxisValue(i).setLabel(""));
            }
            cpuPointValues.add(new PointValue(i, data[i]));
        }
        // 添加一条透明的线(用于保证初始布局)
        initPointValues.add(new PointValue(0, 0));
        initPointValues.add(new PointValue(chartDataNum - 1, 100));
        Line line = new Line(initPointValues).setColor(Color.parseColor("#00ffffff"));

        line.setCubic(true);
        line.setFilled(true);
        line.setHasLines(true);
        line.setStrokeWidth(1);
        line.setHasPoints(false);
        cpuLines.add(line);

        cpuLine = new Line(cpuPointValues).setColor(Color.parseColor("#E65757"));
        cpuLine.setCubic(false);
        cpuLine.setFilled(true);
        cpuLine.setHasLines(true);
        cpuLine.setStrokeWidth(1);
        cpuLine.setHasPoints(false);
        cpuLines.add(cpuLine);
        // 初始化X轴属性
        axisX = new Axis();
        axisX.setHasTiltedLabels(true);
        axisX.setTextSize(10);
        axisX.setValues(axisXValues);
        axisX.setHasLines(true);
        axisX.setHasSeparationLine(true);

        // 初始化Y轴属性
        axisY = Axis.generateAxisFromRange(0, 100, 10);
        axisY.setName("百分比%");
        axisY.setTextColor(Color.BLACK);
        axisY.setHasLines(true);
        axisY.setTextSize(10);

        lineChartData.setAxisYLeft(axisY);
        lineChartData.setAxisXBottom(axisX);
        lineChartData.setAxisXTop(axisX);
        lineChartData.setLines(cpuLines);

        cpuLineChart.setLineChartData(lineChartData);
        cpuLineChart.setZoomEnabled(false);
    }


    /**
     * <summary>
     *  更新涉及到的控件
     * </summary>
     * <param name="memoryPercent">字符串形式的内存百分比</param>
     * <param name="memoryUsed">字符串形式的已使用内存</param>
     * <param name="memoryTotal">字符串形式的总内存</param>
     * <param name="cpuPercent">字符串形式的CPU百分比</param>
     * <param name="memoryPercentValue">整数形式的内存百分比</param>
     * <param name="memoryPercentArray">整数数组形式的内存百分比数组</param>
     * <param name="cpuPercentArray">整数数组形式的CPU百分比数组</param>
     * <returns></returns>
     */
    public void refreshViews(String memoryPercent, String memoryUsed, String memoryTotal,
                             String cpuPercent, int memoryPercentValue, int[] memoryPercentArray,
                             int[] cpuPercentArray) {
        memoryPercent_tv.setText(memoryPercent);
        memoryUsed_tv.setText(memoryUsed);
        memoryTotal_tv.setText(memoryTotal);
        memoryCurrent_tv.setText(memoryUsed);
        cpuCurrent_tv.setText(cpuPercent);

        refreshMemoryPercentView(memoryPercentValue);
        refreshMemoryView(memoryPercentArray);
        refreshCpuView(cpuPercentArray);
    }

}
