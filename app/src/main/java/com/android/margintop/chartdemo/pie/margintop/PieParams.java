package com.android.margintop.chartdemo.pie.margintop;

import android.graphics.PointF;

/**
 * Created by margintop on 2017/10/14.
 *
 * @Describe 画View的辅助参数。
 */

public class PieParams {

    private PointF currentPointF;
    private float rotateAngel;
    private PieSlice pieSlice;

    public PointF getCurrentPointF() {
        return currentPointF;
    }

    public void setCurrentPointF(PointF currentPointF) {
        this.currentPointF = currentPointF;
    }

    public float getRotateAngel() {
        return rotateAngel;
    }

    public void setRotateAngel(float rotateAngel) {
        this.rotateAngel = rotateAngel;
    }

    public PieSlice getPieSlice() {
        return pieSlice;
    }

    public void setPieSlice(PieSlice pieSlice) {
        this.pieSlice = pieSlice;
    }
}
