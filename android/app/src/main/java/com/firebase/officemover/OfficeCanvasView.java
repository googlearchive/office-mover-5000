package com.firebase.officemover;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class OfficeCanvasView extends View {

    private static final String TAG = OfficeCanvasView.class.getSimpleName();

    /**
     * Main bitmap
     */
    private Bitmap mBitmap = null;

    private Rect mMeasuredRect;

    /**
     * Stores data about single circle
     */
    private static class CircleArea {
        int radius;
        int centerX;
        int centerY;
        int zIndex;
        Paint paint;

        CircleArea(int centerX, int centerY, int radius, int zIndex, Paint paint) {
            this.radius = radius;
            this.centerX = centerX;
            this.centerY = centerY;
            this.zIndex = zIndex;
            this.paint = paint;
        }

        @Override
        public String toString() {
            return "Circle[" + centerX + ", " + centerY + ", " + radius + "]";
        }
    }

    private static class CircleAreaComprator implements Comparator<CircleArea> {

        @Override
        public int compare(CircleArea lhs, CircleArea rhs) {
            return lhs.zIndex - rhs.zIndex;
        }
    }

    private final Random mRando = new Random();
    // Radius limit in pixels
    private final static int RADIUS_LIMIT = 150;

    /**
     * All available circles
     */
    private List<CircleArea> mOfficeLayout = new ArrayList<CircleArea>();
    private SparseArray<CircleArea> mCirclePointer = new SparseArray<CircleArea>();

    public List<CircleArea> getStuffLayout() {
        return mOfficeLayout;
    }

    public void setStuffLayout(List<CircleArea> mStuffLayout) {
        this.mOfficeLayout = mStuffLayout;
    }

    public void clearLayout() {
        mOfficeLayout.clear();
        invalidate();
    }

    public void addNewThing() {
        // Random color
        Paint circlePaint = new Paint();

        circlePaint.setColor(0xFF000000 + mRando.nextInt(0xFFFFFF));
        circlePaint.setStrokeWidth(40);
        circlePaint.setStyle(Paint.Style.FILL);

        int zIndex = 0;
        if (mOfficeLayout.size() > 0) {
            zIndex = mOfficeLayout.get(mOfficeLayout.size() - 1).zIndex + 1;
        }

        CircleArea newCircle = new CircleArea(this.getWidth() / 2, this.getHeight() / 2, mRando.nextInt(RADIUS_LIMIT) + RADIUS_LIMIT, zIndex, circlePaint);

        Log.w(TAG, "Added circle " + newCircle);
        mOfficeLayout.add(newCircle);
        Collections.sort(mOfficeLayout, new CircleAreaComprator());

        invalidate();
    }

    /**
     * Default constructor
     *
     * @param ct {@link android.content.Context}
     */
    public OfficeCanvasView(final Context ct) {
        super(ct);

        init(ct);
    }

    public OfficeCanvasView(final Context ct, final AttributeSet attrs) {
        super(ct, attrs);

        init(ct);
    }

    public OfficeCanvasView(final Context ct, final AttributeSet attrs, final int defStyle) {
        super(ct, attrs, defStyle);

        init(ct);
    }

    private void init(final Context ct) {
    }

    @Override
    public void onDraw(final Canvas canv) {
        for (CircleArea circle : mOfficeLayout) {
            canv.drawCircle(circle.centerX, circle.centerY, circle.radius, circle.paint);
        }
    }


    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        boolean handled = false;

        CircleArea touchedCircle;
        int xTouch;
        int yTouch;
        int pointerId;
        int actionIndex = event.getActionIndex();

        // get touch event coordinates and make transparent circle from it
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // it's the first pointer, so clear all existing pointers data
                clearCirclePointer();

                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);

                // check if we've touched inside some circle
                touchedCircle = obtainTouchedCircle(xTouch, yTouch);
                if(touchedCircle == null) {
                    break;
                }
                touchedCircle.centerX = xTouch;
                touchedCircle.centerY = yTouch;
                mCirclePointer.put(event.getPointerId(0), touchedCircle);

                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                Log.w(TAG, "Pointer down");
                // It secondary pointers, so obtain their ids and check circles
                pointerId = event.getPointerId(actionIndex);

                xTouch = (int) event.getX(actionIndex);
                yTouch = (int) event.getY(actionIndex);

                // check if we've touched inside some circle
                touchedCircle = obtainTouchedCircle(xTouch, yTouch);
                if(touchedCircle == null) {
                    break;
                }

                mCirclePointer.put(pointerId, touchedCircle);
                touchedCircle.centerX = xTouch;
                touchedCircle.centerY = yTouch;
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_MOVE:
                final int pointerCount = event.getPointerCount();

                Log.w(TAG, "Move");

                for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
                    // Some pointer has moved, search it by pointer id
                    pointerId = event.getPointerId(actionIndex);

                    xTouch = (int) event.getX(actionIndex);
                    yTouch = (int) event.getY(actionIndex);

                    touchedCircle = mCirclePointer.get(pointerId);

                    if (null != touchedCircle) {
                        touchedCircle.centerX = xTouch;
                        touchedCircle.centerY = yTouch;
                    }
                }
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_UP:
                clearCirclePointer();
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // not general pointer was up
                pointerId = event.getPointerId(actionIndex);

                mCirclePointer.remove(pointerId);
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_CANCEL:
                handled = true;
                break;

            default:
                // do nothing
                break;
        }

        return super.onTouchEvent(event) || handled;
    }

    /**
     * Clears all CircleArea - pointer id relations
     */
    private void clearCirclePointer() {
        Log.w(TAG, "clearCirclePointer");

        mCirclePointer.clear();
    }

    /**
     * Search and creates new (if needed) circle based on touch area
     *
     * @param xTouch int x of touch
     * @param yTouch int y of touch
     * @return obtained {@link CircleArea}
     */
    private CircleArea obtainTouchedCircle(final int xTouch, final int yTouch) {
        CircleArea touchedCircle = getTouchedCircle(xTouch, yTouch);

        if (null != touchedCircle) {
            touchedCircle.zIndex = mOfficeLayout.get(mOfficeLayout.size() - 1).zIndex + 1;
            Collections.sort(mOfficeLayout, new CircleAreaComprator());
        }

        return touchedCircle;
    }

    /**
     * Determines touched circle
     *
     * @param xTouch int x touch coordinate
     * @param yTouch int y touch coordinate
     * @return {@link CircleArea} touched circle or null if no circle has been touched
     */
    private CircleArea getTouchedCircle(final int xTouch, final int yTouch) {
        CircleArea touched = null;

        List<CircleArea> backwardsLayout = new ArrayList<CircleArea>(mOfficeLayout);
        Collections.sort(backwardsLayout, new Comparator<CircleArea>() {
            @Override
            public int compare(CircleArea lhs, CircleArea rhs) {
                return rhs.zIndex - lhs.zIndex;
            }
        });

        for (CircleArea circle : backwardsLayout) {
            if ((circle.centerX - xTouch) * (circle.centerX - xTouch) + (circle.centerY - yTouch) * (circle.centerY - yTouch) <= circle.radius * circle.radius) {
                touched = circle;
                break;
            }
        }
        return touched;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mMeasuredRect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }
}
