/*
 * =========================================================================
 * Copyright (c) 2014 Qualcomm Technologies, Inc. All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 * =========================================================================
 * @file: CameraSurfacePreview.java
 */

package layout.face.sidface.utils;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class CameraSurfacePreview extends SurfaceView implements
		SurfaceHolder.Callback {
	
	private SurfaceHolder mHolder;
	private OrientationEventListener mOrientationListener;
	private Camera mCamera;
	Context mContext;
	private int MAX_NUM_BYTES = 1572864; // Each image is supported for display,
											// upto 1.5 Mb = 1572864 bytes.
	
	public CameraSurfacePreview(Context context, Camera camera,
			OrientationEventListener orientationListener) {
		super(context);
		mCamera = camera;
		mContext = context;
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		mOrientationListener = orientationListener;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the
		// preview.
		try {
			mCamera.setDisplayOrientation(90);
			mCamera.setPreviewDisplay(holder);
			Camera.Parameters pm = mCamera.getParameters();
			
			int index = 0;
			// pm.setPictureFormat(ImageFormat.NV21);
			int format = pm.getPictureFormat();
			List<Size> pictSize = pm.getSupportedPictureSizes();
			for (int i = 0; i < pictSize.size(); i++) {
				int width = pictSize.get(i).width;
				int height = pictSize.get(i).height;
				int size = width * height * 3 / 2;
				if (size < MAX_NUM_BYTES) {
					index = i;
					break;
				}
			}
			pm.setPictureSize(pictSize.get(index).width,
					pictSize.get(index).height);
			
			Log.e("TAG", "FORMAT" + format);
			Log.d("CameraSurfaceView", pictSize.size() + "Picture dimension: "
					+ pictSize.get(0).width + "x" + pictSize.get(0).height);
			mCamera.setParameters(pm);
			mCamera.startPreview();
			if (mOrientationListener.canDetectOrientation()) {
				mOrientationListener.enable();
			}
		} catch (IOException e) {
			Log.d("TAG", "Error setting camera preview", e);
		}
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
}
