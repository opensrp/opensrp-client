package org.ei.opensrp.view.activity;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import org.ei.opensrp.AllConstants;
import org.ei.opensrp.Context;
import org.ei.opensrp.R;
import org.ei.opensrp.domain.FetchStatus;
import org.ei.opensrp.util.BitmapImageCache;
import org.ei.opensrp.util.OpenSRPImageLoader;

import java.io.File;
import java.util.Locale;


public abstract class DrishtiApplication extends Application {
    private static final String TAG = "DrishtiApplication";

    protected Locale locale = null;
    protected Context context;
    private static BitmapImageCache memoryImageCache;
    private static DrishtiApplication mInstance;
    private static OpenSRPImageLoader cachedImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
    }
    public static synchronized DrishtiApplication getInstance() {
        return mInstance;
    }

    public abstract void logoutCurrentUser();

    public static BitmapImageCache getMemoryCacheInstance() {
        if (memoryImageCache == null) {
            memoryImageCache = new BitmapImageCache(BitmapImageCache.calculateMemCacheSize(AllConstants.ImageCache.MEM_CACHE_PERCENT));
        }

        return memoryImageCache;
    }

    public static String getAppDir(){
        File appDir = DrishtiApplication.getInstance().getApplicationContext().getDir("opensrp", android.content.Context.MODE_PRIVATE); //Creating an internal dir;
        return appDir.getAbsolutePath();
    }
    public static OpenSRPImageLoader getCachedImageLoaderInstance() {
        if (cachedImageLoader == null) {
            cachedImageLoader = new OpenSRPImageLoader(DrishtiApplication.getInstance().getApplicationContext(), R.drawable.woman_placeholder)
                    .setFadeInImage((Build.VERSION.SDK_INT >= 12));
        }

        return cachedImageLoader;
    }

    public abstract FetchStatus deleteRecords();
}
