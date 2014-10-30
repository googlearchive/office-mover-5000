package com.firebase.officemover;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.firebase.officemover.model.OfficeLayout;
import com.firebase.officemover.model.OfficeThing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OfficeCanvasView extends View {

    private static final String TAG = OfficeCanvasView.class.getSimpleName();

    private final Paint DEFAULT_PAINT = new Paint();

    private static class OfficeThingComprator implements Comparator<OfficeThing> {

        @Override
        public int compare(OfficeThing lhs, OfficeThing rhs) {
            return lhs.getzIndex() - rhs.getzIndex();
        }
    }

    /**
     * All available circles
     */
    private OfficeLayout mOfficeLayout = new OfficeLayout();
    private SparseArray<OfficeThing> mOfficeThingPointer = new SparseArray<OfficeThing>();

    public List<OfficeThing> getOfficeLayout() {
        return mOfficeLayout;
    }

    public void setOfficeLayout(OfficeLayout officeLayout) {
        this.mOfficeLayout = officeLayout;
    }

    public void addNewThing(String type) {
        if (null == type) throw new IllegalArgumentException();

        OfficeThing newThing = new OfficeThing();
        newThing.setType(type);
        newThing.setzIndex(mOfficeLayout.getHighestzIndex() + 1);
        newThing.setRotation(0);
        newThing.setLeft(this.getWidth() / 2);
        newThing.setTop(this.getHeight() / 2);


        Log.w(TAG, "Added thing " + newThing);
        mOfficeLayout.add(newThing);
        Collections.sort(mOfficeLayout, new OfficeThingComprator());

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
        for (OfficeThing thing : mOfficeLayout) {
            Bitmap thingBitmap = thing.getBitmap(getContext());
            canv.drawBitmap(thingBitmap, thing.getLeft(), thing.getTop(), DEFAULT_PAINT);
            Paint red = new Paint();
            red.setColor(0xFFFF0000);
            canv.drawCircle(100, 100, 100, red);
        }
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        boolean handled = false;

        OfficeThing touchedThing;
        int xTouch;
        int yTouch;
        int pointerId;
        int actionIndex = event.getActionIndex();

        // get touch event coordinates and make transparent circle from it
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // it's the first pointer, so clear all existing pointers data
                mOfficeThingPointer.clear();

                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);

                // check if we've touched inside something
                touchedThing = getTouchedThing(xTouch, yTouch);
                if(touchedThing == null) {
                    break;
                }
                touchedThing.setX(xTouch, getContext());
                touchedThing.setY(yTouch, getContext());
                mOfficeThingPointer.put(event.getPointerId(0), touchedThing);

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
                touchedThing = getTouchedThing(xTouch, yTouch);
                if(touchedThing == null) {
                    break;
                }

                mOfficeThingPointer.put(pointerId, touchedThing);
                touchedThing.setX(xTouch, getContext());
                touchedThing.setY(yTouch, getContext());
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_MOVE:
                final int pointerCount = event.getPointerCount();

                for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
                    // Some pointer has moved, search it by pointer id
                    pointerId = event.getPointerId(actionIndex);

                    xTouch = (int) event.getX(actionIndex);
                    yTouch = (int) event.getY(actionIndex);

                    touchedThing = mOfficeThingPointer.get(pointerId);

                    if (null != touchedThing) {
                        touchedThing.setX(xTouch, getContext());
                        touchedThing.setY(yTouch, getContext());
                    }
                }
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_UP:
                Log.v(TAG, "Last drop at " + (mOfficeThingPointer.get(0).getLeft()) + " by " + (mOfficeThingPointer.get(0).getTop()));

                mOfficeThingPointer.clear();
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                pointerId = event.getPointerId(actionIndex);

                Log.v(TAG, "Dropped at " + mOfficeThingPointer.get(pointerId).getLeft() + " by " + mOfficeThingPointer.get(pointerId).getTop());

                mOfficeThingPointer.remove(pointerId);
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

    private OfficeThing getTouchedThing(final int xTouch, final int yTouch) {
        OfficeThing touched = null;

        //TODO: move this into the office layout
        List<OfficeThing> backwardsLayout = new ArrayList<OfficeThing>(mOfficeLayout);
        Collections.sort(backwardsLayout, new Comparator<OfficeThing>() {
            @Override
            public int compare(OfficeThing lhs, OfficeThing rhs) {
                return rhs.getzIndex() - lhs.getzIndex();
            }
        });

        for (OfficeThing thing : backwardsLayout) {
            int top = thing.getTop();
            int left = thing.getLeft();
            int bottom = thing.getTop() + thing.getHeight(getContext());
            int right = thing.getLeft() + thing.getWidth(getContext());


            if(yTouch <= bottom && yTouch >= top && xTouch >= left && xTouch <= right) {
                touched = thing;
                break;
            }
        }
        return touched;
    }

    //TODO: Dynamically fill device with fixed aspect ratio
//    // Fixes aspect ratio
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int receivedWidth = MeasureSpec.getSize(widthMeasureSpec);
//
//        super.onMeasure(receivedWidth, (int)(receivedWidth / ASPECT_RATIO));
//    }
}
