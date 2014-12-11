package com.firebase.officemover;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
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

    private OfficeThing mSelectedThing;

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

    // Setters called from OfficeMoverActivity
    public void setOfficeLayout(OfficeLayout officeLayout) {
        this.mOfficeLayout = officeLayout;
    }

    public void setThingChangedListener(OfficeMoverActivity.ThingChangeListener thingChangeListener) {
        this.mThingChangedListener = thingChangeListener;
    }

    public void setThingFocusChangeListener(OfficeMoverActivity.SelectedThingChangeListener mSelectedThingChangeListener) {
        this.mSelectedThingChangeListener = mSelectedThingChangeListener;
    }

    @Override
    public void onDraw(final Canvas canv) {
        if (null == mOfficeLayout || null == mRenderUtil) {
            Log.w(TAG, "Tried to render empty office");
            return;
        }

        for (OfficeThing thing : mOfficeLayout.getThingsBottomUp()) {
            Bitmap thingBitmap = mRenderUtil.getBitmap(thing);

            //TODO: reimplement glow rendering
//            // If it's the selected thing, make it GLOW!
//            if (thing.getKey().equals(mSelectedThingKey)) {
//                thingBitmap = mRenderUtil.getGlowingBitmap(thing);
//            }

            // Draw furniture
            canv.drawBitmap(thingBitmap, modelToScreen(thing.getLeft()), modelToScreen(thing.getTop()), DEFAULT_PAINT);

            // Draw desk label
            if (thing.getType().equals("desk") && thing.getName() != null) {
                // TODO: these offset numbers were empirically determined. Calculate them instead
                float centerX = modelToScreen(thing.getLeft()) + 102;
                float centerY = modelToScreen(thing.getTop()) + 70;

                canv.save();
                // TODO: OMG this is so hacky. Fix it. These numbers were empirically determined
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
        int xTouchLogical;
        int yTouchLogical;
        int pointerId;
        int actionIndex = event.getActionIndex();

        // get touch event coordinates and make transparent wrapper from it
        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                // first pointer, clear the pointer
                mSelectedThing = null;

                xTouchLogical = screenToModel((int) event.getX(0));
                yTouchLogical = screenToModel((int) event.getY(0));

                // check if we've touched inside something
                touchedThing = getTouchedThing(xTouchLogical, yTouchLogical);

                if (touchedThing == null) {
                    mSelectedThingChangeListener.thingChanged(null);
                    break;
                }

                mSelectedThing = touchedThing;

                if(null != mSelectedThingChangeListener) {
                    mSelectedThingChangeListener.thingChanged(touchedThing);
                }
                touchedThing.setzIndex(mOfficeLayout.getHighestzIndex() + 1);

                Log.v(TAG, "Selected " + touchedThing);
                handled = true;
                break;


            case MotionEvent.ACTION_MOVE:

                pointerId = event.getPointerId(actionIndex);
                if(pointerId > 0) {
                    break;
                }

                xTouchLogical = screenToModel((int) event.getX(actionIndex));
                yTouchLogical = screenToModel((int) event.getY(actionIndex));

                touchedThing = mSelectedThing;

                if (null == touchedThing) {
                    break;
                }

                moveThing(touchedThing, xTouchLogical, yTouchLogical);

                handled = true;
                break;

            case MotionEvent.ACTION_UP:
                mSelectedThing = null;
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_CANCEL:
                handled = true;
                break;
        }

        return super.onTouchEvent(event) || handled;
    }

    private void moveThing(OfficeThing touchedThing, int xTouchLogical, int yTouchLogical) {
        //TODO: make sure these are accurate
        int newTop = yTouchLogical - mRenderUtil.getModelHeight(touchedThing) / 2;
        int newLeft = xTouchLogical - mRenderUtil.getModelWidth(touchedThing) / 2;
        int newBottom = yTouchLogical + mRenderUtil.getModelHeight(touchedThing) / 2;
        int newRight = xTouchLogical + mRenderUtil.getModelWidth(touchedThing) / 2;

        if(newTop < 0 || newLeft < 0 || newBottom > LOGICAL_HEIGHT || newRight > LOGICAL_WIDTH) {
            Log.v(TAG, "Dragging beyond screen edge. Limiting");
        }
        // Limit moves to the boundaries of the screen
        if(newTop < 0) {
            newTop = 0;
        }
        if(newLeft < 0) {
            newLeft = 0;
        }
        if(newBottom > LOGICAL_HEIGHT) {
            newTop = LOGICAL_HEIGHT - mRenderUtil.getModelHeight(touchedThing);
        }
        if(newRight > LOGICAL_WIDTH) {
            newLeft = LOGICAL_WIDTH - mRenderUtil.getModelWidth(touchedThing);
        }

        // Save the object
        touchedThing.setTop(newTop);
        touchedThing.setLeft(newLeft);

        // Notify listeners
        if (null != this.mThingChangedListener) {
            mThingChangedListener.thingChanged(touchedThing.getKey(), touchedThing);
        }
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

    //Coordinate conversion
    private int modelToScreen(int coordinate) {
        return (int)(coordinate * mScreenRatio);
    }

    private int screenToModel(int coordinate) {
        return (int)(coordinate / mScreenRatio);
    }
}
