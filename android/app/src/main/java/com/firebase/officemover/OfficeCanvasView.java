package com.firebase.officemover;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.firebase.officemover.model.OfficeLayout;
import com.firebase.officemover.model.OfficeThing;

/**
 * @author Jenny Tong (mimming)
 */
public class OfficeCanvasView extends View {

    private static final String TAG = OfficeCanvasView.class.getSimpleName();

    private static final Paint DEFAULT_PAINT = new Paint();
    private static final Paint DESK_LABEL_PAINT = new Paint();

    /**
     * All available things
     */
    private final SparseArray<OfficeThing> mOfficeThingPointer = new SparseArray<OfficeThing>();
    private String mSelectedThingKey;
    private OfficeLayout mOfficeLayout;
    private OfficeMoverActivity.ThingChangeListener mThingChangedListener;
    private OfficeMoverActivity.ThingFocusChangeListener mThingFocusChangeListener;


    public OfficeCanvasView(final Context ct) {
        super(ct);
        init();
    }

    public OfficeCanvasView(final Context ct, final AttributeSet attrs) {
        super(ct, attrs);
        init();
    }

    public OfficeCanvasView(final Context ct, final AttributeSet attrs, final int defStyle) {
        super(ct, attrs, defStyle);
        init();
    }

    private void init() {
        Log.v(TAG, "init new canvas");
        DESK_LABEL_PAINT.setColor(Color.WHITE);
        DESK_LABEL_PAINT.setTextSize(50);
        DESK_LABEL_PAINT.setTextAlign(Paint.Align.CENTER);
        DESK_LABEL_PAINT.setTypeface(Typeface.DEFAULT);
    }

    @Override
    public void onDraw(final Canvas canv) {
        if (null == mOfficeLayout) {
            Log.w(TAG, "Tried to render empty office");
            return;
        }

        for (OfficeThing thing : mOfficeLayout.getThingsBottomUp()) {
            Bitmap thingBitmap = thing.getBitmap(getContext());

            // If it's the selected thing, make it GLOW!
            if (thing.getKey().equals(mSelectedThingKey)) {
                thingBitmap = thing.getGlowingBitmap(getContext());
            }

            canv.drawBitmap(thingBitmap, modelToScreen(thing.getLeft()), modelToScreen(thing.getTop()), DEFAULT_PAINT);

            if (thing.getType().equals("desk") && thing.getName() != null) {
                // TODO: these offset numbers are empirically determined. Calculate them instead
                float centerX = modelToScreen(thing.getLeft()) + 102;
                float centerY = modelToScreen(thing.getTop()) + 70;

                canv.save();
                // TODO: OMG this is so hacky. Fix it. These numbers were also empirically determined
                if (thing.getRotation() == 180) {
                    canv.rotate(-thing.getRotation(), centerX, centerY - 10);
                } else if (thing.getRotation() == 90) {
                    canv.rotate(-thing.getRotation(), centerX, centerY + 45);
                } else if (thing.getRotation() == 270) {
                    canv.rotate(-thing.getRotation(), centerX - 40, centerY);
                }

                canv.drawText(thing.getName(), centerX, centerY, DESK_LABEL_PAINT);
                canv.restore();
            }
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

        int newTop;
        int newLeft;
        int newBottom;
        int newRight;


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
                    mThingFocusChangeListener.thingChanged(null);
                    break;
                }

                //TODO: These numbers were empirically determined for the Nexus 7. Replace with proper math
                newTop = yTouch - touchedThing.getHeight(getContext()) / 2;
                newLeft = xTouch - touchedThing.getWidth(getContext()) / 2;
                newBottom = yTouch + (int) (touchedThing.getHeight(getContext()) / 3.7D);
                newRight = xTouch + (int) (touchedThing.getWidth(getContext()) / 4.15D);

                mOfficeThingPointer.put(event.getPointerId(0), touchedThing);

                if (newTop >= 0 && newLeft >= 0 && newBottom <= 800 && newRight <= 600) {
                    //TODO: decouple this from the local model
                    touchedThing.setX(xTouch, getContext());
                    touchedThing.setY(yTouch, getContext());

                    if (null != this.mThingChangedListener) {
                        mThingChangedListener.thingChanged(touchedThing.getKey(), touchedThing);
                    }
                }


                mSelectedThingKey = touchedThing.getKey();
                mThingFocusChangeListener.thingChanged(touchedThing);
                touchedThing.setzIndex(mOfficeLayout.getHighestzIndex() + 1);

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

                //TODO: These numbers were empirically determined for the Nexus 7. Replace with proper math
                newTop = yTouch - touchedThing.getHeight(getContext()) / 2;
                newLeft = xTouch - touchedThing.getWidth(getContext()) / 2;
                newBottom = yTouch + (int) (touchedThing.getHeight(getContext()) / 3.7D);
                newRight = xTouch + (int) (touchedThing.getWidth(getContext()) / 4.15D);

                mOfficeThingPointer.put(pointerId, touchedThing);

                if (newTop >= 0 && newLeft >= 0 && newBottom <= 800 && newRight <= 600) {
                    touchedThing.setX(xTouch, getContext());
                    touchedThing.setY(yTouch, getContext());

                    if (null != this.mThingChangedListener) {
                        mThingChangedListener.thingChanged(touchedThing.getKey(), touchedThing);
                    }
                }

                handled = true;
                break;

            case MotionEvent.ACTION_MOVE:
                final int pointerCount = event.getPointerCount();

                for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
                    // Some pointer has moved, search it by pointer id
                    pointerId = event.getPointerId(actionIndex);

                    xTouch = screenToModel((int) event.getX(actionIndex));
                    yTouch = screenToModel((int) event.getY(actionIndex));

                    touchedThing = mOfficeThingPointer.get(pointerId);

                    if (null == touchedThing) {
                        break;
                    }

                    //TODO: These numbers were empirically determined for the Nexus 7. Replace with proper math
                    newTop = yTouch - touchedThing.getHeight(getContext()) / 2;
                    newLeft = xTouch - touchedThing.getWidth(getContext()) / 2;
                    newBottom = yTouch + (int) (touchedThing.getHeight(getContext()) / 3.7D);
                    newRight = xTouch + (int) (touchedThing.getWidth(getContext()) / 4.15D);


                    if (newTop >= 0 && newLeft >= 0 && newBottom <= 800 && newRight <= 600) {
                        touchedThing.setX(xTouch, getContext());
                        touchedThing.setY(yTouch, getContext());

                        if (null != this.mThingChangedListener) {
                            mThingChangedListener.thingChanged(touchedThing.getKey(), touchedThing);
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

        for (OfficeThing thing : mOfficeLayout.getThingsTopDown()) {
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

    public OfficeMoverActivity.ThingFocusChangeListener getThingFocusChangeListener() {
        return mThingFocusChangeListener;
    }

    public void setThingFocusChangeListener(OfficeMoverActivity.ThingFocusChangeListener mThingFocusChangeListener) {
        this.mThingFocusChangeListener = mThingFocusChangeListener;
    }
}
