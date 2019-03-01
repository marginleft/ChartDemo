package com.android.margintop.chartdemo.pie.margintop;

import android.animation.TypeEvaluator;
import android.graphics.PointF;
import android.support.annotation.NonNull;

/**
 * Created by margintop on 2017/5/6.
 *
 * @描述 icon移动曲线。
 */

public class BezierEvaluator implements TypeEvaluator<PointF> {

    private PointF mMiddlePoint;

    public BezierEvaluator(PointF middlePoint) {
        this.mMiddlePoint = middlePoint;
    }

    @NonNull
    @Override
    public PointF evaluate(float t, PointF startValue, PointF endValue) {
        float x = (1 - t) * (1 - t) * startValue.x + 2 * t * (1 - t) * mMiddlePoint.x + t * t * endValue.x;
        float y = (1 - t) * (1 - t) * startValue.y + 2 * t * (1 - t) * mMiddlePoint.y + t * t * endValue.y;
        return new PointF(x, y);
    }

}
