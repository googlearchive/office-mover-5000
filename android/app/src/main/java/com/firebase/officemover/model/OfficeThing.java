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



    @Override
    public String toString() {
        return "OfficeThing:" + type + "{name:" + name + ",zIndex:" + zIndex + "}";
    }

    public OfficeThing() {
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
        if (zIndex <= 0) throw new IllegalArgumentException();

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
    public int getHeight(Context context) {
        if (height != 0) {
            return height;
        }

        if (null == this.type) {
            return 0;
        }

        String packageName = context.getPackageName();
        int resourceId = context.getResources().getIdentifier(this.type, "drawable", packageName);

        BitmapFactory.Options dimensions = new BitmapFactory.Options();
        dimensions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resourceId, dimensions);
        height = dimensions.outHeight;
        return height;
    }

    public int getWidth(Context context) {
        if (width != 0) {
            return width;
        }
        if (null == this.type) {
            return 0;
        }

        String packageName = context.getPackageName();
        int resourceId = context.getResources().getIdentifier(this.type, "drawable", packageName);

        BitmapFactory.Options dimensions = new BitmapFactory.Options();
        dimensions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resourceId, dimensions);
        width = dimensions.outWidth;
        return width;
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

    public void setX(int newX, Context context) {
        this.left = newX - (getWidth(context) / 2);
    }
    public void setY(int newY, Context context) {
        this.top = newY - (getHeight(context) / 2);
    }
}
