package com.vijay.jsonwizard.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by vijay.rawat01 on 7/29/15.
 */
public class ImageUtils {

    private static LruCache<String, Bitmap> mBitmapLruCache = new LruCache<>(10000000);

    public static Bitmap loadBitmapFromFile(Context context, String path, int requiredWidth, int requiredHeight) {
        String key = path + ":" + requiredWidth + ":" + requiredHeight;
        Bitmap bitmap = mBitmapLruCache.get(key);
        Log.d("ImagePickerFactory", "Image path is "+path);
        if(bitmap != null) {
            Log.d("ImagePickerFactory", "Found in cache.");
            return bitmap;
        }
        try {
            Uri uri = Uri.fromFile(new File(path));
            bitmap = decodeSampledBitmap(context, uri, requiredWidth, requiredHeight);
            mBitmapLruCache.put(key, bitmap);
        } catch (IOException e) {
            Log.e("ImagePickerFactory", Log.getStackTraceString(e));
        }
        return bitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static int getDeviceWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getWidth();
    }

    /**
     * Rotate an image if required.
     * @param img
     * @param selectedImage
     * @return
     */
    private static Bitmap rotateImageIfRequired(Context context,Bitmap img, Uri selectedImage) {

        // Detect rotation
        int rotation = getRotation(selectedImage);
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            img.recycle();
            return rotatedImg;
        }
        else{
            return img;
        }
    }

    /**
     * Get the rotation of the last image added.
     *
     * @param selectedImage
     * @return
     */
    private static int getRotation(Uri selectedImage) {
        int rotation = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(selectedImage.getPath());
            int exifRotation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            rotation = exifToDegrees(exifRotation);
        } catch (IOException e) {
            Log.e("ImagePickerFactory", Log.getStackTraceString(e));
        }
        return rotation;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public static Bitmap decodeSampledBitmap(Context context, Uri selectedImage, int requiredWidth, int requiredHeight)
            throws IOException {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, requiredWidth, requiredHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(context, img, selectedImage);
        return img;
    }
}
