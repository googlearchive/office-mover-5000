package com.firebase.officemover;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.Log;
import android.util.LruCache;

import com.firebase.officemover.model.OfficeThing;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility class that manages the bitmap representations of office things
 *
 * @author Jenny Tong (mimming)
 * @since 12/9/2014
 */
public class OfficeThingRenderUtil {
    private static final String TAG = OfficeThingRenderUtil.class.getSimpleName();

    private LruCache<String, Bitmap> mBitmapCache;
    private Context mContext;
    private Map<String, Integer> mHeightCache;
    private Map<String, Integer> mWidthCache;
    private float mCanvasRatio;

    public OfficeThingRenderUtil(Context context, float canvasRatio) {
        mContext = context;

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024) / 8;
        Log.v(TAG, "init bitmap cache with size " + cacheSize);
        mBitmapCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

        mHeightCache = new HashMap<>();
        mWidthCache = new HashMap<>();
        mCanvasRatio = canvasRatio;
    }

    private String getCacheKey(OfficeThing thing) {
        return thing.getType() + "-" + thing.getRotation();
    }

    private String getCacheKey(OfficeThing thing, boolean isGlowing) {
        if(isGlowing) {
            return thing.getType() + "-" + thing.getRotation() + "-glowing";
        } else {
            return getCacheKey(thing);
        }
    }

    public Bitmap getBitmap(OfficeThing thing) {
        if(thing == null || thing.getType() == null) {
            throw new RuntimeException("Thing must be defined and not null");
        }

        String cacheKey = getCacheKey(thing);

        if(mBitmapCache.get(cacheKey) == null) {
            String packageName = mContext.getPackageName();
            int resourceId = mContext.getResources().getIdentifier(thing.getType(), "drawable", packageName);
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resourceId);
            int resourceWidth = bitmap.getWidth();
            int resourceHeight = bitmap.getHeight();

            // rotate counter clockwise
            Matrix matrix = new Matrix();
            matrix.postRotate(-thing.getRotation());
            matrix.postScale(0.93F, 0.93F); //TODO: figure out why this hack makes android match web
            // Is it render ratio / px per dp -> 1.5 / 1.8

            bitmap = Bitmap.createBitmap(bitmap, 0, 0, resourceWidth, resourceHeight, matrix, true);
            mBitmapCache.put(cacheKey, bitmap);
        }
        return mBitmapCache.get(cacheKey);
    }


    public Bitmap getGlowingBitmap(OfficeThing thing) {
        if(thing == null || thing.getType() == null) {
            throw new RuntimeException("Thing must be defined and not null");
        }

        String cacheKey = getCacheKey(thing, true);
        if(mBitmapCache.get(cacheKey) == null) {
            // Get the non-glowy bitmap
            Bitmap bitmap = this.getBitmap(thing);

            // Blur it
            Paint blurPaint = new Paint();
            int[] offsetXY = new int[2];
            blurPaint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
            Bitmap bmAlpha = bitmap.extractAlpha(blurPaint, offsetXY);

            Paint glowPaint = new Paint();
            glowPaint.setColor(0xFF2896DD);

            // Create a virtual canvas on which to do the transformation and draw it
            Bitmap glowingBitmap = Bitmap.createBitmap(bitmap.getWidth() + (-offsetXY[0] * 2),
                    bitmap.getHeight() + (-offsetXY[0] * 2), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(glowingBitmap);
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            canvas.drawBitmap(bmAlpha, 0, 0, glowPaint);
            bmAlpha.recycle();
            canvas.drawBitmap(bitmap, -offsetXY[0], -offsetXY[1], null);

            // Put it into the cache for later use
            mBitmapCache.put(cacheKey, glowingBitmap);
        }
        return mBitmapCache.get(cacheKey);
    }

    // TODO: make less copy and paste
    public int getScreenHeight(OfficeThing thing) {
        String key = getCacheKey(thing);

        if(!mHeightCache.containsKey(key)) {
            String packageName = mContext.getPackageName();
            int resourceId = mContext.getResources().getIdentifier(thing.getType(), "drawable",
                    packageName);

            BitmapFactory.Options dimensions = new BitmapFactory.Options();
            dimensions.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(mContext.getResources(), resourceId, dimensions);

            int screenHeight;
            if(thing.getRotation() == 0 || thing.getRotation() == 180) {
                screenHeight = (int) ((dimensions.outHeight / 2D) * mCanvasRatio);
            } else {
                screenHeight = (int) ((dimensions.outWidth / 2D) * mCanvasRatio);
            }
            mHeightCache.put(key,screenHeight);
        }
        return mHeightCache.get(key);
    }

    public int getScreenWidth(OfficeThing thing) {
        String key = getCacheKey(thing);

        if(!mWidthCache.containsKey(key)) {
            String packageName = mContext.getPackageName();
            int resourceId = mContext.getResources().getIdentifier(thing.getType(), "drawable",
                    packageName);

            BitmapFactory.Options dimensions = new BitmapFactory.Options();
            dimensions.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(mContext.getResources(), resourceId, dimensions);

            int screenWidth;
            if(thing.getRotation() == 0 || thing.getRotation() == 180) {
                screenWidth = (int) ((dimensions.outWidth / 2D) * mCanvasRatio);
            } else {
                screenWidth = (int) ((dimensions.outHeight / 2D) * mCanvasRatio);
            }
            mWidthCache.put(key,screenWidth);
        }
        return mWidthCache.get(key);
    }

    public int getModelHeight(OfficeThing thing) {
        return (int)(getScreenHeight(thing) / mCanvasRatio);
    }
    public int getModelWidth(OfficeThing thing) {
        return (int)(getScreenWidth(thing) / mCanvasRatio);
    }
}


