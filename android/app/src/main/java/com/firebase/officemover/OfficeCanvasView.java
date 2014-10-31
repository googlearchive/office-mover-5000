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

public class OfficeCanvasView extends View {

    private static final String TAG = OfficeCanvasView.class.getSimpleName();

    private static final Paint DEFAULT_PAINT = new Paint();

    /**
     * All available things
     */
    private final SparseArray<OfficeThing> mOfficeThingPointer = new SparseArray<OfficeThing>();
    private String mSelectedThingKey;
    private OfficeLayout mOfficeLayout;
    private OfficeMoverActivity.ThingChangeListener mThingChangedListener;


    public OfficeCanvasView(final Context ct) {
        super(ct);
    }

    public OfficeCanvasView(final Context ct, final AttributeSet attrs) {
        super(ct, attrs);
    }

    public OfficeCanvasView(final Context ct, final AttributeSet attrs, final int defStyle) {
        super(ct, attrs, defStyle);
    }

    @Override
    public void onDraw(final Canvas canv) {
        if(null == mOfficeLayout) {
            Log.w(TAG, "Tried to render empty office");
            return;
        }

        // TODO: draw from bottom z-index to top z-index (use queries?)
        for (OfficeThing thing : mOfficeLayout.values()) {
            Bitmap thingBitmap = thing.getBitmap(getContext());

            // If it's the selected thing, make it GLOW!
            if(thing.getKey().equals(mSelectedThingKey)) {
                thingBitmap = thing.getGlowingBitmap(getContext());
            }

            canv.drawBitmap(thingBitmap, modelToScreen(thing.getLeft()), modelToScreen(thing.getTop()), DEFAULT_PAINT);
        }
    }

    //TODO: This needs some serious refactoring
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        boolean handled = false;

        OfficeThing touchedThing;
        int xTouch;
        int yTouch;
        int pointerId;
        int actionIndex = event.getActionIndex();

        // primitive throttling
        // TODO: Make this less stupid. Use a timer for updates to firebase model every 40ms
        synchronized (mOfficeThingPointer) {
            try {
                mOfficeThingPointer.wait(10L);
            } catch (InterruptedException e) {
                //noop
            }
        }

        // get touch event coordinates and make transparent wrapper from it
        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                // first pointer, clear all existing pointers data
                mOfficeThingPointer.clear();

                xTouch = screenToModel((int) event.getX(0));
                yTouch = screenToModel((int) event.getY(0));

                // check if we've touched inside something
                touchedThing = getTouchedThing(xTouch, yTouch);
                if (touchedThing == null) {
                    Log.v(TAG, "Deselected " + mSelectedThingKey);
                    mSelectedThingKey = null;
                    break;
                }

                //TODO: decouple this from the local model
                touchedThing.setX(xTouch, getContext());
                touchedThing.setY(yTouch, getContext());

                if(null != this.mThingChangedListener) {
                    mThingChangedListener.thingChanged(touchedThing.getKey(), touchedThing);
                }

                mOfficeThingPointer.put(event.getPointerId(0), touchedThing);
                mSelectedThingKey = touchedThing.getKey();
                Log.v(TAG, "Selected " + touchedThing);
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                Log.w(TAG, "Pointer down");
                // It secondary pointers, so obtain their ids and check circles
                pointerId = event.getPointerId(actionIndex);

                xTouch = screenToModel((int) event.getX(actionIndex));
                yTouch = screenToModel((int) event.getY(actionIndex));

                // check if we've touched inside something
                touchedThing = getTouchedThing(xTouch, yTouch);
                if (touchedThing == null) {
                    break;
                }

                mOfficeThingPointer.put(pointerId, touchedThing);
                touchedThing.setX(xTouch, getContext());
                touchedThing.setY(yTouch, getContext());

                if(null != this.mThingChangedListener) {
                    mThingChangedListener.thingChanged(touchedThing.getKey(), touchedThing);
                }

                handled = true;
                break;

            case MotionEvent.ACTION_MOVE:
                //TODO: keep thing from getting dragged outside the board
                final int pointerCount = event.getPointerCount();

                for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
                    // Some pointer has moved, search it by pointer id
                    pointerId = event.getPointerId(actionIndex);

                    xTouch = screenToModel((int) event.getX(actionIndex));
                    yTouch = screenToModel((int) event.getY(actionIndex));

                    touchedThing = mOfficeThingPointer.get(pointerId);

                    if (null != touchedThing) {
                        int newTop = yTouch - touchedThing.getHeight(getContext()) / 2;
                        int newLeft = xTouch - touchedThing.getWidth(getContext()) / 2;
                        int newBottom = yTouch + touchedThing.getHeight(getContext()) / 2;
                        int newRight = xTouch + touchedThing.getWidth(getContext()) / 2;

                        if(newTop >= 0 && newLeft >= 0 && newBottom <= 800 && newRight <= 600) {
                            touchedThing.setX(xTouch, getContext());
                            touchedThing.setY(yTouch, getContext());

                            if(null != this.mThingChangedListener) {
                                mThingChangedListener.thingChanged(touchedThing.getKey(), touchedThing);
                            }
                        }
                    }
                }
                handled = true;
                break;

            case MotionEvent.ACTION_UP:
                mOfficeThingPointer.clear();
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                pointerId = event.getPointerId(actionIndex);
                mOfficeThingPointer.remove(pointerId);
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_CANCEL:
                handled = true;
                break;
        }

        return super.onTouchEvent(event) || handled;
    }

    // Accepts model coords
    private OfficeThing getTouchedThing(int xTouchModel, int yTouchModel) {

        OfficeThing touched = null;

        //TODO: get the item with highest zindex, instead of a random one
        for (OfficeThing thing : mOfficeLayout.values()) {
            int top = thing.getTop();
            int left = thing.getLeft();
            int bottom = thing.getTop() + thing.getHeight(getContext());
            int right = thing.getLeft() + thing.getWidth(getContext());

            if (yTouchModel <= bottom && yTouchModel >= top && xTouchModel >= left && xTouchModel <= right) {
                touched = thing;
                break;
            }
        }
        return touched;
    }

    //Coordinate conversion
    //TODO: called from the activity too, consider moving elsewhere
    public int modelToScreen(int coordinate) {
        return (int) ((double) coordinate * (double) this.getHeight() / 800D);
    }

    public int screenToModel(int coordinate) {
        return (int) ((double) coordinate * 800D / (double) this.getHeight());
    }

    public OfficeLayout getOfficeLayout() {
        return mOfficeLayout;
    }

    public void setOfficeLayout(OfficeLayout officeLayout) {
        this.mOfficeLayout = officeLayout;
    }

    public void setThingChangedListener(OfficeMoverActivity.ThingChangeListener thingChangeListener) {
        this.mThingChangedListener = thingChangeListener;
    }
}
