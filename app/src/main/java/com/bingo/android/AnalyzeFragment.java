package com.bingo.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class AnalyzeFragment extends Fragment {

//    private BarChart barChart;
//    private BarData barData;

    private PieChart pieChart;
    private PieData pieData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle("分析");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = View.inflate(getActivity(), R.layout.fragment_analyze, null);

//        barChart = view.findViewById(R.id.bar_chart);
        pieChart = view.findViewById(R.id.pie_chart);

        showPieChart(pieChart, getPieChartData());
        return view;
    }
    /**
     *用来处理数据
     */

    private List<PieEntry> getPieChartData() {
        List<PieEntry> mPie = new ArrayList<>();

        long total = MainActivity.child.getFinish_total();
        mPie.add(new PieEntry(100*((float)MainActivity.child.getFinish_in())/total, "课内作业"));
        mPie.add(new PieEntry(100*((float)MainActivity.child.getFinish_out())/total, "课外学习"));
        mPie.add(new PieEntry(100*((float)MainActivity.child.getFinish_sport())/total, "运动"));
        mPie.add(new PieEntry(100*((float)MainActivity.child.getFinish_housework())/total, "家务"));
        mPie.add(new PieEntry(100*((float)MainActivity.child.getFinish_hobby())/total, "兴趣爱好"));
        return mPie;
    }

    private void showPieChart(PieChart pieChart, List<PieEntry> pieList) {
        PieDataSet dataSet = new PieDataSet(pieList,"任务类型");

        // 设置颜色list，让不同的块显示不同颜色
        ArrayList<Integer> colors = new ArrayList<Integer>();
        int[] MATERIAL_COLORS = {
                Color.rgb(200, 172, 255)
        };
        for (int c : MATERIAL_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c);
        }
        dataSet.setColors(colors);
        PieData pieData = new PieData(dataSet);

        // 设置描述
        Description description = new Description();
        description.setEnabled(false);
        pieChart.setDescription(description);
        // 设置半透明圆环的半径, 0为透明
        pieChart.setTransparentCircleRadius(0f);

        //设置初始旋转角度
        pieChart.setRotationAngle(-15);

        //数据连接线距图形片内部边界的距离，为百分数
        dataSet.setValueLinePart1OffsetPercentage(80f);

        //设置连接线的颜色
        dataSet.setValueLineColor(Color.LTGRAY);
        // 连接线在饼状图外面
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        // 设置饼块之间的间隔
        dataSet.setSliceSpace(1f);
        dataSet.setHighlightEnabled(true);
        // 显示图例
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);

        // 和四周相隔一段距离,显示数据
        pieChart.setExtraOffsets(26, 5, 26, 5);

        // 设置pieChart图表是否可以手动旋转
        pieChart.setRotationEnabled(false);
        // 设置piecahrt图表点击Item高亮是否可用
        pieChart.setHighlightPerTapEnabled(true);
        // 设置pieChart图表展示动画效果，动画运行1.4秒结束
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        //设置pieChart是否只显示饼图上百分比不显示文字
        pieChart.setDrawEntryLabels(true);
        //是否绘制PieChart内部中心文本
        pieChart.setDrawCenterText(false);
        // 绘制内容value，设置字体颜色大小
        pieData.setDrawValues(true);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.DKGRAY);

        pieChart.setData(pieData);
        // 更新 pieChart 视图
        pieChart.postInvalidate();
    }


//    private BarData getBarData(int count, float range) {
//        // x轴的数据集合
//        ArrayList<String> xValues = new ArrayList<String>();
//        for (int i = 0; i < count; i++) {
//            xValues.add(i + "");
//        }
//
//        // y轴的数据集合
//        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
//        for (int i = 0; i < count; i++) {
//            float value = (float) (Math.random() * range/*100以内的随机数*/) + 3;
//            yValues.add(new BarEntry(value, i));
//        }
//        BarDataSet barDataSet = new BarDataSet(yValues, "collection");
//
//        BarData barData = new BarData(xValues, barDataSet);
//
//        return barData;
//    }
}