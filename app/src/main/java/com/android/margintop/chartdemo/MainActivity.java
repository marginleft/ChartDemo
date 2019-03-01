package com.android.margintop.chartdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.android.margintop.chartdemo.line.Line;
import com.android.margintop.chartdemo.line.LineGraph;
import com.android.margintop.chartdemo.line.LinePoint;
import com.android.margintop.chartdemo.pie.margintop.PieGraph;
import com.android.margintop.chartdemo.pie.margintop.PieSlice;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LineGraph mLgLine;
    private PieGraph mPieChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        mLgLine = (LineGraph) findViewById(R.id.lg_line);
        mPieChartView = (PieGraph) findViewById(R.id.pie_chart);
    }


    private void initData() {
        // 线
        Line l = new Line();
        LinePoint p = new LinePoint();
        p.setX(0);
        p.setY(5);
        l.addPoint(p);
        p = new LinePoint();
        p.setX(8);
        p.setY(8);
        l.addPoint(p);
        p = new LinePoint();
        p.setX(10);
        p.setY(4);
        l.addPoint(p);
        l.setColor(Color.parseColor("#FFBB33"));

        mLgLine.addLine(l);
        mLgLine.setRangeY(0, 10);
//        mLgLine.setLineToFill(0);
        // 饼
        List<PieSlice> pieSliceList = new ArrayList<>();
        pieSliceList.add(new PieSlice(0.12, Color.RED, "网购", R.mipmap.ic_launcher));
        pieSliceList.add(new PieSlice(5243327.32, Color.BLUE, "线下", R.mipmap.ic_launcher));
        System.out.println(pieSliceList.get(1).getValue());
//        pieSliceList.add(new PieSlice(200f, Color.YELLOW, "缴费", R.mipmap.ic_launcher));
//        pieSliceList.add(new PieSlice(2.03f, Color.parseColor("#ff00ff"), "手机", R.mipmap.ic_launcher));
//        pieSliceList.add(new PieSlice(60f, Color.parseColor("#9c9c9c"), "其他", R.mipmap.ic_launcher));

//        mPieChartView.hideIcons();
        mPieChartView.setPieSliceList(pieSliceList);
    }

    public void start(View view) {
        mPieChartView.startAnimation();
    }
}
