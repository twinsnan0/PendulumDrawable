package com.twinsnan.pendulumdrawabke;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.animation.LinearInterpolator;

/**
 * Created by hucn on 2016/11/21.
 * Description:
 */

public class MyDrawable extends Drawable implements Animatable {

    private static final float DEFAULT_WIDTH = 100.0f;

    private static final float DEFAULT_HEIGHT = 100.0f;

    private static final float DEFAULT_LINE_WIDTH = 4.0f;

    private static final float DEFAULT_BALL_RADIUS = 20.0f;

    private static final float DEFAULT_POINT_RADIUS = 6.0f;

    private static final float DEFAULT_CIRCLE_RADIUS = 200.0f;

    private static final float DEFAULT_MAX_ANGLE = (float) (2 * Math.PI / 24.0f);

    private static final float DEFAULT_SHADOW_OFFSET = 6.0f;

    private static final int DEFAULT_DURATION = 2000;

    private static final int DEFAULT_COLOR = Color.RED;

    private static final int[] DEFAULT_BACKGROUD_COLOR = new int[]{Color.BLUE, Color.GREEN};

    private static final int DEFAULT_SHADOW_COLOR = 0X55000000;

    private float mWidth;

    private float mHeight;

    private float mMaxAngle;

    private float mLineWidth;

    private float mBallRadius;

    private float mCircleRadius;

    private float mFixedPointRadius;

    private float mLineLength;

    private float mCurrentX;

    private float mCurrentY;

    private int mDuration;

    private Paint mPaint;

    private ValueAnimator mAnimator;

    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener;

    private Bitmap mBitmap;

    private float mCurrentValue;

    public MyDrawable(Context context) {
        // 设置长宽
        mWidth = dp2px(context, DEFAULT_WIDTH);
        mHeight = dp2px(context, DEFAULT_HEIGHT);
        // 线的粗细
        mLineWidth = dp2px(context, DEFAULT_LINE_WIDTH);
        // 球的半径
        mBallRadius = dp2px(context, DEFAULT_BALL_RADIUS);
        // 固定点的半径
        mFixedPointRadius = dp2px(context, DEFAULT_POINT_RADIUS);
        // 背景的半径
        mCircleRadius = dp2px(context, DEFAULT_CIRCLE_RADIUS);
        // 线的长度
        mLineLength = dp2px(context, DEFAULT_WIDTH * 0.75f);
        // 设置最大角度
        mMaxAngle = DEFAULT_MAX_ANGLE;
        // 设置周期
        mDuration = DEFAULT_DURATION;
        // 设置属性动画参数
        mAnimator = new ValueAnimator();
        mAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setDuration(DEFAULT_DURATION);
        mAnimator.setDuration(mDuration);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setInterpolator(new LinearInterpolator());
        // 设置画笔参数
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        // 获取图像
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_loop_white_24dp);
        // 设置动画的回调
        mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                calculate((float) animation.getAnimatedValue());
                invalidateSelf();
            }
        };
    }

    @Override
    public void start() {
        mAnimator.addUpdateListener(mAnimatorUpdateListener);
        mAnimator.start();
    }

    @Override
    public void stop() {
        mAnimator.removeUpdateListener(mAnimatorUpdateListener);
        mAnimator.end();
    }

    @Override
    public boolean isRunning() {
        return mAnimator.isRunning();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        // 绘制背景变换
        float centerX = mWidth / 2.0f;
        float centerY = 0;
       /* if (mCurrentValue <= 0.5f) {
            mPaint.setColor(DEFAULT_BACKGROUD_COLOR[0]);
            canvas.drawCircle(mCurrentX, mCurrentY, mCircleRadius * mCurrentValue, mPaint);
        } else {
            mPaint.setColor(DEFAULT_BACKGROUD_COLOR[1]);
            canvas.drawCircle(mCurrentX, mCurrentY, mCircleRadius * (mCurrentValue - 0.5f), mPaint);
        }*/
        int saveCount = canvas.save();
        // 画线
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setColor(DEFAULT_SHADOW_COLOR);
        canvas.drawLine(mWidth / 2.0f + DEFAULT_SHADOW_OFFSET, 0, mCurrentX + DEFAULT_SHADOW_OFFSET, mCurrentY, mPaint);
        mPaint.setColor(DEFAULT_COLOR);
        canvas.drawLine(mWidth / 2.0f, 0, mCurrentX, mCurrentY, mPaint);
        // 画固定点
        mPaint.setStrokeWidth(mFixedPointRadius * 2);
        mPaint.setColor(DEFAULT_SHADOW_COLOR);
        RectF pointShadowRectF = new RectF(centerX - mFixedPointRadius + DEFAULT_SHADOW_OFFSET, centerY - mFixedPointRadius,
                centerX + mFixedPointRadius + DEFAULT_SHADOW_OFFSET, centerY + mFixedPointRadius);
        canvas.drawArc(pointShadowRectF, 0, 360, true, mPaint);
        mPaint.setColor(DEFAULT_COLOR);
        RectF pointRectF = new RectF(centerX - mFixedPointRadius, centerY - mFixedPointRadius,
                centerX + mFixedPointRadius, centerY + mFixedPointRadius);
        canvas.drawArc(pointRectF, 0, 360, true, mPaint);
        // 画小球
        mPaint.setStrokeWidth(mBallRadius * 2);
        mPaint.setColor(DEFAULT_SHADOW_COLOR);
        RectF ballShadowRectF = new RectF(mCurrentX - mBallRadius + DEFAULT_SHADOW_OFFSET, mCurrentY - mBallRadius,
                mCurrentX + mBallRadius + DEFAULT_SHADOW_OFFSET, mCurrentY + mBallRadius);
        canvas.drawArc(ballShadowRectF, 0, 360, true, mPaint);
        mPaint.setColor(DEFAULT_COLOR);
        RectF ballRectF = new RectF(mCurrentX - mBallRadius, mCurrentY - mBallRadius,
                mCurrentX + mBallRadius, mCurrentY + mBallRadius);
        canvas.drawArc(ballRectF, 0, 360, true, mPaint);
        // 绘制图像
        Rect BitmapShowRectF = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        canvas.drawBitmap(mBitmap, BitmapShowRectF, ballRectF, mPaint);
        canvas.restoreToCount(saveCount);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    // 获取默认的高度
    @Override
    public int getIntrinsicHeight() {
        return (int) mHeight;
    }

    // 获取默认的宽度
    @Override
    public int getIntrinsicWidth() {
        return (int) mWidth;
    }

    private float dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale;
    }

    private void calculate(float animatedValue) {
        float currentAngle = (float) (-Math.cos(animatedValue * Math.PI * 2) * mMaxAngle);
        mCurrentX = (float) (mWidth / 2.0f + mLineLength * Math.sin(currentAngle));
        mCurrentY = (float) (mLineLength * Math.cos(currentAngle));
        mCurrentValue = animatedValue;
    }
}
