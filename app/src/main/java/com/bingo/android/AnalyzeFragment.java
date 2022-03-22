package com.bingo.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;

import java.util.ArrayList;

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

//        barData = getBarData(24, 10);
//        barChart.setData(barData);
        return view;
    }
    /**
     *用来处理数据
     */
    private BarData getBarData(int count, float range) {
        // x轴的数据集合
        ArrayList<String> xValues = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xValues.add(i + "");
        }

        // y轴的数据集合
        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
        for (int i = 0; i < count; i++) {
            float value = (float) (Math.random() * range/*100以内的随机数*/) + 3;
            yValues.add(new BarEntry(value, i));
        }
        BarDataSet barDataSet = new BarDataSet(yValues, "collection");

        BarData barData = new BarData(xValues, barDataSet);

        return barData;
    }
}