package layout.face.fr;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.ei.opensrp.indonesia.lib.FlurryFacade;

import java.io.IOException;
import java.util.List;

/**
 * Created by wildan on 12/22/16.
 */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    private static final int DISPLAY_ANGLE = 90;
    private static final String TAG = CameraSurfaceView.class.getSimpleName();
    private SurfaceHolder mHolder;
    private OrientationEventListener mOrientationEventListener;
    private Camera mCamera;
    Context mContext;


    public CameraSurfaceView(Context context, Camera camera, OrientationEventListener orientationEventListener) {
        super(context);
        mCamera = camera;
        mContext = context;
//      SurfaceHolder.Callback will notified when created and destroyed surface
        mHolder = getHolder();
        mHolder.addCallback(this);
        mOrientationEventListener = orientationEventListener;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setDisplayOrientation(DISPLAY_ANGLE);
            mCamera.setPreviewDisplay(holder);
            Camera.Parameters pm = mCamera.getParameters();

            int index = 0;
            int format = pm.getPictureFormat();

            List<Size> pictSize = pm.getSupportedPictureSizes();

            for (int i = 0; i < pictSize.size(); i++) {
                int widht = pictSize.get(i).width;
                int height = pictSize.get(i).height;
                int size = widht * height * 3 / 2;

                int MAX_NUM_BYTES = 1572864;
                if(size < MAX_NUM_BYTES){
                    index = i;
                    break;
                }
            }

            pm.setPictureSize(pictSize.get(index).width, pictSize.get(index).height);

            FlurryFacade.logEvent("Format : " + format);
//            FlurryFacade.logEvent("Dimension : ");

            mCamera.setParameters(pm);
            mCamera.startPreview();
            if (mOrientationEventListener.canDetectOrientation()){
                mOrientationEventListener.enable();
            }

        } catch(IOException ie){
            ie.printStackTrace();
            Log.d(TAG, "Error setting camera preview", ie);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
