package com.android.margintop.chartdemo.pie.margintop;

import android.support.annotation.DrawableRes;

/**
 * Created by margintop on 2017/5/5.
 *
 * @描述 饼状图中的饼块对象。
 */

public class PieSlice {

    private double value;
    private int color;
    private String describe;
    private int iconRes;

    public PieSlice(double value, int color, String describe, @DrawableRes int iconRes) {
        this.value = value;
        this.color = color;
        this.describe = describe;
        this.iconRes = iconRes;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }
}
