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
import android.view.ViewTreeObserver;

import com.firebase.officemover.model.OfficeLayout;
import com.firebase.officemover.model.OfficeThing;

/**
 * This class contains the View that renders the office to the screen. There's not much in here
 * that's specific to Firebase.
 *
 * @author Jenny Tong (mimming)
 */
public class OfficeCanvasView extends View {

    private static final String TAG = OfficeCanvasView.class.getSimpleName();
    private static final Paint DEFAULT_PAINT = new Paint();
    private static final Paint DESK_LABEL_PAINT = new Paint();

    // The height and width of the canvas in Firebase
    public static final int LOGICAL_HEIGHT = 800;
    public static final int LOGICAL_WIDTH = 600;

    public float mScreenRatio;

    // A sparse array since we might have several pointers at once because multitouch
    private final SparseArray<OfficeThing> mOfficeThingPointer = new SparseArray<OfficeThing>();
    private String mSelectedThingKey;

    private OfficeLayout mOfficeLayout;

    // Listeners for communicating back to the owner activity
    private OfficeMoverActivity.ThingChangeListener mThingChangedListener;
    private OfficeMoverActivity.SelectedThingChangeListener mSelectedThingChangeListener;

    private OfficeThingRenderUtil mRenderUtil;

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


        // This view's height and width aren't known until they're measured once. Add a listener for
        // that time so we can calculate the conversion factor.
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                OfficeCanvasView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (OfficeCanvasView.this.getHeight() / LOGICAL_HEIGHT !=
                        OfficeCanvasView.this.getWidth() / LOGICAL_WIDTH) {
                    Log.e(TAG, "Aspect ratio is incorrect. Expected " +
                            LOGICAL_WIDTH / LOGICAL_HEIGHT + " got " +
                            OfficeCanvasView.this.getWidth() / OfficeCanvasView.this.getHeight());
                }
                // Set the conversion factor for pixels to logical (Firebase) model
                mScreenRatio = (float) OfficeCanvasView.this.getHeight() / (float) LOGICAL_HEIGHT;
                mRenderUtil = new OfficeThingRenderUtil(getContext(), mScreenRatio);
            }
        });
    }


    @Override
    public void onDraw(final Canvas canv) {
        if (null == mOfficeLayout || null == mRenderUtil) {
            Log.w(TAG, "Tried to render empty office");
            return;
        }

        for (OfficeThing thing : mOfficeLayout.getThingsBottomUp()) {
            Bitmap thingBitmap = mRenderUtil.getBitmap(thing);

            // If it's the selected thing, make it GLOW!
            if (thing.getKey().equals(mSelectedThingKey)) {
                thingBitmap = mRenderUtil.getGlowingBitmap(thing);
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

        // get touch event coordinates and make transparent wrapper from it
        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                // first pointer, clear all existing pointers data
                mOfficeThingPointer.clear();

                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);

                // check if we've touched inside something
                touchedThing = getTouchedThing(screenToModel(xTouch), screenToModel(yTouch));

                if (touchedThing == null) {
                    Log.v(TAG, "Deselected " + mSelectedThingKey);
                    mSelectedThingKey = null;
                    mSelectedThingChangeListener.thingChanged(null);
                    break;
                }

                //TODO: make sure these are accurate
                newTop = yTouch - mRenderUtil.getScreenHeight(touchedThing) / 2;
                newLeft = xTouch - mRenderUtil.getScreenWidth(touchedThing) / 2;
                newBottom = yTouch + mRenderUtil.getScreenHeight(touchedThing) / 2;
                newRight = xTouch + mRenderUtil.getScreenWidth(touchedThing) / 2;

                mOfficeThingPointer.put(event.getPointerId(0), touchedThing);

                // Determine if it's still on the canvas
                if (newTop >= 0 && newLeft >= 0 &&
                        newBottom <= modelToScreen(LOGICAL_HEIGHT) &&
                        newRight <= modelToScreen(LOGICAL_WIDTH)) {

                    touchedThing.setX(screenToModel(xTouch), mRenderUtil);
                    touchedThing.setY(screenToModel(yTouch), mRenderUtil);

                    if (null != this.mThingChangedListener) {
                        mThingChangedListener.thingChanged(touchedThing.getKey(), touchedThing);
                    }
                }


                mSelectedThingKey = touchedThing.getKey();
                mSelectedThingChangeListener.thingChanged(touchedThing);
                touchedThing.setzIndex(mOfficeLayout.getHighestzIndex() + 1);

                Log.v(TAG, "Selected " + touchedThing);
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                Log.w(TAG, "Pointer down");
                // It secondary pointers, so obtain their ids and check circles
                pointerId = event.getPointerId(actionIndex);

                xTouch = (int) event.getX(actionIndex);
                yTouch = (int) event.getY(actionIndex);

                // check if we've touched inside something
                touchedThing = getTouchedThing(screenToModel(xTouch), screenToModel(yTouch));
                if (touchedThing == null) {
                    break;
                }

                //TODO: make sure these are accurate
                newTop = yTouch - mRenderUtil.getScreenHeight(touchedThing) / 2;
                newLeft = xTouch - mRenderUtil.getScreenWidth(touchedThing) / 2;
                newBottom = yTouch + mRenderUtil.getScreenHeight(touchedThing) / 2;
                newRight = xTouch + mRenderUtil.getScreenWidth(touchedThing) / 2;

                mOfficeThingPointer.put(pointerId, touchedThing);

                // Determine if it's still on the canvas
                if (newTop >= 0 && newLeft >= 0 &&
                        newBottom <= modelToScreen(LOGICAL_HEIGHT) &&
                        newRight <= modelToScreen(LOGICAL_WIDTH)) {

                    touchedThing.setX(screenToModel(xTouch), mRenderUtil);
                    touchedThing.setY(screenToModel(yTouch), mRenderUtil);

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

                    xTouch = (int) event.getX(actionIndex);
                    yTouch = (int) event.getY(actionIndex);

                    touchedThing = mOfficeThingPointer.get(pointerId);

                    if (null == touchedThing) {
                        Log.e(TAG, "Tried to move a null thing");
                        break;
                    }

                    //TODO: make sure these are accurate
                    newTop = yTouch - mRenderUtil.getScreenHeight(touchedThing) / 2;
                    newLeft = xTouch - mRenderUtil.getScreenWidth(touchedThing) / 2;
                    newBottom = yTouch + mRenderUtil.getScreenHeight(touchedThing) / 2;
                    newRight = xTouch + mRenderUtil.getScreenWidth(touchedThing) / 2;

                    // Determine if it's still on the canvas
                    if (newTop >= 0 && newLeft >= 0 &&
                            newBottom <= modelToScreen(LOGICAL_HEIGHT) &&
                            newRight <= modelToScreen(LOGICAL_WIDTH)) {

                        touchedThing.setX(screenToModel(xTouch), mRenderUtil);
                        touchedThing.setY(screenToModel(yTouch), mRenderUtil);

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

    private OfficeThing getTouchedThing(int xTouchModel, int yTouchModel) {

        OfficeThing touched = null;

        for (OfficeThing thing : mOfficeLayout.getThingsTopDown()) {
            int top = thing.getTop();
            int left = thing.getLeft();
            int bottom = thing.getTop() + mRenderUtil.getScreenHeight(thing);
            int right = thing.getLeft() + mRenderUtil.getScreenWidth(thing);

            if (yTouchModel <= bottom && yTouchModel >= top && xTouchModel >= left && xTouchModel <= right) {
                touched = thing;
                break;
            }
        }
        return touched;
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

    public void setThingFocusChangeListener(OfficeMoverActivity.SelectedThingChangeListener mSelectedThingChangeListener) {
        this.mSelectedThingChangeListener = mSelectedThingChangeListener;
    }

    //Coordinate conversion
    private int modelToScreen(int coordinate) {
        return (int)(coordinate * mScreenRatio);
    }

    private int screenToModel(int coordinate) {
        return (int)(coordinate / mScreenRatio);
    }
}
