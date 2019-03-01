package com.android.margintop.chartdemo.pie.margintop;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.android.margintop.chartdemo.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by margintop on 2017/5/4.
 *
 * @描述 饼状图。
 */

public class PieGraph extends View {

    private List<PieParams> mPieParamsList;
    private Paint mPaint;
    private RectF mOuterRectF;
    private RectF mInnerRectF;
    private Path mPath;
    private AnimatorSet mAs;
    private float mRadius;
    private float mInnerRadius;
    private int mTextSize;
    private float mTextHeight;
    private float mTextBottom;
    private int mMidX;
    private int mMidY;
    private boolean mHideIcons;
    private float mPointMargin;
    private float mPointRadius;
    private int mBrokenLineWidth;
    private float mLeftTextTopY;
    private float mRightTextBottomY;
    private int mIconScale = 2;
    private float mLineTextSpace;

    public PieGraph(Context context) {
        this(context, null);
    }

    public PieGraph(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
        initPaint();
        initRectFsAndPath();
    }

    /**
     * 获取初始化的半径、内半径和字体大小。
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PieGraph, defStyleAttr, 0);
        mRadius = a.getDimensionPixelSize(R.styleable.PieGraph_pieRadius, (int) getDimenValue(TypedValue.COMPLEX_UNIT_DIP, 100));
        mInnerRadius = a.getDimensionPixelSize(R.styleable.PieGraph_pieInnerRadius, (int) getDimenValue(TypedValue.COMPLEX_UNIT_DIP, 50));
        mTextSize = a.getDimensionPixelSize(R.styleable.PieGraph_pieTextSize, (int) getDimenValue(TypedValue.COMPLEX_UNIT_SP, 14));
        mPointRadius = a.getDimensionPixelSize(R.styleable.PieGraph_pointRadius, (int) getDimenValue(TypedValue.COMPLEX_UNIT_DIP, 3));
        mPointMargin = a.getDimensionPixelSize(R.styleable.PieGraph_pointMargin, (int) getDimenValue(TypedValue.COMPLEX_UNIT_DIP, 5));
        mBrokenLineWidth = a.getDimensionPixelSize(R.styleable.PieGraph_brokenLineWidth, (int) getDimenValue(TypedValue.COMPLEX_UNIT_DIP, 10));
        a.recycle();
    }

    /**
     * 初始化画笔。
     */
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(mTextSize);
        mPaint.setDither(true);

