package com.view.drop;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.os.Build.VERSION;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

public class DropCover extends SurfaceView implements SurfaceHolder.Callback {

    private int mMaxDistance = 200;

    private float mBaseX;
    private float mBaseY;

    private float mTargetX;
    private float mTargetY;

    private Bitmap mDest;
    private Paint mPaint = new Paint();

    private float targetWidth;
    private float targetHeight;
    private float mRadius = 0;
    private float mStrokeWidth = 20;
    private boolean isDraw = true;
    private float mStatusBarHeight = 0;
    private OnDragCompeteListener mOnDragCompeteListener;

    public interface OnDragCompeteListener {
        void onDrag();
    }

    @SuppressLint("NewApi")
    public DropCover(Context context) {
        super(context);
        mStatusBarHeight=getStatusBarHeight(context);
        setBackgroundColor(Color.TRANSPARENT);
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        getHolder().addCallback(this);
        setFocusable(false);
        setClickable(false);
        mPaint.setAntiAlias(true);
        if (VERSION.SDK_INT > 11) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    /**
     * draw drop and line
     */
    private void drawDrop() {
        Canvas canvas = getHolder().lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);

            if (isDraw) {
                double distance = Math.sqrt(Math.pow(mBaseX - mTargetX, 2) + Math.pow(mBaseY - mTargetY, 2));
                mPaint.setColor(0xffff0000);
                if (distance < mMaxDistance) {
                    mStrokeWidth = (float) ((1f - distance / mMaxDistance) * mRadius);
                    mPaint.setStrokeWidth(mStrokeWidth);
                    canvas.drawCircle(mBaseX, mBaseY, mStrokeWidth / 2, mPaint);
                    // canvas.drawLine(mBaseX, mBaseY, mTargetX + targetWidth /
                    // 2, mTargetY + targetHeight / 2, mPaint);
                    drawBezier(canvas);
                }
                canvas.drawBitmap(mDest, mTargetX, mTargetY, mPaint);
            }
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void drawBezier(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);

        Point[] points = calculate(new Point(mBaseX, mBaseY), new Point(mTargetX + mDest.getWidth() / 2f, mTargetY + mDest.getHeight() / 2f));

        float centerX = (points[0].x + points[1].x + points[2].x + points[3].x) / 4f;
        float centerY = (points[0].y + points[1].y + points[2].y + points[3].y) / 4f;

        Path path1 = new Path();
        path1.moveTo(points[0].x, points[0].y);
        path1.quadTo((points[2].x + points[3].x) / 2, (points[2].y + points[3].y) / 2, points[1].x, points[1].y);
        canvas.drawPath(path1, mPaint);

        Path path2 = new Path();
        path2.moveTo(points[2].x, points[2].y);
        path2.quadTo((points[0].x + points[1].x) / 2, (points[0].y + points[1].y) / 2, points[3].x, points[3].y);
        canvas.drawPath(path2, mPaint);
    }

    /**
     * ax=by=0 x^2+y^2=s/2
     * 
     * ==>
     * 
     * x=a^2/(a^2+b^2)*s/2
     * 
     * @param start
     * @param end
     * @return
     */
    private Point[] calculate(Point start, Point end) {
        float a = end.x - start.x;
        float b = end.y - start.y;

        float x = (float) Math.sqrt(a * a / (a * a + b * b) * (mStrokeWidth / 2f) * (mStrokeWidth / 2f));
        float y = -b / a * x;

        System.out.println("x:" + x + " y:" + y);

        Point[] result = new Point[4];

        result[0] = new Point(start.x + x, start.y + y);
        result[1] = new Point(end.x + x, end.y + y);

        result[2] = new Point(start.x - x, start.y - y);
        result[3] = new Point(end.x - x, end.y - y);

        return result;
    }

    public void setTarget(Bitmap dest) {
        mDest = dest;
        targetWidth = dest.getWidth();
        targetHeight = dest.getHeight();

        mRadius = dest.getWidth() / 2;
        mStrokeWidth = mRadius;
    }

    public void init(float x, float y) {
        mBaseX = x + mDest.getWidth() / 2f;
        mBaseY = y + mDest.getWidth() / 2f- mStatusBarHeight;
        mTargetX = x;
        mTargetY = y- mStatusBarHeight ;

        isDraw = true;
        drawDrop();
    }

    /**
     * move the drop
     * 
     * @param x
     * @param y
     */
    public void update(float x, float y) {

        mTargetX = x;
        mTargetY = y - mStatusBarHeight;
        drawDrop();
    }

    /**
     * reset datas
     */
    public void clearDatas() {
        mBaseX = -1;
        mBaseY = -1;
        mTargetX = -1;
        mTargetY = -1;
        mDest = null;
    }

    /**
     * remove DropCover
     */
    public void clearViews() {
        if (getParent() != null) {
            CoverManager.getInstance().getWindowManager().removeView(this);
        }
    }

    /**
     * finish drag event and start explosion
     * 
     * @param target
     * @param x
     * @param y
     */
    public void finish(View target, float x, float y) {
        double distance = Math.sqrt(Math.pow(mBaseX - mTargetX, 2) + Math.pow(mBaseY - mTargetY, 2));
        
        
        Canvas canvas = getHolder().lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
            getHolder().unlockCanvasAndPost(canvas);
        }
        if (distance > mMaxDistance) {
            if (mOnDragCompeteListener != null)
                mOnDragCompeteListener.onDrag(); 
        } else {
            clearViews();
            target.setVisibility(View.VISIBLE);
            target.startAnimation(shakeAnimation(distance));
        }
        clearDatas();
        isDraw = false;
    }
    //CycleTimes动画重复的次数
    public Animation shakeAnimation(double distance) {
    	double x,y;
    	x=0.3*(mTargetX-mBaseX)*distance/mMaxDistance;
    	y=0.3*(mTargetY-mBaseY)*distance/mMaxDistance;
        Animation translateAnimation = new TranslateAnimation(0, (int)x, 0, (int)y);
        translateAnimation.setInterpolator(new CycleInterpolator(1));
        translateAnimation.setDuration(200);
        return translateAnimation;
    }
    public void setStatusBarHeight(int statusBarHeight) {
        mStatusBarHeight = statusBarHeight;
    }

    public void setOnDragCompeteListener(OnDragCompeteListener onDragCompeteListener) {
        mOnDragCompeteListener = onDragCompeteListener;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawDrop();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
     
    }
    public void setMaxDragDistance(int maxDistance) {
        mMaxDistance = maxDistance;
    }
    public static int getStatusBarHeight(Context context){

        Class<?> c = null;

        Object obj = null;

        Field field = null;

        int x = 0, statusBarHeight = 0;

        try {

            c = Class.forName("com.android.internal.R$dimen");

            obj = c.newInstance();

            field = c.getField("status_bar_height");

            x = Integer.parseInt(field.get(obj).toString());

            statusBarHeight = context.getResources().getDimensionPixelSize(x);

        } catch (Exception e1) {

            e1.printStackTrace();

        }

        return statusBarHeight;

    }
    class Point {
        float x, y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
