package util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.squareup.picasso.Transformation;

import org.ei.opensrp.Context;

/**
 * Created by oded on 9/15/15.
 * Watermark Transformation for the Picasso image loading library (https://github.com/square/picasso).
 * The transformation will add the text you provide in the constructor to the image.
 * This was created to be implemented in http://wheredatapp.com, android's greatest search engine.
 */
public class WatermarkTransformation implements Transformation {

    private Object waterMark;

    public WatermarkTransformation(Object waterMark) {
        this.waterMark = waterMark;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        if(waterMark instanceof Integer) {
            return addImageWatermark(source);
        }
        else {
            //todo
        }

        return null;
    }

    public Bitmap addImageWatermark(Bitmap source) {
        Bitmap bmp = Bitmap.createBitmap(source);
        Bitmap mutableBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);

        Paint paint = new Paint();

        int w, h;
        Bitmap watermark;

        Matrix matrix;
        RectF r;

        w = mutableBitmap.getWidth();
        h = mutableBitmap.getHeight();

        // Load the watermark
        watermark = BitmapFactory.decodeResource(Context.getInstance().applicationContext().getResources(), Integer.parseInt(waterMark.toString()));
        // Scale the watermark to be approximately 10% of the source image
        float scaley = (float) (((float) h * 0.20) / (float) watermark.getHeight());
        float scalex = (float) (((float) w * 0.20) / (float) watermark.getWidth());

        // Create the matrix
        matrix = new Matrix();
        matrix.postScale(scalex, scaley);
        // Determine the post-scaled size of the watermark
        r = new RectF(0, 0, watermark.getWidth(), watermark.getHeight());
        matrix.mapRect(r);
        // Move the watermark to the bottom right corner
        matrix.postTranslate(10, 10);

        // Draw the watermark
        canvas.drawBitmap(watermark, matrix, paint);
        // Free up the bitmap memory
        watermark.recycle();
        source.recycle();

        return mutableBitmap;
    }

    @Override
    public String key() {
        return "WaterMarkTransformation-" + waterMark;
    }
}
