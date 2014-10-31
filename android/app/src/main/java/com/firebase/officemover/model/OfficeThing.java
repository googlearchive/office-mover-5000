package com.firebase.officemover.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.Log;

public class OfficeThing {

    private static final String TAG = OfficeThing.class.getSimpleName();
    private int top;
    private int left;
    private int zIndex;
    private String type;
    private String name;
    private int rotation;
    private boolean locked;

    //Cache variables
    private int height;
    private int width;
    private Bitmap bitmap;
    private Bitmap glowingBitmap;
    private String key;

    public OfficeThing() {
    }

    @Override
    public String toString() {
        return "OfficeThing:" + type + "{name:" + name + ",X:" + left + ",Y:" + top + ",zIndex:" + zIndex + "}";
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getzIndex() {
        return zIndex;
    }

    public void setzIndex(int zIndex) {

        this.zIndex = zIndex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        if(rotation > 360) {
            rotation = rotation - 360;
        }
        this.rotation = rotation;
        this.bitmap = null;
        this.glowingBitmap = null;
    }

    //TODO: make these based on the real model instead of the screen
    //TODO: Consider moving these somewhere else. It seems odd for a model object to know about context
    private void checkSetDimensions(Context context) {
        if(type == null) {
            return;
        }
        if(height == 0 || width == 0) {
            String packageName = context.getPackageName();
            int resourceId = context.getResources().getIdentifier(this.type, "drawable", packageName);

            BitmapFactory.Options dimensions = new BitmapFactory.Options();
            dimensions.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(), resourceId, dimensions);

            //TODO: make this use the screen logical density instead of a fixed value for TVDPI
            height = (int) ((dimensions.outHeight / 2D) * 1.33D);
            width = (int) ((dimensions.outWidth / 2D) * 1.33D);
        }
    }

    public int getHeight(Context context) {
        checkSetDimensions(context);
        if(rotation == 0 || rotation == 180) {
            return height;
        } else {
            return width;
        }
    }

    public int getWidth(Context context) {
        checkSetDimensions(context);
        if(rotation == 0 || rotation == 180) {
            return width;
        } else {
            return height;
        }
    }

    //TODO: move somewhere else
    public Bitmap getBitmap(Context context) {
        if (bitmap != null) {
            return bitmap;
        }

        //TODO: better exception
        if (null == this.type) {
            Log.w(TAG, "Empty bitmap for "+this);
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }


        String packageName = context.getPackageName();
        int resourceId = context.getResources().getIdentifier(this.type, "drawable", packageName);
        bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        int resourceWidth = bitmap.getWidth();
        int resourceHeight = bitmap.getHeight();

        // rotate counter clockwise
        Matrix matrix = new Matrix();
        matrix.postRotate(-rotation);
        matrix.postScale(0.9F, 0.9F); //TODO: figure out why this hack makes android match web

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, resourceWidth, resourceHeight, matrix, true);

        return bitmap;
    }

    //TODO: These are probably broken because of display resolutions
    public void setX(int newX, Context context) {
        this.setLeft(newX - (getWidth(context) / 2));
    }

    public void setY(int newY, Context context) {
        this.setTop(newY - (getHeight(context) / 2));
    }

    public Bitmap getGlowingBitmap(Context context) {
        if (glowingBitmap != null) {
            return glowingBitmap;
        }

        if(bitmap == null) {
            bitmap = getBitmap(context);
        }

        Bitmap glowingBitmap = Bitmap.createBitmap(bitmap.getWidth() + 150, bitmap.getHeight() + 150, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(glowingBitmap);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        Paint blurPaint = new Paint();
        blurPaint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
        int[] offsetXY = new int[2];
        Bitmap bmAlpha = bitmap.extractAlpha(blurPaint, offsetXY);

        Paint glowPaint = new Paint();
        glowPaint.setColor(0xFF2896DD);
        canvas.drawBitmap(bmAlpha, offsetXY[0], offsetXY[1], glowPaint);
        bmAlpha.recycle();

        canvas.drawBitmap(bitmap, 0, 0, null);

        return glowingBitmap;
    }
}
