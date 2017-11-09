package org.ei.opensrp.gizi.face.camera;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.ei.opensrp.gizi.face.camera.util.FaceConstants;

import java.io.IOException;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{

	private static final String TAG = CameraPreview.class.getSimpleName();
    private final int mPictureFormat;
    SurfaceHolder mHolder;
	private Camera mCamera;
    Context mContext;
    protected List<Size> mPictureSizeList;
    Camera.Parameters cameraParams;

    @SuppressWarnings("deprecation")
	public CameraPreview(Context context, Camera camera) {

		super(context);
		mCamera = camera;
		mContext = context;

		// Install a SurfaceHolder.Callback so we get notified when the underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
    
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        cameraParams = mCamera.getParameters();
        mPictureSizeList = cameraParams.getSupportedPictureSizes();
        mPictureFormat = cameraParams.getPictureFormat();
	}	

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// After created Surface, draw View.
        try {
        	mCamera.setDisplayOrientation(90);
        	mCamera.setPreviewDisplay(mHolder);

        	int index = 0;

			for(int i=0; i < mPictureSizeList.size(); i++) {

				int width = mPictureSizeList.get(i).width;
        		int height = mPictureSizeList.get(i).height;
        		int size = width*height*3/2;
				int MAX_NUM_BYTES = FaceConstants.MAX_PHOTO_SIZE;
				if(size< MAX_NUM_BYTES) {
        			index = i;
        			break;
        		}        		
        	}
        	//int indx = mPictureSizeList.size() - 2;
            cameraParams.setPictureSize(mPictureSizeList.get(index).width, mPictureSizeList.get(index).height);
        	
        	Log.e(TAG, "FORMAT" + mPictureFormat);
        	Log.d("CameraSurfaceView", mPictureSizeList.size() + "Picture dimension: " + mPictureSizeList.get(0).width + "x" + mPictureSizeList.get(0).height);
        	mCamera.setParameters(cameraParams);
            mCamera.startPreview();

        } catch (IOException e) {

            Log.e(TAG, "Error setting camera preview: " + e.getMessage());

        }
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
		mCamera.release();
        mCamera = null;
	}


}