        initTextHeightAndBottom();
    }

    /**
     * 初始化文字的height和bottom。
     */
    private void initTextHeightAndBottom() {
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        mTextHeight = -fontMetrics.ascent;
        mTextBottom = fontMetrics.descent;
    }

    /**
     * 初始化内外圆弧的外切圆的矩形。
     */
    private void initRectFsAndPath() {
        mOuterRectF = new RectF();
        mInnerRectF = new RectF();
        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        mMidX = (getMeasuredWidth() + (paddingLeft - paddingRight)) / 2;
        mMidY = (getMeasuredHeight() + (paddingTop - paddingBottom)) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPieParamsList == null || mPieParamsList.isEmpty())
            return;
        mLeftTextTopY = getMeasuredHeight() - getPaddingBottom();
        mRightTextBottomY = 0f + getPaddingTop();
        mLineTextSpace = getDimenValue(TypedValue.COMPLEX_UNIT_DIP, 1.5f);
        checkRadius();
        // 给对应矩形赋值宽高
        mOuterRectF.set(mMidX - mRadius, mMidY - mRadius, mMidX + mRadius, mMidY + mRadius);
        mInnerRectF.set(mMidX - mInnerRadius, mMidY - mInnerRadius, mMidX + mInnerRadius, mMidY + mInnerRadius);
        // 初始化totalValue
        float totalValue = 0f;
        for (PieParams pieParams : mPieParamsList) {
            totalValue += pieParams.getPieSlice().getValue();
        }
        float availableSweep = 360f;
        float currentAngle = 270f;
        float currentSweep = 0f;
        for (PieParams pieParams : mPieParamsList) {
            mPaint.setColor(pieParams.getPieSlice().getColor());
            currentSweep = totalValue == 0 ?
                    (float) (360 / mPieParamsList.size()) :
                    (float) (pieParams.getPieSlice().getValue() * 360 / totalValue);
            if (currentSweep > 0) {
                // 最小角度30
                currentSweep = (currentSweep < 30 ? 30 : currentSweep);
                currentSweep = (currentSweep > availableSweep ? availableSweep : currentSweep);
                availableSweep -= currentSweep;
                // 获取旋转的中心角
                pieParams.setRotateAngel(-currentAngle - currentSweep / 2);
                // 开始绘画
                addArc(canvas, currentAngle, currentSweep);
                addGuid(canvas, pieParams, pieParams.getRotateAngel());
                if (!mHideIcons)
                    addIcon(canvas, pieParams);
                currentAngle = currentAngle + currentSweep;
            }
        }
    }

    /**
     * 防止设置的半径长度大于了最大长度。
     */
    private void checkRadius() {
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        float textHeight = mTextHeight + mTextBottom;
        float maxRadiusX = (getMeasuredWidth() - paddingLeft - paddingRight) / 2;
        float maxRadiusY = (getMeasuredHeight() - paddingTop - paddingBottom) / 2
                - textHeight - mLineTextSpace - mBrokenLineWidth - mPointMargin;
        float maxRadius = maxRadiusX > maxRadiusY ? maxRadiusY : maxRadiusX;
        mRadius = mRadius > maxRadius ? maxRadius : mRadius;
        float maxInnerRadius = mRadius - 20f;
        mInnerRadius = mInnerRadius > maxInnerRadius ? maxInnerRadius : mInnerRadius;
    }

    /**
     * 画饼块。
     *
     * @param canvas
     * @param currentAngle
     * @param currentSweep
     */
    private void addArc(Canvas canvas, float currentAngle, float currentSweep) {
        mPath.reset();
        if (currentSweep % 360 == 0f) { // 画圆环
            mPath.addArc(mOuterRectF, currentAngle, currentSweep);
            mPath.addArc(mInnerRectF, currentAngle + currentSweep, -currentSweep);
        } else { // 画弧环
            mPath.arcTo(mOuterRectF, currentAngle, currentSweep);
            mPath.arcTo(mInnerRectF, currentAngle + currentSweep, -currentSweep);
            mPath.close();
        }
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 画小圆和细线。
     *
     * @param canvas
     * @param pieParams
     * @param rotateAngel
     */
    private void addGuid(Canvas canvas, PieParams pieParams, float rotateAngel) {
        float circleX = (float) (mMidX + (mPointMargin + mRadius) * Math.cos(Math.toRadians(rotateAngel)));
        float circleY = (float) (mMidY - (mPointMargin + mRadius) * Math.sin(Math.toRadians(rotateAngel)));
        float midX = 0f;
        float midY = 0f;
        float endX = 0f;
        if (circleX >= mMidX && circleY <= mMidY) { // 第一象限
            midX = circleX + mBrokenLineWidth;
            midY = circleY - mBrokenLineWidth;
            endX = getMeasuredWidth() - getPaddingRight();
        } else if (circleX <= mMidX && circleY <= mMidY) { // 第二象限
            midX = circleX - mBrokenLineWidth;
            midY = circleY - mBrokenLineWidth;
            endX = getPaddingLeft();
        } else if (circleX <= mMidX && circleY >= mMidY) { // 第三象限
            midX = circleX - mBrokenLineWidth;
            midY = circleY + mBrokenLineWidth;
            endX = getPaddingLeft();
        } else if (circleX >= mMidX && circleY >= mMidY) { // 第四象限
            midX = circleX + mBrokenLineWidth;
            midY = circleY + mBrokenLineWidth;
            endX = getMeasuredWidth() - getPaddingRight();
        }
        String value = getFormatValue(pieParams);
        String describe = pieParams.getPieSlice().getDescribe();
        checkTextLength(value, describe, Math.abs(endX - midX));
        if (circleX <= mMidX ?
                (midY + mTextHeight + mTextBottom + mLineTextSpace) > mLeftTextTopY :
                (midY - mTextHeight - mTextBottom - mLineTextSpace) < mRightTextBottomY) {
            addGuid(canvas, pieParams, --rotateAngel);
        } else {
            canvas.drawCircle(circleX, circleY, mPointRadius, mPaint);
            canvas.drawLine(circleX, circleY, midX, midY, mPaint);
            canvas.drawLine(midX, midY, endX, midY, mPaint);
            // 画数值和描述的文字
            mPaint.setColor(Color.GRAY);
            if (circleX <= mMidX) {
                mLeftTextTopY = midY - mTextHeight - mTextBottom - mLineTextSpace;
                canvas.drawText(value, endX, midY - mTextBottom - mLineTextSpace, mPaint);
                canvas.drawText(describe, endX, midY + mTextHeight + mLineTextSpace, mPaint);
            } else {
                mRightTextBottomY = midY + mTextHeight + mTextBottom + mLineTextSpace;
                float upWidth = mPaint.measureText(value);
                float downWidth = mPaint.measureText(describe);
                canvas.drawText(value, endX - upWidth, midY - mTextBottom - mLineTextSpace, mPaint);
                canvas.drawText(describe, endX - downWidth, midY + mTextHeight + mLineTextSpace, mPaint);
            }
        }
    }

    /**
     * 获取格式化后的数值字符串。
     *
     * @param pieParams
     * @return
     */
    private String getFormatValue(PieParams pieParams) {
        double value = pieParams.getPieSlice().getValue();
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        return nf.format(value);
    }

    /**
     * 校验文本长度，自适应字体大小。
     *
     * @param value
     * @param describe
     * @param maxLength
     */
    private void checkTextLength(String value, String describe, float maxLength) {
        float maxTextLength = getMaxTextLength(value, describe);
        while (maxTextLength > maxLength) {
            mPaint.setTextSize(--mTextSize);
            initTextHeightAndBottom();
            maxTextLength = getMaxTextLength(value, describe);
        }
    }

    /**
     * 获取文本的最大长度。
     *
     * @param value
     * @param describe
     * @return
     */
    private float getMaxTextLength(String value, String describe) {
        float upWidth = mPaint.measureText(value);
        float downWidth = mPaint.measureText(describe);
        return upWidth > downWidth ? upWidth : downWidth;
    }

    /**
     * 画图标。
     *
     * @param canvas
     * @param pieParams
     */
    private void addIcon(Canvas canvas, PieParams pieParams) {
        PointF currentPointF = pieParams.getCurrentPointF();
        if (currentPointF == null) {
            currentPointF = getCenterPointF(pieParams.getRotateAngel(), mInnerRadius);
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = mIconScale;
        Bitmap icon = BitmapFactory.decodeResource(getResources(), pieParams.getPieSlice().getIconRes(), opts);
        canvas.drawBitmap(icon, currentPointF.x - icon.getWidth() / 2, currentPointF.y - icon.getHeight() / 2, mPaint);
    }

    private PointF getCenterPointF(float rotateAngel, float innerRadius) {
        float pointX = (float) (mMidX + (innerRadius + (mRadius - innerRadius) / 2) * Math.cos(Math.toRadians(rotateAngel)));
        float pointY = (float) (mMidY - (innerRadius + (mRadius - innerRadius) / 2) * Math.sin(Math.toRadians(rotateAngel)));
        return new PointF(pointX, pointY);
    }

    /**
     * 获取对应的px值。
     *
     * @param unit
     * @param value
     * @return
     */
    private float getDimenValue(int unit, float value) {
        return TypedValue.applyDimension(unit, value, getContext().getResources().getDisplayMetrics());
    }

    /**
     * 设置内圈半径长度。
     *
     * @param innerRadius
     */
    private void setInnerRadius(float innerRadius) {
        mInnerRadius = innerRadius;
        postInvalidate();
    }

    /**
     * 设置icon的缩放大小。
     *
     * @param iconScale
     */
    private void setIconScale(int iconScale) {
        mIconScale = iconScale;
        postInvalidate();
    }

    /**
     * 设置输入的饼块集合。
     *
     * @param pieSliceList
     */
    public void setPieSliceList(List<PieSlice> pieSliceList) {
        if (mPieParamsList == null)
            mPieParamsList = new ArrayList<>();
        mPieParamsList.clear();
        for (PieSlice pieSlice : pieSliceList) {
            PieParams pieParams = new PieParams();
            pieParams.setPieSlice(pieSlice);
            mPieParamsList.add(pieParams);
        }
        // 把集合按照value从小到大排序，配合最小角度校验
        Collections.sort(mPieParamsList, new Comparator<PieParams>() {

            @Override
            public int compare(PieParams o1, PieParams o2) {
                return (int) (o1.getPieSlice().getValue() - o2.getPieSlice().getValue());
            }
        });
        postInvalidate();
    }

    /**
     * 隐藏图标。
     */
    public void hideIcons() {
        mHideIcons = true;
        postInvalidate();
    }

    /**
     * 执行动画。
     */
    public void startAnimation() {
        if (mAs == null) {
            mAs = new AnimatorSet();
            List<Animator> animatorList = new ArrayList<>();
            ValueAnimator va1 = ValueAnimator.ofFloat(0, mInnerRadius);
            ValueAnimator va2 = ValueAnimator.ofInt(8, mIconScale);
            va1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    setInnerRadius(value);
                }
            });
            animatorList.add(va1);
            va2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    setIconScale(value);
                }
            });
            animatorList.add(va2);
            for (final PieParams pieParams : mPieParamsList) {
                PointF endPointF = getCenterPointF(pieParams.getRotateAngel(), mInnerRadius);
                PointF startPointF = new PointF(mMidX, mMidY);
                PointF middlePointF = new PointF((startPointF.x + endPointF.x) / 2, (startPointF.y + endPointF.y) / 2);
                BezierEvaluator bezierEvaluator = new BezierEvaluator(middlePointF);
                final ValueAnimator va3 = ValueAnimator.ofObject(bezierEvaluator, startPointF, endPointF);
                va3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        PointF value = (PointF) animation.getAnimatedValue();
                        pieParams.setCurrentPointF(value);
                        postInvalidate();
                    }
                });
                animatorList.add(va3);
            }
            mAs.setInterpolator(new DecelerateInterpolator());
            mAs.playTogether(animatorList);
            mAs.setDuration(500);
        } else {
            mAs.cancel();
        }
        mAs.start();
    }
}
