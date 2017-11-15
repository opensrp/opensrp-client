package layout.face.sidface.utils;

import android.graphics.Bitmap;

/**
 * Created by wildan on 12/16/16.
 */
public class RowItem {

    private Bitmap bitmapImage;

    public RowItem(Bitmap bitmapImage){
        this.bitmapImage = bitmapImage;
    }

    public Bitmap getBitmapImage() {
        return bitmapImage;
    }

    public void setBitmapImage(Bitmap bitmapImage){
        this.bitmapImage = bitmapImage;
    }
}
