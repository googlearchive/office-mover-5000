package com.firebase.officemover.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class OfficeThing {

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
        this.rotation = rotation;
    }

    //TODO: make these based on the real model instead of the screen
    //TODO: Consider moving these somewhere else. It seems odd for a model object to know about context
    private void checkSetDimensions(Context context) {
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
            throw new RuntimeException();
        }


        String packageName = context.getPackageName();
        int resourceId = context.getResources().getIdentifier(this.type, "drawable", packageName);
        bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);

        // rotate
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return bitmap;
    }

    //TODO: These are probably broken because of display resolutions
    public void setX(int newX, Context context) {
        this.setLeft(newX - (getWidth(context) / 2));
    }

    public void setY(int newY, Context context) {
        this.setTop(newY - (getHeight(context) / 2));
    }
}
